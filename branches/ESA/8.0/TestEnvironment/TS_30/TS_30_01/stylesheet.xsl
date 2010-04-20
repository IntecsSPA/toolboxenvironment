<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    File		:       ServiceStylesheetTemplate.xsl
    File Type	:	SSE Service stylesheet  template
    Abstract	:	SSE stylesheet template that can be used  as a basis to be customized  when a new service is registered on the SSE
    Uses		:	sse_common.xsl, massCatalogue.xsl
    Used by	:	SSE Service Providers
    
        History:

     $Log: ServiceStylesheetTemplate.xsl,v $
     Revision 1.1  2007/03/23 12:21:58  mgs
     First Issue

     

     End of history.
  -->
<!--
Replace  http://www.mycompanyname.com/ws/mynamespace by your service namespace
-->
<xsl:stylesheet version="1.0" xmlns:sns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:mass="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:oi="http://www.esa.int/oi" xmlns:gml="http://www.opengis.net/gml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- *********************************************************
imported stylesheets
********************************************************* -->
	<xsl:import href="./sse_common.xsl"/>
	<xsl:import href="./massCatalogue.xsl"/>
	<!-- *********************************************************
Parameter used to specify which part of this style sheet will be applied
********************************************************* -->
	<xsl:param name="part">sendRFQInputXML</xsl:param>
	<!-- *********************************************************
Dispatching to the requested template based on the required operation
and on step e.g. display html input, preparing xml message for the workflow or
formating operation results into html format
********************************************************* -->
	<xsl:template match="/*">
		<xsl:choose>
			<!-- present operation templates -->
			<!-- present xml -->
			<xsl:when test="$part='processPresentInputXML'">
				<mass:processPresentInputMsg xmlns:mass="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:eoli="http://earth.esa.int/XML/eoli" xmlns:oi="http://www.esa.int/oi">
					<xsl:apply-templates select="mass:sendPresentInput" mode="XML"/>
				</mass:processPresentInputMsg>
			</xsl:when>
			<!-- present output html -->
			<xsl:when test="$part='getPresentOutputHTML'">
				<xsl:call-template name="mass:processPresentOutputMsg"/>
			</xsl:when>
			<!-- search operation templates -->
			<!-- search input html -->
			<xsl:when test="$part='sendSearchInputHTML'">
				<xsl:apply-templates select="mass:sendSearchInput"/>
			</xsl:when>
			<!-- search input xml -->
			<xsl:when test="$part='processSearchInputXML'">
				<mass:multiCataloguesSearchInputMsg xmlns:mass="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:oi="http://www.esa.int/oi" xmlns:eoli="http://earth.esa.int/XML/eoli">
					<xsl:apply-templates select="mass:sendSearchInput" mode="XML"/>
				</mass:multiCataloguesSearchInputMsg>
			</xsl:when>
			<!-- search output html -->
			<xsl:when test="$part='getSearchOutputHTML'">
				<xsl:call-template name="mass:multiCataloguesSearchOutputMsg"/>
			</xsl:when>
			<!-- RFQ operation templates -->
			<!-- RFQ input html -->
			<xsl:when test="$part='sendRFQInputHTML'">
				<xsl:apply-templates select="mass:sendRFQInput"/>
			</xsl:when>
			<!--RFQ input xml: synchronous operation -->
			<xsl:when test="$part='processRFQInputXML'">
				<processRFQInputMsg xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:sns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:mass="http://www.esa.int/mass">
					<xsl:apply-templates select="mass:sendRFQInput" mode="XML"/>
				</processRFQInputMsg>
			</xsl:when>
			<!-- RFQ input xml: asynchronous operation-->
			<xsl:when test="$part='sendRFQInputXML'">
				<sendRFQInputMsg xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:sns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:mass="http://www.esa.int/mass">
					<xsl:apply-templates select="mass:sendRFQInput" mode="XML"/>
				</sendRFQInputMsg>
			</xsl:when>
			<!-- RFQ output html -->
			<xsl:when test="$part='getRFQOutputHTML'">
				<!--a common template is used which must be independent of the service namespace
