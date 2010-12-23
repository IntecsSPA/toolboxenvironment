/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.resources.TextResourcesPersistence;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetTextResourceCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try
        {
            String id=req.getParameter("id");

            TextResourcesPersistence instance=TextResourcesPersistence.getInstance();
            File file=instance.getTextFile(new Long(id));
            IOUtil.copy(new FileInputStream(file),resp.getOutputStream());
        }
        catch(Exception e)
        {
              throw new GenericException("Cannot get Resource");
        }
    }

}
