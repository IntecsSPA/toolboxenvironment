<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
<head>
<title>CGI XSLT Processor</title>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<style>
a:link{COLOR: #000099; TEXT-DECORATION: none}
a:visited{COLOR: #000099; TEXT-DECORATION: none}
a:active{COLOR: #000099; TEXT-DECORATION: none}
a:hover{COLOR: #000099; TEXT-DECORATION: underline;}
</style>
</head>
<body>
Copyright © 2004-2005 <a href="http://www.dopscripts.com">dopscripts.com</a><h1 align="center">CGI XSLT Processor</h1>
<xsl:apply-templates select="site-map/pages-div/html-page[@file='doc/description']/.."/>
</body>
</html>
</xsl:template>

<xsl:template match="pages-div">
<div align="center">
<xsl:apply-templates mode="content-of-doc"/>
</div>
<br/>
<xsl:apply-templates mode="content-of-page"/>
</xsl:template>

<xsl:template match="html-page" mode="content-of-doc">
<a href="#{substring-after(@file,'doc/')}"><xsl:value-of select="@menu-item"/></a><br/>
</xsl:template>

<xsl:template match="menu.items" mode="content-of-doc">
<h4><xsl:value-of select="@title"/></h4>
<xsl:apply-templates select="node()" mode="content-of-doc"/>
</xsl:template>

<xsl:template match="img-text-block" mode="content-of-page">
<div>
<xsl:apply-templates select="node()" mode="content-of-page"/>
</div>
</xsl:template>

<xsl:template match="menu.items" mode="content-of-page">
<h2 style="color: red;"><xsl:value-of select="@title"/></h2>
<xsl:apply-templates select="node()" mode="content-of-page"/>
</xsl:template>

<xsl:template match="html-page" mode="content-of-page">
<a name="{substring-after(@file,'doc/')}"></a>
<xsl:apply-templates select="node()" mode="content-of-page"/>
</xsl:template>

<xsl:template match="text()" mode="content-of-page">
<xsl:if test=".!=' | '"><p><xsl:value-of select="string()"/></p></xsl:if>
</xsl:template>

<xsl:template match="pre" mode="content-of-page">
<pre style="color: blue;"><xsl:copy-of select="text()"/></pre>
</xsl:template>

<xsl:template match="ref[starts-with(@file,'doc/')]" mode="content-of-page">
<a href="#{substring-after(@file,'doc/')}"><xsl:value-of select="."/></a>
<xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="ref" mode="content-of-page">
<a href="http://www.dopscripts.com/{@file}.html"><xsl:value-of select="."/></a>
<xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="a" mode="content-of-page">
<a class="ref" href="{@href}">
<xsl:if test="@target"><xsl:attribute name="target"><xsl:value-of select="@target"/></xsl:attribute></xsl:if>
<xsl:value-of select="."/></a>
</xsl:template>

<xsl:template match="b[1]" mode="content-of-page">
<h3><xsl:value-of select="."/></h3>
</xsl:template>

<xsl:template match="b" mode="content-of-page">
<b><xsl:value-of select="."/></b>
</xsl:template>

<xsl:template match="i" mode="content-of-page">
<i><xsl:value-of select="."/></i>
</xsl:template>

<xsl:template match="br" mode="content-of-page"/>

<xsl:template match="i" mode="content-of-page">
<i><xsl:apply-templates select="node()" mode="content-of-page"/></i>
</xsl:template>

<xsl:template match="head" mode="content-of-page"/>

</xsl:stylesheet>
