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
 -  File Name:         $RCSfile: getAsynchronousInstanceFlow.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.10 $
 -  Revision Date:     $Date: 2006/10/11 10:18:22 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.db.*,
                                it.intecs.pisa.toolbox.service.*,
                                it.intecs.pisa.toolbox.service.instances.*,
                                it.intecs.pisa.soap.toolbox.service.*,
                                java.util.*,
                                org.w3c.dom.*,
                                javax.xml.parsers.*,
                                java.io.*,
                                it.intecs.pisa.util.*,
                                javax.xml.transform.*,
                                javax.xml.transform.dom.*,
                                javax.xml.transform.stream.*,
                                it.intecs.pisa.soap.toolbox.*,
                                it.intecs.pisa.toolbox.configuration.*"  errorPage="errorPage.jsp" %>

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
    String instanceId = request.getParameter("id");
    ServiceManager servman;

    servman=ServiceManager.getInstance();
    TBXService serv=servman.getService(service);
    
    String operation = InstanceInfo.getOperationNameFromInstanceId(Long.parseLong(instanceId));
    boolean hasSSE=serv.hasSSEStylesheet(operation);
    boolean hasGML=serv.hasGMLOnMapStylesheet();
    DOMUtil util=new DOMUtil();
    
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("getAsynchronousInstanceFlow.home");
    String monitor = (String)messages.getObject("getAsynchronousInstanceFlow.monitor");
    String asynch= (String)messages.getObject("getAsynchronousInstanceFlow.asynch");
    String flow = (String)messages.getObject("getAsynchronousInstanceFlow.flow");
    String showEmail="false";

    ToolboxConfiguration toolboxConfiguration;
    toolboxConfiguration = ToolboxConfiguration.getInstance();

    String destinations=toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR);
    if(destinations!=null && destinations.equals("")==false)
    {
        showEmail="true";
    }

    String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="
                +service+"'>&nbsp;"+monitor+"</a>&nbsp;&gt;<a href='viewServiceInstances.jsp?instanceType=A&serviceName="
                +service+"'>&nbsp;"+asynch+"</a>&nbsp;&gt;" + flow;
/*
    String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="
                +service+"'>&nbsp;MonitoringCenter</a>&nbsp;&gt;<a href='viewServiceInstances.jsp?instanceType=A&serviceName="
                +service+"'>&nbsp;Asynchronous Service instances</a>&nbsp;&gt; Instance flow";
*/ 
%>

<SCRIPT language="JavaScript">
function viewResource(type,parameters,label)
{
   if(type == 'xml')
     openTab('xml','Tab', "manager?cmd=getXMLResource&output=text&" + parameters, 'XML ' + label);
   if(type == 'tree')
     openTab('tree','Tab', "manager?cmd=getXMLResource&output=xml&" + parameters, 'TREE '+label);
   if(type == 'text')
     downloadPopup ("manager?cmd=getXMLResource&output=xml&" + parameters);
   if(type=='email')
      openTab('xml','Tab', "manager?cmd=getTextResource&" + parameters, 'Error email');
   if(type=='SSEOutput')
      openTab('xml','Tab', "manager?cmd=getOpTestOutputPage&" + parameters, 'SSE Output ');  
   if(type=='GMLOutput')
      openTab('xml','Tab', "manager?cmd=getGMLForMessage&" + parameters, 'GML Output ');  
  
} 
</SCRIPT>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/monitoringCenter.html_Monitoring_center*RE/asynchronousInstances.html_Listing_asynchronous_instances*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="getAsynchronousInstanceFlow.flow" bundle="${lang}"/></FONT></P> 
                <!-- Page contents table--> 
                <P>  
<%
            byte status;
    
            status=InstanceStatuses.getInstanceStatus(Long.parseLong(instanceId));
            Document xslDocument = util.fileToDocument(new File(application.getRealPath("WEB-INF/XSL/asynchronousInstanceFlow_"+status+".xsl")));

            Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
            transformer.setParameter("serviceName", service);
            transformer.setParameter("operationName", operation);
            transformer.setParameter("instanceKey", instanceId);
            transformer.setParameter("language", session.getAttribute("languageReq"));
            transformer.setParameter("hasSSE", Boolean.toString(hasSSE));
            transformer.setParameter("hasGML", Boolean.toString(hasGML));
            transformer.setParameter("showEmail", showEmail);
            transformer.transform(new StreamSource(InstanceFlow.getAsynchronousInstanceFlowAsXML(service,instanceId)), new StreamResult(out));
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
