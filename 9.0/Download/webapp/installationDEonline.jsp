<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="errorPage.jsp"%>
<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    String bc ="<a href='main.jsp'>Home</a>&nbsp;&gt;" + "&nbsp; <a href='toolboxInstallerDownload_60_100.jsp'>Toolbox download</a>&nbsp;&gt; Online Procedure";
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
<p>The online installation can be done with just some steps:</p>
<p>Open Eclipse and select &quot;Find and install&quot; from the menu &quot;Software Updates&quot; under the Help main menu</p>
<p align="center"><img src="./images/eclipse_menu_instal_1.gif" width="431" height="250" align="absmiddle"></p>
<p align="left">Select &quot;Search for new features to install&quot;</p>
<p align="center"><img src="./images/eclipse_menu_instal_2.png" width="600" height="559" align="absmiddle"></p>
<p align="left">Select &quot;New Remote Site&quot; and fill the for as following:</p>
<div align="left">
  <ul>
    <li>Name: Intecs</li>
      <li>URL: http://toolbox.pisa.intecs.it/download/toolbox/developPlugIn/</li>
  </ul>
</div>
<p align="left">Click &quot;Ok&quot;.</p>
<p align="center"><img src="./images/eclipse_menu_instal_3.png" width="356" height="154" align="absmiddle"></p>
<p align="left">If you don't have WTP plugin installed or if you want to check for updates, select all available sites. Click &quot;Finish&quot;.</p>
<p align="center"><img src="./images/eclipse_menu_instal_4.png" width="600" height="559" align="absmiddle"></p>
<p align="left">The update procedure starts</p>
<p align="center"><img src="./images/eclipse_menu_instal_5.png" width="450" height="198" align="absmiddle"></p>
<p align="left">All available updates are shown to user. Select the TOOLBOX Develop Plugin.</p>
<p align="left">Click &quot;Next&quot; and accept the terms of license.</p>
<p align="center"><img src="./images/eclipse_menu_instal_6.png" width="600" height="500" align="absmiddle"></p>
<p align="left">Select &quot;Finish&quot;</p>
<p align="center"><img src="images/eclipse_menu_instal_7.png" width="600" height="500" align="absmiddle"></p>
<p align="left">If asked for installation verification, click &quot;Install All&quot;.</p>
<p align="left">If asked for reboot, click on &quot;Yes&quot;.</p>
<p align="center"><img src="images/eclipse_menu_instal_8.png" width="600" height="500" align="absmiddle"></p>
<p align="left" class="itemDark mandatory">Nightly builds</p>
<p align="left">The procedure described above let you add the download site for the main versions. A nightly build upgrade site is available in order to obtain a newer version before it is ufficially released.To add the Nightly Build site follow the above procedure using the following URL when adding the remote site: http://toolbox.pisa.intecs.it/download/toolbox/developPlugInNightly</p>
<p align="left">Always keep in mind that all versions downloaded from the Nightly build site may contain new features under development, so these versions are potentially unstable. </p>
</TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
