#!/usr/bin/perl 


#perl rast_vect_polygon_intersection.pl --unique_code=tmp_vect_overlay --input_vector_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp" --input_raster_file="/tmp/output" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-03-10 19:30:00,2007-03-31 07:30:00,2007-04-19 19:30:00,2007-05-06 07:30:00" --output_wms=/tmp/wms --output_sos=/tmp/sos
#perl rast_vect_polygon_intersection.pl --unique_code=tmp_vect_overlay --input_vector_file="D:\testdata\poly\poly_aq_2007-03" --input_raster_file="D:\testdata\poly\poly_aq_2007-05" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-03-10 19:30:00,2007-03-31 07:30:00,2007-04-19 19:30:00,2007-05-06 07:30:00"

#perl rast_vect_polygon_intersection.pl --unique_code=genesis_demo --input_vector_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp" --input_raster_file="/tmp/output" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-04-03 09:00:00,2007-04-08 09:00:00,2007-04-13 09:00:00,2007-04-18 09:00:00,2007-04-23 09:00:00,2007-04-28 09:00:00,2007-05-03 09:00:00,2007-05-08 09:00:00,2007-05-13 09:00:00,2007-05-18 09:00:00,2007-05-23 09:00:00,2007-05-28 09:00:00" --output_wms=/tmp/wms --output_sos=/tmp/sos


use FindBin ();
use lib "$FindBin::Bin/lib";

use Getopt::Long;
use Date::Calc qw(Date_to_Time Time_to_Date Add_Delta_Days);
use strict;
use warnings;
use Cwd;
use File::Basename;
use Utils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $shell_scripts_dir=$params{'shell_scripts_dir'};
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $wmc_template=$params{'wmc_template_rast_vect_polygon_intersection_old'};
my $debug=$params{'debug'};


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_raster_file=s" => \my $input_raster_file,
	"input_vector_file=s" => \my $input_vector_file,
	"output_types=s" => \my $output_types,
	"polygon_ID_property:s" =>  \my $polygon_ID_property,
	"elevation:i" => \my $elevation,
	"parameter:s" => \my $parameter,
	"timestamps:s" => \my $timestamps,
	"output_shape:s" => \my $output_shape,
	"output_csv:s" => \my $output_csv,
	"output_context:s" => \my $output_context
);

my $output_type_db = 0;
my $output_type_csv = 0;
my $output_type_shape = 0;

#timestamps contains a comma seperated list
my @timestamps = split(/,/,$timestamps);

#output_types contains a comma seperated list
my @output_types = split(/,/,$output_types);


##########################
# Check input parameters #
##########################

# 1. every mandatory parameter available?
if (!(defined ($unique_code) && defined ($input_raster_file) && defined ($input_vector_file) && @output_types)) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}

#2. output should be db, csv or shape
for my $output_type (@output_types) {
	if ($output_type ne "db" && $output_type ne "csv" && $output_type ne "shape") {
		print "Output type should only be shape, csv or db\n";
		exit 1;
	} else {
		if ($output_type eq "db") {
			$output_type_db = 1;
		}
		if ($output_type eq "csv") {
			$output_type_csv = 1;
		}
		if ($output_type eq "shape") {
			$output_type_shape = 1;
		}
	}
}

#3. every specific parameter for output=db available?
if ($output_type_db && (!(defined ($polygon_ID_property) && defined ($elevation) && defined ($parameter) && @timestamps && defined ($output_context))) ) {
	print "When the output=db, polygon_ID_property, elevation, parameter, timestamps and output_context are required parameters\n";
	exit 1;
}
#3.b. check if parameter equals no2
#if ($output_type_db) {
#	if ($parameter ne "no2") {
#		print "Output parameter should be no2\n";
#		exit 1;
#	}
#}

#4. every specific parameter for output=csv available?
if ($output_type_csv && !defined ($output_csv)) {
	print "When the output=csv, output_csv is a required parameter\n";
	exit 1;
}
#5. every specific parameter for output=shape available?
if ($output_type_shape && !defined ($output_shape)) {
	print "When the output=shape, output_shape is a required parameter\n";
	exit 1;
}



#################
# Set variables #
#################

#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";


##############
# Processing #
##############

Utils::printStart($unique_code, $debug);

#unpack vector zip file
my $input_vector_dir= "$work_dir/vector";
# create vector directory
mkdir($input_vector_dir, 0755) or die "cannot mkdir $input_vector_dir: $!";
Utils::execute("unzip \"$input_vector_file\" -d \"$input_vector_dir\"", $debug);
my @input_vector_files = ();
push (@input_vector_files, glob("${input_vector_dir}/*.shp"));
my $number_of_shp_files_in_zip=@input_vector_files;
if ($number_of_shp_files_in_zip != 1) {
	print "The input vector zip file should contain exactly one .shp file!\n";
	print "Found .shp files in zip file: $number_of_shp_files_in_zip\n";
	exit 1;
}
$input_vector_file = $input_vector_files[0];


