<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:txsl="http://www.w3.org/1999/XSL/Transform/target" xmlns:ctl="http://www.occamlab.com/ctl" version="1.0" >
<xsl:output method="xml" version="1.0" encoding="ISO-8859-1"/>
     <xsl:variable name="imageNode0">dhtmlxtree_icon.gif</xsl:variable>
     <xsl:variable name="imageNode1">dhtmlxtree_icon.gif</xsl:variable>
     <xsl:variable name="imageNode2">dhtmlxtree_icon.gif</xsl:variable>
     
     <!--xsl:variable name="imageAttribute0">folderClosed.gif</xsl:variable>
     <xsl:variable name="imageAttribute1">folderOpen.gif</xsl:variable>
     <xsl:variable name="imageAttribute2">folderClosed.gif</xsl:variable>
     
     <xsl:variable name="imageNameSapce0">folderClosed.gif</xsl:variable>
     <xsl:variable name="imageNameSapce1">folderOpen.gif</xsl:variable>
     <xsl:variable name="imageNameSapce2">folderClosed.gif</xsl:variable>
     
     <xsl:variable name="imageSchema0">tombs.gif</xsl:variable>
     <xsl:variable name="imageSchema1">tombs.gif</xsl:variable>
     <xsl:variable name="imageSchema2">iconSafe.gif</xsl:variable-->
     
	<xsl:template match="/toc">
            <tree id="0">
            
            <xsl:for-each select="topic">
                    <xsl:call-template name="parseNode">
                        <xsl:with-param name="node" select="."/>
                    </xsl:call-template>
            </xsl:for-each>
            </tree>
	</xsl:template>

        <xsl:template name="parseNode">
            <xsl:param name="node"></xsl:param>
            <item text="{$node/@label}" id="{$node/@href}_{generate-id()}" im0="{$imageNode0}" im1="{$imageNode1}" im2="{$imageNode2}">
              <xsl:if test="$node/@href">
              <userdata name="thisurl"><xsl:value-of select="$node/@href"></xsl:value-of></userdata>
            </xsl:if>  
                <!--xsl:for-each select="$node/namespace::*">
                  <item text="xmlns:{name()} = {.}" id="{name()}_{.}" im0="{$imageNameSapce0}" im1="{$imageNameSapce1}" im2="{$imageNameSapce2}"/> 
                </xsl:for-each>
                <xsl:for-each select="$node/@*">
                        <item text="{name()} = {.}" id="{name()}_{.}" im0="{$imageAttribute0}" im1="{$imageAttribute1}" im2="{$imageAttribute2}"></item>
                </xsl:for-each-->
                <xsl:for-each select="$node/*">
                    <xsl:call-template name="parseNode">
                        <xsl:with-param name="node" select="."/>
                    </xsl:call-template>
               </xsl:for-each>     
            </item>
        </xsl:template>
      
</xsl:transform>

