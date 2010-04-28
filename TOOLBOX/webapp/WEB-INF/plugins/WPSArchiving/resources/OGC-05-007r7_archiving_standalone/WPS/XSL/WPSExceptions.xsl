<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:param name="exceptionCode">OperationNotSupported</xsl:param>
<xsl:param name="exceptionText">Request is for an operation that is not supported by this server</xsl:param>
  <xsl:template match="/">
    <ows:ExceptionReport xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" xml:lang="en">
      <ows:Exception exceptionCode="{$exceptionCode}">
        <ows:ExceptionText><xsl:value-of select="$exceptionText"></xsl:value-of></ows:ExceptionText>
      </ows:Exception>
    </ows:ExceptionReport>
  </xsl:template>
</xsl:stylesheet>
