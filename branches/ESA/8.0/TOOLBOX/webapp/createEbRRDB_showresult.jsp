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
%>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>

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
                                        <table width="450" cellspacing="2" cellpadding="2" align="center">
                                            <tr>

                                                <% if (request.getParameter("error") == null) {%>
                                                <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createSchema.success" bundle="${lang}"/></td>
                                                <% } else {%> <td class="sortable"  colspan=1 nowrap><fmt:message key="ebRR.createSchema.failure" bundle="${lang}"/> <%= request.getParameter("error")%></td>
                                                <%
            }%>                             </tr>
                                        </table>
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
