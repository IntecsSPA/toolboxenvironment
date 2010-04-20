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
 -  File Name:         $RCSfile: toolboxUpgradeSubmit.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.2 $
 -  Revision Date:     $Date: 2006/09/25 12:26:40 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.toolbox.configuration.*, java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" /> 
		  	<%
   String userName = request.getParameter("userName").trim();
   String password = request.getParameter("password").trim();                                      
   String disableCheck = request.getParameter("checkVersion");
  if( (disableCheck != null) && disableCheck.equals("on") ) {
     ToolboxConfiguration conf;

     conf=ToolboxConfiguration.getInstance();
     conf.setConfigurationValue(ToolboxConfiguration.TOOLBOX_VERSION_CHECK, "false");
     conf.saveConfiguration();
     
          }
                        %>
    <jsp:forward page="main.jsp">
      <jsp:param name="userName" value="<%=userName%>"/>      
      <jsp:param name="password" value="<%=password%>"/>              
    </jsp:forward>                        
<jsp:include page="footer.html" />
