<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">

    <xsl:param name="serviceName"/>
    <xsl:param name="instanceType"/>
    <xsl:param name="instanceId"/>

    <xsl:template match="/">
		<HTML>
			<HEAD>
				<STYLE>
        a {font-size: x-small;color: #902124;text-decoration: none;border-bottom: 1px dashed;}                            
        BODY {font:x-small 'Verdana'; margin-right:1.5em;font-size: x-small}
      <!-- container for expanding/collapsing content -->
        .c  {cursor:hand}
      <!-- button - contains +/-/nbsp -->
        .b  {color:red; font-family:'Courier New'; font-weight:bold;
text-decoration:none;font-size: x-small}
      <!-- element container -->
        .e  {margin-left:1em; text-indent:-1em; margin-right:1em}
      <!-- comment or cdata -->
        .k  {margin-left:1em; text-indent:-1em; margin-right:1em}
      <!-- tag -->
        .t  {color:#990000;font-size: x-small}
      <!-- tag in xsl namespace -->
        .xt {color:#990099;font-size: x-small}
      <!-- attribute in xml or xmlns namespace -->
        .ns {color:red;font-size: x-small}
      <!-- attribute in dt namespace -->
        .dt {color:green;font-size: x-small}
      <!-- markup characters -->
        .m  {color:blue;font-size: x-small}
      <!-- text node -->
        .tx {font-weight:bold; font-size: x-small}
      <!-- text node -->
        .att { font-size: x-small}
      <!-- multi-line (block) cdata -->
        .db {text-indent:0px; margin-left:1em; margin-top:0px;
margin-bottom:0px;
             padding-left:.3em; border-left:1px solid #CCCCCC; font:small
Courier}
      <!-- single-line (inline) cdata -->
        .di {font:small Courier}
      <!-- DOCTYPE declaration -->
        .d  {color:blue;font-size: x-small}
      <!-- pi -->
        .pi {color:blue:font-size: x-small}
      <!-- multi-line (block) comment -->
        .cb {text-indent:0px; margin-left:1em; margin-top:0px;
margin-bottom:0px;
             padding-left:.3em; font:small Courier; color:#888888; font-size: x-small}
      <!-- single-line (inline) comment -->
        .ci {font:small Courier; color:#888888;font-size: x-small}
        PRE {margin:0px; display:inline}
        
      </STYLE>
			</HEAD>
			<BODY class="st">
				<xsl:apply-templates/>
			</BODY>
		</HTML>
	</xsl:template>
	<!-- Template for attributes not handled elsewhere -->
	<xsl:template match="@*" xml:space="preserve">
		<SPAN class="ns">
			<!--xsl:attribute name="class"><xsl:if test="xsl:*/@*">x</xsl:if>t</xsl:attribute-->
			<xsl:value-of select="name()"/>
		</SPAN>
		<SPAN class="m">="</SPAN>
		<SPAN class="att">
			<xsl:choose>
				<xsl:when test="name()='resourceKey'">
                               <a class="a" href="#" ONCLICK="javascript:parent.openTab('xml','Tab', 'getResource.jsp?serviceName={$serviceName}&amp;instanceType={$instanceType}&amp;instanceId={$instanceId}&amp;resourceKey={.}','XML '+'{.}')">
					<xsl:value-of select="."/>
			       </a>
				</xsl:when>
				<xsl:when test="name()='schemaLocation'">
                               <a class="a" href="#" ONCLICK="javascript:parent.openTab('xml','Tab', 'manager?cmd=getXMLRemoteResource&amp;location={.}&amp;output=text','XML '+'{.}')">
					<xsl:value-of select="."/>
			       </a>
				</xsl:when>

                                <xsl:when test="name()='resourceLink'">
                               <a class="a" href="#" ONCLICK="javascript:parent.openTab('xml','Tab', 'getResource.jsp?serviceName={$serviceName}&amp;instanceType={$instanceType}&amp;instanceId={$instanceId}&amp;resourceKey={.}','XML '+'{.}')">
					Click here to display resource
			       </a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</SPAN>
		<SPAN class="m">"</SPAN>
	</xsl:template>
	<!-- Template for attributes in the xmlns or xml namespace -->
	<!--xsl:template match="@xmlns:*">
		<SPAN class="ns">
			<xsl:value-of select="name()"/>
		</SPAN>
		<SPAN class="m">="</SPAN>
		<SPAN class="att">
			<xsl:value-of select="."/>
		</SPAN>
		<SPAN class="m">"</SPAN>
	</xsl:template-->
	<!-- Template for text nodes -->
	<xsl:template match="text()">
		<DIV class="e">
			<SPAN class="b">
				<xsl:text> </xsl:text>
			</SPAN>
			<SPAN class="tx">
				<xsl:value-of select="."/>
			</SPAN>
		</DIV>
	</xsl:template>
	<!-- Template for comment nodes -->
	<xsl:template match="comment()">
		<DIV class="k">
			<SPAN>
				<SPAN class="m">&lt;!--</SPAN>
			</SPAN>
			<SPAN id="clean" class="ci">
				<PRE>
					<xsl:value-of select="."/>
				</PRE>
			</SPAN>
			<SPAN class="b">
				<xsl:text> </xsl:text>
			</SPAN>
			<SPAN class="m">--&gt;</SPAN>
		</DIV>
	</xsl:template>
	<!-- Template for cdata nodes
<xsl:template match="cdata()">
  <DIV class="k">
  <SPAN> <SPAN class="m">&lt;![CDATA[</SPAN></SPAN>
  <SPAN id="clean" class="di"><PRE><xsl:value-of select="."/></PRE></SPAN>
  <SPAN class="b"><xsl:text> </xsl:text></SPAN> <SPAN
class="m">]]&gt;</SPAN>
  </DIV>
</xsl:template>
-->
	<!-- Template for elements not handled elsewhere (leaf nodes) -->
	<xsl:template match="*">
		<DIV class="e">
			<DIV STYLE="margin-left:1em;text-indent:-2em">
				<SPAN class="b">
					<xsl:text> </xsl:text>
				</SPAN>
				<SPAN class="m">&lt;</SPAN>
				<SPAN>
					<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
					<xsl:value-of select="name()"/>
				</SPAN>
				<xsl:apply-templates select="@*"/>
				<SPAN class="m"> /&gt;</SPAN>
			</DIV>
		</DIV>
	</xsl:template>
	<!-- Template for elements with comment, pi and/or cdata children -->
	<xsl:template match="*[node()]">
		<DIV class="e">
			<DIV class="c">
				<SPAN class="m">&lt;</SPAN>
				<SPAN>
					<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
					<xsl:value-of select="name()"/>
				</SPAN>
				<xsl:apply-templates select="@*"/>
				<SPAN class="m">&gt;</SPAN>
			</DIV>
			<DIV>
				<xsl:apply-templates/>
				<DIV>
					<SPAN class="b">
						<xsl:text> </xsl:text>
					</SPAN>
					<SPAN class="m">&lt;/</SPAN>
					<SPAN>
						<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
						<xsl:value-of select="name()"/>
					</SPAN>
					<SPAN class="m">&gt;</SPAN>
				</DIV>
			</DIV>
		</DIV>
	</xsl:template>
	<!-- Template for elements with only text children -->
	<xsl:template match="*[text() and not (comment())]">
		<DIV class="e">
			<DIV STYLE="margin-left:1em;text-indent:-2em">
				<SPAN class="b">
					<xsl:text> </xsl:text>
				</SPAN>
				<SPAN class="m">&lt;</SPAN>
				<SPAN>
					<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
					<xsl:value-of select="name()"/>
				</SPAN>
				<xsl:apply-templates select="@*"/>
				<SPAN class="m">&gt;</SPAN>
				<SPAN class="tx">
					<xsl:value-of select="."/>
				</SPAN>
				<SPAN class="m">&lt;/</SPAN>
				<SPAN>
					<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
					<xsl:value-of select="name()"/>
				</SPAN>
				<SPAN class="m">&gt;</SPAN>
			</DIV>
		</DIV>
	</xsl:template>
	<!-- Template for elements with element children -->
	<xsl:template match="*[*]">
		<DIV class="e">
			<DIV class="c" STYLE="margin-left:1em;text-indent:-2em">
				<SPAN class="m">&lt;</SPAN>
				<SPAN>
					<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
					<xsl:value-of select="name()"/>
				</SPAN>
				<xsl:apply-templates select="@*"/>
				<SPAN class="m">&gt;</SPAN>
			</DIV>
			<DIV>
				<xsl:apply-templates/>
				<DIV>
					<SPAN class="b">
						<xsl:text> </xsl:text>
					</SPAN>
					<SPAN class="m">&lt;/</SPAN>
					<SPAN>
						<xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute>
						<xsl:value-of select="name()"/>
					</SPAN>
					<SPAN class="m">&gt;</SPAN>
				</DIV>
			</DIV>
		</DIV>
	</xsl:template>
</xsl:stylesheet>
