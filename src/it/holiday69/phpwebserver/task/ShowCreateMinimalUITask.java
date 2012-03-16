/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.task;

import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.ui.MinimalUI;
import java.util.concurrent.Callable;
import javax.swing.SwingUtilities;

/**
 *
 * @author fratuz610
 */
public class ShowCreateMinimalUITask implements Callable<MinimalUI> {

  private MinimalUI _minimalUI;
  private Model _model = Model.getInstance();
  
  @Override
  public MinimalUI call() throws Exception {
    
     _minimalUI = new MinimalUI();
    
    // shows the Main UI
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          _minimalUI.setVisible(true);
          _minimalUI.setLocationRelativeTo(null);
        }
      });
    
    return _minimalUI;
  }
  
}
