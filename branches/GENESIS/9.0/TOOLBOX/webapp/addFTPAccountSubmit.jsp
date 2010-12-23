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
 -  File Name:         $RCSfile: addFTPAccountSubmit.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
 -->
<%@ page import="it.intecs.pisa.toolbox.*"  errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<% 
  String ftpUserName = request.getParameter("ftpUserName").trim();
  String ftpUserPassword = request.getParameter("ftpUserPassword").trim();
  String ftpRootDir = request.getParameter("ftpRootDir").trim();

  boolean writePermission = (request.getParameter("writePermission") != null ? true: false);
  
  String warnMessage = "";  

  if (ftpUserName.length() == 0) {warnMessage = "missingUserName";
  }else if (ftpUserPassword.length() == 0) {warnMessage = "missingPassword";
  }else if (ftpRootDir.length() == 0) {warnMessage = "missingRootDir";} 
  if (warnMessage.length() != 0) {
%> 
<jsp:forward page="addFTPAccountRequest.jsp">
	<jsp:param name="warn" value="<%=warnMessage%>"/>
</jsp:forward>
<% 
  }
  FTPServerManager servMan;

  servMan=FTPServerManager.getInstance();
  servMan.addUser(ftpUserName, ftpUserPassword,  ftpRootDir, writePermission);
%>

<jsp:forward page="listFTPAccounts.jsp" />


