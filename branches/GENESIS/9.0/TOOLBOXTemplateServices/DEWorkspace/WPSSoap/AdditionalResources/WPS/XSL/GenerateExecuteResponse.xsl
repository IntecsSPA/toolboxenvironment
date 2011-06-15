<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
  <xsl:output method="xml"/>
  <xsl:param name="statusEnable"/>
  <xsl:param name="particularReferenceID">,a,b,c,RasterDiffResult,</xsl:param>
  <xsl:param name="particularReferenceURL">,a,b,c,URLValueDiff,</xsl:param>
  <xsl:param name="lineage"/>
  <xsl:param name="asynch">false</xsl:param>
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
  <!--xsl:param name="originalRequest"/-->
  <xsl:param name="capabilitiesServiceURL">http://localhost:8080/TOOLBOX/services/WPSexample/GetCapabilities.xml</xsl:param>
  <xsl:param name="geoserverOWSURL">http://localhost:8080/geoserver/ows</xsl:param>
  <xsl:param name="dateTime">0123456789</xsl:param>
  <!-- INCLUDE SECTION-->
  <!-- -->
  <xsl:template match="*">
    <xsl:variable name="storeRequired">
      <xsl:choose>
        <xsl:when test="$storeExecuteResponse">
          <xsl:value-of select="$storeExecuteResponse"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@storeExecuteResponse"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="lineageRequired">
      <xsl:choose>
        <xsl:when test="$lineage">
          <xsl:value-of select="$lineage"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@lineage"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="statusRequired">
          <xsl:value-of select="@status"/>
    </xsl:variable>
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
          <xsl:choose>
            <xsl:when test="$statusRequired = 'true'">
              <wps:ProcessStarted percentCompleted="{$statusPercentCompleted}"/>
            </xsl:when>  
            <xsl:otherwise>
              <wps:ProcessStarted/>
            </xsl:otherwise>
          </xsl:choose>  
        </xsl:if>
        <xsl:if test="$status = 'ProcessPaused'">
          <wps:ProcessPaused percentCompleted="{$statusPercentCompleted}"/>
        </xsl:if>
        <xsl:if test="$status = 'ProcessFailed'">
          <wps:ProcessFailed>
            <ows:ExceptionReport version="1.0.0" xml:lang="en">
              <ows:Exception exceptionCode="{$statusExceptionCode}">
                <ows:ExceptionText>
                  <xsl:value-of select="$statusMessage"/>
                </ows:ExceptionText>
              </ows:Exception>
            </ows:ExceptionReport>
          </wps:ProcessFailed>
        </xsl:if>
      </wps:Status>
      <!--xsl:if test="$lineageRequired = 'true'">
        <xsl:if test="$originalRequest/wps:Execute/wps:DataInputs">
          <xsl:copy-of select="$originalRequest/wps:Execute/wps:DataInputs"/>
        </xsl:if>
        <xsl:if test="$originalRequest/wps:Execute/wps:ResponseForm">
          <xsl:copy-of select="$originalRequest/wps:Execute/wps:ResponseForm"/>
        </xsl:if>
      </xsl:if-->
      <xsl:if test=" $status = 'ProcessSucceeded' or $status = 'ProcessFailed' ">
        <xsl:variable name="processOutputsVar">
          <wps:ProcessOutputs>
            <xsl:choose>
              <xsl:when test="( ($storeRequired = 'true') or ($asynch = 'true') )">
                <xsl:for-each select="child::*">
                  
                  <xsl:variable name="inputIdentifier" select="local-name()"/>
                  <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]">
                    <xsl:variable name="responseOutputVar">
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
                          <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                            <xsl:variable name="correctComplexOutputPath" select="concat(  'file:///' ,  $outputFolder ,$inputIdentifier,'.xml')"/>
                            <xsl:if test="boolean(document($correctComplexOutputPath))">
                              <Reference>
                                <url>
                                  <xsl:value-of select="concat(  $outputHttpRequest,$inputIdentifier)"/>
                                </url>
                                <xsl:choose>
                                  <xsl:when test="@encoding">
                                    <Encoding>
                                      <xsl:value-of select="@encoding"/>
                                    </Encoding>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding">
                                      <Encoding>
                                        <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding"/>
                                      </Encoding>
                                    </xsl:if>
                                  </xsl:otherwise>
                                </xsl:choose>
                                <xsl:choose>
                                  <xsl:when test="@mime-type">
                                    <MimeType>
                                      <xsl:value-of select="@mime-type"/>
                                    </MimeType>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType">
                                      <MimeType>
                                        <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType"/>
                                      </MimeType>
                                    </xsl:if>
                                  </xsl:otherwise>
                                </xsl:choose>
                                <xsl:choose>
                                  <xsl:when test="@schema">
                                    <Schema>
                                      <xsl:value-of select="@schema"/>
                                    </Schema>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
                                      <Schema>
                                        <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                                      </Schema>
                                    </xsl:if>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </Reference>
                            </xsl:if>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:variable name="correctNotComplexOutputPath" select="concat('file:///' , $outputFolder,$inputIdentifier,'.xml')"/>
                            <xsl:if test="boolean(document($correctNotComplexOutputPath))">
                              <Reference>
                                <url>
                                  <xsl:value-of select="concat($outputHttpRequest,$inputIdentifier,'.xml')"/>
                                </url>
                                <!--Encoding>UTF-8</Encoding>
                                          <MimeType>text/xml</MimeType-->
                              </Reference>
                            </xsl:if>
                            <!--xsl:choose>
                                  <xsl:when test="@uom">
                                    <Uom>
                                      <xsl:value-of select="@uom"/>
                                    </Uom>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM">
                                      <Uom>
                                        <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/LiteralOutput/UOMs/Default/UOM"/>
                                      </Uom>
                                    </xsl:if>
                                  </xsl:otherwise>
                                </xsl:choose-->
                          </xsl:otherwise>
                        </xsl:choose>
                      </wps:Output>
                    </xsl:variable>
                    <xsl:if test="$responseOutputVar//Reference">
                      <wps:Output>
                        <xsl:copy-of select="$responseOutputVar//ows:Identifier"/>
                        <xsl:copy-of select="$responseOutputVar//ows:Title"/>
                        <xsl:copy-of select="$responseOutputVar//ows:Abstract"/>
                        <xsl:call-template name="getReference">
                          <xsl:with-param name="refElement" select="$responseOutputVar//Reference"/>
                          <xsl:with-param name="asynch" select="$asynch"/>
                        </xsl:call-template>
                      </wps:Output>
                    </xsl:if>
                  </xsl:if>
                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <xsl:for-each select="child::*">
                  <xsl:variable name="inputIdentifier" select="local-name()"/>
                  <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]">
                    <xsl:variable name="outputResponseSync">
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
                            <xsl:choose>
                              <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                                <xsl:variable name="correctComplexOutputPath" select="concat('file:///' , $outputFolder,$inputIdentifier,'.xml')"/>
                                <xsl:if test="boolean(document($correctComplexOutputPath))">
                                  <Reference>
                                    <url>
                                      <xsl:value-of select="concat( $outputHttpRequest,$inputIdentifier)"/>
                                    </url>
                                  </Reference>
                                  <xsl:choose>
                                    <xsl:when test="@encoding">
                                      <Encoding>
                                        <xsl:value-of select="@encoding"/>
                                      </Encoding>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding">
                                        <Encoding>
                                          <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Encoding"/>
                                        </Encoding>
                                      </xsl:if>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:choose>
                                    <xsl:when test="@schema">
                                      <Schema>
                                        <xsl:value-of select="@schema"/>
                                      </Schema>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema">
                                        <Schema>
                                          <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                                        </Schema>
                                      </xsl:if>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:choose>
                                    <xsl:when test="@mime-type">
                                      <MimeType>
                                        <xsl:value-of select="@mime-type"/>
                                      </MimeType>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType">
                                        <MimeType>
                                          <xsl:value-of select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/MimeType"/>
                                        </MimeType>
                                      </xsl:if>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:if>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:variable name="correctOutputPath" select="concat('file:///' , $outputFolder,$inputIdentifier,'.xml')"/>
                                <xsl:if test="document($correctOutputPath)">
                                  <Reference>
                                    <url><xsl:value-of select="$correctOutputPath"/></url>
                                  </Reference>
                                </xsl:if>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:choose>
                              <xsl:when test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput">
                                <!-- Apply COMPLEX OUTPUT Template-->
                                <wps:Data>
                                  <xsl:apply-templates select="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifier]/ComplexOutput/Default/Format/Schema"/>
                                </wps:Data>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:variable name="correctOutputValuePath" select="string(concat('file:///' , $outputFolder, $inputIdentifier,'.xml'))"/>
                                <xsl:choose>
                                  <xsl:when test="document($correctOutputValuePath)">
                                    <xsl:variable name="documentCorrectResult" select="document($correctOutputValuePath)"/>
                                    <xsl:copy-of select="$documentCorrectResult"/>
                                  </xsl:when>
                                  <!--xsl:otherwise>
                                  <xsl:variable name="documentErrorResult" select="document(string(concat($outputFolder, $inputIdentifier,'NULL.xml')))"/>
                                  <xsl:copy-of select="$documentErrorResult"/>
                                </xsl:otherwise-->
                                </xsl:choose>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:otherwise>
                        </xsl:choose>
                      </wps:Output>
                    </xsl:variable>
                    <xsl:if test="$outputResponseSync//wps:Data">
                      <xsl:copy-of select="$outputResponseSync"/>
                    </xsl:if>
                    <xsl:if test="$outputResponseSync//Reference">
                      <wps:Output>
                        <xsl:copy-of select="$outputResponseSync//ows:Identifier"/>
                        <xsl:copy-of select="$outputResponseSync//ows:Title"/>
                        <xsl:copy-of select="$outputResponseSync//ows:Abstract"/>
                        <xsl:call-template name="getReference">
                          <xsl:with-param name="refElement" select="$outputResponseSync//Reference"/>
                          <xsl:with-param name="asynch">false</xsl:with-param>
                        </xsl:call-template>
                      </wps:Output>
                    </xsl:if>
                  </xsl:if>
                </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </wps:ProcessOutputs>
        </xsl:variable>
        <xsl:if test="$processOutputsVar//wps:ProcessOutputs/wps:Output">
          <xsl:copy-of select="$processOutputsVar"/>
        </xsl:if>
      </xsl:if>
    </wps:ExecuteResponse>
  </xsl:template>
  <!-- *********************************** Template for Complex Output Imported *********************************************************** -->
  <!-- Currently the include XSL file is not supported-->
  <xsl:template match="Schema[. ='complexdata/WCS-WMSLayer.xsd']">
    <xsl:variable name="wcsIdentifier" select="../../../../ows:Identifier"/>
    <wps:ComplexData xmlns:wps="http://www.opengis.net/wps/1.0.0"><wcs-wmsLayer xmlns="http://toolbox.pisa.intecs.it/schemas/wps/complexData/WCS-WMSLayer"><owsUrl><xsl:value-of select="$geoserverOWSURL"/></owsUrl><owsLayerName><xsl:value-of select="concat( $wcsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/></owsLayerName></wcs-wmsLayer>
