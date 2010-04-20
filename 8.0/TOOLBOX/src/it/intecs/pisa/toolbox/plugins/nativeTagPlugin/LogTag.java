package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogTag extends NativeTagExecutor {

    protected static final String CONFIGURATION_SERVICE_LOGGER = "CONFIGURATION_SERVICE_LOGGER";
    protected static final String FORMAT = "format";
    protected static final String LEVEL = "level";
    protected Logger logger;

    @Override
    public Object executeTag(org.w3c.dom.Element log) throws Exception {
        String logmessage;

        logmessage = String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(log)));

        logger = (Logger) this.engine.getConfigurationVariablesStore().getVariable(CONFIGURATION_SERVICE_LOGGER);
        logger.log(Level.toLevel(log.getAttribute(LEVEL)), logmessage);

        return logmessage;
    }
}
