/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.communication;

import it.intecs.pisa.common.communication.messages.InitBreakpointsMessage;
import it.intecs.pisa.common.communication.messages.InitDebugMessage;
import it.intecs.pisa.common.communication.messages.InitSessionMessage;
import it.intecs.pisa.common.communication.messages.StructuredMessage;
import it.intecs.pisa.common.communication.messages.TerminateMessage;
import it.intecs.pisa.common.communication.sockets.StructuredClientSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

/**
 * This class is used to initialize and manage a Debug console.
 * The console will handle the communication endpoint and all security
 * settings related to it
 * @author Massimiliano
 */
public class ClientDebugConsole extends Thread implements IMessageReceptionListener{

    protected StructuredClientSocket socket=null;
    protected int listeningPort = 0;
    protected boolean connected = false;
    protected String token = null;
    protected IMessageReceptionListener listener = null;

    public ClientDebugConsole() {
        this.listeningPort = findFreePort();
    }

    public ClientDebugConsole(int port) {
        this();

        if (port != -1) {
            this.listeningPort = port;
        }
    }

    public ClientDebugConsole(int port, IMessageReceptionListener listener) {
        this(port);
        this.setListener(listener);
    }

    public boolean startDebugSession(String host, int port, String serviceName, String operationName, String[][] breakpoints) {
        try {
            socket=new StructuredClientSocket(this.listener);
            socket.connect(host, port);
                     
            InitSession();
            InitDebugSession(serviceName, operationName);
            InitBreakpoints(breakpoints);

            connected = true;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            close();

            return false;
        }
    }

    public void close() {
        connected = false;
        socket.close();
        socket=null;
    }

    public StructuredMessage readCommand() {
        StructuredMessage msg;

        if (connected == true && socket != null) {
            msg = socket.getReceivedMessage();

            if (msg instanceof TerminateMessage) {
                close();
            }

            return msg;
        } else {
            return null;
        }
    }

    public void sendCommand(StructuredMessage msg) {
        if (connected == true && socket != null) {
            socket.sendMessage(msg);
        } else {
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

    private void InitBreakpoints(String[][] breakpoints) throws Exception {
        StructuredMessage msg;

        msg = new InitBreakpointsMessage(breakpoints);
        if (!socket.sendMessage(msg)) {
            throw new Exception("Cannot send debug session initialization message");
        }
       
    }

    private void InitDebugSession(String serviceName, String operationName) throws Exception {
        StructuredMessage msg;
        Random rand;
        Integer randInt;

        rand = new Random();
        randInt = new Integer(Math.abs(rand.nextInt()));
        token = String.valueOf(randInt);

        msg = new InitDebugMessage(getToken(), serviceName, operationName);
        if (!socket.sendMessage(msg)) {
            throw new Exception("Cannot send debug session initialization message");
        }
        
    }

    private void InitSession() throws Exception {
        StructuredMessage msg;
        String localHost = null;

        localHost = InetAddress.getLocalHost().getHostAddress();
        msg = new InitSessionMessage(localHost, listeningPort);
        if (!socket.sendMessage(msg)) {
            throw new Exception("Cannot send session initialization message");
        }
        
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns a free port number on localhost, or -1 if unable to find a free port.
     * @return a free port number on localhost, or -1 if unable to find a free port
     */
    public static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return -1;
    }

    public void MessageReceived(StructuredMessage msg) {
        if(this.listener!=null)
        	listener.MessageReceived(msg);
    }

	/**
	 * @param listener the listener to set
	 */
	public void setListener(IMessageReceptionListener listener) {
		this.listener = listener;
	}

	
}
