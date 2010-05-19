<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>

<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%@ include file="checkSession.jsp" %>
<SCRIPT language="JavaScript">
    function gothere() 
    { 
        window.location="<%= response.encodeURL("createNewServiceSelectType.jsp")%>";
    } 
</SCRIPT>
<%
            String serviceAbstract = "";
            String serviceDescription = "";
            String serviceName="";
            Hashtable<String,String> newServiceHashtable;
            String serviceType;

            newServiceHashtable=(Hashtable<String,String>)session.getAttribute("newServiceHashtable");
            serviceName=newServiceHashtable.get("serviceName");


            serviceType=request.getParameter("serviceType");
            if(serviceType!=null)
                newServiceHashtable.put("serviceType",serviceType);

            serviceAbstract=newServiceHashtable.get("serviceAbstract");
            serviceDescription=newServiceHashtable.get("serviceDescription");
            

            PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));
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
                        <P class=arbloc><FONT class=arttl><fmt:message key="createNewService.begin" bundle="${lang}"/> 3 <fmt:message key="createNewService.central" bundle="${lang}"/> 6)</FONT></P>
                        
                        <form  method="post"  action="<%= response.encodeURL("createNewServiceSelectInterface.jsp")%>">
                            
                            <!-- Page contents table--> 
                            <table width="750pxpx" cellspacing="2" cellpadding="2" align="center">
                                
                                <tr><!-- Row 4 -->
                                    <td class="sortableHeader" colspan="2" nowrap><%=serviceName%>: <fmt:message key="createServiceRequest.ServiceInfo" bundle="${lang}"/></td>
                                </tr>
                                <tr><!-- Row 2 -->
                                    <td class="sortable" width="20%" colspan=1 nowrap><fmt:message key="createServiceRequest.ServiceAbstr" bundle="${lang}"/></td>
                                    <td class="sortable" nowrap><input name="serviceAbstract" type="text"  size="95"value="<%=serviceAbstract != null ? serviceAbstract : ""%>"></td>
                                </tr>
                                <tr><!-- Row 2 -->
                                    <td class="sortable" width="20%" colspan=1 nowrap><fmt:message key="createServiceRequest.ServiceDescr" bundle="${lang}"/></td>
                                    <td class="sortable" nowrap><textarea name="serviceDescription"  cols="80" rows="6" ><%=serviceDescription != null ? serviceDescription : ""%></textarea> </td>
                                </tr>
                                
                                <tr> 
                                    <!-- Row 8 --> 
                                    <td class="sortable" colspan="3" rowspan="2" nowrap align="right"><div align="right">
                                            <input type="button" value="<< <fmt:message key="createServiceRequest.Back" bundle="${lang}"/>" onClick="gothere()">
                                            <input type="submit" value="<fmt:message key="selectImportOrCreate.Next" bundle="${lang}"/>">
                                    </div></td> 
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

