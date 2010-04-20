<?xml version="1.0" encoding="UTF-8"?>
<!--     	File			:	MASS2MUISCatalogue.xsl     		File Type	:	XML stylesheet     		Abstract	:	XML stylesheet used to convert a MUICatalogue - MASS like XML file into an EOLI compliant XML file.     		Uses		:	- None.     				   		History 		v1.0 - 20/01/04 S. Gianfranceschi. 		-->
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<!-- Apply the template for the root element from MASS standard template -->
	<xsl:template match="/">
		<xsl:apply-templates select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest"/>
		<xsl:apply-templates select="mass:processPresentInputMsg"/>
	</xsl:template>
	<xsl:template match="mass:processPresentInputMsg">
		<presentRequest>
			<idCitation>
				<xsl:for-each select="eoli:presentRequest/eoli:idCitation/eoli:resTitle">
					<resTitle>
						<xsl:value-of select="."/>
					</resTitle>
				</xsl:for-each>
			</idCitation>
			<presentation>
				<xsl:value-of select="eoli:presentRequest/eoli:presentation"/>
			</presentation>
			<collectionId>
				<xsl:value-of select="eoli:presentRequest/eoli:collectionId"/>
			</collectionId>
		</presentRequest>
	</xsl:template>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="eoli:searchRequest">
		<searchRequest xmlns="http://earth.esa.int/XML/eoli">
			<xsl:apply-templates select="*"/>
		</searchRequest>
	</xsl:template>
	<xsl:template match="eoli:simpleQuery">
		<simpleQuery>
			<xsl:apply-templates select="*"/>
		</simpleQuery>
	</xsl:template>
	<xsl:template match="eoli:dataExt">
		<dataExt>
			<geoEle operator="OVERLAP">
				<geoBndBox>
					<westBL>
						<xsl:value-of select="substring-before(../../../aoi:areaOfInterest/gml:boundedBy/gml:Box/gml:coordinates,',')"/>
						<!--<xsl:value-of select="eoli:geoEle/eoli:geoBndBox/eoli:westBL"/>-->
					</westBL>
					<eastBL>
						<xsl:value-of select="substring-before(substring-after(../../../aoi:areaOfInterest/gml:boundedBy/gml:Box/gml:coordinates,' '),',')"/>
						<!--<xsl:value-of select="eoli:geoEle/eoli:geoBndBox/eoli:eastBL"/>-->
					</eastBL>
					<southBL>
						<xsl:value-of select="substring-after(substring-before(../../../aoi:areaOfInterest/gml:boundedBy/gml:Box/gml:coordinates,' '),',')"/>
						<!--<xsl:value-of select="eoli:geoEle/eoli:geoBndBox/eoli:southBL"/>-->
					</southBL>
					<northBL>
						<xsl:value-of select="substring-after(substring-after(../../../aoi:areaOfInterest/gml:boundedBy/gml:Box/gml:coordinates,' '),',')"/>
						<!--<xsl:value-of select="eoli:geoEle/eoli:geoBndBox/eoli:northBL"/>-->
					</northBL>
				</geoBndBox>
			</geoEle>
			<xsl:apply-templates select="eoli:tempEle"/>
		</dataExt>
	</xsl:template>
	<xsl:template match="eoli:tempEle">
		<tempEle>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<exTemp>
				<beginEnd>
					<begin>
						<xsl:value-of select="eoli:exTemp/eoli:beginEnd/eoli:begin"/>
					</begin>
					<end>
						<xsl:value-of select="eoli:exTemp/eoli:beginEnd/eoli:end"/>
					</end>
				</beginEnd>
			</exTemp>
		</tempEle>
	</xsl:template>
	<xsl:template match="eoli:satelliteDomainConditions">
		<satelliteDomainConditions>
			<xsl:apply-templates select="*"/>
		</satelliteDomainConditions>
	</xsl:template>
	<xsl:template match="eoli:plaInsIdCondition">
		<plaInsIdCondition>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<plaInsId>
				<platfSNm>
					<xsl:value-of select="eoli:plaInsId/eoli:platfSNm"/>
				</platfSNm>
				<xsl:apply-templates select="eoli:plaInsId/eoli:platfSer"/>
			</plaInsId>
		</plaInsIdCondition>
	</xsl:template>
	<!--<xsl:template match="eoli:wwRefSys">
		<wwRefSys>
			<xsl:apply-templates select="*"/>
		</wwRefSys>
	</xsl:template>
	<xsl:template match="eoli:trackCondition">
		<trackCondition>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<track>
				<xsl:value-of select="eoli:track"/>
			</track>
		</trackCondition>
	</xsl:template>
	<xsl:template match="eoli:frameCondition ">
		<frameCondition>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<frame>
				<xsl:value-of select="eoli:frame"/>
			</frame>
		</frameCondition>
	</xsl:template>
	<xsl:template match="eoli:orbitCondition ">
		<orbitCondition>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<orbit>
				<xsl:value-of select="eoli:orbit"/>
			</orbit>
		</orbitCondition>
	</xsl:template>-->
	<xsl:template match="eoli:cloudCoverCondition">
		<cloudCoverCondition operator="LESS EQUAL">
			<cloudCovePerc>
				<xsl:value-of select="eoli:cloudCovePerc"/>
			</cloudCovePerc>
		</cloudCoverCondition>
	</xsl:template>
	<xsl:template match="eoli:genericCondition">
		<genericCondition>
			<xsl:attribute name="operator"><xsl:value-of select="@operator"/></xsl:attribute>
			<attributeId>
				<xsl:value-of select="eoli:attributeId"/>
			</attributeId>
			<attributeValue>
				<xsl:value-of select="eoli:attributeValue"/>
			</attributeValue>
		</genericCondition>
	</xsl:template>
	<xsl:template match="eoli:platfSer">
		<platfSer>
			<xsl:value-of select="."/>
		</platfSer>
	</xsl:template>
	<xsl:template match="eoli:resultType">
		<resultType>
			<xsl:value-of select="."/>
		</resultType>
	</xsl:template>
	<xsl:template match="eoli:iteratorSize">
		<iteratorSize>
			<xsl:value-of select="."/>
		</iteratorSize>
	</xsl:template>
	<xsl:template match="eoli:cursor">
		<cursor>
			<xsl:value-of select="."/>
		</cursor>
	</xsl:template>
	<xsl:template match="eoli:presentation">
		<presentation>
			<xsl:value-of select="."/>
		</presentation>
	</xsl:template>
	<xsl:template match="eoli:collectionId">
		<collectionId>
			<xsl:value-of select="."/>
		</collectionId>
	</xsl:template>
</xsl:stylesheet>
