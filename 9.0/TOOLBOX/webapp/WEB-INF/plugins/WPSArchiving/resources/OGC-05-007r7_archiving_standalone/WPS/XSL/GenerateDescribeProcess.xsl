<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:param name="toolboxURL">http://localhost:8080/TOOLBOX/WSDL/</xsl:param>
  <xsl:template match="/wps:DescribeProcess">
    <wps:ProcessDescriptions xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="WPS" version="1.0.0" xml:lang="en">
      <xsl:apply-templates select="ows:Identifier"/>
    </wps:ProcessDescriptions>
  </xsl:template>
  <xsl:template match="ows:Identifier">
    <!--  ProcessDescription storeSupported="true" statusSupported="false"-->
      <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', ., '.xml')))"/>
      <xsl:copy-of select="$processInformation//ProcessDescription"/>
      <!--  xsl:attribute name="wps:processVersion">
        <xsl:value-of select="$processInformation/ProcessDescription/@wps:processVersion"/>
      </xsl:attribute>
      <xsl:attribute name="storeSupported">
        <xsl:value-of select="$processInformation/ProcessDescription/@storeSupported"/>
      </xsl:attribute>
      <xsl:attribute name="statusSupported">
        <xsl:value-of select="$processInformation/ProcessDescription/@statusSupported"/>
      </xsl:attribute>
      <xsl:copy-of select="$processInformation/ProcessDescription/ows:Identifier"/>
      <xsl:copy-of select="$processInformation/ProcessDescription/ows:Title"/>
      <xsl:if test="$processInformation/ProcessDescription/ows:Abstract">
        <xsl:copy-of select="$processInformation/ProcessDescription/ows:Abstract"/>
      </xsl:if>
      <xsl:for-each select="$processInformation/ProcessDescription/ows:Metadata">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:if test="$processInformation/ProcessDescription/wps:Profile">
        <xsl:copy-of select="$processInformation/ProcessDescription/wps:Profile"/>
      </xsl:if>
      <xsl:if test="$processInformation/ProcessDescription/wps:WSDL">
        <xsl:copy-of select="$processInformation/ProcessDescription/wps:WSDL"/>
      </xsl:if>
      <xsl:apply-templates select="$processInformation/ProcessDescription/DataInputs"/>
      <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs"/>
    </ProcessDescription-->
  </xsl:template>
  <!--  xsl:template match="DataInputs">
    <DataInputs>
      <xsl:for-each select="Input">
        <Input minOccurs="{./@minOccurs}" maxOccurs="{./@maxOccurs}">
          <ows:Identifier>
            <xsl:value-of select="ows:Identifier/text()"/>
          </ows:Identifier>
          <ows:Title>
            <xsl:value-of select="ows:Title/text()"/>
          </ows:Title>
          <ows:Abstract>
            <xsl:value-of select="ows:Abstract/text()"/>
          </ows:Abstract>
          <xsl:if test="ComplexData">
            <ComplexData maximumMegabytes="{ComplexData/@maximumMegabytes}">
              <Default>
                <xsl:apply-templates select="ComplexData/Default/Format"/>
              </Default>
              <Supported>
                <xsl:apply-templates select="ComplexData/Supported/Format"/>
              </Supported>
            </ComplexData>
          </xsl:if>
          <xsl:if test="LiteralData">
            <xsl:copy-of select="LiteralData"/>
          </xsl:if>
          <xsl:if test="BoundingBoxData">
            <xsl:copy-of select="BoundingBoxData"/>
          </xsl:if>
        </Input>
      </xsl:for-each>
    </DataInputs>
  </xsl:template>
  <xsl:template match="Format">
    <Format xmlns:wps="http://www.opengis.net/wps/1.0.0">
      <MimeType>
        <xsl:value-of select="MimeType/text()"/>
      </MimeType>
      <Encoding>
        <xsl:value-of select="Encoding/text()"/>
      </Encoding>
      <Schema>
        <xsl:value-of select="$toolboxURL"/>
        <xsl:value-of select="Schema/text()"/>
      </Schema>
    </Format>
  </xsl:template>
  <xsl:template match="ProcessOutputs">
    <ProcessOutputs xmlns:ows="http://www.opengis.net/ows/1.1">
      <xsl:for-each select="Output">
        <Output>
          <ows:Identifier>
            <xsl:value-of select="ows:Identifier/text()"/>
          </ows:Identifier>
          <ows:Title>
            <xsl:value-of select="ows:Title/text()"/>
          </ows:Title>
          <ows:Abstract>
            <xsl:value-of select="ows:Abstract/text()"/>
          </ows:Abstract>
          <xsl:if test="ComplexOutput">
            <ComplexOutput>
              <Default>
                <xsl:apply-templates select="ComplexOutput/Default/Format"/>
              </Default>
              <Supported>
                <xsl:apply-templates select="ComplexOutput/Supported/Format"/>
              </Supported>
            </ComplexOutput>
          </xsl:if>
          <xsl:if test="LiteralOutput">
            <xsl:copy-of select="LiteralOutput"/>
          </xsl:if>
          <xsl:if test="BoundingBoxOutput">
            <xsl:copy-of select="BoundingBoxOutput"/>
          </xsl:if>
        </Output>
      </xsl:for-each>
    </ProcessOutputs>
  </xsl:template>-->
</xsl:stylesheet>
