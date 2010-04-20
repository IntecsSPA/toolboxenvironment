<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*, org.w3c.dom.*, it.intecs.pisa.util.*,java.io.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<TABLE cellSpacing=0 cellPadding=0 width="100%"> 
  <TBODY> 
    <TR> 
      <TD class=footer> 
          <DIV class=menu id=menuBottom><A class=center 
      href="main.jsp"><fmt:message key="footer.home" bundle="${lang}"/></A> <A class=center 
      href="aboutSSE.jsp"><fmt:message key="footer.about" bundle="${lang}"/></A> <A class=center 
      href="copyrights.jsp"><fmt:message key="footer.copyright" bundle="${lang}"/></A> <A class=center 
      href="mailto:toolbox@pisa.intecs.it"><fmt:message key="footer.contact" bundle="${lang}"/></A> 
          </DIV> 
     <DIV> 
          <P><SMALL>&nbsp;&nbsp;&nbsp;<fmt:message key="footer.bestView" bundle="${lang}"/> <A 
      href="http://www.microsoft.com/ie" target=_blank>Internet Explorer 6.0</A> <fmt:message key="footer.or" bundle="${lang}"/> <A href="http://www.mozilla-europe.org/en/products/firefox/" 
      target=_blank>Firefox 1.5</A>.</SMALL><BR> 
         </P> 
        </DIV></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
</BODY>
</HTML>
