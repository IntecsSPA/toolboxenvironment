<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
        <xsl:param name="language"/>
        <xsl:param name="next">
            <xsl:if test="$language = 'it'">Avanti</xsl:if>
            <xsl:if test="$language = 'en'">Next</xsl:if>
        </xsl:param>
 
	<xsl:template match="/">
		<!-- Page contents table-->
		<table width="50%" cellspacing="2" cellpadding="2" align="center">
			<xsl:apply-templates select="catalogues/catalogue"/>
			<tr>
				<td class="sortable" colspan="4" rowspan="2" nowrap="true" align="right">
					<div align="right">
						<input type="submit" >
                                			<xsl:attribute name="value"><xsl:value-of select='$next'/></xsl:attribute>			                                                    
						</input>
					</div>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="catalogues/catalogue">
			<tr>
		<td class="sortable"  nowrap="true" align="right">
			<input type="radio" name='icd'>
			<xsl:attribute name="value"><xsl:value-of select="interfaceTransformer/@fileName"/></xsl:attribute>
			<xsl:if test='position()=1'>
			<xsl:attribute name="checked">true</xsl:attribute>			
			</xsl:if>
			</input>			
			<xsl:value-of select="@name"/><xsl:value-of select="@version"/>
		</td>
		<td class="sortable"  nowrap="true" align="right">
			<xsl:value-of select="description"/>
		</td>
			</tr>

	</xsl:template>
</xsl:stylesheet>
