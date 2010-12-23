package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.TextNavigator;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class SearchTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element search) throws Exception {
        IVariableStore configStore;
        TextNavigator textNavigator;

        configStore = engine.getConfigurationVariablesStore();

        textNavigator = (TextNavigator) configStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEXT_NAVIGATOR);

       Element child = DOMUtil.getFirstChild(search);
       
      Iterator children = DOMUtil.getChildren(search).iterator();
        String pattern = this.executeChildTag((Element) children.next()).toString();
        return new Boolean(textNavigator.search(
                pattern,
                children.hasNext() ? getInt((Element) children.next()) : 1,
                getBool(search.getAttribute(ABSOLUTE))));
    }
}
