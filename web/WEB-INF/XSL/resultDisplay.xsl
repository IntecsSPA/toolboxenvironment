<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: resultDisplay.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2004/06/25 11:56:15 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml" xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
        <xsl:param name="language"/>
        <xsl:param name="nResult">
            <xsl:if test="$language = 'it'">Numero di risultati:</xsl:if>
            <xsl:if test="$language = 'en'">Number of results:</xsl:if>
        </xsl:param>
        <xsl:param name="prodId">
            <xsl:if test="$language = 'it'">Identificatore Prodotto</xsl:if>
            <xsl:if test="$language = 'en'">Product Identifier</xsl:if>
        </xsl:param>
        <xsl:param name="platform">
            <xsl:if test="$language = 'it'">Piattaforma</xsl:if>
            <xsl:if test="$language = 'en'">Platform</xsl:if>
        </xsl:param>
        <xsl:param name="metadata">
            <xsl:if test="$language = 'it'">Metadata Extension</xsl:if>
            <xsl:if test="$language = 'en'">Metadata Extension</xsl:if>
        </xsl:param>
        <xsl:param name="domain">
            <xsl:if test="$language = 'it'">Dominio del satellite</xsl:if>
            <xsl:if test="$language = 'en'">Satellite Domain</xsl:if>
        </xsl:param>
        <xsl:param name="graphical">
            <xsl:if test="$language = 'it'">Graphical Overview</xsl:if>
            <xsl:if test="$language = 'en'">Graphical Overview</xsl:if>
        </xsl:param>
        <xsl:param name="resp">
            <xsl:if test="$language = 'it'">Responsabile Organizzazione</xsl:if>
            <xsl:if test="$language = 'en'">Responsible Organisation</xsl:if>
        </xsl:param>        
         <xsl:param name="metaDate">
            <xsl:if test="$language = 'it'">Metadata Date</xsl:if>
            <xsl:if test="$language = 'en'">Metadata Date</xsl:if>
        </xsl:param>        
          <xsl:param name="infoId">
            <xsl:if test="$language = 'it'">Identificazione Informazioni</xsl:if>
            <xsl:if test="$language = 'en'">Identification Info</xsl:if>
        </xsl:param>        
        <xsl:param name="contentInfo">
            <xsl:if test="$language = 'it'">Informazioni Contenuto</xsl:if>
            <xsl:if test="$language = 'en'">Content Info</xsl:if>
        </xsl:param>        
         <xsl:param name="dataInfo">
            <xsl:if test="$language = 'it'">Data Quality Info</xsl:if>
            <xsl:if test="$language = 'en'">Data Quality Info</xsl:if>
        </xsl:param>        
         <xsl:param name="addInfo">
            <xsl:if test="$language = 'it'">Informazioni addizionali</xsl:if>
            <xsl:if test="$language = 'en'">Additional Info</xsl:if>
        </xsl:param>        
         <xsl:param name="mission">
            <xsl:if test="$language = 'it'">Missione:</xsl:if>
            <xsl:if test="$language = 'en'">Mission:</xsl:if>
        </xsl:param>        
         <xsl:param name="sensor">
            <xsl:if test="$language = 'it'">Sensore:</xsl:if>
            <xsl:if test="$language = 'en'">Sensor:</xsl:if>
        </xsl:param>        
         <xsl:param name="startDate">
            <xsl:if test="$language = 'it'">Data iniziale:</xsl:if>
            <xsl:if test="$language = 'en'">Start Date: </xsl:if>
        </xsl:param>        
         <xsl:param name="endDate">
            <xsl:if test="$language = 'it'">Data finale:</xsl:if>
            <xsl:if test="$language = 'en'">End Date: </xsl:if>
        </xsl:param>        
         <xsl:param name="polygon">
            <xsl:if test="$language = 'it'">Coordinate poligono:</xsl:if>
            <xsl:if test="$language = 'en'">Polygon Coordinates: </xsl:if>
        </xsl:param>        
         <xsl:param name="orbit">
            <xsl:if test="$language = 'it'">Orbita:</xsl:if>
            <xsl:if test="$language = 'en'">Orbit:</xsl:if>
        </xsl:param>        
         <xsl:param name="orbitDir">
            <xsl:if test="$language = 'it'">Direzione dell'orbita:</xsl:if>
            <xsl:if test="$language = 'en'">Orbit Direction:</xsl:if>
        </xsl:param>        
         <xsl:param name="frame">
            <xsl:if test="$language = 'it'">Frame:</xsl:if>
            <xsl:if test="$language = 'en'">Frame:</xsl:if>
        </xsl:param>        
         <xsl:param name="track">
            <xsl:if test="$language = 'it'">Track:</xsl:if>
            <xsl:if test="$language = 'en'">Track:</xsl:if>
        </xsl:param>        
         <xsl:param name="browse">
            <xsl:if test="$language = 'it'">Browse Type</xsl:if>
            <xsl:if test="$language = 'en'">Browse Type:</xsl:if>
        </xsl:param>        
         <xsl:param name="noPrew">
            <xsl:if test="$language = 'it'">No preview</xsl:if>
            <xsl:if test="$language = 'en'">No preview</xsl:if>
        </xsl:param>        
         <xsl:param name="notAvailable">
            <xsl:if test="$language = 'it'">Non disponibile</xsl:if>
            <xsl:if test="$language = 'en'">Not Available</xsl:if>
        </xsl:param>        
        <xsl:param name="ascending">
            <xsl:if test="$language = 'it'">ascendente</xsl:if>
            <xsl:if test="$language = 'en'">ascending</xsl:if>
        </xsl:param>        
        <xsl:param name="descending">
            <xsl:if test="$language = 'it'">discendente</xsl:if>
            <xsl:if test="$language = 'en'">descending</xsl:if>
        </xsl:param>        
        <xsl:param name="name">
            <xsl:if test="$language = 'it'">Nome:</xsl:if>
            <xsl:if test="$language = 'en'">Name:</xsl:if>
        </xsl:param>        
        <xsl:param name="role">
            <xsl:if test="$language = 'it'">Ruolo:</xsl:if>
            <xsl:if test="$language = 'en'">Role:</xsl:if>
        </xsl:param>        
        <xsl:param name="sensorMode">
            <xsl:if test="$language = 'it'">Modalità del sensore:</xsl:if>
            <xsl:if test="$language = 'en'">Sensor mode:</xsl:if>
        </xsl:param>        
        <xsl:param name="sceneCenter">
            <xsl:if test="$language = 'it'">Centro della scena:</xsl:if>
            <xsl:if test="$language = 'en'">Scene Center:</xsl:if>
        </xsl:param>        
        <xsl:param name="swath">
            <xsl:if test="$language = 'it'">Banda:</xsl:if>
            <xsl:if test="$language = 'en'">Swath:</xsl:if>
        </xsl:param>        
        <xsl:param name="lastOrbit">
            <xsl:if test="$language = 'it'">Ultima Orbita:</xsl:if>
            <xsl:if test="$language = 'en'">Last Orbit:</xsl:if>
        </xsl:param>        
        <xsl:param name="abstract">
            <xsl:if test="$language = 'it'">Abstract:</xsl:if>
            <xsl:if test="$language = 'en'">Abstract:</xsl:if>
        </xsl:param>        
        <xsl:param name="prodStatus">
            <xsl:if test="$language = 'it'">Stato del prodotto:</xsl:if>
            <xsl:if test="$language = 'en'">Product Status:</xsl:if>
        </xsl:param>        
        <xsl:param name="contentType">
            <xsl:if test="$language = 'it'">Tipo contenuto:</xsl:if>
            <xsl:if test="$language = 'en'">Content Type:</xsl:if>
        </xsl:param>
        <xsl:param name="attributeName">
            <xsl:if test="$language = 'it'">Attribute Type Name:</xsl:if>
            <xsl:if test="$language = 'en'">Attribute Type Name:</xsl:if>
        </xsl:param>
        <xsl:param name="typeName">
            <xsl:if test="$language = 'it'">Nome tipo:</xsl:if>
            <xsl:if test="$language = 'en'">Type Name:</xsl:if>
        </xsl:param>
        <xsl:param name="elevation">
            <xsl:if test="$language = 'it'">Illumination Elevation Angle:</xsl:if>
            <xsl:if test="$language = 'en'">Illumination Elevation Angle:</xsl:if>
        </xsl:param>
        <xsl:param name="azimuth">
            <xsl:if test="$language = 'it'">Illumination Azimuth Angle:</xsl:if>            
            <xsl:if test="$language = 'en'">Illumination Azimuth Angle:</xsl:if>
        </xsl:param>
        <xsl:param name="cloudCov">
            <xsl:if test="$language = 'it'">Percentuale di nuvolosità:</xsl:if>
            <xsl:if test="$language = 'en'">Cloud Coverage Percentage:</xsl:if>
        </xsl:param>
        <xsl:param name="procLevelCode">
            <xsl:if test="$language = 'it'">Processing Level Code:</xsl:if>
            <xsl:if test="$language = 'en'">Processing Level Code:</xsl:if>
        </xsl:param>
        <xsl:param name="scope">
            <xsl:if test="$language = 'it'">Scopo:</xsl:if>
            <xsl:if test="$language = 'en'">Scope:</xsl:if>
        </xsl:param>
        <xsl:param name="dataLin">
            <xsl:if test="$language = 'it'">Data Lineage:</xsl:if>
            <xsl:if test="$language = 'en'">Data Lineage</xsl:if>
        </xsl:param>
        <xsl:param name="stepDescr">
            <xsl:if test="$language = 'it'">Step Description:</xsl:if>
            <xsl:if test="$language = 'en'">Step Description:</xsl:if>
        </xsl:param>
        <xsl:param name="procStepDate">
            <xsl:if test="$language = 'it'">Process Step Date:</xsl:if>
            <xsl:if test="$language = 'en'">Process Step Date:</xsl:if>
        </xsl:param>
        <xsl:param name="respOrgName">
            <xsl:if test="$language = 'it'">Responsible Organisation Name:</xsl:if>
            <xsl:if test="$language = 'en'">Responsible Organisation Name:</xsl:if>
        </xsl:param>
        <xsl:param name="respOrgRole">
            <xsl:if test="$language = 'it'">Responsible Organisation Role:</xsl:if>
            <xsl:if test="$language = 'en'">Responsible Organisation Role:</xsl:if>
        </xsl:param>
        <xsl:param name="algorithm">
            <xsl:if test="$language = 'it'">Algoritmo:</xsl:if>
            <xsl:if test="$language = 'en'">Algorithm:</xsl:if>
        </xsl:param>
	<xsl:template match="/">
		<xsl:apply-templates select="soap-env:Envelope/soap-env:Body/mass:processSearchOutputMsg/mass:searchOutput"/>
		<xsl:apply-templates select="soap-env:Envelope/soap-env:Body/mass:processPresentOutputMsg/mass:searchOutput"/>
		<xsl:apply-templates select="soap-env:Envelope/soap-env:Body/eoli:response"/>
	</xsl:template>
	<xsl:template match="eoli:response">
		<xsl:choose>
			<xsl:when test="eoli:retrievedData != ''">
				<xsl:apply-templates select="eoli:retrievedData"/>
			</xsl:when>
			<xsl:otherwise>
						<tr>
							<xsl:choose>
								<xsl:when test="eoli:status = 'success'">
									<td class="sortableHeader">
								<xsl:value-of select="$nResult"/>
									<xsl:value-of select="eoli:hits"/>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="sortableHeader">
										<xsl:value-of select="eoli:status"/>
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="mass:searchOutput">
		<xsl:choose>
			<xsl:when test="mass:retrievedData != '' ">
				<xsl:apply-templates select="mass:retrievedData"/>
			</xsl:when>
			<xsl:otherwise>
						<tr>
							<xsl:choose>
								<xsl:when test="mass:statusInfo/mass:statusId = '0' ">
									<td class="sortable">
								<xsl:value-of select="$nResult"/>
									<xsl:value-of select="mass:hits"/>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td>
										<xsl:value-of select="mass:statusInfo/mass:statusMsg"/>
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="eoli:retrievedData">
			<xsl:choose>
				<xsl:when test="../eoli:status != 'success'">
					<tr>
						<td>
							<xsl:value-of select="../eoli:status"/>
						</td>
					</tr>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'summary')">
				<xsl:call-template name="summaryHeader"/>					
					<xsl:apply-templates select="eoli:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'browse')">
					<xsl:call-template name="browseHeader"/>
					<xsl:apply-templates select="eoli:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'brief')">
					<xsl:call-template name="briefHeader"/>
					<xsl:apply-templates select="eoli:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'full')">
					<xsl:call-template name="fullHeader"/>
					<xsl:apply-templates select="eoli:Metadata"/>
				</xsl:when>
			</xsl:choose>
	</xsl:template>
	<xsl:template match="mass:retrievedData">
			<xsl:choose>
				<xsl:when test="../mass:statusInfo/mass:statusId != '0'">
					<tr>
						<td>
							<xsl:value-of select="../mass:statusInfo/mass:statusMsg"/>
						</td>
					</tr>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'summary')">
					<xsl:call-template name="summaryHeader"/>
					<xsl:apply-templates select="mass:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'browse')">
					<xsl:call-template name="browseHeader"/>
					<xsl:apply-templates select="mass:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'brief')">
					<xsl:call-template name="briefHeader"/>
					<xsl:apply-templates select="mass:Metadata"/>
				</xsl:when>
				<xsl:when test="starts-with(string(@presentation) ,'full')">
					<xsl:call-template name="fullHeader"/>
					<xsl:apply-templates select="mass:Metadata"/>
				</xsl:when>
			</xsl:choose>
	</xsl:template>
	
	<xsl:template match="eoli:Metadata">
		<tr>
			<xsl:choose>
				<!-- Start of Summary Presentation Body-->
				<xsl:when test="starts-with(string(../../eoli:retrievedData/@presentation) ,'summary')">
					<xsl:call-template name="summaryBody"/>					
				</xsl:when>
				<!-- End of Summary Presentation Body-->
				<!-- Start of Browse Presentation Body-->
				<xsl:when test="starts-with(string(../../eoli:retrievedData/@presentation) ,'browse')">
					<xsl:call-template name="browseBody"/>
				</xsl:when>
				<!-- End of Browse Presentation body-->
				<!-- Start of Brief Presentation Body-->
				<xsl:when test="starts-with(string(../../eoli:retrievedData/@presentation) ,'brief')">
					<xsl:call-template name="briefBody"/>
				</xsl:when>
				<!-- End of Brief Presentation Body-->
				<!-- Start of full Presentation Body-->
				<xsl:when test="starts-with(string(../../eoli:retrievedData/@presentation) ,'full')">
					<xsl:call-template name="fullBody"/>
				</xsl:when>
				<!-- End of full Presentation Body-->
			</xsl:choose>
		</tr>
	</xsl:template>

	<xsl:template match="mass:Metadata">
		<tr>
			<xsl:choose>
				<!-- Start of Summary Presentation Body-->
				<xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'summary')">
					<xsl:call-template name="summaryBody"/>
				</xsl:when>
				<!-- End of Summary Presentation Body-->
				<!-- Start of Browse Presentation Body-->
				<xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'browse')">
					<xsl:call-template name="browseBody"/>
				</xsl:when>
				<!-- End of Browse Presentation body-->
				<!-- Start of Brief Presentation Body-->
				<xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'brief')">
					<xsl:call-template name="briefBody"/>
				</xsl:when>
				<!-- End of Brief Presentation Body-->
				<!-- Start of full Presentation Body-->
				<xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'full')">
					<xsl:call-template name="fullBody"/>
				</xsl:when>
				<!-- End of full Presentation Body-->
			</xsl:choose>
		</tr>
	</xsl:template>
	<xsl:template name="summaryHeader">
	<tr>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$prodId"/>
							</div>
						</td>
						<td align="center" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$platform"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$metadata"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$domain"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$graphical"/>
							</div>
						</td>
						<td align="center" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$resp"/>
							</div>
						</td>
					</tr>
	</xsl:template>
	<xsl:template name="browseHeader">
	<tr>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$prodId"/>
							</div>
						</td>
						<td align="center" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$platform"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$metadata"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$domain"/>
							</div>
						</td>
						<td align="center" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$resp"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$graphical"/>
							</div>
						</td>
					</tr>
	</xsl:template>
	<xsl:template name="briefHeader">
	<tr>
						<td align="left" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$prodId"/>
							</div>
						</td>
						<td align="left" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$platform"/>
							</div>
						</td>
						<td align="left" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$metadata"/>
							</div>
						</td>
						<td align="left" width="200" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$domain"/>
							</div>
						</td>
						<td align="left" width="150" class="sortableHeader">
							<div align="center">
								<xsl:value-of select="$resp"/>
							</div>
						</td>
					</tr>
	</xsl:template>
	<xsl:template name="fullHeader">
	<tr>
						<td align="center" width="150" class="sortableHeader">
							<div>
								<xsl:value-of select="$metaDate"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div>
								<xsl:value-of select="$infoId"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div>
								<xsl:value-of select="$contentInfo"/>
							</div>
						</td>
						<td align="center" width="200" class="sortableHeader">
							<div>
								<xsl:value-of select="$dataInfo"/>
							</div>
						</td>
						<td align="center" width="250" class="sortableHeader">
							<div>
								<xsl:value-of select="$addInfo"/>
							</div>
						</td>
						<td align="center" width="150" class="sortableHeader">
							<div>
								<xsl:value-of select="$resp"/>
							</div>
						</td>
					</tr>
	</xsl:template>
	<xsl:template name="summaryBody">
	<td align="left" valign="top" width="200" class="sortable">
						<div align="center">
							<a>
								<!--xsl:attribute name="href">testPresentRequest.jsp?mass=<xsl:value-of select="$mass"/>&amp;serviceLocation=<xsl:value-of select="$serviceLocation"/>&amp;collectionId=<xsl:value-of select="$collectionId"/>&amp;productId=<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>&amp;catalogue=<xsl:value-of select="$catalogue"/>&amp;url=<xsl:value-of select="$url"/>&amp;<xsl:if test="$ssl !=''">ssl&amp;sslCertificateLocation=<xsl:value-of select="$catalogue"/></xsl:if></xsl:attribute-->
								<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
							</a>
						</div>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							<xsl:value-of select="$mission"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
							<xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
							</xsl:if>
						</div>
						<div align="left">
							<xsl:value-of select="$sensor"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm"/>
						</div>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
							<div align="left">
								<xsl:value-of select="$startDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
							<div align="left">
								<xsl:value-of select="$endDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != ''">
							<div align="left">
								<xsl:value-of select="$polygon"/>
                                                                
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbit !=''">
							<div align="left">
								<xsl:value-of select="$orbit"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:orbit"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir !=''">
							<div align="left">
								<xsl:value-of select="$orbitDir"/>
								
									<xsl:choose>
									<xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'"><xsl:value-of select="$ascending"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="$descending"/></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame !=''">
							<div align="left">
								<xsl:value-of select="$frame"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track !=''">
							<div align="left">
								<xsl:value-of select="$track"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:choose>
							<xsl:when test="eoli:dqInfo/eoli:graphOver != ''">
								<xsl:for-each select="eoli:dqInfo/eoli:graphOver">
									<p>
										<div align="center">
											<xsl:choose>
												<xsl:when test="eoli:bgFileName != ''">
													<img alt="Couldn't load image" width="100">
														<xsl:attribute name="src"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
													</img>
												</xsl:when>
												<xsl:otherwise>
											<xsl:value-of select="$noPrew"/>
										</xsl:otherwise>
											</xsl:choose>
										</div>
										<xsl:if test="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
											<div align="left">
										
						<xsl:value-of select="$startDate"/>
										
											<xsl:value-of select="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
											</div>
										</xsl:if>
										<xsl:if test="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
											<div align="left">
										
						<xsl:value-of select="$endDate"/>
										
											<xsl:value-of select="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
											</div>
											<div align="left">
										
						<xsl:value-of select="$browse"/>
										
											<xsl:value-of select="./eoli:brwType"/>
											</div>
										</xsl:if>
									</p>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<div align="left"><xsl:value-of select="$notAvailable"/></div>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							
								<xsl:value-of select="$name"/>
							
								<xsl:value-of select="eoli:mdContact/eoli:rpOrgName"/>
						</div>
						<div align="left">
							<xsl:value-of select="$role"/>
							
								<xsl:choose>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">custodian</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '006'">originator</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">processor</xsl:when>
							</xsl:choose>
						</div>
					</td>
	</xsl:template>
	<xsl:template name="browseBody">
	<td align="left" valign="top" width="200" class="sortable">
						<div align="center">
							<a>
								<!--xsl:attribute name="href">testPresentRequest.jsp?mass=<xsl:value-of select="$mass"/>&amp;serviceLocation=<xsl:value-of select="$serviceLocation"/>&amp;collectionId=<xsl:value-of select="$collectionId"/>&amp;productId=<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>&amp;catalogue=<xsl:value-of select="$catalogue"/>&amp;url=<xsl:value-of select="$url"/>&amp;<xsl:if test="$ssl !=''">ssl&amp;sslCertificateLocation=<xsl:value-of select="$catalogue"/></xsl:if></xsl:attribute-->
								<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
							</a>
						</div>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							<xsl:value-of select="$mission"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
							<xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
							</xsl:if>
						</div>
						<div align="left">
							<xsl:value-of select="$sensor"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm"/>
						</div>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
							<div align="left">
								<xsl:value-of select="$startDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
							<div align="left">
								<xsl:value-of select="$endDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != ''">
							<div align="left">
								<xsl:value-of select="$polygon"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir !=''">
							<div align="left">
								<xsl:value-of select="$orbitDir"/>
								
									<xsl:choose>
									<xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'"><xsl:value-of select="$ascending"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="$descending"/></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame !=''">
							<div align="left">
								<xsl:value-of select="$frame"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track !=''">
							<div align="left">
								<xsl:value-of select="$track"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							
								<xsl:value-of select="$name"/>
							
								<xsl:value-of select="eoli:mdContact/eoli:rpOrgName"/>
						</div>
						<div align="left">
							<xsl:value-of select="$role"/>
							
								<xsl:choose>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">custodian</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '006'">originator</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">processor</xsl:when>
							</xsl:choose>
						</div>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:choose>
							<xsl:when test="eoli:dqInfo/eoli:graphOver != ''">
								<xsl:for-each select="eoli:dqInfo/eoli:graphOver">
									<p>
										<xsl:if test="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
											<div align="left">
										
						<xsl:value-of select="$startDate"/>
										
											<xsl:value-of select="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
											</div>
										</xsl:if>
										<xsl:if test="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
											<div align="left">
										
						<xsl:value-of select="$endDate"/>
										
											<xsl:value-of select="./eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
											</div>
											<div align="left">
										
						<xsl:value-of select="$browse"/>
										
											<xsl:value-of select="./eoli:brwType"/>
											</div>
										</xsl:if>
										<p>
											<div align="center">
												<xsl:choose>
													<xsl:when test="eoli:bgFileName != ''">
														<a target="_blank">
															<xsl:attribute name="href"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
															<img alt="Couldn't load image" width="100">
																<xsl:attribute name="src"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
															</img>
														</a>
													</xsl:when>
													<xsl:otherwise>
												<xsl:value-of select="$noPrew"/>
											</xsl:otherwise>
												</xsl:choose>
											</div>
										</p>
									</p>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<div align="left"><xsl:value-of select="$notAvailable"/></div>
							</xsl:otherwise>
						</xsl:choose>
					</td>
	</xsl:template>
	<xsl:template name="briefBody">
	<td align="left" valign="top" width="200" class="sortable">
						<div align="center">
							<a>
								<!--xsl:attribute name="href">testPresentRequest.jsp?mass=<xsl:value-of select="$mass"/>&amp;serviceLocation=<xsl:value-of select="$serviceLocation"/>&amp;collectionId=<xsl:value-of select="$collectionId"/>&amp;productId=<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>&amp;catalogue=<xsl:value-of select="$catalogue"/>&amp;url=<xsl:value-of select="$url"/>&amp;<xsl:if test="$ssl !=''">ssl&amp;sslCertificateLocation=<xsl:value-of select="$catalogue"/></xsl:if></xsl:attribute--><xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
							</a>
						</div>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							<xsl:value-of select="$mission"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
							<xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
							</xsl:if>
						</div>
						<div align="left">
							<xsl:value-of select="$sensor"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm"/>
						</div>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
							<div align="left">
								<xsl:value-of select="$startDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
							<div align="left">
								<xsl:value-of select="$endDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != ''">
							<div align="left">
								<xsl:value-of select="$polygon"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbit !=''">
							<div align="left">
								<xsl:value-of select="$orbit"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:orbit"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir !=''">
							<div align="left">
								<xsl:value-of select="$orbitDir"/>
								
									<xsl:choose>
									<xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'"><xsl:value-of select="$ascending"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="$descending"/></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame !=''">
							<div align="left">
								<xsl:value-of select="$frame"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track !=''">
							<div align="left">
								<xsl:value-of select="$track"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div align="left">
							
								<xsl:value-of select="$name"/>
							
								<xsl:value-of select="eoli:mdContact/eoli:rpOrgName"/>
						</div>
						<div align="left">
							<xsl:value-of select="$role"/>
							
								<xsl:choose>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">custodian</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '006'">originator</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">processor</xsl:when>
							</xsl:choose>
						</div>
					</td>
	</xsl:template>
	<xsl:template name="fullBody">
	<td align="left" valign="top" width="150" class="sortable">
						<div align="center">
							<xsl:value-of select="eoli:mdDateSt"/>
						</div>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<div align="left">
							
								<xsl:value-of select="$prodId"/>
							
							<a>
								<!--xsl:attribute name="href">testPresentRequest.jsp?mass=<xsl:value-of select="$mass"/>&amp;serviceLocation=<xsl:value-of select="$serviceLocation"/>&amp;collectionId=<xsl:value-of select="$collectionId"/>&amp;productId=<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>&amp;catalogue=<xsl:value-of select="$catalogue"/>&amp;url=<xsl:value-of select="$url"/>&amp;<xsl:if test="$ssl !=''">ssl&amp;sslCertificateLocation=<xsl:value-of select="$catalogue"/></xsl:if></xsl:attribute-->
								<xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
							</a>
						</div>
						<div>
							
							<xsl:value-of select="$mission"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
							<xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
							</xsl:if>
						</div>
						<div>
							
						<xsl:value-of select="$sensor"/>
							
								<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm"/>
						</div>
						<xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:instMode != ''">
							<div align="left">
								
						<xsl:value-of select="$sensorMode"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instMode"/>)
						
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
							<div align="left">
								
								<xsl:value-of select="$startDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
							<div align="left">
								
							<xsl:value-of select="$endDate"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != ''">
							<div align="left">
								
							<xsl:value-of select="$polygon"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:scCenter/eoli:coordinates != ''">
							<div align="left">
								
							<xsl:value-of select="$sceneCenter"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:scCenter/eoli:coordinates"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbit">
							<div align="left">
								
							<xsl:value-of select="$orbit"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:orbit"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir != ''">
							<div align="left">
								
							<xsl:value-of select="$orbitDir"/>
								
									<xsl:choose>
									<xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'"><xsl:value-of select="$ascending"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="$descending"/></xsl:otherwise>
								</xsl:choose>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:lastOrbit != ''">
							<div align="left">
								
								<xsl:value-of select="$lastOrbit"/>
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:lastOrbit"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame != ''">
							<div align="left">
								
								<xsl:value-of select="$frame"/>
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track != ''">
							<div align="left">
								
								<xsl:value-of select="$track"/>
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:swathId != ''">
							<div align="left">
								<xsl:value-of select="$swath"/>
								
									<xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:swathId"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:idAbs != '0'">
							<div align="left">
								
								<xsl:value-of select="$abstract"/>
									<xsl:value-of select="eoli:dataIdInfo/eoli:idAbs"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:dataIdInfo/eoli:idStatus != ''">
							<div align="left">
								
								<xsl:value-of select="$prodStatus"/>
									<xsl:choose>
									<xsl:when test="eoli:dataIdInfo/eoli:idStatus = '001'">completed</xsl:when>
									<xsl:when test="eoli:dataIdInfo/eoli:idStatus = '005'">planned</xsl:when>
									<xsl:when test="eoli:dataIdInfo/eoli:idStatus = '006'">required</xsl:when>
									<xsl:when test="eoli:dataIdInfo/eoli:idStatus = '007'">under development</xsl:when>
									<xsl:when test="eoli:dataIdInfo/eoli:idStatus = '008'">potential</xsl:when>
								</xsl:choose>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<div align="left">
							
							<xsl:value-of select="$contentType"/>
								<xsl:if test="eoli:contInfo/eoli:contType = '001'">image</xsl:if>
						</div>
						<div align="left">
							
							Attribute Type Name: 
								<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:typeName"/>
						</div>
						<div align="left">
							<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:attName"/>: 
							
								<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:typeName"/>
						</div>
						<div align="left">
							
							Type Name: 
							
								<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:typeName"/>
						</div>
						<xsl:if test="eoli:contInfo/eoli:attDesc/eoli:illElevAng != ''">
							<div align="left">
								
							Illumination Elevation Angle: 
									<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:illElevAng"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:contInfo/eoli:attDesc/eoli:illAziAng != ''">
							<div align="left">
								
							Illumination Azimuth Angle: 
									<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:illAziAng"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:contInfo/eoli:attDesc/eoli:cloudCovePerc != ''">
							<div align="left">
								
								<xsl:value-of select="$cloudCov"/>
								
									<xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:cloudCovePerc"/>
							</div>
						</xsl:if>
						<xsl:if test="eoli:contInfo/eoli:prcTypeCod/eoli:identCode != ''">
							<div align="left">
							Processing Level Code: 
									<xsl:value-of select="eoli:contInfo/eoli:prcTypeCod/eoli:identCode"/>
							</div>
						</xsl:if>
					</td>
					<td align="left" valign="top" width="200" class="sortable">
						<xsl:if test="eoli:dqInfo/eoli:dqScope/eoli:scpLvl != ''">
							<div align="left">
								
							<xsl:value-of select="$scope"/>
									<xsl:value-of select="eoli:dqInfo/eoli:dqScope/eoli:scpLvl"/>
							</div>
						</xsl:if>
						<xsl:for-each select="eoli:dqInfo/eoli:dataLineage/*">
							<p>
								<div align="left">
									
						Data Lineage
						
								></div>
								<xsl:if test="./eoli:stepDesc != ''">
									<div align="left">
										<!--<xsl:if test="./eoli:stepDesc != ''">-->
										
							Step Description: 
											<xsl:value-of select="./eoli:stepDesc"/>
										<!--</xsl:if>-->
									</div>
								</xsl:if>
								<xsl:if test="./eoli:stepDateTm != ''">
									<div align="left">
										<!--<xsl:if test="./eoli:stepDateTm != ''">-->
										
							Process Step Date: 
											<xsl:value-of select="./eoli:stepDateTm"/>
										<!--</xsl:if>-->
									</div>
								</xsl:if>
								<xsl:if test="./eoli:stepProc/eoli:rpOrgName != ''">
									<div align="left">
										<!--<xsl:if test="./eoli:stepProc/eoli:rpOrgName != ''">-->
										
							Responsible Organisation Name: 
											<xsl:value-of select="./eoli:stepProc/eoli:rpOrgName"/>
										<!--</xsl:if>-->
									</div>
								</xsl:if>
								<xsl:if test="./eoli:stepProc/eoli:role != ''">
									<div align="left">
										<!--<xsl:if test="./eoli:stepProc/eoli:role != ''">-->
										
							Responsible Organisation Role: 
											<xsl:choose>
											<xsl:when test="./eoli:stepProc/eoli:role = '002'">custodian</xsl:when>
											<xsl:when test="./eoli:stepProc/eoli:role = '006'">originator</xsl:when>
											<xsl:when test="./eoli:stepProc/eoli:role = '009'">processor</xsl:when>
										</xsl:choose>
										<!--</xsl:if>-->
									</div>
								</xsl:if>
								<xsl:if test="./eoli:algorithm != ''">
									<div align="left">
										<!--<xsl:if test="./eoli:algorithm != ''">-->
										
							<xsl:value-of select="$algorithm"/>
											<xsl:value-of select="./eoli:algorithm"/>
										<!--</xsl:if>-->
									</div>
								</xsl:if>
							</p>
						</xsl:for-each>
					</td>
					<td align="left" valign="top" width="250" class="sortable">
						<xsl:for-each select="eoli:addInfo/eoli:locAtt">
							<div align="left">
								<xsl:value-of select="./eoli:locName"/>: 
									<xsl:value-of select="./eoli:locValue"/>
							</div>
						</xsl:for-each>
					</td>
					<td align="left" valign="top" width="150" class="sortable">
						<div>
							
								<xsl:value-of select="$name"/>
								<xsl:value-of select="eoli:mdContact/eoli:rpOrgName"/>
						</div>
						<div align="left">
							
								<xsl:value-of select="$role"/>
								<xsl:choose>
								<xsl:when test="eoli:mdContact/eoli:role = '002'">custodian</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '006'">originator</xsl:when>
								<xsl:when test="eoli:mdContact/eoli:role = '009'">processor</xsl:when>
							</xsl:choose>
						</div>
					</td>
	</xsl:template>
</xsl:stylesheet>
