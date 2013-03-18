package it.intecs.pisa.toolbox.security.chain.commands.support;

import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;

import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;

import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.PolicyFinder;

import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.SelectorModule;
//import com.sun.xacml.finder.impl.FilePolicyModule;
import com.sun.xacml.support.finder.FilePolicyModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class DailPDP
{
    // this is the actual PDP from sunxacml we'll use for policy evaluation
    private PDP pdp = null;
    
    // the directory where policy files, beginning with policy_, can be found
    private String mPolicyDir = "";

    /**
     * Default constructor.
     * @param policyDir where policy files must be searched for
     */
    public DailPDP(String policyDir) throws Exception {
        
        setPolicyDir(policyDir);
        
        // load the configuration
        //ConfigurationStore store = new ConfigurationStore();

        // use the default factories from the configuration
        //store.useDefaultFactories();

        // get the PDP configuration's and setup the PDP
        //pdp = new PDP(store.getDefaultPDPConfig());
        
    }
    
    
    /**
     * Sets the directory where policy files, beginning with policy_, can be found
     */
    public void setPolicyDir(String policyDir) {
        mPolicyDir = policyDir;
    }


    /**
     * Evaluates the given request and returns the Response that the PDP
     * will hand back to the PEP.
     * sunxacml library support for this has not been used and tested.
     *
     * @param requestFile the name of a file that contains a Request
     *
     * @return the result of the evaluation
     *
     * @throws IOException if there is a problem accessing the file
     * @throws ParsingException if the Request is invalid
     */
    public ResponseCtx evaluate(String requestFile)
        throws IOException, ParsingException
    {
        // setup the request based on the file
        RequestCtx request =
            RequestCtx.getInstance(new FileInputStream(requestFile));

        // evaluate the request
        return pdp.evaluate(request);
    }
    
    /**
     * Evaluates the given request and returns the Response that the PDP
     * will hand back to the PEP.
     *
     * @param requestFile the name of a file that contains a Request
     *
     * @return the result of the evaluation
     *
     * @throws IOException if there is a problem accessing the file
     * @throws ParsingException if the Request is invalid
     */
    public ResponseCtx evaluate(RequestCtx request)
        throws IOException, ParsingException
    {
    	//load policy and init
    	String service = ((Attribute) request.getResource().iterator().next()).getValue().encode();
    	String[] policy = gePolicyFiles(service.toString().substring(service.toString().lastIndexOf("/") + 1));
    	init(policy);
        // evaluate the request
        return pdp.evaluate(request);
    }

//    /**
//     * This method lets you invokethe PDP directly from the command-line.
//     *
//     * @param args the input arguments to the class. They are either the
//     *             flag "-config" followed by a request file, or a request
//     *             file followed by one or more policy files. In the case
//     *             that the configuration flag is used, the configuration
//     *             file must be specified in the standard java property,
//     *             com.sun.xacml.PDPConfigFile.
//     */
//    public static void main(String [] args) throws Exception {
//        if (args.length < 2) {
//            System.out.println("Usage: -config <request>");
//            System.out.println("       <request> <policy> [policies]");
//            System.exit(1);
//        }
//        
//        DAILPDP simplePDP = null;
//        String requestFile = null;
//        
//        if (args[0].equals("-config")) {
//            requestFile = args[1];
//            simplePDP = new DAILPDP("testData");
//        } else {
//            requestFile = args[0];
//            String [] policyFiles = new String[args.length - 1];
//            
//            for (int i = 1; i < args.length; i++)
//                policyFiles[i-1] = args[i];
//
//            simplePDP = new DAILPDP("testData");
//            
//        }
//
//        // evaluate the request
//        ResponseCtx response = simplePDP.evaluate(requestFile);
//
//        // for this sample program, we'll just print out the response
//        response.encode(System.out, new Indenter());
//    }
    
    /**
     * 
     * @return Returns the list of the XACML policy files that need to be checked by the PDP
     */
    public String[] gePolicyFiles(String serviceName){
        String[] arr = null;
        
        Vector v = new Vector();

        File dir = new File(mPolicyDir);
    
        String[] children = null;
        
        // It is also possible to filter the list of returned files.
        // This example does not return any files that start with `.'.
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return ((name.startsWith("policy")) && (name.endsWith(".xml")));
            }
        };
        children = dir.list(filter);
        
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            String filename;
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                filename = children[i];
                if (mPolicyDir.length() == 0) {
                    v.add(filename);
                    System.out.println("USE POLICY: " + filename);
                } else {
                    v.add(mPolicyDir + "/" + filename);
                    System.out.println("USE POLICY: " + mPolicyDir + "/" + filename);
                }
            }
        }

        arr = new String[v.size()];
        v.copyInto(arr);
        
        return arr;
    }
    
    /**
     * sets the current PDP with the given policies
     * @author Stefano
     * @param policyFiles
     */
    private void init(String [] policyFiles){
    	// Create a PolicyFinderModule and initialize it...in this case,
        // we're using the sample FilePolicyModule that is pre-configured
        // with a set of policies from the filesystem
        FilePolicyModule filePolicyModule = new FilePolicyModule();
        for (int i = 0; i < policyFiles.length; i++)
            filePolicyModule.addPolicy(policyFiles[i]);

        // next, setup the PolicyFinder that this PDP will use
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(filePolicyModule);
        policyFinder.setModules(policyModules);

        // now setup attribute finder modules for the current date/time and
        // AttributeSelectors (selectors are optional, but this project does
        // support a basic implementation)
        CurrentEnvModule envAttributeModule = new CurrentEnvModule();
        SelectorModule selectorAttributeModule = new SelectorModule();

        // Setup the AttributeFinder just like we setup the PolicyFinder. Note
        // that unlike with the policy finder, the order matters here. See the
        // the javadocs for more details.
        AttributeFinder attributeFinder = new AttributeFinder();
        List attributeModules = new ArrayList();
        attributeModules.add(envAttributeModule);
        attributeModules.add(selectorAttributeModule);
        attributeFinder.setModules(attributeModules);

        // Try to load the time-in-range function, which is used by several
        // of the examples...see the documentation for this function to
        // understand why it's provided here instead of in the standard
        // code base.
        FunctionFactoryProxy proxy =
            StandardFunctionFactory.getNewFactoryProxy();
        FunctionFactory factory = proxy.getConditionFactory();
//        factory.addFunction(new TimeInRangeFunction());
        FunctionFactory.setDefaultFactory(proxy);

        // finally, initialize sunxacml pdp
        pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
    }

}
