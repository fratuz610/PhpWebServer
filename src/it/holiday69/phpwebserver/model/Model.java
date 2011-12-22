package it.holiday69.phpwebserver.model;

import it.holiday69.phpwebserver.vo.ConfigObject;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class Model {

  private static Model instance;

	private Model() {
		
	}
	public static synchronized Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

  /*---------------------------
   * STATIC CONSTANTS
   *-------------------------*/

  public ConfigObject configObject = new ConfigObject();
  
  /*---------------------------
   * EVENTS
   *-------------------------*/

  public final Object shutdownEvent = new Object();
  
  public void notifyShutdown() {
    synchronized(shutdownEvent) {
      shutdownEvent.notifyAll();
    }
  }
  
  public void waitForShutdown() {
    synchronized(shutdownEvent) {
      try { shutdownEvent.wait(); } catch(InterruptedException ex) { }
    }
  }
    
}
