/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.communication;

import it.intecs.pisa.communication.messages.InitSessionMessage;
import it.intecs.pisa.communication.messages.StructuredMessage;
import it.intecs.pisa.communication.messages.MessageFactory;
import it.intecs.pisa.communication.messages.TerminateMessage;
import it.intecs.pisa.communication.sockets.StructuredServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class MessageServerHandler extends IoHandlerAdapter
 {
    protected IMessageReceptionListener recListener=null;
    protected ArrayBlockingQueue<StructuredMessage> queueToUse=null;
    protected StructuredServerSocket socket=null;
    
    public MessageServerHandler(StructuredServerSocket socket,ArrayBlockingQueue<StructuredMessage> queue, IMessageReceptionListener listener) {
        this.queueToUse=queue;        
        this.recListener=listener;
        this.socket=socket;
    }

    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        session.close();
        socket.close();
        cause.printStackTrace();
    }


    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        Document msg;
        StructuredMessage structMsg=null;
        
        try
        {
        msg=(Document) message;

        structMsg=MessageFactory.toStructuredMessage(msg);
        
        if(structMsg instanceof InitSessionMessage)
             this.socket.setSession(session);
        
        queueToUse.add(structMsg);
        
        if(this.recListener!=null)
            this.recListener.MessageReceived(structMsg);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
