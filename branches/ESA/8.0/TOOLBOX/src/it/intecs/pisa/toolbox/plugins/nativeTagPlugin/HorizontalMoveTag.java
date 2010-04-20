package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class HorizontalMoveTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element horizontalMove) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

       Element child = DOMUtil.getFirstChild(horizontalMove);
       
        textNavigator.horizontalMove(((child != null) ? getInt(child) : 1),
                getBool(horizontalMove.getAttribute(
                ABSOLUTE)));
        return null;
    }
}
