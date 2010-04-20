<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/AOIFeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<xsl:output method="text"/>
	<xsl:param name="roi">ST_MPolyFromText('multipolygon(((2.6165 51.6295,3.6165 51.6295,3.6165 52.6295,2.6165 52.6295,2.6165 51.6295)),((2.5099 51.5940,3.9316 51.5940,3.9316 50.6699,2.5099 50.6699,2.5099 51.5940)))',4)
</xsl:param>
	<xsl:param name="httpBasePath">http://www.acsys.it:8080/</xsl:param>
	<xsl:param name="databladeScaleX">128</xsl:param>
	<xsl:param name="databladeScaleY">128</xsl:param>
	<xsl:param name="databladeInterp">INTERP_BILINEAR</xsl:param>
	<xsl:param name="serverPath">/var/jakarta-tomcat-4.0.3-LE-jdk14/webapps/kim/mass/ql????????.jpg</xsl:param>
	<xsl:param name="substringStartIndex">44</xsl:param>
	<xsl:param name="requestedResultSetSize">20</xsl:param>
	<xsl:template match="/">
		<xsl:param name="instShNm">
			<xsl:for-each select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:satelliteDomainConditions/eoli:genericCondition">
				<xsl:if test="eoli:attributeId = 'instShNm'">
					<xsl:value-of select="eoli:attributeValue"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:param>
		<xsl:param name="begin">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
		</xsl:param>
		<xsl:param name="presentation">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:presentation"/>
		</xsl:param>
		<xsl:param name="end">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
		</xsl:param>
		<xsl:param name="platfSNm">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:satelliteDomainConditions/eoli:plaInsIdCondition/eoli:plaInsId/eoli:platfSNm"/>
		</xsl:param>
		<xsl:param name="cloudCoverPerc">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:satelliteDomainConditions/eoli:cloudCoverCondition/eoli:cloudCovePerc"/>
		</xsl:param>
		<xsl:param name="platfSer">
			<xsl:value-of select="mass:processSearchInputMsg/mass:searchInput/eoli:searchRequest/eoli:simpleQuery/eoli:satelliteDomainConditions/eoli:plaInsIdCondition/eoli:plaInsId/eoli:platfSer"/>
		</xsl:param>
		<xsl:param name="FORMULA">
		(ST_Area(ST_Intersection(oi.footprint,<xsl:value-of select="$roi"/>)::ST_Polygon)/ST_Area(<xsl:value-of select="$roi"/>))
</xsl:param>
		<xsl:param name="SELECT_HITS"> select count(oi.se_row_id) </xsl:param>
		<xsl:param name="FROM"> from 
    original_image oi, cloud_percent cp </xsl:param>
		<xsl:param name="FROM_PRESENT">	from
    original_image oi</xsl:param>
		<xsl:param name="COMPUTE">, cp.cloud_w_mean, <xsl:value-of select="$FORMULA"/> as coverage </xsl:param>
		<xsl:param name="WHERE">
where
    oi.se_row_id = cp.master_image_id and
    oi.se_row_id in (select master_image_id from cloud_percent) <xsl:if test="$begin != ''"> and
    ((oi.begin::datetime year to day between '<xsl:value-of select="$begin"/>' and '<xsl:value-of select="$end"/>') or 
    (oi.end::datetime year to day between '<xsl:value-of select="$begin"/>' and '<xsl:value-of select="$end"/>')  or
    ('<xsl:value-of select="$begin"/>'::datetime year to day between oi.begin and oi.end) or
    ('<xsl:value-of select="$end"/>'::datetime year to day between oi.begin and oi.end)) </xsl:if> 
<xsl:if test="$platfSNm != ''">
   and oi.Satellite= '<xsl:value-of select="$platfSNm"/>' 
</xsl:if>
			<xsl:if test="$platfSer != ''">
   and oi.Mission = <xsl:value-of select="$platfSer"/>
			</xsl:if>
			<xsl:if test="$instShNm != ''">
 and   oi.Instrument = '<xsl:value-of select="$instShNm"/>' 
