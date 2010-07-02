
package it.intecs.pisa.toolbox.plugins.wpsPlugin.commands;


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
public class WPSProcessingUpdateCommand extends NativeCommandsManagerPlugin{

 
    private static DOMUtil domUtil= new DOMUtil();

     public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document serviceInformationDocument=null;
        String processingName="";
        String serviceName=req.getParameter("serviceName");
        processingName=req.getParameter("processingName");
        String async=req.getParameter("async");
        String engineType=req.getParameter("engineType");
        String step=req.getParameter("step");
        Document documentResponse=null;
        boolean notWellFormed=false;
        WPSCommands commands= new WPSCommands();

        /* ------- UPDATE DESCRIBE PROCESS -------------*/
        if(step.equalsIgnoreCase("updateDescribe")){
            try{
            serviceInformationDocument = domUtil.inputStreamToDocument(req.getInputStream());
            }catch(Exception ex){
                notWellFormed=true;
                WPSCommandResponse createResponse=new WPSCommandResponse(WPSCommands.PARSE_DESCRIBE_OP);
                createResponse.insertErrorValidation("XML Document is not well-formed. " + ex.getMessage());
                documentResponse=createResponse.getDocumentResponse();
            }
            if(!notWellFormed)
                documentResponse=commands.updateWPSDescribeProcess(serviceInformationDocument,serviceName,
                        processingName,engineType,Boolean.parseBoolean(async));
        }else{
            /* ------- UPDATE SCRIT ENGINE -------------*/
            if(step.equalsIgnoreCase("updateScriptEngine")){     
               documentResponse=commands.updateWPSProcessEngineScript(serviceName, processingName,
                   Boolean.getBoolean(async), engineType, req.getInputStream());
            }

        }
        resp.getOutputStream().print(DOMUtil.getDocumentAsString(documentResponse));

    }

}
