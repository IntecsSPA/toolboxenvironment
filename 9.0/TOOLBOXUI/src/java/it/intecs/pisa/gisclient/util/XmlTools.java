/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.gisclient.util;


import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.apache.xerces.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

import org.xml.sax.SAXException;

/**
 * XML Utility
 * @author Andrea Marongiu
 */
public class XmlTools
{
    public static final String TEXT = "#TEXT";
    public static final String MULTIPOLYGON = "GML:MULTIPOLYGON";
    public static final String POLYGON_MEMBER = "gml:polygonMember";
    public static final String POLYGON = "GML:POLYGON";
    public static final String FEATURE_MEMBER = "GML:FEATUREMEMBER";
    
    public static Document docGenerate(InputStream in) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docF = null;
        DocumentBuilder docB = null;
        org.w3c.dom.Document doc = null;
        
        /* Build xml document */
        docF = DocumentBuilderFactory.newInstance();
        docF.setNamespaceAware(true);
        docB = docF.newDocumentBuilder();
        doc = docB.parse(in);
        
        return doc;
    }

    public static void copyDocToOutputStream(Document doc, OutputStream os)
    {
        TransformerFactory fac = TransformerFactory.newInstance();
        Transformer trans;
        DOMSource ds = new DOMSource(doc);
        try
        {
            trans = fac.newTransformer();
            trans.transform(ds,new StreamResult(os));
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static String extractValue(byte [] b, String pathXml[]) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(b) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        NodeList ndlist=root.getChildNodes();
        int k=0;
        int w=0;
        Node tmpOut=null;
        String tagValue="";
        
        while (k < pathXml.length)
        {
            w=0;
            
            while (!(ndlist.item(w).getNodeName().equalsIgnoreCase(pathXml[k])))
                w++;
            
            tmpOut=ndlist.item(w);
            
            ndlist=tmpOut.getChildNodes();
            if ((ndlist.getLength()==0)&&(k != pathXml.length-1))
            {
                System.out.println("Orchestra Client ERROR: Aggregation Response: "+pathXml[k]+ " tag is empty" );
            }
            else
                if (k == pathXml.length-1)
                    tagValue=tmpOut.getTextContent();
            
            k++;
        }
        
        return(tagValue);
    }
    
    /**
     * Extract all TextValues for a specific tag that is contained in a specific Node in a Document XML.
     *@param byte [] array containing the Xml Document
     *@param pathXlm [] Node Dom Path
     *@param pathValue [] Tag Dom Path inside to Node
     *@return ArrayList containig the List of Values.
     */
    public static ArrayList extractAllValues(byte [] b, String pathXml[], String pathValue[]) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(b) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        NodeList ndlist=root.getChildNodes();
        ArrayList valueExt=new ArrayList();
        int k=0;
        int w=0;
        Node tmpOut=null;
        String tagValue="";
        Node tmpOutInt=null;
        
        while (k < pathXml.length)
        {
            w=0;
            
            while (!(ndlist.item(w).getNodeName().equalsIgnoreCase(pathXml[k])))
                w++;
            
            tmpOut=ndlist.item(w);
            
            NodeList ndlistInt=tmpOut.getChildNodes();
            if ((ndlistInt.getLength()==0)&&(k != pathXml.length-1))
            {
                System.out.println("Orchestra Client ERROR: Aggregation Response: "+pathXml[k]+ " tag is empty" );
            }
            else
                if (k == pathXml.length-1)
                {
                
                int y=0;
                while (y < ndlist.getLength())
                {
                    if(ndlist.item(y).getNodeName() == pathXml[pathXml.length-1])
                    {
                        ndlistInt=ndlist.item(y).getChildNodes();
                        int x=0;
                        while (x < pathValue.length)
                        {
                            int s=0;
                            while ((!ndlistInt.item(s).getNodeName().equalsIgnoreCase(pathValue[x])) && (s < ndlistInt.getLength()))
                                s++;
                            tmpOutInt=ndlistInt.item(s);
                            ndlistInt=tmpOut.getChildNodes();
                            if ((ndlistInt.getLength()==0)&&(x != pathXml.length-1))
                            {
                                System.out.println("Orchestra Client ERROR: Aggregation Response: "+pathValue[x]+ " tag is empty" );
                            }
                            else
                            {
                                valueExt.add(tmpOutInt.getTextContent());
                            }
                            x++;
                        }
                    }
                    y++;
                }
                }
            k++;
        }
        return(valueExt);
    }
    
    
    
    
    public static Node extractNode(byte [] b, String pathXml[]) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(b) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        NodeList ndlist=root.getChildNodes();
        int k=0;
        int w=0;
        Node tmpOut=null;
        Node tagNode=null;
        
        while (k < pathXml.length)
        {
            w=0;
            
            while (!(ndlist.item(w).getNodeName().equalsIgnoreCase(pathXml[k])))
                w++;
            
            tmpOut=ndlist.item(w);
            
            ndlist=tmpOut.getChildNodes();
            if ((ndlist.getLength()==0)&&(k != pathXml.length-1))
            {
                System.out.println("Orchestra Client ERROR: Aggregation Response: "+pathXml[k]+ " tag is empty" );
            }
            else
                if (k == pathXml.length-1)
                    tagNode=tmpOut;
            
            k++;
        }
        
        return(tagNode);
    }
    
    public static String extractRootNamespece(byte [] b) throws ParserConfigurationException, SAXException, IOException
    {
        
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(b) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        NamedNodeMap nn=root.getAttributes();
        String nameSpace=" ";
        for(int i=0; i< nn.getLength();i++)
        {
            if (nn.item(i).getNodeName().contains("xmlns"))
            {
                nameSpace=nameSpace+nn.item(i).getNodeName()+"=\""+nn.item(i).getNodeValue()+"\" ";
            }
        }
        
        return(nameSpace);
    }
    
    public static String xmlStreamToString(String xmlUrl)
    {
        String xmlString="";
        URL acUrl=null;
        try
        {
            acUrl = new URL(xmlUrl);
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        
        BufferedReader in=null;
        String s=null;
        xmlUrl="";
        try
        {
            in = new BufferedReader(new InputStreamReader(acUrl.openStream()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        while ( true )
        {
            try
            {
                s = in.readLine();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            if ( s == null )
                break;
            else
                xmlString=xmlString+s;
        }
        try
        {
            in.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return(xmlString);
    }
    
    /**
     * Extract a Feature Collection for WMS (single part).
     *@param byte [] array containing the feature Collection
     *@return String containig the new Feature Collection.
     */
    public static String extractFCWMS(byte feat_coll[]) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException
    {
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(feat_coll) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        root.removeAttribute("schemaLocation");
        
        Element el2= (Element) changeMultiPolygon(root);
        String fc="";
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        Source source = new DOMSource(el2);
        StringWriter out = new StringWriter();
        Result result = new StreamResult(out);
        transformer.setOutputProperty("encoding", "ISO-8859-1");
        transformer.transform(source, result);
        fc = out.toString();
        
        return(fc);
    }
    
    
    /**
     * Extract a Feature Collection for MAS.
     *@param byte [] array containing the feature Collection
     *@return String containig the new Feature Collection.
     */
    public static String extractFCMAS(byte fc[]) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc=null;
        InputStream temp= new ByteArrayInputStream(fc) ;
        doc = XmlTools.docGenerate(temp);
        Element root=doc.getDocumentElement();
        root.removeAttribute("schemaLocation");
        return(root.toString());
    }
    /**
     * Create a new file xml from a node.
     *@param nd The node in which is the xml tree.
     *@param file The file where new xml is memorized
     *@return the node containing the xml tree.
     */
    public static Node nodeGenerate(Node nd, File file) throws TransformerConfigurationException, FileNotFoundException, TransformerException
    {
        DOMSource ds = new DOMSource(nd);
        TransformerFactory fac = TransformerFactory.newInstance();
        Transformer trans;
        trans = fac.newTransformer();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        StreamResult res = new StreamResult(out);
        trans.transform(ds,res);
        return ds.getNode();
    }
    /**
     * Verifies if the feature collection have any Multipolygon tag and changes them to Polygon tags.
     * @param node the Feature collection
     * @return Node the new feature collection
     */
    private static Node changeMultiPolygon(Node node)
    {
        int i =0;
        int j = 0;
        int count = 0;
        ArrayList array = new ArrayList();
        NodeList list = node.getChildNodes();
        Document doc = node.getOwnerDocument();
        NodeList nl = null;
        Node temp = null;
        boolean flag = false;
        while(i<list.getLength())
        {
            if (list.item(i).getNodeName().equalsIgnoreCase(FEATURE_MEMBER))
            {
                temp = list.item(i);
                j = 0;
                nl = list.item(i).getChildNodes();
                while (j < nl.getLength())
                {
                    
                    if (!nl.item(j).getNodeName().equalsIgnoreCase(TEXT))
                    {
                        
                        nl = nl.item(j).getChildNodes();
                        j = 0;
                        while(j < nl.getLength())
                        {
                            
                            if (nl.item(j).getNodeName().contains("the_geom") || nl.item(j).getNodeName().contains("geometryProperty") || nl.item(j).getNodeName().equalsIgnoreCase("geometry"))
                            {
                                
                                nl = nl.item(j).getChildNodes();
                                j = 0;
                                while(j < nl.getLength())
                                {
                                    
                                    if (nl.item(j).getNodeName().equalsIgnoreCase(MULTIPOLYGON))
                                    {
                                        
                                        array = multiPolygon2Polygon(temp);
                                        j = Integer.MAX_VALUE-10;
                                        flag = true;
                                    }
                                    j++;
                                }
                            }
                            j++;
                        }
                    }
                    j++;
                }
            }
            if(flag)
            {
                count++;
                
                for (int k = 0; k < array.size(); k++)
                {
                    node.appendChild(doc.importNode((Node)array.get(k), true));
                    
                    
                }
                
                node.removeChild(temp);
                
                list = node.getChildNodes();
                i=0;
                flag = false;
            }
            i++;
        }
        return node;
    }
    
    /**
     * Changes a multipolygon feature member to polygon feature members.
     * @param node the Feature member that contains a Multipolygon tag.
     * @return ArrayList a list of feature members.
     */
    private static ArrayList multiPolygon2Polygon(Node node)
    {
        
        ArrayList result = new ArrayList();
        ArrayList array = new ArrayList();
        int i =0;
        int j = 0;
        int k = 0;
        NodeList list = node.getChildNodes();
        Document doc = node.getOwnerDocument();
        NodeList nl = null;
        NodeList nls = null;
        Node parent = null;
        Node temp = null;
        String geometry = null;
        while(i<list.getLength())
        {
            
            if (!list.item(i).getNodeName().equalsIgnoreCase(FEATURE_MEMBER))
            {
                
                j = 0;
                nl = list.item(i).getChildNodes();
                while (j < nl.getLength())
                {         //tag che identifica l'inizio della geometria'
                    
                    if (nl.item(j).getNodeName().contains("the_geom") || nl.item(j).getNodeName().contains("geometryProperty") || nl.item(j).getNodeName().equalsIgnoreCase("geometry"))
                    {
                        
                        parent = nl.item(j);
                        geometry = nl.item(j).getNodeName();
                        nl = nl.item(j).getChildNodes();
                        j = 0;
                        while(j < nl.getLength())
                        {
                            
                            if (nl.item(j).getNodeName().equalsIgnoreCase(MULTIPOLYGON))
                            {
                                
                                temp = nl.item(j);
                                nl = nl.item(j).getChildNodes();
                                j = 0;
                                while (j < nl.getLength())
                                {
                                    
                                    if (nl.item(j).getNodeName().equalsIgnoreCase(POLYGON_MEMBER))
                                    {
                                        
                                        nls = nl.item(j).getChildNodes();
                                        k = 0;
                                        while (k < nls.getLength())
                                        {
                                            
                                            if (nls.item(k).getNodeName().equalsIgnoreCase(POLYGON))
                                            {
                                                
                                                array.add(nls.item(k).cloneNode(true));
                                                
                                            }
                                            k++;
                                        }
                                    }
                                    j++;
                                }
                                
                                j = Integer.MAX_VALUE - 3;
                                i= Integer.MAX_VALUE - 1;
                                
                            }
                            if (temp != null)
                            {
                                
                                parent.removeChild(temp);
                                
                            }
                            j++;
                        }
                    }
                    j++;
                }
            }
            i++;
        }
        Node feature = null;
        for (i = 0; i < array.size(); i++)
        {
            feature = node.cloneNode(true);
            j = 0;
            list = feature.getChildNodes();
            while(j<list.getLength())
            {    if (!list.item(j).getNodeName().equalsIgnoreCase(TEXT)) //nome della feature member
                 {
                     k = 0;
                     nl = list.item(j).getChildNodes();
                     while (k < nl.getLength())
                     {           if (nl.item(k).getNodeName().equalsIgnoreCase(geometry))
                                 {
                                     nl.item(j).appendChild((Node)array.get(i));
                                     k = Integer.MAX_VALUE-10;
                                 }
                                 k++;
                     }
                     j = Integer.MAX_VALUE-10;
                 }
                 j++;
            }
            result.add(feature);
            
        }
        return result;
    }
    
    public static String getStringFile(String urlXML)
    {
        String returnString ="";
        
        URL url=null;
        URLConnection connection=null;
        
        // Open Connection
        try
        {
            url = new URL(urlXML);
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        try
        {
            connection = url.openConnection();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        try
        {
            
            
            StreamSource source = new StreamSource(httpConn.getInputStream());
            
            TransformerFactory fac = TransformerFactory.newInstance();
            Transformer trans = null;
            StreamResult res = new StreamResult(new StringWriter());
            try
            {
                trans = fac.newTransformer();
            }
            catch (TransformerConfigurationException ex)
            {
                ex.printStackTrace();
            }
            try
            {
                
                trans.transform(source,res);
            }
            catch (TransformerException ex)
            {
                ex.printStackTrace();
            }
            StringWriter w=(StringWriter) res.getWriter();
            
            returnString = w.toString();
            
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return returnString;
    }
    
    public static String getASCIIFile(String fileURL) throws IOException
    {
        
        String s="";
        try
        {
            URL url = new URL(fileURL);
            int read;
            DataInputStream stream = new DataInputStream(url.openStream());
            byte b[]=new byte [stream.available()];
            do
            {
                read=stream.read(b);
                s+=new String(b);
            } while ((read!=-1)&&(read!=0));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return(s);
    }
    
 public static org.w3c.dom.Element getElementChild(org.w3c.dom.Element root,String name){
   org.w3c.dom.NodeList nd= root.getChildNodes();
   int i=0;
   while(i<nd.getLength()){ 
    if(nd.item(i).getNodeName().equals(name))
       return (org.w3c.dom.Element) nd.item(i);
    i++;   
   }
    return null;   
  }
 
 public static String getElementTextChildValue(org.w3c.dom.Element root,String name) {
   org.w3c.dom.NodeList nd= root.getChildNodes();
   org.w3c.dom.NodeList childs= null;
   int i=0, u=0;
   while(i<nd.getLength()){ 
    if(nd.item(i).getNodeName().equals(name)){
       childs=nd.item(i).getChildNodes();
       while(u<childs.getLength()){
         if(childs.item(u).getNodeName().equalsIgnoreCase("#text"))  
            return(childs.item(u).getNodeValue());
         u++;
       }  
     return null;  
    }
    i++;   
   }
    return null;   
  }

 private static final String VALIDATE = "http://xml.org/sax/features/validation";

	private static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

	private static final String SCHEMA_LOCATION_NS = "http://apache.org/xml/properties/schema/external-schemaLocation";

	private static final String TRUE = "true";

	private static final String ONE = "1";

	public static LinkedList getChildren(Element element) {
		LinkedList result = new LinkedList();
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element))
				result.add(child);
		}
		return result;
	}

	public static Element getFirstChild(Element element) {
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element))
				return (Element) child;
		}
		return null;
	}

	public static Element getNextElement(Element element) {
		Node next;

		try {
			next = element.getNextSibling();

			while (next != null && (next instanceof Element) == false) {
				next = next.getNextSibling();
			}
		} catch (Exception e) {
			return null;
		}
		return (Element) next;
	}

	public static Element getChildByTagName(Element element, String tag) {
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element)
					&& ((Element) child).getTagName().equals(tag))
				return (Element) child;
		}
		return null;
	}

	public static LinkedList getChildrenByTagName(Element element, String tag) {
		LinkedList result = new LinkedList();
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element)
					&& ((Element) child).getTagName().equals(tag))
				result.add(child);
		}
		return result;
	}

	public static Element getChildByLocalName(Element element, String tag) {
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element)
					&& ((Element) child).getLocalName().equals(tag))
				return (Element) child;
		}
		return null;
	}

	public static LinkedList getChildrenByLocalName(Element element, String tag) {
		LinkedList result = new LinkedList();
		NodeList children = element.getChildNodes();
		Node child;
		for (int index = 0; index < children.getLength(); index++) {
			child = children.item(index);
			if ((child instanceof Element)
					&& ((Element) child).getLocalName().equals(tag))
				result.add(child);
		}
		return result;
	}

	private static final ErrorHandler THROWER_ERROR_HANDLER = new ErrorHandler() {
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		public void warning(SAXParseException e) throws SAXException {
			throw e;
		}
	};

	public static ErrorHandler getThrowerErrorHandler() {
		return THROWER_ERROR_HANDLER;
	}

	public static boolean hasElement(Element element, String tag) {
		return element.getElementsByTagName(tag).getLength() > 0;
	}

	public static boolean hasElementNS(Element element, String tag, String ns) {
		return element.getElementsByTagNameNS(ns, tag).getLength() > 0;
	}

	public static String getStringFromElement(Element element) {
		return element.hasChildNodes() ? element.getFirstChild().getNodeValue()
				: "";
	}

	public static Element setTextToElement(Document document, Element element,
			String text) {
		element.appendChild(document.createTextNode(text));
		return element;
	}

	public static boolean hasChildren(Element element) {
		return !getChildren(element).isEmpty();
	}

	public static Element createLeafElement(Document document, String tagName,
			String text) {
		return XmlTools.setTextToElement(document, document
				.createElement(tagName), text);
	}

	public static Element createLeafElementNS(Document document, String nsURI,
			String qualifiedName, String text) {
		return XmlTools.setTextToElement(document, document.createElementNS(
				nsURI, qualifiedName), text);
	}

	public static int getIntFromElement(Element element) {
		return Integer.parseInt(getStringFromElement(element));
	}

	public static boolean getBool(String b) {
		return b.equals(TRUE) || b.equals(ONE);
	}

	public static boolean getBoolFromElement(Element element) {
		return getBool(getStringFromElement(element));
	}

	public static String getStringByTagName(Element element, String tag) {
		return getStringFromElement(getByTagAndIndex(element, tag, 0));
	}

	public static int getIntByTagName(Element element, String tag) {
		return Integer.parseInt(getStringByTagName(element, tag));
	}

	public static Element getByTagAndIndex(Element element, String tag,
			int index) {
		return (Element) element.getElementsByTagName(tag).item(index);
	}

	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();

	private static DocumentBuilder documentBuilder;

	private static DOMParser parser = new DOMParser();

	public XmlTools() {
		
	}

	public XmlTools(boolean nsAwareness) {
		this();
		setNamespaceAware(nsAwareness);
	}

	public static Document newDocument() {
                try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		parser.setErrorHandler(THROWER_ERROR_HANDLER);
		return documentBuilder.newDocument();
	}

	/*
	 * public void setCoalescing(boolean coalescing) {
	 * documentBuilderFactory.setCoalescing(coalescing); try { documentBuilder =
	 * documentBuilderFactory.newDocumentBuilder(); } catch
	 * (ParserConfigurationException e) { e.printStackTrace(); } }
	 * 
	 * public void setExpandEntityReferences(boolean expandEntityRef) {
	 * documentBuilderFactory.setExpandEntityReferences(expandEntityRef); try {
	 * documentBuilder = documentBuilderFactory.newDocumentBuilder(); } catch
	 * (ParserConfigurationException e) { e.printStackTrace(); } }
	 * 
	 * public void setIgnoringComments(boolean ignoreComments) {
	 * documentBuilderFactory.setIgnoringComments(ignoreComments); try {
	 * documentBuilder = documentBuilderFactory.newDocumentBuilder(); } catch
	 * (ParserConfigurationException e) { e.printStackTrace(); } }
	 * 
	 * public void setIgnoringElementContentWhitespace(boolean whitespace) {
	 * documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
	 * try { documentBuilder = documentBuilderFactory.newDocumentBuilder(); }
	 * catch (ParserConfigurationException e) { e.printStackTrace(); } }
	 */

	public void setNamespaceAware(boolean awareness) {
		documentBuilderFactory.setNamespaceAware(awareness);
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void setValidating(String schemaLocation) {
		try {
			if (schemaLocation == null) {
				parser.setFeature(VALIDATE, false);
			} else {
				parser.setFeature(VALIDATE, true);
				parser.setProperty(SCHEMA_LOCATION, schemaLocation);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void setValidatingNS(String schemaLocation) {
		try {
			if (schemaLocation == null) {
				parser.setFeature(VALIDATE, false);
			} else {
				parser.setFeature(VALIDATE, true);
				parser.setProperty(SCHEMA_LOCATION_NS, schemaLocation);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public static Document fileToDocument(String fileName) throws IOException,
			SAXException {
		// return documentBuilder.parse(fileName);
		synchronized (parser) {
			parser.parse(new InputSource(new FileInputStream(fileName)));
			return parser.getDocument();
		}
	}

	public static Document fileToDocument(File file) throws IOException, SAXException {
		synchronized (parser) {
			parser.parse(new InputSource(new FileInputStream(file)));
			return parser.getDocument();
		}
	}

	public static Document inputStreamToDocument(InputStream xml) throws IOException,
			SAXException {
		synchronized (parser) {
			parser.parse(new InputSource(xml));
			return parser.getDocument();
		}
	}

	public Document stringToDocument(String xml) throws IOException,
			SAXException {
		// return documentBuilder.parse(new InputSource(new StringReader(xml)));
		synchronized (parser) {
			parser.parse(new InputSource(new StringReader(xml)));
			return parser.getDocument();
		}
	}

	public Document readerToDocument(Reader xml) throws IOException,
			SAXException {
		// return documentBuilder.parse(new InputSource(new StringReader(xml)));
		synchronized (parser) {
			parser.parse(new InputSource(xml));
			return parser.getDocument();
		}
	}

	public static DocumentBuilder getValidatingParser(File schema) {
		final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(true);
		documentBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE,
				W3C_XML_SCHEMA);
		documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, schema);
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(XmlTools.getThrowerErrorHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return documentBuilder;
	}

	public static void dumpXML(Document document, File xml) throws Exception {
		OutputStream out = new FileOutputStream(xml);
		new XMLSerializer2(out).serialize(document);
		out.close();
	}

	public static void dumpXML(Document document, File xml, boolean indent)
			throws Exception {
		if (indent)
			XmlTools.indent(document);
		OutputStream out = new FileOutputStream(xml);
		new XMLSerializer2(out).serialize(document);
		out.close();
	}

	public static Document loadXML(File xml, File schema) throws Exception {
		return getValidatingParser(schema).parse(xml);
	}

	public Document stringToValidatedDocument(File schema, String xml)
			throws Exception {
		/*
		 * Document document = stringToDocument(xml); Transformer transformer =
		 * TransformerFactory.newInstance().newTransformer();
		 * ByteArrayOutputStream out = new ByteArrayOutputStream(); StreamResult
		 * res = new StreamResult(out); transformer.transform(new
		 * DOMSource(document), res); ByteArrayInputStream in = new
		 * ByteArrayInputStream(out.toByteArray()); return
		 * getValidatingParser(schema).parse(in);
		 */
		return validateDocument(schema, stringToDocument(xml));
	}

	public Document validateDocument(File schema, Document xml)
			throws Exception {
		/*
		 * Transformer transformer =
		 * TransformerFactory.newInstance().newTransformer();
		 * ByteArrayOutputStream out = new ByteArrayOutputStream(); StreamResult
		 * res = new StreamResult(out); transformer.transform(new
		 * DOMSource(xml), res); ByteArrayInputStream in = new
		 * ByteArrayInputStream(out.toByteArray()); return
		 * getValidatingParser(schema).parse(in);
		 */
		return getValidatingParser(schema).parse(getDocumentAsInputStream(xml));
	}

	public Document validateDocument(File schema, InputStream xml)
			throws Exception {
		/*
		 * Transformer transformer =
		 * TransformerFactory.newInstance().newTransformer();
		 * ByteArrayOutputStream out = new ByteArrayOutputStream(); StreamResult
		 * res = new StreamResult(out); transformer.transform(new
		 * DOMSource(xml), res); ByteArrayInputStream in = new
		 * ByteArrayInputStream(out.toByteArray()); return
		 * getValidatingParser(schema).parse(in);
		 */
		return getValidatingParser(schema).parse(xml);
	}

	public static InputStream getDocumentAsInputStream(Document xml)
			throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult res = new StreamResult(out);
		transformer.transform(new DOMSource(xml), res);
		return new ByteArrayInputStream(out.toByteArray());
	}

	public static InputStream getNodeAsInputStream(Node node) throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult res = new StreamResult(out);
		transformer.transform(new DOMSource(node), res);
		return new ByteArrayInputStream(out.toByteArray());
	}

	public InputStream getDocumentAsInputStream(File schema, Document xml)
			throws Exception {
		return getDocumentAsInputStream(validateDocument(schema, xml));
	}

	public static void indent(Document document) {
		indent(document.getDocumentElement(), "\n");
	}

	private static void indent(Element element, String indent) {
		if (getChildren(element).isEmpty())
			return;
		NodeList children = element.getChildNodes();
		String deeperIndent = indent + '\t';
		Node node;
		Document document = element.getOwnerDocument();
		for (int index = 0; index < children.getLength(); index++) {
			node = children.item(index);
			if (node.getNodeType() == Node.TEXT_NODE) {
				element.removeChild(node);
				index--;
				continue;
			}
			element.insertBefore(document.createTextNode(deeperIndent), node);
			index++;
			if (node.getNodeType() == Node.ELEMENT_NODE)
				indent((Element) node, deeperIndent);
		}
		element.appendChild(document.createTextNode(indent));
	}

	public static void copyAttributeNodes(Element source, Element target)
			throws Exception {
		NamedNodeMap sourceAttributes = source.getAttributes();
		for (int i = 0; i <= sourceAttributes.getLength() - 1; i++) {
			target.setAttributeNode((Attr) sourceAttributes.item(i).cloneNode(
					false));
		}
	}

	public static void moveAttributeNodes(Element source, Element target)
			throws Exception {
		NamedNodeMap sourceAttributes = source.getAttributes();
		int numOfAttributes = sourceAttributes.getLength();
		Attr attribute;
		for (int i = numOfAttributes - 1; i >= 0; i--) {
			target.setAttributeNode((Attr) (attribute = (Attr) sourceAttributes
					.item(i)).cloneNode(false));
			source.removeAttributeNode(attribute);
		}
	}

	public static String getXPath(Element element, Document document) {
		Node currNode = element;
		String xPath = "";
		Node parentNode;
		String currNodeName;
		do {
			currNodeName = currNode.getNodeName();
			parentNode = currNode.getParentNode();
			NodeList children = parentNode.getChildNodes();
			int nodePosition = 0;
			Node tempNode;
			for (int i = 0; i < children.getLength(); i++) {
				if (!((tempNode = children.item(i)) instanceof Element)) {
					continue;
				}
				if (tempNode.getNodeName().equals(currNodeName)) {
					nodePosition++;
				}
				if (children.item(i) == currNode) {
					break;
				}
			}
			xPath = "/"
					+ currNodeName
					+ (nodePosition > 1 ? "[" + String.valueOf(nodePosition)
							+ "]" : "") + xPath;
			currNode = parentNode;
		} while (parentNode != document);
		return xPath;
	}

	/**
	 * @param currentNode
	 */
	public static String getFirstChildXPath(Element currentNode,
			Document document) {
		Element child;

		child = XmlTools.getFirstChild(currentNode);
		return XmlTools.getXPath(child, document);
	}

	public static Element getElementByXPath(String xpath, Document document) {
		StringTokenizer tokenizer;
		int tokenCount = 0, cardinality = 0, index;
		String tagname;
		Element rootElement;
		Element currentNode = null;
		LinkedList childrenList;

		try {
			tokenizer = new StringTokenizer(xpath, "/");
			tokenCount = tokenizer.countTokens();

			if (tokenCount >= 1) {
				tagname = tokenizer.nextToken();
				rootElement = document.getDocumentElement();

				if (tagname.equals(rootElement.getNodeName()) == false) {
					return null;
				}
				currentNode = rootElement;
				for (int i = 1; i < tokenCount; i++) {
					// scan all tokens
					tagname = tokenizer.nextToken();

					// checking cardinality
					if ((index = tagname.indexOf('[')) != -1) {
						int closedSquareIndex = tagname.indexOf(']');
						String cardString = tagname.substring(index + 1,
								closedSquareIndex);

						cardinality = Integer.parseInt(cardString);

						// removeing cardinality from tagname
						tagname = tagname.substring(0, index);

					} else
						cardinality = 1;

					childrenList = XmlTools.getChildrenByTagName(currentNode,
							tagname);

					currentNode = (Element) childrenList.get(cardinality - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return currentNode;
	}

	public static boolean isLeaf(Element element) {
		return getChildren(element).size() == 0;
	}

	public static void main(String[] args) throws Exception {
		XmlTools XmlTools;
		Document doc;
		(XmlTools = new XmlTools(true)).indent(doc = XmlTools
				.fileToDocument(args[0]));
		XmlTools.dumpXML(doc, new File(args[1]));
	}

	public Document getCollapsedDocument(Document oldDoc, Document newDoc) {
		if (newDoc.getDocumentElement() != null) {
			newDoc.removeChild(newDoc.getDocumentElement());
		}

		collapse(oldDoc.getDocumentElement(), newDoc, newDoc);
		return newDoc;
	}

	public static void collapse(Element oldNode, Node newNodeParent,
			Document newDoc) {
		/*
		 * Element newNode = (Element)
		 * newNodeParent.appendChild(newDoc.importNode(oldNode, false));
		 * LinkedList oldNodeChildren = getChildren(oldNode); Element
		 * currOldNode; Node currNewNode = null; for (int i = 0; i <
		 * oldNodeChildren.size(); i++) { if (isLeaf((currOldNode = (Element)
		 * oldNodeChildren.get(i)))) {
		 * newNode.appendChild(newDoc.importNode(currOldNode, true)); } else {
		 * currNewNode = newNode.appendChild(newDoc.importNode(currOldNode,
		 * false)); collapse(currOldNode, currNewNode, newDoc); } }
		 */
		Element newNode = (Element) newDoc.importNode(oldNode, false);
		newNodeParent.appendChild(newNode);
		LinkedList oldNodeChildren = getChildren(oldNode);
		Element currOldChild;
		Element currNewChild;
		for (int i = 0; i < oldNodeChildren.size(); i++) {
			if (isLeaf(currOldChild = (Element) oldNodeChildren.get(i))) {
				currNewChild = (Element) newDoc.importNode(currOldChild, true);
				newNode.appendChild(currNewChild);
			} else {
				collapse(currOldChild, newNode, newDoc);
			}
		}
	}
    /**
     * Serializes the input Document source into the specified output stream.
     * @param source the document source that have to be serialized into the output stream.
     * @param out the output stream where copy the document.
     * @return true if the document is correctly copied into the stream, false otherwise.
     * @throws ExceptionReport if something wrong happens during copy.
     */
    public static boolean serialize(DOMSource source, OutputStream out)
    {
        TransformerFactory fac = TransformerFactory.newInstance();
        Transformer trans;
        try
        {
            trans = fac.newTransformer();
            StreamResult res = new StreamResult(out);
            trans.transform(source,res);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Dumps the xml document directly on an output stream (write the document in the output Stream) and close the stream.
     * @param document the document that have to dumpped.
     * @param out the Stream in which write the file.
     * throws Exception if something wrong happen during the writing of the document.
     */
    public static void dumpXML(Document document, OutputStream out) throws Exception
    {
        XmlTools.serialize(new DOMSource(document), out);
        out.close();
    }
    
    public static Document stringToDom(String xmlSource) 
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }
    
   public static void copyInputStreamToOutputStream(InputStream in, OutputStream out) 
   throws IOException {


    synchronized (in) {
      synchronized (out) {

        byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = in.read(buffer);
          if (bytesRead == -1) break;
          out.write(buffer, 0, bytesRead);
        }
      }
    }
 }
    
}