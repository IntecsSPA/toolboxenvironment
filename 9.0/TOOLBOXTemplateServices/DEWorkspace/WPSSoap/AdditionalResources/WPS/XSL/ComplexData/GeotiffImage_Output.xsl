<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">  <xsl:param name="geoserverUrl">http://localhost:8080/geoserver</xsl:param>
  <xsl:param name="outputRepository">/home/user/data</xsl:param>
  <xsl:param name="outputServlet">http://localhost:8080/TOOLBOX/manager?cmd=GetServiceResource&amp;serviceName=WPSExample&amp;relativePath=/output/</xsl:param>

  
  <xsl:template match="MimeType[text()= 'image/geotiff']">
    <setVariable name="newfileName">
      <string><xsl:value-of select="../../../../ows:Identifier"/>_${wpsProcessingInstance}.tiff</string>
    </setVariable>
    <command>
        <string>mv <xsl:value-of select="$outputRepository"/><xsl:value-of select="../../../../ows:Identifier"/> <xsl:value-of select="concat(' ',$outputRepository)"/>${newfileName}</string>
    </command>
    <setVariable name="particularReferenceIdentifier">
      <string>${particularReferenceIdentifier}<xsl:value-of select="../../../../ows:Identifier"/>,</string>
    </setVariable>
    <setVariable name="particularReferenceURL">
      <string>${particularReferenceURL}<xsl:value-of select="$outputServlet"/>${newfileName},</string>
    </setVariable>
  </xsl:template>
</xsl:stylesheet>
