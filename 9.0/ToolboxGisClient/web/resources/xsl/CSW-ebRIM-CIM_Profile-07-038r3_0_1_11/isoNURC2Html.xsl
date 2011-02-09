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

        <!-- GENERAL METADATA INFORMATION START -->
       
        <xsl:call-template name="metadataInfoSectionTitleTemplate">
              <xsl:with-param name="title">General Data Information</xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">ISO Identifer</xsl:with-param>
              <xsl:with-param name="value" select="gmd:fileIdentifier/gco:CharacterString"/>
        </xsl:call-template>

        <xsl:if test="gmd:parentIdentifier/gco:CharacterString">
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Parent Identifer</xsl:with-param>
              <xsl:with-param name="value" select="gmd:parentIdentifier/gco:CharacterString"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="gmd:dateStamp/gco:Date">
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Metadata Creation Date</xsl:with-param>
              <xsl:with-param name="value" select="gmd:dateStamp/gco:Date"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="gmd:dateStamp/gco:DateTime">
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Metadata Creation Date/Time</xsl:with-param>
              <xsl:with-param name="value" select="gmd:dateStamp/gco:DateTime"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="gmd:metadataStandardName/gco:CharacterString">
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Metadata Standard Name</xsl:with-param>
              <xsl:with-param name="value" select="gmd:metadataStandardName/gco:CharacterString"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="gmd:metadataStandardVersion/gco:CharacterString">
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Metadata Standard Version</xsl:with-param>
              <xsl:with-param name="value" select="gmd:metadataStandardVersion/gco:CharacterString"/>
            </xsl:call-template>
        </xsl:if>
      <!-- GENERAL METADATA INFORMATION END -->

        <!-- CONTACT INFORMATION START -->
        <xsl:for-each select="gmd:contact/gmd:CI_ResponsibleParty">
          <xsl:call-template name="metadataInfoSectionTitleTemplate">
              <xsl:with-param name="title">Contact Information</xsl:with-param>
          </xsl:call-template>  
          <xsl:if test="gmd:organisationName/gco:CharacterString">
            <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Organisation Name</xsl:with-param>
              <xsl:with-param name="value" select="gmd:organisationName/gco:CharacterString"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString">
            <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Electronic Mail Address</xsl:with-param>
              <xsl:with-param name="value" select="gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:for-each select="gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource">
            <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">"<xsl:value-of select="gmd:function/gmd:CI_OnLineFunctionCode"/>" Online Resource</xsl:with-param>
              <xsl:with-param name="value" select="gmd:linkage/gmd:URL"/>
            </xsl:call-template>
          </xsl:for-each>
          <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Contact Role</xsl:with-param>
              <xsl:with-param name="value" select="gmd:role/gmd:CI_RoleCode"/>
          </xsl:call-template>
      </xsl:for-each>
      <!-- CONTACT INFORMATION END -->

      <!-- REFERENCE SYSTEM INFO START -->
        <xsl:for-each select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier">
          <xsl:call-template name="metadataInfoSectionTitleTemplate">
              <xsl:with-param name="title">Reference System Info</xsl:with-param>
          </xsl:call-template>
          <xsl:if test="gmd:RS_Identifier/gmd:code/gco:CharacterString">
            <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">Code</xsl:with-param>
              <xsl:with-param name="value" select="gmd:RS_Identifier/gmd:code/gco:CharacterString"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:for-each select="gmd:RS_Identifier/gmd:authority/gmd:CI_Citation">
             <xsl:apply-templates select="."/>
          </xsl:for-each>
          <xsl:for-each select="gmd:RS_Identifier/gmd:authority/gmd:CI_Citation/gmd:date/gmd:CI_Date">
            <xsl:call-template name="metadataInfoRowTemplate">
              <xsl:with-param name="key">"<xsl:value-of select="gmd:dateType/gmd:CI_DateTypeCode"/>" Date</xsl:with-param>
              <xsl:with-param name="value" select="gmd:date/.[1]"/>
            </xsl:call-template>
          </xsl:for-each>
      </xsl:for-each>
      <!-- REFERENCE SYSTEM INFO END -->


      <!-- DATA IDENTIFICATION START -->
      <xsl:if test="gmd:identificationInfo/gmd:MD_DataIdentification">
          <xsl:call-template name="metadataInfoSectionTitleTemplate">
              <xsl:with-param name="title">Data Identification</xsl:with-param>
          </xsl:call-template>

          <!-- citation -->
          <xsl:for-each select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation">
              <xsl:apply-templates select="."/>
          </xsl:for-each>
      </xsl:if>
      <!-- DATA IDENTIFICATION END -->



       <!-- SERVICE IDENTIFICATION START -->
      <xsl:if test="gmd:identificationInfo/srv:SV_ServiceIdentification">
          <xsl:call-template name="metadataInfoSectionTitleTemplate">
              <xsl:with-param name="title">Service Identification</xsl:with-param>
          </xsl:call-template>

      </xsl:if>
      <!-- SERVICE IDENTIFICATION END -->


    </table>


</xsl:template>


<xsl:template match="gmd:CI_Citation">

    <xsl:if test="gmd:title/gco:CharacterString">
        <xsl:call-template name="metadataInfoRowTemplate">
                  <xsl:with-param name="key">Title</xsl:with-param>
                  <xsl:with-param name="value" select="gmd:title/gco:CharacterString"/>
        </xsl:call-template> 
    </xsl:if>


</xsl:template>




<!-- HTML TEMPLATES START -->
    <xsl:template name="metadataInfoRowTemplate">
        <xsl:param name="key"/>
        <xsl:param name="value"/>
        <tr width='100%'>
           <td BGCOLOR='{$titleColor}'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'><xsl:value-of select="$key"/></b></td>
           <td BGCOLOR='{$valueColor}'><p style='font-size: {$fontSize}px;'><xsl:value-of select="$value"/></p></td>
        </tr>
    </xsl:template>

    <xsl:template name="metadataInfoSectionTitleTemplate">
        <xsl:param name="title"/>

        <tr width='100%' rowspan="2">
                <td colspan='2' BGCOLOR='{$titleColor}' align='center'><b style='color: {$titleFontColor}; font-size: {$fontSize}px;'><xsl:value-of select="$title"/></b></td>
        </tr>
    </xsl:template>
<!-- HTML TEMPLATES END -->

</xsl:stylesheet>
