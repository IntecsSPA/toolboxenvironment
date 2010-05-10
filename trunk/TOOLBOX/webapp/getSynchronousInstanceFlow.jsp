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
 -  File Name:         $RCSfile: getSynchronousInstanceFlow.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.11 $
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
                                 it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>

<jsp:include page="header.jsp?extVers=2.0.1" />
<script src="jsScripts/import/gis-client-library/import/OpenLayers/lib/OpenLayers.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>


<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/widgets/style/css/webgis.css" />

<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Control/Identify.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/Control/Map.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/Control/Toc.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/Control/ScaleList.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/MapAction/MapAction.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/MapAction/Basic.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/MapAction/Editor.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/MapAction/Measure.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/MapAction/Identify.js"></script>

<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/style/locale/en.js"></script>

<script type="text/javascript" src="jsScripts/widgets/mapWin/mapWin.js"></script>

<!-- Widget SelectionTool import -->
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Control/SelectWMS.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Control/RemoveSelectWMS.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Control/SelectionToolbar.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Filter/GeoSpatial.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Format/FILTER.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/SLDWMS.js"></script>


<%
            String service = request.getParameter("serviceName");
            String instanceId = request.getParameter("id");
            TBXService serv = ServiceManager.getInstance().getService(service);
            String operation = InstanceInfo.getOperationNameFromInstanceId(Long.parseLong(instanceId));
            boolean hasSSE = serv.hasSSEStylesheet(operation);
            boolean hasGML = serv.hasGMLOnMapStylesheet();
            DOMUtil util;

            util = new DOMUtil();
            PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
            String home = (String) messages.getObject("getSynchronousInstanceFlow.home");
            String monitoring = (String) messages.getObject("getSynchronousInstanceFlow.monitoring");
            String synch = (String) messages.getObject("getSynchronousInstanceFlow.synch");
            String flow = (String) messages.getObject("getSynchronousInstanceFlow.flow");

            String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="
                    + service + "'>&nbsp;" + monitoring + "</a>&nbsp;&gt;<a href='viewServiceInstances.jsp?instanceType=S&serviceName="
                    + service + "'>&nbsp;" + synch + "</a>&nbsp;&gt; " + flow;
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
        if(type=='SSEOutput')
            openTab('xml','Tab', "manager?cmd=getOpTestOutputPage&" + parameters, 'SSE Output ');
        if(type=='GMLOutput')
            mapWin.viewMapGML('manager?cmd=getGMLForMessage&' + parameters);
      
    }
  
</SCRIPT>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY>
        <TR>
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                    <TBODY>
                        <TR>
                            <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="getSynchronousInstanceFlow.flow" bundle="${lang}"/></FONT></P>
                                <!-- Page contents table-->
                                <P>
                                    <%
                                                byte status;

                                                status = InstanceStatuses.getInstanceStatus(Long.parseLong(instanceId));
                                                Document xslDocument = util.fileToDocument(new File(application.getRealPath("WEB-INF/XSL/synchronousInstanceFlow_" + status + ".xsl")));


                                                Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
                                                //transformer.setParameter("serviceName", service);
                                                transformer.setParameter("operationName", "");
                                                transformer.setParameter("language", session.getAttribute("languageReq"));
                                                transformer.setParameter("hasSSE", Boolean.toString(hasSSE));
                                                transformer.setParameter("hasGML", Boolean.toString(hasGML));
                                                transformer.transform(new StreamSource(InstanceFlow.getSynchronousInstanceFlowAsXML(service, instanceId)), new StreamResult(out));
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

