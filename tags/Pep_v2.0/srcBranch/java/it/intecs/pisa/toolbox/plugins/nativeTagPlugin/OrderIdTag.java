package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

public class OrderIdTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element orderId) throws Exception {
      Object result = get(ORDER_ID);
        
        return result;
    }
    
 
}
