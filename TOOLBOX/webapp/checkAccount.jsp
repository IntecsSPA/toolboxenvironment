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
 -  File Name:         $RCSfile: checkAccount.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*, org.w3c.dom.*, it.intecs.pisa.util.*,java.io.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%	
	if (request.getParameter("userName") == null || request.getParameter("password") == null) {
%>
	<%@ include file="checkSession.jsp" %>
<%		
	} else {
		NodeList adminUsers = DOMUtil.loadXML(new File(application.getRealPath("WEB-INF/xml/adminUsers.xml")),new File(application.getRealPath("WEB-INF/schemas/adminUsers.xsd"))).getDocumentElement().getElementsByTagName("adminUser");
		boolean isValidAccount = false;
		for (int i = 0; i < adminUsers.getLength(); i++ ) {
			if (request.getParameter("userName").equals(((Element) adminUsers.item(i)).getAttribute("userName")) && request.getParameter("password").equals(((Element) adminUsers.item(i)).getAttribute("password"))) {
				isValidAccount = true;
                                session.setMaxInactiveInterval(1800);
                                session.setAttribute("session", session.getId());
                                session.setAttribute("languageReq", request.getSession().getAttribute("language"));
                                break;
			}
		}
		if (!isValidAccount) {
                    PropertyResourceBundle messages = 
                            (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("language")));
                    String warnMsg = (String)messages.getObject("general.incorrectUserNamePw");
%>
	<jsp:forward page="loginRequest.jsp">
                <jsp:param name="warn" value="<%=warnMsg%>"/>
	</jsp:forward>
<%
		}
	}
%>
