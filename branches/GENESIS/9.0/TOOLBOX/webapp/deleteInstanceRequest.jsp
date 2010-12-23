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
 -  File Name:         $RCSfile: deleteSynchronousInstanceRequest.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.9 $
 -  Revision Date:     $Date: 2006/10/09 08:39:23 $
 -
 -->
<%@ page language="java" import="java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>

<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<% 
    String service = request.getParameter("serviceName");
    String opType = request.getParameter("opType");
  
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("deleteSynchronousInstanceRequest.home");
    String delConfirm = (String)messages.getObject("deleteSynchronousInstanceRequest.delConfirm");

    String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + delConfirm;
%>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="deleteSynchronousInstanceRequest.delSConfirm" bundle="${lang}"/></FONT></P> 
                <P>  
           
				<!-- Page contents table-->
					<table width="80%" cellspacing="2" cellpadding="2" align="center">
                                                <!--form method="post" action="deleteSynchronousInstanceSubmit.jsp">
                                                    <input type="hidden" name="serviceName" value="<%=service%>"/-->
						<tr><!-- Row 1 -->
<%

    Enumeration instances = request.getParameterNames();
    instances = request.getParameterNames();
    String instance = null;
    String pos;
    String instanceIds=null;
    String instanceKey=null;
    String queryString="";

    while (instances.hasMoreElements()) {
        instance = (String)instances.nextElement();
        if(instance.startsWith("INST")) {
                pos=instance.substring(4);
                queryString+= "ID_"+pos+"="+request.getParameter("ID_"+pos)+"&";
                instanceKey= request.getParameter("KEY_"+pos);
%>
                                                    <td class="sortable"  colspan="2" nowrap> <%=instanceKey%>  </td>
						</tr>
                                                <tr>
<%
        }
    }

    queryString=queryString.substring(0,queryString.length()-1);
%>                                                     
							<td class="sortableHeader"  nowrap><fmt:message key="deleteSynchronousInstanceRequest.ask1" bundle="${lang}"/> <fmt:message key="deleteSynchronousInstanceRequest.ask2" bundle="${lang}"/><font color="FF0000"></font>
							<td class="sortableHeader" align="center" rowspan="2" valign="middle"> 
                                                        <input type="button" value="<< <fmt:message key="deleteSynchronousInstanceRequest.Back" bundle="${lang}"/>" onClick="history.back()">
                                                        <input type="button" value="<fmt:message key="deleteSynchronousInstanceRequest.Confirm" bundle="${lang}"/> >>" onClick="removeInstances()">
                                                        </td>
						</tr>
                                                <!--/form-->
					</table>
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE>

<SCRIPT language="JavaScript">
<!--
function back()
{
    window.back();
}

function removeInstances()
{
  window.location="<%= response.encodeURL("manager?cmd=deleteInstances&serviceName="+service+"&opType="+opType+"&"+queryString)%>";
}
//-->
</SCRIPT>
<jsp:include page="footer.jsp"/>

