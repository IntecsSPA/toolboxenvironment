/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import java.io.File;

/**
 *
 * @author Massimiliano
 */
public abstract class ManagerCommandPlugin implements IManagerPlugin{
    protected File pluginDir;
    
    public ManagerCommandPlugin()
    {
        
    }
    
      public void setPluginDirectory(File dir) throws Exception
      {
          pluginDir=dir;
      }
   
}
