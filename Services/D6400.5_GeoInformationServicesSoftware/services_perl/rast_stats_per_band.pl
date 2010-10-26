#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use Date::Calc qw(Time_to_Date);
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



print "Input parameters: \n";
Utils::printList(@ARGV, "\n\n");

# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"bands=s" => \my $bands,
	"methods=s" => \my $methods,
	"parameter:s" => \my $parameter,
	"longitude_dataset:s" => \my $longitude_dataset,
	"latitude_dataset:s" => \my $latitude_dataset,
	"output=s" => \my $output
);

#bands and methods contain a comma seperated list
my @bands = split(/,/,$bands);
my @methods = split(/,/,$methods);


##########################
# Check input parameters #
##########################

# 1. every mandatory parameter available?
if (!(defined ($unique_code) && defined ($input_file) && @bands && @methods && defined ($output) ) ) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

# 2.convert input method names to univar method names
my @methods_in_univar = ();
for my $method (@methods) {
	my $method_in_univar_syntax = convertMethodToUnivarSyntax($method);
	#only add the method to the list if it is a known method
	if ($method_in_univar_syntax ne "") {
		push (@methods_in_univar, $method_in_univar_syntax);
	}
}
# check if the number of converted methods is > 0
my $number_of_method_in_univar_syntax = @methods_in_univar;
if ($number_of_method_in_univar_syntax == 0) {
	print "None of the provided methods is valid\n";
	exit 1;
}

#3. if longitude_dataset is defined, latitude_dataset should also be defined and vice versa
if ( (defined($longitude_dataset) && $longitude_dataset ne "") && (!(defined($latitude_dataset) && $latitude_dataset ne "")) ) {
	print "If the longitude dataset is provided, the latitude dataset should also be provided\n";
	exit 1;
}
if ( (!(defined($longitude_dataset) && $longitude_dataset ne "")) && (defined($latitude_dataset) && $latitude_dataset ne "") ) {
	print "If the latitude dataset is provided, the longitude dataset should also be provided\n";
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


my $input = $input_file;
if ($parameter && $parameter ne "") {
	$input = "NETCDF:\"${input_file}\":${parameter}";
}


##############
# Processing #
##############

print "Instance $unique_code  --------- START\n";
print ".\n";

#name of bandnumber parameter in univar output file
my $bandnumber_param = "bandnumber";

my @univar_output_files = ();
my @univar_output_files_bandnumber = ();

#import bands and calculate statistics
my $count_input_raster_maps = 1;
print "Import the selected bands of the input file in the GRASS Location $location\n";
for my $band(@bands) {
	my $raster_map = "raster${count_input_raster_maps}";
	if ($count_input_raster_maps==1) {
		# if count=1, create new location and change to that location
		print "r.in.gdal command\n";
		my $r_in_gdal_command = "r.in.gdal input=$input band=$band output=$raster_map location=$location";
		Utils::execute($r_in_gdal_command, $debug);
		
		print "Change location to $location\n";
		my $g_mapset_command = "g.mapset mapset=PERMANENT location=$location";
		Utils::execute($g_mapset_command, $debug);
	} else {
		print "r.in.gdal command\n";
		my $r_in_gdal_command = "r.in.gdal input=$input band=$band output=$raster_map";
		Utils::execute($r_in_gdal_command, $debug);
	}
	
	print "Calculate statistics\n";
	my $univar_output_file = "${work_dir}/univar_output_${raster_map}";
	#execute univar
	my $r_univar_command = "r.univar -eg map=$raster_map percentile=10,20,30,40,50,60,70,80,90 > $univar_output_file";
	Utils::execute($r_univar_command, $debug);
	
	#add name of output file to a list and the corresponding bandnumber to another list
	push(@univar_output_files, $univar_output_file);
	push(@univar_output_files_bandnumber, $band);
	
	$count_input_raster_maps++;
}



# quick check if number_of_univar_output_files = number_of_univar_output_files
# should be OK
my $number_of_univar_output_files = @univar_output_files;
my $number_of_univar_output_files_bandnumbers = @univar_output_files_bandnumber;
if ($number_of_univar_output_files!=$number_of_univar_output_files_bandnumbers) {
	print "number_of_univar_output_files is ${number_of_univar_output_files}, but number_of_univar_output_files_bandnumbers is ${number_of_univar_output_files_bandnumbers}!";
}


# calculate BBOX
my $bbox_minx = 0; my $bbox_miny = 0; my $bbox_maxx = 0; my $bbox_maxy = 0;
# use longitude and latitude datasets if available
if (defined($longitude_dataset) && $longitude_dataset ne "" && defined($latitude_dataset) && $latitude_dataset ne "") {
	my @min_max_longitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"${input_file}\":${longitude_dataset}");
	my @min_max_latitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"${input_file}\":${latitude_dataset}");
	$bbox_minx = $min_max_longitude[0];
	$bbox_miny = $min_max_latitude[0];
	$bbox_maxx = $min_max_longitude[1];
	$bbox_maxy = $min_max_latitude[1];
} else {
	($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy) = GDALUtils::getBBox($input);
}


