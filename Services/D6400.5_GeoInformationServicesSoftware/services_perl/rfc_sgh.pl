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
my $mosaic_indexer_properties=$params{'mosaic_indexer_properties'};
my $mosaic_regex_properties=$params{'mosaic_regex_properties'};
my $geoserver_url_raster_times=$params{'geoserver_url_raster_times'};
my $geoserver_rest_user=$params{'geoserver_rest_user'};
my $geoserver_rest_password=$params{'geoserver_rest_password'};
my $wmc_template=$params{'wmc_template_rast_format_conv2'};
my $wmc_layer_template=$params{'wmc_template_rast_format_conv2_layer'};

my $debug=$params{'debug'};

	
	
	
#my $input="/var/www/html/genesis/data/SGH/20101115/villerest_grid2D_tra_LII_10m.nc";
#my $input="/tmp/villerest_rev.tiff";
my $input="/tmp/villerest_rev2.tiff";



my @output_files = ();
my $layers_wmc_output = "";
my $main_wmc_time_tag = "";
my $sld = "SGH";
my $output_dir = "/tmp/sgh_output";


Utils::execute("rm -rf /opt/jakarta/apache-tomcat-5.5.27/webapps/geoserver4genesistime/data/data/sgh", $debug);
Utils::execute("rm -rf $output_dir", $debug);
Utils::execute("mkdir $output_dir", $debug);

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
my %bandinfo = GDALUtils::storeBandInfoSGHFileInHash("$input");

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
my $timeline_rendering = "<timelineRendering>range</timelineRendering>";          
$layer_hidden = "0";



