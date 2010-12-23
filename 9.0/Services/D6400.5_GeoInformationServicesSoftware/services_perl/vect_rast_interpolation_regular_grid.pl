#!/usr/bin/perl 

#perl vect_rast_interp.pl --unique_code=tmp_vect_rast_interp --input_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp" --source_attribute=POP --output_file="/tmp/output" --output_data_type=Float32

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
my $acri_dat_vrt_template=$params{'acri-dat_vrt_template'};
my $acri_dat_csvt_file=$params{'acri-dat_csvt_file'};
my $geoserver_url=$params{'geoserver_url'};
my $geoserver_rest_user=$params{'geoserver_rest_user'};
my $geoserver_rest_password=$params{'geoserver_rest_password'};
my $geoserver_rest_workspace=$params{'geoserver_rest_workspace'};
my $wmc_template=$params{'wmc_template_vect_rast_interp_regular_grid'};
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"input_mimetype:s" => \my $input_mimetype,
	"source_attribute=s" => \my $source_attribute,
	"resolution=s" =>  \my $resolution,
	"output=s" => \my $output_types,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type,
	"output_file:s" => \my $output_file,
	"output_wms:s" => \my $output_wms,
	"output_wcs:s" => \my $output_wcs,
	"output_wmc:s" => \my $output_wmc
);

my $output_type_file = 0;
my $output_type_wms = 0;
my $output_type_wcs = 0;
my $output_type_wmc = 0;

#output_types contains a comma seperated list
my @output_types = split(/,/,$output_types);



##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && defined ($source_attribute) && defined ($resolution) && @output_types)) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

#2. output should be file, WMS, WCS or WMC
for my $output_type (@output_types) {
	if ($output_type ne "file" && $output_type ne "WMS" && $output_type ne "WCS" && $output_type ne "WMC") {
		print "Output type should only be file, WMS, WCS or WMC\n";
		exit 1;
	} else {
		if ($output_type eq "file") {
			$output_type_file = 1;
		}
		if ($output_type eq "WMS") {
			$output_type_wms = 1;
		}
		if ($output_type eq "WCS") {
			$output_type_wcs = 1;
		}
		if ($output_type eq "WMC") {
			$output_type_wmc = 1;
		}
	}
}

#3. every specific parameter for output=file available?
if ($output_type_file && (!defined ($output_file)) ) {
	print "When the output=file, output_file is a required parameter\n";
	exit 1;
}

#4. every specific parameter for output=WMS available?
if ($output_type_wms && (!defined ($output_wms)) ) {
	print "When the output=WMS, output_wms is a required parameter\n";
	exit 1;
}

#5. every specific parameter for output=WCS available?
if ($output_type_wcs && (!defined ($output_wcs)) ) {
	print "When the output=WCS, output_wcs is a required parameter\n";
	exit 1;
}

#6. every specific parameter for output=WMC available?
if ($output_type_wmc && (!defined ($output_wmc)) ) {
	print "When the output=WMC, output_wmc is a required parameter\n";
	exit 1;
}



#################
# Set variables #
#################

my $location="Location_$unique_code";

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";


my $output_format="";
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = GDALUtils::getGDALFormatByMimeType($output_mimetype);
	if ($output_format ne "") {
		$output_format = "format=$output_format";
	}
	print "Output format: $output_format\n";
}

if (defined($output_data_type) && $output_data_type ne "") {
	$output_data_type="type=$output_data_type";
} else {
	$output_data_type="";
}


###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);

# if the input MIME type is not text/x-acri-dat, the input file should be a ZIP file
if (! (defined($input_mimetype) && $input_mimetype eq "text/x-acri-dat") ) {
	my $output_dir= "$work_dir/vector";
	$input_file = Utils::unzip($input_file, $output_dir, $debug);
}

