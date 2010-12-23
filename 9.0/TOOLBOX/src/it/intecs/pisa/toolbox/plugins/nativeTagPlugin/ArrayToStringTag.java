

package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Arrays;
import java.util.LinkedList;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class ArrayToStringTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element arrayToStringElement) throws Exception {

        LinkedList arrayUnionParams= DOMUtil.getChildren(arrayToStringElement);

        Object [] array=(Object[]) this.executeChildTag((Element) arrayUnionParams.get(0));

        String arrayString=null;
        arrayString=Arrays.toString(array);


       return arrayString;
    }


}
