<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : SOAP_remover.xsl
    Created on : 21 ottobre 2008, 16.30
    Author     : Massimiliano
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="count(//soap-env:Body)=1">
                <xsl:copy-of select="//soap-env:Body/child::*"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>    
    </xsl:template>
</xsl:stylesheet>
