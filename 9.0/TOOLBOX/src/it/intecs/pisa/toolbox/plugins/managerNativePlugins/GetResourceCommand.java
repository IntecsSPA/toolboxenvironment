/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetResourceCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try
        {
            IOUtil.copy(getResource(req),resp.getOutputStream());
        }
        catch(Exception e)
        {
              throw new GenericException("Cannot get Resource");
        }
    }

}
