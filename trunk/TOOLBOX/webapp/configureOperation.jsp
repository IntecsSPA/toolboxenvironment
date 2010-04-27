<!-- 
 -
 -  Copyright 2003-2004 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: configureAsynchronousOperationRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.8 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
-->
<%@ page import="it.intecs.pisa.toolbox.service.*,
         it.intecs.pisa.pluginscore.*,
         it.intecs.pisa.common.tbx.*,
         it.intecs.pisa.toolbox.*,
         java.util.*,
         it.intecs.pisa.toolbox.util.*,
         it.intecs.pisa.util.*,
         org.w3c.dom.*,
         javax.xml.transform.stream.*"  errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp" />
<%
    String serviceName;
    String operationName;
    DOMUtil domUtil;
    TBXService service;
    Interface implementedInterface;
    String interfacename;
    String interfaceVersion;
    String interfaceType;
    String interfaceMode;
    Interface interf;
    InterfacePluginManager interfmanager;
    String bc = "";
    String hasRepoInterface;
    String soapAction="";
    String admittedHosts;
    String value;
    String requestTimeoutMeasureUnit="";
    String requestTimeoutAmount="";
    String pushPollingRateMeasureUnit="";
    String pushPollingRateAmount="";
    String retryRateAmount="";
    String retryRateMeasureUnit="";
    String retryAttempts="";
    boolean retry=false;
    String[] scriptsNeededTypes=new String[0];
    Operation op=null;
    HashSet implementedOps;
    boolean isSoapActionModifiable=false;
    boolean isAsynchronous=false;

    domUtil = new DOMUtil(true);

    serviceName = request.getParameter("serviceName");
    operationName = request.getParameter("operationName");

    service = ServiceManager.getInstance().getService(serviceName);
    implementedInterface=service.getImplementedInterface();
    
    interfacename = implementedInterface.getName();
    interfaceVersion = implementedInterface.getVersion();
    interfaceType=implementedInterface.getType();
    interfaceMode=implementedInterface.getMode();

    op=implementedInterface.getOperationByName(operationName);
    isAsynchronous=op.getType().equals(Operation.OPERATION_TYPE_ASYNCHRONOUS)?true:false;
    scriptsNeededTypes=op.getNeededScriptsType();
    admittedHosts=implementedInterface.getAdmittedHosts();

    hasRepoInterface = interfacename.equals("") == false && interfaceVersion.equals("") == false ? "true" : "false";
    if (hasRepoInterface.equals("true")) {
        session.setAttribute("isRepoOperation", "true");
        isSoapActionModifiable=false;
    } else {   
        session.setAttribute("isRepoOperation", "false");
        isSoapActionModifiable=true;
    }

    soapAction=op.getSoapAction();

    value=op.getRequestTimeout();
    requestTimeoutAmount=value.substring(0,value.length()-1);
    requestTimeoutMeasureUnit=value.substring(value.length()-1);

    if(isAsynchronous==true)
    {
        value=op.getPollingRate();
        pushPollingRateAmount=value.substring(0,value.length()-1);
        pushPollingRateMeasureUnit=value.substring(value.length()-1);
        
        value=op.getRetryRate();
        if(value!=null && value.equals("")==false)
            {
            retryRateAmount=value.substring(0,value.length()-1);
            retryRateMeasureUnit=value.substring(value.length()-1);
            }


        value=op.getRetryAttempts();
         if(value!=null && value.equals("")==false && Integer.parseInt(value)>0)
            {
            retryAttempts=value;
            retry=true;
            }
        else
            {
                retry=false;
                retryAttempts="0";
            }
    }

    session.setAttribute("opType",op.getType());

    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
    String home = (String)messages.getObject("configureAsynchronousOperationRequest.home");
    String opManag = (String)messages.getObject("configureAsynchronousOperationRequest.opManag");
    String edit = (String)messages.getObject("configureAsynchronousOperationRequest.Edit");

    bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
        "&nbsp;<a href='manageOperations.jsp?serviceName="+serviceName+
        "'>"+opManag+"</a>&nbsp;&gt;" + edit;
%>

