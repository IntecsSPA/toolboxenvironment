package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;


public class LiteralTag extends NativeTagExecutor {

    private static final String VALUE = "value";
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
    public Object executeTag(Element literal) throws Exception {
        String type = literal.getAttribute(TYPE);
        if (type.equals(BYTE)) {
            return Byte.valueOf(literal.getAttribute(VALUE));
        }
        if (type.equals(SHORT)) {
            return Short.valueOf(literal.getAttribute(VALUE));
        }
        if (type.length() == 0 || type.equals(INT)) {
            return Integer.valueOf(literal.getAttribute(VALUE));
        }
        if (type.equals(LONG)) {
            return Long.valueOf(literal.getAttribute(VALUE));
        }
        if (type.equals(FLOAT)) {
            return Float.valueOf(literal.getAttribute(VALUE));
        }
        if (type.equals(DOUBLE)) {
            return Double.valueOf(literal.getAttribute(VALUE));
        }
        if (type.equals(CHAR)) {
            return new Character((char) Integer.parseInt(literal.getAttribute(
                    VALUE)));
        }
        if (type.equals(BOOLEAN)) {
            return Boolean.valueOf(literal.getAttribute(VALUE));
        }
        return null;
    }
}
