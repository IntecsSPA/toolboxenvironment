/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import be.kzen.ergorr.model.csw.DeleteType;
import be.kzen.ergorr.model.csw.InsertType;
import be.kzen.ergorr.model.csw.QueryConstraintType;
import be.kzen.ergorr.model.csw.TransactionResponseType;
import be.kzen.ergorr.model.csw.TransactionType;
import be.kzen.ergorr.model.csw.UpdateType;
import be.kzen.ergorr.model.ogc.FilterType;
import be.kzen.ergorr.model.util.JAXBUtil;
import be.kzen.ergorr.model.util.OFactory;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class TransactionTag extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        CswClient client;
        Object opType=null;
        try
        {
            String operationType = this.engine.evaluateString(connEl.getAttribute("type"), IEngine.EngineStringType.ATTRIBUTE);

            LinkedList<Element> children = DOMUtil.getChildren(connEl);
            client=(CswClient) this.executeChildTag(children.get(0));

            Document xmlSnippet;
            xmlSnippet= (Document) this.executeChildTag(children.get(1));

            TransactionType t = new TransactionType();

            if(operationType.equals("DELETE"))
            {
                DeleteType delete = new DeleteType();
                delete.setTypeName(evaluateAttribute(connEl, "typeName"));

                QueryConstraintType con=new QueryConstraintType();
                con.setVersion("");
                delete.setConstraint(con);

                JAXBElement<FilterType> filter=(JAXBElement<FilterType>) JAXBUtil.getInstance().unmarshall(DOMUtil.getDocumentAsInputStream(xmlSnippet));
                con.setFilter(filter.getValue());

                opType=delete;
            }else if(operationType.equals("INSERT"))
            {
                InsertType insert=new InsertType();
                insert.getAny().add(JAXBUtil.getInstance().unmarshall(DOMUtil.getDocumentAsInputStream(xmlSnippet)));
                opType=insert;
            }/*else
            {
                UpdateType update=new UpdateType();
                update.getAny().add(JAXBUtil.getInstance().unmarshall(DOMUtil.getDocumentAsInputStream(xmlSnippet)));
                opType=update;
            }*/

            t.getInsertOrUpdateOrDelete().add(opType);
            TransactionResponseType response = client.transact(t);

            File tempFile = IOUtil.getTemporaryFile();

            JAXBUtil.getInstance().marshall(OFactory.csw.createTransactionResponse(response), new FileOutputStream(tempFile));

            DOMUtil util;
            util=new DOMUtil();
            Document xmlDoc = util.fileToDocument(tempFile);
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
