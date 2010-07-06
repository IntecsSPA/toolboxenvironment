/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.communication;

import it.intecs.pisa.communication.messages.ErrorMessage;
import it.intecs.pisa.communication.messages.InitBreakpointsMessage;
import it.intecs.pisa.communication.messages.InitDebugMessage;
import it.intecs.pisa.communication.messages.InitSessionMessage;
import it.intecs.pisa.communication.messages.StructuredMessage;
import it.intecs.pisa.communication.messages.TerminateMessage;
import it.intecs.pisa.communication.sockets.StructuredServerSocket;
import java.net.InetAddress;

/**
 * This class is used to initialize and manage a Debug console.
 * The console will handle the communication endpoint and all security
 * settings related to it
 * @author Massimiliano
 */
public class ServerDebugConsole extends Thread implements IMessageReceptionListener {

    protected StructuredServerSocket socket = null;
    protected int listeningPort = 0;
    protected boolean connected = false;
    private boolean terminated = false;
    protected String serviceToDebug;
    protected String operationToDebug;
    protected String debugToken = null;
    protected String[][] breakpoints = null;
    protected IMessageReceptionListener listener = null;
    private Boolean connMutex = new Boolean(false);

    public ServerDebugConsole() {
    }

    public ServerDebugConsole(int listeningPort) {
        this();
        this.listeningPort = listeningPort;
        
        try
        {
        socket = new StructuredServerSocket(listeningPort, this);
        socket.startListening();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public ServerDebugConsole(int listeningPort, IMessageReceptionListener listener) {
        this(listeningPort);

        this.listener = listener;

    }

    @Override
    public void run() {
        try {
            synchronized (connMutex) {
                

                waitForIncomingSession();
                waitForDebugSession();
                initiliazeBreakpoints();

                connected = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorMessage error;

            error = new ErrorMessage();
            error.setErrorMessage(ex.getMessage());

            socket.sendMessage(error);
            close();
        } finally {
           
        }
    }

    public synchronized void close() {
        connected = false;
        terminated = true;
        if (socket != null) {
            socket.close();
            socket = null;
        }
        System.gc();

    }

    public void waitForConnection() {
        synchronized (connMutex) {
            connected = true && connected;
        }
    }

    public StructuredMessage readCommand() {
        StructuredMessage msg;

        if (isConnected() == true && isTerminated() == false && socket != null) {
            msg = socket.getReceivedMessage();
            System.out.println("Readed message: "+msg.getClass().getName());
            return msg;
        } else {
            System.out.println("Cannot read message");
            return null;
        }
    }

    public void sendCommand(StructuredMessage msg) {
        if (isConnected() == true && isTerminated() == false && socket != null) {
            socket.sendMessage(msg);
        } else {
            System.out.println("Cannot send command "+msg.getClass().getCanonicalName());
            return;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    protected void finalize() throws Throwable {
        socket.close();
        this.socket = null;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    private void initiliazeBreakpoints() throws Exception {
        StructuredMessage msg;
        InitBreakpointsMessage breakMsg;

        msg = socket.getReceivedMessage();
        if (msg instanceof InitBreakpointsMessage) {
            breakMsg = (InitBreakpointsMessage) msg;

            breakpoints = breakMsg.getBreakpoints();
        } else {
            throw new Exception("Unexpected message. Received " + msg.getClass().getName() + " instead of InitBreakpointsMessage");
        }
    }

    private void waitForDebugSession() throws Exception {
        StructuredMessage msg;
        InitDebugMessage dbgMessage;

        msg = socket.getReceivedMessage();
        if (msg instanceof InitDebugMessage) {

            dbgMessage = (InitDebugMessage) msg;
            serviceToDebug = dbgMessage.getServiceName();
            operationToDebug = dbgMessage.getOperationName();
            debugToken = dbgMessage.getToken();

            connected = true;
        } else {
            throw new Exception("Unexpected message. Received " + msg.getClass().getName() + " instead of InitDebugMessage");
        }
    }

    private void waitForIncomingSession() throws Exception {
        StructuredMessage msg;
        InitSessionMessage initMsg;

        msg = socket.getReceivedMessage();
        if (msg instanceof InitSessionMessage) {
            connected = true;
        } else {
            throw new Exception("Unexpected message. Received " + msg.getClass().getName() + " instead of InitSessionMessage");
        }
    }

    public String getServiceToDebug() {
        return serviceToDebug;
    }

    public String getOperationToDebug() {
        return operationToDebug;
    }

    public String getDebugToken() {
        return debugToken;
    }

    public String[][] getBreakpoints() {
        return breakpoints;
    }

    public void addBreakpoint(String file, String xpath,String line) {
        String[] newBrkpt;
        String[][] newBrkptList;
        int count = 0;
        int i = 0;

        newBrkpt = new String[3];
        newBrkpt[0] =file;
        newBrkpt[0] =xpath;
        newBrkpt[0] =line;

        count = this.breakpoints.length + 1;

        newBrkptList = new String[count][3];
        for (i = 0; i < count; i++) {
            newBrkptList[i] = breakpoints[i];
        }

        newBrkptList[i] = newBrkpt;
        breakpoints=newBrkptList;
        
    }

    public void removeBreakpoint(String file, String xpath) {
       String[][] newBrkpt;
       String[] brkpt;
        int count=0;
        int j=0;
        
        if(isBreakpointSet(file,xpath))
        {
            count=breakpoints.length-1;
            newBrkpt=new String[count][3];
            
            for(int i=0;i<breakpoints.length;i++)
            {
                brkpt=breakpoints[i];
                
                if((brkpt[0].equals(file) && brkpt[1].equals(xpath))==false)
                {
                    newBrkpt[j]=brkpt;
                    j++;
                }
            }
            
            breakpoints=newBrkpt;
            
        }
    }
    
    public boolean isBreakpointSet(String file,String xPath)
    {
        for(String[] b:breakpoints)
        {
            if(b[0].equals(file) && b[1].equals(xPath))
                return true;
        }
        return false;
    }

    public void MessageReceived(StructuredMessage msg) {
        if (msg instanceof TerminateMessage) {
            this.terminated = true;
        }

        if (this.listener != null) {
            listener.MessageReceived(msg);
        }
    }

    public boolean isTerminated() {
        return terminated;
    }
    
    public InetAddress getListeningAddress()
    {
       return  this.socket.getListeningAddress();
    }
}
