<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="soapNameSpace">http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap</xsl:param>	

  <xsl:template match="*">
  	<xsl:element name="local-name()" namespace="{$soapNameSpace}">
    <xsl:for-each select="child::*">
    	<xsl:choose>
    		<xsl:when test="local-name() = 'Reference'">
    			<xsl:element name="wps:Reference" namespace="">
    				 <xsl:copy-of select="./@*"/>
    			     <xsl:copy-of select="./*"/>
    			</xsl:element>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:copy-of select="."/>
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:for-each>
    </xsl:element>
  </xsl:template>
  
  
  
</xsl:stylesheet>
