package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class IsNotNullTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element isNotNull) throws Exception {
      Object result = new Boolean(this.executeChildTag(DOMUtil.getFirstChild(isNotNull)) != null);
        
        return result;
    }
    

}
