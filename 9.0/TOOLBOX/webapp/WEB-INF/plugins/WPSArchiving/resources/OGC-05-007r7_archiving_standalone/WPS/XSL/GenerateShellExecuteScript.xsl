<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:param name="sleepStatus">5000</xsl:param>
  <xsl:template match="*">
    <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
      <setVariable name="executeProcessName">
        <string>
          <xsl:value-of select="substring-after(local-name(),'_')"/>
        </string>
      </setVariable>
      <!-- Control Input -->
      <setVariable name="controlExecuteInput">
        <xslt xmlOutput="true">
          <variable name="processRequest"/>
          <path startFrom="SERVICE_RESOURCE_DIR">
            <string>../AdditionalResources/WPS/XSL/ExecuteInputControl.xsl</string>
          </path>
          <parameter name="processName">
            <variable name="executeProcessName"/>
          </parameter>
        </xslt>
      </setVariable>
      <!--  dumpXML>
		 <string>${wpsScriptFolder}inputManagerScript.tscript</string>
		 <variable name="inputManagerScript"/>
	  </dumpXML-->
      <dumpXML>
        <path>
          <string>${wpsLogFolder}/${wpsProcessingInstance}/ResultControlRequest.xml</string>
        </path>
        <variable name="controlExecuteInput"/>
      </dumpXML>
      <if>
        <oeq>
          <xPath>
            <variable name="controlExecuteInput"/>
            <string>ControlResult/requestCorrect</string>
          </xPath>
          <string>true</string>
        </oeq>
        <sequence>
          <setVariable name="status">
            <string>
              <xsl:value-of select="./@status"/>
            </string>
          </setVariable>
          <setVariable name="storeExecuteResponse">
            <string>
              <xsl:value-of select="./@storeExecuteResponse"/>
            </string>
          </setVariable>
          <setVariable name="lineage">
            <string>
              <xsl:value-of select="./@lineage"/>
            </string>
          </setVariable>
          <setVariable name="wpsDownloadedInputFolder">
            <string>${wpsLogFolder}/${wpsProcessingInstance}/downloaded/</string>
          </setVariable>
          <setVariable name="wpsOutputFolder">
            <string>${wpsLogFolder}/${wpsProcessingInstance}/output/</string>
          </setVariable>
          <setVariable name="wpsStatusFolder">
            <string>${wpsLogFolder}/${wpsProcessingInstance}/status/</string>
          </setVariable>
          <setVariable name="wpsStatusRelativePath">
            <string>Log/${wpsProcessingInstance}/status/</string>
          </setVariable>
          <setVariable name="wpsStatusPercentageFile">
            <string>${wpsLogFolder}/${wpsProcessingInstance}/status/statusPercentage.xml</string>
          </setVariable>
          <setVariable name="wpsTempFolder">
            <string>${wpsLogFolder}/${wpsProcessingInstance}/temp/</string>
          </setVariable>
          <setVariable name="toolboxURL">
            <toolboxAddress/>
          </setVariable>
          <setVariable name="serviceName">
            <xPath xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1">
              <loadXML>
                <path startFrom="SERVICE_RESOURCE_DIR">
                  <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                </path>
              </loadXML>
              <string>//ExternalServiceInformation/Capabilities/wps:Capabilities/ows:ServiceIdentification/ows:Title</string>
            </xPath>
          </setVariable>
          <setVariable name="geoserverURL">
            <xPath xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1">
              <loadXML>
                <path startFrom="SERVICE_RESOURCE_DIR">
                  <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                </path>
              </loadXML>
              <string>//ExternalServiceInformation/GeoserverInfo/url</string>
            </xPath>
          </setVariable>
          <setVariable name="geoserverUser">
            <xPath xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1">
              <loadXML>
                <path startFrom="SERVICE_RESOURCE_DIR">
                  <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                </path>
              </loadXML>
              <string>//ExternalServiceInformation/GeoserverInfo/user</string>
            </xPath>
          </setVariable>
          <setVariable name="geoserverPassword">
            <xPath xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1">
              <loadXML>
                <path startFrom="SERVICE_RESOURCE_DIR">
                  <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                </path>
              </loadXML>
              <string>//ExternalServiceInformation/GeoserverInfo/password</string>
            </xPath>
          </setVariable>
          <setVariable name="toolboxGetServiceResourceURL">
            <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=Log/${wpsProcessingInstance}/output/</string>
          </setVariable>
          <setVariable name="ErrorType">
            <string/>
          </setVariable>
          <setVariable name="ErrorMessage">
            <string/>
          </setVariable>
          <setVariable name="particularReferenceIdentifier">
            <string>,</string>
          </setVariable>
          <setVariable name="particularReferenceURL">
            <string>,</string>
          </setVariable>
          <setVariable name="statusVariable">
            <string>ProcessSucceeded</string>
          </setVariable>
          <setVariable name="statusLocationVariable">
            <string/>
          </setVariable>
          <setVariable name="LiteralOutputElement">
            <string/>
          </setVariable>
          <setVariable name="BoundingBoxOutputElement">
            <string/>
          </setVariable>
          <mkdir>
            <variable name="wpsOutputFolder"/>
          </mkdir>
          <mkdir>
            <variable name="wpsStatusFolder"/>
          </mkdir>
          <mkdir>
            <variable name="wpsDownloadedInputFolder"/>
          </mkdir>
          <mkdir>
            <variable name="wpsTempFolder"/>
          </mkdir>
          <!-- Default EPSG Code for the GRASS Location Control /-->
          <setVariable name="defaultEPSG">
            <string>
              <xsl:for-each select="child::*">
                <xsl:if test="local-name()='DefaultEPSG'">
                  <xsl:value-of select="./text()"/>
                </xsl:if>
              </xsl:for-each>
            </string>
          </setVariable>
          <if>
            <oeq>
              <variable name="defaultEPSG"/>
              <string/>
            </oeq>
            <sequence>
              <setVariable name="controlEPSG">
                <literal type="boolean" value="true"/>
              </setVariable>
              <setVariable name="defaultEPSG">
                <string>4326</string>
              </setVariable>
            </sequence>
            <sequence>
              <setVariable name="controlEPSG">
                <literal type="boolean" value="false"/>
              </setVariable>
              <setVariable name="idx">
                <literal value="1"/>
              </setVariable>
              <while>
                <oneq>
                  <xPath>
                    <loadXML>
                      <path startFrom="SERVICE_RESOURCE_DIR">
                        <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                      </path>
                    </loadXML>
                    <string>//ExternalServiceInformation/EPSGSupported/AllowedValues/Value[${idx}]</string>
                  </xPath>
                  <string/>
                </oneq>
                <sequence>
                  <if>
                    <oeq>
                      <xPath>
                        <loadXML>
                          <path startFrom="SERVICE_RESOURCE_DIR">
                            <string>../AdditionalResources/ExternalServiceInformation.xml</string>
                          </path>
                        </loadXML>
                        <string>//ExternalServiceInformation/EPSGSupported/AllowedValues/Value[${idx}]</string>
                      </xPath>
                      <variable name="defaultEPSG"/>
                    </oeq>
                    <sequence>
                      <setVariable name="controlEPSG">
                        <literal type="boolean" value="true"/>
                      </setVariable>
                    </sequence>
                  </if>
                  <inc>
                    <variable name="idx"/>
                  </inc>
                </sequence>
              </while>
            </sequence>
          </if>
          <if>
            <variable name="controlEPSG"/>
            <sequence>
              <!-- Generate Input Manager Toolbox script /-->
              <setVariable name="inputManagerScript">
                <xslt xmlOutput="true">
                  <variable name="processRequest"/>
                  <path startFrom="SERVICE_RESOURCE_DIR">
                    <string>../AdditionalResources/WPS/XSL/ExecuteInputManager.xsl</string>
                  </path>
                  <parameter name="wpsDownloadedInputFolder">
                    <variable name="wpsDownloadedInputFolder"/>
                  </parameter>
                  <parameter name="processName">
                    <variable name="executeProcessName"/>
                  </parameter>
                </xslt>
              </setVariable>
              <dumpXML>
                <string>${wpsScriptFolder}inputManagerScript.tscript</string>
                <variable name="inputManagerScript"/>
              </dumpXML>
              <loadProcedure name="InputManagerProcedure">
                <string>${wpsScriptFolder}inputManagerScript.tscript</string>
              </loadProcedure>
              <call procedure="InputManagerProcedure">
                <argument name="LocalURIReferenecesList">
                  <string/>
                </argument>
                <argument name="AttributeReferenecesList">
                  <string/>
                </argument>
              </call>
              <!-- End Input Manager-->
              <if>
                <oeq>
                  <variable name="ErrorType"/>
                  <string/>
                </oeq>
                <sequence>
                  <if>
                    <oeq>
                      <variable name="storeExecuteResponse"/>
                      <string>true</string>
                    </oeq>
                    <sequence>
                      <setVariable name="statusLocationVariable">
                        <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=${wpsStatusRelativePath}executeResponseStatus.xml</string>
                      </setVariable>
                      <!-- Asynchronous Fork-->
                      <setVariable name="statusVariable"><string>ProcessStarted</string></setVariable>
                      <fork>
                        <sequence>
                          <!-- Generate Shell enviorement Shell Script /-->
                          <setVariable name="jobScript">
                            <xslt xmlOutput="false">
                              <variable name="processRequest"/>
                              <path startFrom="SERVICE_RESOURCE_DIR">
                                <string>../AdditionalResources/WPS/XSL/Execute_Shell_Enviorement.xsl</string>
                              </path>
                              <parameter name="retrivedDataUrls">
                                <variable name="LocalURIReferenecesList"/>
                              </parameter>
                              <parameter name="statusFileInstance">
                                <variable name="wpsStatusPercentageFile"/>
                              </parameter>
                              <parameter name="inputNameDataUrls">
                                <variable name="AttributeReferenecesList"/>
                              </parameter>
                              <parameter name="instanceValue">
                                <variable name="wpsProcessingInstance"/>
                              </parameter>
                              <parameter name="processingName">
                                <variable name="executeProcessName"/>
                              </parameter>
                              <parameter name="grassOutputFolder">
                                <variable name="wpsOutputFolder"/>
                              </parameter>
                              <parameter name="grassTempFolder">
                                <variable name="wpsTempFolder"/>
                              </parameter>
                              <parameter name="downloadFolder">
                                <variable name="wpsDownloadedInputFolder"/>
                              </parameter>
                              <parameter name="scriptPath">
                                <path startFrom="SERVICE_RESOURCE_DIR">
                                  <string>GrassScripts/</string>
                                </path>
                              </parameter>
                              <parameter name="grassLogFileInstance">
                                <string>${wpsLogFolder}/${wpsProcessingInstance}/GrassLog.txt</string>
                              </parameter>
                              <parameter name="wpsWorkspace">
                                <path startFrom="SERVICE_RESOURCE_DIR">
                                  <string>../AdditionalResources/WPS/GrassWorkspace/</string>
                                </path>
                              </parameter>
                              <parameter name="epsgGrassLocation">
                                <variable name="defaultEPSG"/>
                              </parameter>
                              <parameter name="grassPath">
                                <string></string>
                              </parameter>
                            </xslt>
                          </setVariable>
	  		  <setVariable name="shellFile">
			  	<path startFrom="SERVICE_RESOURCE_DIR">
                                  <string>../Log/${wpsProcessingInstance}/shellScript.tscript</string>
                                </path>
                          </setVariable>
                          <dumpFile>
                            <path>
                              <variable name="shellFile"/>
                            </path>
                            <variable name="jobScript"/>
                          </dumpFile>
                          <command>
                            <string>chmod +x ${shellFile}</string>
                          </command>
                          <if>
                            <oeq>
                              <variable name="status"/>
                              <string>true</string>
                            </oeq>.
		                        <sequence>
		                          <!-- Check Status -->
                              <fork>
                               <sequence>
                                <setVariable name="currentPercentage">
                                  <string>0</string> 
                                </setVariable>
                                <!-- Output Control -->
                                      <setVariable name="outputControlScript">
                                        <xslt xmlOutput="true">
                                          <variable name="processRequest"/>
                                          <path startFrom="SERVICE_RESOURCE_DIR">
                                            <string>../AdditionalResources/WPS/XSL/ExecuteOutputControl.xsl</string>
                                          </path>
                                          <parameter name="outputRepository">
                                            <variable name="wpsOutputFolder"/>
                                          </parameter>
                                        </xslt>
                                      </setVariable>
                                      <dumpXML>
                                        <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                        <variable name="outputControlScript"/>
                                    </dumpXML>
                                     <loadProcedure name="OutputControlProcedure">
                                      <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                    </loadProcedure>
                                    <call procedure="OutputControlProcedure">
                                        <argument name="outputsControl">
                                          <string/>
                                        </argument>
                                        <argument name="outputsException">
                                          <string/>
                                        </argument>
                                        <argument name="outputsExceptionMessage">
                                          <string/>
                                        </argument>
                                    </call>
                                      <!-- End Output Control-->
                                <while>
                                  <and>
                                    <oneq>
                                      <variable name="currentPercentage"/>
                                      <string>100</string>
                                    </oneq>
                                    <oeq>
                                      <variable name="outputsControl"/>
                                      <string>ProcessPending</string>
                                    </oeq>
                                  </and>
                                  <sequence>
                                      <log><stringCat><string>Current Percentage: </string><variable name="currentPercentage"/></stringCat></log>
                                      <if>
                                        <fileExists>
	                                        <variable name="wpsStatusPercentageFile"/>
                                        </fileExists>
                                        <sequence>
                                          <setVariable name="newPercentage">
                                            <xPath>
                                                <loadXML>
                                                  <variable name="wpsStatusPercentageFile"/>
                                                </loadXML>
                                                <string>Percentage/@value</string>
                                            </xPath>
                                          </setVariable>
                                          <if>
                                            <oneq>
                                              <variable name="newPercentage"/>
                                              <variable name="currentPercentage"/>
                                            </oneq>
                                            <sequence>
                                              <setVariable name="currentPercentage">
                                                <variable name="newPercentage"/>
                                              </setVariable>
                                              <setVariable name="statusExecuteResponse">
                                                 <!-- Generate Execute Response -->
                                                  <xslt xmlOutput="true">
                                                    <variable name="processRequest"/>
                                                    <path startFrom="SERVICE_RESOURCE_DIR"><string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string></path>
                                                    <parameter name="processName"><variable name="executeProcessName"/></parameter>
                                                    <parameter name="status"><string>ProcessStarted</string></parameter>
                                                    <parameter name="statusMessage"><string/></parameter>
                                                    <parameter name="statusLocation"><variable name="statusLocationVariable"/></parameter>
                                                    <parameter name="creationTime">
                                                      <dateStringFormatter format="yyyy-MM-dd'T'HH:mm:ss'Z'"><now/></dateStringFormatter>
                                                    </parameter>
                                                    <parameter name="outputHttpRequest"><variable name="toolboxGetServiceResourceURL"/></parameter>
                                                    <parameter name="particularReferenceID"><variable name="particularReferenceIdentifier"/></parameter>
                                                    <parameter name="particularReferenceURL"><variable name="particularReferenceURL"/></parameter>
                                                    <parameter name="outputFolder"><variable name="wpsOutputFolder"/></parameter>
                                                    <parameter name="statusPercentCompleted"><variable name="currentPercentage"/></parameter>
                                                    <parameter name="originalRequest"><xmlRequest/></parameter>
                                                  </xslt>
                                                <!-- End Response-->
                                            </setVariable>
                                            <dumpXML>
                                              <string>${wpsStatusFolder}executeResponseStatus.xml</string>
                                              <variable name="statusExecuteResponse"/>
                                            </dumpXML>
                                          </sequence>
                                        </if>  
                                      </sequence>
                                    </if>
                                     
                                   <loadProcedure name="OutputControlProcedure">
                                    <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                  </loadProcedure>
                                  <call procedure="OutputControlProcedure">
                                      <argument name="outputsControl">
                                        <string/>
                                      </argument>
                                      <argument name="outputsException">
                                        <string/>
                                      </argument>
                                      <argument name="outputsExceptionMessage">
                                        <string/>
                                      </argument>
                                  </call>
                                    <!-- End Output Control-->
                                  <if>
                                    <oneq>
                                      <variable name="outputsControl"/>
                                      <string>ProcessPending</string>
                                    </oneq>
                                    <sequence>
                                      <setVariable name="statusExecuteResponse">
                                                 <!-- Generate Execute Response -->
                                                  <xslt xmlOutput="true">
                                                    <variable name="processRequest"/>
                                                    <path startFrom="SERVICE_RESOURCE_DIR"><string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string></path>
                                                    <parameter name="processName"><variable name="executeProcessName"/></parameter>
                                                    <parameter name="status"><variable name="outputsControl"/></parameter>
                                                    <parameter name="statusMessage"><string/></parameter>
                                                    <parameter name="statusLocation"><variable name="statusLocationVariable"/></parameter>
                                                    <parameter name="creationTime">
                                                      <dateStringFormatter format="yyyy-MM-dd'T'HH:mm:ss'Z'"><now/></dateStringFormatter>
                                                    </parameter>
                                                    <parameter name="outputHttpRequest"><variable name="toolboxGetServiceResourceURL"/></parameter>
                                                    <parameter name="particularReferenceID"><variable name="particularReferenceIdentifier"/></parameter>
                                                    <parameter name="particularReferenceURL"><variable name="particularReferenceURL"/></parameter>
                                                    <parameter name="outputFolder"><variable name="wpsOutputFolder"/></parameter>
                                                    <parameter name="statusExceptionCode"><variable name="outputsException"/></parameter>
                                                    <parameter name="statusMessage"><variable name="outputsExceptionMessage"/></parameter>
                                                    <parameter name="originalRequest"><xmlRequest/></parameter>
                                                  </xslt>
                                                <!-- End Response-->
                                            </setVariable>
                                            <dumpXML>
                                              <string>${wpsStatusFolder}executeResponseStatus.xml</string>
                                              <variable name="statusExecuteResponse"/>
                                            </dumpXML>
                                    </sequence>
                                  </if>  




                                    <sleep amount="{$sleepStatus}"/>
                                  </sequence>          
                                </while>
                               </sequence>                                 
                              </fork>

  
                          </sequence>
                          </if>
                          <command asynchronous="false">
                            <variable name="shellFile"/>
                          </command>
                          <setVariable name="currentPercentage">
                            <literal type="int" value="100"/>
                          </setVariable>
                          <!-- End Grass Section-->
                          <!-- Generate Output Manager Toolbox script-->
                          <setVariable name="outputManagerScript">
                            <xslt xmlOutput="true">
                              <variable name="processRequest"/>
                              <path startFrom="SERVICE_RESOURCE_DIR">
                                <string>../AdditionalResources/WPS/XSL/ExecuteOutputManager.xsl</string>
                              </path>
                              <parameter name="outputRepository">
                                <variable name="wpsOutputFolder"/>
                              </parameter>
                              <parameter name="processName">
                                <variable name="executeProcessName"/>
                              </parameter>
                              <parameter name="geoserverUrl">
                                <variable name="geoserverURL"/>
                              </parameter>
                              <parameter name="geoserverUser">
                                <variable name="geoserverUser"/>
                              </parameter>
                              <parameter name="geoserverPassword">
                                <variable name="geoserverPassword"/>
                              </parameter>  
                            </xslt>
                          </setVariable>
                          <dumpXML>
                            <string>${wpsScriptFolder}outputManagerScript.tscript</string>
                            <variable name="outputManagerScript"/>
                          </dumpXML>
                          <loadProcedure name="OutputManagerProcedure">
                            <string>${wpsScriptFolder}outputManagerScript.tscript</string>
                          </loadProcedure>
                          <call procedure="OutputManagerProcedure">
                            <argument name="ErrorType">
                              <string/>
                            </argument>
                            <argument name="ErrorMessage">
                              <string/>
                            </argument>
                            <argument name="LiteralOutputElement">
                              <string/>
                            </argument>
                            <argument name="BoundingBoxOutputElement">
                              <string/>
                            </argument>
                          </call>
                          <!-- End Output Manager -->
                          <if>
                            <oeq>
                              <variable name="ErrorType"/> <!-- Output Manager Error Control-->
                              <string/>
                            </oeq>
                            <sequence>
                                  <!-- Output Control -->
                                  <setVariable name="outputControlScript">
                                    <xslt xmlOutput="true">
                                      <variable name="processRequest"/>
                                      <path startFrom="SERVICE_RESOURCE_DIR">
                                        <string>../AdditionalResources/WPS/XSL/ExecuteOutputControl.xsl</string>
                                      </path>
                                      <parameter name="outputRepository">
                                        <variable name="wpsOutputFolder"/>
                                      </parameter>
                                    </xslt>
                                  </setVariable>
                                  <dumpXML>
                                    <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                    <variable name="outputControlScript"/>
                                </dumpXML>
                                 <loadProcedure name="OutputControlProcedure">
                                  <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                </loadProcedure>
                                <call procedure="OutputControlProcedure">
                                    <argument name="outputsControl">
                                      <string/>
                                    </argument>
                                    <argument name="outputsException">
                                      <string/>
                                    </argument>
                                    <argument name="outputsExceptionMessage">
                                      <string/>
                                    </argument>
                                </call>
                                  <!-- End Output Control-->
                              <setVariable name="statusExecuteResponse">
                                <!-- Generate Execute Status Response -->
                                <xslt xmlOutput="true">
                                  <variable name="processRequest"/>
                                  <path startFrom="SERVICE_RESOURCE_DIR">
                                    <string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string>
                                  </path>
                                  <parameter name="processName">
                                    <variable name="executeProcessName"/>
                                  </parameter>
                                  <parameter name="status">
                                    <variable name="outputsControl"/>
                                  </parameter>
                                  <parameter name="statusMessage">
                                    <string/>
                                  </parameter>
                                  <parameter name="statusLocation">
                                    <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=${wpsStatusRelativePath}executeResponseStatus.xml</string>
                                  </parameter>
                                  <parameter name="creationTime">
                                    <dateStringFormatter format="ddMMyyyyHHmmssSSS">
                                      <now/>
                                    </dateStringFormatter>
                                  </parameter>
                                  <parameter name="outputHttpRequest">
                                    <variable name="toolboxGetServiceResourceURL"/>
                                  </parameter>
                                  <parameter name="particularReferenceID">
                                    <variable name="particularReferenceIdentifier"/>
                                  </parameter>
                                  <parameter name="particularReferenceURL">
                                    <variable name="particularReferenceURL"/>
                                  </parameter>
                                  <parameter name="outputFolder">
                                    <variable name="wpsOutputFolder"/>
                                  </parameter>
                                  <parameter name="originalRequest">
                                    <xmlRequest/>
                                  </parameter>
                                   <parameter name="statusExceptionCode">
                                    <variable name="outputsException"/>
                                  </parameter>
                                  <parameter name="statusMessage">
                                    <variable name="outputsExceptionMessage"/>
                                  </parameter> 
                                </xslt>
                                <!-- End Generate Execute Status Response-->
                              </setVariable>
                            </sequence>
                            <sequence>
                              <setVariable name="statusExecuteResponse">
                                <!-- Generate Status Execute Response Error -->
                                <xslt xmlOutput="true">
                                  <variable name="processRequest"/>
                                  <path startFrom="SERVICE_RESOURCE_DIR">
                                    <string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string>
                                  </path>
                                  <parameter name="processName">
                                    <variable name="executeProcessName"/>
                                  </parameter>
                                  <parameter name="status">
                                    <string>ProcessFailed</string>
                                  </parameter>
                                  <parameter name="statusMessage">
                                    <string>${ErrorType}: ${ErrorMessage}</string>
                                  </parameter>
                                  <parameter name="statusLocation">
                                    <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=${wpsStatusRelativePath}executeResponseStatus.xml</string>
                                  </parameter>
                                  <parameter name="creationTime">
                                    <dateStringFormatter format="ddMMyyyyHHmmssSSS">
                                      <now/>
                                    </dateStringFormatter>
                                  </parameter>
                                  <parameter name="outputHttpRequest">
                                    <variable name="toolboxGetServiceResourceURL"/>
                                  </parameter>
                                  <parameter name="particularReferenceID">
                                    <variable name="particularReferenceIdentifier"/>
                                  </parameter>
                                  <parameter name="particularReferenceURL">
                                    <variable name="particularReferenceURL"/>
                                  </parameter>
                                  <parameter name="outputFolder">
                                    <variable name="wpsOutputFolder"/>
                                  </parameter>
                                  <parameter name="originalRequest">
                                    <xmlRequest/>
                                  </parameter>
                                </xslt>
                                <!-- End Generate Status Execute Response Error-->
                              </setVariable>
                            </sequence>
                          </if>
                          <dumpXML>
                            <string>${wpsStatusFolder}executeResponseStatus.xml</string>
                            <variable name="statusExecuteResponse"/>
                          </dumpXML>
                        </sequence>
                      </fork>
                      <!-- Generate Execute Response -->
                      <setVariable name="statusExecuteResponse">
                        <xslt xmlOutput="true">
                          <variable name="processRequest"/>
                          <path startFrom="SERVICE_RESOURCE_DIR">
                            <string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string>
                          </path>
                          <parameter name="processName">
                            <variable name="executeProcessName"/>
                          </parameter>
                          <parameter name="status">
                            <variable name="statusVariable"/>
                          </parameter>
                          <parameter name="statusMessage">
                            <string/>
                          </parameter>
                          <parameter name="statusLocation">
                            <variable name="statusLocationVariable"/>
                          </parameter>
                          <parameter name="creationTime">
                            <dateStringFormatter format="yyyy-MM-dd'T'HH:mm:ss'Z'">
                              <now/>
                            </dateStringFormatter>
                          </parameter>
                          <parameter name="outputHttpRequest">
                            <variable name="toolboxGetServiceResourceURL"/>
                          </parameter>
                          <parameter name="particularReferenceID">
                            <variable name="particularReferenceIdentifier"/>
                          </parameter>
                          <parameter name="particularReferenceURL">
                            <variable name="particularReferenceURL"/>
                          </parameter>
                          <parameter name="outputFolder">
                            <variable name="wpsOutputFolder"/>
                          </parameter>
                          <parameter name="originalRequest">
                            <xmlRequest/>
                          </parameter>
                        </xslt>
                      </setVariable>
                      <dumpXML>
                        <string>${wpsStatusFolder}executeResponseStatus.xml</string>
                        <variable name="statusExecuteResponse"/>
                      </dumpXML>
                      <!-- End Response-->
                      <variable name="statusExecuteResponse"/>
                    </sequence>
                    <sequence>
                      <!-- Synchronous-->
                      <!-- Generate Grass enviorement Shell Script /-->
                      <setVariable name="jobScript">
                        <xslt xmlOutput="false">
                          <variable name="processRequest"/>
                          <path startFrom="SERVICE_RESOURCE_DIR">
                            <string>../AdditionalResources/WPS/XSL/Execute_Grass_Enviorement.xsl</string>
                          </path>
                          <parameter name="retrivedDataUrls">
                            <variable name="LocalURIReferenecesList"/>
                          </parameter>
                          <parameter name="instanceValue">
                            <variable name="wpsProcessingInstance"/>
                          </parameter>
                          <parameter name="statusFileInstance">
                            <variable name="wpsStatusPercentageFile"/>
                          </parameter>
                          <parameter name="inputNameDataUrls">
                              <variable name="AttributeReferenecesList"/>
                          </parameter>
                          <parameter name="processingName">
                            <variable name="executeProcessName"/>
                          </parameter>
                          <parameter name="grassOutputFolder">
                            <variable name="wpsOutputFolder"/>
                          </parameter>
                          <parameter name="grassTempFolder">
                            <variable name="wpsTempFolder"/>
                          </parameter>
                          <parameter name="downloadFolder">
                                <variable name="wpsDownloadedInputFolder"/>
                          </parameter>
                          <parameter name="grassScriptPath">
                            <path startFrom="SERVICE_RESOURCE_DIR">
                              <string>GrassScripts/</string>
                            </path>
                          </parameter>
                          <parameter name="grassLogFileInstance">
                            <string>${wpsLogFolder}/${wpsProcessingInstance}/GrassLog.txt</string>
                          </parameter>
                          <parameter name="wpsWorkspace">
                            <path startFrom="SERVICE_RESOURCE_DIR">
                              <string>../AdditionalResources/WPS/GrassWorkspace/</string>
                            </path>
                          </parameter>
                          <parameter name="epsgGrassLocation">
                            <variable name="defaultEPSG"/>
                          </parameter>
                          <parameter name="grassPath">
                            <variable name="grassPath"/>
                          </parameter>
                        </xslt>
                      </setVariable>
			<setVariable name="grassEnviorementFile">
				<path startFrom="SERVICE_RESOURCE_DIR">
		                    <string>../AdditionalResources/WPS/shellScript.sh</string>
		                  </path>
			</setVariable>
                      <dumpFile>
                        <path>
                          <variable name="grassEnviorementFile"/>
                        </path>
                        <variable name="jobScript"/>
                      </dumpFile>
                      <command>
                        <string>chmod +x ${grassEnviorementFile}</string>
                      </command>
                      <command asynchronous="false">
                        <variable name="grassEnviorementFile"/>
                      </command>
                      <!-- End Grass Section-->
                      <!-- Generate Output Manager Toolbox script-->
                      <setVariable name="outputManagerScript">
                        <xslt xmlOutput="true">
                          <variable name="processRequest"/>
                          <path startFrom="SERVICE_RESOURCE_DIR">
                            <string>../AdditionalResources/WPS/XSL/ExecuteOutputManager.xsl</string>
                          </path>
                          <parameter name="outputRepository">
                            <variable name="wpsOutputFolder"/>
                          </parameter>
                          <parameter name="processName">
                            <variable name="executeProcessName"/>
                          </parameter>
                        </xslt>
                      </setVariable>
                      <dumpXML>
                        <string>${wpsScriptFolder}outputManagerScript.tscript</string>
                        <variable name="outputManagerScript"/>
                      </dumpXML>
                      <loadProcedure name="OutputManagerProcedure">
                        <string>${wpsScriptFolder}outputManagerScript.tscript</string>
                      </loadProcedure>
                      <call procedure="OutputManagerProcedure">
                        <argument name="ErrorType">
                          <string/>
                        </argument>
                        <argument name="ErrorMessage">
                          <string/>
                        </argument>
                        <argument name="LiteralOutputElement">
                          <string/>
                        </argument>
                        <argument name="BoundingBoxOutputElement">
                          <string/>
                        </argument>
                      </call>
                      <!-- End Output Manager -->
                      <if>
                        <oeq>
                          <variable name="ErrorType"/> <!-- Output Manager Error Control-->
                          <string/>
                        </oeq>
                        <sequence>
                          <!-- Output Control -->
                                  <setVariable name="outputControlScript">
                                    <xslt xmlOutput="true">
                                      <variable name="processRequest"/>
                                      <path startFrom="SERVICE_RESOURCE_DIR">
                                        <string>../AdditionalResources/WPS/XSL/ExecuteOutputControl.xsl</string>
                                      </path>
                                      <parameter name="outputRepository">
                                        <variable name="wpsOutputFolder"/>
                                      </parameter>
                                    </xslt>
                                  </setVariable>
                                  <dumpXML>
                                    <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                    <variable name="outputControlScript"/>
                                </dumpXML>
                                 <loadProcedure name="OutputControlProcedure">
                                  <string>${wpsScriptFolder}outputControlScript.tscript</string>
                                </loadProcedure>
                                <call procedure="OutputControlProcedure">
                                    <argument name="outputsControl">
                                      <string/>
                                    </argument>
                                    <argument name="outputsException">
                                      <string/>
                                    </argument>
                                    <argument name="outputsExceptionMessage">
                                      <string/>
                                    </argument>
                                </call>
                                  <!-- End Output Control-->


                          <!-- Generate Execute Response -->
                          <xslt xmlOutput="true">
                            <variable name="processRequest"/>
                            <path startFrom="SERVICE_RESOURCE_DIR">
                              <string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string>
                            </path>
                            <parameter name="processName">
                              <variable name="executeProcessName"/>
                            </parameter>
                            <parameter name="status">
                              <variable name="outputsControl"/>
                            </parameter>
                            <parameter name="statusMessage">
                              <string/>
                            </parameter>
                            <parameter name="statusLocation">
                              <variable name="statusLocationVariable"/>
                            </parameter>
                            <parameter name="creationTime">
                              <dateStringFormatter format="yyyy-MM-dd'T'HH:mm:ss'Z'">
                                <now/>
                              </dateStringFormatter>
                            </parameter>
                            <parameter name="outputHttpRequest">
                              <variable name="toolboxGetServiceResourceURL"/>
                            </parameter>
                            <parameter name="particularReferenceID">
                              <variable name="particularReferenceIdentifier"/>
                            </parameter>
                            <parameter name="particularReferenceURL">
                              <variable name="particularReferenceURL"/>
                            </parameter>
                            <parameter name="outputFolder">
                              <variable name="wpsOutputFolder"/>
                            </parameter>
                            <parameter name="originalRequest">
                              <xmlRequest/>
                            </parameter>
                            <parameter name="statusExceptionCode">
                                <variable name="outputsException"/>
                            </parameter>
                            <parameter name="statusMessage">
                                <variable name="outputsExceptionMessage"/>
                              </parameter>   
                          </xslt>
                          <!-- End Response-->
                        </sequence>
                        <sequence>
                          <xslt xmlOutput="true">
                            <variable name="processRequest"/>
                            <path startFrom="SERVICE_RESOURCE_DIR">
                              <string>../AdditionalResources/WPS/XSL/WPSExceptions.xsl</string>
                            </path>
                            <parameter name="exceptionCode">
                              <variable name="ErrorType"/>
                            </parameter>
                            <parameter name="exceptionText">
                              <variable name="ErrorMessage"/>
                            </parameter>
                          </xslt>
                        </sequence>
                      </if>
                    </sequence>
                  </if>
                </sequence>
                <sequence>
                  <xslt xmlOutput="true">
                    <variable name="processRequest"/>
                    <path startFrom="SERVICE_RESOURCE_DIR">
                      <string>../AdditionalResources/WPS/XSL/WPSExceptions.xsl</string>
                    </path>
                    <parameter name="exceptionCode">
                      <variable name="ErrorType"/>
                    </parameter>
                    <parameter name="exceptionText">
                      <variable name="ErrorMessage"/>
                    </parameter>
                  </xslt>
                </sequence>
              </if>
            </sequence>
            <sequence>
              <xslt xmlOutput="true">
                <variable name="processRequest"/>
                <path startFrom="SERVICE_RESOURCE_DIR">
                  <string>../AdditionalResources/WPS/XSL/WPSExceptions.xsl</string>
                </path>
                <parameter name="exceptionCode">
                  <string>InvalidParameterValue</string>
                </parameter>
                <parameter name="exceptionText">
                  <string>dafault EPSG code: ${defaultEPSG} not supported.</string>
                </parameter>
              </xslt>
            </sequence>
          </if>
        </sequence>
        <sequence>
          <xslt xmlOutput="true">
            <variable name="processRequest"/>
            <path startFrom="SERVICE_RESOURCE_DIR">
              <string>../AdditionalResources/WPS/XSL/WPSExceptions.xsl</string>
            </path>
            <parameter name="exceptionCode">
              <xPath>
                <variable name="controlExecuteInput"/>
                <string>ControlResult/exceptionCode</string>
              </xPath>
            </parameter>
            <parameter name="exceptionText">
              <xPath>
                <variable name="controlExecuteInput"/>
                <string>ControlResult/exceptionMessage</string>
              </xPath>
            </parameter>
          </xslt>
        </sequence>
        <!-- End Contol Input -->
      </if>
    </sequence>
  </xsl:template>
</xsl:stylesheet>
