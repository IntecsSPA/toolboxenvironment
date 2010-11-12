<!-- 
 -
 -  Copyright 2003-2010 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: configureServiceRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.15 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
 -->
<%@ page import="it.intecs.pisa.util.*,
                it.intecs.pisa.toolbox.service.*,
                it.intecs.pisa.common.tbx.*,
                it.intecs.pisa.toolbox.*,
                java.util.*,
                it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator,
         it.intecs.pisa.toolbox.util.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<SCRIPT language="JavaScript">
    function setCheckbox(id,checkEl)
    {
        var elementHidden;

        elementHidden=document.getElementById(id);
        elementHidden.value=checkEl.checked;

    }
    function goDownloadPage()
    {
        var button= document.getElementById("portalSchemaButton");

        if(button.value=="Cancel import from SSE portal")
        {
            var label=document.getElementById("portalSelectionString");
            label.innerHTML="<b></b>";

            var component= document.getElementById("fullSchemaSet");
            component.value=" ";

            button.value="Download schema from SSE portal ";
        }
        else   openTab('xml','Import schema set','<%= response.encodeURL("setSchemaFromPortalStep1.jsp?download=version&serviceName=" + (String) session.getAttribute("newServiceName") + "&importOrCreate=create")%>','Import');
    }

    function toggle_visibility(id) {
        /*
        var e = document.getElementById(id);

        if(e.style.display == 'block' || e.style.display == "block")
            e.style.display = 'none';
        else
            e.style.display = 'block';*/

        var el = document.getElementById("jksButton");
        el.value = "";
        el.disabled = el.disabled ? false : true;
        el = document.getElementById("jksUserId");
        el.value = "";
        el.disabled = el.disabled ? false : true;
        el = document.getElementById("jksPasswordId");
        el.value = "";
        el.disabled = el.disabled ? false : true;

        el = document.getElementById("XACMLcheckbox");
        el.value = "";
        el.disabled = el.disabled ? false : true;

        //el = document.getElementById("XACMLDivArea");
        //el.disabled = el.disabled ? false : true;
        el = document.getElementById("xacmlButton");
        var check = document.getElementById("XACMLcheckbox");
        if (check.disabled){
            el.disabled = true;
        }else{
            el.disabled = (document.getElementById("XACMLcheckbox").checked) ? false : true;
        }


    }

    function toggleXACML(){
        el = document.getElementById("xacmlButton");
        el.disabled = el.disabled ? false : true;
        el.value = "";



    }

    function checkWSFields(frm) {
        if (!frm.WSSecurityFlag.checked)
            return true;
        var jksFile = frm.jksFile;
        var jksUserName = frm.jksUserName;
        var password = frm.jksPasswd;

        if ( (jksFile.value.length < 1) || (jksUserName.value.length < 1) || (password.value.length < 1)) {
            alert("Missing parameter. Please fill all mandatory WS-Security fields (KeyStore, UserName and Password)");
            frm.jksFile.focus();
            return false;
        }

        if ( (checkEmptyString(jksUserName.value) == true) || (checkEmptyString(password.value) == true) ) {
            alert("Missing parameter. Please fill all mandatory WS-Security fields (KeyStore, UserName and Password)");
            frm.userName.focus();
            return false;
        }
        /*
        if ( (frm.sslCertificateLocation.value.length < 1)){
            Ext.Msg.confirm('', 'WS-Security for asynchronous operation response requires HTTPS and so SSL certificate: are you sure you want to continue without SSL certificate?',
                function(btn, text){
                    if (btn == 'yes'){
                        document.conf.submit();
                    }
                }

            );
        return false;
        }*/

        return true;

    }

</SCRIPT>    

