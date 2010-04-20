package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class LocalhostTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element and) throws Exception {
        Object result = java.net.InetAddress.getLocalHost().getHostAddress();

        return result;
    }
}
