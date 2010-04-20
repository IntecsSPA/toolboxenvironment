package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class OneqTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element oneq) throws Exception {
       Iterator children = DOMUtil.getChildren(oneq).iterator();
        Object o1 = this.executeChildTag((Element) children.next());
        Object o2 = this.executeChildTag((Element) children.next());
        Object result = new Boolean(o1 != o2 &&
                (o1 == null || o2 == null || !o1.equals(o2)));
        
        return result;
    }
    

}