if ($output_type_db) {
	
	my @input_raster_files = ();

	my $nr_of_bands_in_file = @timestamps;

	#split file in several files per band
	my $rast_split_bands_unique_code="${unique_code}_rast_split_bands";
	#my $rast_split_bands_command="$shell_scripts_dir/rast_split_bands.sh \"$rast_split_bands_unique_code\" \"$input_raster_file\" $nr_of_bands_in_file";
	# output will be $work_dir/outputX
	my $rast_split_bands_command="perl $perl_scripts_dir/rast_split_bands.pl --unique_code=\"$rast_split_bands_unique_code\" --input_file=\"$input_raster_file\" --output_folder=\"$work_dir\" --output_prefix=output";
	print "SPLIT BANDS:\n";
	Utils::execute($rast_split_bands_command, $debug);
	
	
	push (@input_raster_files, glob("$work_dir/output*"));

	print "Raster files: @input_raster_files\n";

	my $input_raster_files = "@input_raster_files";
	my $original_csv="${work_dir}/output.csv";
	performStarspan("$original_csv", $input_vector_file, $input_raster_files, "--fields $polygon_ID_property");

	# remove header line from csv file
	my $csv_wo_header="$work_dir/output_wo_header.csv";
	open (IN, $original_csv) or die "cannot open $original_csv for reading: $!";
	open (OUT, ">$csv_wo_header") or die "cannot open $csv_wo_header for writing: $!";
	my $linenumber=1;
	while (<IN>) {
		if ($linenumber!=1) {
			print OUT $_;
		}
		$linenumber = $linenumber + 1;
	}
	close(IN) or die "cannot close $original_csv: $!";
	close(OUT) or die "cannot close $csv_wo_header: $!";
	
	
	my $sed_expr="$work_dir/sed_expr.txt";
	open (OUT, ">>$sed_expr") or die "cannot open $sed_expr for writing: $!";
	my $band_index=1;
	foreach my $timestamp (@timestamps) {
		# change RID from CSV file
		# RID should contain timestamp
		
		# add expression to file containing all expressions
		#name of input file was output<i>
		print OUT "s|,output${band_index},|,${timestamp},|g\n"; 
		$band_index = $band_index + 1;
		
	}
	close(OUT) or die "cannot close $sed_expr: $!";
	
	my $csv_wo_header_w_timestamp = "${work_dir}/output_wo_header_w_timestamp.csv";
	my $sed_command="sed -f $sed_expr $csv_wo_header > $csv_wo_header_w_timestamp";
	print "SED command\n";
	Utils::execute($sed_command, $debug);
	
	
	#my $rast_vect_polygon_intersection_unique_code="${unique_code}_vect_csv2db";
	##my $csv2db_command="$shell_scripts_dir/vect_csv2db.sh \"$rast_vect_polygon_intersection_unique_code\" \"$csv_wo_header_w_timestamp\" $unique_code $elevation $parameter \"pg\" \"host=localhost,dbname=grass\" rast_tmp rast postgres grespost";
	#my $csv2db_command="perl $perl_scripts_dir/vect_csv2db.pl --unique_code=\"$rast_vect_polygon_intersection_unique_code\" --input_file=\"$csv_wo_header_w_timestamp\" --order_id=\"$unique_code\" --elevation=\"$elevation\" --parameter=\"$parameter\" --db_driver=\"pg\" --db_setting=\"host=localhost,dbname=grass\" --tmp_table=rast_tmp --perm_table=rast --db_user=postgres --db_psw=grespost";
	#print "CSV2DB command:\n";
	#Utils::execute($csv2db_command, $debug);

	#USE CSV2SOS instead of CSV2DB
	#perl vect_csv2sos.pl --input_file="/var/www/html/genesis/data/GIM_test_data/CSV/output_wo_header_w_timestamp.csv" --order_id="test1" --parameter="no2" 
	my $csv2sos_command="perl $perl_scripts_dir/vect_csv2sos.pl --input_file=\"$csv_wo_header_w_timestamp\" --order_id=\"$unique_code\" --parameter=\"$parameter\"";
	print "CSV2SOS command:\n";
	Utils::execute($csv2sos_command, $debug);
	
	
	my $all_timestamps_in_ISO8601 = "";
	my $default_timestamp_in_ISO8601 = "";
	foreach my $timestamp (@timestamps) {
		#if this is not the first timestamp, add a comma before adding the new timestamp
		if ($all_timestamps_in_ISO8601 ne "") {
			$all_timestamps_in_ISO8601 .= ",";
		}
		$all_timestamps_in_ISO8601 .= Utils::convertDateTimeToISO8601($timestamp);
		# make the first timestamp the default one
		if ($default_timestamp_in_ISO8601 eq "") {
			$default_timestamp_in_ISO8601 = $all_timestamps_in_ISO8601;
		}
	}
				
	if ($all_timestamps_in_ISO8601 eq "" || $default_timestamp_in_ISO8601 eq "") {
		print "No timestamps in ISO8601 format!\n";
		print "Check timestamps parameter: $timestamps\n";
		exit 1;
	}

	# CONTEXT
	print "Creating WMC file\n";
	Utils::createFileFromTemplate($wmc_template, $output_context, 
		('##WMS_ORDER_ID##', $unique_code, '##WMS_DEFAULT_TIME##', $default_timestamp_in_ISO8601, '##WMS_ALL_TIMES##', $all_timestamps_in_ISO8601, '##SOS_ORDER_ID##', $unique_code));
}	
	

