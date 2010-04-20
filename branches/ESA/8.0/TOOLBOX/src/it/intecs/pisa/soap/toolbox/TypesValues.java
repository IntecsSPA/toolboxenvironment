/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.soap.toolbox;

import it.intecs.pisa.toolbox.plugins.ITagExecutor;
import it.intecs.pisa.toolbox.plugins.ITagPlugin;
import it.intecs.pisa.toolbox.plugins.TagPluginManager;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;

 /**
     * The Class TypesValues.
     */
    public class TypesValues {
        
        /**
         * The values.
         */
        private Class[] types;
    private

    /**
     * The values.
     */
    Object[] values;
        
        /**
         * The Constructor.
         *
         * @param parameters the parameters
         *
         * @throws Exception the exception
         */
      public  TypesValues(IEngine engine,LinkedList parameters) throws Exception {
            types = new Class[parameters.size()];
            values = new Object[parameters.size()];
            Iterator iterator = parameters.iterator();
            Iterator subIterator;
            for (int index = 0; index < types.length; index++) {
                subIterator = DOMUtil.getChildren((Element) iterator.next()).
                        iterator();
                    
                types[index] = (Class) executeChildTag(engine,(Element) subIterator.next(),null);
                values[index] = executeChildTag(engine,(Element) subIterator.next(),null);
            }
        }
      
       protected Object executeChildTag(IEngine engine, org.w3c.dom.Element element, org.w3c.dom.Element debugTag) throws Exception {
        ITagPlugin plugin = null;
        ITagExecutor executor = null;
        String tagNamespace = null;
        String tagName = null;
        TagPluginManager manager=null;

       manager = TagPluginManager.getInstance();
      
        tagNamespace = element.getNamespaceURI();
        tagName = element.getNodeName();

        plugin = manager.getTagPlugin(tagNamespace);
        executor = plugin.getTagExecutorClass(tagName,engine);

        return executor.executeTag(element, debugTag,false);
    }
        
        /**
         * The Constructor.
         *
         * @param object the object
         * @param parameters the parameters
         *
         * @throws Exception the exception
         */
      public  TypesValues(IEngine engine,Object object, LinkedList parameters) throws Exception {
            types = new Class[parameters.size() + 1];
            types[0] = object.getClass();
            values = new Object[parameters.size() + 1];
            values[0] = object;
            Iterator iterator = parameters.iterator();
            Iterator subIterator;
            for (int index = 1; index < types.length; index++) {
                subIterator = DOMUtil.getChildren((Element) iterator.next()).
                        iterator();
                types[index] = (Class) this.executeChildTag(engine,(Element) subIterator.next(),null);
                values[index] = this.executeChildTag(engine,(Element) subIterator.next(),null);
            }
        }

    public Class[] getTypes() {
        return types;
    }

    public Object[] getValues() {
        return values;
    }
        
    }
