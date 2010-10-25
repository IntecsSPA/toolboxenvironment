<%@ page language="java"   %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">   
<%
            String languageSet = request.getParameter("lang");
            if (languageSet == null) {
                languageSet = "en";
            }
            request.getSession().setAttribute("language", languageSet);
            String requestURL = request.getQueryString();
            if ((requestURL != null) && (requestURL.indexOf("viewServiceLog.jsp") < 0) && (requestURL.indexOf("viewAllServicesInstances.jsp") < 0)) {
                requestURL = null;
            }
%>
<c:if test="${lang == null}">
    <fmt:setLocale value="en" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<c:if test="${param.lang!= null}">
    <fmt:setLocale value="${param.lang}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<HTML lang=it><HEAD><TITLE>TOOLBOX - Login</TITLE>
        <META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
        <LINK href="jsScripts/dom.css" rel=stylesheet>
        <LINK href="jsScripts/dom.directory.css" rel=stylesheet>
        <LINK href="jsScripts/portal.css" rel=stylesheet>
        <LINK href="jsScripts/styles.css" rel=stylesheet>
        <LINK rel="SHORTCUT ICON" href="images/toolboxIcon.gif">
        <META http-equiv=Pragma content=no-cache>
        <META http-equiv=Cache-Control content="no-store, no-cache, must-revalidate, post-check=0, pre-check=0">
        <META http-equiv=Expires content=0>
        <SCRIPT language=JavaScript src="jsScripts/CommonScript.js"></SCRIPT>
        <META content="MSHTML 6.00.2900.2873" name=GENERATOR></HEAD>
    <BODY id=body text=#000000 bgColor=#ffffff leftMargin=0 topMargin=0 marginheight="0" marginwidth="0">
        <TABLE cellSpacing=0 cols=1 cellPadding=0 width="98%" align=center bgColor=#607a92 border=1>
            <TBODY>
                <TR>
                    <TD vAlign=center align=right width="100%">
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
                            <TBODY>
                                <TR class=flagcolour>
                                    <TD height=2><IMG src="images/1x1.gif"></TD></TR>
                                <TR>
                                    <TD>
                                        <TABLE class=flagcolour cellSpacing=0 cellPadding=0 width="100%"
                                               align=center border=0>
                                            <TBODY>
                                                <TR bgColor=#607a92>
                                                    <TD vAlign=bottom align=left bgColor=#607a92 height=60><IMG
                                                            src="images/toolboxLogo.png"
                                                            border=0></A> </TD>
                                                    <TD vAlign=bottom align=middle bgColor=#607a92 height=60><!--A
                                                            href="http://services.eoportal.org/index.jsp" target=_top><IMG
                                                                src="images/sse.gif" border=0></A--> </TD>
                                                    <TD vAlign=bottom align=right bgColor=#607a92 height=60><IMG
                                                            height=60 src="images/directory_top.png"
                                                            width=293 border=0 name=top> </TD></TR>
                                                <TR class=flagcolour>
                                                    <TD colSpan=3 height=1><IMG
                                                            src="images/1x1.gif"></TD></TR>
                                                <TR bgColor=#607a92 height=10>
                                                    <TD bgColor=#607a92 colSpan=2 height=12></TD>
                                                    <TD vAlign=top align=right bgColor=#607a92><IMG height=13
                                                                                                    src="images/directory_bot.gif" width=293
                                                                                                    border=0 name=bot> </TD></TR>
                                                <TR bgColor=#e0dfe3>
                                                    <TD colSpan=3 height=1><IMG height=1 alt=""
                                                                                src="images/1x1.gif" border=0></TD></TR>
                                                <TR class=flagcolour>
                                                    <TD colSpan=3>
                                                        <DIV class=mnu>
                                                            <TABLE id=menuTop width="100%" align=center bgColor=#000040
                                                                   border=0>
                                                                <TBODY>
                                                                    <TR align=middle>
                                                                        <TD class=itm align=left width="90%"><fmt:message key="loginRequest.not_logged" bundle="${lang}"/></TD>
                                                                        <TD align=right width="10%"><A class=itm
                                                                                                       href="loginRequest.jsp"
                                                                                                       target=_top><fmt:message key="loginRequest.login" bundle="${lang}"/></A></TD></TR></TBODY></TABLE></DIV></TD></TR>
                                                <TR bgColor=#e0dfe3>
                                                    <TD colSpan=3 height=1><IMG height=1 alt=""
                                                                                src="images/1x1.gif" border=0></TD></TR>
                                                <TR bgColor=#607a92>
                                                    <TD class="itm path" id=breadCrumb align=left colSpan=2></TD>
                                                    <TD class="itm path" id=infoCenter align=right></TD></TR>
                                                </TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE>
                        <SCRIPT>
                        </SCRIPT>
                    </TD></TR>
                <TR vAlign=top>
                    <TD></TD></TR>
                <TR vAlign=top height="81%">
                    <TD class=darkbloc id=main2 vAlign=top align=middle width="100%"
                        height="100%">
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center
                               valign="top"><TBODY>
                                <TR>
                                    <TD id=main>
                                        <SCRIPT language=JavaScript>

                                            function checkEmptyString(str) {
                                                var index;
                                                for (index=0; index < str.length; index++) {
                                                    if (str.charAt(index) != ' ') {
                                                        return false;
                                                    }
                                                }
                                                return true;
                                            }

                                            function checkMandatoryFields(frm) {

                                                var userId = frm.userName;
                                                var password = frm.password;
                                                if ( (userId.value.length < 1) || (password.value.length < 1) ) {
                                                    alert("Missing parameter. Please fill all mandatory fields.");
                                                    frm.userName.focus();
                                                    return false;
                                                }

                                                if ( (checkEmptyString(userId.value) == true) || (checkEmptyString(password.value) == true) ) {
                                                    alert("Missing parameter. Please fill all mandatory fields.");
                                                    frm.userName.focus();
                                                    return false;
                                                }

                                                return true;

                                            }
                                        </SCRIPT>

                                        <SCRIPT>
                                            addBreadCrumb("Login");
                                        </SCRIPT>
                                        <BR>
                                        <P class=arbloc><fmt:message key="loginRequest.login" bundle="${lang}"/></P><BR>
                                        <P class=Header><FONT class=arttl><fmt:message key="loginRequest.log_pw" bundle="${lang}" />
                                                <BR><BR><I><BR></I></FONT></P><BR>
                                                    <%
                                                                if (requestURL != null) {
                                                                    int ix = requestURL.indexOf("/TOOLBOX/");
                                                                    requestURL = requestURL.substring(ix + 9);
                                                    %>
                                        <FORM name="UserLoginForm"
                                              onsubmit="return checkMandatoryFields(UserLoginForm)"
                                              action="<%=requestURL%>"
                                              method="post">
                                            <%
                                                                    } else {
                                            %>
                                            <FORM name="UserLoginForm"
                                                  onsubmit="return checkMandatoryFields(UserLoginForm)"
                                                  action="main.jsp"
                                                  method="post">
                                                <%                                        }

                                                            String warn = request.getParameter("warn");
                                                            if (warn != null && warn.length() != 0) {
                                                %>
                                                <table width="100%">
                                                    <tr>
                                                        <td width="100%" align="left"><font class="PortalInputError"><%=warn%></font></td>
                                                    </tr>
                                                    <tr>
                                                        <td width="100%" align="left"><font class="PortalInputError"></font></td>
                                                    </tr>
                                                </table>
                                                <%
                                                            }
                                                %>
                                                <input type="hidden" name="language" value="<%= languageSet%>">
                                                <input type="hidden" name="lang" value="<%= languageSet%>">

                                                <TABLE width="100%">
                                                    <TBODY>
                                                        <TR>
                                                            <TD align=left width="100%"><FONT
                                                                    class=PortalInputError></FONT></TD></TR>
                                                        <TR>
                                                            <TD align=left width="100%"><FONT
                                                                    class=PortalInputError></FONT></TD></TR></TBODY></TABLE>
                                                <TABLE cellSpacing=2 cellPadding=1 width="90%" align=center>
                                                    <TBODY>
                                                        <TR>
                                                            <TD noWrap align=right width="40%"><FONT class=><fmt:message key="loginRequest.username" bundle="${lang}"/>&nbsp;&nbsp;</FONT></TD>
                                                            <TD align=left width="20%"><INPUT maxLength=40 size=30
                                                                                              name=userName> </TD>
                                                            <TD>&nbsp;</TD></TR>
                                                        <TR>
                                                            <TD noWrap align=right width="40%"><FONT class=><fmt:message key="loginRequest.password" bundle="${lang}"/>&nbsp;&nbsp;</FONT></TD>
                                                            <TD align=left width="20%"><INPUT type=password maxLength=40
                                                                                              size=30 name=password> </TD>
                                                            <TD>&nbsp;</TD></TR>
                                                        <TR>
                                                            <TD></TD></TR>
                                                        <TR>
                                                            <TD width="40%">&nbsp;</TD>
                                                            <TD align=middle width="20%"><INPUT title="<fmt:message key="loginRequest.onsystem" bundle="${lang}"/>" onclick="return checkMandatoryFields(UserLoginForm)" type=submit value=Login>
                                                            </TD>
                                                            <TD>&nbsp;</TD></TR></TBODY></TABLE></FORM>
                                            <SCRIPT language=JavaScript type=text/javascript>
                                                    <!--
                                    document.forms["UserLoginForm"].elements["userName"].focus()
                                    // -->
                                        </SCRIPT>

                                        <P></P>
                                <TABLE width="100%">
                                <TBODY>
                                <TR>
                                <TD align=left width="40%"><SPAN>
                                            <fmt:message key="loginRequest.after_msg" bundle="${lang}"/>
                                                    </SPAN></TD>
                                                    </TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE>
                                            <%-- Offer locale choice to user --%>
        <div align=center>
        <a href="loginRequest.jsp?lang=en">English</a> -

        <a href="loginRequest.jsp?lang=it">Italian</a> <br>
        </div>
        <DIV class=mnu>
        <TABLE cellSpacing=0 cellPadding=0 width="100%">
        <TBODY> 
        <TR>
        <TD class=footer>
        <DIV class=menu id=menuBottom><A class=center
    href="main.jsp?language=<%= languageSet%>"><fmt:message key="footer.home" bundle="${lang}"/></A> <A class=center
    href="aboutSSE.jsp?language=<%= languageSet%>"><fmt:message key="footer.about" bundle="${lang}"/></A> <A class=center
    href="copyrights.jsp?language=<%= languageSet%>"><fmt:message key="footer.copyright" bundle="${lang}"/></A> <A class=center
    href="mailto:toolbox@pisa.intecs.it"><fmt:message key="footer.contact" bundle="${lang}"/></A>
        </DIV>
        <DIV>
        <P><SMALL>&nbsp;&nbsp;&nbsp;<fmt:message key="footer.bestView" bundle="${lang}"/> <A
    href="http://www.microsoft.com/ie" target=_blank>Internet Explorer 6.0</A> or <A href="http://www.mozilla-europe.org/en/products/firefox/"
    target=_blank>Firefox 1.5</A>.</SMALL><BR>
        </P>
        </DIV></TD>
        </TR>
        </TBODY> 
        </TABLE>
        </DIV></BODY></HTML>
