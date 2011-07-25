package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class StringCatTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element stringCat) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Iterator values = DOMUtil.getChildren(stringCat).iterator();
        while (values.hasNext()) {
            buffer.append(this.executeChildTag((Element) values.next()));
        }
        String result = buffer.toString();
       
        return result;
    }
}
