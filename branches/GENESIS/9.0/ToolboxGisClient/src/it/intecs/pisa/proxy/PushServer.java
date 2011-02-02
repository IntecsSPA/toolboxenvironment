/* 
 *
 * ****************************************************************************
 *  Copyright 2009 Intecs
 ****************************************************************************
 *
 */

package it.intecs.pisa.proxy;

import it.intecs.pisa.gisclient.util.XMLSerializer2;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import it.intecs.pisa.gisclient.util.XmlTools;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.xml.sax.SAXException;

public class PushServer extends HttpServlet {

    private static final String INSTANCES_DIR = "/log/instances";
    private static final String STATUS_FILE = "status.xml";
    private static final String RESPONSE_FILE = "response.xml";
    private static final String REQUEST_FILE = "request.xml";
    private static final String PUSH_NS_URI = "http://www.intecs.it/push";
    private static final String PUSH_NS = "ins";
    private static final String INSTANCES_TAG = "instances";
    private static final String INSTANCE_TAG = "instance";
    private static final String MESSAGE_ID_TAG = "MessageID";
    private static final String RELATES_TO_TAG = "RelatesTo";
    private static final String STATUS_TAG = "Status";
    private static final String STATUS_ATTRIBUTE = "status";
    private static final String RESPONSE_TAG= "response";
    private static final String COMPLETED = "completed";
    private static final String PENDING = "pending";
    private static final String ERROR = "error";
    private static final String DESCRIPTION_TAG = "Description";
    private static final String ARRIVAL_TIME_TAG = "ArrivalTime";
    private static final String SERVICE_INSTANCE_TAG = "ServiceInstance";
    private static final String COMPLETION_TIME_TAG = "CompletionTime";
    private static final String RESULTS_TAG = "results";


    private static File root;
    private PushServerManager serverManager;

    /**
     * This method overrides its parent method.
     * It handles all the get requsts invoking the PusServerManager
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        try {
            //parsing request parameters in order to match operation to implement
            this.serverManager.execute(request, response);
        } catch (Throwable e) {
            throw new ServletException(e);
        }

    }

    /**
     * This method overrides its parent method. 
     * It handles the messages pushed back from the end point invoked 
     * via the WS-Addressing protocol (/PushServer/soap). Moreover it handles the 
     * instance creation trigered by the WS-Addressing client (/PushServer/manager).
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/PushServer/soap")) {
            try {
                this.handleSoap(request, response);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (requestURI.contains("/PushServer/manager")) {
            try {
                this.serverManager.createInstance(request, response);
            } catch (TransformerException ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else
            try {
                //parsing request parameters in order to match operation to implement
                this.serverManager.execute(request, response);
                } catch (Throwable e) {
                    throw new ServletException(e);
                }
    }

    /**
     * This method handles the messages pushed back from the end point invoked 
     * via the WS-Addressing protocol. Responses are stored in the instance directory
     * created by the WS-Addressin client when the asynchronous message is sent to the server.
     * The arrival time is also updated within the status file stored in the same directory.
     * The message received is sent back to the WS-Addressin ed point.
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void handleSoap(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException, ParserConfigurationException, SAXException {
        String messageId = null;
        Document soapRequestDocument = null;
        try {
            soapRequestDocument = XmlTools.docGenerate(request.getInputStream());
            try {
                // Lets try with SOAP 1.1
                messageId = getModifiedMessageId(soapRequestDocument.getElementsByTagNameNS("http://schemas.xmlsoap.org/ws/2003/03/addressing", RELATES_TO_TAG).item(0).getFirstChild().getNodeValue());
            } catch (Exception e) {
                try {
                    // Lets try with SOAP 1.2
                    messageId = getModifiedMessageId(soapRequestDocument.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", RELATES_TO_TAG).item(0).getFirstChild().getNodeValue());
                } catch (Exception ecc) {
                    Logger.getLogger(PushServer.class.getName()).log(Level.SEVERE, null, "A message with no RelatesTo tag has been received. The message is discarded.");
                }
            }

            if (messageId != null) {
                // we create the insstance dir if it is not existing
                File instanceDir = new File(root, messageId);
                if (!instanceDir.exists()) {
                    instanceDir.mkdir();
                }

                // check if there are instances related to the SOAP response just pushed
                XmlTools.dumpXML(soapRequestDocument, new File(instanceDir, RESPONSE_FILE), true);
                setCompletionTime(messageId);

                response.setContentType("text/xml");
                PrintWriter writer = response.getWriter();
                try {
                    // currently we send back the message we are receiving
                    // to be updated ....
                    new XMLSerializer2(writer).serialize(soapRequestDocument);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            } else {
                response.sendError(400, ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            response.sendError(400, ERROR);
        }
    }

    /**
     * This method initialize the servlet parameters.
     * @param config
     * @throws javax.servlet.ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            root = new File(getServletContext().getRealPath(INSTANCES_DIR));
            if (!root.exists()) {
                root.mkdir();
            }
            serverManager = new PushServerManager(this);

            System.out.println("nurc.PushServer: instances directory set to :" + root.getAbsolutePath());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    /**
     * Convert the messageId passed as input ino a format suitable to e used for creating an instance directory.
     * @param messageId
     */
    public String getModifiedMessageId(String messageId) {
        return messageId.replaceAll("[/\\\\:*?<>|]", "_");
    }

