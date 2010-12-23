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
 -  File Name:         $RCSfile: addFTPAccountRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
-->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>

<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%
String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
boolean isWarning = (warn.length() != 0);
String serviceName = request.getParameter("serviceName").trim();

String url = (isWarning ? request.getParameter("url") : "");
%>
<%
PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String home = (String)messages.getObject("bc.home");
String deploy = (String)messages.getObject("bc.deployAServiceRemotely");
String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + deploy;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                    <TBODY>
                        <TR>
                            <TD id=main>
                                <P class=arbloc><FONT class=arttl><fmt:message key="bc.deployAServiceRemotely" bundle="${lang}"/></FONT></P>
                                <!-- Page contents table-->
                                <form  action="<%= response.encodeURL("deployAServiceRemotelySubmit.jsp")%>">
                                    <input type="hidden" name="serviceName" value="<%=serviceName%>">
                                    
                                    <P>
                                        <table width="60%" cellspacing="2" cellpadding="2" border="0" align="center">
                                            <tr><!-- Row 1 -->
                                                <td class="sortableHeader" width="40%" colspan=1 nowrap><fmt:message key="deployAServiceRemotely.url" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingUrl") ? " " + (String)messages.getObject("general.MISSING") : ""%></font></td>
                                                <td class="sortable" nowrap><input type="text" name="url" size="50" value="<%=url%>" ></input></td>
                                            </tr>
                                            <tr><!-- Row 2 -->
                                                <td colspan="3" rowspan="2" align="right" nowrap class="sortable"><div align="right">
                                                        <input type="submit" value="<fmt:message key="deployAServiceRemotely.deploy" bundle="${lang}"/>">
             </div></td>
                                            </tr>
                                        </table>
                                    </P> &nbsp;
                                </form>
                </TD></TR></TBODY></TABLE>
</TD></TR> </TBODY></TABLE>
<jsp:include page="footer.jsp"/>

