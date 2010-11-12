<%@ page language="java"  errorPage="errorPage.jsp" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<%
        String serviceName = request.getParameter("serviceName") == null ? "" : request.getParameter("serviceName");
        PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
        String home = (String) messages.getObject("FTPManagement.home");
        String FTP = (String) messages.getObject("FTPManagement.ftp");
        String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;" + "&nbsp;" + FTP;

        FTPServerManager servMan;
        servMan = FTPServerManager.getInstance();

        Boolean disabled = (FTPServerManager.getInstance() == null);
%>

<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
    <TBODY>
        <TR>
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*RE/ftpManagement.html_FTP_management*");</SCRIPT>
                <%
        if (disabled) {
                %>
                <DIV class=portletItem id=01>
                    <!-- TITLE part -->
                    <DIV>
                        <fmt:message key="FTPManagement.accountList" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <IMG class=labelHomePage title=list alt="FTP account list" src="images/ftpList.png" align=middle border=0>
                        <fmt:message key="FTPManagement.pageDescr" bundle="${lang}"/>
                    </P>
                    <P>
                        <IMG src="images/warn.png"><fmt:message key="FTPManagement.error" bundle="${lang}"/>
                    </P>
                </DIV>
                <DIV class=portletItem id=02>
                    <DIV>
                        <fmt:message key="FTPManagement.newAccount" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <IMG class=labelHomePage title="addFTP" alt="Add an FTP account" src="images/ftpCreate.png" align=middle border=0>
                        <fmt:message key="FTPManagement.pageAllows" bundle="${lang}"/>
                    </P>
                    <P>
                        <IMG src="images/warn.png"><fmt:message key="FTPManagement.error" bundle="${lang}"/>
                    </P>
                </DIV>
                <%                } else {
                %>                        <DIV class=portletItem id=01>
                    <!-- TITLE part -->
                    <DIV>
                        <A href="<%= response.encodeURL("listFTPAccounts.jsp")%>"><fmt:message key="FTPManagement.accountList" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href="<%= response.encodeURL("listFTPAccounts.jsp")%>"><IMG class=labelHomePage title=list alt="FTP account list" src="images/ftpList.png" align=middle border=0></A>
                        <fmt:message key="FTPManagement.pageDescr" bundle="${lang}"/>
                    </P>
                </DIV>
                <DIV class=portletItem id=02>
                    <DIV>
                        <A href="<%= response.encodeURL("addFTPAccountRequest.jsp?serviceName=" + serviceName)%>"><fmt:message key="FTPManagement.newAccount" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P> <A href="<%= response.encodeURL("addFTPAccountRequest.jsp?serviceName=" + serviceName)%>"><IMG class=labelHomePage title="addFTP" alt="Add an FTP account" src="images/ftpCreate.png" align=middle border=0></A><fmt:message key="FTPManagement.pageAllows" bundle="${lang}"/>
                    </P>
                </DIV>
                <%        }
                %>

            </TD>
        </TR>
    </TBODY>
</TABLE>


<jsp:include page="footer.jsp"/>