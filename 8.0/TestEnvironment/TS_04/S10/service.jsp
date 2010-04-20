<%@ page errorPage="error.jsp" import="java.io.*,java.util.*,javax.mail.*,javax.mail.internet.*" %>
<%
	StringBuffer buffer = new StringBuffer();
	StringBuffer date = new StringBuffer();
	String oldDate = request.getParameter("DATE");
	date.append(oldDate.substring(0,4)+oldDate.substring(5,7)+oldDate.substring(8,10));
	buffer.append("V1KRNS10__" +date+"_"+request.getParameter("FORMAT")
				+"_"+request.getParameter("ROI")+".ZIP");


if(oldDate.compareTo("1998-01-01")<0)
{
%>
	<jsp:forward page="message.jsp">
  	<jsp:param name="message" value="Product Not Available"/>
	</jsp:forward>
<%
}
%>
<html>
<head>
<title>SUCCESSFUL response page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<table border="0" cellpadding="0" cellspacing="0">
  <tr> 
    <td bgcolor="#333366"><FONT COLOR="#FFFFFF">RFQ RESPONSE</FONT></td>
 </tr> 
 <tr> 
    <td bgcolor="#333366"><FONT COLOR="#FFFFFF">Product File Name</FONT></td>
    <td><%=buffer.toString()%></td> 
  </tr>
  <tr> 
    <td bgcolor="#333366"><FONT COLOR="#FFFFFF">Product Size</FONT></td>
	<td>23.5</td>
    <td>Mb</td>
   </tr>
<tr>
    <td bgcolor="#333366"><FONT COLOR="#FFFFFF">Available User Quota</FONT></td>
<td>9.6</td>
<td>Gb</td>
   </tr>
 </table>
</body>
</html>

