package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;

public class IfTag extends NativeTagExecutor{

	@Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
            Object result = null;
            Iterator values = null;
            boolean condition=false;    
            Element thenElement =null;
  
            values = DOMUtil.getChildren(tagEl).iterator();
            condition = getBool((Element) values.next());
            thenElement = (Element) values.next();
            if (condition) {
                result = executeChildTag(thenElement);
            } else if (values.hasNext()) {
                result = executeChildTag((Element) values.next());
            }
            return result;
    }

   

}
