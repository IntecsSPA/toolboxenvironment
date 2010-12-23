<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="errorPage.jsp" import="java.io.*,java.util.*,javax.mail.*,javax.mail.internet.*" %>

<%
String email = request.getParameter("EMAIL");
String license = request.getParameter("acceptOrDecline");
if(email.equals(""))
{
%>
	<jsp:forward page="download.jsp">
  		<jsp:param name="message" value="email"/>
	</jsp:forward>
<%
}
if(license.equals("decline"))
{
%>
	<jsp:forward page="download.jsp">
	  	<jsp:param name="message" value="noAgree"/>
	</jsp:forward>
<%
}
	StringBuffer buffer = new StringBuffer();
    String firstName = " ";
    String lastName = " ";
    String company =  " ";
    String eMail = " ";
	buffer.append("   First Name : " + (firstName = request.getParameter("FIRST_NAME")) +'\n');
	buffer.append("   Last Name : " + (lastName = request.getParameter("LAST_NAME")) +'\n');
	buffer.append("   Company: " + (company = request.getParameter("COMPANY")) +'\n');
	buffer.append("   Emails : " + (eMail = request.getParameter("EMAIL")) +'\n');	
	
	Properties properties = System.getProperties();
    properties.put("mail.transport.protocol", "smtp");
    properties.put("mail.smtp.host", "78.5.104.178");
    
    Session ses = Session.getInstance(properties, null);
    ses.getProperties().put("mail.smtp.auth", "true");
    
    MimeMessage message = new MimeMessage(ses);
    message.setFrom(new InternetAddress("toolbox@intecs.it"));
    message.setRecipient(Message.RecipientType.TO, new InternetAddress("toolbox@intecs.it"));
    message.setSubject("TOOLBOX DOWNLOAD :"+request.getParameter("FIRST_NAME") +" "+request.getParameter("LAST_NAME"));
    message.setText("Contacts:"+'\n'+buffer.toString());
   // Transport.send(message);

    
    Transport tr = ses.getTransport("smtp");
    tr.connect("78.5.104.178", 25, "toolbox", ".toolbox");
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();

    
    
    
    message = new MimeMessage(ses);
    message.setFrom(new InternetAddress("toolbox@intecs.it"));
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(request.getParameter("EMAIL")));
    message.setSubject("TOOLBOX download confirmation.");
    message.setText("Dear "+request.getParameter("FIRST_NAME")+" "+request.getParameter("LAST_NAME")+","+'\n'+'\n'
 					+"You can now download the TOOLBOX by following this link http://toolbox.esrin.esa.int/Download/toolboxInstallerDownload.jsp"+'\n'
 				    +"You are now registered to the TOOLBOX update list."+'\n'
				    +"You will receive a notification when the TOOLBOX will be updated."+'\n'
					+"If you want to be removed from the TOOLBOX update mail list please send an email to toolbox@intecs.it with subject TOOLBOX_MAIL_LIST_REMOVE"+'\n'+'\n'
					+"NOTE: If you are not concerned about this message, please ignore it.");
    //Transport.send(message);
    tr = ses.getTransport("smtp");
    tr.connect("78.5.104.178", 25, "toolbox", ".toolbox");
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();
    
    Calendar calendar = new GregorianCalendar();
    Hashtable hash = new Hashtable();
    hash.put(new Integer(0), "Jan");
    hash.put(new Integer(1), "Feb");
    hash.put(new Integer(2), "Mar");
    hash.put(new Integer(3), "Apr");
    hash.put(new Integer(4), "May");
    hash.put(new Integer(5), "Jun");
    hash.put(new Integer(6), "Jul");
    hash.put(new Integer(7), "Aug");
    hash.put(new Integer(8), "Sep");
    hash.put(new Integer(9), "Oct");
    hash.put(new Integer(10), "Nov");
    hash.put(new Integer(11), "Dec");
    String date = (calendar.get(Calendar.DAY_OF_MONTH) + "-" + hash.get(new Integer(calendar.get(Calendar.MONTH))) + "-" + String.valueOf(calendar.get(Calendar.YEAR)));
    String sep = ", ";
    String reportLine = (date + sep + firstName + sep + lastName + sep + company + sep + eMail + '\n');
    String fileName = application.getRealPath("WEB-INF/report/downloadStat.txt");
    DataOutputStream output = new DataOutputStream(new FileOutputStream(fileName, true));
    output.writeBytes(reportLine);
    output.close();
%>

<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center>
  <TBODY>
  
  <TR>
  
  <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<a href='main.jsp'>Home</a>&nbsp;&gt;<a href='download.jsp'>Download</a>&nbsp;&gt;" +
              "&nbsp;Email confirmation");</SCRIPT>
    <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
      <TBODY>
        <TR>
          <TD id=main><P class=arbloc>Email confirmation</P>
					<div style="border: 2px none ; margin-left: 0pt;"> 
                    <p align="left">An email with the download link has been sent 
                      to your email address. To download the TOOLBOX follow the 
                      link included in the email you should have received.</p>
                    
            <p align="left">To install the TOOLBOX read the on-line documentation.</p>
                    <p align="left">&nbsp;</p>
                  </div>
    </TABLE></TD>
  </TR>
  
  </TBODY>
  
</TABLE>
<jsp:include page="footer.jsp"/>
