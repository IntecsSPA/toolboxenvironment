/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano
 */
public class GetTimerScriptCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String expirationDate = "";
        String serviceName="";
        String output="";
        Document script;
        File xsltFile;

        try {
            expirationDate = req.getParameter("expirationDate");
            serviceName=req.getParameter("serviceName");
            output=req.getParameter("output");

            ServiceManager servMan;

            servMan=ServiceManager.getInstance();
            TBXService serv=servMan.getService(serviceName);

            DOMUtil util;
            util=new DOMUtil();
            Document timerDoc=util.inputStreamToDocument(serv.getTimerStatus());

            Element rootEl=timerDoc.getDocumentElement();
            NodeList children = rootEl.getChildNodes();

            for(int i=0;i<children.getLength();i++)
            {
                Node ithNode;

                ithNode=children.item(i);
                if(ithNode instanceof Element)
                {
                    Element el=(Element) ithNode;
                    String exprDate=el.getAttribute("expirationDateTime");
                    if(exprDate.equals(expirationDate))
                    {
                        NodeList scriptList=el.getElementsByTagNameNS("http://pisa.intecs.it/mass/toolbox/timerStatus", "script");
                        Element statusEl=(Element)scriptList.item(0);

                        if(output!=null && output.equals("text"))
                        {
                            xsltFile=new File(tbxServlet.getRootDir(),"WEB-INF/XSL/resourceDisplay.xsl");
                            resp.setContentType("text/html");

                            Document newDoc=DOMUtil.getElementAsNewDocument(statusEl);
                            XSLT.transform(new StreamSource(xsltFile), new DOMSource(newDoc), new StreamResult(resp.getOutputStream()));
                        }
                        else
                        {
                            sendXMLAsResponse(resp, DOMUtil.getFirstChild(statusEl));
                        }
                        
                    }
                }
            }
            // sendXMLAsResponse(resp, script);
        } catch (Exception ex) {
            String errorMsg = "Error getting timer. Details: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
