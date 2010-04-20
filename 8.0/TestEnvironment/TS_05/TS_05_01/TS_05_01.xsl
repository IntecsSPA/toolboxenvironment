<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:gml="http://www.opengis.net/gml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<!--XSL Stylesheet for generating a present response-->
	<xsl:template match="mass:sendRFQInputMsg">
orderId = <xsl:value-of select="mass:commonInput/mass:orderId"/>
AOI = <xsl:value-of select="mass:sendRFQInput/aoi:areaOfInterest/gml:featureMember/aoi:Feature/aoi:Label"/>
userId = <xsl:value-of select="mass:sendRFQInput/mass:userId"/>
date = <xsl:value-of select="mass:sendRFQInput/mass:date"/>
instrument = <xsl:value-of select="mass:sendRFQInput/mass:instrument"/>
format = <xsl:value-of select="mass:sendRFQInput/mass:format"/>
	</xsl:template>
	<xsl:template match="mass:sendOrderInputMsg">
orderId = <xsl:value-of select="mass:commonInput/mass:orderId"/>
AOI = <xsl:value-of select="mass:sendOrderInput/aoi:areaOfInterest/gml:featureMember/aoi:Feature/aoi:Label"/>
userId = <xsl:value-of select="mass:sendOrderInput/mass:userId"/>
date = <xsl:value-of select="mass:sendOrderInput/mass:date"/>
instrument = <xsl:value-of select="mass:sendOrderInput/mass:instrument"/>
format = <xsl:value-of select="mass:sendOrderInput/mass:format"/>
	</xsl:template>
</xsl:stylesheet>

