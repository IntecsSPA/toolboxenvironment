package it.intecs.pisa.communication.sockets;

import it.intecs.pisa.communication.ClientProtcolHandler;
import it.intecs.pisa.communication.IMessageReceptionListener;
import it.intecs.pisa.communication.messages.StructuredMessage;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * This class is used to implement a client that communicate with a StructuredServerSocket.
 * It is used by the class StructuredCommunicationSocket.
 * @author Massimiliano
 *
 */
public class StructuredClientSocket {
    private NioSocketConnector connector = null;
    private InetSocketAddress address = null;
    private ConnectFuture connectFuture = null;
    protected ArrayBlockingQueue<StructuredMessage> receivedMessages = null;
    protected IMessageReceptionListener listener=null;
    
    public StructuredClientSocket()
    {
        
    }
    
    public StructuredClientSocket(IMessageReceptionListener listener)
    {
        this.listener=listener;
    }
    
    public boolean connect(String host, int port) {
        try {
            receivedMessages = new ArrayBlockingQueue<StructuredMessage>(1024, true);
            
            address = new InetSocketAddress(host, port);
            connector = new NioSocketConnector();
            connector.setConnectTimeoutMillis(30 * 1000L);

            connector.setHandler(new ClientProtcolHandler(receivedMessages,this.listener));

            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
            connectFuture = connector.connect(address);
            connectFuture.awaitUninterruptibly();

            
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }

    }

    /**
     * This method is used to send a StructuredMessage to the server side. This method
     * returns the received acknowledge mesage
     * @param host Server host address
     * @param port Server port
     * @param msg Message to send
     * @return Acknowledge message.
     */
    public boolean sendMessage(StructuredMessage msg) {
        IoSession session = null;

        try {
            session = connectFuture.getSession();
            session.write(msg.getDoc());
            return true;
        } catch (Exception e) {
        		e.printStackTrace();
              return false;
        }
    }
    
     public StructuredMessage getReceivedMessage() {
       try {
            return this.receivedMessages.poll(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }
    
    public void close()
    {
         IoSession session = null;
         
       try
       {
    	   if(connectFuture!=null)
    	   {
             session=connectFuture.getSession();
             if(session != null)
               session.close();
             
             if(connector!=null)
            	 connector.dispose();
    	   }
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        close();
    }
    
    public boolean isConnected()
    {
       return connectFuture!=null &&  connectFuture.isConnected();
    }
}
