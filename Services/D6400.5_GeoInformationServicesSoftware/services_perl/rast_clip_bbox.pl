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
	"output_file=s" => \my $output_file,
	"output_metadata_file=s" => \my $output_metadata_file,
	"bbox_lower_corner=s" => \my $bbox_lower_corner,
	"bbox_upper_corner=s" => \my $bbox_upper_corner,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);


##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && defined ($output_file) && defined ($bbox_lower_corner) && defined ($bbox_upper_corner) )) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}
print "BBOX lower corner: $bbox_lower_corner\n";
print "BBOX upper corner: $bbox_upper_corner\n";

(my $south, my $west) = split(/ /,$bbox_lower_corner);
(my $north, my $east) = split(/ /,$bbox_upper_corner);
if (!(defined ($south) && defined ($west))) {
	print "South: $south\n";
	print "West: $west\n";
	print "bbox_lower_corner has invalid format\n";
	exit 1;
}
if (!(defined ($north) && defined ($east))) {
	print "North: $north\n";
	print "East: $east\n";
	print "bbox_upper_corner has invalid format\n";
	exit 1;
}


#################
# Set variables #
#################

if (!(defined $input_metadata_file)) {
	$input_metadata_file="";
}

my $location="Location_$unique_code";

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


##############
# Processing #
##############

Utils::printStart($unique_code, $debug);

# rename input metadata file
if ($input_metadata_file ne "") {
	Utils::execute("mv $input_metadata_file ${input_file}.aux.xml", $debug);
}

my $gdal_translate_command = "gdal_translate -projwin $west $north $east $south $output_format $output_data_type $input_file $output_file";
Utils::execute($gdal_translate_command, $debug);

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


Utils::printEnd($unique_code, $debug);


