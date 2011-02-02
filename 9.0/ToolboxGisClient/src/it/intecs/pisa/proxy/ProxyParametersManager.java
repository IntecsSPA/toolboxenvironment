
package it.intecs.pisa.proxy;

import it.intecs.pisa.gisclient.util.XmlTools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;

/**
 *
 * @author Andrea Marongiu
 */
public class ProxyParametersManager {

    private static final String INPUT_SERVICEURL="ServiceUrl";
    private static final String INPUT_PROTOCOL="Protocol";
    private static final String INPUT_XSLREQUEST="XSLRequest";
    private static final String INPUT_XSLRESPONSE="XSLResponse";
    private static final String INPUT_XSLURIRESOLVER="XSLUriResolver";
    private static final String INPUT_XSLFULLPAGING="XSLFullPagingInfo";
    private static final String INPUT_FULLPAGINGPAGESIZE="FullPagingInfoPageSize";
    private static final String INPUT_VALUELISTXPATH="ValueListXPATH";
    private static final String INPUT_LOGFOLDER="LogFolder";
    private static final String INPUT_IDENT="Ident";
    private static final String INPUT_SOAPACTION="SoapAction";
    private static final String INPUT_OUTPUTFORMAT="outputFormat";
    private static final String INPUT_OUTPUTMOD="outputMod";
    private static final String INPUT_IDREQUEST="idRequest";
    private static final String INPUT_SOAPVERSION="soapVersion";
    private static final String INPUT_REPLAYTO="replyTo";
    private static final String INPUT_WSAMAGER="WSAManager";
    private static final String INPUT_ORDER_BY="orderBy";
    private static final String INPUT_ORDER="order";
    private static final String INPUT_ASYNC="asynchronous";
    private static final String INPUT_DESCRIPTION="description";
    private static final String INPUT_FIRSTPAGERESULT="FisrtPageResult";
    private static final String INPUT_LASTPAGERESULT="LastPageResult";



    // Get Paramters
    private static final String INPUT_GET_ENCODE="type";
    private static final String INPUT_GET_SERVICEURL="url";
    private static final String INPUT_GET_OUTPUTFORMAT="outFormat";
    private static final String INPUT_GET_PAGING="paging";
    private static final String INPUT_GET_START="start";
    private static final String INPUT_GET_LIMIT="limit";
    private static final String INPUT_GET_ID="id";

    private final static String LOG_SERVICES = "/log/services";


    // General Parameters
    private String serviceUrl;
    private String protocol;
    private String xslRequest;
    private String xslResponse;
    private String xslUriResolver;
    private String xslFullPagingInfo;
    private String valueListXPATH;
    private String pageSize;
    private String logService;
    private String logRootRealPath;
    private String ident;
    private String soapAction;
    private String outputFormat;
    private String orderBy;
    private String order;
    private String outputMod;
    private String idRequest;
    private String contextPath;
    private String fisrtPageResult;
    private String lastPageResult;

    //Soap Paramters
    private String replyTo;
    private String WSAManager;
    private String soapVersion;
    private String asynchronous;
    private String description;

    //Get Paramters
    private String enconde;
    private String otherParamters;
    private String paging;
    private String start;
    private String limit;
    private String id;
    private String xslResponseRelativePath;

    private String logRealPath;
    private File logRequestDir;

