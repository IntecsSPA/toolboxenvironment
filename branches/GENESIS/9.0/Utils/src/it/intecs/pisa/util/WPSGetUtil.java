/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util;

import java.io.IOException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author simone
 */
public class WPSGetUtil {

    public static Document getCapabilitiesRequestDocument() throws IOException, SAXException {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<wps:GetCapabilities xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
                + "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" service=\"WPS\" language=\"en-UK\">"
                + "<wps:AcceptVersions>"
                + "<ows:Version>1.0.0</ows:Version>"
                + "</wps:AcceptVersions>"
                + "</wps:GetCapabilities>";
        DOMUtil du = new DOMUtil();
        return du.stringToDocument(xmlString);
    }

    public static Document getCapabilitiesRequestDocument(String operation) throws IOException, SAXException {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<DescribeProcess xmlns=\"http://www.opengis.net/wps/1.0.0\" "
                + "xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "service=\"WPS\" "
                + "version=\"1.0.0\" "
                + "language=\"en-UK\">"
                + "<ows:Identifier>"
                + operation
                + "</ows:Identifier>"
                + "</DescribeProcess>";
        DOMUtil du = new DOMUtil();
        return du.stringToDocument(xmlString);
    }

    public static Document getWPSExceptionDocument(String code, String text ,String locator) throws IOException, SAXException {
        String xmlString = "<ows:ExceptionReport "
                + "xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "version=\"1.0.0\" xml:lang=\"en-CA\">"
                + "<ows:Exception exceptionCode=\""
                + code
                + "\" locator=\""
                + locator
                + "\">"
                + "<ows:ExceptionText>"
                + text
                + "</ows:ExceptionText>"
                + "</ows:Exception>"
                + "</ows:ExceptionReport>";
        DOMUtil du = new DOMUtil();
        return du.stringToDocument(xmlString);
    }

    public static Document getWPSExceptionDocument(String code, String text) throws IOException, SAXException {
        String xmlString = "<ows:ExceptionReport "
                + "xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "version=\"1.0.0\" xml:lang=\"en-CA\">"
                + "<ows:Exception exceptionCode=\""
                + code
                + "\">"
                + "<ows:ExceptionText>"
                + text
                + "</ows:ExceptionText>"
                + "</ows:Exception>"
                + "</ows:ExceptionReport>";
        DOMUtil du = new DOMUtil();
        return du.stringToDocument(xmlString);
    }

}
