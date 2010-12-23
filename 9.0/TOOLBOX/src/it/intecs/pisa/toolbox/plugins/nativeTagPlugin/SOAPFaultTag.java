package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.util.DOMUtil;

import org.apache.axis2.AxisFault;
import org.apache.axis2.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SOAPFaultTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element soapFault) throws Exception {
        AxisFault axisFault;
        //SOAPFault sFault;
        Document faultDoc;
        Element firstChild;

        firstChild=DOMUtil.getFirstChild(soapFault);

        axisFault=new AxisFault((String) this.executeChildTag(firstChild));
        //axisFault.setFaultActor("TOOLBOX 8.0");
        axisFault.setNodeURI("TOOLBOX 8.0");
        //axisFault.setFaultString((String) this.executeChildTag(firstChild));
        axisFault.addReason((String) this.executeChildTag(firstChild));
        
        //axisFault.setFaultDetail(null);
        return XMLUtils.toDOM(axisFault.getFaultNodeElement().getFirstElement()).getOwnerDocument();
        /*
        sFault=new SOAPFault(axisFault);

        faultDoc=sFault.getAsDocument();
        return faultDoc;*/
    }
    
 
}
