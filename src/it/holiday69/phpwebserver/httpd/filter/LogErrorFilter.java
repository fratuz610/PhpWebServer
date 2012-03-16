/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.httpd.filter;

import it.holiday69.phpwebserver.model.Model;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author fratuz610
 */
public class LogErrorFilter implements Filter {

  private final Model _model = Model.getInstance();
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    
    HttpServletRequest httpRequest=(HttpServletRequest)request;
    HttpServletResponse httpResponse=(HttpServletResponse)response;
    chain.doFilter(request,response);
    
    _model.logHttpRequest(httpResponse.getStatus(), httpRequest.getServletPath());
  }

  @Override
  public void destroy() {
    
  }
  
}
