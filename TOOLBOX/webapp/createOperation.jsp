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
-  File Name:         $RCSfile: createOperation.jsp,v $
-  TOOLBOX Version:   $Name:  $
-  File Revision:     $Revision: 1.5 $
-  Revision Date:     $Date: 2004/09/07 13:24:17 $
-
-->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.*,it.intecs.pisa.toolbox.service.util.*,it.intecs.pisa.toolbox.*,java.util.*,it.intecs.pisa.pluginscore.*,it.intecs.pisa.common.tbx.*" errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>

<%
        String serviceName;
        String operationName;
        TBXService service;
        String interfacename;
        String interfaceVersion;
        String interfaceType;
        String interfaceMode;
        Interface interf;
        Interface implementedInterface;
        InterfacePluginManager interfmanager;
        String bc = "";
        String hasRepoInterface;
        String opType;
        String[] scriptsNeededTypes=new String[0];
        it.intecs.pisa.common.tbx.Operation op=null;
        HashSet implementedOps;
        String soapAction;

        serviceName = request.getParameter("serviceName");

        service = ServiceManager.getInstance().getService(serviceName);

        implementedInterface=service.getImplementedInterface();
        interfacename = implementedInterface.getName();
        interfaceVersion = implementedInterface.getVersion();
        interfaceType=implementedInterface.getType();
        interfaceMode=implementedInterface.getMode();
        hasRepoInterface = interfacename.equals("") == false && interfaceVersion.equals("") == false ? "true" : "false";

        interfmanager = InterfacePluginManager.getInstance();
        if (hasRepoInterface.equals("true")) {
            
            interf = interfmanager.getInterfaceDescription(interfacename, interfaceVersion,interfaceType,interfaceMode);

            operationName = request.getParameter("operationName");
            op = interf.getOperationByName(operationName);

            opType = op.getType();
            soapAction=op.getSoapAction();

            session.setAttribute("opType", opType);
            session.setAttribute("isRepoOperation", "true");

            scriptsNeededTypes = op.getNeededScriptsType();
        } else {
            operationName = "";
            soapAction="";
            session.setAttribute("isRepoOperation", "false");

            opType = request.getParameter("communicationType");
            session.setAttribute("opType", opType);

            interf=interfmanager.getOutOfRepoInterfaceDescription();
        
            op=interf.getOperationByName(opType);

            scriptsNeededTypes = op.getNeededScriptsType();
        }
%>

<jsp:include page="header.jsp" />
<script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>

