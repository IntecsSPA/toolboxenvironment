package it.intecs.pisa.toolbox.security.validator;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Indenter;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.StringAttribute;

import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Subject;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis2.context.MessageContext;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLStatement;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class implements a PEP; it generates an XACML
 * Request and pass it to the PDP.
 *
 * @author Stefano
 */
public class ToolboxPEP {

    String NAMESPACE_GML = "http://www.opengis.net/gml";
    String NAMESPACE_OGC = "http://www.opengis.net/ogc";
   
    private String subjectIdvalue = null;   //the subject identifier
   
    private URI resourceIdValue = null;     //the resource being requested
    private String actionIdValue = null;    //the action being requested
    private Document soapDoc = null;        //the SOAP request in input to be authorised
  
    private SAMLAssertion samlAssertion = null;

    public ToolboxPEP() {
    }

    /**
     * Sets up the Subject section of the request. This Request only has
     * one Subject section, and it uses the default category. To create a
     * Subject with a different category, you simply specify the category
     * when you construct the Subject object.
     *
     * @return a Set of Subject instances for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public Set setupSubjects() throws URISyntaxException {
        HashSet attributes = new HashSet();

        // setup the id and value for the requesting subject
        URI subjectId =
                new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");

        // create the subject section with two attributes, the first with
        // the subject's identity...
        attributes.add(new Attribute(subjectId, null, null, new StringAttribute(subjectIdvalue)));
        // ...and the second with the SAML attributes in the request

        SAMLAttributeStatement attrStatem = null;
        SAMLAttribute attr = null;
        for (Iterator iter = samlAssertion.getStatements(); iter.hasNext();) {
            SAMLStatement statem = (SAMLStatement) iter.next();
            if (statem instanceof SAMLAttributeStatement == false) {
                continue;
            }
            attrStatem = (SAMLAttributeStatement) statem;
            for (Iterator attriter = attrStatem.getAttributes(); attriter.hasNext();) {
                attr = (SAMLAttribute) attriter.next();
                attributes.add(new Attribute(new URI(attr.getName()), null, null, new StringAttribute(attr.getValues().next().toString())));
            }

        }//end retrieve SAML attributes


        // bundle the attributes in a Subject with the default category
        HashSet subjects = new HashSet();
        subjects.add(new Subject(attributes));

        return subjects;
    }

    /**
     * Creates a Resource specifying the resource-id, a required attribute.
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public Set setupResource() throws URISyntaxException {
        HashSet resource = new HashSet();

        // the resource being requested
        AnyURIAttribute value =
                new AnyURIAttribute(resourceIdValue);

        // create the resource using a standard, required identifier for
        // the resource being requested
        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
                null, null, value));

        if (soapDoc.getElementsByTagNameNS("http://www.opengis.net/cat/csw/2.0.2", "GetRecords").getLength() > 0) {
            handleGetRecords();
        }

        return resource;
    }

    /**
     * Creates an Action specifying the action-id, an optional attribute.
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public Set setupAction() throws URISyntaxException {
        HashSet action = new HashSet();

        // this is a standard URI that can optionally be used to specify
        // the action being requested
        URI actionId =
                new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");

        // create the action
        action.add(new Attribute(actionId, null, null,
                new StringAttribute(actionIdValue)));

        return action;
    }

    /**
     * Command-line interface that creates a new Request by invoking the
     * static methods in this class. The Request has no Environment section.
     */
    public static void main(String[] args) throws Exception {
        // create the new Request...
        ToolboxPEP reqBuild = new ToolboxPEP();
        reqBuild.checkPolicies("anHmaId", new URI("http://localhost:8080/axis2/execute"), "serviceX", getRequestPayload());

    }

