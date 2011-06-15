﻿<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:tbx="http://toolbox.pisa.intecs.it/WPS/ComplexData/referenceData" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="text"/>
  <!-- xsl:param name="toolboxServiceURL">http://localhost:8080/TOOLBOX</xsl:param> -->
  <xsl:param name="retrivedDataUrls">,A,B,C,D,</xsl:param>
  <xsl:param name="inputNameDataUrls">,A,B,InputRaster,D,</xsl:param>
  <xsl:param name="processingName">RasterClipBBox</xsl:param>
  <xsl:param name="instanceValue">TEST</xsl:param>
  <xsl:param name="shellOutputFolder">/SHELL_OUTPUT_FOLDER/</xsl:param>
  <xsl:param name="shellTempFolder">/SHELL_TEMP_FOLDER/</xsl:param>
  <xsl:param name="downloadFolder">/DOWNLOAD_FOLDER/</xsl:param>
  <xsl:param name="shellLogFileInstance">/SHELL_LOG_FOLDER/SHELL_INSTANCE.txt</xsl:param>
  <xsl:param name="statusFileInstance">/SHELL_LOG_FOLDER/statusPercentage.xml</xsl:param>
  <xsl:param name="shellScriptPath">/SHELL_PATH_SCRIPT/</xsl:param>
  <!-- xsl:include href="ComplexData/CD_ReferenceData.xsl"/ -->
  <xsl:template match="*">
        #!/bin/bash
        	export INSTANCE_VALUE="<xsl:value-of select="$instanceValue"/>"
        	export OUTPUT_REPOSITORY="<xsl:value-of select="$shellOutputFolder"/>"
            export INPUT_REPOSITORY="<xsl:value-of select="$downloadFolder"/>"
        	export TEMP_REPOSITORY="<xsl:value-of select="$shellTempFolder"/>"
            export GENERAL_SERVICES_RESOURCES="<xsl:value-of select="concat( $shellOutputFolder, '../../../../../GeneralServicesResources' )"/>"
            export SERVICE_RESOURCES="<xsl:value-of select="concat( $shellOutputFolder, '../../../Resources' )"/>"
            export SHELL_LOG_FILE="<xsl:value-of select="$shellOutputFolder"/>Log.txt"
        	export WPS_PROCESSING_NAME="<xsl:value-of select="$processingName"/>"
            export STATUS_FILE="<xsl:value-of select="$statusFileInstance"/>"
          <xsl:variable name="processInformation" select="document(string(concat('../../DescribeProcess/DescribeInformation_', $processingName, '.xml')))"/>

            export RESPONSE_STORE="<xsl:value-of select="@storeExecuteResponse"/>"

	
         <xsl:for-each select="child::*"><xsl:variable name="positionIdentifier" select="position()"/><xsl:variable name="nameIdentifier" select="local-name()"/><xsl:variable name="inputIdentifierRequest" select="local-name()"/><xsl:variable name="maxOccursInput"><xsl:number value="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/@maxOccurs"/></xsl:variable><!--  MAXOCC <xsl:value-of select="$maxOccursInput"/>
              ID: <xsl:value-of select="$inputIdentifierRequest"/>--><xsl:choose><xsl:when test="wps:Reference"><xsl:choose><xsl:when test="$maxOccursInput &gt; 1"><xsl:variable name="indentifierPostions"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:value-of select="position()"/>,</xsl:if></xsl:for-each></xsl:variable><xsl:variable name="firstPosition"><xsl:value-of select="substring-before($indentifierPostions, ',')"/></xsl:variable><xsl:if test="$positionIdentifier = $firstPosition">
                    export <xsl:value-of select="local-name()"/>_MULTIPLE_INPUT_FOLDER="<xsl:value-of select="$downloadFolder"/><xsl:value-of select="local-name()"/>"
                  
                    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/MimeType"><xsl:variable name="mimeTypeList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:choose><xsl:when test="wps:Reference/@mimeType"><xsl:value-of select="wps:Reference/@mimeType"/>,</xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/MimeType"/>,</xsl:otherwise></xsl:choose></xsl:if></xsl:for-each></xsl:variable>                 
                            export <xsl:value-of select="local-name()"/>_MULTIPLE_INPUT_MimeType="<xsl:value-of select="substring($mimeTypeList,1,string-length($mimeTypeList)-1)"/>"
                    </xsl:if>
                    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/Encoding"><xsl:variable name="encodingList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:choose><xsl:when test="wps:Reference/@encoding"><xsl:value-of select="wps:Reference/@encoding"/>,</xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/Encoding"/>,</xsl:otherwise></xsl:choose></xsl:if></xsl:for-each></xsl:variable>
                            export <xsl:value-of select="local-name()"/>_MULTIPLE_INPUT_Encoding="<xsl:value-of select="substring($encodingList,1,string-length($encodingList)-1)"/>"
                    </xsl:if>
                    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/Schema"><xsl:variable name="schemaList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:choose><xsl:when test="wps:Reference/@schema"><xsl:value-of select="wps:Reference/@schema"/>,</xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData/Default/Format/Schema"/>,</xsl:otherwise></xsl:choose></xsl:if></xsl:for-each></xsl:variable>
                            export <xsl:value-of select="local-name()"/>_MULTIPLE_INPUT_Schema="<xsl:value-of select="substring($schemaList,1,string-length($schemaList)-1)"/>"
                    </xsl:if>
                  </xsl:if></xsl:when><xsl:otherwise><!--POSITION: <xsl:value-of select="$positionIdentifier"/>--><!--INPUT: <xsl:copy-of select="."/>--><xsl:call-template name="GenericInputReferenceShellEnviorement"><xsl:with-param name="genericInput" select="."/><xsl:with-param name="genericInputName" select="$nameIdentifier"/><xsl:with-param name="processInformation" select="$processInformation"/></xsl:call-template></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="$maxOccursInput &gt; 1"><xsl:variable name="indentifierPostions"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:value-of select="position()"/>,</xsl:if></xsl:for-each></xsl:variable><xsl:variable name="firstPosition"><xsl:value-of select="substring-before($indentifierPostions, ',')"/></xsl:variable><xsl:if test="$positionIdentifier = $firstPosition"><xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/LiteralData"><xsl:variable name="literalList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:value-of select="."/>,</xsl:if></xsl:for-each></xsl:variable>
                          export <xsl:value-of select="local-name()"/>_MULTIPLE_LITERAL_LIST="<xsl:value-of select="substring($literalList,1,string-length($literalList)-1)"/>"

                          <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/LiteralData/UOMs/Default/ows:UOM"><xsl:variable name="literalUOMList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:choose><xsl:when test="@uom"><xsl:value-of select="@uom"/>,</xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/LiteralData/UOMs/Default/ows:UOM"/>,</xsl:otherwise></xsl:choose></xsl:if></xsl:for-each></xsl:variable>
                            export <xsl:value-of select="local-name()"/>_MULTIPLE_LITERAL_UOM_LIST="<xsl:value-of select="substring($literalUOMList,1,string-length($literalUOMList)-1)"/>"
                          </xsl:if>
                    </xsl:if><xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/BoundingBoxData"><xsl:variable name="bboxEPSGList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:if test="wps:BoundingBoxData"><xsl:choose><xsl:when test="wps:BoundingBoxData/@crs"><xsl:value-of select="wps:BoundingBoxData/@crs"/></xsl:when><xsl:otherwise>4326</xsl:otherwise></xsl:choose>,</xsl:if></xsl:if></xsl:for-each></xsl:variable>
                        export <xsl:value-of select="local-name()"/>_MULTIPLE_BBOX_EPSG="<xsl:value-of select="substring($bboxEPSGList,1,string-length($bboxEPSGList)-1)"/>"
                        <!--<xsl:variable name="bboxDimensionsList">
                            <xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:if test="wps:BoundingBoxData"><xsl:choose><xsl:when test="wps:BoundingBoxData/@dimensions"><xsl:value-of select = "wps:BoundingBoxData/@dimensions" /></xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>,</xsl:if></xsl:if></xsl:for-each>
                        </xsl:variable>
                        export <xsl:value-of select = "local-name()" />_MULTIPLE_BBOX_DIMENSIONS="<xsl:value-of select="substring($bboxDimensionsList,1,string-length($bboxDimensionsList)-1)"/>"-->
                        <xsl:variable name="lowerCornerBBOXList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:if test="wps:BoundingBoxData"><xsl:value-of select="wps:BoundingBoxData/ows:LowerCorner"/>,</xsl:if></xsl:if></xsl:for-each></xsl:variable>             
                        export <xsl:value-of select="local-name()"/>_MULTIPLE_BBOX_LOWER_CORNER="<xsl:value-of select="substring($lowerCornerBBOXList,1,string-length($lowerCornerBBOXList)-1)"/>"
                        <xsl:variable name="upperCornerBBOXList"><xsl:for-each select="../child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:if test="wps:BoundingBoxData"><xsl:value-of select="wps:BoundingBoxData/ows:UpperCorner"/>,</xsl:if></xsl:if></xsl:for-each></xsl:variable>      
                        export <xsl:value-of select="local-name()"/>_MULTIPLE_BBOX_UPPER_CORNER="<xsl:value-of select="substring($upperCornerBBOXList,1,string-length($upperCornerBBOXList)-1)"/>"
                    </xsl:if><xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData"><!-- apply specific template for new Complex Data Import --></xsl:if></xsl:if></xsl:when><xsl:otherwise><xsl:call-template name="GenericInputShellEnviorement"><xsl:with-param name="genericInput" select="."/><xsl:with-param name="processInformation" select="$processInformation"/></xsl:call-template></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:for-each> 

        chmod +x <xsl:value-of select="$shellScriptPath"/>/execute_<xsl:value-of select="$processingName"/>.sh
        dos2unix <xsl:value-of select="$shellScriptPath"/>/execute_<xsl:value-of select="$processingName"/>.sh
	      cd <xsl:value-of select="$shellScriptPath"/>
        ./execute_<xsl:value-of select="$processingName"/>.sh &gt; $SHELL_LOG_FILE 2&gt;&amp;1
	#wait  
		
   # Input Reference Name List :  <xsl:value-of select="$inputNameDataUrls"/>	
   # Input Reference Path List : <xsl:value-of select="$retrivedDataUrls"/>
     

   
  </xsl:template>
  <xsl:template name="GenericInputShellEnviorement">
    <xsl:param name="genericInput"/>
    <xsl:param name="processInformation"/>
    <xsl:variable name="inputIdentifier" select="local-name($genericInput)"/>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData">
      <!-- Apply External Template for a new Complex Data-->
    </xsl:if>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/LiteralData">
      <xsl:call-template name="LiteralData">
        <xsl:with-param name="literalInput" select="$genericInput"/>
        <xsl:with-param name="processInformation" select="$processInformation"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/BoundingBoxData">
      <xsl:call-template name="BoundingBoxData">
        <xsl:with-param name="bboxInput" select="$genericInput"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]">
      <xsl:choose>
        <xsl:when test="@asReference">
                export <xsl:value-of select="$inputIdentifier"/>_STORE="<xsl:value-of select="@asReference"/>"
              </xsl:when>
        <xsl:otherwise>
                export <xsl:value-of select="$inputIdentifier"/>_STORE="false"
              </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/ows:UOM">
              		export <xsl:value-of select="$inputIdentifier"/>_UOM="<xsl:choose><xsl:when test="$genericInput/@uom"><xsl:value-of select="$genericInput/@uom"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/ows:UOM"/></xsl:otherwise></xsl:choose>"
              </xsl:if>
      <xsl:choose>
        <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType"><xsl:variable name="outputmimeType"><xsl:choose><xsl:when test="$genericInput/@mimeType"><xsl:value-of select="$genericInput/@mimeType"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType"/></xsl:otherwise></xsl:choose></xsl:variable>		    
			export <xsl:value-of select="$inputIdentifier"/>_MimeType="<xsl:value-of select="$outputmimeType"/>"
		    <xsl:if test="not($processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema)"><xsl:call-template name="getGenericType"><xsl:with-param name="mimeType" select="$outputmimeType"/><xsl:with-param name="outputIdentifier" select="$inputIdentifier"/></xsl:call-template></xsl:if>
		</xsl:when>
        <xsl:otherwise>
          <xsl:if test="not($processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema)">
		    export <xsl:value-of select="$inputIdentifier"/>_GlobalType="BIN"
		  </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
              		export <xsl:value-of select="$inputIdentifier"/>_Schema="<xsl:choose><xsl:when test="$genericInput/@schema"><xsl:value-of select="$genericInput/@schema"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/></xsl:otherwise></xsl:choose>"
			export <xsl:value-of select="$inputIdentifier"/>_GlobalType="TEXT"
	      </xsl:if>
      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding">
              		export <xsl:value-of select="$inputIdentifier"/>_Encoding="<xsl:choose><xsl:when test="$genericInput/@encoding"><xsl:value-of select="$genericInput/@encoding"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding"/></xsl:otherwise></xsl:choose>"
              </xsl:if>
      <xsl:choose>
        <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
          <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
        </xsl:when>
        <xsl:otherwise>
                    export <xsl:value-of select="$inputIdentifier"/>="<xsl:value-of select="$inputIdentifier"/>"
                    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                        export <xsl:value-of select="$inputIdentifier"/>_OUTPUT_PATH="<xsl:value-of select="$shellOutputFolder"/>/<xsl:value-of select="$inputIdentifier"/>"
                    </xsl:if>
                  </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <!-- *********************************** Template for Complex Output Imported *********************************************************** -->
  <!-- Currently the include XSL file is not supported-->
  <xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']"><xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
  export <xsl:value-of select="$wcsIdentifier"/>="<xsl:value-of select="$wcsIdentifier"/>"
  export <xsl:value-of select="$wcsIdentifier"/>_OUTPUT_PATH="<xsl:value-of select="$shellOutputFolder"/>/<xsl:value-of select="$wcsIdentifier"/>"