<%
        String serviceName = request.getParameter("serviceName");
        TBXService tbxservice;
        Interface implInterface;
        String suspendMode;
        String sslCertificateLocation;
        String targetNS;
        boolean ssl;
        String warn = (request.getParameter("warn") == null ? "" : request.getParameter("warn"));
        String serviceAbstract = "";
        String serviceDescription = "";
        String queuingChecked;
        String[] schemas;
        String serviceSchemaName;
        String validationActive;

        tbxservice = ServiceManager.getInstance().getService(serviceName);
        implInterface = tbxservice.getImplementedInterface();

        boolean ws_security = tbxservice.isWSSecurity();
        String keystoreLocation = "";

        String keystoreUser = ToolboxSecurityConfigurator.getJKSuser(tbxservice);
        String keystorePassword = ToolboxSecurityConfigurator.getJKSpassword(tbxservice);

        ToolboxSecurityConfigurator.getJKSpassword(tbxservice);
        String XACMLUrl = ToolboxSecurityConfigurator.getXACMLpolicyURL(serviceName);

        targetNS = implInterface.getTargetNameSpace();
        suspendMode = tbxservice.getSuspendMode();
        sslCertificateLocation = tbxservice.getSSLcertificate();
        ssl = (sslCertificateLocation != null && sslCertificateLocation.length() != 0 ? true : false);
        serviceAbstract = IOUtil.inputToString(tbxservice.getServiceAbstract());


        serviceDescription = IOUtil.inputToString(tbxservice.getServiceDescription());
        queuingChecked = tbxservice.isQueuing() ? "checked" : "";

        schemas = tbxservice.getScheamsPaths();
        serviceSchemaName = tbxservice.getImplementedInterface().getSchemaRoot();

        validationActive = tbxservice.getImplementedInterface().isValidationActive() ? "checked" : "";
        boolean schemaItemsActive = tbxservice.getImplementedInterface().isValidationActive();

        PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
        String home = (String) messages.getObject("configureServiceRequest.home");
        String serviceConf = (String) messages.getObject("configureServiceRequest.serviceConf");
        String change = (String) messages.getObject("configureServiceRequest.change");
        String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;" +
                "&nbsp;<a href='serviceConfiguration.jsp?serviceName=" + serviceName + ">" + serviceConf + "</a>&nbsp;&gt;" + change;
