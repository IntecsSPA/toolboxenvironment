<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/1998/Math/MathML">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:for-each select="apply">
			<xsl:call-template name="apply"/>
		</xsl:for-each>
		<xsl:for-each select="reln">
			<xsl:call-template name="apply"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="apply" name="apply">
		<xsl:param name="operator" />
		<xsl:choose>
			<xsl:when test="name(child::*[1]) = 'plus'">
				<xsl:call-template name="expressionTemplate">
					<xsl:with-param name="operator">+</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="name(child::*[1]) = 'minus'">
				<xsl:call-template name="expressionTemplate">
					<xsl:with-param name="operator">-</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="name(child::*[1]) = 'times'">
				<xsl:call-template name="expressionTemplate">
					<xsl:with-param name="operator">*</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="name(child::*[1]) = 'divide'">
				<xsl:call-template name="expressionTemplate">
					<xsl:with-param name="operator">/</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<xsl:value-of select="$operator" />
	</xsl:template>
	<xsl:template name="expressionTemplate">
		<xsl:param name="operator" />
			<xsl:variable name="expression">
				<xsl:for-each select="child::*">
					<xsl:if test="(name(.) = 'ci') or  (name(.) = 'cn') or (name(.) = 'apply')">
						<xsl:apply-templates select=".">
							<xsl:with-param name="operator" select="$operator" /> 
						</xsl:apply-templates>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<!-- chop last operator -->
			<xsl:text>(</xsl:text>
			<xsl:value-of select="substring($expression,1,string-length($expression)-1)"/>
			<xsl:text>)</xsl:text>
	</xsl:template>
	<xsl:template match="ci">
		<xsl:param name="operator" />
		<xsl:value-of select="."/>
		<xsl:value-of select="$operator" />
	</xsl:template>
	<xsl:template match="cn">
		<xsl:param name="operator" />
		<xsl:value-of select="."/>
		<xsl:value-of select="$operator" />
	</xsl:template>
</xsl:stylesheet>
