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
 -  File Name:         $RCSfile: viewServiceInstances.jsp,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.19 $
 -  Revision Date:     $Date: 2007/01/16 12:53:27 $
 -
 -->
<%@ page language="java" import="it.intecs.pisa.toolbox.service.instances.*,it.intecs.pisa.toolbox.service.*,java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>

<%@ include file="checkSession.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<jsp:include page="header.jsp" />

<%      Document instancesDoc=null;

        String service = request.getParameter("serviceName");
        String orderBy = request.getParameter("orderBy");


        String order = request.getParameter("order");
        String pageNum = request.getParameter("page") != null ? request.getParameter("page") : "1";

        String instanceType = request.getParameter("instanceType") != null ? request.getParameter("instanceType") : "S";
        if (orderBy  == null)  {orderBy = "noOrder";}
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("viewServiceInstances.home");
        String monitoring = (String)messages.getObject("viewServiceInstances.monitoring");
        String servInstance = (String)messages.getObject("viewServiceInstances.servInstance");
        String typeInstance = null;
        if( instanceType.equals("S")){
            typeInstance = (String)messages.getObject("viewServiceInstances.typeInstanceS");
        } else {
            typeInstance = (String)messages.getObject("viewServiceInstances.typeInstanceA");
        }
        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + "<a href='monitoringCenter.jsp?serviceName="+service+"'>&nbsp;"+monitoring+"</a>&nbsp;&gt;&nbsp; "+servInstance+"</a>&nbsp; - &nbsp; "+typeInstance;
        int instanceNum = 0;

       ServiceManager servMan;
       TBXService serv;

       servMan=ServiceManager.getInstance();
       serv=servMan.getService(service);
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("monitoringCenter");</SCRIPT>         
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
<%
        if( instanceType.equals("S")){
                instanceNum = InstanceLister.getSynchronousInstancesNum(service);
                instancesDoc=InstanceLister.getSynchronousInstancesAsDocument(service,Integer.parseInt(pageNum),20);
%>              
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewServiceInstances.servInstance" bundle="${lang}"/>&nbsp; - &nbsp;<fmt:message key="viewServiceInstances.typeInstanceS" bundle="${lang}"/></FONT></P> 
<%
        } else {
                instanceNum = InstanceLister.getAsynchronousInstancesNum(service);
                instancesDoc=InstanceLister.getAsynchronousInstancesAsDocument(service,Integer.parseInt(pageNum),20);
%>              
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="viewServiceInstances.servInstance" bundle="${lang}"/>&nbsp; - &nbsp;<fmt:message key="viewServiceInstances.typeInstanceA" bundle="${lang}"/></FONT></P> 

<%
        } 
%>        
                <!-- Page contents table--> 
                <P>  

<%
         

          int pagesCount;
          int instancesCount;

          pagesCount=Integer.parseInt(instancesDoc.getDocumentElement().getAttribute("pagesNumber"));
          instancesCount=Integer.parseInt(instancesDoc.getDocumentElement().getAttribute("instancesCount"));
%>
          <%= instancesCount%> instances found, displaying page
<%
          for(int i=1;i<=pagesCount; i++)
          {
              if(i!=Integer.parseInt(pageNum))
                  {%>
                  <a href="<%=response.encodeURL("viewServiceInstances.jsp")%>?serviceName=<%= service%>&orderBy=<%= orderBy%>&order=<%= order %>&page=<%= i%>&instanceType=<%= instanceType %>">
		  <%= i%></a><%= i<pagesCount?",":""%><%}
              else {%><%= i%><%}

          }

%>            
        	<form name="deleteChecked" method="post" action="deleteInstanceRequest.jsp?opType=<%= instanceType %>&serviceName=<%= service %>">
<%
       


          DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            Document xslDocument = docFactory.newDocumentBuilder().parse(new File(application.getRealPath("WEB-INF/XSL/instancesList.xsl")));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
            transformer.setParameter("type", instanceType);
            transformer.setParameter("serviceName", service);
            transformer.setParameter("instanceIdOrderDirection", (orderBy.equals("INSTANCE_ID")?(order.equals("ASC")?"DESC":"ASC"):"ASC") );
            transformer.setParameter("orderIdOrderDirection", (orderBy.equals("ORDER_ID")?(order.equals("ASC")?"DESC":"ASC"):"ASC"));
            transformer.setParameter("arrivalDateOrderDirection",  (orderBy.equals("ARRIVAL_DATE")?(order.equals("ASC")?"DESC":"ASC"):"ASC"));
            transformer.setParameter("statusOrderDirection",  (orderBy.equals("STATUS")?(order.equals("ASC")?"DESC":"ASC"):"ASC"));
            transformer.setParameter("expirationDateOrderDirection",  (orderBy.equals("EXPIRATION_DATE")?(order.equals("ASC")?"DESC":"ASC"):"ASC"));
            transformer.setParameter("pushHostOrderDirection",  (orderBy.equals("PUSH_HOST")?(order.equals("ASC")?"DESC":"ASC"):"ASC"));


            transformer.transform(new DOMSource(instancesDoc), new StreamResult(out));

%>
<SCRIPT language="JavaScript">
<!--
function selectAll()
{
    <%  for(int i=1; i<=instanceNum; i++) {  %>
    	if(document.deleteChecked.INST<%=i%>)
        	document.deleteChecked.INST<%=i%>.checked = true;      
   <%} %>
} 

function toggleAll()
{
    <%  for(int i=1; i<=instanceNum; i++) {  %>
    	if(document.deleteChecked.INST<%=i%>)
        {
            if(document.deleteChecked.INST<%=i%>.checked==true)
                    document.deleteChecked.INST<%=i%>.checked = false; 
            else
                    document.deleteChecked.INST<%=i%>.checked = true;
         }
   <%} %>
}

function deleteAll()
{
    javascript: confirm ('manager?cmd=delAllInstances&serviceName=<%= service %>&opType=<%=instanceType %>', 'Are you sure to delete all instances?', 'Confirm', 'C');
}

function deleteSel()
{
    var ids="";

     <%  for(int i=1; i<=instanceNum; i++) {  %>
    	if(document.deleteChecked.INST<%=i%>.checked == true)
        	ids+='ID_'+<%=i%>+'='+document.deleteChecked.ID_<%=i%>.value+"&";
   <%} %>

   if(ids.length>0)
   {
       ids=ids.substring(0,ids.length-1);
       javascript: confirm ('manager?cmd=deleteInstances&serviceName=<%= service %>&opType=<%=instanceType %>&'+ids, 'Delete selected instances?', 'Confirm', 'C');
   }
}

//-->
</SCRIPT>  
                       
</P>                  
            <input type="button" name="select" value="<fmt:message key="viewServiceInstances.toggleAll" bundle="${lang}"/>" onClick="javascript:toggleAll()"/>
            <input type="button" name="deleteSelected" value="<fmt:message key="viewServiceInstances.DeleteChecked" bundle="${lang}"/>" onClick="javascript:deleteSel()"/>
            <input type="button" name="DELETEaLL" value="Delete all instances" onClick="javascript:deleteAll()"/>

                </form>
 </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

