/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.schemas.ToolboxServiceSchemaEntityResolver;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.XMLSerializer2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class TBXSOAPInterface extends Interface{
    private DocumentBuilder validatingParser;

    public TBXSOAPInterface()
    {
        super();
    }

    @Override
    protected Operation[] getOperationsDefinition(Element parentEl) {
        LinkedList operationList;
        Element operationEl;
        int count = 0;
        Operation[] operationsDef;
        String opType;

        operationList = DOMUtil.getChildrenByTagName(parentEl, TAG_OPERATION);
        count = operationList.size();

        operationsDef = new TBXOperation[count];
        for (int j = 0; j < count; j++) {
            operationEl = (Element) operationList.get(j);

            opType=operationEl.getAttribute(ATTRIBUTE_TYPE);

            if(opType.equals(Operation.OPERATION_TYPE_SYNCHRONOUS))
                operationsDef[j] = new TBXSynchronousOperation();
            else
                operationsDef[j] = new TBXAsynchronousOperation();

            operationsDef[j].initFromXMLDocument(operationEl);
        }

        return operationsDef;
    }

 /*   @Override
    public void addOperations(Operation op) {
        TBXOperation newOp;

        if(op.getType().equals(Operation.OPERATION_TYPE_SYNCHRONOUS))
                newOp = new TBXSynchronousOperation(op);
            else
                newOp = new TBXAsynchronousOperation(op);

        super.addOperations(newOp);
    }*/

    public Document validateDocument(Document document) throws Exception {
        File schemaFile;
        File fullPathSchemaFile;
        File serviceRoot;

        if(schemaRoot!=null && schemaRoot.equals("")==false)
        {
            serviceRoot=((TBXService)this.parentService).getServiceRoot();
            schemaFile=new File(serviceRoot,schemaDir);
            fullPathSchemaFile=new File(schemaFile,schemaRoot);

            validatingParser=DOMUtil.getValidatingParser(fullPathSchemaFile);
            validatingParser.setEntityResolver(new ToolboxServiceSchemaEntityResolver(schemaFile));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new XMLSerializer2(out).serialize(document);
            out.close();

                //entityResolver.resetFetchedSchemas();
             document = validatingParser.parse(new ByteArrayInputStream(out.toByteArray()));
        }

        return document;
    }

    public boolean hasWSDL()
    {
        return hasSchema();
    }

    public boolean hasSchema()
    {
        if(schemaRoot==null || schemaRoot.equals(""))
            return false;
        else return true;
    }

   
}
