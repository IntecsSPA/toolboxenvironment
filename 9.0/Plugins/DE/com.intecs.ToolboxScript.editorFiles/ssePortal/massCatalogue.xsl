<?xml version="1.0" encoding="UTF-8"?>
<!--     File:catalogue.xsl
File Type:XML stylesheet
Abstract:XML stylesheet used to process service information contained in the WSDL and XML instance file.
Uses:- Import MASS standard stylesheet (mass.xsl)
History:
$Log: massCatalogue.xsl,v $
Revision 1.12  2007/02/22 12:12:26  mgs
Add template name for Present and Search output for compatibility with schema 1.4 stylesheet template and mass.xsl

Revision 1.11  2006/12/15 15:32:44  acl
Display improvement

Revision 1.9  2006/06/09 14:02:10  sde
fixe the NCR 356 and 358: now we just make a present when you click on the product id or thumbnail of the catalogue result list.

Revision 1.8  2006/05/10 13:09:31  mgs
update to display role, idStatus received value if it does not match an expected (predefined one. Updated for PHR Catalogue

Revision 1.7  2006/02/03 10:48:33  sde
change the style for the product identifier (of the search result)

Revision 1.6  2005/07/07 12:43:31  mgs
GML String

Revision 1.5.4.1  2005/07/07 12:07:23  mgs
GML String

Revision 1.5  2005/02/01 13:30:19  mgs
implement NCR 284: check max number of selected items in shopping basket

Revision 1.4  2004/12/15 17:04:25  mgs
Added  for the multi items selections

Revision 1.1  2004/10/14 08:28:46  mgs
Common templates for the service with Search and Present operation

Revision 1.12  2004/10/01 12:07:37  mgs
call timeline even if no data, update the list of collections

Revision 1.11  2004/09/29 14:25:21  mgs
Add all eecf collections

Revision 1.10  2004/09/27 10:22:31  mgs
Remove CR for the writeGeneralStatus argument

Revision 1.9  2004/09/20 16:11:23  mgs
remove ACS collection, add some collections (PROBA, ERS) from ESA.EECF

Revision 1.8  2004/09/20 09:46:44  mgs
Add the proba collection

Revision 1.7  2004/09/15 06:44:45  mgs
Add thumbnail display + timeline

Revision 1.6  2004/09/03 11:47:48  mgs
Add collection for ATSR ERS

Revision 1.5  2004/07/07 07:11:57  mgs
Add template for the ESRIN Order

Revision 1.4  2004/07/01 08:00:11  mgs
Set the proposed date to the current date

Revision 1.3  2004/06/30 12:21:48  mgs
Updated  for netscape : display of thumbnails (row height is fixed)

Revision 1.2  2004/06/28 06:45:18  mgs
Add browse presentation

End of history.
-->
<xsl:stylesheet version="1.0"   xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oi="http://www.esa.int/oi" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" >

<!-- Parameter used to filter display when the same "part" of stylesheet is used on different page :
          e.g for part getRFQOutputHTML, all RFQ results are displayed in the Order Preparation while
          a single RFQ result is displayed in the Order Result page. -->
<xsl:param name="displayFilter"/>


<!-- ***************************************************************************
	       Templates used to display Metadata
            mass:multiSearchOutput
     *************************************************************************** !-->

<xsl:template match="mass:Metadata">
<!--   <td>presentation:<xsl:value-of select="../../mass:retrievedData/@presentation"/></td>-->
<xsl:choose>
<!-- Start of Summary Presentation Body-->
<!-- Search output -->
    <xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'summary')">
        <!-- Product Id -->
        <td align="left" valign="top">
        <div>
        <xsl:choose>
            <xsl:when test="$displayFilter='SELECTEDRESULT'">
                <a href="JavaScript:showPresent('{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../eoli:collectionId}' ) " class="stylesheetSmTextLink"><xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/></a>
            </xsl:when>
            <xsl:when test="$displayFilter='SHOPPING_BASKET'">
            <input type="checkbox" name="searchRadioSelection" value="{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}"
						       class="stylesheetSmText" >
                               <a href="JavaScript:showPresent('{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../eoli:collectionId}' ); " class="stylesheetSmTextLink"><xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/></a></input>
             </xsl:when>
            <xsl:otherwise>
            <input type="checkbox" name="searchRadioSelection" value="{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}"
						      onclick="updateSelectedSearchResultTime('{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../eoli:collectionId}','{eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin}','{eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end}');" class="stylesheetSmText" ><a href="JavaScript:showPresent('{eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../eoli:collectionId}' );" class="stylesheetSmTextLink"><xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/></a></input>
            </xsl:otherwise>
        </xsl:choose>
        </div>
        </td>
        <!-- Collection Id -->
        <td align="left" valign="top">
        <div align="left">
            <font class="stylesheetSmText">
            <xsl:value-of select="../../../eoli:collectionId"/>
            <input type="hidden" name="collectionId" value="{../../../eoli:collectionId}"/>
            </font>
        </div>
        </td>
        <!-- Platform -->
        <td align="left" valign="top">
        <div align="left">
        <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
            <xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
            </xsl:if>
        </font>
        </div>
        </td>
	     <!-- Acquisition date -->
         <td align="left" valign="top">
         <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
            <div align="left">
            <!--	<font class="stylesheetBoldSmText">Start Date: </font> -->
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
            </font>
            <input type="hidden" name="beginDate" value="{eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin}"/>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
            <input type="hidden" name="endDate" value="{eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end}"/>
        </xsl:if>
        </td>
		<!-- Satellite data -->
        <td align="left" valign="top">
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbit !=''">
            <div>
            <font class="stylesheetBoldSmText">Orbit: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:orbit"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir !=''">
            <div>
            <font class="stylesheetBoldSmText">Orbit Direction: </font>
            <font class="stylesheetSmText">
            <xsl:choose>
                <xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'">ascending</xsl:when>
                <xsl:otherwise>descending</xsl:otherwise>
            </xsl:choose>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame !=''">
            <div align="left">
            <font class="stylesheetBoldSmText">Frame: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track !=''">
            <div align="left">
            <font class="stylesheetBoldSmText">Track: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
            </font>
            </div>
        </xsl:if>
        </td>
		<!-- Overlap info -->
		<xsl:choose>
		<xsl:when test="eoli:addInfo/eoli:locAtt/eoli:locValue !=''">
			<td align="center" valign="top">
			<font class="stylesheetSmText">
			<xsl:value-of select="eoli:addInfo/eoli:locAtt/eoli:locValue"/>%
			</font>
			</td>
		</xsl:when>
		<xsl:otherwise>
			<td align="center" valign="top">
			<font class="stylesheetSmText">n/a</font>
			</td>
		</xsl:otherwise>
		</xsl:choose>
		<!-- Graphical Overview -->
        <td align="left" valign="top" height="100">
        <xsl:for-each select="eoli:dqInfo/eoli:graphOver">
            <div align="left">
            <xsl:choose>
            <xsl:when test="eoli:bgFileName != ''">
                <xsl:choose>
                <xsl:when test="$displayFilter='SELECTEDRESULT'">
                    <a href="JavaScript:showPresent('{../../eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../../../eoli:collectionId}' ); "  >
                        <img alt="Show details" width="100" border="0">
                        <xsl:attribute name="src"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
                        </img></a>
                        <script LANGUAGE="JavaScript" >
                        writeDisplayThumbnailCode('<xsl:value-of select="../../../../../eoli:collectionId"/>',
                            '<xsl:value-of select="../../eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>',
                            '<xsl:value-of select="../../eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>');
                        </script>
                </xsl:when>
               <xsl:when test="$displayFilter='SHOPPING_BASKET'">
                    <a href="JavaScript:showPresent('{../../eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../../../eoli:collectionId}' ); "  >
                        <img alt="Show details" width="100" border="0">
                        <xsl:attribute name="src"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
                        </img></a>
                </xsl:when>
                <xsl:otherwise>
                    <a href="JavaScript:showPresent('{../../eoli:dataIdInfo/eoli:idCitation/eoli:resTitle}','{../../../../../eoli:collectionId}' ); "  >
                        <img alt="Show details" width="100" border="0">
                        <xsl:attribute name="src"><xsl:value-of select="eoli:bgFileName"/></xsl:attribute>
                        </img>
                    </a>
                    <script LANGUAGE="JavaScript" >
                    writeDisplayThumbnailCode('<xsl:value-of select="../../../../../eoli:collectionId"/>',
                                            '<xsl:value-of select="../../eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>',
                                            '<xsl:value-of select="../../eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>');
                    </script>
                </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <font class="stylesheetSmText">No preview</font>
            </xsl:otherwise>
            </xsl:choose>
            </div>
        </xsl:for-each>
        </td>
    </xsl:when>
