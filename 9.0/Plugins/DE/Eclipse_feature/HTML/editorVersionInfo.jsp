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
 -  File Name:         $RCSfile: editorVersionInfo.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.3 $
 -  Revision Date:     $Date: 2006/12/11 12:37:25 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp" %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<jsp:include page="header.jsp" />


<p><big><big> TOOLBOX Development Plugin version @@@</big></big><br>
  
  </p>
<p><em><strong>Features:</strong></em></p>
<ul>
  <li>Create new TOOLBOX projects and TOOLBOX scripts</li>
  <li>Script validation on the fly (no offline validation)</li>
  <li>Visualization of validation error</li>
  <li>Enhanced tree visualization of the script </li>
  <li>Assisted script creation</li>
  <li>Debugging of script without the need of an installed TOOLBOX</li>
  <li>Automatic update feature through Intecs TOOLBOX server</li>
  <li>Multiple running configuration with massHost, xmlRequest and orderId settings</li>
  <li>Wizards for Projects, xml scripts and xml requests</li>
</ul>
<p><br>
  
  <br>
  Download Files<br>
  
  <a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugIn/offline-install.zip">offline-install.zip</a> Click and download it on your local drive. Unpack it into the Eclipse
  directory<br>
<a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugIn/offline-install.libs.zip">offline-install.libs.zip</a> Click and download it on your local drive. Unpack it into the Eclipse directory</p>
<jsp:include page="footer.jsp"/>
