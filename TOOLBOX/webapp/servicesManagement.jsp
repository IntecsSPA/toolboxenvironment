<%@ page language="java"  errorPage="errorPage.jsp"  import="it.intecs.pisa.toolbox.db.*,it.intecs.pisa.toolbox.service.status.*,it.intecs.pisa.toolbox.service.*"%>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp?firebugControl=false&loadPanel=true&loadDefer=250&extVers=3" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<%
PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
ServiceManager servMan;
servMan=ServiceManager.getInstance();
String service = request.getParameter("serviceName") == null ? "": request.getParameter("serviceName");

String showAllServicesStatus = "showAllServicesStatus.jsp";

String home = (String)messages.getObject("servicesManagement.home");
String serviceManag = (String)messages.getObject("servicesManagement.serviceManag");

String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;&nbsp;" + serviceManag;
%>

<script type="text/javascript" language="javascript">
    function assign (strg){
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
            case ("startAllServices"):
                address="startAllServices.jsp";
                text+="<%= (String)messages.getObject("servicesManagement.msgStartAll") %>?";
                break;
            case ("stopAllServices"):
                address="stopAllServices.jsp";
                text+="<%= (String)messages.getObject("servicesManagement.msgStopAll") %>?";
                break;
            case ("startService"):
                address="startService.jsp?sourcePage=servicesManagement.jsp&serviceName=<%= service %>";
                text+="<%= (String)messages.getObject("servicesManagement.msgStartThe")+" "+service+ " " +(String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("stopService"):
                address="stopService.jsp?sourcePage=servicesManagement.jsp&serviceName=<%= service %>";
                text+="<%= (String)messages.getObject("servicesManagement.msgStopThe")+" "+service+ " " +(String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("suspendService"):
                address="suspendService.jsp?sourcePage=servicesManagement.jsp&serviceName=<%= service %>";
                text="<%= (String)messages.getObject("servicesManagement.msgSuspendThe")+" "+service+ " " +(String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("resumeService"):
                address="resumeService.jsp?sourcePage=servicesManagement.jsp&serviceName=<%= service %>";
                text+="<%= (String)messages.getObject("servicesManagement.msgResumeThe")+" "+service+ " " +(String)messages.getObject("servicesManagement.msgService") %>?";
                break;
            case ("resetServiceFile"):
                address="manager?cmd=resetServiceStatusFiles&serviceName=<%= service %>";
                text="<%= (String)messages.getObject("servicesManagement.resetStatusFiles.confirmaDialog")%>?";
                break;    
                
            default:
                address="";
                break;
        }
        confirm (address, text, title);
    }
</script>


                <link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/import/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/widgets/style/css/webgis.css" />
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/OpenLayers/lib/OpenLayers.js"></script>
<!--script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/jquery/jquery-1.2.6.js"></script-->

<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/import/ext/ux/fileuploadfield/css/fileuploadfield.css"/>


<!--script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/jquery/ext-jquery-adapter.js"></script-->
<!--script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ext-all.js"></script-->
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ux/fileuploadfield/FileUploadField.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ux/Spotlight.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Format/XMLKeyValue.js"></script>

<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/webgis/Panel/WindowInterfacePanel.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/manager.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/browserDetect.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/XmlDoc.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/ExtFormUtils.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/ExtFormType.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>




<script type="text/javascript" src="jsScripts/servicesManagement/scripts/servicesManagementManager.js"></script>
<script type="text/javascript" src="jsScripts/wpsWizard/scripts/wpsWizardManager.js"></script>

<style type="text/css">

.upload-icon {
            background: url('images/image_add.png') no-repeat 0 0 !important;
        }
</style>


