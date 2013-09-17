<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:stt="http://pisa.intecs.it/mass/toolbox/serviceStatus">
    <xsl:variable name="startIndex">0</xsl:variable>
    <xsl:variable name="pageSize">20</xsl:variable>
    <xsl:template match="/">
        <stt:serviceStatus>
            <xsl:attribute name="pagesNumber"><xsl:value-of select="floor(count(stt:serviceStatus/stt:request) div number($pageSize)) +1"/></xsl:attribute>
            <xsl:for-each select="stt:serviceStatus/stt:request">
                <xsl:if test="position() &gt;= $startIndex">
                    <xsl:if test="(position() - $startIndex) &lt; $pageSize">
                        <stt:request>
                            <xsl:attribute name="index"><xsl:value-of select="position()"/></xsl:attribute>
                            <xsl:attribute name="instanceKey">                                
                                <xsl:if test="@hostName != '' ">
                                    <xsl:value-of select="concat(@operation,'.',@hostName,'.',@modifiedMessageId )"/>
                                </xsl:if>
                                <xsl:if test="@hostName = '' ">
                                    <xsl:value-of select="concat(@operation,'.',@modifiedMessageId )"/>
                                </xsl:if>
                            </xsl:attribute>                                                        
                            <xsl:attribute name="operation"><xsl:value-of select="@operation"/></xsl:attribute>
                            <xsl:attribute name="messageId"><xsl:value-of select="@messageId"/></xsl:attribute>
                            <xsl:attribute name="modifiedMessageId"><xsl:value-of select="@modifiedMessageId"/></xsl:attribute>
                            <xsl:attribute name="pushURL"><xsl:value-of select="@pushURL"/></xsl:attribute>
                            <xsl:attribute name="hostName"><xsl:value-of select="@hostName"/></xsl:attribute>
                            <xsl:attribute name="hostIP"><xsl:value-of select="@hostIP"/></xsl:attribute>
                            <xsl:attribute name="orderId"><xsl:value-of select="@orderId"/></xsl:attribute>
                            <xsl:attribute name="arrivalDateTime"><xsl:value-of select="@arrivalDateTime"/></xsl:attribute>
                            <xsl:attribute name="serverExpirationDateTime"><xsl:value-of select="@serverExpirationDateTime"/></xsl:attribute>
                            <xsl:attribute name="clientExpirationDateTime"><xsl:value-of select="@clientExpirationDateTime"/></xsl:attribute>
                            <xsl:attribute name="xmlRequest"><xsl:value-of select="@xmlRequest"/></xsl:attribute>
                            <xsl:attribute name="status"><xsl:value-of select="@status"/></xsl:attribute>
                            <xsl:attribute name="reasonForStatus"><xsl:value-of select="@reasonForStatus"/></xsl:attribute>
                            <xsl:attribute name="statusPath"><xsl:value-of select="@statusPath"/></xsl:attribute>
                            <xsl:attribute name="errorNotificationEmail"><xsl:value-of select="@errorNotificationEmail"/></xsl:attribute>
                            <xsl:attribute name="remainingAttempts"><xsl:value-of select="stt:leavingResponse/@remainingAttempts"/></xsl:attribute>
                        </stt:request>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
        </stt:serviceStatus> 
    </xsl:template>
    
</xsl:stylesheet>