Thats why apply-templates is replace by call-template and
mass namespace is used. -->
				<xsl:call-template name="mass:getRFQOutput"/>
			</xsl:when>
			<!-- order operation templates -->
			<!-- sendorderInput html -->
			<xsl:when test="$part='sendOrderInputHTML'">
				<xsl:apply-templates select="mass:sendOrderInput"/>
			</xsl:when>
			<!-- sendorderInput xml in case asynchronous order-->
			<xsl:when test="$part='sendOrderInputXML'">
				<sendOrderInputMsg xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:sns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:mass="http://www.esa.int/mass" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:oi="http://www.esa.int/oi">
					<xsl:apply-templates select="mass:sendOrderInput" mode="XML"/>
				</sendOrderInputMsg>
			</xsl:when>
			<!-- sendorderInput xml in case synchronous order-->
			<xsl:when test="$part='processOrderInputXML'">
				<sns:processOrderInputMsg xmlns:sns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer" xmlns:aoi="http://www.esa.int/xml/schemas/mass/aoifeatures" xmlns:mass="http://www.esa.int/mass" xmlns:oi="http://www.esa.int/oi">
					<xsl:apply-templates select="mass:sendOrderInput" mode="XML"/>
				</sns:processOrderInputMsg>
			</xsl:when>
			<!-- getOrderOutput html -->
			<xsl:when test="$part='getOrderOutputHTML'">
				<!--service namespace is used -->
				<xsl:apply-templates select="sns:getOrderOutput"/>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>
	<!-- *********************************************************
Templates specific for each operations
********************************************************* -->
	<!--Search operation -->
	<!--Search input html -->
	<!-- Templates used to display Search input information using HTML format -->
	<xsl:template match="mass:sendSearchInput">
		<!-- JavaScript used to validate form fields -->
	</xsl:template>
	<!-- Template used to increase the number of products a user can select in the shopping basket for ordering.
If this template is not provided, the default is defined in massCatalogue.xsl as 1.
You can customize the number 1 to your need -->
	<xsl:template name="mass:getMaxNbrOfItemsTemplate">
		<script language="JavaScript">
function getMaxNumberOfSelectedItems() {return 1; /* 1 to be customized */}
</script>
	</xsl:template>
	<!--RFQ operation -->
	<!--RFQ input html -->
	<!-- Template for the RFQ input information using HTML as output format -->
	<xsl:template match="mass:sendRFQInput">
		<!-- JavaScript used to validate form fields -->
		<script type="text/javascript" language="javascript">
			<xsl:text disable-output-escaping="yes">
			function checkDuration() 
				{
						if(MASS.DURATION.value &lt; 1)
						{
							alert("Duration must be greater than 0");
							MASS.DURATION.value=1;
						}
					
						if(MASS.DURATION.value &gt; 72)
						{
							alert("Duration must be less than 72");
							MASS.DURATION.value=72;
						}
					return 1;
				}  
				
			function checkOptionsFields()
			{
				if(MASS.START_DATE.value == "" || MASS.START_TIME.value == "")
				 {
					alert("Start Date and Time must be set");
					return false;
				 }
				
				var startDate= str2dt2(MASS.START_DATE.value, MASS.START_TIME.value);
				
				if(startDate &lt; new Date())
				{
					alert("Data Date/Time shall be in the future");
					return false;
				}
				
				
				if(MASS.DELIVERY_DATE.value == "" || MASS.DELIVERY_TIME.value == "")
				 {
					return true;
				 }
				
				var deliveryDate= str2dt2(MASS.DELIVERY_DATE.value, MASS.DELIVERY_TIME.value);
				
				if(startDate &lt; deliveryDate)
				{
					alert("Delivery Date/Time shall be earlier than Start Date/time");
					return false;
				}
				
				if(deliveryDate &lt; new Date())
				{
					alert("Delivery Date/Time shall be in the future");
					return false;
				}
				
				return true;
			}
			
			function checkVariableSelection()
			{
				if(MASS.TEMPERATURE.checked ||
					MASS.TWO_METERS_TEMPERATURE.checked ||
					MASS.TEN_METERS_TEMPERATURE.checked ||
					MASS.RELATIVE_HUMIDITY.checked ||
					MASS.SURFACE_TEMPERATURE.checked ||
					MASS.SURFACE_WATER_TEMPERATURE.checked ||
					MASS.WIND.checked ||
					MASS.PRECIPITATION.checked ||
					MASS.PRESSURE.checked)
					{
						return true;
					}
					
					alert("At least one variable shall be selected");
					return false;
			}
			
			