<!-- ****************** Start of full Presentation Body ********************** -->
<!-- ****************** Use for the Present operation   ********************** -->
    <xsl:when test="starts-with(string(../../mass:retrievedData/@presentation) ,'full')">
    <table  border="1"  cellspacing="0" cellpadding="0" width="100%" class="lghtbloc">
    <tr>
    <td valign="top" width="60%">
        <table>
        <!--firt level left table -->
        <tr>
        <!-- table with Administrative information -->
        <td width="100%" class="stylesheetHeader">Administrative information
        </td>
        </tr>
        <tr><td width="100%">
        <div>
        <!--Responsible Organisation-->
        <font class="stylesheetBoldSmText">Organisation Name: </font>
        <font class="stylesheetSmText">
            <xsl:value-of select="eoli:mdContact/eoli:rpOrgName"/>
        </font>
        </div>
        <div>
        <font class="stylesheetBoldSmText">Organisation Role: </font>
        <font class="stylesheetSmText">
        <xsl:choose>
        <xsl:when test="eoli:mdContact/eoli:role = '002'">custodian</xsl:when>
        <xsl:when test="eoli:mdContact/eoli:role = '006'">originator</xsl:when>
        <xsl:when test="eoli:mdContact/eoli:role = '009'">processor</xsl:when>
        <xsl:otherwise><xsl:value-of select="eoli:mdContact/eoli:role"/></xsl:otherwise>
        </xsl:choose>
        </font>
        </div>
        <div>
        <font class="stylesheetBoldSmText">Product Identifier: </font>
        <font class="stylesheetSmText">
        <xsl:value-of select="eoli:dataIdInfo/eoli:idCitation/eoli:resTitle"/>
        </font>
        </div>
        <xsl:if test="eoli:dataIdInfo/eoli:idAbs != '0'">
            <div>
            <font class="stylesheetBoldSmText">Abstract: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:idAbs"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:idStatus != ''">
            <div>
            <font class="stylesheetBoldSmText">Product Status: </font>
            <font class="stylesheetSmText">
            <xsl:choose>
            <xsl:when test="eoli:dataIdInfo/eoli:idStatus = '001'">completed</xsl:when>
            <xsl:when test="eoli:dataIdInfo/eoli:idStatus = '005'">planned</xsl:when>
            <xsl:when test="eoli:dataIdInfo/eoli:idStatus = '006'">required</xsl:when>
            <xsl:when test="eoli:dataIdInfo/eoli:idStatus = '007'">under development</xsl:when>
            <xsl:when test="eoli:dataIdInfo/eoli:idStatus = '008'">potential</xsl:when>
            <xsl:otherwise><xsl:value-of select="eoli:dataIdInfo/eoli:idStatus"/></xsl:otherwise>
            </xsl:choose>
            </font>
            </div>
        </xsl:if>
        </td>
        </tr>
        <tr>
        <!-- table with Geographic information -->
        <td width="100%" class="stylesheetHeader">Geographic location</td>
        </tr>
        <tr>
        <td width="100%" valign="top">
        <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates != ''">
            <div>
            <font class="stylesheetBoldSmText">Polygon Coordinates:</font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:polygon/eoli:coordinates"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:scCenter/eoli:coordinates != ''">
            <div>
            <font class="stylesheetBoldSmText">Scene Center: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:geoEle/eoli:scCenter/eoli:coordinates"/>
            </font>
            </div>
        </xsl:if>
        </td>
        </tr>
        <tr>
        <!-- table with temporal information -->
        <td width="100%" class="stylesheetHeader">Temporal information</td>
        </tr>
        <tr><td width="100%">
        <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin != ''">
            <div>
            <font class="stylesheetBoldSmText">Start Date: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end != ''">
            <div>
            <font class="stylesheetBoldSmText">End Date: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:dataExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
            </font>
            </div>
        </xsl:if>
        <div>
        <font class="stylesheetBoldSmText">Metadata Date:</font>
        <font class="stylesheetSmText">
        <xsl:value-of select="eoli:mdDateSt"/>
        </font>
        </div>
        </td>
        </tr>
    </table>
    </td>
    <td valign="top" width="40%">
    <!--firt level right table -->
        <table>
        <tr>
        <!-- Technical characteristics -->
        <td width="100%" class="stylesheetHeader">Technical characteristics
        </td>
        </tr>
        <tr>
        <td width="100%">
        <div>
        <font class="stylesheetBoldSmText">Mission: </font>
        <font class="stylesheetSmText">
        <xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSNm"/>
        <xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer != ''">-<xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:platfSer"/>
        </xsl:if>
        </font>
        </div>
        <div>
        <font class="stylesheetBoldSmText">Code Identifier: </font>
        <font class="stylesheetSmText">
        <xsl:value-of select="eoli:dataIdInfo/eoli:prcTypeCode/eoli:identCode"/>
        </font>
        </div>
        <div>
        <font class="stylesheetBoldSmText">Sensor: </font>
        <font class="stylesheetSmText">
        <xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instShNm"/>
        </font>
        </div>
        <xsl:if test="eoli:dataIdInfo/eoli:plaInsId/eoli:instMode != ''">
            <div>
            <font class="stylesheetBoldSmText">Sensor mode: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:plaInsId/eoli:instMode"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbit">
            <div>
            <font class="stylesheetBoldSmText">Orbit: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:orbit"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir != ''">
            <div>
            <font class="stylesheetBoldSmText">Orbit direction: </font>
            <font class="stylesheetSmText">
            <xsl:choose>
            <xsl:when test="eoli:dataIdInfo/eoli:satDom/eoli:orbitDir = '0'">ascending</xsl:when>
            <xsl:otherwise>descending</xsl:otherwise>
            </xsl:choose>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:lastOrbit != ''">
            <div>
            <font class="stylesheetBoldSmText">Last Orbit: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:lastOrbit"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame != ''">
            <div align="left">
            <font class="stylesheetBoldSmText">Frame: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:frame"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track != ''">
            <div>
            <font class="stylesheetBoldSmText">Track: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:wwRefSys/eoli:track"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:swathId != ''">
            <div>
            <font class="stylesheetBoldSmText">Swath: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:swathId"/>
            </font>
            </div>
        </xsl:if>
		<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:passCoverage/eoli:start != ''">
            <div>
            <font class="stylesheetBoldSmText">Start pass coverage: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:passCoverage/eoli:start"/>
            </font>
            </div>
        </xsl:if>
		<xsl:if test="eoli:dataIdInfo/eoli:satDom/eoli:passCoverage/eoli:stop != ''">
            <div>
            <font class="stylesheetBoldSmText">Stop pass coverage: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dataIdInfo/eoli:satDom/eoli:passCoverage/eoli:stop"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo">
            <br/>
            <div>
            <font class="stylesheetBoldSmText">Content information:</font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo/eoli:attDesc/eoli:typeName != ''">
            <div>
            <font class="stylesheetBoldSmText">typeName: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:typeName"/>
            </font>
            </div>
        </xsl:if>
		 <xsl:if test="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:attName != ''">
            <div>
            <font class="stylesheetBoldSmText">attribute name: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:attName"/>
            </font>
            </div>
        </xsl:if>
		<xsl:if test="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:typeName != ''">
            <div>
            <font class="stylesheetBoldSmText">atribute type name: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:attDesc/eoli:attTypes/eoli:typeName"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo/eoli:contType != ''">
            <div>
            <font class="stylesheetBoldSmText">content Type: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:contType"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo/eoli:illElevAng != ''">
            <div>
            <font class="stylesheetBoldSmText">Ill. Elevation Angle: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:illElevAng"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo/eoli:illAziAng != ''">
            <div>
            <font class="stylesheetBoldSmText">Ill. Azimuth Angle: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:illAziAng"/>
            </font>
            </div>
        </xsl:if>
        <xsl:if test="eoli:contInfo/eoli:cloudCovePerc != ''">
            <div>
            <font class="stylesheetBoldSmText">Cloud Coverage Percentage: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:contInfo/eoli:cloudCovePerc"/>
            </font>
            </div>
        </xsl:if>
    </td>
    </tr>
    <xsl:if test="eoli:addInfo/eoli:locAtt">
    <tr>
        <td>
        <!--Additional Info-->
            <br/>
            <div>
            <font class="stylesheetBoldSmText">Additional information:</font>
            </div>
        </td>
    </tr>
    <tr>
        <td> 
            <font class="stylesheetBoldSmText">Local Attribute elements :</font>
        </td>
    </tr>
    <tr>
        <td>

        <xsl:for-each select="eoli:addInfo/eoli:locAtt">
			<div>
            <font class="stylesheetBoldSmText">
            <xsl:value-of select="./eoli:locName"/>: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="./eoli:locValue"/>
            </font>
            </div>
            
        </xsl:for-each>

        </td>
    </tr>
    </xsl:if>
        </table>
    </td>
    </tr>
