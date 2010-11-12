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
	String ftpUserName = (isWarning ? request.getParameter("ftpUserName") : "");
	String ftpUserPassword = (isWarning ? request.getParameter("ftpUserPassword") : "");
	String ftpRootDir = (isWarning ? request.getParameter("ftpRootDir") : "");
	String writePermissionChecked = (request.getParameter("writePermission") != null ? "checked": "");
%>
<%
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("addFTPAccountRequest.home");
    String FTP = (String)messages.getObject("addFTPAccountRequest.FTP");
    String AddFTP = (String)messages.getObject("addFTPAccountRequest.AddFTP");
    String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;<a href='FTPManagement.jsp'>"+FTP+"</a>&nbsp;&gt;" + AddFTP;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/ftpManagement.html_FTP_management*RE/ftpAdding.html_Adding_an_FTP_account*");</SCRIPT>
       <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
	   <TBODY>
        <TR>
          <TD id=main>
            <P class=arbloc><FONT class=arttl><fmt:message key="addFTPAccountRequest.AddFTP" bundle="${lang}"/></FONT></P>
				<!-- Page contents table-->
			<form  action="<%= response.encodeURL("addFTPAccountSubmit.jsp")%>">
				<P>
					<table width="60%" cellspacing="2" cellpadding="2" border="0" align="center">
						<tr><!-- Row 1 -->
							<td class="sortableHeader" width="40%" colspan=1 nowrap><fmt:message key="addFTPAccountRequest.username" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingUserName") ? " " + (String)messages.getObject("general.MISSING") : ""%></font></td>
							<td class="sortable" nowrap><input type="text" name="ftpUserName" size="50" value="<%=ftpUserName%>" ></input></td>
						</tr>
						<tr><!-- Row 2 -->
							<td class="sortableHeader" width="40%" colspan=1 nowrap><fmt:message key="addFTPAccountRequest.Password" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingPassword") ? " " + (String)messages.getObject("general.MISSING") : ""%></font></td>
                            <td class="sortable" nowrap><input type="password" name="ftpUserPassword" size="50" value="<%=ftpUserPassword%>"></input></td>
                        </tr>
						<tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="40%"><fmt:message key="addFTPAccountRequest.root" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingRootDir") ? " " + (String)messages.getObject("general.MISSING") : ""%></font></td>
							<td class="sortable" nowrap><input type="text" name="ftpRootDir" size="50" value="<%=ftpRootDir%>"></input></td>
						</tr>
						<tr><!-- Row 4 -->
							<td class="sortableHeader" nowrap width="50%"><fmt:message key="addFTPAccountRequest.Write" bundle="${lang}"/></td>
							<td class="sortable" nowrap><input name="writePermission" type="checkbox" <%=writePermissionChecked%> ></td>
						</tr>
						<tr><!-- Row 4 -->
						  <td colspan="3" rowspan="2" align="right" nowrap class="sortable"><div align="right">
						    <input type="submit" value="<fmt:message key="addFTPAccountRequest.Add" bundle="${lang}"/>">
					      </div></td>
						</tr>
				  </table>
				</P> &nbsp;
			</form>
       </TD></TR></TBODY></TABLE>
       </TD></TR> </TBODY></TABLE>
<jsp:include page="footer.jsp"/>

