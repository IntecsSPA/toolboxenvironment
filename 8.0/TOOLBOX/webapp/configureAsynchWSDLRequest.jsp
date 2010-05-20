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
 -  File Name:         $RCSfile: configureAsynchWSDLRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.3 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
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

  window.location="<%= response.encodeURL("viewAsynchWSDLInfo.jsp?serviceName=" + serviceName + "&operationName=" + operationName)%>";

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

    String pushMessageName = oper.getCallbackInputType();
    String pushMessageNS = oper.getCallbackInputTypeNameSpace();
    String pushMessageSoapAction = oper.getCallbackSoapAction();
    String pushResponseName = oper.getCallbackOutputType();
    String pushResponseNS = oper.getCallbackOutputTypeNameSpace();

    String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
    boolean isWarning = (warn.length() != 0);
    
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("configureAsynchWSDLRequest.home");
    String opManag = (String)messages.getObject("configureAsynchWSDLRequest.opManag");
    String edit = (String)messages.getObject("configureAsynchWSDLRequest.Edit");
        
	String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
	            "&nbsp;<a href='manageOperations.jsp?serviceName="+serviceName+"'>"+opManag+
                    "</a>&nbsp;&gt;" + edit;
    
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/operationManagement.html_Operations_management*RE/operationReconfiguring.html_Re-configuring_an_operation*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="configureAsynchWSDLRequest.Edit" bundle="${lang}"/></FONT></P> 
                <P>  
                                <FORM method="post" action="<%= response.encodeURL("configureAsynchWSDLSubmit.jsp") %>">
                                    <input type="hidden" name="serviceName" value="<%=serviceName%>">
                                    <input type="hidden" name="operationName" value="<%=operationName%>">
				<!-- Page contents table-->
					<table width="90%" cellspacing="3" cellpadding="2" align="center">
                                                <tr><!-- Row 1 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureAsynchWSDLRequest.Input" bundle="${lang}"/></td>
						</tr>
						<tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureAsynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"><%=isWarning && warn.equals("missingRequestMessageName") ? " "+(String)messages.getObject("general.MISSING") : ""%></FONT></td>
							<td class="sortable" nowrap><input name="requestMessageName" value="<%=requestMessageName%>" size='50' type="text"></td>
						</tr>

						<tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"><%=isWarning ? (warn.equals("missingRequestMessageNS") ? " "+(String)messages.getObject("general.MISSING") : (warn.equals("badRequestMessageNS") ? " "+(String)messages.getObject("general.badSyntax") : "")) : ""%></FONT></td>


							<td class="sortable" nowrap><input name="requestMessageNS" value="<%=requestMessageNS%>" size='50' type="text"></td>

						</tr>
						<tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="configureAsynchWSDLRequest.Output" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"><%=isWarning && warn.equals("missingResponseMessageName") ? " "+(String)messages.getObject("general.MISSING") : ""%></font></td>
							<td class="sortable" nowrap><input name='responseMessageName' value="<%=responseMessageName%>" size='50' type="text"></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"><%=isWarning ? (warn.equals("missingResponseMessageNS") ? " "+(String)messages.getObject("general.MISSING") : (warn.equals("badResponseMessageNS") ? " "+(String)messages.getObject("general.badSyntax") : "")) : ""%></FONT></td>
                                                        <td class="sortable" nowrap><input name='responseMessageNS' value="<%=responseMessageNS%>" size='50' type="text"></td>
                                                </tr>
                                                <tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="configureAsynchWSDLRequest.Push" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"><%=isWarning && warn.equals("missingPushMessageName") ? " "+(String)messages.getObject("general.MISSING") : ""%></font></td>
							<td class="sortable" nowrap><input name='pushMessageName' value="<%=pushMessageName%>" size='50' type="text"></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"><%=isWarning ? (warn.equals("missingPushMessageNS") ? " "+(String)messages.getObject("general.MISSING") : (warn.equals("badPushMessageNS") ? " "+(String)messages.getObject("general.badSyntax") : "")) : ""%></FONT></td>
							<td class="sortable" nowrap><input name="pushMessageNS" value="<%=pushMessageNS%>" size='50' type="text"></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.SOAPAct" bundle="${lang}"/><FONT color="FF0000"><%=isWarning && warn.equals("missingPushSoapAction") ? " "+(String)messages.getObject("general.MISSING") : ""%></font></td>
                                                    <td class="sortable" nowrap><input name="pushMessageSoapAction" value="<%=pushMessageSoapAction%>" size='50' type="text"></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="configureAsynchWSDLRequest.PushResp" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Name" bundle="${lang}"/><FONT color="FF0000"><%=isWarning && warn.equals("missingPushResponseName") ? " "+(String)messages.getObject("general.MISSING") : ""%></font></td>
							<td class="sortable" nowrap><input name="pushResponseName" value="<%=pushResponseName%>" size='50' type="text"></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="configureAsynchWSDLRequest.Namespace" bundle="${lang}"/><FONT color="FF0000"><%=isWarning ? (warn.equals("missingPushResponseNS") ? " "+(String)messages.getObject("general.MISSING") : (warn.equals("badPushResponseNS") ? " "+(String)messages.getObject("general.badSyntax") : "")) : ""%></FONT></td>
							<td class="sortable" nowrap><input name="pushResponseNS" value="<%=pushResponseNS%>" size='50'></td>

						</tr>
                                                <tr><!-- Row 13 -->
							<td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="configureAsynchWSDLRequest.Back" bundle="${lang}"/>" onClick="back()"><input type="submit" value="<fmt:message key="configureAsynchWSDLRequest.Configure" bundle="${lang}"/>" onClick=configure()></td>
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



