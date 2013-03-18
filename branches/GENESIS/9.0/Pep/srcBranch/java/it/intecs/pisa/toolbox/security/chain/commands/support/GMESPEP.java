package it.intecs.pisa.toolbox.security.chain.commands.support;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Indenter;

import com.sun.xacml.attr.StringAttribute;

import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Subject;
import java.io.ByteArrayOutputStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class implements a PEP; it generates an XACML
 * Request and pass it to the PDP.
 */
public class GMESPEP {
    /*
    private static String ORGANIZATION ="organization";
    private static String HMA_ID ="hmaId";
    private static String COUNTRY ="c";
     */

    private static final String SUBMIT_NAMESPACE = "http://earth.esa.int/hma/ordering";
    private static final String SUBMIT_REQUEST = "Submit";
    private static final String SUBMIT_RESOURCE = "identifier";
    //private static final String SUBMIT_RESOURCE = "collectionId";
    //private static final String PARENT_IDENTIFIER_URI = "parentIdentifier";
    private static final String HMA_SERVICE_NAME = "hmaServiceName";
    private static final String HMA_SERVICE_NAME_SEPARATOR = ";";
    private static final String ATTRIBUTE_NAME = "AttributeName";
    private String subjectIdvalue = null; //the subject identifier
    private String actionIdValue = null; //the action being requested (request)
    private String actionNamespaceValue = null; // namespace of the action (namespace request)
    private Document mDocWithAssertion = null;
    private boolean mAnonymous = false;
    // the directory where policy files, beginning with policy_, can be found
    private String mPolicyDir = "";
    static final Logger logger = Logger.getLogger(GMESPEP.class);

