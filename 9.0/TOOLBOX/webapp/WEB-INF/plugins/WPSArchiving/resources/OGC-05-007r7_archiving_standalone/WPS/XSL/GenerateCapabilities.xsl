<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="toolboxURL">http://localhost:8080/TOOLBOX</xsl:param>
  <xsl:template match="//ExternalServiceInformation/Capabilities/wps:Capabilities">
    <wps:Capabilities xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="{@service}" version="{@version}" xml:lang="{@xml:lang}" updateSequence="{@updateSequence}">
      <ows:ServiceIdentification>
        <xsl:copy-of select="ows:ServiceIdentification/ows:Title"/>
        <xsl:if test="ows:ServiceIdentification/ows:Abstract">
        	<xsl:copy-of select="ows:ServiceIdentification/ows:Abstract"/>
        </xsl:if>
		    <xsl:if test="ows:ServiceIdentification/ows:Keywords/ows:Keyword">
          <ows:Keywords>
        	  <xsl:copy-of select="ows:ServiceIdentification/ows:Keywords/ows:Keyword"/>
          </ows:Keywords>
        </xsl:if>
        <xsl:copy-of select="ows:ServiceIdentification/ows:ServiceType"/>
        <xsl:for-each select="ows:ServiceIdentification/ows:ServiceTypeVersion">
        	<xsl:copy-of select="."/>
        </xsl:for-each>
        <xsl:if test="ows:ServiceIdentification/ows:Fees">
        	<xsl:copy-of select="ows:ServiceIdentification/ows:Fees"/>
        </xsl:if>
        <xsl:if test="ows:ServiceIdentification/ows:AccessConstraints">
        	<xsl:copy-of select="ows:ServiceIdentification/ows:AccessConstraints"/>
        </xsl:if>
      </ows:ServiceIdentification>
      <ows:ServiceProvider>
      	<xsl:if test="ows:ServiceProvider/ows:ProviderName">
        	<xsl:copy-of select="ows:ServiceProvider/ows:ProviderName"/>
        </xsl:if>
        <xsl:if test="ows:ServiceProvider/ows:ProviderSite">
        	<xsl:copy-of select="ows:ServiceProvider/ows:ProviderSite"/>
        </xsl:if>
        <xsl:if test="ows:ServiceProvider/ows:ServiceContact">
        	<xsl:copy-of select="ows:ServiceProvider/ows:ServiceContact"/>
        </xsl:if>
      </ows:ServiceProvider>
      <ows:OperationsMetadata>
        <ows:Operation name="GetCapabilities">
          <ows:DCP>
            <ows:HTTP>
              <xsl:if test="ows:OperationsMetadata/ows:Operation[@name='GetCapabilities']/ows:DCP/ows:HTTP/ows:Get">
                <ows:Get xlink:href="{$toolboxURL}{ows:OperationsMetadata/ows:Operation[@name='GetCapabilities']/ows:DCP/ows:HTTP/ows:Get/@xlink:href}"/>
              </xsl:if>
              <xsl:if test="ows:OperationsMetadata/ows:Operation[@name='GetCapabilities']/ows:DCP/ows:HTTP/ows:Post">
                <ows:Post xlink:href="{$toolboxURL}{ows:OperationsMetadata/ows:Operation[@name='GetCapabilities']/ows:DCP/ows:HTTP/ows:Post/@xlink:href}"/>
              </xsl:if>
            </ows:HTTP>
          </ows:DCP>
        </ows:Operation>
        <ows:Operation name="DescribeProcess">
          <ows:DCP>
            <ows:HTTP>
              <xsl:if test="ows:OperationsMetadata/ows:Operation[@name='DescribeProcess']/ows:DCP/ows:HTTP/ows:Get">
                <ows:Get xlink:href="{$toolboxURL}{ows:OperationsMetadata/ows:Operation[@name='DescribeProcess']/ows:DCP/ows:HTTP/ows:Get/@xlink:href}"/>
              </xsl:if>
              <xsl:if test="ows:OperationsMetadata/ows:Operation[@name='DescribeProcess']/ows:DCP/ows:HTTP/ows:Post">
                <ows:Post xlink:href="{$toolboxURL}{ows:OperationsMetadata/ows:Operation[@name='DescribeProcess']/ows:DCP/ows:HTTP/ows:Post/@xlink:href}"/>
              </xsl:if>
            </ows:HTTP>
          </ows:DCP>
        </ows:Operation>
        <ows:Operation name="Execute">
          <ows:DCP>
            <ows:HTTP>
              <xsl:if test="ows:OperationsMetadata/Operation[@name='Execute']/DCP/HTTP/Get">
                <ows:Get xlink:href="{$toolboxURL}{OperationsMetadata/Operation[@name='Execute']/DCP/HTTP/Get/@xlink:href}"/>
              </xsl:if>
              <xsl:if test="ows:OperationsMetadata/ows:Operation[@name='Execute']/ows:DCP/ows:HTTP/ows:Post">
                <ows:Post xlink:href="{$toolboxURL}{ows:OperationsMetadata/ows:Operation[@name='Execute']/ows:DCP/ows:HTTP/ows:Post/@xlink:href}"/>
              </xsl:if>
            </ows:HTTP>
          </ows:DCP>
        </ows:Operation>
      </ows:OperationsMetadata>
      <wps:ProcessOfferings>
        <xsl:variable name="treeOperations" select="document(string('../DescribeTreeDump.xml'))"/>
        <xsl:for-each select="$treeOperations//tree/item/item">
          <xsl:variable name="currentDescribeDocument" select="document(string(concat('../DescribeProcess/',@text)))"/>  
          <wps:Process wps:processVersion="{$currentDescribeDocument/ProcessDescription/@wps:processVersion}">
              <ows:Identifier><xsl:value-of select="$currentDescribeDocument/ProcessDescription/ows:Identifier"/></ows:Identifier>
              <ows:Title><xsl:value-of select="$currentDescribeDocument/ProcessDescription/ows:Title"/></ows:Title>
              <ows:Abstract><xsl:value-of select="$currentDescribeDocument/ProcessDescription/ows:Abstract"/></ows:Abstract>
              <xsl:for-each select="$currentDescribeDocument/ProcessDescription/ows:Metadata">
                <xsl:copy-of select="."/>
              </xsl:for-each>
		      </wps:Process>
        </xsl:for-each>
      </wps:ProcessOfferings>
      <xsl:if test="wps:Languages">
        <xsl:copy-of select="wps:Languages"/>
      </xsl:if>
      <xsl:if test="wps:WSDL">
      	<wps:WSDL xlink:href="{$toolboxURL}{wps:WSDL/@xlink:href}"/>
      </xsl:if>
    </wps:Capabilities>
  </xsl:template>
  <xsl:template match="//ExternalServiceInformation/EPSGSupported">
  </xsl:template>
</xsl:stylesheet>
