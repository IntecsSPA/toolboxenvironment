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
 -  File Name:         $RCSfile: chooseCommunicationMode.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.12 $
 -  Revision Date:     $Date: 2004/09/07 13:24:16 $
 -
 -->
<%@ page import="it.intecs.pisa.toolbox.service.*,it.intecs.pisa.toolbox.service.util.*,java.io.*,it.intecs.pisa.toolbox.*, java.util.*,it.intecs.pisa.pluginscore.*,it.intecs.pisa.common.tbx.*" errorPage="errorPage.jsp"%>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
            String serviceName;
            TBXService service;
            ServiceManager servMan;
            String interfacename;
            String interfaceVersion;
            String interfaceType;
            String interfaceMode;
            Interface interf;
            Interface implementedInterface;
            InterfacePluginManager interfmanager;
            Vector<String> creatableOperations;
            Hashtable<String, Properties> props;
            String mode;

            creatableOperations=new Vector<String>();
             
            serviceName = request.getParameter("serviceName");
            PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));

            String home = (String) messages.getObject("chooseCommunicationMode.home");
            String opManag = (String) messages.getObject("chooseCommunicationMode.opManag");
            String newOp = (String) messages.getObject("chooseCommunicationMode.newOp");

            String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;" +
                    "&nbsp;<a href='manageOperations.jsp?extVers=3&serviceName=" + serviceName + "'>" + opManag + "</a>&nbsp;&gt;" +
                    newOp;

            servMan = ServiceManager.getInstance();
            service = servMan.getService(serviceName);
            
            implementedInterface=service.getImplementedInterface();
            interfacename = implementedInterface.getName();
            interfaceVersion = implementedInterface.getVersion();
            interfaceType=implementedInterface.getType();
            interfaceMode=implementedInterface.getMode();
            
            interfmanager=InterfacePluginManager.getInstance();
            interf=interfmanager.getInterfaceDescription(interfacename, interfaceVersion,interfaceType,interfaceMode);



            if(interf!=null)
             {
                 Operation[] ops;
                 HashSet implementedOps;

                 implementedOps=new HashSet();
                 for(Operation o:service.getImplementedInterface().getOperations())
                    {
                        implementedOps.add(o.getName());
                    }

                 ops=interf.getOperations();

              for(it.intecs.pisa.common.tbx.Operation op: ops)
                    {
                        if(implementedOps.contains(op.getName())==false)
                            creatableOperations.add(op.getName());
                    }
              }

            session.setAttribute("hasRepoInterface", interf!=null);
%>

<SCRIPT language="JavaScript">
    <!--

    function gothere() 
    { 
        window.location="<%= response.encodeURL("manageOperations.jsp?extVers=3&" + request.getQueryString())%>";
    } 
    //-->
</SCRIPT>

<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
                <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/operationManagement.html_Operations_management*RE/createOperation.html_Add_an_operation*");</SCRIPT>
                <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
                    <TBODY> 
                        <TR> 
                            <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="chooseCommunicationMode.newOp" bundle="${lang}"/></FONT></P> 
                                <P>  
                                <form action="<%= response.encodeURL("createOperation.jsp")%>" >
                                    <input type="hidden" name="serviceName" value="<%= request.getParameter("serviceName")%>"/>
                                    <input name="hasRepoInterface" type="hidden" value="<%= interf!=null %>"/>
                                    <!-- Page contents table-->
                                    <table width="20%" cellspacing="2" cellpadding="2" align="center">
                                    <tr><!-- Row 1 -->
                                        <td class="sortableHeader"  colspan=2 nowrap><fmt:message key="chooseCommunicationMode.chooseOp" bundle="${lang}"/><font color="FF0000"></font></td>
                                    </tr>
                                    <tr><!-- Row 2 -->
                                    <td class="sortable" align="center" colspan=2 nowrap>
                                    <TABLE width='90%'>
                                        
                                        <c:choose>
                                            <c:when test="${hasRepoInterface==true}">
                                                    <tr><!-- Row 2 -->
                                                         <td  align="right" colspan=2 nowrap>
                                                             <fmt:message key="selectOperationToAdd.label" bundle="${lang}"/>
                                                             <select name="operationName" >
                                                                    <% 
                                                                           
                                                                            for(Object op: creatableOperations.toArray())
                                                                            {%>
                                                                            <option><%= op.toString() %></option>
                                                                           <% }                                                            
                                                                    %>
                                                                 </select>
                                            </td>
                                        </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <tr><!-- Row 2 -->
                                                    <td class="sortable" align="center" colspan=2 nowrap>
                                                        <TABLE width='90%'>
                                                            <tr>
                                                                <td align="left" class="sortable"><input checked type="radio" name="communicationType" value="synchronous" ><fmt:message key="chooseCommunicationMode.syncro" bundle="${lang}"/></TD>
                                                            </tr>
                                                            <tr>
                                                                <td align="left" class="sortable"><input type="radio" name="communicationType" value="asynchronous" ><fmt:message key="chooseCommunicationMode.asyncro" bundle="${lang}"/></TD>
                                                            </tr>
                                                        </TABLE>
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        
                                        
                                        <tr><!-- Row 4 -->
                                            <td colspan="2" rowspan="2" nowrap align="right"><input type="button" value="<< <fmt:message key="chooseCommunicationMode.back" bundle="${lang}"/>" onClick="gothere()"><input type="submit" value="<fmt:message key="chooseCommunicationMode.next" bundle="${lang}"/> >>"></td>
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

