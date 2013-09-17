package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class LtTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element lt) throws Exception {
        Iterator children = DOMUtil.getChildren(lt).iterator();
        Object result = new Boolean(getInt((Element) children.next()) <
                getInt((Element) children.next()));
      
        return result;
    }
    
   
    
}
