/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.CswBackendClient;
import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import be.kzen.ergorr.model.csw.AbstractQueryType;
import be.kzen.ergorr.model.csw.GetRecordsResponseType;
import be.kzen.ergorr.model.csw.GetRecordsType;
import be.kzen.ergorr.model.csw.ResultType;
import be.kzen.ergorr.model.rim.AdhocQueryType;
import be.kzen.ergorr.model.util.JAXBUtil;
import be.kzen.ergorr.model.util.OFactory;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class GetRecordsTag extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        CswClient client;
        Object clnt;
        LinkedList children;
        Document xmlDoc;
        String maxRecords;
        String startPosition;
        String resultType;
        JAXBElement<? extends AbstractQueryType> query;
        DOMUtil util;
        File tempFile;
        GetRecordsResponseType response;
        Object genericQuery;
        Object queryClass;
        try
        {
            util = new DOMUtil();
            maxRecords = this.engine.evaluateString(connEl.getAttribute("maxRecords"), IEngine.EngineStringType.ATTRIBUTE);
            startPosition = this.engine.evaluateString(connEl.getAttribute("startPosition"), IEngine.EngineStringType.ATTRIBUTE);
            resultType= this.engine.evaluateString(connEl.getAttribute("resultType"), IEngine.EngineStringType.ATTRIBUTE);

            children = DOMUtil.getChildren(connEl);
            client=(CswClient) this.executeChildTag((Element) children.get(0));

            genericQuery=this.executeChildTag((Element) children.get(1));

            GetRecordsType request = new GetRecordsType();
            JAXBElement jaxbEl;

            jaxbEl=(JAXBElement) genericQuery;
            queryClass=jaxbEl.getValue();

            if(queryClass instanceof AdhocQueryType)
            {
                request.setAny(OFactory.rim.createAdhocQuery((AdhocQueryType) queryClass));
            }
            else
            {
                request.setAbstractQuery((JAXBElement<? extends AbstractQueryType>)genericQuery);
            }
            
            request.setOutputFormat("application/xml");
            request.setMaxRecords(new BigInteger(maxRecords));
            request.setStartPosition(new BigInteger(startPosition));
            request.setResultType(ResultType.fromValue(resultType));

            response = client.getRecords(request);
            
            tempFile = IOUtil.getTemporaryFile();

            JAXBUtil.getInstance().marshall(OFactory.csw.createGetRecordsResponse(response), new FileOutputStream(tempFile));

            xmlDoc = util.fileToDocument(tempFile);
            tempFile.delete();

            return xmlDoc;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        
        //JAXBUtil.getInstance().marshall(response, new FileOutputStream("/home/massi/TEMP/GetRecordsResponse.xml"));
        
    }

    
}
