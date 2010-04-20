<%@ page language="java" import="it.intecs.pisa.soap.toolbox.*" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%
String requestURL = (request.getRequestURL()).toString();
if(requestURL != null) {
  if( ( requestURL.indexOf("viewServiceLog.jsp") >= 0 ) || ( requestURL.indexOf("viewAllServicesInstances.jsp") >= 0 ) ) {
    if (request.getQueryString() != null) {
        requestURL = requestURL + "?" + request.getQueryString();
        request.setAttribute( "requestURL", requestURL);
    }
  }
}
%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%  

    if (session.getAttribute("session") == null ||
        !session.getAttribute("session").equals(session.getId()) ||
        request.getSession(true).isNew() ) {
%>
			<jsp:forward page="logout.jsp" />
<%
        }


%>
