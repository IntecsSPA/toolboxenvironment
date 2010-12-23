<%@ page import="java.io.*,java.util.*,javax.mail.*,javax.mail.internet.*" %>
<%
String fileName = application.getRealPath("") + "/WEB-INF/xml/toolbox.xml";

String thisLine;
FileInputStream fis =  new FileInputStream( fileName );
BufferedReader inputReader = new BufferedReader(new InputStreamReader(fis));
StringBuffer outBuf = new StringBuffer();
while ((thisLine = inputReader.readLine()) != null) {  
  outBuf.append(thisLine);
}
String fileXml = outBuf.toString();
%>
<%=fileXml%>

