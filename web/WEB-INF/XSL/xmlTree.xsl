<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"></xsl:output>
<xsl:template match="/">    
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
<xsl:variable name="ID"><xsl:value-of select="generate-id()"/></xsl:variable>
<span class="trigger">
<xsl:attribute name="onClick">
showBranch('<xsl:value-of select="$ID"/>');
</xsl:attribute>
<img src="images/closed.gif">
<xsl:attribute name="id">I<xsl:value-of select="$ID"/></xsl:attribute>
</img>
<span class="Stile2">&lt;</span><span class="Stile3"><xsl:value-of select="name()"/></span><xsl:for-each select="@*"><span class="Stile4"><xsl:value-of select="string(' ')"/><xsl:value-of select="name()"/></span><span class="Stile5">=&quot;</span><xsl:value-of select="."/><span class="Stile2">&quot;</span></xsl:for-each><span class="Stile2">&gt;</span>
<br/>
</span>
<span class="branch">
<xsl:attribute name="id">
<xsl:value-of select="$ID"/>
</xsl:attribute>
<xsl:apply-templates/>
</span>

</xsl:template>

<xsl:template match="leaf">
<img src="images/doc.gif"/>
<xsl:value-of select="name()"/>
<br/>
</xsl:template>

</xsl:stylesheet>
