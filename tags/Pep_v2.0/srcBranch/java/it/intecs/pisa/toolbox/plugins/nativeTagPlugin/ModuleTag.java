package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class ModuleTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element module) throws Exception {
        Iterator children = DOMUtil.getChildren(module).iterator();
        Object result = new Integer(getInt((Element) children.next()) %
                getInt((Element) children.next()));
       
        return result;
        
       
    }
    
   
}
