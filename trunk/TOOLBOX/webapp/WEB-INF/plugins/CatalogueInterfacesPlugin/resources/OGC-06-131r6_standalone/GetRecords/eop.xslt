ï»¿<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc">
	<xsl:template match="/">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<!-- =================================================================== -->
	<xsl:template match="ogc:PropertyIsEqualTo">
		<xsl:choose>
			<xsl:when test="ogc:PropertyName[1]/text() = '/rim:RegistryPackage/rim:RegistryObjectList[*]/rim:RegistryObject/@id'">
				<!--START - Associatate the ExtrinsicObject to the RegistryPackage-->
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>/rim:Association/@targetObject</ogc:PropertyName>
					<ogc:PropertyName>/wrs:ExtrinsicObject/@id</ogc:PropertyName>
				</ogc:PropertyIsEqualTo>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>/rim:Association/@sourceObject</ogc:PropertyName>
					<ogc:PropertyName>/rim:RegistryPackage/@id</ogc:PropertyName>
				</ogc:PropertyIsEqualTo>
				<!--END-->
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- === copy template ================================================= -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<!-- =================================================================== -->
	<!-- === copy template ================================================= -->
	<xsl:template match="csw:Query">
			<csw:Query>
				<xsl:attribute name="typeNames"><xsl:value-of select="@typeNames"/> <xsl:value-of select="string(' ')"/>rim:Association</xsl:attribute>
					<xsl:apply-templates select="node()"/>
			</csw:Query>
	</xsl:template>
	<!-- =================================================================== -->
</xsl:stylesheet>
