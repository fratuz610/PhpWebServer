/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver;

import it.holiday69.phpwebserver.handler.MainHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.FastCGIServlet;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIErrorHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandlerFactory;
import it.holiday69.phpwebserver.httpd.filter.ExtResourceHandler;
import it.holiday69.phpwebserver.httpd.filter.LogErrorFilter;
import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.task.*;
import it.holiday69.phpwebserver.utils.ThreadUtils;
import it.holiday69.tinyutils.ExceptionUtils;
import it.holiday69.tinyutils.FatalErrorUtils;
import it.holiday69.tinyutils.StringUtils;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.swing.JFrame;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
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
  
  private static File _wwwFolder;
  private static MainHandler _mainHandler;
  
  private static JFrame _currentUI;
 
  public static void main(String[] args) {
    
    
    
    try {
      _backgroundExecutor.submit(new SetupUILookAndFeelTask()).get();
      _backgroundExecutor.submit(new ReadConfigTask()).get();
      _backgroundExecutor.submit(new FindPhpInstallationTask(Main.class)).get();
      _backgroundExecutor.submit(new WriteConfigTask()).get();
      _backgroundExecutor.submit(new AddShutdownHookTask()).get();
      
      if(StringUtils.hasContent(_model.configObject.interfaceURL) && StringUtils.hasContent(_model.configObject.interfaceTitle)) {
        log.info("Package mode detected, showing the MINIMAL UI only");
        _currentUI = _backgroundExecutor.submit(new ShowCreateMinimalUITask()).get();
      } else {
        log.info("Free mode detected, showing the MAIN UI only");
        _currentUI = _backgroundExecutor.submit(new ShowCreateMainUITask()).get();
      }
      
    } catch(ExecutionException ex) {
      FatalErrorUtils.showFatalError("Unable to start PhpWebServer because:\n" + ExceptionUtils.getDisplableExceptionInfo(ex.getCause()));
      log.severe("Preboot task failed: " + ExceptionUtils.getFullExceptionInfo(ex.getCause()));
      _backgroundExecutor.shutdownNow();
      return;
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
        
        _mainHandler = new MainHandler();
        
        // initialize both servers
        _httpServer.setHandler(_mainHandler);

        _model.logToScreen("** Starting server **");
        _httpServer.start();
        _model.logToScreen("** Server started **");

      } catch(InterruptedException ex) {
        return;
      } catch(Throwable e) {
        log.info("Unable to start the webserver because: " + ExceptionUtils.getFullExceptionInfo(e));
        _model.logErrorToScreen("Unable to start webserver because: " + ExceptionUtils.getDisplableExceptionInfo(e));
      }

      Model.getInstance().waitForEvent();
      
      // an event has occured, let's stop the server
      
      try { 
        _httpServer.stop();
        _mainHandler.destroy();
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
      _backgroundExecutor.submit(new DestroyMainUITask(_currentUI));
    } catch(Exception ex) {
      
    }
    
    // shuts down the executor
    _backgroundExecutor.shutdownNow();
    
    log.info("All done, closing down");
    
    try { Thread.sleep(1000); } catch(Exception e) { }
    
    List<Thread> threadList = ThreadUtils.getFullThreadList(false);
    
    for(Thread th : threadList)
      th.interrupt();
    
    try { Thread.sleep(5000); } catch(Exception e) { }
    
    System.exit(0);
  }
  
  
}
