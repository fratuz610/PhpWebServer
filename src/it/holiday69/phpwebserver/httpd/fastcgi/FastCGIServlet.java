/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   
 */
package it.holiday69.phpwebserver.httpd.fastcgi;

import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIErrorHandler;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandler;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.FastCGIHandlerFactory;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.ServletRequestAdapter;
import it.holiday69.phpwebserver.httpd.fastcgi.impl.ServletResponseAdapter;

/**
 * @author jrialland
 *
 */
public class FastCGIServlet extends HttpServlet {

	private static final long serialVersionUID = -8597795652806478718L;

  private FastCGIHandler handler;

  public FastCGIServlet(FastCGIErrorHandler errorHandler) {
    this.handler = new FastCGIHandler(errorHandler);
  }

  public FastCGIServlet() {
    this.handler = new FastCGIHandler();
  }
	

  @Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		Map<String, String> config = new TreeMap<String, String>();
		for (String paramName : FastCGIHandlerFactory.PARAM_NAMES) {
			String value = servletConfig.getInitParameter(paramName);
			if(value != null){
				config.put(paramName, getInitParameter(paramName));
			}
		}
		handler = FastCGIHandlerFactory.create(config);
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handler.service(new ServletRequestAdapter(request), new ServletResponseAdapter(response));
	}
	
	@Override
	public void destroy() {
		super.destroy();
		handler.destroy();
	}
}
