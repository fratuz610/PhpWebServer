/*
 * (c) 2009 Julien Rialland, and the jFastCGI project developpers.
 * 
 * Released under BSD License (see license.txt)
 *  
 *   $Id$ 
 */
package it.holiday69.phpwebserver.httpd.fastcgi.impl;

import java.io.IOException;
import java.util.Map;

import it.holiday69.phpwebserver.httpd.fastcgi.ConnectionFactory;
import java.util.logging.Logger;

/**
 * This class helps instanciating FastCGIHandlers using a properties-based configuration.
 * 
 * 
 * @author jrialland
 *
 */
public class FastCGIHandlerFactory {

	private static final Logger log = Logger.getLogger(FastCGIHandlerFactory.class.getSimpleName());
	
	/**
	 * Address of the fastcgi provider service to use.
	 */
	public static final String PARAM_SERVER_ADDRESS = "server-address";
	
	/**
	 * executable that should be launched as the servlet starts.
	 */
	public static final String PARAM_START_EXECUTABLE = "start-executable";
  public static final String PARAM_START_EXECUTABLE_PATH = "start-executable-path";
  public static final String PARAM_START_EXECUTABLE_PARAMS = "start-executable-params";
	
	/**
	 * user-provided class that will provide tcp connections.
	 */
	public static String PARAM_CONNECTION_FACTORY = "connection-factory";
	
	/**
	 * comma-separated list of adresses when using seveal fastcgi endpoints.
	 */
	public static String PARAM_CLUSTER_ADRESSES = "cluster-adresses";

	public static String[] PARAM_NAMES = new String[]{
		PARAM_SERVER_ADDRESS,
		PARAM_START_EXECUTABLE,
    PARAM_START_EXECUTABLE_PATH,
    PARAM_START_EXECUTABLE_PARAMS,
		PARAM_CONNECTION_FACTORY,
		PARAM_CLUSTER_ADRESSES
	};
	
	public static FastCGIHandler create(Map<String, String> config)
	{
		FastCGIHandler handler = new FastCGIHandler();

    if(config.get(PARAM_START_EXECUTABLE) != null) {

      log.info("Starting server executable: " +config.get(PARAM_START_EXECUTABLE));

      String path = "";
      if(config.get(PARAM_START_EXECUTABLE_PATH) != null)
        path= config.get(PARAM_START_EXECUTABLE_PATH);
      
      if(!path.endsWith("/"))
        path = path + "/";

      path = path.replace('\\', '/');
      
      if(!path.equals(""))
        log.info("Starting server executable from path: " +path);

      String params = "";
      if(config.get(PARAM_START_EXECUTABLE_PARAMS) != null)
        params = config.get(PARAM_START_EXECUTABLE_PARAMS);

      if(!path.equals(""))
        log.info("Starting server executable with param string path: " +params);
      
      try {
        handler.startProcess(path + config.get(PARAM_START_EXECUTABLE), params);
      } catch(IOException ex) {
        log.severe("Unable to start FastCGI server because: " + ex.getMessage());
      }
    }

		if(config.get(PARAM_SERVER_ADDRESS) != null)
		{
			log.info("configuring fastCGI handler using a single connection-based policy");
			handler.setConnectionFactory(new SingleConnectionFactory(config.get(PARAM_SERVER_ADDRESS)));
		}
		else if(config.get(PARAM_CONNECTION_FACTORY) != null)
		{
			String className = config.get(PARAM_CONNECTION_FACTORY).trim(); 
			log.info("configuring fastCGI handler using custom class '"+className+"'");
			handler.setConnectionFactory(buildConnectionFactoryForClass(className));
		}
		else if(config.get(PARAM_CLUSTER_ADRESSES) != null)
		{
			PoolFactory factory = new PoolFactory();
			log.info("configuring fastCGI handler using the following adresses : ");
			for(String addr : config.get(PARAM_CLUSTER_ADRESSES).split(";"))
			{
				log.info("  => "+addr);
				factory.addAddress(addr.trim());
			}
			handler.setConnectionFactory(new PooledConnectionFactory(factory));//sorry for the confusion, everything seems to be named 'factory'...
		}
		else throw new IllegalArgumentException("Cannot create fcgi handler : did you provide any configuration ?");
		
		return handler;
	}
	
	private static ConnectionFactory buildConnectionFactoryForClass(String className)
	{
		try
		{
			return (ConnectionFactory)Class.forName(className).newInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
