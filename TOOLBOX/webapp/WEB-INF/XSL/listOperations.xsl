<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs S.P.A.
 -  File Name:         $RCSfile: listOperations.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2004/05/21 15:11:58 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:param name="orderBy">operation</xsl:param>
	<xsl:param name="serviceName"/>
	<xsl:param name="manageOperationsURL"/>
	<xsl:param name="configureOperationURL"/>
	<xsl:param name="deleteOperationURL"/>
        <xsl:param name="viewWSDLInfoURL"></xsl:param>
        <xsl:param name="language"/>
        <xsl:param name="opTxt">
            <xsl:if test="$language = 'it'">Operazione</xsl:if>
            <xsl:if test="$language = 'en'">Operation</xsl:if>
        </xsl:param>
        <xsl:param name="typeTxt">
            <xsl:if test="$language = 'it'">Tipo</xsl:if>
            <xsl:if test="$language = 'en'">Type</xsl:if>
        </xsl:param>
        <xsl:param name="confTxt">
            <xsl:if test="$language = 'it'">Configura</xsl:if>
            <xsl:if test="$language = 'en'">Configure</xsl:if>
        </xsl:param>
       <xsl:param name="delTxt">
            <xsl:if test="$language = 'it'">Cancella</xsl:if>
            <xsl:if test="$language = 'en'">Delete</xsl:if>
        </xsl:param>
        
        <xsl:template match="/">
        	<xsl:apply-templates select="sd:service/sd:interface/sd:operations"/>
        </xsl:template>
        
	<xsl:template match="sd:operations">
		<table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
			<tr>
				<td class="sortableHeader" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=operation</xsl:attribute><xsl:value-of select="$opTxt"/></a>
				</td>
				<td class="sortableHeader" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=SOAPAction</xsl:attribute>SOAP Action</a>
				</td>
				<td class="sortableHeader" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=type</xsl:attribute><xsl:value-of select="$typeTxt"/></a>
				</td>
				<td class="sortableHeader" height="50">Timeout</td>
				<td class="sortableHeader" height="50">Polling Rate</td>
				<td class="sortableHeader" height="50">Retry attempts</td>
				<td class="sortableHeader" height="50">Retry rate</td>
				<td class="sortableHeader" height="50">Push Hosts</td>
				<td class="sortableHeader" height="50">Tools</td>
			</tr>
			<xsl:if test="$orderBy='noOrder'">
				<xsl:apply-templates select="sd:operation"/>
			</xsl:if>
			<xsl:if test="$orderBy='operation'">
				<xsl:apply-templates select="sd:operation">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='SOAPAction'">
				<xsl:apply-templates select="sd:operation">
					<xsl:sort select="sd:script/@SOAPAction"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='type'">
				<xsl:apply-templates select="sd:operation">
					<xsl:sort select="@type"/>
				</xsl:apply-templates>
			</xsl:if>
		</table>
	</xsl:template>
	
	<xsl:template match="sd:admittedHost">
		<xsl:value-of select="."/>
		<br/>
	</xsl:template>
	
	<xsl:template match="sd:operation">
		<tr>
			<td class="sortable">
				<xsl:value-of select="@name"/>
			</td>
			<td class="sortable">
				<xsl:value-of select="@SOAPAction"/>
			</td>
			<td class="sortable">
				<xsl:value-of select="@type"/>
			</td>
			<td class="sortable">
                            <xsl:if test="@type = 'asynchronous'">
				<xsl:value-of select="@requestTimeout"/>
                            </xsl:if>
			</td>
			<td class="sortable">
				<xsl:if test="@type = 'asynchronous'">
					<xsl:value-of select="@pollingRate"/>
				</xsl:if>
				
			</td>
			<td class="sortable">
				<xsl:if test="@type = 'asynchronous'">
					<xsl:value-of select="@retryAttempts"/>
				</xsl:if>
				
			</td>
			<td class="sortable">
				<xsl:if test="@type = 'asynchronous'">
					<xsl:value-of select="@retryRate"/>
				</xsl:if>
				
			</td>
			<td class="sortable">
				<xsl:apply-templates select="sd:admittedHosts/sd:admittedHost"/>
			</td>
			<td class="sortable" align="center">
                    <a>
					<xsl:attribute name="href"><xsl:value-of select="$configureOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/></xsl:attribute>
					<xsl:value-of select="$confTxt"/></a> - 
					<a>
					<xsl:attribute name="href"><xsl:value-of select="$viewWSDLInfoURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/></xsl:attribute>
					Info
					</a> - 
                     <a>
					<xsl:attribute name="href">javascript:confirm('<xsl:value-of select="$deleteOperationURL"/>&amp;serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/>','Delete operations?','Confirm')</xsl:attribute>
					<xsl:value-of select="$delTxt"/>
					</a>
			</td>
		</tr>
	</xsl:template>
		<xsl:template match="sd:abstract">
	</xsl:template>

	<xsl:template match="sd:description">
	</xsl:template>

</xsl:stylesheet>

