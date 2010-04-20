package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;

public class GotoTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element goTo) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

         textNavigator.goTo(goTo.getAttribute(NAME));
        return null;
    }
}
