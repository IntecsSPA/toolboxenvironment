<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs S.P.A.
 -  File Name:         $RCSfile: listOperationsInfo.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.2 $
 -  Revision Date:     $Date: 2006/09/18 12:09:53 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html"  encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
        <xsl:param name="language"/>
        <xsl:param name="operation">
            <xsl:if test="$language = 'it'">Operazione</xsl:if>
            <xsl:if test="$language = 'en'">Operation</xsl:if>
        </xsl:param>
        <xsl:param name="synchronous">
            <xsl:if test="$language = 'it'">Sincrono</xsl:if>
            <xsl:if test="$language = 'en'">Synchronous</xsl:if>
        </xsl:param>
	<xsl:template match="/">
				<xsl:apply-templates select="sd:service/sd:interface/sd:operations"/>	
	</xsl:template>
	<xsl:template match="sd:service/sd:interface/sd:operations">
		<table width="100%"  cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td class="cellsx_t" vAlign="top" height="30"><xsl:value-of select="$operation"/></td>
				<td  class="cellsx_t" vAlign="top" height="30">SOAP Action</td>
				<td  class="cellsx_t" vAlign="top" height="30"><xsl:value-of select="$synchronous"/></td>
			</tr>
				<xsl:apply-templates select="sd:operation"/>
		</table>
	</xsl:template>
	<xsl:template match="sd:operation">
		<tr>
			<td  class="celldx" vAlign="top" height="30">
				<xsl:value-of select="@name"/>
			</td>
			<td class="celldx" vAlign="top" height="30">
				<xsl:value-of select="@SOAPAction"/>
			</td>
			<td class="celldx" vAlign="top" height="30">
		<xsl:if test="@type = 'synchronous'">
			<INPUT disabled= "true" type="checkbox" CHECKED="true" value="on" name="synch"/>
		</xsl:if>
		<xsl:if test="@type = 'asynchronous'">
			<INPUT disabled= "true" type="checkbox"  value="off" name="asynch"/>
		</xsl:if>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>

