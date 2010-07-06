<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:apply-templates select="//xsd:element[@name=$tagName]"/>
	</xsl:template>
	<xsl:template match="xsd:element">
		<xsl:value-of select="xsd:annotation/xsd:documentation"/>
	</xsl:template>
</xsl:stylesheet>
