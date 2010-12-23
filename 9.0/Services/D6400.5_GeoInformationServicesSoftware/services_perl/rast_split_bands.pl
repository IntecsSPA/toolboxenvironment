#!/usr/bin/perl 

#perl rast_split_bands.pl --unique_code=tmp_rast_split_bands_abc --input_file="/tmp/output" --output_folder="/tmp/out" --output_prefix="output"

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
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#

GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"number_of_bands:s" => \my $number_of_bands,
	# name of output files, will just be 1, 2, 3, ...
	"output_folder=s" => \my $output_folder,
	"output_prefix=s" => \my $output_prefix,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);

 

##########################
# Check input parameters #
##########################

if (!(defined ($unique_code) && defined ($input_file) && defined ($output_folder) && defined ($output_prefix) )) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
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


##############
# Processing #
##############

Utils::printStart($unique_code, $debug);


print "Import the input file in the GRASS Location $location\n";
Utils::execute("r.in.gdal input=$input_file output=raster location=$location", $debug);


print "Change location to $location\n";
Utils::execute("g.mapset mapset=PERMANENT location=$location", $debug);


if (!(defined $number_of_bands)) {
	$number_of_bands = GDALUtils::countBands("gdalinfo $input_file");
}

print "Export the $number_of_bands files using format $output_format and data type $output_data_type\n";
# get the index of the elevation in the list
if ($number_of_bands == 1) {
	# if there is only 1 band, input should just be raster (without index)
	Utils::execute("r.out.gdal input=raster output=\"$output_folder/${output_prefix}1\" $output_format $output_data_type", $debug);
} else {
	for (my $index=1; $index <= $number_of_bands; $index++) {
		Utils::execute("r.out.gdal input=raster.$index output=\"$output_folder/${output_prefix}${index}\" $output_format $output_data_type", $debug);
	}
}

Utils::printEnd($unique_code, $debug);


