package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class CallTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element call) throws Exception {
        setArguments(DOMUtil.getChildrenByLocalName(call, ARGUMENT).iterator());

        return this.executeChildTag((Element) get(this.engine.evaluateString(call.getAttribute(PROCEDURE),IEngine.EngineStringType.ATTRIBUTE)));
    }
}
