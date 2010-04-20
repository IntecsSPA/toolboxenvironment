<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: toolboxUpgrade.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2006/09/19 10:57:17 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tbx="http://pisa.intecs.it/mass/toolbox/toolbox" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO" xmlns:h="http://www.mass.com/toolbox/help">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="/">
        <TABLE cellSpacing="2" cellPadding="2" width="100%" align="center" valign="top"> 
          <TBODY> 
            <TR> 
              <TD id="main"> <P class="arbloc">TOOLBOX UPGRADE</P> 
                <table cellspacing="2" cellpadding="2"  width="100%">
                  <tr>
                    <td valign="top">
<xsl:value-of select="tbx:toolbox/tbx:description/tbx:abstract"></xsl:value-of>
                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
<div class="content">
The new TOOLBOX features are:
<ul>
<xsl:for-each select="tbx:toolbox/tbx:description/tbx:newFeatures/tbx:feature">
	<li><xsl:value-of select="."></xsl:value-of></li>
</xsl:for-each>
</ul>
                    </div></td> 
                  </tr>
                    <tr>
                  <td ><div class="content">
click 
         		<a><xsl:attribute name="href"><xsl:value-of select="tbx:toolbox/tbx:description/tbx:infoPage"/></xsl:attribute>HERE</a>                  
                   to display additional information</div></td>
<br></br>
                  </tr>
<div class="content">
                  <tr>
              
                  <td class="sortableHeader">
                        Download Toolbox new version 
                  </td><td class="sortableHeader" align="right">
                    <input type="button" >
			<xsl:attribute name="onclick">goThere('<xsl:value-of select="tbx:toolbox/tbx:description/tbx:download"/>')</xsl:attribute> 
			<xsl:attribute name="value">Version <xsl:value-of select="tbx:toolbox/@version"/></xsl:attribute>                    
                    </input>
                  </td>
                  </tr>
                  <tr>
                    <td class="sortableHeader">
                    <input name="checkVersion" type="checkbox"/> Disable Check for new TOOLBOX version at login time
                    </td>
                  <td class="sortableHeader" align="right"><input type="submit" value="Skip"/></td> 
                  </tr>                  
</div>
                </table></TD> 
            </TR> 
          </TBODY> 
        </TABLE>
</xsl:template>
</xsl:stylesheet>
