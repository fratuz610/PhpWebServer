/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation used for plain servlets.
 * 
 * @author jrialland
 *
 */
public class ServletRequestAdapter implements RequestAdapter {

	public ServletRequestAdapter(HttpServletRequest httpServletRequest) {
		super();
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	private HttpServletRequest httpServletRequest;

	public InputStream getInputStream() {
		try {
			return httpServletRequest.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getAuthType() {
		return httpServletRequest.getAuthType();
	}

	public int getContentLength() {
		return httpServletRequest.getContentLength();
	}

	public String getContextPath() {
		return httpServletRequest.getContextPath();
	}

	public String getHeader(String key) {
		return httpServletRequest.getHeader(key);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeaderNames() {
		return httpServletRequest.getHeaderNames();
	}

	public String getMethod() {
		return httpServletRequest.getMethod();
	}

	public String getProtocol() {
		return httpServletRequest.getProtocol();
	}

	public String getQueryString() {
		return httpServletRequest.getQueryString();
	}

	public String getRemoteAddr() {
		return httpServletRequest.getRemoteAddr();
	}

	public String getRemoteUser() {
		return httpServletRequest.getRemoteUser();
	}

	public String getRequestURI() {
		return httpServletRequest.getRequestURI();
	}

	public String getServerName() {
		return httpServletRequest.getServerName();
	}

	public int getServerPort() {
		return httpServletRequest.getServerPort();
	}

	public String getServletPath() {
		return httpServletRequest.getServletPath();
	}

	public String getRealPath(String relPath) {
		return httpServletRequest.getSession().getServletContext().getRealPath(relPath);
	}
}
