<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="text"/>
  <xsl:param name="processingName">RasterClipBBox</xsl:param>
  <xsl:param name="geoserverUrl">http://localhost:8080/geoserver</xsl:param>
  <!-- xsl:param name="complexDataFolderPath">complexDataFolderPath</xsl:param> -->
 
  <!-- INCLUDE SECTION-->
  <!--xsl:include href="ComplexData/WCSLayer_ExternalOutput.xsl"/-->
  <!-- -->

  
  <xsl:template match="ProcessDescription">
 # ------------------------------  OUTPUT MANAGER GRASS SCRIPT ------------------------------------------------------------------------------------------------------------------------------ 

    <!-- Post Processing BBOX e Literal Output--> 
     <xsl:for-each select="ProcessOutputs/Output">
        <xsl:variable name="currentIdentifier" select="ows:Identifier"/>  
        <xsl:if test="LiteralOutput">

          if [ -n "$<xsl:value-of select="$currentIdentifier"/>" ] &amp;&amp;  [ "$<xsl:value-of select="$currentIdentifier"/>" != "<xsl:value-of select="$currentIdentifier"/>" ]
          then
                  export LITERALOutputFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$currentIdentifier"/>.xml"
                  document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:Data xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wps:LiteralData&gt;"$<xsl:value-of select="$currentIdentifier"/>"&lt;/wps:LiteralData&gt;&lt;/wps:Data&gt;"
                  echo $document >> $LITERALOutputFile
                  echo "LiteralOutput "$<xsl:value-of select="$currentIdentifier"/>" generated."
          else
                  export LITERALOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$currentIdentifier"/>NULL.xml"
                  document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:Data xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wps:LiteralData&gt;NaN&lt;/wps:LiteralData&gt;&lt;/wps:Data&gt;"
                  echo $document >> $LITERALOutputNULLFile
                  echo "LiteralOutput "$<xsl:value-of select="$currentIdentifier"/>" not generated."
          fi
       </xsl:if>
      <xsl:if test="BoundingBoxOutput">
          if [ -n "$<xsl:value-of select="$currentIdentifier"/>_S" ]
          then
              		export BBOXOutputFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$currentIdentifier"/>.xml"
                  document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:Data xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wps:BoundingBoxData xmlns:ows=\"http://www.opengis.net/ows/1.1\"&gt;&lt;ows:LowerCorner&gt;"$<xsl:value-of select="$currentIdentifier"/>_S" "$<xsl:value-of select="$currentIdentifier"/>_W"&lt;/ows:LowerCorner&gt;&lt;ows:UpperCorner&gt;"$<xsl:value-of select="$currentIdentifier"/>_N" "$<xsl:value-of select="$currentIdentifier"/>_E"&lt;/ows:UpperCorner&gt;&lt;/wps:BoundingBoxData&gt;&lt;/wps:Data&gt;"
                  echo $document >> $BBOXOutputFile
                  echo "BBOXOutput "$<xsl:value-of select="$currentIdentifier"/>" generated."
          else
                  export BBOXOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$currentIdentifier"/>NULL.xml"
                  document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:Data xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wps:BoundingBoxData xmlns:ows=\"http://www.opengis.net/ows/1.1\"&gt;&lt;ows:LowerCorner&gt;NaN NaN&lt;/ows:LowerCorner&gt;&lt;ows:UpperCorner&gt;NaN NaN&lt;/ows:UpperCorner&gt;&lt;/wps:BoundingBoxData&gt;&lt;/wps:Data&gt;"
                  echo $document >> $BBOXOutputNULLFile
                   echo "BBOXOutput "$<xsl:value-of select="$currentIdentifier"/>"not generated."
          fi
        </xsl:if>
        <xsl:if test="ComplexOutput">
          <xsl:choose>
            <xsl:when test="ComplexOutput/Default/Format/Schema">
              <xsl:apply-templates select="ComplexOutput/Default/Format/Schema"/>
            </xsl:when>
            <xsl:otherwise>
              if [ -f "$OUTPUT_REPOSITORY$<xsl:value-of select="$currentIdentifier"/>" ]; then 
                  echo "ComplexOutput "$<xsl:value-of select="$currentIdentifier"/>" generated."
              else 
                  export ComplexOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$currentIdentifier"/>NULL.xml"
                  echo "Output "$<xsl:value-of select="$currentIdentifier"/>" is NULL." >> $ComplexOutputNULLFile
                  echo "ComplexOutput "$<xsl:value-of select="$currentIdentifier"/>" not generated."
              fi 
            </xsl:otherwise>  
          </xsl:choose>    
          
          <!--xsl:apply-templates select="ComplexOutput/Default/Format/Encoding"/>
          <xsl:apply-templates select="ComplexOutput/Default/Format/MimeType"/-->
        </xsl:if>
        
     </xsl:for-each>
