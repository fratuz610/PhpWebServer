/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.task;

import com.google.gson.Gson;
import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.vo.ConfigObject;
import it.holiday69.tinyutils.help.IOHelper;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

/**
 *
 * @author fratuz610
 */
public class ReadConfigTask implements Callable<Void> {

  private final Model _model = Model.getInstance();
  
  @Override
  public Void call() throws Exception {
    
    try {
      FileInputStream in = new FileInputStream("config.json");
      String configString = new IOHelper().readAsString(in);
      _model.configObject = new Gson().fromJson(configString, ConfigObject.class);
      in.close();
    } catch(Throwable th) {
      
    }
    return null;
  }

}
