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
 -  File Name:         $RCSfile: validateScriptSubmit.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.7 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, it.intecs.pisa.util.*, java.io.*, org.w3c.dom.*, org.apache.commons.fileupload.*, java.util.*"  errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<jsp:include page="header.jsp" />
<%
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("validateScriptSubmit.home");
        String testCenter = (String)messages.getObject("validateScriptSubmit.testCenter");
        String validate = (String)messages.getObject("validateScriptSubmit.validate");
        String validateResult = (String)messages.getObject("validateScriptSubmit.validateResult");

        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;"+
              "&nbsp;<a href='testCenter.jsp'>"+testCenter+"</a>&nbsp;&gt;" + "&nbsp;<a href='"+response.encodeURL("validateScriptRequest.jsp")+"'>"+validate+"</a>&nbsp;&gt;"+
              validateResult;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="validateScriptSubmit.validateResult" bundle="${lang}"/></FONT></P> 
                <P>
                    <table width="70%"  cellspacing="2" cellpadding="2" border="0" align="center">
                            <tr><!-- Row 1 -->
                                    <td class="sortable" width="40%" colspan=1 nowrap><%
                        if(request.getParameter("valid").equals("true"))
                        {
                        %><fmt:message key="scriptValidation.valid" bundle="${lang}"/><%
                        }
                        else
                        {
                        %><fmt:message key="scriptValidation.notvalid" bundle="${lang}"/><%
                        }
                    %></td>
                                    </tr>

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


