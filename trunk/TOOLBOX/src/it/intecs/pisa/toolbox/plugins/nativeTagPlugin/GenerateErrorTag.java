package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class GenerateErrorTag extends NativeTagExecutor{
    
          
	@Override
	public Object executeTag(org.w3c.dom.Element generateError) throws Exception{
                     throw new Exception(String.valueOf(executeChildTag(DOMUtil.getFirstChild(
                generateError))));
    }

   

}
