package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class FieldMoveTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element fieldMove) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

        Element child = DOMUtil.getFirstChild(fieldMove);
        String positionString = fieldMove.getAttribute(POSITION);
        int position;
        if (positionString.equals(START)) {
            position = TextNavigator.START;
        } else {
            position = TextNavigator.END;
        }
        textNavigator.fieldMove(
                ((child != null) ? getInt(child) : 1),
                fieldMove.getAttribute(SEPARATORS),
                getBool(fieldMove.getAttribute(GREEDY)),
                getBool(fieldMove.getAttribute(ABSOLUTE)),
                position);
        return null;
    }
}
