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
 -  File Name:         $RCSfile: viewServiceConfiguration.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.10 $
 -  Revision Date:     $Date: 2004/09/15 16:40:30 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.db.*,it.intecs.pisa.toolbox.service.status.*,java.io.*,it.intecs.pisa.common.tbx.*,it.intecs.pisa.util.*,it.intecs.pisa.toolbox.service.*,it.intecs.pisa.soap.toolbox.*, java.util.*, it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator"  errorPage="errorPage.jsp" %>
<%@ include file="checkSession.jsp" %>
<jsp:include page="header.jsp" />
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
        String service = (request.getParameter("serviceName") == null ? "": request.getParameter("serviceName"));
		if (service.equals("")) {
%>
	<jsp:forward page="main.jsp"/>
<%
		}
        TBXService tbxservice=ServiceManager.getInstance().getService(service);
        String abstractStr,descriptionStr;
        String versionToPrint="";

        abstractStr=IOUtil.inputToString(tbxservice.getServiceAbstract());
        descriptionStr=IOUtil.inputToString(tbxservice.getServiceDescription());

	byte status = ServiceStatuses.getStatus(service);
	String color = "4EBC58";
	if (status==ServiceStatuses.STATUS_STOPPED) {color = "FF0B0B";}
	else if (status==ServiceStatuses.STATUS_RUNNING) {color = "0BFF0B";}
	else if (status==ServiceStatuses.STATUS_SUSPENDED) {color = "CA7840";}
                
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("viewServiceConfiguration.home");
        String servConf = (String)messages.getObject("viewServiceConfiguration.servConf");
        String currentConf = (String)messages.getObject("viewServiceConfiguration.currentConf");


	String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;<a href='serviceConfiguration.jsp?serviceName="+service+"'>"+servConf+"</a>&nbsp;&gt;" +currentConf;
    
    String XACMLUrl = ToolboxSecurityConfigurator.getXACMLpolicyURL(service);        
    
	%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewServiceConfiguration.currentConf" bundle="${lang}"/></FONT></P> 
                <P>  
				<!-- Page contents table-->
					<table width="90%" cellspacing="2" cellpadding="2" align="center">
                                                 <tr><!-- Row 4 -->
							<td class="sortableHeader" colspan="2"><fmt:message key="viewServiceConfiguration.servInfo" bundle="${lang}"/></td>
						</tr>
                                                <tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.servName" bundle="${lang}"/></td>
                                                        <td class="sortable" nowrap> <%= service %> (<fmt:message key="viewServiceConfiguration.status" bundle="${lang}"/>: <font color="<%= color %>"><%= ServiceStatuses.toString(status) %></font>)</td>
						</tr>
                                                <tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1><fmt:message key="viewServiceConfiguration.servAbstract" bundle="${lang}"/></td>
                                                        <td class="sortable" ><%=( abstractStr != null && abstractStr.length() > 0) ? abstractStr : "--"%></td>
						</tr>
                                                <tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 ><fmt:message key="viewServiceConfiguration.servDescr" bundle="${lang}"/></td>
                                                        <td class="sortable" ><textarea name="serviceDescription" cols="70" rows="6" disabled><%=( descriptionStr != null && descriptionStr.length() > 0) ? descriptionStr : ""%></textarea></td>
						</tr>
						
<%
    String[] serviceSchemas;
    String serviceSchemaName;
    File serviceSchemaNameFile;
    Interface servInterface;
    
    serviceSchemas = tbxservice.getScheamsPaths();
    serviceSchemaName = tbxservice.getImplementedInterface().getSchemaRoot();
   // serviceSchemaNameFile=new File(serviceSchemaName);

    servInterface=tbxservice.getImplementedInterface();
    
    if(servInterface.getName().equals(""))
            versionToPrint = "";
    else  versionToPrint = servInterface.getName()+ " version "+servInterface.getVersion();

    String validationActive;

    validationActive=tbxservice.getImplementedInterface().isValidationActive()?"checked":"";
    
    if(serviceSchemas.length>0)
                                                {
                                        %>
                                        <tr><!-- Row 1 -->
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.schemaSpec" bundle="${lang}"/></td>
                                        </tr>

                                       <tr>
                                            <td class="sortable"><fmt:message key="configureServiceRequest.enableValidation" bundle="${lang}"/></td>
                                            <td class="sortable"><input type="checkbox" id="validationActive" disabled <%=validationActive %>></input></td>
                                       </tr>

                                            <%

                                          }

    for(String ss:serviceSchemas)
       {
           boolean isRootSchema=false;

           isRootSchema=serviceSchemaName!=null && serviceSchemaName.equals(ss);
       
           if(isRootSchema)
               {
%>
						<tr>
                            
							<td class="sortable" nowrap ><fmt:message key="viewConfiguration.rootSchema" bundle="${lang}"/></td>
                            <td class="sortable" nowrap width="50%"><%=service+"/Schemas/"+ss%></td>
<%
 }
           else
               {
       
%>
                        <tr>
                           
							<td class="sortable" nowrap ><fmt:message key="viewConfiguration.schema" bundle="${lang}"/></td>
                             <td class="sortable" nowrap width="50%"><%=service+"/Schemas/"+ss%></td>
<%
}
           }
