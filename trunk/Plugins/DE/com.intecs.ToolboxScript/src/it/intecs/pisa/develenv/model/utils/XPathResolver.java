/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: XPathResolver.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.6 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/03/07 10:30:27 $
 * File ID: $Id: XPathResolver.java,v 1.6 2007/03/07 10:30:27 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.utils;

import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.util.DOMUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathResolver.
 */
public class XPathResolver {

	/**
	 * 
	 */
	protected String cleanedDocument;

	/**
	 * The document.
	 */
	protected IDocument document;

	/**
	 * The tag line.
	 */
	protected int tagLine;
	protected Element selectedNode;
	
	/**
	 * The Constructor.
	 */
	public XPathResolver() {
		document = null;
	}

	/**
	 * The Constructor.
	 * 
	 * @param doc
	 *            the doc
	 */
	public XPathResolver(IDocument doc) {
		this.document = doc;
	}

	
	public String computeFromLine(int lineNumber) {
		int offset=0;
		String xpath=null;
		String cutDocument=null;
		InputStream stream=null;
		Document tree=null;
		int index=0;

		try
		{
			try
			{
				offset=document.getLineOffset(lineNumber);
				cutDocument=document.get(0,offset);
			}catch(Exception ecc)
			{
				cutDocument=document.get();
			}
					
			stream=new ByteArrayInputStream(cutDocument.getBytes());
			tree=parseForTree(new XmlDirectivesFilterStream(stream));
			
			xpath=DOMUtil.getXPathNS(selectedNode, selectedNode.getOwnerDocument());
			//System.out.println("XPATH: "+xpath);
			return xpath.substring("http://namespace.it:root".length()+1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	private Document parseForTree(InputStream stream) {
		String tag;
		PushbackInputStream pStream;
		DOMUtil util;
		Document doc=null;
		Element rootEl;
		try
		{
			util=new DOMUtil(true);
			doc=util.newDocument();
			rootEl=doc.createElementNS("http://namespace.it","root");
			doc.appendChild(rootEl);
			
			pStream=new PushbackInputStream(stream);
			
			parseTag(pStream,rootEl, new String[0][2]);
		}
		catch(Exception e)
		{
			
		}
		return doc;
	}

	private String parseTag(PushbackInputStream stream, Element parentEl, String[][] parentNamespaces) throws Exception {
		String wholeTagString;
		String tagName;
		Document doc;
		Element currentEl;
		String[][] nodeNameSpaces;
				
		doc=parentEl.getOwnerDocument();
		
		wholeTagString=getTag(stream);
		nodeNameSpaces=getNamespaces(wholeTagString,parentNamespaces);
		tagName=getTagName(wholeTagString,nodeNameSpaces);
		
		if(wholeTagString.startsWith("</"))
			return "</"+parentEl.getNamespaceURI()+":"+parentEl.getNodeName()+">";
		
		currentEl=doc.createElementNS(tagName.substring(0,tagName.lastIndexOf(":")),tagName.substring(tagName.lastIndexOf(":")+1));
		parentEl.appendChild(currentEl);
		selectedNode=currentEl;
		
		if(wholeTagString.endsWith("/>"))
		{
			return wholeTagString;
		}
		else
		{
			do
			{
				wholeTagString=parseTag(stream,currentEl, nodeNameSpaces);
			}
			while(wholeTagString.startsWith("</"+tagName)==false);
			
			selectedNode=currentEl;
			return wholeTagString;
		}
		
	}

	

	private String[][] getNamespaces(String tagStr, String[][] parentNamespaces) {
		StringTokenizer tokenizer;
		String[][] namespaces=null;
		String token;
		String key;
		int size=0;
		Enumeration keys;
		Hashtable<String,String> nameTable;
		
		nameTable=new Hashtable<String,String>();
		
		tokenizer=new StringTokenizer(tagStr,"< >");
		tokenizer.nextElement();
		
		while(tokenizer.hasMoreTokens())
		{
			token=tokenizer.nextToken();
			if(token.startsWith("xmlns:"))
			{
				nameTable.put(token.substring(6,token.indexOf("=")),
						token.substring(token.indexOf("\"")+1,token.lastIndexOf("\"")));
			}
			else if(token.startsWith("xmlns"))
			{
				nameTable.put("",
						token.substring(token.indexOf("\"")+1,token.lastIndexOf("\"")));
			}
		}
		
		for(String[] parentNames: parentNamespaces)
		{
			if(nameTable.containsKey(parentNames[0])==false)
			{
				nameTable.put(parentNames[0], parentNames[1]);
			}
		}
		
		size=nameTable.size();
		namespaces=new String[size][2];
		keys=nameTable.keys();
		for(int i=0;i< size;i++)
		{
			key=(String) keys.nextElement();
			namespaces[i][0]=key;
			namespaces[i][1]=nameTable.get(key);
		}
		return namespaces;
	}

	private String getTagName(String wholeTagString, String[][] nodeNameSpaces) {
		StringTokenizer tokenizer;
		String token;
		String namespaceabbr;
		
		tokenizer=new StringTokenizer(wholeTagString,"< />");
		token=tokenizer.nextToken();;
		
		if(token.indexOf(":")>-1)
		{
			namespaceabbr=token.substring(0,token.indexOf(":"));
		}
		else namespaceabbr="";
		
		for(String[] nspaces:nodeNameSpaces)
		{
			if(namespaceabbr.equals(nspaces[0]))
			{
				return nspaces[1]+":"+token.substring(token.indexOf(":")+1);
			}
		}
			
		return token;
	}

	private String getTag(PushbackInputStream stream) throws Exception {
		int character=0;
		String tagString="";
		
		do
		{
			character=stream.read();
		}
		while(character>-1 && character!= '<');
		
		
		do
		{
			tagString+=String.valueOf((char)character);
			character=stream.read();
		}
		while(character>-1 && character!= '>');
		
		tagString+=String.valueOf((char)character);
				
		if(tagString.startsWith("<!--"))
		{
			if(tagString.endsWith("-->")==false)
				runUntilEndOfComment(stream);
			
			return getTag(stream);
		}
		else return tagString;
	}

	private void runUntilEndOfComment(PushbackInputStream stream) throws Exception {
		boolean isFirstMinusHit=false;
		boolean isSecondMinusHit=false;
		boolean end=false;
		int character=0;
			
		//System.out.println("Running until end of comment");
		do
		{
			character=stream.read();
			//System.out.print((char)character);
			
			if(character=='-' && isFirstMinusHit==false)
			{
				isFirstMinusHit=true;
			}
			else if(character=='-' && isFirstMinusHit==true && isSecondMinusHit==false)
			{
				isSecondMinusHit=true;
			}
			else if(character=='>' && isFirstMinusHit==true && isSecondMinusHit==true)
			{
				end=true;
			}
			else
			{
				end=false;
				isFirstMinusHit=false;
				isSecondMinusHit=false;
			}
		}
		while(end==false);
		
		
	}

	/**
	 * Sets the document.
	 * 
	 * @param doc
	 *            the document
	 */
	public void setDocument(IDocument doc) {
		this.document = doc;
	}
}
