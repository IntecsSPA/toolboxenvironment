package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;

public class GotoLineEndTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element and) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

         textNavigator.gotoLineEnd();
        return null;
    }
}