%>
						<tr><!-- Row Version -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.InterfaceType" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%= versionToPrint %>
						</tr>
<tr><!-- Row 4 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="viewServiceConfiguration.infoWSDL" bundle="${lang}"/></td>
						
							
						</tr>
                                                <tr><!-- Row 2 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.namespace" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%=tbxservice.getImplementedInterface().getTargetNameSpace()!=null & tbxservice.getImplementedInterface().getTargetNameSpace().length() > 0 ? tbxservice.getImplementedInterface().getTargetNameSpace() : "--"%></td>
						</tr>
<%
/*}*/
%>
                                                <tr><!-- Row 5 -->
							<td class="sortableHeader" colspan="2" nowrap><fmt:message key="viewServiceConfiguration.reqManag" bundle="${lang}"/></td>
						</tr>
                                                
						<tr><!-- Row 6 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.queue" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%= /*(toolboxConfigurator != null && toolboxConfigurator.getQueuing()) ? "YES (global queuing)" :*/ (tbxservice.isQueuing()? "YES" : "NO") %></td>
						</tr>
						<tr><!-- Row 8 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.suspMode" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%= (tbxservice.getSuspendMode() != null && tbxservice.getSuspendMode().equals("SOFT")) ? "SOFT" : "HARD" %></td>
						</tr>
						<tr><!-- Row 14 -->
							<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="viewServiceConfiguration.SSLCert" bundle="${lang}"/></td>
							<td class="sortable" nowrap><%= (tbxservice.getSSLcertificate() != null && tbxservice.getSSLcertificate().length() != 0) ? tbxservice.getSSLcertificate() : "--" %>
						</td>
						</tr>
					
							<tr><!-- Row WS-Security -->
								<td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="createServiceRequest.wssecurity" bundle="${lang}"/></td>
								<td class="sortable" nowrap><%= (tbxservice.hasWSSecurity()) ? "YES" : "NO" %>                                    
	                             </TD>
							</tr>
							<tr><!-- Row XACML -->
								<td class="sortable" width="50%" colspan=1 nowrap>XACML</td>
								<td class="sortable" nowrap> 
								<% 
	                                  if (tbxservice.hasWSSecurity()){
		                                  if(XACMLUrl.length() != 0)  {
		                                  %>
		                                  YES (<A class=smpgttl href="#" onclick="javascript:downloadPopup('manager?cmd=proxyRedirect&method=GET&url=<%=XACMLUrl%>');"><fmt:message key="viewServiceInfo.see" bundle="${lang}"/> </A>)   
		                                  <%    
		                                  }           
		                                  else{
		                                  %>
		                                  NO       
		                                  <%    
		                                  }
	                                  }else{
	                                  %> 
	                                  	NO
	                                  <%} %></td>
	                        </tr> 
	                          
	                                      
                       

                       <%
    Hashtable<String,Hashtable<String,String>> variables;
    variables=servInterface.getUserVariable();

    if(variables.isEmpty()==false)
        {
        %>
             <tr>
                 <td class="sortableHeader" colspan="2" nowrap><fmt:message key="configureServiceRequest.variablesHader" bundle="${lang}"/></td>
             </tr>
        <%
        }

    Enumeration<String> keys=variables.keys();
    String key,type,displayText,value;
    Hashtable<String,String> varValue;
    while(keys.hasMoreElements())
        {
            key=keys.nextElement();
            varValue=variables.get(key);

            type=varValue.get(Interface.VAR_TABLE_TYPE);
            displayText=varValue.get(Interface.VAR_TABLE_DISPLAY_TEXT);
            value=varValue.get(Interface.VAR_TABLE_VALUE);

            if(type.equals("string") || type.equals("integer"))
                {
%>
                <tr>
                    <td class="sortable" width="50%" colspan=1 nowrap><%=displayText%></td>
                    <td class="sortable" nowrap><input disabled type="text" name="serviceVariable<%=key%>" size="80" value="<%=value%>"></input></td>
                </tr>

                                        <%
                }
                else if(type.equals("boolean"))
                    {
%>
                <tr>
                    <td class="sortable" width="50%" colspan=1 nowrap><%=displayText%></td>
                    <td class="sortable" nowrap><input  disabled type=CHECKBOX  name="serviceVariable<%=key%>" <%=value.equals("true")?"checked":""%>/></td>
                </tr>

                                        <%
                }


        }
                                        %>

					</table>
					&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>