<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: helpPage.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.9 $
 -  Revision Date:     $Date: 2005/07/25 10:34:17 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO" xmlns:h="http://www.mass.com/toolbox/help">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:param name="topic"></xsl:param>
	<xsl:template match="h:help">
		<xsl:value-of select="h:key[@value=$topic]/h:sumLink"/>
	</xsl:template>
</xsl:stylesheet>
