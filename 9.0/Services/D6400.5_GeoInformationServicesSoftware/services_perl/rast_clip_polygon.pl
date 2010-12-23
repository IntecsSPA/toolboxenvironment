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
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $debug=$params{'debug'};

	
# read input parameters:
#
# = indicates mandatory options
# : indicates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_raster_file=s" => \my $input_raster_file,
	"input_polygon=s" => \my $input_polygon,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype
);


##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster_file) && defined ($input_polygon) && defined ($output_file) )) {
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
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = GDALUtils::getGDALFormatByMimeType($output_mimetype);
	if ($output_format ne "") {
		$output_format = "-of $output_format";
	}
	print "Output format: $output_format\n";
}

#if (defined($output_data_type) && $output_data_type ne "") {
#	$output_data_type="-ot $output_data_type";
#} else {
#	$output_data_type="";
#}


##############
# Processing #
##############

Utils::printStart($unique_code, $debug);

# create output directory
my $output_starspan = "$work_dir/output_starspan";
mkdir($output_starspan, 0755) or die "cannot mkdir $output_starspan: $!";

performStarspan("$output_starspan", $input_polygon, $input_raster_file);

my $rast_format_conv_command="perl $perl_scripts_dir/rast_format_conv.pl --unique_code=\"${unique_code}_rast_format_conv\" --input_file=\"${output_starspan}/output_starspan__mr0001.img\" --output=\"file\" --output_file=\"$output_file\" --output_mimetype=\"$output_mimetype\"";
#--output_data_type="$OutputDataType"

print "RASTER FORMAT CONVERSION\n";
Utils::execute($rast_format_conv_command, $debug);


Utils::printEnd($unique_code, $debug);


exit;


sub performStarspan {
	my ($output_dir, $input_vector_file, $input_raster_file) = @_;

	#starspan2 info:
	# If –-in is given, pixels not contained in the geometry feature are nullified in the resulting mini raster. By default, all pixels in the feature envelope (bounding box) are retained. 
	#starspan2 --vector "/var/www/html/genesis/data/GIM_test_data/gml/gml2_south_france.gml" --raster "/var/www/html/genesis/data/ACRI/GENESIS-Data/CWQ-Others/L3_OSTIA_OSTIA_SST_j__20090326_PACA_PC_ACR___0000.tif" --out-prefix output_starspan_ --out-type mini_rasters --in
	my $starspan_command="starspan2 --vector $input_vector_file --raster $input_raster_file --out-prefix output_starspan_ --out-type mini_rasters --in;";
	print "STARSPAN\n";
	Utils::execute($starspan_command, $debug);

	# output:
	#output_starspan__mr0001.hdr
	#output_starspan__mr0001.img
	#output_starspan__mr0001.img.aux.xml
	
	# move output
	Utils::execute("mv output_starspan_* ${output_dir}/", $debug);
	
	
	
	#remove generated files
	unlink "output_starspan_*";
}



