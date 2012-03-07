/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.task;

import it.holiday69.phpwebserver.model.Model;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 *
 * @author fratuz610
 */
public class AddShutdownHookTask implements Callable<Void> {

  private final Logger log = Logger.getLogger(AddShutdownHookTask.class.getSimpleName());
  
  @Override
  public Void call() throws Exception {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      @Override
      public void run() {
        log.info("Shutdown hook triggered, closing down");
        Model.getInstance().notifyShutdown();
      }
    }));
    
    log.info("Shutdown hook added, waiting for shutdown signal");
    return null;
  }
  
}