<!-- Browse images -->
<!-- Graphical Overview -->
    <tr>
    <td colspan="2">
         <xsl:if test="eoli:dqInfo">
            <div>
            <font class="stylesheetBoldSmText">Data Quality Info:</font>
            </div>
            <br/>
        </xsl:if>
        <xsl:if test="eoli:dqInfo/eoli:dqScope/eoli:ScpLvl">
        <div>
            <font class="stylesheetBoldSmText">Level Element: </font>
            <font class="stylesheetSmText">
            <xsl:value-of select="eoli:dqInfo/eoli:dqScope/eoli:ScpLvl"/>	
            </font>
        </div>
        </xsl:if>

    <xsl:for-each select="eoli:dqInfo/eoli:graphOver">
        <xsl:choose>
        <xsl:when test="eoli:bgFileName != ''">
            <xsl:if test="eoli:brwId">
                <div>
                <font class="stylesheetBoldSmText">Browse identification: </font>
                <font class="stylesheetSmText">
                    <xsl:value-of select="eoli:brwId"/>
                </font>
                </div>
            </xsl:if>
            <xsl:if test="eoli:brwType">
            <div>
                <font class="stylesheetBoldSmText">Browse Type: </font>
                <font class="stylesheetSmText">
                    <xsl:value-of select="eoli:brwType"/>
                </font>
            </div>
            </xsl:if>
            <xsl:if test="eoli:brwId">
                <div>
                <font class="stylesheetBoldSmText">Browse identification: </font>
                <font class="stylesheetSmText">
                    <xsl:value-of select="eoli:brwId"/>
                </font>
                </div>
            </xsl:if>
            <xsl:if test="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin">
                <div>
                <font class="stylesheetBoldSmText">Start Date: </font>
                <font class="stylesheetSmText">
                    <xsl:value-of select="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:begin"/>
                </font>
                </div>
            </xsl:if>
             <xsl:if test="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end">
                <div>
                <font class="stylesheetBoldSmText">End Date: </font>
                <font class="stylesheetSmText">
                    <xsl:value-of select="eoli:brwExt/eoli:tempEle/eoli:exTemp/eoli:beginEnd/eoli:end"/>
                </font>
                </div>
            </xsl:if>
            <br/>
            <div align="left">
            <img alt="Browse Image" >
            <xsl:attribute name="src">
            <xsl:value-of select="eoli:bgFileName"/>
            </xsl:attribute>
            </img>
            </div>
        </xsl:when>
        <xsl:otherwise>
            <font class="stylesheetSmText">No preview</font>
        </xsl:otherwise>
        </xsl:choose>
        </xsl:for-each>
        </td>
        </tr>
