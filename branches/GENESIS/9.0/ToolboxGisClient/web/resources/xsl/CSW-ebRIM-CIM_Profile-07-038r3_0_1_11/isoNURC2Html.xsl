<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:gmd="http://www.isotc211.org/2005/gmd"
xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
xmlns:wrs="http://www.opengis.net/cat/wrs/1.0"
xmlns:gco="http://www.isotc211.org/2005/gco"
xmlns:gmx="http://www.isotc211.org/2005/gmx"
xmlns:srv="http://www.isotc211.org/2005/srv"
xmlns:gml="http://www.opengis.net/gml/3.2"
xmlns:xlink="http://www.w3.org/1999/xlink"
xmlns:nurc="http://www.nurc.int/isoExtent">
<xsl:output method="html" indent="yes"/>

<xsl:variable name="titleColor">#325e8f</xsl:variable>
<xsl:variable name="valueColor">#a9c6e2</xsl:variable>
<xsl:variable name="titleFontColor">#ffffff</xsl:variable>
<xsl:variable name="fontSize">11</xsl:variable>

<xsl:template match="//gmd:MD_Metadata">
    <xsl:variable name="fullDataURL" select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>

    <table  width='100%'>
        <tr width='100%' rowspan="2">
            <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>General Data Information PIPPO</b></td>

        </tr>
        <tr width='100%'>
            <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>ISO Identifer :</b></td>
            <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'><xsl:value-of select="gmd:fileIdentifier/gco:CharacterString"/></p></td>
        </tr>
        <tr width='100%'>
            <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Metadata Creation Date :</b></td>
            <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'><xsl:value-of select="gmd:dateStamp/gco:Date"/></p></td>
        </tr>

<!-- EXTENDED DATA GENERIC INFO-->
        <tr width='100%' rowspan="2">
            <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Extended Data Information</b></td>
        </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Cruise/Experiment</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataInformation/gmd:experimentOrCruise"/>
                </p></td>
             </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Forecast time</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="//gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimeInstant/gml:timePosition"/>
                </p></td>
             </tr>

        <xsl:for-each select="//gmd:nurcExtent//gmd:variablesList/gmd:variable">
            <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Variable</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                    Name: <xsl:value-of select="gmd:variableName"/> <br/>
                    UOM: <xsl:value-of select="gmd:variableUOM"/> <br/>
                    Long name: <xsl:value-of select="gmd:variableLongName"/> <br/>
                </p></td>
            </tr>
        </xsl:for-each>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Bounding box</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>                
                <xsl:value-of select="//gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal"/><xsl:value-of select="string(' ')"/>
                <xsl:value-of select="//gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal"/><xsl:value-of select="string(' ')"/>
                <xsl:value-of select="//gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal"/><xsl:value-of select="string(' ')"/>
				<xsl:value-of select="//gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal"/>
                </p></td>
             </tr>


<!-- MODEL METADATA INFORMATION-->
    <xsl:choose>
      <xsl:when test="gmd:nurcExtent/gmd:dataSpecs//gmd:modelName">
          <tr width='100%' rowspan="2">
              <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Model Metadata</b></td>
          </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Model Name</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:modelName"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Model Type</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:modelType"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Model Run Time</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:modelRunTime"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Tau</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:modelTau"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Post processing</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:postProcessingFlag"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Z level</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:zLevel"/>
                </p></td>
             </tr>
      </xsl:when>
      <xsl:when test="gmd:nurcExtent/gmd:dataSpecs/gmd:remoteSensingData">
          <tr width='100%' rowspan="2">
              <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Remote Sensing Metadata</b></td>
          </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Sensor Name</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:sensorName"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Satellite</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:satellite"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Post Processing</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:postProcessingFlag"/>
                </p></td>
             </tr>
            <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Vector Data Information</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:vectorDataInformation/@value"/>
                </p></td>
             </tr>
              <xsl:if test="gmd:nurcExtent/gmd:dataSpecs//gmd:vectorDataInformation/@value = 'true'">
                    <tr width='100%'>
                    <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Vector Data Type</b></td>
                    <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                    <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:vectorDataType"/>
                    </p></td>
                 </tr>
                 <tr width='100%'>
                    <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Related Data</b></td>
                    <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                    <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:relatedData"/>
                    </p></td>
                 </tr>
              </xsl:if>
             
          
      </xsl:when>
  </xsl:choose>

<!-- GRID INFORMATION-->
    <xsl:if test="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation">
        <tr width='100%' rowspan="2">
            <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Grid Information</b></td>
        </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Reference system</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:referenceSystemIdentifier"/>
                </p></td>
             </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Pixel Size</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
				Res-X:<xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:pixelSize/gmd:resX"/>
				<br/>
				Res-Y:<xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:pixelSize/gmd:resY"/>
                </p></td>
             </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Grid Size</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
				Width:<xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:gridSize/gmd:width"/>
				<br/>
				Heigth:<xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:gridSize/gmd:height"/>
                </p></td>
             </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Grid origin</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:gridOrigin"/>
                </p></td>
             </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Grid offset</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:gridOffsets"/>
                </p></td>
             </tr>


             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>No data value</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:dataSpecs//gmd:originalGridInformation/gmd:noDataValue"/>
                </p></td>
             </tr>
    </xsl:if>


<!-- DISTRIBUTION INFORMATION-->

        <tr width='100%' rowspan="2">
            <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Distribution Information</b></td>
        </tr>

             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>WCS URL</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:distribuitionInfo/gmd:wcsLayer/gmd:wcsURL"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>WCS Layer Name</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:distribuitionInfo/gmd:wcsLayer/gmd:wcsLayerName"/>
                </p></td>
             </tr>


             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>WMS URL</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:distribuitionInfo/gmd:wmsLayer/gmd:wmsURL"/>
                </p></td>
             </tr>
             <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>WMS Layer Name</b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                <xsl:value-of select="gmd:nurcExtent/gmd:distribuitionInfo/gmd:wmsLayer/gmd:wmsLayerName"/>
                </p></td>
             </tr>

<!-- POINT OF CONTACT-->

        <tr width='100%' rowspan="2">
            <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>Point of Contact</b></td>

        </tr>
        <xsl:for-each select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty">
            <tr width='100%'>
                <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>
                    <xsl:value-of select="gmd:role/gmd:CI_RoleCode/@codeListValue"/> :
                </b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                    <xsl:value-of select="gmd:individualName/gco:CharacterString"/> <xsl:value-of select="string(' ')"/><xsl:value-of select="gmd:organisationName/gco:CharacterString"/>
                </p></td>
            </tr>
        </xsl:for-each>
        <tr width='100%'>
            <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'>
                    Download Full Data:
                </b></td>
                <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'>
                    <a href="{$fullDataURL}"><img  title='Download Data' src='styles/img/download.png' onmouseout="javascript:this.src='styles/img/download.png';this.width='24';this.height='24';"
                      onmouseover="javascript:this.src='styles/img/download.png';this.width='48';this.height='48';" width='24'  height='24'/></a>
                    <img src='style/img/empty.png' width='1' height='50'/>
                    </p>
                </td>
        </tr>
    </table>


</xsl:template>


</xsl:stylesheet>
