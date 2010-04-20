/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins;

import java.io.File;
import java.util.Hashtable;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class TagPluginManager extends ToolboxPluginManager {

    public static final String EXTENSION_TYPE_TAGPLUGIN = "tagPlugin";
    private static TagPluginManager manager = new TagPluginManager();
    protected Hashtable<String, ITagPlugin> plugins;

    /**
     * This method is used to add a plugin to the ToolboxPlusing manager
     * @param namespace namespace that is defined by the plugin
     * @param plugin plugin interface
     */
    protected ITagPlugin addTagPlugin(String namespace, String className) {
        ITagPlugin plugin = null;

        try {
            if (namespace != null && className != null) {
                plugin = (ITagPlugin) loader.loadClass(className).newInstance();

                plugins.put(namespace, plugin);

                return plugin;
            }
        } catch (Exception ecc) {
            System.out.println("Error while loading tag plugin");
            ecc.printStackTrace();
        }

        return null;
    }

    /**
     * This method is used to retrieve the Tag plugin that is sassociated to
     * the input parameter tagNamespace.
     * @param tagNamespace
     */
    public ITagPlugin getTagPlugin(String tagNamespace) throws Exception {
        ITagPlugin plugin = null;

        try {
            plugin = plugins.get(tagNamespace);
        } catch (Exception e) {
            e.printStackTrace();
            plugin = null;
        } finally {
            if (plugin == null) {
                throw new Exception("Cannot find a plugin associated to the namespace " + tagNamespace);
            }
        }
        return plugin;
    }

    public static TagPluginManager getInstance() {
        return manager;
    }

    @Override
    protected boolean isTagHandled(String tagname) {
        return tagname.equals(EXTENSION_TYPE_TAGPLUGIN);
    }

    @Override
    protected void handleTag(File file, Element extensionElement) {
        ITagPlugin tagPlugin;

        tagPlugin = addTagPlugin(extensionElement.getAttribute(ATTRIBUTE_NAMESPACE), extensionElement.getAttribute(ATTRIBUTE_CLASS));
        tagPlugin.init(file.getAbsolutePath());
    }

    @Override
    public void initManager(String pluginDirectory) {
        this.plugins = new Hashtable<String, ITagPlugin>();
        super.initManager(pluginDirectory);

    }
}