if ($output_type_shape || $output_type_csv) {

	my $original_csv="${work_dir}/output.csv";
	performStarspan("$original_csv", $input_vector_file, $input_raster_file, "");

	# copy original shape file
	#my $copy_shape_files_command = "$shell_scripts_dir/copy_shape_files.sh \"/var/www/html/genesis/data/Bayern/deud52________a8\" \"$work_dir/output_shape\"";
	
	# remove FID and RID from CSV file
	# remove the first field (until ,) in every line (FID-field)
	# remove first occurrence of 'RID' (in header), remove $input_raster_filename in every line
	my $input_raster_filename = basename ($input_raster_file);
	my $sed_command="sed -e \"s|^[^,]*,||g\"";
	   $sed_command="$sed_command -e \"s|,RID,|,|\" -e \"s|,$input_raster_filename,|,|g\"";
	   $sed_command="$sed_command $original_csv > $work_dir/star_stats_shape_wo_RID.csv";
	print "SED command\n";
	Utils::execute($sed_command, $debug);
	
	if ($output_type_csv) {
		my $copy_csv_command = "cp \"${work_dir}/star_stats_shape_wo_RID.csv\" \"$output_csv\"";
		print "Copy CSV command\n";
		Utils::execute($copy_csv_command, $debug);
	}
	
	if ($output_type_shape) {
		# overwrite dbf file of original shape file by creating a new dbf file from CSV output
		my $ogr2ogr_command = "ogr2ogr -f \"ESRI Shapefile\" \"${work_dir}/star_stats_shape.shp\" \"${work_dir}/star_stats_shape_wo_RID.csv\"";
		print "OGR command\n";
		Utils::execute($ogr2ogr_command, $debug);

		#get name of dbf file
		# match set parentheses around (.)* to match all characters
		# the parentheses around . only match the last character
		if ($input_vector_file=~/^((.)*)\.shp$/) {
			
			my $dbf_filename = "${1}.dbf";
		
			my $copy_dbf_command = "cp \"${work_dir}/star_stats_shape.dbf\" \"$dbf_filename\"";
			print "Copy DBF command\n";
			Utils::execute($copy_dbf_command, $debug);

			# zip the shapefiles
			Utils::zip("zip $output_shape *", $input_vector_dir, $output_shape, $debug);
		} else {
			print "Input vector file $input_vector_file does not match regular expression /^((.)*)\.shp$/\n";
			print "Therefore the output shapefile cannot be created\n";
		}
	}
}


Utils::printEnd($unique_code, $debug);

exit;

sub performStarspan {

	my ($output_file, $input_vector_file, $input_raster_files, $fields) = @_;

	# output:
	#star_stats.csv
	#star__table.csv
	
	#starspan2 --vector D:\Bayern\a8\deud51________a8.shp --raster D:\Bayern\starrast\output1.tif D:\Bayern\starrast\output2.tif D:\Bayern\starrast\output3.tif --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats min max avg median stdev --fields ID --delimiter ;
	#starspan2 --vector /var/www/html/genesis/data/Bayern/deud52________a8.shp --raster /home_local/projects/c08678b/scripts/starrast/output1.tif /home_local/projects/c08678b/scripts/starrast/output2.tif /home_local/projects/c08678b/scripts/starrast/output3.tif --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats min max avg median stdev --fields ID --delimiter \;
	#$input_vector_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp";
	my $starspan_command="starspan2 --vector $input_vector_file --raster $input_raster_files --out-prefix star_ --out-type table --pixprop 0.0 --summary-suffix stats.csv --stats min max avg median stdev $fields;";
	print "STARSPAN\n";
	Utils::execute($starspan_command, $debug);
	
	# move output
	Utils::execute("mv star_stats.csv $output_file", $debug);
	
	#remove star__table.csv
	unlink "star__table.csv";
}
