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
    <xsl:param name="orderBy">INSTANCE_ID</xsl:param>
    <xsl:param name="order">ASC</xsl:param>
    <xsl:param name="inverseOrder"></xsl:param>
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
                        <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=INSTANCE_ID&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$instanceTxt"/>
                        <xsl:if test="$orderBy='INSTANCE_ID' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
			<xsl:if test="$orderBy='INSTANCE_ID' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                    </a>
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=ORDER_ID&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$ordTxt"/>
                        <xsl:if test="$orderBy='ORDER_ID' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
			<xsl:if test="$orderBy='ORDER_ID' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                    </a>
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=SOAP_ACTION&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$actTxt"/>
                        <xsl:if test="$orderBy='SOAP_ACTION' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
			<xsl:if test="$orderBy='SOAP_ACTION' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                    </a>
                     
                </td>
                <td class="sortable">
                    <a>
                        <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=ARRIVAL_DATE&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$arrivalTxt"/>
                        <xsl:if test="$orderBy='ARRIVAL_DATE' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
			<xsl:if test="$orderBy='ARRIVAL_DATE' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                    </a>
                </td>
                <xsl:choose>
                        <xsl:when test="$type='A'">
                            <td class="sortable">
                               <a>
                                    <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=EXPIRATION_DATE&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                                    <xsl:value-of select="$beTxt"/>
                                    <xsl:if test="$orderBy='EXPIRATION_DATE' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
                                    <xsl:if test="$orderBy='EXPIRATION_DATE' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                                </a>
                            </td>
                            <td class="sortable">
                               <a>
                                    <xsl:attribute name="href">viewServiceInstances.jsp?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=PUSH_HOST&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                                    <xsl:value-of select="$hostTxt"/>
                                    <xsl:if test="$orderBy='PUSH_HOST' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
                                    <xsl:if test="$orderBy='PUSH_HOST' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                                </a>
                            </td>
                          </xsl:when>
                </xsl:choose>

                <td class="sortable">
                   <a>
                        <xsl:attribute name="href">viewServiceInstances.jsp?opType=<xsl:value-of select="$type"/>&amp;serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=STATUS&amp;order=<xsl:value-of select="$inverseOrder"/>&amp;page=<xsl:value-of select="$page"/></xsl:attribute>
                        <xsl:value-of select="$statusTxt"/>
                        <xsl:if test="$orderBy='STATUS' and $order='ASC' "><img src="images/down.gif" border="0"/></xsl:if>
                        <xsl:if test="$orderBy='STATUS' and $order='DESC' "><img src="images/up.gif" border="0"/></xsl:if>
                    </a>
                </td>
            </tr>

            <xsl:choose>
                    <xsl:when test="$order='ASC'">
                            <xsl:choose>
                                    <xsl:when test="$orderBy='INSTANCE_ID'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@key" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='ARRIVAL_DATE'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@date" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='ORDER_ID'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@orderId" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='SOAP_ACTION'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@soapAction" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                     <xsl:when test="$orderBy='EXPIRATION_DATE'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@serverExpirationDateTime" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                     <xsl:when test="$orderBy='PUSH_HOST'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@hostName" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                     <xsl:when test="$orderBy='STATUS'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@status" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:otherwise>
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@key" order="ascending"/>
                                            </xsl:apply-templates>
                                    </xsl:otherwise>
                            </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                            <xsl:choose>
                                    <xsl:when test="$orderBy='INSTANCE_ID'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@key" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='ARRIVAL_DATE'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@date" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='ORDER_ID'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@orderId" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='SOAP_ACTION'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@soapAction" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="$orderBy='EXPIRATION_DATE'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@serverExpirationDateTime" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                     <xsl:when test="$orderBy='PUSH_HOST'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@hostName" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                     <xsl:when test="$orderBy='STATUS'">
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@status" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:otherwise>
                                            <xsl:apply-templates select="instance">
                                                    <xsl:sort select="@key" order="descending"/>
                                            </xsl:apply-templates>
                                    </xsl:otherwise>
                            </xsl:choose>
                    </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>

    <xsl:template match="instance">
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
    </xsl:template>
</xsl:stylesheet>