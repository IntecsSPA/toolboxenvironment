<%-- 
    Document   : createNewServiceSelectType.jsp
    Created on : 1-dic-2008, 16.23.43
    Author     : massi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="it.intecs.pisa.soap.toolbox.*, java.util.*,it.intecs.pisa.toolbox.plugins.*"%>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ include file="checkSession.jsp" %>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<jsp:include page="header.jsp" />

<%
        String interfaceNames;
        String serviceName;
        String interfaceName,interfaceVersion,interfaceType;
        Hashtable<String,String> newServiceHashtable;
        InterfacePluginManager interfManager;
        String[] interfaceModes;
        String modes;
        Properties props;
        StringTokenizer tokenizer;
        int tokenCount=0;
        int i=0;


        newServiceHashtable=(Hashtable<String,String>)session.getAttribute("newServiceHashtable");

        interfaceNames = (String) request.getParameter("interfaceNames");
        if(interfaceNames!=null)
            newServiceHashtable.put("interfaceNames", interfaceNames);

        session.setAttribute("interfaceNames", interfaceNames);

        serviceName = newServiceHashtable.get("serviceName");
        interfaceName = interfaceNames.substring(0, interfaceNames.indexOf(" "));
        interfaceVersion = interfaceNames.substring(interfaceNames.lastIndexOf(" ")+1);
        interfaceType=newServiceHashtable.get("serviceType");
        
        interfManager=InterfacePluginManager.getInstance();
        interfaceModes=interfManager.getInterfacesModes(interfaceName,interfaceVersion,interfaceType);

        PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
        String home = (String) messages.getObject("createServiceRequest.home");
        String serviceManag = (String) messages.getObject("createServiceRequest.serviceManag");
        String bc = "<a href='main.jsp'>" + home + "</a>&nbsp;&gt;&nbsp;<a href='servicesManagement.jsp'>" + serviceManag + "</a>";
%>
<SCRIPT language="JavaScript">
       

   function gothere()
    {
        window.location="<%= response.encodeURL("createNewServiceSelectInterface.jsp?backbutton=true")%>";
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
                        <SCRIPT>addHelp("serviceCreation");</SCRIPT>
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                            <TBODY>
                                <TR>
                                    <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="createNewService.begin" bundle="${lang}"/> 5 <fmt:message key="createNewService.central" bundle="${lang}"/> 6)</FONT></P>
                                        <P>
                                            <form   method="post" action="<%= response.encodeURL("createServiceRequestOptions.jsp")%>" >
                                                <!-- Page contents table-->
                                                <table width="250" cellspacing="2" cellpadding="2" align="center">
                                                    <tr>
                                                        <td class="sortable"  colspan=2 nowrap>
                                                            <fmt:message key="catalogueCreation.selectMode" bundle="${lang}"/>
                                                            <select name="serviceImplementationMode" id="serviceImplementationMode">
                                                               <% for(int j=0;j<interfaceModes.length;j++)
                                                                    {
                                                                        String mode;
                                                                        mode=interfaceModes[j];%>
                                                                        <option  value="<%=mode%>"><%=mode%></option>
                                                               <% }%>
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