package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.lang.reflect.Array;

public class ArrayLengthTag extends NativeTagExecutor {
   

    @Override
    public Object executeTag(org.w3c.dom.Element arrayLength) throws Exception {
        Object result = new Integer(Array.getLength(this.executeChildTag(DOMUtil.getFirstChild(
                arrayLength))));
       
        return result;
    }
}
