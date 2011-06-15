<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="outputRepository">/home/user/data</xsl:param>
  <xsl:param name="processName">RasterDiff</xsl:param>
 <xsl:param name="serviceName">TestService</xsl:param>
  <xsl:param name="dateTime">2010_04_20_08_30_24</xsl:param>
  
  <!-- INCLUDE SECTION-->
  <!--xsl:include href="ComplexData/WCSLayer_Output.xsl"/>
  <xsl:include href="ComplexData/WMSLayer_Output.xsl"/>
  <xsl:include href="ComplexData/GeotiffImage_Output.xsl"/-->
  <!-- -->
  <xsl:template match="*">
    <xsl:if test="(local-name() != 'Schema') and (local-name() != 'Encoding') and (local-name() != 'MimeType')">
      <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName,'.xml')))"/> 
      <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
        <xsl:for-each select="child::*">
          <xsl:variable name="inputIdentifier" select="local-name()"/>
          <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
            <xsl:call-template name="ComplexOutputManager">
              <xsl:with-param name="requestData" select="."/>
              <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput">
            <!--xsl:call-template name="LiteralOutputManager">
              <xsl:with-param name="requestData" select="."/>
              <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]"/>
            </xsl:call-template-->
          </xsl:if>
          <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/BoundingBoxOutput">
            <!--xsl:call-template name="BoundingBoxOutputManager">
              <xsl:with-param name="requestData" select="."/>
              <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]"/>
            </xsl:call-template-->
          </xsl:if>
        </xsl:for-each>
        <!--setVariable name="BoundingBoxOutputElement">
          <string>${BoundingBoxOutputElement}*</string>
        </setVariable>
        <setVariable name="LiteralOutputElement">
          <string>${LiteralOutputElement}*</string>
        </setVariable-->
      </sequence>
    </xsl:if>
      <!--xsl:choose>
        <xsl:when test="//wps:ResponseForm">
          <xsl:apply-templates select="//wps:ResponseForm/wps:ResponseDocument"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="//wps:RawDataOutput">
              <setVariable name="ErrorType">
                <string>NoApplicableCode</string>
              </setVariable>
              <setVariable name="ErrorMessage">
                <string>"wps:RawDataOutput" tag not supported.</string>
              </setVariable>
            </xsl:when>
            <xsl:otherwise>
              
              <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose-->
</xsl:template>

  <xsl:template name="ComplexOutputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
                
    <xsl:variable name="identifierRequest" select="local-name($requestData)"/>
    <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName,'.xml')))"/>

    <xsl:choose>
      <xsl:when test="$requestData/@schema">
        <xsl:variable name="schemaRequest" select="$requestData/@schema"/>
        <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Supported/Format/Schema[text()= $schemaRequest]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$requestData/@mimeType">
            <xsl:variable name="mimeTypeRequest" select="./@mimeType"/>
            <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Supported/Format/MimeType[text()= $mimeTypeRequest]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$requestData/@encoding">
                <xsl:variable name="encodingRequest" select="$requestData/@encoding"/>
                <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Supported/Format/Encoding[text()= $encodingRequest]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Default/Format/Schema"/>
                <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Default/Format/Encoding"/>
                <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $identifierRequest]/ComplexOutput/Default/Format/MimeType"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="LiteralOutputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
    <!--xsl:variable name="identifierRequest" select="local-name($requestData)"/>
    <variable name="{string(concat($identifierRequest,'LiteralOutput'))}">
      <loadFile>
        <string>
          <xsl:value-of select="$outputRepository"/><xsl:value-of select="$identifierRequest"/>
        </string>
      </loadFile>
    </variable>
    <setVariable name="LiteralOutputElement">
      <string>${LiteralOutputElement}*${<xsl:value-of select="string(concat($identifierRequest,'_LiteralOutput.xml'))"/>}</string>
    </setVariable-->
  </xsl:template>

  <xsl:template name="BoundingBoxOutputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
    <!--xsl:variable name="identifierRequest" select="local-name($requestData)"/>
    <variable name="{string(concat($identifierRequest,'BoundingBoxOutput'))}">
      <loadFile>
        <string>
          <xsl:value-of select="$outputRepository"/><xsl:value-of select="$identifierRequest"/>
        </string>
      </loadFile>
    </variable>
    <setVariable name="BoundingBoxOutputElement">
      <string>${BoundingBoxOutputElement}*${<xsl:value-of select="string(concat($identifierRequest,'_BoundingBoxOutput.xml'))"></xsl:value-of>}</string>
    </setVariable-->
  </xsl:template>



<!-- *********************************** Template for Complex Output Imported *********************************************************** -->
<!-- Currently the include XSL file is not supported-->

<xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']">

  <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
<geoserverDataDeploy xmlns="http://pisa007.pisa.intecs.it/toolbox/GISPlugin">
      <data type="geotiff">concat( $outputRepository, '/', $wcsIdentifier )</data>
      <deployName><xsl:value-of select="concat( $wcsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/></deployName>  
  </geoserverDataDeploy>
 
</xsl:template>



<xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']">

<xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/>

<zip file="{concat( $outputRepository, '/', $wfsIdentifier, '.zip')}">
		<dir>
	    	<string><xsl:value-of select="concat( $outputRepository, '/', $wfsIdentifier )"/></string>
		</dir>
</zip>

<zip file="{concat( $outputRepository, '/', $wfsIdentifier)}">
		<dir>
	    	<string><xsl:value-of select="concat( $outputRepository, '/', $wfsIdentifier )"/></string>
		</dir>
</zip>

  <geoserverDataDeploy xmlns="http://pisa007.pisa.intecs.it/toolbox/GISPlugin">
      <data type="shp"><xsl:value-of select="concat( $outputRepository, '/', $wfsIdentifier, '.zip')"/></data>
      <deployName><xsl:value-of select="concat( $wfsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/></deployName>  
  </geoserverDataDeploy>

</xsl:template>

<!-- *********************************************************************************************************************************** -->



</xsl:stylesheet>
