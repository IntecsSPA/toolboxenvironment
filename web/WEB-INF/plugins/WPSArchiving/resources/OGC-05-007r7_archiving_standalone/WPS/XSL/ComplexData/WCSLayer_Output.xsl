<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:param name="geoserverUrl">http://192.168.31.4:8080/geoserver</xsl:param>
  <xsl:param name="geoserverUser">admin</xsl:param>
  <xsl:param name="geoserverPassword">geoserver</xsl:param>
  <xsl:param name="outputRepository">/home/user/data</xsl:param>
  <xsl:param name="coverageType">geotiff</xsl:param>
  
  <xsl:template match="wps:Output">
    
  </xsl:template>
  
  <xsl:template match="Schema[text()= 'complexdata/WCSLayer.xsd']">
    <setVariable name="layerName">
      <string><xsl:value-of select="../../../../ows:Identifier"></xsl:value-of>_${wpsInstance}</string>
    </setVariable>
    <setVariable name="dataStoreName">
      <string><xsl:value-of select="../../../../ows:Identifier"></xsl:value-of>Store_${wpsInstance}</string>
    </setVariable>
    <putCoverage>
      <string><xsl:value-of select="$geoserverUrl"/>/rest/folders/${dataStoreName}/layers/${layerName}/file.<xsl:value-of select="$coverageType"></xsl:value-of></string>
      <path>
        <string><xsl:value-of select="$outputRepository"></xsl:value-of><xsl:value-of select="../../../../ows:Identifier"></xsl:value-of></string>
      </path>
      <string><xsl:value-of select="$geoserverUser"/></string>
      <string><xsl:value-of select="$geoserverPassword"/></string>
    </putCoverage>
  </xsl:template>
</xsl:stylesheet>
