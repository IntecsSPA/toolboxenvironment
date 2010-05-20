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
 -  File Name:         $RCSfile: viewAsynchWSDLInfo.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.common.tbx.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.toolbox.*, java.util.*, javax.xml.transform.stream.*, it.intecs.pisa.util.*, org.w3c.dom.*"  errorPage="errorPage.jsp" %>

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

  window.location="<%= response.encodeURL("manageOperations.jsp?serviceName=" + serviceName)%>";

}
function configure() 
{ 

  window.location="<%= response.encodeURL("configureAsynchWSDLRequest.jsp?serviceName=" + serviceName + "&operationName=" + operationName)%>";

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
		
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("viewAsynchWSDLInfo.home");
        String opManag = (String)messages.getObject("viewAsynchWSDLInfo.opManag");
        String asynchOp = (String)messages.getObject("viewAsynchWSDLInfo.asynchOp");

        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
	                "&nbsp;<a href='manageOperations.jsp?serviceName="+serviceName+"'>"+opManag+"</a>&nbsp;&gt;&nbsp;" + asynchOp;

        
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/operationManagement.html_Operations_management*RE/operationWSDL.html_WSDL*RE/operationWSDLDisplayingInfo.html_Displaying_the_WSDL_information*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewAsynchWSDLInfo.opManag" bundle="${lang}"/></FONT></P> 
                <P>  

				<!-- Page contents table-->
					<table width="90%" cellspacing="2" cellpadding="2" align="center">
                                                <tr><!-- Row 1 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="viewAsynchWSDLInfo.inputMsg" bundle="${lang}"/></td>
						</tr>
						<tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewAsynchWSDLInfo.name" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=requestMessageName.length() != 0 ? requestMessageName : "--"%></td>
						</tr>

						<tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.namespace" bundle="${lang}"/></td>


							<td class="sortable" nowrap><%=requestMessageNS.length() != 0 ? requestMessageNS : "--"%></td>

						</tr>
						<tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="viewAsynchWSDLInfo.outMsg" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.name" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=responseMessageName.length() != 0 ? responseMessageName : "--"%></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.namespace" bundle="${lang}"/></td>
                                                        <td class="sortable" nowrap><%=responseMessageNS.length() != 0 ? responseMessageNS : "--"%></td>
                                                </tr>
                                                <tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="viewAsynchWSDLInfo.pushMsg" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.name" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=pushMessageName.length() != 0 ? pushMessageName : "--"%></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.namespace" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=pushMessageNS.length() != 0 ? pushMessageNS : "--"%></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.SOAPAct" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=pushMessageSoapAction.length() != 0 ? pushMessageSoapAction : "--"%></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortableHeader" nowrap width="50%" colspan="2"><fmt:message key="viewAsynchWSDLInfo.pushResp" bundle="${lang}"/></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.name" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=pushResponseName.length() != 0 ? pushResponseName : "--"%></td>

						</tr>
                                                <tr><!-- Row 3 -->
							<td class="sortable" nowrap width="50%"><fmt:message key="viewAsynchWSDLInfo.namespace" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=pushResponseNS.length() != 0 ? pushResponseNS : "--"%></td>

						</tr>
                                                <tr><!-- Row 13 -->
							<td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="viewAsynchWSDLInfo.Back" bundle="${lang}"/>" onClick="back()"><input type="button" value="<fmt:message key="viewAsynchWSDLInfo.Configure" bundle="${lang}"/> >>" onClick=configure() ></td>
						</tr>
					</table>
					&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>


