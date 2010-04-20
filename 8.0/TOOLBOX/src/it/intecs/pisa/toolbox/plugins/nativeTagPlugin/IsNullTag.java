package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class IsNullTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element isNull) throws Exception {
     Object result = new Boolean(this.executeChildTag(DOMUtil.getFirstChild(isNull)) == null);
    
        return result;
    }
    

}
