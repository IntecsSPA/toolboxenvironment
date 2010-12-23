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
 -  File Name:         $RCSfile: errorPage.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.toolbox.*, 
                 java.util.*,
                 java.io.*"  isErrorPage="true"%>
<!--jsp:include page="header.jsp"/-->
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<!--%@ include file="checkSession.jsp" %-->
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<% 
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("errorPage.home");
    String errorPage = (String)messages.getObject("errorPage.errorPage");

    String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + errorPage;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> 
                  <P class=arbloc><FONT class=arttl><fmt:message key="errorPage.errorPage" bundle="${lang}"/></FONT></P> 
                            <P>  
				<!-- Page contents table-->
					<table width="90%" cellspacing="2" cellpadding="2" align="center">
						<form>
                                                <tr><!-- Row 1 -->
							<td class="tdInfo"  ><textarea rows="25" cols="100" readonly><% exception.printStackTrace(new PrintWriter(out, true)); %></textarea></td>
						</tr>
						<tr><!-- Row 2 -->
									<td class="tdHead" align="right" valign="middle" height="50"><p><input type="button" value="< <fmt:message key="errorPage.Back" bundle="${lang}"/>" onClick="history.back()"></td>
						</tr></form>
					</table>
                </P> 
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 		
<jsp:include page="footer.jsp"/>
