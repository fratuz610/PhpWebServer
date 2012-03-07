package it.holiday69.phpwebserver.model;

import it.holiday69.phpwebserver.vo.ConfigObject;
import java.util.concurrent.LinkedBlockingQueue;

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

  private final Object _eventObject = new Object();
  public enum Event { SHUTDOWN, RESTART };
  private Event _lastEvent;
  
  public void notifyShutdown() {
    synchronized(_eventObject) {
      _lastEvent = Event.SHUTDOWN;
      _eventObject.notifyAll();
    }
  }
  
  public void notifyRestart() {
    synchronized(_eventObject) {
      _lastEvent = Event.RESTART;
      _eventObject.notifyAll();
    }
  }
  
  public void waitForEvent() {
    synchronized(_eventObject) {
      try { _eventObject.wait(); } catch(InterruptedException ex) { }
    }
  }
  
  public Event getLastEvent() { return _lastEvent; }
  
  public final LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<String>();
  
  public void logToScreen(String debugString) { logQueue.offer(debugString); }
  public void logErrorToScreen(String errorString) { logQueue.offer("<span style='color:#ff0000'>" + errorString + "</span>"); }
}
