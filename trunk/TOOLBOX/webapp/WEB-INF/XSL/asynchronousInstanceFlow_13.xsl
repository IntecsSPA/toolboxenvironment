<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs  S.P.A.
 -  File Name:         $RCSfile: gaFlow.xsl,v $
 -  TOOLBOX Version:   $Name: HEAD $
 -  File Revision:     $Revision: 1.11 $
 -  Revision Date:     $Date: 2006/11/23 15:15:25 $
 -
-->
<xsl:stylesheet version="1.0" xmlns:log="http://pisa.intecs.it/mass/toolbox/log" xmlns:tbx="http://pisa.intecs.it/mass/toolbox/toolbox" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ipo="http://www.mass.com/IPO" xmlns:h="http://www.mass.com/toolbox/help" xmlns:ss="http://pisa.intecs.it/mass/toolbox/serviceStatus" >
    <xsl:output method="html" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/>
    <xsl:param name="serviceName"></xsl:param>
    <xsl:param name="operationName"></xsl:param>
    <xsl:param name="instanceKey"></xsl:param>
    <xsl:param name="hasSSE"></xsl:param>
    <xsl:param name="hasGML"></xsl:param>
    <xsl:param name="language"></xsl:param>
    <xsl:param name="inputTxt">
        <xsl:if test="$language = 'en'">Input message (</xsl:if>
        <xsl:if test="$language = 'it'">Messaggio di input (</xsl:if>
    </xsl:param>
    <xsl:param name="ackTxt">
        <xsl:if test="$language = 'en'">Acknowledge message (</xsl:if>
        <xsl:if test="$language = 'it'">Messaggio di acknowledge (</xsl:if>
    </xsl:param>
    <xsl:param name="fexecTxt">
        <xsl:if test="$language = 'en'">First script execution result (</xsl:if>
        <xsl:if test="$language = 'it'">Risultato esecuzione del primo script (</xsl:if>
    </xsl:param>
    <xsl:param name="sexecTxt">
        <xsl:if test="$language = 'en'">Second script execution result (</xsl:if>
        <xsl:if test="$language = 'it'">Risultato esecuzione del secondo script(</xsl:if>
    </xsl:param>
    <xsl:param name="texecTxt">
        <xsl:if test="$language = 'en'">Third script execution result (</xsl:if>
        <xsl:if test="$language = 'it'">Risultato esecuzione del terzo script (</xsl:if>
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
        <TABLE cellSpacing="0" cellPadding="0" width="100%" align="center" >
            <TBODY>
                <TR>
                    <TD class="pageBody" id="main">
                        <p>
                            <TABLE bgcolor="#FFFFFF" cellSpacing="0" cellPadding="0" width="100%" align="center" valign="top">
                                <TBODY>
                                    <xsl:apply-templates select="*"></xsl:apply-templates>
                                </TBODY>
                            </TABLE>
                        </p>
                    </TD>
                </TR>
            </TBODY>
        </TABLE>
    </xsl:template>

    <xsl:template match="/log:flow">


        <!-- ###############  START ITEM ############  -->
        <TR>
            <TD id="main" align="middle">
                <img src="images/start.default.jpg" alt="start"></img>
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>

        <!-- ###############  INPUT MESSAGE ############  -->
        <TR>
            <TD id="main" align="middle">
                <img src="images/receive.default.jpg" alt="receive"></img>
                <br></br>
                <xsl:value-of select="$inputTxt"/>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:flow/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                    <img src="images/xml-icon.jpg" alt="XML view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:flow/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                    <img src="images/tree-icon.jpg" alt="tree view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:flow/log:inputMessage/@id"/>','Input Message')</xsl:attribute>
                    <img src="images/download-icon.gif" alt="XML view"/>
                </a>
                )
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>

        <!-- ###############  RESPONSE BUILDER ############  -->
        <TR>
            <TD id="main" align="middle">
                <img src="images/execute1.default.jpg" alt="execute first script"></img>
                <br></br>
                <xsl:value-of select="$respTxt"/>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:flow/log:responseScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/xml-icon.jpg" alt="XML view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:flow/log:responseScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/tree-icon.jpg" alt="tree view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:flow/log:responseScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/download-icon.gif" alt="XML view"/>
                </a>
                )
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>

        <!-- ########## Response builder send ########### -->
        <TR>
            <TD id="main" align="middle">
                <img src="images/send.default.jpg" alt="send"></img>
                <br></br>
                <xsl:value-of select="$ackTxt"/>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:flow/log:acknowledgeMessage/@id"/>','Acknowledge Message')</xsl:attribute>
                    <img src="images/xml-icon.jpg" alt="XML view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:flow/log:acknowledgeMessage/@id"/>','Acknowledge Message')</xsl:attribute>
                    <img src="images/tree-icon.jpg" alt="tree view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:flow/log:acknowledgeMessage/@id"/>','Acknowledge Message')</xsl:attribute>
                    <img src="images/download-icon.gif" alt="XML view"/>
                </a>
                )
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/execute1.faulted.jpg" alt="execute first script"></img>
                <br></br>
                <xsl:value-of select="$fexecTxt"/>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('xml','id=<xsl:value-of select="/log:flow/log:firstScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/xml-icon.jpg" alt="XML view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('tree','id=<xsl:value-of select="/log:flow/log:firstScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/tree-icon.jpg" alt="tree view"/>
                </a>
                <a>
                    <xsl:attribute name="href">javascript:viewResource('text','id=<xsl:value-of select="/log:flow/log:firstScriptExecution/@id"/>','Operation First Script')</xsl:attribute>
                    <img src="images/download-icon.gif" alt="XML view"/>
                </a>
                <xsl:if test="/log:flow//log:email/@id !=''">
                    <a>
                        <xsl:attribute name="href">javascript:viewResource('email','id=<xsl:value-of select="/log:flow//log:email/@id"/>','Error email')</xsl:attribute>
                        <img src="images/email-icon.jpg" alt="Email view"/>
                    </a>
                </xsl:if>
                )
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>


        <TR>
            <TD id="main" align="middle">
                <img src="images/execute2.disabled.jpg" alt="execute second script"></img>
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>


        <TR>
            <TD id="main" align="middle">
                <img src="images/execute3.disabled.jpg" alt="execute third script"></img>
            </TD>
        </TR>


        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/send.disabled.jpg" alt="send"></img>
            </TD>
        </TR>

        <TR>
            <TD id="main" align="middle">
                <img src="images/garrow.jpg" alt="arrow"></img>
            </TD>
        </TR>


        <TR>
            <TD id="main" align="middle">
                <img src="images/end.default.jpg" alt="end"></img>
            </TD>
        </TR>

    </xsl:template>

</xsl:stylesheet>