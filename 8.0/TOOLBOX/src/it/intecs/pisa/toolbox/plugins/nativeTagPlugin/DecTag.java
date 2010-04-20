package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class DecTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dec) throws Exception {
        Element target =null; 
        Object value =null;
        Number number=null;
 
        target=DOMUtil.getFirstChild(dec);
        number=(Number) this.executeChildTag(target);
        value = new Integer( number.intValue() - 1);
        
        setLogic(target, value);
        
        return value;
    }
}
