<%@page contentType="text/html" pageEncoding="UTF-8" import="org.w3c.dom.*,it.intecs.pisa.util.*,it.intecs.pisa.toolbox.service.*,java.util.*,java.io.*"%>
<%@taglib uri="http://java.sun.com/jstl/core"  prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt"  prefix="fmt"%>
<%@ include file="checkSession.jsp" %>
<c:if test="${sessionScope.languageReq!= null}">
    <fmt:setLocale value="${sessionScope.languageReq}" />
    <fmt:setBundle basename="ToolboxBundle" var="lang" scope="page"/>
</c:if>
<jsp:include page="header.jsp" />

<%
        PropertyResourceBundle messages = (PropertyResourceBundle) ResourceBundle.getBundle("ToolboxBundle", new Locale((String) session.getAttribute("languageReq")));

        String bc = "<a href='main.jsp'>Home</a>&nbsp;&gt;" +
                    "&nbsp;<a href='servicesManagement.jsp'>" +
                    (String) messages.getObject("ebrr.configure.capabilities") +
                    "</a>";

        String serviceName = "";

        serviceName = request.getParameter("serviceName");

%>
  <script type="text/javascript" src="jsScripts/import/gis-client-library/widgets/lib/utils/general.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/Sarissa.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/sarissa/sarissa_ieemu_xpath.js"></script>
<script type="text/javascript" src="jsScripts/import/gis-client-library/import/edit_area/edit_area_full.js"></script>
<script language="Javascript" type="text/javascript">

 editAreaLoader.init({id: "textarea",
     start_highlight: true,
     allow_resize: "both",
     allow_toggle: false,
     language: "en",
     syntax: "xml",
     toolbar: "new_document, |, search, go_to_line, |, undo, redo, |, select_font, |, syntax_selection, |, change_smooth_selection, highlight, reset_highlight, |, help",
     syntax_selection_allow: "java,html,js,php,python,xml,c,cpp,sql",
     show_line_colors: true});

 function getEditAreaValue(){
     return editAreaLoader.getValue("textarea");
 }
 function setEditAreaValue(newValue){
     editAreaLoader.setValue("textarea",newValue);
 }

 function sendXML(){
            var ajax=assignXMLHttpRequest(); 
                    ajax.open('POST','<%=response.encodeURL("manager?cmd=ebRRCapabilitiesSet&serviceName=" + serviceName)%>',true);
                    ajax.setRequestHeader('Content-Length',0);
                    ajax.setRequestHeader('connection','close');

                ajax.onreadystatechange= function() {
		if(ajax.readyState == 4) {
                      if(ajax.status!=200)
                          Ext.Msg.show({
   title:'Error',
   msg: 'Cannot set capabilities',
   buttons: Ext.Msg.OK,
   animEl: 'elId',
   icon: Ext.MessageBox.INFO
});
                      else{ Ext.Msg.show({
   title:'Status',
   msg: 'Capabilities successfully set',
   buttons: Ext.Msg.OK,
   animEl: 'elId',
   icon: Ext.MessageBox.INFO
});
    
                      }
                }

	    }
            ajax.send(editAreaLoader.getValue("textarea"));
 }

function back()
{
  window.back();
}


</script>
        <TABLE cellSpacing=0 cellPadding=0 width="100%" height="100%" align=center>
            <TBODY>
                <TR>
                    <TD class=pageBody id=main>
                        <SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>
                        <SCRIPT>addHelp("serviceCreation");</SCRIPT>
                        <TABLE cellSpacing=0 cellPadding=0 width="100%" align=center valign="top">
                            <TBODY>
                                <TR>
                                    <TD id=main align="center"> <P class=arbloc><FONT class=arttl></FONT></P>
                                        <P>
                                            <textarea id="textarea" name="textarea" rows="25" cols="150"><%
TBXService service;
ServiceManager servMan;
File capabilitiesFile;
DOMUtil util;
Document xml;

util=new DOMUtil();
servMan=ServiceManager.getInstance();
service=servMan.getService(serviceName);

capabilitiesFile=new File(service.getServiceRoot(),"AdditionalResources/EOCat/ServiceProviderInfo.xml");
xml=util.fileToDocument(capabilitiesFile);
%><%=DOMUtil.getDocumentAsString(xml) %></textarea>
                                        </P>
                                   </TD>
                                </TR>
                                <TR>
                                    <TD  align="center">
                                        <input  type="button" value="Update" onclick="sendXML()"/>
                                    </TD>
                                </TR>
                            </TBODY>
                    </TABLE></TD>
                </TR>
            

<jsp:include page="footer.jsp"/>
