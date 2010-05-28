package it.intecs.pisa.toolbox.plugins;

import it.intecs.pisa.communication.ServerDebugConsole;
import it.intecs.pisa.communication.messages.BooleanValueMessage;
import it.intecs.pisa.communication.messages.BreakpointHitMessage;
import it.intecs.pisa.communication.messages.CanStepIntoMessage;
import it.intecs.pisa.communication.messages.CanStepOverMessage;
import it.intecs.pisa.communication.messages.CanStepReturnMessage;
import it.intecs.pisa.communication.messages.DescribeVariableMessage;
import it.intecs.pisa.communication.messages.ExecutionResumedMessage;
import it.intecs.pisa.communication.messages.ExecutionTreeMessage;
import it.intecs.pisa.communication.messages.GetExecutionTreeMessage;
import it.intecs.pisa.communication.messages.GetVariablesMessage;
import it.intecs.pisa.communication.messages.RemoveBreakpointMessage;
import it.intecs.pisa.communication.messages.SetBreakpointMessage;
import it.intecs.pisa.communication.messages.SetVariableValueMessage;
import it.intecs.pisa.communication.messages.StepIntoMessage;
import it.intecs.pisa.communication.messages.StepOverMessage;
import it.intecs.pisa.communication.messages.StepReturnMessage;
import it.intecs.pisa.communication.messages.StructuredMessage;
import it.intecs.pisa.communication.messages.TerminateMessage;
import it.intecs.pisa.communication.messages.VariableDescriptionMessage;
import it.intecs.pisa.communication.messages.VariablesListMessage;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.plugins.exceptions.DebugTerminatedException;
import it.intecs.pisa.toolbox.plugins.exceptions.ReturnTagException;
import it.intecs.pisa.toolbox.plugins.exceptions.TagException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Level;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @version 1.0
 * @created 11-apr-2008 14.49.06
 */
public class TagExecutor implements ITagExecutor {

    protected String tagName;
    protected TagPluginManager manager = null;
    protected Element offlineDbgTag = null;
    protected IEngine engine = null;
    protected ITagPlugin plugIn = null;
    protected boolean debugMode = false;
    protected Element currentNode = null;
    protected Level logLevel=null;
    protected static final String CONFIGURATION_INSTANCE_DEBUG_CONSOLE = "CONFIGURATION_INSTANCE_DEBUG_CONSOLE";
    protected static final String CONFIGURATION_SCRIPT_IN_EXECUTION = "CONFIGURATION_SCRIPT_IN_EXECUTION";
    protected static final String CONFIGURATION_IS_STEPPING = "CONFIGURATION_IS_STEPPING";
    protected static final String CONFIGURATION_STOP_AT_WHEN_STEPPING = "CONFIGURATION_STOP_AT_WHEN_STEPPING";
    protected static final String CONFIGURATION_INSTANCE_LOG_LEVEL = "CONFIGURATION_INSTANCE_LOG_LEVEL";
     protected static final String INSTANCE_DIRECTORY = "INSTANCE_DIRECTORY";
    protected static final Object NULL_MARKER = new Object();

    public TagExecutor() {
    }

    /**
     * This method implements part of the ITagExecutor. It shall be overridden if you need to perform a special task with the debug tree, 
     * otherwise you shall override the executeTag(IEngine engine, org.w3c.dom.Element tagEl)
     * @param engine
     * @param tagEl
     * @param debugTag
     * @return
     */
    public Object executeTag(org.w3c.dom.Element tagEl, org.w3c.dom.Element debugTag, boolean debugMode) throws Exception {
        // Add here code for setting debug informations
        Object returnValue = null;
        Element tag = null;
        Element excTag = null;
        Element excDetailsTag = null;
        Document doc = null;

        logLevel=(Level) this.engine.getConfigurationVariablesStore().getVariable(CONFIGURATION_INSTANCE_LOG_LEVEL);
                
        if (debugTag != null) {
            offlineDbgTag = debugTag.getOwnerDocument().createElement(tagEl.getNodeName());
            addAttributes(tagEl, offlineDbgTag);
            debugTag.appendChild(offlineDbgTag);
        }
        this.debugMode = debugMode;
        
        if (debugMode) {
            currentNode = tagEl;
            checkForBreakpoints(DOMUtil.getXPathNS(tagEl, tagEl.getOwnerDocument()));
        }
        returnValue = executeTagTrapped(tagEl, debugTag, excTag, doc, excDetailsTag);

        if (offlineDbgTag != null && returnValue != null) {
            offlineDbgTag.setAttribute("value", returnValue.toString());
            offlineDbgTag.setAttribute("valueType", returnValue.getClass().getCanonicalName());
        }

        return returnValue;
    }

