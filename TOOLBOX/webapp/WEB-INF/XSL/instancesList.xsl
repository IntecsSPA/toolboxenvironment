<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: asynchronousInstances.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.19 $
 -  Revision Date:     $Date: 2007/02/08 08:24:02 $
 -
 -->
<xsl:stylesheet version="1.0" xmlns:tss="http://pisa.intecs.it/mass/toolbox/serviceStatus" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO">
    <xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>

    <xsl:param name="type">S</xsl:param>
    <xsl:param name="instanceIdOrderDirection">ASC</xsl:param>
    <xsl:param name="orderIdOrderDirection">ASC</xsl:param>
    <xsl:param name="arrivalDateOrderDirection">ASC</xsl:param>
    <xsl:param name="statusOrderDirection">ASC</xsl:param>
    <xsl:param name="expirationDateOrderDirection">ASC</xsl:param>
    <xsl:param name="pushHostOrderDirection">ASC</xsl:param>
    <xsl:param name="serviceName"/>
    <xsl:param name="page"/>
    <xsl:param name="language">en</xsl:param>

     <xsl:param name="viewInstancePage">
         <xsl:if test="$type = 'S'">getSynchronousInstanceFlow.jsp</xsl:if>
            <xsl:if test="$type = 'A'">getAsynchronousInstanceFlow.jsp</xsl:if>
     </xsl:param>

    <xsl:param name="toolsTxt">
        <xsl:if test="$language = 'it'">TOOLS</xsl:if>
        <xsl:if test="$language = 'en'">TOOLS</xsl:if>
    </xsl:param>
    <xsl:param name="instanceTxt">
        <xsl:if test="$language = 'it'">instanza</xsl:if>
        <xsl:if test="$language = 'en'">instance</xsl:if>
    </xsl:param>
    <xsl:param name="actTxt">
        <xsl:if test="$language = 'it'">Operazione</xsl:if>
        <xsl:if test="$language = 'en'">Operation</xsl:if>
    </xsl:param>
    <xsl:param name="dateTxt">
        <xsl:if test="$language = 'it'">Data</xsl:if>
        <xsl:if test="$language = 'en'">Date</xsl:if>
    </xsl:param>
    <xsl:param name="statusTxt">
        <xsl:if test="$language = 'it'">Stato</xsl:if>
        <xsl:if test="$language = 'en'">Status</xsl:if>
    </xsl:param>
    <xsl:param name="deleteTxt">
        <xsl:if test="$language = 'it'">Cancella</xsl:if>
        <xsl:if test="$language = 'en'">Cancel</xsl:if>
    </xsl:param>

    <xsl:param name="arrivalTxt">
        <xsl:if test="$language = 'it'">Data di arrivo</xsl:if>
        <xsl:if test="$language = 'en'">Arrival Date</xsl:if>
    </xsl:param>
    <xsl:param name="beTxt">
        <xsl:if test="$language = 'it'">Data finale</xsl:if>
        <xsl:if test="$language = 'en'">Back-end Expiration Date</xsl:if>
    </xsl:param>

    <xsl:param name="ordTxt">
        <xsl:if test="$language = 'it'">Id Ordine</xsl:if>
        <xsl:if test="$language = 'en'">order Id</xsl:if>
    </xsl:param>
    <xsl:param name="hostTxt">
        <xsl:if test="$language = 'it'">Push Host</xsl:if>
        <xsl:if test="$language = 'en'">Push Host</xsl:if>
    </xsl:param>


    <xsl:template match="instances">
        <table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
            <tr>
                <td class="sortable">
                    <xsl:value-of select="$toolsTxt"/>
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=INSTANCE_ID&amp;order=<xsl:value-of select="$instanceIdOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$instanceTxt"/>
                    </a>
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=ORDER_ID&amp;order=<xsl:value-of select="$orderIdOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$ordTxt"/>
                    </a>
                </td>
                <td class="sortable">
                     <xsl:value-of select="$actTxt"/>
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=ARRIVAL_DATE&amp;order=<xsl:value-of select="$arrivalDateOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$arrivalTxt"/>
                    </a>
                </td>
                <xsl:choose>
                        <xsl:when test="$type='A'">
                            <td class="sortable">
                               <a>
                                    <xsl:attribute name="href">viewServiceInstances?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=EXPIRATION_DATE&amp;order=<xsl:value-of select="$expirationDateOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                                    <xsl:value-of select="$beTxt"/>
                                </a>
                            </td>
                            <td class="sortable">
                               <a>
                                    <xsl:attribute name="href">viewServiceInstances?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=PUSH_HOST&amp;order=<xsl:value-of select="$pushHostOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                                    <xsl:value-of select="$hostTxt"/>
                                </a>
                            </td>
                          </xsl:when>
                </xsl:choose>

                <td class="sortable">
                   <a>
                        <xsl:attribute name="href">viewServiceInstances?opType=<xsl:value-of select="$type"/>&amp;serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=STATUS&amp;order=<xsl:value-of select="$statusOrderDirection"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$statusTxt"/>
                    </a>
                </td>
            </tr>

            <xsl:for-each select="instance">
               <tr>
                    <td>
                            <xsl:choose>
                                    <xsl:when test='@statusAsByte="2" or @statusAsByte="4" or @statusAsByte="6" or @statusAsByte="7" or @statusAsByte="13" or @statusAsByte="10" or @statusAsByte="5" or @statusAsByte="11" or @statusAsByte="9"'>
                                            <input type="checkbox">
                                                    <xsl:attribute name="name">INST<xsl:number value="position()" format="1"/></xsl:attribute>
                                            </input>
                                            <input type="hidden">
                                                    <xsl:attribute name="name">ID_<xsl:number value="position()" format="1"/></xsl:attribute>
                                                    <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
                                            </input>
                                             <input type="hidden">
                                                    <xsl:attribute name="name">KEY_<xsl:number value="position()" format="1"/></xsl:attribute>
                                                    <xsl:attribute name="value"><xsl:value-of select="@key"/></xsl:attribute>
                                            </input>
                                            <!--input type="hidden" name="instanceNum">
                                                    <xsl:attribute name="value"><xsl:number value="position()" format="1"/></xsl:attribute>
                                            </input-->
                                    </xsl:when>

                                    <xsl:otherwise>
                                            <a>
                                                <xsl:attribute name="href">manager?cmd=cancelInstance&amp;serviceName=<xsl:value-of select="$serviceName"/>&amp;opType=<xsl:value-of select="$type"/>&amp;id=<xsl:value-of select="@id"/></xsl:attribute>
                                                    <xsl:value-of select="$deleteTxt"/>
                                            </a>
                                    </xsl:otherwise>
                            </xsl:choose>
                    </td>

                    <td>
                            <a>
                                    <xsl:attribute name="href"><xsl:value-of select="$viewInstancePage"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;id=<xsl:value-of select="@id"/></xsl:attribute>
                                    <xsl:value-of select="@key"/>
                            </a>
                    </td>
                    <td>
                            <xsl:value-of select="@orderId"/>
                    </td>
                    <td>
                            <xsl:value-of select="@operation"/>
                    </td>

                   
                    <td>
                            <xsl:value-of select="@date"/>
                    </td>
                    <xsl:choose>
                        <xsl:when test="$type='A'">
                            <td>
                                <xsl:value-of select="@serverExpirationDateTime"/>
                            </td>
                             <td>
                                    <xsl:value-of select="@hostName"/>
                            </td>
                        </xsl:when>
                    </xsl:choose>
                     <td>
                            <xsl:value-of select="@status"/>
                    </td>

                    
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>