<!-- end Browse images -->
    </table>
    </xsl:when>
    </xsl:choose>
</xsl:template>
<xsl:template match="mass:getOrderOutput" mode="XML"/>
<!-- ***************************************************************************
	       Templates used to generate XML input msg for workflow Present
     *************************************************************************** !-->
<xsl:template match="mass:sendPresentInput" mode="XML">
    <eoli:presentRequest>
    <eoli:idCitation>
    <eoli:resTitle>
        <xsl:value-of select="productId"/>
    </eoli:resTitle>
    </eoli:idCitation>
    <eoli:presentation>
        <xsl:value-of select="presentation"/>
     </eoli:presentation>
     <eoli:collectionId>
        <xsl:value-of select="collection"/>
     </eoli:collectionId>
    </eoli:presentRequest>
</xsl:template>
<!-- The following template is defined for backward compatibility for services that are implemented with the mass.xsl !-->
<xsl:template match="mass:processPresentOutputMsg">
<xsl:call-template name="mass:processPresentOutputMsg" />
</xsl:template >
<!-- ***************************************************************************
	       Templates used to generate Present output HTML
     *************************************************************************** !-->
<!-- Default HTML template of the SearchOutput node. -->
<xsl:template name="mass:processPresentOutputMsg">
    <!-- Status OK-->
    <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusId='0'">
    <table  border="0"  cellspacing="0" cellpadding="0" width="100%" class="lghtbloc">
    <xsl:for-each select="mass:searchOutput/mass:retrievedData/mass:Metadata">
            <tr><td><xsl:apply-templates select="." /></td> </tr>
        </xsl:for-each>
        </table>
    </xsl:if>
    <!-- Status not OK -->
    <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusId!='0'">
        <table class="lghtbloc" border="1"  cellspacing="0" cellpadding="10" width="100%" height="100%">
        <tr>
        <td width="100%" align="left" valign="top">
        <font class="stylesheetBoldText">Present Status : </font>
        <font class="stylesheetText">
        <xsl:value-of select="mass:searchOutput/mass:statusInfo/mass:statusId"/>
        <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusMsg!= ''">, <xsl:value-of select="mass:searchOutput/mass:statusInfo/mass:statusMsg"/>
        </xsl:if>
        </font>
        </td>
        </tr>
        </table>
    </xsl:if>