    /**
     * 
     * @param engine
     * @param tagEl
     * @return
     */
    public Object executeTag(org.w3c.dom.Element tagEl) throws Exception {
        System.out.println("Executing tag " + this.getClass().getCanonicalName());

        return null;
    }

    /**
     * This method is used to execute a child tag. it performs a call to the engine.
     * @param engine Reference to the engine that is currently running the script
     * @param element Tag that shall be executed
     * @param element debugTag Parent tag under which excution result shall be appended
     * @return The execution return
     */
    protected Object executeChildTag(org.w3c.dom.Element element, org.w3c.dom.Element debugTag) throws Exception {
        ITagPlugin plugin = null;
        ITagExecutor executor = null;
        String tagNamespace = null;
        String tagName = null;

        if (manager == null) {
            manager = TagPluginManager.getInstance();
        }
        tagNamespace = element.getNamespaceURI();
        tagName = element.getNodeName();

        plugin = manager.getTagPlugin(tagNamespace);
        executor = plugin.getTagExecutorClass(tagName, this.engine);

        return executor.executeTag(element, debugTag, debugMode);
    }

    protected Object executeChildTag(org.w3c.dom.Element element) throws Exception {
        return executeChildTag(element, offlineDbgTag);
    }

    protected Object executeTagTrapped(Element tagEl, Element debugTag, Element excTag, Document doc, Element excDetailsTag) throws DOMException, ReturnTagException, TagException, DebugTerminatedException {
        Object returnValue;

        try {
            returnValue = executeTag(tagEl);
        } catch (TagException tagException) {
            throw tagException;
        } catch (ReturnTagException retTagException) {
            throw retTagException;
        } catch (DebugTerminatedException dbgTerminatedException) {
            throw dbgTerminatedException;
        } catch (Exception exEcc) {
            if (offlineDbgTag != null) {
                excTag = offlineDbgTag;
            } else {
                excTag = debugTag;
            }
            if (excTag != null) {
                doc = excTag.getOwnerDocument();
                excDetailsTag = doc.createElement("ThrownExceptionDetails");
                DOMUtil.setTextToElement(doc, excDetailsTag, exEcc.toString());

                excTag.appendChild(excDetailsTag);
            }

            throw new TagException(this.getClass());
        }
        catch(Throwable t)
        {
            if (offlineDbgTag != null) {
                excTag = offlineDbgTag;
            } else {
                excTag = debugTag;
            }
            if (excTag != null) {
                doc = excTag.getOwnerDocument();
                excDetailsTag = doc.createElement("ThrownExceptionDetails");
                DOMUtil.setTextToElement(doc, excDetailsTag, t.toString());

                excTag.appendChild(excDetailsTag);
            }

            throw new TagException(this.getClass());
        }

        return returnValue;
    }

    public void setEngine(IEngine engine) {
        this.engine = engine;
    }

    protected void addAttributes(Element tagEl, Element tag) {
        NamedNodeMap attributesMap = null;
        int count = 0;
        Node ithNode = null;
        Attr attr = null;


        attributesMap = tagEl.getAttributes();
        count = attributesMap.getLength();

        for (int i = 0; i < count; i++) {
            ithNode = attributesMap.item(i);
            if (ithNode instanceof Attr) {
                attr = (Attr) ithNode;
                tag.setAttribute(attr.getName(), engine.evaluateString(attr.getValue(), IEngine.EngineStringType.ATTRIBUTE));
            }

        }
    }

    public void setPlugIn(ITagPlugin plugIn) {
        this.plugIn = plugIn;
    }

