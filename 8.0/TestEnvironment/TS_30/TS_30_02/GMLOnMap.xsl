<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:eoli="http://earth.esa.int/XML/eoli">
	<xsl:template match="/eoli:response">
		<gml:featureMember xmlns:gml="http://www.opengis.net/gml">
			<gml:boundedBy>
	    		<gml:Box srsName="EPSG:4326">
	      			<gml:coordinates>40,5 55,18</gml:coordinates>
	    		</gml:Box>
  			</gml:boundedBy>
			<gml:MultiPolygon srsName="EPSG:4326">
				<xsl:for-each select="eoli:retrievedData/eoli:Metadata">
					<gml:polygonMember>
						<gml:Polygon>
							<gml:LinearRing>
								<gml:coordinates>
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates/text()"/>
								</gml:coordinates>
							</gml:LinearRing>
						</gml:Polygon>
					</gml:polygonMember>
				</xsl:for-each>
			</gml:MultiPolygon>
		</gml:featureMember>
	</xsl:template>
</xsl:stylesheet>
