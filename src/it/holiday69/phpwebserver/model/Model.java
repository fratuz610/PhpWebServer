package it.holiday69.phpwebserver.model;

import it.holiday69.phpwebserver.vo.ConfigObject;
import it.holiday69.tinyutils.TimeFormatUtils;
import it.holiday69.tinyutils.help.Timestamp;
import java.io.File;
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
  public File phpCGIExecutable = null; 
  
  private String _osName = System.getProperty("os.name");
  
  public String getOsName() { return _osName; }
  public boolean isMac() { return _osName.toLowerCase().indexOf("mac") != -1; }
  public boolean isWindows() { return _osName.toLowerCase().indexOf("windows") != -1; }
  public boolean isLinux() { return _osName.toLowerCase().indexOf("linux") != -1; }
  
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
  
  public void logToScreen(String debugString) { 
    logQueue.offer(getFormattedTS() + ": "+ debugString); 
  }
  public void logErrorToScreen(String errorString) { 
    errorString = errorString.trim();
    logQueue.offer("<span style='color:#ff0000'><b>" + getFormattedTS() + ": " + errorString + "</b></span>"); 
  }
  public void logHttpRequest(int code, String path) { 
    String logReqString = "HTTP " + code + ":"+ path.trim();
    
    if(code > 399)
      logErrorToScreen(logReqString);
    else
      logQueue.offer("<span style='color:#0000ff'>" + getFormattedTS() + ": " + logReqString + "</span>"); 
  }
  
  private String getFormattedTS() {
    return TimeFormatUtils.formatTimestamp(new Timestamp().toLong(),"HH:mm:ss:SSS");
  }

}
