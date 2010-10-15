

package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.LinkedList;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class StringSplitTag extends NativeTagExecutor{

    @Override
    public Object executeTag(org.w3c.dom.Element stringSplitElement) throws Exception {

        LinkedList splitParams= DOMUtil.getChildren(stringSplitElement);
        String stringSplit=String.valueOf(this.executeChildTag((Element) splitParams.get(0)));
        String stringSep=String.valueOf(this.executeChildTag((Element) splitParams.get(1)));

        return stringSplit.split(stringSep);
    }
}