    /**
     * 
     *  Returns:
     *  0 - permit
     *  1 - deny
     *  2 - a decision cannot be made (!?)
     *  3 - no applicable policies were found for the request
     *
     * */
    public int enforceChecks(URI requestURI, String soapAction, SAMLAssertion saml, Node soap) {
        int resp = 2;

        this.samlAssertion = saml;

        try {
            resp = checkPolicies(this.subjectIdvalue, requestURI, soapAction, soap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resp;
    }

    /**
     * 
     *  Returns:
     *  0 - permit
     *  1 - deny
     *  2 - a decision cannot be made (!?)
     *  3 - no applicable policies were found for the request
     *
     * */
    public int checkPolicies(String subjectId,
            URI resourceId, String actionId, Node soapDoc) throws Exception {


        subjectIdvalue = subjectId;
        resourceIdValue = resourceId;
        actionIdValue = actionId;

        this.soapDoc = (Document) soapDoc;

        //
        RequestCtx requestCtx =
                new RequestCtx(setupSubjects(), setupResource(),
                setupAction(), new HashSet(), this.soapDoc.getDocumentElement());


        System.out.println("The generated XACML request :");
        // encode the Request and print it to standard out
        requestCtx.encode(System.out, new Indenter());

        
        //invoke the PDP

        ToolboxPDP toolboxPDP = null;

        toolboxPDP = new ToolboxPDP();

        // evaluate the request
        ResponseCtx response = toolboxPDP.evaluate(requestCtx);

        System.out.println("The XACML response :");

        response.encode(System.out, new Indenter());
        Object resp = response.getResults().iterator().next();

        return ((Result) resp).getDecision();

    }

    /***
     * This method can be used to run and test this class
     * @return
     */
    private static Document getRequestPayload() {

        MessageContext msgCtx = MessageContext.getCurrentMessageContext();
        String serviceRealPath = msgCtx.getConfigurationContext().getRealPath("/").getPath(); // axis2\web-inf directory

        Document xmlInputStream = null;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            xmlInputStream = docBuilder.parse(new File(serviceRealPath + "toolbox/body.xml"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return xmlInputStream;
    }

    /* The following method serves a twofold purpose:
     * a) converts the GML3.1.x geometries of the SOAP request to GML2.1 geometries; 
     *    this is due to a limitation in the JTS framework used by the geo-pdp library
     * b) remove the "PropertyName" element as the first child of a OGC spatial operator,
     *    so that in the GeoXACML suthorization file a RequestContextPath can be used to retrieve 
     *    GML2.1 geometries
     */
    private void handleGetRecords() {

        if (soapDoc.getElementsByTagNameNS(NAMESPACE_GML, "*").getLength() == 0) {
            createDefaultBBOX();
        }

        NodeList envelopeList = soapDoc.getElementsByTagNameNS(NAMESPACE_GML, "Envelope");
        if (envelopeList.getLength() != 0) {
            Node envelope = envelopeList.item(0);
            Element box = soapDoc.createElementNS(NAMESPACE_GML, "Box");
            box.setAttribute("srsName", "http://www.opengis.net/gml/srs/epsg.xml#4326");

            NodeList lowerCornerList = ((Element) envelope).getElementsByTagNameNS(NAMESPACE_GML, "lowerCorner");
            if (lowerCornerList.getLength() != 0) {
                String lowerCorner = lowerCornerList.item(0).getTextContent().trim();

                Element coord1 = soapDoc.createElementNS(NAMESPACE_GML, "coord");
                box.appendChild(coord1);
                // The X value corresponds to the longitude of the coordinate
                Element X1 = soapDoc.createElementNS(NAMESPACE_GML, "X");
                ((Node) X1).setTextContent(lowerCorner.substring(lowerCorner.indexOf(' '), lowerCorner.length() - 1));

                // The Y value corresponds to the latitude of the coordinate
                Element Y1 = soapDoc.createElementNS(NAMESPACE_GML, "Y");
                ((Node) Y1).setTextContent(lowerCorner.substring(0, lowerCorner.indexOf(' ')));

                coord1.appendChild(X1);
                coord1.appendChild(Y1);
            }
            NodeList upperCornerList = ((Element) envelope).getElementsByTagNameNS(NAMESPACE_GML, "upperCorner");
            if (upperCornerList.getLength() != 0) {
                String upperCorner = upperCornerList.item(0).getTextContent().trim();

                Element coord1 = soapDoc.createElementNS(NAMESPACE_GML, "coord");
                box.appendChild(coord1);
                // The X value corresponds to the longitude of the coordinate
                Element X2 = soapDoc.createElementNS(NAMESPACE_GML, "X");
                ((Node) X2).setTextContent(upperCorner.substring(upperCorner.indexOf(' '), upperCorner.length() - 1));

                // The Y value corresponds to the latitude of the coordinate
                Element Y2 = soapDoc.createElementNS(NAMESPACE_GML, "Y");
                ((Node) Y2).setTextContent(upperCorner.substring(0, upperCorner.indexOf(' ')));

                coord1.appendChild(X2);
                coord1.appendChild(Y2);
            }

            Node envelopeParent = envelope.getParentNode();

            Node newEnvelopeParent = envelopeParent.cloneNode(false);
            newEnvelopeParent.appendChild(box);

            envelopeParent.getParentNode().replaceChild(newEnvelopeParent, envelopeParent);

        }

        /* Temporary comment, wating for a suitable handling of the spatial operators other than "BBOX"
         * in the GeoXACML policy file
        
        String[] spatialOperators = {"BBOX", "Beyond", "Contains", "Crosses", "DWithin", "Disjoint", "Equals", "Intersects", "Overlaps", "Touches", "Within"};
        for (int i = 0; i < spatialOperators.length; i++) {
            NodeList tempList = soapDoc.getElementsByTagNameNS(NAMESPACE_OGC, spatialOperators[i]);
            for (int j = 0; j < tempList.getLength(); j++) {
                Node spatialOperator = tempList.item(j);
                for (Node child = spatialOperator.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child instanceof Element && child.getLocalName().equals("PropertyName")) {
                        spatialOperator.removeChild(child);
                    }
                }
            }
        }
         */
        
        
        /*try {
            DOMUtil.dumpXML((Document) this.soapDoc, System.out, true);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
         
    }

    /* The following method creates a default OGC BBOX spatial operator in a SOAP request with 
     * no (BBOX) spatial operator.
     */
    private void createDefaultBBOX() {

        Element bbox = soapDoc.createElementNS(NAMESPACE_OGC, "BBOX");

        Element box = soapDoc.createElementNS(NAMESPACE_GML, "Box");
        box.setAttribute("srsName", "http://www.opengis.net/gml/srs/epsg.xml#4326");
        bbox.appendChild(box);

        Element coord1 = soapDoc.createElementNS(NAMESPACE_GML, "coord");
        box.appendChild(coord1);
        // The X value corresponds to the longitude of the coordinate
        Element X1 = soapDoc.createElementNS(NAMESPACE_GML, "X");
        ((Node) X1).setTextContent("-180");

        // The Y value corresponds to the latitude of the coordinate
        Element Y1 = soapDoc.createElementNS(NAMESPACE_GML, "Y");
        ((Node) Y1).setTextContent("-90");

        coord1.appendChild(X1);
        coord1.appendChild(Y1);


        Element coord2 = soapDoc.createElementNS(NAMESPACE_GML, "coord");
        box.appendChild(coord2);
        // The X value corresponds to the longitude of the coordinate
        Element X2 = soapDoc.createElementNS(NAMESPACE_GML, "X");
        ((Node) X2).setTextContent("180");

        // The Y value corresponds to the latitude of the coordinate
        Element Y2 = soapDoc.createElementNS(NAMESPACE_GML, "Y");
        ((Node) Y2).setTextContent("90");

        coord2.appendChild(X2);
        coord2.appendChild(Y2);

        // The following assignment can throw an Exception
        Node filter = soapDoc.getElementsByTagNameNS(NAMESPACE_OGC, "Filter").item(0);
        NodeList filterChildren = filter.getChildNodes();

        if (filterChildren.getLength() > 0) {

            Element filterChildElement = null;
            Element andOperator = null;
            for (int index = 0; index < filterChildren.getLength(); index++) {
                if (filterChildren.item(index) instanceof Element) {
                    filterChildElement = (Element) filterChildren.item(index);
                    break;
                }
            }
            if (filterChildElement.getLocalName().equals("And")) {
                filterChildElement.appendChild(bbox);
            } else {
                andOperator = soapDoc.createElementNS(NAMESPACE_OGC, "And");
                filter.removeChild(filterChildElement);
                andOperator.appendChild(filterChildElement);
                andOperator.appendChild(bbox);
                filter.appendChild(andOperator);

            }
        }
    }
}