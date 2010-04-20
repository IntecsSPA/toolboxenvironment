package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class AtoiTag extends NativeTagExecutor {

    private static final String SHORTCUT = "shortcut";

    @Override
    public Object executeTag(org.w3c.dom.Element atoi) throws Exception {
        Object result = new Integer(this.executeChildTag(DOMUtil.getFirstChild(atoi)).toString());

        return result;
    }
}
