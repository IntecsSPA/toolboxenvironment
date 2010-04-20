/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.communication.messages;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class MessageFactory {

    public static final String[][] messageClasses = {{"initdebug", "it.intecs.pisa.communication.messages.InitDebugMessage"},
        {"error", "it.intecs.pisa.communication.messages.ErrorMessage"},
        {"initsession", "it.intecs.pisa.communication.messages.InitSessionMessage"},
        {"initbreakpoints", "it.intecs.pisa.communication.messages.InitBreakpointsMessage"},
        {"terminate", "it.intecs.pisa.communication.messages.TerminateMessage"},
        {"breakpointhit", "it.intecs.pisa.communication.messages.BreakpointHitMessage"},
        {"executioncompleted", "it.intecs.pisa.communication.messages.ExecutionCompletedMessage"},
        {"executionstarted", "it.intecs.pisa.communication.messages.ExecutionStartedMessage"},
        {"getvariables", "it.intecs.pisa.communication.messages.GetVariablesMessage"},
        {"variableslist", "it.intecs.pisa.communication.messages.VariablesListMessage"},
        {"executionresumed", "it.intecs.pisa.communication.messages.ExecutionResumedMessage"},
        {"variableDescription", "it.intecs.pisa.communication.messages.VariableDescriptionMessage"},
        {"describeVariable", "it.intecs.pisa.communication.messages.DescribeVariableMessage"},
        {"setVariableValue", "it.intecs.pisa.communication.messages.SetVariableValueMessage"},
        {"booleanValue", "it.intecs.pisa.communication.messages.BooleanValueMessage"},
        {"canStepInto", "it.intecs.pisa.communication.messages.CanStepIntoMessage"},
        {"canStepOver", "it.intecs.pisa.communication.messages.CanStepOverMessage"},
        {"canStepReturn", "it.intecs.pisa.communication.messages.CanStepReturnMessage"},
        {"stepXPath", "it.intecs.pisa.communication.messages.StepXPathMessage"},
        {"runUntilXPath", "it.intecs.pisa.communication.messages.RunUntilXPathMessage"},
        {"setBreakpoint", "it.intecs.pisa.communication.messages.SetBreakpointMessage"},
        {"removeBreakpoint", "it.intecs.pisa.communication.messages.RemoveBreakpointMessage"},
        {"stepInto", "it.intecs.pisa.communication.messages.StepIntoMessage"},
        {"stepOver", "it.intecs.pisa.communication.messages.StepOverMessage"},
        {"stepReturn", "it.intecs.pisa.communication.messages.StepReturnMessage"},
        {"getExecutionTree", "it.intecs.pisa.communication.messages.GetExecutionTreeMessage"},
        {"executionTree", "it.intecs.pisa.communication.messages.ExecutionTreeMessage"}
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
