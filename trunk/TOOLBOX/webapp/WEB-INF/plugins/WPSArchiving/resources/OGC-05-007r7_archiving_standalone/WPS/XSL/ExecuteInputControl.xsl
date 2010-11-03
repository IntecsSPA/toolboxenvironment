<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="processName">RasterFormatConversion</xsl:param>
  <!-- 
INCLUDE SECTION
<xsl:include href=""/>
-->
  <xsl:template match="*">
    <ControlResult>
        <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName, '.xml')))"/>
        <xsl:variable name="storeRequest" select="@storeExecuteResponse"/>
        <xsl:variable name="statusRequest" select="@status"/>
        <xsl:variable name="storeDescribe" select="$processInformation/ProcessDescription/@storeSupported"/>
        <xsl:variable name="statusDescribe" select="$processInformation/ProcessDescription/@statusSupported"/>
        <xsl:choose>
          <xsl:when test="( $storeRequest = 'true' ) and  ( ( not($storeDescribe) ) or ( $storeDescribe != 'true' ) ) ">
              <requestCorrect>false</requestCorrect>
              <exceptionCode>StorageNotSupported</exceptionCode>
              <exceptionMessage>The service not supports the Storage. Please set [storeExecuteResponse="false"] in the request.</exceptionMessage>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="( $statusRequest = 'true' ) and  ( ( not($statusDescribe) ) or ( $statusDescribe != 'true' ) ) ">
                 <requestCorrect>false</requestCorrect>   
                 <exceptionCode>NoApplicableCode</exceptionCode>
                <exceptionMessage>The service not supports the Status. Please set [status="false"] in the request. </exceptionMessage> 
              </xsl:when>
              <xsl:otherwise>
                
                <xsl:variable name="executeRequest" select="../."/> 
                <xsl:variable name="InputsControl">
                    <xsl:for-each select="$processInformation/ProcessDescription/DataInputs/Input">
                        <xsl:variable name="inputIdentifierDescribe" select="ows:Identifier"/>
                        <!--NAME><xsl:value-of select="$inputIdentifierDescribe"/></NAME-->
                        <xsl:variable name="minOccursInput"><xsl:number value="@minOccurs"/></xsl:variable>
                        <!--OCCURS><xsl:value-of select="$minOccursInput"/></OCCURS-->
                        <xsl:if test="$minOccursInput &gt; 0">
                            <xsl:variable name="inputControl">
                                <!--xsl:copy-of select="$executeRequest/./child::*"/-->
                                <xsl:for-each select="$executeRequest/*/child::*">
                                
                                    <xsl:variable name="inputIdentifierRequest" select="local-name()"/>
                                      <!--IDRequest><xsl:value-of select="$inputIdentifierRequest"/></IDRequest>
                                      <IDDescribe><xsl:value-of select="$inputIdentifierDescribe"/></IDDescribe-->
                                      <xsl:variable name="indentifierPostions">
                                        <xsl:for-each select="$executeRequest/*/child::*"><xsl:if test="local-name() = $inputIdentifierRequest"><xsl:value-of select="position()"></xsl:value-of>,</xsl:if></xsl:for-each>
                                      </xsl:variable>
                                      <xsl:variable name="firstPosition"><xsl:value-of select="substring-before($indentifierPostions, ',')"/></xsl:variable>
                                      <!--firstPosition><xsl:value-of select="$firstPosition"/></firstPosition-->
                                      <xsl:if test="($inputIdentifierRequest = $inputIdentifierDescribe) and (position() =$firstPosition)">
                                        <xsl:variable name="countInput" select="count(//*[local-name() = $inputIdentifierRequest])"/>
                                        <!--CountInput><xsl:value-of select="$countInput"/></CountInput>
                                        <minOccursInput><xsl:value-of select="$minOccursInput"/></minOccursInput-->
                                        <xsl:choose>
                                          <xsl:when test="$minOccursInput &gt; $countInput">
                                              <xsl:text>The minimun occurs of the "</xsl:text><xsl:value-of select="$inputIdentifierDescribe"/><xsl:text>" input is </xsl:text><xsl:value-of select="$minOccursInput"/><xsl:text> (in the request is </xsl:text><xsl:value-of select="$countInput"/><xsl:text> ). </xsl:text>
                                          </xsl:when>
                                          <xsl:otherwise>
                                            <xsl:variable name="outputList">
                                              <xsl:text>" </xsl:text>
                                              <xsl:for-each select="$processInformation/ProcessDescription/ProcessOutputs/Output">
                                                <xsl:value-of select="ows:Identifier"/><xsl:text>,</xsl:text>
                                              </xsl:for-each>
                                              <xsl:text> "</xsl:text>
                                            </xsl:variable>
                                            <xsl:variable name="outputsControl">
                                              <xsl:for-each select="$processInformation/ProcessDescription/ProcessOutputs/Output">
                                                  <xsl:variable name="outputIdentifierDescribe" select="ows:Identifier"/>
                                                    <xsl:for-each select="$executeRequest/*/child::*">
                                                      <xsl:if test="local-name() = $outputIdentifierDescribe">
                                                         <xsl:text>OK</xsl:text> 
                                                      </xsl:if>
                                                    </xsl:for-each>
                                              </xsl:for-each>
                                            </xsl:variable>
                                            <xsl:choose>
                                              <xsl:when test="$outputsControl and ($outputsControl !='')">
                                                <xsl:text>OK</xsl:text>    
                                              </xsl:when>
                                              <xsl:otherwise>
                                                <xsl:text>One of </xsl:text><xsl:value-of select="$outputList"/><xsl:text>" output is mandatory. </xsl:text>
                                              </xsl:otherwise>
                                            </xsl:choose>
                                          </xsl:otherwise>
                                        </xsl:choose>
                                      </xsl:if>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:choose>
                              <xsl:when test="$inputControl and ($inputControl !='')">
                                    <xsl:if test="$inputControl != 'OK'">
                                      <xsl:value-of select="$inputControl"/>
                                    </xsl:if>
                              </xsl:when>
                              <xsl:otherwise>
                                  <xsl:text>The "</xsl:text><xsl:value-of select="$inputIdentifierDescribe"/><xsl:text>" input is mandatory. </xsl:text>
                              </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:choose>
                  <xsl:when test="$InputsControl and ($InputsControl !='')">
                      <requestCorrect>false</requestCorrect>   
                      <exceptionCode>MissingParameterValue</exceptionCode>
                      <exceptionMessage><xsl:value-of select="$InputsControl"/></exceptionMessage>
                  </xsl:when>
                  <xsl:otherwise>
                      <requestCorrect>true</requestCorrect>   
                      <exceptionCode></exceptionCode>
                      <exceptionMessage></exceptionMessage>
                  </xsl:otherwise>
                </xsl:choose>
                <!--result><xsl:value-of select="$InputsControl"/></result-->
                </xsl:otherwise>    
              </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
    </ControlResult>  
  </xsl:template>
</xsl:stylesheet>
