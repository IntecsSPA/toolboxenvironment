package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.lang.reflect.Array;
import java.util.Iterator;
import org.w3c.dom.Element;

public class ArrayLocationTag extends NativeTagExecutor {
   

    @Override
    public Object executeTag(org.w3c.dom.Element arrayLocation) throws Exception {
        Iterator iterator = DOMUtil.getChildren(arrayLocation).iterator();
        return getArrayLocation(this.executeChildTag((Element) iterator.next()), iterator);
    }
}