function show_calendar(str_target, str_datetime) {
	var arr_months = ["January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"];
	var week_days = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];
	var n_weekstart = 1; // day week starts from (normally 0 or 1)

	var dt_datetime = (str_datetime == null || str_datetime =="" ?  new Date() : str2dt(str_datetime));
	var dt_prev_month = new Date(dt_datetime);
	dt_prev_month.setMonth(dt_datetime.getMonth()-1);
	var dt_next_month = new Date(dt_datetime);
	dt_next_month.setMonth(dt_datetime.getMonth()+1);
	var dt_firstday = new Date(dt_datetime);
	dt_firstday.setDate(1);
	dt_firstday.setDate(1-(7+dt_firstday.getDay()-n_weekstart)%7);
	var dt_lastday = new Date(dt_next_month);
	dt_lastday.setDate(0);

	var str_buffer = new String (
		"&lt;html&gt;\n"+
		"&lt;head&gt;\n"+
		"	&lt;title&gt;Calendar&lt;/title&gt;\n"+
		"&lt;/head&gt;\n"+
		"&lt;body bgcolor=\"White\"&gt;\n"+
		"&lt;table class=\"clsOTable\" cellspacing=\"0\" border=\"0\" width=\"100%\"&gt;\n"+
		"&lt;tr&gt;&lt;td bgcolor=\"#4682B4\"&gt;\n"+
		"&lt;table cellspacing=\"1\" cellpadding=\"3\" border=\"0\" width=\"100%\"&gt;\n"+
		"&lt;tr&gt;\n	&lt;td bgcolor=\"#4682B4\"&gt;&lt;a href=\"javascript:window.opener.show_calendar('"+
		str_target+"', '"+ dt2dtstr(dt_prev_month)+"');\"&gt;"+
		"&lt;&lt;&lt;/a&gt;&lt;/td&gt;\n"+
		"	&lt;td bgcolor=\"#4682B4\" colspan=\"5\"&gt;"+
		"&lt;font color=\"white\" face=\"tahoma, verdana\" size=\"2\"&gt;"
		+arr_months[dt_datetime.getMonth()]+" "+dt_datetime.getFullYear()+"&lt;/font&gt;&lt;/td&gt;\n"+
		"	&lt;td bgcolor=\"#4682B4\" align=\"right\"&gt;&lt;a href=\"javascript:window.opener.show_calendar('"
		+str_target+"', '"+dt2dtstr(dt_next_month)+"');\"&gt;"+
		"&gt;&gt;&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n"
	);

	var dt_current_day = new Date(dt_firstday);

	str_buffer += "&lt;tr&gt;\n";
	for (var n=0; n&lt;7; n++)
		str_buffer += "	&lt;td bgcolor=\"#87CEFA\"&gt;"+
		"&lt;font color=\"white\" face=\"tahoma, verdana\" size=\"2\"&gt;"+
		week_days[(n_weekstart+n)%7]+"&lt;/font&gt;&lt;/td&gt;\n";

	str_buffer += "&lt;/tr&gt;\n";
	while (dt_current_day.getMonth() == dt_datetime.getMonth() ||
		dt_current_day.getMonth() == dt_firstday.getMonth()) {
	
		str_buffer += "&lt;tr&gt;\n";
		for (var n_current_wday=0; n_current_wday&lt;7; n_current_wday++) {
				if (dt_current_day.getDate() == dt_datetime.getDate() &amp;&amp;
					dt_current_day.getMonth() == dt_datetime.getMonth())
				
					str_buffer += "	&lt;td bgcolor=\"#FFB6C1\" align=\"right\"&gt;";
				else if (dt_current_day.getDay() == 0 || dt_current_day.getDay() == 6)
					
					str_buffer += "	&lt;td bgcolor=\"#DBEAF5\" align=\"right\"&gt;";
				else
			
					str_buffer += "	&lt;td bgcolor=\"white\" align=\"right\"&gt;";

				if (dt_current_day.getMonth() == dt_datetime.getMonth())
			
					str_buffer += "&lt;a href=\"javascript:window.opener."+str_target+
					".value='"+dt2dtstr(dt_current_day)+"'; window.close();\"&gt;"+
					"&lt;font color=\"black\" face=\"tahoma, verdana\" size=\"2\"&gt;";
				else 
			
					str_buffer += "&lt;a href=\"javascript:window.opener."+str_target+
					".value='"+dt2dtstr(dt_current_day)+"'; window.close();\"&gt;"+
					"&lt;font color=\"gray\" face=\"tahoma, verdana\" size=\"2\"&gt;";
				str_buffer += dt_current_day.getDate()+"&lt;/font&gt;&lt;/a&gt;&lt;/td&gt;\n";
				dt_current_day.setDate(dt_current_day.getDate()+1);
		}
		// print row footer
		str_buffer += "&lt;/tr&gt;\n";
	}
	

	var vWinCal = window.open("", "Calendar", 
		"width=200,height=250,status=no,resizable=yes,top=200,left=200");
	vWinCal.opener = self;
	var calc_doc = vWinCal.document;
	calc_doc.write (str_buffer);
	calc_doc.close();
}

