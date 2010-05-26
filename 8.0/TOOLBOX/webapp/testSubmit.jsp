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
 -  File Name:         $RCSfile: testSubmit.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="java.net.*,javax.xml.soap.*,it.intecs.pisa.toolbox.configuration.*,it.intecs.pisa.soap.toolbox.*, org.w3c.dom.*, it.intecs.pisa.util.*, java.io.*, javax.xml.transform.stream.*, java.util.*"  errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>
<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<% DOMUtil domUtil = new DOMUtil(true);
 Document responseMessage;
 Element responseElement;
 ToolboxConfiguration configuration;

 configuration= ToolboxConfiguration.getInstance();
 String tomcatPort =configuration.getConfigurationValue(ToolboxConfiguration.TOMCAT_PORT);
String responseMessageString="";
 SOAPUtils soapUtils = new SOAPUtils();
 Document message = domUtil.fileToDocument(application.getRealPath("WEB-INF/xml/testMsg.xml"));


   try
        {
           responseElement = AxisSOAPClient.sendReceive(new URL("http://localhost:" + tomcatPort + "/TOOLBOX/services/testService"), message.getDocumentElement(), "test");
           responseMessage=responseElement.getOwnerDocument();
           DOMUtil.indent(responseMessage);
           responseMessageString = DOMUtil.getDocumentAsString(responseMessage);
        }
        catch(Exception ecc)
        {
            responseMessageString="The test returned an error. Check the configuration parameters (e.g Tomcat Installation, Apache Installation or Proxy settings) and try again.";
        }

 String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt; Installation test page";

%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
  <TBODY>
    <TR>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
          <TBODY>
            <TR>
              <TD id=main> <P class=arbloc><FONT class=arttl>Installation test page</FONT></P>
                <P>


							<table width="90" cellspacing="2" cellpadding="2"  align="center">
										<form>
								<tr>
									<td align="center" nowrap>
											<textarea rows="10" cols="90" readonly><%=responseMessageString%></textarea>
									</td>
								</tr>
								<tr>
									<td class="tdHead" align="right" valign="middle" height="50"><a href="<%= response.encodeURL("main.jsp") %>">&lt;&lt;Back</a></td>
								</tr>
										</form>
							</table>
												&nbsp; </TD>
            </TR>
          </TBODY>
        </TABLE></TD>
    </TR>
  </TBODY>
</TABLE>

<%
String language = request.getParameter("lang");
if( language == null ) language ="en";
%>
<jsp:include page="footer.jsp"/>


