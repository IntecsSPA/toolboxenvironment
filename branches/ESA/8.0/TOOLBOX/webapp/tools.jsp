<%@ page language="java"  import="it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.service.*" errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>

<%@ include file="checkAccount.jsp" %>
<%@ include file="header.jsp" %> 

<c:if test="${sessionScope.languageReq!= null}">
<fmt:setLocale value="${sessionScope.languageReq}" />
<fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<%
PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;&nbsp;Tools";
String toolboxUrl=Toolbox.getInstance().getPublicAddress();
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <DIV class=portletItem id=01> 
                    <DIV>
                        <A href="createEbRRDB.jsp"><fmt:message key="tools.createebRRDB" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV> 
                    <P> 
                        <A href="createEbRRDB.jsp"><IMG class=labelHomePage title=Toolbox alt="Send a SOAP message" src="images/sendSOAP.jpg" align=middle border=0></A>
                        <fmt:message key="tools.createebRRDB.descr" bundle="${lang}"/>
                    </P> 
                </DIV> 
               

                <%
                    TBXService service;
                    ServiceManager servMan;

                    servMan=ServiceManager.getInstance();

                    if(serviceName!=null && serviceName.equals("")==false)
                        {
                    service=servMan.getService(serviceName);

                    if(service !=null && 
                            (service.getImplementdInterface().startsWith("OGC-06-131r6")||service.getImplementdInterface().startsWith("OGC-05-025r3")||service.getImplementdInterface().startsWith("OGC-07-038")||service.getImplementdInterface().startsWith("OGC-07-045")))
                    {
                %>
                <DIV class=portletItem id=05>
                    <DIV>
                        <A href="<%= response.encodeURL("ebrrHarvest.jsp?serviceName="+serviceName) %>"><fmt:message key="ebrr.harvest" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href="<%= response.encodeURL("ebrrHarvest.jsp?serviceName="+serviceName) %>"><IMG class=labelHomePage title=validate alt="" src="images/pushServer.jpg" align=middle border=0></A>
                    <fmt:message key="ebrr.harvest.description" bundle="${lang}"/></P>
                </DIV>
                <DIV class=portletItem id=05>
                    <DIV>
                        <A href="<%= response.encodeURL("ebrrHarvestFromDisk.jsp?serviceName="+serviceName) %>"><fmt:message key="ebrr.harvestfromdisk" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
                    </DIV>
                    <P>
                        <A href="<%= response.encodeURL("ebrrHarvestFromDisk.jsp?serviceName="+serviceName) %>"><IMG class=labelHomePage title=validate alt="<fmt:message key="ebrr.harvestfromdisk" bundle="${lang}"/>" src="images/pushServer.jpg" align=middle border=0></A>
                    <fmt:message key="ebrr.harvestfromdisk.description" bundle="${lang}"/></P>
                </DIV>
                <%
                    }
                    }
                %>
                
                              </TD>
        </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
