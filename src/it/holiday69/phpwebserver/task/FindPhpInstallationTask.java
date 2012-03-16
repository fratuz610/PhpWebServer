/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.holiday69.phpwebserver.task;

import it.holiday69.phpwebserver.model.Model;
import it.holiday69.tinyutils.ExceptionUtils;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class FindPhpInstallationTask implements Callable<Void> {

  private static final Logger log = Logger.getLogger(FindPhpInstallationTask.class.getSimpleName());
  
  private final Model _model = Model.getInstance();
          
  private Class<?> _mainClass;
  
  public FindPhpInstallationTask(Class<?> mainClass) {
    _mainClass = mainClass;
  }
  
  @Override
  public Void call() throws Exception {
    
    if(_model.isWindows())
      findPhpInstallationWindows();
    else if(_model.isMac())
      findPhpInstallationMac();
    else
      throw new Exception("The current platform: " + _model.getOsName() + " is unsupported");
    
    return null;
  }
  
  private void findPhpInstallationWindows() throws Exception {
    
    String thisFolder;
    try {
      thisFolder= getThisJARFolder(_mainClass);
    } catch(Exception ex) {
      log.severe("Unable to determine the current folder: " + ExceptionUtils.getFullExceptionInfo(ex));
      throw new Exception("Unable to determine the current folder:\n" + ex.getMessage());
    }
    
    log.info("Current running folder: " + thisFolder);
    
    File phpFolder = new File(thisFolder, "php");
    
    if(!phpFolder.exists())
      throw new Exception("Unable to find php folder: " + phpFolder.getAbsolutePath());
    
    File phpCGIExecutable = new File(phpFolder, "php-cgi.exe");
    
    if(!phpCGIExecutable.exists())
      throw new Exception("Unable to find the php-cgi executable: " + phpCGIExecutable.getAbsolutePath());
    
    log.info("Setting php cgi executable to: " + _model.phpCGIExecutable);
    
    _model.phpCGIExecutable = phpCGIExecutable;
  }
  
  private void findPhpInstallationMac() throws Exception {
    
    String thisFolder;
    try {
      thisFolder= getThisJARFolder(_mainClass);
    } catch(Exception ex) {
      log.severe("Unable to determine the current folder: " + ExceptionUtils.getFullExceptionInfo(ex));
      throw new Exception("Unable to determine the current folder:\n" + ex.getMessage());
    }
    
    log.info("Mac os x environment detected: current running folder: " + thisFolder);
    
    File phpFolder = new File(thisFolder, "php");
    
    if(!phpFolder.exists())
      throw new Exception("Unable to find php folder: " + phpFolder.getAbsolutePath());
    
    File phpCGIExecutable = new File(phpFolder, "bin/php-cgi");
    
    if(!phpCGIExecutable.exists())
      throw new Exception("Unable to find the php-cgi executable: " + phpCGIExecutable.getAbsolutePath());
    
    _model.phpCGIExecutable = phpCGIExecutable;
    
    log.info("Setting php cgi executable to: " + _model.phpCGIExecutable);    
  }
  
  private String getThisJARFolder(Class refClass) throws Exception {
    
    // get name and path
    String name = refClass.getName().replace('.', '/');
    
    log.info("Jar name: " + name);
    
    name = refClass.getClass().getResource("/" + name + ".class").toString();
    
    log.info("Jar name (x2): " + name);
    
    // removes the head
    if(name.indexOf("file:/") == -1)
      throw new Exception("Internal Error: no 'file:/' section found in the current class OS path");
    
    if(_model.isWindows())
      name = name.substring(name.indexOf("file:/")+6);
    else
      name = name.substring(name.indexOf("file:/")+5);
    
    log.info("Jar name (x2): " + name);
    
    // removes what's after PhpWebServer etc
    if(name.lastIndexOf("PhpWebServer") == -1)
      throw new Exception("Internal Error: no 'PhpWebServer' section found in the current class OS path");
    
    name = name.substring(0, name.lastIndexOf("PhpWebServer"));
    
    return name;
  }

}
