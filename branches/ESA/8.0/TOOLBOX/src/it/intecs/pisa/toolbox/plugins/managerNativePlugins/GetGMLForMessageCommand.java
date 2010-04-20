/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class GetGMLForMessageCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        InputStream resourceStream;
        String serviceName;
        File stylesheet;
        DOMUtil util;
        Document xslDocument ;
        Transformer transformer;
        PrintWriter printer;
        OutputStream out;
        Document doc;
        Element boundedByElement;
        ServiceManager servMan;
        try
        {
            resp.setContentType("text/xml");
            serviceName = InstanceInfo.getServiceNameFromInstanceId(Long.parseLong(req.getParameter("instanceId")));

            servMan=ServiceManager.getInstance();

            stylesheet=new File(servMan.getService(serviceName).getServiceRoot(),"AdditionalResources/OutputOnMap/OUTPUTONMAPXSL.xsl");
            
            
                util=new DOMUtil();
                doc=util.newDocument();
                out=resp.getOutputStream();
                
                xslDocument=util.fileToDocument(stylesheet);
                transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
                  
                resourceStream = getResource(req);
               
                transformer.transform(new StreamSource(resourceStream), new DOMResult(doc));
                
                out.write("<wfs:FeatureCollection  xmlns:wfs=\"http://www.opengis.net/wfs\" >".getBytes());
                
               if(DOMUtil.hasElementNS(doc.getDocumentElement(), "boundedBy", "http://www.opengis.net/gml")==true)
               {
                        boundedByElement=DOMUtil.getChildByTagName(doc.getDocumentElement(),"gml:boundedBy");
                         IOUtil.copy(new XmlDirectivesFilterStream(DOMUtil.getNodeAsInputStream(boundedByElement)), out);
               }
                
                IOUtil.copy(new XmlDirectivesFilterStream(DOMUtil.getDocumentAsInputStream(doc)), out);
                out.write("</wfs:FeatureCollection>".getBytes());
            
        }
        catch(Exception e)
        {
           String errorMsg = "Cannot get GML for output message";
            throw new GenericException(errorMsg);
        }
    }

}
