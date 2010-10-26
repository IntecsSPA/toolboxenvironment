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
	"input_raster_file=s" => \my $input_raster_file,
	"band=i" => \my $band,
	"min_level:f" => \my $min_level,
	"max_level:f" => \my $max_level,
	"step=f" => \my $step,
	"output_vector_file=s" => \my $output_vector_file,
	"output_mimetype:s" => \my $output_mimetype
);



##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster_file) && defined ($band) && defined ($step) && defined ($output_vector_file))) {
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


my $min_level_in_r_contour="";
if (! defined ($min_level)) {
	$min_level_in_r_contour="";
} elsif ($min_level eq 0) {
	$min_level_in_r_contour="";
} else {
	$min_level_in_r_contour="minlevel=${min_level}";
}

my $max_level_in_r_contour="";
if (! defined ($max_level)) {
	$max_level_in_r_contour="";
} elsif ($max_level eq 0) {
	$max_level_in_r_contour="";
} else {
	$max_level_in_r_contour="maxlevel=${max_level}";
}




my $output_format="";
if (defined($output_mimetype) && $output_mimetype ne "") {
	print "Set output format\n";
	print "Output MimeType: $output_mimetype\n";
	$output_format = OGRUtils::getOGRFormatByMimeTypeInGrassStyle($output_mimetype);
	if ($output_format ne "") {
		$output_format = "format=\"$output_format\"";
	}
	print "Output format: $output_format\n";
}

#If MapInfo File is the output format, specify that we want to create MIF/MID files
#Otherwise, TAB files are created
my $dataset_creation_option = "";

if ($output_format eq "format=\"MapInfo File\"") {
	$dataset_creation_option = "dsco FORMAT=MIF";
}


###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);

print "Import the input file in the GRASS Location $location\n";
Utils::execute("r.in.gdal input=$input_raster_file output=raster band=$band location=$location", $debug);

print "Change location to $location\n";
Utils::execute("g.mapset mapset=PERMANENT location=$location", $debug);

print "Perform contouring\n";
Utils::execute("r.contour input=raster output=output_vector ${min_level_in_r_contour} ${max_level_in_r_contour} step=${step}", $debug);


#if output is shapefile, ZIP it
if ( (!defined($output_mimetype)) && $output_mimetype eq "" || $output_mimetype eq "application/x-esri-shapefile") {
	
	# create output directory
	my $output_dir = "$work_dir/output";
	mkdir($output_dir, 0755) or die "cannot mkdir $output_dir: $!";
	
	# create output (we set the output type hardcoded to line, since r.contour will only create lines)
	Utils::execute("v.out.ogr input=output_vector dsn=$output_dir $output_format $dataset_creation_option type=line", $debug);
	
	# zip the shapefiles
	Utils::zip("zip $output_vector_file *", $output_dir, $output_vector_file, $debug);
	
} else {
	# create output (we set the output type hardcoded to line, since r.contour will only create lines)
	Utils::execute("v.out.ogr input=output_vector dsn=$output_vector_file $output_format $dataset_creation_option type=line", $debug);
}

Utils::printEnd($unique_code, $debug);


