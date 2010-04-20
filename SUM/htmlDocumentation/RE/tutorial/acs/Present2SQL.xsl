<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/AOIFeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
	<xsl:param name="resTitle">'EN1-03110-2167.XI','EN1-03110-2168.XI','EN1-03110-2169.XI'</xsl:param>
	<xsl:param name="httpBasePath">http://www.acsys.it:8080/</xsl:param>
	<xsl:param name="databladeScaleX">128</xsl:param>
	<xsl:param name="databladeScaleY">128</xsl:param>
	<xsl:param name="databladeInterp">INTERP_BILINEAR</xsl:param>
	<xsl:param name="serverPath">/var/jakarta-tomcat-4.0.3-LE-jdk14/webapps/kim/mass/ql????????.jpg</xsl:param>
	<xsl:param name="substringStartIndex">44</xsl:param>
	<xsl:param name="roi">ST_MPolyFromText('multipolygon(((2.6165 51.6295,3.6165 51.6295,3.6165 52.6295,2.6165 52.6295,2.6165 51.6295)),((2.5099 51.5940,3.9316 51.5940,3.9316 50.6699,2.5099 50.6699,2.5099 51.5940)))',4)
</xsl:param>
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:param name="FORMULA">
		(ST_Area(ST_Intersection(oi.footprint,<xsl:value-of select="$roi"/>)::ST_Polygon)/ST_Area(<xsl:value-of select="$roi"/>))
</xsl:param>
		<xsl:param name="COMPUTE">, cp.cloud_w_mean, <xsl:value-of select="$FORMULA"/> as coverage </xsl:param>
		<xsl:param name="FROM_PRESENT">
    from
    original_image oi</xsl:param>
		<xsl:param name="WHERE_PRESENT">
    where
    oi.Res_title in (<xsl:value-of select="$resTitle"/>)
    </xsl:param>
		<xsl:param name="ORDER_BY">	order by cloud_w_mean ASC, coverage DESC;</xsl:param>
		<xsl:param name="presentation">
			<xsl:value-of select="mass:processPresentInputMsg/eoli:presentRequest/eoli:presentation"/>
		</xsl:param>
		<xsl:param name="SELECT_BRIEF">
    select
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
    oi.lon_up_right</xsl:param>
		<xsl:param name="SELECT_SUMMARY">
    select
    oi.se_row_id,
    oi.Satellite,
    oi.Mission,
    oi.Instrument,
    oi.Orbit,
    oi.Orbit_dir,
    oi.Track,
    oi.Frame,
    oi.Res_Title,
    to_char(oi.begin,'%Y-%m-%dT%H:%M:%S%iF2Z') as begin,
    to_char(oi.end,'%Y-%m-%dT%H:%M:%S%iF2Z') as end,
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
    select
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
    select
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
		<xsl:if test="$presentation = 'summary'">
			<xsl:value-of select="$SELECT_SUMMARY"/>
			<xsl:value-of select="$FROM_PRESENT"/>
			<xsl:value-of select="$WHERE_PRESENT"/>
		</xsl:if>
		<xsl:if test="$presentation = 'brief'">
			<xsl:value-of select="$SELECT_BRIEF"/>
			<xsl:value-of select="$FROM_PRESENT"/>
			<xsl:value-of select="$WHERE_PRESENT"/>
		</xsl:if>
		<xsl:if test="$presentation = 'browse'">
			<xsl:value-of select="$SELECT_BROWSE"/>
			<xsl:value-of select="$FROM_PRESENT"/>
			<xsl:value-of select="$WHERE_PRESENT"/>
		</xsl:if>
		<xsl:if test="$presentation = 'full'">
			<xsl:value-of select="$SELECT_FULL"/>
			<xsl:value-of select="$FROM_PRESENT"/>
			<xsl:value-of select="$WHERE_PRESENT"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
