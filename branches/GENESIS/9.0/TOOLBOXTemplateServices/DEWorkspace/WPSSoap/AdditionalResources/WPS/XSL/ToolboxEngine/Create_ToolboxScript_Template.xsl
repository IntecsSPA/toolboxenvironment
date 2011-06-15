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


<xsl:comment>
 #############################################################################
 
  $Id: execute_<xsl:value-of select="ows:Identifier"/>.sh,v <!--xsl:value-of select="$version"/--> <!--xsl:value-of  select="current-dateTime()"/-->  $
  UPDATED:	<!--xsl:value-of select="fn:current-dateTime()"/-->
 
  MODULE:   	<xsl:value-of select="$module"/>
 
  AUTHOR(S):	<xsl:value-of select="$authors"/>
                
  PURPOSE:  	<xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>
 
  COPYRIGHT: <xsl:value-of select="$copyright"/> 
  
 #############################################################################
 </xsl:comment>
                
                                                                              

<xsl:comment>
  ********* External TOOLBOX Variables ***************************************************************************************************************************************
  -----WPS Service Varibles
  *OUTPUT_REPOSITORY : Output Repository Directory (This Folder should contain all output files)
  *INPUT_REPOSITORY: Input Repository Directory (This Folder should contain all Input files downloaded)
  *GENERAL_SERVICES_RESOURCES : Services General Resources Directory (This Folder contains general resources for all Toolbox services ).
  *SERVICE_RESOURCES : Service Resources Directory (This Folder contains only the resources of the current service)
  *TEMP_REPOSITORY : Temporaney Data created Repository Directory (This Folder should contain all the temporaney files)		
  *WPS_PROCESSING_NAME: WPS Processing Name
  *INSTANCE_VALUE: Instance Operation Value (for multiple Instances)
  *LOG_FILE: File path for the process text LOG
  *STATUS_FILE: File path for XML Status file (This XML File should contain the status information of the operation)
  *DOWNLOAD_INPUT_FLAG: This booelan variable is true if the ComplexData Reference Inputs are downloaded or false if the ComplexData Reference Inputs are not  downloaded
  *TOOLBOX_ENGINE_PROCEDURE_FOLDER: Folder wich contains the global procedures of the TOOLBOX engine.  
  *************************************************************************************************************************************************************************--
   </xsl:comment>

		
<xsl:comment>
  ********* Input TOOLBOX Variables ****************************************************************************************************************************************
