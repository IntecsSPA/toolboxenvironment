package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class StringLengthTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element stringLength) throws Exception {
        Object result = new Integer(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(
                stringLength))).length());

        return result;
    }
}
