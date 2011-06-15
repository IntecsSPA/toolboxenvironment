<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="soapNameSpace">http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap</xsl:param>

  <xsl:template match="/wps:Execute">
    <xsl:element name="{string(concat('ExecuteProcess_',ows:Identifier))}" namespace="{$soapNameSpace}">
      <xsl:if test="wps:ResponseForm/wps:ResponseDocument/@storeExecuteResponse">
        <xsl:attribute name="storeExecuteResponse"><xsl:value-of select="wps:ResponseForm/wps:ResponseDocument/@storeExecuteResponse"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="wps:ResponseForm/wps:ResponseDocument/@lineage">
        <xsl:attribute name="lineage"><xsl:value-of select="wps:ResponseForm/wps:ResponseDocument/@lineage"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="wps:ResponseForm/wps:ResponseDocument/@status">
        <xsl:attribute name="status"><xsl:value-of select="wps:ResponseForm/wps:ResponseDocument/@status"/></xsl:attribute>
      </xsl:if>
      <xsl:for-each select="wps:DataInputs/wps:Input">
        <xsl:element name="{ows:Identifier}" namespace="{$soapNameSpace}">
          <xsl:choose>
            <xsl:when test="wps:Data/wps:LiteralData">
                <xsl:value-of select="wps:Data/wps:LiteralData"></xsl:value-of>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="wps:Reference">
                    <xsl:copy-of select="wps:Reference"></xsl:copy-of>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:copy-of select="wps:Data/*[1]/*"></xsl:copy-of>        
                  </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="wps:ResponseForm/wps:ResponseDocument/wps:Output">
        <xsl:element name="{ows:Identifier}" namespace="{$soapNameSpace}">
          <xsl:if test="@asReference">
            <xsl:attribute name="asReference"><xsl:value-of select="@asReference"/></xsl:attribute>
          </xsl:if>
          <xsl:if test="@mimeType">
            <xsl:attribute name="mimeType"><xsl:value-of select="@mimeType"/></xsl:attribute>
          </xsl:if>
          <xsl:if test="@uom">
            <xsl:attribute name="uom"><xsl:value-of select="@uom"/></xsl:attribute>
          </xsl:if>
          <xsl:if test="@schema">
            <xsl:attribute name="schema"><xsl:value-of select="@schema"/></xsl:attribute>
          </xsl:if>
          <xsl:if test="@encoding">
            <xsl:attribute name="encoding"><xsl:value-of select="@encoding"/></xsl:attribute>
          </xsl:if>   
        </xsl:element>
      </xsl:for-each>
    </xsl:element>   
  </xsl:template>
  
  
  
</xsl:stylesheet>
