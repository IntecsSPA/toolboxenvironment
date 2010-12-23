#!/usr/bin/perl 

#perl rast_group.pl --unique_code=tmp_rast_group_abcd --input_file="/home_local/projects/c08678b/work/tmp_abc/tmp_avg_1_day_1172739600" --input_file="/home_local/projects/c08678b/work/tmp_abc/tmp_avg_1_day_1172826000" --input_file="/home_local/projects/c08678b/work/tmp_abc/tmp_avg_1_day_1172912400" --output_file="/tmp/group"


use Getopt::Long;
use Switch;
use strict;
use warnings;

# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my @input_files,
	"output_file=s" => \my $output_file,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);

#input files can be provided as a mix of multiple input parameters or seperated by a comma, Eg. --input_file=c:\test --input_file=d:\test,e:\test --input_file=f:\test
@input_files = split(/,/,join(',',@input_files));


##########################
# Check input parameters #
##########################

if (!(defined ($unique_code) && @input_files && defined ($output_file) )) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#################
# Set variables #
#################

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


print "Group the rasters\n"; 
system("i.group group=raster_group input=$rasters");

print "Export the file using format $output_format and data type $output_data_type\n";
system("r.out.gdal input=raster_group output=$output_file $output_format $output_data_type");
       
print ".\n";
print "Instance $unique_code  --------- END\n";





