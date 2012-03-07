/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.phpwebserver.vo;

/**
 *
 * @author fratuz610
 */
public class ConfigObject {
  public int httpPort = 9080;
  public int fastCGIPort = 9079;
  public String startupURL = "http://127.0.0.1:" + httpPort + "/index.php";
  public String wwwFolder = "www";
}
