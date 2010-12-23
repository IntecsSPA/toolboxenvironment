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
 -  File Name:         $RCSfile: getDownloadSchemaSubmit.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.7 $
 -  Revision Date:     $Date: 2007/02/05 15:39:38 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.soap.toolbox.*, it.intecs.pisa.util.*, org.w3c.dom.*, javax.xml.transform.stream.*, java.io.*, java.util.*, org.apache.commons.fileupload.*" errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<% 
   String serviceName = request.getParameter("serviceName").trim();
   String versionName = request.getParameter("versionToImport").trim();   
   String download = request.getParameter("download").trim();   
   String warnMessage = "";
   String versionSchemaRequest = "getDownloadSchemaRequest.jsp";

   String urlVersion = "/schemas/"; 
   String fullString="";
  try {      
     Enumeration param = request.getParameterNames();
     // Name's file
     Vector fileToSave = new Vector();
     // Name's file with path
     String tmpString = null;
     
     while (param.hasMoreElements()) {
         tmpString = (String)param.nextElement();
         if( tmpString.startsWith("fileToImport")) {
           tmpString = tmpString.substring(12);
           //fileToSave.add(tmpString);
           
           fullString+=tmpString+" ";
          }
     }
      } catch (Exception e) {
            e.printStackTrace(System.out);
          warnMessage = "Not well-formed file!";
      }
   
   
%>
<html>
    <head>
        <script type="text/javascript">
        function load()
        {
           var label=parent.document.getElementById("portalSelectionString");
           label.innerHTML="<b>SSE schema version <%= versionName.substring(0,versionName.length()-1) %> will be imported</b>";                            
          
           var component= parent.document.getElementById("fullSchemaSet");
           component.value="<%= fullString %> ";
           
           var schemaZipButton=parent.document.getElementById("schemaZipButton");
           schemaZipButton.disable=true; 
           
            var button= parent.document.getElementById("portalSchemaButton");
           button.value="Cancel import from SSE portal";           
        }
        </script>
    </head>

<body onload="load()">
    <h1>Schema set successfully imported</h1>
</body>

</html>          
