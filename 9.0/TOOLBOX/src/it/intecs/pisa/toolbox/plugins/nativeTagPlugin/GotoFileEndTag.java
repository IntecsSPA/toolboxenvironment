package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;

public class GotoFileEndTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element extract) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

        textNavigator.gotoFileEnd();
        return null;
    }
}
