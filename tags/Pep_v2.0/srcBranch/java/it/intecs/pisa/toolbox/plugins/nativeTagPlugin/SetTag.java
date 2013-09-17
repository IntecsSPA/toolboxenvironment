package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;

public class SetTag extends NativeTagExecutor {
    
     
    @Override
    public Object executeTag(org.w3c.dom.Element element) throws Exception {
        Iterator children = DOMUtil.getChildren(element).iterator();
        Element target = (Element) children.next();
        Object value = this.executeChildTag((Element) children.next());
        setLogic(target, value);
        return value;
    }

     
}
