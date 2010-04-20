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
 -  File Name:         $RCSfile: configureSynchWSDLSubmit.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.common.tbx.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.util.*, org.w3c.dom.*, javax.xml.transform.stream.*, java.io.*, java.util.*, java.net.*" errorPage="errorPage.jsp" %>

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

  String requestMessageName = request.getParameter("requestMessageName").trim();
  String requestMessageNS = request.getParameter("requestMessageNS").trim();
  String responseMessageName = request.getParameter("responseMessageName").trim();
  String responseMessageNS = request.getParameter("responseMessageNS").trim();

  String warnMessage = "";
  if (requestMessageName.length() == 0) {
    warnMessage = "missingRequestMessageName";
  } else if (requestMessageNS.length() == 0) {
    warnMessage = "missingRequestMessageNS";
  } else {
    try {
        new URI(requestMessageNS);
       
    } catch (URISyntaxException e) {
        warnMessage = "badRequestMessageNS";
    }
  }  
  if (warnMessage.length() != 0) {
%>
<jsp:forward page="configureSynchWSDLRequest.jsp">
	<jsp:param name="serviceName" value="<%=serviceName%>"/>
	<jsp:param name="warn" value="<%=warnMessage%>"/>
	<jsp:param name="operationName" value="<%=operationName%>"/>
        <jsp:param name="requestMessageName" value="<%=requestMessageName%>"/>
        <jsp:param name="requestMessageNS" value="<%=requestMessageNS%>"/>
        <jsp:param name="responseMessageName" value="<%=responseMessageName%>"/>
        <jsp:param name="responseMessageNS" value="<%=responseMessageNS%>"/>
</jsp:forward>
<%
  }
  if (responseMessageName.length() == 0) {
    warnMessage = "missingResponseMessageName";
  } else if (responseMessageNS.length() == 0) {
    warnMessage = "missingResponseMessageNS";
  } else {
    try {
        new URI(responseMessageNS);
    } catch (URISyntaxException e) {
        warnMessage = "badResponseMessageNS";
    }
  }
  
  if (warnMessage.length() != 0) {
%>
<jsp:forward page="configureSynchWSDLRequest.jsp">
	<jsp:param name="serviceName" value="<%=serviceName%>"/>
	<jsp:param name="warn" value="<%=warnMessage%>"/>
	<jsp:param name="operationName" value="<%=operationName%>"/>
        <jsp:param name="requestMessageName" value="<%=requestMessageName%>"/>
        <jsp:param name="requestMessageNS" value="<%=requestMessageNS%>"/>
        <jsp:param name="responseMessageName" value="<%=responseMessageName%>"/>
        <jsp:param name="responseMessageNS" value="<%=responseMessageNS%>"/>
</jsp:forward>
<%
  }
  
    TBXService service;
    TBXOperation oper;
    Interface interf;
    ServiceManager servMan;
    servMan=ServiceManager.getInstance();

    service=servMan.getService(serviceName);
    interf=service.getImplementedInterface();
    oper=(TBXOperation)interf.getOperationByName(operationName);

    oper.setInputType(requestMessageName);
    oper.setInputTypeNameSpace(requestMessageNS);
    oper.setOutputType(responseMessageName);
    oper.setOutputTypeNameSpace(responseMessageNS);


    service.dumpService();
%>
<jsp:forward page="viewSynchWSDLInfo.jsp" >
	<jsp:param name="serviceName" value="<%=serviceName%>"/>
        <jsp:param name="operationName" value="<%=operationName%>"/>
</jsp:forward>
