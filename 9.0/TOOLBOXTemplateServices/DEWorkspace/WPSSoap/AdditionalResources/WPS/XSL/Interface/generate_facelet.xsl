<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core" xmlns:rich="http://richfaces.org/rich" xmlns:c="http://java.sun.com/jstl/core" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:a4j="http://richfaces.org/a4j" xmlns:spb="http://www.spacebel.be/genesis/jsf" xmlns:ui="http://java.sun.com/jsf/facelets">
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<html>
			<ui:composition>
				<h:panelGrid columns="2" columnClasses="searchInputForm-label,searchInputForm-value">
					<xsl:apply-templates select="//ProcessDescription/DataInputs/Input"/>
					<!-- only create input field for output containing multiple format options -->
					<xsl:apply-templates select="//ProcessDescription/ProcessOutputs/Output[count(ComplexOutput/Supported/Format/MimeType) &gt; 1]"/>
				</h:panelGrid>
				<xsl:text>(*): required field</xsl:text>
			</ui:composition>
		</html>
	</xsl:template>
	<xsl:template match="Input">
		<xsl:param name="required">
			<xsl:if test="@minOccurs &lt;= 0">
				<xsl:text>false</xsl:text>
			</xsl:if>
			<xsl:if test="not(@minOccurs &lt;= 0)">
				<xsl:text>true</xsl:text>
			</xsl:if>
		</xsl:param>
		<xsl:param name="paramLabel">
			<xsl:value-of select="ows:Title"/>
			<xsl:if test="$required = 'true'">
				<xsl:text> (*)</xsl:text>
			</xsl:if>
		</xsl:param>
		<xsl:param name="paramName">
			<xsl:text>#{form['</xsl:text>
			<xsl:value-of select="ows:Identifier"/>
			<xsl:text>']}</xsl:text>
		</xsl:param>
		<!-- don't create an input field for a boundingbox (boudingbox is selected using the viewer) -->
		<xsl:if test="count(BoundingBoxData) = 0">
			<h:outputText value="{$paramLabel}"/>
			<!-- if there is no list with allowed values given, just show an input box -->
			<xsl:if test="count(LiteralData/ows:AllowedValues/ows:Value)  = 0">
				<h:inputText value="{$paramName}" required="{$required}"/>
			</xsl:if>
			<!-- if there is a list with allowed values given, show a select box or a number of check boxes -->
			<xsl:if test="count(LiteralData/ows:AllowedValues/ows:Value) &gt; 0">
				<!-- if only one value is allowed, show a select box -->
				<xsl:if test="not(@maxOccurs &gt; 1)">
					<h:selectOneMenu value="{$paramName}">
						<xsl:apply-templates select="LiteralData/ows:AllowedValues/ows:Value">
							<xsl:with-param name="type">listItem</xsl:with-param>
						</xsl:apply-templates>
					</h:selectOneMenu>
				</xsl:if>
				<!-- if multiple values are allowed, show a checkbox for each allowed value -->
				<xsl:if test="@maxOccurs &gt; 1">
					<!-- first, set empty outputText to fill the column next to the label -->
					<h:outputText value=""/>
					<xsl:apply-templates select="LiteralData/ows:AllowedValues/ows:Value">
						<xsl:with-param name="type">checkBox</xsl:with-param>
						<xsl:with-param name="paramName" select="ows:Identifier" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template match="Output">
		<xsl:param name="paramLabel">
			<xsl:text>Format </xsl:text>
			<xsl:value-of select="ows:Title"/>
		</xsl:param>
		<!-- parameter name = ows:Identifier + 'Format' -->
		<xsl:param name="paramName">
			<xsl:text>#{form['</xsl:text>
			<xsl:value-of select="ows:Identifier"/>
			<xsl:text>Format</xsl:text>
			<xsl:text>']}</xsl:text>
		</xsl:param>
		<h:outputText value="{$paramLabel}"/>
		<h:selectOneMenu value="{$paramName}">
			<xsl:for-each select="ComplexOutput/Supported/Format/MimeType">
				<f:selectItem itemLabel="{.}" itemValue="{.}"/>
			</xsl:for-each>
		</h:selectOneMenu>
	</xsl:template>
	<xsl:template match="ows:Value">
		<xsl:param name="type"/>
		<xsl:param name="paramName"/>
		<xsl:param name="valueLabel" select="."/>
		<xsl:param name="valueValue" select="."/>
		<!-- check box value refers to form and consists of the parameter name + underscore + parameter value -->
		<xsl:param name="checkBoxValue">
			<xsl:text>#{form['</xsl:text>
			<xsl:value-of select="$paramName"/>
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$valueValue"/>
			<xsl:text>']}</xsl:text>
		</xsl:param>
		<xsl:if test="$type = 'listItem'">
			<f:selectItem itemLabel="{$valueLabel}" itemValue="{$valueValue}"/>
		</xsl:if>
		<xsl:if test="$type = 'checkBox'">
			<h:outputText value="{$valueLabel}"/>
			<h:selectBooleanCheckbox value="{$checkBoxValue}"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
