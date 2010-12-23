package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlGetResponseTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element xmlGetResponse) throws Exception {
       DOMUtil domUtilNS = new DOMUtil(true);
       Element getOperationOutput = ((Document) this.executeChildTag(DOMUtil.getFirstChild(xmlGetResponse))).getDocumentElement();
        String targetNamespace = getOperationOutput.getNamespaceURI();
        String getOperationOutputPrefix = getOperationOutput.getPrefix();
        String XML_RESPONSE_ROOT;
        Document xmlResponse = domUtilNS.newDocument();
        String OPERATION = (String) get(XmlGetResponseTag.OPERATION);
        Element xmlResponseRoot;
        String operationMode;
        if ((operationMode = (String) get(OPERATION_MODE)) != null &&
                operationMode.equals(SYNCHRONOUS)) {
            XML_RESPONSE_ROOT = PROCESS + OPERATION + OUTPUT_MSG;
            xmlResponseRoot = (Element) xmlResponse.appendChild(xmlResponse.createElementNS(targetNamespace,
                    (getOperationOutputPrefix == null ? XML_RESPONSE_ROOT : getOperationOutputPrefix + ":" +
                    XML_RESPONSE_ROOT)));
        } else {
            XML_RESPONSE_ROOT = RETURN + OPERATION + RESULT + INPUT_MSG;
            Element commonInput = xmlResponse.createElementNS(MASS_NAMESPACE,
                    //                    (getOperationOutputPrefix == null ? COMMON_INPUT :
                    //                     getOperationOutputPrefix + ":" + COMMON_INPUT));
                    "mass" + ':' + COMMON_INPUT);
            commonInput.setAttribute("xmlns:mass", MASS_NAMESPACE);
            Element orderId;
            (orderId = xmlResponse.createElementNS(MASS_NAMESPACE,
                    //                    (getOperationOutputPrefix == null ? ORDER_ID :
                    //                     getOperationOutputPrefix + ":" + ORDER_ID))
                    "mass" + ':' + ORDER_ID)).appendChild(xmlResponse.createTextNode((String) get(ORDER_ID)));
            commonInput.appendChild(orderId);
            xmlResponseRoot = (Element) xmlResponse.appendChild(xmlResponse.createElementNS(targetNamespace,
                    (getOperationOutputPrefix == null ? XML_RESPONSE_ROOT : getOperationOutputPrefix + ":" +
                    XML_RESPONSE_ROOT)));
            xmlResponseRoot.appendChild(commonInput);
        }
        getOperationOutput = (Element) xmlResponse.importNode(
                getOperationOutput, true);
        DOMUtil.moveAttributeNodes(getOperationOutput, xmlResponseRoot);
        xmlResponseRoot.appendChild(getOperationOutput);
        return xmlResponse;
    }
    
 
}
