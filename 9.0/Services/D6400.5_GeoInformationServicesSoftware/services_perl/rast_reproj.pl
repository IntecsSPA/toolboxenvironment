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
	"source_crs:s" => \my $source_crs,
	"target_crs=s" => \my $target_crs,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);


##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && defined ($target_crs) && defined ($output_file))) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
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

if (defined($source_crs) && $source_crs ne "") {
	$source_crs="-s_srs '$source_crs'";
} else {
	$source_crs="";
}


###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);

# rename input metadata file
if ($input_metadata_file ne "") {
	Utils::execute("mv $input_metadata_file ${input_file}.aux.xml", $debug);
}

my $gdalwarp_command = "gdalwarp -t_srs '$target_crs' $source_crs $output_format $output_data_type $input_file $output_file";
Utils::execute($gdalwarp_command, $debug);

Utils::printEnd($unique_code, $debug);



