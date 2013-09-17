package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class ItoaTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element itoa) throws Exception {
        String result = String.valueOf(getInt(DOMUtil.getFirstChild(itoa)));

        return result;
    }
}
