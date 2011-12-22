/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;


/**
 * 
 */
public interface FastCGIErrorHandler {
  
  void logError(String errorString);

}
