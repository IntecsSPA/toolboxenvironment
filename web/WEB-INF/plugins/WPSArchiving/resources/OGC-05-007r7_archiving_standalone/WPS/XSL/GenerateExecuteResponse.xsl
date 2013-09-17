<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="statusEnable"/>
  <xsl:param name="particularReferenceID">,a,b,c,RasterDiffResult,</xsl:param>
  <xsl:param name="particularReferenceURL">,a,b,c,URLValueDiff,</xsl:param>
  <xsl:param name="lineage"/>
  <xsl:param name="storeExecuteResponse"/>
  <xsl:param name="processName">TestOperation</xsl:param>
  <xsl:param name="serviceName">TestService</xsl:param>
  <xsl:param name="status">ProcessSucceeded</xsl:param>
  <xsl:param name="statusMessage">ProcessSucceeded</xsl:param>
  <xsl:param name="statusPercentCompleted">0</xsl:param>
  <xsl:param name="statusExceptionCode">MissingParameterValue</xsl:param>
  <xsl:param name="statusLocation">http://localhost:8080</xsl:param>
  <xsl:param name="creationTime">2007-04-18T12:13:14Z</xsl:param>
  <xsl:param name="outputHttpRequest">http://localhost:8080/TOOLBOX/manager?cmd=GetServiceResource&amp;serviceName=WPSExample&amp;relativePath=/output/</xsl:param>
  <xsl:param name="outputLiteralData"/>
  <xsl:param name="outputBoundingBoxData"/>
  <xsl:param name="outputFolder">/home/maro/Programmi/apache-tomcat-6.0.16/webapps/TOOLBOX/WEB-INF/services/WPSDummy/Log/11062009182133192/output/</xsl:param>
  <xsl:param name="originalRequest"/>
  <xsl:param name="capabilitiesServiceURL">http://localhost:8080/TOOLBOX/services/WPSexample/GetCapabilities.xml</xsl:param>
  <xsl:param name="geoserverOWSURL">http://localhost:8080/geoserver/ows</xsl:param>
  <xsl:param name="dateTime">0123456789</xsl:param>

 <!-- INCLUDE SECTION-->

  
  <!-- -->

  <xsl:template match="*">
    <xsl:variable name="storeRequired"><xsl:choose><xsl:when test="$storeExecuteResponse"><xsl:value-of select="$storeExecuteResponse"/></xsl:when><xsl:otherwise><xsl:value-of select="@storeExecuteResponse"/></xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="lineageRequired"><xsl:choose><xsl:when test="$lineage"><xsl:value-of select="$lineage"/></xsl:when><xsl:otherwise><xsl:value-of select="@lineage"/></xsl:otherwise></xsl:choose></xsl:variable> 
    <xsl:variable name="statusRequired"><xsl:choose><xsl:when test="$statusEnable"><xsl:value-of select="$statusEnable"/></xsl:when><xsl:otherwise><xsl:value-of select="@status"/></xsl:otherwise></xsl:choose></xsl:variable> 
    <wps:ExecuteResponse xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="WPS" version="1.0.0" xml:lang="en" serviceInstance="{$capabilitiesServiceURL}">
      <xsl:if test="$storeRequired = 'true'">
        <xsl:attribute name="statusLocation">
          <xsl:value-of select="$statusLocation"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName,'.xml')))"/>
      <wps:Process wps:processVersion="{$processInformation/ProcessDescription/@wps:processVersion}">
        <ows:Identifier>
          <xsl:value-of select="$processInformation/ProcessDescription/ows:Identifier"/>
        </ows:Identifier>
        <ows:Title>
          <xsl:value-of select="$processInformation/ProcessDescription/ows:Title"/>
        </ows:Title>
        <ows:Abstract>
          <xsl:value-of select="$processInformation/ProcessDescription/ows:Abstract"/>
        </ows:Abstract>
        <wps:Profile>
          <xsl:value-of select="$processInformation/ProcessDescription/wps:Profile"/>
        </wps:Profile>
        <xsl:if test="$processInformation/ProcessDescription/wps:WSDL">
          <wps:WSDL xlink:href="{$processInformation/ProcessDescription/wps:WSDL}"/>
        </xsl:if>
      </wps:Process>
      <xsl:variable name="resposneNode" select="/*/wps:ResponseForm/wps:ResponseDocument"/>
      <wps:Status creationTime="{$creationTime}">
            <xsl:if test="$status = 'ProcessSucceeded'">
              <wps:ProcessSucceeded>
                <xsl:value-of select="$statusMessage"/>
              </wps:ProcessSucceeded>
            </xsl:if>
            <xsl:if test="$status = 'ProcessAccepted'">
              <wps:ProcessAccepted>
                <xsl:value-of select="$statusMessage"/>
              </wps:ProcessAccepted>
            </xsl:if>
            <xsl:if test="$status = 'ProcessStarted'">
              <wps:ProcessStarted percentCompleted="{$statusPercentCompleted}"/>
            </xsl:if>
            <xsl:if test="$status = 'ProcessPaused'">
              <wps:ProcessPaused percentCompleted="{$statusPercentCompleted}"/>
            </xsl:if>
            <xsl:if test="$status = 'ProcessFailed'">
              <wps:ProcessFailed>
                <ows:ExceptionReport version="1.0.0" xml:lang="en">
                  <ows:Exception exceptionCode="{$statusExceptionCode}">
                    <ows:ExceptionText><xsl:value-of select="$statusMessage"/></ows:ExceptionText>
                  </ows:Exception>
                </ows:ExceptionReport>
              </wps:ProcessFailed>
            </xsl:if>
      </wps:Status>
      <xsl:if test="$lineageRequired = 'true'">
        <xsl:if test="$originalRequest/wps:Execute/wps:DataInputs">
          <xsl:copy-of select="$originalRequest/wps:Execute/wps:DataInputs"/>
        </xsl:if>
        <xsl:if test="$originalRequest/wps:Execute/wps:ResponseForm">
          <xsl:copy-of select="$originalRequest/wps:Execute/wps:ResponseForm"/>
        </xsl:if>
      </xsl:if>
      <wps:ProcessOutputs>
        <xsl:choose>
          <xsl:when test="$storeRequired = 'true'">
            <xsl:for-each select="child::*">
              <xsl:variable name="inputIdentifier" select="local-name()"/>
              <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]">
                <wps:Output>
                  <ows:Identifier>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Identifier"/>
                  </ows:Identifier>
                  <ows:Title>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Title"/>
                  </ows:Title>
                  <ows:Abstract>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Abstract"/>
                  </ows:Abstract>
                  <wps:Reference>
                    <xsl:variable name="indexParticularUrl">
                      <xsl:call-template name="geListtIndexByValue">
                        <xsl:with-param name="list" select="$particularReferenceID"/>
                        <xsl:with-param name="value" select="$inputIdentifier"/>
                        <xsl:with-param name="currentPosition" select="0"/>
                      </xsl:call-template>
                    </xsl:variable>
                    <!--indexParticularUrl><xsl:value-of select="$indexParticularUrl"></xsl:value-of></indexParticularUrl-->
                    <xsl:choose>
                      <xsl:when test="$indexParticularUrl = -1">
                        <xsl:choose>
                          <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                            <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                              <xsl:value-of select="concat($outputHttpRequest,$inputIdentifier)"/>
                            </xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                              <xsl:value-of select="concat($outputHttpRequest,$inputIdentifier,'.xml')"/>
                            </xsl:attribute>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:variable name="particularUrl">
                          <xsl:call-template name="geListtValueByIndex">
                            <xsl:with-param name="list" select="$particularReferenceURL"/>
                            <xsl:with-param name="index" select="$indexParticularUrl"/>
                            <xsl:with-param name="currentPosition" select="0"/>
                          </xsl:call-template>
                        </xsl:variable>
                        <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                          <xsl:value-of select="$particularUrl"/>
                        </xsl:attribute>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                      <xsl:when test="@encoding">
                        <xsl:attribute name="encoding">
                          <xsl:value-of select="@encoding"/>
                        </xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding">
                          <xsl:attribute name="encoding">
                            <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding"/>
                          </xsl:attribute>
                        </xsl:if>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                      <xsl:when test="@schema">
                        <xsl:attribute name="schema">
                          <xsl:value-of select="@schema"/>
                        </xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
                          <xsl:attribute name="schema">
                            <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                          </xsl:attribute>
                        </xsl:if>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                      <xsl:when test="@uom">
                        <xsl:attribute name="uom">
                          <xsl:value-of select="@uom"/>
                        </xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM">
                          <xsl:attribute name="uom">
                            <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM"/>
                          </xsl:attribute>
                        </xsl:if>
                      </xsl:otherwise>
                    </xsl:choose>
                  </wps:Reference>
                </wps:Output>
              </xsl:if>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="child::*">
              <xsl:variable name="inputIdentifier" select="local-name()"/>
              <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]">
                <wps:Output>
                  <ows:Identifier>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Identifier"/>
                  </ows:Identifier>
                  <ows:Title>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Title"/>
                  </ows:Title>
                  <ows:Abstract>
                    <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ows:Abstract"/>
                  </ows:Abstract>
                  <xsl:choose>
                    <xsl:when test="./@asReference ='true'">
                      <wps:Reference>
                        <xsl:variable name="indexParticularUrl">
                          <xsl:call-template name="geListtIndexByValue">
                            <xsl:with-param name="list" select="$particularReferenceID"/>
                            <xsl:with-param name="value" select="$inputIdentifier"/>
                            <xsl:with-param name="currentPosition" select="0"/>
                          </xsl:call-template>
                        </xsl:variable>
                        <xsl:choose>
                          <xsl:when test="$indexParticularUrl = -1">
                            <xsl:choose>
                              <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                                 <xsl:variable name="correctComplexOutputPath" select="concat($outputHttpRequest,$inputIdentifier)"/>
                                <!--xsl:attribute name="test">
                                      <xsl:value-of select="$correctComplexOutputPath"/>
                                    </xsl:attribute>
                                <xsl:choose>
                                  <xsl:when test="boolean(document($correctComplexOutputPath))"-->
                                    <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                                      <xsl:value-of select="$correctComplexOutputPath"/>
                                    </xsl:attribute>
                                  <!--/xsl:when>
                                  <xsl:otherwise>
                                    <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                                      <xsl:value-of select="concat($outputHttpRequest,$inputIdentifier,'NULL.xml')"/>
                                    </xsl:attribute>  
                                  </xsl:otherwise>
                                </xsl:choose-->
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:variable name="correctOutputPath" select="concat($outputHttpRequest,$inputIdentifier,'.xml')"/>
                                <xsl:choose>
                                  <xsl:when test="document($correctOutputPath)">
                                    <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                                      <xsl:value-of select="$correctOutputPath"/>
                                    </xsl:attribute>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                                      <xsl:value-of select="concat($outputHttpRequest,$inputIdentifier,'NULL.xml')"/>
                                    </xsl:attribute>  
                                  </xsl:otherwise>
                                </xsl:choose>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:variable name="particularUrl">
                              <xsl:call-template name="geListtValueByIndex">
                                <xsl:with-param name="list" select="$particularReferenceURL"/>
                                <xsl:with-param name="index" select="$indexParticularUrl"/>
                                <xsl:with-param name="currentPosition" select="0"/>
                              </xsl:call-template>
                            </xsl:variable>
                            <!--particularUrl><xsl:value-of select="$particularUrl"></xsl:value-of></particularUrl-->
                            <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                              <xsl:value-of select="$particularUrl"/>
                            </xsl:attribute>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                          <xsl:when test="@encoding">
                            <xsl:attribute name="encoding">
                              <xsl:value-of select="@encoding"/>
                            </xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding">
                              <xsl:attribute name="encoding">
                                <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding"/>
                              </xsl:attribute>
                            </xsl:if>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                          <xsl:when test="@schema">
                            <xsl:attribute name="schema">
                              <xsl:value-of select="@schema"/>
                            </xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
                              <xsl:attribute name="schema">
                                <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                              </xsl:attribute>
                            </xsl:if>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                          <xsl:when test="@uom">
                            <xsl:attribute name="uom">
                              <xsl:value-of select="@uom"/>
                            </xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM">
                              <xsl:attribute name="uom">
                                <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM"/>
                              </xsl:attribute>
                            </xsl:if>
                          </xsl:otherwise>
                        </xsl:choose>
                      </wps:Reference>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:choose>
                        <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                          <!-- Apply COMPLEX OUTPUT Template-->
                          <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="correctOutputValuePath" select="string(concat($outputFolder, $inputIdentifier,'.xml'))"/>
                            <xsl:choose>
                                  <xsl:when test="document($correctOutputValuePath)">
                                    <xsl:variable name="documentCorrectResult" select="document($correctOutputValuePath)"/>
                                    <xsl:copy-of select="$documentCorrectResult"/>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:variable name="documentErrorResult" select="document(string(concat($outputFolder, $inputIdentifier,'NULL.xml')))"/>
                                    <xsl:copy-of select="$documentErrorResult"/>
                                  </xsl:otherwise>
                          </xsl:choose>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:otherwise>
                  </xsl:choose>
                </wps:Output>
              </xsl:if>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </wps:ProcessOutputs>
    </wps:ExecuteResponse>
  </xsl:template>


