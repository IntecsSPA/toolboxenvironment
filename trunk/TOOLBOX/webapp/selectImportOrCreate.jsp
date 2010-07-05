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
 -  File Name:         $RCSfile: selectImportOrCreate.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.8 $
 -  Revision Date:     $Date: 2004/09/07 13:24:17 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp"%>
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
        String home = (String) messages.getObject("createServiceRequest.home");
        String serviceManag = (String) messages.getObject("createServiceRequest.serviceManag");
        String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;&nbsp;<a href='servicesManagement.jsp'>" + serviceManag + "</a>";

        String serviceName = "";
        String warnMsg = "";
        String warn = "";
        String createChecked = "checked";
        String importChecked = "";
        Hashtable<String,String> newServiceHashtable;

        if (request.getParameter("backbutton") == null || request.getParameter("backbutton").equals("false")) {
            newServiceHashtable=new Hashtable<String,String>();
            session.setAttribute("newServiceHashtable", newServiceHashtable);
            newServiceHashtable.put("importOrCreate","");
            newServiceHashtable.put("serviceName","");
            newServiceHashtable.put("serviceAbstract","");
            newServiceHashtable.put("serviceDescription","");
            newServiceHashtable.put("serviceType","UserDefined");

        } else {
            newServiceHashtable=(Hashtable<String,String>)session.getAttribute("newServiceHashtable");
            serviceName = newServiceHashtable.get("serviceName");
            importChecked=newServiceHashtable.get("importOrCreate").equals("create")?"":"checked";
            createChecked=newServiceHashtable.get("importOrCreate").equals("create")?"checked":"";
        }
    
%>
<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/import/ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/widgets/style/css/webgis.css" />
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/jquery/jquery-1.2.6.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/jquery/ext-jquery-adapter.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ext-all.js"></script>
<script src="jsScripts/import/gis-client-library/import/OpenLayers/lib/OpenLayers.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/openlayers/Format/XMLKeyValue.js"></script>

<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/manager.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/ext/ux/Spotlight.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/XmlDoc.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/browserDetect.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/ExtFormUtils.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>


<script type="text/javascript" src="jsScripts/wpsWizard/scripts/wpsWizardManager.js"></script>
<SCRIPT>
    function controlNewService(serviceName){
       var newServiceName;
     if(!serviceName){
       if(document.newService.importOrCreate[1].checked)
          newServiceName=document.newService.newServiceName.value;
     }else
         newServiceName=serviceName;
          if(newServiceName.length<=0){

               Ext.Msg.show({
                    title:'Create a new service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please insert a new Service Name',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
               return false;
          }else{
             try{  
             var controlServiceName=function(response){
                 if(!response){
                          Ext.Msg.show({
                                title:'Create a new service: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });  
                      }else{
                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                        if(isValid !='true'){
                            Ext.Msg.show({
                                title:'Create a new service: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service already defined. Please choose another name',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                            });  
                            globalControl=false;
                        }
                      }   
             }; 
             var controlServiceTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };
             
             var globalControl=true;
             var onSubmit=sendXmlHttpRequestTimeOut("GET", 
                         "manager?cmd=isServiceNameAvailable&serviceName="+newServiceName, 
                         false, null, 8000, controlServiceName, controlServiceTimeOut,null);          
            return globalControl && onSubmit;             
              }catch(e){
                    return false;
              }
         }
       
       return true; 
    }
</SCRIPT>
<TABLE cellSpacing=0 cellPadding=0 width="100%" height="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("serviceCreation");</SCRIPT>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="selectImportOrCreate.create" bundle="${lang}"/></FONT></P> 
                <P>  
			<form NAME="newService" onsubmit="return controlNewService()" method="post" action="<%= response.encodeURL("createNewServiceSelectType.jsp")%>">
				<!-- Page contents table-->
					<table width="80%" cellspacing="2" cellpadding="2" align="center">
                        <tr><!-- Row 1 -->
							<td class="sortable"  colspan=2 nowrap><input type="radio" name="importOrCreate" value="import" <%=importChecked%> ><fmt:message key="selectImportOrCreate.Import" bundle="${lang}"/><font color="FF0000"></font></td>
						</tr>						
						<tr><!-- Row 3 -->
							<td class="sortable"  colspan=1 nowrap><input type="radio" name="importOrCreate" value="create"  <%=createChecked%> ><fmt:message key="selectImportOrCreate.createNew" bundle="${lang}"/><font color="FF0000"><%=warnMsg%></FONT></td>
						<td align="left" class="sortable"><STRONG><fmt:message key="selectImportOrCreate.serviceName" bundle="${lang}"/> <input type="text" NAME="newServiceName" size='30' value="<%=serviceName%>"></STRONG></td>
						</tr>						
						<tr><!-- Row 5 -->
							<td colspan="2" rowspan="2" nowrap align="right" class="sortable">
                                <div align="right">
                                    <input type="submit" value="<fmt:message key="selectImportOrCreate.Next" bundle="${lang}"/> >>">
                                </div>
                            </td>
						</tr>
                        <tr>
                            <td class="sortable"  colspan=1 nowrap> Create a Web Processing Service (WPS) Wizard  </td>
                            <td class="sortable"  colspan=1 nowrap> <input type="button" value="Start Wizard" name="WPSWizardButton" onclick="javascript:wpsWizardManager()" /> </td>
                        </tr>
					</table>
			</form>
                </P> 
&nbsp; </TD> 
            </TR>
            

          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 		
<jsp:include page="footer.jsp"/>
