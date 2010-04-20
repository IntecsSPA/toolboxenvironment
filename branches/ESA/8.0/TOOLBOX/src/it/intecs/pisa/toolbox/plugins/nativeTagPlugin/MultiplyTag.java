package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class MultiplyTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element multiply) throws Exception {
        int result = 1;
        Iterator values = DOMUtil.getChildren(multiply).iterator();
        while (values.hasNext()) {
            result *= ((Number) this.executeChildTag((Element) values.next())).intValue();
        }
        return new Integer(result);
    }
}