    public ProxyParametersManager (Document docProxy, String contextPath) throws Exception{

        this.serviceUrl = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_SERVICEURL);
            if (this.serviceUrl == null)
                this.serviceUrl = "";
        this.protocol= XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_PROTOCOL);
            if (this.protocol == null)  
                this.protocol = "HTTPPOST";
            else
               if (this.protocol.equalsIgnoreCase("SOAP"))
                  this.setSoapParameters(docProxy);

        this.xslRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLREQUEST);
        this.xslResponse = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLRESPONSE);
        this.xslResponse = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLRESPONSE);
        this.xslUriResolver = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLURIRESOLVER);
        this.logService = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_LOGFOLDER);
        this.ident = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_IDENT);
            if (this.ident == null) this.ident = "false";
        this.soapAction = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_SOAPACTION);
        this.outputFormat = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_OUTPUTFORMAT);
            if (this.outputFormat == null) this.outputFormat = "XML";
        this.outputMod = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_OUTPUTMOD);
            if (this.outputMod == null) this.outputMod = "VALUE";
            else
                if (this.outputMod.equalsIgnoreCase("PAGING")){
                    this.fisrtPageResult = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_FIRSTPAGERESULT);
                    this.lastPageResult = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_LASTPAGERESULT);
                    this.orderBy = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_ORDER_BY);
                    this.order = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_ORDER);
                }else
                   if (this.outputMod.equalsIgnoreCase("FULLPAGING")){
                    this.xslFullPagingInfo = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLFULLPAGING);
                    this.pageSize= XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_FULLPAGINGPAGESIZE);
                    this.valueListXPATH= XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_VALUELISTXPATH);
                    this.orderBy = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_ORDER_BY);
                    this.order = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_ORDER);
                   }
        this.idRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_IDREQUEST);
        this.logRealPath=contextPath+ LOG_SERVICES+ this.logService;
        this.logRootRealPath=contextPath;
        this.contextPath=contextPath+"/";
        this.createLogRequest();

    }

    public ProxyParametersManager (HttpServletRequest getRequest, String contextPath) {
            String key = "";
            this.otherParamters = "";
            this.idRequest=
            this.enconde = getRequest.getParameter(INPUT_GET_ENCODE);
            this.serviceUrl = getRequest.getParameter(INPUT_GET_SERVICEURL);
            if (this.serviceUrl == null)
                this.serviceUrl = "";
            this.outputFormat = getRequest.getParameter(INPUT_GET_OUTPUTFORMAT);
            this.xslResponseRelativePath = getRequest.getParameter(INPUT_XSLRESPONSE);
            this.paging = getRequest.getParameter(INPUT_GET_PAGING);
            if (this.paging == null)
                this.paging = "";
            else
                this.setPagingParameters(getRequest);

            Enumeration e = getRequest.getParameterNames();
            while (e.hasMoreElements()) {
                key = (String) e.nextElement();
                if (!(key.equals(INPUT_GET_ENCODE) || key.equals(INPUT_GET_SERVICEURL) || key.equals(INPUT_GET_OUTPUTFORMAT))) {
                    this.otherParamters += "&" + key + "=" + getRequest.getParameter(key);
                }
            }

            
        /*this.serviceUrl = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_SERVICEURL);
        this.protocol= XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_PROTOCOL);
            if (this.protocol == null)
                this.protocol = "HTTPPOST";
            else
               if (this.protocol.equalsIgnoreCase("SOAP"))
                  this.setSoapParameters(docProxy);

        this.xslRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLREQUEST);
        this.xslResponse = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_XSLRESPONSE);
        this.logService = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_LOGFOLDER);
        this.ident = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_IDENT);
        this.soapAction = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_SOAPACTION);
        this.outputFormat = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_OUTPUTFORMAT);
            if (this.outputFormat == null) this.outputFormat = "XML";
        this.outputMod = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_OUTPUTMOD);
            if (this.outputMod == null) this.outputMod = "VALUE";
        this.idRequest = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_IDREQUEST);
        this.logRealPath=contextPath+ LOG_SERVICES+ this.logService;
        this.contextPath=contextPath+"/";
        this.createLogRequest();*/

    }


    public File getXmlKeyValueRequestFile(){
        return new File(logRequestDir, ProxyRedirect.CLIENT_REQUEST_PRFX + ".xml");
    }

    public FileInputStream getXmlKeyValueRequestFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlKeyValueRequestFile());
    }

    public File getXmlServiceRequestFile(){
        return new File(logRequestDir, ProxyRedirect.SERVICE_REQUEST_PRFX + ".xml");
    }

    public FileOutputStream getXmlServiceRequestFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlServiceRequestFile());
    }

    public FileInputStream getXmlServiceRequestFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlServiceRequestFile());
    }

    public File getXmlServiceResponseFile(){
        return new File(logRequestDir, ProxyRedirect.SERVICE_RESPONSE_PRFX + ".xml");
    }

    public FileOutputStream getXmlServiceResponseFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlServiceResponseFile());
    }

    public FileInputStream getXmlServiceResponseFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlServiceResponseFile());
    }

    public File getXmlServiceResponseFile(String startPaging, String limitPaging){
        int endPage=new Integer(startPaging)+new Integer(limitPaging);
        return new File(logRequestDir, ProxyRedirect.SERVICE_RESPONSE_PRFX + "_" + (new Integer(startPaging)) + "_" + (endPage-1) +".xml");
    }

    public FileOutputStream getXmlServiceResponseFileOut(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileOutputStream(this.getXmlServiceResponseFile(startPaging,limitPaging));
    }

    public FileInputStream getXmlServiceResponseFileIn(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileInputStream(this.getXmlServiceResponseFile(startPaging,limitPaging));
    }


    public File getXmlTagRequestFile(){
        return new File(logRequestDir, ProxyRedirect.CLIENT_TAG_REQUEST_PRFX + ".xml");
    }
   

    public File getXSLRequestFile(){
        if(this.xslRequest == null)
            return null;
        else
            return new File(this.contextPath+this.xslRequest);
    }

    public FileInputStream getXSLRequestFileIn() throws Exception{
        if(this.xslRequest == null)
            return null;
        else
            return new FileInputStream(this.contextPath+this.xslRequest);
    }

    public File getXSLResponseFile(){
        if(this.xslResponse == null)
            return null;
        else
            return new File(this.contextPath+this.xslResponse);
    }

    public FileInputStream getXSLResponseFileIn() throws Exception{
        if(this.xslResponse == null)
            return null;
        else
            return new FileInputStream(this.contextPath+this.xslResponse);
    }

    public File getXSLFullPagingInfoFile(){
        if(this.xslFullPagingInfo == null)
            return null;
        else
            return new File(this.contextPath+this.xslFullPagingInfo);
    }

    public FileInputStream getXSLFullPagingInfoFileIn() throws Exception{
        if(this.xslFullPagingInfo == null)
            return null;
        else
            return new FileInputStream(this.contextPath+this.xslFullPagingInfo);
    }


    public File getXmlProxyResponseFile(){
        return new File(logRequestDir, ProxyRedirect.PROXY_RESPONSE_PRFX + ".xml");
    }

    public FileInputStream getXmlProxyResponseFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyResponseFile());
    }

    public FileOutputStream getXmlProxyResponseFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyResponseFile());
    }

    public File getXmlProxyResponseFile(String startPaging, String limitPaging){
        int endPage=new Integer(startPaging)+new Integer(limitPaging);
        return new File(logRequestDir, ProxyRedirect.PROXY_RESPONSE_PRFX + "_" + (new Integer(startPaging)) + "_" + (endPage-1) +".xml");
    }

    public FileInputStream getXmlProxyResponseFileIn(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyResponseFile(startPaging,limitPaging));
    }

    public FileOutputStream getXmlProxyResponseFileOut(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyResponseFile(startPaging,limitPaging));
    }

    public File getXmlProxyJSONResponseFile(){
        return new File(logRequestDir, ProxyRedirect.JSON_RESPONSE_PRFX + ".txt");
    }

    public FileInputStream getXmlProxyJSONResponseFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyJSONResponseFile());
    }

    public FileOutputStream getXmlProxyJSONResponseFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyJSONResponseFile());
    }

    public File getXmlProxyJSONResponseFile(String startPaging, String limitPaging){
        int endPage=new Integer(startPaging)+new Integer(limitPaging);
        return new File(logRequestDir, ProxyRedirect.JSON_RESPONSE_PRFX + "_" + (new Integer(startPaging)) + "_" + (endPage-1) + ".txt");
    }

    public FileInputStream getXmlProxyJSONResponseFileIn(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyJSONResponseFile(startPaging,limitPaging));
    }

    public FileOutputStream getXmlProxyJSONResponseFileOut(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyJSONResponseFile(startPaging,limitPaging));
    }

    public File getXmlProxyJSResponseFile(){
        return new File(logRequestDir, ProxyRedirect.JS_RESPONSE_PRFX + ".txt");
    }

    public FileInputStream getXmlProxyJSResponseFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyJSResponseFile());
    }

    public FileOutputStream getXmlProxyJSResponseFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyJSResponseFile());
    }

    public File getXmlProxyJSResponseFile(String startPaging, String limitPaging){
        int endPage=new Integer(startPaging)+new Integer(limitPaging);
        return new File(logRequestDir, ProxyRedirect.JS_RESPONSE_PRFX + "_" + (new Integer(startPaging)) + "_" + (endPage-1) + ".txt");
    }

    public FileInputStream getXmlProxyJSResponseFileIn(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyJSResponseFile(startPaging,limitPaging));
    }

    public FileOutputStream getXmlProxyJSResponseFileOut(String startPaging, String limitPaging) throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyJSResponseFile(startPaging,limitPaging));
    }

    public File getXmlProxyFullPagingInfoResponseFile(){
        return new File(logRequestDir, ProxyRedirect.FULLPAGINGINFO_RESPONSE_PRFX + ".xml");
    }

    public FileInputStream getXmlProxyFullPagingInfoResponseFileIn() throws FileNotFoundException{
        return new FileInputStream(this.getXmlProxyFullPagingInfoResponseFile());
    }

    public FileOutputStream getXmlProxyFullPagingInfoResponseFileOut() throws FileNotFoundException{
        return new FileOutputStream(this.getXmlProxyFullPagingInfoResponseFile());
    }

    private void setSoapParameters(Document docProxy){
        this.replyTo = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_REPLAYTO);
            if (this.getReplyTo() == null) this.replyTo = "";
        this.WSAManager = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_WSAMAGER);
            if (this.WSAManager == null) this.WSAManager = "";
        this.asynchronous = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_ASYNC);
            if (this.getAsynchronous() == null) asynchronous = "false";
        this.description = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_DESCRIPTION);
        this.soapVersion = XmlTools.getElementTextChildValue(docProxy.getDocumentElement(), INPUT_SOAPVERSION);
            if (this.soapVersion == null) this.soapVersion = "1.1";
    }

    private void setPagingParameters(HttpServletRequest getRequest){

        this.start = getRequest.getParameter(INPUT_GET_START);
        this.limit = getRequest.getParameter(INPUT_GET_LIMIT);
        this.id = getRequest.getParameter(INPUT_GET_ID);
        this.logService = getRequest.getParameter(INPUT_LOGFOLDER);

    }


    private void createLogRequest(){
        String pathIdRequest = "/Request_" + this.getIdRequest().hashCode();
        this.logRequestDir = new File(this.getLogRealPath());
        this.logRequestDir.mkdir();
        this.logRequestDir = new File(this.logRequestDir,pathIdRequest);
        this.logRequestDir.mkdir();
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        if(this.otherParamters !=null)
            if (!this.otherParamters.equalsIgnoreCase("")) {
                    if (serviceUrl.contains("?")) {
                        serviceUrl += this.otherParamters;
                    } else {
                        serviceUrl += "?" + this.otherParamters.substring(1, this.otherParamters.length());

                    }
                }
       return serviceUrl;
    }

    /**
     * @return the soapAction
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @return the asynchronous
     */
    public String getAsynchronous() {
        return asynchronous;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the replyTo
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @return the outputMod
     */
    public String getOutputMod() {
        return outputMod;
    }

    /**
     * @return the logRealPath
     */
    public String getLogRealPath() {
        return logRealPath;
    }

   
    /**
     * @return the logService
     */
    public String getLogService() {
        return logService;
    }

    /**
     * @return the ident
     */
    public String getIdent() {
        return ident;
    }

    /**
     * @return the enconde
     */
    public String getEnconde() {
        return enconde;
    }

    /**
     * @return the paging
     */
    public String getPaging() {
        return paging;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @return the limit
     */
    public String getLimit() {
        return limit;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the fisrtPageResult
     */
    public String getFisrtPageResult() {
        return fisrtPageResult;
    }

    /**
     * @return the lastPageResult
     */
    public String getLastPageResult() {
        return lastPageResult;
    }

    /**
     * @return the idRequest
     */
    public String getIdRequest() {
        return idRequest;
    }

    /**
     * @return the pageSize
     */
    public String getPageSize() {
        return pageSize;
    }

    /**
     * @return the valueListXPATH
     */
    public String getValueListXPATH() {
        return valueListXPATH;
    }

    /**
     * @return the xslUriResolver
     */
    public String getXslUriResolver() {
        return xslUriResolver;
    }

    /**
     * @return the logRootRealPath
     */
    public String getLogRootRealPath() {
        return logRootRealPath;
    }

    /**
     * @return the xslResponseRelativePath
     */
    public String getXslResponseRelativePath() {
        return xslResponseRelativePath;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.order = order;
    }

}
