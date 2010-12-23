<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ows="http://www.opengis.net/ows/1.1" version="1.0">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <xsl:param name="wpsSchemaLocation">test</xsl:param>
  <xsl:template match="ProcessDescription">
    <xs:schema xmlns="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" targetNamespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
      <xs:import namespace="http://www.opengis.net/wps/1.0.0" schemaLocation="file:{$wpsSchemaLocation}"/>
        
            
    
      <xs:element name="ExecuteProcess_{ows:Identifier}">
        <xs:complexType>
          <xs:sequence>
          <xsl:for-each select="DataInputs/Input">
              <!-- xs:element ref="{ows:Identifier}"  maxOccurs="unbounded" minOccurs="0"/-->
              <!-- maxOccurs="{@maxOccurs}" minOccurs="{@minOccurs} -->
              <xsl:choose>
                <xsl:when test="ComplexData">
                  <xs:element name="{ows:Identifier}" minOccurs="0" maxOccurs="unbounded">
                    <xs:complexType>
                      <xs:choice>
                        <xs:element ref="wps:ComplexData"/>
                        <xs:element ref="wps:Reference"/>
                      </xs:choice>
                    </xs:complexType>
                  </xs:element>
                  <!-- xs:complexType
										name="ComplexSoapExecute_{ows:Identifier}">
										<xs:choice>
											<xs:element
												ref="wps:ComplexData" />
											<xs:element
												ref="wps:Reference" />
										</xs:choice>
									</xs:complexType -->
                  <!-- xs:element name="{ows:Identifier}"
										type="ComplexSoapExecute_{ows:Identifier}" /-->
                </xsl:when>
                <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="LiteralData">
                      <!--  xs:simpleType
												name="uom_{ows:Identifier}">
												<xs:restriction
													base="xs:string" />
											</xs:simpleType>-->
                      <xs:element name="{ows:Identifier}" minOccurs="0" maxOccurs="unbounded">
                        <xs:complexType>
                          <xs:simpleContent>
                            <xs:extension base="xs:string">
                              <xs:attribute name="uom" use="optional">
                                <xs:simpleType>
                                  <xs:restriction base="xs:string"/>
                                </xs:simpleType>
                              </xs:attribute>
                            </xs:extension>
                          </xs:simpleContent>
                        </xs:complexType>
                      </xs:element>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:choose>
                        <xsl:when test="BoundingBoxData">
                          <xs:element name="{ows:Identifier}" minOccurs="0" maxOccurs="unbounded">
                            <xs:complexType>
                              <xs:sequence>
                                <xs:element ref="wps:BoundingBoxData"/>
                              </xs:sequence>
                            </xs:complexType>
                          </xs:element>
                        </xsl:when>
                      </xsl:choose>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
            <xsl:for-each select="ProcessOutputs/Output">
              <!-- xs:element ref="{ows:Identifier}" minOccurs="0" /> -->
              <!-- maxOccurs="1" minOccurs="0"-->
              <xs:element name="{ows:Identifier}" minOccurs="0" maxOccurs="unbounded">
                <xsl:choose>
                  <xsl:when test="ComplexOutput">
                    <xs:complexType>
                      <xs:attribute name="mimeType" use="optional"/>
                      <xs:attribute name="schema" use="optional"/>
                      <xs:attribute name="encoding" use="optional"/>
                      <xs:attribute name="asReference" type="xs:boolean" use="optional"/>
                    </xs:complexType>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="LiteralOutput">
                        <xs:complexType>
                          <xs:attribute name="uom" type="xs:string" use="optional"/>
                          <xs:attribute name="asReference" type="xs:boolean" use="optional"/>
                        </xs:complexType>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                          <xsl:when test="BoundingBoxOutput">
                            <xs:complexType>
                              <xs:attribute name="asReference" type="xs:boolean" use="optional"/>
                            </xs:complexType>
                          </xsl:when>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>
              </xs:element>
            </xsl:for-each>
          </xs:sequence>
          <xs:attribute name="storeExecuteResponse" type="xs:boolean" use="optional"/>
          <xs:attribute name="lineage" type="xs:boolean" use="optional"/>
          <xs:attribute name="status" type="xs:boolean" use="optional"/>
        </xs:complexType>
      </xs:element>
    </xs:schema>
  </xsl:template>
</xsl:stylesheet>
