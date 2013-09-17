package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;

/**
 * This class shall be used to be inherited by other classes. it implements basic functionalities
 * for reading/writing messages over network
 * @author Massimiliano
 *
 */
public class StructuredMessage {
		
        protected Document doc;
              
        public void initFromDocument(Document doc)  {}
        
        public Document getDoc()
        {
            return null;
        }

}
