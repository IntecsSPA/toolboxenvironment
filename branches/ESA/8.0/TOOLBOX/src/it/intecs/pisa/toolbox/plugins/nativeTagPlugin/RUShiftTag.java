package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class RUShiftTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element rUShift) throws Exception {
         Iterator children = DOMUtil.getChildren(rUShift).iterator();
        Object result = new Integer(getInt((Element) children.next()) >>>
                getInt((Element) children.next()));
        
        return result;
    }
}
