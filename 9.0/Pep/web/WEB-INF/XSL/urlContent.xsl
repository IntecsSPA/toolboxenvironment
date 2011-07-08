<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: urlContent.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2007/02/05 15:39:41 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tss="http://pisa.intecs.it/mass/toolbox/serviceStatus" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="urlContent">
		<xsl:param name="pnAncestors" select="0"/>
		<xsl:apply-templates select="directory">
			<xsl:with-param name="pnAncestors" select="$pnAncestors+1"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="directory">
		<xsl:param name="pnAncestors" select="0"/>
		<tr>
			<td valign="middle" class="sortableHeader" colspan="2" >
				<img align="middle" border="0" height="27" src="images/e-node.png" alt="[DIRECTORY]">
					<xsl:attribute name="width"><xsl:value-of select="24*$pnAncestors"/></xsl:attribute>
				</img>
				<img align="middle" border="0" width="24" height="27" src="images/folder.png" alt="[DIRECTORY]"/>
				<xsl:value-of select="@name"/>
				<br/>
			</td>
		</tr>
		<xsl:apply-templates select="file">
			<xsl:with-param name="pnAncestors" select="$pnAncestors"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="directory">
			<xsl:with-param name="pnAncestors" select="$pnAncestors+1"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="file">
		<xsl:param name="pnAncestors" select="0"/>
		<tr>
			<td valign="middle" class="sortable" colspan="2" >
				<!--xsl:choose>
					<xsl:when test="position() mod 2 = 0">
						<xsl:attribute name="class">sortable</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class"/>
					</xsl:otherwise>
				</xsl:choose-->
						<xsl:attribute name="class">sortable</xsl:attribute>

				<input type="checkbox" checked="checked">
					<xsl:attribute name="name">fileToImport<xsl:value-of select="@name"/></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
				</input>
				<img align="middle" border="0" height="26" src="images/e-node.png" alt="[DIRECTORY]">
					<xsl:attribute name="width"><xsl:value-of select="24*$pnAncestors"/></xsl:attribute>
				</img>
				<xsl:choose>
					<xsl:when test="count(../file) = position()">
						<img align="middle" border="0" width="24" height="27" src="images/l-node.png" alt="[DIRECTORY]"/>
					</xsl:when>
					<xsl:otherwise>
						<img align="middle" border="0" width="24" height="27" src="images/t-node.png" alt="[DIRECTORY]"/>
					</xsl:otherwise>
				</xsl:choose>
				<img align="middle" border="0" width="24" height="27" src="images/filexsd.png" alt="[FILE]"/>
				<xsl:value-of select="@name"/>
				<br/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
