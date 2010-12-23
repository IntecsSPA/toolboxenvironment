<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:gml="http://www.opengis.net/gml" xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/>
  <xsl:template match="/SoapFault">
         <xsl:copy-of select="*"></xsl:copy-of>
  </xsl:template>
  <xsl:template match="/csw:GetRecordsResponse">
    <response>
        <retrievedData>
            <xsl:apply-templates select="csw:SearchResults/wrs:ExtrinsicObject[@objectType='urn:ogc:def:ebRIM-ObjectType:OGC:MetadataInformation']"/>
        </retrievedData>
        <!--xsl:copy-of select="eoli:cursor"/>
        <xsl:copy-of select="eoli:hits"/>
        <xsl:copy-of select="eoli:status"/-->
    </response> 
  </xsl:template>
  <xsl:template match="wrs:ExtrinsicObject">
    <xsl:variable name="eopId" select='@id'/>
    <Metadata>
        <xsl:apply-templates select="rim:Slot"></xsl:apply-templates>
        <xsl:apply-templates select="../rim:Association[@sourceObject=$eopId]"></xsl:apply-templates>
    </Metadata>
  </xsl:template>

    <xsl:template match="rim:Slot">
    <xsl:variable name="nameString" select="substring-after(@name,'OGC-06-131:')"/>
    <!--xsl:text> </xsl:text><xsl:value-of select="$nameString"/><xsl:text> </xsl:text-->
     <xsl:variable name="name">
      <xsl:choose>
          <xsl:when test="$nameString = 'fileName'"><xsl:value-of select="$nameString"/>_<xsl:value-of select="../rim:Name/rim:LocalizedString/@value"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="$nameString"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
      <xsl:element name="{$name}">
        <xsl:if test="(@slotType='string') or (@slotType='int') or (@slotType='dateTime')">
            <xsl:value-of select="rim:ValueList/rim:Value[1]"/>
        </xsl:if>
        <xsl:if test="(@slotType='geometry')">
            <xsl:value-of select=".//gml:posList"/>
        </xsl:if>
      </xsl:element>
    </xsl:template>

    <xsl:template match="rim:Association">
    <xsl:variable name="targetObject" select='@targetObject'/>
        <xsl:apply-templates select="//wrs:ExtrinsicObject[@id=$targetObject]/rim:Slot"></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template name="extactNameProperty">
      <xsl:parm name="stringProperty"/>
        <xsl:if test="contains($stringProperty, 'htpp:')">
            <xsl:call-template name="extractLastName">
                <xsl:within-param name="processingString" select="$stringProperty">
                <xsl:within-param name="stringSeparator">/</xsl:within-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="contains($stringProperty, 'urn:')">
            <xsl:call-template name="extractLastName">
                <xsl:within-param name="processingString" select="$stringProperty">
                <xsl:within-param name="stringSeparator">:</xsl:within-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="extractLastName">
      <xsl:parm name="processingString"/>
      <xsl:parm name="stringSeparator"/>
      <xsl:variable name="firstName" select="subas"/>
    </xsl:template>
</xsl:stylesheet>