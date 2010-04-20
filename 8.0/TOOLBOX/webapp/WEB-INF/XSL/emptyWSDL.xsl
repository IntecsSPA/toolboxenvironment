<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:param name="serviceName"/>
	<xsl:param name="targetNS"/>
	<xsl:param name="url"/>
	<xsl:param name="eoliSearch"/>
	<xsl:param name="eoliPresent"/>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$targetNS = 'http://earth.esa.int/XML/eoli'">
				<definitions xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tns="http://earth.esa.int/XML/dsm" xmlns:eoli="http://earth.esa.int/XML/eoli" targetNamespace="http://earth.esa.int/XML/dsm">
					<import namespace="http://earth.esa.int/XML/eoli">
                                            <xsl:attribute name="location"><xsl:value-of select="substring-before($url, 'services')"/>WSDL/<xsl:value-of select="$serviceName"/>/eoli.xsd</xsl:attribute>
					</import>
					<xsl:if test="$eoliSearch != ''">
						<message name="searchRequestInput">
							<part name="searchRequestParameter" element="eoli:searchRequest"/>
						</message>
					</xsl:if>
					<xsl:if test="$eoliPresent != ''">
						<message name="presentRequestInput">
							<part name="searchRequestParameter" element="eoli:presentRequest"/>
						</message>
					</xsl:if>
					<message name="responseOutput">
						<part name="responseParameter" element="eoli:response"/>
					</message>
					<portType name="dsmPortType">
						<xsl:if test="$eoliSearch != ''">
							<operation name="processSearchRequest">
								<input message="tns:searchRequestInput"/>
								<output message="tns:responseOutput"/>
							</operation>
						</xsl:if>
						<xsl:if test="$eoliPresent != ''">
							<operation name="processPresentRequest">
								<input message="tns:presentRequestInput"/>
								<output message="tns:responseOutput"/>
							</operation>
						</xsl:if>
					</portType>
					<binding name="dsmBinding" type="tns:dsmPortType">
						<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
						<xsl:if test="$eoliSearch != ''">
							<operation name="processSearchRequest">
								<soap:operation soapAction="http://earth.esa.int/SOAP/searchRequest"/>
								<input>
									<soap:body use="literal"/>
								</input>
								<output>
									<soap:body use="literal"/>
								</output>
							</operation>
						</xsl:if>
						<xsl:if test="$eoliPresent != ''">
							<operation name="processPresentRequest">
								<soap:operation soapAction="http://earth.esa.int/SOAP/presentRequest"/>
								<input>
									<soap:body use="literal"/>
								</input>
								<output>
									<soap:body use="literal"/>
								</output>
							</operation>
						</xsl:if>
					</binding>
					<service>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<port name="dsmSoap" binding="tns:dsmBinding">
							<soap:address>
								<xsl:attribute name="location"><xsl:value-of select="$url"/></xsl:attribute>
							</soap:address>
						</port>
					</service>
				</definitions>
			</xsl:when>
			<xsl:otherwise>
				<definitions xmlns:tns="http://services.eoportal.org/mass/wsdl" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:plnk="http://schemas.xmlsoap.org/ws/2003/05/partner-link/" xmlns:wsa="http://schemas.xmlsoap.org/ws/2003/03/addressing" xmlns:mass="http://www.esa.int/mass">
					<xsl:attribute name="targetNamespace"><xsl:value-of select="$targetNS"/></xsl:attribute>
					<types>
						<schema attributeFormDefault="qualified" elementFormDefault="qualified" targetNamespace="http://schemas.xmlsoap.org/wsdl/" xmlns="http://www.w3.org/2001/XMLSchema">
							<import namespace="http://www.esa.int/mass" schemaLocation="NPPBasic.xsd"/>
							<import namespace="http://schemas.xmlsoap.org/ws/2003/03/addressing" schemaLocation="http://schemas.xmlsoap.org/ws/2003/03/addressing"/>
						</schema>
					</types>
					<message name="StartHeader">
						<part name="MessageID" element="wsa:MessageID"/>
						<part name="ReplyTo" element="wsa:ReplyTo"/>
					</message>
					<message name="ContinueHeader">
						<part name="RelatesTo" element="wsa:RelatesTo"/>
					</message>
					<portType>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
					</portType>
					<portType>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>Callback</xsl:attribute>
					</portType>
					<binding>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>SoapBinding</xsl:attribute>
						<xsl:attribute name="type">tns:<xsl:value-of select="$serviceName"/></xsl:attribute>
						<wsdlsoap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
					</binding>
					<binding>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>CallbackSoapBinding</xsl:attribute>
						<xsl:attribute name="type">tns:<xsl:value-of select="$serviceName"/>Callback</xsl:attribute>
						<wsdlsoap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
					</binding>
					<service>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<port>
							<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
							<xsl:attribute name="binding">tns:<xsl:value-of select="$serviceName"/>SoapBinding</xsl:attribute>
							<wsdlsoap:address>
								<xsl:attribute name="location"><xsl:value-of select="$url"/></xsl:attribute>
							</wsdlsoap:address>
						</port>
					</service>
					<service>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>Callback</xsl:attribute>
						<port>
							<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>Callback</xsl:attribute>
							<xsl:attribute name="binding">tns:<xsl:value-of select="$serviceName"/>CallbackSoapBinding</xsl:attribute>
							<wsdlsoap:address location="http://openuri.org"/>
						</port>
					</service>
					<plnk:partnerLinkType>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<plnk:role>
							<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>ServiceProvider</xsl:attribute>
							<plnk:portType>
								<xsl:attribute name="name">tns:<xsl:value-of select="$serviceName"/></xsl:attribute>
							</plnk:portType>
						</plnk:role>
						<plnk:role>
							<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>ServiceRequester</xsl:attribute>
							<plnk:portType>
								<xsl:attribute name="name">tns:<xsl:value-of select="$serviceName"/>Callback</xsl:attribute>
							</plnk:portType>
						</plnk:role>
					</plnk:partnerLinkType>
				</definitions>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>