    public GMESPEP(String policyDir, boolean anonymous) {
        mPolicyDir = policyDir;
        mAnonymous = anonymous;
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
    public Set setupSubjects() throws URISyntaxException, Exception {
        HashSet attributes = new HashSet();
        HashSet subjects = new HashSet();
        int i, j;

        if (mAnonymous) {
            // add default attributes to the context
            attributes.add(new Attribute(new URI("userProfileGMES"), null, null, new StringAttribute("0")));
            attributes.add(new Attribute(new URI("hmaServiceName"), null, null, new StringAttribute("none")));

        } else {

            NodeList list = mDocWithAssertion.getElementsByTagNameNS("urn:oasis:names:tc:SAML:1.0:assertion", "Attribute");
            if (list == null || list.getLength() == 0) {
                throw new Exception("Cannot get Attribute element");
            }

            for (i = 0; i < list.getLength(); i++) {
                Element attr = (Element) list.item(i);
                NodeList serviceNamesList = attr.getElementsByTagName("AttributeValue");
                String attributeName = attr.getAttribute(ATTRIBUTE_NAME);
                if (attributeName.compareTo(HMA_SERVICE_NAME) == 0) {

                    if ((serviceNamesList == null)
                            || (serviceNamesList.getLength() == 0)) {
                        throw new Exception("AttributeValue not found");
                    }
                    String serviceNames = serviceNamesList.item(0).getTextContent();

                    String serviceNamesArr[] = serviceNames.split(HMA_SERVICE_NAME_SEPARATOR);
                    for (j = 0; j < serviceNamesArr.length; j++) {
                        attributes.add(new Attribute(new URI(attributeName),
                                null, null,
                                new StringAttribute(serviceNamesArr[j])));
                    }
                } else {
                    for (j = 0; j < serviceNamesList.getLength(); j++) {
                        attributes.add(new Attribute(new URI(attributeName), null, null,
                                new StringAttribute(serviceNamesList.item(j).getTextContent())));
                    }
                }
            }
        }

        // bundle the attributes in a Subject with the default category
        subjects.add(new Subject(attributes));

        return subjects;
    }

//    public Set setupSubjectsFake() throws URISyntaxException, Exception {
//        HashSet attributes = new HashSet();
//        HashSet subjects = new HashSet();
//        
//        attributes.add(new Attribute(new URI("hmaServiceName"), null, null, new StringAttribute("GMOSAIC.GMOSAIC_NonEmer.Signed")));
//          
//        // bundle the attributes in a Subject with the default category
//        subjects.add(new Subject(attributes));
//
//        return subjects;
//    }
//
    /**
     * Creates a Resource specifying the resource-id, a required attribute, as an empty value
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public Set setupResource() throws URISyntaxException {
        HashSet resource = new HashSet();

        StringAttribute value = new StringAttribute("");

        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
                null, null, value));

        return resource;
    }

    /**
     * Creates a Resource specifying the resource-id, a required attribute, as the parameter
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public Set setupResource(String resourceIdValue) throws URISyntaxException {
        HashSet resource = new HashSet();

        StringAttribute value = new StringAttribute(resourceIdValue);

        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
                null, null, value));

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

        URI actionNamespace = new URI("urn:oasis:names:tc:xacml:1.0:action:action-namespace");
        action.add(new Attribute(actionNamespace, null, null,
                new StringAttribute(actionNamespaceValue)));

        return action;
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
    public int enforceChecks(String soapAction, Document soap, Document docWithAssertion) {
        int resp = 2;

        //this.samlAssertion = saml;

        try {
            resp = checkPolicies(this.subjectIdvalue, soapAction, soap, docWithAssertion);
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
    public int checkPolicies(String subjectId, String actionId, Document soapDoc, Document docWithAssertion) throws Exception {
        logger.info("checkPolicies");

        subjectIdvalue = subjectId;
        actionIdValue = actionId.substring(0, actionId.indexOf(' '));
        actionNamespaceValue = actionId.substring(actionId.indexOf(' ') + 1, actionId.length());
        mDocWithAssertion = docWithAssertion;

        if (actionNamespaceValue.equals(SUBMIT_NAMESPACE) && actionIdValue.equals(SUBMIT_REQUEST)) {
            return checkSubmitPolicies(soapDoc);
        }
        //
        RequestCtx requestCtx =
                new RequestCtx(setupSubjects(), setupResource(),
                setupAction(), new HashSet(), soapDoc);


        ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        requestCtx.encode(outStr, new Indenter());
        logger.info("The generated XACML request: " + outStr.toString());

        //invoke the PDP

        GMESPDP toolboxPDP = null;

        toolboxPDP = new GMESPDP(mPolicyDir);

        // evaluate the request
        ResponseCtx response = toolboxPDP.evaluate(requestCtx);

        ByteArrayOutputStream outStr2 = new ByteArrayOutputStream();
        response.encode(outStr2, new Indenter());
        logger.info("The XACML response: " + outStr2.toString());


        Object resp = response.getResults().iterator().next();

        return ((Result) resp).getDecision();

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
    public int checkSubmitPolicies(Document soapDoc) throws Exception {
        logger.info("checkPolicies");

        NodeList list = mDocWithAssertion.getElementsByTagNameNS(SUBMIT_NAMESPACE, SUBMIT_RESOURCE);
        if (list == null || list.getLength() == 0) {
            throw new Exception("Cannot get resources from request");
        }
        HashSet resourceIdValues = new HashSet(); //the datasets to be accessed
        String prefix = "urn:ogc:def:EOP:GSC:";
        int prefixLen = prefix.length();

        for (int i = 0; i < list.getLength(); i++) {
            Element resource = (Element) list.item(i);
            NodeList textFNList = resource.getChildNodes();

            String tempIdentifier = ((Node) textFNList.item(0)).getNodeValue().trim();
            if (!tempIdentifier.startsWith(prefix)) {
                throw new Exception("At least a resource is malformed (prefix not matched)");
            }

            int endIdentifier = tempIdentifier.indexOf(':', prefixLen);

            String identifier = tempIdentifier;
            if (endIdentifier != -1) {
                identifier = tempIdentifier.substring(0, endIdentifier);
            }
            resourceIdValues.add(identifier);
        }

        int decision = 0;
        Iterator iterator = resourceIdValues.iterator();

        while (iterator.hasNext()) {

            //invoke the PDP

            GMESPDP toolboxPDP = null;

            toolboxPDP = new GMESPDP(mPolicyDir);

            RequestCtx requestCtx =
                    new RequestCtx(setupSubjects(), setupResource((String) iterator.next()),
                    setupAction(), new HashSet(), soapDoc);


            ByteArrayOutputStream outStr = new ByteArrayOutputStream();
            requestCtx.encode(outStr, new Indenter());
            logger.info("The generated XACML request: " + outStr.toString());

            // evaluate the request
            ResponseCtx response = toolboxPDP.evaluate(requestCtx);

            ByteArrayOutputStream outStr2 = new ByteArrayOutputStream();
            response.encode(outStr2, new Indenter());
            logger.info("The XACML response: " + outStr2.toString());

            Object resp = response.getResults().iterator().next();

            decision = ((Result) resp).getDecision();
            if (decision != 0) {
                break;
            }
        }
        return decision;
    }
}
