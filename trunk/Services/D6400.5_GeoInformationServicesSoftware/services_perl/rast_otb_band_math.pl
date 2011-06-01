#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use GDALUtils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $ndvi=$params{'ndvi_location'};
my $geoserver_url=$params{'geoserver_url'};
my $geoserver_rest_user=$params{'geoserver_rest_user'};
my $geoserver_rest_password=$params{'geoserver_rest_password'};
my $geoserver_rest_workspace=$params{'geoserver_rest_workspace'};
my $wmc_template=$params{'wmc_template_rast_otb_band_math'};
my $debug=$params{'debug'};

# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"expression:s" => \my $expression,
	"sld=s" => \my $geoserver_style,
	"output_raster=s" => \my $output_raster,
	"output_raster_mimetype:s" => \my $output_raster_mimetype,
	"output_wmc=s" => \my $output_wmc
);


##########################
# Check input parameters #
##########################

#check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && defined ($geoserver_style) && defined ($output_raster) && defined ($output_wmc))) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#################
# Set variables #
#################

#derived parameters
my $work_dir = "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";


my $location="Location_$unique_code";

if ( !defined($expression) || $expression eq "") {
	$expression = "sqrt((b3-b2)/(b3+b2)+0.5)";
}

my $output_format="";
if (defined($output_raster_mimetype) && $output_raster_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_raster_mimetype\n";
	$output_format = GDALUtils::getGDALFormatByMimeType($output_raster_mimetype);
	if ($output_format ne "") {
		$output_format = "-of $output_format";
	}
	print "Output format: $output_format\n";
}

my $output_raster_tiff="${work_dir}/output.tiff";
my $output_raster_tiff_4326="${work_dir}/output_4326.tiff";




###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);


my $ndvi_command = "${ndvi} \"${input_file}\" \"${output_raster_tiff}\" \"${expression}\"";
Utils::execute($ndvi_command, $debug);

# convert output to correct format
if (-e "$output_raster_tiff") {
	if ($output_format eq "" || $output_format eq "-of GTiff") {
		Utils::execute("cp \"$output_raster_tiff\" \"$output_raster\"", $debug);
	} else {
		my $gdal_translate_command = "gdal_translate $output_format \"$output_raster_tiff\" \"$output_raster\"";
		Utils::execute($gdal_translate_command, $debug);
	}
}

## Publish to GeoServer
# reproject to EPSG:4326
my $gdalwarp_command = "gdalwarp -t_srs EPSG:4326 $output_raster_tiff $output_raster_tiff_4326";
Utils::execute($gdalwarp_command, $debug);

if (-e "$output_raster_tiff_4326") {
	
	# remove output_raster_tiff
	if ($debug) {
		print "Deleting $output_raster_tiff";
	}
	unlink($output_raster_tiff);
	
	# use cURL to add raster to geoserver using REST
	my $rest_file_extension = "geotiff";
	my $curl_add_raster_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} --data-binary \@${output_raster_tiff_4326} ${geoserver_url}/rest/workspaces/${geoserver_rest_workspace}/coveragestores/${unique_code}/file.${rest_file_extension}?coverageName=${unique_code}";
	Utils::execute($curl_add_raster_command, $debug);
	
	# use cURL to change default style using REST
	my $curl_change_style_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><defaultStyle><name>${geoserver_style}</name></defaultStyle></layer>' ${geoserver_url}/rest/layers/${geoserver_rest_workspace}:${unique_code}";
	Utils::execute($curl_change_style_command, $debug);
	
	#use cURL to enable layer using REST
	my $curl_enable_layer_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><enabled>true</enabled></layer>' ${geoserver_url}/rest/layers/${geoserver_rest_workspace}:${unique_code}";
	Utils::execute($curl_enable_layer_command, $debug);
	
	
	print "Creating WMC file\n";
	# get bbox of raster file
	my $bbox_minx = 0; my $bbox_miny = 0; my $bbox_maxx = 0; my $bbox_maxy = 0;
	if ($output_raster_tiff_4326 ne "") {
		($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy) = GDALUtils::getBBox($output_raster_tiff_4326);
	}
	my $layer_name = "${geoserver_rest_workspace}:${unique_code}";
	print "Create file from template\n";
	print "WMC template: $wmc_template\n";
	print "Output WMC: $output_wmc\n";
	Utils::createFileFromTemplate($wmc_template, $output_wmc, 
		('##GEOSERVER_URL##', $geoserver_url,
		 '##LAYER_NAME##', ${layer_name},
		 '##STYLE##', ${geoserver_style},
		 '##BBOX_MINX##', $bbox_minx,
		 '##BBOX_MINY##', $bbox_miny,
		 '##BBOX_MAXX##', $bbox_maxx,
		 '##BBOX_MAXY##', $bbox_maxy)
		);
}


Utils::printEnd($unique_code, $debug);


