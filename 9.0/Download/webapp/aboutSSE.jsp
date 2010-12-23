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
 -  File Name:         $RCSfile: aboutSSE.jsp,v $
 -  TOOLBOX Version:   $Name: V6_0 $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2006/10/12 11:46:09 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    String bc ="<a href='main.jsp'>Home</a>&nbsp;&gt;" + "&nbsp; About Toolbox";
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><fmt:message key="aboutSSE.about" bundle="${lang}"/></P> 
                <table cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="tdHelp" valign="top"><p align="justify">
					The TOOLBOX  is a module that facilitate the integration of services in the SSE  framework. Moreover it provides an easy way to deploy generic message based Web Services.                    
                      <p align="justify">SSE (Service Support Environment) aims at providing an e-business architecture for the integration of end-to-end Web services in the Earth Observation (EO) activities. The main part of this architecture is be an Internet Portal where users, Service Providers and data providers will be integrated in a coherent supply chain.
  
At the longer term, SSE should become an ESA standard Internet interface to EO thematic applications. It will support cost effective and easy production of thematic products that integrate EO products and geodata from different providers.                      
                      <p align="justify">SSE is a collection of functionality, infrastructure components and gateways that provides an open service-oriented and distributed environment among business users (service users and service providers). Via SSE , users and service providers can interact automatically and dynamically to exchange services. 
	                </td>
                  </tr>
                </table>                <p align="justify">&nbsp;</p>		</TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

