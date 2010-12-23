#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use File::Basename;
use File::Find;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use OGRUtils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $wmc_template=$params{'wmc_template_rast_vect_polylines_intersection'};
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
	"input_vector_file=s" => \my $input_vector_file,
	"method=s" => \my $method,
	"order_id:s" => \my $order_id,
	"output=s" => \my $output_types,
	"output_vector:s" => \my $output_vector,
	"output_mimetype:s" => \my $output_mimetype,
	"output_wmc:s" => \my $output_wmc
);

my $output_type_file = 0;
my $output_type_WMC = 0;

#badsn, methods and output_types contain a comma seperated list
my @output_types = split(/,/,$output_types);



##########################
# Check input parameters #
##########################

#1. check mandatory parameters
if (!(defined ($unique_code) && defined ($input_raster_file) && defined ($band) && defined ($input_vector_file) &&  defined ($method) && @output_types)) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#2. output should be file or WMC
for my $output_type (@output_types) {
	if ($output_type ne "file" && $output_type ne "WMC") {
		print "Output type can only be file or WMC\n";
		exit 1;
	} else {
		if ($output_type eq "file") {
			$output_type_file = 1;
		}
		if ($output_type eq "WMC") {
			$output_type_WMC = 1;
		}
	}
}

#3. order_id is mandatory if output type is WMC
if ($output_type_WMC && (!defined($order_id) || $order_id eq "")) {
	print "order_id is a mandatory parameter is (one of) the output type(s) is WMC\n";
	exit 1;
}


#4. output_vector is mandatory if output type is file
if ($output_type_file && (!defined($output_vector) || $output_vector eq "")) {
	print "$output_vector is a mandatory parameter is (one of) the output type(s) is file\n";
	exit 1;
}

#5. $output_wmc is mandatory if output type is WMC
if ($output_type_WMC && (!defined($output_wmc) || $output_wmc eq "")) {
	print "output_wmc is a mandatory parameter is (one of) the output type(s) is WMC\n";
	exit 1;
}

#6. convert input method method to starpsan syntax
my $method_in_starspan_syntax = convertMethodToStarspanSyntax($method);
if ($method_in_starspan_syntax eq "") {
	print "The provided methods is not valid\n";
}



#################
# Set variables #
#################

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";


my $location="Location_$unique_code";

#dbf file in unzipped shapefile directory
my @dbf_files_in_dir;
my $dbf_file = "";


###############
#  Processing #
###############

Utils::printStart($unique_code, $debug);


# 1. CONVERT INPUT RASTER TO GEOTIFF
my $single_band_raster_file = "$work_dir/raster.tiff";
my $gdal_translate_command = "gdal_translate -b $band -of GTiff ${input_raster_file} ${single_band_raster_file}";
Utils::execute($gdal_translate_command, $debug);


# 2. CONVERT INPUT VECTOR TO SHAPEFILE (IF OUTPUT TYPE IS FILE)
if ($output_type_file) {
	# zip the vector file
	my $input_vector_dirname = dirname ($input_vector_file);
	my $input_vector_basename = basename ($input_vector_file);
	my $zipped_input_vector = "$work_dir/zipped_input_vector.zip";
	Utils::zip("zip $zipped_input_vector $input_vector_basename", $input_vector_dirname, $zipped_input_vector, $debug);
	
	# convert to zipped shapefile
	my $vector_file_as_zipped_shapefile="${work_dir}/shape.zip";
	my $vect_format_conv_unique_code="${unique_code}_vect_format_conv";
	my $convert_to_shapefile_command = "perl $perl_scripts_dir/vect_format_conv.pl --unique_code=\"$vect_format_conv_unique_code\" --input_file=\"$zipped_input_vector\" --output_file=\"$vector_file_as_zipped_shapefile\" --output_mimetype=\"application/x-esri-shapefile\"";
	Utils::execute($convert_to_shapefile_command, $debug);
	
	#unzip shapefile
	my $unzipped_shapefile_dir= "$work_dir/unzipped_shapefile";
	Utils::unzip($vector_file_as_zipped_shapefile, $unzipped_shapefile_dir, $debug);
	
	#find(sub { push @array, $File::Find::name }, $unzipped_shapefile_dir);
	find(\&addDBFfilesToArray, $unzipped_shapefile_dir);
	my $number_of_shapefiles = @dbf_files_in_dir;
	if ($number_of_shapefiles != 1) {
		print "Number of shapefiles in unzipped shapefile directory is not 1";
		exit;
	}
	$dbf_file = $dbf_files_in_dir[0];
}



