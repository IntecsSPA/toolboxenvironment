#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use Date::Calc qw(Time_to_Date);
use File::Path qw(mkpath);
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use GDALUtils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $mosaic_indexer_properties=$params{'mosaic_indexer_properties'};
my $mosaic_regex_properties=$params{'mosaic_regex_properties'};
my $geoserver_url=$params{'geoserver_url'};
my $geoserver_url_raster_times=$params{'geoserver_url_raster_times'};
my $geoserver_rest_user=$params{'geoserver_rest_user'};
my $geoserver_rest_password=$params{'geoserver_rest_password'};
my $geoserver_rest_workspace=$params{'geoserver_rest_workspace'};
my $wmc_template=$params{'wmc_template_rast_format_conv2'};
my $wmc_layer_template=$params{'wmc_template_rast_format_conv2_layer'};

my $debug=$params{'debug'};

	
# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"input_metadata_file:s" => \my $input_metadata_file,
	"parameters:s" => \my $parameters,
	"slds:s" => \my $slds,
	"gdal_translate_options:s" => \my $gdal_translate_options,
	"longitude_dataset:s" => \my $longitude_dataset,
	"latitude_dataset:s" => \my $latitude_dataset,
	"output=s" => \my $output_types,
	"output_file:s" => \my $output_file,
	"output_metadata_file:s" => \my $output_metadata_file,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type,
	"output_wmc:s" => \my $output_wmc
);

my $output_type_file = 0;
my $output_type_wmc = 0;

my @parameters = ();
if (defined($parameters) && $parameters ne "") {
	#parameters contains a comma seperated list
	@parameters = split(/,/,$parameters);
}

#output_types contains a comma seperated list
my @output_types = split(/,/,$output_types);


my @slds = ();
if (defined($slds) && $slds ne "") {
	#slds contains a comma seperated list
	@slds = split(/,/,$slds);	
}


##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && @output_types)) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#2. output should be file or WMC
for my $output_type (@output_types) {
	if ($output_type ne "file" && $output_type ne "WMC") {
		print "Output should only be file or WMC\n";
		exit 1;
	} else {
		if ($output_type eq "file") {
			$output_type_file = 1;
		}
		if ($output_type eq "WMC") {
			$output_type_wmc = 1;
		}
	}
}

#3. if output = WMC
#	  and no parameters are provided, exactly 1 sld should be provided
#	  and parameters are provided, the number of slds must be equal to the number of parameters 
my $number_of_parameters = @parameters;
my $number_of_slds = @slds;
if($output_type_wmc == 1) {
	
	#to be backwards compatible:
	# if no SLDs are provided, use 'ARI'
	if ($number_of_slds == 0) {
		if ($number_of_parameters == 0) {
			$slds[0] = 'ARI';
		} else {
			my $i = 0;
			while ($i < $number_of_parameters) {
				$slds[$i] = 'ARI';
				$i++;
			}
		}
		$number_of_slds = @slds;
	}
	
	#if ($number_of_parameters == 0 && $number_of_slds != 1) {
	#	print "If output=wmc and no parameters (= datasets) are provided, exactly one SLD should be provided\n";
	#	exit 1;
	#}
	if ($number_of_parameters > 0 && $number_of_slds != $number_of_parameters) {
		print "If output=wmc and parameters (= datasets) are provided";
		print " the number of provided SLDs should be equal to the number of provided parameters (= datasets)\n";
		exit 1;
	}
}

#4. if longitude_dataset is defined, latitude_dataset should also be defined and vice versa
if ( (defined($longitude_dataset) && $longitude_dataset ne "") && (!(defined($latitude_dataset) && $latitude_dataset ne "")) ) {
	print "If the longitude dataset is provided, the latitude dataset should also be provided\n";
	exit 1;
}
if ( (!(defined($longitude_dataset) && $longitude_dataset ne "")) && (defined($latitude_dataset) && $latitude_dataset ne "") ) {
	print "If the latitude dataset is provided, the longitude dataset should also be provided\n";
	exit 1;
}


