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
 -  File Name:         $RCSfile: validateScriptRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp" />
<%		String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
		boolean isWarning = (warn.length() != 0);
%>
<%
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("validateScriptRequest.home");
        String testCenter = (String)messages.getObject("validateScriptRequest.testCenter");
        String validate = (String)messages.getObject("validateScriptRequest.validate");

        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;<a href='testCenter.jsp'>"+testCenter+"</a>&nbsp;&gt;" + validate;
%>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/blankpage.html_Test_Center*RE/validatingAScript.html_Validating_a_script*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="validateScriptRequest.validate" bundle="${lang}"/></FONT></P> 
                <P>  
			<form method="post" enctype="multipart/form-data" action="<%= response.encodeURL("manager?cmd=validateToolboxScript")%>">
				<!-- Page contents table-->
					<table width="70%"  cellspacing="2" cellpadding="2" border="0" align="center">
						<tr><!-- Row 1 -->
							<td class="sortable" width="40%" colspan=1 nowrap><fmt:message key="validateScriptRequest.xmlFile" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingScriptFile") ? " - MISSING!" : (warn.equals("noExistentScriptFile") ? " - NO SUCH FILE!" : "")%></td>
							<td class="sortable" nowrap><input type="file" name="scriptFile" size="50"></input></td>
						</tr>
						<tr><!-- Row 8 -->
							<td class="sortable" colspan="3" rowspan="2" nowrap align="right"><div align="right">
							  <input type="submit" value="Validate">
							  </div></td>
						</tr>
					</table>
			</form>
                </P> 
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 		
<jsp:include page="footer.jsp"/>