# 3. CALCULATE STATISTICS
my $original_csv="${work_dir}/output_${method}.csv";
performStarspan("$original_csv", $input_vector_file, $single_band_raster_file, $method_in_starspan_syntax, "--fields gid");

# remove header line from csv file
my $csv_wo_header="$work_dir/output_${method}_wo_header.csv";
Utils::removeFirstLineFromFile($original_csv, $csv_wo_header);



if ($output_type_file) {
	
	# 4. ADD DBF TO DATABASE AND ADD AVERAGE COLUMN (IF OUTPUT TYPE IS FILE)
	my $output_dbf_file = "$work_dir/output_dbf";
	my $csv2db_file_unique_code="${unique_code}_vect_csv2db_polylines_intersection";
	# dbf driver gives an error:
	#   dbmi: Protocol error
	#   ERROR: Unable to open database <$GISDBASE/$LOCATION_NAME/$MAPSET/dbf/>
	# this is why we use postgres
	#my $csv2db_file_command="perl $perl_scripts_dir/vect_csv2db_polylines_intersection.pl --unique_code=\"$csv2db_file_unique_code\" --csv_input_file=\"$csv_wo_header\" --input_file=\"${dbf_file}\" --db_driver=\"dbf\" --mode=file --output_file=\"${output_dbf_file}\"";
	my $csv2db_file_command="perl $perl_scripts_dir/vect_csv2db_polylines_intersection.pl --unique_code=\"$csv2db_file_unique_code\" --csv_input_file=\"$csv_wo_header\" --input_file=\"${dbf_file}\" --db_driver=\"pg\" --db_setting=\"host=localhost,dbname=grass\" --db_user=postgres --db_psw=grespost --mode=file --output_file=\"${output_dbf_file}\" --column_name=\"${method}\"";

	print "CSV2DB command:\n";
	Utils::execute($csv2db_file_command, $debug);
	Utils::execute("mv $output_dbf_file $dbf_file", $debug);
	
	# 5. CONVERT OUTPUT VECTOR (IF OUTPUT TYPE IS FILE)
	# zip the vector file
	my $output_shapefile_dirname = dirname ($dbf_file);
	my $zipped_output_shapefile = "$work_dir/zipped_output_shapefile.zip";
	Utils::zip("zip $zipped_output_shapefile *", $output_shapefile_dirname, $zipped_output_shapefile, $debug);
	
	# convert to output mimetype
	my $vect_format_conv2_unique_code="${unique_code}_vect_format_conv2";
	my $vect_format_conv_command = "perl $perl_scripts_dir/vect_format_conv.pl --unique_code=\"$vect_format_conv2_unique_code\" --input_file=\"$zipped_output_shapefile\"  --output_file=\"$output_vector\" --output_mimetype=\"$output_mimetype\"";
	Utils::execute($vect_format_conv_command, $debug);

}



