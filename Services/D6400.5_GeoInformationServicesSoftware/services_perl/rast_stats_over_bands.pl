#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;

#general parameters
my %params = Utils::getParams();
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_folder=s" => \my $input_folder,
	"bands=s" => \my $bands,
	"methods=s" => \my $methods,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);

#bands and methods contain a comma seperated list
my @bands = split(/,/,$bands);
my @methods = split(/,/,$methods);


##########################
# Check input parameters #
##########################

# 1. every mandatory parameter available?
if (!(defined ($unique_code) && defined ($input_folder) && @bands && @methods && defined ($output_file) ) ) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

# 2. check if input_folder is a directory
# glob input file names
if (!(-d $input_folder)) {
	print "The input_folder parameter should be a directory\n";
	exit 1;
}
my @input_files = glob("${input_folder}/*");

# 3. check if all methods are one of the following: minimum, maximum, average, median or stdev
for my $method (@methods) {
	my @valid_methods = ("minimum", "maximum", "average", "median", "stddev");
	if (! (grep $_ eq $method, @valid_methods) ) {
		print "Only the following methods are allowed: minimum, maximum, average, median and stdev\n";
		exit 1;
	}
} 




#################
# Set variables #
#################

my $location="Location_$unique_code";
my $output_format="";

if (defined($output_mimetype) && $output_mimetype ne "") {
	print "set output format\n";
	print "Output MimeType: $output_mimetype\n";
	switch ( $output_mimetype ) {
		case "image/tiff" 			{ $output_format="format=GTiff"; }
		case "application/x-netcdf"	{ $output_format="format=netCDF"; }
		case "application/x-hdf"	{ $output_format="format=HDF4"; }
		case "image/png"			{ $output_format="format=PNG"; }
		case "image/jpeg"			{ $output_format="format=JPEG"; }
		case "image/gif"			{ $output_format="format=GIF"; }
	}
	print "Output format: $output_format\n";
}

if (defined($output_data_type) && $output_data_type ne "") {
	$output_data_type="type=$output_data_type";
} else {
	$output_data_type="";
}

my $number_of_input_files = @input_files;



##############
# Processing #
##############

print "Instance $unique_code  --------- START\n";
print ".\n";

my $comma_separated_raster_maps = "";

my $count_input_raster_maps = 1;
print "Import the selected bands of the input files in the GRASS Location $location\n";
for my $input_file (@input_files) {
	
	for my $band(@bands) {
		my $raster_map = "raster${count_input_raster_maps}";
		if ($count_input_raster_maps==1) {
			# if count=1, create new location and change to that location
			print "r.in.gdal command\n";
			my $r_in_gdal_command = "r.in.gdal input=$input_file band=$band output=$raster_map location=$location";
			Utils::execute($r_in_gdal_command, $debug);
			
			print "Change location to $location\n";
			my $g_mapset_command = "g.mapset mapset=PERMANENT location=$location";
			Utils::execute($g_mapset_command, $debug);
		} else {
			print "r.in.gdal command\n";
			my $r_in_gdal_command = "r.in.gdal input=$input_file band=$band output=$raster_map";
			Utils::execute($r_in_gdal_command, $debug);
		}
		
		# add raster_map to the comma seperated list
		if ($comma_separated_raster_maps ne "") {
			$comma_separated_raster_maps = "${comma_separated_raster_maps},";
		}
		$comma_separated_raster_maps = "${comma_separated_raster_maps}${raster_map}";
		
		$count_input_raster_maps++;
	}
}

my $comma_separated_r_series_output = "";
for my $method (@methods) {
	print "r.series command\n";
	my $r_series_command = "r.series input=\"$comma_separated_raster_maps\" output=rast_stats_output_${method} method=$method";
	Utils::execute($r_series_command, $debug);
	
	# add rast_stats_output_${method} to the comma seperated list
	if ($comma_separated_r_series_output ne "") {
		$comma_separated_r_series_output = "${comma_separated_r_series_output},";
	}
	$comma_separated_r_series_output = "${comma_separated_r_series_output}rast_stats_output_${method}";
}

# group all the output raster into one output raster
print "Group the rasters\n"; 
my $i_group_command = "i.group group=raster_group input=$comma_separated_r_series_output";
Utils::execute($i_group_command, $debug);

print "Export the file using format $output_format and data type $output_data_type\n";
my $r_out_gdal_command = "r.out.gdal input=raster_group output=$output_file $output_format $output_data_type";
Utils::execute($r_out_gdal_command, $debug);

print ".\n";
print "Instance $unique_code  --------- END\n";


