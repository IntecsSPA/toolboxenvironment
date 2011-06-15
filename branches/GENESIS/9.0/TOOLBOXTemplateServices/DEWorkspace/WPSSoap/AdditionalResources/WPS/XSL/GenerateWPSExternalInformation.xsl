<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"/>
<xsl:template match="KeyValues">
    <ExternalServiceInformation xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1">
      <Capabilities>
        <wps:Capabilities xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="WPS" version="1.0.0" xml:lang="en" updateSequence="1">
          <ows:ServiceIdentification>
            <ows:Title><xsl:value-of select="serviceName"/></ows:Title>
            <ows:Abstract><xsl:value-of select="serviceAbstract"/></ows:Abstract>
            <ows:Keywords>
              <xsl:call-template name="createKeyWordsElement">
                <xsl:with-param name="keyWordsList"><xsl:value-of select="serviceKeyWords"/></xsl:with-param>
              </xsl:call-template>
            </ows:Keywords>
            <ows:ServiceType>WPS</ows:ServiceType>
            <ows:ServiceTypeVersion>1.0.0</ows:ServiceTypeVersion>
            <ows:Fees>NONE</ows:Fees>
            <ows:AccessConstraints>NONE</ows:AccessConstraints>
          </ows:ServiceIdentification>
          <ows:ServiceProvider>
            <ows:ProviderName><xsl:value-of select="ProviderName"/></ows:ProviderName>
            <ows:ProviderSite xlink:type="simple" xlink:href="{ProviderSite}"/>
            <ows:ServiceContact>
              <ows:IndividualName><xsl:value-of select="IndividualName"/></ows:IndividualName>
              <ows:PositionName><xsl:value-of select="PositionName"/></ows:PositionName>
              <ows:ContactInfo>
                <ows:Phone>
                  <ows:Voice><xsl:value-of select="PhoneVoice"/></ows:Voice>
                  <ows:Facsimile><xsl:value-of select="PhoneFacsimile"/></ows:Facsimile>
                </ows:Phone>
                <ows:Address>
                  <ows:DeliveryPoint><xsl:value-of select="DeliveryPoint"/></ows:DeliveryPoint>
                  <ows:City><xsl:value-of select="City"/></ows:City>
                  <ows:AdministrativeArea><xsl:value-of select="AdministrativeArea"/></ows:AdministrativeArea>
                  <ows:PostalCode><xsl:value-of select="PostalCode"/></ows:PostalCode>
                  <ows:Country><xsl:value-of select="Country"/></ows:Country>
                  <ows:ElectronicMailAddress><xsl:value-of select="ElectronicMailAddress"/></ows:ElectronicMailAddress>
                </ows:Address>
              </ows:ContactInfo>
            </ows:ServiceContact>
          </ows:ServiceProvider>
          <ows:OperationsMetadata>
            <ows:Operation name="GetCapabilities">
              <ows:DCP>
                <ows:HTTP>
                  <ows:Get xlink:type="simple" xlink:href="{concat(concat('/services/',serviceName),'?')}"/>
                  <ows:Post xlink:type="simple" xlink:href="{concat(concat('/services/',serviceName),'?')}"/>
                </ows:HTTP>
              </ows:DCP>
            </ows:Operation>
            <ows:Operation name="DescribeProcess">
              <ows:DCP>
                <ows:HTTP>
                  <ows:Get xlink:type="simple" xlink:href="{concat(concat('/services/',serviceName),'?')}"/>
                  <ows:Post xlink:type="simple" xlink:href="{concat(concat('/services/',serviceName),'?')}"/>
                </ows:HTTP>
              </ows:DCP>
            </ows:Operation>
            <ows:Operation name="Execute">
              <ows:DCP>
                <ows:HTTP>
                  <ows:Post xlink:type="simple" xlink:href="{concat(concat('/services/',serviceName),'?')}"/>
                </ows:HTTP>
              </ows:DCP>
            </ows:Operation>
          </ows:OperationsMetadata>
          <wps:Languages>
            <wps:Default>
              <ows:Language>en</ows:Language>
            </wps:Default>
            <wps:Supported>
              <ows:Language>en</ows:Language>
            </wps:Supported>
          </wps:Languages>
          <wps:WSDL xlink:href="/WSDL/{serviceName}/{serviceName}.wsdl"/>
        </wps:Capabilities>
      </Capabilities>
      <EPSGSupported>
        <AllowedValues>
          <Value>4236</Value>
          <Value>4258</Value>
        </AllowedValues>
      </EPSGSupported>
    </ExternalServiceInformation>
  </xsl:template>

<xsl:template name="createKeyWordsElement" xmlns:ows="http://www.opengis.net/ows/1.1">
  <xsl:param name="keyWordsList"/>
      <xsl:variable name="listSeparator">,</xsl:variable>
    	<xsl:variable name="firstKeyWord" select="substring-before($keyWordsList, $listSeparator)"/>
    	<xsl:variable name="remainingKeyWords" select="substring-after($keyWordsList, $listSeparator)"/>
      <xsl:if test="$firstKeyWord!=''">
          <ows:Keyword><xsl:value-of select="$firstKeyWord"/></ows:Keyword>
      </xsl:if>
      <xsl:if test="( $firstKeyWord='') and ( $remainingKeyWords='' ) and ( $keyWordsList!='' )">
          <ows:Keyword><xsl:value-of select="$keyWordsList"/></ows:Keyword>
      </xsl:if>
      <xsl:if test="$remainingKeyWords!=''">
            <xsl:call-template name="createKeyWordsElement">
            	  <xsl:with-param name="keyWordsList" select="$remainingKeyWords"/>
          	</xsl:call-template>
        </xsl:if> 
</xsl:template>
</xsl:stylesheet>