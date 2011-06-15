<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:param name="toolboxURL">http://localhost:8080/TOOLBOX/WSDL/</xsl:param>
  <xsl:template match="/wps:DescribeProcess">
  	
  	<xsl:variable name="error">
	  	<xsl:for-each select="ows:Identifier">
	  		<xsl:if test="not(document(string(concat('../DescribeProcess/DescribeInformation_', ., '.xml'))))">
	  			<xsl:text>error,</xsl:text>
	  		</xsl:if>
	  	</xsl:for-each>
	</xsl:variable>  	
  
  	<xsl:choose>
  		<xsl:when test="$error = '' ">
  			<wps:ProcessDescriptions xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="WPS" version="1.0.0" xml:lang="en">
      			<xsl:apply-templates select="ows:Identifier"/>
    		</wps:ProcessDescriptions>
  		</xsl:when>
    	<xsl:otherwise>
    		<ows:ExceptionReport xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" xml:lang="en">
		      <ows:Exception exceptionCode="InvalidParameterValue">
		        <ows:ExceptionText>Invalid Process Identifier</ows:ExceptionText>
		      </ows:Exception>
		    </ows:ExceptionReport>
    	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="ows:Identifier">
     <xsl:variable name="processInformation" select="document(string(concat('../DescribeProcess/DescribeInformation_', ., '.xml')))"/>
      <ProcessDescription>
	      <xsl:attribute name="wps:processVersion">
	        <xsl:value-of select="$processInformation/ProcessDescription/@wps:processVersion"/>
	      </xsl:attribute>
	      <xsl:attribute name="storeSupported">
	        <xsl:value-of select="$processInformation/ProcessDescription/@storeSupported"/>
	      </xsl:attribute>
	      <xsl:attribute name="statusSupported">
	        <xsl:value-of select="$processInformation/ProcessDescription/@statusSupported"/>
	      </xsl:attribute>
      
      	  <xsl:copy-of select="$processInformation/ProcessDescription/ows:Identifier"/>
      	  <xsl:copy-of select="$processInformation/ProcessDescription/ows:Title"/>
	      <xsl:if test="$processInformation/ProcessDescription/ows:Abstract">
	        <xsl:copy-of select="$processInformation/ProcessDescription/ows:Abstract"/>
	      </xsl:if>
	      
      <xsl:for-each select="$processInformation/ProcessDescription/ows:Metadata">
        <!-- <xsl:copy-of select="."/>  -->
        <ows:Metadata>
        	<xsl:attribute name="xlink:title">
	        	<xsl:value-of select="$processInformation/ProcessDescription/ows:Metadata/@xlink:title"/>
	      	</xsl:attribute>
	      	<xsl:attribute name="xlink:type"><xsl:text>simple</xsl:text></xsl:attribute>
        </ows:Metadata>
      </xsl:for-each>
      
	      <xsl:if test="$processInformation/ProcessDescription/wps:Profile">
	        <xsl:copy-of select="$processInformation/ProcessDescription/wps:Profile"/>
	      </xsl:if>
	      <xsl:if test="$processInformation/ProcessDescription/wps:WSDL">
	        <xsl:copy-of select="$processInformation/ProcessDescription/wps:WSDL"/>
	      </xsl:if>
	      <xsl:if test="$processInformation/ProcessDescription/DataInputs">
	        <!-- xsl:copy-of select="$processInformation/ProcessDescription/DataInputs"/-->
	        <DataInputs>
		      <xsl:for-each select="$processInformation/ProcessDescription/DataInputs/Input">
		        <Input minOccurs="{./@minOccurs}" maxOccurs="{./@maxOccurs}">
		          <ows:Identifier>
		            <xsl:value-of select="ows:Identifier/text()"/>
		          </ows:Identifier>
		          <ows:Title>
		            <xsl:value-of select="ows:Title/text()"/>
		          </ows:Title>
		          <ows:Abstract>
		            <xsl:value-of select="ows:Abstract/text()"/>
		          </ows:Abstract>
		          <xsl:if test="ComplexData">
		            <xsl:copy-of select="ComplexData"/>
		          </xsl:if>
		          <xsl:if test="LiteralData">
		          	<LiteralData>
			            <xsl:if test="LiteralData/ows:DataType">
			            	<xsl:copy-of select="LiteralData/ows:DataType"/>
			            </xsl:if>
			            <xsl:if test="LiteralData/UOMs">
			            	<xsl:copy-of select="LiteralData/UOMs"/>
			            </xsl:if>
			            <xsl:if test="LiteralData/ows:AnyValue">
			            	<xsl:copy-of select="LiteralData/ows:AnyValue"/>
			            </xsl:if>
			            <xsl:if test="LiteralData/ows:AllowedValues">
			            	<xsl:copy-of select="LiteralData/ows:AllowedValues"/>
			            </xsl:if>
			           
			        </LiteralData>    
		          </xsl:if>
		          <xsl:if test="BoundingBoxData">
		            <xsl:copy-of select="BoundingBoxData"/>
		          </xsl:if>
		        </Input>
		      </xsl:for-each>
    		</DataInputs>
	      </xsl:if>
	      <xsl:if test="$processInformation/ProcessDescription/ProcessOutputs">
	        <xsl:copy-of select="$processInformation/ProcessDescription/ProcessOutputs"/>
	      </xsl:if>
    </ProcessDescription>
  </xsl:template>
  
</xsl:stylesheet>