<SCRIPT>
    function controlNewOperation(){
          var newOperationName=document.newOperation.operationName.value;
          var newOperationSoapAction=document.newOperation.soapaction.value;
          var serviceName=document.newOperation.serviceName.value;
          var globalControl=true;
          if(newOperationName.length<=0){
               Ext.Msg.show({
                    title:'Create a new operation: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please insert a new Operation Name',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
               return false;
          }else{
            if(newOperationSoapAction.length<=0){
               Ext.Msg.show({
                    title:'Create a new operation: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please insert a Soap Action',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
               return false;
            }else{
             try{
               var controlOperationName=function(response){
                 if(!response){
                          Ext.Msg.show({
                                title:'Create a new operation: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                          globalControl=false;
                      }else{
                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                            if(isValid !='true'){
                                Ext.Msg.show({
                                    title:'Create a new operation: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Operation already defined. Please choose another name',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
                                globalControl=false;
                            }else{

                                var controlSoapAction=function(response){
                                    if(!response){
                                          Ext.Msg.show({
                                                title:'Soap Action Control: Error',
                                                buttons: Ext.Msg.OK,
                                                msg: 'Service Exception!',
                                                animEl: 'elId',
                                                icon: Ext.MessageBox.ERROR
                                          });
                                       globalControl=false;
                                    }else{
                                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                                        if(isValid !='true'){
                                            Ext.Msg.show({
                                                title:'Create a new operation: Error',
                                                buttons: Ext.Msg.OK,
                                                msg: 'Soap Action already defined. Please choose another name',
                                                animEl: 'elId',
                                                icon: Ext.MessageBox.ERROR
                                            });
                                            globalControl=false;
                                        }else{
                                            var inputs=document.getElementsByTagName("input");
                                            var fileName=null;
                                            for(var i=0; i<inputs.length;i++){
                                                if(inputs[i].type == "file"){
                                                    fileName=inputs[i].value;
                                                    if(fileName.length <= 0){
                                                       globalControl=false;
                                                       Ext.Msg.show({
                                                            title:'Create a new operation: Error',
                                                            buttons: Ext.Msg.OK,
                                                            msg: "Please select a "+inputs[i].name+" file",
                                                            animEl: 'elId',
                                                            icon: Ext.MessageBox.ERROR
                                                      });
                                                      i=inputs.length;
                                                     }
                                                }
                                            }
                                        }
                                 }
                              };
                             var controlSoapActionTimeOut=function(){
                                 Ext.Msg.show({
                                    title:'Soap Action Control: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Request TIME-OUT!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
                             };

                            var result=sendXmlHttpRequestTimeOut("GET",
                                "manager?cmd=isSoapActionAvailable&serviceName="+serviceName+"&soapAction="+newOperationSoapAction,
                                false, null, 8000, controlSoapAction, controlSoapActionTimeOut,null);
                            }
                            globalControl= globalControl && result;
                          //  alert("PRIMA: " + globalControl);
                      }
             };
             var controlOperationTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new operation: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };


             var onsubmit=sendXmlHttpRequestTimeOut("GET",
                         "manager?cmd=isOperationNameAvailable&serviceName="+serviceName+"&operationName="+newOperationName,
                         false, null, 8000, controlOperationName, controlOperationTimeOut,null);
            /*/  alert("arrivato");
              alert(globalControl && onsubmit);*/
              return globalControl && onsubmit;
              }catch(e){
                    return false;
              }
            }
         }

       return true;
    }
</SCRIPT>
<table cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <tbody>
        <tr>
            <td class=pageBody id=main>
                <script>addBreadCrumb("<%=bc%>");</script>
                <script>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/operationManagement.html_Operations_management*");</script>

                <form NAME="newOperation" onsubmit="return controlNewOperation()" action="<%= response.encodeURL("manager?cmd=CreateOperationFromInterface")%>" method="post" ENCTYPE="multipart/form-data">
                    <input type="hidden" name="serviceName" value="<%= serviceName%>">
                    <input type="hidden" name="opType" value="<%= opType%>">
                    <table cellSpacing=0 cellPadding=0 width="600px" align=center valign="top">
                        <tbody>
                            <tr>
                                <td class="sortableHeader" colspan="2" ><fmt:message key="chooseCommunicationMode.chooseOp" bundle="${lang}"/><font color="FF0000"></font></td>
                            </tr>

                                    <tr>
                                        <td class="sortable" width="30%" >Operation Name:</td>
                                        <c:choose>
                                            <c:when test="${isRepoOperation=='true'}">
                                                <td class="sortable" width="70%" ><%= operationName%><input name="operationName" type="hidden" size="78" value="<%= operationName%>"></input></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td class="sortable" width="70%" ><input name="operationName" type="text" size="78" value="<%= operationName%>"></input></td>
                                            </c:otherwise>
                                        </c:choose>
                                    </tr>
                                    <tr>
                                        <td class="sortable" width="30%" >SOAP Action:</td>
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
                               
                           
                            <tr>
                                <td colspan="2" rowspan="2" nowrap align="right" class="sortable"><div align="right">
                                        <input type="submit" value="<fmt:message key="createSynchronousOperationRequest.Create" bundle="${lang}"/>">
                                </div></td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </form>
            </td>
        </tr>
    </tbody>
</table>
<jsp:include page="footer.jsp"/>
