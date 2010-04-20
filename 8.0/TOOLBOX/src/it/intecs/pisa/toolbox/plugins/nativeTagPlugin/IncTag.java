package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class IncTag extends NativeTagExecutor{

	@Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
             Element target = null;
             Object value=null;
             int childValue=0;
             
             target=DOMUtil.getFirstChild(tagEl);
             
             childValue=((Number) this.executeChildTag(target)).intValue();
             value=new Integer(childValue+1);
    
           setLogic(target, value);
            
            return value;
    }

   

}
