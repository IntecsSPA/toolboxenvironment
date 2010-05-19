<!-- 
 -
 -  Copyright 2003-2004 Intecs
 -
 -  This file is part of TOOLBOX.
 -  TOOLBOX is free software; you can redistribute it andOr modify
 -  it under the terms of the GNU General Public License as published by
 -  the Free Software Foundation; either version 2 of the License, or
 -  (at your option) any later version.
 -
 -  File Name:         $RCSfile: listFTPAccounts.jsp,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2005/04/14 11:52:13 $
 -
 -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"  errorPage="errorPage.jsp"  %>
<%@ page import="it.intecs.pisa.toolbox.*,it.intecs.pisa.toolbox.configuration.*, java.util.*,java.io.*" %>
<%@ include file="checkSession.jsp" %>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>
<jsp:include page="header.jsp?firebugControl=true&loadPanel=false&loadDefer=5000&extVers=3" />
<%		
  PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)session.getAttribute("languageReq")));
  String home = (String)messages.getObject("listFTPAccounts.home");
  String ftpManag = (String)messages.getObject("listFTPAccounts.ftpManag");
  String ftpList = (String)messages.getObject("listFTPAccounts.ftpList");
  
  String bc = "<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" +
              "&nbsp;<a href='FTPManagement.jsp'>"+ftpManag+"</a>&nbsp;&gt;" + ftpList;

  String user="";
  String writePermissionChecked="";
  String rootDir="";
  String removeFTPAccount="";

  String xmlFormAsString="";
  FileOutputStream outStr=null;
  PrintWriter printer=null;

  ToolboxConfiguration configuration;
  configuration=ToolboxConfiguration.getInstance();

  FTPServerManager ftpMan;
  ftpMan=FTPServerManager.getInstance();

  xmlFormAsString="<?xml version=\"1.0\" encoding=\"UTF-8\"?><inputInterface xmlns=\"http://gisClient.pisa.intecs.it/gisClient\"><section name=\"Catalogues\" cols=\"2\">";
  String[] ftpUsers =ftpMan.getUsers();
   for (int i =0; i<ftpUsers.length ; i++){
	user = ftpUsers[i];
        writePermissionChecked = ftpMan.doesUserHasWritePermission(user)? "checked" : "";
        rootDir = ftpMan.getUser(user).getHomeDirectory() != null ? ftpMan.getUser(user).getHomeDirectory() : "";
        removeFTPAccount = "removeFTPAccount.jsp?user=" + user;
        xmlFormAsString+="<group label=\"\">";
        xmlFormAsString+="<input type=\"label\" id=\"usernameLabel"+user+"\" name=\"catalogueDescription\" value=\"Username: \"/>";
        xmlFormAsString+="<input type=\"label\" id=\"usernameLabelValue"+user+"\" name=\"catalogueDescription\" value=\""+user+" \"/>";
        xmlFormAsString+="<input type=\"label\" id=\"pathLabel"+user+"\" name=\"catalogueDescription\" value=\"Path: \"/>";
        xmlFormAsString+="<input type=\"label\" id=\"pathLabelValue"+user+"\" name=\"catalogueDescription\" value=\""+rootDir+"\"/>";
        xmlFormAsString+="<input type=\"checkbox\" id=\"writePermissionCheckBox"+user+"\" name=\"catalogueDescription\" value=\""+writePermissionChecked+"\" label=\"Write permission\"  onclick=\"xmlInt.checkboxChanged\" handlerParameters=\"{id: 'writePermissionCheckBox"+user+"'}\"/>";
        xmlFormAsString+="<input type=\"label\" id=\"hidden"+user+"\" name=\"catalogueDescription\" value=\"\"/>";
        xmlFormAsString+="<input type=\"button\" id=\"apply"+user+"\" name=\"Apply\"  label=\"Apply changes\" onclick=\"xmlInt.sendWritePermissionMessageToManager\" handlerParameters=\"{fieldId: 'writePermissionCheckBox"+user+"'   ,managerUrl: 'manager?cmd=applyFtpChanges&amp;user="+user+"', disableId: 'apply"+user+"'}\"  />";
        if(user.equals("admin")==false)
            xmlFormAsString+="<input type=\"button\" id=\"delete"+user+"\" name=\"Delete\"  label=\"Delete FTP account\" onclick=\"xmlInt.sendDeleteUserMessageToManager\" handlerParameters=\"{managerUrl: 'manager?cmd=deleteFtpUser&amp;user="+user+"'}\"  />";                 
       
        xmlFormAsString+="</group>";
  }
  xmlFormAsString+="</section></inputInterface>";
  outStr=new FileOutputStream(new File(Toolbox.getInstance().getRootDir(),"jsScripts/widgets/FTPAccountList/FTPAccountList.xml"));
  printer=new PrintWriter(outStr);
  
  printer.print(xmlFormAsString);
  printer.close();
  outStr.close();
             
%>

<TABLE cellSpacing=0 cellPadding=0 width="100%"  align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main>
        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <SCRIPT>addHelp("RE/main.html_Run-time_environment*RE/blankpage.html_tasks*RE/ftpManagement.html_FTP_management*RE/ftpListing.html_Listing_and_deleting_an_FTP_account*");</SCRIPT>
       
     
       
       <script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>

        <link rel="stylesheet" type="text/css" href="jsScripts/import/gis-client-library/widgets/style/css/webgis.css" />

        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/manager.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/XmlDoc.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/browserDetect.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/ExtFormUtils.js"></script>
        <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/ext/RowExpander.js"></script>
        <!--script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/style/locale/en.js"></script-->
        <script type="text/javascript" src="jsScripts/widgets/FTPAccountList/scripts/FTPList.js"></script>
        
        
        
        <!--<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl><fmt:message key="listFTPAccounts.ftpList" bundle="${lang}"/></FONT></P> 
               
                <P>  
                <table width="60%" cellspacing="2" cellpadding="2" border="0" align="center"> 
                  <tr> 
                 
                    <td class=sortableHeader nowrap><fmt:message key="listFTPAccounts.username" bundle="${lang}"/></td> 
                    <td class=sortableHeader nowrap><fmt:message key="listFTPAccounts.rootDir" bundle="${lang}"/></td> 
                    <td class=sortableHeader nowrap><fmt:message key="listFTPAccounts.write" bundle="${lang}"/></td> 
                    <td class=sortableHeader width="71" align="center"></td> 
                  </tr> 
      
                  <tr> 
                 
                    <td class=sortable height="20" nowrap>&nbsp;<%= user %></td> 
                    <td class=sortable height="20" nowrap>&nbsp;<%= rootDir %></td> 
  		    <td class="sortable" nowrap><input name="writePermission" type="checkbox" <%=writePermissionChecked%> disabled="true"></td>
                    
                        <td class=sortable align="center"><a href=# onclick="javascript: switchLanguage('<%= removeFTPAccount %>');" class="sortable">&nbsp;<fmt:message key="listFTPAccounts.Delete" bundle="${lang}"/>&nbsp;</a></td> 
                    
                </tr> 
                 
                </table> 
                </P> 
		&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE>--></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>

