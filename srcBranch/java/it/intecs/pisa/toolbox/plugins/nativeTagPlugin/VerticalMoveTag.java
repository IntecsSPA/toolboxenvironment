package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class VerticalMoveTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element verticalMove) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

      Element child = DOMUtil.getFirstChild(verticalMove);
        String positionString = verticalMove.getAttribute(POSITION);
        int position;
        if (positionString.equals(START)) {
            position = TextNavigator.START;
        } else if (positionString.equals(END)) {
            position = TextNavigator.END;
        } else {
            position = TextNavigator.COLUMN;
        }
        textNavigator.verticalMove(((child != null) ? getInt(child) : 1),
                getBool(verticalMove.getAttribute(ABSOLUTE)),
                position);
        return null;
    }
}