</xsl:template>
<!-- ***************************************************************************
	       Templates used to process service Search information
            mass:multiSearchOutput
     *************************************************************************** !-->
<xsl:template match="mass:multiCataloguesSearchOutputMsg">
    <xsl:call-template name="mass:multiCataloguesSearchOutputMsg" />
</xsl:template>

<!-- Default HTML template of the getSearchOutput node. -->
<xsl:template name="mass:multiCataloguesSearchOutputMsg">
<!-- case General staus is OK-->
<table  border="0"  cellspacing="0" cellpadding="0" width="100%" class="lghtbloc">
<xsl:call-template name="mass:getMaxNbrOfItemsTemplate" />
<xsl:if test="mass:statusInfo/mass:statusId='0'">
    <xsl:if test="count(mass:searchOutputs)>'0'">
        <tr><td>
        <table border="1"  id="rsTable" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <xsl:call-template name="mass:searchOutputHeader" />
        </tr>
        <xsl:for-each select="mass:searchOutputs">
            <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusId='0'">
                <xsl:if test="mass:searchOutput/mass:hits>'0'">
                    <xsl:choose>
                    <xsl:when test="$displayFilter='ALLRESULTS'">
                        <xsl:apply-templates select="mass:searchOutput/mass:viewFileResult" />
						<xsl:apply-templates select="mass:searchOutput/mass:viewEmbeddedResult" />
                    </xsl:when>
                    </xsl:choose>
                    <xsl:for-each select="mass:searchOutput/mass:retrievedData/mass:Metadata">
                        <tr>
                        <xsl:apply-templates select="." />
                        </tr>
                    </xsl:for-each>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
        </table>
        </td></tr>
        <xsl:choose>
        <xsl:when test="$displayFilter='ALLRESULTS'">
            <script type="text/javascript" LANGUAGE="JavaScript" >
            displayGMLfilesFromPortal();
            </script>
        </xsl:when>
        </xsl:choose>
    </xsl:if>
    <xsl:if test="count(mass:searchOutputs)='0'">
        <tr>
        <td>
        <table border="1"  cellspacing="0" cellpadding="0" width="100%">
        <tr><td align="center" height="100">
        <font class="stylesheetBoldText"><i>No retrieved data.</i></font>
        </td></tr>
        </table>
        </td>
        </tr>
    </xsl:if>
