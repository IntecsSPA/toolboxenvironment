<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:csw="http://www.opengis.net/cat/csw" xmlns:hma="http://earth.esa.int/hma" xmlns:ows="http://www.opengeospatial.net/ows" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="/">
		<eoli:presentRequest xmlns="http://www.esa.int/mass" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<eoli:idCitation>
				<eoli:resTitle>
					<xsl:value-of select="substring-after(substring-after(csw:GetRecordById/csw:Id,'EoliCatalogue:'),':')"/>
				</eoli:resTitle>
			</eoli:idCitation>
			<eoli:presentation><xsl:value-of select="csw:GetRecordById/csw:ElementSetName"/></eoli:presentation>
			<eoli:collectionId>
					<xsl:value-of select="substring-before(substring-after(csw:GetRecordById/csw:Id,'EoliCatalogue:'),':')"/>
			</eoli:collectionId>
		</eoli:presentRequest>
	</xsl:template>
</xsl:stylesheet>
