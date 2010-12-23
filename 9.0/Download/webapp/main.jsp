<%@ page language="java"  errorPage="errorPage.jsp" %>
<jsp:include page="header.jsp" /> 
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
	   <TD class=pageBody id=main> <SCRIPT>addBreadCrumb("Welcome to the Toolbox Home Page.");</SCRIPT> 
        <br> 
    	 <DIV class=portletItem id=01>  
          <!-- TITLE part --> 
          <DIV> 
            <A href="overview.jsp">Overview&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
          </DIV> 
          <P> 
            <IMG class="labelHomePage" title="Toolbox" alt=Toolbox src="images/toolbox.png" align=middle border=0>
            In this section an overview of the whole project is provided in order to better understand all functionality of Toolbox.
          </P> 
        </DIV> 
		
		<DIV class=portletItem id=02> 
          <DIV> 
            <A href="http://toolbox.esrin.esa.int/TOOLBOXDOC/">Documentation&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
          </DIV> 
          <p><IMG src="images/documentation.png" alt="Download" width="104" height="69" border=0 align=middle class=labelHomePage title="Downlaod">
            Here you can find the Software User Manual (SUM) of Toolbox and a complete set of tutorials.
          </p>
		</DIV> 
		
		<DIV class=portletItem id=02> 
          <DIV> 
            <A href="http://wiki.services.eoportal.org/tiki-view_forum.php?forumId=3">Online Forum&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN>
          </DIV> 
          <P> <IMG src="images/Toolbox_forum.png" alt="Download" width="162" height="90" border=0 align=middle class=labelHomePage title="Downlaod">
		  Online forum for all Toolbox related questions.
		  </P> 
        </DIV> 
		
        <DIV class=portletItem id=02> 
          <DIV> 
            <A href="download.jsp">Download Toolbox&nbsp;<IMG src="images/arrow.gif"></A><SPAN><IMG src="images/tt_square2.gif"></SPAN> 
          </DIV> 
          <P> <IMG class=labelHomePage title="Downlaod" alt="Download" src="images/download.png" align=middle border=0>
		  From this section you can download all Toolbox packages for Windows and Linux platforms
		  </P> 
        </DIV> 
		
		
        
		</TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
