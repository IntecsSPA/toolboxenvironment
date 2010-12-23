<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs S.P.A.
 -  File Name:         $RCSfile: scriptDisplay.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2006/09/29 13:01:14 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:ts="http://pisa.intecs.it/mass/toolbox/timerStatus" xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:param name="orderId"></xsl:param>
	<xsl:param name="expirationDateTime"></xsl:param>
	<xsl:param name="language"/>
	<xsl:template match="ts:timerStatus">
	    <xsl:apply-templates select="ts:timer[@expirationDateTime=$expirationDateTime]"/>
	</xsl:template>
	<xsl:template match="ts:timer">
            <xsl:copy-of select="ts:script"/>
	</xsl:template>
</xsl:stylesheet>
