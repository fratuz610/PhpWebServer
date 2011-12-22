/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.task;

import com.google.gson.Gson;
import it.holiday69.phpwebserver.model.Model;
import it.holiday69.phpwebserver.vo.ConfigObject;
import it.holiday69.tinyutils.help.IOHelper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

/**
 *
 * @author fratuz610
 */
public class WriteConfigTask implements Callable<Void> {

  private final Model _model = Model.getInstance();
  
  @Override
  public Void call() throws Exception {
    
    try {
      File configFile = new File("config.json");

      if(configFile.exists())
        configFile.delete();

      configFile.createNewFile();

      FileOutputStream out = new FileOutputStream(configFile);

      String configString = new Gson().toJson(_model.configObject);
      new IOHelper().copy(new ByteArrayInputStream(configString.getBytes()), out);

      out.close();
    } catch(Throwable th) {
      
    }
    return null;
  }

}
