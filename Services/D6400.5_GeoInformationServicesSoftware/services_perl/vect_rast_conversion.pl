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
	"feature_types:s" => \my $feature_types,
	"output_data_type:s" => \my $output_data_type,
	"output_file:s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype
);

my @feature_types_array = ();
if ($feature_types) {
	@feature_types_array = split(/,/,$feature_types);
}



##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_file) && defined ($source_attribute) && defined ($resolution))) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

#2. check if all output types are one of the following: point, kernel, centroid, line, boundary, area, face
for my $feature_type (@feature_types_array) {
	my @valid_feature_types = ("point", "line", "area");
	if (! (grep $_ eq $feature_type, @valid_feature_types) ) {
		print "Only the following output types are allowed: point, line, area\n";
		exit 1;
	}
} 



#################
# Set variables #
#################

my $location="Location_$unique_code";

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";

# the input file should be a ZIP file
my $output_dir= "$work_dir/vector";
$input_file = Utils::unzip($input_file, $output_dir, $debug);


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


if (! defined ($feature_types)) {
	$feature_types = "";
} elsif ($feature_types ne "") {
	$feature_types = "type=$feature_types";
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

# import vector into new location
Utils::execute("v.in.ogr dsn=\"$input_file\" output=vector location=$location", $debug);

# change location
Utils::execute("g.mapset mapset=PERMANENT location=$location", $debug);

# set resolution
Utils::execute("g.region res=$resolution", $debug);

# vector to raster
Utils::execute("v.to.rast input=vector output=raster column=\"$source_attribute\" $feature_types", $debug);

my $output_raster= "$work_dir/raster";

# copy output_raster to output_file
Utils::execute("r.out.gdal input=raster output=${output_raster} $output_format $output_data_type", $debug);
	
Utils::execute("cp ${output_raster} ${output_file}" , $debug);


Utils::printEnd($unique_code, $debug);

