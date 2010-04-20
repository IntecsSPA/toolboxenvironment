/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.common.communication.ServerDebugConsole;
import it.intecs.pisa.common.communication.messages.ExecutionCompletedMessage;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.toolbox.util.TimeUtil;
import java.io.File;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class TBXSynchronousOperation extends TBXOperation {

    public TBXSynchronousOperation() {
        super();
    }

    public TBXSynchronousOperation(Operation op) {
        super(op);
    }

    public Document executeRequestScripts(long instanceId, Document soapRequest, boolean debugMode) throws Exception {
        Document request;
        Toolbox tbxServlet = null;
        String instanceKey;
        String orderId;
        Interface interf;
        TBXService parentService;
        long debugInstanceKey = -1;
        TBXScript tbxScript;
        Element script;
        String scriptPrefix;
        final Element cleanupProcedure;

        try {
            tbxServlet = Toolbox.getInstance();
            parentService = getParentService();

            request = Util.removeSOAPElements(soapRequest);
            orderId = parentService.getOrderId(request);

            interf = (Interface) parentService.getImplementedInterface().clone();

            debugInstanceKey = Toolbox.getInstance().getInstanceKeyUnderDebug();
            debugMode = debugInstanceKey==instanceId;
            

            tbxScript = (TBXScript) getScript(Script.SCRIPT_TYPE_FIRST_SCRIPT);
            script = tbxScript.getScriptDoc().getDocumentElement();

            if (DOMUtil.hasElement(script, (scriptPrefix = script.getPrefix()) == null ? TBXService.CLEANUP_PROCEDURE : scriptPrefix + ":" + TBXService.CLEANUP_PROCEDURE)) {
                cleanupProcedure = (Element) (script = ((Element) script.cloneNode(true))).removeChild(DOMUtil.getChildByLocalName(script, TBXService.CLEANUP_PROCEDURE));
            } else {
                cleanupProcedure = null;
            }

            try {
                InstanceHandler handler;
                handler = new InstanceHandler(instanceId);

                return (Document) handler.executeScript(Script.SCRIPT_TYPE_FIRST_SCRIPT, debugMode);

            } catch (Exception e) {
                throw new Exception("Error while executing synchronous instance", e);
            }

        } finally {
            if (debugMode) {
                ServerDebugConsole console;

                console = tbxServlet.getDbgConsole();

                console.sendCommand(new ExecutionCompletedMessage());

                console.close();
                tbxServlet.signalDebugConsoleClosed();
            }
        }
    }

    @Override
    public void start() {
        logger.info("Synchronous operation " + this.name + " started");
    }

    @Override
    public void stop() {
        logger.info("Synchronous operation " + this.name + " stopped");
    }

    protected void sendErrorMail(String orderId, String errorMsg, File requestLogDir) {

        if (Toolbox.getErrorMailer() != null && Toolbox.getMailError() != null) {
            HashMap contentParts = new HashMap();
            contentParts.put("serviceName", getServiceName());
            contentParts.put("soapAction", soapAction);
            contentParts.put("orderId", orderId);

            Toolbox.getErrorMailer().sendMail(contentParts, errorMsg, requestLogDir, Toolbox.getMailError());
        }
    }

    @Override
    protected void storeInstanceKeys(long seviceInstanceId, Document soapRequest, boolean debugEnabled) throws Exception {
        TBXService parentService;
        String instanceKey;
        String orderId;
        Document request;

        instanceKey = "tbx" + TimeUtil.getDateTime("", "", "");
        
        parentService=(TBXService) parentInterf.getParent();
        
        request = Util.removeSOAPElements(soapRequest);
        orderId = parentService.getOrderId(request);
        if (orderId == null || orderId.equals("") == true) {
            orderId = instanceKey;
        }

        updateInstanceInformation(seviceInstanceId, orderId, instanceKey, "");
    }
}