</xsl:if>
<!-- display the statys-->
<xsl:if test="$displayFilter='ALLRESULTS'">
<tr>
<td>
    <table border="1"  cellspacing="0" cellpadding="0" width="100%">
    <tr><td>
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <!-- general status-->
        <script language="JavaScript" type="text/javascript">
        displayTimeLine();
        top.window.writeGeneralStatusCode('<table><tr><td width="20%" align="left"><font class="stylesheetBoldText">General Search Status :</font></td><td width="80%" align="left"><font class="stylesheetText"><xsl:if test="mass:statusInfo/mass:statusId='0'">OK</xsl:if><xsl:if test="mass:statusInfo/mass:statusId!='0'"><xsl:value-of select="mass:statusInfo/mass:statusId"/></xsl:if><xsl:if test="mass:statusInfo/mass:statusMsg!= ''">, <xsl:value-of select="mass:statusInfo/mass:statusMsg"/></xsl:if></font></td></tr></table>');
        </script>
        <!-- status per collections -->
        <xsl:for-each select="mass:searchOutputs">
            <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusId!='0'">
                <tr><td nowrap="true" width="50%" align="left">
                <font class="stylesheetBoldText">Search Collection <xsl:value-of select="eoli:collectionId"/> Status :</font>
                </td>
                <td width="50%" align="left">
                <font class="stylesheetText">              
                <xsl:if test="mass:searchOutput/mass:statusInfo/mass:statusMsg!= ''"><xsl:value-of select="mass:searchOutput/mass:statusInfo/mass:statusMsg"/>
                </xsl:if>
                </font>
                </td>
                </tr>
            </xsl:if>
        </xsl:for-each>
        </table>
    </td>
    </tr>
    </table>
</td>
</tr>
</xsl:if>
</table>
</xsl:template>
<!--  *********************************************************
	             mass:searchOutputHeader
      *********************************************************  -->
<xsl:template name="mass:searchOutputHeader">
    <td  class="stylesheetHeader">Product Identifier</td>
    <td class="stylesheetHeader">Collection</td>
    <td  class="stylesheetHeader">Platform</td>
    <td  class="stylesheetHeader">Acquisition Date/Time</td>
    <td  class="stylesheetHeader">Satellite Domain</td>
    <td  class="stylesheetHeader">Percentage of Overlap</td>
    <td class="stylesheetHeader">Graphical Overview</td>
</xsl:template>
<!-- Default XML template  -->
<xsl:template name="mass:getMaxNbrOfItemsTemplate">
   <script  language="JavaScript" >
       function getMaxNumberOfSelectedItems() {return 1;}
   </script>
</xsl:template>
</xsl:stylesheet>