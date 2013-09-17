package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class GteTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element gte) throws Exception {
      Iterator children = DOMUtil.getChildren(gte).iterator();
        Object result = new Boolean(getInt((Element) children.next()) >=
                getInt((Element) children.next()));
      
        return result;
    }
    

}
