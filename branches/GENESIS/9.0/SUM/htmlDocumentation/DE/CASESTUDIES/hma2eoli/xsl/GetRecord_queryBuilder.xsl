<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:csw="http://www.opengis.net/cat/csw" xmlns:hma="http://earth.esa.int/hma" xmlns:ows="http://www.opengeospatial.net/ows" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="/">
		<eoli:searchRequest xmlns="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:gml="http://www.opengis.net/gml">
			<eoli:simpleQuery>
				<eoli:dataExt>
					<eoli:geoEle operator="OVERLAP">
						<eoli:geoBndBox>
							<eoli:westBL>
								<xsl:value-of select="substring-before(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/ogc:BBOX/gml:Envelope/gml:lowerCorner,' ')"/>
							</eoli:westBL>
							<eoli:eastBL>
								<xsl:value-of select="substring-before(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/ogc:BBOX/gml:Envelope/gml:upperCorner,' ')"/>
							</eoli:eastBL>
							<eoli:southBL>
								<xsl:value-of select="substring-after(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/ogc:BBOX/gml:Envelope/gml:lowerCorner,' ')"/>
							</eoli:southBL>
							<eoli:northBL>
								<xsl:value-of select="substring-after(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/ogc:BBOX/gml:Envelope/gml:upperCorner,' ')"/>
							</eoli:northBL>
						</eoli:geoBndBox>
					</eoli:geoEle>
					<eoli:tempEle operator="OVERLAP">
						<eoli:exTemp>
							<eoli:beginEnd>
								<eoli:begin>
									<xsl:value-of select="substring-before(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/*/ogc:PropertyName[text()='StartDate' and local-name(..)='PropertyIsGreaterThan']/../ogc:Literal,'T')"/>
								</eoli:begin>
								<eoli:end>
									<!--xsl:value-of select="substring-before(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/*/ogc:PropertyName[text()='EndDate']/../ogc:Literal,'T')"/-->
									<xsl:value-of select="substring-before(csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter/*/*/ogc:PropertyName[text()='StartDate' and local-name(..)='PropertyIsLessThan']/../ogc:Literal,'T')"/>
								</eoli:end>
							</eoli:beginEnd>
						</eoli:exTemp>
					</eoli:tempEle>
				</eoli:dataExt>
				<eoli:satelliteDomainConditions/>
			</eoli:simpleQuery>
			<eoli:resultType>results</eoli:resultType>
			<eoli:iteratorSize>
				<xsl:value-of select="csw:GetRecords/@maxRecords"/>
			</eoli:iteratorSize>
			<eoli:cursor>
				<xsl:value-of select="csw:GetRecords/@startPosition"/>
			</eoli:cursor>
			<eoli:presentation>
				<xsl:value-of select="csw:GetRecords/csw:Query/csw:ElementSetName"/>
			</eoli:presentation>
			<eoli:collectionId>
				<xsl:value-of select="csw:GetRecords/csw:Query/csw:Constraint/*/*/*/ogc:PropertyName[text()='ParentIdentifier']/../ogc:Literal"/>
			</eoli:collectionId>
		</eoli:searchRequest>
	</xsl:template>
</xsl:stylesheet>