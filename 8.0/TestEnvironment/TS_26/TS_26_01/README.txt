The purpose of this test is to check that application variables can be
used freely inside your scripts. Reference of variable from attributes
is also checked.

To add an application variable open the file
<TOMCAT_ROOT>/conf/server.xml and add the following lines:
put the line  <Environment name="fileName" type="java.lang.String" value="/home/toolbox/TestEnvironment/Responses/Order_response_AAOAOA.xml"/> as child of the node <GlobalNamingResources>
put <Context path="/TOOLBOX" docBase="TOOLBOX">
	     <ResourceLink name="test" global="fileName" type="java.lang.String"/>
    </Context>

just before the end tag </Host>

Restart Tomcat
