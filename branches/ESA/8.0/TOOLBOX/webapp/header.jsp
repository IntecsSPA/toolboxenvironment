<%@ page import="it.intecs.pisa.toolbox.*,it.intecs.pisa.util.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
String serviceName = (request.getParameter("serviceName") == null ? "": request.getParameter("serviceName"));
String error = (request.getParameter("error") == null ? "": request.getParameter("error"));
String servicesManagement = "servicesManagement.jsp" +(serviceName == "" ? "" :"?serviceName=") + serviceName;
String monitoringCenter = "monitoringCenter.jsp" +(serviceName == "" ? "" :"?serviceName=") + serviceName;
String downloadVersion = ""; 
String newVersion=null;
    try
    {
        ServiceManager servMan;
        servMan=ServiceManager.getInstance();
        TBXService service=servMan.getService(serviceName);

        if(service.getImplementedInterface().getName().equals("SSE"))
           newVersion= URLReader.downloadLastVersion( serviceName );
    }catch(Exception ecc)
      {
      newVersion=null;
    }

  Toolbox tbxServlet;

  tbxServlet=Toolbox.getInstance();
  String tbxVersion=tbxServlet.getToolboxVersion();
  String tbxRevision=tbxServlet.getToolboxRevision();
                
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>

    <meta http-equiv="expires" content="now">
 <meta http-equiv="pragma" content="no-cache">
 <meta http-equiv="Cache-Control" content="no-cache">

    <TITLE>SSE Toolbox</TITLE>
    <META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
   
    <link rel="stylesheet" type="text/css" href="jsScripts/ext-2.0.1/resources/css/ext-all.css" ></link>
    <link rel="stylesheet" href="jsScripts/dhtmlwindow.css" type="text/css" ></link>
      
     <!-- <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/jquery/jquery-1.2.6.js"></script>
      <script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/jquery/ext-jquery-adapter.js"></script>
      <script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/ext/ext-base.js"></script>
      <script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ext-all.js"></script>-->
      
      <script type="text/javascript" src="jsScripts/ext-2.0.1/adapter/jquery/jquery.js"></script>
      <script type="text/javascript" src="jsScripts/ext-2.0.1/adapter/jquery/ext-jquery-adapter.js"></script>
      <script type="text/javascript" src="jsScripts/ext-2.0.1/adapter/ext/ext-base.js"></script>
      <script type="text/javascript" src="jsScripts/ext-2.0.1/ext-all.js"></script>

      <script type="text/javascript" src="jsScripts/DhtmlNew.js"></script>
    
    <link href="jsScripts/dom.css" rel=stylesheet></link>
    <link href="jsScripts/dom.directory.css" rel=stylesheet></link>
    <link href="jsScripts/portal.css" rel=stylesheet></link>
    <link href="jsScripts/styles.css" rel=stylesheet></link>
    <link rel="SHORTCUT ICON" href="images/toolboxIcon.gif"></link>
    <META http-equiv=Pragma content=no-cache>
    <META http-equiv=Cache-Control content="no-store, no-cache, must-revalidate, post-check=0, pre-check=0">
    <META http-equiv=Expires content=0>
    <SCRIPT type="text/javascript" src="jsScripts/CommonScript.js"></SCRIPT>
    <META content="MSHTML 6.00.2900.2873" name=GENERATOR>

   
</HEAD>
<BODY id=body> 
<TABLE cellSpacing=0 cellPadding=0 width="100%">
    <TR> 
        <TD class=headerTemplate> 
            <!-- bad --> 
            <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
                <TR class=flagcolour> 
                    <TD height=2><IMG src="images/1x1.gif"></TD>
                </TR> 
                <TR> 
                    <TD> 
                        <TABLE class=flagcolour cellSpacing=0 cellPadding=0 width="100%" 
                               align=center border=0> 
                        <TBODY> 
                        <TR bgColor=#607a92> 
                            <TD vAlign=bottom align=left bgColor=#607a92 height=60><A 
                                    href="main.jsp" target=_top><IMG 
                                src="images/toolboxLogo.png" border=0></A> </TD>
                            <TD vAlign=bottom align=middle bgColor=#607a92 height=60><!-- &nbsp; --> 
                                <A 
                                    href="http://services.eoportal.org/index.jsp" target=_top><IMG 
                                src="images/sse.gif" border=0></A> </TD>
                            
                            <%
                            if ( newVersion != null ) {                          
                            %>
                            <TD vAlign=bottom align=right bgColor=#607a92 height=60><IMG 
                  height=60 src="images/directory_top_schema.gif" width=293
                  border=0 name=top> </TD> 
                          <%
			} else
                            { 
					%> 
            
    <TD vAlign=bottom align=right bgColor=#607a92 height=60><IMG 
                  height=60 src="images/directory_top.png" width=293
                  border=0 name=top> </TD> 
                          <%
			} 
