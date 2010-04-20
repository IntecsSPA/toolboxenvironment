package it.intecs.pisa.toolbox.plugins;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @version 1.0
 * @created 11-apr-2008 14.49.06
 */
public class TagPlugin implements ITagPlugin {

    protected String[][] tagClassTable = null;
    protected Hashtable<String,String> configParameters=null;
    
    public TagPlugin() {
    }

    public org.w3c.dom.Document getCapabilities() {
        return null;
    }

    /**
     * This class returns the class executor. In future implementation a pool of classes can be used in order to fasten execution
     * @param tagName
     */
    public ITagExecutor getTagExecutorClass(String tagName, IEngine engine) throws Exception {
        ITagExecutor executor = null;
        String parsedTagName = null;
        int index = -1;

        try {
            //checking if tagName has prefix
            index = tagName.indexOf(':');

            if (index > -1) {
                parsedTagName = tagName.substring(tagName.indexOf(':') + 1);
            } else {
                parsedTagName = tagName;
            }

            for (String[] tag : tagClassTable) {
                if (tag[0].equals(parsedTagName)) {
                    executor = (ITagExecutor) this.getClass().getClassLoader().loadClass(tag[1]).newInstance();
                    executor.setEngine(engine);
                    executor.setPlugIn(this);
                    return executor;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            executor = null;
        } finally {
            if (executor == null) {
                throw new Exception("Cannot find a tag executor associated to the tag  " + tagName);
            }
        }

        return executor;
    }

    public void init(String pluginDirectory) {

        initTags(pluginDirectory);
        initConfigurationParameters(pluginDirectory);
    }

    private void initConfigurationParameters(String pluginDirectory) {
        File tagListFile = null;
        Document initDoc = null;
        DOMUtil util = null;
        Element root = null;
        Element tagEl = null;
        LinkedList children = null;
        int childrenCount = 0;

        try {
            util = new DOMUtil();
            configParameters=new Hashtable<String,String>();
            
            tagListFile = new File(pluginDirectory, "resources/config.xml");
            if (tagListFile.exists()) {
                initDoc = util.fileToDocument(tagListFile);

                root = initDoc.getDocumentElement();
                children = DOMUtil.getChildren(root);

                childrenCount = children.size();
  
                for (int i = 0; i < childrenCount; i++) {
                    tagEl = (Element) children.get(i);
                    configParameters.put(tagEl.getAttribute("name"),tagEl.getAttribute("value"));
                }
            }
        } catch (Exception ecc) {
        }
    }

    private void initTags(String pluginDirectory) {
        File tagListFile = null;
        Document initDoc = null;
        DOMUtil util = null;
        Element root = null;
        Element tagEl = null;
        LinkedList children = null;
        int childrenCount = 0;

        try {
            util = new DOMUtil();

            tagListFile = new File(pluginDirectory, "tagList.xml");
            initDoc = util.fileToDocument(tagListFile);

            root = initDoc.getDocumentElement();
            children = DOMUtil.getChildren(root);

            childrenCount = children.size();
            tagClassTable = new String[childrenCount][2];

            for (int i = 0; i < childrenCount; i++) {
                tagEl = (Element) children.get(i);
                tagClassTable[i][0] = tagEl.getAttribute("name");
                tagClassTable[i][1] = tagEl.getAttribute("class");
            }
        } catch (Exception ecc) {
        }
    }

    public String getConfigurationParameter(String parameter) {
      return this.configParameters.get(parameter);
    }
}