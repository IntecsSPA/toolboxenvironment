<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:gc="http://gisclient.pisa.intecs.it" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:gml="http://www.opengis.net/gml" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/> 
  <xsl:param name="bboxFormat">W,S,E,N</xsl:param>
  <xsl:template match="gc:KeyValues">
    <eoli:searchRequest>
      <eoli:simpleQuery>
        <eoli:dataExt>
          <eoli:geoEle operator="OVERLAP">
            <eoli:geoBndBox>
              <eoli:westBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">W</xsl:with-param>
                </xsl:call-template>
              </eoli:westBL>
              <eoli:eastBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">E</xsl:with-param>
                </xsl:call-template>
              </eoli:eastBL>
              <eoli:southBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">S</xsl:with-param>
                </xsl:call-template>
              </eoli:southBL>
              <eoli:northBL>
                <xsl:call-template name="getCoordinate">
                  <xsl:with-param name="bbox" select="gc:bbox"/>
                  <xsl:with-param name="format" select="$bboxFormat"/>
                  <xsl:with-param name="coordinate">N</xsl:with-param>
                </xsl:call-template>
              </eoli:northBL>
            </eoli:geoBndBox>
          </eoli:geoEle>
          <xsl:if test="gc:rangeDateStartDate">
            <eoli:tempEle operator="OVERLAP">
              <eoli:exTemp>
                <eoli:beginEnd>
                  <eoli:begin>
                    <xsl:value-of select="gc:rangeDateStartDate"/>
                  </eoli:begin>
                  <eoli:end>
                    <xsl:value-of select="gc:rangeDateEndDate"/>
                  </eoli:end>
                </eoli:beginEnd>
              </eoli:exTemp>
            </eoli:tempEle>
          </xsl:if>
        </eoli:dataExt>
        <eoli:satelliteDomainConditions>
          <xsl:if test="gc:platfSNm or gc:platfSer">
            <eoli:plaInsIdCondition operator="EQUAL">
              <eoli:plaInsId>
                <xsl:if test="gc:platfSNm">
                  <eoli:platfSNm>
                    <xsl:value-of select="gc:platfSNm"/>
                  </eoli:platfSNm>
                </xsl:if>
                <xsl:if test="gc:platfSer">
                  <eoli:platfSer>
                    <xsl:value-of select="gc:platfSer"/>
                  </eoli:platfSer>
                </xsl:if>
              </eoli:plaInsId>
            </eoli:plaInsIdCondition>
          </xsl:if>
          <xsl:if test="gc:cloudCovePerc">
            <eoli:cloudCoverCondition operator="LESS EQUAL">
              <eoli:cloudCovePerc>
                <xsl:value-of select="gc:cloudCovePerc"/>
              </eoli:cloudCovePerc>
            </eoli:cloudCoverCondition>
          </xsl:if>
        </eoli:satelliteDomainConditions>
      </eoli:simpleQuery>
      <eoli:resultType>
        <xsl:choose>
          <xsl:when test="gc:presentation = 'hits'">hits</xsl:when>
          <xsl:otherwise>results</xsl:otherwise>
        </xsl:choose>
      </eoli:resultType>
      <xsl:if test="gc:presentation != 'hits'">
        <xsl:if test="gc:iteratorSize != ''">
          <eoli:iteratorSize>
            <xsl:value-of select="gc:iteratorSize"/>
          </eoli:iteratorSize>
        </xsl:if>
        <xsl:if test="gc:cursor != ''">
          <eoli:cursor>
            <xsl:value-of select="gc:cursor"/>
          </eoli:cursor>
        </xsl:if>
        <eoli:presentation>
          <xsl:value-of select="gc:presentation"/>
        </eoli:presentation>
      </xsl:if>
      <eoli:collectionId>
        <xsl:value-of select="gc:collectionId"/>
      </eoli:collectionId>
    </eoli:searchRequest>
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
