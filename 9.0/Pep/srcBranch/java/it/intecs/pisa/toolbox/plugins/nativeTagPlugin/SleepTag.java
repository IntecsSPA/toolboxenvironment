package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

public class SleepTag extends NativeTagExecutor{
           protected static final String AMOUNT = "amount";
          
	@Override
	public Object executeTag(org.w3c.dom.Element sleep) throws Exception{
                   Thread.sleep(Integer.parseInt(sleep.getAttribute(AMOUNT)));
        return null;
    }

   

}
