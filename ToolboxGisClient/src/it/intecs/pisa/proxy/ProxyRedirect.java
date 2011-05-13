
package it.intecs.pisa.proxy;

import it.intecs.pisa.gisclient.util.AxisSOAPClient;
import it.intecs.pisa.gisclient.util.XmlTools;
import it.intecs.pisa.gisclient.util.OGCUtil;
import it.intecs.pisa.saxon.SaxonURIResolver;
import it.intecs.pisa.saxon.SaxonXSLT;
import it.intecs.pisa.saxon.SaxonXSLTParameter;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.SAXSource;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;

// import log4j packages
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class ProxyRedirect extends HttpServlet {
    public final static String LOG_SERVICES = "log/services";
    public final static String CLIENT_REQUEST_PRFX = "ClientRequest";
    public final static String CLIENT_TAG_REQUEST_PRFX = "TagRequest";
    public final static String SERVICE_REQUEST_PRFX = "ServiceRequest";
    public final static String SERVICE_RESPONSE_PRFX = "ServiceResponse";
    public final static String PROXY_RESPONSE_PRFX = "ProxyResponse";
    public final static String JSON_RESPONSE_PRFX = "ProxyResponseJSON";
    public final static String JS_RESPONSE_PRFX = "ProxyResponseJS";
    public final static String FULLPAGINGINFO_RESPONSE_PRFX = "ProxyResponseFullPagingInfo";
    public final static String JSON_XSLT_PATH = "WEB-INF/classes/it/intecs/pisa/proxy/resources/xsl/xml2json.xslt";
    public final static String JAVASCRIPT_XSLT_PATH = "WEB-INF/classes/it/intecs/pisa/proxy/resources/xsl/xml2js.xslt";


    private final static Logger log = Logger.getLogger(ProxyRedirect.class);

//---------------------------------------------------------------------------
// to be removed when the HTTP connection with the Push server will work correctly
//----------------------------------------------------------------------------
    private static final String INSTANCES_DIR = "/log/instances";
    private static final String REQUEST_FILE = "request.xml";
        private static final String PUSH_NS_URI = "http://www.intecs.it/push";
    private static final String PUSH_NS = "ins";
    private static final String INSTANCES_TAG = "instances";
    private static final String INSTANCE_TAG = "instance";
    private static final String MESSAGE_ID_TAG = "MessageID";
    private static final String RELATES_TO_TAG = "RelatesTo";
    private static final String STATUS_TAG = "Status";
    private static final String STATUS_ATTRIBUTE = "status";
    private static final String STATUS_FILE = "status.xml";
    private static final String DESCRIPTION_TAG = "Description";
    private static final String ARRIVAL_TIME_TAG = "ArrivalTime";
    private static final String SERVICE_INSTANCE_TAG = "ServiceInstance";
    private static final String COMPLETION_TIME_TAG = "CompletionTime";


//---------------------------------------------------------------------------
// Public Methods
//----------------------------------------------------------------------------
    public ServletContext context_ = null;
    public HttpServletRequest requestInfo = null;


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
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           this.requestInfo=request;
           ProxyParametersManager inputsGetReq=new ProxyParametersManager(request, getServletContext().getRealPath(""));
           String serverUrl=inputsGetReq.getServiceUrl();
           String type=inputsGetReq.getEnconde();
           InetAddress addr = InetAddress.getLocalHost();
           String appUrl ="http://"+addr.getHostAddress()+":"+request.getServerPort()+request.getContextPath()+"/";
           String outputFormat=inputsGetReq.getOutputFormat();
           if(inputsGetReq.getPaging().equalsIgnoreCase("")){
            if (!serverUrl.startsWith("http://")) {
                serverUrl=appUrl+serverUrl;
             }
                log.debug("GET param serverUrl:" + serverUrl);

                HttpClient client = new HttpClient();
                if (type != null) {
                    if (type.equalsIgnoreCase("ENCODED")) {
                        serverUrl = OGCUtil.getSLDEncoded(serverUrl);
                        log.debug("ENCODED serverUrl:" + serverUrl);
                    } else if (type.equalsIgnoreCase("ENCODEDFILTER")) {
                        serverUrl = OGCUtil.getFilterEncoded(serverUrl);
                        log.debug("ENCODED FILTER serverUrl:" + serverUrl);
                    }
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


                     if (inputsGetReq.getXslResponseRelativePath() != null) {
                        PipedInputStream pipeInput = null;
                        SaxonXSLT saxonUtil=new SaxonXSLT();
                        SAXSource xsltDoc;
                         try {
                              xsltDoc = new SAXSource(new InputSource(new FileInputStream(getServletContext().getRealPath(inputsGetReq.getXslResponseRelativePath()))));
                              pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(httpget.getResponseBodyAsStream())), xsltDoc, null);
                             } catch (Exception ex) {
                               log.fatal("GisClient -- GET XSLT Response Transform Error: " + ex.getMessage(), ex);
                               System.out.println("GisClient -- GET XSLT Response Transform Error: " + ex.getMessage());
                               ex.printStackTrace();
                             }
                            XmlTools.copyInputStreamToOutputStream(pipeInput, response.getOutputStream());
                    }else{
                        String responseBody = httpget.getResponseBodyAsString().trim();
                        PrintWriter out = response.getWriter();
                        out.print(responseBody);
                        response.flushBuffer();
                    }
                } else {
                    log.error("Unexpected failure: " + httpget.getStatusLine().toString());
                }
                httpget.releaseConnection();
           
          }else{
                // Start Paging
                String seviceLogFolder = getServletContext().getRealPath(ProxyRedirect.LOG_SERVICES + inputsGetReq.getLogService());
                File logDir = new File(seviceLogFolder);
                String pathIdRequest = "/Request_" + inputsGetReq.getId();
                File requestDir;
                String start=null,limit=null;
                requestDir = new File(logDir, pathIdRequest);
            try {
                    Document docProxy = XmlTools.docGenerate(new FileInputStream(new File(requestDir, ProxyRedirect.CLIENT_REQUEST_PRFX + ".xml")));
                    ProxyParametersManager inputsPagingReq=new ProxyParametersManager(docProxy, getServletContext().getRealPath(""));
                    if(inputsGetReq.getStart()!=null && inputsGetReq.getLimit()!=null){
                        start=inputsGetReq.getStart();
                        limit=inputsGetReq.getLimit();
                    }else{
                        if(inputsPagingReq.getFisrtPageResult()!=null && inputsPagingReq.getLastPageResult()!=null){
                            start=inputsPagingReq.getFisrtPageResult();
                            limit=inputsPagingReq.getLastPageResult();
                        }else
                           if(inputsPagingReq.getPageSize()!=null){
                             start="1";
                             limit= ""+(new Integer(inputsPagingReq.getPageSize()));
                           }
                    }
                    if(inputsGetReq.getPaging().equalsIgnoreCase("SOAP"))
                        this.sendSoapRequest(docProxy, inputsPagingReq, start, limit);
                    if(inputsGetReq.getPaging().equalsIgnoreCase("POST"))
                        this.sendHttpPostRequest(docProxy, inputsPagingReq, start, limit);
                    this.responseManager(inputsPagingReq, response, start, limit);


                } catch (Exception ex) {
                   log.fatal("GisClient -- Paging Request Exception: " + ex.getMessage(), ex);
                   System.out.println("GisClient -- Paging Request Exception: " + ex.getMessage());
                   ex.printStackTrace();
                }

                // End paging
          }


    }// doGet



    @Override
    public void doDelete (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

           this.requestInfo=request;
           ProxyParametersManager inputsGetReq=new ProxyParametersManager(request, getServletContext().getRealPath(""));
           String serverUrl=inputsGetReq.getServiceUrl();
           String type=inputsGetReq.getEnconde();
           InetAddress addr = InetAddress.getLocalHost();
           String appUrl ="http://"+addr.getHostAddress()+":"+request.getServerPort()+request.getContextPath()+"/";
           
           if(inputsGetReq.getPaging().equalsIgnoreCase("")){
            if (!serverUrl.startsWith("http://")) {
                serverUrl=appUrl+serverUrl;
             }

                HttpClient client = new HttpClient();
                if (type != null) {
                    if (type.equalsIgnoreCase("ENCODED")) {
                        serverUrl = OGCUtil.getSLDEncoded(serverUrl);
                        log.info("ENCODED serverUrl:" + serverUrl);
                    } else if (type.equalsIgnoreCase("ENCODEDFILTER")) {
                        serverUrl = OGCUtil.getFilterEncoded(serverUrl);
                        log.info("ENCODED FILTER serverUrl:" + serverUrl);
                    }
                }

                serverUrl = serverUrl.replaceAll("<", "%3C");
                serverUrl = serverUrl.replaceAll(">", "%3E");
                serverUrl = serverUrl.replaceAll(" ", "%20");
                serverUrl = serverUrl.replaceAll("\"", "%22");
                serverUrl = serverUrl.replaceAll("#", "%23");


                DeleteMethod httpget = new DeleteMethod(serverUrl);

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
                    response.setContentType(httpget.getResponseHeader("Content-Type").getValue());

                    String responseBody = httpget.getResponseBodyAsString().trim();
                    //response.setContentLength(responseBody.length());
                   // log.info("responseBody:" + responseBody);
                     if (inputsGetReq.getXslResponseRelativePath() != null) {
                        PipedInputStream pipeInput = null;
                        SaxonXSLT saxonUtil=new SaxonXSLT();
                        SAXSource xsltDoc;
                         try {
                              xsltDoc = new SAXSource(new InputSource(new FileInputStream(getServletContext().getRealPath(inputsGetReq.getXslResponseRelativePath()))));
                              pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(new StringReader(responseBody))), xsltDoc, null);
                             } catch (Exception ex) {
                               log.fatal("GisClient -- GET XSLT Response Transform Error: " + ex.getMessage(), ex);
                               System.out.println("GisClient -- GET XSLT Response Transform Error: " + ex.getMessage());
                               ex.printStackTrace();
                             }
                            XmlTools.copyInputStreamToOutputStream(pipeInput, response.getOutputStream());
                    }else{
                        PrintWriter out = response.getWriter();
                        out.print(responseBody);
                        response.flushBuffer();
                    }
                } else {
                    log.error("Unexpected failure: " + httpget.getStatusLine().toString());
                }
                httpget.releaseConnection();
          }
           String[] modelsArray;
           String modelListString="";
           modelListString=modelListString.substring(0,modelListString.length()-1);
           if(modelListString.indexOf(",")!=-1)
				    modelsArray=modelListString.split(",");
				else {
                                    modelsArray=new String[1];
                                    modelsArray[0]= modelListString;
                                }

    }
    /***************************************************************************
     * Process the HTTP Post request
     */
    @SuppressWarnings("empty-statement")
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        this.requestInfo=request;
        InetAddress addr = InetAddress.getLocalHost();
        String appUrl ="http://"+addr.getHostAddress()+":"+request.getServerPort()+request.getContextPath()+"/";
        Document docProxy = null;
        InputStream resp = null;
        Boolean soapFault = false;
        ProxyParametersManager inputsMan=null;
        try {
            docProxy = XmlTools.docGenerate(request.getInputStream());
            inputsMan = new ProxyParametersManager(docProxy, getServletContext().getRealPath(""));
        } catch (Exception ex) {
            log.debug("GisClient Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        /*Dump Xml Key Value Client Request*/
        try {
            XmlTools.dumpXML(docProxy, inputsMan.getXmlKeyValueRequestFile(), true);
        } catch (Exception ex) {
            log.debug("GisClient Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        if (!inputsMan.getOutputMod().equalsIgnoreCase("PAGING") &&
            !inputsMan.getOutputMod().equalsIgnoreCase("FULLPAGING")    ) {

            if (inputsMan.getProtocol().equalsIgnoreCase("HTTPPOST")) {
                // Http Post Request
                try {
                    this.sendHttpPostRequest(docProxy, inputsMan, null, null);
                } catch (Exception ex) {
                    log.fatal("GisClient -- Send HTTP POST Exception: " + ex.getMessage(), ex);
                    System.out.println("GisClient -- Send HTTP POST Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }

            } else
                {
                    // SOAP Request
                    //System.out.println("SOAP Request...");
                    try {
                        soapFault = this.sendSoapRequest(docProxy, inputsMan, null, null);
                    } catch (Exception ex) {
                        log.fatal("GisClient -- Send SOAP Request Exception: " + ex.getMessage(), ex);
                        System.out.println("GisClient -- Send SOAP Request Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

            if (soapFault) { // delete instance if not it has been created
                response.setStatus(400);
                XmlTools.copyInputStreamToOutputStream(inputsMan.getXmlServiceResponseFileIn(), response.getOutputStream());
            } else {
                try {
                    //Generate Proxy Response
                    this.responseManager(inputsMan, response, null, null);
                } catch (Exception ex) {
                        log.fatal("GisClient -- Generate Response Exception: " + ex.getMessage(), ex);
                        System.out.println("GisClient -- Generate Response Exception: " + ex.getMessage());
                        ex.printStackTrace();
               }

            }
      }else {
             String pathIdRequest=""+inputsMan.getIdRequest().hashCode();
             if (inputsMan.getOutputMod().equalsIgnoreCase("PAGING")){
                response.setContentType("text/xml;charset=UTF-8");
                String referenceMessage = "<ProxyRedirectResponse id='" + pathIdRequest + "'>";
                referenceMessage += "<responseUrl>" + this.getServletName() + "?id="
                        + pathIdRequest
                        + "&amp;LogFolder=" + inputsMan.getLogService()
                        + "&amp;paging="+ inputsMan.getProtocol()
                        + "</responseUrl>";
                referenceMessage += "</ProxyRedirectResponse>";
                try {
                    response.getOutputStream().write(referenceMessage.getBytes());
                } catch (Exception ex) {
                       log.fatal("GisClient -- POST Paging Request Exception: " + ex.getMessage(), ex);
                       System.out.println("GisClient -- POST Paging Request Exception: " + ex.getMessage());
                       ex.printStackTrace();
               }
            }else
               if(inputsMan.getOutputMod().equalsIgnoreCase("FULLPAGING")){
                   Document fullPaginInfoXML=null;
                   Element fullPaginInfoElemet=null;
                   int i, pageSize=0, totalPagingRecors=0;


                   String getRequestFirstPage= appUrl+this.getServletName()
                        + "?id="+pathIdRequest
                        + "&LogFolder=" + inputsMan.getLogService()
                        + "&paging="+ inputsMan.getProtocol();
                   if(inputsMan.getPageSize()!=null)
                       pageSize=new Integer(inputsMan.getPageSize());
                   HttpClient client = new HttpClient();
                   GetMethod httpget = new GetMethod(getRequestFirstPage);
                   client.executeMethod(httpget);
                   if (httpget.getStatusCode() == HttpStatus.SC_OK) {
                       GetMethod httpgetPaging = null;
                       String pagingUrl="";
                       NodeList vlNodeList;
                       List <String> vlList=new ArrayList<String>();
                       int u;
                        String responseBody = httpget.getResponseBodyAsString().trim();
                        try {
                            fullPaginInfoXML = XmlTools.stringToDom(responseBody);
                            fullPaginInfoElemet=XmlTools.getElementByXPath("response/results", fullPaginInfoXML);
                            totalPagingRecors=new Integer(fullPaginInfoElemet.getTextContent());
                            vlNodeList=fullPaginInfoXML.getElementsByTagName("valueList");
                            for(u=0; u<vlNodeList.getLength(); u++){
                               vlList.add(u,"["+vlNodeList.item(u).getTextContent());
                            }
                            for(i=pageSize+1; i<=totalPagingRecors; i=i+pageSize){
                                pagingUrl=appUrl+this.getServletName()
                                    + "?id="+ pathIdRequest
                                    + "&LogFolder=" + inputsMan.getLogService()
                                    + "&paging="+ inputsMan.getProtocol()
                                    + "&start="+ i
                                    + "&limit="+ pageSize;
                                httpgetPaging=new GetMethod(pagingUrl);
                                client.executeMethod(httpgetPaging);
                                if (httpgetPaging.getStatusCode() == HttpStatus.SC_OK) {
                                    responseBody = httpgetPaging.getResponseBodyAsString().trim();
                                    fullPaginInfoXML = XmlTools.stringToDom(responseBody);
                                    vlNodeList=fullPaginInfoXML.getElementsByTagName("valueList");

                                    for(u=0; u<vlNodeList.getLength(); u++){
                                        vlList.set(u, vlList.get(u)+vlNodeList.item(u).getTextContent());
                                    }
                                }
                            }
                            for(u=0; u<vlList.size(); u++){
                                vlList.set(u, ((String)vlList.get(u)).substring(0, ((String)vlList.get(u)).length()-1)+"]");
                            }
                            response.setContentType("text/xml;charset=UTF-8");
                            String referenceMessage = "<ProxyRedirectResponse id='" + pathIdRequest + "'>";
                            referenceMessage += "<pathFullPaginig>" + inputsMan.getLogService()+"/Request_"+pathIdRequest + "</pathFullPaginig>";
                            referenceMessage += "<totalRecords>" + totalPagingRecors + "</totalRecords>";
                            referenceMessage += "<pageSize>" + pageSize + "</pageSize>";
                            referenceMessage += "<pageValueLists>";
                                for(u=0; u<vlList.size(); u++)
                                   referenceMessage += "<valueList>" + (String)vlList.get(u) + "</valueList>";
                            referenceMessage += "</pageValueLists>";
                            referenceMessage += "</ProxyRedirectResponse>";
                            response.getOutputStream().write(referenceMessage.getBytes());
                        } catch (Exception ex) {
                            log.fatal("GisClient -- POST FULL PAGING Request Unexpected failure: " + ex.getMessage(), ex);
                            System.out.println("GisClient -- POST FULL PAGING Request Unexpected failure: " + ex.getMessage());
                        }
                   } else {
                        log.error("GisClient -- POST FULL PAGING Request Unexpected failure: " + httpget.getStatusLine().toString());
                        System.out.println("GisClient -- POST FULL PAGING Request Unexpected failure: " +  httpget.getStatusLine().toString());
                   }
                httpget.releaseConnection();




               }
        }
    }


    private void sendHttpPostRequest(Document docRequest, ProxyParametersManager httpPostInputs, String startPage, String limitPage) throws Exception{
        OutputStream connOut = null;
        HttpURLConnection connection = null;

        InetAddress addr = InetAddress.getLocalHost();
        String appUrl ="http://"+addr.getHostAddress()+":"+this.requestInfo.getServerPort()+this.requestInfo.getContextPath()+"/";
        String serviceURL="";

        SAXSource xsltDoc=null;
        PipedInputStream pipeInput = null;
        SaxonXSLTParameter [] parameters= null;
        SaxonXSLT saxonUtil=new SaxonXSLT();

        if(httpPostInputs.getServiceUrl().contains("http://"))
            serviceURL=httpPostInputs.getServiceUrl();
        else
            serviceURL=appUrl+httpPostInputs.getServiceUrl();


        connection = (HttpURLConnection) new URL(serviceURL).openConnection();
        connection.setDoOutput(true);
        connection.setAllowUserInteraction(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "text/xml");
        if (httpPostInputs.getSoapAction() != null)
              connection.setRequestProperty("SOAPAction", httpPostInputs.getSoapAction());

        FileInputStream xslRequest=httpPostInputs.getXSLRequestFileIn();
        if (xslRequest!= null) {
             xsltDoc= new SAXSource(new InputSource(xslRequest));
             if(startPage!=null && limitPage!=null){
              parameters=new SaxonXSLTParameter[2];
              parameters[0]=new SaxonXSLTParameter("startPosition", startPage);
              parameters[1]=new SaxonXSLTParameter("maxRecords", limitPage);
             }

             pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(httpPostInputs.getXmlKeyValueRequestFileIn())),
                                                        xsltDoc, null);
             XmlTools.copyInputStreamToOutputStream(pipeInput, httpPostInputs.getXmlServiceRequestFileOut());
             XmlTools.copyInputStreamToOutputStream(pipeInput, connection.getOutputStream());
        } else
            {
             Node processingRequest = XmlTools.getElementChild(docRequest.getDocumentElement(), "Request");
             if (processingRequest != null){

                if (processingRequest.getFirstChild() != null) {
                   Document documentReq = XmlTools.newDocument();
                   Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                   documentReq.appendChild(payload);

                   /*Dump Xml Service Request content in the TAG Request*/
                   File reqTag=httpPostInputs.getXmlTagRequestFile();
                   XmlTools.dumpXML(documentReq, httpPostInputs.getXmlTagRequestFile());

                   connection.setRequestProperty("Content-length", Long.toString(reqTag.length()));
                   connOut = connection.getOutputStream();
                   XmlTools.copyInputStreamToOutputStream(new FileInputStream(reqTag), connOut);
                   connOut.close();
                }else {
                       File fINSttr = httpPostInputs.getXSLRequestFile();
                       connection.setRequestProperty("Content-length", Long.toString(fINSttr.length()));
                       connOut = connection.getOutputStream();
                       XmlTools.copyInputStreamToOutputStream(new FileInputStream(fINSttr), connOut);
                       connOut.close();
                    }
              }else{
                    processingRequest = XmlTools.getElementChild(docRequest.getDocumentElement(), "PostMessage");     
                    String postmessage="";
                    if(processingRequest != null){
                        postmessage=XmlTools.getStringFromElement((Element) processingRequest);
                        postmessage=postmessage.replaceAll("<PostMessage>", "");
                        postmessage=postmessage.replaceAll("</PostMessage>", "");
                        connection.setRequestProperty("Content-length", Long.toString(postmessage.length()));
                        connOut = connection.getOutputStream();
                        XmlTools.copyInputStreamToOutputStream(new ByteArrayInputStream(postmessage.getBytes()), connOut);
                        connOut.close();
                    }
              }
            }
          InputStream resp = connection.getInputStream();
          if(startPage!=null && limitPage!=null)
            XmlTools.copyInputStreamToOutputStream(resp, httpPostInputs.getXmlServiceResponseFileOut(startPage,limitPage));
          else
            XmlTools.copyInputStreamToOutputStream(resp, httpPostInputs.getXmlServiceResponseFileOut());
    }


    private Boolean sendSoapRequest(Document docRequest, ProxyParametersManager httpPostInputs, String startPage, String limitPage) throws Exception{
       Document docResp = null;
       InetAddress addr = InetAddress.getLocalHost();
       String appUrl ="http://"+addr.getHostAddress()+":"+this.requestInfo.getServerPort()+this.requestInfo.getContextPath()+"/";

      /*SaxonURIResolver uriResolver=null;
      if(httpPostInputs.getXslUriResolver() != null)
           uriResolver = new SaxonURIResolver(new URL(appUrl+httpPostInputs.getXslUriResolver()));
        else
           uriResolver = new SaxonURIResolver(new URL(appUrl));
       SaxonXSLT saxonUtil=new SaxonXSLT(uriResolver);*/
       SaxonXSLT saxonUtil=new SaxonXSLT();
       SAXSource xsltDoc=null;
       PipedInputStream pipeInput = null;
       String messageID = java.util.UUID.randomUUID().toString();
       Element respElement = null;
       Boolean soapFault=false;
       SaxonXSLTParameter [] parameters= null;

       FileInputStream xslRequest=httpPostInputs.getXSLRequestFileIn();
       Document documentReq=null;
       if (xslRequest != null) {
           xsltDoc= new SAXSource(new InputSource(xslRequest));
           if(startPage!=null && limitPage!=null){
              parameters=new SaxonXSLTParameter[3];
              parameters[0]=new SaxonXSLTParameter("startPosition", startPage);
              parameters[1]=new SaxonXSLTParameter("maxRecords", limitPage);
              parameters[2]=new SaxonXSLTParameter("applicationURL", appUrl);
           }else{
              parameters=new SaxonXSLTParameter[1];
              parameters[0]=new SaxonXSLTParameter("applicationURL", appUrl);
           }
           pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(httpPostInputs.getXmlKeyValueRequestFileIn())),
                                                        xsltDoc, parameters);
           XmlTools.copyInputStreamToOutputStream(pipeInput, httpPostInputs.getXmlServiceRequestFileOut());
           documentReq = XmlTools.docGenerate(httpPostInputs.getXmlServiceRequestFileIn());
       }
        else
          {
           Node processingRequest = XmlTools.getElementChild(docRequest.getDocumentElement(), "Request");
           if (processingRequest != null)
               if (processingRequest.getFirstChild() != null) {
                   documentReq = XmlTools.newDocument();
                   Node payload = documentReq.importNode(XmlTools.getFirstChild((Element) processingRequest), true);
                   documentReq.appendChild(payload);
                   XmlTools.dumpXML(documentReq, httpPostInputs.getXmlServiceRequestFile());
               }
          }
        try {
             if (httpPostInputs.getAsynchronous().equalsIgnoreCase("true")) {
                 // In this veersion we create the folder locally not via the Push manager interface
                 
                 // this.createAsynchronousInstance(messageID, documentReq, WSAManager);
                 respElement = AxisSOAPClient.sendReceive(new URL(httpPostInputs.getServiceUrl()),
                 documentReq, httpPostInputs.getSoapAction(), messageID, httpPostInputs.getReplyTo());
                 docResp = respElement.getOwnerDocument();
                 String serviceInstance="";
                 NodeList nl=docResp.getElementsByTagName("processingInstance");
                 if(nl.getLength()>0)
                   serviceInstance="Instances/"+nl.item(0).getTextContent();


                 this.createAsynchronousInstance(messageID, serviceInstance, documentReq, httpPostInputs.getDescription());
             } else {
                  respElement = AxisSOAPClient.sendReceive(new URL(httpPostInputs.getServiceUrl()),
                  XmlTools.docGenerate(httpPostInputs.getXmlServiceRequestFileIn()).getDocumentElement(),
                             httpPostInputs.getSoapAction());
                  docResp = respElement.getOwnerDocument();
             }
           } catch (Exception ex) {
                    log.fatal("GisClient Exception: " + ex.getMessage(), ex);
                    ex.printStackTrace();
                   // response.setStatus(400);
                    httpPostInputs.getXmlServiceResponseFileOut().write(ex.getMessage().getBytes());
                    soapFault = true;
          }

          if (!soapFault) {
               // try {
                    if (httpPostInputs.getAsynchronous().equalsIgnoreCase("true")) {
                        // to be handled
                    }
                    if(startPage!=null && limitPage!=null)
                        XmlTools.dumpXML(docResp, httpPostInputs.getXmlServiceResponseFileOut(startPage,limitPage));
                    else
                        XmlTools.dumpXML(docResp, httpPostInputs.getXmlServiceResponseFile());
            }
       return soapFault;
    }



    // This should be replaced with the connection with the Push Server via HTTP

   public void createAsynchronousInstance(String messageId, String serverInstance, Document requestMessage, String description) throws Exception {
        try {
            File root = new File(getServletContext().getRealPath(INSTANCES_DIR));
            String modifiedMesssageId;
            modifiedMesssageId = getModifiedMessageId(messageId);
            File instanceDir = new File(root, modifiedMesssageId);
            instanceDir.mkdirs();
            String descriptionString = (description != null) ? description : "No description available";

            Document statusDocument = XmlTools.newDocument();
            Element statusNode = statusDocument.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + STATUS_TAG);
            statusDocument.appendChild(statusNode);

            Element serverInstanceNode = statusDocument.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + SERVICE_INSTANCE_TAG);
            Text content = statusDocument.createTextNode(serverInstance);
            serverInstanceNode.appendChild(content);
            statusNode.appendChild(serverInstanceNode);
            

            Element descriptionNode = statusDocument.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + DESCRIPTION_TAG);

            content = statusDocument.createTextNode(descriptionString);
            descriptionNode.appendChild(content);
            statusNode.appendChild(descriptionNode);

            Element arrivalTimeNode = statusDocument.createElementNS(PUSH_NS_URI, PUSH_NS + ":" + ARRIVAL_TIME_TAG);
           
            content = statusDocument.createTextNode(getNow("yyyy-MM-dd'T'HH:mm:ss"));
            arrivalTimeNode.appendChild(content);
            statusNode.appendChild(arrivalTimeNode);

            XmlTools.dumpXML(statusDocument, new File(instanceDir, STATUS_FILE), true);
            XmlTools.dumpXML(requestMessage, new File(instanceDir, REQUEST_FILE), true);
        } catch (Exception ex) {
            log.debug("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }





    private String responseManager(ProxyParametersManager parameters, HttpServletResponse response, String startPage, String limitPage) throws Exception{
        FileInputStream xslResponse=null;
        String extractValue=null;
       SaxonURIResolver uriResolver=null;
       String startJsonPage="-1";
       String sizeJsonPage="-1";
       InetAddress addr = InetAddress.getLocalHost();
        String appUrl ="http://"+addr.getHostAddress()+":"+this.requestInfo.getServerPort()+this.requestInfo.getContextPath()+"/";

      if(parameters.getXslUriResolver() != null)
           uriResolver = new SaxonURIResolver((appUrl+parameters.getXslUriResolver()));
        else
          // uriResolver = new SaxonURIResolver((appUrl));
            uriResolver = new SaxonURIResolver(parameters.getXmlKeyValueRequestFile().getParentFile());


        SaxonXSLT saxonUtil=new SaxonXSLT(uriResolver);
        SAXSource xsltDoc=null;
        String valueListMessage;
        FileInputStream serviceResponse, proxyResponseIn;
        FileOutputStream proxyResponseOut,jsonOut, javascriptOut;
        PipedInputStream pipeInput = null;
        if(startPage!=null && limitPage!=null){
            serviceResponse=parameters.getXmlServiceResponseFileIn(startPage,limitPage);
            proxyResponseOut=parameters.getXmlProxyResponseFileOut(startPage,limitPage);
        }
        else{
            serviceResponse=parameters.getXmlServiceResponseFileIn();
            proxyResponseOut=parameters.getXmlProxyResponseFileOut();
        }

        xslResponse = parameters.getXSLResponseFileIn();
        if (xslResponse != null) {
            SaxonXSLTParameter [] xsltParameters;
            if(startPage!=null && limitPage!=null){
              xsltParameters=new SaxonXSLTParameter[5];
              xsltParameters[0]=new SaxonXSLTParameter("startPosition", startPage);
              xsltParameters[1]=new SaxonXSLTParameter("maxRecords", limitPage);
              xsltParameters[2]=new SaxonXSLTParameter("applicationURL", appUrl);
              xsltParameters[3]=new SaxonXSLTParameter("orderBy", parameters.getOrderBy());
              xsltParameters[4]=new SaxonXSLTParameter("order", parameters.getOrder());
           }else{
              xsltParameters=new SaxonXSLTParameter[1];
              xsltParameters[0]=new SaxonXSLTParameter("applicationURL", appUrl);
             // xsltParameters[1]=new SaxonXSLTParameter("orderBy", parameters.getOrderBy());
           }
            xsltDoc= new SAXSource(new InputSource(xslResponse));
            pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(serviceResponse)), xsltDoc, xsltParameters);
            XmlTools.copyInputStreamToOutputStream(pipeInput, proxyResponseOut);
        } else{
               if(startPage != null && limitPage!= null){
                  startJsonPage=startPage;
                  sizeJsonPage=limitPage;
               }
              XmlTools.copyInputStreamToOutputStream(serviceResponse,proxyResponseOut);
            }
            

        proxyResponseOut.close();
        serviceResponse.close();

        if(startPage!=null && limitPage!=null)
            proxyResponseIn=parameters.getXmlProxyResponseFileIn(startPage,limitPage);
        else
            proxyResponseIn=parameters.getXmlProxyResponseFileIn();



        String pathResult = "";

        if (parameters.getOutputFormat().equalsIgnoreCase("JSON")) {
           if(startPage!=null && limitPage!=null)
              jsonOut=parameters.getXmlProxyJSONResponseFileOut(startPage, limitPage);
           else
              jsonOut=parameters.getXmlProxyJSONResponseFileOut();
            SaxonXSLTParameter[] saxParameters=new SaxonXSLTParameter[2];
            saxParameters[0]=new SaxonXSLTParameter("startPage", startJsonPage);
            saxParameters[1]=new SaxonXSLTParameter("sizePage", sizeJsonPage);
           xsltDoc= new SAXSource(new InputSource(new FileInputStream(getServletContext().getRealPath(JSON_XSLT_PATH))));
           pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(proxyResponseIn)),
                                                        xsltDoc, null);
           XmlTools.copyInputStreamToOutputStream(pipeInput, jsonOut);
           response.setContentType("text/json");
           if(startPage!=null && limitPage!=null)
             pathResult = parameters.getXmlProxyJSONResponseFile(startPage,limitPage).getAbsolutePath();
           else
             pathResult = parameters.getXmlProxyJSONResponseFile().getAbsolutePath();
        } else
            {
             if (parameters.getOutputFormat().equalsIgnoreCase("JAVASCRIPT")) {
                if(startPage!=null && limitPage!=null)
                  javascriptOut=parameters.getXmlProxyJSResponseFileOut(startPage, limitPage);
                else
                  javascriptOut=parameters.getXmlProxyJSResponseFileOut();
                xsltDoc= new SAXSource(new InputSource(new FileInputStream(getServletContext().getRealPath(JAVASCRIPT_XSLT_PATH))));
                pipeInput = saxonUtil.saxonXSLPipeTransform(new SAXSource(new InputSource(proxyResponseIn)),
                                                        xsltDoc, null);
                XmlTools.copyInputStreamToOutputStream(pipeInput, javascriptOut);
                response.setContentType("text/javascript");
                if(startPage!=null && limitPage!=null)
                    pathResult = parameters.getXmlProxyJSResponseFile(startPage,limitPage).getAbsolutePath();
                else
                    pathResult = parameters.getXmlProxyJSResponseFile().getAbsolutePath();

             } else { // Outputformat = XML

                      if(startPage!=null && limitPage!=null)
                            pathResult =  parameters.getXmlProxyResponseFile(startPage,limitPage).getAbsolutePath();
                        else
                            pathResult =  parameters.getXmlProxyResponseFile().getAbsolutePath();
                    response.setContentType("text/xml;charset=UTF-8");
                }
            }
       if(parameters.getValueListXPATH() !=null){
            valueListMessage="<response><valueListPages>";
            Document proxyResponseDoc = XmlTools.docGenerate(parameters.getXmlProxyResponseFileIn());
            valueListMessage+="<results>"+XmlTools.getElementByXPath("response/results", proxyResponseDoc)+"</results>";
            String [] xpathArray= parameters.getValueListXPATH().split(",");
            if(xpathArray.length == 0){
              Element valueListElemet=XmlTools.getElementByXPath(parameters.getValueListXPATH(), proxyResponseDoc);
              valueListMessage+="<valueList>"+valueListElemet.getTextContent()+"</valueList>";
            }else
              for(int kk=0; kk< xpathArray.length; kk++){
                 Element valueListElemet=XmlTools.getElementByXPath(xpathArray[kk], proxyResponseDoc);
                 valueListMessage+="<valueList>"+valueListElemet.getTextContent()+"</valueList>";
              }
            valueListMessage+="</valueListPages></response>";
            response.getOutputStream().write(valueListMessage.getBytes());
       }else
           if (parameters.getOutputMod().equalsIgnoreCase("REFERENCE")) {
                    response.setContentType("text/xml;charset=UTF-8");
                    String referenceMessage = "<ProxyRedirectResponse id='" + "pathIdRequest" + "'>";
                    referenceMessage += "<responseUrl>" + (pathResult.substring(parameters.getLogRealPath().length() - parameters.getLogService().length() - LOG_SERVICES.length(), pathResult.length())).replaceAll("\\\\", "/") + "</responseUrl>";
                    referenceMessage += "</ProxyRedirectResponse>";
                    response.getOutputStream().write(referenceMessage.getBytes());
            }else{
                if (parameters.getIdent().equalsIgnoreCase("true")) {
                    Document docIdent = XmlTools.docGenerate(new FileInputStream(pathResult));
                    XmlTools.indent(docIdent);
                    XmlTools.copyDocToOutputStream(docIdent, new FileOutputStream(pathResult));
                }
                XmlTools.copyInputStreamToOutputStream(new FileInputStream(pathResult), response.getOutputStream());
            }
       response.getOutputStream().close();
       return extractValue;
    }

    private String getNow(String format) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        return formatter.format(new Date()).toString();

    }



    private void dumpMessage(Document message, File absoluteFile) {
        try {
            XmlTools.dumpXML(message, absoluteFile, true);
        } catch (Exception e) {
        }
    }

    public String getModifiedMessageId(String messageId) {
        return messageId.replaceAll("[/\\\\:*?<>|]", "_");
    }

    public void createAsynchronousInstance(String messageId, Document requestMessage, String WSAManager, String description) throws Exception {

        try {
            OutputStream connOut = null;
            HttpURLConnection connection = (HttpURLConnection) new URL(WSAManager).openConnection();
            connection.setDoOutput(true);
            connection.setAllowUserInteraction(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "text/xml");
            connOut = connection.getOutputStream();
            XmlTools.copyInputStreamToOutputStream(XmlTools.getNodeAsInputStream((Node) requestMessage.getDocumentElement()), connOut);
            connOut.close();

        } catch (Exception ex) {
            log.debug("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}


