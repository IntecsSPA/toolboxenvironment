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
      <setVariable name="SoapActionUpdateState">
          <string><xsl:value-of select="local-name()"/>_ASYNC_statusUpdate</string>
        </setVariable>
      <setVariable name="ExecuteErrorMessage">
        <null/>
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
          <setVariable name="status">
            <string><xsl:value-of select="./@status"/></string>
          </setVariable>
          <setVariable name="storeExecuteResponse">
            <string><xsl:value-of select="./@storeExecuteResponse"/></string>
          </setVariable>
          <setVariable name="lineage">
            <string><xsl:value-of select="./@lineage"/></string>
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
		        <string>ProcessAccepted</string>
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
                        <string>ProcessAccepted</string>
                      </setVariable-->
                     <setVariable name="statusLocationVariable">
                        <string>${toolboxURL}/manager?cmd=getServiceResource&amp;serviceName=${serviceName}&amp;relativePath=${wpsStatusRelativePath}executeResponseStatus.xml</string>
                      </setVariable>
                     

                <setVariable name="statusExecuteResponse">
		                  <!-- Generate Execute Response -->
		                  <xslt xmlOutput="true">
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
		                    <!--parameter name="outputLiteralData">
		                      <variable name="LiteralOutputElement"/>
		                    </parameter>
		                    <parameter name="outputBoundingBoxData">
		                      <variable name="BoundingBoxOutputElement"/>
		                    </parameter-->
  		                  <parameter name="outputFolder">
                            <variable name="wpsOutputFolder"/>
                        </parameter>  
		                    <parameter name="originalRequest">
		                      <xmlRequest/>
		                    </parameter>
                        <parameter name="serviceName">
                            <variable name="serviceName"/>
                           </parameter>
                          <parameter name="dateTime">
                            <variable name="wpsProcessingInstance"/>
                          </parameter>
                          <parameter name="geoserverOWSURL">
                            <geoserverOWSUrl xmlns="http://pisa007.pisa.intecs.it/toolbox/GISPlugin"/>
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
