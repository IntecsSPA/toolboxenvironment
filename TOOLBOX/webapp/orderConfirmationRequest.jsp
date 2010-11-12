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
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

 <% 
     Enumeration instances = request.getParameterNames();
     Vector filteredIds=new Vector();
     Vector screenNames=new Vector();   
     Vector ids=new Vector();
     
    while(instances.hasMoreElements())
     {
        String id=(String) instances.nextElement();
        if(id.startsWith("CONFIRM"))
            {
               /* screenNames.add(request.getParameter("SCREENNAME"+id.substring(7)));
                   
                filteredIds.add(id.substring(7));*/
                ids.add(id.substring(7));
            }
     }
     
  /*  request.setAttribute("names",screenNames);
    request.setAttribute("ids",filteredIds);*/
     request.setAttribute("ids",ids);
%>

<jsp:include page="header.jsp" />

<SCRIPT language="JavaScript">
<!--
function back() 
{  
  window.location="<%= response.encodeURL("viewOrderConfirmations.jsp?serviceName="+request.getParameter("serviceName"))%>";
} 

//-->
</SCRIPT>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
          
      <form name="confirmationForm" method="post" action="orderConfirmationSubmit.jsp">        
          <table width="70%" cellspacing="2" cellpadding="2" align="center"> 
            <thead>
                <tr class="sortable"><td width="20%">ID</td><td width="80%">Error messages (If field is filled the error status is automatically set to order confirmation)</tr>
            </thead>
              
              <% 
                    Enumeration en=ids.elements();
                    String id;
                    
                    while(en.hasMoreElements())
                        {
                            id=(String)en.nextElement();
                        %>
                        <tr><td>
                        <%= request.getParameter("SCREENNAME" + id)%><input type="hidden" name="ID<%=id%>" value="<%=id%>"/></td><td><input type="text" size="100"  name="ERROR<%=id%>" value=""/>
                           
                        </td></tr>
                            <%
                           
                        }
              
              
              %>
              
              <tr><td>
              <input type="button" value="<< <fmt:message key="deleteSynchronousInstanceRequest.Back" bundle="${lang}"/>" onClick="back()">   <input type="submit" value="<fmt:message key="orderConfirmation.confrmButton" bundle="${lang}"/>">          </td><td>
                    
                 
              </td></tr>
          </table>
          
      </form>
  
     </TD> 
    </TR> 
  </TBODY> 
</TABLE> 
  <jsp:include page="footer.jsp"/>
