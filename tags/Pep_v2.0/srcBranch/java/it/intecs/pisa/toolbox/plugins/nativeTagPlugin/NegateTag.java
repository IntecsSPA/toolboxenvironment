package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class NegateTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element negate) throws Exception {
        Object result = new Integer(-getInt(DOMUtil.getFirstChild(negate)));

        return result;
    }

  
}
