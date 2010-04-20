package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class PrimitiveTypeTag extends NativeTagExecutor {
    private static final String TYPE = "type";
    private static final String BYTE = "byte";
    private static final String SHORT = "short";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";

    @Override
    public Object executeTag(org.w3c.dom.Element primitiveType) throws Exception {
        String type = primitiveType.getAttribute(TYPE);
        if (type.equals(BYTE)) {
            return byte.class;
        }
        if (type.equals(SHORT)) {
            return short.class;
        }
        if (type.equals(INT)) {
            return int.class;
        }
        if (type.equals(LONG)) {
            return long.class;
        }
        if (type.equals(FLOAT)) {
            return float.class;
        }
        if (type.equals(DOUBLE)) {
            return double.class;
        }
        if (type.equals(CHAR)) {
            return char.class;
        }
        if (type.equals(BOOLEAN)) {
            return boolean.class;
        }
        return null;
    }
}
