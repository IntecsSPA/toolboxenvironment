package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class XorTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element xor) throws Exception {
        boolean result = false;
        Iterator values = DOMUtil.getChildren(xor).iterator();
        while (values.hasNext()) {
            result ^= getBool((Element) values.next());
        }
        Object res = new Boolean(result);
  
        return res;
    }
}
