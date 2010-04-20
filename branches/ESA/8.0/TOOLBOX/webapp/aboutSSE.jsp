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
 -  File Name:         $RCSfile: listFTPAccounts.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2005/04/14 11:52:13 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("aboutSSE.home");
    String about = (String)messages.getObject("aboutSSE.about");
    String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + "&nbsp;"+ about;
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><fmt:message key="aboutSSE.about" bundle="${lang}"/></P> 
                <table cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="tdHelp" valign="top"><p align="justify"><fmt:message key="aboutSSE.the" bundle="${lang}"/> <a href="doc/SUM/index.html">TOOLBOX </a> <fmt:message key="aboutSSE.toolboxIs" bundle="${lang}"/> <a href="http://services.eoportal.org">SSE </a> <fmt:message key="aboutSSE.framework" bundle="${lang}"/></p>
                        <p align="justify"><a href="http://services.eoportal.org">SSE </a> <fmt:message key="aboutSSE.SSE" bundle="${lang}"/></p>
                        <p align="justify"><fmt:message key="aboutSSE.at" bundle="${lang}"/><a href="http://services.eoportal.org">SSE </a> <fmt:message key="aboutSSE.ESA" bundle="${lang}"/> </p>
                        <p align="justify"><a href="http://services.eoportal.org">SSE </a> <fmt:message key="aboutSSE.collection" bundle="${lang}"/><a href="http://services.eoportal.org">SSE </a>, <fmt:message key="aboutSSE.users" bundle="${lang}"/></p></td>
                  </tr>
                </table>                <p align="justify">&nbsp;</p>		</TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

