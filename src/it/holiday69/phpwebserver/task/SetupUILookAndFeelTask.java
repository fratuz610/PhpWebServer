/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.holiday69.phpwebserver.task;

import java.util.concurrent.Callable;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class SetupUILookAndFeelTask implements Callable<Void> {

  private static final Logger log = Logger.getLogger(SetupUILookAndFeelTask.class.getSimpleName());
  
  @Override
  public Void call() throws Exception {
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { }
    return null;
  }


}
