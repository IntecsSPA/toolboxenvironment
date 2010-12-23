<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:gc="http://gisclient.pisa.intecs.it" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns="http://earth.esa.int/XML/eoli" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:gml="http://www.opengis.net/gml" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/> 
  <xsl:param name="bboxFormat">W,S,E,N</xsl:param>
  <xsl:template match="gc:KeyValues">
    <searchRequest xmlns="http://earth.esa.int/XML/eoli">
      <simpleQuery>
        <dataExt>
          <geoEle operator="OVERLAP">
            <geoBndBox>
              <westBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">W</xsl:with-param>
                </xsl:call-template>
              </westBL>
              <eastBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">E</xsl:with-param>
                </xsl:call-template>
              </eastBL>
              <southBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">S</xsl:with-param>
                </xsl:call-template>
              </southBL>
              <northBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">N</xsl:with-param>
                </xsl:call-template>
              </northBL>
            </geoBndBox>
          </geoEle>
          <xsl:if test="gc:rangeDateStartDate">
            <tempEle operator="OVERLAP">
              <exTemp>
                <beginEnd>
                  <begin>
                    <xsl:value-of select="gc:rangeDateStartDate"/>
                  </begin>
                  <end>
                    <xsl:value-of select="gc:rangeDateEndDate"/>
                  </end>
                </beginEnd>
              </exTemp>
            </tempEle>
          </xsl:if>
        </dataExt>
        <satelliteDomainConditions>
          <xsl:if test="gc:platfSNm or gc:platfSer">
            <plaInsIdCondition operator="EQUAL">
              <plaInsId>
                <xsl:if test="gc:platfSNm">
                  <platfSNm>
                    <xsl:value-of select="gc:platfSNm"/>
                  </platfSNm>
                </xsl:if>
                <xsl:if test="gc:platfSer">
                  <platfSer>
                    <xsl:value-of select="gc:platfSer"/>
                  </platfSer>
                </xsl:if>
              </plaInsId>
            </plaInsIdCondition>
          </xsl:if>
          <xsl:if test="gc:cloudCovePerc">
            <cloudCoverCondition operator="LESS EQUAL">
              <cloudCovePerc>
                <xsl:value-of select="gc:cloudCovePerc"/>
              </cloudCovePerc>
            </cloudCoverCondition>
          </xsl:if>
        </satelliteDomainConditions>
      </simpleQuery>
      <resultType>
        <xsl:choose>
          <xsl:when test="gc:presentation = 'hits'">hits</xsl:when>
          <xsl:otherwise>results</xsl:otherwise>
        </xsl:choose>
      </resultType>
      <xsl:if test="gc:presentation != 'hits'">
        <xsl:if test="gc:iteratorSize != ''">
          <iteratorSize>
            <xsl:value-of select="gc:iteratorSize"/>
          </iteratorSize>
        </xsl:if>
        <xsl:if test="gc:cursor != ''">
          <cursor>
            <xsl:value-of select="gc:cursor"/>
          </cursor>
        </xsl:if>
        <presentation>
          <xsl:value-of select="gc:presentation"/>
        </presentation>
      </xsl:if>
      <collectionId>
        <xsl:value-of select="gc:collectionId"/>
      </collectionId>
    </searchRequest>
  </xsl:template>
  <xsl:template name="getCoordinate">
    <xsl:param name="bbox"/>
    <xsl:param name="format"/>
    <xsl:param name="coordinate"/>
    <xsl:variable name="firstBbox" select="substring-before($bbox, ',')"/>
    <xsl:variable name="firstFormat" select="substring-before($format, ',')"/>
    <xsl:variable name="remainingBbox" select="substring-after($bbox, ',')"/>
    <xsl:variable name="remainingFormat" select="substring-after($format, ',')"/>
    <xsl:choose>
    <xsl:when test="$format = $coordinate">
      <xsl:value-of select="$bbox"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$firstFormat = $coordinate">
          <xsl:value-of select="$firstBbox"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="getCoordinate">
            <xsl:with-param name="bbox" select="$remainingBbox"/>
            <xsl:with-param name="format" select="$remainingFormat"/>
            <xsl:with-param name="coordinate" select="$coordinate"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