    /**
     * This method return a File class that point to the push message requested through its messageId.
     * @param messageId
     * @return Reference to pushed message
     */
    public File getResponseMessage(String messageId) {
        String modifiedMesssageId;
        File pushedMessage;
        modifiedMesssageId = getModifiedMessageId(messageId);
        pushedMessage = new File(new File(root, modifiedMesssageId), RESPONSE_FILE);
        return pushedMessage;
    }

    /**
     * This method return a File class that point to the request message requested through its messageId.
     * @param messageId
     * @return Reference to pushed message
     */
    public File getRequestMessage(String messageId) {
        String modifiedMesssageId;
        File message;
        modifiedMesssageId = getModifiedMessageId(messageId);
        message = new File(new File(root, modifiedMesssageId), REQUEST_FILE);
        return message;
    }

    /**
     * This method create a directory to handle a message exchange.
     * The directory is built starting from the messageId.
     * The request Mseesge is also stored in the directory.
     * @param messageId
     * @param requestMessage
     * @return Reference to pushed message
     */
    public void createInstance(String messageId, Document requestMessage) throws Exception {
        String modifiedMesssageId;
        modifiedMesssageId = getModifiedMessageId(messageId);
        File instanceDir = new File(root, modifiedMesssageId);
        instanceDir.mkdir();
        XmlTools.dumpXML(requestMessage, new File(instanceDir, REQUEST_FILE), true);
    }

    /**
     * This method delete an istance diretory using th messageId as key.
     * The directory content is als deleted.
     * @param messageId
     * @return void
     */
    public void deleteInstance(String messageId) throws Exception {
        String modifiedMesssageId;
        modifiedMesssageId = getModifiedMessageId(messageId);
        File instanceDir = new File(root, modifiedMesssageId);
        rmdir(instanceDir);
    }

    /**
     * This method a complete directory tree. 
     * @param directory
     * @return void
     */
    private static void rmdir(File directory) throws Exception {
        File[] list = directory.listFiles();
        for (int index = 0; list != null && index < list.length; index++) {
            if (list[index].isDirectory()) {
                rmdir(list[index]);
            } else {
                list[index].delete();
            }
        }
        directory.delete();
    }


