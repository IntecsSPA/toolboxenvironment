package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;

public class NewTypedArrayTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element newArray) throws Exception {
        LinkedList children = DOMUtil.getChildren(newArray);
        Class itemType = (Class) this.executeChildTag((Element) children.removeFirst());
        int[] dimensions = new int[children.size()];
        Iterator iterator = children.iterator();
        for (int index = 0;
                index < dimensions.length;
                dimensions[index++] = getInt((Element) iterator.next())) {
            ;
        }
        return Array.newInstance(itemType, dimensions);
    }
}
