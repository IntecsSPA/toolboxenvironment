package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.configuration.ToolboxNetwork;

public class ToolboxAddressTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element address) throws Exception {
        return ToolboxNetwork.getEndpointURL();
    }
    
 
}
