package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import java.io.StringReader;

public class SetTextTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element setText) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        if (getBool(setText.getAttribute(NEW))) {
            textNavigator = new TextNavigator(new StringReader((String) this.executeChildTag(DOMUtil.getFirstChild(setText))));
        } else {
            textNavigator = (TextNavigator) executeChildTag(DOMUtil.getFirstChild(setText));
        }

        configStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR, textNavigator);
        return textNavigator;
    }
}
