<%@ page  language="java"%>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>

<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
session.invalidate();
if(request.getAttribute("requestURL") != null) {
    response.sendRedirect("loginRequest.jsp?" + request.getAttribute("requestURL") );
} else response.sendRedirect("loginRequest.jsp"  );
%>
