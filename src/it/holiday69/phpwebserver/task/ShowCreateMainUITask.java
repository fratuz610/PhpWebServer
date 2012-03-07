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
public class ShowCreateMainUITask implements Callable<MainUI> {

  private MainUI _mainUI;
  
  @Override
  public MainUI call() throws Exception {
    
     _mainUI = new MainUI();
    
    // shows the Main UI
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          _mainUI.setVisible(true);
          _mainUI.setLocationRelativeTo(null);
        }
      });
    
    return _mainUI;
  }
  
}
