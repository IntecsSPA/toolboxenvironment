/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.communication.messages;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class MessageFactory {

    public static final String[][] messageClasses = {{"initdebug", "it.intecs.pisa.common.communication.messages.InitDebugMessage"},
        {"error", "it.intecs.pisa.common.communication.messages.ErrorMessage"},
        {"initsession", "it.intecs.pisa.common.communication.messages.InitSessionMessage"},
        {"initbreakpoints", "it.intecs.pisa.common.communication.messages.InitBreakpointsMessage"},
        {"terminate", "it.intecs.pisa.common.communication.messages.TerminateMessage"},
        {"breakpointhit", "it.intecs.pisa.common.communication.messages.BreakpointHitMessage"},
        {"executioncompleted", "it.intecs.pisa.common.communication.messages.ExecutionCompletedMessage"},
        {"executionstarted", "it.intecs.pisa.common.communication.messages.ExecutionStartedMessage"},
        {"getvariables", "it.intecs.pisa.common.communication.messages.GetVariablesMessage"},
        {"variableslist", "it.intecs.pisa.common.communication.messages.VariablesListMessage"},
        {"executionresumed", "it.intecs.pisa.common.communication.messages.ExecutionResumedMessage"},
        {"variableDescription", "it.intecs.pisa.common.communication.messages.VariableDescriptionMessage"},
        {"describeVariable", "it.intecs.pisa.common.communication.messages.DescribeVariableMessage"},
        {"setVariableValue", "it.intecs.pisa.common.communication.messages.SetVariableValueMessage"},
        {"booleanValue", "it.intecs.pisa.common.communication.messages.BooleanValueMessage"},
        {"canStepInto", "it.intecs.pisa.common.communication.messages.CanStepIntoMessage"},
        {"canStepOver", "it.intecs.pisa.common.communication.messages.CanStepOverMessage"},
        {"canStepReturn", "it.intecs.pisa.common.communication.messages.CanStepReturnMessage"},
        {"stepXPath", "it.intecs.pisa.common.communication.messages.StepXPathMessage"},
        {"runUntilXPath", "it.intecs.pisa.common.communication.messages.RunUntilXPathMessage"},
        {"setBreakpoint", "it.intecs.pisa.common.communication.messages.SetBreakpointMessage"},
        {"removeBreakpoint", "it.intecs.pisa.common.communication.messages.RemoveBreakpointMessage"},
        {"stepInto", "it.intecs.pisa.common.communication.messages.StepIntoMessage"},
        {"stepOver", "it.intecs.pisa.common.communication.messages.StepOverMessage"},
        {"stepReturn", "it.intecs.pisa.common.communication.messages.StepReturnMessage"},
        {"getExecutionTree", "it.intecs.pisa.common.communication.messages.GetExecutionTreeMessage"},
        {"executionTree", "it.intecs.pisa.common.communication.messages.ExecutionTreeMessage"}
    };

    public static StructuredMessage toStructuredMessage(Document msg) {
        String rootTagName;
        Element rootEl;
        StructuredMessage message;

        try {
            rootEl = msg.getDocumentElement();
            rootTagName = rootEl.getTagName();

            for (String[] msgClass : messageClasses) {
                if (rootTagName.equals(msgClass[0])) {
                    message = (StructuredMessage) Class.forName(msgClass[1]).newInstance();
                    message.initFromDocument(msg);
                    System.out.println("MSG "+message.getClass().getCanonicalName());
                    return message;
                }
            }

            return null;
        //  message.initFromXMLMessage(msg);

        } catch (Exception ex) {
            Logger.getLogger(MessageFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
