package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DivideTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element divide) throws Exception {
        int result = 1;
        Iterator values = DOMUtil.getChildren(divide).iterator();
        if (values.hasNext()) {
            result = ((Number) this.executeChildTag((Element) values.next())).intValue();
            while (values.hasNext()) {
                result /= ((Number) this.executeChildTag((Element) values.next())).intValue();
            }
        }
        Object res = new Integer(result);
       
        return res;
    }
}
