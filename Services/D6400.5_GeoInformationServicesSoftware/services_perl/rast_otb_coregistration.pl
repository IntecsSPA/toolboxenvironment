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
my $coregistration_location=$params{'coregistration_location'};
my $debug=$params{'debug'};


print "Input parameters: \n";
Utils::printList(@ARGV, "\n\n");

# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_raster=s" => \my $input_raster,
	"reference_raster=s" => \my $reference_raster,
	"point_set_step_x=i" => \my $point_set_step_x,
	"point_set_step_y=i" => \my $point_set_step_y,
	"exploration_size=i" => \my $exploration_size,
	"window_size=i" => \my $window_size,
	"learning_rate=i" => \my $learning_rate,
	"nb_iterations=i" => \my $nb_iterations,
	"metric_threshold=f" => \my $metric_threshold,
	"output_raster=s" => \my $output_raster,
	"output_raster_mimetype:s" => \my $output_raster_mimetype,
	"field_output_raster=s" => \my $field_output_raster,
	"field_output_raster_mimetype:s" => \my $field_output_raster_mimetype
);



##########################
# Check input parameters #
##########################

#check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster) && defined ($reference_raster)
	 && defined ($point_set_step_x) && defined ($point_set_step_y)  && defined ($exploration_size) && defined ($window_size)
	 && defined ($learning_rate) && defined ($nb_iterations) && defined ($metric_threshold)
	 && defined ($output_raster) && defined ($field_output_raster))) {
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

my $field_output_format="";
if (defined($field_output_raster_mimetype) && $field_output_raster_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $field_output_raster_mimetype\n";
	$field_output_format = GDALUtils::getGDALFormatByMimeType($field_output_raster_mimetype);
	if ($field_output_format ne "") {
		$field_output_format = "-of $field_output_format";
	}
	print "Output format: $field_output_format\n";
}

my $output_raster_tiff="${work_dir}/output.tiff";
my $field_output_raster_tiff="${work_dir}/field_output.tiff";



###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);


my $orthorectification_command = "${coregistration_location} \"${reference_raster}\" \"${input_raster}\" \"${field_output_raster_tiff}\" \"${output_raster_tiff}\"";
$orthorectification_command .= " $point_set_step_x $point_set_step_y $exploration_size $window_size $learning_rate $nb_iterations $metric_threshold";
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

# convert field output to correct format
if (-e "$field_output_raster_tiff") {
	if ($field_output_format eq "" || $field_output_format eq "-of GTiff") {
		Utils::execute("cp \"$field_output_raster_tiff\" \"$field_output_raster\"", $debug);
	} else {
		my $gdal_translate_command = "gdal_translate $field_output_format \"$field_output_raster_tiff\" \"$field_output_raster\"";
		Utils::execute($gdal_translate_command, $debug);
	}
}

Utils::printEnd($unique_code, $debug);