#################
# Set variables #
#################

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkpath($work_dir, { mode=>0755}) or die "cannot mkdir $work_dir: $!";

my $location="Location_$unique_code";

if (!(defined $input_metadata_file)) {
	$input_metadata_file="";
}

#create input list
#if no parameters are provided, the input file is added to the list
#otherwise all parameters are added as should be NETCDF:\"${input_file}\":${parameter}
my @input_list = ();
if ($number_of_parameters == 0) {
	push(@input_list, $input_file);
} else {
	for my $parameter (@parameters) {
		push(@input_list, "NETCDF:\"${input_file}\":${parameter}");
	}
}


if (!(defined $gdal_translate_options)) {
	$gdal_translate_options="";
}

my $output_format="";
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = GDALUtils::getGDALFormatByMimeType($output_mimetype);
	if ($output_format ne "") {
		$output_format = "-of $output_format";
	}
	print "Output format: $output_format\n";
}

if (defined($output_data_type) && $output_data_type ne "") {
	$output_data_type="-ot $output_data_type";
} else {
	$output_data_type="";
}


if (defined($longitude_dataset) && $longitude_dataset ne "" && defined($latitude_dataset) && $latitude_dataset ne "") {
	my @min_max_longitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"${input_file}\":${longitude_dataset}");
	my @min_max_latitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"${input_file}\":${latitude_dataset}");

	if ( defined($min_max_longitude[0]) && $min_max_longitude[0] ne "" &&
		 defined($min_max_longitude[1]) && $min_max_longitude[1] ne "" &&
		 defined($min_max_latitude[0]) && $min_max_latitude[0] ne "" &&
		 defined($min_max_latitude[1]) && $min_max_latitude[1] ne "") {

		$gdal_translate_options .= " -a_ullr $min_max_longitude[0] $min_max_latitude[1] $min_max_longitude[1] $min_max_latitude[0]";		 	
	 } 
}



###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);

# rename input metadata file
if ($input_metadata_file ne "") {
	Utils::execute("mv $input_metadata_file ${input_file}.aux.xml", $debug);
}


if ($output_type_file) {
	
	if ($number_of_parameters == 0) {
		my $gdal_translate_command = "gdal_translate $output_format $output_data_type $gdal_translate_options $input_file $output_file";
		Utils::execute($gdal_translate_command, $debug);
	} else {
		# create output directory
		my $output_dir_gdal_translate = "$work_dir/output_gdal_translate/file";
		if (!(-e $output_dir_gdal_translate)) {
			# create working directory
			mkpath($output_dir_gdal_translate, { mode=>0755}) or die "cannot mkdir $output_dir_gdal_translate: $!";
		}
		my $input_list_counter = 0;
		for my $input (@input_list) {
			my $gdal_translate_output_file = "";
			my $parameter = "";
			if (defined($parameters[$input_list_counter]) && $parameters[$input_list_counter] ne "") {
				$parameter = $parameters[$input_list_counter];
				$gdal_translate_output_file = "$output_dir_gdal_translate/$parameter";
			} else {
				$gdal_translate_output_file = "$output_dir_gdal_translate/$input_list_counter";
			}
			my $gdal_translate_command = "gdal_translate $output_format $output_data_type $gdal_translate_options $input $gdal_translate_output_file";
			Utils::execute($gdal_translate_command, $debug);
			
			$input_list_counter = $input_list_counter + 1;
		}
		#zip all files in the output directory
		#the created zip file is the output file
		Utils::zip("zip output.zip *", $output_dir_gdal_translate, "${output_dir_gdal_translate}/output.zip", $debug);
		Utils::execute("mv \"${output_dir_gdal_translate}/output.zip\" \"$output_file\"", $debug);
	}
	
	if ( defined($output_metadata_file) && $output_metadata_file ne "" ) {
		# rename output metadata file if it exists
		if (-e "${output_file}.aux.xml") {
			Utils::execute("mv ${output_file}.aux.xml $output_metadata_file", $debug);
		}
		
		# if the output metadata file does not exist, create it
		# this is necessary because if not all output files are created,
		# the process will fail
		unless (-e $output_metadata_file) {
			Utils::execute("touch \"$output_metadata_file\"");
		}
	}
}

