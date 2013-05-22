/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.handler;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;

/**
 *
 * @author mariarosaria
 */
public class EncryptedDataStoreHandler extends AbstractHandler {

    public static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY = "Security";
    static public String ENCRYPTED_DATA_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
    static public String ENCRYPTED_DATA = "EncryptedData";

    @Override
    public InvocationResponse invoke(MessageContext msgCtx) throws AxisFault {

        SOAPEnvelope envelope = msgCtx.getEnvelope();

        SOAPHeader soapHeader = envelope.getHeader();

        if (soapHeader != null) {

            OMElement wsSecurity = soapHeader.getFirstChildWithName(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY));
            if (wsSecurity != null) {

                OMElement encryptedData = wsSecurity.getFirstChildWithName(new QName(ENCRYPTED_DATA_NAMESPACE, ENCRYPTED_DATA));
                if (encryptedData != null) {
                    msgCtx.setProperty(ENCRYPTED_DATA, encryptedData);
                }
            }
        }
        return InvocationResponse.CONTINUE;
    }
}
