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
 -  File Name:         $RCSfile: createServiceRequest.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.17 $
 -  Revision Date:     $Date: 2004/12/21 13:23:47 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<SCRIPT language="JavaScript">             
    function gothere() 
    { 
        window.location="<%= response.encodeURL("createNewServiceSelectInterface.jsp?backbutton=true")%>";
    }
    function resetPortalSchema()
    {
            var label=document.getElementById("portalSelectionString");
           label.innerHTML="<b></b>";

           var component= document.getElementById("fullSchemaSet");
           component.value=" ";

           var schemaZipButton=document.getElementById("schemaZipButton");
           schemaZipButton.disable=false;

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
             else   openTab('xml','Import schema set','<%= response.encodeURL("setSchemaFromPortalStep1.jsp?download=version&serviceName="+(String) session.getAttribute("newServiceName")+"&importOrCreate=create")%>','Import');
    }

    function toggleJKSDivAreaDisabled(id)
    {
        /*
       var e = document.getElementById(id);
       if(e.style.display == 'block')
          e.style.display = 'none';
       else
          e.style.display = 'block';*/
    	//toggleDisabled(document.getElementById(id));

    	var el = document.getElementById("jksButton");
    	el.value = "";
    	el.disabled = el.disabled ? false : true;
    	el = document.getElementById("jksUserNameId");
    	el.value = "";
    	el.disabled = el.disabled ? false : true;
    	el = document.getElementById("jksPasswdId");
    	el.value = "";
    	el.disabled = el.disabled ? false : true;
    	el = document.getElementById("xacmlFileId");
    	el.value = "";
    	el.disabled = el.disabled ? false : true;
    	
    }

    function checkEmptyString(str) {
        var index;
        for (index=0; index < str.length; index++) {
        if (str.charAt(index) != ' ') {
        return false;
        }
        }
        return true;
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
    					document.createServiceForm.submit();
    				}
				}
	
			);
		return false;
        }*/
        return true;

    }

</SCRIPT>
<%@ include file="checkSession.jsp" %>

<%
            String interfaceNames;
            String mode;
            String serviceName;
            Hashtable<String,String> newServiceHashtable;
            Enumeration<String> keys;
            String key;

            newServiceHashtable=(Hashtable<String,String>)session.getAttribute("newServiceHashtable");

            interfaceNames = (String) request.getParameter("interfaceNames");
            if(interfaceNames!=null)
                newServiceHashtable.put("interfaceNames", interfaceNames);
            else interfaceNames=newServiceHashtable.get("interfaceNames");
            
            mode=(String) request.getParameter("serviceImplementationMode");
            if(mode!=null)
                newServiceHashtable.put("serviceImplementationMode", mode);

            String serviceAbstract,serviceDescription;
            serviceAbstract=(String) request.getParameter("serviceAbstract");
            if(serviceAbstract!=null)
                    newServiceHashtable.put("serviceAbstract", serviceAbstract);

            serviceDescription=(String) request.getParameter("serviceDescription");
            if(serviceDescription!=null)
                    newServiceHashtable.put("serviceDescription", serviceDescription);

            serviceName=newServiceHashtable.get("serviceName");
            
            PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
            String newServ = (String) messages.getObject("createServiceRequest.newServ");
            String newServ2 = (String) messages.getObject("createServiceRequest.newServ2");
            String queuingChecked = "";
            String suspendMode = "soft";
            boolean ssl = false;
            String sslCertificateLocation = "";
            
            boolean ws_security = false;
            String keystoreUser ="";

            String home = (String) messages.getObject("createServiceRequest.home");
            String serviceManag = (String) messages.getObject("createServiceRequest.serviceManag");
            String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;&nbsp;<a href='servicesManagement.jsp'>" + serviceManag + "</a>";
