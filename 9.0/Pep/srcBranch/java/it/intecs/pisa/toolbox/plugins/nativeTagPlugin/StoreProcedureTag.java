package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class StoreProcedureTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element storeProcedure) throws Exception {
        Element result = DOMUtil.getFirstChild(storeProcedure);

        put(this.engine.evaluateString(storeProcedure.getAttribute(NAME),IEngine.EngineStringType.ATTRIBUTE), result);
        return result;
    }
}
