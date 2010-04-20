package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.exceptions.ReturnTagException;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class ReturnTag extends NativeTagExecutor{
     
    @Override
    public Object executeTag(org.w3c.dom.Element returnEl) throws Exception {
            Object returnedValue=null;
            
            returnedValue=this.executeChildTag(DOMUtil.getFirstChild(returnEl));
           throw new ReturnTagException(returnedValue);
    }
}
