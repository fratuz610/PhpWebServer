/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.handler;

import it.holiday69.phpwebserver.Main;
import it.holiday69.phpwebserver.httpd.fastcgi.FastCGIServlet;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIErrorHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandlerFactory;
import it.holiday69.phpwebserver.httpd.filter.ExtResourceHandler;
import it.holiday69.phpwebserver.httpd.filter.LogErrorFilter;
import it.holiday69.phpwebserver.model.Model;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.MultiPartFilter;

/**
 *
 * @author Stefano
 */
public class MainHandler extends AbstractHandlerContainer {

  private final static Logger log = Logger.getLogger(MainHandler.class.getSimpleName());
  
  private static ServletContextHandler _phpHandler;
  private static File _wwwFolder;
  private static Model _model = Model.getInstance();
  private static FastCGIServlet _fastCGIServlet;
  private static ServletHolder _fastCGIHolder;
  private static ExtResourceHandler _resourceHandler;
  
  public MainHandler() {
    
    // gets the www folder
    _wwwFolder = new File(_model.configObject.wwwFolder);
    
    log.info("MainHandler using www folder: " + _wwwFolder);
    
    _phpHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    _phpHandler.setResourceBase(_wwwFolder.getAbsolutePath());
    _phpHandler.setContextPath("/");
    _phpHandler.setMaxFormContentSize(Integer.MAX_VALUE);
    
    // PHP settings
    _fastCGIServlet = new FastCGIServlet(new FastCGIErrorHandler() {
      @Override
      public void logError(String errorString) {

        String[] errorStringList = errorString.split("\n");

        for(String errorStr : errorStringList) {
          _model.logErrorToScreen(errorStr);
        }
      }
    });

    _fastCGIHolder = new ServletHolder(_fastCGIServlet);
    _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_SERVER_ADDRESS, "localhost:" + _model.configObject.fastCGIPort);
    _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE, _model.phpCGIExecutable.getName());
    _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PATH, _model.phpCGIExecutable.getParent());
    _fastCGIHolder.setInitParameter(FastCGIHandlerFactory.PARAM_START_EXECUTABLE_PARAMS, "-b" + _model.configObject.fastCGIPort);

    _phpHandler.addServlet(_fastCGIHolder,"*.php");

    // File serving settings
    _resourceHandler = new ExtResourceHandler();
    _resourceHandler.setWelcomeFiles(new String[]{ "index.php", "index.html" });
    _resourceHandler.setResourceBase(_wwwFolder.getAbsolutePath());

    //_resourceHandler.addFilter(LogErrorFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

    // allows uploads to work
    FilterHolder multipartFilterHolder = new FilterHolder(MultiPartFilter.class);
    multipartFilterHolder.setInitParameter("maxFileSize", ""+Integer.MAX_VALUE);
    multipartFilterHolder.setInitParameter("maxRequestSize", ""+Integer.MAX_VALUE);

    log.info("javax.servlet.context.tempdir: " + _phpHandler.getAttribute("javax.servlet.context.tempdir"));

    _phpHandler.setAttribute("javax.servlet.context.tempdir", new File(System.getProperty("java.io.tmpdir")));
    _phpHandler.addFilter(multipartFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
    _phpHandler.addFilter(LogErrorFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    
  }
  
  @Override
  protected void doStart() throws Exception {
    _phpHandler.start();
    _resourceHandler.start();
    super.doStart();
  }
  
  @Override
  protected void doStop() throws Exception {
    super.doStop();
    _phpHandler.stop();
    _resourceHandler.stop();
  }
  
  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    
    log.info("Target: " + target + " baseRequest: " + baseRequest.getQueryString());
    
    if(target.endsWith(".php"))
      _phpHandler.handle(target, baseRequest, request, response);
    else
      _resourceHandler.handle(target, baseRequest, request, response);
    
    log.info("Handling completed -> request handled?: " + baseRequest.isHandled());
  }
  
  @Override
  public void setServer(Server server)
  {
    super.setServer(server);
    _phpHandler.setServer(server);
    _resourceHandler.setServer(server);
  }
  
  @Override
  protected Object expandChildren(Object list, Class byClass)
  {
    Handler[] handlers = getHandlers();
    for (int i=0;handlers!=null && i<handlers.length;i++)
        list=expandHandler(handlers[i], list, byClass);
    return list;
  }

  
  @Override
  public void destroy()
  {
      _phpHandler.destroy();
      _resourceHandler.destroy();
      super.destroy();
  }
  
  @Override
  public Handler[] getHandlers() {
    return new Handler[] {_phpHandler, _resourceHandler};
  }
  
}
