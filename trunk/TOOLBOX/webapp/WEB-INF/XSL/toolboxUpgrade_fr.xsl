<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: toolboxUpgrade_fr.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.1 $
 -  Revision Date:     $Date: 2006/09/25 12:44:15 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tbx="http://pisa.intecs.it/mass/toolbox/toolbox" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO" xmlns:h="http://www.mass.com/toolbox/help">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
	<xsl:template match="/">

        <TABLE cellSpacing="0" cellPadding="0" width="100%" align="center" valign="top"> 
          <TBODY> 
            <TR> 
              <TD id="main"> <P class="arbloc">TOOLBOX MISE À NIVEAU</P> 
                <table cellspacing="0" cellpadding="0"  width="100%">
                  <tr>
                    <td class="sortable" valign="top">
<xsl:value-of select="tbx:toolbox/tbx:description/tbx:abstract"></xsl:value-of>
                    </td>
                  <td class="sortable"></td>                                       
                  </tr>
                  <tr>
                    <td valign="top">

Les nouveaux dispositifs de TOOLBOX sont :
<ul>
<xsl:for-each select="tbx:toolbox/tbx:description/tbx:newFeatures/tbx:feature">
	<li><xsl:value-of select="."></xsl:value-of></li>
</xsl:for-each>
</ul>
                    </td> 
                  <td></td>                   
                  </tr>
                    <tr>
                  <td >click 
         		<a><xsl:attribute name="href"><xsl:value-of select="tbx:toolbox/tbx:description/tbx:infoPage"/></xsl:attribute>ICI</a>                  
                   pour montrer l'information additionnelle</td>
                  <td></td>                   
                  </tr>
                  <tr>
              
                  <td>
                       Version de Toolbox de téléchargement nouvelle 
                  </td><td align="right">
                    <input type="button" >
			<xsl:attribute name="onclick">goThere('<xsl:value-of select="tbx:toolbox/tbx:description/tbx:download"/>')</xsl:attribute> 
			<xsl:attribute name="value">Version <xsl:value-of select="tbx:toolbox/@version"/></xsl:attribute>                    
                    </input>
                  </td>
                  </tr>
                  <tr>
                    <td>
                    <input name="checkVersion" type="checkbox"/> Neutraliser le contrôle pour la nouvelle version de TOOLBOX au temps d'ouverture 
                    </td>
                  <td align="right"><input type="submit" value="Saut"/></td> 
                  </tr>                  
                </table></TD> 
            </TR> 
          </TBODY> 
        </TABLE>

</xsl:template>
</xsl:stylesheet>