if ($output_type_WMC) {
	# 6. CALCULATE STATISTICS FOR AVG
	my $csv_avg_wo_header = "";
	if ($method_in_starspan_syntax eq "avg") {
		#use already created file, don't use starspan again
		$csv_avg_wo_header = $csv_wo_header;
	} else {
		my $original_csv_avg="${work_dir}/output_avg.csv";
		performStarspan("$original_csv_avg", $input_vector_file, $single_band_raster_file, "avg", "--fields gid");
		
		# remove header line from csv file
		$csv_avg_wo_header="$work_dir/output_avg_wo_header.csv";
		Utils::removeFirstLineFromFile($original_csv_avg, $csv_avg_wo_header);
	}

	# 7. CREATE WMC (IF OUTPUT TYPE IS WMC)
	my $csv2db_WMC_unique_code="${unique_code}_vect_csv2db_polylines_intersection";
	my $csv2db_WMC_command="perl $perl_scripts_dir/vect_csv2db_polylines_intersection.pl --unique_code=\"$csv2db_WMC_unique_code\" --csv_input_file=\"$csv_avg_wo_header\" --db_driver=\"pg\" --db_setting=\"host=localhost,dbname=genesis-gtad\" --db_user=postgres --db_psw=grespost --mode=WMC  --column_name=\"AQ-concentration\"";
	
	print "CSV2DB command:\n";
	Utils::execute($csv2db_WMC_command, $debug);
	
	
	# CONTEXT
	print "Creating WMC file\n";
	Utils::createFileFromTemplate($wmc_template, $output_wmc, ('##ORDER_ID##', $order_id));
}

# import raster into new location
#Utils::execute("r.in.gdal input=\"$input_raster_file\" output=raster location=$location", $debug);

# change location
#Utils::execute("g.mapset mapset=PERMANENT location=$location", $debug);

# r.profile		outputs the raster map layer values lying on user-defined line(s)
# v.in.ascii	creates a vector map from ASCII points file or ASCII vector file
#Utils::execute("r.profile -g input=raster profile=LIST_OF_LINEPOINTS res=OUTPUT_RESOLUTION | v.in.ascii output=vector columns='easting double precision, northing double precision, cumullength double precision, value int' fs=space", $debug);
#or without resolution (keep raster resolution):
##Utils::execute("r.profile -g input=raster profile=LIST_OF_LINEPOINTS | v.in.ascii output=vector columns='easting double precision, northing double precision, cumullength double precision, value int' fs=space", $debug);

# export vector
#Utils::execute("v.out.ogr input=vector dsn=$output $output_format $dataset_creation_option type=point", $debug);


Utils::printEnd($unique_code, $debug);


exit;

sub performStarspan {

	my ($output_file, $input_vector_file, $input_raster_files, $methods, $fields) = @_;

	# output:
	#star_stats.csv
	#star__table.csv
	
	#starspan2 --vector "/var/www/html/genesis/data/GIM_test_data/gml/gml2_linestrings_bayern.xml" --raster "/var/www/html/genesis/data/GIM_test_data/averaged_raster_data/averaged_2007-08-01_2007-08-10.tiff" --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats avg --fields id
	my $starspan_command="starspan2 --vector $input_vector_file --raster $input_raster_files --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats $methods $fields;";
	print "STARSPAN\n";
	Utils::execute($starspan_command, $debug);
	
	# move output
	Utils::execute("mv star_stats.csv $output_file", $debug);
	
	#remove star__table.csv
	unlink "star__table.csv";
}

sub addDBFfilesToArray {
	
	# if this is a regular file with extension .shp
	if ( (-f "$File::Find::name") && $File::Find::name =~ /\.dbf$/) {
		push (@dbf_files_in_dir, $File::Find::name);
	}
}



# convert the name of the method as input/output parameter to the name of the method in univar output
sub convertMethodToStarspanSyntax {
	
	my $input_output_method =  $_[0];
	
	my $method_in_starspan_syntax = "";
	switch ( $input_output_method ) {
		case "minimum"					{ $method_in_starspan_syntax="min"; }
		case "maximum"					{ $method_in_starspan_syntax="max"; }
		case "average"					{ $method_in_starspan_syntax="avg"; }
		case "median"					{ $method_in_starspan_syntax="median"; }
		case "mode"						{ $method_in_starspan_syntax="mode"; }
		case "stddev"					{ $method_in_starspan_syntax="stddev"; }
		case "sum"						{ $method_in_starspan_syntax="sum"; }
	}
	return $method_in_starspan_syntax;
}



