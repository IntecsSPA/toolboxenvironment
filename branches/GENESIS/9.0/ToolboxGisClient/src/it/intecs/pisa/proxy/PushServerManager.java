/* 
 *
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 */
package it.intecs.pisa.proxy;

import it.intecs.pisa.gisclient.util.XMLSerializer2;
import it.intecs.pisa.gisclient.util.XmlTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is used to process all requests received by the Push Server on its doGet entry point
 * @author Massimiliano
 */
public class PushServerManager {

    protected static final String REQUEST_PARAMETER_COMMAND = "cmd";
    protected static final String REQUEST_PARAMETER_GET_RESPONSE_MESSAGE = "GetResponseMessage";
    protected static final String REQUEST_PARAMETER_GET_REQUEST_MESSAGE = "GetRequestMessage";
    protected static final String REQUEST_PARAMETER_GET_INSTANCES_LIST = "GetInstances";
    protected static final String REQUEST_PARAMETER_DELETE_INSTANCE = "DeleteInstance";
    protected static final String REQUEST_PARAMETER_MESSAGE_ID = "MessageID";
    protected static final String REQUEST_PARAMETER_CREATE_INSTANCE = "CreateInstance";
    protected static final String MESSAGE_ID_TAG = "MessageId";

    protected PushServer server;

    /**
     * Default constructor
     */
    public PushServerManager(PushServer server) {
        this.server = server;
    }

    /**
     *  This method execute Push server operation based on the input parameters in the request header
     * @param request
     * @param response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, TransformerException, SAXException, ParserConfigurationException, Exception {
        String command;

//        try {
            command = request.getParameter(REQUEST_PARAMETER_COMMAND);

            if (command.equalsIgnoreCase(REQUEST_PARAMETER_GET_RESPONSE_MESSAGE)) {
                executeGetResponseMessage(request, response);
            } else if (command.equalsIgnoreCase(REQUEST_PARAMETER_GET_REQUEST_MESSAGE)) {
                executeGetRequestMessage(request, response);
            } else if (command.equalsIgnoreCase(REQUEST_PARAMETER_GET_INSTANCES_LIST)) {
                executeGetInstancesList(request, response);
            } else if (command.equalsIgnoreCase(REQUEST_PARAMETER_DELETE_INSTANCE)) {
                executeDeleteInstance(request, response);
            } else if (command.equalsIgnoreCase(REQUEST_PARAMETER_CREATE_INSTANCE)) {
                createInstance(request, response);
            } else {
                //returning error message to caller
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }
 /*       } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            throw new ServletException(e);
        }*/
    }

    protected void executeGetResponseMessage(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException {
        File pushedMessage;
        PrintWriter writer;
        Document pushedMessageDoc;
        String messageId;

        messageId = request.getParameter(REQUEST_PARAMETER_MESSAGE_ID);
        pushedMessage = server.getResponseMessage(messageId);

        if (pushedMessage.exists()) {
            response.setContentType("text/xml");
            writer = response.getWriter();

            //sending back pushed message
            pushedMessageDoc = XmlTools.docGenerate(new FileInputStream(pushedMessage));
            new XMLSerializer2(writer).serialize(pushedMessageDoc);
            writer.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void executeGetRequestMessage(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException {
        File message;
        PrintWriter writer;
        Document messageDoc;
        String messageId;

        messageId = request.getParameter(REQUEST_PARAMETER_MESSAGE_ID);
        message = server.getRequestMessage(messageId);

        if (message.exists()) {
            response.setContentType("text/xml");
            writer = response.getWriter();

            //sending back pushed message
            messageDoc = XmlTools.docGenerate(new FileInputStream(message));
            new XMLSerializer2(writer).serialize(messageDoc);
            writer.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void executeGetInstancesList(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException {
        Document list;
        PrintWriter writer;
        list = server.GetInstancesList();

        if (list != null) {
            response.setContentType("text/xml");
            writer = response.getWriter();
            new XMLSerializer2(writer).serialize(list);
            writer.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void executeDeleteInstance(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException, Exception {
        String messageId;
        messageId = request.getParameter(REQUEST_PARAMETER_MESSAGE_ID);
        server.deleteInstance(messageId);
        String message = "Instance "+messageId+" deleted";
        response.setContentType("text/html");
        response.getOutputStream().write(message.getBytes());
    }

    protected void createInstance(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException, Exception {
        Document requestMessage = null;
        String messageId = "";
        try {
            requestMessage = XmlTools.docGenerate(request.getInputStream());
            messageId = server.getModifiedMessageId(requestMessage.getElementsByTagNameNS("http://schemas.xmlsoap.org/ws/2003/03/addressing", MESSAGE_ID_TAG).item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            try {
                messageId = server.getModifiedMessageId(requestMessage.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", MESSAGE_ID_TAG).item(0).getFirstChild().getNodeValue());
            } catch (Exception ecc) {
                messageId = "noMessageId";
                ecc.printStackTrace();
                response.setStatus(400);
                response.getOutputStream().write(ecc.getMessage().getBytes());
            }
        }

        server.createInstance(messageId, requestMessage);
        String message = "OK";
        response.getOutputStream().write(message.getBytes());
    }

    protected void deleteInstance(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException, ParserConfigurationException, Exception {
        Document requestMessage = null;
        String messageId = request.getParameter(MESSAGE_ID_TAG);



        try {
            requestMessage = XmlTools.docGenerate(request.getInputStream());
            messageId = server.getModifiedMessageId(requestMessage.getElementsByTagNameNS("http://schemas.xmlsoap.org/ws/2003/03/addressing", MESSAGE_ID_TAG).item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            try {
                messageId = server.getModifiedMessageId(requestMessage.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", MESSAGE_ID_TAG).item(0).getFirstChild().getNodeValue());
            } catch (Exception ecc) {
                messageId = "noMessageId";
                ecc.printStackTrace();
                response.setStatus(400);
                response.getOutputStream().write(ecc.getMessage().getBytes());
            }
        }

        server.createInstance(messageId, requestMessage);
        String message = "OK";
        response.getOutputStream().write(message.getBytes());
    }
}
