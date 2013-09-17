
package it.intecs.pisa.pep.rest.manager;

import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.constants.MiscConstants;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
@Path("manager/storefile")
public class ManagerStoreFileResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of ManagerStoreFileResource */
    public ManagerStoreFileResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.manager.ManagerStoreFileResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ManagerStoreFileResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Produces("text/plain")
    public String store(@Context HttpServletRequest request) {
     //   AuthenticationManager am=new AuthenticationManager();
        RestResponse storeResponse= new RestResponse("storeFile");
        String filePath="";
        ServletMultipartRequest parser=null;
       
        //try {
          /*  String authHeader=hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            
            if(am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)){*/
               try {
                    parser = new ServletMultipartRequest(request, MiscConstants.MAX_READ_BYTES, MultipartRequest.ABORT_IF_MAX_BYES_EXCEEDED, null);
                    String storeFileName=parser.getFileParameterNames().nextElement().toString();
                    File outputFile=File.createTempFile(storeFileName, "tmp");
                    IOUtil.copy(parser.getFileContents(storeFileName), new FileOutputStream(outputFile));
                    filePath=outputFile.getCanonicalPath();
                  
                    filePath = filePath.replaceAll("\\\\", "\\\\\\\\");


                } catch (Exception ex) {
                   storeResponse.setSuccess(Boolean.FALSE);
                   storeResponse.setDetails(ex.getMessage());
                   return storeResponse.getStringRestResponse();
                }
           /* }else
              return JsonUtil.getJsonAsString(am.notAuthorizatedResponse()); */
            
       /* } catch (Exception ex) {
           ex.printStackTrace(); 
           return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }    */
       // storeResponse.setSuccess(Boolean.TRUE); 
         storeResponse.addJsonProperty("success", "true");
        //Extjs workeraound       
        return "\"success\":true , \"filePath\" : \""+filePath +"\"";//storeResponse.getStringRestResponse();
    }
}
