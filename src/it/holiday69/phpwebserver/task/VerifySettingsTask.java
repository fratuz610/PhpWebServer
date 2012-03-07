/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.holiday69.phpwebserver.task;

import it.holiday69.phpwebserver.model.Model;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class VerifySettingsTask implements Callable<Void> {

  private static final Logger log = Logger.getLogger(VerifySettingsTask.class.getSimpleName());
  
  private final Model _model = Model.getInstance();
  
  @Override
  public Void call() throws Exception {
    
    File wwwFolder = new File(_model.configObject.wwwFolder);
    if(!wwwFolder.exists())
      wwwFolder.mkdir();

    if(!wwwFolder.exists())
      throw new Exception("Unable to use a www folder and I cannot create it: " + wwwFolder.getAbsolutePath());
    
    testSocketPortLocally(_model.configObject.fastCGIPort);
    testSocketPortLocally(_model.configObject.httpPort);
    
    return null;
  }
  
  private void testSocketPortLocally(int port) throws Exception {
    
    ServerSocket httpSocket = null;
    try {
      httpSocket = new ServerSocket(port);
      httpSocket.close();
    } catch(Throwable ex) {
      throw new Exception("The port: " + port + " is already in use");
    }
  }


}
