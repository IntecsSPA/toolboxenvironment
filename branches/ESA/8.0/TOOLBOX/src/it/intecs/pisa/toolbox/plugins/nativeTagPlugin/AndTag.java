package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class AndTag extends NativeTagExecutor{
          
	@Override
	public Object executeTag(org.w3c.dom.Element and) throws Exception{
                   boolean shortcut = getBool(and.getAttribute(SHORTCUT));
        boolean result = true;
        Iterator values = DOMUtil.getChildren(and).iterator();
        if (shortcut) {
            while (values.hasNext() && result) {
                result &= getBool((Element) values.next());
            }
        } else {
            while (values.hasNext()) {
                result &= getBool((Element) values.next());
            }
        }
        Object res = new Boolean(result);
       
        return res;
    }

   

}
