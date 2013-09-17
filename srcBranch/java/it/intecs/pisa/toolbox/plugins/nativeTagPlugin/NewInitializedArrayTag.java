package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;

public class NewInitializedArrayTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element initializedArray) throws Exception {
        LinkedList children = DOMUtil.getChildren(initializedArray);
        Object array = Array.newInstance(Object.class, children.size());
        Iterator iterator = children.iterator();
        for (int index = 0;
                index < children.size();
                Array.set(array, index++,
                        this.executeChildTag((Element) iterator.next()))) {
            ;
        }
        return array;
    }
}
