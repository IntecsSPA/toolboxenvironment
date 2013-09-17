/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.commons.CommonProperties;
import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import be.kzen.ergorr.model.csw.HarvestResponseType;
import be.kzen.ergorr.model.csw.HarvestType;
import be.kzen.ergorr.model.util.JAXBUtil;
import be.kzen.ergorr.model.util.OFactory;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class HarvestTag extends TagExecutor {
    public static final String ATTRIBUTE_RESOURCE_TYPE = "resourceType";
    public static final String ATTRIBUTE_HARVEST_INTERVAL = "harvestInterval";

    @Override
    public Object executeTag(org.w3c.dom.Element harvestEl) throws Exception {
        CswClient client;
        LinkedList children;
        Document xmlDoc=null;
        DOMUtil util;
        File tempFile;
        HarvestType request;
        String documentURI;
        String resourceType;
        String harvestInterval;
        Object connObj;
        HarvestResponseType response;
        CommonProperties commonProps;
        try
        {
            util = new DOMUtil();
       
            children = DOMUtil.getChildren(harvestEl);
            client = (CswClient)this.executeChildTag((Element) children.get(0));
            documentURI=(String) this.executeChildTag((Element) children.get(1));

            if(harvestEl.hasAttribute(ATTRIBUTE_RESOURCE_TYPE))
            {
                 resourceType=evaluateAttribute(harvestEl,ATTRIBUTE_RESOURCE_TYPE);
            }
            else resourceType=null;

            if(harvestEl.hasAttribute(ATTRIBUTE_HARVEST_INTERVAL))
            {
                 harvestInterval=evaluateAttribute(harvestEl,ATTRIBUTE_HARVEST_INTERVAL);
            }
            else harvestInterval=null;

            request=new HarvestType();
            request.setSource(documentURI);
            
            if(resourceType!=null)
                request.setResourceType(resourceType);

            System.out.println("REPO: "+System.getProperty("ergorr.common.properties"));
           /* if(harvestInterval!=null)
                request.setHarvestInterval();*/
            
            response = client.harvest(request);
           
            tempFile = IOUtil.getTemporaryFile();

            JAXBUtil.getInstance().marshall(OFactory.csw.createHarvestResponse(response), new FileOutputStream(tempFile));

            xmlDoc = util.fileToDocument(tempFile);
            tempFile.delete();

            return xmlDoc;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        
      
        
    }

    
}
