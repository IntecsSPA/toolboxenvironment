/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.communication;

import it.intecs.pisa.communication.messages.StructuredMessage;

/**
 *
 * @author Massimiliano
 */
public interface IMessageReceptionListener {
    public void MessageReceived(StructuredMessage msg);
}
