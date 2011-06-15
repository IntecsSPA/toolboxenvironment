<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="processingName">RasterDiff</xsl:param>
  <xsl:template match="*">
    <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
      <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processingName, '.xml')))"/>
      <setVariable name="check">
        <literal type="boolean" value="true"/>
      </setVariable>
      <setVariable name="SoapActionUpdateState">
        <string>ExecuteProcess_<xsl:value-of select="$processingName"/>_ASYNC_statusUpdate</string>
      </setVariable>
      
      <!-- OUTPUTS CONTROL-->
      <xsl:for-each select="child::*">
        <xsl:variable name="inputIdentifierRequest" select="local-name()"/>
        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifierRequest]/LiteralOutput">
          <if>
            <or>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/>.xml</string>
              </fileExists>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/>NULL.xml</string>
              </fileExists>
            </or>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="true"/>
                </and>
              </setVariable>
            </sequence>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="false"/>
                </and>
              </setVariable>
            </sequence>
          </if>
        </xsl:if>
        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifierRequest]/ComplexOutput">
          <if>
            <or>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/></string>
              </fileExists>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/>NULL.xml</string>
              </fileExists>
            </or>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="true"/>
                </and>
              </setVariable>
            </sequence>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="false"/>
                </and>
              </setVariable>
            </sequence>
          </if>
        </xsl:if>
        <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs/Output[ows:Identifier = $inputIdentifierRequest]/BoundingBoxOutput">
          <if>
            <or>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/>.xml</string>
              </fileExists>
              <fileExists>
                <string>${wpsOutputFolder}<xsl:value-of select="$inputIdentifierRequest"/>NULL.xml</string>
              </fileExists>
            </or>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="true"/>
                </and>
              </setVariable>
            </sequence>
            <sequence>
              <setVariable name="check">
                <and>
                  <variable name="check"/>
                  <literal type="boolean" value="false"/>
                </and>
              </setVariable>
            </sequence>
          </if>
        </xsl:if>
      </xsl:for-each>
      <!-- END OUTPUTS CONTROL-->
      <if><oeq><variable name="status"/><string>true</string></oeq>.
		   <sequence><!-- Check Status --><if><fileExists><variable name="wpsStatusPercentageFile"/></fileExists><sequence><setVariable name="newPercentage"><xPath><loadXML><variable name="wpsStatusPercentageFile"/></loadXML><string>Percentage/@value</string></xPath></setVariable><if><oneq><variable name="newPercentage"/><variable name="currentPercentage"/></oneq><sequence><setVariable name="currentPercentage"><variable name="newPercentage"/></setVariable><setVariable name="statusExecuteResponse"><!-- Generate Execute Response --><xslt xmlOutput="true"><variable name="processRequest"/><path startFrom="SERVICE_RESOURCE_DIR"><string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string></path><parameter name="asynch"><string>true</string></parameter><parameter name="statusEnable"><variable name="status"/></parameter><parameter name="lineage"><variable name="lineage"/></parameter><parameter name="processName"><variable name="executeProcessName"/></parameter><parameter name="status"><variable name="statusVariable"/></parameter><parameter name="statusMessage"><string/></parameter><parameter name="statusLocation"><variable name="statusLocationVariable"/></parameter><parameter name="creationTime"><dateStringFormatter format="yyyy-MM-dd'T'HH:mm:ss'Z'"><now/></dateStringFormatter></parameter><parameter name="outputHttpRequest"><variable name="toolboxGetServiceResourceURL"/></parameter><parameter name="particularReferenceID"><variable name="particularReferenceIdentifier"/></parameter><parameter name="particularReferenceURL"><variable name="particularReferenceURL"/></parameter><parameter name="outputFolder"><variable name="wpsOutputFolder"/></parameter><parameter name="statusPercentCompleted"><variable name="currentPercentage"/></parameter><!-- parameter name="originalRequest">
                                  <xmlRequest/>
                                 </parameter -->&gt;
                                <parameter name="serviceName"><variable name="serviceName"/></parameter>
                                <parameter name="dateTime"><variable name="wpsProcessingInstance"/></parameter>
                                <parameter name="geoserverOWSURL"><geoserverOWSUrl xmlns="http://pisa007.pisa.intecs.it/toolbox/GISPlugin"/></parameter>
                              </xslt><!-- End Response--></setVariable>
                              <setVariable name="randomMessageID">
                              	<randomString length="16" />
                              </setVariable>
                              <!--  Dump New Status --><dumpXML><string>${wpsStatusFolder}executeResponseStatus.xml</string><variable name="statusExecuteResponse"/></dumpXML><soapCall messageId="$randomMessageID" relatesTo="$instanceKey" operation="$SoapActionUpdateState"><variable name="replyTo"/><variable name="statusExecuteResponse"/></soapCall><!--  End Dump New Status --></sequence></if></sequence></if><!-- End Check Status --></sequence></if>
      <log>
        <string>Second Script CONTROL: ${check}</string>
      </log>
      <variable name="check"/>
    </sequence>
  </xsl:template>
</xsl:stylesheet>
