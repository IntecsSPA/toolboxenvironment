package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class SleepTag extends NativeTagExecutor{
           protected static final String AMOUNT = "amount";
          
	@Override
	public Object executeTag(org.w3c.dom.Element sleep) throws Exception{
                   Thread.sleep(Integer.parseInt(sleep.getAttribute(AMOUNT)));
        return null;
    }

   

}
