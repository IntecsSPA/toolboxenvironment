<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:eoli="http://earth.esa.int/XML/eoli"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:csw="http://www.opengis.net/cat/csw"
	xmlns:hma="http://earth.esa.int/hma"
	xmlns:ows="http://www.opengeospatial.net/ows"
	xmlns:gml="http://www.opengis.net/gml"
	xmlns:ogc="http://www.opengis.net/ogc"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" version="1.0" encoding="ISO-8859-1"
		omit-xml-declaration="no" indent="no" media-type="text/html" />
	<xsl:param name="collection">
		urn:HMA:EoliCatalogue:ESA.EECF.ERS_SAR_xF
	</xsl:param>
	<xsl:param name="requestType">getRecords</xsl:param>
	<xsl:param name="startPosition">1</xsl:param>
	<xsl:param name="maxRecords">10</xsl:param>
	<xsl:template match="/">
		<xsl:variable name="position">
			<xsl:choose>
				<xsl:when
					test="$startPosition + $maxRecords - 1 &lt; count(eoli:response/eoli:retrievedData/eoli:Metadata)">
					<xsl:value-of
						select="$startPosition + $maxRecords - 1" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of
						select="count(eoli:response/eoli:retrievedData/eoli:Metadata)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="resultsReturned">
			<xsl:value-of select="$position - $startPosition + 1" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$requestType='getRecordById'">
				<csw:GetRecordByIdResponse>
					<xsl:apply-templates
						select="eoli:response/eoli:retrievedData/eoli:Metadata" />
				</csw:GetRecordByIdResponse>
			</xsl:when>
			<xsl:otherwise>
				<csw:GetRecordsResponse version="2.0.1">
					<csw:SearchStatus timestamp="2001-12-17T09:30:47.0Z"
						status="complete" />
					<csw:SearchResults elementSet="summary"
						recordSchema="http://earth.esa.int/hma"
						resultSetId="resultSetId">
						<xsl:attribute name="numberOfRecordsMatched">
							<xsl:value-of
								select="count(eoli:response/eoli:retrievedData/eoli:Metadata)" />
						</xsl:attribute>
						<xsl:attribute name="numberOfRecordsReturned">
							<xsl:value-of select="$resultsReturned" />
						</xsl:attribute>
						<xsl:if
							test="$position &lt; count(eoli:response/eoli:retrievedData/eoli:Metadata)">
							<xsl:attribute name="nextRecord">
								<xsl:value-of select="$position + 1" />
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates
							select="eoli:response/eoli:retrievedData/eoli:Metadata" />
					</csw:SearchResults>
				</csw:GetRecordsResponse>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template
		match="eoli:response/eoli:retrievedData/eoli:Metadata">
		<xsl:if
			test="(position() &lt;= $startPosition + $maxRecords - 1) and (position() &gt;= $startPosition)">
			<hma:EarthObservationProduct gml:id="DS_2044263010822">
				<xsl:attribute name="gml:id">
					<xsl:value-of
						select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle" />
				</xsl:attribute>
				<hma:identifier>
					<xsl:value-of select="$collection" />
				</hma:identifier>
				<hma:parentIdentifier>
					urn:HMA:EoliCatalogue:
					<xsl:value-of
						select="substring-before(substring-after($collection,'urn:HMA:EoliCatalogue:'),':')" />
				</hma:parentIdentifier>
				<hma:acquisitionType>NOMINAL</hma:acquisitionType>
				<hma:productType>
					<xsl:value-of
						select="eoli:dataIdInfo/eoli:prcTypeCode/eoli:identCode" />
				</hma:productType>
				<!-- In the catalogue we have only acquired data-->
				<hma:status>ACQUIRED</hma:status>
				<xsl:if
					test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != '' ">
					<hma:startDate>
						<xsl:value-of
							select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin" />
					</hma:startDate>
				</xsl:if>
				<xsl:if
					test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != '' ">
					<hma:completionDate>
						<xsl:value-of
							select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end" />
					</hma:completionDate>
				</xsl:if>
				<xsl:if
					test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != '' ">
					<gml:extentOf>
						<gml:Polygon srsName="EPSG:4326">
							<gml:exterior>
								<gml:LinearRing>
									<gml:posList>
										<!-- We ahev to close the polygon-->
										<!--xsl:value-of select="translate(substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),']'),'a;b:c','A B C')"/><xsl:value-of select="string(' ')"/><xsl:value-of select="substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),':')"/><xsl:value-of select="string(' ')"/><xsl:value-of select="substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,':'),';')"/-->
										<xsl:value-of
											select="translate(eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates,'A,B', 'a b')" />
										<xsl:value-of
											select="string(' ')" />
										<xsl:value-of
											select="translate(substring-before(eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates,' '),'A,B', 'a b')" />
									</gml:posList>
								</gml:LinearRing>
							</gml:exterior>
						</gml:Polygon>
					</gml:extentOf>
				</xsl:if>
				<hma:acquiredBy>
					<hma:EarthObservationEquipment>
						<hma:platform>
							<hma:Platform>
								<hma:shortName>
									<xsl:value-of
										select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm" />
								</hma:shortName>
								<hma:serialIdentifier>
									<xsl:value-of
										select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer" />
								</hma:serialIdentifier>
							</hma:Platform>
						</hma:platform>
						<hma:instrument>
							<hma:Instrument>
								<hma:shortName>
									<xsl:value-of
										select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm" />
								</hma:shortName>
							</hma:Instrument>
						</hma:instrument>
					</hma:EarthObservationEquipment>
				</hma:acquiredBy>
				<xsl:if
					test="eoli:dqInfo/eoli:graphOver/eoli:bgFileName != '' ">
					<hma:browse>
						<hma:BrowseInformation>
							<hma:type>QUICKLOOK</hma:type>
							<hma:referenceSystemIdentifier
								codeSpace="EPSG" />
							<hma:fileName>
								<xsl:value-of
									select="eoli:dqInfo/eoli:graphOver/eoli:bgFileName" />
							</hma:fileName>
						</hma:BrowseInformation>
					</hma:browse>
				</xsl:if>
			</hma:EarthObservationProduct>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
