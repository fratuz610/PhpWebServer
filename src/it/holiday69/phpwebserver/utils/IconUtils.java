/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.holiday69.phpwebserver.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class IconUtils {

  public static void associateIcons(JFrame frame, String ... iconRefList) throws Exception {

    List<Image> iconList = new ArrayList<Image>();
    
    for(String iconRef : iconRefList) {
      BufferedImage icon = ImageIO.read(IconUtils.class.getResource(iconRef));
      iconList.add(icon);
    }
    
    frame.setIconImages(iconList);
    
    
  }
  
  public static void associateIcons(JWindow window, String ... iconRefList) throws Exception {
    
    List<Image> iconList = new ArrayList<Image>();
    
    for(String iconRef : iconRefList) {
      BufferedImage icon = ImageIO.read(IconUtils.class.getResource(iconRef));
      iconList.add(icon);
    }
    
    window.setIconImages(iconList);
  }


}
