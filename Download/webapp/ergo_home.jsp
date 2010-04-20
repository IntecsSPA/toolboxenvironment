<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="errorPage.jsp"%>
<jsp:include page="header.jsp" />
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center> 
  <TBODY> 
    <TR> 
    <%
    String bc ="<a href='main.jsp'>Home</a>&nbsp;";
%>
      <TD class=pageBody id=main>

	<SCRIPT>addBreadCrumb("<%=bc%>");</SCRIPT>

	<h2>Ergo project context</h2>
<p style="text-align: center;"><img align="center" src="images/catalogueTest.jpeg" alt="" ></p>
<p>Intecs S.p.A.- an Italian IT company
specialized in space system development - is contributing by providing
a test framework (based on the OGC TEAM engine) and by integrating the
registry in the SSE Toolbox package.</p>
<p>The Toolbox is a configurable application
that helps the Service Provider to easily convert its legacy services
into SOAP based services. It is usually used to integrate different
kind of services and/or catalogues providing SOAP interfaces to them.
Different kinds of back-end communication systems are foreseen:
<ul>
    <li>File exchange</li>
    <li>File Transfer Protocol (FTP)</li>
    <li>Hyper Text Transfer Protocol</li>
    <li>Java API support</li>
    <li>Script support</li>
    <li>JDBC</li>
    <li>SOAP</li>
    <li>Email</li>
</ul></p>
<p>Furthermore the Toolbox provides an easy
mechanism to convert the incoming XML files into other files (based on
XML or on a proprietary format) or data structures suitable to be used
for the communication with the back-end systems.
The Toolbox supports both synchronous and an asynchronous communication
mechanism:
</p>
<ul>
<li>
Synchronous: in this solution, the SSE Portal is a SOAP client and the
Toolbox acts only as a SOAP server. It only responds to SOAP requests.
The XML response is sent back to the client during the same HTTP
exchange.</li>
<li>
Asynchronous: in this solution, the service sends a SOAP message back
to the client when a result is ready. Thus the Toolbox implements a
SOAP server as well as a SOAP client. The Toolbox uses the
WS-Addressing protocol.</li>
</ul>
<p></p>
<p>The approach used to build a configurable
application that will help the Service Provider to easily convert its
service into a SOAP service is based on XML scripting. In order
to convert a legacy Service into a SOAP service the Service Provider has to provide a number of
XML scripting files (depending on the number of operations supported by such services
and on the related communication mechanism). These files will describe
the operations needed to complete a request coming from the client.
The configuration of the Toolbox, as well as the configuration and
monitoring of the services, occur through the Toolbox administrator
Web application coming with the Toolbox.</p>
<p>Toolbox is composed by two separate applications: the
Toolbox RE (runtime
environment) and the Toolbox DE (Development Environment).
</p>
<p>The Toolbox RE is the web application
with functionalities such as:
</p>
<ul>
<li>Native support for the HMA Ordering interfaces</li>
<li>Native support for the OGC Catalogue Services
Specification 2.0 Extension Package for ebRIM (ISO/TS 15000-3)
Application Profile interface (OGC-06-131r4)</li>
<li>Native support for the OGC Cataloguing of ISO
Metadata (CIM) Using the ebRIM profile of CS-W interface (OGC-07-038)</li>
<li>Service creation and lifecycle handling</li>
<li>Monitoring of services requests: it is possible to trace the
lifecycle of a request , display the incoming/outgoing messages and
inspect the service execution by means of some graphical flow.</li>
<li>Testing tools for both standard and catalogue services</li>
<li>Graphical installation procedure with the possibility to upgrade an existing installation without loosing any already deployed service..</li>
<li>Integrated Buddata ebXML Registry instance to provide catalogue functionalities</li>
</ul>
<p>In Toolbox RE it is possible to instantiate the new
Catalogue Service following two different approaches: gateway and
stand-alone.
The former allows the user to link an existing catalogue providing
the Toolbox Catalogue Service logic in the form of XML scripting. In
this case the development approach is similar to developing a Toolbox
service.
The latter approach provides the functionalities to create a local
Catalogue Service without the need of an already existing catalogue and
without the need of providing any form of logic. </p>
<p>
Catalogue testing functionalities allows user perform searches over data directly from
inside TOOLBOX, allowing the user to select search areaa within a map , overimpose catalogue search results over this and retrieve detailed information about these. The above picture shows an example of catalogue search.</p>
<p>
The Toolbox DE is an Eclipse plugin, written in Java that extends some
already existing functionality of the IDE itself and some other
exported by the WTP plugin. It can be used to create a TOOLBOX service and write XML scripts that builds its logic.
It provides built-in features like graphical vs textual visualization, tag suggestion, online validation.
It supports the following functionalities:
</p>
<ul>
<li>Toolbox services creation through wizards</li>
<li>Direct deployment of services into Toolbox RE</li>
<li>Native support for the HMA Ordering interfaces</li>
<li>Native support for the OGC Catalogue Services
Specification 2.0 Extension Package for ebRIM (ISO/TS 15000-3)
Application Profile interface (OGC-06-131r4)</li>
<li>Native support for the OGC Cataloguing of ISO
Metadata (CIM) Using the ebRIM profile of CS-W interface (OGC-07-038)</li>
<li>Toolbox Script editor: the plug-in allows
building a Toolbox script</li>
<li>Test functionalities: the plug-in allows executing a service operation directly into the specified Toolbox RE instance.
</li>
<li>Debug: the plug-in allows debugging a service operation directly into the specified Toolbox RE instance. Typical debugging functionalities are provided: variable visualization, breakpoint management, scripts execution visualization and many more.  </li>
<li>Automatic upgrade via remote server.</li>
</ul>
<p></p>

<p></p>


 </TD>
    </TR> 
  </TBODY> 
</TABLE> 
<jsp:include page="footer.jsp"/>
