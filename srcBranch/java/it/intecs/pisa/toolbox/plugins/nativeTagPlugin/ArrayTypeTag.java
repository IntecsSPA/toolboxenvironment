package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;

public class ArrayTypeTag extends NativeTagExecutor {
   

    @Override
    public Object executeTag(org.w3c.dom.Element arrayType) throws Exception {
        int dimensions = Integer.parseInt(this.engine.evaluateString(arrayType.getAttribute(DIMENSIONS),IEngine.EngineStringType.ATTRIBUTE));
        StringBuffer buffer = new StringBuffer();
        while (dimensions-- > 0) {
            buffer.append(ARRAY_DIMENSION_SYMBOL);
        }
        return Class.forName(buffer.append(getClassName((Class) this.executeChildTag(
                DOMUtil.getFirstChild(arrayType)))).toString());
    }
}
