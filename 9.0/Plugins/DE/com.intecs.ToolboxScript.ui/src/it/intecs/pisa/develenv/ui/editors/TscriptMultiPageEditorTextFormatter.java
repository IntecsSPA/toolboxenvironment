/**
 * 
 */
package it.intecs.pisa.develenv.ui.editors;

import it.intecs.pisa.util.DOMUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is a text formatter that is linked to the XML editor in order to provide 
 * a text formatter that doesn't produce any error as the default one does. It implements 
 * the requisite SAS-DE-SR2-550 described in  SAS-RB-510-INT Issue 1 Revision 3
 *  
 * @author Massimiliano
 */
public class TscriptMultiPageEditorTextFormatter extends org.eclipse.jface.text.formatter.ContentFormatter
{

	private static final String XML_DIRECTIVE_CLOSING_TAG = "?>";
	private static final String XML_DIRECTIVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.formatter.ContentFormatter#format(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public void format(IDocument document, IRegion region) {
		long offset=0;
		long length=0;
		
		offset=region.getOffset();
		length=region.getLength();
		
		if(offset==0 && length==document.getLength())
			formatWholeDocument(document);
		else formatDocumentRegion(document, region);
	}

	/**
	 * This method is used to format a region of the document. This method is invoked when the user choose Format > Active Document
	 * from the editor's context menu
	 * @param document Document to format
	 * @param region Region to format
	 */
	private void formatDocumentRegion(IDocument document, IRegion region) {
		String regionStr=null;
		String topStr=null;
		String bottomStr=null;
		String formattedFullString=null;
		String formattedRegionString=null;
		int bottomStartIndex=0;
		int offset=0;
		int length=0;
		String replaceString=null;
		String indentatedString=null;
		
		try
		{
			offset=region.getOffset();
			length=region.getLength();
			bottomStartIndex=offset+length;
			
			topStr=document.get(0,  offset);
			bottomStr=document.get(bottomStartIndex,document.getLength()-bottomStartIndex);
			
			regionStr=document.get(offset, length);
			
			formattedRegionString=formatSubRegion(regionStr);
			
			replaceString = getIndentationStringForOffset(document, offset);
			
			indentatedString=formattedRegionString.replaceAll("\n", replaceString);
			
			formattedFullString=topStr+indentatedString+bottomStr;
			
			document.set(formattedFullString);
		}
		catch(SAXException saxEx)
		{
			MessageDialog.openError(null, "Selection error", "You have to select an XML snippet that comprehends a start node and its closing node.");
		}
		catch(Exception e)
		{
			MessageDialog.openError(null, "Error", "An error has occurred while formatting document.");
		}
	}

	private String getIndentationStringForOffset(IDocument document, int offset) throws BadLocationException {
		int lineOfOffset;
		int lineOffset;
		String replaceString;
		String lineString=null;
		char[] array=null;
		
		lineOfOffset=document.getLineOfOffset(offset);
		lineOffset=document.getLineOffset(lineOfOffset);
		
		lineString=document.get(lineOffset, offset-lineOffset);
		array=lineString.toCharArray();
		
		replaceString=new String();
		
		for(char car:array)
		{
			switch(car)
			{
				case '\t':
				case ' ':
					replaceString+=car;
				break;
								
				default:
				break;
			}
		}
		
		return replaceString;
	}

	private String formatSubRegion(String regionStr) throws Exception {
		String trimmedString=null;
		String formattedString=null;
		int index=0;
		
		trimmedString=regionStr.trim();	
		
		formattedString=formatString(XML_DIRECTIVE+trimmedString);
		
		index=formattedString.indexOf(XML_DIRECTIVE_CLOSING_TAG);
		
		return index>-1?formattedString.substring(index+3):formattedString;
	}

	

	/**
	 * This method is used to format the whole document. This method is called if user has choosen Format > Document
	 * from the editor's context menu.
	 * @param document Document to format
	 */
	private void formatWholeDocument(IDocument document) {
		String docStr=null;
		String formattedDocument=null;
				
		try
		{
			docStr=document.get();
			formattedDocument = formatString(docStr);
			document.set(formattedDocument);
		}
		catch(Exception e)
		{
			MessageDialog.openError(null, "Error", "An error has occurred while formatting document. Check for document well-formedness before retrying.");
		}
	}

	private String formatString(String docStr) throws IOException, SAXException, Exception {
		Document doc;
		ByteArrayOutputStream outputStream;
		String formattedDocument;
		String xmlDirective=null;
		String formatedDocumentWithNoDirective=null;
		DOMUtil util=null;
		
		util=new DOMUtil();
		
		doc=util.stringToDocument(docStr);
		
		outputStream = new ByteArrayOutputStream();
		DOMUtil.dumpXML(doc, outputStream, true);
		
		formattedDocument=outputStream.toString();
			
		xmlDirective=formattedDocument.substring(0,formattedDocument.indexOf(XML_DIRECTIVE_CLOSING_TAG)+2);
		formatedDocumentWithNoDirective=formattedDocument.substring(formattedDocument.indexOf(XML_DIRECTIVE_CLOSING_TAG)+2);
		
		return xmlDirective+"\n"+formatedDocumentWithNoDirective;
	}

}
