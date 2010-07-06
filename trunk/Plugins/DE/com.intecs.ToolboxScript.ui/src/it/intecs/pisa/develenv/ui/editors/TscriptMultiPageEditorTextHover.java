/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptMultiPageEditorTextHover.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.3 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptMultiPageEditorTextHover.java,v 1.3 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.editors;

import it.intecs.pisa.util.XSLT;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

/**
 * The Class TscriptMultiPageEditorTextHover.
 */
public class TscriptMultiPageEditorTextHover implements ITextHover {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(final ITextViewer textViewer, final IRegion hoverRegion) {
		// Getting documentation
		String HooveredText;
		String schemaPath, transformationPath;
		final Hashtable xslParameters = new Hashtable();
		String tagName;
		Document xslDocument;
		StringWriter out;
		IDocument doc;
		StringTokenizer tokenizer;
		
		try {
			HooveredText = "";
						
			doc = textViewer.getDocument();
		
			tokenizer=new StringTokenizer(doc.get(hoverRegion.getOffset(), hoverRegion.getLength()),"</ ");
			tagName=tokenizer.nextToken();
			
			transformationPath = this.getPluginFilePath("transform.xsl","/");
			schemaPath= this.getPluginFilePath("xmlScript.xsd","/");
			
			out=new StringWriter();
			
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			docFactory.setNamespaceAware(true);

			xslParameters.put(new String("tagName"), tagName);

			xslDocument = docFactory.newDocumentBuilder().parse(
					new File(transformationPath));

			XSLT.addParameter(xslDocument, xslParameters);
			XSLT.transform(new DOMSource(xslDocument), new StreamSource(new File(schemaPath)),
					new StreamResult(out));
			
			HooveredText=out.toString();
			
		} catch (final Exception e) {
			HooveredText = "No documentation available for the selected item";
		}

		return HooveredText;
	}

	/**
	 * Gets the plugin file path.
	 * 
	 * @param filename the filename
	 * @param startPath the start path
	 * 
	 * @return the plugin file path
	 */
	private String getPluginFilePath(final String filename,final String startPath) {
		String eclipse_path;
		String schemaPath;
		Bundle filesBundle;
		URL url;
		filesBundle = Platform
				.getBundle("com.intecs.ToolboxScript.editorFiles");
		final Enumeration e = filesBundle.findEntries(startPath, filename, true);
		url = (URL) e.nextElement();
		final String resource = url.getPath();

		eclipse_path = filesBundle.getLocation() + resource;
		schemaPath = eclipse_path.substring(eclipse_path.indexOf('@') + 1);
		return schemaPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(final ITextViewer textViewer, final int offset) {
		String docPiece, pieceAfter, pieceBefore;
		int startTagOffset, endTagOffset;
		StringTokenizer tokenizer;
		
		Region reg;
		try {			
			final IDocument doc = textViewer.getDocument();
			
			docPiece=doc.get();
			
			pieceBefore=docPiece.substring(0,offset);
			startTagOffset=pieceBefore.lastIndexOf('<');
			
			pieceAfter=docPiece.substring(offset);
			tokenizer=new StringTokenizer(pieceAfter,"/> ");
			endTagOffset=tokenizer.nextToken().length();
			
			reg = new Region(startTagOffset, endTagOffset+offset - startTagOffset);
			return reg;
			

		} catch (final Exception e) {
			return new Region(0, 100);
		}
	}

}
