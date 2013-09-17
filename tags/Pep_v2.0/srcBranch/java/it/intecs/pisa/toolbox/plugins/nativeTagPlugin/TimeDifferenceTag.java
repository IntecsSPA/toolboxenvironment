package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;
import it.intecs.pisa.util.DOMUtil;
import java.util.Date;
import java.util.Iterator;

public class TimeDifferenceTag extends NativeTagExecutor{

	@Override
	public Object executeTag( Element tagEl) throws Exception{
            Iterator children=DOMUtil.getChildren(tagEl).iterator();
        
            Date firstDate=(Date)this.executeChildTag((Element) children.next());
            Date secondDate=(Date)this.executeChildTag((Element) children.next());

            long difference=firstDate.getTime()-secondDate.getTime();

            return new Long(difference);
	}
        
}