function str2dt (str_datetime) {
	var re_date = /^(\d+)\-(\d+)\-(\d+)$/;
	if (!re_date.exec(str_datetime))
		return alert("Invalid Datetime format: "+ str_datetime);
	return (new Date (RegExp.$1, RegExp.$2-1, RegExp.$3, 0, 0, 0));
}

function str2dt2 (str_date, str_time) {
	var re_date = /^(\d+)\-(\d+)\-(\d+)$/;
	if (!re_date.exec(str_date))
		return alert("Invalid Datetime format: "+ str_date);
		
	var year=	RegExp.$1;
	var month= RegExp.$2-1;
	var days= RegExp.$3;
	
	
	var re_time = /^(\d+).(\d+).(\d+)$/;
	if (!re_time.exec(str_time))
		return alert("Invalid time format: "+ str_time);
		
	var hours=RegExp.$1;
	var minutes=RegExp.$2;
	var seconds= RegExp.$3;
	
	return (new Date (year, month, days, hours, minutes, seconds));
}

function dt2dtstr (dt_datetime) {
	var ret_str=new String(dt_datetime.getFullYear()+"-");
	
	if(dt_datetime.getMonth() &lt; 9)
		ret_str+="0";
		
	ret_str+=(dt_datetime.getMonth()+1)+"-";
	
	if(dt_datetime.getDate() &lt; 10)
		ret_str+="0";
		
	ret_str+=dt_datetime.getDate();
	return ret_str;
			
}
function dt2tmstr (dt_datetime) {
	return (new String (
			dt_datetime.getHours()+":"+dt_datetime.getMinutes()+":"+dt_datetime.getSeconds()));
}</xsl:text>
		</script>
		<!-- <script language="JavaScript"  src="http://toolbox.pisa.intecs.it/TOOLBOX/WSDL/javascript/ts_picker.js"></script> -->
		<!--script language="JavaScript" type="text/javascript" src="ts_picker.js"></script-->
		<table border="0" cellspacing="4" width="100%" cellpadding="2">
			<tr>
				<td>
					<!-- ******************************************************************************************************
							RFQ form
						  ******************************************************************************************************-->	
						  <div id="form">					
						  <div id="tabPanel">
							<p id="tabTitle">
								<span onclick="javascript:panelRefresh('classic');" id="title_classic">Time</span>
								<span onclick="javascript:panelRefresh('advanced');" id="title_advanced">Variables</span>
							</p>
							<!-- ******************************************************************************************************
							Time and output format selection tab
						  ******************************************************************************************************-->
							<div class="pgttl" id="classic">
								<!--p>Select start time and duration for the forecast. The delivery date is optional. If it is not provided the system will generate the forecast as soon as the data are availeble.</p-->
								<p>Time selection</p>
								<table border="0" cellspacing="4" width="100%" cellpadding="2" style="border:1px #000000 dashed">
									<tr>
										<td CLASS="stylesheetText" height="30">
											<font class="stylesheetText">
												<i>Insert the start date (yyyy-mm-dd):</i>
												<br/>
											</font>
											<input name="START_DATE" type="text" id="ID_START_DATE" size="20" onChange="javascript:str2dt(document.MASS.START_DATE.value);" onLoad="document.MASS.START_DATE.value=new Date();"/>
											<a href="javascript:show_calendar('document.MASS.START_DATE', document.MASS.START_DATE.value);">
												<img src="http://services-test.eoportal.org/portal/images/cal.gif" width="16" height="16" border="0" alt="Select the start date"/>
											</a>
											<br/>
											<select id="ID_START_TIME" name="START_TIME">
												<option value="" selected="1"/>
												<option value="00:00:00">00:00:00</option>
												<option value="01:00:00">01:00:00</option>
												<option value="02:00:00">02:00:00</option>
												<option value="03:00:00">03:00:00</option>
												<option value="04:00:00">04:00:00</option>
												<option value="05:00:00">05:00:00</option>
												<option value="06:00:00">06:00:00</option>
												<option value="07:00:00">07:00:00</option>
												<option value="08:00:00">08:00:00</option>
												<option value="09:00:00">09:00:00</option>
												<option value="10:00:00">10:00:00</option>
												<option value="11:00:00">11:00:00</option>
												<option value="12:00:00">12:00:00</option>
												<option value="13:00:00">13:00:00</option>
												<option value="14:00:00">14:00:00</option>
												<option value="15:00:00">15:00:00</option>
												<option value="16:00:00">16:00:00</option>
												<option value="17:00:00">17:00:00</option>
												<option value="18:00:00">18:00:00</option>
												<option value="19:00:00">19:00:00</option>
												<option value="20:00:00">20:00:00</option>
												<option value="21:00:00">21:00:00</option>
												<option value="22:00:00">22:00:00</option>
												<option value="23:00:00">23:00:00</option>
											</select>
										</td>
									</tr>
									<tr>
										<td CLASS="stylesheetText" height="30">
											<font class="stylesheetText">
												<i>Duration (1-72)</i>
												<br/>
											</font>
											<input name="DURATION" type="text" id="DURATION" value="" size="20" onChange="javascript:checkDuration();"/>
										</td>
									</tr>
									<tr>
										<td CLASS="stylesheetText" height="30">
											<font class="stylesheetText">
												<i>Delivery date (yyyy-mm-dd):</i>
												<br/>
											</font>
											<input name="DELIVERY_DATE" type="text" id="ID_DELIVERY_DATE" size="20" onChange="javascript:str2dt(document.MASS.ID_DELIVERY_DATE.value);"/>
											<a href="javascript:show_calendar('document.MASS.ID_DELIVERY_DATE', document.MASS.ID_DELIVERY_DATE.value);">
												<img src="http://services-test.eoportal.org/portal/images/cal.gif" width="16" height="16" border="0" alt="Select the delivery date"/>
											</a>
											<br/>
											<select id="ID_DELIVERY_TIME" name="DELIVERY_TIME">
												<option value="" selected="1"/>
												<option value="00:00:00">00:00:00</option>
												<option value="01:00:00">01:00:00</option>
												<option value="02:00:00">02:00:00</option>
												<option value="03:00:00">03:00:00</option>
												<option value="04:00:00">04:00:00</option>
												<option value="05:00:00">05:00:00</option>
												<option value="06:00:00">06:00:00</option>
												<option value="07:00:00">07:00:00</option>
												<option value="08:00:00">08:00:00</option>
												<option value="09:00:00">09:00:00</option>
												<option value="10:00:00">10:00:00</option>
												<option value="11:00:00">11:00:00</option>
												<option value="12:00:00">12:00:00</option>
												<option value="13:00:00">13:00:00</option>
												<option value="14:00:00">14:00:00</option>
												<option value="15:00:00">15:00:00</option>
												<option value="16:00:00">16:00:00</option>
												<option value="17:00:00">17:00:00</option>
												<option value="18:00:00">18:00:00</option>
												<option value="19:00:00">19:00:00</option>
												<option value="20:00:00">20:00:00</option>
												<option value="21:00:00">21:00:00</option>
												<option value="22:00:00">22:00:00</option>
												<option value="23:00:00">23:00:00</option>
											</select>
										</td>
									</tr>
								</table>
								<p>Output format</p>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tbody>
										<tr height="30">
											<td width="30" class="stylesheetBoldText">
												<i>Output format</i>
											</td>
											<td class="stylesheetText">
												<select id="ID_OUTPUT_FORMAT" name="OUTPUT_FORMAT">
													<option value="netcdf">netcdf</option>
													<option value="hdf5">HDF5</option>
													<option value="grib">GRIB</option>
												</select>
											</td>
										</tr>
										<tr height="30">
											<td width="30" class="stylesheetBoldText">
												<i>Preview maps</i>
											</td>
											<td class="stylesheetText">
												<input name="PREVIEW" type="checkbox" id="ID_PREVIEW"/>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<!-- ******************************************************************************************************
							Variable selection tab
						  ******************************************************************************************************-->
							<div class="pgttl" id="advanced">
								<p>Variable selection</p>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Temperature</i>
										</td>
										<td class="stylesheetText">
											<input name="TEMPERATURE" type="checkbox" id="ID_TEMPERATURE"/>
										</td>
									</tr>
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Level</i>
										</td>
										<td class="stylesheetText">
											<select id="ID_TEMPERATURE_OPTION1" name="TEMPERATURE_OPTION1">
												<option value="1000">1000 mb</option>
												<option value="925">925 mb</option>
												<option value="850">850 mb</option>
												<option value="700">700 mb</option>
												<option value="500">500 mb</option>
												<option value="300">300 mb</option>
											</select>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>2m Temperature</i>
										</td>
										<td class="stylesheetText">
											<input name="TWO_METERS_TEMPERATURE" type="checkbox" id="ID_TWO_METERS_TEMPERATURE"/>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>10m Temperature</i>
										</td>
										<td class="stylesheetText">
											<input name="TEN_METERS_TEMPERATURE" type="checkbox" id="ID_TEN_METERS_TEMPERATURE"/>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tbody>
										<tr height="30">
											<td width="30" class="stylesheetBoldText">
												<i>Relative humidity</i>
											</td>
											<td class="stylesheetText">
												<input name="RELATIVE_HUMIDITY" type="checkbox" id="ID_RELATIVE_HUMIDITY"/>
											</td>
										</tr>
										<tr height="30">
											<td width="30" class="stylesheetBoldText">
												<i>Level</i>
											</td>
											<td class="stylesheetText">
												<select id="ID_RELATIVE_HUMIDITY_OPTION1" name="RELATIVE_HUMIDITY_OPTION1">
													<option value="1000">1000 mb</option>
													<option value="925">925 mb</option>
													<option value="850">850 mb</option>
													<option value="700">700 mb</option>
													<option value="500">500 mb</option>
													<option value="300">300 mb</option>
												</select>
											</td>
										</tr>
									</tbody>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tbody>
										<tr height="30">
											<td width="30" class="stylesheetBoldText">
												<i>Surface Temperature</i>
											</td>
											<td class="stylesheetText">
												<input name="SURFACE_TEMPERATUE" type="checkbox" id="ID_SURFACE_TEMPERATUE"/>
											</td>
										</tr>
										<tr height="30">
											<!-- <td width="30" class="stylesheetBoldText">
												<i>Level</i>
											</td>
											<td class="stylesheetText">
												<select id="ID_SURFACE_TEMPERATUE_OPTION1" name="SURFACE_TEMPERATUE_OPTION1">
													<option value="1000">1000 mb</option>
													<option value="925">925 mb</option>
													<option value="850">850 mb</option>
													<option value="700">700 mb</option>
													<option value="500">500 mb</option>
													<option value="300">300 mb</option>
												</select>
											</td> -->
										</tr>
									</tbody>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Surface Water temperature</i>
										</td>
										<td class="stylesheetText">
											<input name="SURFACE_WATER_TEMPERATURE" type="checkbox" id="ID_SURFACE_WATER_TEMPERATURE"/>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>x, y and z-wind component</i>
										</td>
										<td class="stylesheetText">
											<input name="WIND" type="checkbox" id="ID_WIND"/>
										</td>
									</tr>
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Level</i>
										</td>
										<td class="stylesheetText">
											<select id="ID_WIND_OPTION1" name="WIND_OPTION1">
												<option value="1000">1000 mb</option>
												<option value="925">925 mb</option>
												<option value="850">850 mb</option>
												<option value="700">700 mb</option>
												<option value="500">500 mb</option>
												<option value="300">300 mb</option>
											</select>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Precipitation</i>
										</td>
										<td class="stylesheetText">
											<input name="PRECIPITATION" type="checkbox" id="ID_PRECIPITATION"/>
										</td>
									</tr>
								</table>
								<br/>
								<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #000000 dashed">
									<tr height="30">
										<td width="30" class="stylesheetBoldText">
											<i>Pressure</i>
										</td>
										<td class="stylesheetText">
											<input name="PRESSURE" type="checkbox" id="PRESSURE"/>
										</td>
									</tr>
								</table>
								<br/>
							</div>
						</div>
					
			
						<script type="text/javascript" LANGUAGE="JavaScript">
	                                       panelRefresh("classic");
                                   </script>
                                   </div>
				</td>
			</tr>
		</table>
	</xsl:template>
	<!--RFQ input xml -->
	<xsl:template match="mass:sendRFQInput" mode="XML">
		<mass:commonInput>
			<mass:orderId>
				<xsl:value-of select="orderId"/>
			</mass:orderId>
		</mass:commonInput>
		<sendRFQInput xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer">
			<xsl:copy-of select="AOI/*"/>
			<!--only if the AOI tool is used and RFQ is the first operation -->
			<timeSelection>
				<startDateTime>
					<xsl:value-of select="START_DATE"/>T<xsl:value-of select="START_TIME"/>
				</startDateTime>
				<duration>PT<xsl:value-of select="DURATION"/>H</duration>
				<xsl:if test="DELIVERY_DATE != ''">
					<deliveryDateTime>
						<xsl:value-of select="DELIVERY_DATE"/>T<xsl:value-of select="DELIVERY_TIME"/>
					</deliveryDateTime>
				</xsl:if>
			</timeSelection>
			<outputFormat>
				<xsl:value-of select="OUTPUT_FORMAT"/>
			</outputFormat>
			<variables>
				<xsl:if test="SURFACE_WATER_TEMPERATURE = 'on'">
					<variable name="swt"/>
				</xsl:if>
				<xsl:if test="RELATIVE_HUMIDITY = 'on'">
					<variable name="rh">
						<option name="level">
							<xsl:value-of select="RELATIVE_HUMIDITY_OPTION1"/>
						</option>
					</variable>
				</xsl:if>
				<xsl:if test="SURFACE_TEMPERATUE = 'on'">
					<variable name="st"/>
				</xsl:if>
				<xsl:if test="TEMPERATURE = 'on'">
					<variable name="temp">
						<option name="level">
							<xsl:value-of select="TEMPERATURE_OPTION1"/>
						</option>
					</variable>
				</xsl:if>
				<xsl:if test="WIND = 'on'">
					<variable name="wind">
						<option name="level">
							<xsl:value-of select="WIND_OPTION1"/>
						</option>
					</variable>
				</xsl:if>
				<xsl:if test="TWO_METERS_TEMPERATURE = 'on'">
					<variable name="t2m"/>
				</xsl:if>
				<xsl:if test="TEN_METERS_TEMPERATURE = 'on'">
					<variable name="t10m"/>
				</xsl:if>
				
				<xsl:if test="PRECIPITATION = 'on'">
					<variable name="raintot"/>
				</xsl:if>
				<xsl:if test="PRESSURE = 'on'">
					<variable name="press"/>
				</xsl:if>
			</variables>
			<previewMaps>
				<xsl:choose>
					<xsl:when test="PREVIEW = 'on'">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</previewMaps>
		</sendRFQInput>
	</xsl:template>
	<!-- Templates for the RFQ output information using HTML format-->
	<!-- Template for the display of titles of the rfqOutput table using HTML format -->
	<xsl:template name="mass:rfqOutputHeader">
		<!--titles of the table containing RFQ results -->
		<td class="stylesheetHeader">RFQ results for the area selected</td>
	</xsl:template>
	<!-- Template for the RFQ output information using HTML format -->
	<xsl:template match="sns:rfqOutput">
		<!--add statements to display your RFQ result data according to the Service RFQOutputType (see Figure 15) -->
		<td>
			<table cellpadding="3" cellspacing="3" width="100%" class="celltablesm" style="border:1px #FFFFFF">
				<tr height="30">
					<td class="stylesheetHeader">Variable</td>
					<xsl:if test="sns:resultMaps/child::sns:variable[attribute::animatedUrl]"><td class="stylesheetHeader">Animated preview</td></xsl:if>
					<td class="stylesheetHeader">Details</td>
				</tr>
				<xsl:for-each select="sns:resultMaps/sns:variable">
					<tr height="30">
						<td class="stylesheetText">
							<xsl:value-of select="sns:description"/>
						</td>
						<xsl:if test="attribute::animatedUrl">
							<td class="stylesheetText">
								<a href="{attribute::animatedUrl}" target="_blank"><img alt="Show details" border="0" src="{attribute::animatedUrl}" width="400" height="400"/></a>
							</td>
						</xsl:if>
						<td class="stylesheetText">
							<xsl:for-each select="sns:map">
								<xsl:choose>
									<xsl:when test="sns:url"><a href="{sns:url}" target="_blank"><xsl:value-of select="sns:dateTime"/></a><br/></xsl:when>
									<xsl:otherwise><xsl:value-of select="sns:dateTime"/><br/></xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</td>
						
					</tr>
				</xsl:for-each>
			</table>
		</td>
	</xsl:template>
	<!--Order operation -->
	<!-- Template for the Order input information using HTML format -->
	<xsl:template match="mass:sendOrderInput">
		<!--script language="JavaScript" type="text/javascript">
