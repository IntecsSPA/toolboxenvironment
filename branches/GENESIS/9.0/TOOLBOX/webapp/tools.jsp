<%@ page language="java"  import="it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.service.*, it.intecs.pisa.toolbox.configuration.*" errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>

<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp?firebugControl=false&loadPanel=true&loadDefer=250&extVers=3" />

<c:if test="${sessionScope.languageReq!= null}">
<fmt:setLocale value="${sessionScope.languageReq}" />
<fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%
String serviceName = request.getParameter("serviceName") == null ? "": request.getParameter("serviceName");

PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;&nbsp;Tools";
String toolboxUrl=ToolboxNetwork.getEndpointURL();
%>

<style type="text/css">

.upload-icon {
            background: url('images/image_add.png') no-repeat 0 0 !important;
        }

</style>

<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/widgets/style/css/webgis.css" />
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/OpenLayers/lib/OpenLayers.js"></script>
<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/import/ext/ux/fileuploadfield/css/fileuploadfield.css"/>

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

<script type="text/javascript" src="jsScripts/serviceTools/scripts/serviceToolsManager.js"></script>


<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <DIV class=portletItem id=01>
                    <DIV>
                        <A href="createEbRRDB.jsp"><!--<A href=# onclick="javascript:showToolInterface ('CreateCatalogueDatabase', 'Create Catalogue Database', '<%=serviceName%>');">--><fmt:message key="tools.createebRRDB" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                        
                    </DIV> 
                    <P> 
                        <!--<A href=# onclick="javascript:showToolInterface ('CreateCatalogueDatabase', 'Create Catalogue Database', '<%=serviceName%>');">-->
                        <A href="createEbRRDB.jsp"><IMG class=labelHomePage title=Toolbox src="images/EOPdatabase.png" align=middle border=0></A>
                        <fmt:message key="tools.createebRRDB.descr" bundle="${lang}"/>
                    </P> 
                </DIV>

               <!-- <DIV class=portletItem id=01>
                    <DIV>
                        <A href="manager?cmd=getReport"><fmt:message key="tools.createReport" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href="createEbRRDB.jsp"><IMG class=labelHomePage title=Toolbox alt="Send a SOAP message" src="images/statistics.png" align=middle border=0></A>
                        <fmt:message key="tools.createReport.descr" bundle="${lang}"/>
                    </P>
                </DIV>-->
               

                <%
                    TBXService service;
                    ServiceManager servMan;

                    servMan=ServiceManager.getInstance();

                    if(serviceName!=null && serviceName.equals("")==false)
                        {
                    service=servMan.getService(serviceName);

                    if(service !=null && 
                            (service.getImplementdInterface().startsWith("OGC-06-131r6")||service.getImplementdInterface().startsWith("OGC-05-025r3")||service.getImplementdInterface().startsWith("OGC-07-038")||service.getImplementdInterface().startsWith("OGC-07-045")))
                    {
                %>
                <DIV class=portletItem id=05>
                    <DIV>

                        <A href=# onclick="javascript:showToolInterface('HarvestFromURL', 'Harvest from URL', '<%=serviceName%>');"><fmt:message key="ebrr.harvest" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href=# onclick="javascript:showToolInterface('HarvestFromURL', 'Harvest from URL', '<%=serviceName%>');"><IMG class=labelHomePage title=validate alt="" src="images/harvest.png" align=middle border=0></A>
                    <fmt:message key="ebrr.harvest.description" bundle="${lang}"/></P>
                </DIV>
                <DIV class=portletItem id=05>
                    <DIV>
                        <!-- <A href=" response.encodeURL("ebrrHarvestFromDisk.jsp?serviceName="+serviceName) ">-->
                        <A href=# onclick="javascript:showToolInterface('HarvestFromFile','Harvest from filesystem','<%=serviceName%>');">
                        <fmt:message key="ebrr.harvestfromdisk" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                       <A href=# onclick="javascript:showToolInterface('HarvestFromFile','Harvest from filesystem','<%=serviceName%>');"><IMG class=labelHomePage title=validate alt="<fmt:message key="ebrr.harvestfromdisk" bundle="${lang}"/>" src="images/harvestFromDisk.png" align=middle border=0></A>
                    <fmt:message key="ebrr.harvestfromdisk.description" bundle="${lang}"/></P>
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

