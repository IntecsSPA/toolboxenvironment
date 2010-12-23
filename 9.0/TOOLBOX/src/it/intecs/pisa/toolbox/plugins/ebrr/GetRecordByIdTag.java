/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import be.kzen.ergorr.model.csw.AbstractQueryType;
import be.kzen.ergorr.model.csw.ElementSetNameType;
import be.kzen.ergorr.model.csw.ElementSetType;
import be.kzen.ergorr.model.csw.GetRecordByIdResponseType;
import be.kzen.ergorr.model.csw.GetRecordByIdType;
import be.kzen.ergorr.model.util.JAXBUtil;
import be.kzen.ergorr.model.util.OFactory;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class GetRecordByIdTag extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        CswClient client;
        LinkedList children;
        Document xmlDoc;
        JAXBElement<? extends AbstractQueryType> query;
        File tempFile;
        DOMUtil util;
        int childrenCount=0;
        Object objToAdd;
        String elementSetName;
        GetRecordByIdResponseType response;
        try
        {
        util=new DOMUtil();

        elementSetName = this.engine.evaluateString(connEl.getAttribute("elementSetName"), IEngine.EngineStringType.ATTRIBUTE);

        children = DOMUtil.getChildren(connEl);
        childrenCount=children.size();

        client = (CswClient)  this.executeChildTag((Element) children.get(0));

        GetRecordByIdType request = new GetRecordByIdType();

        for(int i=1;i<childrenCount;i++)
        {
            objToAdd= this.executeChildTag((Element) children.get(i));

            if(objToAdd instanceof String)
                request.getId().add((String)objToAdd);
            else if(objToAdd instanceof String[])
            {
                String[] values;

                values=(String[])objToAdd;
                for(int j=0;j<values.length;j++)
                    request.getId().add(values[j]);
            }
        }
        
        ElementSetNameType setName = new ElementSetNameType();
        setName.setValue(ElementSetType.fromValue(elementSetName));
        request.setElementSetName(setName);


        dumpIds(request.getId().iterator());

        response = client.getRecordById(request);
           
        tempFile=IOUtil.getTemporaryFile();

        JAXBUtil.getInstance().marshall(OFactory.csw.createGetRecordByIdResponse(response),new FileOutputStream(tempFile));

        xmlDoc=util.fileToDocument(tempFile);
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

    private void dumpIds(Iterator<String> ids) {
       while(ids.hasNext())
       {
           System.out.println("Adding id: "+ids.next());
       }
    }

   
}
