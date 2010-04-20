<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/>
  <xsl:template match="/SoapFault">
     
         <xsl:copy-of select="*"></xsl:copy-of>
    
  </xsl:template>
  <xsl:template match="/eoli:response">
     
    <response>
         <a><xsl:value-of select="name()"></xsl:value-of></a>
         <!--b><xsl:value-of select="name(././*)"></xsl:value-of></b-->
        <retrievedData>
            <xsl:apply-templates select="eoli:retrievedData/eoli:Metadata"></xsl:apply-templates>
        </retrievedData>
        <xsl:copy-of select="eoli:cursor"/>
        <xsl:copy-of select="eoli:hits"/>
        <xsl:copy-of select="eoli:status"/>
    </response> 
    <!--xsl:copy-of select="*"/-->
  </xsl:template>
  <xsl:template match="eoli:retrievedData/eoli:Metadata">
    <Metadata xmlns="http://earth.esa.int/XML/eoli">
      <xsl:choose>
        <xsl:when test="eoli:mdContact">
          <mdContact>
            <xsl:copy-of select="eoli:mdContact"/>
          </mdContact>
        </xsl:when>
        <xsl:otherwise>
          <mdContact>
            <rpOrgName>-</rpOrgName>
            <role>-</role>
          </mdContact>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="eoli:mdDateSt">
          <xsl:copy-of select="eoli:mdDateSt"/>
        </xsl:when>
        <xsl:otherwise>
          <mdDateSt>-</mdDateSt>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:element name="dataIdInfo" namespace="http://earth.esa.int/XML/eoli">
   
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:prcTypeCode">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:prcTypeCode"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="prcTypeCode" namespace="http://earth.esa.int/XML/eoli">
              <xsl:element name="identCode" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:plaInsId">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:plaInsId"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="plaInsId" namespace="http://earth.esa.int/XML/eoli">
              <xsl:element name="platfSNm" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              <xsl:element name="instShNm" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:satDom">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:satDom"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="satDom" namespace="http://earth.esa.int/XML/eoli">
              <xsl:element name="orbit" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              <xsl:element name="lastOrbit" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              <xsl:element name="orbitDir" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              <xsl:element name="wwRefSys" namespace="http://earth.esa.int/XML/eoli">
                <xsl:element name="frame" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                <xsl:element name="track" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              </xsl:element>
              <xsl:element name="passCoverage" namespace="http://earth.esa.int/XML/eoli">
                <xsl:element name="start" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                <xsl:element name="stop" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
              </xsl:element>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idCitation">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idCitation"/>
          </xsl:when>
          <xsl:otherwise>
            <idCitation>
              <xsl:element name="resTitle" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
            </idCitation>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idAbs">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idAbs"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="idAbs" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idStatus">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idStatus"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="idStatus" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:dataExt">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:dataExt"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="dataExt" namespace="http://earth.esa.int/XML/eoli">
              <xsl:element name="tempEle" namespace="http://earth.esa.int/XML/eoli">
                <xsl:element name="exTemp" namespace="http://earth.esa.int/XML/eoli">
                  <xsl:element name="beginEnd" namespace="http://earth.esa.int/XML/eoli">
                    <xsl:element name="begin" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                    <xsl:element name="end" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                  </xsl:element>
                </xsl:element>
              </xsl:element>
              <xsl:element name="geoEle" namespace="http://earth.esa.int/XML/eoli">
                <xsl:element name="polygon" namespace="http://earth.esa.int/XML/eoli">
                  <xsl:element name="coordinates" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                </xsl:element>
                <xsl:element name="scCenter" namespace="http://earth.esa.int/XML/eoli">
                  <xsl:element name="coordinates" namespace="http://earth.esa.int/XML/eoli">-</xsl:element>
                </xsl:element>
              </xsl:element>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
      <dqInfo>
        <graphOver>
          <xsl:choose>
            <xsl:when test="eoli:dqInfo/eoli:graphOver/eoli:bgFileName">
              <bgFileName><xsl:value-of select="eoli:dqInfo/eoli:graphOver[1]/eoli:bgFileName"></xsl:value-of></bgFileName>  
              
            </xsl:when>
            <xsl:otherwise>
              <bgFileName>-</bgFileName>
            </xsl:otherwise>
          </xsl:choose>
        </graphOver>
      </dqInfo>
    </Metadata>
  </xsl:template>
</xsl:stylesheet>