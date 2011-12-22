/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver;

import it.holiday69.phpwebserver.httpd.fastcgi.FastCGIServlet;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIErrorHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandlerFactory;
import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.task.ReadConfigTask;
import it.holiday69.phpwebserver.task.SetupUILookAndFeelTask;
import it.holiday69.phpwebserver.task.WriteConfigTask;
import it.holiday69.phpwebserver.ui.MainUI;
import it.holiday69.tinyutils.FatalErrorUtils;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
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
  
  private final static ExecutorService _prebootExecutor = Executors.newSingleThreadExecutor();
  
  private static Server _httpServer;
  private static Model _model = Model.getInstance();
  
  private static String _thisFolder;
  private static File _phpCGIExecutable;
  private static File _phpFolder;
  private static File _wwwFolder;
  
  
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
    
    _wwwFolder = new File(_thisFolder, "www");
    if(!_wwwFolder.exists())
      _wwwFolder.mkdir();
    
    if(!_wwwFolder.exists()) {
      FatalErrorUtils.showFatalError("Unable to find www folder and I cannot create it: " + _wwwFolder.getAbsolutePath());
      return;
    }
    
    try {
      _prebootExecutor.submit(new SetupUILookAndFeelTask()).get();
      _prebootExecutor.submit(new ReadConfigTask()).get();
      _prebootExecutor.submit(new WriteConfigTask()).get();
    } catch(ExecutionException ex) {
      log.info("Preboot task failed: " + ex.getCause().getMessage());
    } catch(InterruptedException ex) {
      return;
    }
    
    // shuts down the executor
    _prebootExecutor.shutdown();
    
    _httpServer = new Server(_model.configObject.httpPort);
    
    ServletContextHandler phpContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
    phpContext.setResourceBase(_wwwFolder.getAbsolutePath());
    phpContext.setContextPath("/");
    
    // PHP settings
    final FastCGIServlet fastCGIServlet = new FastCGIServlet(new FastCGIErrorHandler() {
      @Override
      public void logError(String errorString) {
        
      }
    });
        
    ServletHolder fastCGIHolder = new ServletHolder(fastCGIServlet);
    fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_SERVER_ADDRESS, "localhost:" + _model.configObject.fastCGIPort);
    fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE, _phpCGIExecutable.getName());
    fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PATH, _phpFolder.getAbsolutePath());
    fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PARAMS, "-b" + _model.configObject.fastCGIPort);

    phpContext.addServlet(fastCGIHolder,"*.php");
    
    // File serving settings
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setWelcomeFiles(new String[]{ "index.php", "index.html" });
    resource_handler.setResourceBase(_wwwFolder.getAbsolutePath());

    // allows uploads to work
    FilterHolder multipartFilter = new FilterHolder(MultiPartFilter.class);
    multipartFilter.setInitParameter("maxFileSize", ""+Integer.MAX_VALUE);
    multipartFilter.setInitParameter("maxRequestSize", ""+Integer.MAX_VALUE);
        
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { phpContext, resource_handler});
    
    // initialize both servers
    _httpServer.setHandler(handlers);
    
    try {
      _httpServer.start();
    } catch(Throwable th) {
      FatalErrorUtils.showFatalError("Unable to start web server on port: " + _model.configObject.httpPort + ". Is this port already in use??");
      return;
    }
    
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      @Override
      public void run() {
        log.info("Shutdown hook triggered, closing down");
        Model.getInstance().notifyShutdown();
      }
    }));

    final MainUI mainUI = new MainUI();
    mainUI.setVisible(true);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mainUI.setLocationRelativeTo(null);
      }
    });
    
    
    log.info("Shutdown hook added, waiting for shutdown signal");
    
    Model.getInstance().waitForShutdown();
    
    log.info("Closing main window");
    
    mainUI.dispose();
    
    log.info("Stopping server");
    
    try {
      _httpServer.stop();
    } catch(Exception ex) {
      FatalErrorUtils.showWarning("Unable to stop server because: " + ex.getMessage());
    }
    
    fastCGIServlet.destroy();
    
    log.info("All done, closing down");
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
