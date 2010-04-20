package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class RSShiftTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element rSShift) throws Exception {
         Iterator children = DOMUtil.getChildren(rSShift).iterator();
        Object result = new Integer(getInt((Element) children.next()) >>
                getInt((Element) children.next()));
       
        return result;
    }
}