// Insert your Javascript code herafter in order to valid input fields values, mandatory fields
 function checkMandatoryFields(form) {/* return true if check ok else false */}
</script-->
		<!--insert here HTML code to collect Order specific input data -->
	</xsl:template>
	<!-- Template for the order input information using XML format -->
	<xsl:template match="mass:sendOrderInput" mode="XML">
		<mass:commonInput>
			<mass:orderId>
				<xsl:value-of select="orderId"/>
			</mass:orderId>
		</mass:commonInput>
		<sendOrderInput xmlns="http://www.lammamed.rete.toscana.it/ws/meteoLayer">
			<xsl:copy-of select="AOI/*"/>
			<!--only if the AOI tool is used and Order is the first operation -->
			<mass:id>rfq<xsl:value-of select="orderId"/></mass:id>
			<userInfo>
				<userId>
					<xsl:value-of select="userId"/>
				</userId>
				<firstName>
					<xsl:value-of select="firstName"/>
				</firstName>
				<lastName>
					<xsl:value-of select="lastName"/>
				</lastName>
				<invoiceAddress>
					<xsl:value-of select="invoiceAddress"/>
				</invoiceAddress>
				<city>
					<xsl:value-of select="city"/>
				</city>
				<state>
					<xsl:value-of select="state"/>
				</state>
				<country>
					<xsl:value-of select="country"/>
				</country>
				<emailAddress>
					<xsl:value-of select="emailAddress"/>
				</emailAddress>
				<telNumber>+39 6 2356541</telNumber>
				<faxNumber>+39 6 2356541</faxNumber>
			</userInfo>
		</sendOrderInput>
	</xsl:template>
	<!-- Template for the order output information using HTML format -->
	<xsl:template match="sns:getOrderOutput">
		<!-- insert here HTML code to display Order results according to the Service OrderOutputType definition (see Figure 21) -->
		<td class="stylesheetText">
			Product Download Link: <a target="_blank">
				<xsl:attribute name="href"><xsl:value-of select="sns:orderResultURL"/></xsl:attribute>
				<xsl:value-of select="sns:orderResultURL"/>
			</a>
			<br/>
			Data available until: <xsl:value-of select="sns:timePeriodOfService"/>
			
		</td>
	</xsl:template>
</xsl:stylesheet>
