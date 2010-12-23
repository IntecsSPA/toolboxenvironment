<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: processSearch.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2004/06/25 11:56:15 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns="http://www.esa.int/mass" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:gml="http://www.opengis.net/gml">
	<xsl:param name="west"/>
	<xsl:param name="east"/>
	<xsl:param name="south"/>
	<xsl:param name="north"/>
	<xsl:param name="startYear"/>
	<xsl:param name="startMonth"/>
	<xsl:param name="startDay"/>
	<xsl:param name="endYear"/>
	<xsl:param name="endMonth"/>
	<xsl:param name="endDay"/>
	<xsl:param name="platfSNm"/>
	<xsl:param name="platfSer"/>
	<xsl:param name="cloudCovePerc"/>
	<xsl:param name="presentation"/>
	<xsl:param name="iteratorSize"/>
	<xsl:param name="cursor"/>
	<xsl:param name="collectionId"/>
	<xsl:param name="mass"/>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$mass != ''">
				<processSearchInputMsg>
					<searchInput>
						<aoi:areaOfInterest>
							<gml:boundedBy>
								<gml:Box srsName="http://www.opengis.net/gml/srs/epsg.xml#4326">
									<gml:coordinates><xsl:choose><xsl:when test="$west != ''"><xsl:value-of select="$west"/>,<xsl:value-of select="$south"/><xsl:value-of select="string(' ')"/><xsl:value-of select="$east"/>,<xsl:value-of select="$north"/></xsl:when><xsl:otherwise>-180,-90 180,90</xsl:otherwise></xsl:choose></gml:coordinates>
								</gml:Box>
							</gml:boundedBy>
						</aoi:areaOfInterest>
						<xsl:call-template name="eoli"/>
					</searchInput>
				</processSearchInputMsg>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="eoli"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="eoli">
		<eoli:searchRequest>
			<eoli:simpleQuery>
				<eoli:dataExt>
					<xsl:if test="$west != '' and $mass =''">
						<eoli:geoEle operator="OVERLAP">
							<eoli:geoBndBox>
								<eoli:westBL><xsl:value-of select="$west"/></eoli:westBL>
								<eoli:eastBL><xsl:value-of select="$east"/></eoli:eastBL>
								<eoli:southBL><xsl:value-of select="$south"/></eoli:southBL>
								<eoli:northBL><xsl:value-of select="$north"/></eoli:northBL>
							</eoli:geoBndBox>
						</eoli:geoEle>
					</xsl:if>
					<xsl:if test="$startYear != ''">
						<eoli:tempEle operator="OVERLAP">
							<eoli:exTemp>
								<eoli:beginEnd>
									<eoli:begin><xsl:value-of select="$startYear"/>-<xsl:value-of select="$startMonth"/>-<xsl:value-of select="$startDay"/></eoli:begin>
									<eoli:end><xsl:value-of select="$endYear"/>-<xsl:value-of select="$endMonth"/>-<xsl:value-of select="$endDay"/></eoli:end>
								</eoli:beginEnd>
							</eoli:exTemp>
						</eoli:tempEle>
					</xsl:if>
				</eoli:dataExt>
				<eoli:satelliteDomainConditions>
					<xsl:if test="$platfSNm != '' or $platfSer != ''">
						<eoli:plaInsIdCondition operator="EQUAL">
							<eoli:plaInsId>
								<xsl:if test="$platfSNm != ''">
									<eoli:platfSNm><xsl:value-of select="$platfSNm "/></eoli:platfSNm>
								</xsl:if>
								<xsl:if test="$platfSer != ''">
									<eoli:platfSer><xsl:value-of select="$platfSer "/></eoli:platfSer>
								</xsl:if>
							</eoli:plaInsId>
						</eoli:plaInsIdCondition>
					</xsl:if>
					<xsl:if test="$cloudCovePerc != ''">
						<eoli:cloudCoverCondition operator="LESS EQUAL">
							<eoli:cloudCovePerc><xsl:value-of select="$cloudCovePerc"/></eoli:cloudCovePerc>
						</eoli:cloudCoverCondition>
					</xsl:if>
				</eoli:satelliteDomainConditions>
			</eoli:simpleQuery>
			<eoli:resultType><xsl:choose><xsl:when test="$presentation = 'hits'">hits</xsl:when><xsl:otherwise>results</xsl:otherwise></xsl:choose></eoli:resultType>
			<xsl:if test="$presentation != 'hits'">
				<xsl:if test="$iteratorSize != ''">
					<eoli:iteratorSize><xsl:value-of select="$iteratorSize"/></eoli:iteratorSize>
				</xsl:if>
				<xsl:if test="$cursor != ''">
					<eoli:cursor><xsl:value-of select="$cursor"/></eoli:cursor>
				</xsl:if>
				<eoli:presentation><xsl:value-of select="$presentation"/></eoli:presentation>
			</xsl:if>
			<eoli:collectionId><xsl:value-of select="$collectionId"/></eoli:collectionId>
		</eoli:searchRequest>
	</xsl:template>
</xsl:stylesheet>