</xsl:template>
  <xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']"><xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/>

mkdir <xsl:value-of select="$shellOutputFolder"/>/<xsl:value-of select="$wfsIdentifier"/>
export <xsl:value-of select="$wfsIdentifier"/>="<xsl:value-of select="$wfsIdentifier"/>"
export <xsl:value-of select="$wfsIdentifier"/>_OUTPUT_PATH="<xsl:value-of select="$shellOutputFolder"/>/<xsl:value-of select="$wfsIdentifier"/>"

</xsl:template>
  <!-- *********************************************************************************************************************************** -->
  <xsl:template name="GenericInputReferenceShellEnviorement"><xsl:param name="genericInput"/><xsl:param name="genericInputName"/><xsl:param name="processInformation"/><xsl:variable name="inputIdentifier" select="local-name($genericInput)"/>
  
          export <xsl:value-of select="$inputIdentifier"/>="<xsl:call-template name="getReferenceVariableByName"><xsl:with-param name="urlList" select="$retrivedDataUrls"/><xsl:with-param name="nameVariable" select="$genericInputName"/><xsl:with-param name="nameList" select="$inputNameDataUrls"/></xsl:call-template>"
                                                              <!--xsl:call-template name="getReferenceVariableByPosition">
            	                                                                                      <xsl:with-param name="urlList" select="$retrivedDataUrls"/>
            	                                                                                      <xsl:with-param name="positionVariable" select="$genericInputPosition"/>
                                                                                                    <xsl:with-param name="currentPosition">0</xsl:with-param>
                                                                                          </xsl:call-template-->
     <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/MimeType">
          export <xsl:value-of select="$inputIdentifier"/>_MimeType="<xsl:choose><xsl:when test="$genericInput/wps:Reference/@mimeType"><xsl:value-of select="$genericInput/wps:Reference/@mimeType"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/MimeType"/>,</xsl:otherwise></xsl:choose>"
      </xsl:if>
      <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/Encoding">
          export <xsl:value-of select="local-name()"/>_Encoding="<xsl:choose><xsl:when test="$genericInput/wps:Reference/@encoding"><xsl:value-of select="$genericInput/wps:Reference/@encoding"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/Encoding"/>,</xsl:otherwise></xsl:choose>"
      </xsl:if>
      <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/Schema">
          export <xsl:value-of select="local-name()"/>_Schema="<xsl:choose><xsl:when test="$genericInput/wps:Reference/@schema"><xsl:value-of select="$genericInput/wps:Reference/@schema"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData/Default/Format/Schema"/>,</xsl:otherwise></xsl:choose>"
      </xsl:if>     
  </xsl:template>
  <xsl:template name="LiteralData"><xsl:param name="literalInput"/><xsl:param name="processInformation"/><xsl:variable name="literalIdentifier" select="local-name($literalInput)"/>
          export <xsl:value-of select="$literalIdentifier"/>="<xsl:value-of select="$literalInput/text()"/>"
        <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $literalIdentifier]/LiteralData/UOMs/Default/ows:UOM">
            export <xsl:value-of select="local-name($literalInput)"/>_UOM="<xsl:choose><xsl:when test="$literalInput/@uom"><xsl:value-of select="$literalInput/@uom"/></xsl:when><xsl:otherwise><xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $literalIdentifier]/LiteralData/UOMs/Default/ows:UOM"/></xsl:otherwise></xsl:choose>"
        </xsl:if>
  </xsl:template>
  <xsl:template name="BoundingBoxData"><xsl:param name="bboxInput"/><!--xsl:if test="$bboxInput/ows:BoundingBox"--><xsl:choose><xsl:when test="$bboxInput/wps:BoundingBoxData/@crs">
                    export <xsl:value-of select="local-name($bboxInput)"/>_EPSG="<xsl:call-template name="extractEPSG"><xsl:with-param name="CRS" select="$bboxInput/wps:BoundingBoxData/@crs"/></xsl:call-template>"
                  </xsl:when><xsl:otherwise>
                    export <xsl:value-of select="local-name($bboxInput)"/>_EPSG=4326
                  </xsl:otherwise></xsl:choose><!--xsl:if test="$bboxInput/wps:BoundingBoxData/@dimensions">
                    export <xsl:value-of select = "local-name($bboxInput)" />_DIMENSION=2<xsl:value-of select = "$bboxInput/wps:BoundingBoxData/@dimensions"/>"
                </xsl:if-->
                  export <xsl:value-of select="local-name($bboxInput)"/>_LOWER_CORNER="<xsl:value-of select="$bboxInput/wps:BoundingBoxData/ows:LowerCorner"/>"
                  export <xsl:value-of select="local-name($bboxInput)"/>_UPPER_CORNER="<xsl:value-of select="$bboxInput/wps:BoundingBoxData/ows:UpperCorner"/>"
            <!--/xsl:if-->
            <!--xsl:if test="$bboxInput/ows:WGS84BoundingBox">
            	  export <xsl:value-of select = "name($bboxInput)" />_EPSG=4326
                export <xsl:value-of select = "name($bboxInput)" />_LOWER_CORNER=<xsl:value-of select = "$bboxInput/ows:WGS84BoundingBox/ows:LowerCorner"/>
                export <xsl:value-of select = "name($bboxInput)" />_UPPER_CORNER=<xsl:value-of select = "$bboxInput/ows:WGS84BoundingBox/ows:UpperCorner" />
            </xsl:if-->
  </xsl:template>
  <xsl:template name="extractEPSG">
    <xsl:param name="CRS"/>
    <xsl:variable name="firstItem" select="substring-before($CRS, ':')"/>
    <xsl:variable name="remainingItems" select="substring-after($CRS, ':')"/>
    <xsl:if test="string-length($CRS) != 0">
      <xsl:choose>
        <xsl:when test="string-length($firstItem) = 0">
          <xsl:value-of select="$CRS"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="extractEPSG">
            <xsl:with-param name="CRS" select="$remainingItems"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <!--xsl:template name="getReferenceVariableByPosition">
    	<xsl:param name="urlList"/>
    	<xsl:param name="positionVariable"/>
    	<xsl:param name="currentPosition"/>

      <xsl:variable name="listSeparator">,</xsl:variable>
    	<xsl:variable name="firstUrl" select="substring-before($urlList, $listSeparator)"/>
    	<xsl:variable name="remainingUrls" select="substring-after($urlList, $listSeparator)"/>
      <xsl:choose>
        <xsl:when test="$positionVariable = $currentPosition">
          <xsl:value-of select="$firstUrl"></xsl:value-of>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$remainingUrls!=''">
            <xsl:call-template name="getReferenceVariableByPosition">
            	<xsl:with-param name="urlList" select="$remainingUrls"/>
            	<xsl:with-param name="positionVariable" select="$positionVariable"/>
              <xsl:with-param name="currentPosition" select="$currentPosition+1"/>
          	</xsl:call-template>
        </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template-->
  <xsl:template name="getReferenceVariableByName">
    <xsl:param name="urlList"/>
    <xsl:param name="nameList"/>
    <xsl:param name="nameVariable"/>
    <xsl:variable name="listSeparator">,</xsl:variable>
    <xsl:variable name="firstUrl" select="substring-before($urlList, $listSeparator)"/>
    <xsl:variable name="firstName" select="substring-before($nameList, $listSeparator)"/>
    <xsl:variable name="remainingUrls" select="substring-after($urlList, $listSeparator)"/>
    <xsl:variable name="remainingNames" select="substring-after($nameList, $listSeparator)"/>
    <xsl:choose>
      <xsl:when test="$firstName = $nameVariable">
        <xsl:value-of select="$firstUrl"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$remainingUrls!=''">
          <xsl:call-template name="getReferenceVariableByName">
            <xsl:with-param name="urlList" select="$remainingUrls"/>
            <xsl:with-param name="nameList" select="$remainingNames"/>
            <xsl:with-param name="nameVariable" select="$nameVariable"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="ParseMultipleInput">
    <xsl:param name="IdentifierProcessed"/>
    <xsl:param name="input"/>
    <IdentifierProcessed>
      <xsl:value-of select="$IdentifierProcessed"/>
    </IdentifierProcessed>
    <input>
      <xsl:copy-of select="$input"/>
    </input>
    <xsl:variable name="listSeparator">,</xsl:variable>
    <!--xsl:choose>
        <xsl:when test="$IdentifierProcessed"-->
    <xsl:variable name="firstProcessed" select="substring-before($IdentifierProcessed, $listSeparator)"/>
    <xsl:variable name="remainingProcessed" select="substring-after($IdentifierProcessed, $listSeparator)"/>
    <!--/xsl:when>
        <xsl:otherwise>
          <xsl:variable name="firstProcessed" select="substring-before(',', $listSeparator)"/>
    	    <xsl:variable name="remainingProcessed" select="substring-after(',', $listSeparator)"/>
        </xsl:otherwise>  
      </xsl:choose-->
    <firstProcessed>
      <xsl:value-of select="$firstProcessed"/>
    </firstProcessed>
    <ident>
      <xsl:value-of select="local-name($input)"/>
    </ident>
    <xsl:choose>
      <xsl:when test="$firstProcessed = local-name($input)">
        <parseResult>
          <control>true</control>
          <processed>
            <xsl:value-of select="$IdentifierProcessed"/>
          </processed>
        </parseResult>
      </xsl:when>
      <xsl:otherwise>
        <remainingProcessed>
          <xsl:value-of select="$remainingProcessed"/>
        </remainingProcessed>
        <xsl:choose>
          <xsl:when test="$remainingProcessed!=''">
            <xsl:call-template name="ParseMultipleInput">
              <xsl:with-param name="IdentifierProcessed" select="$remainingProcessed"/>
              <xsl:with-param name="input" select="$input"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <parseResult>
              <control>false</control>
              <processed><xsl:value-of select="$IdentifierProcessed"/><xsl:value-of select="local-name($input)"/>,</processed>
            </parseResult>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="getGenericType">
    <xsl:param name="mimeType"/>
    <xsl:param name="outputIdentifier"/>
    <xsl:variable name="mainType" select="substring-before($mimeType, '/')"/>
    <xsl:choose>
      <xsl:when test="$mainType = 'text' or $mainType = 'message'">
        	export <xsl:value-of select="$outputIdentifier"/>_GlobalType="TEXT"
      </xsl:when>
      <xsl:otherwise>
         export <xsl:value-of select="$outputIdentifier"/>_GlobalType="BIN"
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
