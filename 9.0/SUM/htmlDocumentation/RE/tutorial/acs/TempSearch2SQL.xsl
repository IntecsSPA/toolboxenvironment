<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/AOIFeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<xsl:output method="text"/>
	<xsl:param name="roi">ST_MPolyFromText('multipolygon((2.6165 51.6295,3.6165 51.6295,3.6165 52.6295,2.6165 52.6295,2.6165 51.6295),(2.5099 51.5940,3.9316 51.5940,3.9316 50.6699,2.5099 50.6699,2.5099 51.5940))',4)
</xsl:param>
	<xsl:template match="/">select master_image_id, (AVG(cloud) * 100)::integer as cloud_w_mean from cloud_square where ST_Intersects(shape, <xsl:value-of select="$roi"/>) group by master_image_id into temp cloud_percent
	</xsl:template>
</xsl:stylesheet>
