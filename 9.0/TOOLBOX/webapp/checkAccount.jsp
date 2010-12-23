<%@ page import="it.intecs.pisa.toolbox.*, 
                 java.util.*,
                 org.w3c.dom.*,
                 it.intecs.pisa.util.*,
                 java.io.*"  errorPage="errorPage.jsp" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%	
	if (request.getParameter("userName") == null || request.getParameter("password") == null) {
%>
	<%@ include file="checkSession.jsp" %>
<%		
	} else {
		NodeList adminUsers = DOMUtil.loadXML(new File(application.getRealPath("WEB-INF/xml/adminUsers.xml")),new File(application.getRealPath("WEB-INF/schemas/adminUsers.xsd"))).getDocumentElement().getElementsByTagName("adminUser");
		boolean isValidAccount = false;
		for (int i = 0; i < adminUsers.getLength(); i++ ) {
			if (request.getParameter("userName").equals(((Element) adminUsers.item(i)).getAttribute("userName")) && request.getParameter("password").equals(((Element) adminUsers.item(i)).getAttribute("password"))) {
				isValidAccount = true;
                                session.setMaxInactiveInterval(1800);
                                session.setAttribute("session", session.getId());
                                session.setAttribute("languageReq", request.getSession().getAttribute("language"));
                                break;
			}
		}
		if (!isValidAccount) {
                    PropertyResourceBundle messages = 
                            (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("language")));
                    String warnMsg = (String)messages.getObject("general.incorrectUserNamePw");
%>
	<jsp:forward page="loginRequest.jsp">
                <jsp:param name="warn" value="<%=warnMsg%>"/>
	</jsp:forward>
<%
		}
	}
%>
