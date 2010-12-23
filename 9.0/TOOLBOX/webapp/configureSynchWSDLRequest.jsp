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
 -  File Name:         $RCSfile: configureSynchWSDLRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.common.tbx.*,it.intecs.pisa.toolbox.service.*, java.util.*, javax.xml.transform.stream.*, it.intecs.pisa.util.*, org.w3c.dom.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
    String serviceName = request.getParameter("serviceName");
    String operationName = request.getParameter("operationName");
    
%>

<SCRIPT language="JavaScript">
<!--

function back() 
{ 

  window.location="<%= response.encodeURL("viewSynchWSDLInfo.jsp?serviceName=" + serviceName + "&operationName=" + operationName)%>";

}


//-->
</SCRIPT>


<jsp:include page="header.jsp" />
<%  
    TBXService service;
    TBXOperation oper;
    Interface interf;
    ServiceManager servMan;
    servMan=ServiceManager.getInstance();

    service=servMan.getService(serviceName);
    interf=service.getImplementedInterface();
    oper=(TBXOperation)interf.getOperationByName(operationName);

    String requestMessageName = oper.getInputType();
    String requestMessageNS = oper.getInputTypeNameSpace();
    String responseMessageName = oper.getOutputType();
    String responseMessageNS = oper.getOutputTypeNameSpace();

    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("configureSynchWSDLRequest.home");
    String opManag = (String)messages.getObject("configureSynchWSDLRequest.opManag");
    String edit = (String)messages.getObject("configureSynchWSDLRequest.Edit");

    String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
	            "&nbsp;<a href='manageOperations.jsp?serviceName="+serviceName+
                    "'>"+opManag+"</a>&nbsp;&gt;" + edit;
    
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/operationManagement.html_Operations_management*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="configureSynchWSDLRequest.Edit" bundle="${lang}"/></FONT></P> 
                <P>  
			
                       <FORM method="post" action="<%= response.encodeURL("configureSynchWSDLSubmit.jsp") %>">
                       <input type="hidden" name="serviceName" value="<%=serviceName%>">
                       <input type="hidden" name="operationName" value="<%=operationName%>">

				<!-- Page contents table-->
					<table width="90%" cellspacing="2" cellpadding="2" align="center">
                                                <input type="hidden" name="serviceName" value="<%=serviceName%>">
                                                <tr><!-- Row 1 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureSynchWSDLRequest.Input" bundle="${lang}"/></td>
						</tr>
						<tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureSynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"></FONT></td>
							<td class="sortable" nowrap><input type="text" name="requestMessageName" value="<%=requestMessageName%>" size='40'></td>
						</tr>

						<tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureSynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"></FONT></td>


							<td class="sortable" nowrap><input type="text" name="requestMessageNS" value="<%=requestMessageNS%>" size='40'></td>

						</tr>
						<tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="configureSynchWSDLRequest.Output" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureSynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"></FONT></td>
							<td class="sortable" nowrap><input type="text" name="responseMessageName" value="<%=responseMessageName%>" size='40'></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureSynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"></FONT></td>
                                                        <td class="sortable" nowrap><input type="text" name="responseMessageNS" value="<%=responseMessageNS%>" size='40'></td>
                                                </tr>
                                                <tr><!-- Row 13 -->
							<td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="configureSynchWSDLRequest.Back" bundle="${lang}"/>" onClick="back()"><input type="submit" value="<fmt:message key="configureSynchWSDLRequest.Configure" bundle="${lang}"/>"></td>
						</tr>
					</table>
                        </FORM>
								&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
