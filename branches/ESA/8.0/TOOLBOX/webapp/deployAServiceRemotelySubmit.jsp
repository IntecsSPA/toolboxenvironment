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
 -  File Name:         $RCSfile: sendSOAPMessageSubmit.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.13 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
 -->
<%@ page import="java.text.*,java.net.*,it.intecs.pisa.soap.toolbox.*, org.w3c.dom.*, it.intecs.pisa.util.*, java.io.*, javax.xml.transform.stream.*, java.util.*,it.intecs.pisa.soap.toolbox.service.*"  errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp" />
<% DOMUtil domUtil = new DOMUtil(true);
String warnMessage = "";
  String serviceName = request.getParameter("serviceName").trim();

   ServiceManager servMan;
    TBXService serv;

    servMan=ServiceManager.getInstance();
    serv=servMan.getService(serviceName);

  Document message = serv.createServiceDescriptor();
  String url = request.getParameter("url").trim();
  String deployService="/deploy";
  String method="deploy";

  Document responseMessage = Toolbox.getToolboxConfigurator().soapCall(url + deployService, method, message, true);
  DOMUtil.indent(responseMessage);
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));

String home = (String)messages.getObject("bc.home");
String deploy = (String)messages.getObject("bc.deployAServiceRemotely");
String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + deploy;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><%=deploy%></FONT></P> 
                <P>  
							<table width="90" cellspacing="2" cellpadding="2" align="center">
										<form>
								<tr>
									<td align="center" nowrap>
											<textarea rows="25" cols="90" readonly><% XSLT.serialize(responseMessage, new StreamResult(out));%></textarea>
									</td>
								</tr>
								<tr>
									<td class="tdHead" align="right" valign="middle" height="50"><a href="<%= response.encodeURL("sendSOAPMessageRequest.jsp") %>" >&lt;&lt; <fmt:message key="sendSOAPMessageSubmit.Back" bundle="${lang}"/></a></td>
								</tr>
										</form>
							</table>
						</td>
					</tr>
                </P> 
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>