    protected Element getNextElement(Element el) {
        Node tmp = el;
        do {
            tmp = tmp.getNextSibling();
        } while ((tmp != null) && (tmp instanceof Element == false));
        
        return (Element) tmp;
    }

    protected Element getStepIntoTag(Element el) {
        return  DOMUtil.getFirstChild(el);
    }

    protected Element getStepOverTag(Element el) {
            return getNextElement(el);
    }
    
    protected Element getStepReturnTag(Element el) {
        Element parentEl=null;
        Element nextEl=null;
        
        parentEl = (Element) el.getParentNode();
        nextEl=getNextElement(parentEl);   
        
        return nextEl;
    }

    protected void setSteppingInfo( Element stopAtEl) {
        IVariableStore store=null;
        
        store = this.engine.getConfigurationVariablesStore();
        store.setVariable(CONFIGURATION_IS_STEPPING, Boolean.TRUE);
        store.setVariable(CONFIGURATION_STOP_AT_WHEN_STEPPING, stopAtEl);
    }

    private void AddBreakpoint(SetBreakpointMessage respMsg, ServerDebugConsole console) {
       console.addBreakpoint(respMsg.getFile(),respMsg.getXPath(),"");
    }

    private void CheckStepInto(ServerDebugConsole console) {
       Element stepEl=null;
        boolean canStep=false;
        
        try {
            stepEl = getStepIntoTag(currentNode);
             canStep=stepEl!=null;
        } catch (Exception e) {
            
            canStep=false;
        }

        console.sendCommand(new BooleanValueMessage(canStep));
    }

    private void CheckStepOver(ServerDebugConsole console) {
            Element stepEl=null;
          boolean canStep=false;
          
        try {
            stepEl = getStepOverTag(currentNode);
            canStep=stepEl!=null;
        } catch (Exception e) {
            canStep=false;
        }

        console.sendCommand(new BooleanValueMessage(canStep));
    }

    private void CheckStepReturn(ServerDebugConsole console) {
        Element stepEl = null;
        boolean canStep=false;
        
        try {
           stepEl=getStepReturnTag(currentNode);
        canStep=stepEl!=null;
         
        } catch (Exception e) {
            canStep=false;
        }

        console.sendCommand(new BooleanValueMessage(canStep));
    }

    private void RemoveBreakpoint(RemoveBreakpointMessage respMsg, ServerDebugConsole console) {
        console.removeBreakpoint(respMsg.getFile(),respMsg.getXPath() );
    }

    private void SetVariableToVault(SetVariableValueMessage msg, ServerDebugConsole console) {
        IVariableStore varStore;
        Object var;
        String key;
        String type;
        String valueStr;
        DOMUtil util;
        ClassLoader loader;

        varStore = this.engine.getVariablesStore();

        key = msg.getName();
        type = msg.getType();

        if (type.equals("org.w3c.dom.Document")) {
            var = (org.w3c.dom.Document) msg.getValue();
        } else {
            valueStr = (String) msg.getValue();
            if (type.equals("java.lang.Short")) {
                var = new Short(valueStr);
            } else if (type.equals("java.lang.Long")) {
                var = new Long(valueStr);
            } else if (type.equals("java.lang.Integer")) {
                var = new Integer(valueStr);
            } else if (type.equals("java.lang.Float")) {
                var = new Float(valueStr);
            } else if (type.equals("java.lang.Double")) {
                var = new Double(valueStr);
            } else if (type.equals("java.lang.Character")) {
                var = new Character(valueStr.charAt(0));
            } else if (type.equals("java.lang.Byte")) {
                var = new Byte(valueStr);
            } else if (type.equals("java.lang.Boolean")) {
                var = new Boolean(valueStr);
            } else if (type.equals("java.lang.String")) {
                var = new String(valueStr);
            } else {
                var = null;
            }
        }

        if (var != null) {
            varStore.setVariable(key, var);
        }
    }

    private void StepInto(ServerDebugConsole console) {
      Element stopAtEl=null;
            
      stopAtEl=getStepIntoTag(currentNode);
      setSteppingInfo(stopAtEl);
    }

