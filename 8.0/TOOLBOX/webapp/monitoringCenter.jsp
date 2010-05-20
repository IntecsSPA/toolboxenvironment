<!-- 
 -
 -  $copyright$
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $Id$
 -  TOOLBOX Version:   $Name$
 -  File Revision:     $Rev$
 -  Revision Date:     $Date: 2008-02-21 06:46:40 +0100 (Thu, 21 Feb 2008) $
 -
-->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.*" errorPage="errorPage.jsp" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" /> 
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
String service = request.getParameter("serviceName") == null ? "": request.getParameter("serviceName");

//Setting service variable in order to be used with c tag library
request.setAttribute("service",service);
String viewToolboxLog= "viewToolboxLog.jsp";
String viewServiceLog= "viewServiceLog.jsp?serviceName=" + service;
String viewSServiceInstances = "viewServiceInstances.jsp?instanceType=S&serviceName=" + service+"&orderBy=INSTANCE_ID&order=ASC";
String viewAServiceInstances = "viewServiceInstances.jsp?instanceType=A&serviceName=" + service+"&orderBy=INSTANCE_ID&order=ASC";
String viewServiceTimers= "viewServiceTimers.jsp?serviceName=" + service;
String viewOrdersConfirmation="viewOrderConfirmations.jsp?serviceName="+ service;
PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String home = (String)messages.getObject("monitoringCenter.home");
String monitoring = (String)messages.getObject("monitoringCenter.monitoring");
String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
        "&nbsp;"+monitoring;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR>
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
            <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/monitoringCenter.html_Monitoring_center*");</SCRIPT>

                <DIV class=portletItem id=01>
                    <DIV> 
                     <A href="javascript:view('manager?cmd=getTbxLog&serviceName=<%=service%>', 'Toolbox Log');"><fmt:message key="monitoringCenter.logDisplay" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                        </DIV> 
                    <P> 
                   <A href="javascript:view('manager?cmd=getTbxLog&serviceName=<%=service%>', 'Toolbox Log');"><IMG class=labelHomePage title=create alt="TOOLBOX log" src="images/log.jpg" align=middle border=0></A> 
                        <fmt:message key="monitoringCenter.logDescr" bundle="${lang}"/></P> 
                </DIV> 
                <c:choose>
                    <c:when test="${service==''}">
                        <DIV class=double> 
                        <DIV class="searchDiv itemDark"> 
                            <%
                            ServiceManager servMan;
                            TBXService[] services;

                            servMan=ServiceManager.getInstance();
                            services=servMan.getServicesAsArray();

                            boolean isEnabled = services.length>0;
                            %>
                            
                            <form name="AdvancedSearchForm" method="post" action="<%= response.encodeURL("monitoringCenter.jsp") %>"> 
                                <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%> onChange="javascript: document.AdvancedSearchForm.submit();"> 
                                    <option value=""></option> 
                                    <%
                                    for(TBXService serv:services)
                                        {
                                            String serviceName = serv.getServiceName();
                                    %> 
                                    <option value="<%= serviceName%>"><%= serviceName%></option> 
                                    <%
                                    }
                                    %> 
                                </select> 
                            </form>
                        </DIV> 
                        <DIV class="quickLink itemLight"> 
                        </DIV>
                        
                    </c:when>
                    <c:otherwise>
                        
                        
                        <DIV class=portletItem id=01> 
                            <DIV> 
                                <A href="javascript:view('manager?cmd=getSrvLog&serviceName=<%=service%>', 'Service Log');"><fmt:message key="monitoringCenter.serviceDisplay" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                            </DIV> 
                            <P>
                                 <A href="javascript:view('manager?cmd=getSrvLog&serviceName=<%=service%>', 'Service Log');"><IMG class=labelHomePage title=create alt="Service log" src="images/tbxlog.jpg" align=middle border=0></A> 
                                <fmt:message key="monitoringCenter.serviceDescr1" bundle="${lang}"/> <%=service%> <fmt:message key="monitoringCenter.serviceDescr2" bundle="${lang}"/></P> 
                        </DIV>
                        <DIV class=portletItem id=01> 
                            <DIV> 
                                <A href="<%= response.encodeURL(viewSServiceInstances) %>"><fmt:message key="monitoringCenter.synchDisplay" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                            </DIV> 
                            <P> 
                                <A href="<%= response.encodeURL(viewSServiceInstances) %>"><IMG class=labelHomePage title=create alt="Synchronous instances" src="images/asynchronousInstances.jpg" align=middle border=0></A> 
                            <fmt:message key="monitoringCenter.descrSynch1" bundle="${lang}"/> <%=service%> <fmt:message key="monitoringCenter.descrSynch2" bundle="${lang}"/></P> 
                        </DIV>
                        <DIV class=portletItem id=01> 
                            <DIV> 
                                <A href="<%= response.encodeURL(viewAServiceInstances) %>"><fmt:message key="monitoringCenter.asynchDisplay" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                            </DIV> 
                            <P> 
                                <A href="<%= response.encodeURL(viewAServiceInstances) %>"><IMG class=labelHomePage title=create alt="Asynchronous instances" src="images/asynchronousInstances.jpg" align=middle border=0></A> 
                            <fmt:message key="monitoringCenter.asynchDescr1" bundle="${lang}"/> <%=service%> <fmt:message key="monitoringCenter.asynchDescr2" bundle="${lang}"/></P> 
                        </DIV>
                        <DIV class=portletItem id=01> 
                            <DIV> 
                                <A href="<%= response.encodeURL(viewServiceTimers) %>"><fmt:message key="monitoringCenter.view" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                            </DIV> 
                            <P> 
                                <A href="<%= response.encodeURL(viewServiceTimers) %>"><IMG class=labelHomePage title=create alt="Create a new service" src="images/timer.jpg" align=middle border=0></A> 
                            <fmt:message key="monitoringCenter.viewDescr" bundle="${lang}"/> <%=service%> <fmt:message key="monitoringCenter.service" bundle="${lang}"/>.</P> 
                        </DIV>          
                        <DIV class=portletItem id=01> 
                            <DIV> 
                                <A href="<%= response.encodeURL(viewOrdersConfirmation) %>"><fmt:message key="monitoringCenter.ordersConfirmation" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                            </DIV> 
                            <P> 
                                <A href="<%= response.encodeURL(viewOrdersConfirmation) %>"><IMG class=labelHomePage title=create alt="Monitor orders" src="images/orders.jpg" align=middle border=0></A> 
                            <fmt:message key="monitoringCenter.orderConfirmationDescr" bundle="${lang}"/> <%=service%> <fmt:message key="monitoringCenter.service" bundle="${lang}"/>.</P> 
                        </DIV>                        
                    </c:otherwise>
                </c:choose>
            </TD>
        </TR> 
    </TBODY>
</TABLE> 
<jsp:include page="footer.jsp"/>

