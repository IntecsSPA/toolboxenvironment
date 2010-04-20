package it.intecs.pisa.toolbox.plugins;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @version 1.0
 * @created 11-apr-2008 14.49.06
 */
public abstract class ToolboxPluginManager {
    protected static final String ATTRIBUTE_CLASS = "class";
    protected static final String ATTRIBUTE_NAMESPACE = "namespace";
    protected String pluginDirectory=null;	
    protected URLClassLoader loader=null;

	protected ToolboxPluginManager(){
                ClassLoader clLoader;
            
                clLoader=this.getClass().getClassLoader();
		loader=URLClassLoader.newInstance(new URL[0],clLoader);
	}
	
	        
        public void initManager(String pluginDirectory)
        {
            File pluginDir=null;
            File[] listFiles =null;
                        
            this.pluginDirectory=pluginDirectory;
            pluginDir=new File(pluginDirectory);

            listFiles = pluginDir.listFiles();
            
            for(File file: listFiles)
            {
                if(file.isDirectory())
                {
                    scanPluginDirectory(file);
                }
            }
            
            
        }

        /**
         * This method adds plugin libraries to the URLClassLoader
         * @param file
         */
    private void addLibsToClassLoader(File dir) throws MalformedURLException {
       File libsDir=null;
       String[] jarList=null;
       File jarFile=null;
       URL[] currentURL;
       URL[] newURL;
       
       libsDir=new File(dir,"libs");
       if(libsDir.exists())
       {
           jarList=libsDir.list();
           
           for(String jar:jarList)
           {
               jarFile=new File(libsDir,jar);
               if(jarFile.isFile())
               {
                   currentURL=loader.getURLs();
                   newURL=new URL[currentURL.length+1];
                   
                   for(int i=0; i< currentURL.length;i++)
                   newURL[i]=currentURL[i];
                 
                   newURL[currentURL.length]=jarFile.toURL();
                   
                   loader=URLClassLoader.newInstance(newURL,loader.getParent());   
               }
               
               
           }
       }
    }

        /**
         * This method scan the directory in search of the file plugin.xml. Then parses this file.
         * @param file
         */
    private void scanPluginDirectory(File file) {
       Document pluginXML=null;
       DOMUtil util=null;
       File descriptionFile=null;
       Element root=null;
       Element extensionElement=null;
       LinkedList extensions=null;
       int extensionCount=0;
       
       try
       {
           util=new DOMUtil();
           descriptionFile=new File(file,"plugin.xml");

           addLibsToClassLoader(file);
           
           pluginXML=util.fileToDocument(descriptionFile);
           root=pluginXML.getDocumentElement();
           
           extensions=DOMUtil.getChildren(root);
           extensionCount=extensions.size();
           
           
           for(int i=0;i<extensionCount;i++)
           {
               extensionElement=(Element) extensions.get(i);
            
               if(isTagHandled(extensionElement.getTagName()))
               {
                   handleTag(file,extensionElement);       
               }
           }
           
       }
       catch(Exception ecc)
       {
           ecc.printStackTrace();
       }
    }
    
    /**
     * This method returns true if and only if the tag is handled by the class
     * @param tagname
     * @return
     */
    protected boolean isTagHandled(String tagname)
    {
        return false;
    }
    
    /**
     * This method scans a new plugin directory and adds new entry if handled
     * @param pluginName
     */
    public void scanAndAddNewPlugin(String pluginName)
    {
         File pluginRootDir=null;
         File newPluginDir=null;
         File[] listFiles =null;
                        
         pluginRootDir=new File(pluginDirectory);
         newPluginDir=new File(pluginRootDir,pluginName);
         
          scanPluginDirectory(newPluginDir);
         
    }
    
    public String getPluginDirectory()
    {
        return this.pluginDirectory;
    }

   protected abstract  void handleTag(File file,Element el);   
}