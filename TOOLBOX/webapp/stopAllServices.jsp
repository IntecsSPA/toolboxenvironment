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
 -  File Name:         $RCSfile: startAllServices.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.toolbox.service.*" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<%
    try
    {
        ServiceManager servMan;

        servMan=ServiceManager.getInstance();
        TBXService[] services=servMan.getServicesAsArray();

        for(TBXService service:services)
            service.stop();
    }
    catch(Exception e)
            {}

%>

<jsp:forward page="showAllServicesStatus.jsp" />