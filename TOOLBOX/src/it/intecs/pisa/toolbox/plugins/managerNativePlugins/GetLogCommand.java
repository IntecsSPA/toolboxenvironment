package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Andrea Marongiu
 */
public class GetLogCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String resourceRelativePath=req.getParameter("relativePath");
        File resourceabsolutePath=null;
        File serviceRoot=null;
        serviceRoot=tbxServlet.getRootDir();
        resourceabsolutePath=new File(serviceRoot,resourceRelativePath);

                    try {
                    resp.setHeader("Content-Disposition", "inline; filename="+resourceabsolutePath.getName() );
                    IOUtil.copy(new FileInputStream(resourceabsolutePath), resp.getOutputStream());
                    } catch (Exception ex) {
                        throw new GenericException("Cannot get Resource");
                    }


    }

}