if ($output_type_wmc) {
	
	my @output_files = ();
	my $layers_wmc_output = "";
	my $main_wmc_time_tag = "";
	
	my $input_list_counter = 0;
	
	for my $input (@input_list) {
		
		my $parameter = "";
		if (defined($parameters[$input_list_counter]) && $parameters[$input_list_counter] ne "") {
			$parameter = $parameters[$input_list_counter];
		}
		
		# create output directory
		my $output_dir_gdal_translate = "$work_dir/output_gdal_translate/wmc";
		if ($parameter ne "") {
			$output_dir_gdal_translate .= "/$parameter"
		}
		if (!(-e $output_dir_gdal_translate)) {
			mkpath($output_dir_gdal_translate, { mode=>0755}) or die "cannot mkdir $output_dir_gdal_translate: $!";
		}
		
		
		# if the raster contains bands representing timestamps (ACRI case)
		# export all bands to GeoTIFF and publish the bands as an ImageMosaic store in GeoServer
		#
		# if we can't find any timestamps, export the raster (all bands) to GeoTIFF
		# and publish the raster as a GeoTIFF to GeoServer	

		
		# store information about the bands in the raster in a hash
		# the returned information is a hashmap with this structure:
		#		timestamp
		#			'band'
		#				bandnumber
		#			'variable_name'
		#				variable_name
		#		timestamp
		#			'band'
		#				bandnumber
		#			'variable_name'
		#				variable_name
		#		...
		my %bandinfo = GDALUtils::storeBandInfoACRIFileInHash("timestamp", "$input");
		
		#order numeric using the "spaceship operator" <=>
		my @ordered_timestamps = sort { $a <=> $b } (keys %bandinfo);
	
		my $number_of_timestamps = @ordered_timestamps;

		# WMC variables		
		my $layer_name = "";
		my $layer_title = ""; 
		my $all_timestamps_in_ISO8601 = "";
		my $default_timestamp_in_ISO8601 = "";
		# only the first layer should be active, all other layers are hidden
		# the time line should also only be rendered for the first layer 
		my $layer_hidden = "1";
		my $timeline_rendering = "";
		if ($input_list_counter == 0) {
			$layer_hidden = "0";
			$timeline_rendering = "<timelineRendering>range</timelineRendering>";          
		}
		
		if ($number_of_timestamps == 0) {
				
			#convert the bands to a GeoTIFF before storing it into GeoServer 
			my $gdal_translate_output_file = "$output_dir_gdal_translate/wmc_output.tiff";
			my $gdal_translate_command = "gdal_translate -of GTiff $output_data_type $gdal_translate_options $input \"$gdal_translate_output_file\"";
			
			Utils::execute($gdal_translate_command, $debug);

			push(@output_files,$gdal_translate_output_file)	;

			#publish file to GeoServer
			($layer_name, $layer_title) = publishToGeoServerAndReturnLayerNameAndTitle
					(${geoserver_rest_workspace}, ${unique_code}, ${parameter}, ${geoserver_url}, ${geoserver_rest_user},${geoserver_rest_password},
					 ${gdal_translate_output_file}, "geotiff", $slds[$input_list_counter]);
			
		} else {
		
			#my $count = 1;
			#while ( my ($key, $value) = each(%timestamp_band) ) {
			#	print "$count. $key => $value\n";
			#	$count += 1;
			#}
		
			
			
			#execute gdal_translate for each band and store date/time in filename
			for my $timestamp (@ordered_timestamps) {
				
				my $bandinfo_values_ref = $bandinfo{$timestamp};
				if (defined $bandinfo_values_ref) {
					#dereference hash using % sigil (actually, 2 dimensional hashmaps don't exist in Perl,
					#	 so the %bandinfo_values_ref is not a hashmap, but the link to a hashmap)
					my %bandinfo_values = %$bandinfo_values_ref;
					
					my $band = $bandinfo_values{'band'};
					if ($band && $band ne "") {
						(my $year, my $month, my $day, my $hour, my $min, my $sec) = Utils::getDateInFormatYYYYMMDDhhmmss(Time_to_Date($timestamp));
						#($year, $month, $day, $hour, $min, $sec) = Utils::getDateInFormatYYYYMMDDhhmmss($year, $month, $day, $hour, $min, $sec);
						#print "${year}${month}${day}T${hour}${min}${sec}\n";
						
						my $timestamp_in_filename = "${year}${month}${day}T${hour}${min}${sec}";
						my $timestamp_in_ISO8601  = "${year}-${month}-${day}T${hour}:${min}:${sec}Z";
						
						#convert the bands to a GeoTIFF before storing it into GeoServer 
						#to add GeoTIFF files as an image mosaic to GeoServer using cURL, it is necessary to add the .tiff extension
						#if you don't add the extension you will receive a 500 Internal Server Error when uploading the ZIP file
						my $gdal_translate_output_file = "$output_dir_gdal_translate/wmc_output_${timestamp_in_filename}.tiff";
						my $gdal_translate_command = "gdal_translate -b $band -of GTiff $output_data_type $gdal_translate_options $input \"$gdal_translate_output_file\"";
						Utils::execute($gdal_translate_command, $debug);
						
						push(@output_files,$gdal_translate_output_file)	;
						
						#store timestamp in ISO8601 format (to use in WMC)
						#if this is not the first timestamp, add a comma before adding the new timestamp to $all_timestamps_in_ISO8601
						if ($all_timestamps_in_ISO8601 ne "") {
							$all_timestamps_in_ISO8601 .= ",";
						}
						$all_timestamps_in_ISO8601 .= $timestamp_in_ISO8601;
						# make the first timestamp the default one
						if ($default_timestamp_in_ISO8601 eq "") {
							$default_timestamp_in_ISO8601 = $timestamp_in_ISO8601;
							# if there is no default timestamp for the main WMC, make this one the default
							if ($main_wmc_time_tag eq "") {
								$main_wmc_time_tag = "<time>${default_timestamp_in_ISO8601}</time>";
							}
						}
					}
				}
			}
			
			if ($all_timestamps_in_ISO8601 eq "" || $default_timestamp_in_ISO8601 eq "") {
				print "Timestamps are not in ISO8601 format!\n";
				print "Cannot create WMC\n";
				print "Not publishing raster to GeoServer\n";
				exit 1;
			}
			
			#copy indexer.properties and regex.properties to directory containing gdal_translate output files	
			my $cp_command = "cp $mosaic_indexer_properties $output_dir_gdal_translate/indexer.properties";
			Utils::execute($cp_command, $debug);
			$cp_command = "cp $mosaic_regex_properties $output_dir_gdal_translate/regex.properties";
			Utils::execute($cp_command, $debug);
		
			#zip all files (gdal_translate output files + indexer.properties + regex.properties) in the output directory
			Utils::zip("zip wmc.zip *", $output_dir_gdal_translate, "${output_dir_gdal_translate}/wmc.zip", $debug);
			
			#publish file to GeoServer
			my $file_to_publish = "${output_dir_gdal_translate}/wmc.zip";
			($layer_name, $layer_title) = publishToGeoServerAndReturnLayerNameAndTitle
					(${geoserver_rest_workspace}, ${unique_code}, ${parameter}, ${geoserver_url_raster_times}, ${geoserver_rest_user},${geoserver_rest_password},
					 ${file_to_publish},"imagemosaic",$slds[$input_list_counter]);
			
		}
	
		# Create WMC layer
		if ($layer_name ne "") {
			my $dimension_list = "";
			my $geoserver = $geoserver_url;
			
			if ($default_timestamp_in_ISO8601 ne "" && $all_timestamps_in_ISO8601 ne "") {
				$dimension_list .= "      <DimensionList>\n";
				$dimension_list .= "         <Dimension name='time' units='ISO8601' default='${default_timestamp_in_ISO8601}' nearestValue='0'>${all_timestamps_in_ISO8601}</Dimension>\n";
				$dimension_list .= "      </DimensionList>\n";
				
				$geoserver = $geoserver_url_raster_times;
			}
			
			print "Creating WMC layer\n";
			$layers_wmc_output .= Utils::createStringFromTemplate($wmc_layer_template, 
				('##LAYER_HIDDEN##', $layer_hidden,
				 '##GEOSERVER_URL##', $geoserver,
				 '##LAYER_NAME##', ${layer_name},
				 '##LAYER_TITLE##', ${layer_title},
				 '##DIMENSION_LIST##', ${dimension_list},
				 '##SLD##', $slds[$input_list_counter],
				 '##TIMELINERENDERING##', $timeline_rendering)
				);
		}
		$input_list_counter++;
	}
		
	print "Creating WMC file\n";
	# get bbox of sample file (since all output files have the same extent, we only need 1 sample file)
	my $bbox_minx = 0; my $bbox_miny = 0; my $bbox_maxx = 0; my $bbox_maxy = 0;
	my $number_of_output_files = @output_files;
	if ($number_of_output_files > 0) {
		($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy) = GDALUtils::getBBox(@output_files);
	}
	
	# workaround to make sure for the SGH demo, the boudingbox is correct
	#(viewer cannot handle bbox in another CRS than EPSG:4326)
	if (defined($slds[0]) && $slds[0] eq "SGH") {
		$bbox_minx = 4;
		$bbox_miny = 45.864;
		$bbox_maxx = 4.119;
		$bbox_maxy = 45.984;
	}
	
	
	#if no raster files with times are published, the TIME_TAG will be empty (so doesn't exist)
	Utils::createFileFromTemplate($wmc_template, $output_wmc, 
		('##TIME_TAG##', $main_wmc_time_tag,
		 '##BBOX_MINX##', $bbox_minx,
		 '##BBOX_MINY##', $bbox_miny,
		 '##BBOX_MAXX##', $bbox_maxx,
		 '##BBOX_MAXY##', $bbox_maxy,
		 '##WMS_LAYERS##', $layers_wmc_output)
		);
}


