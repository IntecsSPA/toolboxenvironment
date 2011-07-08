<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: gFlow.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.8 $
 -  Revision Date:     $Date: 2006/10/25 13:46:11 $
 -
-->
<xsl:stylesheet version="1.0" xmlns:log="http://pisa.intecs.it/mass/toolbox/log" xmlns:tbx="http://pisa.intecs.it/mass/toolbox/toolbox" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO" xmlns:h="http://www.mass.com/toolbox/help">
    <xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
    <xsl:param name="serviceName"/>
    <xsl:param name="operationName">aa</xsl:param>
    <xsl:param name="language"></xsl:param>
     <xsl:param name="hasSSE"></xsl:param>
      <xsl:param name="hasGML"></xsl:param>
    <xsl:param name="inputTxt">
        <xsl:if test="$language = 'en'">Input message (</xsl:if>
        <xsl:if test="$language = 'it'">Messaggio di input (</xsl:if>
    </xsl:param>
    <xsl:param name="execTxt">
        <xsl:if test="$language = 'en'">Execution result(</xsl:if>
        <xsl:if test="$language = 'it'">Risultato esecuzione(</xsl:if>
    </xsl:param>
    <xsl:param name="respTxt">
        <xsl:if test="$language = 'en'">Response builder execution result (</xsl:if>
        <xsl:if test="$language = 'it'">risultato esecuzione del Response builder (</xsl:if>
    </xsl:param>
    <xsl:param name="mailTxt">
        <xsl:if test="$language = 'en'">- error email (</xsl:if>
        <xsl:if test="$language = 'it'">- errore email (</xsl:if>
    </xsl:param>
    <xsl:param name="remainTxt">
        <xsl:if test="$language = 'en'">Remaining attempts (</xsl:if>
        <xsl:if test="$language = 'it'">Tentativi rimanenti (</xsl:if>
    </xsl:param>
    <xsl:param name="unpushedTxt">
        <xsl:if test="$language = 'en'">Unpushed message(</xsl:if>
        <xsl:if test="$language = 'it'">Messaggio non spedito(</xsl:if>
    </xsl:param>
    <xsl:param name="outputTxt">
        <xsl:if test="$language = 'en'">Output message (</xsl:if>
        <xsl:if test="$language = 'it'">Messaggio di output (</xsl:if>
    </xsl:param>

        <xsl:template match="/">
        <script>
            var serviceName= '<xsl:value-of select="$serviceName"/>';
            var instanceType= 'S';
            var instanceID='<xsl:value-of select="/log:log/log:instance/@key"/>';
        </script>
        <TABLE cellSpacing="0" cellPadding="0" width="100%" align="center">
            <TBODY>
                <TR>
                    <TD class="pageBody" id="main">
                        <p>
                            <TABLE bgcolor="#FFFFFF" cellSpacing="0" cellPadding="0" width="100%" align="center" valign="top">
                                <TBODY>
                                    <!-- START -->
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/start.default.jpg" alt="start"/>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/garrow.jpg" alt="start"/>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <xsl:choose>
                                                <!-- Received invalid input message -->
                                                <xsl:when test='/log:log//log:invalidInputMessage'>
                                                    <img src="images/receive.faulted.jpg" alt="receive"/>
                                                    <br/>
                                                    <xsl:value-of select="$inputTxt"/>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:invalidInputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/xml-icon.jpg" alt="arrow"/>
                                                        
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log//log:invalidInputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:log//log:invalidInputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                </xsl:when>
                                                <!-- Valid message -->
                                                <xsl:when test='/log:log/log:inputMessage'>
                                                    <img src="images/receive.default.jpg" alt="receive"/>
                                                    <br/>
                                                    <xsl:value-of select="$inputTxt"/>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/xml-icon.jpg" alt="arrow"/>
                                                        
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:log/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                                                        <img src="images/download-icon.gif" alt="arrow"/>
                                                    </a>
                                                </xsl:when>
                                            </xsl:choose>)
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/garrow.jpg" alt="arrow"/>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <!-- execution image -->
                                        <TD id="main" align="middle">
                                            <xsl:choose>
                                                <xsl:when test='/log:log//log:error'>
                                                    <img src="images/execute.faulted.jpg" alt="execute"/>
                                                    <br/>
                                                    <xsl:value-of select="$execTxt"/>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:error/@id"/>','Operation Script')</xsl:attribute>
                                                        <img src="images/xml-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log//log:error/@id"/>','Operation Script')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    )<xsl:if test='/log:log/log:log/log:errorEmail/@sent != "" '> - <xsl:value-of select="$mailTxt"/>
                                                        <a>
                                                            <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:error/@id"/>','Operation Script')</xsl:attribute>
                                                            <img src="images/xml-icon.gif" alt="error email"/>
                                                        </a>
                                                    )</xsl:if>
                                                </xsl:when>
                                                <xsl:when test='/log:log//log:completed'>
                                                    <img src="images/execute.default.jpg" alt="execute"/>
                                                    <br/>
                                                    <xsl:value-of select="$execTxt"/>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:completed/@id"/>','Operation Script')</xsl:attribute>
                                                        <img src="images/xml-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log//log:completed/@id"/>','Operation Script')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:log//log:completed/@id"/>','Operation Script')</xsl:attribute>
                                                        <img src="images/download-icon.gif" alt="arrow"/>
                                                    </a>
                                                    )
                                                </xsl:when>
                                             
                                                <xsl:when test='/log:log//log:executing'>
                                                    <img src="images/execute.active.jpg" alt="execute"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <img src="images/execute.disabled.jpg" alt="execute"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/garrow.jpg" alt="arrow"/>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <xsl:choose>
                                                <xsl:when test='/log:log//log:invalidOutputMessage'>
                                                    <img src="images/send.faulted.jpg" alt="receive"/>
                                                    <br/>
                                                    <xsl:value-of select="$invalidOutputTxt"/>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:invalidOutputMessage/@id"/>','Output Message')</xsl:attribute>
                                                        <img src="images/xml-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log//log:invalidOutputMessage/@id"/>','Output Message')</xsl:attribute>
                                                        <img src="images/tree-icon.jpg" alt="arrow"/>
                                                    </a>
                                                    <a>
                                                        <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:log//log:invalidOutputMessage/@id"/>','Output Message')</xsl:attribute>
                                                        <img src="images/download-icon.gif" alt="arrow"/>
                                                    </a>
                                                    )
                                                </xsl:when>
                                                <xsl:when test="/log:log//log:outputMessage">
                                                    <img src="images/send.default.jpg" alt="send"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <img src="images/send.disabled.jpg" alt="send"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:if test='/log:log//log:outputMessage'>
                                                <br/>
                                                <xsl:value-of select="$outputTxt"/>
                                                <a>
                                                    <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:log//log:outputMessage/@id"/>','Output Message')</xsl:attribute>
                                                    <img src="images/xml-icon.jpg" alt="arrow"/>
                                                </a>
                                                <a>
                                                    <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:log//log:outputMessage/@id"/>','Output Message')</xsl:attribute>
                                                    <img src="images/tree-icon.jpg" alt="arrow"/>
                                                </a>
                                                <a>
                                                    <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:log//log:outputMessage/@id"/>','Output Message')</xsl:attribute>
                                                    <img src="images/download-icon.gif" alt="arrow"/>
                                                </a>
                                                <xsl:if test="$hasSSE='true'">
                                                     <a>
                                                     <xsl:attribute name="href">javascript:viewResource('SSEOutput','id=<xsl:value-of select="/log:log//log:outputMessage/@id"/>&amp;instanceId=<xsl:value-of select="/log:log/log:instance/@key"/>')</xsl:attribute>
                                                    <img src="images/sse.png" alt="SSE"/>
                                                </a>
                                                </xsl:if>
                                                <xsl:if test="$hasGML='true'">
                                                     <a>
                                                    <xsl:attribute name="href">javascript:viewResource('GMLOutput','id=<xsl:value-of select="/log:log//log:outputMessage/@id"/>&amp;instanceId=<xsl:value-of select="/log:log/log:instance/@key"/>')</xsl:attribute>
                                                    <img src="images/map.png" alt="GML"/>
                                                </a>
                                                </xsl:if>
                                                )
                                            </xsl:if>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/garrow.jpg" alt="arrow"/>
                                        </TD>
                                    </TR>
                                    <TR>
                                        <TD id="main" align="middle">
                                            <img src="images/end.default.jpg" alt="start"/>
                                        </TD>
                                    </TR>
                                    <!-- STOP -->
                                </TBODY>
                            </TABLE>
                        </p>
                    </TD>
                </TR>
            </TBODY>
        </TABLE>
    </xsl:template>
</xsl:stylesheet>
