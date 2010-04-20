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
 -  File Name:         $RCSfile: toolboxUpgrade.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.10 $
 -  Revision Date:     $Date: 2006/10/09 08:39:23 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"   %>
<%@ page import="it.intecs.pisa.toolbox.configuration.*,it.intecs.pisa.soap.toolbox.*, java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*" %>
<%@ include file="checkAccount.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%
    String userName;
    String password;
    String upgradePage = "";
    try
            {
    userName = request.getParameter("userName").trim();
    password = request.getParameter("password").trim();                                      
    ToolboxConfiguration configuration;

    configuration=ToolboxConfiguration.getInstance();
    Boolean isFirstCheck;

    isFirstCheck=Boolean.valueOf(configuration.getConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK));
    if ( isFirstCheck )
               request.setAttribute("firstTime","true");
      else request.setAttribute("firstTime","false");
 %>              
 <c:choose>
 <c:when test="${firstTime=='true'}">
     <jsp:forward page="configureToolboxRequest.jsp">
         <jsp:param name="userName" value="<%=userName%>"/>      
         <jsp:param name="password" value="<%=password%>"/>      
         <jsp:param name="pageStatus" value="enabled"/>
     </jsp:forward>     
 </c:when>
 </c:choose>
  <% 
     
     if(session.getAttribute("languageReq") != null && session.getAttribute("languageReq").equals( java.util.Locale.FRENCH.toString() ) ) {
        URLReader.writeURLContentToXML(application.getRealPath("WEB-INF/xml/info.xml"),
                                       application.getRealPath("WEB-INF/toolboxUpgrade.xml"));
        if( !URLReader.isVersionToUpdate(application.getRealPath("WEB-INF/toolboxUpgrade.xml"), application.getRealPath("WEB-INF/xml/info.xml") ) ) {
       
  %>
     <jsp:forward page="main.jsp">
      <jsp:param name="userName" value="<%=userName%>"/>      
      <jsp:param name="password" value="<%=password%>"/>              
    </jsp:forward>                        
  <%
        }    
        Transformer transformer =  TransformerFactory.newInstance().newTemplates(new StreamSource(new File(application.getRealPath("WEB-INF/XSL/toolboxUpgrade_fr.xsl")))).newTransformer();
        DOMSource xmlDoc = new DOMSource(new DOMUtil(true).fileToDocument(new File(application.getRealPath("WEB-INF/toolboxUpgrade_fr.xml"))));
        StringWriter stringWriter = new StringWriter();
        transformer.transform(xmlDoc, new StreamResult(stringWriter));
        upgradePage = stringWriter.toString();
     } else {
        URLReader.writeURLContentToXML(application.getRealPath("WEB-INF/xml/info.xml"),
                                       application.getRealPath("WEB-INF/toolboxUpgrade.xml"));
        if( configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_VERSION_CHECK).equals("false") || !URLReader.isVersionToUpdate(application.getRealPath("WEB-INF/toolboxUpgrade.xml"), application.getRealPath("WEB-INF/xml/info.xml") ) ) {
  %>
     <jsp:forward page="main.jsp">
      <jsp:param name="userName" value="<%=userName%>"/>      
      <jsp:param name="password" value="<%=password%>"/>              
    </jsp:forward>                        
  <%
        }    
        Transformer transformer =  TransformerFactory.newInstance().newTemplates(new StreamSource(new File(application.getRealPath("WEB-INF/XSL/toolboxUpgrade.xsl")))).newTransformer();
        DOMSource xmlDoc = new DOMSource(new DOMUtil(true).fileToDocument(new File(application.getRealPath("WEB-INF/toolboxUpgrade.xml"))));
        StringWriter stringWriter = new StringWriter();
        transformer.transform(xmlDoc, new StreamResult(stringWriter));
        upgradePage = stringWriter.toString();
     } // if(session.getAttribute("languageReq") != null && session.getAttribute("languageReq").equals( java.util.Locale.FRENCH.toString() ) else

     }
    catch(Exception e)
            {
            userName="";
            password="";
        
        %>
        <jsp:forward page="main.jsp">
      <jsp:param name="userName" value="<%=userName%>"/>      
      <jsp:param name="password" value="<%=password%>"/>              
    </jsp:forward> 
    <%}
     %>
<SCRIPT language="JavaScript">
<!--
function goThere(page) 
{ 
   myRef = window.open(page,'newwin',
               'left=20,top=20,toolbar=1,resizable=0');
  window.location="main.jsp?userName=<%=userName%>&password=<%=password%>";
} 
function goMain() 
{ 
  window.location="toolboxUpgradeSubmit.jsp?userName=<%=userName%>&password=<%=password%>";
} 

//-->
</SCRIPT>    
<!--
  window.location=page;
-->
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing="0" cellPadding="0" width="100%" align="center"> 
  <TBODY>
    <form  method="post" action="<%= response.encodeURL("toolboxUpgradeSubmit.jsp")%>" >
      <input type="hidden" name="userName" value="<%= request.getParameter("userName")%>"> 
      <input type="hidden" name="password" value="<%= request.getParameter("password")%>">
    <TR> 
      <TD class="pageBody" id="main"><SCRIPT>addBreadCrumb("<a href='main.jsp'><fmt:message key="toolboxUpgrade.home" bundle="${lang}"/></a>&nbsp;&gt;" +
              "&nbsp;<fmt:message key="toolboxUpgrade.toolboxUpgrade" bundle="${lang}"/>");</SCRIPT> 
<p>
<%=upgradePage%>
</p>        
        </TD> 
    </TR> 
   </form>
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
