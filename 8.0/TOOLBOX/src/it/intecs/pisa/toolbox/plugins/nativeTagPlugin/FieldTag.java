package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class FieldTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element field) throws Exception {
        String fieldName;
        Element target;

        fieldName = this.engine.evaluateString(field.getAttribute(FIELD_NAME), IEngine.EngineStringType.ATTRIBUTE);
        target = DOMUtil.getFirstChild(field);

        if (target.getLocalName().equals(OBJECT)) {
            Object object = this.executeTag(DOMUtil.getFirstChild(target));
            return object.getClass().getField(fieldName).get(object);
        } else {
            return ((Class) this.executeChildTag(DOMUtil.getFirstChild(target))).getField(
                    fieldName).get(null);
        }

    }
}
