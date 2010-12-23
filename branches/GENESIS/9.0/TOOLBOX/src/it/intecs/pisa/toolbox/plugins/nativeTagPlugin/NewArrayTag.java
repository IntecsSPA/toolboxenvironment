package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.util.DOMUtil;
import java.lang.reflect.Array;

public class NewArrayTag extends NativeTagExecutor{
   
          
	@Override
	public Object executeTag(org.w3c.dom.Element newArray) throws Exception{
                    return Array.newInstance(Object.class, getInt(DOMUtil.getFirstChild(newArray)));
    }

   

}
