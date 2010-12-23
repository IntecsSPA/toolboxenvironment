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
 -  File Name:         $RCSfile: overview.jsp,v $
 -  TOOLBOX Version:   $Name: V6_0 $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2007/01/31 12:33:36 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    String bc ="<a href='main.jsp'>Home</a>&nbsp;&gt;" + "&nbsp; Overview";
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><fmt:message key="aboutSSE.about" bundle="${lang}"/></P> 
                <table cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="tdHelp" valign="top">
					<h2>The context</h2>
					<p>The TOOLBOX has been developed under the SSE project.<br>
					SSE integrates service providers (having variety of component models or middle-ware suited for their different 
					platforms or environments) through XML, i.e. using SOAP and WSDL transported over a variety of transport mechanisms, 
					i.e. IP, HTTP, and HTTPS. Thus, any service to be integrated within the SSE infrastructure must expose a SOAP interface 
					(described by a WSDL file) according to the SSE Interface Control Document (SSE ICD). 
					<br>The TOOLBOX helps the service provider to convert its services in a SOAP based service, leveraging from all coding problems. 
					The real convertion is made by Toolbox executing a user-provided set of script files, through which the legacy service is invoked.
					The only development requested to the final user is to write and provide that set of Toolbox scripts.
					<br>A wide selection of legacy services can be converted by Toolbox thanks to its powerful and always growing scripting language.
					 Some examples are:</p>
					 <ul>
					 	<li>
							<p>Http</p>
						</li>
						<li>
							<p>Ftp</p>
						</li>
						<li>
							<p>Smtp</p>
						</li>
						<li><p>JDBC</p></li>
					 </ul>
					 <p>The Toolbox project is composed by two applications, a development environment and a run-time environment.<br>
					 The run-time environment is a web application that runs under Apache Tomcat. It conatins the script engine, used for invoking the legacy service, and some other services used for managing the whole system. A web interface is provided to the administrator in order to manager Toolbox and all deployed SOAP services. <br>
                       The development environment is an Eclipse plugin which provides some tool to easily create the XML scripts to be uploaded in the run-time environment. The built-in debug functionality of Eclipse is extended by the Development environment in order to execute and debug the script before deploying it into the Toolbox Runtime Environment.</p>
					 <p>The run-time and development environment can be used separately on different machines. Even if the XML scripts can be created with any tool supporting XML or with a text editor (the scripts have to be compliant to the XMLScript Schema), the use of the Toolbox Development Environment is highly recommended because all bundled functionality let the final developer write, run &amp; debug a Toolbox script directly into Eclipse, without the need of deploying it under the Toolbox Runtime Environment.</p>					 <p>Both Toolbox Runtime Environment and Toolbox Development Environment can be installed under Windows  and Linux platforms.</p>
					  <h2>New in this release </h2>
					  <p>Toolbox Runtime Environment (from 7.1 to 8.0)</p>
					  <ul>
                        <li>Support for ebRIM catalogue insterfaces</li>
                        <li>Toolbox 8.0 ships with a bundled EO Product ebRIM catalogue. Multiple catalogue services can be instantiated.</li>
                        <li>Code rebuilt in some sector in order to enhance speed and reliability.</li>
                        </ul>
					  <p>Toolbox Development Environment (version 1.3)</p>
					  <ul>
					  	<li>Interface repository aligned to the one in Toolbox RE 8.0</li>
						<li>Checked to run against Toolbox 8.0.</li>
	
					  </ul>
					 
					  
                  </tr>
                </table>                <p align="justify">&nbsp;</p>		</TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

