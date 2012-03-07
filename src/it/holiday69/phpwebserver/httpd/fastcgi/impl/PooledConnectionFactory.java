/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;

import it.holiday69.phpwebserver.httpd.fastcgi.ConnectionFactory;
import java.net.Socket;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * A connection factory that handles multiple connections, using an underlying connection pool provided
 * by commons-pool. (http://commons.apache.org/pool/)
 * 
 * @author jrialland
 *
 */
public class PooledConnectionFactory implements ConnectionFactory {

	private ObjectPool pool;
  
	public PooledConnectionFactory(PoolableObjectFactory poolableObjectFactory)
	{
		this.pool = new GenericObjectPool(poolableObjectFactory);
	}
	
	/**
	 * get a connection from the pool.
	 */
  @Override
	public Socket getConnection() {
		try
		{
			Socket s = (Socket)pool.borrowObject();
			return s;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * returns a connection to the pool.
	 * 
	 */
  @Override
	public void releaseConnection(Socket socket) {
		try
		{
			pool.returnObject(socket);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public ObjectPool getPool() {
		return pool;
	}

	public void setPool(ObjectPool pool) {
		this.pool = pool;
	}
}
