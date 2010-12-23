#!/usr/bin/perl 

#perl rast_calc.pl --unique_code=tmp_rast_calc_abc --input_file="/tmp/march_april_may/poly_aq_2007-03-01.nc" --output_file="/tmp/rast_calc" --calculation="(raster.35+raster.40+raster.45+raster.50+raster.55+raster.60+raster.65)/7"

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use lib "$FindBin::Bin/xsltp15_1";
use CGIXSLT;


## For the moment, we only calculate statistics on 1 input file


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $resources_dir=$params{'resources_dir'};
my $math_xsl=$params{'math_xsl'};
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_folder=s" => \my $input_folder,
	"calculation=s" => \my $MathML_calculation,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);


##########################
# Check input parameters #
##########################

# 1. every mandatory parameter available?
if (!(defined ($unique_code) && defined ($input_folder) && defined ($output_file) && defined ($MathML_calculation) )) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

# 2. check if input_folder is a directory
# glob input file names
if (!(-d $input_folder)) {
	print "The input_folder parameter should be a directory\n";
	exit 1;
}
my @input_files = glob("${input_folder}/*");

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

print "Instance $unique_code  --------- START\n";
print ".\n";

print "Convert MathML to r.mapcalc calculation expression\n";
#xslt_pl expects the stylesheet to have the .xsl extension and the source file to have the .xml extension
Utils::execute("mv $MathML_calculation ${MathML_calculation}.xml", $debug); 
my $params=CGIXSLT::params({style=>"${math_xsl}",'source'=>"${MathML_calculation}.xml"});
my $new_xml;
eval{$new_xml=CGIXSLT::transform();};
if($@){CGIXSLT::error($@);};

#print output to file
my $calc_output = "${work_dir}/calc";
open(CALC, ">${calc_output}") or die "cannot open $calc_output for writing: $!";
my $fh=\*CALC;
CGIXSLT::printResult($fh,$new_xml);
XSLT::xsl_FINISH();#for mod_perl
close(CALC);

#read calc ouptut into calculation variable
open(CALC, ${calc_output}) or die "cannot open $calc_output for reading: $!";
my @lines = <CALC>;
close(CALC);

my $calculation = "";
# read all lines
foreach my $line (@lines) {
	$calculation .= $line;
}

print "Calculation: $calculation\n";


my $rasters="";
my $index=1;
foreach my $input_file (@input_files) {
	
	#raster name will be "raster" followed by the index
	my $raster_name="raster$index";
	if ( $index == 1) {
		# import raster into new location
		system("r.in.gdal input=$input_file output=$raster_name location=$location");
		# change location
		system("g.mapset mapset=PERMANENT location=$location");
	}
	else {
		# import raster into new location
		system("r.in.gdal input=$input_file output=$raster_name");
	}
	#keep a comma seperated list of rasters
	$rasters="$rasters,$raster_name";
	
	$index += 1;
}



print "Change location to $location\n";
system("g.mapset mapset=PERMANENT location=$location");

print "Perform calculation\n"; 
system("r.mapcalc calc_output='$calculation'");

print "Export the file using format $output_format and data type $output_data_type\n";
system("r.out.gdal input=calc_output output=$output_file $output_format $output_data_type");

print ".\n";
print "Instance $unique_code  --------- END\n";






