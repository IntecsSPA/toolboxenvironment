<%@page contentType="text/html" pageEncoding="UTF-8" import="it.intecs.pisa.pluginscore.*,
                                                             java.io.*,
                                                             it.intecs.pisa.toolbox.*,
                                                             java.util.*"%>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ include file="checkSession.jsp" %>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<jsp:include page="header.jsp" />
<%
    Hashtable<String,String> newServiceHashtable;

    String serviceName="";
    String serviceType="";
    String importOrCreate="";
    String[] types;

    PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
    String home = (String) messages.getObject("createServiceRequest.home");
        String serviceManag = (String) messages.getObject("createServiceRequest.serviceManag");
        String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;&nbsp;<a href='servicesManagement.jsp'>" + serviceManag + "</a>";
        
    newServiceHashtable=(Hashtable<String,String>)session.getAttribute("newServiceHashtable");
    types=InterfacePluginManager.getInstance().getInterfacesTypes();

    serviceName=request.getParameter("serviceName");
    if(serviceName!=null)
        newServiceHashtable.put("serviceName",serviceName);

    importOrCreate=request.getParameter("importOrCreate");
    if(importOrCreate!=null)
        newServiceHashtable.put("importOrCreate",importOrCreate);
    
    serviceType=newServiceHashtable.get("serviceType");

    importOrCreate=newServiceHashtable.get("importOrCreate");
    if(importOrCreate.equals("import"))
        {
    %>
    <jsp:forward page="importNewService.jsp"/>
<% }%>


<SCRIPT language="JavaScript">
   function gothere()
    {
        window.location="<%= response.encodeURL("selectImportOrCreate.jsp?bachbutton=true")%>";
    }
</SCRIPT>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" height="100%" align=center>
            <TBODY>
                <TR>
                    <TD class=pageBody id=main>
                        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/serviceManagement.html_Service_management*RE/serviceCreation.html_Service_creation*");</SCRIPT>
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                            <TBODY>
                                <TR>
                                    <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="createNewService.begin" bundle="${lang}"/> 2 <fmt:message key="createNewService.central" bundle="${lang}"/> 6)</FONT></P>
                                        <P>
                                            <form   method="post" action="<%= response.encodeURL("createNewServiceSetAbstractDescription.jsp")%>" >
                                                <!-- Page contents table-->
                                                <table width="250" cellspacing="2" cellpadding="2" align="center">
                                                    <tr>
                                                        <td class="sortable"  colspan=2 nowrap>
                                                            <fmt:message key="serviceCreation.selectType" bundle="${lang}"/>
                                                            <select name="serviceType" id="serviceType">
                                                               <option <%=serviceType.equals("UserDefined")?"selected=\"true\"":"" %> value="<%= serviceType%>"><%= serviceType%></option>
                                                               <%
                                                                    for(String type:types)
                                                                        {
                                                               %>
                                                               <option <%=type.equals(serviceType)?"selected=\"true\"":"" %> value="<%= type%>"><%= type%></option>
                                                             <% } %>
                                                            </select>
                                                        </td>
                                                    </tr>

                                                    <tr>
                                                        <td colspan="2" rowspan="2" nowrap align="right" class="sortable"><div align="right">
                                                                <input type="button" value="<< <fmt:message key="createServiceRequest.Back" bundle="${lang}"/>" onclick="gothere()">
                                                                <input type="submit" value="<fmt:message key="selectImportOrCreate.Next" bundle="${lang}"/> >>">
                                                        </div></td>
                                                    </tr>
                                                </table>
                                            </form>
                                        </P>
                                    </TD>
                                </TR>
                            </TBODY>
                    </TABLE></TD>
                </TR>
            </TBODY>
        </TABLE>
    </body>
</html>
<jsp:include page="footer.jsp"/>