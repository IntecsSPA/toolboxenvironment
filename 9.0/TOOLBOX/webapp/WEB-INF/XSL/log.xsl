<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: log.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.3 $
 -  Revision Date:     $Date: 2004/04/15 12:16:49 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tss="http://pisa.intecs.it/mass/toolbox/log" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO">
	<xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="tss:log">
		<xsl:variable name="newOrder">
			<xsl:choose>
				<xsl:when test="$order='ascending'">descending</xsl:when>
				<xsl:otherwise>ascending</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<table width="100%" height="90%" bordercolor="#808080" cellpadding="3" cellspacing="1" border="0" align="center" valign="middle">
			<tr>
				<td class="sortable" width="70">Level</td>
				<td class="sortable" width="*">
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$page"/>&amp;order=<xsl:value-of select="$newOrder"/></xsl:attribute>
                                                Date
					</a>
					<xsl:choose>
						<xsl:when test="$order='ascending'">
							<img src="images/down.gif" border="0"/>
						</xsl:when>
						<xsl:otherwise>
							<img src="images/up.gif" border="0"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="sortable" width="*">Log sentence</td>
			</tr>
			<xsl:choose>
				<xsl:when test="$order='ascending'">
					<xsl:apply-templates select="*">
						<xsl:sort select="@date" order="ascending"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="*">
						<xsl:sort select="@date" order="descending"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>
	<xsl:template match="tss:INFO">
		<xsl:if test="$logLevel>3">
			<xsl:if test=". != ''">
				<tr>
					<td class="logTableInfo">INFO</td>
					<td class="logTableInfo">
						<xsl:value-of select="@date"/>
					</td>
					<td class="logTableInfo">
						<xsl:value-of select="."/>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tss:DEBUG">
		<xsl:if test="$logLevel>4">
			<tr>
				<td class="logTableDebug">DEBUG</td>
				<td class="logTableDebug">
					<xsl:value-of select="@date"/>
				</td>
				<td class="logTableDebug">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tss:WARN">
		<xsl:if test="$logLevel>2">
			<tr>
				<td class="logTableWarn">WARN</td>
				<td class="logTableWarn">
					<xsl:value-of select="@date"/>
				</td>
				<td class="logTableWarn">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tss:ERROR">
		<xsl:if test="$logLevel>1">
			<tr>
				<td class="logTableError">ERROR</td>
				<td class="logTableError">
					<xsl:value-of select="@date"/>
				</td>
				<td class="logTableError">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<xsl:template match="tss:FATAL">
		<xsl:if test="$logLevel>0">
			<tr>
				<td class="logTableError">FATAL</td>
				<td class="logTableError">
					<xsl:value-of select="@date"/>
				</td>
				<td class="logTableError">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
