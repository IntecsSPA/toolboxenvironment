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
my $orthorectification=$params{'orthorectification_location'};
my $debug=$params{'debug'};

# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_raster=s" => \my $input_raster,
	"utm_zone=s" => \my $utm_zone,
	"utm_hemisphere=s" => \my $utm_hemisphere,
	"upper_left_x=s" => \my $upper_left_x,
	"upper_left_y=s" => \my $upper_left_y,
	"number_rows=s" => \my $number_rows,
	"number_columns=s" => \my $number_columns,
	"resolution_x=s" => \my $resolution_x,
	"resolution_y=s" => \my $resolution_y,
	"output_raster=s" => \my $output_raster,
	"output_raster_mimetype:s" => \my $output_raster_mimetype,
);


##########################
# Check input parameters #
##########################

#check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster) && defined ($utm_zone) && defined ($utm_hemisphere)
	 && defined ($upper_left_x) && defined ($upper_left_y)  && defined ($number_rows) && defined ($number_columns)
	 && defined ($resolution_x) && defined ($resolution_y) && defined ($output_raster))) {
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



###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);


my $orthorectification_command = "${orthorectification} \"${input_raster}\" \"${output_raster_tiff}\" $utm_zone $utm_hemisphere $upper_left_x $upper_left_y $number_rows $number_columns $resolution_x $resolution_y";
Utils::execute($orthorectification_command, $debug);

# convert output to correct format
if (-e "$output_raster_tiff") {
	if ($output_format eq "" || $output_format eq "-of GTiff") {
		Utils::execute("cp \"$output_raster_tiff\" \"$output_raster\"", $debug);
	} else {
		my $gdal_translate_command = "gdal_translate $output_format \"$output_raster_tiff\" \"$output_raster\"";
		Utils::execute($gdal_translate_command, $debug);
	}
}

Utils::printEnd($unique_code, $debug);


