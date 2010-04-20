package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;

public class OeqTag extends NativeTagExecutor{

	@Override
	public Object executeTag(Element tagEl) throws Exception{
             Iterator children = DOMUtil.getChildren(tagEl).iterator();
            Object o1 = executeChildTag((Element) children.next());
            Object o2 = executeChildTag((Element) children.next());
            Object result = new Boolean(o1 == o2 || o1 != null && o2 != null && o1.equals(o2));
            return result;
    }

   

}
