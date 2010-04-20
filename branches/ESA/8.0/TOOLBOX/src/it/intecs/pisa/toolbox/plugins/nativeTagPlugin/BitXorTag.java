package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class BitXorTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element bitXor) throws Exception {
        int result = 0;
        Iterator values = DOMUtil.getChildren(bitXor).iterator();
        while (values.hasNext()) {
            result ^= getInt((Element) values.next());
        }
        Object res = new Integer(result);
    
        return res;
    }
}
