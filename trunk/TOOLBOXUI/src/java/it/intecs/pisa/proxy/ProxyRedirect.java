/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.proxy;

import it.intecs.pisa.gisclient.util.XmlTools;
import it.intecs.pisa.gisclient.util.SoapTools;
import it.intecs.pisa.gisclient.util.OGCUtil;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.httpclient.*;

import org.apache.commons.httpclient.methods.GetMethod;

// import log4j packages
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ProxyRedirect extends HttpServlet {

    private final static Logger log = Logger.getLogger(ProxyRedirect.class);
    private final static String LOG_SERVICES = "log/services";
    private final static String CLIENT_REQUEST_PRFX = "ClientRequest";
    private final static String CLIENT_TAG_REQUEST_PRFX = "TagRequest";
    private final static String SERVICE_REQUEST_PRFX = "ServiceRequest";
    private final static String SERVICE_RESPONSE_PRFX = "ServiceResponse";
    private final static String PROXY_RESPONSE_PRFX = "ProxyResponse";
    private final static String JSON_RESPONSE_PRFX = "ProxyResponseJSON";
    private final static String JS_RESPONSE_PRFX = "ProxyResponseJS";
    private final static String JSON_XSLT_PATH = "WEB-INF/classes/it/intecs/pisa/proxy/resources/xsl/xml2json.xslt";
    private final static String JAVASCRIPT_XSLT_PATH = "WEB-INF/classes/it/intecs/pisa/proxy/resources/xsl/xml2js.xslt";
//---------------------------------------------------------------------------
// Public Methods
//----------------------------------------------------------------------------
    public ServletContext context_ = null;

    /***************************************************************************
     * Initialize variables called when context is initialized
     *
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context_ = config.getServletContext();
        log.info("GisClient.ProxyRedirect: context initialized to:" + context_.getServletContextName());
    }

    /***************************************************************************
     * Process the HTTP Get request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        TransformerFactory tfactory = null;
        Transformer transformer = null;
        try {
            if (log.isDebugEnabled()) {
                Enumeration e = request.getHeaderNames();
                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    String value = request.getHeader(name);
                    log.debug("request header:" + name + ":" + value);
                }
            }

            // Transfer bytes from in to out
            log.debug("HTTP GET: transferring...");

            //execute the GET
            String type = request.getParameter("type") == null?"":request.getParameter("type");
            String serverUrl = request.getParameter("url");
            String outputFormat = request.getParameter("outFormat");
            String XSLResponse = request.getParameter("XSLResponse");

       
            if (!type.equalsIgnoreCase("paging") ) {

                Enumeration e = request.getParameterNames();
                String key = "";
                String otherParameters = "";
                while (e.hasMoreElements()) {
                    key = (String) e.nextElement();
                    if (!(key.equals("type") || key.equals("url") || key.equals("outFormat"))) {
                        otherParameters += "&" + key + "=" + request.getParameter(key);
                    }
                }

                if (!otherParameters.equalsIgnoreCase("")) {
                    if (serverUrl.contains("?")) {
                        serverUrl += otherParameters;
                    } else {
                        serverUrl += "?" + otherParameters.substring(1, otherParameters.length());
                    }
                }


                if (serverUrl.startsWith("http://")) {
                    log.info("GET param serverUrl:" + serverUrl);
                    System.out.println("GET REQUEST= " + serverUrl);
                    // System.out.println("GET param serverUrl:" + serverUrl);
                    HttpClient client = new HttpClient();
                  
                        if (type.equalsIgnoreCase("ENCODED")) {
                            serverUrl = OGCUtil.getSLDEncoded(serverUrl);
                            //  System.out.println("ENCODED SLD serverUrl:" + serverUrl);
                            log.info("ENCODED serverUrl:" + serverUrl);
                        } else if (type.equalsIgnoreCase("ENCODEDFILTER")) {
                            serverUrl = OGCUtil.getFilterEncoded(serverUrl);
                            //     System.out.println("ENCODED FILTER serverUrl:" + serverUrl);
                            log.info("ENCODED FILTER serverUrl:" + serverUrl);
                        }
                    

                    serverUrl = serverUrl.replaceAll("<", "%3C");
                    serverUrl = serverUrl.replaceAll(">", "%3E");
                    serverUrl = serverUrl.replaceAll(" ", "%20");
                    serverUrl = serverUrl.replaceAll("\"", "%22");
                    serverUrl = serverUrl.replaceAll("#", "%23");


                    GetMethod httpget = new GetMethod(serverUrl);

                    client.executeMethod(httpget);

                    if (log.isDebugEnabled()) {
                        Header[] respHeaders = httpget.getResponseHeaders();
                        for (int i = 0; i < respHeaders.length; ++i) {
                            String headerName = respHeaders[i].getName();
                            String headerValue = respHeaders[i].getValue();
                            log.debug("responseHeaders:" + headerName + "=" + headerValue);
                        }
                    }

                    //dump response to out
                    if (httpget.getStatusCode() == HttpStatus.SC_OK) {
                        //force the response to have XML content type (WMS servers generally don't)
                        response.setContentType(outputFormat);
                        if (XSLResponse != null && !XSLResponse.equalsIgnoreCase("")) {
                            try {
                                tfactory = TransformerFactory.newInstance();
                                transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(XSLResponse))));
                            } catch (Exception ex) {
                                log.debug("GisClient Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                            try {
                                transformer.transform(new StreamSource(httpget.getResponseBodyAsStream()), new StreamResult(response.getOutputStream()));

                            } catch (Exception ex) {
                                log.debug("GisClient Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        } else {
                            String responseBody = httpget.getResponseBodyAsString().trim();
                            response.setContentLength(responseBody.length());
                            log.info("responseBody:" + responseBody);
                            PrintWriter out = response.getWriter();
                            out.print(responseBody);
                        }
                        response.flushBuffer();
                    } else {
                        log.error("Unexpected failure: " + httpget.getStatusLine().toString());
                    }
                    httpget.releaseConnection();
                } else {
                    throw new ServletException("only HTTP protocol supported");
                }
            } else 
                {   //Paging Section -- Start
                    String id = request.getParameter("id");
                    String logService = request.getParameter("LogFolder");
                    String startPage = request.getParameter("start");
                    String limitPage = request.getParameter("limit");
                    String seviceLogFolder = getServletContext().getRealPath(ProxyRedirect.LOG_SERVICES + logService);
                    File logDir = new File(seviceLogFolder);
                    String pathIdRequest = "/Request_" + id;
                    File requestDir;
                    requestDir = new File(logDir, pathIdRequest);
                    Document docProxy = XmlTools.docGenerate(new FileInputStream(new File(requestDir, ProxyRedirect.CLIENT_REQUEST_PRFX + ".xml")));
                    sendPostRequest(docProxy, response, startPage, limitPage);
                    //Paging Section  -- End
            }

        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }// doGet

    /***************************************************************************
     * Process the HTTP Post request
     */
    @SuppressWarnings("empty-statement")
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        TransformerFactory tfactory = null;
        Document docProxy = null;
        InputStream resp = null;
        Transformer transformer = null;
        Boolean soapFault = false;
        try {
            // XmlTools.copyInputStreamToOutputStream(request.getInputStream(), new FileOutputStream("/home/maro/Desktop/ProxyGeoserverWPS.txt"));
            docProxy = XmlTools.docGenerate(request.getInputStream());
        } catch (ParserConfigurationException ex) {
            java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
        }
        String serviceUrl = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "ServiceUrl");
        System.out.println("Service URL: " + serviceUrl);
        String protocol = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Protocol");
        if (protocol == null) {
            protocol = "HTTPPOST";
        }
        String xslRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "XSLRequest");
        String xslResponse = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "XSLResponse");
        String logService = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "LogFolder");
        String ident = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Ident");
        String iteratorElement = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "IteratorElement");
        String soapAction = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "SoapAction");
        String outputFormat = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "outputFormat");
        if (outputFormat == null) {
            outputFormat = "XML";
        }

        String outputMod = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "outputMod");
        if (outputMod == null) {
            outputMod = "VALUE";
        }

        String seviceLogFolder = getServletContext().getRealPath(ProxyRedirect.LOG_SERVICES + logService);


        System.out.println("Log Folder:  " + seviceLogFolder);

        File logDir = new File(seviceLogFolder);
        boolean b = logDir.mkdir();

        System.out.println("Create Log Folder : " + b);

        String pathIdRequest = "/Request_" + XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "idRequest").hashCode();
        File requestDir;
        requestDir = new File(logDir, pathIdRequest);

        //seviceLogFolder+=pathIdRequest;

        b = requestDir.mkdir();


        seviceLogFolder += "/";
        File xmlRequestNamePath = new File(requestDir, ProxyRedirect.CLIENT_REQUEST_PRFX + ".xml");
        File xmlTagRequestPath = new File(requestDir, ProxyRedirect.CLIENT_TAG_REQUEST_PRFX + ".xml");
        File serviceRequestPath = new File(requestDir, ProxyRedirect.SERVICE_REQUEST_PRFX + ".xml");
        File serviceResponsePath = new File(requestDir, ProxyRedirect.SERVICE_RESPONSE_PRFX + ".xml");
        File proxyResponsePath = new File(requestDir, ProxyRedirect.PROXY_RESPONSE_PRFX + ".xml");
        File proxyResponseJSONPath = new File(requestDir, ProxyRedirect.JSON_RESPONSE_PRFX + ".txt");
        File proxyResponseJSPath = new File(requestDir, ProxyRedirect.JS_RESPONSE_PRFX + ".js");


        File test = xmlRequestNamePath;
        if (test.exists()) {
            test = new File(xmlRequestNamePath + "2");
        }
        try {
            XmlTools.dumpXML(docProxy, test, true);
        } catch (Exception ex) {
            log.debug("GisClient Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        HttpURLConnection connection = null;
        if (!outputMod.equalsIgnoreCase("PAGING")) {
            if (protocol.equalsIgnoreCase("HTTPPOST")) {
                System.out.println("HTTP-POST");
                OutputStream connOut = null;
                try {
                    connection = (HttpURLConnection) new URL(serviceUrl).openConnection();
                    connection.setDoOutput(true);
                    connection.setAllowUserInteraction(false);
                    if (soapAction != null) {
                        connection.setRequestProperty("SOAPAction", soapAction);
                    }
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-type", "text/xml");

                } catch (Exception ex) {
                    log.debug("GisClient Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }

                tfactory = TransformerFactory.newInstance();


                if (xslRequest != null) {
                    try {
                        transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslRequest))));
                    } catch (Exception ex) {
                        log.debug("GisClient Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    try {
                        transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(serviceRequestPath));
                        transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(connection.getOutputStream()));
                    } catch (Exception ex) {
                        log.debug("GisClient Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    Node processingRequest = XmlTools.getElementChild(docProxy.getDocumentElement(), "Request");
                    if (processingRequest != null) {
                        if (processingRequest.getFirstChild() != null) {
                            Document documentReq = XmlTools.newDocument();
                            Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                            documentReq.appendChild(payload);

                            File reqTag = xmlTagRequestPath;
                            try {
                                XmlTools.dumpXML(documentReq, reqTag);

                                connection.setRequestProperty("Content-length", Long.toString(reqTag.length()));
                                connOut = connection.getOutputStream();
                                XmlTools.copyInputStreamToOutputStream(new FileInputStream(reqTag), connOut);
                                connOut.close();
                                // transformer.transform(new DOMSource(documentReq), new StreamResult(connection.getOutputStream()));
                            } catch (Exception ex) {
                                log.debug("GisClient Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            File fINSttr = xmlRequestNamePath;
                            connection.setRequestProperty("Content-length", Long.toString(fINSttr.length()));
                            connOut = connection.getOutputStream();
                            XmlTools.copyInputStreamToOutputStream(new FileInputStream(fINSttr), connOut);
                            connOut.close();
                        } catch (Exception ex) {
                            log.debug("GisClient Exception: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                }
                try {
                    resp = connection.getInputStream();
                    XmlTools.copyInputStreamToOutputStream(resp, new FileOutputStream(serviceResponsePath));
                } catch (IOException ex) {
                    log.debug("GisClient Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                System.out.println("SOAP Request...");


                String soapVersion = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "soapVersion");
                String security = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Security");
                String userAuthentication = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "UserAutentication");
                String passwordAuthentication = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "PasswordAutentication");

                if (security.equalsIgnoreCase("true")) {
                    if (userAuthentication == null) {
                        userAuthentication = "";
                    }
                    if (passwordAuthentication == null) {
                        passwordAuthentication = "";
                    }
                }

                if (soapVersion == null) {
                    soapVersion = "1.1";
                }
                Document docResp = null;
                tfactory = TransformerFactory.newInstance();
                SoapTools spt = new SoapTools();
                if (xslRequest != null) {
                    try {
                        transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslRequest))));
                    } catch (TransformerConfigurationException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                    try {
                        transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(serviceRequestPath));
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                    try {
                        if (security.equalsIgnoreCase("true")) {
                            docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(serviceRequestPath)), userAuthentication, passwordAuthentication);
                        } else {
                            docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(serviceRequestPath))/*,soapVersion*/);
                        }
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                        response.setStatus(400);
                        new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                        soapFault = true;
                    }
                } else {
                    Node processingRequest = XmlTools.getElementChild(docProxy.getDocumentElement(), "Request");
                    if (processingRequest != null) {
                        if (processingRequest.getFirstChild() != null) {
                            Document documentReq = XmlTools.newDocument();
                            Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                            documentReq.appendChild(payload);
                            System.out.println("SOAP ACTION: " + soapAction);
                            try {
                                if (security.equalsIgnoreCase("true")) {
                                    docResp = spt.getSoapCall(serviceUrl, soapAction, documentReq, userAuthentication, passwordAuthentication);
                                } else {
                                    docResp = spt.getSoapCall(serviceUrl, soapAction, documentReq/*, soapVersion*/);
                                }
                            } catch (Exception ex) {
                                log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                                ex.printStackTrace();
                                response.setStatus(400);
                                new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                                soapFault = true;
                            }
                        }
                    } else {
                        try {
                            if (security.equalsIgnoreCase("true")) {
                                docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(xmlRequestNamePath)), userAuthentication, passwordAuthentication);
                            } else {
                                docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(xmlRequestNamePath))/*, soapVersion*/);
                            }
                        } catch (Exception ex) {
                            log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                            ex.printStackTrace();
                            response.setStatus(400);
                            new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                            soapFault = true;

                        }
                    }
                }
                if (!soapFault) {
                    try {
                        XmlTools.dumpXML(docResp, serviceResponsePath);
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                }
            }
            if (soapFault) {
                XmlTools.copyInputStreamToOutputStream(new FileInputStream(serviceResponsePath), response.getOutputStream());

            } else {

                if (xslResponse != null) {
                    try {
                        transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslResponse))));
                    } catch (TransformerConfigurationException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                    try {
                        transformer.transform(new StreamSource(serviceResponsePath), new StreamResult(proxyResponsePath));

                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                } else {
                    proxyResponsePath = serviceResponsePath;
                }
                String pathResult = "";
                tfactory = TransformerFactory.newInstance();
                if (outputFormat.equalsIgnoreCase("JSON")) {
                    try {
                        transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(JSON_XSLT_PATH))));
                        if (iteratorElement != null) {
                            transformer.setParameter("iteratorElement", iteratorElement);
                        }
                    } catch (TransformerConfigurationException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                    try {
                        transformer.transform(new StreamSource(proxyResponsePath), new StreamResult(proxyResponseJSONPath));

                        response.setContentType("text/json");
                        pathResult = proxyResponseJSONPath.getAbsolutePath();
                        // XmlTools.copyInputStreamToOutputStream(new FileInputStream(proxyResponseJSONPath), response.getOutputStream());
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                } else {
                    if (outputFormat.equalsIgnoreCase("JAVASCRIPT")) {
                        try {
                            transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(JAVASCRIPT_XSLT_PATH))));
                        } catch (TransformerConfigurationException ex) {
                            log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                            ex.printStackTrace();
                        }
                        try {
                            transformer.transform(new StreamSource(proxyResponsePath), new StreamResult(proxyResponseJSPath));
                            response.setContentType("text/javascript");
                            pathResult = proxyResponseJSPath.getAbsolutePath();
                            //XmlTools.copyInputStreamToOutputStream(new FileInputStream(proxyResponseJSPath), response.getOutputStream());
                        } catch (Exception ex) {
                            log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                            ex.printStackTrace();
                        }
                    } else {
                        pathResult = proxyResponsePath.getAbsolutePath();
                        response.setContentType("text/xml;charset=UTF-8");

                    }
                }

                if (outputMod.equalsIgnoreCase("REFERENCE")) {
                    response.setContentType("text/xml;charset=UTF-8");
                    String referenceMessage = "<ProxyRedirectResponse id='" + pathIdRequest + "'>";
                    referenceMessage += "<responseUrl>" + (pathResult.substring(logDir.getAbsolutePath().length()/*-pathIdRequest.length()*/ - logService.length() - LOG_SERVICES.length(), pathResult.length())).replaceAll("\\\\", "/") + "</responseUrl>";
                    referenceMessage += "</ProxyRedirectResponse>";
                    try {
                        response.getOutputStream().write(referenceMessage.getBytes());
                    } catch (IOException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                } else {
                    if (ident != null) {
                        if (ident.equalsIgnoreCase("true")) {
                            try {
                                Document docIdent = XmlTools.docGenerate(new FileInputStream(pathResult));
                                XmlTools.indent(docIdent);
                                XmlTools.copyDocToOutputStream(docIdent, new FileOutputStream(pathResult));
                            } catch (ParserConfigurationException ex) {
                                java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SAXException ex) {
                                java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    try {
                        XmlTools.copyInputStreamToOutputStream(new FileInputStream(pathResult), response.getOutputStream());
                    } catch (IOException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            String firstRes = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "FisrtPageResult");
            String lastRes = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "LastPageResult");
            response.setContentType("text/xml;charset=UTF-8");
            String referenceMessage = "<ProxyRedirectResponse id='" + pathIdRequest + "'>";
            referenceMessage += "<responseUrl>" + this.getServletName() + "?id="
                    + XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "idRequest").hashCode()
                    + "&amp;LogFolder=" + logService
                    + "&amp;type=paging"
                    + /* "&first="+firstRes+
                    "&last="+lastRes*/ "</responseUrl>";
            referenceMessage += "</ProxyRedirectResponse>";
            try {
                response.getOutputStream().write(referenceMessage.getBytes());
            } catch (IOException ex) {
                log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                ex.printStackTrace();
            }
        }

    }

    private void sendPostRequest(Document docProxy, HttpServletResponse response, String startPage, String limitPage) throws FileNotFoundException, IOException {
        TransformerFactory tfactory = null;
        Transformer transformer = null;
        InputStream resp = null;
        Boolean soapFault = false;
        String serviceUrl = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "ServiceUrl");
        System.out.println("Service URL: " + serviceUrl);
        String protocol = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Protocol");
        if (protocol == null) {
            protocol = "HTTPPOST";
        }
        String xslRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "XSLRequest");
        String xslResponse = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "XSLResponse");
        String logService = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "LogFolder");
        String ident = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Ident");
        String iteratorElement = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "IteratorElement");
        String soapAction = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "SoapAction");
        String outputFormat = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "outputFormat");
        if (outputFormat == null) {
            outputFormat = "XML";
        }

        String outputMod = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "outputMod");
        if (outputMod == null) {
            outputMod = "VALUE";
        }
        String seviceLogFolder = getServletContext().getRealPath(ProxyRedirect.LOG_SERVICES + logService);
        System.out.println("Log Folder:  " + seviceLogFolder);

        File logDir = new File(seviceLogFolder);
        boolean b = logDir.mkdir();
        System.out.println("Create Log Folder : " + b);

        String pathIdRequest = "/Request_" + XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "idRequest").hashCode();
        File requestDir;
        requestDir = new File(logDir, pathIdRequest);
        b = requestDir.mkdir();
        seviceLogFolder += "/";
        File xmlRequestNamePath = new File(requestDir, ProxyRedirect.CLIENT_REQUEST_PRFX + ".xml");
        File xmlTagRequestPath = new File(requestDir, ProxyRedirect.CLIENT_TAG_REQUEST_PRFX + ".xml");
        File serviceRequestPath = new File(requestDir, ProxyRedirect.SERVICE_REQUEST_PRFX + ".xml");
        File serviceResponsePath = new File(requestDir, ProxyRedirect.SERVICE_RESPONSE_PRFX + ".xml");
        File proxyResponsePath = new File(requestDir, ProxyRedirect.PROXY_RESPONSE_PRFX + ".xml");
        File proxyResponseJSONPath = new File(requestDir, ProxyRedirect.JSON_RESPONSE_PRFX + ".txt");
        File proxyResponseJSPath = new File(requestDir, ProxyRedirect.JS_RESPONSE_PRFX + ".js");

        if (!xmlRequestNamePath.exists()) {
            try {
                XmlTools.dumpXML(docProxy, xmlRequestNamePath, true);
            } catch (Exception ex) {
                log.debug("GisClient Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        HttpURLConnection connection = null;
        if (protocol.equalsIgnoreCase("HTTPPOST")) {
            System.out.println("HTTP-POST");
            OutputStream connOut = null;
            try {
                connection = (HttpURLConnection) new URL(serviceUrl).openConnection();
                connection.setDoOutput(true);
                connection.setAllowUserInteraction(false);
                if (soapAction != null) {
                    connection.setRequestProperty("SOAPAction", soapAction);
                }
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type", "text/xml");

            } catch (Exception ex) {
                log.debug("GisClient Exception: " + ex.getMessage());
                ex.printStackTrace();
            }

            tfactory = TransformerFactory.newInstance();


            if (xslRequest != null) {
                try {
                    transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslRequest))));
                } catch (Exception ex) {
                    log.debug("GisClient Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
                try {
                    if(startPage!=null && limitPage!=null){
                      transformer.setParameter("startPosition", startPage);
                      transformer.setParameter("maxRecords", limitPage);
                    }
                    transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(serviceRequestPath));
                    transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(connection.getOutputStream()));
                } catch (Exception ex) {
                    log.debug("GisClient Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                Node processingRequest = XmlTools.getElementChild(docProxy.getDocumentElement(), "Request");
                if (processingRequest != null) {
                    if (processingRequest.getFirstChild() != null) {
                        Document documentReq = XmlTools.newDocument();
                        Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                        documentReq.appendChild(payload);

                        File reqTag = xmlTagRequestPath;
                        try {
                            XmlTools.dumpXML(documentReq, reqTag);

                            connection.setRequestProperty("Content-length", Long.toString(reqTag.length()));
                            connOut = connection.getOutputStream();
                            XmlTools.copyInputStreamToOutputStream(new FileInputStream(reqTag), connOut);
                            connOut.close();
                            // transformer.transform(new DOMSource(documentReq), new StreamResult(connection.getOutputStream()));
                        } catch (Exception ex) {
                            log.debug("GisClient Exception: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                } else {
                    try {
                        File fINSttr = xmlRequestNamePath;
                        connection.setRequestProperty("Content-length", Long.toString(fINSttr.length()));
                        connOut = connection.getOutputStream();
                        XmlTools.copyInputStreamToOutputStream(new FileInputStream(fINSttr), connOut);
                        connOut.close();
                    } catch (Exception ex) {
                        log.debug("GisClient Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

            }
            try {
                resp = connection.getInputStream();
                XmlTools.copyInputStreamToOutputStream(resp, new FileOutputStream(serviceResponsePath));
            } catch (IOException ex) {
                log.debug("GisClient Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            System.out.println("SOAP Request...");
            String soapVersion = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "soapVersion");
            String security = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "Security");
            String userAuthentication = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "UserAutentication");
            String passwordAuthentication = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), "PasswordAutentication");

            if (security.equalsIgnoreCase("true")) {
                if (userAuthentication == null) {
                    userAuthentication = "";
                }
                if (passwordAuthentication == null) {
                    passwordAuthentication = "";
                }
            }

            if (soapVersion == null) {
                soapVersion = "1.1";
            }
            Document docResp = null;
            tfactory = TransformerFactory.newInstance();
            SoapTools spt = new SoapTools();
            if (xslRequest != null) {
                try {
              
                    
                    transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslRequest))));

                } catch (TransformerConfigurationException ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                try {
                    if(startPage!=null && limitPage!=null){
                      transformer.setParameter("startPosition", startPage);
                      transformer.setParameter("maxRecords", limitPage); 
                    }

                    transformer.transform(new StreamSource(xmlRequestNamePath), new StreamResult(serviceRequestPath));
                } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                try {
                    if (security.equalsIgnoreCase("true")) {
                        docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(serviceRequestPath)), userAuthentication, passwordAuthentication);
                    } else {
                        docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(serviceRequestPath))/*,soapVersion*/);
                    }
                } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                    response.setStatus(400);
                    new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                    soapFault = true;
                }
            } else {
                Node processingRequest = XmlTools.getElementChild(docProxy.getDocumentElement(), "Request");
                if (processingRequest != null) {
                    if (processingRequest.getFirstChild() != null) {
                        Document documentReq = XmlTools.newDocument();
                        Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                        documentReq.appendChild(payload);
                        System.out.println("SOAP ACTION: " + soapAction);
                        try {
                            if (security.equalsIgnoreCase("true")) {
                                docResp = spt.getSoapCall(serviceUrl, soapAction, documentReq, userAuthentication, passwordAuthentication);
                            } else {
                                docResp = spt.getSoapCall(serviceUrl, soapAction, documentReq/*, soapVersion*/);
                            }
                        } catch (Exception ex) {
                            log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                            ex.printStackTrace();
                            response.setStatus(400);
                            new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                            soapFault = true;
                        }
                    }
                } else {
                    try {
                        if (security.equalsIgnoreCase("true")) {
                            docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(xmlRequestNamePath)), userAuthentication, passwordAuthentication);
                        } else {
                            docResp = spt.getSoapCall(serviceUrl, soapAction, XmlTools.docGenerate(new FileInputStream(xmlRequestNamePath))/*, soapVersion*/);
                        }
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                        response.setStatus(400);
                        new FileOutputStream(serviceResponsePath).write(ex.getMessage().getBytes());
                        soapFault = true;

                    }
                }
            }
            if (!soapFault) {
                try {
                    XmlTools.dumpXML(docResp, serviceResponsePath);
                } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        }
        if (soapFault) {
            XmlTools.copyInputStreamToOutputStream(new FileInputStream(serviceResponsePath), response.getOutputStream());

        } else {

            if (xslResponse != null) {
                try {
                    transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(xslResponse))));
                } catch (TransformerConfigurationException ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                try {
                    transformer.transform(new StreamSource(serviceResponsePath), new StreamResult(proxyResponsePath));

                } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            } else {
                proxyResponsePath = serviceResponsePath;
            }
            String pathResult = "";
            tfactory = TransformerFactory.newInstance();
            if (outputFormat.equalsIgnoreCase("JSON")) {
                try {
                    transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(JSON_XSLT_PATH))));
                    if (iteratorElement != null) {
                        transformer.setParameter("iteratorElement", iteratorElement);
                    }
                } catch (TransformerConfigurationException ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                try {
                    transformer.transform(new StreamSource(proxyResponsePath), new StreamResult(proxyResponseJSONPath));

                    response.setContentType("text/json");
                    pathResult = proxyResponseJSONPath.getAbsolutePath();
                    // XmlTools.copyInputStreamToOutputStream(new FileInputStream(proxyResponseJSONPath), response.getOutputStream());
                } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            } else {
                if (outputFormat.equalsIgnoreCase("JAVASCRIPT")) {
                    try {
                        transformer = tfactory.newTransformer(new StreamSource(new File(getServletContext().getRealPath(JAVASCRIPT_XSLT_PATH))));
                    } catch (TransformerConfigurationException ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                    try {
                        transformer.transform(new StreamSource(proxyResponsePath), new StreamResult(proxyResponseJSPath));
                        response.setContentType("text/javascript");
                        pathResult = proxyResponseJSPath.getAbsolutePath();
                        //XmlTools.copyInputStreamToOutputStream(new FileInputStream(proxyResponseJSPath), response.getOutputStream());
                    } catch (Exception ex) {
                        log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                } else {
                    pathResult = proxyResponsePath.getAbsolutePath();
                    response.setContentType("text/xml;charset=UTF-8");

                }
            }

            if (outputMod.equalsIgnoreCase("REFERENCE")) {
                response.setContentType("text/xml;charset=UTF-8");
                String referenceMessage = "<ProxyRedirectResponse id='" + pathIdRequest + "'>";
                referenceMessage += "<responseUrl>" + (pathResult.substring(logDir.getAbsolutePath().length()/*-pathIdRequest.length()*/ - logService.length() - LOG_SERVICES.length(), pathResult.length())).replaceAll("\\\\", "/") + "</responseUrl>";
                referenceMessage += "</ProxyRedirectResponse>";
                try {
                    response.getOutputStream().write(referenceMessage.getBytes());
                } catch (IOException ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            } else {
                if (ident != null) {
                    if (ident.equalsIgnoreCase("true")) {
                        try {
                            Document docIdent = XmlTools.docGenerate(new FileInputStream(pathResult));
                            XmlTools.indent(docIdent);
                            XmlTools.copyDocToOutputStream(docIdent, new FileOutputStream(pathResult));
                        } catch (ParserConfigurationException ex) {
                            java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SAXException ex) {
                            java.util.logging.Logger.getLogger(ProxyRedirect.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                try {
                    XmlTools.copyInputStreamToOutputStream(new FileInputStream(pathResult), response.getOutputStream());
                } catch (IOException ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        }
    }
}