</xsl:if>
			<xsl:if test="$cloudCoverPerc != ''">and 
 cp.cloud_w_mean &lt;= <xsl:value-of select="$cloudCoverPerc"/>
			</xsl:if>
		</xsl:param>
		<xsl:param name="ORDER_BY"> order by cloud_w_mean ASC, coverage DESC; </xsl:param>
		<xsl:param name="DROP_TABLE">drop table cloud_percent</xsl:param>
		<xsl:param name="SELECT_BRIEF">
    select first <xsl:value-of select="$requestedResultSetSize"/>
    oi.se_row_id,
    replace(oi.satellite," ","") as satellite,
    oi.mission,
    replace(oi.instrument," ","") as instrument,
    oi.orbit,
    replace(oi.orbit_dir," ","") as orbit_dir,
    oi.track,
    oi.frame,
    replace(oi.res_title," ","") as res_title,
    replace(to_char(oi.begin,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as begin,
    replace(to_char(oi.end,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as end,
    oi.lat_up_left, 
    oi.lon_up_left,
    oi.lat_dn_left,
    oi.lon_dn_left,
    oi.lat_dn_right,
    oi.lon_dn_right,
    oi.lat_up_right,
    oi.lon_up_right
    </xsl:param>
		<xsl:param name="SELECT_SUMMARY">
select first <xsl:value-of select="$requestedResultSetSize"/>
    oi.se_row_id,
    replace(oi.satellite," ","") as satellite,
    oi.mission,
    replace(oi.instrument," ","") as instrument,
    oi.orbit,
    replace(oi.orbit_dir," ","") as orbit_dir,
    oi.track,
    oi.frame,
    replace(oi.res_title," ","") as res_title,
    replace(to_char(oi.begin,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as begin,
    replace(to_char(oi.end,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as end,
    oi.lat_up_left, 
    oi.lon_up_left,
    oi.lat_dn_left,
    oi.lon_dn_left,
    oi.lat_dn_right,
    oi.lon_dn_right,
    oi.lat_up_right,
    oi.lon_up_right, 
     '<xsl:value-of select="$httpBasePath"/>' ||  
        substring(LoToFile(oi.thumbnail, '<xsl:value-of select="$serverPath"/>', 'server') from <xsl:value-of select="$substringStartIndex"/>)  	as url,     
    replace(oi.image_extention," ","") as image_extention
</xsl:param>
		<xsl:param name="SELECT_FULL">
    select first <xsl:value-of select="$requestedResultSetSize"/>
    oi.se_row_id,
    replace(oi.satellite," ","") as satellite,
    oi.mission,
    replace(oi.instrument," ","") as instrument,
    replace(oi.instrument_mode," ","") as instrument_mode,
    oi.orbit,
    replace(oi.orbit_dir," ","") as orbit_dir,
    oi.track,
    oi.frame,
    replace(oi.swath_id," ","") as swath_id,
    replace(oi.res_title," ","") as res_title,
    replace(oi.abstract_element," ","") as abstract_element,
    replace(oi.id_status," ","") as id_status,
    replace(to_char(oi.begin,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as begin,
    replace(to_char(oi.end,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as end,
    oi.lat_up_left, 
    oi.lon_up_left,
    oi.lat_dn_left,
    oi.lon_dn_left,
    oi.lat_dn_right,
    oi.lon_dn_right,
    oi.lat_up_right,
    oi.lon_up_right, 
    oi.lat_center,
    oi.lon_center,
    oi.ill_elev_ang,
    oi.ill_azi_ang,
    oi.cloud_cover_percentage
</xsl:param>
		<xsl:param name="SELECT_BROWSE">
select first <xsl:value-of select="$requestedResultSetSize"/>
oi.se_row_id,
    replace(oi.satellite," ","") as satellite,
    oi.mission,
    replace(oi.instrument," ","") as instrument,
    replace(oi.orbit_dir," ","") as orbit_dir,
    oi.track,
    oi.frame,
    replace(oi.res_title," ","") as res_title,
    replace(to_char(oi.begin,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as begin,
    replace(to_char(oi.end,'%Y-%m-%dT%H:%M:%S%iF2Z')," ","") as end,
    oi.lat_up_left, 
    oi.lon_up_left,
    oi.lat_dn_left,
    oi.lon_dn_left,
    oi.lat_dn_right,
    oi.lon_dn_right,
    oi.lat_up_right,
    oi.lon_up_right,    
     '<xsl:value-of select="$httpBasePath"/>' ||  
        substring(LoToFile(oi.quicklook, '<xsl:value-of select="$serverPath"/>', 'server') from <xsl:value-of select="$substringStartIndex"/>)  	as url,     
    replace(oi.image_extention," ","") as image_extention
</xsl:param>
		<xsl:if test="$presentation = ''">
			<xsl:value-of select="$SELECT_HITS"/>
			<xsl:value-of select="$FROM"/>
			<xsl:value-of select="$WHERE"/>
		</xsl:if>
		<xsl:if test="$presentation = 'summary'">
			<xsl:value-of select="$SELECT_SUMMARY"/>
			<xsl:value-of select="$COMPUTE"/>
			<xsl:value-of select="$FROM"/>
			<xsl:value-of select="$WHERE"/>
			<xsl:value-of select="$ORDER_BY"/>
		</xsl:if>
		<xsl:if test="$presentation = 'brief'">
			<xsl:value-of select="$SELECT_BRIEF"/>
			<xsl:value-of select="$COMPUTE"/>
			<xsl:value-of select="$FROM"/>
			<xsl:value-of select="$WHERE"/>
			<xsl:value-of select="$ORDER_BY"/>
		</xsl:if>
		<xsl:if test="$presentation = 'browse'">
			<xsl:value-of select="$SELECT_BROWSE"/>
			<xsl:value-of select="$COMPUTE"/>
			<xsl:value-of select="$FROM"/>
			<xsl:value-of select="$WHERE"/>
			<xsl:value-of select="$ORDER_BY"/>
		</xsl:if>
		<xsl:if test="$presentation = 'full'">
			<xsl:value-of select="$SELECT_FULL"/>
			<xsl:value-of select="$COMPUTE"/>
			<xsl:value-of select="$FROM"/>
			<xsl:value-of select="$WHERE"/>
			<xsl:value-of select="$ORDER_BY"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
