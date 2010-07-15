<%@page import="it.intecs.pisa.toolbox.configuration.ToolboxNetwork"%>
<%@page import="it.intecs.pisa.toolbox.configuration.ToolboxConfiguration"%>
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
String home = (String)messages.getObject("testCenter.home");
String testCenter = (String)messages.getObject("testCenter.testCenter"); 
String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +"&nbsp;"+testCenter;
ToolboxConfiguration td = ToolboxConfiguration.getInstance();
String toolboxUrl=ToolboxNetwork.getTOOLBOXEndpointURL();
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
    <TBODY> 
        <TR> 
            <TD class=pageBody id=main>
                <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                <SCRIPT>addHelp("RE/main.html_Run-time_environment*blankpage.html_tasks*blankpage.html_Test_Center*");</SCRIPT>

                <DIV class=portletItem id=01> 
                    <DIV> <!--%= response.encodeURL("../ToolboxGisClient/testCenter.html")  %-->
                        <A href="javascript:fullScreenPopup ('../ToolboxGisClient/clientTest.jsp?tbxUrl=<%=toolboxUrl%>&service=<%=serviceName%>', 'Toolbox Test Client');"><fmt:message key="testCenter.sendSOAP" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="javascript:fullScreenPopup ('../ToolboxGisClient/clientTest.jsp?tbxUrl=<%=toolboxUrl%>&service=<%=serviceName%>', 'Toolbox Test Client');"><IMG class=labelHomePage title=Toolbox alt="Send a SOAP message" src="images/sendSOAP.png" align=middle border=0></A>
                        <fmt:message key="testCenter.sendSOAPDescr" bundle="${lang}"/>
                    </P> 
                </DIV> 
                <DIV class=portletItem id=02> 
                    <DIV> <!-- <!-- %= response.encodeURL("catalogueSelection.jsp") % --> 
                        <A href="javascript:fullScreenPopup ('../ToolboxGisClient/catalogues.html', 'Toolbox Catalogues Client');"><fmt:message key="testCenter.testCatalogue" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P><A href="javascript:fullScreenPopup ('../ToolboxGisClient/catalogues.html', 'Toolbox Catalogues Client');"><IMG class=labelHomePage title="catalogue" alt="Test a catalogue" src="images/catalogueSearch.png" align=middle border=0></A><fmt:message key="testCenter.testCatalogueDescr" bundle="${lang}"/>
                    </P> 
                </DIV> 
                <DIV class=portletItem id=03> 
                    <DIV> 
                        <A href="<%= response.encodeURL("validateScriptRequest.jsp") %>"><fmt:message key="testCenter.validate" bundle="${lang}"/> &nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="<%= response.encodeURL("validateScriptRequest.jsp") %>"><IMG class=labelHomePage title=validate alt="Validate a script" src="images/validateXML.png" align=middle border=0></A>
                    <fmt:message key="testCenter.validateDescr" bundle="${lang}"/> </P> 
                </DIV> 
                <DIV class=portletItem id=04> 
                    <DIV> 
                        <A href="<%= response.encodeURL("viewPushedMsg.jsp") %>"><fmt:message key="testCenter.Push" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
                    </DIV> 
                    <P> 
                        <A href="<%= response.encodeURL("viewPushedMsg.jsp") %>"><IMG class=labelHomePage title=validate alt="Validate a script" src="images/pushServer.png" align=middle border=0></A>
                    <fmt:message key="testCenter.PushDescr" bundle="${lang}"/></P> 
                </DIV>
 </TD>
        </TR> 
    </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

