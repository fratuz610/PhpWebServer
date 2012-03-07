/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.task;

import it.holiday69.phpwebserver.ui.MainUI;
import java.util.concurrent.Callable;
import javax.swing.SwingUtilities;

/**
 *
 * @author fratuz610
 */
public class DestroyMainUITask implements Callable<Void> {

  private MainUI _mainUI;
  
  public DestroyMainUITask(MainUI mainUI) {
    _mainUI = mainUI;
  }
  
  @Override
  public Void call() throws Exception {
    
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          _mainUI.dispose();
        }
      });
    return null;
  }
  
}
