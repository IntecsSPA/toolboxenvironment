<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
  <xsl:output method="text"/>
  <xsl:param name="authors">Insert autors</xsl:param>
  <xsl:param name="module">TEST MODULE </xsl:param>
  <xsl:param name="copyright">(C) 2009 Intecs Informatica e tecnologia del Software SpA</xsl:param>

  <!-- 
INCLUDE SECTION -->
<!--xsl:include href="ComplexData/WCSLayer_UpadateGrassTemplate.xsl"/-->
<!-- END INCLUDE SECTION-->

  <xsl:template match="//ProcessDescription">
#############################################################################
#
# $Id: execute_<xsl:value-of select="ows:Identifier"/>.sh,v <!--xsl:value-of select="$version"/--> <!--xsl:value-of  select="current-dateTime()"/-->  $
# UPDATED:	<!--xsl:value-of select="fn:current-dateTime()"/-->
#
# MODULE:   	<xsl:value-of select="$module"/>
#
# AUTHOR(S):	<xsl:value-of select="$authors"/>
#               
# PURPOSE:  	<xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>
#
# COPYRIGHT: <xsl:value-of select="$copyright"/> 
#
#               
#############################################################################

# ********* External Variables ************************************************************************************************
# -----WPS Service Varibles
# *OUTPUT_REPOSITORY : Output Repository Directory 
# *GENERAL_SERVICES_RESOURCES : General Services Resources Directory (This Folder contains all Toolbox services resources)
# *SERVICE_RESOURCES : Service Resources Directory (This Folder contains resources decicate service)
# *TEMP_REPOSITORY : Temporaney Data created Repository Directory		
# *WPS_PROCESSING_NAME: WPS Processing Name
# *INSTANCE_VALUE: Instance Operation Value (for multiple Instances)		
# *STATUS_FILE: File path for XML Status file		
		
#------Processing Variables	
<xsl:for-each select="DataInputs/Input">
    <xsl:if test="ComplexData">
      <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_FOLDER: (Folder that contains the multiple Complex Value "<xsl:value-of select="ows:Identifier"/>" Input defined by reference in the Execute Request.)
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
   <xsl:if test="ComplexData/Default/Format/MimeType">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_MimeType: Mime Types List separted from ',' for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
#      (Each value correspond at the Mime Type of the file, called like the list position of the Mime Type value, in the "<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_FOLDER" folder)
#      (Supported:  <xsl:for-each select="ComplexData/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Schema">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_Schema: Schemas List separted from ',' for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
#      (Each value correspond at the Schema URL of the file, called like the list position of the Schema URL value, in the "<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_FOLDER" folder)
#      (Supported:  <xsl:for-each select="ComplexData/Supported/Format/Schema"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Encoding">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_Encoding: Encodings List separted from ',' for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
#      (Each value correspond at the Encoding of the file, called like the list position of the Encoding value, in the "<xsl:value-of select="ows:Identifier"/>_MULTIPLE_INPUT_FOLDER" folder)
#      (Supported:  <xsl:for-each select="ComplexData/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
        </xsl:when>
        <xsl:otherwise>
# *<xsl:value-of select="ows:Identifier"/>: Local Path of Complex Value "<xsl:value-of select="ows:Identifier"/>" defined by reference in the Execute Request. 
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
   <xsl:if test="ComplexData/Default/Format/MimeType">
# *<xsl:value-of select="ows:Identifier"/>_MimeType: Mime Type of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
#      (Mime Type Supported:  <xsl:for-each select="ComplexData/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Schema">
# *<xsl:value-of select="ows:Identifier"/>_Schema: Schema of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
#      (Schema Supported:  <xsl:for-each select="ComplexData/Supported/Format/Schema"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Encoding">
# *<xsl:value-of select="ows:Identifier"/>_Encoding: Encoding of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
#      (Encoding Supported:  <xsl:for-each select="ComplexData/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>



        <!-- apply import complex Data template for not default complex data parsing-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="LiteralData">
      <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL_LIST: Values List separted from ',' that contains the multiple Literal Value "<xsl:value-of select="ows:Identifier"/>" Input.
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralData/ows:DataType">#      (Data Type: <xsl:value-of select="LiteralData/ows:DataType"/>)</xsl:if>
<xsl:if test="LiteralData/ows:AllowedValues">#      (Allowed Values:  <xsl:for-each select="LiteralData/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if>
    <xsl:if test="LiteralData/UOMs">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL_UOM_LIST: Units of Measure List separted from ',' for the multiple  "<xsl:value-of select="ows:Identifier"/>" Literal Data. 
#      (Each value correspond at the Unit of Measure of the value in the same position in the "<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL_LIST")
#      (UOMs Supported:  <xsl:for-each select="LiteralData/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)



    </xsl:if> 
        </xsl:when>
        <xsl:otherwise>
# *<xsl:value-of select="ows:Identifier"/>: Literal Datata "<xsl:value-of select="ows:Identifier"/>" Input value.
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralData/ows:DataType">#      (Data Type: <xsl:value-of select="LiteralData/ows:DataType"/>)</xsl:if>
 <xsl:if test="LiteralData/ows:AllowedValues">#      (Allowed Values:  <xsl:for-each select="LiteralData/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if> 
    <xsl:if test="LiteralData/UOMs">
