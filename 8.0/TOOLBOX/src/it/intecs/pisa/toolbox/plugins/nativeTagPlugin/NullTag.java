package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class NullTag extends NativeTagExecutor{
  
	@Override
	public Object executeTag(org.w3c.dom.Element date) throws Exception{
              return null;
    }

   

}
