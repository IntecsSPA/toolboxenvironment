/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.util.wsdl.Binding;
import it.intecs.pisa.util.wsdl.BoundedOperation;
import it.intecs.pisa.util.wsdl.Message;
import it.intecs.pisa.util.wsdl.Operation;
import it.intecs.pisa.util.wsdl.Part;
import it.intecs.pisa.util.wsdl.PortTypes;
import it.intecs.pisa.util.wsdl.WSDL;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author massi
 */
public class WSDLBuilder {

    public static WSDL buildFromService(Service service,String serviceURL,String schemaBaseUrl) {
        WSDL wsdl;
        Hashtable<String,String> namespaces;
        wsdl = new WSDL();

        wsdl.setTargetNameSpace("http://www.intecs.it/TOOLBOX/service/" + service.getServiceName() + "/wsdl");
        wsdl.setServiceURL(serviceURL);

        namespaces=wsdl.getNameSpaces();
        namespaces.put("soap", "http://schemas.xmlsoap.org/wsdl/soap/");

        addImports(service,wsdl,schemaBaseUrl);
        addMessages(service, wsdl);
        addPortTypes(service,wsdl);
        addBindings(service,wsdl);
        return wsdl;
    }

    protected static void addMessages(Service service, WSDL wsdl) {
        it.intecs.pisa.common.tbx.Operation[] operations;
        Message[] msgs;
        Part[] parts;
        int messageCount=0;
        int i=0;

        operations = service.getImplementedInterface().getOperations();
        for (it.intecs.pisa.common.tbx.Operation op : operations) {
           if(op.isAsynchronous())
               messageCount+=4;
           else messageCount+=2;
        }

        msgs=new Message[messageCount];
        for (it.intecs.pisa.common.tbx.Operation op : operations) {
           msgs[i]=new Message();
           msgs[i].setName(op.getName()+"Request");

           parts=new Part[1];
           parts[0]=new Part();
           parts[0].setName("body");
           parts[0].setElementNameSpace(getNameSpacePrefix(op.getInputTypeNameSpace(),wsdl));
           parts[0].setElementType(op.getInputType());

           msgs[i].setParts(parts);
           i++;

           msgs[i]=new Message();
           msgs[i].setName(op.getName()+"Response");

           parts=new Part[1];
           parts[0]=new Part();
           parts[0].setName("body");
           parts[0].setElementNameSpace(getNameSpacePrefix(op.getOutputTypeNameSpace(),wsdl));
           parts[0].setElementType(op.getOutputType());

           msgs[i].setParts(parts);
           i++;

           if(op.isAsynchronous())
           {
               msgs[i]=new Message();
               msgs[i].setName(op.getName()+"CallbackRequest");

               parts=new Part[1];
               parts[0]=new Part();
               parts[0].setName("body");
               parts[0].setElementNameSpace(getNameSpacePrefix(op.getCallbackInputTypeNameSpace(),wsdl));
               parts[0].setElementType(op.getCallbackInputType());

               msgs[i].setParts(parts);
               i++;

               msgs[i]=new Message();
               msgs[i].setName(op.getName()+"CallbackResponse");

               parts=new Part[1];
               parts[0]=new Part();
               parts[0].setName("body");
               parts[0].setElementNameSpace(getNameSpacePrefix(op.getCallbackOutputTypeNameSpace(),wsdl));
               parts[0].setElementType(op.getCallbackOutputType());

               msgs[i].setParts(parts);
               i++;
           }
        }

        wsdl.setMessages(msgs);

    }

    protected static String getNameSpacePrefix(String namespace, WSDL wsdl)
    {
        Hashtable<String,String> names;
        Enumeration<String> en;
        String avName=null;
        String key;

        names=wsdl.getNameSpaces();
        en=names.keys();

        while(en.hasMoreElements())
        {
            key=en.nextElement();
            avName=names.get(key);

            if(avName.equals(namespace))
                return key;
        }

        key="ns"+(names.keySet().size()+1);
        names.put(key, namespace);
        
        return key;
    }

    private static void addBindings(Service service, WSDL wsdl) {
        Binding[] bind;
        BoundedOperation[] boundedOperations;
        it.intecs.pisa.common.tbx.Operation[] operations;
        int i=0;

        bind=new Binding[1];
        bind[0]=new Binding();

        bind[0].setName("ToolboxServiceSOAPBinding");
        bind[0].setType("tns:ToolboxSOAPPortForService");

        operations=service.getImplementedInterface().getOperations();
        boundedOperations=new BoundedOperation[operations.length];

        bind[0].setBoundedOperations(boundedOperations);

        for(it.intecs.pisa.common.tbx.Operation op:operations)
        {
            boundedOperations[i]=new BoundedOperation();
            boundedOperations[i].setName(op.getName());
            boundedOperations[i].setSoapAction(op.getSoapAction());
            i++;
        }

        wsdl.setBindings(bind);
        
        //add WS-Security policy to the binding element
        if (service.hasWSSecurity()){
        	bind[0].setWSSPolicy(service.getWSSPolicy());
        }


    }

    private static void addImports(Service service, WSDL wsdl,String schemaBaseUrl) {
        String schemaRoot;
        String namespace;
        String schemaUrl;

        Interface implInterf;

        try
        {
            implInterf=service.getImplementedInterface();

            schemaRoot=implInterf.getSchemaRoot();
            namespace=implInterf.getTargetNameSpace();

            schemaUrl=schemaBaseUrl+"/WSDL/"+service.getServiceName()+"/"+schemaRoot;

            wsdl.addImport(namespace, schemaUrl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void addPortTypes(Service service, WSDL wsdl) {
        it.intecs.pisa.common.tbx.Operation[] operations;
        PortTypes[] ports;
        Operation[] oper;
        int i=0;

        operations = service.getImplementedInterface().getOperations();
        ports=new PortTypes[1];
        ports[0]=new PortTypes();
        ports[0].setName("ToolboxSOAPPortForService");

        oper=new Operation[operations.length];

        for(it.intecs.pisa.common.tbx.Operation op:operations)
        {
            oper[i]=new Operation();
            oper[i].setName(op.getName());
            oper[i].setInputNameType(op.getName()+"Request");
            oper[i].setInputNameNameSpace(op.getInputTypeNameSpace());
            oper[i].setOutputNameNameSpace(op.getOutputTypeNameSpace());
            oper[i].setOutputNameType(op.getName()+"Response");

            i++;
        }

        ports[0].setOperations(oper);

        wsdl.setPortTypes(ports);

    }



}