<div id="workspace"></div>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR>
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/serviceManagement.html_Service_management*");</SCRIPT>
                <DIV class="portletItem" id="01"> 
                    <DIV> 
                        <A href="<%= response.encodeURL("selectImportOrCreate.jsp") %>"><fmt:message key="servicesManagement.createNew" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV> 
                    <P> <!--%= response.encodeURL("selectImportOrCreate.jsp") %-->
                        <!--A href=""><IMG class=labelHomePage title=create alt="Create a new service" src="images/createService.png" align=middle border=0></A-->
                        <A href=# onclick="javascript:createServiceInterface();"><IMG class=labelHomePage title=create alt="Create a new service" src="images/createService.png" align=middle border=0></A>
                    
                        <fmt:message key="servicesManagement.selection" bundle="${lang}"/></P>
                </DIV>
                <!-- ************ Backup/Restore Services Section *****************-->
                <DIV class="portletItem" id="05">
                    <DIV>
                        <A href=# onclick="javascript: importExportGroupServices();"><fmt:message key="servicesManagement.impExp" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href=# onclick="javascript: importExportGroupServices();"><IMG class=labelHomePage title="import/export services" alt="import/export services" src="images/ImportExport.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.impExpDescr" bundle="${lang}"/>
                    </P>
                </DIV>
                <!-- **************************************************************-->
 
                <DIV class="portletItem" id="02"> 
                    <DIV> 
                        <A href="<%= response.encodeURL(showAllServicesStatus) %>"><fmt:message key="servicesManagement.display" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="<%= response.encodeURL(showAllServicesStatus) %>"> <IMG class=labelHomePage title=serviceStatus alt="Display the service status" src="images/status.png" align=middle border=0> </a>
                    <fmt:message key="servicesManagement.displayDescr" bundle="${lang}"/></P> 
                </DIV> 
                <DIV class="portletItem" id="03">                     
                    <DIV> 
                        <A href=# onclick="javascript: assign('stopAllServices');"><fmt:message key="servicesManagement.stopAll" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <A href=# onclick="javascript: assign('stopAllServices');"><IMG class=labelHomePage title="stopAllServices" alt="stopAllServices" src="images/stopAll.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.stopDescr" bundle="${lang}"/>
                    </P>
                    
                </DIV> 
                <DIV class="portletItem" id="04"> 
                    <DIV>
                        <a href=# onclick="javascript: assign('startAllServices');"><fmt:message key="servicesManagement.startAll" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>                        
                        <A href=# ONCLICK="javascript: assign('startAllServices');"><IMG class=labelHomePage title="startAllServices" alt="startAllServices" src="images/startAll.png" align=middle border=0> </a>
                        <fmt:message key="servicesManagement.startDescr" bundle="${lang}"/>
                    </P>
                </DIV>

                
                <%
                if (service.equals("")) {
                %>
                <!-- Search Form  -->  
                <DIV class="searchDiv itemDark"> 
                    <%
            
                    TBXService[] services = servMan.getServicesAsArray();
                    boolean isEnabled = services.length>0;
                    %>
                    
                    <!-- Modifica Gazzano Davide -->
                    <!-- Link al Service diretto senza conferma -->
                    <form name="AdvancedSearchForm" method="post" action="<%= response.encodeURL("servicesManagement.jsp") %>"> 
                        <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%> onChange="javascript: document.AdvancedSearchForm.submit();"> 
                            <option value=""></option> 
                            <%
                            for(TBXService serv:services)
                                {
                                String serviceName = serv.getServiceName();
                            %> 
                            <option value="<%= serviceName%>" <%= service.equals(serviceName) ? "selected" : "" %>><%= serviceName%></option> 
                            <%
                            }
                            %> 
                        </select> 
                    </form>
                    <!-- fine Modifica Gazzano Davide -->
                    
                    <!--
                    <FORM name="AdvancedSearchForm" action="<%= response.encodeURL("servicesManagement.jsp") %>" method="post"> 
                    <P class=searchForm><fmt:message key="servicesManagement.serviceMonitoring" bundle="${lang}"/>&nbsp;&nbsp; 
                    <select name="serviceName" size="1" <%= isEnabled ? "" : "disabled"%>>
                                <option value=""></option>
                    <%
                   for(TBXService serv:services)
                        {
                        String serviceName = serv.getServiceName();
                    %>
                    <option value="<%= serviceName%>"><%= serviceName%></option>
                    <%
                    }
                    %>
                    </select>&nbsp;<input type="submit" value="<fmt:message key="servicesManagement.Go" bundle="${lang}"/>" <%= isEnabled ? "" : "disabled"%> name="sm">
                        </P>
                    </FORM> -->
                </DIV>                
                <%
                } else{
  
  try
          {
  
    byte status;

    status=ServiceStatuses.getStatus(service);


    //String status = srv.getServiceStatus();

    if (status==ServiceStatuses.STATUS_STOPPED) {
                %>				
                <DIV class="portletItem" id="05">                     
                    <DIV> 
                        <A href=# onclick="javascript: assign('startService');"><fmt:message key="servicesManagement.Start" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> <a href=# onclick="javascript: assign('startService');"><IMG class=labelHomePage title="start" alt="Start the service" src="images/start.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.The" bundle="${lang}"/> <%=service%> <fmt:message key="servicesManagement.servCurrent" bundle="${lang}"/> <span style="color: #FF0000"><fmt:message key="servicesManagement.STOPPED" bundle="${lang}"/></span>. <fmt:message key="servicesManagement.Click" bundle="${lang}"/> <span style="color: #00FF00"><fmt:message key="servicesManagement.RUNNING" bundle="${lang}"/></span> <fmt:message key="servicesManagement.mode" bundle="${lang}"/>.
                    </P>
                    
                </DIV> 
                <%		
                } else if (status==ServiceStatuses.STATUS_SUSPENDED) {
                %>				
                <DIV class="portletItem" id="06">                     
                    <DIV> 
                        <A href=# onclick="javascript: assign('resumeService');"><fmt:message key="servicesManagement.Resume" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> <A href=# onclick="javascript: assign('resumeService');"><IMG class=labelHomePage title="resume" alt="Resume a Service" src="images/start.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.The" bundle="${lang}"/> <%=service%> <fmt:message key="servicesManagement.servCurrent" bundle="${lang}"/> <span style="color: #FF9900"><fmt:message key="servicesManagement.SUSPENDED" bundle="${lang}"/></span>. <fmt:message key="servicesManagement.Click" bundle="${lang}"/> <span style="color: #00FF00"><fmt:message key="servicesManagement.RUNNING" bundle="${lang}"/></span> <fmt:message key="servicesManagement.modeAllows" bundle="${lang}"/>
                    </P>                    
                </DIV>                 
                <%		
                } else if (status==ServiceStatuses.STATUS_RUNNING) {
                %>				
                <DIV class="portletItem" id="07">                     
                    <DIV> 
                        <A href=# onclick="javascript: assign('stopService');"><fmt:message key="servicesManagement.Stop" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> <a href=# onclick="javascript: assign('stopService');"><IMG class=labelHomePage title="stop" alt="Stop a service" src="images/stop.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.The" bundle="${lang}"/> <%=service%> <fmt:message key="servicesManagement.servCurrent" bundle="${lang}"/> <span style="color: #00FF00"><fmt:message key="servicesManagement.RUNNING" bundle="${lang}"/></span>. <fmt:message key="servicesManagement.clickToStop" bundle="${lang}"/>
                        <br>
                        <fmt:message key="servicesManagement.Stopping" bundle="${lang}"/> 
                    </P>                    
                </DIV> 
                <DIV class="portletItem" id="08">                     
                    <DIV> 
                        <A href="#" onclick="javascript: assign('suspendService');"><fmt:message key="servicesManagement.Suspend" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <a href="#" onclick="javascript: assign('suspendService');"><IMG class=labelHomePage title="suspend" alt="Suspend a Service" src="images/suspend.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.The" bundle="${lang}"/> <%=service%> <fmt:message key="servicesManagement.servCurrent" bundle="${lang}"/> <span style="color: #00FF00"><fmt:message key="servicesManagement.RUNNING" bundle="${lang}"/></span>. <fmt:message key="servicesManagement.clickToSuspend" bundle="${lang}"/><br>
                        <fmt:message key="servicesManagement.suspendDescr" bundle="${lang}"/> <span style="color: #FF0000"><fmt:message key="servicesManagement.HARD" bundle="${lang}"/></span> <fmt:message key="servicesManagement.and" bundle="${lang}"/> <span style="color: #FFCC00"><fmt:message key="servicesManagement.SOFT" bundle="${lang}"/></span>.<br>
                        <span style="color: #FF0000"><fmt:message key="servicesManagement.HARD" bundle="${lang}"/></span> <fmt:message key="servicesManagement.HARDDescr" bundle="${lang}"/> <span style="color: #FFCC00"><fmt:message key="servicesManagement.SOFT" bundle="${lang}"/></span> <fmt:message key="servicesManagement.SOFTDescr" bundle="${lang}"/>
                    </P>                     
                </DIV> 
                <%				
                }
   
                }catch(Exception e)
                        {
                  %>
    <DIV class="portletItem" id="10">
                    <DIV> 
                        <A href="#" onclick="javascript: assign('resetServiceFile');">Errore nei files di stato!<!--<fmt:message key="servicesManagement.resetService" bundle="${lang}"/>-->&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <a href="#" onclick="javascript: assign('resetServiceFile');"><IMG class=labelHomePage title="suspend" alt="Reset  Service's status files" src="images/resetFiles.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.resetService.description" bundle="${lang}"/> 
                    </P>                     
                </DIV> 
    
    <%
                }
                finally
                {
                   %>
    <DIV class="portletItem" id="09">                     
                    <DIV> 
                        <A href="#" onclick="javascript: assign('resetServiceFile');"><fmt:message key="servicesManagement.resetService" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P>
                        <a href="#" onclick="javascript: assign('resetServiceFile');"><IMG class=labelHomePage title="suspend" alt="Reset  Service's status files" src="images/resetFiles.png" align=middle border=0></A>
                        <fmt:message key="servicesManagement.resetService.description" bundle="${lang}"/> 
                    </P>                     
                </DIV> 
    
    <%
                }
  }
                %>
            
            </TD>
        </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