    private void StepOver(ServerDebugConsole console) {
        Element stopAtEl=null;
      
      stopAtEl=getStepOverTag(currentNode);
      setSteppingInfo(stopAtEl);
    }

    private void StepReturn(ServerDebugConsole console) {
        Element stopAtEl=null;
      
      stopAtEl=getStepReturnTag(currentNode);
      setSteppingInfo(stopAtEl);
    }

    private void checkForBreakpoints(String tagXPath) throws Exception {
        ServerDebugConsole console;
        IVariableStore confVar;
        String[][] breakpoints;
        String currentScriptFile;
        boolean breakpointHit = false;
        Boolean stepping;
        BreakpointHitMessage brkptMsg;
        StructuredMessage respMsg;
        Element steppingEl=null;
        int lineNumber = 0;


        confVar = this.engine.getConfigurationVariablesStore();
        IVariableStore varStore = this.engine.getVariablesStore();

        console = (ServerDebugConsole) confVar.getVariable(CONFIGURATION_INSTANCE_DEBUG_CONSOLE);

        if (console == null || console.isTerminated() == true) {
            throw new DebugTerminatedException();
        }
        currentScriptFile = (String) confVar.getVariable(CONFIGURATION_SCRIPT_IN_EXECUTION);

        breakpoints = console.getBreakpoints();

        for (String[] breakpoint : breakpoints) {
            if (breakpoint[0].endsWith(currentScriptFile) &&
                    breakpoint[1].equals(tagXPath)) {
                lineNumber = Integer.parseInt(breakpoint[2]);
                breakpointHit = true;
                break;
            }
        }

        stepping=(Boolean) confVar.getVariable(CONFIGURATION_IS_STEPPING);
        if(stepping!=null && stepping.booleanValue())
        {
            steppingEl=(Element) confVar.getVariable(CONFIGURATION_STOP_AT_WHEN_STEPPING);
            breakpointHit=breakpointHit||(steppingEl==currentNode);
            System.out.println("STEPPING HIT: "+DOMUtil.getXPathNS(currentNode, currentNode.getOwnerDocument()));
        }
        
        if (breakpointHit) {
            String serviceName;

            serviceName=(String) varStore.getVariable("serviceName");
            if(currentScriptFile.startsWith("/"+serviceName)==false)
            {
                currentScriptFile="/"+serviceName+"/"+currentScriptFile;
                currentScriptFile.replaceAll("//", "/");
            }
            System.out.println("BREAKPOINT HIT: " + tagXPath + " in file " + currentScriptFile);

            brkptMsg = new BreakpointHitMessage();
            brkptMsg.setBreakFilePath(currentScriptFile);
            brkptMsg.setBreakXpath(tagXPath);
            brkptMsg.setLineNumber(lineNumber);

            console.sendCommand(brkptMsg);
            do {
                respMsg = console.readCommand();
            } while (handleMessageReceivedAfterBreakpointStop(respMsg, console) == false);

        }

    }

    private boolean handleMessageReceivedAfterBreakpointStop(StructuredMessage respMsg, ServerDebugConsole console) throws Exception {
        if (respMsg == null) {
            throw new Exception("Cannot read message from TDE");
        } else if (respMsg instanceof ExecutionResumedMessage) {
            return true;
        } else if (respMsg instanceof GetVariablesMessage) {
            sendVariableList(console);

            return false;
        } else if (respMsg instanceof TerminateMessage) {
            throw new DebugTerminatedException();
        } else if (respMsg instanceof DescribeVariableMessage) {
            sendVariableDescription((DescribeVariableMessage) respMsg, console);
            return false;
        } else if (respMsg instanceof SetVariableValueMessage) {
            SetVariableToVault((SetVariableValueMessage) respMsg, console);
            return false;
        } else if (respMsg instanceof CanStepIntoMessage) {
            CheckStepInto(console);
            return false;
        } else if (respMsg instanceof CanStepOverMessage) {
            CheckStepOver(console);
            return false;
        } else if (respMsg instanceof CanStepReturnMessage) {
            CheckStepReturn(console);
            return false;
        }else if (respMsg instanceof SetBreakpointMessage) {
            AddBreakpoint((SetBreakpointMessage) respMsg,console);
            return false;
        }else if (respMsg instanceof RemoveBreakpointMessage) {
            RemoveBreakpoint((RemoveBreakpointMessage) respMsg,console);
            return false;
        }else if (respMsg instanceof StepIntoMessage) {
            StepInto(console);
            return true;
        } else if (respMsg instanceof StepOverMessage) {
            StepOver(console);
            return true;
        } else if (respMsg instanceof StepReturnMessage) {
            StepReturn(console);
            return true;
        }else if (respMsg instanceof GetExecutionTreeMessage) {
             ExecutionTreeMessage treeMessage;
             
            treeMessage=new ExecutionTreeMessage("",this.offlineDbgTag.getOwnerDocument());
            console.sendCommand(treeMessage);
            return false;
        }

        return true;
    }