# *<xsl:value-of select="ows:Identifier"/>_UOM: Unit of Measure of "<xsl:value-of select="ows:Identifier"/>" Literal Data. 
#      (Supported:  <xsl:for-each select="LiteralData/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)

    </xsl:if>  

        </xsl:otherwise>
      </xsl:choose>

    </xsl:if>
    <xsl:if test="BoundingBoxData">
    <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_LOWER_CORNER: Lower Corner Coordinate List separted from '*' for the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_UPPER_CORNER: Upper Corner Coordinate List separted from '*' for the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
# *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_EPSG: EPSG value list separted from ',' of  the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
#      (EPSG Supported:  <xsl:for-each select="BoundingBoxData/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
        </xsl:when>
        <xsl:otherwise>
# *<xsl:value-of select="ows:Identifier"/>_LOWER_CORNER: Lower Corner Coordinate of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
# *<xsl:value-of select="ows:Identifier"/>_UPPER_CORNER: Upper Corner Coordinate of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
#      (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
# *<xsl:value-of select="ows:Identifier"/>_EPSG: EPSG of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
#      (EPSG Supported:  <xsl:for-each select="BoundingBoxData/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
        </xsl:otherwise>
     </xsl:choose>   


    </xsl:if>

</xsl:for-each>

<xsl:for-each select="ProcessOutputs/Output">

<xsl:choose>
  <xsl:when test="ComplexOutput">
    <xsl:choose>
      <xsl:when test="ComplexOutput/Default/Format/Schema">
        <xsl:apply-templates select="ComplexOutput/Default/Format/Schema"/>
      </xsl:when>
      <xsl:otherwise>
# *<xsl:value-of select="ows:Identifier"/>: File Name of the "<xsl:value-of select="ows:Identifier"/>" Complex Data Output.
#      (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
    <xsl:if test="ComplexOutput/Default/Format/MimeType">
# *<xsl:value-of select="ows:Identifier"/>_MimeType: <xsl:value-of select="ows:Identifier"/> Output Mime Type.
#      (Mime Type Supported:  <xsl:for-each select="ComplexOutput/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
    <xsl:if test="ComplexOutput/Default/Format/Encoding">
# *<xsl:value-of select="ows:Identifier"/>_Encoding: <xsl:value-of select="ows:Identifier"/> Output Encoding.
#      (Encoding Supported:  <xsl:for-each select="ComplexOutput/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
      </xsl:otherwise> 
    </xsl:choose> 
    
    
  </xsl:when>
  <xsl:otherwise>
    <xsl:choose>
      <xsl:when test="LiteralOutput">
# *<xsl:value-of select="ows:Identifier"/>: This variabile will contain the value for "<xsl:value-of select="ows:Identifier"/>" Literal Data Output.
#      (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10;#</xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralOutput/ows:DataType">#      (Data Type: <xsl:value-of select="LiteralOutput/ows:DataType"/>)</xsl:if>
 <xsl:if test="LiteralOutput/ows:AllowedValues">#      (Allowed Values:  <xsl:for-each select="LiteralOutput/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if> 
<xsl:if test="LiteralOutput/UOMs/Default/ows:UOM">
# *<xsl:value-of select="ows:Identifier"/>_UOM: <xsl:value-of select="ows:Identifier"/> Literal Output Unit of Measure.
#      (UOMs Supported:  <xsl:for-each select="LiteralOutput/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
      </xsl:when>
      
      
      
      <xsl:otherwise>
      <!-- BoundingBoxOutput -->
# *<xsl:value-of select="ows:Identifier"/>_N: This variabile will contain the NORTH coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box Data Output.
#  <xsl:value-of select="ows:Identifier"/>_S: This variabile will contain the SOUTH coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box Data Output.
#  <xsl:value-of select="ows:Identifier"/>_E: This variabile will contain the EAST coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box Data Output.
#  <xsl:value-of select="ows:Identifier"/>_W: This variabile will contain the WEST coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box Data Output.
#      (Output Description: <xsl:value-of select="ows:Abstract"/>)
# *<xsl:value-of select="ows:Identifier"/>_EPSG: EPSG of "<xsl:value-of select="ows:Identifier"/>" Bounding Box Data Output.
#      (Supported:  <xsl:for-each select="BoundingBoxOutput/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)



      </xsl:otherwise>    
    </xsl:choose>
  </xsl:otherwise>
</xsl:choose>
</xsl:for-each>

#					 
#******************************************************************************************************************************


# ------------------------------  SHELL SCRIPT -------------------------------------------------------------------------------------------------------------------------------------











                              # Add your shell statements here














# ------------------------------  END SHELL SCRIPT ------------------------------------------------------------------------------------------------------------------------------
  </xsl:template>
  

<xsl:template name="replaceAll">
    <xsl:param name="string"/>
    <xsl:param name="char"/> <!--  '&#10;' -->
    <xsl:param name="replaceTo"/>
    <xsl:choose>
        <xsl:when test="contains($string,$char)">
            <xsl:value-of select="substring-before($string,$char)"/>
            <xsl:value-of select="$replaceTo"/>
            <xsl:call-template name="replaceAll">
                <xsl:with-param name="string" select="substring-after($string,$char)"/>
                <xsl:with-param name="char" select="$char"/>
                <xsl:with-param name="replaceTo" select="$replaceTo"/>  
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$string"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
</xsl:stylesheet>