    /**
     * This method returns a DOm document listing all the instances created by the server.
     * The Document cretaed has the following structure:
     * <ins:instances>
     *       <ins:instance>
     *       <ins:MessageID>3e7ef802-2680-4ee5-aa51-6166adc2c691</ins:MessageID>
     *       <ins:Status>pending</ins:Status>
     *       <ins:Description>No description available</ins:Description>
     *       <ins:ArrivalTime>2009-11-330 08:36:03</ins:ArrivalTime>
     *       </ins:instance>
     * </ins:instances>
     * Status can be:
     *    - pending:instance cetaed and request stored but no response available yet
     *    - completed:request and response received
     *    - error:the instance dir is creted but no inut message is dtored
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document GetInstancesList() throws ParserConfigurationException, SAXException, IOException {
        Document instances = XmlTools.newDocument();
        Element instancesNode;
        Element instanceNode;

        Element responseNode= instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + RESPONSE_TAG);
        instances.appendChild(responseNode);
        instancesNode = instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + INSTANCES_TAG);
        responseNode.appendChild(instancesNode);

        File[] files = root.listFiles();
        File request = null;
        File response = null;

        String status = "";

        Node importedNode;

        Element resultsNode = instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + RESULTS_TAG);
        responseNode.appendChild(resultsNode);
        Text instancesNum = instances.createTextNode(""+files.length);
        resultsNode.appendChild(instancesNum);


        for (int j = 0; j < files.length; j++) {
            instanceNode = instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + INSTANCE_TAG);
            instancesNode.appendChild(instanceNode);
            Element messageIdTag = instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + MESSAGE_ID_TAG);
            instanceNode.appendChild(messageIdTag);
            Text content = instances.createTextNode(files[j].getName());
            messageIdTag.appendChild(content);

            response = new File(files[j], RESPONSE_FILE);
            request = new File(files[j], REQUEST_FILE);

            status = request.exists() ? response.exists() ? COMPLETED : PENDING : ERROR;
            Element statusTag = instances.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + STATUS_TAG);
            instanceNode.appendChild(statusTag);
            content = instances.createTextNode(status);
            statusTag.appendChild(content);

            File statusFile = new File(files[j], STATUS_FILE);
            Node tempNode = null;
            if (statusFile != null && statusFile.exists()) {
                Document statusTree = XmlTools.docGenerate(new FileInputStream(statusFile));
                tempNode = statusTree.getElementsByTagNameNS(PUSH_NS_URI, DESCRIPTION_TAG).item(0);
                importedNode = instances.importNode((Node) tempNode, true);
                instanceNode.appendChild(importedNode);

                tempNode = statusTree.getElementsByTagNameNS(PUSH_NS_URI, ARRIVAL_TIME_TAG).item(0);
                if (tempNode != null) {
                    importedNode = instances.importNode((Node) tempNode, true);
                    instanceNode.appendChild(importedNode);
                }

                tempNode = statusTree.getElementsByTagNameNS(PUSH_NS_URI, COMPLETION_TIME_TAG).item(0);
                if (tempNode != null) {
                    importedNode = instances.importNode((Node) tempNode, true);
                    instanceNode.appendChild(importedNode);
                }

                tempNode = statusTree.getElementsByTagNameNS(PUSH_NS_URI, SERVICE_INSTANCE_TAG).item(0);
                if (tempNode != null) {
                    importedNode = instances.importNode((Node) tempNode, true);
                    instanceNode.appendChild(importedNode);
                }
            }
        }
        return instances;
    }

    /**
     * This method return the current time formatted according to the pattern passed as input.
     * @param format
     * @return void
     */
    private String getNow(String format) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        return formatter.format(new Date()).toString();
    }

    /**
     * This method sets the response time in the status file.
     * @param messageId
     * @return void
     */
    private void setCompletionTime(String messageId) throws ParserConfigurationException, SAXException, IOException, Exception {
        File instanceDir = new File(root, messageId);

        File statusFile = new File(instanceDir, STATUS_FILE);
        if (statusFile != null && statusFile.exists()) {
            Document statusDocument = XmlTools.docGenerate(new FileInputStream(statusFile));
            Element arrivalTimeNode = statusDocument.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + COMPLETION_TIME_TAG);
            Text content = statusDocument.createTextNode(getNow("yyyy-MM-dd'T'HH:mm:ss"));
            arrivalTimeNode.appendChild(content);
            Node statusNode = statusDocument.getElementsByTagNameNS(PUSH_NS_URI, STATUS_TAG).item(0);
            statusNode.appendChild(arrivalTimeNode);
            XmlTools.dumpXML(statusDocument, statusFile, true);
        }
    }
}