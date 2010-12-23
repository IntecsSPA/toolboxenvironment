<%@ page import="it.intecs.pisa.toolbox.configuration.*,
         it.intecs.pisa.util.*,
         it.intecs.pisa.toolbox.service.*,
         it.intecs.pisa.toolbox.util.*,
         it.intecs.pisa.toolbox.configuration.*,
         it.intecs.pisa.toolbox.*,
         java.util.*"  errorPage="errorPage.jsp" %>

<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<%
        ToolboxConfiguration configuration;
        configuration = ToolboxConfiguration.getInstance();
        if (configuration.getConfigurationValue(ToolboxConfiguration.CLASSIC_HEADER).equals("true")) {
%><jsp:include page="headerClassic.jsp" /><%} else {
%><jsp:include page="headerDock.jsp" /><%        }
%>
