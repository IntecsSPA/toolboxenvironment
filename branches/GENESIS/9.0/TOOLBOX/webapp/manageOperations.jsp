<%@page import="it.intecs.pisa.toolbox.configuration.ToolboxNetwork"%>
<%@page import="it.intecs.pisa.toolbox.configuration.ToolboxConfiguration"%>
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
 -  File Name:         $RCSfile: manageOperations.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.11 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.*,java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<jsp:include page="header.jsp?extVers=3" />
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

<!--script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/jquery/jquery-1.2.6.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/jquery/ext-jquery-adapter.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ext-all.js"></script-->
<script src="jsScripts/import/gis-client-library/import/OpenLayers/lib/OpenLayers.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Format/XMLKeyValue.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/Panel/WindowInterfacePanel.js"></script>

<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/manager.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/browserDetect.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/XmlDoc.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ux/Spotlight.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/ExtFormUtils.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>


<script type="text/javascript" src="jsScripts/wpsWizard/scripts/wpsWizardManager.js"></script>


<style type="text/css">

.upload-icon {
            background: url('images/image_add.png') no-repeat 0 0 !important;
        }

</style>
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
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/operationManagement.html_Operations_management*");</SCRIPT>
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
        Document xslDocument=null;
        if(serv.getFullSchemaPath().contains("WPSSoapAll.xsd"))
          xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/listWPSOperations.xsl"))); 
        else
	  xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/listOperations.xsl")));
	 //= docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/listOperations.xsl")));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
        transformer.setParameter("serviceName", service);
        transformer.setParameter("orderBy", orderBy);
        if(serv.getFullSchemaPath().contains("WPSSoapAll.xsd")){
            transformer.setParameter("proxyURL", ToolboxNetwork.getEndpointURL());
        }
        transformer.setParameter("viewWSDLInfoURL", response.encodeURL("viewWSDLInfo.jsp"));
        transformer.setParameter("configureOperationURL", response.encodeURL("configureOperation.jsp"));
        transformer.setParameter("deleteOperationURL", response.encodeURL("manager?cmd=deleteOperation"));
        transformer.setParameter("language", session.getAttribute("languageReq"));
        transformer.transform(new StreamSource(serv.viewDescriptorFile()), new StreamResult(out));
%></td>
								</tr>
                                            <tr>
                                                <% if(serv.getFullSchemaPath().contains("WPSSoapAll.xsd")){ %>
                                                       <td colspan="2" rowspan="2" nowrap align="right"><input id="buttonLabel" type="button" value="<fmt:message key="manageOperations.addWPS" bundle="${lang}"/> >>" onClick="wpsProcessingWizardManager()" > <!-- editDescribeProcess('wpsE', 'TestOperation')  -->
                                                <%}else{ %>
                                                       <td colspan="2" rowspan="2" nowrap align="right"><input id="buttonLabel" type="button" value="<fmt:message key="manageOperations.addOperation" bundle="${lang}"/> >>" onClick="gothere()" >
                                                <% }%>

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

