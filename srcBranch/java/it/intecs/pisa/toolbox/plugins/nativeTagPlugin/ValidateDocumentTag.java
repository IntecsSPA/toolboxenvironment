package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ValidateDocumentTag extends NativeTagExecutor{
        
	@Override
	public Object executeTag(org.w3c.dom.Element validateElement) throws Exception{
                Boolean isValid;
        LinkedList children;
        String path;
        Document docToValidate;
        DOMUtil domUtilNS = new DOMUtil(true);

        //setting return value, negative value is returned after exception catching
        isValid = new Boolean(true);

        try {
            children = DOMUtil.getChildren(validateElement);

            //getting xml document and schema file path
            docToValidate = (Document) this.executeChildTag((Element) children.get(0));
            path = (String) executeChildTag((Element) children.get(1));

            //going to validate document
            domUtilNS.validateDocument(new File(path), docToValidate);
        } catch (Exception ecc) {
            isValid = new Boolean(false);
        }

        return isValid;
    }

   

}
