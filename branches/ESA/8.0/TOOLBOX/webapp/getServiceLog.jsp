<%@ page language="java" import="java.util.*, org.w3c.dom.*, javax.xml.parsers.*, java.io.*, it.intecs.pisa.util.*, javax.xml.transform.*,  javax.xml.transform.dom.*,  javax.xml.transform.stream.*, it.intecs.pisa.soap.toolbox.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<% 
    String redirectURL;
  

        String service = request.getParameter("serviceName");
        String textParam = request.getParameter("text") == null ? "" : request.getParameter("text");
        
          redirectURL="manager?cmd=getSrvLog&serviceName="+service;
          
          response.sendRedirect(redirectURL);
%>          
 <!--       boolean text = textParam.equals("yes");

        try{
            if (text) {                
IOUtil.inputToString(Toolbox.getToolboxConfigurator().viewTextServiceLog(service))

            } else {
             
               IOUtil.inputToString(Toolbox.getToolboxConfigurator().viewServiceLog(service))

            }
        } catch (Exception e){
            e.printStackTrace();
        }
     
-->