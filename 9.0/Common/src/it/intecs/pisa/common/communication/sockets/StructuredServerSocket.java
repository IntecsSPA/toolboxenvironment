/**
 * 
 */
package it.intecs.pisa.common.communication.sockets;

import it.intecs.pisa.common.communication.IMessageReceptionListener;
import it.intecs.pisa.common.communication.MessageServerHandler;
import it.intecs.pisa.common.communication.messages.InitSessionMessage;
import it.intecs.pisa.common.communication.messages.StructuredMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author Massimiliano
 *
 */
public class StructuredServerSocket  implements IMessageReceptionListener{

    protected IoAcceptor acceptor = null;
    protected ArrayBlockingQueue<StructuredMessage> receivedMessages = null;
    protected int listeningPort = 0;
    protected IMessageReceptionListener recListener=null;
    protected IoSession session = null;
    private InetAddress bindAddress;
    
    /**
     * Public constructor for the class. It takes the port number to be used to listen
     * for incoming connnections
     * 
     * @param port Port number to be used for listening for incoming connections
     * @throws Exception 
     */
    public StructuredServerSocket(int port,IMessageReceptionListener listener) throws Exception {
        this.listeningPort = port;
        this.recListener=listener;
        this.initialize();
    }

    public void close() {
        int sessionCount=0;
        Map<Long,IoSession> sessionMap;
        Collection<IoSession> sessionColl;
        
        if (acceptor != null) {
           sessionCount= acceptor.getManagedSessionCount();
           if(sessionCount>0)
           {
               sessionMap=acceptor.getManagedSessions();
               sessionColl=sessionMap.values();
               
               for(IoSession ses: sessionColl)
               {
                   ses.close();
                 
               }
           }
           
            acceptor.dispose();
        }
        acceptor=null;
    }

    public InetAddress getListeningAddress() {
        return this.bindAddress;
    }

    @Override
    protected void finalize() throws Throwable {
       close();
        receivedMessages=null;
    }

    /**
     * This method initializes all resources of the class.
     * @throws Exception 
     */
    private void initialize() throws Exception {
        receivedMessages = new ArrayBlockingQueue<StructuredMessage>(1024, true);

        acceptor = new NioSocketAcceptor();
        
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        acceptor.setHandler(new MessageServerHandler(this,receivedMessages,this));
   
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        
        this.bindAddress=InetAddress.getLocalHost();
    }

    /**
     * 
     */
    public void startListening() {
        InetSocketAddress address;

        try {
            address = new InetSocketAddress(this.bindAddress, this.listeningPort);

            acceptor.bind(address);
        } catch (Exception ecc) {
            ecc.printStackTrace();
        } finally {
        }
    }

    public StructuredMessage getReceivedMessage() {
        try {
            return this.receivedMessages.poll(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
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
        try {
            WriteFuture write;
            
            if(session!=null)
            {
            write= session.write(msg.getDoc());
            write.await();
            
            return true;
            }
            else return false;
        } catch (Exception e) {
              return false;
        }
    }
    
    public void setSession(IoSession session)
    {
        this.session=session;
    }

    public void MessageReceived(StructuredMessage msg) {
     if(this.recListener!=null)
         this.recListener.MessageReceived(msg);
           
    }
}
