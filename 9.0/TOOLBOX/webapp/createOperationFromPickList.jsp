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
 -  File Name:         $RCSfile: configureServiceRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.15 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
 -->
<%@ page import="it.intecs.pisa.pluginscore.*,
         it.intecs.pisa.util.*,
         it.intecs.pisa.toolbox.service.*,
         it.intecs.pisa.common.tbx.*,
         it.intecs.pisa.soap.toolbox.*,
         java.util.*,
         it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<SCRIPT language="JavaScript">
 
</SCRIPT>    

<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main>
               
                <form id="createOperationsOneShot" name="conf" method="post"  action="<%= response.encodeURL("manager?cmd=bulkOperationAdd")%>">
                   <input type="hidden" name="serviceName" value="<%= request.getParameter("serviceName") %>"></input>


                    <table  cellspacing="2" cellpadding="2" align="center">
                     <tr>
                            <td>
                                <FONT class=arttl><fmt:message key="createOperationFromPickList.banner" bundle="${lang}"/></FONT>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <FONT class=arttl><fmt:message key="createOperationFromPickList.bannerP2" bundle="${lang}"/></FONT>
                            </td>
                        </tr>



                        <%
String serviceName = request.getParameter("serviceName");

ServiceManager serviceManager;
TBXService service;
Interface implInterface, interf;
InterfacePluginManager interfman;
Operation[] operations;
Script[] scripts;
boolean canAdd = false;
boolean opRequireUser = false;

interfman = InterfacePluginManager.getInstance();
serviceManager = ServiceManager.getInstance();



service = serviceManager.getService(serviceName);
implInterface = service.getImplementedInterface();


interf = (Interface) interfman.getInterfaceDescription(implInterface);
operations = interf.getOperations();

for (Operation op : operations) {
    scripts = op.getScripts();

    opRequireUser = false;
    for (Script sc : scripts) {
        opRequireUser |= sc.isOverrideByUser();
    }


    if (opRequireUser == false) {
                        %>
                        <tr>
                            <td>
                                <input id="operationToAdd<%= op.getName()%>" name="operationToAdd<%= op.getName()%>" type="checkbox"><%= op.getName()%></input><br/>
                            </td>
                        </tr>
                        <%
                        }

                    }

                        %>
                        <tr>
                            <td>
                                <input type="submit" value="<fmt:message key="createServiceRequest.Create" bundle="${lang}"/>"/>
                            </td>
                        </tr>

                    </table>
                </form>
            </TD>
        </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

