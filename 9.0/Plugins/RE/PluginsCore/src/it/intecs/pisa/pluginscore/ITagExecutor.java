package it.intecs.pisa.pluginscore;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;

/**
 * This interface is exposed by all classes that implements the core of an XML
 * script tag.
 * @version 1.0
 * @created 11-apr-2008 14.49.06
 */
public interface ITagExecutor {

	/**
	 * 
	 * @param debugTag
	 * @param tagEl
	 */
	public Object executeTag( org.w3c.dom.Element tagEl,org.w3c.dom.Element debugTag,boolean debugMode) throws Exception;

        public void setEngine(IEngine engine);
        public void setPlugIn(ITagPlugin plugIn); 
}