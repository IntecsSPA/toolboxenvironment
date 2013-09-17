package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

public class LineNumberTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element horizontalMove) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

       Element child = DOMUtil.getFirstChild(horizontalMove);
       
      return new Integer(textNavigator.getCurrentLine());
    }
}
