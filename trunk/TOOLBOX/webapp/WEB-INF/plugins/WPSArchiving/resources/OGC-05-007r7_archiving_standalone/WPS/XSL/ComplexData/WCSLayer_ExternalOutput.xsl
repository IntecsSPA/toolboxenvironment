<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:param name="geoserverUrl">http://localhost:8080/geoserver</xsl:param>
  <xsl:param name="outputRepository">/home/user/data</xsl:param>
  <xsl:param name="coverageType">geotiff</xsl:param>
  <!-- xsl:template match="ComplexOutput/Default/Format/Schema[text()= 'complexdata/WCSLayer.xsd']">
		  <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
		  if [ -f "$OUTPUT_REPOSITORY$<xsl:value-of select="$wcsIdentifier"/>" ]; then 
		       export ComplexOutputWCSComplexDataFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>.xml"
		       document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:ComplexData xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;WCSLayer  xmlns=\"complexdata/WCSLayer.xsd\"&gt;&lt;wcsURL&gt;<xsl:value-of select="$geoserverUrl"/>&lt;/wcsURL&gt;&lt;wcsLayerName&gt;<xsl:value-of select="$wcsIdentifier"/>&lt;/wcsLayerName&gt;&lt;/WCSLayer&gt;&lt;/wps:ComplexData&gt;"
		      echo $document >> $ComplexOutputWCSComplexDataFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" generated."
		  else 
		      export ComplexOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>NULL.xml"
		      echo "Output "$<xsl:value-of select="$wcsIdentifier"/>" is NULL." >> $ComplexOutputNULLFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" not generated."
		  fi 
  </xsl:template> -->
</xsl:stylesheet>
