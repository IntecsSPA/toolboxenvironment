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
 -  File Name:         $RCSfile: viewServiceInstances.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.19 $
 -  Revision Date:     $Date: 2007/01/16 12:53:27 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.configuration.*,java.text.*,org.hsqldb.*,java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.toolbox.service.*,it.intecs.pisa.toolbox.*,java.sql.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>

<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<jsp:include page="header.jsp" />

<% 
        Toolbox tbxInstance=Toolbox.getInstance();
        
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("viewServiceInstances.home");
        String monitoring = (String)messages.getObject("viewServiceInstances.monitoring");
        String service = request.getParameter("serviceName");
  
        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="+service+"'>&nbsp;"+monitoring+"</a>&nbsp;&gt;&nbsp; View Order Confirmation";
        int instanceNum = 0;
        
        String rootDir;
        
        rootDir=getServletContext().getRealPath("/");
        File webInfDir=new File(rootDir,"WEB-INF");
        File dbDir=new File(webInfDir,"db");
               
        String dbStr=("jdbc:hsqldb:file:"+dbDir.getAbsolutePath()+File.separatorChar+"TOOLBOX").trim();
          
        request.setAttribute("serviceName",service);
        request.setAttribute("dbStr",dbStr);        
        
        
        SimpleDateFormat fromFormatter=new SimpleDateFormat("yyyyMMdhhmmss");
        SimpleDateFormat toFormatter=new SimpleDateFormat();
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <sql:setDataSource   user="TOOLBOX" password="intecs" url="${dbStr}" driver="org.hsqldb.jdbcDriver" />
        <sql:query  var="confirmations">select id,mnemonicid,TO_CHAR (creationdate, 'DD/MM/YYYY HH24:MI:SS' ),operation,instancekey  from t_sync_points where service=? <sql:param value="${serviceName}"/> and purpose='ORDER' and status='NO_SYNC'</sql:query>

        
    <SCRIPT language="JavaScript">
<!--

function selectAll()
{
      <c:forEach var="row" items="${confirmations.rows}">
    	if(document.confirmationForm.CONFIRM<c:out value="${row.id}"/>)
        	document.confirmationForm.CONFIRM<c:out value="${row.id}"/>.checked = true;      
   </c:forEach>
} 

function toggleAll()
{
  <c:forEach var="row" items="${confirmations.rows}">
    	if(document.confirmationForm.CONFIRM<c:out value="${row.id}"/>)
        {
            if(document.confirmationForm.CONFIRM<c:out value="${row.id}"/>.checked==true)
                    document.confirmationForm.CONFIRM<c:out value="${row.id}"/>.checked = false; 
            else
                    document.confirmationForm.CONFIRM<c:out value="${row.id}"/>.checked = true;
         }
    </c:forEach>
} 

function viewResource(parameters) 
{
       if (document.all) {
                               window.showModelessDialog( "getResource.jsp?" + parameters + "&outputType=TREE", 
                               "popup", // Pass as object reference to bypass size limitation 
                               "dialogHeight:730px; dialogwidth:842px; center: Yes; help: No; resizable: Yes; status: No;"
                               );
       }
       else {
                               window.open("getResource.jsp?" + parameters + "&outputType=TREE",'resource', 'width=842,height=730,status=yes');
       }
  }

//-->
</SCRIPT>  

        
        
        
        <form name="confirmationForm" method="post" action="orderConfirmationRequest.jsp">            
        
        <TABLE width="100%" valign="middle" height="90%" cellspacing="1" cellpadding="2" align="center"> 
            <%-- Get the column names for the header of the table --%>
            <th class="sortable">Confirm</th>
            <th class="sortable">Identifier</th>
            <th class="sortable">Creation Date</th>
            <th class="sortable">Operation</th>
            <th class="sortable">View incoming request</th>
            
            <%-- Get the value of each column while iterating over rows --%>
            <c:forEach var="row" items="${confirmations.rows}">
                <tr>
                    <td>
                            <input type="checkbox" name="CONFIRM<c:out value="${row.id}"/>"/>
                            <input type="hidden" name="SCREENNAME<c:out value="${row.id}"/>" value="<c:out value="${row.mnemonicid}"/>"/>
                    </td>
                     <td><c:out value="${row.mnemonicid}"/></td>
                     <td><c:out value="${row.creationdate}"/></td>
                     <td><c:out value="${row.operation}"/></td>
                     <td>
                            <%
                                TreeMap ithRow=(TreeMap)pageContext.getAttribute("row");
                                String operation=(String)ithRow.get("operation");
                                String instKey=(String)ithRow.get("instanceKey");

                                ServiceManager servMan;
                                servMan=ServiceManager.getInstance();
                                TBXService serv=servMan.getService(service);
                                String operType=serv.getOperationType(operation);
                                
                                operType=operType.substring(0,1).toUpperCase();

                                ToolboxConfiguration conf;
                                conf=ToolboxConfiguration.getInstance();

                                String logDir=conf.getConfigurationValue(ToolboxConfiguration.LOG_DIR);
                                File serviceLogDir=new File(logDir,service);
                                File operationDir;
                                
                                if(operType.equals("S"))
                                    operationDir=new File(serviceLogDir,"synchronousInstances");
                                   else operationDir=new File(serviceLogDir,"asynchronousInstances");
                                
                                  File instDir=new File(operationDir,instKey);
                                  String[] files=instDir.list();
                                  
                                  pageContext.setAttribute("xmlResource","");
                                  
                                  if(files!=null)
                                 { 
                                      for(int i=0;i<files.length;i++)
                                      {
                                            if(files[i].indexOf("inputMessage")!=-1)
                                             pageContext.setAttribute("xmlResource",files[i]);
                                  }
                                   }
                            %>
                            <a href="javascript:viewResource('serviceName=<c:out value="${serviceName}"/>&outputType=XML&&instanceType=<%=operType%>&instanceId=<c:out value="${row.instancekey}"/>&resourceKey=<c:out value="${xmlResource}"/>')"><img src="images/xml-icon.jpg" alt="XML view"></a>
                            <a href="javascript:viewResource('serviceName=<c:out value="${serviceName}"/>&outputType=TREE&&instanceType=<%=operType%>&instanceId=<c:out value="${row.instancekey}"/>&resourceKey=<c:out value="${xmlResource}"/>')"><img src="images/tree-icon.jpg" alt="tree view"></a>
                     </td>
                </tr>
            </c:forEach>
            
        
        </TABLE>
               <input type="hidden" name="serviceName" value="<c:out value="${serviceName}"/>"/>
            <input type="button" name="select" value="<fmt:message key="viewServiceInstances.toggleAll" bundle="${lang}"/>" onClick="javascript:toggleAll()"/>
            <input type="submit" value="<fmt:message key="orderConfirmation.confirm" bundle="${lang}"/>">            
            </form>
    </TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>