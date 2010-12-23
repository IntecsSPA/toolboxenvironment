<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:csw="http://www.opengis.net/cat/csw" xmlns:hma="http://earth.esa.int/hma" xmlns:ows="http://www.opengeospatial.net/ows" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:param name="collection">urn:HMA:Cosmo-SkyMed_catalog:P:SCS_B</xsl:param>
	<xsl:param name="requestType">getRecords</xsl:param>
	<xsl:param name="startPosition">3</xsl:param>
	<xsl:param name="maxRecords">4</xsl:param>
	<xsl:template match="/">
		<xsl:variable name="position">
			<xsl:choose>
				<xsl:when test="$startPosition + $maxRecords - 1 &lt; count(InventoryReply/InventoryResult/ProductList)">
					<xsl:value-of select="$startPosition + $maxRecords - 1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="count(InventoryReply/InventoryResult/ProductList)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="resultsReturned">
			<xsl:value-of select="$position - $startPosition + 1"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$requestType='getRecordById'">
				<csw:GetRecordByIdResponse version="2.0.1">
					<xsl:apply-templates select="InventoryReply/InventoryResult/ProductList"/>
				</csw:GetRecordByIdResponse>
			</xsl:when>
			<xsl:otherwise>
				<csw:GetRecordsResponse version="2.0.1">
					<csw:SearchStatus timestamp="2001-12-17T09:30:47.0Z" status="complete"/>
					<csw:SearchResults elementSet="summary" recordSchema="http://earth.esa.int/hma" resultSetId="resultSetId">
						<xsl:attribute name="numberOfRecordsMatched"><xsl:value-of select="count(InventoryReply/InventoryResult/ProductList)"/></xsl:attribute>
						<xsl:attribute name="numberOfRecordsReturned"><xsl:value-of select="$resultsReturned"/></xsl:attribute>
						<xsl:if test="$position &lt; count(InventoryReply/InventoryResult/ProductList)">
							<xsl:attribute name="nextRecord"><xsl:value-of select="$position + 1"/></xsl:attribute>
						</xsl:if>
						<xsl:apply-templates select="InventoryReply/InventoryResult/ProductList"/>
					</csw:SearchResults>
				</csw:GetRecordsResponse>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="InventoryReply/InventoryResult/ProductList">
		<xsl:if test="(position() &lt;= $startPosition + $maxRecords - 1) and (position() &gt;= $startPosition)">
			<hma:EarthObservationProduct gml:id="DS_2044263010822">
				<xsl:attribute name="gml:id">CSK_<xsl:value-of select="TagValues[@Tag='RecordNumber']/@Value"/></xsl:attribute>
				<hma:identifier>
					<xsl:value-of select="$collection"/>:CSK_<xsl:value-of select="TagValues[@Tag='RecordNumber']/@Value"/>
				</hma:identifier>
				<hma:parentIdentifier>
					<xsl:value-of select="$collection"/>
				</hma:parentIdentifier>
				<!-- TBC Check which is the valid value for cosmo-->
				<hma:acquisitionType>NOMINAL</hma:acquisitionType>
				<hma:productType>
					<xsl:choose>
						<xsl:when test="TagValues[@Tag='ProductType']/@Value != '' ">
							<xsl:value-of select="TagValues[@Tag='ProductType']/@Value"/>
						</xsl:when>
						<xsl:otherwise>RAW</xsl:otherwise>
					</xsl:choose>
				</hma:productType>
				<!-- In the catalogue we have only acquired data-->
				<hma:status>ACQUIRED</hma:status>
				<xsl:if test="TagValues[@Tag='StartOfCoverage']/@Value != '' ">
					<hma:startDate>
						<xsl:value-of select="substring(translate(TagValues[@Tag='StartOfCoverage']/@Value,'a b','ATB'),1,23)"/>
					</hma:startDate>
				</xsl:if>
				<xsl:if test="TagValues[@Tag='EndOfCoverage']/@Value != '' ">
					<hma:completionDate>
						<xsl:value-of select="substring(translate(TagValues[@Tag='EndOfCoverage']/@Value,'a b','ATB'),1,23)"/>
					</hma:completionDate>
				</xsl:if>
				<xsl:if test="TagValues[@Tag='Polygon']/@Value != '' ">
					<gml:extentOf>
						<gml:Polygon srsName="EPSG:4326">
							<gml:exterior>
								<gml:LinearRing>
									<gml:posList>
										<!-- If the polygons are closed-->
										<!--xsl:value-of select="translate(substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),']'),'a;b:c','A B C')"/-->
										<!-- If the polygons are opened-->
										<xsl:value-of select="translate(substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),']'),'a;b:c','A B C')"/><xsl:value-of select="string(' ')"/><xsl:value-of select="substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,'['),':')"/><xsl:value-of select="string(' ')"/><xsl:value-of select="substring-before(substring-after(TagValues[@Tag='Polygon']/@Value,':'),';')"/>
									</gml:posList>
								</gml:LinearRing>
							</gml:exterior>
						</gml:Polygon>
					</gml:extentOf>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="TagValues[@Tag='ProductType']/@Value != '' ">
					</xsl:when>
					<xsl:otherwise>
						<hma:acquiredBy>
							<hma:Platform>
								<hma:shortName>
									<xsl:value-of select="TagValues[@Tag='SatelliteId']/@Value"/>
								</hma:shortName>
								<!--hma:serialIdentifier>2</hma:serialIdentifier-->
							</hma:Platform>
							<hma:Instrument>
								<hma:shortName>
									<xsl:value-of select="TagValues[@Tag='SensorId']/@Value"/>
								</hma:shortName>
							</hma:Instrument>
						</hma:acquiredBy>
					</xsl:otherwise>
				</xsl:choose>
				<hma:browse>
					<hma:BrowseInformation>
						<hma:type>THUMBNAIL</hma:type>
						<hma:referenceSystemIdentifier codeSpace="EPSG"/>
						<hma:fileName>http://toolbox.pisa.intecs.it/TOOLBOX/public/IMG-<xsl:value-of select="TagValues[@Tag='RecordNumber']/@Value"/>_T.jpg</hma:fileName>
					</hma:BrowseInformation>
				</hma:browse>
			</hma:EarthObservationProduct>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
