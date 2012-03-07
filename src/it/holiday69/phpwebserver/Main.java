/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver;

import it.holiday69.phpwebserver.httpd.fastcgi.FastCGIServlet;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIErrorHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandlerFactory;
import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.task.*;
import it.holiday69.phpwebserver.ui.MainUI;
import it.holiday69.phpwebserver.utils.ThreadUtils;
import it.holiday69.tinyutils.ExceptionUtils;
import it.holiday69.tinyutils.FatalErrorUtils;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.swing.SwingUtilities;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.MultiPartFilter;

/**
 *
 * @author fratuz610
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  
  private final static Logger log = Logger.getLogger(Main.class.getSimpleName());
  
  private final static ExecutorService _backgroundExecutor = Executors.newFixedThreadPool(3);
  
  private static Server _httpServer;
  private static Model _model = Model.getInstance();
  
  private static String _thisFolder;
  private static File _phpCGIExecutable;
  private static File _phpFolder;
  private static File _wwwFolder;
  private static ServletContextHandler _phpContext;
  private static FastCGIServlet _fastCGIServlet;
  private static ServletHolder _fastCGIHolder;
  private static ResourceHandler _resourceHandler;
  
  private static MainUI _mainUI;
 
  public static void main(String[] args) {
    
    try {
      _thisFolder = getThisJARFolder(Main.class);
    } catch(Exception ex) {
      FatalErrorUtils.showFatalError("Unable to determine the current folder:\n" + ex.getMessage());
      return;
    }
    
    _phpFolder = new File(_thisFolder, "php");
    
    if(!_phpFolder.exists()) {
      FatalErrorUtils.showFatalError("Unable to find php folder: " + _phpFolder.getAbsolutePath());
      return;
    }
    
    _phpCGIExecutable = new File(_phpFolder, "php-cgi.exe");
    
    if(!_phpCGIExecutable.exists()) {
      FatalErrorUtils.showFatalError("Unable to find php-cgi executable: " + _phpCGIExecutable.getAbsolutePath());
      return;
    }
    
    try {
      _backgroundExecutor.submit(new SetupUILookAndFeelTask()).get();
      _backgroundExecutor.submit(new ReadConfigTask()).get();
      _backgroundExecutor.submit(new WriteConfigTask()).get();
      _backgroundExecutor.submit(new AddShutdownHookTask()).get();
      _mainUI = _backgroundExecutor.submit(new ShowCreateMainUITask()).get();
    } catch(ExecutionException ex) {
      log.info("Preboot task failed: " + ex.getCause().getMessage());
    } catch(InterruptedException ex) {
      return;
    }
    
    _model.logToScreen("Opening Main UI");
    
    while(true) {

      try {
        
        _model.logToScreen("** Verifying configuration **");
        
        try {
          _backgroundExecutor.submit(new VerifySettingsTask()).get();
        } catch(ExecutionException ex) {
          throw new Exception("Verification exception: " + ex.getCause().getMessage());
        }
        
        _wwwFolder = new File(_model.configObject.wwwFolder);
        
        _model.logToScreen("Using www folder: " + _wwwFolder.getAbsolutePath());
        _model.logToScreen("Using http port: " + _model.configObject.httpPort);
        _model.logToScreen("Using fcgi port: " + _model.configObject.fastCGIPort);
        
        _httpServer = new Server(_model.configObject.httpPort);
        
        _phpContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        _phpContext.setResourceBase(_wwwFolder.getAbsolutePath());
        _phpContext.setContextPath("/");
        _phpContext.setMaxFormContentSize(Integer.MAX_VALUE);

        // PHP settings
        _fastCGIServlet = new FastCGIServlet(new FastCGIErrorHandler() {
          @Override
          public void logError(String errorString) {
            _model.logErrorToScreen(errorString);
          }
        });
        
        _fastCGIHolder = new ServletHolder(_fastCGIServlet);
        _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_SERVER_ADDRESS, "localhost:" + _model.configObject.fastCGIPort);
        _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE, _phpCGIExecutable.getName());
        _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PATH, _phpFolder.getAbsolutePath());
        _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PARAMS, "-b" + _model.configObject.fastCGIPort);

        _phpContext.addServlet(_fastCGIHolder,"*.php");

        // File serving settings
        _resourceHandler = new ResourceHandler();
        _resourceHandler.setWelcomeFiles(new String[]{ "index.php", "index.html" });
        _resourceHandler.setResourceBase(_wwwFolder.getAbsolutePath());

        // allows uploads to work
        FilterHolder multipartFilter = new FilterHolder(MultiPartFilter.class);
        multipartFilter.setInitParameter("maxFileSize", ""+Integer.MAX_VALUE);
        multipartFilter.setInitParameter("maxRequestSize", ""+Integer.MAX_VALUE);

        _phpContext.addFilter(multipartFilter, "/*", EnumSet.of(DispatcherType.REQUEST));
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { _phpContext, _resourceHandler});
        

        // initialize both servers
        _httpServer.setHandler(handlers);

        _model.logToScreen("** Starting server **");
        _httpServer.start();
        _model.logToScreen("** Server started **");

      } catch(InterruptedException ex) {
        return;
      } catch(Throwable e) {
        _model.logErrorToScreen("Unable to start webserver because: " + ExceptionUtils.getDisplableExceptionInfo(e));
      }

      Model.getInstance().waitForEvent();
      
      // an event has occured, let's stop the server
      
      try { 
        _httpServer.stop();
        _phpContext.destroy();
        _resourceHandler.destroy();
      } catch(Throwable th) {
        
      }
      
      if(Model.getInstance().getLastEvent() == Model.Event.RESTART) {
         _model.logToScreen("** Restarting server **");
        continue;
      } else {
        _model.logToScreen("** Closing down **");
        break;
      }

    }
    
    log.info("Closing main window");
    
    // shows the Main UI
    
    try {
      _backgroundExecutor.submit(new DestroyMainUITask(_mainUI));
    } catch(Exception ex) {
      
    }
    
    // shuts down the executor
    _backgroundExecutor.shutdownNow();
    
    log.info("All done, closing down");
    
    try { Thread.sleep(1000); } catch(Exception e) { }
    
    List<Thread> threadList = ThreadUtils.getFullThreadList(false);
    
    for(Thread th : threadList)
      th.interrupt();
    
  }
  
  private static String getThisJARFolder(Class refClass) throws Exception {
    
    // get name and path
    String name = refClass.getName().replace('.', '/');
    name = refClass.getClass().getResource("/" + name + ".class").toString();
    
    // removes the head
    if(name.indexOf("file:/") == -1)
      throw new Exception("Internal Error: no 'file:/' section found in the current class OS path");
    
    name = name.substring(name.indexOf("file:/")+6);
    
    // removes what's after PhpWebServer etc
    if(name.lastIndexOf("PhpWebServer") == -1)
      throw new Exception("Internal Error: no 'PhpWebServer' section found in the current class OS path");
    
    name = name.substring(0, name.lastIndexOf("PhpWebServer"));
    
    return name;
  }
}