# ------------------------------  END OUTPUT MANAGER GRASS SCRIPT ----------------------------------------------------------------------------------------------------------------------------
  </xsl:template>
  
 
  <!--xsl:template match="ComplexOutput/Default/Format/Schema[text()= 'complexdata/WCSLayer.xsd']">
		  <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
		  if [ -f "$OUTPUT_REPOSITORY$<xsl:value-of select="$wcsIdentifier"/>" ]; then 
		       export ComplexOutputWCSComplexDataFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>".xml
		       document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:ComplexData xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;WCSLayer  xmlns=\"complexdata/WCSLayer.xsd\"&gt;&lt;wcsURL&gt;<xsl:value-of select="$geoserverUrl"/>&lt;/wcsURL&gt;&lt;wcsLayerName&gt;<xsl:value-of select="$wcsIdentifier"/>&lt;/wcsLayerName&gt;&lt;/WCSLayer&gt;&lt;/wps:ComplexData&gt;"
		      echo $document >> $ComplexOutputWCSComplexDataFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" generated."
		  else 
		      export ComplexOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>NULL.xml"
		      echo "Output "$<xsl:value-of select="$wcsIdentifier"/>" is NULL." >> $ComplexOutputNULLFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" not generated."
		  fi 
  </xsl:template-->

<xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']">
		  <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
		  if [ -f "$OUTPUT_REPOSITORY$<xsl:value-of select="$wcsIdentifier"/>" ]; then 
		       export ComplexOutputWCSComplexDataFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>".xml
		       document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:ComplexData xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wcs-wmsLayer  xmlns=\"http://toolbox.pisa.intecs.it/schemas/wps/complexData/WCS-WMSLayer\"&gt;&lt;owsUrl&gt;<xsl:value-of select="$geoserverUrl"/>&lt;/owsUrl&gt;&lt;owsLayerName&gt;<xsl:value-of select="$wcsIdentifier"/>&lt;/owsLayerName&gt;&lt;/wcs-wmsLayer&gt;&lt;/wps:ComplexData&gt;"
		      echo $document >> $ComplexOutputWCSComplexDataFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" generated."
		  else 
		      export ComplexOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wcsIdentifier"/>NULL.xml"
		      echo "Output "$<xsl:value-of select="$wcsIdentifier"/>" is NULL." >> $ComplexOutputNULLFile
		      echo "ComplexOutput "$<xsl:value-of select="$wcsIdentifier"/>" not generated."
		  fi 
  </xsl:template>


<xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']">
		  <xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/>
		  if [ -f "$OUTPUT_REPOSITORY$<xsl:value-of select="$wfsIdentifier"/>.zip" ]; then 
		       export ComplexOutputWFSComplexDataFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wfsIdentifier"/>".xml
		       document="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;wps:ComplexData xmlns:wps=\"http://www.opengis.net/wps/1.0.0\"&gt;&lt;wfs-wmsLayer  xmlns=\"http://toolbox.pisa.intecs.it/schemas/wps/complexData/WFS-WMSLayer\"&gt;&lt;owsUrl&gt;<xsl:value-of select="$geoserverUrl"/>&lt;/owsUrl&gt;&lt;owsLayerName&gt;<xsl:value-of select="$wfsIdentifier"/>&lt;/owsLayerName&gt;&lt;/wfs-wmsLayer&gt;&lt;/wps:ComplexData&gt;"
		      echo $document >> $ComplexOutputWFSComplexDataFile
		      echo "ComplexOutput "$<xsl:value-of select="$wfsIdentifier"/>" generated."
		  else 
		      export ComplexOutputNULLFile=$OUTPUT_REPOSITORY"<xsl:value-of select="$wfsIdentifier"/>NULL.xml"
		      echo "Output "$<xsl:value-of select="$wfsIdentifier"/>" is NULL." >> $ComplexOutputNULLFile
		      echo "ComplexOutput "$<xsl:value-of select="$wfsIdentifier"/>" not generated."
		  fi 
  </xsl:template>
  
</xsl:stylesheet>
