package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class NotTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element not) throws Exception {
        Object value = this.executeChildTag(DOMUtil.getFirstChild(not));
        Object result;
        if (value instanceof Boolean) {
            result = new Boolean(!((Boolean) value).booleanValue());

            return result;
        }
        result = new Integer(~((Integer) value).intValue());

        return result;
    }
}
