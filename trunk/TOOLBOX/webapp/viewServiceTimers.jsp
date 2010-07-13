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
 -  File Name:         $RCSfile: viewServiceTimers.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2006/10/09 08:39:23 $
 -
 -->
<%@ page language="java" import="java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.service.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<jsp:include page="header.jsp" />

<% 
	String service = request.getParameter("serviceName");

        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String status = (String)messages.getObject("viewServiceTimers.timerStatus");
        String monitoring = (String)messages.getObject("viewServiceTimers.monitoring");
       
        String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="+service+"'>&nbsp;"+monitoring+"</a>&nbsp;&gt; "+status;
%>
<SCRIPT language="JavaScript">
function viewResource(type,parameters,label)
    {
        if(type == 'xml')
            openTab('xml','Tab', "manager?cmd=getTimerScript&output=text&" + parameters, 'XML ' + label);
        if(type == 'tree')
            openTab('tree','Tab', "manager?cmd=getTimerScript&output=xml&" + parameters, 'TREE '+label);
        if(type == 'text')
            downloadPopup ("manager?cmd=getTimerScript&output=xml&" + parameters);
    }
 </SCRIPT>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/monitoringCenter.html_Monitoring_center*RE/timerStatus.html_View_the_timers_status*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewServiceTimers.timerStatus" bundle="${lang}"/></FONT></P> 
                <!-- Page contents table--> 
                <P>  
<%
            ServiceManager servMan;
            TBXService serv;

            servMan=ServiceManager.getInstance();
            serv=servMan.getService(service);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();        
            docFactory.setNamespaceAware(true);
            Document xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/timerDisplay.xsl")));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
            transformer.setParameter("language", session.getAttribute("languageReq"));
            transformer.setParameter("service", service);
            transformer.transform(new StreamSource(serv.getTimerStatus()), new StreamResult(out));
%>

</P>
		&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

