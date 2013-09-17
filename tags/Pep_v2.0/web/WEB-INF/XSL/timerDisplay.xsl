<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs S.P.A.
 -  File Name:         $RCSfile: timerDisplay.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2006/10/02 12:18:44 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:ts="http://pisa.intecs.it/mass/toolbox/timerStatus" xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
        <xsl:param name="language"/>
        <xsl:param name="service"/>
        <xsl:param name="status">
            <xsl:if test="$language = 'it'">Stato</xsl:if>
            <xsl:if test="$language = 'en'">Status</xsl:if>
        </xsl:param>
        <xsl:param name="expiration">
            <xsl:if test="$language = 'it'">Expiration date</xsl:if>
            <xsl:if test="$language = 'en'">Data di scadenza</xsl:if>
        </xsl:param>                             
	<xsl:template match="ts:timerStatus">
		<table width="100%" height="90%" cellpadding="2" cellspacing="2" align="center" valign="middle">
			<tr>
				<td class="sortableHeader" height="50">
					OrderId
				</td>
				<td class="sortableHeader" height="50">
                    <xsl:value-of select="$expiration"/>
				</td>
				<td class="sortableHeader" height="50" valign="middle" align="center">
                    <xsl:value-of select="$status"/>
				</td>
			</tr>
				<xsl:apply-templates select="ts:timer"/>
		</table>
	</xsl:template>
	<xsl:template match="ts:timer">
		<tr>
			<td class="sortable">
				<xsl:value-of select="ts:status/ts:variable[@name='orderId']/@value"/>
                                
			</td>
			<td class="sortable">
				<xsl:value-of select="@expirationDateTime"/>
			</td>
			<td class="sortable">
                        SCRIPT(
              <a>
				<xsl:attribute name="href">javascript:viewResource('xml','expirationDate=<xsl:value-of select="@expirationDateTime"/>&amp;output=XML&amp;serviceName=<xsl:value-of select="$service"/>','Timer script')</xsl:attribute>
 				<img src="images/xml-icon.jpg" alt="arrow"/>
            </a>
              <a>
				<xsl:attribute name="href">javascript:viewResource('tree','expirationDate=<xsl:value-of select="@expirationDateTime"/>&amp;output=TREE&amp;serviceName=<xsl:value-of select="$service"/>','Timer script')</xsl:attribute>
 				<img src="images/tree-icon.jpg" alt="arrow"/>
            </a>
                        )                        
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>

