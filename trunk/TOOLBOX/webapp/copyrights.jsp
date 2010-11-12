<!-- 
 -
 -  Copyright 2003-2007 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: listFTPAccounts.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2005/04/14 11:52:13 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<%@ include file="checkAccount.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%
String languageC = (String)(request.getSession().getAttribute("language"));
if( languageC == null )
   languageC = request.getParameter("language");
request.setAttribute("languageReq", languageC);
request.setAttribute("languageReq", request.getAttribute("languageReq"));
%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<a href='main.jsp'><fmt:message key="copyrights.home" bundle="${lang}"/></a>&nbsp;&gt;" +
              "&nbsp;Copyrights");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc>Copyrights</P> 
                <p align="justify"><fmt:message key="copyrights.The" bundle="${lang}"/><a href="http://www.apache.org/">Apache </a> <fmt:message key="copyrights.License" bundle="${lang}"/> ( <a href="http://www.apache.org/licenses/">http://www.apache.org/licenses/ </a>); <fmt:message key="copyrights.code" bundle="${lang}"/> <a href="http://oss.software.ibm.com/icu4j/">http://oss.software.ibm.com/icu4j/ </a>, <fmt:message key="copyrights.see" bundle="${lang}"/> <a href="http://oss.software.ibm.com/cvs/icu/%7Echeckout%7E/icu/license.html"><fmt:message key="copyrights.license" bundle="${lang}"/> </a>; <fmt:message key="copyrights.the" bundle="${lang}"/> <a href="http://www.enterprisedt.com/">edtFTPj </a> <fmt:message key="copyrights.ftp" bundle="${lang}"/></p>
		<p align="justify"><fmt:message key="copyrights.trademarks" bundle="${lang}"/></p>
		<p align="justify">Copyright 2003-2007 Intecs </p>
		<p><fmt:message key="copyrights.free" bundle="${lang}"/></p>
		<p><fmt:message key="copyrights.distributed" bundle="${lang}"/></p>
		<p><fmt:message key="copyrights.See" bundle="${lang}"/><a href="gpl.txt"><fmt:message key="copyrights.GNU" bundle="${lang}"/></a></p>
            
            <p>
                <fmt:message key="copyrights.dockIcon" bundle="${lang}"/>
                Alert64 <a href="http://alert84.deviantart.com/">http://alert84.deviantart.com/</a>
            </p>

            </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<%
String language = request.getParameter("lang");
if( language == null ) language ="en";
%>
<jsp:include page="footer.jsp"/>

