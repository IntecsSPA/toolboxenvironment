package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Date;
import java.util.LinkedList;
import org.w3c.dom.Element;

public class IsBeforeTag extends NativeTagExecutor {

    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element isBefore) throws Exception {
        Date first, second;

        LinkedList dates = DOMUtil.getChildren(isBefore);

        first = (Date) this.executeChildTag((Element) dates.get(0));
        second = (Date) this.executeChildTag((Element) dates.get(1));

        if (first.before(second) == true) {
            return new Boolean(true);
        } else {
            return new Boolean(false);
        }
    }
}
