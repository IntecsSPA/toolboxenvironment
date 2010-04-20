<!-- 
 -
 -  Copyright 2003-2004 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: viewWSDLInfo.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.3 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.common.tbx.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.soap.toolbox.*, java.util.*, javax.xml.transform.stream.*, it.intecs.pisa.util.*, org.w3c.dom.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
    String serviceName = request.getParameter("serviceName");
    String operationName = request.getParameter("operationName");

    TBXService service;
    TBXOperation oper;
    Interface interf;
    ServiceManager servMan;
    servMan=ServiceManager.getInstance();

    service=servMan.getService(serviceName);
    interf=service.getImplementedInterface();
    oper=(TBXOperation)interf.getOperationByName(operationName);

    if (oper.isAsynchronous()==false) {
%>
<jsp:forward page="viewSynchWSDLInfo.jsp">
    <jsp:param name="serviceName" value="<%=serviceName%>"/>
    <jsp:param name="operationName" value="<%=operationName%>"/>
</jsp:forward>
<%
    } else {
%>
<jsp:forward page="viewAsynchWSDLInfo.jsp">
    <jsp:param name="serviceName" value="<%=serviceName%>"/>
    <jsp:param name="operationName" value="<%=operationName%>"/>
</jsp:forward>
<%
    }
%>

