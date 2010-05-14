<%@ page import="it.intecs.pisa.toolbox.configuration.*,it.intecs.pisa.soap.toolbox.*, java.util.*, org.w3c.dom.*, it.intecs.pisa.util.*,java.io.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>

<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%
String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));

String pageStatus;
String disabled;
pageStatus=request.getParameter("pageStatus");

if(pageStatus==null || pageStatus.equals("disabled") || pageStatus.equals("refresh"))
     disabled=new String("disabled");
else
     disabled=new String("");

request.setAttribute("pageStatus",pageStatus);

boolean isWarning = (warn.length() != 0);


ToolboxConfiguration configuration;

configuration=ToolboxConfiguration.getInstance();

String queuingChecked = "";
String firstTimeChecked = "";
String toolboxVersionChecked = "";
String schemaVersionChecked = "";
String inMsgLogChecked = "";
String outMsgLogChecked = "";

String currLogDir = configuration.getConfigurationValue(ToolboxConfiguration.LOG_DIR);
String currLogLevel = configuration.getConfigurationValue(ToolboxConfiguration.LOG_LEVEL);
String currLogFileSize = configuration.getConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE);
String currFtpAdminDir = configuration.getConfigurationValue(ToolboxConfiguration.FTP_ADMIN_DIR);
String currFtpAdminPassword = configuration.getConfigurationValue(ToolboxConfiguration.FTP_ADMIN_PASSWORD);
String currFtpPort = configuration.getConfigurationValue(ToolboxConfiguration.FTP_PORT);
String currFtpPoolPort = configuration.getConfigurationValue(ToolboxConfiguration.FTP_POOL_PORT);
String currFtpServerHost = configuration.getConfigurationValue(ToolboxConfiguration.FTP_SERVER_HOST);
String currApacheAddress = configuration.getConfigurationValue(ToolboxConfiguration.APACHE_ADDRESS);
String currApachePort = configuration.getConfigurationValue(ToolboxConfiguration.APACHE_PORT);
String currProxyHost = configuration.getConfigurationValue(ToolboxConfiguration.PROXY_HOST);
String currProxyPort = configuration.getConfigurationValue(ToolboxConfiguration.PROXY_PORT);
String currTomcatPort = configuration.getConfigurationValue(ToolboxConfiguration.TOMCAT_PORT);
String currTomcatSSLPort = configuration.getConfigurationValue(ToolboxConfiguration.TOMCAT_SSL_PORT);
String mailErrorSSEChecked = "";
String mailErrorSPChecked = "";
String currSender = configuration.getConfigurationValue(ToolboxConfiguration.SENDER);
String currRecipients = configuration.getConfigurationValue(ToolboxConfiguration.RECIPIENTS);
String currSmtpServer = configuration.getConfigurationValue(ToolboxConfiguration.SMTP_SERVER);
String currSmtpAuthUsername= configuration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_USERNAME);
String currSmtpAuthPassword= configuration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_PASSWORD);
String currSmtpPort= configuration.getConfigurationValue(ToolboxConfiguration.SMTP_PORT);
String mailFrom=configuration.getConfigurationValue(ToolboxConfiguration.MAIL_FROM);
String currCompanyName = configuration.getConfigurationValue(ToolboxConfiguration.COMPANY_NAME);
String currCompanyContact = configuration.getConfigurationValue(ToolboxConfiguration.COMPANY_CONTACT);
String ebRRREpoHome=configuration.getConfigurationValue(ToolboxConfiguration.EBRR_REPO_HOME);
String tbxKeystorePwd= configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD);
String sseOpTeamEmail=configuration.getConfigurationValue(ToolboxConfiguration.SSE_SUPPORT_TEAM_EMAIL);

    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK))) {
        firstTimeChecked = "checked";
    }
    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.QUEUING))) {
        queuingChecked = "checked";
    }
    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.SCHEMA_VERSION_CHECK))) {
        schemaVersionChecked = "checked";
    }
    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_VERSION_CHECK))) {
        toolboxVersionChecked = "checked";
    }
    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.INPUT_MESSAGES_LOG))) {
        inMsgLogChecked = "checked";
    }
    if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.OUTPUT_MESSAGES_LOG))) {
        outMsgLogChecked = "checked";
    }
    if (configuration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR).equals("SSE") || configuration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR).equals("BOTH")) {
        mailErrorSSEChecked = "checked";
    }
    if (configuration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR).equals("SP") || configuration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR).equals("BOTH")) {
        mailErrorSPChecked = "checked";
    }


boolean tbxLevelKeystoreSet=false;

tbxLevelKeystoreSet=Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE));


PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));

