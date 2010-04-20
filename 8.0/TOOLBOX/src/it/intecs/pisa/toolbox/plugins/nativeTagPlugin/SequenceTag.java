package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.util.LinkedList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SequenceTag extends NativeTagExecutor{
    
    /**
	 * 
	 * @param debugTag
	 * @param tagEl
	 */
	public Object executeTag( org.w3c.dom.Element tagEl) throws Exception{
		Object returnObject=null;
                LinkedList children=null;
                               
                children=DOMUtil.getChildren(tagEl);
                if(children!=null)
                    for(int i=0;i<children.size();i++)
                    {
                        returnObject=this.executeChildTag((Element)children.get(i));
                    }

                return returnObject;     
	}
}
