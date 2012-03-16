/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.core;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 *
 * @author fratuz610
 */
public class ProcessWatchdog implements Runnable, Callable<Void> {

  private final static Logger log = Logger.getLogger(ProcessWatchdog.class.getSimpleName());
  
  private List<String> _argList;
  private Process _process;
  
  public ProcessWatchdog(List<String> argList) {
    _argList = argList;
  }
  
  @Override
  public Void call() throws Exception {
    startProcess();
    return null;
  }
  
  @Override
  public void run() {
    
    if(_process == null)
      return;
    
    while(true) {
    
      
      if(Thread.currentThread().isInterrupted())
        break;
      
      try {
        _process.waitFor();
      } catch(InterruptedException ex) {
        break;
      }
      
      log.info("The process: " + _argList.get(0) + " has shutdown, restarting it");
      
      try { 
        startProcess();
      } catch(Exception ex) { 
        log.warning("Unable to restart process: " + _argList.get(0) + " closing down");
      }
    }
    
    
    if(_process != null) {
      
      log.info("Destroying process...");
      
      try {
        _process.destroy();
      } catch(Throwable th) { }
    }
    
    log.info("Closing down: " + _argList.get(0));
  }
  
  private void startProcess() throws Exception {
    
    ProcessBuilder pb = new ProcessBuilder(_argList);

    try {
      _process = pb.start();
    } catch(IOException ex) {
      throw new Exception("Unable to start process: " + _argList.get(0) + " because: " + ex.getMessage());
    }
  }

  
  
}
