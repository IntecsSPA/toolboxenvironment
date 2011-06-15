<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="wpsDownloadedInputFolder">/download_PATH/</xsl:param>
  <xsl:param name="processName">RasterClipBBox</xsl:param>
  <!-- 
		INCLUDE SECTION
		<xsl:include href=""/>
	-->
  <xsl:template match="*">
    <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName, '.xml')))"/>
    <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
      <setVariable name="LocalURIReferenecesList">
        <string/>
      </setVariable>
      <setVariable name="AttributeReferenecesList">
        <string/>
      </setVariable>
      <xsl:for-each select="child::*">
        <xsl:variable name="inputIdentifierRequest" select="local-name(.)"/>
        <xsl:variable name="maxOccursInput">
          <xsl:number value="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/@maxOccurs"/>
          <!--xsl:value-of select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/@maxOccurs"/-->
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$maxOccursInput &gt; 1">
            <xsl:variable name="multipleInputPath"><xsl:value-of select="$wpsDownloadedInputFolder"/><xsl:value-of select="$inputIdentifierRequest"/>/</xsl:variable>
            <if>
              <mkdir>
                <string>
                  <xsl:value-of select="$multipleInputPath"/>
                </string>
              </mkdir>
              <sequence>
                <xsl:for-each select="../*[local-name() = $inputIdentifierRequest]">
                  <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/ComplexData">
                    <xsl:choose>
                      <xsl:when test="wps:Reference">
                        <xsl:choose>
                          <xsl:when test="wps:Reference/wps:BodyReference">
                            <setVariable name="ErrorType">
                              <string>NoApplicableCode</string>
                            </setVariable>
                            <setVariable name="ErrorMessage">
                              <string>"wps:BodyReference" tag not supported.</string>
                            </setVariable>
                          </xsl:when>
                          <xsl:otherwise>



                            <setVariable name="localURI">
                              <string>
                                <xsl:value-of select="$multipleInputPath"/>
                                <xsl:value-of select="position()"/>
                              </string>
                            </setVariable>
                            <xsl:choose>
                              <xsl:when test="wps:Reference/@method = 'POST'">
                                <xsl:if test="wps:Reference/wps:Body">
                                  <setVariable name="requestBody">
                                    <xml textTag="evaluate" attributePrefix="%">
                                      <xsl:copy-of select="wps:Reference/wps:Body/*"/>
                                    </xml>
                                  </setVariable>
                                </xsl:if>
                                <dumpFile fileType="binary">
                                  <variable name="localURI"/>
                                  <http fileType="binary" method="POST">
                                    <url>
                                      <string>
                                        <xsl:value-of select="wps:Reference/@xlink:href"/>
                                      </string>
                                    </url>
                                    <xsl:if test="wps:Reference/wps:Header">
                                      <headers>
                                        <xsl:for-each select="wps:Reference/wps:Header">
                                          <header key="{./@key}" value="{./@value}"/>
                                        </xsl:for-each>
                                      </headers>
                                    </xsl:if>
                                    <body>
                                      <variable name="requestBody"/>
                                    </body>
                                  </http>
                                </dumpFile>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:variable name="remoteMultiProtocol" select="substring-before(wps:Reference/@xlink:href, ':')"/>
                                  <xsl:choose>
                                  <xsl:when test="(($remoteMultiProtocol = 'ftp') or ($remoteMultiProtocol = 'FTP'))">

                                    <xsl:variable name="address" select="substring-after(wps:Reference/@xlink:href, '://')"/>
                                    <xsl:variable name="credentials" select="substring-before($address, '@')"/>
                                    <xsl:variable name="user">
                                      <xsl:if test="$credentials">
                                        <xsl:value-of select="substring-before($credentials, ':')"/>
                                      </xsl:if>
                                    </xsl:variable>
                                    <xsl:variable name="password">
                                      <xsl:if test="$credentials">
                                        <xsl:value-of select="substring-after($credentials, ':')"/>
                                      </xsl:if>
                                    </xsl:variable>
                                    <xsl:variable name="tmpAddress">
                                      <xsl:choose>
                                        <xsl:when test="contains($address, '@')">
                                          <xsl:value-of select="substring-after($address, '@')"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="$address"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:variable>
                                      <xsl:variable name="tmpHost" select="substring-before($tmpAddress, '/')"/>
                                      <xsl:variable name="ftpHost">
                                        <xsl:choose>
                                          <xsl:when test="contains($tmpHost, ':')">
                                            <xsl:value-of select="substring-before($tmpHost, ':')"/>
                                          </xsl:when>
                                          <xsl:otherwise>
                                            <xsl:value-of select="$tmpHost"/>
                                          </xsl:otherwise>
                                        </xsl:choose>
                                      </xsl:variable>
                                      <xsl:variable name="ftpPort">
                                        <xsl:choose>
                                          <xsl:when test="contains($tmpHost, ':')">
                                            <xsl:value-of select="substring-after($tmpHost, ':')"/>
                                          </xsl:when>
                                          <xsl:otherwise>
                                            <xsl:value-of select="'21'"/>
                                          </xsl:otherwise>
                                        </xsl:choose>
                                      </xsl:variable>
                                      <xsl:variable name="ftpRemotePath" select="concat('/', substring-after($tmpAddress, '/'))"/>
                                      <ftpGet transfer="binary" pasv="true">
                                        <host>
                                          <string>
                                            <xsl:value-of select="$ftpHost"/>
                                          </string>
                                        </host>
                                        <port>
                                          <xsl:element name="literal">
                                            <xsl:attribute name="value">
                                              <xsl:value-of select="$ftpPort"/>
                                            </xsl:attribute>
                                          </xsl:element>
                                        </port>
                                        <user>
                                          <string>
                                            <xsl:value-of select="$user"/>
                                          </string>
                                        </user>
                                        <password>
                                          <string>
                                            <xsl:value-of select="$password"/>
                                          </string>
                                        </password>
                                        <remotePath>
                                          <string>
                                            <xsl:value-of select="$ftpRemotePath"/>
                                          </string>
                                        </remotePath>
                                        <localPath>
                                          <variable name="localURI"/>
                                        </localPath>
                                      </ftpGet>
                                  </xsl:when>
              
                                  <xsl:otherwise>
                                      <dumpFile fileType="binary">
                                      <variable name="localURI"/>
                                      <http fileType="binary" method="GET">
                                        <url>
                                          <string>
                                            <xsl:value-of select="wps:Reference/@xlink:href"/>
                                          </string>
                                        </url>
                                      </http>
                                    </dumpFile>
                                  </xsl:otherwise>
                                 </xsl:choose>   
                              </xsl:otherwise>
                            </xsl:choose>
                         



                             </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>

										</xsl:otherwise>
                    </xsl:choose>
                  </xsl:if>
                  <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/LiteralData">

								</xsl:if>
                  <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifierRequest]/BoundingBoxData">

								</xsl:if>
                </xsl:for-each>
              </sequence>
            </if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="GenericInputManager">
              <xsl:with-param name="genericInput" select="."/>
              <xsl:with-param name="processInformation" select="$processInformation"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <setVariable name="LocalURIReferenecesList">
        <string>${LocalURIReferenecesList},</string>
      </setVariable>
      <setVariable name="AttributeReferenecesList">
        <string>${AttributeReferenecesList},</string>
      </setVariable>
    </sequence>
  </xsl:template>
  <xsl:template name="GenericInputManager">
    <xsl:param name="genericInput"/>
    <xsl:param name="processInformation"/>
    <xsl:variable name="inputIdentifier" select="local-name($genericInput)"/>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/ComplexData">
      <xsl:call-template name="ComplexDataInputManager">
        <xsl:with-param name="requestData" select="$genericInput"/>
        <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/LiteralData">
      <xsl:call-template name="LiteralDataInputManager">
        <xsl:with-param name="requestData" select="."/>
        <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]/BoundingBoxData">
      <xsl:call-template name="BoundingBoxDataInputManager">
        <xsl:with-param name="requestData" select="."/>
        <xsl:with-param name="describeData" select="$processInformation/ProcessDescription/DataInputs/Input[ows:Identifier = $inputIdentifier]"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  <xsl:template name="ComplexDataInputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
    <xsl:apply-templates select="$requestData//wps:Reference"/>
  </xsl:template>
  <xsl:template name="LiteralDataInputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
    <xsl:apply-templates select="$requestData//wps:Reference"/>
    <!--LITERALDATA>
			<xsl:value-of select="$requestData"/>
			</LITERALDATA-->
  </xsl:template>
  <xsl:template name="BoundingBoxDataInputManager">
    <xsl:param name="requestData"/>
    <xsl:param name="describeData"/>
    <xsl:apply-templates select="$requestData//wps:Reference"/>
  </xsl:template>
  <xsl:template match="wps:Reference">
    <xsl:choose>
      <xsl:when test="wps:BodyReference">
        <setVariable name="ErrorType">
          <string>NoApplicableCode</string>
        </setVariable>
        <setVariable name="ErrorMessage">
          <string>
						"wps:BodyReference" tag not supported.
					</string>
        </setVariable>
      </xsl:when>
      <xsl:otherwise>
        <setVariable name="localURI">
          <string>
            <xsl:value-of select="$wpsDownloadedInputFolder"/>
            <xsl:value-of select="local-name(../.)"/>
          </string>
        </setVariable>
        <setVariable name="localInputName">
          <string>
            <xsl:value-of select="local-name(../.)"/>
          </string>
        </setVariable>
        <xsl:choose>
          <xsl:when test="@method = 'POST'">
            <xsl:if test="wps:Body">
              <setVariable name="requestBody">
                <xml textTag="evaluate" attributePrefix="%">
                  <xsl:copy-of select="wps:Body/*"/>
                </xml>
              </setVariable>
            </xsl:if>
            <dumpFile fileType="binary">
              <variable name="localURI"/>
              <http fileType="binary" method="POST">
                <url>
                  <string>
                    <xsl:value-of select="@xlink:href"/>
                  </string>
                </url>
                <xsl:if test="wps:Header">
                  <headers>
                    <xsl:for-each select="wps:Header">
                      <header key="{./@key}" value="{./@value}"/>
                    </xsl:for-each>
                  </headers>
                </xsl:if>
                <body>
                  <variable name="requestBody"/>
                </body>
              </http>
            </dumpFile>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="remoteProtocol" select="substring-before(@xlink:href, ':')">
						</xsl:variable>
            <xsl:choose>
              <xsl:when test="(($remoteProtocol = 'ftp') or ($remoteProtocol = 'FTP'))">
                <!-- 
									<xsl:variable name="ftpHost" select="substring-before(substring-after(@xlink:href, '://'),'/')">
									</xsl:variable>
									<xsl:variable name="ftpRemotePath" select="substring-after(substring-after(@xlink:href, '://'),'/')">
									</xsl:variable>
								-->
                <xsl:variable name="address" select="substring-after(@xlink:href, '://')"/>
                <xsl:variable name="credentials" select="substring-before($address, '@')"/>
                <xsl:variable name="user">
                  <xsl:if test="$credentials">
                    <xsl:value-of select="substring-before($credentials, ':')"/>
                  </xsl:if>
                </xsl:variable>
                <xsl:variable name="password">
                  <xsl:if test="$credentials">
                    <xsl:value-of select="substring-after($credentials, ':')"/>
                  </xsl:if>
                </xsl:variable>
                <xsl:variable name="tmpAddress">
                  <xsl:choose>
                    <xsl:when test="contains($address, '@')">
                      <xsl:value-of select="substring-after($address, '@')"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$address"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:variable name="tmpHost" select="substring-before($tmpAddress, '/')"/>
                <xsl:variable name="ftpHost">
                  <xsl:choose>
                    <xsl:when test="contains($tmpHost, ':')">
                      <xsl:value-of select="substring-before($tmpHost, ':')"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$tmpHost"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:variable name="ftpPort">
                  <xsl:choose>
                    <xsl:when test="contains($tmpHost, ':')">
                      <xsl:value-of select="substring-after($tmpHost, ':')"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="'21'"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:variable name="ftpRemotePath" select="concat('/', substring-after($tmpAddress, '/'))"/>
                <ftpGet transfer="binary" pasv="true">
                  <host>
                    <string>
                      <xsl:value-of select="$ftpHost"/>
                    </string>
                  </host>
                  <port>
                    <xsl:element name="literal">
                      <xsl:attribute name="value">
                        <xsl:value-of select="$ftpPort"/>
                      </xsl:attribute>
                    </xsl:element>
                  </port>
                  <user>
                    <string>
                      <xsl:value-of select="$user"/>
                    </string>
                  </user>
                  <password>
                    <string>
                      <xsl:value-of select="$password"/>
                    </string>
                  </password>
                  <remotePath>
                    <string>
                      <xsl:value-of select="$ftpRemotePath"/>
                    </string>
                  </remotePath>
                  <localPath>
                    <variable name="localURI"/>
                  </localPath>
                </ftpGet>
              </xsl:when>
              <xsl:otherwise>
                <dumpFile fileType="binary">
                  <variable name="localURI"/>
                  <http fileType="binary" method="GET">
                    <url>
                      <string>
                        <xsl:value-of select="@xlink:href"/>
                      </string>
                    </url>
                  </http>
                </dumpFile>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
        <setVariable name="LocalURIReferenecesList">
          <string>${LocalURIReferenecesList},${localURI}</string>
        </setVariable>
        <setVariable name="AttributeReferenecesList">
          <string>${AttributeReferenecesList},${localInputName}</string>
        </setVariable>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
