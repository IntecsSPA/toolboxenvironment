/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.communication;

import it.intecs.pisa.communication.messages.InitSessionMessage;
import it.intecs.pisa.communication.messages.MessageFactory;
import it.intecs.pisa.communication.messages.StructuredMessage;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.w3c.dom.Document;

/**
 * 
 * @author Massimiliano
 */
public class ClientProtcolHandler extends IoHandlerAdapter {

	protected IMessageReceptionListener recListener = null;

	protected ArrayBlockingQueue<StructuredMessage> queueToUse;

	public ClientProtcolHandler(ArrayBlockingQueue<StructuredMessage> queue,
			IMessageReceptionListener listener) {
		this.queueToUse = queue;
		this.recListener = listener;
	}

	@Override
	public void sessionOpened(IoSession session) {

	}

	@Override
	public void sessionClosed(IoSession session) {
		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		
	}

	@Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	Document msg;
        StructuredMessage structMsg=null;
        
        try
        {
        msg=(Document) message;

        structMsg=MessageFactory.toStructuredMessage(msg);
        
        queueToUse.add((StructuredMessage) structMsg);
        
        if(this.recListener!=null)
            this.recListener.MessageReceived( structMsg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }

    }
}

