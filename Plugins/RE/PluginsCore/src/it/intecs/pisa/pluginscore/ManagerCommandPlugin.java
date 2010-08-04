/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public abstract class ManagerCommandPlugin implements IManagerPlugin{
    protected File pluginDir;

    public boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
        return true;
    }

    public boolean validateInput(HttpServletRequest req, HttpServletResponse resp) {
        return true;
    }
    
    public ManagerCommandPlugin()
    {
        
    }
    
      public void setPluginDirectory(File dir) throws Exception
      {
          pluginDir=dir;
      }
   
}
