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
 -  File Name:         $RCSfile: errorPage.jsp,v $
 -  TOOLBOX Version:   $Name: V6_0 $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2006/10/12 11:46:09 $
 -
 -->
<%@ page import="it.intecs.pisa.soap.toolbox.*, java.util.*, java.io.*"  isErrorPage="true"%>

<jsp:include page="header.jsp" />
<% 
    String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt; Error page";
%>
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT> 
        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top"> 
          <TBODY> 
            <TR> 
              <TD id=main> <P class=arbloc><FONT class=arttl>Error page</FONT></P> 
                <P>  
				<!-- Page contents table-->
					<table width="90%" cellspacing="2" cellpadding="2" align="center">
						<form>
                                                <tr><!-- Row 1 -->
							<td class="tdInfo"  ><textarea rows="25" cols="100" readonly><% exception.printStackTrace(new PrintWriter(out, true)); %></textarea></td>
						</tr>
						<tr><!-- Row 2 -->
									<td class="tdHead" align="right" valign="middle" height="50"><p><input type="button" value="<< Back" onClick="history.back()"></td>
						</tr></form>
					</table>
                </P> 
&nbsp; </TD> 
            </TR> 
          </TBODY> 
        </TABLE></TD> 
    </TR> 
  </TBODY> 
</TABLE> 		
<jsp:include page="footer.jsp"/>
