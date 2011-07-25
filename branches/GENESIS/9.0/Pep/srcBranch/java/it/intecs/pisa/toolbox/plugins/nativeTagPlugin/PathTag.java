package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import org.w3c.dom.Element;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class PathTag extends NativeTagExecutor {
    
    public PathTag()
    {
        tagName="path";
    }
    
    @Override
    public Object executeTag(org.w3c.dom.Element pathel) throws Exception {
        File path = null;
        LinkedList children = null;
        Iterator iter = null;
        IVariableStore configurationStore = null;
        String serviceResourceDir = null;

        configurationStore = engine.getConfigurationVariablesStore();

        serviceResourceDir = (String) configurationStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR);

        children = DOMUtil.getChildren(pathel);
        iter = children.iterator();

        if (pathel.hasAttribute("startFrom")) {
            String initialPath;
            String attrValue = engine.evaluateString(pathel.getAttribute("startFrom"), IEngine.EngineStringType.ATTRIBUTE);

            if (attrValue.equals("HOME")) {
                initialPath = System.getProperty("user.home");
            } else if (attrValue.equals("TEMP")) {
                initialPath = System.getProperty("java.io.tmpdir");
            } else if (attrValue.equals("SERVICE_RESOURCE_DIR")) {
                initialPath = serviceResourceDir;
            } else if (attrValue.equals("SERVICE_COMMON_SCRIPTS_DIR")) {
                File commonScriptDir;

                commonScriptDir = new File(serviceResourceDir, "Common Scripts");

                initialPath = commonScriptDir.getAbsolutePath();
            } else {
                initialPath = System.getProperty("user.home");
            }
            path = new File(initialPath);
        } else {
            path = new File((String) this.executeChildTag((Element) iter.next()));
        }

        while (iter.hasNext()) {
            path = new File(path, (String) this.executeChildTag((Element) iter.next()));
        }

        String absPath = path.getAbsolutePath();

        absPath = absPath.replace('/', File.separatorChar);
        absPath = absPath.replace('\\', File.separatorChar);

        addResourceLinkToDebugTree(path);
        return absPath;
    }
}
