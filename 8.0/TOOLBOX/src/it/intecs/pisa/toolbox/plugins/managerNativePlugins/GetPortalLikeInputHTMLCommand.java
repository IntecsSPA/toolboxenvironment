/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.sseportal.simulation.MessageForXSLCreator;
import it.intecs.pisa.toolbox.sseportal.simulation.PortalXSLTransformer;
import java.io.File;
import java.io.OutputStream;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetPortalLikeInputHTMLCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String service;
        String operation,toolboxUrl,aoi;
        OutputStream out;
        Document portalXML = null;
        File serviceRoot;
        File propsDir;
        ServiceAdditionalParameters servAddParameters;
        String portalNodeNamespace;
        String portalNodeName;
        Random rand;
        
        try {
            rand=new Random();
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html; charset=utf-8");

            toolboxUrl = req.getParameter("toolboxUrl");
            service = req.getParameter("serviceName");
            operation = req.getParameter("operationName");
            aoi = req.getParameter("AOI");
            
            String  formSSEEnvelope="<HTML><HEAD>"+
                      "<link rel=\"stylesheet\" href=\"jsScripts/dom.css\">"+
                      "<link rel=\"stylesheet\" href=\"jsScripts/dom.directory.css\">"+
                      "<link rel=\"stylesheet\" href=\"jsScripts/portal.css\">"+
                      "<link rel=\"stylesheet\" href=\"jsScripts/styles.css\">"+
                      "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS\"  href=\"http://services.eoportal.org/portal/system/getRSSFeed.do\" />"+
                      "<META http-equiv=\"Pragma\" content=\"no-cache\">"+
                      "<META http-equiv=\"Cache-Control\" content=\"no-store, no-cache, must-revalidate, post-check=0, pre-check=0\">"+
                      "<META http-equiv=\"Expires\" content=\"0\">"+
                      "<script  language=\"JavaScript\" src=\"http://services.eoportal.org/portal/template/portalScripts.js\"></script>"+
                      "<script  language=\"JavaScript\" src=\"http://services.eoportal.org/portal/template/CommonScript.js\"></script>"+
                      "<script  language=\"JavaScript\" src=\"http://services.eoportal.org/portal/template/interactivity.js\"></script>"+
                      "</HEAD><BODY>"+
                      "<form name=\"MASS\" id=\"MASSFORM\" method=\"POST\" onSubmit=\"return parent.tbxTCenter.sseManager('"+toolboxUrl+"','"+service+"','"+operation+"','"+aoi+"');\" action=\"#\">"+
                          "<div id=\"usedParameters\" style=\"display:none\">This text is hidden</div>"+
                          "<input type=\"hidden\" name=\"startRequest\"      value=\"\"/>"+
                          "<input type=\"hidden\" name=\"ServiceUrl\"      value=\""+toolboxUrl+"/manager?cmd=executePortalLike\"/>"+
                          "<input type=\"hidden\" name=\"LogFolder\"      value=\"/SSE_TEST\"/>"+
                          "<input type=\"hidden\" name=\"serviceName\"      value=\""+service+"\"/>"+
                          "<input type=\"hidden\" name=\"operationName\"      value=\""+operation+"\"/>"+
                          "<input type=\"hidden\" name=\"idRequest\"      value=\""+service+"','"+operation+"','"+aoi+"\"/>"+
                          "<input type=\"hidden\" name=\"Protocol\"      value=\"HTTPPOST\"/>"+
                          "<input type=\"hidden\" name=\"XSLResponse\"      value=\"\"/>"+
                          "<input type=\"hidden\" name=\"XSLRequest\"      value=\"\"/>"+
                          "<input type=\"hidden\" name=\"startSearch\"       value=\"\"/>"+
                          "<input type=\"hidden\" name=\"endRequest\"        value=\"\"/>"+
                          "<input type=\"hidden\" name=\"footMessage\"        value=\"\"/>"+
                          "<input type=\"hidden\" name=\"resultFrameName\"   value=\"\"/>"+
                          "<input type=\"hidden\" name=\"operationSynchronousFlag\" value=\"false\"/>"+
                          "<input type=\"hidden\" name=\"serviceId\" value='43804C89' />"+
                          "<input type=\"hidden\" name=\"operationName\" value='' />"+
                          "<input type=\"hidden\" name=\"serviceName\" value='' />"+
                          "<input type=\"hidden\" id=\"AOI\" name=\"AOI\" value='' />"+
                          "<input type=\"hidden\" name=\"orderId\" value=\""+rand.nextInt()+"\" />";

            serviceRoot = tbxServlet.getServiceRoot(service);
            propsDir = new File(serviceRoot, "AdditionalResources/SSEPortalXSL");

            servAddParameters = new ServiceAdditionalParameters(propsDir, "INTECS_TEST_OPERATION");
            portalNodeNamespace = servAddParameters.getParameter(operation + ".PORTALXMLNAMESPACE");
            portalNodeName = servAddParameters.getParameter(operation + ".PORTALXMLNODE");

            portalXML = MessageForXSLCreator.getPortalTemplateXML(portalNodeNamespace, portalNodeName, null);
            out = resp.getOutputStream();
            out.write(formSSEEnvelope.getBytes());
            
            PortalXSLTransformer.transform(service, operation, "INPUTHTMLTARGET", portalXML, out);
            out.write("<input type=\"submit\" value=\"Send Request\" name=\"SendRequest\" /></form></BODY></HTML>".getBytes());
            out.flush();
        } catch (Exception ex) {
             String errorMsg = "Error getting portal like input HTML page: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
