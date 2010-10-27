/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Andrea Marongiu
 */
public class DeleteServiceResourceCommand extends NativeCommandsManagerPlugin{
     public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        String serviceName=req.getParameter("serviceName");
        String resourceRelativePath=req.getParameter("relativePath");
        File resourceabsolutePath=null;
        File serviceRoot=null;
      
            
                serviceRoot=tbxServlet.getServiceRoot(serviceName);
                resourceabsolutePath=new File(serviceRoot,resourceRelativePath); 
                if(!resourceabsolutePath.getCanonicalPath().startsWith(serviceRoot.getCanonicalPath()))
                  throw new GenericException("Unauthorized access: \n Resource Absolute Path: " + resourceabsolutePath.getCanonicalPath()+
                                   "\n Service Root Absolute Path: " + serviceRoot.getCanonicalPath() );
                else{
                    try {
                    resp.setHeader("Content-Type", "text/json" );
                    IOUtil.rmdir(resourceabsolutePath);
                    resp.getWriter().print("{\"success\": true}");
                    } catch (Exception ex) {
                        throw new GenericException("Cannot Delete Resource");
                    }
                }
            

        }
}



