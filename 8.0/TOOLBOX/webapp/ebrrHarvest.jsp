<%@page contentType="text/html" pageEncoding="UTF-8" import="it.intecs.pisa.soap.toolbox.*, java.util.*"%>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ include file="checkSession.jsp" %>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<jsp:include page="header.jsp" />

<%
        PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));

        String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;" +
                "&nbsp;<a href='servicesManagement.jsp'>" +
                (String) messages.getObject("ebrr.harvest") +
                "</a>";

        String serviceName = "";

        serviceName = request.getParameter("serviceName");

%>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>
<SCRIPT language="JavaScript">
    <!--
    function back()
    {
        window.location="<%= response.encodeURL("manageOperations.jsp?serviceName=" + serviceName)%>";
    }
    //-->

    function harvest(){
        var serviceName=document.harvestForm.serviceName.value;
        var resUrl=document.harvestForm.source.value;
        var resType=document.harvestForm.resourceType.value;

        if(resUrl.length <= 0){
            Ext.Msg.show({
                title:'Error',
                buttons: Ext.Msg.OK,
                msg: '<%= response.encodeURL("ebrr.harvest.resurlnotset")%>',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
            return false;
        }else{
            if(resType.length <= 0){
                Ext.Msg.show({
                    title:'Error',
                    buttons: Ext.Msg.OK,
                    msg: '<%= response.encodeURL("ebrr.harvest.resTypenotset")%>',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
                return false;
            }

            try{
                var harvestResponseFunction=function(response){
                    var resultWin=new Ext.Window({
                        title: 'Harvesting response',
                        border: false,
                        animCollapse : true,
                        autoScroll : true,
                        maximizable: true,
                        collapsible: true,
                        layout: 'fit',
                        listeners:{
                            close:function(){window.location.reload();}
                        },
                        width: screen.availWidth/2,
                        height: screen.availHeight/2,
                        closeAction:'close',
                        buttons : [{
                                text: 'Close',
                                handler: function(){
                                    window.location.reload();
                                }
                            }],
                        html:response
                    });
                    resultWin.show();
                };

                var harvestResponseFunctionTimeOut=function(){
                    Ext.Msg.show({
                        title:'Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                };
                var headers= new Array();
                var param="serviceName="+serviceName+"&source="+resUrl+"&resourceType="+resType;
                headers.push("Content-type,application/x-www-form-urlencoded");
                headers.push("Content-length,"+param.length);

                var onSubmit=sendXmlHttpRequestTimeOut("POST",
                "manager?cmd=ebRRHarvest",
                false, param,
                60000, harvestResponseFunction, harvestResponseFunctionTimeOut,headers);

                return false;
            }catch(e){
                return false;
            }
        }

        return false;
    }
</SCRIPT>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" height="100%" align=center>
            <TBODY>
                <TR>
                    <TD class=pageBody id=main>
                        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/tools.html_Tools_center*RE/ebRRmetadata.html_Harvesting_metadata*");</SCRIPT>
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                            <TBODY>
                                <TR>
                                    <TD id=main> <P class=arbloc><FONT class=arttl></FONT></P>
                                        <P>
                                            <form name="harvestForm" onSubmit="return harvest()" enctype="multipart/form-data" method="post">
                                                <!-- Page contents table-->
                                                <input type="hidden" name="serviceName" value="<%= serviceName%>">
                                                <table width="450" cellspacing="2" cellpadding="2" align="center">
                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap> <fmt:message key="ebrr.harvest.url" bundle="${lang}"/></td>
                                                        <td class="sortable"  colspan=1 nowrap>    <input type="text" name="source" size='60' value="http://"/> </td>
                                                    </tr>
                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap> <fmt:message key="ebrr.harvest.resourceType" bundle="${lang}"/></td>
                                                        <td class="sortable"  colspan=1 nowrap>    <input type="text" name="resourceType" size='60' value="urn:x-ogc:specification:csw-ebrim:ObjectType:EO:EOProduct"/></td>

                                                    </tr>
                                                    <tr><!-- Row 5 -->
                                                        <td colspan="2" rowspan="2" nowrap align="right" class="sortable"><div align="right">
                                                                <input type="submit" value="<fmt:message key="ebrr.harvest" bundle="${lang}"/>">
                                                        </div></td>
                                                    </tr>
                                                </table>
                                            </form>
                                        </P>
                                    </TD>
                                </TR>
                            </TBODY>
                    </TABLE></TD>
                </TR>
            </TBODY>
        </TABLE>
    </body>
</html>

<jsp:include page="footer.jsp"/>