    private void sendVariableDescription(DescribeVariableMessage msg, ServerDebugConsole console) {
        VariableDescriptionMessage descrMsg;
        IVariableStore varStore;
        Hashtable variables;
        Object var;
        String name;

        varStore = this.engine.getVariablesStore();
        variables = varStore.getVariables();

        name = msg.getName();
        var = variables.get(name);

        if (var instanceof org.w3c.dom.Document) {
            descrMsg = new VariableDescriptionMessage(name, (Document) var);
        } else {
            descrMsg = new VariableDescriptionMessage(name, var.getClass().getName(), var.toString());
        }
        console.sendCommand(descrMsg);
    }

    private void sendVariableList(ServerDebugConsole console) {
        VariablesListMessage msg;
        IVariableStore varStore;
        Hashtable variables;
        String[] variablesString;
        Enumeration keyEnum;

        varStore = this.engine.getVariablesStore();
        variables = varStore.getVariables();
        variablesString = new String[variables.size()];

        keyEnum = variables.keys();
        for (int i = 0; i < variables.size(); i++) {
            variablesString[i] = (String) keyEnum.nextElement();
        }
        msg = new VariablesListMessage(variablesString);

        console.sendCommand(msg);
    }
    
      protected void addScriptLinkToDebugTree(long id) {
        if (this.logLevel.equals(Level.DEBUG) && offlineDbgTag!=null) {
            this.offlineDbgTag.setAttribute("resourceKey", Long.toString(id));
        }
    }

       protected void addResourceLinkToDebugTree(File file) {
        if (this.logLevel.equals(Level.DEBUG)&& offlineDbgTag!=null) {
            this.offlineDbgTag.setAttribute("resourceLink",file.toURI().toString());
        }
    }



    protected void dumpResourceAndAddToDebugTree(Document xml) throws Exception {
        File resourceFile;
        String datetime;
        SimpleDateFormat formatter;
        File dir;

        if (this.logLevel.isGreaterOrEqual(Level.DEBUG)) {
            dir=new File((String)this.engine.getConfigurationVariablesStore().getVariable(INSTANCE_DIRECTORY));       
            formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            resourceFile = new File(dir, tagName + "_" + formatter.format(new Date())+".xml");

            DOMUtil.dumpXML(xml, resourceFile);
            
            this.offlineDbgTag.setAttribute("resourceLink", resourceFile.toURI().toString());
        }
    }

    protected void dumpResourceAndAddToDebugTree(String text) throws FileNotFoundException {
         File resourceFile;
        String datetime;
        SimpleDateFormat formatter;
        PrintWriter printer;
        File dir;
        
        if (this.logLevel.isGreaterOrEqual(Level.DEBUG)) {
            dir=new File((String)this.engine.getConfigurationVariablesStore().getVariable(INSTANCE_DIRECTORY)) ; 
            formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            resourceFile = new File(dir, tagName + "_" + formatter.format(new Date())+".txt");

            printer=new PrintWriter(new FileOutputStream(resourceFile));
            printer.print(text);
            printer.close();
            
            this.offlineDbgTag.setAttribute("resourceLink", resourceFile.toURI().toString());
        }
    }

    protected String evaluateAttribute(Element node,String attribute)
    {
        return this.engine.evaluateString(node.getAttribute(attribute), IEngine.EngineStringType.ATTRIBUTE);
    }
}