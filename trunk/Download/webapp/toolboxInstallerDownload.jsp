<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="errorPage.jsp"%>
<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
      <TD class=pageBody id=main><SCRIPT>addBreadCrumb("<a href='main.jsp'>Home</a>&nbsp;&gt;<a href='download.jsp'>Download</a>&nbsp;&gt;" +
              "&nbsp;Installer download");</SCRIPT>
                  <!-- TITLE part -->
				<h2 class="itemCategoryTitle">  Toolbox Runtime Environment 8.0 </h2>
                <DIV class=portletItem id=01>
                  <DIV> <A href="">Toolbox installer&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> </DIV>
                  <P> <IMG src="images/toolbox.png" alt=Linux width="104" height="69" border=0 align=middle class=labelHomePage title=Toolbox>
				      <img src="images/arrow.gif"> Download the <a href="toolbox/Toolbox8.0.jar">Toolbox full installer</a></P>
                  <P> <img src="images/arrow.gif"> Download the <a href="toolbox/Toolbox8.0-web.jar">Toolbox Light Installer</a></P>
         
                </DIV>
                <DIV class=portletItem id=01>
                  <DIV> <A href="">Toolbox installer with bundle Apache Tomcat&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> </DIV>
                  <P> <IMG src="images/toolbox.png" alt=Linux width="104" height="69" border=0 align=middle class=labelHomePage title=Toolbox>
        	      <img src="images/arrow.gif"> Download the <a href="toolbox/Toolbox8.0-apache.jar">Toolbox full installer with bundled Tomcat</a></P>
                  <P> <img src="images/arrow.gif"> Download the <a href="toolbox/Toolbox8.0-apache-web.jar">Toolbox Light Installer with bundled Tomcat</a></P>

                </DIV>
                
	  </TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
