package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.soap.toolbox.TypesValues;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class NewObjectTag extends NativeTagExecutor {
    private static final String PARAMETER = "parameter";
      
    @Override
    public Object executeTag(org.w3c.dom.Element newObject) throws Exception {
         TypesValues typesValues = new TypesValues( this.engine, DOMUtil.getChildrenByLocalName(newObject, PARAMETER));
        return ((Class) this.executeChildTag(DOMUtil.getFirstChild(newObject))).getConstructor(typesValues.getTypes()).newInstance(typesValues.getValues());
    }
}
