<!-- 
 -
 -  Copyright 2003-2010 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: configureToolboxRequest.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.1.1.1 $
 -  Revision Date:     $Date: 2006/06/13 15:02:26 $
 -
-->
<%@ page import="it.intecs.pisa.toolbox.configuration.*,
                 it.intecs.pisa.soap.toolbox.*,
                 java.util.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ include file="checkSession.jsp" %>

<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<jsp:include page="header.jsp" />

<% 
ToolboxConfiguration configuration;

configuration=ToolboxConfiguration.getInstance();

String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
Boolean configurationChanged = (request.getParameter("configurationChanged") == null ? false : true);
Boolean hasError=(request.getParameter("error") == null ? false : true);
boolean firstTimeCheck=Boolean.getBoolean(configuration.getConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK));
     
String status=request.getParameter("pageStatus");

request.setAttribute("pageStatus",status);

PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String home = (String)messages.getObject("configureToolboxRequest.home");
String Configuration = (String)messages.getObject("configureToolboxRequest.Configuration");
String change;


if(status.equals("disabled"))
    change = (String)messages.getObject("toolboxConfiguration.view");
else
    change = (String)messages.getObject("configureToolboxRequest.change");


String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
        "&nbsp;<a href='toolboxConfiguration.jsp'>"+Configuration+"</a>&nbsp;&gt;&nbsp;" + change;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
<TBODY> 
<TR> 
<TD class=pageBody id=main>
    <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/blankpage.html_TOOLBOX_management*RE/toolboxViewingConfiguration.html_Viewing_the_TOOLBOX_configuration*");</SCRIPT>
    <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>		

    <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
    <TBODY>
    <TR>


<%
if(hasError)
    {
        %>
        <fmt:message key="configureToolboxRequest.error" bundle="${lang}"/>
    <%
    }


if (configurationChanged) {
						String reloadTOOLBOX = "/manager/reload?path=/TOOLBOX";
					%>
					    <form method="get" action="<%=reloadTOOLBOX%>" target="reload">
						<input type="hidden" name="path" value="/TOOLBOX" />
						<font color="FF0000"><strong><fmt:message key="viewToolboxConfiguration.WARNING" bundle="${lang}"/>! </strong></font>
                        <strong>
                        <p>
                        <fmt:message key="viewToolboxConfiguration.inOrder" bundle="${lang}"/>
						<fmt:message key="viewToolboxConfiguration.toReload" bundle="${lang}"/>
                        <input type="submit" value="<fmt:message key="viewToolboxConfiguration.HERE" bundle="${lang}"/>">
                        <fmt:message key="viewToolboxConfiguration.orPoint" bundle="${lang}"/> http://TOMCAT_HOST:TOMCAT_PORT/manager/reload?path=/TOOLBOX
                        </strong></p>
						</form>
                     <%}%>



        <TD id=main>
            <c:choose>
                <c:when test="${pageStatus=='disabled'}">
                    <P class=arbloc><FONT class=arttl><fmt:message key="viewToolboxConfiguration.currentConf" bundle="${lang}"/></FONT></P>
                    <form method="get" action="<%= response.encodeURL("configureToolboxRequest.jsp") %>">
                    <input type="hidden" name="pageStatus" value="enabled"> 
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${firstTimeCheck}">
                            <P class=arbloc><FONT class=arttl><fmt:message key="configureToolboxRequest.update" bundle="${lang}"/></FONT></P>  
                        </c:when>
                        <c:otherwise>
                            <P class=arbloc><FONT class=arttl><fmt:message key="configureToolboxRequest.updateConf" bundle="${lang}"/></FONT></P>
                        </c:otherwise>
                    </c:choose>
                         <form method="POST" enctype="multipart/form-data" action="<%= response.encodeURL("manager?cmd=configureToolbox") %>">
                </c:otherwise>
                    
            </c:choose>
            <jsp:include page="toolboxConfigurationTable.jsp"/>
          
         
            </form>
    </TD></TR></TBODY></TABLE>
</TD></TR> </TBODY></TABLE>
<jsp:include page="footer.jsp"/>

