#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use OGRUtils;

#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
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
	"target_crs=s" => \my $target_crs,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype
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

my $location="Location_$unique_code";

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";


my $output_format="";
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = OGRUtils::getOGRFormatByMimeType($output_mimetype);
	if ($output_format ne "") {
		$output_format = "-f \"$output_format\"";
	}
	print "Output format: $output_format\n";
} else {
	#if no mimetype is provided, keep format of input file
	$output_format = OGRUtils::getOGRFormatOfFile($input_file);
}

#If MapInfo File is the output format, specify that we want to create MIF/MID files
#Otherwise, TAB files are created
my $dataset_creation_option = "";
if ($output_format eq "-f \"MapInfo File\"") {
	$dataset_creation_option = "-dsco FORMAT=MIF";
}




###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);

my $output_dir= "$work_dir/vector";
$input_file = Utils::unzip($input_file, $output_dir, $debug);


# create ogr2ogr output directory
my $ogr2ogr_output_dir = "$work_dir/ogr2ogr_output";
mkdir($ogr2ogr_output_dir, 0755) or die "cannot mkdir $ogr2ogr_output_dir: $!";

Utils::execute("ogr2ogr $output_format -t_srs '$target_crs' $dataset_creation_option \"$ogr2ogr_output_dir/Output\" \"$input_file\"", $debug);

#mostly, eg. to generate a shapefile, ogr2ogr creates files in the ogr2ogr_output/Output directory
#   ogr2ogr_output/Output/ALL1A01e.dbf
#   ogr2ogr_output/Output/ALL1A01e.prj
#   ogr2ogr_output/Output/ALL1A01e.shp
#   ogr2ogr_output/Output/ALL1A01e.shx
#for GML output however, the following files are generated (so no extra directory):
#   ogr2ogr_output/Output
#   ogr2ogr_output/Output.xsd
#
#we just zip recursively ogr2ogr_output/Output*, so in the case of a shapefile the Ouput-directory is zipped
#and in the case of GML, the Output and Output.xsd files are zipped
Utils::zip("zip -r \"$output_file\" Output*", $ogr2ogr_output_dir, $output_file, $debug);

Utils::printEnd($unique_code, $debug);





 