/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.holiday69.phpwebserver.httpd.fastcgi.ConnectionFactory;

/**
 * A connection factory that always tries to connect to the same ip/port.
 * 
 * @author jrialland
 *
 */
public class SingleConnectionFactory implements ConnectionFactory {

	private InetAddress host;

	private int port;

	public SingleConnectionFactory(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}

	public SingleConnectionFactory(String descriptor)
	{
		Matcher m = Pattern.compile("([^:]+):([1-9][0-9]*)$").matcher(descriptor.trim());
		if (m.matches()) {
			try {
				this.host = InetAddress.getByName(m.group(1));
				this.port = Integer.parseInt(m.group(2));
			}
			catch(Exception e)
			{
				throw new IllegalArgumentException(e);
			}
		}
		else throw new IllegalArgumentException("syntax error (required format is <host>:<port>) - "+descriptor);
	}

  @Override
	public Socket getConnection() {
		try {
			return new Socket(host, port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

  @Override
	public void releaseConnection(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
