<!-- 
 -
 -  Copyright 2003-2010 Intecs
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
<%@ page contentType="text/html; charset=iso-8859-1" language="java"   %>
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*" %>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<c:if test="${lang == null}">
    <fmt:setLocale value="en" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<c:if test="${sessionScope.languageReq!= null}">
  <fmt:setLocale value="${sessionScope.languageReq}" />
  <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>  
</c:if>

<HTML>
    <HEAD>
        <meta http-equiv="expires" content="now">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">

    <TITLE>TOOLBOX</TITLE>
    <META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" href="jsScripts/dhtmlwindow.css" type="text/css" >
    <script type="text/javascript" src="jsScripts/DhtmlNew.js"></script>

    <link href="jsScripts/dom.css" rel=stylesheet>
    <link href="jsScripts/dom.directory.css" rel=stylesheet>
    <link href="jsScripts/portal.css" rel=stylesheet>
    <link href="jsScripts/styles.css" rel=stylesheet>
    <link rel="SHORTCUT ICON" href="images/toolboxIcon.gif">
    <META http-equiv=Pragma content=no-cache>
    <META http-equiv=Cache-Control content="no-store, no-cache, must-revalidate, post-check=0, pre-check=0">
    <META http-equiv=Expires content=0>
    <SCRIPT type="text/javascript" src="jsScripts/CommonScript.js"></SCRIPT>
    <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/manager.js"></script>
    
    </HEAD>
    <BODY><TABLE cellSpacing=0 cellPadding=0 width="100%">
    <TR>
        <TD class=headerTemplate>
            <!-- bad -->
            <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
                <TR class=flagcolour>
                    <TD height=2><IMG src="images/1x1.gif"></TD>
                </TR>
                <TR>
                    <TD>
                        <TABLE class=flagcolour cellSpacing=0 cellPadding=0 width="100%" align=center border=0>
                        <TBODY>
                        <TR bgColor=#000000>
                            <TD vAlign=bottom align=left bgColor=#000000 height=60><A
                                    href="main.jsp" target=_top><IMG src="images/toolboxLogo.png" border=0></A> </TD>
                            <TD vAlign=bottom align=middle bgColor=#000000 height=60><!-- &nbsp; --></TD>
                            <TD vAlign=bottom align=right bgColor=#000000 height=60>
                                <IMG height=64 src="images/directory_top.png" width=293 border=0 name=top> </TD>
                        </TR>
                        <TR class=flagcolour>
                            <TD colSpan=3 height=1><IMG src="images/1x1.gif"></TD>
                        </TR>
                        <TR bgColor=#000000 height=10>
                                            <TD bgColor=#000000 colSpan=2 height=12></TD>
                                            <TD vAlign=top align=right bgColor=#000000>
                                                <IMG height=13
                                                     src="images/directory_bot.png" width=293
                                                     border=0 name=bot>
                                            </TD>
                                        </TR>
                                        <TR bgColor=#e0dfe3>
                                            <TD colSpan=3 height=1><IMG height=1 alt=""
                                                                    src="images/1x1.gif" border=0></TD>
                                        </TR>

                        </TBODY>
                        </TABLE>
                        </TD>
                        </TR>
                        </TABLE>
                    </TD>
                </TR>
            </TABLE>


<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    PropertyResourceBundle messages = (PropertyResourceBundle)ResourceBundle.getBundle("ToolboxBundle", new Locale((String)request.getParameter("language")));
    String home = (String)messages.getObject("about.home");
    String about = (String)messages.getObject("about.about");
    String bc ="<a href='main.jsp'>"+home+"</a>&nbsp;&gt;" + "&nbsp;"+ about;
%>
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><fmt:message key="about.about" bundle="${lang}"/></P> 
                <table cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="tdHelp" valign="top"><p align="justify"><fmt:message key="about.description" bundle="${lang}"/></p></td>
                  </tr>
                </table>                <p align="justify">&nbsp;</p>		</TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 
<TABLE cellSpacing=0 cellPadding=0 width="100%">
  <TBODY>
    <TR>
      <TD class=footer>
          <DIV class=menu id=menuBottom><A class=center
      href="main.jsp"><fmt:message key="footer.home" bundle="${lang}"/></A> <A class=center
      href=""><fmt:message key="footer.about" bundle="${lang}"/></A> <A class=center
      href="copyrightsNotLogged.jsp?language=en"><fmt:message key="footer.copyright" bundle="${lang}"/></A> <A class=center
      href="mailto:toolbox@intecs.it"><fmt:message key="footer.contact" bundle="${lang}"/></A>
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


