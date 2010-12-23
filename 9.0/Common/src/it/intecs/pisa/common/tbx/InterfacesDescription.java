/**
 * 
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class InterfacesDescription {
	private static final String TAG_INTERFACES = "interfaces";
	private static final String TAG_INTERFACE = Interface.TAG_INTERFACE;
	
	private Vector<Interface> interfaces=null;
	
	public InterfacesDescription()
	{
		interfaces=new Vector<Interface>();
		
	}
	
	public void addInterface(Interface interf)
	{
        this.interfaces.add(interf);
	}
	
	public void dumpToDisk(File file)
	{
		Document interfacesDoc=null;
		Element root=null;
		DOMUtil domutil=null;
		

		try
		{
			domutil=new DOMUtil();
			
			interfacesDoc=domutil.newDocument();
			root=interfacesDoc.createElement(TAG_INTERFACES);
			interfacesDoc.appendChild(root);
		
			for(Interface ithInterf: interfaces)
			{	
				ithInterf.appendToDoc(root);
			}

			DOMUtil.dumpXML(interfacesDoc, file);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	/**
	 * This method is used to load an initialization XML Docuemnt from file
	 * @param userDefinedInterfacesFile
	 * @param removeExistingInterfaces TODO
	 * @throws TDEException
	 */
	public void loadFromFile(File userDefinedInterfacesFile, boolean removeExistingInterfaces)  {
		DOMUtil domutil=null;
		Document doc=null;
		
		try {
			domutil=new DOMUtil();
			doc=domutil.fileToDocument(userDefinedInterfacesFile);
		} catch (Exception e) {
			//handle error here
		} 
		
		parseDocument(doc, removeExistingInterfaces);
	}
	
	/**
	 * This method is used to load an initialization XML Docuemnt from file
	 * @param userDefinedInterfacesFile
	 * @param removeExistingInterfaces TODO
	 * @throws TDEException
	 */
	public void loadFromStream(InputStream stream, boolean removeExistingInterfaces)  {
		DOMUtil domutil=null;
		Document doc=null;
		
		try {
			domutil=new DOMUtil();
			doc=domutil.inputStreamToDocument(stream);
		} catch (Exception e) {
		    //	  handle error here
		} 
		
		parseDocument(doc, removeExistingInterfaces);
	}
	/**
	 *  This method is used to parse the XMLDocument content in order to initialize
	 *  the class
	 * @param doc Document to parse 
	 * @param removeExisting TODO
	 * @throws TDEException An exception is thron if something unexpected happens
	 */
	public void parseDocument(Document doc, boolean removeExisting)
	{
		Element root=null;
		Element interfNode=null;
		LinkedList interfacesList=null;
		Interface interf=null;
		int count=0;
		
		try
		{
			root=doc.getDocumentElement();
			
			interfacesList=DOMUtil.getChildrenByTagName(root, TAG_INTERFACE);
			count=interfacesList.size();
			
			if(removeExisting==true)
				interfaces.clear();
			
			for(int i=0;i<count;i++)
			{
				interfNode=(Element)interfacesList.get(i);
				
				interf=new Interface();
				
				interf.initFromXML(interfNode);
				
				interfaces.add(interf);
			}
		}catch(Exception e)
		{
		    //	  handle error here
		}
		
	}

	public Vector<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Vector<Interface> interfaces) {
		this.interfaces = interfaces;
	}
	
	public Interface getInterface(String name, String version)
	{
		for(Interface interf:interfaces)
		{
			if(name.equals(interf.getName()) &&
			   version.equals(interf.getVersion()))
				 return interf;
		}
		
		return null;
	}

   
}
