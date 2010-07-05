<?xml version="1.0" encoding="UTF-8"?>
<!--
 -
 -  Developed By:      Intecs S.P.A.
 -  File Name:         $RCSfile: listOperations.xsl,v $
 -  TOOLBOX Version:   $Name:  $
 -  File Revision:     $Revision: 1.4 $
 -  Revision Date:     $Date: 2004/05/21 15:11:58 $
 -
 -->
<xsl:stylesheet xmlns:sd="http://pisa.intecs.it/mass/toolbox/serviceDescriptor" xmlns:exsl="http://exslt.org/common"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://pisa.intecs.it/mass/toolbox/service" xmlns:ipo="http://www.mass.com/IPO" version="2.0">
  <!--xsl:output method="html" version="1.0" encoding="ISO-8859-1" omit-xml-declaration="no" indent="no" media-type="text/html"/-->
  <xsl:output method="xml"/>
  <xsl:param name="orderBy">noOrder</xsl:param>
  <xsl:param name="serviceName"/>
  <xsl:param name="proxyURL"/>
  <xsl:param name="manageOperationsURL"/>
  <xsl:param name="configureOperationURL"/>
  <xsl:param name="deleteOperationURL"/>
  <xsl:param name="viewWSDLInfoURL"/>
  <xsl:param name="language"/>
  <xsl:param name="opTxt">
    <xsl:if test="$language = 'it'">Operazione</xsl:if>
    <xsl:if test="$language = 'en'">Operation</xsl:if>
  </xsl:param>
  <xsl:param name="typeTxt">
    <xsl:if test="$language = 'it'">Tipo</xsl:if>
    <xsl:if test="$language = 'en'">Type</xsl:if>
  </xsl:param>
  <xsl:param name="confTxt">
    <xsl:if test="$language = 'it'">Configura</xsl:if>
    <xsl:if test="$language = 'en'">Configure</xsl:if>
  </xsl:param>
  <xsl:param name="delTxt">
    <xsl:if test="$language = 'it'">Cancella</xsl:if>
    <xsl:if test="$language = 'en'">Delete</xsl:if>
  </xsl:param>
  <xsl:template match="/">
    <xsl:apply-templates select="sd:service/sd:interface/sd:operations"/>
  </xsl:template>
  <xsl:template match="sd:operations">

      <xsl:variable name="interface">
        <table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
      <tr>
        <td class="sortableHeader" height="50">
          <a>
            <xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=operation</xsl:attribute>
            <xsl:value-of select="$opTxt"/>
          </a>
        </td>
        <td class="sortableHeader" height="50">
          <a><xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=SOAPAction</xsl:attribute>SOAP Action</a>
        </td>
        <td class="sortableHeader" height="50">
          <a>
            <xsl:attribute name="href"><xsl:value-of select="$manageOperationsURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;orderBy=type</xsl:attribute>
            <xsl:value-of select="$typeTxt"/>
          </a>
        </td>
        <td class="sortableHeader" height="50">Timeout</td>
        <td class="sortableHeader" height="50">Polling Rate</td>
        <td class="sortableHeader" height="50">Retry attempts</td>
        <td class="sortableHeader" height="50">Retry rate</td>
        <td class="sortableHeader" height="50">Push Hosts</td>
        <td class="sortableHeader" height="50">Tools</td>
      </tr>
      </table>
      </xsl:variable>
      <xsl:if test="$orderBy='noOrder'">
        <!--xsl:apply-templates select="sd:operation"/-->
        <!--tr>
          <td colspan="10"-->

            <xsl:call-template name="getWPSOperationsInterface">
              <xsl:with-param name="operationsInterface"><xsl:copy-of select="$interface"/></xsl:with-param>
              <xsl:with-param name="operationsNode" select="."/>
              <xsl:with-param name="currentOperation" select="1"/>
            </xsl:call-template>
          <!--/td>
        </tr-->
      </xsl:if>
      <xsl:if test="$orderBy='operation'">
        <xsl:apply-templates select="sd:operation">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="$orderBy='SOAPAction'">
        <xsl:apply-templates select="sd:operation">
          <xsl:sort select="sd:script/@SOAPAction"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="$orderBy='type'">
        <xsl:apply-templates select="sd:operation">
          <xsl:sort select="@type"/>
        </xsl:apply-templates>
      </xsl:if>

  </xsl:template>
  <xsl:template match="sd:admittedHost">
    <xsl:value-of select="."/>
    <br/>
  </xsl:template>
  <xsl:template name="getWPSOperationsInterface">
    <xsl:param name="operationsInterface"/>
    <xsl:param name="operationsNode"/>
    <xsl:param name="currentOperation"/>
    <!--DOCUMENTO>
      <xsl:copy-of select="$operationsNode//sd:operation[$currentOperation]"/>
    </DOCUMENTO-->
    <xsl:choose>
      <xsl:when test="$operationsNode//sd:operation[$currentOperation]">
       
        <xsl:variable name="currOp" select="$operationsNode//sd:operation[$currentOperation]"/>
        <xsl:variable name="operationName" select="$currOp/@name"/>
        <xsl:variable name="processNameTemp" select="substring-after($currOp/@name, '_')"/>
        <xsl:variable name="processName">
          <xsl:choose>
           <xsl:when test="substring-after($processNameTemp, '_') != ''">
              <xsl:value-of select="substring-before($processNameTemp, '_')"/>
           </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$processNameTemp"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable> 
          <xsl:comment>Interface For <xsl:value-of select="$processName"/> Operation: <xsl:value-of select="$operationName"/></xsl:comment> 
        <xsl:variable name="newInterface">

          <xsl:choose>
            <xsl:when test="$processName !=''">
              <xsl:choose>
                <xsl:when test="$operationsInterface//table/tr[@processname = $processName]">
                  <table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
                    <xsl:for-each select="$operationsInterface//table/tr">
                      <xsl:choose>
                        <xsl:when test="@processname= $processName and @operationname">
                          <xsl:copy-of select="."/>
                          <tr processname="{$processName}" operationname="{$operationName}">
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@name"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@SOAPAction"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@type"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@requestTimeout"/>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@pollingRate"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@retryAttempts"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@retryRate"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:apply-templates select="$currOp/sd:admittedHosts/sd:admittedHost"/>
                          </td>
                          <td class="sortable" align="center"><a><xsl:attribute name="href"><xsl:value-of select="$configureOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/></xsl:attribute><xsl:value-of select="$confTxt"/></a> -
                                                              <a><xsl:attribute name="href"><xsl:value-of select="$viewWSDLInfoURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/></xsl:attribute>
                                                              Info
                                                  </a> <!-- -
                                                  <a><xsl:attribute name="href">javascript:confirm('<xsl:value-of select="$deleteOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/>','Delete operations?','Confirm')</xsl:attribute><xsl:value-of select="$delTxt"/></a> -->
                          </td>
                          <td>
                          </td>
                        </tr>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:copy-of select="."/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:for-each>
                  </table>

                </xsl:when>
                <xsl:otherwise>
                <table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
                    <xsl:for-each select="$operationsInterface//table/tr">
                      <xsl:copy-of select="."/>
                    </xsl:for-each>
                    <tr processname="{$processName}" rowspan="3">
                       <xsl:variable name="processInfo" select="document(string(concat($proxyURL, '/TOOLBOX/manager?cmd=getServiceResource&amp;serviceName=', $serviceName, '&amp;relativePath=AdditionalResources/WPS/InfoProcess/', $processName, '_Info.xml')))"/>
                       <xsl:variable name="processType" select="$processInfo//Type"/>
                       <xsl:variable name="async" select="$processInfo//Asynchronous"/>
                       <!--xsl:variable name="processType" select="concat($proxyURL, '/TOOLBOX/manager?cmd=getServiceResource&amp;serviceName=', $serviceName, '&amp;relativePath=AdditionalResources/WPS/InfoProcess/', $processName, '_Info.xml')"/-->
                       <td colspan="2" bgcolor="#e7a82d">WPS <b> <xsl:value-of select="$processName"/></b> Processing Operations: </td>
                       <td colspan="3" align="center" bgcolor="#e7a82d"/>
                       <td colspan="2" align="center" bgcolor="#e7a82d">
                            <input type="button" value="Resources Manager" name="showResources" onclick="javascript:getProcessingResourceWindow('{$serviceName}', '{$processName}', '{$processType}', '{$async}');"/>
                       </td>
                       <td colspan="2" align="center" bgcolor="#e7a82d">
                            <input type="button" value="Remove" name="removeProcessing" onclick="javascript:removeProcessing('{$serviceName}', '{$processName}', '{$processType}', '{$async}');"/>
                       </td>
                       <!--td colspan="2" align="center" bgcolor="#e7a82d">
                            <input type="button" value="Change Describe Process" name="changeDescribe" onclick="javascript:alert('describe');"/>
                       </td>
                       <td colspan="3" align="center" bgcolor="#e7a82d">
                            <input type="button" value="Change Engine Script" name="changeScript" onclick="javascript:alert('engine');"/>
                       </td-->
                    </tr>
                    <tr processname="{$processName}" operationname="{$operationName}">
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@name"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@SOAPAction"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@type"/>
                          </td>
                          <td class="sortable">
                            <xsl:value-of select="$currOp/@requestTimeout"/>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@pollingRate"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@retryAttempts"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:if test="$currOp/@type = 'asynchronous'">
                              <xsl:value-of select="$currOp/@retryRate"/>
                            </xsl:if>
                            <xsl:if test="$currOp/@type = 'synchronous'">-</xsl:if>
                          </td>
                          <td class="sortable">
                            <xsl:apply-templates select="$currOp/sd:admittedHosts/sd:admittedHost"/>
                          </td>
                          <td class="sortable" align="center"><a><xsl:attribute name="href"><xsl:value-of select="$configureOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/></xsl:attribute><xsl:value-of select="$confTxt"/></a> -
                                                              <a><xsl:attribute name="href"><xsl:value-of select="$viewWSDLInfoURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/></xsl:attribute>
                                                              Info
                                                  </a> <!-- -
                                                  <a><xsl:attribute name="href">javascript:confirm('<xsl:value-of select="$deleteOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="$currOp/@name"/>','Delete operations?','Confirm')</xsl:attribute><xsl:value-of select="$delTxt"/></a-->
                          </td>
                          <td>
                          </td>
                        </tr>
                       
                  </table>
      
                  </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <table width="100%" height="90%" cellpadding="2" cellspacing="1" align="center" valign="middle">
                  <xsl:for-each select="$operationsInterface/table/tr">
                      <xsl:copy-of select="."/>
                  </xsl:for-each>
                <xsl:apply-templates select="$operationsNode//sd:operation[$currentOperation]"/>
                </table>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        
        <!--xsl:comment><xsl:copy-of select="$newInterface"/></xsl:comment--> 
        <xsl:call-template name="getWPSOperationsInterface">
          <xsl:with-param name="operationsInterface">
            <xsl:copy-of select="$newInterface"/>
          </xsl:with-param>
          <xsl:with-param name="operationsNode" select="$operationsNode"/>
          <xsl:with-param name="currentOperation" select="$currentOperation+1"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$operationsInterface"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="sd:operation">
    <tr>
      <td class="sortable">
        <xsl:value-of select="@name"/>
      </td>
      <td class="sortable">
        <xsl:value-of select="@SOAPAction"/>
      </td>
      <td class="sortable">
        <xsl:value-of select="@type"/>
      </td>
      <td class="sortable">
        <xsl:value-of select="@requestTimeout"/>
      </td>
      <td class="sortable">
        <xsl:if test="@type = 'asynchronous'">
          <xsl:value-of select="@pollingRate"/>
        </xsl:if>
        <xsl:if test="@type = 'synchronous'">-</xsl:if>
      </td>
      <td class="sortable">
        <xsl:if test="@type = 'asynchronous'">
          <xsl:value-of select="@retryAttempts"/>
        </xsl:if>
        <xsl:if test="@type = 'synchronous'">-</xsl:if>
      </td>
      <td class="sortable">
        <xsl:if test="@type = 'asynchronous'">
          <xsl:value-of select="@retryRate"/>
        </xsl:if>
        <xsl:if test="@type = 'synchronous'">-</xsl:if>
      </td>
      <td class="sortable">
        <xsl:apply-templates select="sd:admittedHosts/sd:admittedHost"/>
      </td>
      <td class="sortable" align="center"><a><xsl:attribute name="href"><xsl:value-of select="$configureOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="$confTxt"/></a>  -
					<a><xsl:attribute name="href"><xsl:value-of select="$viewWSDLInfoURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/></xsl:attribute>
					Info
					</a> <!---
                     <a><xsl:attribute name="href">javascript:confirm('<xsl:value-of select="$deleteOperationURL"/>?serviceName=<xsl:value-of select="$serviceName"/>&amp;operationName=<xsl:value-of select="@name"/>','Delete operations?','Confirm')</xsl:attribute><xsl:value-of select="$delTxt"/></a-->
			</td>
    </tr>
  </xsl:template>
  <xsl:template match="sd:abstract">
	</xsl:template>
  <xsl:template match="sd:description">
	</xsl:template>
</xsl:stylesheet>