Utils::printEnd($unique_code, $debug);


exit 0;

sub publishToGeoServerAndReturnLayerNameAndTitle {

	my ($workspace,$layer_name_wo_workspace,$parameter,$geoserver_url,$geoserver_rest_user,$geoserver_rest_password,$file_to_publish,$rest_file_extension,$style) = @_;
	
	my $layer_title = "Output Raster";
	if ($parameter ne "") {
		$layer_name_wo_workspace .= "_${parameter}";
		$layer_title = $parameter;
	}
	my $coverage_name = $layer_name_wo_workspace;
	my $layer_name = "${geoserver_rest_workspace}:${layer_name_wo_workspace}";
	my $content_type = "";
	if ($rest_file_extension eq "imagemosaic") {
		$content_type = "-H 'Content-type: application/zip'"; 
	}
	
	# use cURL to add raster to geoserver using REST
	my $curl_add_raster_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} ${content_type} --data-binary \@${file_to_publish} ${geoserver_url}/rest/workspaces/${geoserver_rest_workspace}/coveragestores/${coverage_name}/file.${rest_file_extension}?coverageName=${layer_name_wo_workspace}";
	Utils::execute($curl_add_raster_command, $debug);
	
	# use cURL to change default style using REST
	if (defined $style) {
		my $curl_change_style_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><defaultStyle><name>${style}</name></defaultStyle></layer>' ${geoserver_url}/rest/layers/${layer_name}";
		Utils::execute($curl_change_style_command, $debug);
	}
	
	
	#use cURL to enable layer using REST
	my $curl_enable_layer_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><enabled>true</enabled></layer>' ${geoserver_url}/rest/layers/${layer_name}";
	Utils::execute($curl_enable_layer_command, $debug);
	
	return ($layer_name,$layer_title);

}

