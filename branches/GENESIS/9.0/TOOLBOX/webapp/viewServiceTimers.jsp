<%@page import="java.text.SimpleDateFormat"%>
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
 -  File Name:         $RCSfile: viewServiceTimers.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2006/10/09 08:39:23 $
 -
 -->
<%@ page language="java" import="java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.service.*"  errorPage="errorPage.jsp" %>

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
	String service = request.getParameter("serviceName");

        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String status = (String)messages.getObject("viewServiceTimers.timerStatus");
        String monitoring = (String)messages.getObject("viewServiceTimers.monitoring");
       
        String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="+service+"'>&nbsp;"+monitoring+"</a>&nbsp;&gt; "+status;

        String rootDir;

        rootDir=getServletContext().getRealPath("/");
        File webInfDir=new File(rootDir,"WEB-INF");
        File dbDir=new File(webInfDir,"db");

        String dbStr=("jdbc:hsqldb:file:"+dbDir.getAbsolutePath()+File.separatorChar+"TOOLBOX").trim();

        request.setAttribute("serviceName",service);
        request.setAttribute("dbStr",dbStr);

        SimpleDateFormat toFormatter=new SimpleDateFormat();
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
    }
 </SCRIPT>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/monitoringCenter.html_Monitoring_center*RE/timerStatus.html_View_the_timers_status*");</SCRIPT>
        <sql:setDataSource   user="TOOLBOX" password="intecs" url="${dbStr}" driver="org.hsqldb.jdbcDriver" />
        <sql:query  var="timers">select EXTRA_VALUE,DUE_DATE,SCRIPT from T_TIMERS where TYPE='TIMER' and INSTANCE_ID in (select ID from T_SERVICE_INSTANCES where SERVICE_NAME='<%=service%>')</sql:query>


        <TABLE width="70%" valign="middle" height="90%" cellspacing="1" cellpadding="2" align="center">
            <th class="sortable"><fmt:message key="common.identifier" bundle="${lang}"/></th>
            <th class="sortable"><fmt:message key="common.due_date" bundle="${lang}"/></th>
            <th class="sortable"><fmt:message key="common.view" bundle="${lang}"/></th>


            <c:forEach var="row" items="${timers.rows}">
                <tr>
                     <td><c:out value="${row.extra_value}"/></td>
                     <td><%
                            TreeMap ithRow=(TreeMap)pageContext.getAttribute("row");
                            Long duedate=(Long)ithRow.get("due_date");

                            out.write(toFormatter.format(new Date(duedate)).toString());
                      %></td>
                     <td><a href="javascript:viewResource('tree','id=<c:out value="${row.script}"/>','Timer <c:out value="${row.extra_value}"/>')"><img src="images/xml-icon.jpg" alt="XML view"></a>
                         <a href="javascript:viewResource('xml','id=<c:out value="${row.script}"/>','Timer <c:out value="${row.extra_value}"/> (XML)')"><img src="images/tree-icon.jpg" alt="tree view"></a></td>
                </tr>

            </c:forEach>

        </TABLE>
      </TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

