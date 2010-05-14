<%@ page language="java"  errorPage="errorPage.jsp" %>
<%@ include file="checkAccount.jsp" %>
<jsp:include page="header.jsp" /> 
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<%
        PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
        String home = (String)messages.getObject("toolboxConfiguration.home");
        String configuration = (String)messages.getObject("toolboxConfiguration.configuration");

        String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +"&nbsp;"+configuration;
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
        <DIV class=portletItem id=01> 
          <DIV> 
            <A href="<%= response.encodeURL("configureToolboxRequest.jsp") %>?pageStatus=disabled"><fmt:message key="toolboxConfiguration.view" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
          </DIV> 
          <P> 
              <A href="<%= response.encodeURL("configureToolboxRequest.jsp") %>?pageStatus=disabled"><IMG class=labelHomePage title="view" alt="View the TOOLBOX configuration" src="images/configureView.png" align=middle border=0></A>
            <fmt:message key="toolboxConfiguration.viewDescr" bundle="${lang}"/>
          </P> 
        </DIV> 
        <DIV class=portletItem id=02> 
          <DIV> 
            <A href="<%= response.encodeURL("configureToolboxRequest.jsp") %>?pageStatus=enabled"><fmt:message key="toolboxConfiguration.changeConf" bundle="${lang}"/>&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
          </DIV> 
             <P> <A href="<%= response.encodeURL("configureToolboxRequest.jsp") %>?pageStatus=enabled"><IMG 
                     class=labelHomePage title="change" alt="Change the TOOLBOX configuration" src="images/configure.png" align=middle border=0></A><fmt:message key="toolboxConfiguration.changeConfDescr" bundle="${lang}"/>
	     </P> 
        </DIV> 
	  </TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

