<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: processPresent.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2004/06/25 11:56:15 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns="http://www.esa.int/mass" xmlns:eoli="http://earth.esa.int/XML/eoli">
	<xsl:param name="presentation"/>
	<xsl:param name="productId"/>
	<xsl:param name="collectionId"/>
	<xsl:param name="mass"/>
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$mass != ''">
				<processPresentInputMsg>
					<xsl:call-template name="eoli"/>
				</processPresentInputMsg>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="eoli"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="eoli">
		<eoli:presentRequest>
			<eoli:idCitation>
				<eoli:resTitle><xsl:value-of select="$productId"/></eoli:resTitle>
			</eoli:idCitation>
			<eoli:presentation><xsl:value-of select="$presentation"/></eoli:presentation>
			<eoli:collectionId><xsl:value-of select="$collectionId"/></eoli:collectionId>
		</eoli:presentRequest>
	</xsl:template>
</xsl:stylesheet>
