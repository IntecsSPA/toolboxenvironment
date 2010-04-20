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
 -  File Name:         $RCSfile: manageOperations.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.11 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.*,java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<% 
	String service = request.getParameter("serviceName");
    String orderBy = request.getParameter("orderBy");
	TBXService serv;

    serv=ServiceManager.getInstance().getService(service);

    if (orderBy  == null)  {orderBy = "noOrder";}

        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("manageOperations.home");
        String opManag = (String)messages.getObject("manageOperations.opManag");
  
	String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;"+opManag;
%>
<SCRIPT language="JavaScript">
<!--
function gothere() 
{ 
  window.location="<%=response.encodeURL("selectOperationToAdd.jsp?serviceName="+service)%>";
} 

function deleteOp(service, operation) 
{ 
  window.location="deleteOperationRequest.jsp?serviceName=service&operationName=operation";
} 
//-->
</SCRIPT>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("operationManagement");</SCRIPT>         
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="manageOperations.opManag" bundle="${lang}"/></FONT></P> 
                <P>  
							<table width="90%" cellspacing="3" cellpadding="2" align="center">
								<tr>
									<td>
<%
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
	Document xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/listOperations.xsl")));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
        transformer.setParameter("serviceName", service);
        transformer.setParameter("orderBy", orderBy);
        transformer.setParameter("viewWSDLInfoURL", response.encodeURL("viewWSDLInfo.jsp"));
        transformer.setParameter("configureOperationURL", response.encodeURL("configureOperation.jsp"));
        transformer.setParameter("deleteOperationURL", response.encodeURL("manager?cmd=deleteOperation"));
        transformer.setParameter("language", session.getAttribute("languageReq"));
        transformer.transform(new StreamSource(serv.viewDescriptorFile()), new StreamResult(out));
%>
									</td>
								</tr>
                                            <tr>
                                                <td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<fmt:message key="manageOperations.addOperation" bundle="${lang}"/> >>" onClick="gothere()">
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

