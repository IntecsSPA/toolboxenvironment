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
 -  File Name:         $RCSfile: getDownloadSchemaRequest.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.12 $
 -  Revision Date:     $Date: 2007/02/05 15:39:38 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, org.w3c.dom.*,javax.xml.transform.dom.*,  javax.xml.transform.stream.*, javax.xml.parsers.*,it.intecs.pisa.util.*, java.util.*,java.io.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

    <link href="jsScripts/dom.css" rel=stylesheet></link>
    <link href="jsScripts/dom.directory.css" rel=stylesheet></link>
    <link href="jsScripts/portal.css" rel=stylesheet></link>
    <link href="jsScripts/styles.css" rel=stylesheet></link>
<SCRIPT language="JavaScript">
<!--
function gothere() 
{ 
  window.location="<%= response.encodeURL("setSchemaFromPortalStep1.jsp?" + request.getQueryString())%>";
}
 
</SCRIPT>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
    <TD class=pageBody id=main> 
  <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
    <TBODY> <TR> 
       <TD id=main> 
<% 
           int ix = request.getParameter("versionToImport").indexOf("/");
           String versName = request.getParameter("versionToImport");
           if ( ix >= 0) {versName = versName.substring( 0, ix);}
       %>
            <P class=arbloc><FONT class=arttl><fmt:message key="getDownloadSchemaRequest.download" bundle="${lang}"/> <%=versName%></FONT></P> 
            <form  method="post" action="<%= response.encodeURL("setSchemaFromPortalConfirmation.jsp")%>" >
              <input type="hidden" name="serviceName" value="<%= request.getParameter("serviceName")%>"> 
              <input type="hidden" name="download" value="<%= request.getParameter("download")%>">
              <input type="hidden" name="versionToImport" value="<%= request.getParameter("versionToImport")%>">
<% if (request.getParameter("importOrCreate") != null) {%>
               <input type="hidden" name="importOrCreate" value="create">
<%}%>               
	    <!-- Page contents table-->
		<table width="100%" cellspacing="1" cellpadding="0" align="center">
                  <tr><!-- Row version -->
                      <td class="sortable"  colspan=2 nowrap>
                       <fmt:message key="getDownloadSchemaRequest.serviceName" bundle="${lang}"/><%= request.getParameter("serviceName")%> <br></td>
		  </tr>
                    
  	<%    
               String tmpvers = request.getParameter("versionToImport");
//               DOMUtil.dumpXML(URLReader.getDirectoryStructure(tmpvers),new File( Toolbox.getToolboxConfigurator().getToolboxRoot(), "text.xml"));

        Document xslDocument;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/urlContent.xsl")));

        XSLT.transform(new DOMSource(xslDocument), new StreamSource(DOMUtil.getDocumentAsInputStream(URLReader.getDirectoryStructure(tmpvers))), new StreamResult(out));               
%>
	    </table>
             
        
          <tr> 
            <!-- Last Row --> 
            <td class="sortable" colspan="3" rowspan="2" nowrap align="right"><div align="right">
                <input type="button" value="<< <fmt:message key="getDownloadSchemaRequest.Back" bundle="${lang}"/>" onclick="gothere()"> 
                <input type="submit" value="<fmt:message key="getDownloadSchemaRequest.Import" bundle="${lang}"/>">
            </div></td> 
          </tr> 
        </table> 
          </form> 
    </TD> 
    </TR> 
    </TBODY> 
  </TABLE> 
  </TD> 
  </TR> 
   </TBODY> 
 </TABLE> 
