<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: logRequest.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.5 $
 -  Revision Date:     $Date: 2004/06/28 16:00:01 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tss="http://pisa.intecs.it/mass/toolbox/log" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<!--XSL Stylesheet for generating a present response-->
        <xsl:param name="language"/>
        <xsl:param name="operation">
            <xsl:if test="$language = 'it'">Operazione</xsl:if>
            <xsl:if test="$language = 'en'">Operation</xsl:if>
        </xsl:param>                        
        <xsl:param name="data">
            <xsl:if test="$language = 'it'">Data</xsl:if>
            <xsl:if test="$language = 'en'">Date</xsl:if>
        </xsl:param>                        
        <xsl:param name="result">
            <xsl:if test="$language = 'it'">Risultato</xsl:if>
            <xsl:if test="$language = 'en'">Result</xsl:if>
        </xsl:param>                        
	<xsl:template match="tss:log">
							<table width="100%" height="90%" bordercolor="#808080" cellpadding="3" cellspacing="1" border="0" align="center" valign="middle">
								<tr>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=soapPort&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>								
									<xsl:value-of select="$operation"/></a>
									</td>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=date&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>
									<xsl:value-of select="$date"/></a>
									</td>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=result&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>
									<xsl:value-of select="$result"/></a>
									</td>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=messageId&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>

									Message Id</a>
									</td>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=orderId&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>

									Order Id</a>
									</td>
									<td class="logTableHeading">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="$viewLogURL"/>?orderBy=host&amp;logLevel=<xsl:value-of select="$logLevel"/>&amp;serviceName=<xsl:value-of select="$serviceName"/></xsl:attribute>

									Push Host</a>
									</td>
								</tr>
									<xsl:if test="$orderBy='noOrder'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary"/>
									</xsl:if>
									<xsl:if test="$orderBy='soapPort'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="@soapPort"/>
										</xsl:apply-templates>
									</xsl:if>
									<xsl:if test="$orderBy='date'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="../@date"/>
										</xsl:apply-templates>
									</xsl:if>
									<xsl:if test="$orderBy='result'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="@result"/>
										</xsl:apply-templates>
									</xsl:if>
									<xsl:if test="$orderBy='messageId'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="@messageId"/>
										</xsl:apply-templates>
									</xsl:if>
                                                                        <xsl:if test="$orderBy='orderId'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="@orderId"/>
										</xsl:apply-templates>
									</xsl:if>
									<xsl:if test="$orderBy='host'">
										<xsl:apply-templates select="tss:INFO/tss:requestProcessingSummary">
											<xsl:sort select="@host"/>
										</xsl:apply-templates>
									</xsl:if>
							</table>
	</xsl:template>
	<xsl:template match="tss:requestProcessingSummary">
		<tr>
			<td class="logTableInfo">
				<xsl:value-of select="@soapPort"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="../@date"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@result"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@messageId"/>
			</td>
                        <td class="logTableInfo">
				<xsl:value-of select="@orderId"/>
			</td>
			<td class="logTableInfo">
				<xsl:value-of select="@host"/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
