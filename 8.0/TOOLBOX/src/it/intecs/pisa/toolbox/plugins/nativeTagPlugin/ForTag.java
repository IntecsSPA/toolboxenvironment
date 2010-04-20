package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ForTag extends NativeTagExecutor {
    protected static final String FOR_CONDITION = "condition";
    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element forElement) throws Exception {
        Object result = null;
        Iterator children = DOMUtil.getChildren(forElement).iterator();
        Element init = (Element) children.next();
        Element condition;
    
        
        if (init.getLocalName().equals(FOR_CONDITION)) {
            condition = init;
            init = null;
        } else {
            condition = (Element) children.next();
        }
        if (init != null) {
            result = this.executeChildTag(init);
        }
        Element increment = (Element) children.next();
        Element body = children.hasNext() ? (Element) children.next() : null;
        if (body == null) {
            while (getBool(DOMUtil.getFirstChild(condition))) {
                removeLastChildNode();
                removeLastChildNode();
                
                result = this.executeChildTag(increment);
            }
        } else {
            while (getBool(DOMUtil.getFirstChild(condition))) {
                 removeLastChildNode();
                 removeLastChildNode();
                 removeLastChildNode();
                  
                this.executeChildTag(body);
                result = this.executeChildTag(increment);    
            }
        }
        return result;
    }

   
}
