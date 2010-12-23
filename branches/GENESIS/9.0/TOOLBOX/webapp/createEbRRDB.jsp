<%@page contentType="text/html" pageEncoding="UTF-8" import="it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.configuration.*,it.intecs.pisa.soap.toolbox.*, java.util.*"%>
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
        String localhost;
        String port;

        ToolboxConfiguration configuration;
        configuration=ToolboxConfiguration.getInstance();

        localhost="localhost";
        port="5432";
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
                        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/tools.html_Tools_center*RE/createebRRDBinstance.html_Creating_an_EOP_catalogue_database*");</SCRIPT>
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                            <TBODY>
                                <TR>
                                    <TD id=main> <P class=arbloc><FONT class=arttl></FONT></P>
                                        <P>
                                        <form name="harvestForm"  action="manager?cmd=ebRRDBcreate" enctype="multipart/form-data" method="post">
                                                <!-- Page contents table-->
                                                <input type="hidden" name="serviceName" value="<%= serviceName%>">
                                                <table width="450" cellspacing="2" cellpadding="2" align="center">
                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.host" bundle="${lang}"/></td>
                                                        <td class="sortable"  colspan=1 nowrap><input type="text" name="host" size='60' value="<%=localhost%>"/> </td>
                                                    </tr>
                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.port" bundle="${lang}"/></td>
                                                         <td class="sortable"  colspan=1 nowrap><input type="text" name="port" size='60' value="<%=port%>"/></td>
                                                        
                                                    </tr>
                                                     <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.username" bundle="${lang}"/></td>
                                                        <td class="sortable"  colspan=1 nowrap><input type="text" name="username" size='60' value=""/> </td>
                                                    </tr>
                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.password" bundle="${lang}"/></td>
                                                         <td class="sortable"  colspan=1 nowrap><input type="text" name="password" size='60' value=""/></td>

                                                    </tr>

                                                     <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.schema" bundle="${lang}"/></td>
                                                         <td class="sortable"  colspan=1 nowrap><input type="text" name="schema" size='60' value=""/></td>

                                                    </tr>

                                                    <tr><!-- Row 3 -->
                                                        <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createdb.templatename" bundle="${lang}"/></td>
                                                         <td class="sortable"  colspan=1 nowrap><input type="text" name="template" size='60' value="template_postgis"/></td>

                                                    </tr>






                                                    <tr><!-- Row 5 -->
                                                        <td colspan="2" rowspan="2" nowrap align="right" class="sortable"><div align="right">
                                                                <input type="submit" value="<fmt:message key="ebRR.createdb.create" bundle="${lang}"/>">
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