# get polygon positions
my $polygon_pos_1 = "${bbox_miny} ${bbox_minx}";
my $polygon_pos_2 = "${bbox_miny} ${bbox_maxx}";
my $polygon_pos_3 = "${bbox_maxy} ${bbox_maxx}";
my $polygon_pos_4 = "${bbox_maxy} ${bbox_minx}";
my $polygon_pos_5 = $polygon_pos_1;


# store information about the bands in the raster in a hash
# the returned information is a hashmap with this structure:
#		bandnumber
#			'timestamp'
#				timestamp
#			'variable_name'
#				variable_name
#		bandnumber
#			'timestamp'
#				timestamp
#			'variable_name'
#				variable_name
#		...
my %bandinfo = GDALUtils::storeBandInfoACRIFileInHash("band", "$input");



#create GML output file
my $xml_output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
$xml_output .= "<rs:BandCollection xmlns:rs=\"http://www.gim.be/genesis/RasterStatistics\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xsi:schemaLocation=\"http://www.gim.be/genesis/RasterStatistics http://einstein.gim.be/schemas/Test/RasterStatistics2.xsd\">\n";
$xml_output .= "    <gml:boundedBy>\n";
$xml_output .= "		<gml:Envelope srsName=\"EPSG:4326\">\n";
$xml_output .= "			<gml:lowerCorner>${bbox_miny} ${bbox_minx}</gml:lowerCorner>\n";
$xml_output .= "			<gml:upperCorner>${bbox_maxy} ${bbox_maxx}</gml:upperCorner>\n";
$xml_output .= "		</gml:Envelope>\n";
$xml_output .= "	</gml:boundedBy>\n";

	
	
#loop all output files
for (my $index = 0; $index < $number_of_univar_output_files; $index++) {
	
	my $file = $univar_output_files[$index];
	my $bandnumber = $univar_output_files_bandnumber[$index];
	
	my $timestamp_in_gml_output = "";
	my $name_in_gml_output = "";
	my $description_in_gml_output = "";
	
	my $bandinfo_values_ref = $bandinfo{$bandnumber};
	if (defined $bandinfo_values_ref) {
		#dereference hash using % sigil (actually, 2 dimensional hashmaps don't exist in Perl,
		#	 so the %bandinfo_values_ref is not a hashmap, but the link to a hashmap)
		my %bandinfo_values = %$bandinfo_values_ref;
		
		my $name = $bandinfo_values{'variable_name'};
		if ($name && $name ne "") {
			$name_in_gml_output = $name; 
			$description_in_gml_output = $name;
		}
		
		my $timestamp = $bandinfo_values{'timestamp'};
		if ($timestamp && $timestamp ne "") {
			(my $year, my $month, my $day, my $hour, my $min, my $sec) = Utils::getDateInFormatYYYYMMDDhhmmss(Time_to_Date($timestamp));
			$timestamp_in_gml_output = "${year}-${month}-${day} ${hour}:${min}:${sec}";
		}
	}
	
	
	# read file
	open(IN, $file) or die "cannot open $file for reading: $!";
	my @lines = <IN>;
	close(IN);

	$xml_output .= "	<gml:featureMember>\n";
	$xml_output .= "		<rs:BandFeature gml:id=\"feature${bandnumber}\">\n";
	if ($name_in_gml_output && $name_in_gml_output ne "") {
		$xml_output .= "			<gml:name>${name_in_gml_output}</gml:name>\n";
	}
	if ($description_in_gml_output && $description_in_gml_output ne "") {
		$xml_output .= "			<gml:description>${description_in_gml_output}</gml:description>\n";
	}
	$xml_output .= "			<rs:bandNumber>${bandnumber}</rs:bandNumber>\n";
	if ($timestamp_in_gml_output && $timestamp_in_gml_output ne "") {
		$xml_output .= "			<rs:timestamp>${timestamp_in_gml_output}</rs:timestamp>\n";
	}
	
		
	# read all lines
	foreach my $line (@lines) {
		# check if the line contains a parameter
		# this is done by checking if the line starts with one or more non-space characters, followed by a = sign, followed by at least one character
		if ($line =~ /^[\S]+=.+$/ ) {
			# split line on = sign
			my @fields = split(/=/, $line);
			my $number_of_fields = @fields;
			if ($number_of_fields == 2) {
				my $parameter = $fields[0];
				my $value = $fields[1];
				chomp($parameter);
				chomp($value);
				#add this parameter to the output if it was requested
				if (grep $_ eq $parameter, @methods_in_univar) {
					my $method_in_input_output_syntax = convertMethodToInputOutputSyntax($parameter);
					#if the parameter cannot be converted, just use the parameter in the output XML
					# this should never happen!
					if ($method_in_input_output_syntax eq "") {
						$xml_output .= "			<rs:${parameter}>${value}</rs:${parameter}>\n";
					} else {
						$xml_output .= "			<rs:${method_in_input_output_syntax}>${value}</rs:${method_in_input_output_syntax}>\n";
					}
				}
			}
		}
	}
	$xml_output .= "			<rs:geometry>\n";
	$xml_output .= "				<gml:Polygon srsName=\"EPSG:4326\">\n";
	$xml_output .= "					<gml:exterior>\n";
	$xml_output .= "						<gml:LinearRing>\n";
	$xml_output .= "							<gml:pos>$polygon_pos_1</gml:pos>\n";
	$xml_output .= "							<gml:pos>$polygon_pos_2</gml:pos>\n";
	$xml_output .= "							<gml:pos>$polygon_pos_3</gml:pos>\n";
	$xml_output .= "							<gml:pos>$polygon_pos_4</gml:pos>\n";
	$xml_output .= "							<gml:pos>$polygon_pos_5</gml:pos>\n";
	$xml_output .= "						</gml:LinearRing>\n";
	$xml_output .= "					</gml:exterior>\n";
	$xml_output .= "				</gml:Polygon>\n";
	$xml_output .= "			</rs:geometry>\n";
	$xml_output .= "		</rs:BandFeature>\n";
	$xml_output .= "	</gml:featureMember>\n";
}
$xml_output .= "</rs:BandCollection>\n";

