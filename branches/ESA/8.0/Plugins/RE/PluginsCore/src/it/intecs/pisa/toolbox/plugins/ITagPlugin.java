package it.intecs.pisa.toolbox.plugins;

import it.intecs.pisa.soap.toolbox.IEngine;


/**
 * This interface exposed method to be used by a client that wants to execute a
 * TOOLBOX XML script.
 * @version 1.0
 * @created 11-apr-2008 14.49.06
 */
public interface ITagPlugin {
        
        /**
         * This method is used  
         * @param pluginDirectory
         */
        public void init(String pluginDirectory);
   
	/**
	 * This method returns a reference of class ITagExecutor, providing  an instance
	 * of the class that can execute the tag whose name is passed as parameter.
	 * 
	 * @param tagName
	 */
	public ITagExecutor getTagExecutorClass(String tagName,IEngine engine) throws Exception;

        public String getConfigurationParameter(String parameter);
}