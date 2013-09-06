

package it.intecs.pisa.toolbox.plugins.wpsPlugin.commands;


import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.NativeCommandsManagerPlugin;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSCommandResponse;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSCommands;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSProcessingCreateCommand extends NativeCommandsManagerPlugin {

 
     private static DOMUtil domUtil= new DOMUtil();



    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document serviceInformationDocument=null;
        String processingName, processingTitle, processingVersion;
        String asynchronous;
     
        String serviceName=req.getParameter("serviceName");
        String step=req.getParameter("step");
        Document documentResponse=null;
        WPSCommands commands= new WPSCommands();
        boolean notWellFormed=false;

        /* ------- PARSE DESCRIBE PROCESS -------------*/
        if(step.equalsIgnoreCase("parseDescribe")){
            try{
            serviceInformationDocument = domUtil.inputStreamToDocument(req.getInputStream());
            }catch(Exception ex){
                notWellFormed=true;
                WPSCommandResponse createResponse=new WPSCommandResponse(WPSCommands.PARSE_DESCRIBE_OP);
                createResponse.insertErrorValidation("XML Document is not well-formed. "+ ex.getMessage());
                documentResponse=createResponse.getDocumentResponse();
            }
            if(!notWellFormed)
               documentResponse=commands.parseWPSDescribeProcess(serviceInformationDocument,serviceName, pluginDir);
        }else{
            /* ------- GENERATE OPERATION -------------*/
            if(step.equalsIgnoreCase("generateOperation")){
               processingName=req.getParameter("processingName");
               processingTitle=req.getParameter("processingTitle");
               processingVersion=req.getParameter("processingVersion");
               asynchronous=req.getParameter("asynchronous");
               String engineType=req.getParameter("engineType");
               documentResponse=commands.createWPSProcess(serviceName,
                    processingName,
                    processingTitle, processingVersion,  
                    Boolean.valueOf(asynchronous), engineType, req.getInputStream());
            }
           
        }
        resp.getOutputStream().print(DOMUtil.getDocumentAsString(documentResponse));

    }

   

}
