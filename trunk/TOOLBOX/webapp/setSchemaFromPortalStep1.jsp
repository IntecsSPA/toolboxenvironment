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
  window.location="<%= response.encodeURL("configureServiceRequest.jsp?" + request.getQueryString())%>";
}
 
</SCRIPT>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
    <TD class=pageBody id=main> 
  <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
    <TBODY> <TR> 
       <TD id=main> 

            <P class=arbloc><FONT class=arttl><fmt:message key="getDownloadSchemaRequest.SSE" bundle="${lang}"/></FONT></P> 
            <form  method="post" action="<%= response.encodeURL("setSchemaFromPortalStep2.jsp")%>" >
               <input type="hidden" name="serviceName" value="<%= request.getParameter("serviceName")%>"> 
               <input type="hidden" name="download" value="<%= request.getParameter("download")%>">
<% if (request.getParameter("importOrCreate") != null) {%>
               <input type="hidden" name="importOrCreate" value="create">
<%}%>               
	    <!-- Page contents table-->
               <table width="100%" cellspacing="2" cellpadding="2" align="center"> 
                  <tr><!-- Row version -->
                      <td class="sortable"  colspan=2 nowrap>
                       <fmt:message key="getDownloadSchemaRequest.serviceName" bundle="${lang}"/><%= request.getParameter("serviceName")%> <br></td>
		  </tr>
  
  	<%
               String[] versions = URLReader.getVersionsToDW();
               int index = 0;
               int ix = 0;
               while(index < versions.length ) {
                   String versionN = versions[index];
                   String versionToPrint = "";
                   if( (ix = versionN.indexOf("/") ) > 0 )
                     versionToPrint = versionN.substring(0, ix);
                   if( index == (versions.length -1) ) {
                 %>
		  <tr><!-- Row version -->
                      <td class="sortable"  colspan=2 nowrap>
                         <input type="radio" name="versionToImport" value="<%=versionN%>" checked />
                         <%=versionToPrint%><br></td>
		  </tr>
                  <% } else { %>
		  <tr><!-- Row version -->
                      <td class="sortable"  colspan=2 nowrap>
                     <input type="radio" name="versionToImport" value="<%=versionN%>" />
                         <%=versionToPrint%><br></td>
		  </tr>                       
                 <%
                   }
                index++;
               }           
			 %> 
	</table>
       
        
          <tr> 
            <td class="sortable" colspan="3" rowspan="2" nowrap align="right">
                <div align="right">     
                    <input type="submit" value="<fmt:message key="getDownloadSchemaRequest.getVersFile" bundle="${lang}"/>">
                </div>
            </td> 
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
