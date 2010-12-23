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


/**
 * This class implements a PEP; it generates an XACML
 * Request and pass it to the PDP.
 *
 * @author Stefano
 */
public class ToolboxPEP
{
    /*
    private static String ORGANIZATION ="organization";
    private static String HMA_ID ="hmaId";
    private static String COUNTRY ="c";
    */
	
    private String subjectIdvalue = null; //the subject identifier
    /*
    private String cValue = null;
    private String oValue = null;
    private String hmaProjectNameValue = null;
    private String hmaAccountValue = null;
     */
    private URI resourceIdValue = null; // the resource being requested
    private String actionIdValue = null; //the action being requested
    private Node soapDoc = null;

    // MRB ADDITION 2010-01
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
        for (Iterator iter=samlAssertion.getStatements(); iter.hasNext();){
            SAMLStatement statem = (SAMLStatement) iter.next();
            if (statem instanceof SAMLAttributeStatement == false)
                continue;
            attrStatem = (SAMLAttributeStatement) statem;
            for (Iterator attriter=attrStatem.getAttributes(); attriter.hasNext();){
                attr = (SAMLAttribute) attriter.next();
                attributes.add(new Attribute(new URI(attr.getName()), null, null, new StringAttribute(attr.getValues().next().toString())));
            }

        }//end retrieve SAML attributes

        /*
        attributes.add(new Attribute(new URI("hmaAccount"), null, null, 
                                     new StringAttribute(hmaAccountValue)));
        //..and so on
        attributes.add(new Attribute(new URI("hmaProjectName"), null, null, 
                new StringAttribute(hmaProjectNameValue)));
        
        attributes.add(new Attribute(new URI("c"), null, null, 
                new StringAttribute(cValue)));
        
        attributes.add(new Attribute(new URI("o"), null, null, 
                new StringAttribute(oValue)));
         */

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
    public static void main(String [] args) throws Exception {
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
    public int enforceChecks(URI requestURI, String soapAction, SAMLAssertion saml, Node soap){
        int resp = 2;
        
        /**
         * here I could retrieve SAML attributes and pass them as XACML attribute in the request, in the following way.
         */
        /*  
        SAMLAttributeStatement attrStatem = null;
        SAMLAttribute attr = null;
        for (Iterator iter=saml.getStatements(); iter.hasNext();){
            SAMLStatement statem = (SAMLStatement) iter.next();
            if (statem instanceof SAMLAttributeStatement == false)
                continue;
            attrStatem = (SAMLAttributeStatement) statem;
            for (Iterator attriter=attrStatem.getAttributes(); attriter.hasNext();){
                attr = (SAMLAttribute) attriter.next();
                if (attr.getName().compareTo(ToolboxPEP.ORGANIZATION)==0){
                    this.oValue = attr.getValues().next().toString();
                    continue;
                }
                if (attr.getName().compareTo(ToolboxPEP.HMA_ID)==0){
                    this.subjectIdvalue = attr.getValues().next().toString();
                    continue;
                }
                if (attr.getName().compareTo(ToolboxPEP.COUNTRY)==0){
                    this.cValue = attr.getValues().next().toString();
                    continue;
                }
            }
            
        }//end retrieve SAML attributes
        */
        
         // MRB ADDITION 2010-01
        this.samlAssertion = saml;

        try{
            resp = checkPolicies(this.subjectIdvalue, requestURI, soapAction, soap);
        }
        catch (Exception ex){
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
			URI resourceId, String actionId, Node soapDoc) throws Exception{
        

    	subjectIdvalue = subjectId;
		/*cValue = "";
		oValue = "";
		hmaProjectNameValue = "";
		hmaAccountValue = "";*/
		resourceIdValue = resourceId;
		actionIdValue = actionId;
		this.soapDoc = soapDoc;
		
        //
        RequestCtx requestCtx =
            new RequestCtx(setupSubjects(), setupResource(),
            		setupAction(),  new HashSet(), soapDoc);
        
        
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
    		xmlInputStream = docBuilder.parse (new File(serviceRealPath+"toolbox/body.xml"));

    	}catch (Exception ex){
    		ex.printStackTrace();
    	}
    	return xmlInputStream;
     }
    
    

}