# if the input MIME type is text/x-acri-dat, the input file should be an ACRI dat file
# in this case: 
# - convert the ACRI dat file to a CSV file
# - create a VRT file accompanying the CSV file
# - create a CSVT file accompanying the CSV file
# this is necessary because OGR cannot handle the ACRI dat file out of the box
if (defined($input_mimetype) && $input_mimetype eq "text/x-acri-dat") {
	
	# convert ACRI dat file to CSV file and write output file to work directory
	# sed does this by trimming the first spaces and
	# replacing the other spaces with comma's
	my $sed_command="sed -e \"s|[ ]*\\([^ ]*\\)[ ]*\\([^ ]*\\)[ ]*\\([^ ]*\\)[ ]*|\\1,\\2,\\3|g\"";
	   $sed_command="$sed_command $input_file > \"${input_file}.csv\"";
	print "SED command\n";
	Utils::execute($sed_command, $debug);

	
	#prepend header
	$sed_command="sed -i '1i\\
Longitude,Latitude,Value' \"${input_file}.csv\"";
	print "SED command\n";
	Utils::execute($sed_command, $debug);
	
	# get file name from file path
	# tr -d '\n' removes the trailing newline
	my $input_file_name=`basename ${input_file} | tr -d '\n'`;
	
	#create vrt file from template (fill data source in template)
	Utils::createFileFromTemplate($acri_dat_vrt_template, "${input_file}.vrt", 
		('##LAYER_NAME##', ${input_file_name}, '##DATA_SOURCE##', "${input_file}.csv"));

	#copy csvt file (containing types of fields in csv file)
	Utils::execute("cp \"$acri_dat_csvt_file\" \"${input_file}.csvt\"", $debug);
	
	#set input_file to vrt file
	$input_file = "${input_file}.vrt";
} 


# import vector into new location
Utils::execute("v.in.ogr dsn=\"$input_file\" output=vector location=$location", $debug);

# change location
Utils::execute("g.mapset mapset=PERMANENT location=$location", $debug);

# set resolution
Utils::execute("g.region res=$resolution", $debug);

# interpolate
Utils::execute("v.surf.idw input=vector output=raster column=\"$source_attribute\"", $debug);

my $output_raster= "$work_dir/raster";

# copy output_raster to output_file
if ($output_type_file) {
	Utils::execute("r.out.gdal input=raster output=${output_raster} $output_format $output_data_type", $debug);
	
	Utils::execute("cp ${output_raster} ${output_file}" , $debug);
}

#add output_raster to geoserver and return WMS URL and/or WCS URL and/or WMC 
if ($output_type_wmc || $output_type_wms || $output_type_wcs) {

	#convert to GeoTIFF before storing the raster into GeoServer 
	Utils::execute("r.out.gdal input=raster output=${output_raster} format=GTiff $output_data_type", $debug);

	
	# use cURL to add raster to geoserver using REST
	my $rest_file_extension = "geotiff";
	my $curl_add_raster_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} --data-binary \@${output_raster} ${geoserver_url}/rest/workspaces/${geoserver_rest_workspace}/coveragestores/${unique_code}/file.${rest_file_extension}?coverageName=${unique_code}";
	Utils::execute($curl_add_raster_command, $debug);
	
	# use cURL to change default style using REST
	my $curl_change_style_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><defaultStyle><name>jellyfish</name></defaultStyle></layer>' ${geoserver_url}/rest/layers/${geoserver_rest_workspace}:${unique_code}";
	Utils::execute($curl_change_style_command, $debug);

	#use cURL to enable layer using REST
	my $curl_enable_layer_command = "curl -v -XPUT  -u ${geoserver_rest_user}:${geoserver_rest_password} -H 'Content-type: text/xml' -d '<layer><enabled>true</enabled></layer>' ${geoserver_url}/rest/layers/${geoserver_rest_workspace}:${unique_code}";
	Utils::execute($curl_enable_layer_command, $debug);
	
	
	if ($output_type_wms) {
		# TODO: change URL
		Utils::execute("echo ${geoserver_url}/wms > ${output_wms}" , $debug);
	}
	
	if ($output_type_wcs) {
		# TODO: change URL
		Utils::execute("echo ${geoserver_url}/wcs > ${output_wcs}" , $debug);
	}
	
	if ($output_type_wmc) {
		print "Creating WMC file\n";
		# get bbox of raster file
		my $bbox_minx = 0; my $bbox_miny = 0; my $bbox_maxx = 0; my $bbox_maxy = 0;
		if ($output_raster ne "") {
			($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy) = GDALUtils::getBBox($output_raster);
		}
		my $layer_name = "${geoserver_rest_workspace}:${unique_code}";
		Utils::createFileFromTemplate($wmc_template, $output_wmc, 
			('##GEOSERVER_URL##', $geoserver_url,
			 '##LAYER_NAME##', ${layer_name},
			 '##BBOX_MINX##', $bbox_minx,
			 '##BBOX_MINY##', $bbox_miny,
			 '##BBOX_MAXX##', $bbox_maxx,
			 '##BBOX_MAXY##', $bbox_maxy)
			);
	}
}

Utils::printEnd($unique_code, $debug);



