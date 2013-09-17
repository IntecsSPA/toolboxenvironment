package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;

public class LocalhostTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element and) throws Exception {
        ToolboxConfiguration tbxConfig;

        return ToolboxConfiguration.getInstance().getConfigurationValue(ToolboxConfiguration.ENDPOINT_ADDRESS);
    }
}
