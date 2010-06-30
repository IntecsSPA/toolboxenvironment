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
 -  File Name:         $RCSfile: main.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.12 $
 -  Revision Date:     $Date: 2004/09/23 10:51:27 $
 -
-->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.*, it.intecs.pisa.toolbox.configuration.*" errorPage="errorPage.jsp" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" /> 
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<%
TBXService[] services = ServiceManager.getInstance().getServicesAsArray();
boolean isEnabled = services.length>0;

Boolean warnFTP = (FTPServerManager.getInstance() == null);

Boolean warnAddress = false;
String toolboxUrl="";
String warnAddressError="";

       try {
            toolboxUrl = ToolboxNetwork.getEndpointURL();
        } catch (Exception e) {
            warnAddress = true;
            warnAddressError = e.getMessage();
        }


%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main> <SCRIPT>addBreadCrumb  ("<fmt:message key="main.home" bundle="${lang}"/>");</SCRIPT>         
                <DIV class=portletItem id=01> 
                    <DIV> 
                        <A href="main.jsp"><fmt:message key="main.home" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="main.jsp"><IMG class=labelHomePage title=Toolbox alt=Toolbox src="images/toolbox.png" align=middle border=0></A>
                        <fmt:message key="main.welcome" bundle="${lang}"/>
                    </P> 

<%
if (warnFTP)
{
    %>
                            <P>
                                <IMG src="images/warn.png"><fmt:message key="main.warnFTP" bundle="${lang}"/>
                            </P>

 <%
                    }
%>
<%
if (warnAddress)
{
    %>
                            <P>
                                <IMG src="images/warn.png"><fmt:message key="main.warnAddress" bundle="${lang}"/><%=warnAddressError%>
                            </P>

 <%
                    }
%>



                </DIV>
                <DIV class=portletItem id=02> 
                    <DIV> 
                        <A href="main.jsp"><fmt:message key="main.howTo" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> <A href="main.jsp"><IMG class=labelHomePage title="HowTo" alt="HowTo" src="images/howTo.png" align=middle border=0></A>
                        
                                 <IMG src="images/arrow.gif"> <span class="jsAction" onclick="javascript:helpPopup(docUrl, 'RE/main.html_Run-time_environment*blankpage.html_tasks*blankpage.html_TOOLBOX_management*RE/toolboxConfiguring.html_Configuring_the_TOOLBOX*')"><fmt:message key="main.configure" bundle="${lang}"/></span><br>
                    <IMG src="images/arrow.gif"> <span class="jsAction" onclick="javascript:helpPopup(docUrl, 'RE/main.html_Run-time_environment*blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceCreation.html_Service_creation*')"><fmt:message key="main.create" bundle="${lang}"/></span><br>
                    <IMG src="images/arrow.gif"> <span class="jsAction" onclick="javascript:helpPopup(docUrl, 'RE/main.html_Run-time_environment*blankpage.html_tasks*RE/operationManagement.html_Operations_management*RE/createOperation.html_Add_an_operation*')"><fmt:message key="main.add" bundle="${lang}"/></span>
                    </P> 
                </DIV>

                 <div class="searchDivTwitter twitter">
                    
                    <script src="http://widgets.twimg.com/j/2/widget.js"></script>
                        <script>
                        new TWTR.Widget({
                          version: 2,
                          type: 'profile',
                          rpp: 4,
                          interval: 6000,
                          width: screen.width/2.2,
                          height:screen.height/5,
                          theme: {
                            shell: {
                              background: '#b6cee7',
                              color: '#336699'
                            },
                            tweets: {
                              background: '#e7edf5',
                              color: '#336699',
                              links: '#eb1707'
                            }
                          },
                          features: {
                            scrollbar: false,
                            loop: false,
                            live: true,
                            hashtags: true,
                            timestamp: true,
                            avatars: true,
                            behavior: 'all'
                          }
                        }).render().setUser('toolboxintecs').start();
                        </script>
               </div>
                <DIV class=double> 
                    <DIV class="searchDiv itemDark"> 
                        
                        <!-- Modifica Gazzano Davide -->
                        <!-- Link al Service diretto senza conferma -->
                        <form name="AdvancedSearchForm" method="post" action="<%= response.encodeURL("serviceConfiguration.jsp") %>"> 
                            <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%> onChange="javascript: document.AdvancedSearchForm.submit();"> 
                                <option value=""></option> 
                                <%
                               for(TBXService service:services) {
                                     String serviceName = service.getServiceName();
                                %> 
                                <option value="<%= serviceName%>"><%= serviceName%></option> 
                                <%
                                }
                                %> 
                            </select> 
                        </form>
                        <!-- fine Modifica Gazzano Davide -->
              
                        <!--
                        <FORM name="AdvancedSearchForm" action="<%= response.encodeURL("serviceConfiguration.jsp") %>" method="post"> 
                            <P class=searchForm><fmt:message key="main.select" bundle="${lang}"/>&nbsp;&nbsp; 
                                <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%>>
                                    <option value=""></option>
                                    <%
                                   for(TBXService service:services) {
                                         String serviceName = service.getServiceName();
                                    %>
                                    <option value="<%= serviceName%>" <%= (request.getParameter("serviceName") != null && request.getParameter("serviceName").equals(serviceName)) ? "selected" : "" %>><%= serviceName%></option>
                                    <%
                                    }
                                    %>
                                </select>&nbsp;<input type="submit" value="<fmt:message key="main.Go" bundle="${lang}"/>" <%= isEnabled ? "" : "disabled"%> name="sm">
                            </P>
                            <P class=top>&nbsp;</P> 
                        </FORM> -->
                    </DIV> 
                    <DIV class="quickLink itemLight"> 
                        <DIV><A><fmt:message key="main.Quick" bundle="${lang}"/></A> </DIV>            
                        <A class=twoColumns target="_blank" href="Documentation/docsExplorer/index.html"><fmt:message key="main.document" bundle="${lang}"/></A>
                            
                        <A class=twoColumns href="monitoringCenter.jsp"><fmt:message key="main.monitoring" bundle="${lang}"/></A> 
                    <A class=twoColumns href="testSubmit.jsp"><fmt:message key="main.testSubmit" bundle="${lang}"/></A></DIV> 
            </DIV></TD>

            <!--DIV class=portletItem id=03>
                        <DIV>
                            <A href="main.jsp">Updates</A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                        </DIV-->

                        
                    <!--/DIV-->

        </TR>
        <TR>
            <TD>
               
            <TD>
        </TR>
    </TBODY> 
</TABLE>

                    
<jsp:include page="footer.jsp"/>
