/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.communication;

import it.intecs.pisa.common.communication.messages.StructuredMessage;

/**
 *
 * @author Massimiliano
 */
public interface IMessageReceptionListener {
    public void MessageReceived(StructuredMessage msg);
}
