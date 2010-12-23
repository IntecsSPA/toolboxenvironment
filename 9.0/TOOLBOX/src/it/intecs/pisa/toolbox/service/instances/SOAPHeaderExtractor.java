/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package it.intecs.pisa.toolbox.service.instances;

import it.intecs.pisa.soap.toolbox.SOAPUtils;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class SOAPHeaderExtractor {

    public static final String ELEMENT_ADDRESS = "Address";
    protected static final String NAMESPACE_ADDRESSING = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    protected static final String NAMESPACE_ADDRESSING_2 ="http://www.w3.org/2005/08/addressing";
    protected static final String ELEMENT_MESSAGE_ID = "MessageID";
    protected static final String ELEMENT_REPLY_TO = "ReplyTo";
    private String namespace;
    private String messageId;
    private String replyTo_address;

    public SOAPHeaderExtractor(Document doc) throws Exception {
        extractInfo(doc);
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return the replyTo_address
     */
    public String getReplyTo_address() {
        return replyTo_address;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }


    private void extractInfo(Document doc) throws Exception {
        SOAPUtils soapUtil;
        SOAPMessage soapmes;
        SOAPHeader header;
        SOAPHeaderElement element;
        Iterator list;

        soapUtil = new SOAPUtils();
        soapmes = (SOAPMessage) soapUtil.getSOAPMessage(doc);

        header = soapmes.getSOAPHeader();
        if(header!=null)
        {
            list = header.examineAllHeaderElements();

            while (list.hasNext()) {
                element = (SOAPHeaderElement) list.next();

                extractInfoFromElement(element);
            }
        }
    }

    private void extractInfoFromElement(SOAPHeaderElement element) {
        QName qname;
        String elName;
        Iterator iter;
        SOAPElement subElement;

        
            qname = element.getElementQName();
            namespace = qname.getNamespaceURI();
            elName = qname.getLocalPart();

            if (namespace.equals(NAMESPACE_ADDRESSING) || namespace.equals(NAMESPACE_ADDRESSING_2)) {
                if (elName.equals(ELEMENT_MESSAGE_ID)) {
                    messageId = element.getValue();
                } else if (elName.equals(ELEMENT_REPLY_TO)) {
                    iter = element.getChildElements();
                    while(iter.hasNext())
                    {
                        Object obj;

                        obj=iter.next();
                        if(obj instanceof SOAPElement)
                        {
                            subElement = (SOAPElement) obj;
                            qname=subElement.getElementQName();
                            elName = qname.getLocalPart();
                            if(elName.equals(ELEMENT_ADDRESS))
                                replyTo_address = subElement.getValue();
                        }
                    }
                    

                    
                }
            }
    }
}
