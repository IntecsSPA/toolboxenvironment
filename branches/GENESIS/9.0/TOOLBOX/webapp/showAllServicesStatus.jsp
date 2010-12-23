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
 -  File Name:         $RCSfile: showAllServicesStatus.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
-->
<%@ page language="java" import="it.intecs.pisa.toolbox.db.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.toolbox.service.status.*,java.util.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String home = (String)messages.getObject("showAllServicesStatus.home");
String serviceManag = (String)messages.getObject("showAllServicesStatus.servManag");
String serviceList = (String)messages.getObject("showAllServicesStatus.servList");

String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
        "&nbsp;<a href='servicesManagement.jsp'>"+serviceManag+"</a>&nbsp;&gt;" + serviceList;

%>

<!-- Modifica Gazzano Davide -->
<script type="text/javascript" language="javascript">
    function assign (strg, serv){
        var address;
        var language="<%= (String)session.getAttribute("languageReq") %>";
        var text;
        var title;
        switch (language){
            case ("en"):
                text="Want you ";
                title="Confirm";
                break;
            case ("it"):
                text="Vuoi eseguire il comando: ";
                title="Conferma";
                break;
            default:
                text="Are you sure?";
                title="Confirm";
                break;
        }
        
        switch (strg){
            case ("startService"):
                address="startService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=";
                text+="<%= (String)messages.getObject("servicesManagement.msgStartThe") %> " + serv + " <%= (String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("stopService"):
                address="stopService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=";
                text+="<%= (String)messages.getObject("servicesManagement.msgStopThe") %> " + serv + " <%= (String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("suspendService"):
                address="suspendService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=";
                text="<%= (String)messages.getObject("servicesManagement.msgSuspendThe") %> " + serv + " <%= (String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("resumeService"):
                address="resumeService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=";
                text+="<%= (String)messages.getObject("servicesManagement.msgResumeThe") %> " + serv + " <%= (String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            default:
                address="";
                break;
        }
        address+=serv;
        confirm (address, text, title);
    }
</script>
<!-- fine Modifica Gazzano Davide -->

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
                    <TBODY> 
                        <TR> 
                            <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="showAllServicesStatus.servList" bundle="${lang}"/></FONT></P> 
                                <P>  
                                    <!-- Page contents table-->
                                    <table width="60%" cellspacing="2" cellpadding="2" align="center">
                                        <tr><!-- Row 1 -->
                                            <td class="sortable" width="50" align="center"><fmt:message key="showAllServicesStatus.controls" bundle="${lang}"/></td>
                                            <td class="sortable" width="50" align="center"><fmt:message key="showAllServicesStatus.status" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><fmt:message key="showAllServicesStatus.servName" bundle="${lang}"/></td>
                                        </tr>
                                        <% 
                                       TBXService[] arrService = ServiceManager.getInstance().getServicesAsArray();
                                        TBXService service = null;
                                        for (int i=0; i<arrService.length; i++)
                                        {
                                        	service = arrService[i];
                              
                                            String serviceName = service.getServiceName();
                                            byte serviceStatus = ServiceStatuses.getStatus(serviceName);
                                            
                                            // Modifica Gazzano Davide
                                            String startService = "startService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=" + serviceName;
                                            String stopService = "stopService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=" + serviceName;
                                            String suspendService = "suspendService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=" + serviceName;
                                            String resumeService = "resumeService.jsp?sourcePage=showAllServicesStatus.jsp&serviceName=" + serviceName;
                                            // fine modifica Gazzano Davide
                                            
                                      
                                        
                                        %>
                                        
                                        <!-- Modifica Gazzano Davide -->
                                        <script language="javascript">
                                            var startService="<%= startService %>";
                                            var stopService="<%= stopService %>";
                                            var suspendService="<%= suspendService %>";
                                            var resumeService="<%= resumeService %>";
                                        </script>
                                        <!-- fine Modifica Gazzano Davide -->
                                        
                                        <tr><!-- Row 2 -->
                                            <td class="tdItem" nowrap>
                                            <!-- Modifica Gazzano Davide -->
                                            <% if(serviceStatus==ServiceStatuses.STATUS_STOPPED){
                                                %>
                                                <a href=# onclick="javascript: assign('startService', '<%= serviceName %>');"><img src="images/play.png" alt="START"></a>
                                                <%
                                                } else if (serviceStatus==ServiceStatuses.STATUS_RUNNING){
                                                %>
                                                <a href=# onclick="javascript: assign('stopService', '<%= serviceName %>')"><img src="images/stop.png" alt="STOP"></a>
                                                <a href=# onclick="javascript: assign('suspendService', '<%= serviceName %>');"><img src="images/pause.png" alt="SUSPEND"></a> 
                                                <%
                                                } else if (serviceStatus==ServiceStatuses.STATUS_SUSPENDED){
                                                %>
                                                <a href=# onclick="javascript: assign('resumeService', '<%= serviceName %>')"><img src="images/play.png" alt="RESUME"></a>
                                                <%
                                                }
                                                %>
                                           
                                                
                                            </td>
                                            <td class="tdInfo" align="center"><img src="images/<%= (serviceStatus==ServiceStatuses.STATUS_STOPPED ? "ledred.png" : (serviceStatus==ServiceStatuses.STATUS_SUSPENDED? "ledorange.png" : "ledgreen.png"))%>" alt="<%= ServiceStatuses.toString(serviceStatus) %>"></td>
                                            <td class="tdItem" nowrap><a href="servicesManagement.jsp?serviceName=<%=serviceName%>"><%=serviceName%></a></td>
                                        </tr>
                                        <%
                                        }
                                        %>
                                        <tr>
                                            <div align="right"><td colspan="3" class="sortable"><div align="right"><A href="<%= response.encodeURL("servicesManagement.jsp") %>">&lt;&lt; <fmt:message key="showAllServicesStatus.Back" bundle="${lang}"/>&nbsp;</A></div></td>
                                            </div>
                                        </tr>
                                    </table>
                                </P> 
                            &nbsp; </TD> 
                        </TR> 
                    </TBODY> 
            </TABLE></TD> 
        </TR> 
    </TBODY> 
</TABLE> 		
<jsp:include page="footer.jsp"/>

