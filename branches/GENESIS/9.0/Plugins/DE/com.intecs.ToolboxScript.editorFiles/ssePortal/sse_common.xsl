<?xml version="1.0" encoding="UTF-8"?>
<!--
    File		:	mass.xsl
    File Type	:	XML stylesheet
    Abstract	:	XML standard stylesheet used to process service information contained in the WSDl and XML instance file.
    Uses		:	An XML instance based on MASS standard schema (mass.xsd) as input
    History	:	v1.0 - 19/11/01 - T. Nguyen Minh - Created.
    				v1.1 - 26/11/01 - T. Nguyen Minh - Remove UserData and templates for service specific information.
    				v1.2 - 05/12/01 - T. Nguyen Minh - Remove commonInput template ("HTML" mode).
    				v1.3 - 13/12/01 - T. Nguyen Minh - Add a new parameter (workflowName) used to set the root element, and add 2 new parts used to handle output information in XML mode.
				v1.4 - 02/01/02 - T. Nguyen Minh - Add a new parameter (subscriptionFlag) used to distinguish a single order and a subscription
				v1.5 - 14/01/02 - T. Nguyen Minh - Add a node of sendRFQInput to order input XML document. This is useful for the processing of single order / subscription at the workflow engine.
				v1.6 - 11/03/02 - Dung Nguyen Huu - modify template match='/*' for matching any elements of xml instances
                v1.7 -  8/07/03 - M. Gilles - updated for Multi RFQ and view file result
                v1.8 - 4/9/03 - G. Gilles - Add displayFilter XSL parameter
                v2.0 - New version of AOI schema for MASS-ENV
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:mass="http://www.esa.int/mass"
xmlns:srs="http://www.esa.int/xml/schemas/mass/serviceresult" xmlns:eoli="http://earth.esa.int/XML/eoli" >
	<xsl:output omit-xml-declaration="yes"/>
	<!-- Parameter used to specify which part of this style sheet will be applied -->
	<xsl:param name="part"/>
    <!-- Parameter used to filter display when the same "part" of stylesheet is used on different page :
          e.g for part getRFQOutputHTML, all RFQ results are displayed in the Order Preparation while
          a single RFQ result is displayed in the Order Result page. -->
	<xsl:param name="displayFilter"/>
	<!-- Template application -->
	
	 <xsl:template name="mass:getRFQOutput">
      
	       <table border="0" cellspacing="0"  width="100%" cellpadding="0">
	      <xsl:choose>
		     <xsl:when test="//mass:statusInfo/mass:statusId='0'">
			
			       
                <!-- display of orderPreparation Page -->
                 <xsl:choose><xsl:when test="$displayFilter='ALL'">
                    <xsl:apply-templates select="//mass:viewFileResult" /> 
					 <xsl:apply-templates select="//mass:viewEmbeddedResult" />
                     <tr>
                     <td width="100%" align="left">
                         <IFRAME  name="frameOperationResult"  align="left" frameborder="1" marginwidth="0" width="100%" height="200" scrolling="auto" >
                                [Sorry you browser does not support iframe]
                         </IFRAME>
                         <script type="text/javascript" LANGUAGE="JavaScript" >
                            displayRFQOutput(document.formRFQOutput); /* load the multi RFQ result in frameOperationResult*/ 
                         </script>
                     </td>
                     </tr>
                   </xsl:when>
				   
				    <xsl:when test="$displayFilter='ALLRESULTS'">
				    <xsl:apply-templates select="//mass:viewFileResult" /> 
					<xsl:apply-templates select="//mass:viewEmbeddedResult" />
                    <!-- display the MULTI RFQ in the frameOperationResult frame -->
                    <table  border="1"  cellspacing="0" cellpadding="0" width="100%" class="lghtbloc">
                      <tr>
                         <td  class="stylesheetHeader">
                            Id
			             </td>
                         <xsl:call-template name="mass:rfqOutputHeader" />
                         </tr>
                        <xsl:for-each select="//child::*">
						 <xsl:if test="local-name()='rfqOutput' ">
						 
						
			             <tr>
                          <td width="6%" class="stylesheetText">
                         <xsl:choose>
                           <xsl:when test="./mass:id !=''">
                              <script LANGUAGE="JavaScript" >
                                  writeIdRFQSelectionRadioButton('<xsl:value-of select="./mass:id"/>');
                              </script>
                           </xsl:when>
                           <xsl:otherwise>
                              <script LANGUAGE="JavaScript" >
                                  writeRFQSelectionRadioButton();
                              </script>
                           </xsl:otherwise>
                        </xsl:choose>
                             </td>
			              <!-- apply the service provider stylesheet
                                for the formatting of the rfq output -->
                             <xsl:apply-templates select="." />
	                   </tr>
					   </xsl:if>
                      </xsl:for-each>
                      </table>
                     </xsl:when>
					 
					   <xsl:otherwise>
                    <!-- display the selected RFQ output in the order Result Page
                    after the ordering only the selected rfqOutput is stored in the DB -->
                    <tr><td>
                    	<xsl:apply-templates select="//mass:viewFileResult" /> 
					 	<xsl:apply-templates select="//mass:viewEmbeddedResult" />
                        <table border="1"  cellspacing="0" cellpadding="3" width="100%">
		                <tr>
                        <xsl:call-template name="mass:rfqOutputHeader" />
                        </tr>
                          <xsl:for-each select="//child::*">
						 <xsl:if test="local-name()='rfqOutput' ">
			            <tr>
			              <!-- apply the service provider stylesheet
                                for the formatting of the rfq output -->
                             <xsl:apply-templates select="." />
			             <!--</td> -->
	                    </tr>
						</xsl:if> 
                        </xsl:for-each>
                        </table>
                    </td></tr>
                    </xsl:otherwise>
				   
					 </xsl:choose> 
			 
			 
              
		     </xsl:when>
	      <xsl:otherwise>
		<!-- the error case or bad result-->
		 <tr>
		   <td width="40%" align="left">
		      <font class="stylesheetBoldText">RFQ Status :</font>
		   </td>
		   <td width="60%" align="left">
                        <font class="stylesheetText">
			<xsl:value-of select="//mass:statusInfo/mass:statusMsg"/>
                        </font>
		   </td>
		</tr>
		</xsl:otherwise>
	    </xsl:choose>
	</table>
        
	
	</xsl:template>
	<!-- default template -->
<xsl:template name="mass:rfqOutputHeader">
	</xsl:template>
    <!--     ViewFileResult  -->
     <xsl:template match="mass:viewFileResult">
        <!-- template used to generate the jaavscript/html code needed
             to call the file viewer -->
             <div name="renderingURL" id="renderingURL">
         		<script LANGUAGE="JavaScript" >
            		tbxTCenter.sseRenderingReference('<xsl:value-of select="mass:fileURL"/>', '<xsl:value-of select="mass:fileType"/>');
         		</script>
         	</div>
    </xsl:template>
<!--     ViewFileResult  -->
     <xsl:template match="mass:viewEmbeddedResult">
         <!-- template used to generate the jaavscript/html code needed
             to call the file viewer -->
              <div name="renderingEmbedded" id="renderingEmbedded">
		       <script LANGUAGE="JavaScript" >
			   tbxTCenter.sseRenderingValue('<xsl:copy-of select="./mass:embeddedResult/*"/>','<xsl:value-of select="mass:embeddedType"/>');
			   </script>
         </div>
    </xsl:template>

	
</xsl:stylesheet>
