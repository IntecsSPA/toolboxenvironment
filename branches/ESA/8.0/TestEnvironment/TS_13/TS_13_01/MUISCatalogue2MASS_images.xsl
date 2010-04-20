<?xml version="1.0" encoding="UTF-8"?>
<!--     	File			:	MUISCatalogue2MASS.xsl     		File Type	:	XML stylesheet     		Abstract	:	XML stylesheet used to convert an EOLI compliant XML file into a MASS like XML file  		Uses		:	- None.     				   		History 		v1.0 - 20/01/04 B. Baldini. 		-->
<xsl:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.gim.be/xml/schemas/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:gml="http://www.opengis.net/gml">
		 <xsl:param name="gmlFileURL">http://localhost/gml/1hwg1223m.gml</xsl:param>
		 <xsl:param name="operation">search</xsl:param>
		 <!--<xsl:param name="imageFileName"></xsl:param>-->
		 <xsl:template match="/">
		 		 <xsl:choose>
		 		 		 <xsl:when test="$operation='search'">
		 		 		 		 <mass:processSearchOutputMsg>
		 		 		 		 		 <mass:searchOutput>
		 		 		 		 		 		 <mass:statusInfo>
		 		 		 		 		 		 		 <mass:statusId>0</mass:statusId>
		 		 		 		 		 		 		 <mass:statusMsg>successful</mass:statusMsg>
		 		 		 		 		 		 </mass:statusInfo>
		 		 		 		 		 		 <mass:hits>
		 		 		 		 		 		 		 <xsl:value-of select="eoli:response/eoli:hits"/>
		 		 		 		 		 		 </mass:hits>
		 		 		 		 		 		 <xsl:if test="eoli:response/eoli:cursor != ''">
		 		 		 		 		 		 		 <mass:cursor>
		 		 		 		 		 		 		 		 <xsl:value-of select="eoli:response/eoli:cursor"/>
		 		 		 		 		 		 		 </mass:cursor>
		 		 		 		 		 		 </xsl:if>
		 		 		 		 		 		 <mass:retrievedData>
		 		 		 		 		 		 		 <xsl:apply-templates select="eoli:response/eoli:retrievedData/eoli:Metadata"/>
		 		 		 		 		 		 </mass:retrievedData>
		 		 		 		 		 		 <xsl:if test="eoli:response/eoli:hits != '0' and eoli:response/eoli:retrievedData/eoli:Metadata != ''">
		 		 		 		 		 		 		 <mass:viewFileResult>
		 		 		 		 		 		 		 		 <mass:fileURL>
		 		 		 		 		 		 		 		 		 <xsl:value-of select="$gmlFileURL"/>
		 		 		 		 		 		 		 		 </mass:fileURL>
		 		 		 		 		 		 		 		 <mass:fileType>gml</mass:fileType>
		 		 		 		 		 		 		 </mass:viewFileResult>
		 		 		 		 		 		 </xsl:if>
		 		 		 		 		 </mass:searchOutput>
		 		 		 		 </mass:processSearchOutputMsg>
		 		 		 </xsl:when>
		 		 		 <xsl:otherwise>
		 		 		 		 <mass:processPresentOutputMsg>
		 		 		 		 		 <mass:searchOutput>
		 		 		 		 		 		 <mass:statusInfo>
		 		 		 		 		 		 		 <mass:statusId>0</mass:statusId>
		 		 		 		 		 		 		 <mass:statusMsg>successful</mass:statusMsg>
		 		 		 		 		 		 </mass:statusInfo>
		 		 		 		 		 		 <mass:hits>
		 		 		 		 		 		 		 <xsl:value-of select="eoli:response/eoli:hits"/>
		 		 		 		 		 		 </mass:hits>
		 		 		 		 		 		 <xsl:if test="eoli:response/eoli:cursor != ''">
		 		 		 		 		 		 		 <mass:cursor>
		 		 		 		 		 		 		 		 <xsl:value-of select="eoli:response/eoli:cursor"/>
		 		 		 		 		 		 		 </mass:cursor>
		 		 		 		 		 		 </xsl:if>
		 		 		 		 		 		 <mass:retrievedData>
		 		 		 		 		 		 		 <xsl:apply-templates select="eoli:response/eoli:retrievedData/eoli:Metadata"/>
		 		 		 		 		 		 </mass:retrievedData>
		 		 		 		 		 		 <xsl:if test="eoli:response/eoli:hits != '0'">
		 		 		 		 		 		 		 <mass:viewFileResult>
		 		 		 		 		 		 		 		 <mass:fileURL>
		 		 		 		 		 		 		 		 		 <xsl:value-of select="$gmlFileURL"/>
		 		 		 		 		 		 		 		 </mass:fileURL>
		 		 		 		 		 		 		 		 <mass:fileType>gml</mass:fileType>
		 		 		 		 		 		 		 </mass:viewFileResult>
		 		 		 		 		 		 </xsl:if>
		 		 		 		 		 </mass:searchOutput>
		 		 		 		 </mass:processPresentOutputMsg>
		 		 		 </xsl:otherwise>
		 		 </xsl:choose>
		 </xsl:template>
		 <xsl:template match="eoli:Metadata">
		 		 <xsl:attribute name="presentation"><xsl:value-of select="../../eoli:retrievedData/@presentation"/></xsl:attribute>
		 		 <xsl:apply-templates select="eoli:retrievedData/eoli:Metadata"/>
		 		 <mass:Metadata>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </mass:Metadata>
		 </xsl:template>
		 <xsl:template match="eoli:mdContact">
		 		 <eoli:mdContact>
		 		 		 <eoli:rpOrgName>
		 		 		 		 <xsl:value-of select="eoli:rpOrgName"/>
		 		 		 </eoli:rpOrgName>
		 		 		 <eoli:role>
		 		 		 		 <xsl:value-of select="eoli:role"/>
		 		 		 </eoli:role>
		 		 </eoli:mdContact>
		 </xsl:template>
		 <xsl:template match="eoli:mdDateSt">
		 		 <eoli:mdDateSt>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:mdDateSt>
		 </xsl:template>
		 <xsl:template match="eoli:instMode">
		 		 <eoli:instMode>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:instMode>
		 </xsl:template>
		 <xsl:template match="eoli:dataIdInfo">
		 		 <eoli:dataIdInfo>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:dataIdInfo>
		 </xsl:template>
		 <xsl:template match="eoli:plaInsId">
		 		 <eoli:plaInsId>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:plaInsId>
		 </xsl:template>
		 <xsl:template match="eoli:satDom">
		 		 <eoli:satDom>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:satDom>
		 </xsl:template>
		 <xsl:template match="eoli:orbit">
		 		 <eoli:orbit>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:orbit>
		 </xsl:template>
		 <xsl:template match="eoli:lastOrbit">
		 		 <eoli:lastOrbit>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:lastOrbit>
		 </xsl:template>
		 <xsl:template match="eoli:orbitDir">
		 		 <eoli:orbitDir>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:orbitDir>
		 </xsl:template>
		 <xsl:template match="eoli:wwRefSys">
		 		 <eoli:wwRefSys>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:wwRefSys>
		 </xsl:template>
		 <xsl:template match="eoli:passCoverage">
		 		 <eoli:passCoverage>
					 <eoli:start><xsl:value-of select="./eoli:start"/></eoli:start>
		 		 	<eoli:stop><xsl:value-of select="./eoli:stop"/></eoli:stop>
		 		 </eoli:passCoverage>
		 </xsl:template>
		 <xsl:template match="eoli:track">
		 		 <eoli:track>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:track>
		 </xsl:template>
		 <xsl:template match="eoli:frame">
		 		 <eoli:frame>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:frame>
		 </xsl:template>
		 <xsl:template match="eoli:swathId">
		 		 <eoli:swathId>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:swathId>
		 </xsl:template>
		 <xsl:template match="eoli:idAbs">
		 		 <eoli:idAbs>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:idAbs>
		 </xsl:template>
		 <xsl:template match="eoli:idStatus">
		 		 <eoli:idStatus>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:idStatus>
		 </xsl:template>
		 <xsl:template match="eoli:platfSNm">
		 		 <eoli:platfSNm>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:platfSNm>
		 </xsl:template>
		 <xsl:template match="eoli:platfSer">
		 		 <eoli:platfSer>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:platfSer>
		 </xsl:template>
		 <xsl:template match="eoli:instShNm">
		 		 <eoli:instShNm>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:instShNm>
		 </xsl:template>
		 <xsl:template match="eoli:idCitation">
		 		 <eoli:idCitation>
		 		 		 <eoli:resTitle>
		 		 		 		 <xsl:value-of select="eoli:resTitle"/>
		 		 		 </eoli:resTitle>
		 		 </eoli:idCitation>
		 </xsl:template>
		 <xsl:template match="eoli:contInfo">
		 		 <eoli:contInfo>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:contInfo>
		 </xsl:template>
		 <xsl:template match="eoli:attDesc">
		 		 <eoli:attDesc>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:attDesc>
		 </xsl:template>
		 <xsl:template match="eoli:typeName">
		 		 <eoli:typeName>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:typeName>
		 </xsl:template>
		 <xsl:template match="eoli:attTypes">
		 		 <eoli:attTypes>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:attTypes>
		 </xsl:template>
		 <xsl:template match="eoli:attName">
		 		 <eoli:attName>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:attName>
		 </xsl:template>
		 <xsl:template match="eoli:contType">
		 		 <eoli:contType>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:contType>
		 </xsl:template>
		 <xsl:template match="eoli:illElevAng">
		 		 <eoli:illElevAng>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:illElevAng>
		 </xsl:template>
		 <xsl:template match="eoli:illAziAng">
		 		 <eoli:illAziAng>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:illAziAng>
		 </xsl:template>
		 <xsl:template match="eoli:cloudCovePerc">
		 		 <eoli:cloudCovePerc>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:cloudCovePerc>
		 </xsl:template>
		 <xsl:template match="eoli:prcTypeCod">
		 		 <eoli:prcTypeCod>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:prcTypeCod>
		 </xsl:template>
		 <xsl:template match="eoli:identCode">
		 		 <eoli:identCode>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:identCode>
		 </xsl:template>
		 <xsl:template match="eoli:dqScope">
		 		 <eoli:dqScope>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:dqScope>
		 </xsl:template>
		 <xsl:template match="eoli:scpLvl">
		 		 <eoli:scpLvl>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:scpLvl>
		 </xsl:template>
		 <xsl:template match="eoli:dataLineage">
		 		 <eoli:dataLineage>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:dataLineage>
		 </xsl:template>
		 <xsl:template match="eoli:prcStep">
		 		 <eoli:prcStep>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:prcStep>
		 </xsl:template>
		 <xsl:template match="eoli:stepDesc">
		 		 <eoli:stepDesc>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:stepDesc>
		 </xsl:template>
		 <xsl:template match="eoli:stepDateTm">
		 		 <eoli:stepDateTm>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:stepDateTm>
		 </xsl:template>
		 <xsl:template match="eoli:stepProc">
		 		 <eoli:stepProc>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:stepProc>
		 </xsl:template>
		 <xsl:template match="eoli:rpOrgName">
		 		 <eoli:rpOrgName>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:rpOrgName>
		 </xsl:template>
		 <xsl:template match="eoli:role">
		 		 <eoli:role>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:role>
		 </xsl:template>
		 <xsl:template match="eoli:algorithm">
		 		 <eoli:algorithm>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:algorithm>
		 </xsl:template>
		 <xsl:template match="eoli:dataExt">
		 		 <eoli:dataExt>
		 		 		 <xsl:apply-templates select="eoli:tempEle"/>
		 		 		 <xsl:apply-templates select="eoli:geoEle"/>
		 		 </eoli:dataExt>
		 </xsl:template>
		 <xsl:template match="eoli:tempEle">
		 		 <eoli:tempEle>
		 		 		 <eoli:exTemp>
		 		 		 		 <eoli:beginEnd>
		 		 		 		 		 <eoli:begin>
		 		 		 		 		 		 <xsl:value-of select="eoli:exTemp/eoli:beginEnd/eoli:begin"/>
		 		 		 		 		 </eoli:begin>
		 		 		 		 		 <eoli:end>
		 		 		 		 		 		 <xsl:value-of select="eoli:exTemp/eoli:beginEnd/eoli:end"/>
		 		 		 		 		 </eoli:end>
		 		 		 		 </eoli:beginEnd>
		 		 		 </eoli:exTemp>
		 		 </eoli:tempEle>
		 </xsl:template>
		 <xsl:template match="eoli:geoEle">
		 		 <eoli:geoEle>
		 		 		 <xsl:apply-templates select="eoli:polygon"/>
		 		 		 <xsl:apply-templates select="eoli:scCenter"/>
		 		 </eoli:geoEle>
		 </xsl:template>
		 <xsl:template match="eoli:polygon">
		 		 <eoli:polygon>
		 		 		 <eoli:coordinates>
		 		 		 		 <xsl:value-of select="eoli:coordinates"/>
		 		 		 </eoli:coordinates>
		 		 </eoli:polygon>
		 </xsl:template>
		 <xsl:template match="eoli:scCenter">
		 		 <eoli:scCenter>
		 		 		 <eoli:coordinates>
		 		 		 		 <xsl:value-of select="eoli:coordinates"/>
		 		 		 </eoli:coordinates>
		 		 </eoli:scCenter>
		 </xsl:template>
		 <xsl:template match="eoli:dqInfo">
		 		 <eoli:dqInfo>
		 		 		 <xsl:apply-templates select="eoli:dqScope"/>
		 		 		 <xsl:apply-templates select="eoli:datalineage"/>
		 		 		 <xsl:apply-templates select="eoli:graphOver"/>
		 		 </eoli:dqInfo>
		 </xsl:template>
		 <xsl:template match="eoli:graphOver">
		 		 <eoli:graphOver>
		 		 		 <xsl:apply-templates select="eoli:bgFileName"/>
		 		 		 <xsl:apply-templates select="eoli:brwExt"/>
		 		 		 <xsl:apply-templates select="eoli:brwType"/>
		 		 </eoli:graphOver>
		 </xsl:template>
		 <xsl:template match="eoli:bgFileName">
		 		 <eoli:bgFileName><xsl:value-of select="."></xsl:value-of></eoli:bgFileName>
		 </xsl:template>
		 <xsl:template match="eoli:brwExt">
		 		 <xsl:if test="eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
		 		 		 <eoli:brwExt>
		 		 		 		 <eoli:tempEle>
		 		 		 		 		 <eoli:exTemp>
		 		 		 		 		 		 <eoli:beginEnd>
		 		 		 		 		 		 		 <eoli:begin>
		 		 		 		 		 		 		 		 <xsl:value-of select="eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
		 		 		 		 		 		 		 </eoli:begin>
		 		 		 		 		 		 		 <eoli:end>
		 		 		 		 		 		 		 		 <xsl:value-of select="eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
		 		 		 		 		 		 		 </eoli:end>
		 		 		 		 		 		 </eoli:beginEnd>
		 		 		 		 		 </eoli:exTemp>
		 		 		 		 </eoli:tempEle>
		 		 		 </eoli:brwExt>
		 		 </xsl:if>
		 </xsl:template>
		 <xsl:template match="eoli:brwType">
		 		 <eoli:brwType>JPG</eoli:brwType>
		 </xsl:template>
		 <xsl:template match="eoli:addInfo">
		 		 <eoli:addInfo>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:addInfo>
		 </xsl:template>
		 <xsl:template match="eoli:locAtt">
		 		 <eoli:locAtt>
		 		 		 <xsl:apply-templates select="*"/>
		 		 </eoli:locAtt>
		 </xsl:template>
		 <xsl:template match="eoli:locName">
		 		 <eoli:locName>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:locName>
		 </xsl:template>
		 <xsl:template match="eoli:locValue">
		 		 <eoli:locValue>
		 		 		 <xsl:value-of select="."/>
		 		 </eoli:locValue>
		 </xsl:template>
</xsl:stylesheet>

