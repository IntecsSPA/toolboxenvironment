<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ows="http://www.opengis.net/ows/1.1">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:param name="wpsSchemaLocation">test</xsl:param>
 <xsl:param name="wpsExecuteSchemaFolder">test2</xsl:param>
	<xsl:template match="tree/item">
		<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" elementFormDefault="unqualified"
	         targetNamespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap"
	        version="0.1" xml:lang="en">
	<annotation>
		<appinfo>$Id: WPSSoapAll.xsd  $</appinfo>
		<documentation>
			<description></description>
			<copyright></copyright>
		</documentation>
	</annotation>
  	<import schemaLocation="{$wpsSchemaLocation}" namespace="http://www.opengis.net/wps/1.0.0" />
	<!-- ==============================================================
		includes -->
  <xsl:for-each select="item">
    <include schemaLocation="{$wpsExecuteSchemaFolder}/{substring-before(substring-after(@text, '_'), '.')}.xsd"/> 
  </xsl:for-each>
		<!-- ============================================================== -->


 


</schema>
	</xsl:template>
</xsl:stylesheet>
