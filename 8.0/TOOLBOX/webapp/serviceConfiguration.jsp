<%@ page language="java"  import="it.intecs.pisa.toolbox.service.*" errorPage="errorPage.jsp" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" /> 
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
String service = request.getParameter("serviceName");
if (service.equals("")) {
%>
<jsp:forward page="main.jsp"/>
<%
}
String viewServiceConfiguration = "viewServiceConfiguration.jsp?serviceName=" + service;
String configureServiceRequest = "configureService.jsp?serviceName=" + service;
String exportServiceDescriptor = "manager?cmd=getServiceDescriptor&&serviceName=" + service;
String viewServiceInfo = "viewServiceInfo.jsp?serviceName=" + service;
String deployAServiceRemotelyRequest = "deployAServiceRemotelyRequest.jsp?serviceName=" + service;
String resetServiceFilesRequest = "manager?cmd=resetServiceFiles&&serviceName=" + service;

PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String home = (String)messages.getObject("serviceConfiguration.home");
String serviceConf = (String)messages.getObject("serviceConfiguration.serviceConf");

String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
        "&nbsp;"+ serviceConf;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR>
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
            <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceConfiguring.html_Configuring_a_Service*");</SCRIPT>
                <DIV class=portletItem id=01>
                    <!-- TITLE part --> 
                    <DIV> 
                        <A href="<%= response.encodeURL(viewServiceConfiguration) %>"><fmt:message key="serviceConfiguration.view" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="<%= response.encodeURL(viewServiceConfiguration) %>">
                            <IMG class=labelHomePage title="view" alt="View current configuration" src="images/configureView.jpg" align=middle border=0> 
                        </A>
                        <fmt:message key="serviceConfiguration.viewDescr" bundle="${lang}"/> <%=service%> <fmt:message key="serviceConfiguration.configuration" bundle="${lang}"/>.
                    </P> 
                </DIV> 
                <DIV class=portletItem id=02> 
                    <DIV> 
                        <A href="<%= response.encodeURL(configureServiceRequest) %>"><fmt:message key="serviceConfiguration.changeConf" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="<%= response.encodeURL(configureServiceRequest) %>">
                            <IMG class=labelHomePage title="change" alt="Change the configuration" src="images/configure.jpg" align=middle border=0>
                        </A>
                        <fmt:message key="serviceConfiguration.confDescr" bundle="${lang}"/> <%=service%> <fmt:message key="serviceConfiguration.configuration" bundle="${lang}"/>.
                        
                    </P> 
                </DIV> 
                <DIV class=portletItem id=03> 
                    <DIV> 
                        <A href="<%= response.encodeURL(exportServiceDescriptor) %>"><fmt:message key="serviceConfiguration.export" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <A href="<%= response.encodeURL(exportServiceDescriptor) %>">    
                            <IMG class=labelHomePage title="export" alt="Export" src="images/export.jpg" align=middle border=0>
                        </A>
                        <fmt:message key="serviceConfiguration.exportDescr1" bundle="${lang}"/> <%=service%> <fmt:message key="serviceConfiguration.service" bundle="${lang}"/>.
                        
                    </P> 
                </DIV> 
                <DIV class=portletItem id=04> 
                    <DIV> 
                        <A href="<%= response.encodeURL(viewServiceInfo) %>">
                            <fmt:message key="serviceConfiguration.serviceInfo" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif">
                        </A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>        
                        <A href="<%= response.encodeURL(viewServiceInfo) %>">
                            <IMG class=labelHomePage title="display" alt="Display" src="images/viewServiceInfo.jpg" align=middle border=0>
                        </A>        
                        <fmt:message key="serviceConfiguration.serviceInfoDescription" bundle="${lang}"/>.
                    </P> 
                </DIV> 
                <DIV class=portletItem id=05> 
                    <DIV> 
                        <A href="<%= response.encodeURL(deployAServiceRemotelyRequest) %>"><fmt:message key="serviceConfiguration.remoteDeployment" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <A href="<%= response.encodeURL(deployAServiceRemotelyRequest) %>">
                            <IMG class=labelHomePage title="remoteDeploy" alt="remoteDeploy" src="images/remoteDeploy.jpg" align=middle border=0>
                        </A>
                        <fmt:message key="serviceConfiguration.remoteDeploymentDescription" bundle="${lang}"/>.
                        
                    </P> 
                </DIV> 
                 <%
                    TBXService serv;
                    ServiceManager servMan;

                    servMan=ServiceManager.getInstance();

                    serv=servMan.getService(service);

                    if(serv !=null && serv.getImplementdInterface().startsWith("OGC-06-131"))
                    {
                %>
                <DIV class=portletItem id=06>
                    <DIV>
                        <A href="<%= response.encodeURL("catalogueSetCapabilities.jsp?serviceName="+service) %>"><fmt:message key="serviceConfiguration.configureCapabilities" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href="<%= response.encodeURL("catalogueSetCapabilities.jsp?serviceName="+service) %>"><IMG class=labelHomePage title=validate alt="Validate a script" src="images/pushServer.jpg" align=middle border=0></A>
                    <fmt:message key="serviceConfiguration.configureCapabilities.description" bundle="${lang}"/></P>
                </DIV>
                <%
                    }
                %>
            </TD>
        </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

