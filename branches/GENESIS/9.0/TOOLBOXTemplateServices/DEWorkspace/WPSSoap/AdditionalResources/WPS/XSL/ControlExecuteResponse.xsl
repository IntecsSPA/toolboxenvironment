<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="outputFolder">/home/maro/Programmi/apache-tomcat-6.0.16/webapps/TOOLBOX/WEB-INF/services/WPSDummy/Log/11062009182133192/output/</xsl:param>
<xsl:param name="outputHttpRequest">http://localhost:8080/TOOLBOX/services</xsl:param>
  <!-- INCLUDE SECTION-->
  <!-- -->
  <xsl:template match="wps:ExecuteResponse">
    <wps:ExecuteResponse xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="{@service}" serviceInstance="{@serviceInstance}" statusLocation="{@statusLocation}" version="{@version}" xml:lang="{@xml:lang}">
      <xsl:copy-of select="wps:Process"/>
      <xsl:copy-of select="wps:Status"/>
      <xsl:if test="wps:ProcessOutputs">
        <xsl:variable name="processOutputsVar">
          <wps:ProcessOutputs>
            <xsl:for-each select="wps:ProcessOutputs/wps:Output">
              <xsl:choose>
                <xsl:when test="wps:Reference or wps:Data/wps:LiteralData or wps:Data/wps:BoundingBoxData">
                  <xsl:copy-of select="."/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:if test="wps:Data">
                    <wps:Output>
                      <xsl:copy-of select="ows:Identifier"/>
                      <xsl:copy-of select="ows:Title"/>
                      <xsl:copy-of select="ows:Abstract"/>
                      <xsl:variable name="correctOutputValuePath" select="string(concat($outputHttpRequest, ows:Identifier, '_Value.xml'))"/>

                      <xsl:choose>
                        <xsl:when test="document($correctOutputValuePath)">
                          <xsl:variable name="documentCorrectResult" select="document($correctOutputValuePath)"/>
                          <xsl:copy-of select="$documentCorrectResult"/>
                        </xsl:when>
                        <!--xsl:otherwise>
                    <wps:Data>
                       <wps:ComplexData>Complex Data ERROR</wps:ComplexData>
                    </wps:Data>
                  </xsl:otherwise-->
                      </xsl:choose>
                    </wps:Output>
                  </xsl:if>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </wps:ProcessOutputs>
        </xsl:variable>
        <xsl:if test="$processOutputsVar//wps:Output/wps:Reference or $processOutputsVar//wps:Output/wps:Data">
          <xsl:copy-of select="$processOutputsVar"/>
        </xsl:if>
      </xsl:if>
    </wps:ExecuteResponse>
  </xsl:template>
</xsl:stylesheet>