%>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
    <TD class=pageBody id=main> 
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceCreation.html_Service_creation*");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
            <TBODY> <TR> 
                    <TD id=main> 
                        <P class=arbloc><FONT class=arttl><fmt:message key="createNewService.begin" bundle="${lang}"/> 6 <fmt:message key="createNewService.central" bundle="${lang}"/> 6)</FONT></P>
                        <form  id="createServiceFormId" name="createServiceForm"  action="<%= response.encodeURL("manager?cmd=DeployFromInterface")%>" ENCTYPE="multipart/form-data" method="post">
                            <input type="hidden" name="fullSchemaSet" id="fullSchemaSet"  value=""></input>
                            <input type="hidden" name="requestComingFromServiceCreationWizard" id="requestComingFromServiceCreationWizard"  value="true"></input>
                            <%
                                keys=newServiceHashtable.keys();
                                while(keys.hasMoreElements())
                                {
                                    key=keys.nextElement();
                                    %><input type="hidden" name="<%= key%>" id="<%= key%>"  value="<%= newServiceHashtable.get(key)%>"></input>
                                    <%
                                }
                            %>

                            <!-- Page contents table-->
                            <table width="90%" cellspacing="2" cellpadding="2" align="center">
                                <%
                                    if(interfaceNames.equals("UserDefined"))
                                        {%>
                                        <tr>
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="createServiceRequest.ServSchema" bundle="${lang}"/></td>
                                        </tr>
                                        <tr>
                                        <tr>
                                            <td class="sortable" width="20%" colspan=1 nowrap>Schema package</td>
                                            <td class="sortable" width="80%" colspan=1 nowrap>
                                                <input type="file" name="schemaZip" id="schemaZipButton"  size="100%"/>
                                            </td> 
                                        </tr>     
                                        <tr>
                                            <td class="sortable"/>
                                            <td class="sortable"><input type="button" id="portalSchemaButton" onclick="goDownloadPage()" value="Download schema from SSE portal"></input><div  id="portalSelectionString"/></td>
                                        </tr>
                                        <tr>
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="createServiceRequest.ServiceInfo" bundle="${lang}"/></td>
                                        </tr>
                                        <%
                                        } else{
                                        %>
                                        <tr>
                                            <td class="sortableHeader" colspan="2" nowrap><fmt:message key="createServiceRequest.ServiceInfo" bundle="${lang}"/></td>
                                        </tr>
                                    <% } %>
                                
                                <tr> 
                                    <!-- Row 5 --> 
                                    <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="createServiceRequest.Queue" bundle="${lang}"/></td> 
                                    <td class="sortable" nowrap><input name="queuing" type="checkbox" <%= queuingChecked%>></td> 
                                </tr> 
                                <tr> 
                                    <!-- Row 6 --> 
                                    <td class="sortable" width="50%" colspan=1 nowrap><fmt:message key="createServiceRequest.Suspend" bundle="${lang}"/></td> 
                                    <td class="sortable" nowrap> <input type="radio" name="suspendMode" value="soft" <%= suspendMode.equals("soft") ? "checked" : ""%>> 
                                                                        <fmt:message key="createServiceRequest.SOFT" bundle="${lang}"/> <br> 
                                    <input type="radio" name="suspendMode" value="hard" <%= suspendMode.equals("hard") ? "checked" : ""%>> 
                                           <fmt:message key="createServiceRequest.HARD" bundle="${lang}"/> </td> 
                                </tr> 
                                <tr> 
                                    <!-- Row 7--> 
                                    <td width="50%" rowspan="1" nowrap class="sortable"><input type="checkbox" name="SSLFlag" <%= ssl ? "checked" : ""%>> 
                                                                                               <fmt:message key="createServiceRequest.secure" bundle="${lang}"/><font color="FF0000"></td> 
                                    <td class="sortable" nowrap> <fmt:message key="createServiceRequest.SSL" bundle="${lang}"/>
                                    <input name="sslCertificateLocation" type="text" size="40" value="<%= ssl ? sslCertificateLocation : ""%>"> </td> 
                                </tr>
                                
                                <tr> 
                                    <!-- Row 8 WS-Security--> 
                                    <td width="50%" rowspan="1" nowrap class="sortable">
                                    <input type="checkbox" name="WSSecurityFlag" <%= ws_security ? "checked" : ""%> onclick="toggleJKSDivAreaDisabled('JKSDivArea')" > 
                                                                                               <fmt:message key="createServiceRequest.wssecurity" bundle="${lang}"/><font color="FF0000"></td> 
                                    <td class="sortable" nowrap>  
	                                    
	                                    <TABLE id="JKSDivArea" cellSpacing=0 cellPadding=0 width="90%" align=left style='display:block'>
		                                	<TBODY>
			                                	<tr>
			                                		<td class="sortable" nowrap> <fmt:message key="createServiceRequest.keystore" bundle="${lang}"/>	
		                                    		</td>
		                                    		<td class="sortable" width="80%" colspan=1 nowrap >
                                                		<input type="file" name="jksFile" id="jksButton"  size="100%" disabled=true />
                                            		</td> 
                             
		                                    	</tr>
			                                	
			                                    <TR>
			                                        <TD class="sortable" noWrap align=left width="40%"><fmt:message key="createServiceRequest.alias" bundle="${lang}"/>
			                                        
			                                        </TD>
			                                        <TD align=left width="20%"> <input type="text" name="jksUserName" maxLength=40 size=30 id="jksUserNameId" value=" " disabled=true> 
			                                        </TD>
			                                    
			                                    </TR>
			                                    <TR>
			                                        <TD class="sortable" noWrap align=left ><fmt:message key="loginRequest.password" bundle="${lang}"/></TD>
			                                        <TD align=left width="20%"><INPUT type=password maxLength=40 size=30 name=jksPasswd id ="jksPasswdId" disabled=true> </TD>
			                                    <TD>&nbsp;
			                                    </TD>
			                                    </TR>
			                                    
			                                    <tr>
			                                		<td class="sortable" nowrap> <fmt:message key="createServiceRequest.xacml" bundle="${lang}"/>	
		                                    		</td>
		                                    		<td class="sortable" width="80%" colspan=1 nowrap>
                                                		<input type="file" name="xacmlFile" id="xacmlFileId"  size="100%"  disabled=true/>
                                            		</td> 
		                                    	</tr>
		                                   
		                        			</TBODY>
		                        		</TABLE>
                                    </td>
                                     
                                </tr>
                                
                                
                                <tr>
                                    <!-- Row 9 --> 
                                    <td class="sortable" colspan="3" rowspan="2" nowrap align="right"><div align="right">
                                            <input type="button" value="<< <fmt:message key="createServiceRequest.Back" bundle="${lang}"/>" onclick="gothere()">
                                            <input onClick="return checkWSFields(createServiceForm)" type="submit" value="<fmt:message key="createServiceRequest.Create" bundle="${lang}"/>">
                                    </div></td> 

                                    <%
                                        String value;

                                        keys=newServiceHashtable.keys();
                                        while(keys.hasMoreElements())
                                        {
                                            key=keys.nextElement();
                                            value=newServiceHashtable.get(key);
                                    %>
                                    <input type="hidden" name="<%= key %>" value="<%= value %>"/>
                                    <% } %>
                                </tr> 
                            </table> 
                        </form> 
                    </TD> 
                </TR> 
            </TBODY> 
        </TABLE> 
    </TD> 
    </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

