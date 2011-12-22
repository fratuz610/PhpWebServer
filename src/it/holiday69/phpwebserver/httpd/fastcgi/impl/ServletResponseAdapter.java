/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * Implementation used for plain servlets.
 */
public class ServletResponseAdapter implements ResponseAdapter {

	private HttpServletResponse httpServletResponse;

	public ServletResponseAdapter(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

  @Override
	public void addHeader(String key, String value) {
		httpServletResponse.addHeader(key, value);
	}

  @Override
	public void sendError(int errorCode) {
		try {
			httpServletResponse.sendError(errorCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

  @Override
	public void sendRedirect(String targetUrl) {
		try {
			httpServletResponse.sendRedirect(targetUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

  @Override
	public void setStatus(int statusCode) {
		httpServletResponse.setStatus(statusCode);
	}

  @Override
	public OutputStream getOutputStream() {
		try {
			return httpServletResponse.getOutputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