<!-- *********************************** Template for Complex Output Imported *********************************************************** -->
<!-- Currently the include XSL file is not supported-->

<xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']">

  <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
 <wps:ComplexData xmlns:wps="http://www.opengis.net/wps/1.0.0">
    <wcs-wmsLayer xmlns="http://toolbox.pisa.intecs.it/schemas/wps/complexData/WCS-WMSLayer">
        <owsUrl><xsl:value-of select="$geoserverOWSURL"/></owsUrl>
        <owsLayerName><xsl:value-of select="concat( $wcsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/></owsLayerName>  
    </wcs-wmsLayer>
.</wps:ComplexData>

</xsl:template>




<xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']">

   <xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/> 
<wps:ComplexData xmlns:wps="http://www.opengis.net/wps/1.0.0">
    <wfs-wmsLayer xmlns="http://toolbox.pisa.intecs.it/schemas/wps/complexData/WFS-WMSLayer">
      <owsUrl><xsl:value-of select="$geoserverOWSURL"/></owsUrl>
      <owsLayerName><xsl:value-of select="concat( $wfsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/></owsLayerName>  
  </wfs-wmsLayer>
</wps:ComplexData>

</xsl:template>


<!-- *********************************************************************************************************************************** -->


  <xsl:template name="geListtIndexByValue">
    <xsl:param name="list"/>
    <xsl:param name="value"/>
    <xsl:param name="currentPosition"/>
    <xsl:variable name="listSeparator">,</xsl:variable>
    <xsl:variable name="firstValue" select="substring-before($list, $listSeparator)"/>
    <xsl:variable name="remainingValues" select="substring-after($list, $listSeparator)"/>
    <xsl:choose>
      <xsl:when test="$firstValue = $value">
        <xsl:value-of select="$currentPosition"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$remainingValues!=''">
            <xsl:call-template name="geListtIndexByValue">
              <xsl:with-param name="list" select="$remainingValues"/>
              <xsl:with-param name="value" select="$value"/>
              <xsl:with-param name="currentPosition" select="$currentPosition+1"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:number value="-1"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="geListtValueByIndex">
    <xsl:param name="list"/>
    <xsl:param name="index"/>
    <xsl:param name="currentPosition"/>
    <xsl:variable name="listSeparator">,</xsl:variable>
    <xsl:variable name="firstValue" select="substring-before($list, $listSeparator)"/>
    <xsl:variable name="remainingValues" select="substring-after($list, $listSeparator)"/>
    <xsl:choose>
      <xsl:when test="$index = $currentPosition">
        <xsl:value-of select="$firstValue"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$remainingValues!=''">
            <xsl:call-template name="geListtValueByIndex">
              <xsl:with-param name="list" select="$remainingValues"/>
              <xsl:with-param name="index" select="$index"/>
              <xsl:with-param name="currentPosition" select="$currentPosition+1"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