%>
<table width="90%" bordercolor="#808080" cellspacing="1" cellpadding="1" border="0" align="center">
                <tr><!-- Row 1 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.fSetting" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 2 -->
                    <td class="tdItem" width="50%" colspan=1 nowrap><fmt:message key="configureToolboxRequest.queue" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="globalQueuing" type="checkbox" <%=disabled%> <%=queuingChecked%> ></td>
                </tr>
                <tr><!-- Row 2 -->
                    <td class="tdItem" width="50%" colspan=1 nowrap><fmt:message key="configureToolboxRequest.checkVers" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="toolboxVersionCheck" type="checkbox" <%=disabled%> <%=toolboxVersionChecked%> ></td>
                </tr>
                <tr><!-- Row 2 -->
                    <td class="tdItem" width="50%" colspan=1 nowrap><fmt:message key="configureToolboxRequest.checkSchema" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="schemaVersionCheck" type="checkbox" <%=disabled%> <%=schemaVersionChecked%> ></td>
                </tr>
                
                <tr><!-- Row 4 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.FTPSet" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 5 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.FTPAdmin" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingFTPAdminDir") ? " - MISSING!" : ""%></font></td>
                    <td class="tdForm" nowrap><input name="ftpAdminDir" type="text" value="<%= currFtpAdminDir %>" size="40" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 6 -->
                    <td width="50%" nowrap class="tdItem"><fmt:message key="configureToolboxRequest.FTPPW" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingFTPAdminPassword") ? " - MISSING!" : ""%></font></td>
                    <td class="tdForm" nowrap><input name="ftpAdminPassword" type="text" value="<%= currFtpAdminPassword %>" size="10" maxlength="10" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 7 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.FTPPort" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingFTPPort") ? " - MISSING!" : (warn.equals("wrongFormatFTPPort") ? " - WRONG FORMAT!" : "") %></td>
                    <td nowrap class="tdForm"><input name="ftpPort" type="text" value="<%= currFtpPort %>" size="5" maxlength="5" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 7 a -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.FTPPoolPort" bundle="${lang}"/><font color="FF0000"></td>
                    <td nowrap class="tdForm"><input name="ftpPoolPort" type="text" value="<%= currFtpPoolPort %>" size="60" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 7 b -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.FTPServerHost" bundle="${lang}"/><font color="FF0000"></td>
                    <td nowrap class="tdForm"><input name="ftpServerHost" type="text" value="<%= currFtpServerHost %>" size="60" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.logSet" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 11 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.logLevel" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><select name="logLevel" <%=disabled%>>
                            <option <%= currLogLevel.equals("ALL") ? "selected" : "" %> value=ALL><fmt:message key="configureToolboxRequest.ALL" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("DEBUG") ? "selected" : "" %> value=DEBUG><fmt:message key="configureToolboxRequest.DEBUG" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("INFO") ? "selected" : "" %> value=INFO><fmt:message key="configureToolboxRequest.INFO" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("WARN") ? "selected" : "" %> value=WARN><fmt:message key="configureToolboxRequest.WARN" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("ERROR") ? "selected" : "" %> value=ERROR><fmt:message key="configureToolboxRequest.ERROR" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("FATAL") ? "selected" : "" %> value=FATAL><fmt:message key="configureToolboxRequest.FATAL" bundle="${lang}"/></option>
                            <option <%= currLogLevel.equals("OFF") ? "selected" : "" %> value=OFF><fmt:message key="configureToolboxRequest.OFF" bundle="${lang}"/></option>
                        </select>
                    </td>
                </tr>
                <tr><!-- Row 12 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.logDir" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingLogDir") ? " - MISSING!" : ""%></td>
                    <td class="tdForm" nowrap><input name="logDir" type="text" value="<%= currLogDir %>" size="40" <%=disabled%>></td>
                </tr>
                <tr>
                    <td nowrap class="tdItem" width="50%"><fmt:message key="configureToolboxRequest.logFile" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="logFileSize" type="text" value="<%=currLogFileSize%>" <%=disabled%> ></td>
                </tr>
                
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.tomcat" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.portn" bundle="${lang}"/><font color="FF0000"><%=warn.equals("wrongFormatTomcatPort") ? " - WRONG FORMAT!" : "" %></td>
                    <td class="tdForm" nowrap><input name="tomcatPort" type="text" value="<%= currTomcatPort %>" size="5" maxlength="5" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.portSSL" bundle="${lang}"/><font color="FF0000"><%=warn.equals("wrongFormatTomcatSSLPort") ? " - WRONG FORMAT!" : "" %></td>
                    <td class="tdForm" nowrap><input name="tomcatSSLPort" type="text" value="<%= currTomcatSSLPort %>" size="5" maxlength="5" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.apache" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.hostName" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="apacheAddress" type="text" value="<%= currApacheAddress %>" <%=disabled%> ></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.portn" bundle="${lang}"/><font color="FF0000"><%=warn.equals("wrongFormatApachePort") ? " - WRONG FORMAT!" : "" %></td>
                    <td class="tdForm" nowrap><input name="apachePort" type="text" value="<%= currApachePort %>" size="5" maxlength="5" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.proxy" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.hostName" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="proxyHost" type="text" value="<%= currProxyHost %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.portn" bundle="${lang}"/><font color="FF0000"><%=warn.equals("wrongFormatProxyPort") ? " - WRONG FORMAT!" : "" %></td>
                    <td class="tdForm" nowrap><input name="proxyPort" type="text" value="<%= currProxyPort %>" size="5" maxlength="5" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.contact" bundle="${lang}"/></td>						
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.companyName" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingCompanyName") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="companyName" type="text" value="<%= currCompanyName %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.companyRef" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="companyContact" type="text" value="<%= currCompanyContact %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.mail" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSender") ? " - MISSING!" : (warn.equals("wrongSender") ? " - WRONG FORMAT!" : "") %></td>
                    <td class="tdForm" nowrap><input name="sender" type="text" value="<%= currSender %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="configureToolboxRequest.errorReport" bundle="${lang}"/></td>						
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.mailErrSSE" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="mailErrorSSE" type="checkbox" <%=mailErrorSSEChecked%> <%=disabled%>></td>
                </tr>
                 <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.other" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingTo") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="sseOpTeamEmail" type="text" value="<%= sseOpTeamEmail %>" size="40" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.mailErr" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="mailErrorSP" type="checkbox" <%=mailErrorSPChecked%> <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.other" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingTo") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="recipients" type="text" value="<%= currRecipients %>" size="40" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.SMTP" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSmtpServer") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="smtpServer" type="text" value="<%= currSmtpServer %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.SMTP.username" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSmtpServer") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="smtpServerAuthUsername" type="text" value="<%= currSmtpAuthUsername %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.SMTP.password" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSmtpServer") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="smtpServerAuthPassword" type="text" value="<%= currSmtpAuthPassword %>" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.SMTP.mailFrom" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSmtpServer") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="mailFrom" type="text" value="<%= mailFrom %>" <%=disabled%>></td>
                </tr>
               
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="configureToolboxRequest.SMTP.port" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingSmtpServer") ? " - MISSING!" : "" %></td>
                    <td class="tdForm" nowrap><input name="smtpServerPort" type="text" value="<%= currSmtpPort %>" <%=disabled%>></td>
                </tr>

                <tr><!-- Row 8 -->
                    <td class=sortable colspan="2" nowrap><fmt:message key="ebRR.configuration.configSection" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="ebRR.configuration.ebRepoHome" bundle="${lang}"/><font color="FF0000"></td>
                    <td class="tdForm" nowrap><input name="ebRRRepoHome" type="text" value="<%= ebRRREpoHome %>" <%=disabled%>></td>
                </tr>

                <tr>
                    <td class=sortable colspan="2" nowrap><fmt:message key="security.tbxlevel.keystore.section" bundle="${lang}"/></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="security.tbxlevel.keystore" bundle="${lang}"/>(
                        <% if(tbxLevelKeystoreSet==true) { %><fmt:message key="set" bundle="${lang}"/>
                        <% }else{ %><fmt:message key="notset" bundle="${lang}"/><% } %>
                        )</td>
                    <td class="tdForm" nowrap><input name="keystore" type="file" <%=disabled%>></td>
                </tr>
                <tr><!-- Row 9 -->
                    <td class="tdItem" nowrap width="50%"><fmt:message key="security.tbxlevel.keystore.password" bundle="${lang}"/></td>
                    <td class="tdForm" nowrap><input name="keystorePwd" type="text" value="<%= tbxKeystorePwd %>" <%=disabled%>></td>
                </tr>

       
                <tr><!-- Row 13 -->
                    <td colspan="2" rowspan="2" nowrap align="right">
                        <c:choose>
                            <c:when test="${pageStatus=='disabled'}">
                                    <input type="submit" value="<fmt:message key="viewToolboxConfiguration.change" bundle="${lang}"/>">
                            </c:when>
                             <c:when test="${pageStatus=='refresh'}">
                                   
                            </c:when>
                            <c:otherwise>
                                      <input type="submit" value="<fmt:message key="configureToolboxRequest.Configure" bundle="${lang}"/>">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>						
            </table>
