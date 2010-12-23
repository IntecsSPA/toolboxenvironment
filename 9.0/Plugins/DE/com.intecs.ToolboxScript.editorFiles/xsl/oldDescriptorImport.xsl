<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"  xmlns:ed="http://pisa.intecs.it/mass/toolbox/exportDescriptor"  xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns="http://pisa.intecs.it/mass/toolbox/serviceDescriptor">
	<xsl:template match="ed:exportDescriptor">
		<xsl:apply-templates select="sd:service"/>
	</xsl:template>
	
	<xsl:template match="sd:service">
		<service xmlns="http://pisa.intecs.it/mass/toolbox/serviceDescriptor">
			<xsl:attribute name="queuing"><xsl:value-of select="@queuing"/></xsl:attribute>
			<xsl:attribute name="serviceName"><xsl:value-of select="@serviceName"/></xsl:attribute>
			<xsl:attribute name="serviceSchema"><xsl:value-of select="@serviceSchema"/></xsl:attribute>
			<xsl:attribute name="sslCertificate"><xsl:value-of select="@sslCertificate"/></xsl:attribute>
			<xsl:attribute name="suspendMode"><xsl:value-of select="@suspendMode"/></xsl:attribute>
			<xsl:attribute name="version">2.0</xsl:attribute>
			<xsl:attribute name="wsdlTargetNS"><xsl:value-of select="@wsdlTargetNS"/></xsl:attribute>
			<interface name="" version="">
				<rootSchemaFile><xsl:value-of select="sd:schemas/sd:schema[1]/@schemaName"/></rootSchemaFile>
				<schemaSetLocation>Schemas</schemaSetLocation>
				<wsdlFile/>
				<targetNameSpace><xsl:value-of select="@wsdlTargetNS"/></targetNameSpace>
				<operations>
					<xsl:for-each select="sd:operations/sd:operation">
						<operation>
							<xsl:attribute name="SOAPAction"><xsl:value-of select="sd:script[1]/@soapAction"/></xsl:attribute>
							<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
							<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
							<xsl:if test="@type = 'asynchronous'">
								<xsl:attribute name="callbackSOAPAction"><xsl:value-of select="sd:wsdlInfo/sd:pushMessage/@soapAction"/></xsl:attribute>
								<xsl:attribute name="pollingRate"><xsl:value-of select="@pollingRate"/></xsl:attribute>
								<xsl:attribute name="requestTimeout"><xsl:value-of select="@requestTimeout"/></xsl:attribute>
								<xsl:attribute name="retryAttempts"><xsl:value-of select="@retryAttempts"/></xsl:attribute>
								<xsl:attribute name="retryRate"><xsl:value-of select="@retryRate"/></xsl:attribute>
							</xsl:if>
							<inputType>
								<xsl:attribute name="namespace"><xsl:value-of select="sd:wsdlInfo/sd:requestMessage/@ns"/></xsl:attribute>
								<xsl:attribute name="type"><xsl:value-of select="sd:wsdlInfo/sd:requestMessage/@name"/></xsl:attribute>
							</inputType>
							<outputType>
								<xsl:attribute name="namespace"><xsl:value-of select="sd:wsdlInfo/sd:responseMessage/@ns"/></xsl:attribute>
								<xsl:attribute name="type"><xsl:value-of select="sd:wsdlInfo/sd:responseMessage/@name"/></xsl:attribute>
							</outputType>
							<xsl:if test="@type = 'asynchronous'">
								<callbackInputType>
									<xsl:attribute name="namespace"><xsl:value-of select="sd:wsdlInfo/sd:pushMessage/@ns"/></xsl:attribute>
									<xsl:attribute name="type"><xsl:value-of select="sd:wsdlInfo/sd:pushMessage/@name"/></xsl:attribute>
								</callbackInputType>
								<callbackOutputType>
									<xsl:attribute name="namespace"><xsl:value-of select="sd:wsdlInfo/sd:pushResponse/@ns"/></xsl:attribute>
									<xsl:attribute name="type"><xsl:value-of select="sd:wsdlInfo/sd:pushResponse/@name"/></xsl:attribute>
								</callbackOutputType>
							</xsl:if>
							<xsl:copy-of select="sd:admittedHosts"/>
							<xsl:if test="sd:script[1]">
								<scriptFile>
									<xsl:attribute name="path">Operations/<xsl:value-of select="@name"/>/script_1.tscript</xsl:attribute>
									<xsl:attribute name="type">FIRST_SCRIPT</xsl:attribute>
								</scriptFile>
							</xsl:if>
							<xsl:if test="sd:script[2]">
								<scriptFile>
									<xsl:attribute name="path">Operations/<xsl:value-of select="@name"/>/script_2.tscript</xsl:attribute>
									<xsl:attribute name="type">SECOND_SCRIPT</xsl:attribute>
								</scriptFile>
							</xsl:if>
							<xsl:if test="sd:script[3]">
								<scriptFile>
									<xsl:attribute name="path">Operations/<xsl:value-of select="@name"/>/script_3.tscript</xsl:attribute>
									<xsl:attribute name="type">THIRD_SCRIPT</xsl:attribute>
								</scriptFile>
							</xsl:if>
							<xsl:if test="sd:script[4]">
								<scriptFile>
									<xsl:attribute name="path">Operations/<xsl:value-of select="@name"/>/script_4.tscript</xsl:attribute>
									<xsl:attribute name="type">RESP_BUILDER</xsl:attribute>
								</scriptFile>
							</xsl:if>
						</operation>
					</xsl:for-each>
				</operations>
			</interface>
		</service>
	</xsl:template>
	
	
</xsl:stylesheet>
