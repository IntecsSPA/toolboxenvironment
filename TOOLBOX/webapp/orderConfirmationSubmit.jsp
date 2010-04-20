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
<%@ page language="java" import="java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

 <% 
       String rootDir;
        String ErrorMsg;
        String fullid;
        String id;
        rootDir=getServletContext().getRealPath("/");
        File webInfDir=new File(rootDir,"WEB-INF");
        File dbDir=new File(webInfDir,"db");
               
        String dbStr=("jdbc:hsqldb:file:"+dbDir.getAbsolutePath()+File.separatorChar+"TOOLBOX").trim();
  
        request.setAttribute("dbStr",dbStr);        
        
     Enumeration instances = request.getParameterNames();
     Vector confirmedId=new Vector();
     Vector errorId=new Vector();
    while(instances.hasMoreElements())
     {
        fullid=(String) instances.nextElement();
        if(fullid.startsWith("ID"))
            {
                id=fullid.substring(2);
                ErrorMsg=request.getParameter("ERROR"+id);
                
                if(ErrorMsg.equals(""))
                    confirmedId.add(id);
                else
                    {
                         errorId.add(new String[]{id,ErrorMsg});
                }
            }     
     }
     
     
     
   request.setAttribute("confirmedId",confirmedId);
   request.setAttribute("errorId",errorId);

%>
<sql:setDataSource   user="toolbox" password="intecs" url="${dbStr}" driver="org.hsqldb.jdbcDriver" />
<c:forEach var="item" items="${confirmedId}">
        <sql:update>update t_sync_points set status='CONFIRMED' where id=? <sql:param value="${item}"/></sql:update>
 </c:forEach>
 <c:forEach var="item" items="${errorId}">
        <sql:update>update t_sync_points set status='ERROR',ERRORMSG=? <sql:param value="${item[1]}"/> where id=? <sql:param value="${item[0]}"/></sql:update>
 </c:forEach>
<jsp:include page="header.jsp" />

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        
        
        
          <table width="50%" valign="middle" height="90%" cellspacing="1" cellpadding="2" align="center"> 
              <tr class="sortable"><td><fmt:message key="orderConfirmation.confrmationStatement" bundle="${lang}"/></td></tr>
              
                  <tr><td>
                       
                  </td></tr>
             
          </table>
          

          

     </TD> 
    </TR> 
  </TBODY> 
</TABLE> 
  <jsp:include page="footer.jsp"/>
