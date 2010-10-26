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
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_raster_file=s" => \my $input_raster_file,
	"input_vector_file=s" => \my $input_vector_file,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype
);


##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster_file) && defined ($input_vector_file) && defined ($output_file))) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#################
# Set variables #
#################

my $location="Location_$unique_code";

my $output_format="";
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = OGRUtils::getOGRFormatByMimeType($output_mimetype);
	if ($output_format ne "") {
		$output_format = "-f \"$output_format\"";
	}
	print "Output format: $output_format\n";
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

# output:
#star_stats.csv
#star__table.csv
#starspan2 --vector /var/www/html/genesis/data/Bayern/deud52________a8.shp --raster /var/www/html/genesis/data/DLR/POLY-AQ-test/poly_aq_2007-12-28.nc --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats min max avg median stdev
my $starspan_command="starspan2 --vector $input_vector_file --raster $input_raster_file --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats min max avg median stdev";
Utils::execute($starspan_command, $debug);

# move output
Utils::execute("mv star_stats.csv $output_file", $debug);

#remove star__table.csv
Utils::execute("unlink star__table.csv", $debug);

Utils::printEnd($unique_code, $debug);


