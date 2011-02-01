<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
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
		urn:HMA:Cosmo-SkyMed_catalog:P:SCS_B:CSK_3001
	</xsl:param>
	<xsl:param name="requestType">getRecordById</xsl:param>
	<xsl:param name="startPosition">1</xsl:param>
	<xsl:param name="maxRecords">1</xsl:param>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$requestType='getRecordById'">
				<csw:GetRecordByIdResponse>
					<xsl:apply-templates
						select="InventoryReply/InventoryResult/ProductList" />
				</csw:GetRecordByIdResponse>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="position">
					<xsl:choose>
						<xsl:when
							test="$startPosition + $maxRecords - 1 &lt; count(InventoryReply/InventoryResult/ProductList)">
							<xsl:value-of
								select="$startPosition + $maxRecords - 1" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="count(InventoryReply/InventoryResult/ProductList)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="resultsReturned">
					<xsl:value-of
						select="$position - $startPosition + 1" />
				</xsl:variable>
				<csw:GetRecordsResponse version="2.0.1">
					<csw:SearchStatus timestamp="2001-12-17T09:30:47.0Z"
						status="complete" />
					<csw:SearchResults elementSet="full"
						recordSchema="http://earth.esa.int/hma"
						resultSetId="resultSetId">
						<xsl:attribute name="numberOfRecordsMatched">
							<xsl:value-of
								select="count(InventoryReply/InventoryResult/ProductList)" />
						</xsl:attribute>
						<xsl:attribute name="numberOfRecordsReturned">
							<xsl:value-of select="$resultsReturned" />
						</xsl:attribute>
						<xsl:if
							test="$position &lt; count(InventoryReply/InventoryResult/ProductList)">
							<xsl:attribute name="nextRecord">
								<xsl:value-of select="$position + 1" />
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates
							select="InventoryReply/InventoryResult/ProductList" />
					</csw:SearchResults>
				</csw:GetRecordsResponse>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="InventoryReply/InventoryResult/ProductList">
		<xsl:if
			test="(position() &lt;= $startPosition + $maxRecords - 1) and (position() &gt;= $startPosition)">
			<hma:EarthObservationProduct gml:id="DS_2044263010822">
				<xsl:attribute name="gml:id">
					CSK_
					<xsl:value-of
						select="TagValues[@Tag='RecordNumber']/@Value" />
				</xsl:attribute>
				<hma:identifier>
					<xsl:value-of select="$collection" />
					:CSK_
					<xsl:value-of
						select="TagValues[@Tag='RecordNumber']/@Value" />
				</hma:identifier>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<hma:doi>CHANGEIT</hma:doi>
				<hma:parentIdentifier>
					<xsl:value-of select="$collection" />
				</hma:parentIdentifier>

				<hma:acquisitionType>NOMINAL</hma:acquisitionType>
				<hma:productType>
					<xsl:choose>
						<xsl:when
							test="TagValues[@Tag='ProductType']/@Value != '' ">
							<xsl:value-of
								select="TagValues[@Tag='ProductType']/@Value" />
						</xsl:when>
						<xsl:otherwise>RAW</xsl:otherwise>
					</xsl:choose>
				</hma:productType>
				<!-- In the catalogue we have only acquired data-->
				<hma:status>ACQUIRED</hma:status>
				<xsl:if
					test="TagValues[@Tag='AcquisitionStation']/@Value != '' ">
					<hma:downlinkedTo>
						<hma:AcquisitionInformation>
							<hma:acquisitionStation>
								<xsl:value-of
									select="TagValues[@Tag='AcquisitionStation']/@Value" />
							</hma:acquisitionStation>
						</hma:AcquisitionInformation>
					</hma:downlinkedTo>
				</xsl:if>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<hma:archivedIn>
					<hma:ArchivingInformation>
						<hma:archivingCenter>
							CHANGEIR
						</hma:archivingCenter>
						<hma:archivingDate>
							2006-08-12T08:25:53.00Z
						</hma:archivingDate>
					</hma:ArchivingInformation>
				</hma:archivedIn>
				<hma:startDate>
					<xsl:value-of
						select="substring(translate(TagValues[@Tag='StartOfCoverage']/@Value,'a b','ATB'),1,23)" />
				</hma:startDate>
				<hma:completionDate>
					<xsl:value-of
						select="substring(translate(TagValues[@Tag='EndOfCoverage']/@Value,'a b','ATB'),1,23)" />
				</hma:completionDate>
				<gml:extentOf>
					<gml:Polygon srsName="EPSG:4326">
						<gml:exterior>
							<gml:LinearRing>
								<gml:posList>
									<!-- The polygons should be closed -->
									<xsl:value-of
										select="translate(substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),']'),'a;b:c','A B C')" />
								</gml:posList>
							</gml:LinearRing>
						</gml:exterior>
					</gml:Polygon>
				</gml:extentOf>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<gml:centerOf>
					<gml:Point>
						<gml:pos>41.93 34.56</gml:pos>
					</gml:Point>
				</gml:centerOf>
				<xsl:choose>
					<xsl:when
						test="TagValues[@Tag='ProductType']/@Value != '' ">
					</xsl:when>
					<xsl:otherwise>
						<hma:acquiredBy>
							<hma:Platform>
								<hma:shortName>
									<xsl:value-of
										select="TagValues[@Tag='SatelliteId']/@Value" />
								</hma:shortName>
							</hma:Platform>
							<hma:Instrument>
								<hma:shortName>
									<xsl:value-of
										select="TagValues[@Tag='SensorId']/@Value" />
								</hma:shortName>
							</hma:Instrument>
						</hma:acquiredBy>
					</xsl:otherwise>
				</xsl:choose>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<hma:acquisitionParameters>
					<hma:Acquisition>
						<hma:orbitNumber>123</hma:orbitNumber>
						<hma:lastOrbitNumber>12</hma:lastOrbitNumber>
						<hma:orbitDirection>
							ASCENDING
						</hma:orbitDirection>
						<hma:ascendingNodeDate>
							2006-08-12T08:24:26.26Z
						</hma:ascendingNodeDate>
						<hma:ascendingNodeLongitude>
							45
						</hma:ascendingNodeLongitude>
						<hma:orbitDuration>12</hma:orbitDuration>
						<hma:acrossTrackPointingAngle uom="degree">
							45
						</hma:acrossTrackPointingAngle>
						<hma:alongTrackPointingAngle uom="degree">
							50
						</hma:alongTrackPointingAngle>
						<hma:pitch uom="degree">10</hma:pitch>
						<hma:roll uom="degree">40</hma:roll>
						<hma:yaw uom="degree">50</hma:yaw>
					</hma:Acquisition>
				</hma:acquisitionParameters>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<hma:imageQualityCode uom="percentage">
					50
				</hma:imageQualityCode>
				<hma:browse>
					<hma:BrowseInformation>
						<hma:type>QUICKLOOK</hma:type>
						<hma:referenceSystemIdentifier codeSpace="EPSG" />
						<hma:fileName>
							http://toolbox.pisa.intecs.it/TOOLBOX/public/IMG-
							<xsl:value-of
								select="TagValues[@Tag='RecordNumber']/@Value" />
							_B.jpg
						</hma:fileName>
					</hma:BrowseInformation>
				</hma:browse>
				<!--********************************************************************************CHECK THE MAPPING********************************************************************************-->
				<hma:product>
					<hma:ProductInformation>
						<hma:fileName>CHANGE IT</hma:fileName>
						<hma:version>1.1</hma:version>
						<hma:size uom="10" />
					</hma:ProductInformation>
				</hma:product>
				<hma:vendorSpecific>
					<hma:SpecificInformation>
						<hma:localAttribute>
							MYATTRIBUTE
						</hma:localAttribute>
						<hma:localValue>12</hma:localValue>
					</hma:SpecificInformation>
				</hma:vendorSpecific>
			</hma:EarthObservationProduct>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
