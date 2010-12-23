<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: WSDL.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.3 $
 -  Revision Date:     $Date: 2004/04/15 12:16:49 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:param name="rfq"/>
	<xsl:param name="order"/>
	<xsl:param name="search"/>
	<xsl:param name="present"/>
	<xsl:param name="push"/>
	<xsl:param name="synchronous"/>
	<xsl:param name="serviceName"/>
	<xsl:param name="serviceSchema"/>
	<xsl:param name="url"/>
	<xsl:template match="/">
		<!--<definitions xmlns:tns="http://www.companyA.com/" xmlns:mass="http://www.esa.int/mass" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" targetNamespace="http://www.companyA.com/">-->
                	<xsl:choose>
				<xsl:when test="$synchronous = ''">
                                    <definitions xmlns:tns="http://www.companyA.com/" xmlns:mass="http://www.esa.int/mass" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" targetNamespace="http://www.companyA.com/">
					<import namespace="http://www.esa.int/mass">
						<xsl:attribute name="location"><xsl:value-of select="concat($url, '/TOOLBOX/WSDL/', $serviceName, '/mass.xsd')"/></xsl:attribute>
					</import>
					<import namespace="http://www.esa.int/mass">
						<xsl:attribute name="location"><xsl:value-of select="concat($url, '/TOOLBOX/WSDL/', $serviceName, '/', $serviceSchema)"/></xsl:attribute>
					</import>
					<xsl:choose>
						<xsl:when test="$rfq != ''">
							<message name="sendRFQInput">
								<part name="rfqInput" element="mass:sendRFQInputMsg"/>
							</message>
							<message name="sendRFQOutput">
								<part name="status" element="mass:sendRFQOutputMsg"/>
							</message>
							<xsl:choose>
								<xsl:when test="$push = ''">
									<message name="checkRFQResultInput">
										<part name="rfqInfo" element="mass:checkRFQResultInputMsg"/>
									</message>
									<message name="checkRFQResultOutput">
										<part name="rfqStatus" element="mass:checkRFQResultOutputMsg"/>
									</message>
									<message name="getRFQResultInput">
										<part name="rfqInfo" element="mass:getRFQResultInputMsg"/>
									</message>
									<message name="getRFQResultOutput">
										<part name="rfqResult" element="mass:getRFQResultOutputMsg"/>
									</message>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="$order != ''">
							<message name="sendOrderInput">
								<part name="orderInput" element="mass:sendOrderInputMsg"/>
							</message>
							<xsl:choose>
								<xsl:when test="$push = ''">
									<message name="sendOrderOutput">
										<part name="status" element="mass:sendOrderOutputMsg"/>
									</message>
									<message name="checkOrderResultInput">
										<part name="orderInfo" element="mass:checkOrderResultInputMsg"/>
									</message>
									<message name="checkOrderResultOutput">
										<part name="orderStatus" element="mass:checkOrderResultOutputMsg"/>
									</message>
									<message name="getOrderResultInput">
										<part name="orderInfo" element="mass:getOrderResultInputMsg"/>
									</message>
									<message name="getOrderResultOutput">
										<part name="orderResult" element="mass:getOrderResultOutputMsg"/>
									</message>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
					</xsl:choose>
					<portType>
						<xsl:attribute name="name"><xsl:value-of select="concat($serviceName, 'PortType')"/></xsl:attribute>
						<xsl:choose>
							<xsl:when test="$rfq != ''">
								<operation name="sendRFQ">
									<input message="tns:sendRFQInput"/>
									<output message="tns:sendRFQOutput"/>
								</operation>
								<xsl:choose>
									<xsl:when test="$push = ''">
										<operation name="checkRFQResult">
											<input message="tns:checkRFQResultInput"/>
											<output message="tns:checkRFQResultOutput"/>
										</operation>
										<operation name="getRFQResult">
											<input message="tns:getRFQResultInput"/>
											<output message="tns:getRFQResultOutput"/>
										</operation>
									</xsl:when>
								</xsl:choose>
							</xsl:when>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="$order != ''">
								<operation name="sendOrder">
									<input message="tns:sendOrderInput"/>
									<output message="tns:sendOrderOutput"/>
								</operation>
								<xsl:choose>
									<xsl:when test="$push = ''">
										<operation name="checkOrderResult">
											<input message="tns:checkOrderResultInput"/>
											<output message="tns:checkOrderResultOutput"/>
										</operation>
										<operation name="getOrderResult">
											<input message="tns:getOrderResultInput"/>
											<output message="tns:getOrderResultOutput"/>
										</operation>
									</xsl:when>
								</xsl:choose>
							</xsl:when>
						</xsl:choose>
					</portType>
					<binding>
						<xsl:attribute name="name"><xsl:value-of select="concat($serviceName, 'Binding')"/></xsl:attribute>
						<xsl:attribute name="type"><xsl:value-of select="concat('tns:', $serviceName, 'PortType')"/></xsl:attribute>
						<documentation>All operations are bound to SOAP actions.  Two types are possible, 
                    RPC or document.  The "document" style shall be used in MASS.</documentation>
						<xsl:choose>
							<xsl:when test="$rfq != ''">
								<operation name="sendRFQ">
									<soap:operation style="document">
										<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/sendRFQ')"/></xsl:attribute>
									</soap:operation>
									<input>
										<soap:body use="literal"/>
									</input>
									<output>
										<soap:body use="literal"/>
									</output>
								</operation>
								<xsl:choose>
									<xsl:when test="$push = ''">
										<operation name="checkRFQResult">
											<soap:operation style="document">
												<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/checkRFQResult')"/></xsl:attribute>
											</soap:operation>
											<input>
												<soap:body use="literal"/>
											</input>
											<output>
												<soap:body use="literal"/>
											</output>
										</operation>
										<operation name="getRFQResult">
											<soap:operation style="document">
												<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/getRFQResult')"/></xsl:attribute>
											</soap:operation>
											<input>
												<soap:body use="literal"/>
											</input>
											<output>
												<soap:body use="literal"/>
											</output>
										</operation>
									</xsl:when>
								</xsl:choose>
							</xsl:when>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="$order != ''">
								<operation name="sendOrder">
									<soap:operation style="document">
										<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/sendOrder')"/></xsl:attribute>
									</soap:operation>
									<input>
										<soap:body use="literal"/>
									</input>
									<output>
										<soap:body use="literal"/>
									</output>
								</operation>
								<xsl:choose>
									<xsl:when test="$push = ''">
										<operation name="checkOrderResult">
											<soap:operation style="document">
												<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/checkOrderResult')"/></xsl:attribute>
											</soap:operation>
											<input>
												<soap:body use="literal"/>
											</input>
											<output>
												<soap:body use="literal"/>
											</output>
										</operation>
										<operation name="getOrderResult">
											<soap:operation style="document">
												<xsl:attribute name="soapAction"><xsl:value-of select="concat($url, '/', $serviceName, '/soap/getOrderResult')"/></xsl:attribute>
											</soap:operation>
											<input>
												<soap:body use="literal"/>
											</input>
											<output>
												<soap:body use="literal"/>
											</output>
										</operation>
									</xsl:when>
								</xsl:choose>
							</xsl:when>
						</xsl:choose>
					</binding>
					<service>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<documentation>
							<xsl:value-of select="$serviceName"/>
						</documentation>
						<port>
							<xsl:attribute name="name"><xsl:value-of select="concat('tns:', $serviceName, 'Port')"/></xsl:attribute>
							<xsl:attribute name="binding"><xsl:value-of select="concat('tns:', $serviceName, 'Binding')"/></xsl:attribute>
							<soap:address>
								<xsl:attribute name="location"><xsl:value-of select="concat($url, '/TOOLBOX/SOAPAccess')"/></xsl:attribute>
							</soap:address>
						</port>
					</service>
					<documentation>
						<xsl:value-of select="$serviceName"/>
					</documentation>
                                        </definitions>
				</xsl:when>
				<xsl:otherwise>
                                    <definitions xmlns:tns="http://www.esa.int/mass" xmlns:mass="http://www.esa.int/mass" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" targetNamespace="http://www.esa.int/mass">
					<import namespace="http://www.esa.int/mass">
						<xsl:attribute name="location"><xsl:value-of select="concat($url, '/TOOLBOX/WSDL/', $serviceName, '/', $serviceSchema)"/></xsl:attribute>
					</import>
					<xsl:if test="$search != ''">
						<message name="processSearchInput">
							<part name="parameters" element="mass:processSearchInputMsg"/>
						</message>
						<message name="processSearchOutput">
							<part name="parameters" element="mass:processSearchOutputMsg"/>
						</message>
					</xsl:if>
					<xsl:if test="$present != ''">
						<message name="processPresentInput">
							<part name="parameters" element="mass:processPresentInputMsg"/>
						</message>
						<message name="processPresentOutput">
							<part name="parameters" element="mass:processPresentOutputMsg"/>
						</message>
					</xsl:if>
                                        <xsl:if test="$rfq != ''">
						<message name="processRFQInput">
							<part name="parameters" element="mass:processRFQInputMsg"/>
						</message>
						<message name="processRFQOutput">
							<part name="parameters" element="mass:processRFQOutputMsg"/>
						</message>
					</xsl:if>
                                        <xsl:if test="$order != ''">
						<message name="processOrdertInput">
							<part name="parameters" element="mass:processOrderInputMsg"/>
						</message>
						<message name="processOrderOutput">
							<part name="parameters" element="mass:processOrderOutputMsg"/>
						</message>
					</xsl:if>
					<portType>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<xsl:if test="$search != ''">
							<operation name="processSearch">
								<input name="processSearchInput" message="tns:processSearchInput"/>
								<output name="processSearchOutput" message="tns:processSearchOutput"/>
							</operation>
						</xsl:if>
						<xsl:if test="$present != ''">
							<operation name="processPresent">
								<input name="processPresentInput" message="tns:processPresentInput"/>
								<output name="processPresentOutput" message="tns:processPresentOutput"/>
							</operation>
						</xsl:if>
                                                <xsl:if test="$rfq != ''">
							<operation name="processRFQ">
								<input name="processRFQInput" message="tns:processRFQInput"/>
								<output name="processRFQOutput" message="tns:processRFQOutput"/>
							</operation>
						</xsl:if>
                                                <xsl:if test="$order != ''">
							<operation name="processOrder">
								<input name="processOrderInput" message="tns:processOrderInput"/>
								<output name="processOrderOutput" message="tns:processOrderOutput"/>
							</operation>
						</xsl:if>
					</portType>
					<binding type="tns:$serviceName">
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/>SoapBinding</xsl:attribute>
						<xsl:attribute name="type">tns:<xsl:value-of select="$serviceName"/></xsl:attribute>
						<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
						<xsl:if test="$search != ''">
							<operation name="processSearch">
								<soap:operation soapAction="processSearch"/>
								<input name="processSearchInput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</input>
								<output name="processSearchOutput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</output>
							</operation>
						</xsl:if>
						<xsl:if test="$present != ''">
							<operation name="processPresent">
								<soap:operation soapAction="processPresent"/>
								<input name="processPresentInput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</input>
								<output name="processPresentOutput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</output>
							</operation>
						</xsl:if>
                                                <xsl:if test="$rfq != ''">
							<operation name="processRFQ">
								<soap:operation soapAction="processRFQ"/>
								<input name="processRFQInput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</input>
								<output name="processRFQOutput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</output>
							</operation>
						</xsl:if>
                                                <xsl:if test="$order != ''">
							<operation name="processOrder">
								<soap:operation soapAction="processOrder"/>
								<input name="processOrderInput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</input>
								<output name="processOrderOutput">
									<soap:body use="literal" namespace="http://www.esa.int/mass"/>
								</output>
							</operation>
						</xsl:if>
					</binding>
					<service>
						<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
						<port binding="tns:RasterClippingServiceSoapBinding">
							<xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
							<xsl:attribute name="binding">tns:<xsl:value-of select="$serviceName"/>SoapBinding</xsl:attribute>
							<soap:address>
								<xsl:attribute name="location"><xsl:value-of select="concat($url, '/TOOLBOX/SOAPAccess')"/></xsl:attribute>
							</soap:address>
						</port>
					</service>
                                        </definitions>
				</xsl:otherwise>
			</xsl:choose>
		<!--</definitions>-->
	</xsl:template>
</xsl:stylesheet>
