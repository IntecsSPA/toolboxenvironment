package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.TypesValues;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class MethodInvocationTag extends NativeTagExecutor {
        protected static final String PARAMETER = "parameter";
        protected static final String METHOD_NAME = "methodName";
        
    @Override
    public Object executeTag(org.w3c.dom.Element methodInvocation) throws Exception {
        TypesValues typesValues = new TypesValues(this.engine, DOMUtil.getChildrenByLocalName(
                methodInvocation, PARAMETER));

        String methodName = this.engine.evaluateString(methodInvocation.getAttribute( METHOD_NAME),IEngine.EngineStringType.ATTRIBUTE);
        Element target = DOMUtil.getFirstChild(methodInvocation);
        if (target.getLocalName().equals(OBJECT)) {
            Object object = this.executeChildTag(DOMUtil.getFirstChild(target));
            return object.getClass().getMethod(methodName, typesValues.getTypes()).
                    invoke(object, typesValues.getValues());
        } else {
            return ((Class) executeChildTag(DOMUtil.getFirstChild(target))).getMethod(
                    methodName, typesValues.getTypes()).invoke(null, typesValues.getValues());
        }
    }
}
