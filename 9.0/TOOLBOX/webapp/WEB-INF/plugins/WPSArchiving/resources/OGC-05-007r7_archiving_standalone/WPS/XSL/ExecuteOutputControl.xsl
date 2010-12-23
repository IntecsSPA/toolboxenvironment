<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="outputRepository"/>
  <xsl:variable name="processName" select="substring-after( local-name(*), '_' )"/>
  <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', $processName, '.xml')))"/>
  <!-- 
INCLUDE SECTION
<xsl:include href=""/>
-->
  <xsl:template match="*">
    <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
      <xsl:variable name="executeRequest" select="../."/>
      <if>
        <or>
          <xsl:for-each select="$executeRequest/*/child::*">
            <xsl:variable name="identifierExecuteRequest" select="local-name()"/>
            <xsl:for-each select="$processInformation/ProcessDescription/ProcessOutputs/Output">
              <xsl:variable name="outputIdentifierDescribe" select="ows:Identifier"/>
              <xsl:if test="$identifierExecuteRequest = $outputIdentifierDescribe">
                <fileExists>
                  <string><xsl:value-of select="$outputRepository"/><xsl:value-of select="$identifierExecuteRequest"/>NULL.xml</string>
                </fileExists>
              </xsl:if>
            </xsl:for-each>
          </xsl:for-each>
        </or>
        <sequence>
          <setVariable name="outputsControl">
            <string>ProcessFailed</string>
          </setVariable>
          <setVariable name="outputsException">
            <string>NoApplicableCode</string>
          </setVariable>
          <setVariable name="outputsExceptionMessage">
            <string>Output Not Generated</string>
          </setVariable>  
        </sequence>
        <sequence>
          <if>
            <and>
              <xsl:for-each select="$executeRequest/*/child::*">
                <xsl:variable name="identifierExecuteRequest" select="local-name()"/>
                <xsl:for-each select="$processInformation/ProcessDescription/ProcessOutputs/Output">
                  <xsl:variable name="outputIdentifierDescribe" select="ows:Identifier"/>
                  <xsl:if test="$identifierExecuteRequest = $outputIdentifierDescribe">
                    <or>
                      <fileExists>
                        <string><xsl:value-of select="$outputRepository"/><xsl:value-of select="$identifierExecuteRequest"/>.xml</string>
                      </fileExists>
                      <fileExists>
                        <string><xsl:value-of select="$outputRepository"/><xsl:value-of select="$identifierExecuteRequest"/></string>
                      </fileExists>
                    </or>
                  </xsl:if>
                </xsl:for-each>
              </xsl:for-each>
            </and>
            <sequence>
            <setVariable name="outputsControl">
              <string>ProcessSucceeded</string>
            </setVariable>
          </sequence>
          <sequence>
            <setVariable name="outputsControl">
              <string>ProcessPending</string>
            </setVariable>
          </sequence>  
          </if>
        </sequence>
      </if>
    </sequence>
  </xsl:template>
</xsl:stylesheet>