# write output file
open (OUT, ">$output") or die "cannot open $output for writing: $!";
print OUT "$xml_output"; 
close(OUT) or die "cannot close $output: $!";

print ".\n";
print "Instance $unique_code  --------- END\n";


exit;


# convert the name of the method as input/output parameter to the name of the method in univar output
sub convertMethodToUnivarSyntax {
	
	my $input_output_method =  $_[0];
	
	my $method_in_univar_syntax = "";
	
	switch ( $input_output_method ) {
		case "numberOfCells" 			{ $method_in_univar_syntax="n"; }
		case "numberOfNullCells"		{ $method_in_univar_syntax="null_cells"; }
		case "minimum"					{ $method_in_univar_syntax="min"; }
		case "maximum"					{ $method_in_univar_syntax="max"; }
		case "range"					{ $method_in_univar_syntax="range"; }
		case "arithmeticMean"			{ $method_in_univar_syntax="mean"; }
		case "meanOfAbsoluteValues"		{ $method_in_univar_syntax="mean_of_abs"; }
		case "standardDeviation"		{ $method_in_univar_syntax="stddev"; }
		case "populationVariance"		{ $method_in_univar_syntax="variance"; }
		case "coefficientOfVariation"	{ $method_in_univar_syntax="coeff_var"; }
		case "sum"						{ $method_in_univar_syntax="sum"; }
		case "firstQuartile"			{ $method_in_univar_syntax="first_quartile"; }
		case "median"					{ $method_in_univar_syntax="median"; }
		case "thirdQuartile"			{ $method_in_univar_syntax="third_quartile"; }
	}
	return $method_in_univar_syntax;
}


# convert the name of the method in univar output to the name of the method as input/output parameter
sub convertMethodToInputOutputSyntax {
	
	my $method_in_univar_syntax =  $_[0];
	
	my $input_output_method = "";
	
	switch ( $method_in_univar_syntax ) {
		case "n" 				{ $input_output_method="numberOfCells"; }
		case "null_cells"		{ $input_output_method="numberOfNullCells"; }
		case "min"				{ $input_output_method="minimum"; }
		case "max"				{ $input_output_method="maximum"; }
		case "range"			{ $input_output_method="range"; }
		case "mean"				{ $input_output_method="arithmeticMean"; }
		case "mean_of_abs"		{ $input_output_method="meanOfAbsoluteValues"; }
		case "stddev"			{ $input_output_method="standardDeviation"; }
		case "variance"			{ $input_output_method="populationVariance"; }
		case "coeff_var"		{ $input_output_method="coefficientOfVariation"; }
		case "sum"				{ $input_output_method="sum"; }
		case "first_quartile"	{ $input_output_method="firstQuartile"; }
		case "median"			{ $input_output_method="median"; }
		case "third_quartile"	{ $input_output_method="thirdQuartile"; }
	}
	return $input_output_method;
}
