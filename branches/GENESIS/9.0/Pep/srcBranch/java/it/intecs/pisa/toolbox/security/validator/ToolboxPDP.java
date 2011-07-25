package it.intecs.pisa.toolbox.security.validator;

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
import com.sun.xacml.finder.PolicyFinderModule;

import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.support.finder.FilePolicyModule;
import com.sun.xacml.finder.impl.SelectorModule;
import it.intecs.pisa.toolbox.Toolbox;

import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.eig.geoxacml.pdp.*;
import net.eig.geoxacml.pdp.builder.GeoBuilderFactory;
import net.eig.geoxacml.pdp.builder.IGeoBuilder;
import net.eig.geoxacml.pdp.builder.IGeoBuilderFactory;
import net.eig.geoxacml.pdp.finder.GeoFilePolicyModule;

public class ToolboxPDP {

    // this is the actual PDP from sunxacml we'll use for policy evaluation
    //private PDP pdp = null;
    private IGeoPDP pdp = null;
    private final IGeoPDPFactory pdpFactory;

    /**
     * Default constructor.
     */
    public ToolboxPDP() throws Exception {
        // load the configuration
        //ConfigurationStore store = new ConfigurationStore();

        // use the default factories from the configuration
        //store.useDefaultFactories();

        // get the PDP configuration's and setup the PDP
        //pdp = new PDP(store.getDefaultPDPConfig());     

        this.pdpFactory = new GeoPDPFactory();

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
            throws IOException, ParsingException {
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
            throws IOException, ParsingException, Exception {
        
        //load policy and init
        String service = ((Attribute) request.getResource().iterator().next()).getValue().encode();
        String[] policy = getPolicyFiles(service.toString().substring(service.toString().lastIndexOf("/") + 1));
        //init(policy);
        setup(policy);
        // evaluate the request
        return pdp.evaluate(request);
    }

    /**
     * This method lets you invokethe PDP directly from the command-line.
     *
     * @param args the input arguments to the class. They are either the
     *             flag "-config" followed by a request file, or a request
     *             file followed by one or more policy files. In the case
     *             that the configuration flag is used, the configuration
     *             file must be specified in the standard java property,
     *             com.sun.xacml.PDPConfigFile.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: -config <request>");
            System.out.println("       <request> <policy> [policies]");
            System.exit(1);
        }

        ToolboxPDP simplePDP = null;
        String requestFile = null;

        if (args[0].equals("-config")) {
            requestFile = args[1];
            simplePDP = new ToolboxPDP();
        } else {
            requestFile = args[0];
            String[] policyFiles = new String[args.length - 1];

            for (int i = 1; i < args.length; i++) {
                policyFiles[i - 1] = args[i];
            }

            simplePDP = new ToolboxPDP();

        }

        // evaluate the request
        ResponseCtx response = simplePDP.evaluate(requestFile);

        // for this sample program, we'll just print out the response
        response.encode(System.out, new Indenter());
    }

    /**
     * 
     * @return Returns the list of the XACML policy files that need to be checked by the PDP
     */
    public String[] getPolicyFiles(String serviceName) {
        String[] arr = null;

        Vector v = new Vector();

        String policiesDir = ToolboxSecurityConfigurator.getXACMLpolicyDir(serviceName);

        File dir = new File(policiesDir);

        String[] children = null;

        // It is also possible to filter the list of returned files.
        // This example does not return any files that start with `.'.
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        children = dir.list(filter);


        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                v.add(policiesDir + "/" + filename);
            }
        }

        arr = new String[v.size()];
        v.copyInto(arr);

        return arr;
    }

    protected void setup(String[] policies) throws Exception {

        this.pdp = this.pdpFactory.createPDP();

        final List<PolicyFinderModule> modules = new ArrayList<PolicyFinderModule>(1);
        final GeoFilePolicyModule finderModule = new GeoFilePolicyModule();

        for (final String policy : policies) {
            finderModule.addPolicy(policy);
        }

        modules.add(finderModule);

        final Field field = this.pdp.getClass().getDeclaredField("policyFinderModules");
        field.setAccessible(true);
        field.set(this.pdp, modules);


        IGeoBuilderFactory factory = new GeoBuilderFactory();
        IGeoBuilder builder = factory.createGeoBuilder();

        Toolbox tbx = Toolbox.getInstance();

        String propLogFile = tbx.getRootDir() + "/WEB-INF/classes/log4j.properties";

        final Field propField = builder.getClass().getDeclaredField("loggerPropertiesFile");
        propField.setAccessible(true);
        propField.set(builder, propLogFile);


        final Field geoField = this.pdp.getClass().getDeclaredField("geoBuilder");
        geoField.setAccessible(true);
        geoField.set(this.pdp, builder);

        this.pdp.setup();

    }
}
