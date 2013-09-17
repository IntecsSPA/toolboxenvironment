<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:param name="geoserverUrl">http://192.168.31.4:8080/geoserver</xsl:param>
  <xsl:param name="geoserverUser">admin</xsl:param>
  <xsl:param name="geoserverPassword">geoserver</xsl:param>
  <xsl:param name="outputRepository">/home/user/data</xsl:param>
  <xsl:param name="coverageType">geotiff</xsl:param>
  
  <!--xsl:template match="wps:Output">
    
  </xsl:template>
  
  <xsl:template match="Schema[text()= 'complexdata/WCSLayer.xsd']">
# *<xsl:value-of select="ows:Identifier"/>_WCS: Name of file to be deployed in the Web Coverage Service (WCS) ("<xsl:value-of select="ows:Identifier"/>" Compex Data Output).
#      (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)


# *<xsl:value-of select="ows:Identifier"/>_Schema: <xsl:value-of select="ows:Identifier"/> Output Schema.
#      (Schema Supported:  <xsl:for-each select="ComplexOutput/Supported/Format/Schema"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)  
  </xsl:template-->
  
</xsl:stylesheet>
