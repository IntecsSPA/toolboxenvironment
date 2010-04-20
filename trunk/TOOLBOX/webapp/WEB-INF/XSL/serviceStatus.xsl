<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: serviceStatus.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.6 $
 -  Revision Date:     $Date: 2004/06/28 16:00:01 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tss="http://pisa.intecs.it/mass/toolbox/serviceStatus" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:param name="serviceName"/>
	<xsl:param name="orderBy"/>
	<xsl:param name="viewServiceStatusURL"/>
	<xsl:param name="viewServiceReqURL"/>
	<xsl:param name="deleteServiceReqRequestURL"/>
	<xsl:param name="helpURL"/>
	<xsl:param name="push"/>
        <xsl:param name="language"/>
        <xsl:param name="operation">
            <xsl:if test="$language = 'it'">Operazione</xsl:if>
            <xsl:if test="$language = 'en'">Operation</xsl:if>
        </xsl:param>        
        <xsl:param name="status">
            <xsl:if test="$language = 'it'">Stato</xsl:if>
            <xsl:if test="$language = 'en'">Status</xsl:if>
        </xsl:param>        
        <xsl:param name="arrivalDate">
            <xsl:if test="$language = 'it'">Data di arrivo</xsl:if>
            <xsl:if test="$language = 'en'">Arrival Date</xsl:if>
        </xsl:param>        
        <xsl:param name="backEnd">
            <xsl:if test="$language = 'it'">Back-end Expiration Date</xsl:if>
            <xsl:if test="$language = 'en'">Back-end Expiration Date</xsl:if>
        </xsl:param>                
	<xsl:template match="tss:serviceStatus">
		<table width="100%" height="90%" bordercolor="#808080" cellpadding="3" cellspacing="1" border="0" align="center" valign="middle">
			<tr>
				<td class="logTableHeading" height="50" valign="middle" align="center">
					<a target="help">
						<xsl:attribute name="href"><xsl:value-of select="$helpURL"/></xsl:attribute>
						help
					</a>
				</td>
				<td class="logTableHeading" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=operation</xsl:attribute><xsl:value-of select="$operation"/></a>
				</td>
				<td class="logTableHeading" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=messageId</xsl:attribute>Message Id</a>
				</td>
                                <td class="logTableHeading" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=orderId</xsl:attribute>Order Id</a>
				</td>
				<xsl:if test="$push != ''">
					<td class="logTableHeading" height="50">
						<a>
							<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=hostName</xsl:attribute>Push Host</a>
					</td>
				</xsl:if>
				<td class="logTableHeading" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=status</xsl:attribute><xsl:value-of select="$status"/></a>
				</td>
				<td class="logTableHeading" height="50">XML request</td>
				<td class="logTableHeading" height="50">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=arrivalDateTime</xsl:attribute>
<xsl:value-of select="$arrivalDate"/></a>
				</td>
				<td class="logTableHeading" height="50">
					<a pippo="{concat('@',string($orderBy))}">
						<xsl:attribute name="href"><xsl:value-of select="$viewServiceStatusURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=serviceExpirationDateTime</xsl:attribute>
<xsl:value-of select="$backEnd"/></a>
				</td>
			</tr>
			<xsl:if test="$orderBy='noOrder'">
				<xsl:apply-templates select="tss:request"/>
			</xsl:if>
			<xsl:if test="$orderBy='operation'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@operation"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='messageId'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@modifiedMessageId"/>
				</xsl:apply-templates>
			</xsl:if>
                        <xsl:if test="$orderBy='orderId'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@orderId"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='hostName'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@hostName"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='status'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@status"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='arrivalDateTime'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@arrivalDateTime"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='serviceExpirationDateTime'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@serviceExpirationDateTime"/>
				</xsl:apply-templates>
			</xsl:if>
			<xsl:if test="$orderBy='clientExpirationDateTime'">
				<xsl:apply-templates select="tss:request">
					<xsl:sort select="@clientExpirationDateTime"/>
				</xsl:apply-templates>
			</xsl:if>
		</table>
	</xsl:template>
	<xsl:template match="tss:request">
		<tr>
			<td class="logTableHeading" align="center">
				<a>
					<xsl:choose>
						<xsl:when test="$push != ''">
							<xsl:attribute name="href"><xsl:value-of select="$deleteServiceReqRequestURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;host=<xsl:value-of select="@hostName"/>&amp;messageId=<xsl:value-of select="@modifiedMessageId"/>&amp;operation=<xsl:value-of select="@operation"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="href"><xsl:value-of select="$deleteServiceReqRequestURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;messageId=<xsl:value-of select="@modifiedMessageId"/>&amp;operation=<xsl:value-of select="@operation"/></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<img src="images/deleteRequest_off.gif" alt="delete" border="0">
						<xsl:attribute name="name">m<xsl:number value="position()" format="1"/></xsl:attribute>
					</img>
				</a>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@operation"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@modifiedMessageId"/>
			</td>
                        <td class="logTableInfo">
				<xsl:value-of select="@orderId"/>
			</td>
			<xsl:if test="$push != ''">
				<td class="logTableInfo">
					<xsl:value-of select="@hostName"/>
				</td>
			</xsl:if>
			<td class="logTableInfo">
				<xsl:value-of select="@status"/>
			</td>
			<td class="logTableInfo">
				<a>
				<xsl:choose>
					<xsl:when test="$push != ''"><xsl:attribute name="href"><xsl:value-of select="$viewServiceReqURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;host=<xsl:value-of select="@hostName"/>&amp;messageId=<xsl:value-of select="@modifiedMessageId"/>&amp;operation=<xsl:value-of select="@operation"/></xsl:attribute>view_request</xsl:when>
					<xsl:otherwise><xsl:attribute name="href"><xsl:value-of select="$viewServiceReqURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;messageId=<xsl:value-of select="@modifiedMessageId"/>&amp;operation=<xsl:value-of select="@operation"/></xsl:attribute>view_request</xsl:otherwise>
				</xsl:choose>
					
											</a>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@arrivalDateTime"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@serverExpirationDateTime"/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
