<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:owsInput="http://www.opengis.net/ows/1.1" xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core" xmlns:rich="http://richfaces.org/rich" xmlns:c="http://java.sun.com/jstl/core" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:a4j="http://richfaces.org/a4j" xmlns:spb="http://www.spacebel.be/genesis/jsf" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:x="http://does_not_matter" xmlns:mass="http://www.esa.int/mass" xmlns:serviceNS="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap" xmlns:gml="http://www.opengis.net/gml" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>
	<xsl:template match="/">
		<xsl:param name="wpsName" select="//ProcessDescription/owsInput:Identifier"/>
		<x:stylesheet version="1.0" xmlns:mass="http://www.esa.int/mass" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:serviceNS="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap" xmlns:gml="http://www.opengis.net/gml" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures">
			<x:param name="part"/>
			<x:template match="/*">
            <x:choose>
					<!-- Used for service's WPS presentation and processing -->
					<x:when test="$part='sendOrderInputHTML'">
						<x:apply-templates select="mass:sendOrderInput"/>
					</x:when>
					<x:when test="$part='processOrderInputXML'">
						<x:apply-templates select="mass:sendOrderInput" mode="XML">
							<x:with-param name="storeExecuteResponse">false</x:with-param>
						</x:apply-templates>
					</x:when>
					<x:when test="$part='sendOrderInputXML'">
						<x:apply-templates select="mass:sendOrderInput" mode="XML">
							<x:with-param name="storeExecuteResponse">true</x:with-param>
						</x:apply-templates>
					</x:when>
					<x:otherwise/>
				</x:choose>
			</x:template>
			<x:template match="mass:sendOrderInput">
				<inputOrderDefaults>
					<!--
				  <input type="string" name="exampleParameter" value="exampleDefaultValue"/>
				 -->
					<xsl:apply-templates select="//ProcessDescription/DataInputs/Input[@maxOccurs &gt; 1 and count(LiteralData/owsInput:AllowedValues/owsInput:Value) &gt; 1]/LiteralData/owsInput:AllowedValues/owsInput:Value">
						<xsl:with-param name="type">defaultValues</xsl:with-param>
					</xsl:apply-templates>
				</inputOrderDefaults>
			</x:template>
			<!-- This template displays the WPS request wrapped by portal request -->
			<x:template match="mass:sendOrderInput" mode="XML">
				<x:param name="storeExecuteResponse"/>
				<sendOrderInputMsg xmlns="http://www.spacebel.be/ws/wps" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
					<mass:commonInput>
						<mass:orderId>
							<x:value-of select="orderId"/>
						</mass:orderId>
					</mass:commonInput>
					<sendOrderInput>
						<xsl:element name="wps:ExecuteProcess_{$wpsName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
							<xsl:attribute name="lineage">false</xsl:attribute>
							<xsl:attribute name="status">false</xsl:attribute>
							<xsl:attribute name="storeExecuteResponse">{$storeExecuteResponse}</xsl:attribute>
							<xsl:apply-templates select="//ProcessDescription/DataInputs/Input">
								<xsl:with-param name="type">wpsRequest</xsl:with-param>
							</xsl:apply-templates>
							<xsl:apply-templates select="//ProcessDescription/ProcessOutputs/Output"/>
						</xsl:element>
					</sendOrderInput>
				</sendOrderInputMsg>
			</x:template>
			<!-- The templates below display the WPS response -->
			<x:template match="serviceNS:getOrderOutput">
				<orderResults>
					<x:apply-templates select="*"/>
				</orderResults>
			</x:template>
			<x:template match="wps:ExecuteResponse" xmlns:wps="http://www.opengis.net/wps/1.0.0">
				<x:for-each select="wps:ProcessOutputs/wps:Output">
					<x:choose>
						<x:when test="ows:Identifier = 'OutputContext' or ows:Identifier = 'OutputWMC'" xmlns:ows="http://www.opengis.net/ows/1.1">
							<property name="Web Map Context" type="mapcontext">
								<x:value-of select="wps:Reference/@xlink:href" xmlns:xlink="http://www.w3.org/1999/xlink"/>
							</property>
						</x:when>
						<x:when test="ows:Identifier = 'OutputGML'" xmlns:ows="http://www.opengis.net/ows/1.1">
							<property name="GML" type="gml">
								<x:value-of select="wps:Reference/@xlink:href" xmlns:xlink="http://www.w3.org/1999/xlink"/>
							</property>
						</x:when>
						<x:otherwise>
							<property name="{{ows:Identifier}}" type="link" xmlns:ows="http://www.opengis.net/ows/1.1">
								<x:value-of select="wps:Reference/@xlink:href" xmlns:xlink="http://www.w3.org/1999/xlink"/>
							</property>
						</x:otherwise>
					</x:choose>
				</x:for-each>
			</x:template>
			<x:template name="trim">
				<x:param name="string" select="''"/>
				<x:variable name="string_length" select="string-length($string)"/>
				<x:variable name="first_char" select="substring($string, 1, 1)"/>
				<x:variable name="last_char" select="substring($string, $string_length, 1)"/>
				<x:choose>
					<x:when test="$first_char = ' '">
						<!-- chop first character (space) -->
						<x:call-template name="trim">
							<x:with-param name="string" select="substring($string, 2)"/>
						</x:call-template>
					</x:when>
					<x:when test="$last_char = ' '">
						<!-- chop last character (space) -->
						<x:call-template name="trim">
							<x:with-param name="string" select="substring($string, 1, $string_length - 1)"/>
						</x:call-template>
					</x:when>
					<x:otherwise>
						<x:value-of select="$string"/>
					</x:otherwise>
				</x:choose>
			</x:template>
		</x:stylesheet>
	</xsl:template>
	<xsl:template match="Input">
		<xsl:param name="type"/>
		<xsl:param name="parameterName" select="owsInput:Identifier"/>
		<xsl:if test="$type = 'defaultValues'">
			<xsl:apply-templates select="LiteralData/owsInput:AllowedValues/owsInput:Value">
				<xsl:with-param name="type">defaultValues</xsl:with-param>
			</xsl:apply-templates>
		</xsl:if>
		<xsl:if test="$type = 'wpsRequest'">
			<!-- BBOX -->
			<!--  xsl:if test="exists(BoundingBoxData)">-->
			<xsl:if test="BoundingBoxData">
				<xsl:if test="BoundingBoxData/*">
					<x:if test="not({$parameterName}='')">
						<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
							<xsl:element name="ns:BoundingBoxData" namespace="http://www.opengis.net/wps/1.0.0">
								<x:variable name="a" select="substring-before(//aoi:AreaOfInterest//gml:boundedBy/gml:Envelope/gml:lowerCorner, ' ')"/>
								<x:variable name="b" select="substring-after(//aoi:AreaOfInterest//gml:boundedBy/gml:Envelope/gml:lowerCorner, ' ')"/>
								<x:variable name="c" select="substring-before(//aoi:AreaOfInterest//gml:boundedBy/gml:Envelope/gml:upperCorner, ' ')"/>
								<x:variable name="d" select="substring-after(//aoi:AreaOfInterest//gml:boundedBy/gml:Envelope/gml:upperCorner, ' ')"/>
								<x:variable name="space" select="' '"/>
								<ows:LowerCorner xmlns:ows="http://www.opengis.net/ows/1.1">
									<x:value-of select="$a"/>
									<x:value-of select="$space"/>
									<x:value-of select="$b"/>
								</ows:LowerCorner>
								<ows:UpperCorner xmlns:ows="http://www.opengis.net/ows/1.1">
									<x:value-of select="$c"/>
									<x:value-of select="$space"/>
									<x:value-of select="$d"/>
								</ows:UpperCorner>
							</xsl:element>
						</xsl:element>
					</x:if>
				</xsl:if>
			</xsl:if>
			<!-- File -->
			<!-- xsl:if test="exists(ComplexData)"> -->
			<xsl:if test="ComplexData">
				<xsl:if test="ComplexData/*">
					<x:if test="not({$parameterName}='')">
						<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
							<x:variable name="strippedParameterValue">
								<x:call-template name="trim">
									<x:with-param name="string">
										<x:value-of select="{$parameterName}"/>
									</x:with-param>
								</x:call-template>
							</x:variable>
							<xsl:element name="ns:Reference" namespace="http://www.opengis.net/wps/1.0.0">
								<xsl:attribute name="xlink:href" namespace="http://www.w3.org/1999/xlink">{$strippedParameterValue}</xsl:attribute>
								<xsl:attribute name="method">GET</xsl:attribute>
							</xsl:element>
						</xsl:element>
					</x:if>
				</xsl:if>
			</xsl:if>
			<!--  xsl:if test="exists(LiteralData)"-->
			<xsl:if test="LiteralData">
				<xsl:if test="LiteralData/*">
					<!-- no list with allowed values given, read from input box -->
					<xsl:if test="count(LiteralData/owsInput:AllowedValues/owsInput:Value) = 0">
						<x:if test="not({$parameterName}='')">
							<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
								<x:variable name="strippedParameterValue">
									<x:call-template name="trim">
										<x:with-param name="string">
											<x:value-of select="{$parameterName}"/>
										</x:with-param>
									</x:call-template>
								</x:variable>
								<x:value-of select="$strippedParameterValue"/>
							</xsl:element>
						</x:if>
					</xsl:if>
					<!-- list with allowed values given, read from select box or a number of check boxes -->
					<xsl:if test="count(LiteralData/owsInput:AllowedValues/owsInput:Value) &gt; 0">
						<!-- if only one value is allowed, read from select box -->
						<xsl:if test="not(@maxOccurs &gt; 1)">
							<x:if test="not({$parameterName}='')">
								<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
									<x:value-of select="{$parameterName}"/>
								</xsl:element>
							</x:if>
						</xsl:if>
						<!-- if multiple values are allowed, read from check boxes -->
						<xsl:if test="@maxOccurs &gt; 1">
							<xsl:apply-templates select="LiteralData/owsInput:AllowedValues/owsInput:Value">
								<xsl:with-param name="type">wpsRequest</xsl:with-param>
							</xsl:apply-templates>
						</xsl:if>
					</xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template match="owsInput:Value">
		<xsl:param name="type"/>
		<!-- parameter name for check box is WPS parameter name + underscore + parameter value -->
		<xsl:param name="wpsParameterName">
			<xsl:value-of select="../../../owsInput:Identifier"/>
		</xsl:param>
		<xsl:param name="wpsParameterValue">
			<xsl:value-of select="."/>
		</xsl:param>
		<xsl:param name="parameterName">
			<xsl:value-of select="$wpsParameterName"/>
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$wpsParameterValue"/>
		</xsl:param>
		<xsl:if test="$type = 'defaultValues'">
			<input type="boolean" name="{$parameterName}" value="false"/>
		</xsl:if>
		<xsl:if test="$type = 'wpsRequest'">
			<x:if test="{$parameterName}='true'">
				<xsl:element name="wps:{$wpsParameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
					<xsl:value-of select="$wpsParameterValue"/>
				</xsl:element>
			</x:if>
		</xsl:if>
	</xsl:template>
	<xsl:template match="Output">
		<xsl:param name="parameterName" select="owsInput:Identifier"/>
		<!-- parameter format = owsInput:Identifier + 'Format' -->
		<xsl:param name="parameterFormat">
			<xsl:value-of select="owsInput:Identifier"/>
			<xsl:text>Format</xsl:text>
		</xsl:param>
		<!-- 1. parameterFormat exists and is not empty: use that value -->
		<x:if test="{$parameterFormat} and not({$parameterFormat}='')">
			<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
				<xsl:attribute name="asReference">true</xsl:attribute>
				<xsl:attribute name="mimeType">{<xsl:value-of select="$parameterFormat"/>}</xsl:attribute>
			</xsl:element>
		</x:if>
		<x:if test="not({$parameterFormat}) or {$parameterFormat}=''">
			<!-- 2. if parameterFormat doesn't exist or is empty, take the default if it exists -->
			<xsl:if test="count(ComplexOutput/Default/Format/MimeType) = 1">
				<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
					<xsl:attribute name="asReference">true</xsl:attribute>
					<xsl:attribute name="mimeType"><xsl:value-of select="ComplexOutput/Default/Format/MimeType"/></xsl:attribute>
				</xsl:element>
			</xsl:if>
			<!-- 3. if parameterFormat doesn't exist or is empty, and there is no default MIME type, don't specify a MIME type -->
			<xsl:if test="not(count(ComplexOutput/Default/Format/MimeType) = 1)">
				<xsl:element name="wps:{$parameterName}" namespace="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap">
					<xsl:attribute name="asReference">true</xsl:attribute>
				</xsl:element>
			</xsl:if>
		</x:if>
	</xsl:template>
</xsl:stylesheet>
