<?xml version="1.0" encoding="UTF-8"?>
<!--     	File			:	MUISCatalogue2GML.xsl     		File Type	:	XML stylesheet     		Abstract	:	XML stylesheet used to convert an EOLI compliant XML file into a GML compliant file.     		Uses		:	- None.     				   		History 		v1.1 - 20/01/04 B.Baldini. 		-->
<xsl:stylesheet version="1.0" xmlns="http://www.gim.be/xml/schemas/mass/serviceresult.xsd" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/AOIFeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<xsl:param name="hostName"/>
	<xsl:template match="/">
		<xsl:apply-templates select="mass:processSearchOutputMsg"/>
		<xsl:apply-templates select="mass:processPresentOutputMsg"/>
	</xsl:template>
	<xsl:template match="mass:processSearchOutputMsg">
		<featureCollection xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.gim.be/xml/schemas/mass/serviceresult.xsd serviceresult.xsd">
			<gml:boundedBy>
				<gml:Box srsName="EPSG:4326">
					<gml:coordinates>-180,-90 180,90</gml:coordinates>
				</gml:Box>
			</gml:boundedBy>
			<xsl:apply-templates select="mass:searchOutput/mass:retrievedData"/>
		</featureCollection>
	</xsl:template>
	<xsl:template match="mass:processPresentOutputMsg">
		<featureCollection xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.gim.be/xml/schemas/mass/serviceresult.xsd serviceresult.xsd">
			<gml:boundedBy>
				<gml:Box srsName="EPSG:4326">
					<gml:coordinates>-180,-90 180,90</gml:coordinates>
				</gml:Box>
			</gml:boundedBy>
			<xsl:apply-templates select="mass:searchOutput/mass:retrievedData"/>
		</featureCollection>
	</xsl:template>
	<xsl:template match="mass:retrievedData">
		<xsl:for-each select="mass:Metadata">
			<gml:featureMember>
				<srFeature>
					<xsl:attribute name="fid">srFeature.<xsl:number value="position()"/></xsl:attribute>
					<id>
						<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
					</id>
					<!--<xsl:apply-templates select="child::*"/>-->
					<xsl:apply-templates select="eoli:dataIdInfo"/>
					<xsl:apply-templates select="eoli:addInfo"/>
					<Geometry1>
						<gml:Polygon srsName="EPSG:4326">
							<gml:outerBoundaryIs>
								<gml:LinearRing>
									<gml:coordinates><xsl:apply-templates select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/></gml:coordinates>
								</gml:LinearRing>
							</gml:outerBoundaryIs>
						</gml:Polygon>
					</Geometry1>
				</srFeature>
			</gml:featureMember>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="eoli:coordinates">
		<xsl:value-of select="."/>
		<xsl:value-of select="string(' ')"/>
		<xsl:value-of select="substring-before(., ' ')"/>
	</xsl:template>
	<!--<xsl:template match="eoli:mdContact">
		<xsl:element name="attribute1">Organisation Name: <xsl:value-of select="eoli:rpOrgName"/> (role: <xsl:choose>
				<xsl:when test="eoli:role = '002'">custodian</xsl:when>
				<xsl:when test="eoli:role = '006'">originator</xsl:when>
				<xsl:when test="eoli:role = '002'">processor</xsl:when>
			</xsl:choose>)</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:mdDateSt">
		<xsl:element name="attribute1">Metadata Date: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>-->
	<xsl:template match="eoli:dataIdInfo">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:plaInsId">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:platfSNm">
		<xsl:element name="attribute1">Platform,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:platfSer">
		<xsl:element name="attribute1">Satellite Number,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:instShNm">
		<xsl:element name="attribute1">Sensor Id,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:instMode">
		<xsl:element name="attribute1">Sensor Mode,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:satDom">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:orbit">
		<xsl:element name="attribute1">Orbit,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:lastOrbit">
		<xsl:element name="attribute1">Last Orbit,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:orbitDir">
		<xsl:element name="attribute1">Orbit Direction,<xsl:choose>
				<xsl:when test=". = '0'">ascending</xsl:when>
				<xsl:otherwise>descending</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:wwRefSys">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:track">
		<xsl:element name="attribute1">Track,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:frame">
		<xsl:element name="attribute1">Frame,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:swathId">
		<xsl:element name="attribute1">Swath,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:idCitation">
		<xsl:element name="attribute1">Short Name,<xsl:value-of select="eoli:resTitle"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:idAbs">
		<xsl:element name="attribute1">Abstract,<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:idStatus">
		<xsl:element name="attribute1">Product Status,<xsl:choose>
				<xsl:when test=". = '001'">completed</xsl:when>
				<xsl:when test=". = '005'">planned</xsl:when>
				<xsl:when test=". = '006'">required</xsl:when>
				<xsl:when test=". = '007'">under development</xsl:when>
				<xsl:when test=". = '008'">potential</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:dataExt">
		<xsl:element name="attribute1">Start Date,<xsl:value-of select="eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
		</xsl:element>
		<xsl:element name="attribute1">End Date,<xsl:value-of select="eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
		</xsl:element>
		<xsl:if test="eoli:geoEle/eoli:scCenter/eoli:coordinates != ''">
			<xsl:element name="attribute1">Scene Center,<xsl:value-of select="eoli:geoEle/eoli:scCenter/eoli:coordinates"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<!--<xsl:template match="eoli:contInfo">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:attDesc">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:typeName">
		<xsl:element name="attribute1">Attribute description Type: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:attTypes">
		<xsl:element name="attribute1">Attribute types - <xsl:value-of select="eoli:attName"/>: <xsl:value-of select="eoli:typeName"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:contType">
		<xsl:element name="attribute1">Content Type: <xsl:value-of select="."/>
			<xsl:if test=". = '001'">(image)</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:illElevAng">
		<xsl:element name="attribute1">Illumination Elevation Angle: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:illAziAng">
		<xsl:element name="attribute1">Illumination Azimuth Angle: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:cloudCovePerc">
		<xsl:element name="attribute1">Cloud Coverage Percentage: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:prcTypeCod/eoli:identCode">
		<xsl:element name="attribute1">Processing Level Code: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:dqInfo">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:dqScope/eoli:scpLvl">
		<xsl:element name="attribute1">Data Quality Scope Level: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:dataLineage">
		<xsl:for-each select="eoli:prcStep">
		<xsl:apply-templates select="*"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="eoli:stepDesc">
		<xsl:element name="attribute1">Step Description: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:stepDateTm">
		<xsl:element name="attribute1">Process Step Date: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:stepProc/eoli:rpOrgName">
		<xsl:element name="attribute1">Responsible Organisation: <xsl:value-of select="."/> (role: <xsl:choose>
				<xsl:when test="../eoli:role = '002'">custodian</xsl:when>
				<xsl:when test="../eoli:role = '006'">originator</xsl:when>
				<xsl:when test="../eoli:role = '002'">processor</xsl:when></xsl:choose>)</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:stepProc/eoli:role"/>
	<xsl:template match="eoli:algorithm">
		<xsl:element name="attribute1">Algorithm: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="graphOver">
	<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="eoli:bgFileName">
		<xsl:element name="attribute1">Preview: http://<xsl:value-of select="$hostName"/>/muis/<xsl:value-of select="substring-before(substring-after(substring-after(., '//'), '/'), '/')"/>_<xsl:value-of select="substring-before(substring-after(substring-after(substring-after(., '//'), '/'), '/'), '/')"/>_<xsl:value-of select="substring-after(substring-after(substring-after(substring-after(., '//'), '/'), '/'), '/')"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin">
		<xsl:element name="attribute1">Start Date: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end">
		<xsl:element name="attribute1">End Date: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eoli:brwType">
		<xsl:element name="attribute1">Browse Type: <xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>-->
	<xsl:template match="eoli:addInfo">
		<xsl:for-each select="eoli:locAtt">
			<xsl:element name="attribute1">
				<xsl:value-of select="eoli:locName"/>,<xsl:value-of select="eoli:locValue"/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
