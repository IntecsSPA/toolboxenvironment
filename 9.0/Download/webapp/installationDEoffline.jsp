<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="errorPage.jsp"%>
<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    String bc ="<a href='main.jsp'>Home</a>&nbsp;&gt;" + "&nbsp; <a href='toolboxInstallerDownload_60_100.jsp'>Toolbox download</a>&nbsp;&gt; Offline Procedure";
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
 <p>To install download the following zip files:</p>
  <p>
  <a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugIn/offline-install.zip">offline-install.zip</a> Code package of the Toolbox Development Environment. <br>
<a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugIn/offline-install.libs.zip">offline-install.libs.zip</a> Library package for the Toolbox Development Environment.</p>

<p>When downloaded, unpack both package under the Eclipse installation directory.</p>
<p>Start Eclipse.</p>
<p>&nbsp;</p>
<p align="left" class="itemDark mandatory">Nightly builds</p>
<p align="left">The procedure described above let you download and install the main version of TOOLBOX Development Environment. A nightly build upgrade site is available in order to obtain a newer version before it is ufficially released. To download the latest Nightly Build packages click on the following links:</p>
<p align="left"><a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugInNightly/offline-install.zip">offline-install.zip</a> Code package of the Toolbox Development Environment. <br>
    <a href="http://toolbox.pisa.intecs.it/download/toolbox/developPlugInNightly/offline-install.libs.zip">offline-install.libs.zip</a> Library package for the Toolbox Development Environment.</p>
<p>When downloaded, unpack both package under the Eclipse installation directory and start Eclipse.</p>
<p align="left">Always keep in mind that all versions downloaded from the Nightly build site may contain new features under development, so these versions are potentially unstable. </p><p>&nbsp;</p>
<p>&nbsp;</p></TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