<xsl:for-each select="DataInputs/Input">
    <xsl:if test="ComplexData">
      <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_COMPLEX_DATA: (Array of String that contains the multiple Complex Value "<xsl:value-of select="ows:Identifier"/>" Input defined by reference in the Execute Request.)
      (If the DOWNLOAD_INPUT_FLAG variable is 'true' the value is the local path of the downloaded resource. 
       If the DOWNLOAD_INPUT_FLAG variable is 'false' the value is the resource reference.)
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
   <xsl:if test="ComplexData/Default/Format/MimeType">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_COMPLEX_DATA_MimeType: Array of String that contains the Mime Types for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
       (Each value correspond at the Mime Type of the file, called like the array position of the Mime Type value, in the "<xsl:value-of select="ows:Identifier"/> array)
       (Supported:  <xsl:for-each select="ComplexData/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Schema">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_COMPLEX_DATA_Schema: Array of String that contains the Schemas for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
       (Each value correspond at the Schema URL of the file, called like the array position of the Schema URL value, in the "<xsl:value-of select="ows:Identifier"/> array)
       (Supported:  <xsl:for-each select="ComplexData/Supported/Format/Schema"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Encoding">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_COMPLEX_DATA_Encoding: Array of String that contains the Encodings for the multiple "<xsl:value-of select="ows:Identifier"/>" Complex Data. 
       (Each value correspond at the Encoding of the file, called like the array position of the Encoding value, in the "<xsl:value-of select="ows:Identifier"/> array)
       (Supported:  <xsl:for-each select="ComplexData/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
        </xsl:when>
        <xsl:otherwise>
  *<xsl:value-of select="ows:Identifier"/>: Value of Complex Value "<xsl:value-of select="ows:Identifier"/>" defined by reference in the Execute Request.
    (If the DOWNLOAD_INPUT_FLAG variable is 'true' the value is the local path of the downloaded resource. 
    If the DOWNLOAD_INPUT_FLAG variable is 'false' the value is the resource reference.)
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
   <xsl:if test="ComplexData/Default/Format/MimeType">
  *<xsl:value-of select="ows:Identifier"/>_MimeType: Mime Type of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
       (Mime Type Supported:  <xsl:for-each select="ComplexData/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Schema">
  *<xsl:value-of select="ows:Identifier"/>_Schema: Schema of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
       (Schema Supported:  <xsl:for-each select="ComplexData/Supported/Format/Schema"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
<xsl:if test="ComplexData/Default/Format/Encoding">
  *<xsl:value-of select="ows:Identifier"/>_Encoding: Encoding of "<xsl:value-of select="ows:Identifier"/>" Input Complex Data. 
       (Encoding Supported:  <xsl:for-each select="ComplexData/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>



        <!-- apply import complex Data template for not default complex data parsing-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="LiteralData">
      <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL: Values List separted from ',' that contains the multiple Literal Value "<xsl:value-of select="ows:Identifier"/>" Input.
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralData/ows:DataType">       (Data Type: <xsl:value-of select="LiteralData/ows:DataType"/>)</xsl:if>
<xsl:if test="LiteralData/ows:AllowedValues">       (Allowed Values:  <xsl:for-each select="LiteralData/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if>
    <xsl:if test="LiteralData/UOMs">
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL_UOM: Units of Measure List separted from ',' for the multiple  "<xsl:value-of select="ows:Identifier"/>" Literal Data. 
       (Each value correspond at the Unit of Measure of the value in the same position in the "<xsl:value-of select="ows:Identifier"/>_MULTIPLE_LITERAL_LIST")
       (UOMs Supported:  <xsl:for-each select="LiteralData/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)



    </xsl:if> 
        </xsl:when>
        <xsl:otherwise>
  *<xsl:value-of select="ows:Identifier"/>: Literal Data "<xsl:value-of select="ows:Identifier"/>" Input value.
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralData/ows:DataType">       (Data Type: <xsl:value-of select="LiteralData/ows:DataType"/>)</xsl:if>
 <xsl:if test="LiteralData/ows:AllowedValues">       (Allowed Values:  <xsl:for-each select="LiteralData/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if> 
    <xsl:if test="LiteralData/UOMs">
  *<xsl:value-of select="ows:Identifier"/>_UOM: Unit of Measure of "<xsl:value-of select="ows:Identifier"/>" Literal Data. 
       (Supported:  <xsl:for-each select="LiteralData/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)

    </xsl:if>  

        </xsl:otherwise>
      </xsl:choose>

    </xsl:if>
    <xsl:if test="BoundingBoxData">
    <xsl:choose>      
        <xsl:when test="@maxOccurs &gt; 1">
  The "<xsl:value-of select="ows:Identifier"/>" Multiple Bounding Box Input information is contained in two environment variables (one for the Lower Cornerslist and one for Upper Corners list ): 
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_Lower_Corner: Lower Corner Coordinate List separted from '*' for the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_Upper_Corner: Upper Corner Coordinate List separted from '*' for the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
  *<xsl:value-of select="ows:Identifier"/>_MULTIPLE_BBOX_EPSG: EPSG value list separted from ',' of  the "<xsl:value-of select="ows:Identifier"/>" multiple Input Bounding Box Data.
       (EPSG Supported:  <xsl:for-each select="BoundingBoxData/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
        </xsl:when>
        <xsl:otherwise>
  The "<xsl:value-of select="ows:Identifier"/>" Bounding Box Input information is contained in two environment variables (one for the Lower Corner and one for Upper Corner): 
  *<xsl:value-of select="ows:Identifier"/>_Lower_Corner: Lower Corner Coordinate of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
  *<xsl:value-of select="ows:Identifier"/>_Upper_Corner: Upper Corner Coordinate of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
       (Input Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
  *<xsl:value-of select="ows:Identifier"/>_EPSG: EPSG of "<xsl:value-of select="ows:Identifier"/>" Input Bounding Box Data.
       (EPSG Supported:  <xsl:for-each select="BoundingBoxData/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
        </xsl:otherwise>
     </xsl:choose>   


    </xsl:if>

</xsl:for-each>
 					 
 					 
  ***************************************************************************************************************************************
</xsl:comment>


<xsl:comment>
  ********* Output TOOLBOX Variables *************************************************************************************************
<xsl:for-each select="ProcessOutputs/Output">

<xsl:choose>
  <xsl:when test="ComplexOutput">
    <xsl:choose>
      <xsl:when test="ComplexOutput/Default/Format/Schema">
        <xsl:apply-templates select="ComplexOutput/Default/Format/Schema"/>
      </xsl:when>
      <xsl:otherwise>
  *<xsl:value-of select="ows:Identifier"/>: Name of the file that will contain the "<xsl:value-of select="ows:Identifier"/>" Complex Data Output. (This file must be saved in theOutput Repository Folder. This folder is defined by the environment variable OUTPUT_REPOSITORY)
  *<xsl:value-of select="ows:Identifier"/>_OUTPUT_PATH: Path of the file that will contain the "<xsl:value-of select="ows:Identifier"/>" Complex Data Output. (This PATH is obtained from the concatenation of the environment variable "OUTPUT_REPOSITORY" and the the environment variable "<xsl:value-of select="ows:Identifier"/>")
       (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
    <xsl:if test="ComplexOutput/Default/Format/MimeType">
  *<xsl:value-of select="ows:Identifier"/>_MimeType: <xsl:value-of select="ows:Identifier"/> Output Mime Type (Mime type, for the <xsl:value-of select="ows:Identifier"/> Complex Output, required in the Execute Request).
       (Mime Type Supported:  <xsl:for-each select="ComplexOutput/Supported/Format/MimeType"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
    <xsl:if test="ComplexOutput/Default/Format/Encoding">
  *<xsl:value-of select="ows:Identifier"/>_Encoding: <xsl:value-of select="ows:Identifier"/> Output Encoding .(Encoding, for the <xsl:value-of select="ows:Identifier"/> Complex Output, required in the Execute Request).
       (Encoding Supported:  <xsl:for-each select="ComplexOutput/Supported/Format/Encoding"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
      </xsl:otherwise> 
    </xsl:choose> 
    
    
  </xsl:when>
  <xsl:otherwise>
    <xsl:choose>
      <xsl:when test="LiteralOutput">
  *<xsl:value-of select="ows:Identifier"/>: This environment variable will contain the value for "<xsl:value-of select="ows:Identifier"/>" Literal Data Output.
       (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="ows:Abstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
<xsl:if test="LiteralOutput/ows:DataType">       (Data Type: <xsl:value-of select="LiteralOutput/ows:DataType"/>)</xsl:if>
 <xsl:if test="LiteralOutput/ows:AllowedValues">       (Allowed Values:  <xsl:for-each select="LiteralOutput/ows:AllowedValues/ows:Value"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)</xsl:if> 
<xsl:if test="LiteralOutput/UOMs/Default/ows:UOM">
  *<xsl:value-of select="ows:Identifier"/>_UOM: <xsl:value-of select="ows:Identifier"/> Literal Output Unit of Measure.(Unit of Measure, for the <xsl:value-of select="ows:Identifier"/> LiteralOutput, required in the Execute Request).
       (UOMs Supported:  <xsl:for-each select="LiteralOutput/UOMs/Supported/ows:UOM"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)
    </xsl:if>
      </xsl:when>
      
      
      
      <xsl:otherwise>
      <!-- BoundingBoxOutput -->
  The "<xsl:value-of select="ows:Identifier"/>" Bounding Box Output information will be contained in four environment variables (one for each cardinal directions): 
  *<xsl:value-of select="ows:Identifier"/>_N: This variable will contain the NORTH coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box  Output.
   <xsl:value-of select="ows:Identifier"/>_S: This variable will contain the SOUTH coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box  Output.
   <xsl:value-of select="ows:Identifier"/>_E: This variable will contain the EAST coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box  Output.
   <xsl:value-of select="ows:Identifier"/>_W: This variable will contain the WEST coordinate for the  "<xsl:value-of select="ows:Identifier"/>" Bounding Box  Output.
       (Output Description: <xsl:value-of select="ows:Abstract"/>)
  *<xsl:value-of select="ows:Identifier"/>_EPSG: EPSG of "<xsl:value-of select="ows:Identifier"/>" Bounding Box Output. (Projection Code, for the <xsl:value-of select="ows:Identifier"/> Bounding Box Output, required in the Execute Request).
       (Supported:  <xsl:for-each select="BoundingBoxOutput/Supported/CRS"><xsl:value-of select="."/><xsl:text>  </xsl:text></xsl:for-each>)



      </xsl:otherwise>    
    </xsl:choose>
  </xsl:otherwise>
</xsl:choose>
</xsl:for-each>
 					 
 					 
 ******************************************************************************************************************************
 </xsl:comment>

<xsl:comment>
  ********* TOOLBOX Procedure *************************************************************************************************
  
  ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
  -SetCurrentStatus: Procedure for setting the percentage of completion (interval [0..100]) of the process 
                        example:
                                    <loadProcedure name="setCurrentStatus">
                                        <string>${TOOLBOX_ENGINE_PROCEDURE_FOLDER}SetCurrentStatus.tscript</string>
                                    </loadProcedure>
                                      <call procedure="setCurrentStatus">
                                        <argument name="currentStatus">
                                           	<literal value="80"/>  
                                        </argument>
                                      </call>
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------




 ******************************************************************************************************************************
 </xsl:comment>



<xsl:comment> ------------------------------  TOOLBOX SCRIPT ---------------------------------------------------------------------------------------------------------------------------------------</xsl:comment>











                             <xsl:comment>  Insert TOOLBOX Script</xsl:comment>














<xsl:comment>------------------------------  END TOOLBOX SCRIPT --------------------------------------------------------------------------------------------------------------------------------</xsl:comment>
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





<!-- *********************************** Template for Complex Output Imported *********************************************************** -->
<!-- Currently the include XSL file is not supported-->

<xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']">

<xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
<xsl:variable name="wcsAbstract" select="../../../../ows:Abstract"/>
  *<xsl:value-of select="$wcsIdentifier"/>: Name of the GEOTIFF file that will contain the "<xsl:value-of select="$wcsIdentifier"/>" WCS/WMS Layer Output to be published on Geoserver. (This file must be saved in theOutput Repository Folder. This folder is defined by the environment variable OUTPUT_REPOSITORY)
  *<xsl:value-of select="$wcsIdentifier"/>_OUTPUT_PATH: Path of the GEOTIFF file that will contain the "<xsl:value-of select="$wcsIdentifier"/>" WCS/WMS Layer Output to be published on Geoserver. (This PATH is obtained from the concatenation of the environment variable "OUTPUT_REPOSITORY" and the the environment variable "<xsl:value-of select="$wcsIdentifier"/>")
       (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="$wcsAbstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)

</xsl:template>

<!--xsl:template match="Schema[. ='complexdata/WMSLayer.xsd']">




</xsl:template-->

<xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']">

<xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/>
<xsl:variable name="wfsAbstract" select="../../../../ows:Abstract"/> 
  *<xsl:value-of select="$wfsIdentifier"/>: Name of the Folder that will contain the Shape file (for the "<xsl:value-of select="$wfsIdentifier"/>" WFS/WMS Layer Output) to be published on Geoserver. (This folder must be contained in theOutput Repository Folder. This folder is defined by the environment variable OUTPUT_REPOSITORY)
  *<xsl:value-of select="$wfsIdentifier"/>_OUTPUT_PATH: Path of the Folder that will contain the Shape file (for the <xsl:value-of select="$wfsIdentifier"/>" WFS/WMS Layer Output) to be published on Geoserver. (This PATH is obtained from the concatenation of the environment variable "OUTPUT_REPOSITORY" and the the environment variable "<xsl:value-of select="$wfsIdentifier"/>")
       (Output Description: <xsl:call-template name="replaceAll"><xsl:with-param name="string" select="$wfsAbstract"/><xsl:with-param name="char"><xsl:text>&#10;</xsl:text></xsl:with-param><xsl:with-param name="replaceTo"><xsl:text>&#10; </xsl:text></xsl:with-param></xsl:call-template>)
</xsl:template>

 
<!-- *********************************************************************************************************************************** -->
</xsl:stylesheet>