.</wps:ComplexData>
  </xsl:template>
  <xsl:template match="Schema[. ='complexdata/WFS-WMSLayer.xsd']">
    <xsl:variable name="wfsIdentifier" select="../../../../ows:Identifier"/>
    <wps:ComplexData xmlns:wps="http://www.opengis.net/wps/1.0.0">
      <wfs-wmsLayer xmlns="http://toolbox.pisa.intecs.it/schemas/wps/complexData/WFS-WMSLayer">
        <owsUrl>
          <xsl:value-of select="$geoserverOWSURL"/>
        </owsUrl>
        <owsLayerName>
          <xsl:value-of select="concat( $wfsIdentifier , '_', $serviceName, '_', $processName, '_', $dateTime )"/>
        </owsLayerName>
      </wfs-wmsLayer>
    </wps:ComplexData>
  </xsl:template>
  <!-- *********************************************************************************************************************************** -->
  <xsl:template name="getReference">
    <xsl:param name="refElement"/>
    <xsl:param name="asynch"/>
    <xsl:choose>
      <xsl:when test="$asynch = 'true' ">
        <wps:Reference xlink:href="{$refElement//url}">
          <xsl:if test="$refElement//Schema">
            <xsl:attribute name="schema">
              <xsl:value-of select="$refElement//Schema"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$refElement//MimeType">
            <xsl:attribute name="mime-type">
              <xsl:value-of select="$refElement//MimeType"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$refElement//Encoding">
            <xsl:attribute name="encoding">
              <xsl:value-of select="refElement//Encoding"/>
            </xsl:attribute>
          </xsl:if>
        </wps:Reference>
      </xsl:when>
      <xsl:otherwise>
        <wps:Reference href="{$refElement//url}">
          <xsl:if test="$refElement//Schema">
            <xsl:attribute name="schema">
              <xsl:value-of select="$refElement//Schema"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$refElement//MimeType">
            <xsl:attribute name="mime-type">
              <xsl:value-of select="$refElement//MimeType"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$refElement//Encoding">
            <xsl:attribute name="encoding">
              <xsl:value-of select="refElement//Encoding"/>
            </xsl:attribute>
          </xsl:if>
        </wps:Reference>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
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
