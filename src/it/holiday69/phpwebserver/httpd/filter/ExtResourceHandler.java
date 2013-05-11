/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.httpd.filter;

import it.holiday69.phpwebserver.model.Model;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 *
 * @author fratuz610
 */
public class ExtResourceHandler extends ResourceHandler {
  
  private final Logger _log = Logger.getLogger(ExtResourceHandler.class.getSimpleName());
  private final Model _model = Model.getInstance();
  
  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    
    _log.info("Handling as a resource");
    
    super.handle(target, baseRequest, request, response);
    
    _log.info("Handling as a resource: DONE -> request completed: " + baseRequest.isHandled());
    
    if(!baseRequest.isHandled())
      _model.logHttpRequest(404, request.getServletPath() + request.getPathInfo());
    else
      _model.logHttpRequest(200, request.getServletPath() + request.getPathInfo());
    
  }
}
