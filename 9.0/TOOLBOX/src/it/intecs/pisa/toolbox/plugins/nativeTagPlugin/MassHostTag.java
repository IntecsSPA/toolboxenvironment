package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

public class MassHostTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element massHost) throws Exception {
     Object result = get(MASS_HOST);
      
        return result;
    }
    
 
}
