<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/>
  <xsl:template match="/SoapFault">
     
         <xsl:copy-of select="*"></xsl:copy-of>
    
  </xsl:template>
  <xsl:template match="/eoli:response">
     
    <response>
        
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
            <xsl:copy-of select="eoli:mdContact"/>
        </xsl:when>
        <xsl:otherwise>
          <eoli:mdContact>
            <eoli:rpOrgName>-</eoli:rpOrgName>
            <eoli:role>-</eoli:role>
          </eoli:mdContact>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="eoli:mdDateSt">
          <xsl:copy-of select="eoli:mdDateSt"/>
        </xsl:when>
        <xsl:otherwise>
          <eoli:mdDateSt>-</eoli:mdDateSt>
        </xsl:otherwise>
      </xsl:choose>
      <eoli:dataIdInfo>
   
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:prcTypeCode">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:prcTypeCode"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:prcTypeCode>
              <eoli:identCode>-</eoli:identCode>
            </eoli:prcTypeCode>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:plaInsId">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:plaInsId"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:plaInsId>
              <eoli:platfSNm>-</eoli:platfSNm>
              <eoli:instShNm>-</eoli:instShNm>
            </eoli:plaInsId>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:satDom">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:satDom"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:satDom>
              <eoli:orbit>-</eoli:orbit>
              <eoli:lastOrbit>-</eoli:lastOrbit>
              <eoli:orbitDir>-</eoli:orbitDir>
              <eoli:wwRefSys>
                <eoli:frame>-</eoli:frame>
                <eoli:track>-</eoli:track>
              </eoli:wwRefSys>
              <eoli:passCoverage>
                <eoli:start>-</eoli:start>
                <eoli:stop>-</eoli:stop>
              </eoli:passCoverage>
            </eoli:satDom>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idCitation">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idCitation"/>
          </xsl:when>
          <xsl:otherwise>
            <idCitation>
              <eoli:resTitle>-</eoli:resTitle>
            </idCitation>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idAbs">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idAbs"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:idAbs>-</eoli:idAbs>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:idStatus">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:idStatus"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:idStatus>-</eoli:idStatus>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="eoli:dataIdInfo/eoli:dataExt">
            <xsl:copy-of select="eoli:dataIdInfo/eoli:dataExt"/>
          </xsl:when>
          <xsl:otherwise>
            <eoli:dataExt>
              <eoli:tempEle>
                <eoli:exTemp>
                  <eoli:beginEnd>
                    <eoli:begin>-</eoli:begin>
                    <eoli:end>-</eoli:end>
                  </eoli:beginEnd>
                </eoli:exTemp>
              </eoli:tempEle>
              <eoli:geoEle>
                <eoli:polygon>
                  <eoli:coordinates>-</eoli:coordinates>
                </eoli:polygon>
                <eoli:scCenter>
                  <eoli:coordinates>-</eoli:coordinates>
                </eoli:scCenter>
              </eoli:geoEle>
            </eoli:dataExt>
          </xsl:otherwise>
        </xsl:choose>
      </eoli:dataIdInfo>
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