%> 
      
        </TR> 
          <TR class=flagcolour> 
          <TD colSpan=3 height=1><IMG 
              src="images/1x1.gif"></TD>
        </TR> 
          <%

                  TBXService[] services = ServiceManager.getInstance().getServicesAsArray();
                  boolean isEnabled = services.length>0;
				 %> 
          <TR bgColor=#607a92 height=10> 
          <TD bgColor=#607a92 colSpan=2 height=12> 
              <form name="service" method="post" action="<%= response.encodeURL("serviceConfiguration.jsp") %>"> 
              <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%> onChange="javascript:document.service.submit();"> 
                <option value=""></option> 
                <%
						 for(TBXService service:services) {
						String itemServiceName = service.getServiceName();
					%> 
                <option value="<%= itemServiceName%>" <%= serviceName.equals(itemServiceName) ? "selected" : "" %>><%= itemServiceName%></option> 
                <%
						}
					%> 
              </select> 
            </form></TD> 

          <TD vAlign=center align=right bgColor=#607a92><fmt:message key="revision" bundle="${lang}"/> <%=tbxRevision%><!--IMG height=13
                  src="images/directory_bot.gif" width=293 border=0
                  name=bot--> </TD>
 
                  
        </TR> 
          <TR bgColor=#e0dfe3> 
          <TD colSpan=3 height=1><IMG height=1 alt="" 
                  src="images/1x1.gif" border=0></TD>
        </TR> 
          <TR class=flagcolour> 
          <TD colSpan=3> <DIV class=mnu> 
              <TABLE id=menuTop width="100%" align=center bgColor=#000040 border=0> 
                  <TR align=middle> 
                    <TD align=left width="18%"><A class=itm 
                        href="<%= response.encodeURL("toolboxConfiguration.jsp") %>"><fmt:message key="header.configure" bundle="${lang}"/></A></TD> 
                    <TD align=left width="18%"><A class=itm 
                        href="<%= response.encodeURL(servicesManagement) %>"><fmt:message key="header.services" bundle="${lang}"/></A></TD> 
                    <TD align=left width="18%"><A class=itm 
                        href="<%= response.encodeURL("FTPManagement.jsp") %>"><fmt:message key="header.ftp" bundle="${lang}"/></A></TD> 
                    <TD align=left width="18%"><A class=itm 
                        href="<%= response.encodeURL(monitoringCenter) %>"><fmt:message key="header.monitoring" bundle="${lang}"/></A></TD> 
                    <TD align=left width="18%"><A class=itm 
                        href="<%= response.encodeURL("testCenter.jsp?serviceName="+serviceName) %>"><fmt:message key="header.test" bundle="${lang}"/></A></TD> 
                    <TD align=left width="10%"><A class=itm
                        href="<%= response.encodeURL("tools.jsp?serviceName="+serviceName) %>"><fmt:message key="header.tools" bundle="${lang}"/></A></TD>
                    <TD align=right width="10%"><A class=itm
                        href="javascript: confirm('logout.jsp','<fmt:message key="header.logout" bundle="${lang}"/>','<fmt:message key="header.logout" bundle="${lang}"/>');"><fmt:message key="header.logout" bundle="${lang}"/></A></TD>
                        
                        <!-- <TD align=right width="10%"><A class=itm  
                        href="<%= response.encodeURL("logout.jsp") %>"><fmt:message key="header.logout" bundle="${lang}"/></A></TD> -->
                  </TR> 
                  <%
                      boolean isServiceNew = true;
                      String s;
                      if ((s = request.getParameter("serviceName")) != null) {
                       for(TBXService service:services) {
                          if (s.equals(service.getServiceName())) {
                          isServiceNew = false;
                          break;
                         }
                        }
                       }
					   //request.getParameter("warn") != null ||
					 if (serviceName.equals("") || isServiceNew){
					%> 
                  <%
						} else {
				  %> 
   			       <TR bgColor=#e0dfe3> 
          				<TD colSpan=6 height=1><IMG height=1 alt="" src="images/1x1.gif" border=0></TD>
		           </TR> 
                  <!-- inserting service commands--> 
                  <TR align=middle> 
                    <%
						String serviceConfiguration = "serviceConfiguration.jsp?serviceName=" + serviceName;
						String manageOperations= "manageOperations.jsp?serviceName=" + serviceName;
                                                String rssFeed="manager?cmd=getServiceRSS&serviceName=" + serviceName;
						//String deleteService = "deleteServiceRequest.jsp?serviceName=" + serviceName;
					%> 
                    <TD align=left ><A class=itm 
                        href="<%= response.encodeURL(serviceConfiguration) %>"><fmt:message key="header.service" bundle="${lang}"/></A></TD> 
                    <TD align=left ><A class=itm 
                        href="<%= response.encodeURL(manageOperations) %>"><fmt:message key="header.operations" bundle="${lang}"/></A></TD>  
                    
                    <TD align=left colspan=4><A class="itm" href="javascript: confirm ('manager?cmd=delSrv&serviceName=<%= serviceName %>', '<fmt:message key="header.delete" bundle="${lang}"/> <%= serviceName %>', translateDelete('<%= (String)session.getAttribute("languageReq") %>'), 'delete');"><fmt:message key="header.delete" bundle="${lang}"/></A></TD>                                       
               
               <TD align=left><a href="<%= response.encodeURL(rssFeed)%>"><img src="images/rssicon.jpg" alt="rss feed"></a>
               </TR> 
                  <%
						}
					%> 
              </TABLE> 
            </DIV></TD> 
        </TR> 
          <TR bgColor=#e0dfe3> 
          <TD colSpan=3 height=1><IMG height=1 alt="" 
                  src="images/1x1.gif" border=0></TD>
        </TR> 
          <TR bgColor="#607a92"> 
          <TD class="itm path" id=breadCrumb align=left colSpan=2></TD> 
          <TD class="itm path" id=infoCenter align=right></TD> 
        </TR> 
      </TD> 
       </TR> 
         </TABLE> 
    </TD> 
     </TR> 
       </TABLE> 

