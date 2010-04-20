<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/AOIFeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:for-each select="mass:processPresentInputMsg/eoli:presentRequest/eoli:idCitation/eoli:resTitle">'<xsl:value-of select="."/>',</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
