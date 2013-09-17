<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>Schema</title>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="*">
		<!-- Start Tag-->
		<br/>
		<span class="Stile2">&lt;</span>
		<span class="Stile3">
			<xsl:value-of select="name()"/>
		</span>
		<xsl:for-each select="@*">
			<span class="Stile4">
				<xsl:value-of select="string(' ')"/>
				<xsl:value-of select="name()"/>
			</span>
			<span class="Stile5">=&quot;</span>
			<xsl:choose>
				<xsl:when test="name()='schemaLocation'">
                                    <A >
                                        <xsl:attribute name="href">javascript:viewResource('outputType=XML&amp;resourceKey=<xsl:value-of select="."></xsl:value-of>')</xsl:attribute>
						<xsl:value-of select="."/>
   				</A>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
			<span class="Stile2">&quot;</span>
		</xsl:for-each>
		<span class="Stile2">&gt;</span>
		<span class="branch_visible">
			<xsl:apply-templates/>
		</span>
		<!-- End Tag-->
		<span class="Stile2">&lt;/</span>
		<span class="Stile3">
			<xsl:value-of select="name()"/>
		</span>
		<span class="Stile2">&gt;</span>
	</xsl:template>
	<xsl:template match="leaf">
		<xsl:value-of select="name()"/>
		<br/>
	</xsl:template>
</xsl:stylesheet>
