package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.TypesValues;
import it.intecs.pisa.util.DOMUtil;

public class NewInternalObjectTag extends NativeTagExecutor {
    protected static final String PARAMETER = "parameter";
    protected static final String SUB_CLASS_SYMBOL = "$";
    protected static final String INTERNAL_CLASS = "internalClass";
        
    @Override
    public Object executeTag(org.w3c.dom.Element newInternalObject) throws Exception {
        Object object = this.executeChildTag(DOMUtil.getFirstChild(newInternalObject));
        TypesValues typesValues = new TypesValues( this.engine,object,  DOMUtil.getChildrenByLocalName(newInternalObject, PARAMETER));
        return Class.forName(object.getClass().getName() + SUB_CLASS_SYMBOL +
                newInternalObject.getAttribute(INTERNAL_CLASS)).
                getConstructor(typesValues.getTypes()).newInstance(typesValues.getValues());

    }
}
