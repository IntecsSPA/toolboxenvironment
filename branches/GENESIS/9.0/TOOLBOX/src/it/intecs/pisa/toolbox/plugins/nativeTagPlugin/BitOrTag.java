package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class BitOrTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element bitOr) throws Exception {
        int result = 0;
        Iterator values = DOMUtil.getChildren(bitOr).iterator();
        while (values.hasNext()) {
            result |= getInt((Element) values.next());
        }
        Object res = new Integer(result);

        return res;
    }
}