%>
<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY>
        <TR>
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceViewingConfiguration.html_Viewing_a_service_configuration*");</SCRIPT>
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                    <TBODY>
                        <TR>
                            <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="configureServiceRequest.change" bundle="${lang}"/></FONT></P>
                                <P>
                                <form id="configureServiceFormId" name="conf" method="post" enctype="multipart/form-data" action="<%= response.encodeURL("manager?cmd=configureService")%>">
                                    <%
        if (request.getParameter("requestComingFromServiceCreationWizard") != null) {
                                    %>
                                    <input type="hidden" name="requestComingFromServiceCreationWizard" id="requestComingFromServiceCreationWizard"  value="true"></input>
                                    <%        }
                                    %>

                                    <input type="hidden" name="serviceName" value="<%= request.getParameter("serviceName")%>">
                                    <!-- Page contents table-->
                                    <table width="90%" cellspacing="2" cellpadding="2" align="center">
                                        <tr><!-- Row 4 -->
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.serviceInfo" bundle="${lang}"/></td>
                                        </tr>
                                        <tr><!-- Row 2 -->
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureServiceRequest.serviceName" bundle="${lang}"/><font color="FF0000"></font></td>
                                            <td class="sortable" nowrap><%=serviceName%></td>
                                        </tr>
                                        <tr><!-- Row 2 -->
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureServiceRequest.serviceAbstr" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><input name="serviceAbstract" type="text" size="80" value="<%=serviceAbstract != null ? serviceAbstract : ""%>"></td>
                                        </tr>
                                        <tr><!-- Row 2 -->
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureServiceRequest.serviceDescr" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><textarea name="serviceDescription" cols="70" rows="6"><%=serviceDescription != null ? serviceDescription : ""%></textarea></td>
                                        </tr>

                                        <%
        if (schemas.length > 0) {
                                        %>
                                        <tr><!-- Row 1 -->
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.schemaSpec" bundle="${lang}"/></td>
                                        </tr>

                                        <tr>
                                            <td class="sortable"><fmt:message key="configureServiceRequest.enableValidation" bundle="${lang}"/></td>
                                            <td class="sortable"><input type="checkbox" name="validationActive" <%=validationActive%>></input></td>
                                        </tr>
                                        <tr>
                                            <td class="sortable"><fmt:message key="configureServiceRequest.schemaSelectionDetails" bundle="${lang}"/></td>
                                            <td class="sortable"/>
                                        </tr>
                                        <%

        }

        for (String ss : schemas) {
            boolean isRootSchema = false;
            String isMainSchema;

            isRootSchema = serviceSchemaName != null && serviceSchemaName.equals(ss);

            if (isRootSchema) {
                isMainSchema = "checked";
            } else {
                isMainSchema = "";
            }


            //<%= schemaItemsActive?"enabled":"disabled"

                                        %>
                                        <tr>
                                            <td class="sortable" width="20%" colspan=1 nowrap><input type="radio" name="mainSchema" value="<%= ss%>" <%=isMainSchema%> ></input><%= ss%> <a href="manager?cmd=getSchemaFile&amp;serviceName=<%= serviceName%>&amp;schema=<%= ss%>"><img src="images/download-icon.gif" alt="download"></a> </td>
                                            <td class="sortable" width="80%" colspan=1 nowrap>
                                                <input type="file" name="schemaFile<%=ss%>" size="100%" />
                                        </td> </tr>
                                        <%
        }

        String newSSEVersion = "";
        try {
            newSSEVersion = URLReader.downloadLastVersion(serviceName);
            if (newSSEVersion.endsWith("/")) {
                newSSEVersion = newSSEVersion.substring(0, newSSEVersion.length() - 1);
            }
        } catch (Exception ecc) {
            newSSEVersion = "";
        }
        if (newSSEVersion.equals("") == false) {
                                        %>
                                        <tr>
                                            <td class="sortable" width="20%" colspan=1 nowrap></td>
                                            <td class="sortable" width="80%" colspan=1 nowrap>
                                                <input type="checkbox" name="updateSSESchemaCheckBox" ><fmt:message key="configureService.updateSSESchemaSet" bundle="${lang}"/><%=newSSEVersion%></input>
                                            </td>
                                        </tr>
                                        <%
        }
                                        %>



                                        <tr><!-- Row 4 -->
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.Request" bundle="${lang}"/></td>
                                        </tr>
                                        <tr><!-- Row 5 -->
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureServiceRequest.Queue" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><input name="queuing" type="checkbox" <%= queuingChecked%>></td>
                                        </tr>
                                        <tr><!-- Row 6 -->
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureServiceRequest.Suspend" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap>
                                                <input type="radio" name="suspendMode" value="soft" <%= suspendMode.equals("soft") ? "checked" : ""%>> <fmt:message key="configureServiceRequest.SOFT" bundle="${lang}"/> <br>
                                                <input type="radio" name="suspendMode" value="hard" <%= suspendMode.equals("hard") ? "checked" : ""%>> <fmt:message key="configureServiceRequest.HARD" bundle="${lang}"/>
                                            </td>
                                        </tr>

                                        <tr><!-- Row 7-->
                                            <td width="50%" rowspan="1" nowrap class="sortable"><input type="checkbox" name="SSLFlag" <%= ssl ? "checked" : ""%>> <fmt:message key="configureServiceRequest.Secure" bundle="${lang}"/><font color="FF0000"><%=warn.equals("missingCertificateLocation") ? " - MISSING SSL CERTIFICATE LOCATION!" : (warn.equals("noCertificateLocation") ? " - NO SUCH SSL CERTIFICATE!" : "")%></td>
                                            <td class="sortable" nowrap>
                                                <fmt:message key="configureServiceRequest.SSL" bundle="${lang}"/>
                                                <input name="sslCertificateLocation" type="text" size="60" value="<%= ssl ? sslCertificateLocation : ""%>">
                                            </td>
                                        </tr>


                                        <tr><!-- Row 8  WS-SECURITY -->
                                            <td width="50%" rowspan="1" nowrap class="sortable">
                                                <input type="checkbox" name="WSSecurityFlag" <%= ws_security ? "checked" : ""%> onclick="toggle_visibility('JKSDivArea')" >
                                                <fmt:message key="createServiceRequest.wssecurity" bundle="${lang}"/><font color="FF0000">
                                            </td>
                                            <td class="sortable" nowrap>
                                            <TABLE id="JKSDivArea" cellSpacing=0 cellPadding=0 width="90%" align=left style=<%= (tbxservice.isWSSecurity()) ? "display:block" : "display:block"%> >
                                                <TBODY>
                                                    <tr> <!-- keystore -->
                                                        <td class="sortable" nowrap> <fmt:message key="createServiceRequest.keystore" bundle="${lang}"/></td>
                                                        <td class="sortable" width="80%" colspan=1 nowrap>
                                                            <input type="file" name="jksFile" id="jksButton"  size="100%" <%= ws_security ? "" : "disabled"%>/>
                                                        </td>
                                                    </tr>
                                                    <TR><!-- username -->
                                                        <TD class="sortable" noWrap align=left width="40%"><fmt:message key="createServiceRequest.alias" bundle="${lang}"/></TD
                                                        <TD align=left width="20%">
                                                            <input name="jksUserName" id="jksUserId" type="text" size="30" value="<%=keystoreUser%>" <%= ws_security ? "" : "disabled"%> >
                                                        </TD>
                                                    </TR>
                                                    <TR><!-- password -->
                                                        <TD class="sortable" noWrap align=left ><fmt:message key="loginRequest.password" bundle="${lang}"/></TD>
                                                        <TD align=left width="20%">
                                                            <INPUT type=password id="jksPasswordId" maxLength=40 size=30 name=jksPasswd value="<%=keystorePassword%>" <%= ws_security ? "" : "disabled"%> >
                                                        </TD>
                                                        <TD>&nbsp;</TD>
                                                    </TR>
                                                    <tr><!-- XACML -->
                                                        <td width="50%" rowspan="1" nowrap class="sortable">
                                                            <input type="checkbox" id="XACMLcheckbox" name="XACMLFlag" <%= (XACMLUrl.length() != 0) ? "checked" : ""%> onclick="toggleXACML()" <%= ws_security ? "" : "disabled"%> />XACML
                                                            <%
        if (tbxservice.hasWSSecurity()) {
            if (XACMLUrl.length() != 0) {
                                                            %>
                                                            (<A class=smpgttl href="#" onclick="javascript:downloadPopup('manager?cmd=proxyRedirect&method=GET&url=<%=XACMLUrl%>');"><fmt:message key="viewServiceInfo.see" bundle="${lang}"/> </A>)
                                                            <%
            }
        }
                                                            %>
                                                        </td>
                                                        <td>
                                                            <TABLE id="XACMLDivArea" cellSpacing=0 cellPadding=0 width="90%" align=left  >
                                                                <TBODY id="XACMLDivArea" disabled=<%= ws_security ? "true" : "false"%>>
                                                                    <tr>
                                                                        <td class="sortable" width="80%" colspan=1 nowrap>
                                                                            <input type="file" name="xacmlFile" id="xacmlButton"  size="100%" <%= (XACMLUrl.length() == 0) ? "disabled=true" : ""%>>
                                                                        </td>
                                                                    </tr>
                                                                </TBODY>
                                                            </TABLE>
                                                        </td>
                                                    </tr>
                                                </TBODY>
                                            </TABLE>
                                        </tr>
                                        <%
        Hashtable<String, Hashtable<String, String>> variables;
        variables = implInterface.getUserVariable();

        if (variables.isEmpty() == false) {
                                        %>
                                        <tr>
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.variablesHader" bundle="${lang}"/></td>
                                        </tr>
                                        <%        }

        Enumeration<String> keys = variables.keys();
        String key, type, displayText, value;
        Hashtable<String, String> varValue;
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            varValue = variables.get(key);

            type = varValue.get(Interface.VAR_TABLE_TYPE);
            displayText = varValue.get(Interface.VAR_TABLE_DISPLAY_TEXT);
            value = varValue.get(Interface.VAR_TABLE_VALUE);

            if (type.equals("string") || type.equals("integer")) {
                                        %>
                                        <tr>
                                            <td class="sortable" width="50%" colspan=1 nowrap><%=displayText%></td>
                                            <td class="sortable" nowrap><input  type="text" name="serviceVariable<%=key%>" size="80" value="<%=value%>"></input></td>
                                        </tr>

                                        <%
                                                    } else if (type.equals("boolean")) {
                                        %>
                                        <tr>
                                            <td class="sortable" width="50%" colspan=1 nowrap><%=displayText%></td>
                                            <td class="sortable" nowrap><input  name="serviceVariable<%=key%>" id="serviceVariable<%=key%>" type="hidden" value="<%=value%>"/>
                                            <input  type="checkbox"  name="chk<%=key%>" id="chk<%=key%>" <%=value.equals("true") ? "checked" : ""%> onchange="javascript:setCheckbox('serviceVariable<%=key%>',this)"/></td>
                                        </tr>

                                        <%
            }


        }
                                        %>
                                        <tr><!-- Row 8 -->
                                            <td colspan="3" rowspan="2" nowrap align="right"><input type="submit"  onClick= <%=ws_security ? "return true" : "\"return checkWSFields(conf)\""%> value="<fmt:message key="configureServiceRequest.Configure" bundle="${lang}"/>"></td>
                                        </tr>
                                    </table>
                                </form>
                            &nbsp; </TD>
                        </TR>
                    </TBODY>
            </TABLE></TD>
        </TR>
    </TBODY>
</TABLE> 
<jsp:include page="footer.jsp"/>

