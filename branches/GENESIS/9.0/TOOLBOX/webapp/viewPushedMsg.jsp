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
 -  File Name:         $RCSfile: viewPushedMsg.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2005/04/15 14:22:42 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="java.util.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.stream.*, org.w3c.dom.*" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%
String refresh = response.encodeURL("viewPushedMsg.jsp");
%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
    File pushDir;
    File[] files = (pushDir = new File(application.getRealPath("Push"))).listFiles();
    File file;
    String fileAbsoluteName;
    String fileName;
    DOMUtil domUtil = new DOMUtil(true);
    
    
%>

<SCRIPT language="JavaScript">
<!--
<% 
   for (int j = 0; j < files.length; j++) {
    fileAbsoluteName = (file = files[j]).getAbsolutePath();
    fileName = file.getName();
%>
function show<%=j%>() 
{ 

  window.location="<%= response.encodeURL("viewPushedMsg.jsp?show=" + fileName) %>";

}
function delete<%=j%>() 
{ 

  window.location="<%= response.encodeURL("removePushedFileRequest.jsp?fileName=" + fileName) %>";

}



<%
   }
%>


function closeggg() 
{ 

  window.location="<%= response.encodeURL("viewPushedMsg.jsp") %>";

}
//-->
</SCRIPT>

<%@ include file="checkSession.jsp" %>


<jsp:include page="header.jsp" />
<%
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("viewPushedMsg.home");
        String testCenter = (String)messages.getObject("viewPushedMsg.testCenter");
        String pushServer = (String)messages.getObject("viewPushedMsg.pushServer");

        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;<a href='testCenter.jsp'>"+testCenter+"</a>&nbsp;&gt;&nbsp;" + pushServer;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/blankpage.html_Test_Center*RE/pushServer.html_Displaying_the_messages_pushed_to_the_server*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewPushedMsg.pushServer" bundle="${lang}"/></FONT></P> 
                <P>
				<table width="97%" cellspacing="3" cellpadding="2" align="center">
					<tr><!-- Row 2 --><%String icd = response.encodeURL("help.jsp?topic=icd");%>
							<td class="tdExpl"  colspan=3><fmt:message key="viewPushedMsg.pageDescr" bundle="${lang}"/>
                                                         <fmt:message key="viewPushedMsg.Click" bundle="${lang}"/> <a href="<%=refresh%>"><fmt:message key="viewPushedMsg.HERE" bundle="${lang}"/></a> <fmt:message key="viewPushedMsg.refresh" bundle="${lang}"/>
                                                         </p>
							</td>							
					</tr>
					<tr><!-- Row 2 -->
							<td class="sortableHeader" nowrap><fmt:message key="viewPushedMsg.msgId" bundle="${lang}"/></td>
							<td align="left" class="sortableHeader" width='20%' nowrap><fmt:message key="viewPushedMsg.Date" bundle="${lang}"/></td>
							<td class="sortableHeader" align="center" width='20%'><fmt:message key="viewPushedMsg.TOOLS" bundle="${lang}"/></td>
					</tr>
					<% 
                                                for (int j = 0; j < files.length; j++) {
                                                    fileAbsoluteName = (file = files[j]).getAbsolutePath();
                                                    fileName = file.getName();
                                                    String removeFile = "removePushedFileRequest.jsp?fileName=" + fileName;
                                                    boolean show = false;
                                                    String showFile;
                                                    if (!(fileName.endsWith(".xml"))) {
                                                        continue;
                                                    }
                                                    if ((showFile = request.getParameter("show")) != null && showFile.equals(fileName)) {
                                                        show = true;
                                                    }
                                               
					%>

						<tr><!-- Row 2 -->
                                                        <%String href= ("/TOOLBOX/Push/" + fileName);%>
							<td class="sortable" colspan=1 nowrap><a href="<%=href%>"><%= fileName %></a></td>
							<td align="left" class="sortable" width='20%' colspan=1 nowrap><%=new Date(file.lastModified()).toString()%></td>
							<td class="sortable" align="left" width='20%'>
                                                            <%if (show) {%>
                                                                <INPUT type="button" onclick="closeggg()" value="<fmt:message key="viewPushedMsg.Close" bundle="${lang}"/>">
                                                            <%} else {%>
                                                                <INPUT type="button" onclick="<%="show" + Integer.toString(j) + "()"%>" value="<fmt:message key="viewPushedMsg.View" bundle="${lang}"/>">
                                                            <%}%>
                                                            <INPUT type="button" onclick="confirm('<%= response.encodeURL("manager?cmd=delPushedMsg&name=" + fileName) %>', '<fmt:message key="pushServer.delete" bundle="${lang}"/>', '<fmt:message key="Confirm" bundle="${lang}"/>');" value="<fmt:message key="viewPushedMsg.Delete" bundle="${lang}"/>"></td>
						</tr>
					<%          if (show) {
                                                    Document fileDoc;
                                                    domUtil.indent(fileDoc = domUtil.fileToDocument(fileAbsoluteName));
                                                    %>

						<tr><!-- Row 2 -->
							<td class="sortable" height="40" colspan=3 nowrap><textarea rows="25" cols="90" readonly><% XSLT.serialize(fileDoc, new StreamResult(out));%></textarea></td>
						</tr>
					<%  
                                                        
                                                    }
                                                }
					%>
					</table>
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 			
<jsp:include page="footer.jsp"/>

