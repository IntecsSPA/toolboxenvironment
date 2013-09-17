<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : asynchLogToRSS.xsl
    Created on : 19 settembre 2008, 11.17
    Author     : Massimiliano
    Description:
        This transformation will create an RSS feed from the log file
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:log="http://pisa.intecs.it/mass/toolbox/log">
        <xsl:param name="currentDate">date</xsl:param>
         <xsl:param name="timeZone"></xsl:param>
   
    <xsl:template match="/log:logs">
        
        <xsl:variable name="serviceName"><xsl:value-of select="@serviceName"/></xsl:variable>
        <xsl:variable name="link">http://toolbox.pisa.intecs.it/</xsl:variable>
    
        <rss version ="2.0">
            <channel>
                <title>Service <xsl:value-of select="$serviceName"/> feed</title>
                <link><xsl:value-of select="$link"/></link>
                <description>RSS feed</description>
                <xsl:for-each select="log:log">
                    <xsl:if test="@level!='DEBUG'">
                    <item>
                        <title><xsl:value-of select="text()"/></title>
                        <description>Date: <xsl:value-of select="@date"/>
                        Event: <xsl:value-of select="text()"/>
                        Level: <xsl:value-of select="@level"/></description>
                        <pubDate><xsl:apply-templates select="@date"/></pubDate>
                        <guid><xsl:value-of select="@date"/><xsl:value-of select="text()"/></guid>
                      </item>
                    </xsl:if>
                </xsl:for-each>
            </channel>
        </rss>
    </xsl:template>

    <xsl:template match="@date">
           <xsl:value-of select="substring-after(substring-after(substring-before(.,' '),'-'),'-')"/> <xsl:text> </xsl:text>
            <xsl:choose>
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='gen'">Jan</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='jan'">Jan</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='feb'">Feb</xsl:when>
                    
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='mar'">Mar</xsl:when>
        
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='apr'">Apr</xsl:when>
                 
                  <xsl:when test="substring-before(substring-after(.,'-'),'-')='mag'">May</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='may'">May</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='giu'">Jun</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='jun'">Jun</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='lug'">Jul</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='jul'">Jul</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='ago'">Aug</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='aug'">Aug</xsl:when>
                     
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='set'">Sep</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='sep'">Sep</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='ott'">Oct</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='oct'">Oct</xsl:when>
                     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='nov'">Nov</xsl:when>
                    
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='dic'">Dec</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='dec'">Dec</xsl:when>
                 
                <!-- <xsl:when test="substring-before(substring-after(.,'-'),'-')='01'">Jan</xsl:when>
                 <xsl:when test="substring-before(substring-after(.,'-'),'-')='02'">Feb</xsl:when>
                 <xsl:when test="substring-before(substring-after(.,'-'),'-')='03'">Mar</xsl:when>
                <xsl:when test="substring-before(substring-after(.,'-'),'-')='04'">Apr</xsl:when>
                 <xsl:when test="substring-before(substring-after(.,'-'),'-')='05'">May</xsl:when>
                 <xsl:when test="substring-before(substring-after(.,'-'),'-')='06'">Jun</xsl:when>
                   <xsl:when test="substring-before(substring-after(.,'-'),'-')='07'">Jul</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='08'">Aug</xsl:when>
                   <xsl:when test="substring-before(substring-after(.,'-'),'-')='09'">Sep</xsl:when>
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='10'">Oct</xsl:when>     
                     <xsl:when test="substring-before(substring-after(.,'-'),'-')='11'">Nov</xsl:when>     
                    <xsl:when test="substring-before(substring-after(.,'-'),'-')='12'">Dec</xsl:when>-->
          </xsl:choose>
                <xsl:text> </xsl:text>
                <xsl:value-of select="substring-before(.,'-')"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="substring-after(., ' ')"/><xsl:text> </xsl:text><xsl:value-of select="$timeZone"/>
        </xsl:template>
</xsl:stylesheet>