<SCRIPT language="JavaScript">
<!--
function back()
{

  window.location="<%= response.encodeURL("manageOperations.jsp?serviceName=" + serviceName)%>";
}
//-->
</SCRIPT>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
                <SCRIPT>addHelp("operationManagement");</SCRIPT>
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
                    <TBODY> 
                        <TR> 
                            <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="configureAsynchronousOperationRequest.Edit" bundle="${lang}"/></FONT></P> 
                                <P>  
                                
                                <form method="post" enctype="multipart/form-data"  action="<%= response.encodeURL("manager?cmd=configureOperation") %>">
                                    <input name="operationName" type="hidden" value="<%=operationName%>">
                                    <input type="hidden" name="serviceName" value="<%=serviceName%>">
                                                                     
                                    <!-- Page contents table-->
                                    <table width="60%" cellspacing="2" cellpadding="2" border="0" align="center">
                                        <tr>
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureAsynchronousOperationRequest.OpSpec" bundle="${lang}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="configureAsynchronousOperationRequest.OpName" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><%=operationName%></td>
                                        </tr>
                                        <tr>
                                            <td class="sortable" nowrap width="30%"><fmt:message key="configureAsynchronousOperationRequest.SOAPAct" bundle="${lang}"/></td>
                                            <c:choose>
                                                <c:when test="${isRepoOperation=='true'}">
                                                    <td class="sortable" nowrap><input name="soapAction" type="hidden"size="78"  value="<%=soapAction %>"><%=soapAction %></td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td class="sortable" nowrap><input name="soapAction" type="text"size="78"  value="<%=soapAction %>"></td>
                                                </c:otherwise>
                                            </c:choose>
                                       </tr>
                                        
                                    <%
                                      for (String type : scriptsNeededTypes)
                                      {
                                          if(op.scriptMustBeOverridden(type))
                                              {
                                                session.setAttribute("scriptType", "scriptTypes."+type);
                                    %>
                                    <tr>
                                        <td class="sortable" width="30%"><fmt:message key="${scriptType}" bundle="${lang}"/></td>
                                        <td  class="sortable" width="70%"><input name="<%= type %>" type="file" type="file" size="65"></input></td>
                                    </tr>
                                    <% }}%>

                                       
                                        <c:choose>
                                            <c:when test="${opType=='asynchronous'}">
                                                 <tr><!-- Row 8 -->
                                            <td width="50%" nowrap class="sortable"><fmt:message key="configureAsynchronousOperationRequest.Request" bundle="${lang}"/><font color="FF0000"></td>
                                            <td class="sortable" nowrap><input name="requestTimeoutAmount" type="text" size="2" maxlength="2" value="<%=requestTimeoutAmount%>">
                                                <select name="requestTimeoutMeasureUnit">
                                                    <option value="s" <%=requestTimeoutMeasureUnit.equals("s") ? "selected=\"true\"" : ""%>><fmt:message key="configureAsynchronousOperationRequest.seconds" bundle="${lang}"/></option>
                                                    <option value="m" <%=requestTimeoutMeasureUnit.equals("m") ? "selected=\"true\"" : ""%>><fmt:message key="configureAsynchronousOperationRequest.minutes" bundle="${lang}"/></option>
                                                    <option value="h" <%=requestTimeoutMeasureUnit.equals("h") ? "selected=\"true\"" : ""%>><fmt:message key="configureAsynchronousOperationRequest.hours" bundle="${lang}"/></option>
                                                    <option value="d" <%=requestTimeoutMeasureUnit.equals("d") ? "selected=\"true\"" : ""%>><fmt:message key="configureAsynchronousOperationRequest.days" bundle="${lang}"/></option>
                                                    <option value="w" <%=requestTimeoutMeasureUnit.equals("w") ? "selected=\"true\"" : ""%>><fmt:message key="configureAsynchronousOperationRequest.weeks" bundle="${lang}"/></option>
                                                </select>
                                            </td>
                                        </tr>
                                                <tr>
                                            <td width="50%" nowrap class="sortable"><fmt:message key="configureAsynchronousOperationRequest.Push" bundle="${lang}"/></td>
                                            <td class="sortable" nowrap><input name="admittedHosts" type="text" size="40" value="<%=admittedHosts%>"></td>
                                        </tr>
                                            <tr><!-- Row 9 -->
                                            <td width="50%" rowspan="1" nowrap class="sortable"><fmt:message key="configureAsynchronousOperationRequest.Polling" bundle="${lang}"/><font color="FF0000"></td>
                                            <td class="sortable" nowrap>
                                                <input name="pushPollingRateAmount" type="text" size="2" maxlength="2" value="<%=pushPollingRateAmount%>">
                                                <select name="pushPollingRateMeasureUnit">
                                                    <option value="s" <%=pushPollingRateMeasureUnit.equals("s") ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.seconds" bundle="${lang}"/></option>
                                                    <option value="m" <%=pushPollingRateMeasureUnit.equals("m") ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.minutes" bundle="${lang}"/></option>
                                                    <option value="h" <%=pushPollingRateMeasureUnit.equals("h") ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.hours" bundle="${lang}"/></option>
                                                    <option value="d" <%=pushPollingRateMeasureUnit.equals("d") ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.days" bundle="${lang}"/></option>
                                                    <option value="w" <%=pushPollingRateMeasureUnit.equals("w") ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.weeks" bundle="${lang}"/></option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr><!-- Row 10 -->
                                            <td width="50%" rowspan="1" nowrap class="sortable"><input type="checkbox" name="retry" <%=retry ? "checked" : ""%>>
                                            <fmt:message key="configureAsynchronousOperationRequest.Rentry" bundle="${lang}"/><font color="FF0000"></td>
                                            <td class="sortable" nowrap><TABLE cellspacing='2'><tr><td align='left' class="tdForm"><fmt:message key="configureAsynchronousOperationRequest.Attempts" bundle="${lang}"/>
                                                    <input type="text" size="3" name="retryAttempts" value="<%=retryAttempts%>">	</TD></TR><tr><td align='left' class="tdForm">Rate
                                                            <input name="retryRateAmount" type="text" size="2" maxlength="2" value="<%=retryRateAmount%>">
                                                            <select name="retryRateMeasureUnit">
                                                                <option value="s" <%=retryRateMeasureUnit.equals("s") && retry ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.seconds" bundle="${lang}"/></option>
                                                                <option value="m" <%=retryRateMeasureUnit.equals("m") && retry ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.minutes" bundle="${lang}"/></option>
                                                                <option value="h" <%=retryRateMeasureUnit.equals("h") && retry ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.hours" bundle="${lang}"/></option>
                                                                <option value="d" <%=retryRateMeasureUnit.equals("d") && retry ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.days" bundle="${lang}"/></option>
                                                                <option value="w" <%=retryRateMeasureUnit.equals("w") && retry ? "selected" : ""%>><fmt:message key="configureAsynchronousOperationRequest.weeks" bundle="${lang}"/></option>
                                                </select></TD></TR></TABLE>
                                            </td>
                                        </tr>
                                       
                                        </c:when>
                                        </c:choose>
                                        <tr>
                                            <td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="configureAsynchronousOperationRequest.Back" bundle="${lang}"/>" onClick="back()"><input type="submit" value="<fmt:message key="configureAsynchronousOperationRequest.Configure" bundle="${lang}"/>"></td>
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