for my $timestamp (@ordered_timestamps) {
		
	my $band = $bandinfo{$timestamp};
	if ($band && $band ne "") {
		(my $year, my $month, my $day, my $hour, my $min, my $sec) = Utils::getDateInFormatYYYYMMDDhhmmss(Time_to_Date($timestamp));
		#($year, $month, $day, $hour, $min, $sec) = Utils::getDateInFormatYYYYMMDDhhmmss($year, $month, $day, $hour, $min, $sec);
		#print "${year}${month}${day}T${hour}${min}${sec}\n";
		
		my $timestamp_in_filename = "${year}${month}${day}T${hour}${min}${sec}";
		my $timestamp_in_ISO8601  = "${year}-${month}-${day}T${hour}:${min}:${sec}Z";
		
		print "Bandnumber: $band \n";
		print "Timestamp filename: $timestamp_in_filename \n";
		print "Timestamp ISO: $timestamp_in_ISO8601 \n";
		
		
		#convert the bands to a GeoTIFF before storing it into GeoServer 
		#to add GeoTIFF files as an image mosaic to GeoServer using cURL, it is necessary to add the .tiff extension
		#if you don't add the extension you will receive a 500 Internal Server Error when uploading the ZIP file
		my $gdal_translate_output_file = "wmc_output_${timestamp_in_filename}.tiff";
		my $gdal_translate_command = "gdal_translate -b $band -of GTiff -a_srs EPSG:27572 $input \"$output_dir/$gdal_translate_output_file\"";
		
		
		Utils::execute($gdal_translate_command, $debug);
		
		push(@output_files, $gdal_translate_output_file);
		
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
if ($all_timestamps_in_ISO8601 eq "" || $default_timestamp_in_ISO8601 eq "") {
	print "Timestamps are not in ISO8601 format!\n";
	print "Cannot create WMC\n";
	print "Not publishing raster to GeoServer\n";
	exit 1;
}

#copy indexer.properties and regex.properties to directory containing gdal_translate output files	
my $cp_command = "cp $mosaic_indexer_properties $output_dir/indexer.properties";
Utils::execute($cp_command, $debug);
$cp_command = "cp $mosaic_regex_properties $output_dir/regex.properties";
Utils::execute($cp_command, $debug);

#zip all files (gdal_translate output files + indexer.properties + regex.properties) in the output directory
Utils::zip("zip wmc.zip *", "$output_dir", "$output_dir/wmc.zip", $debug);

#publish file to GeoServer
my $file_to_publish = "$output_dir/wmc.zip";
($layer_name, $layer_title) = publishToGeoServerAndReturnLayerNameAndTitle
		("genesis_sgh", "sgh", "", ${geoserver_url_raster_times}, ${geoserver_rest_user},${geoserver_rest_password},
		 ${file_to_publish},"imagemosaic",${sld});
	

# Create WMC layer
if ($layer_name ne "") {
	my $dimension_list = "";
	
	if ($default_timestamp_in_ISO8601 ne "" && $all_timestamps_in_ISO8601 ne "") {
		$dimension_list .= "      <DimensionList>\n";
		$dimension_list .= "         <Dimension name='time' units='ISO8601' default='${default_timestamp_in_ISO8601}' nearestValue='0'>${all_timestamps_in_ISO8601}</Dimension>\n";
		$dimension_list .= "      </DimensionList>\n";
	}
	
	print "Creating WMC layer\n";
	$layers_wmc_output .= Utils::createStringFromTemplate($wmc_layer_template, 
		('##LAYER_HIDDEN##', $layer_hidden,
		 '##GEOSERVER_URL##', $geoserver_url_raster_times,
		 '##LAYER_NAME##', ${layer_name},
		 '##LAYER_TITLE##', ${layer_title},
		 '##DIMENSION_LIST##', ${dimension_list},
		 '##SLD##', ${sld},
		 '##TIMELINERENDERING##', $timeline_rendering)
		);
}
		
print "Creating WMC file\n";
# workaround to make sure for the SGH demo, the boudingbox is correct
#(viewer cannot handle bbox in another CRS than EPSG:4326)
Utils::createFileFromTemplate($wmc_template, "$output_dir/wmc.xml", 
	('##TIME_TAG##', $main_wmc_time_tag,
	 '##BBOX_MINX##', 4,
	 '##BBOX_MINY##', 45.864,
	 '##BBOX_MAXX##', 4.119,
	 '##BBOX_MAXY##', 45.984,
	 '##WMS_LAYERS##', $layers_wmc_output)
	);


exit 0;

sub publishToGeoServerAndReturnLayerNameAndTitle {

	my ($workspace,$layer_name_wo_workspace,$parameter,$geoserver_url,$geoserver_rest_user,$geoserver_rest_password,$file_to_publish,$rest_file_extension,$style) = @_;
	
	my $layer_title = "Output Raster";
	if ($parameter ne "") {
		$layer_name_wo_workspace .= "_${parameter}";
		$layer_title = $parameter;
	}
	my $coverage_name = $layer_name_wo_workspace;
	my $layer_name = "${workspace}:${layer_name_wo_workspace}";
	my $content_type = "";
	if ($rest_file_extension eq "imagemosaic") {
		$content_type = "-H 'Content-type: application/zip'"; 
	}
	
	# use cURL to add raster to geoserver using REST
	my $curl_add_raster_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} ${content_type} --data-binary \@${file_to_publish} ${geoserver_url}/rest/workspaces/${workspace}/coveragestores/${coverage_name}/file.${rest_file_extension}?coverageName=${layer_name_wo_workspace}";
	print "$curl_add_raster_command \n";
	Utils::execute($curl_add_raster_command, $debug);
	
	# use cURL to change default style using REST
	if (defined $style) {
		my $curl_change_style_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><defaultStyle><name>${style}</name></defaultStyle></layer>' ${geoserver_url}/rest/layers/${layer_name}";
		print "$curl_change_style_command \n";
		Utils::execute($curl_change_style_command, $debug);
	}
	
	
	#use cURL to enable layer using REST
	my $curl_enable_layer_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><enabled>true</enabled></layer>' ${geoserver_url}/rest/layers/${layer_name}";
	print "$curl_enable_layer_command \n";
	Utils::execute($curl_enable_layer_command, $debug);
	
	return ($layer_name,$layer_title);

}
