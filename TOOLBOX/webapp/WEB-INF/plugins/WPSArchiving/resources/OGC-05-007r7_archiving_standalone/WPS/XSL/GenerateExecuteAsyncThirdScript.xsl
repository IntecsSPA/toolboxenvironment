<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml"/>
  <xsl:template match="*">
    <sequence xmlns="http://pisa.intecs.it/mass/toolbox/xmlScript">
      <setVariable name="executeProcessName">
        <string>
          <xsl:value-of select="substring-after(local-name(),'_')"/>
        </string>
      </setVariable>
      <setVariable name="ExecuteErrorMessage">
        <string></string>
      </setVariable>
      <setVariable name="OutputFileName">
        <string></string>
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
		      <setVariable name="wpsDownloadedInputFolder">
		        <string>${wpsLogFolder}/${wpsProcessingInstance}/downloaded/</string>
		      </setVariable>
		      <setVariable name="wpsOutputFolder">
		        <string>${wpsLogFolder}/${wpsProcessingInstance}/output/</string>
		      </setVariable>
		      <setVariable name="wpsStatusFolder">
		        <string>${wpsLogFolder}/${wpsProcessingInstance}/status/</string>
		      </setVariable>
		      <setVariable name="wpsTempFolder">
		        <string>${wpsLogFolder}/${wpsProcessingInstance}/temp/</string>
		      </setVariable>
		      <setVariable name="grassEnviorementFile">
		        <string>${wpsTempFolder}grassEnviorement.sh</string>
		      </setVariable>
		      <setVariable name="grassPath">
		        <string/>
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
                      <!--setVariable name="statusVariable">
                        <string>ProcessStarted</string>
                      </setVariable-->
                     <setVariable name="statusLocationVariable">
                      <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=Log/status/executeResponseStatus.xml</string>
                      </setVariable>
		                  <!-- Generate Execute Response -->
		                  <!--xslt xmlOutput="true">
		                    <variable name="processRequest"/>
		                    <path startFrom="SERVICE_RESOURCE_DIR">
		                      <string>../AdditionalResources/WPS/XSL/GenerateExecuteResponse.xsl</string>
		                    </path>
		                    <parameter name="statusEnable">
		                      <variable name="status"/>
		                    </parameter>
		                    <parameter name="lineage">
		                      <variable name="lineage"/>
		                    </parameter>
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
		                    <parameter name="outputLiteralData">
		                      <variable name="LiteralOutputElement"/>
		                    </parameter>
		                    <parameter name="outputBoundingBoxData">
		                      <variable name="BoundingBoxOutputElement"/>
		                    </parameter>
		                    <parameter name="originalRequest">
		                      <xmlRequest/>
		                    </parameter>
		                  </xslt-->
		                  <!-- End Response-->


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
              <setVariable name="ExecuteErrorMessage">
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
              </setVariable>
		        </sequence>
		      </if>
		</sequence>  
		<sequence>
      <setVariable name="ExecuteErrorMessage">
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
  		</setVariable>
    </sequence>
  		<!-- End Contol Input -->
	  </if>       
	</sequence>		  
  </xsl:template>
</xsl:stylesheet>
