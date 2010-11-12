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
 -  File Name:         $RCSfile: importNewServiceRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<SCRIPT language="JavaScript">
<!--

function gothere() 
{ 
  window.location="<%= response.encodeURL("selectImportOrCreate.jsp?backbutton=true")%>";
} 
//-->
</SCRIPT>        
<%
		String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
		boolean isWarning = (warn.length() != 0);
		String importFile = request.getParameter("importFile");
                String keepNameChecked = ((isWarning && request.getParameter("keepOrChange").equals("keep")) || !isWarning ? "checked" : "");
                String changeNameChecked = (keepNameChecked.length() == 0 ? "checked" : "");
                String newNameParam;
                String serviceName = (isWarning && changeNameChecked.length() > 0 && (newNameParam = request.getParameter("newName")) != null ? newNameParam : "");
		
                PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
                String home = (String)messages.getObject("importNewServiceRequest.home");
                String serviceManag = (String)messages.getObject("importNewServiceRequest.serviceManag");
                String create = (String)messages.getObject("importNewServiceRequest.create");
                String import2 = (String)messages.getObject("importNewServiceRequest.import2");

                String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
                             "&nbsp;<a href='servicesManagement.jsp'>"+serviceManag+"</a>&nbsp;&gt;" +
                             "&nbsp;<a href='selectImportOrCreate.jsp?importOrCreate=create&serviceName="+
                             serviceName+"'>"+create+"</a>&nbsp;&gt;"+ import2;  
%>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
    <TD class=pageBody id=main> 
    <SCRIPT>addBreadCrumb("<%=bc%>")</SCRIPT> 
                        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceCreation.html_Service_creation*RE/serviceImporting.html_Importing_a_Service*");</SCRIPT>
  <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
    <TBODY> <TR> 
       <TD id=main> 
        <P class=arbloc><FONT class=arttl><fmt:message key="importNewServiceRequest.import2" bundle="${lang}"/></FONT></P> 
			<form method="post" enctype="multipart/form-data" action="<%= response.encodeURL("deploy") %>">
				<!-- Page contents table-->
                                        <input type="hidden" name="forwardToPage" id="forwardToPage" value="true"/>
					<table width="90%" cellspacing="2" cellpadding="2"  align="center">
						<tr><!-- Row 1 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="importNewServiceRequest.serviceSpec" bundle="${lang}"/></td>
						</tr>

                                          <tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap>
                                                        <input type="radio" name='keepOrChange' value='keep' <%=keepNameChecked%>><fmt:message key="importNewServiceRequest.keep" bundle="${lang}"/> <br>
                                                        <input type="radio" name='keepOrChange' value='change' <%=changeNameChecked%>><fmt:message key="importNewServiceRequest.change" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingServiceName") ? " - MISSING!" : ""%></font></P>
                                                        </td>
							<td class="sortable" nowrap valign="bottom">
							<input name="newName" type="text" size="30" value="<%=serviceName%>"><br/>
						</tr>
						<tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="importNewServiceRequest.descrFile" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingImportFile") ? " - MISSING!" : (warn.equals("invalidImportedDocument") ? " - INVALID IMPORTED DOCUMENT!" : "")%></font></td>
							<td class="sortable" nowrap>
							<input name="importFile"  type="file" size="50" value="<%=importFile != null ? importFile : ""%>"><br/>
                                                        <input name="h" type="hidden" value='h'/>
						</tr>                                               
						<tr><!-- Row 8 -->
							<td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="importNewServiceRequest.Back" bundle="${lang}"/>" onClick="gothere()"><input type="submit" value="<fmt:message key="importNewServiceRequest.Create" bundle="${lang}"/>"></td>
						</tr>
					</table>
			</form>
    </TD> 
    </TR> 
    </TBODY> 
  </TABLE> 
  </TD> 
  </TR> 
   </TBODY> 
 </TABLE> 
<jsp:include page="footer.jsp"/>

