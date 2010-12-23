package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class OrTag extends NativeTagExecutor {

    private static final String SHORTCUT = "shortcut";

    @Override
    public Object executeTag(org.w3c.dom.Element or) throws Exception {
        boolean shortcut = getBool(or.getAttribute(SHORTCUT));
        boolean result = false;
        Iterator values = DOMUtil.getChildren(or).iterator();
        if (shortcut) {
            while (values.hasNext() && !result) {
                result |= getBool((Element) values.next());
            }
        } else {
            while (values.hasNext()) {
                result |= getBool((Element) values.next());
            }
        }
        Object res = new Boolean(result);
       
        return res;
    }
}
