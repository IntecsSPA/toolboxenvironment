package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class PrintTag extends NativeTagExecutor {
private static final String NEW_LINE = "newLine";

    @Override
    public Object executeTag(org.w3c.dom.Element print) throws Exception {
        Object result = null;

        result = this.executeChildTag(DOMUtil.getFirstChild(print));


        if (getBool(print.getAttribute(NEW_LINE))) {
            System.out.println(result);
        } else {
            System.out.print(result);
        }

        return result;
    }
}
