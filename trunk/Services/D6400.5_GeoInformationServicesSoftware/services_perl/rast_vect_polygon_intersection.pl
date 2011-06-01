#!/usr/bin/perl 


#perl rast_vect_polygon_intersection.pl --unique_code=tmp_vect_overlay --input_vector_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp" --input_raster_file="/tmp/output" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-03-10 19:30:00,2007-03-31 07:30:00,2007-04-19 19:30:00,2007-05-06 07:30:00" --output_wms=/tmp/wms --output_sos=/tmp/sos
#perl rast_vect_polygon_intersection.pl --unique_code=tmp_vect_overlay --input_vector_file="D:\testdata\poly\poly_aq_2007-03" --input_raster_file="D:\testdata\poly\poly_aq_2007-05" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-03-10 19:30:00,2007-03-31 07:30:00,2007-04-19 19:30:00,2007-05-06 07:30:00"

#perl rast_vect_polygon_intersection.pl --unique_code=genesis_demo --input_vector_file="/var/www/html/genesis/data/Bayern/deud52________a8.shp" --input_raster_file="/tmp/output" --output=db --elevation=2500 --parameter=NO2 --timestamps="2007-04-03 09:00:00,2007-04-08 09:00:00,2007-04-13 09:00:00,2007-04-18 09:00:00,2007-04-23 09:00:00,2007-04-28 09:00:00,2007-05-03 09:00:00,2007-05-08 09:00:00,2007-05-13 09:00:00,2007-05-18 09:00:00,2007-05-23 09:00:00,2007-05-28 09:00:00" --output_wms=/tmp/wms --output_sos=/tmp/sos


use Getopt::Long;
use Cwd;
use Date::Calc qw(Date_to_Time Time_to_Date Add_Delta_Days);
use File::Basename;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $shell_scripts_dir=$params{'shell_scripts_dir'};
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $geoserver_url=$params{'geoserver_url'};
my $sos_url=$params{'sos_url'};
my $wmc_template=$params{'wmc_template_rast_vect_polygon_intersection'};
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
	"output=s" => \my $output_types,
	"polygon_ID_property:s" =>  \my $polygon_ID_property,
	"elevation:i" => \my $elevation,
	"parameter:s" => \my $parameter,
	"timestamps:s" => \my $timestamps,
	"output_shape:s" => \my $output_shape,
	"output_csv:s" => \my $output_csv,
	"output_wmc:s" => \my $output_wmc
);

my $output_type_wmc = 0;
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

#2. output should be WMC, CSV, or shape
for my $output_type (@output_types) {
	if ($output_type ne "WMC" && $output_type ne "CSV" && $output_type ne "shape") {
		print "Output type can only be CSV, shape or WMC\n";
		exit 1;
	} else {
		if ($output_type eq "WMC") {
			$output_type_wmc = 1;
		}
		if ($output_type eq "CSV") {
			$output_type_csv = 1;
		}
		if ($output_type eq "shape") {
			$output_type_shape = 1;
		}
	}
}

#3. every specific parameter for output=WMC available?
if ($output_type_wmc && (!(defined ($polygon_ID_property) && defined ($elevation) && defined ($parameter) && @timestamps && defined ($output_wmc))) ) {
	print "When the output=WMC, polygon_ID_property, elevation, parameter, timestamps and output_wmc are required parameters\n";
	exit 1;
}
#4. every specific parameter for output=CSV available?
if ($output_type_csv && !defined ($output_csv)) {
	print "When the output=CSV, output_csv is a required parameter\n";
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


if ($output_type_wmc) {
	
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
	my $starspan_original_output="${work_dir}/output.csv";
	performStarspan("$starspan_original_output", $input_vector_file, $input_raster_files, "--fields $polygon_ID_property");

	# remove header line from csv file
	my $csv_wo_header="$work_dir/output_wo_header.csv";
	open (IN, $starspan_original_output) or die "cannot open $starspan_original_output for reading: $!";
	open (OUT, ">$csv_wo_header") or die "cannot open $csv_wo_header for writing: $!";
	my $linenumber=1;
	while (<IN>) {
		if ($linenumber!=1) {
			print OUT $_;
		}
		$linenumber = $linenumber + 1;
	}
	close(IN) or die "cannot close $starspan_original_output: $!";
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
	
	#convert NO2 to no2
	if ($parameter eq "NO2") {
		$parameter = "no2";
	}
	#add to SOS
	#perl vect_csv2sos.pl --input_file="/var/www/html/genesis/data/GIM_test_data/CSV/output_wo_header_w_timestamp.csv" --order_id="test1" --parameter="no2" 
	my $csv2sos_command="perl $perl_scripts_dir/vect_csv2sos.pl --input_file=\"$csv_wo_header_w_timestamp\" --sos_url=\"${sos_url}/sos\" --order_id=\"$unique_code\" --parameter=\"$parameter\"";
	print "CSV2SOS command:\n";
	Utils::execute($csv2sos_command, $debug);
	
	
	my $all_timestamps_in_ISO8601 = "";
	my $first_timestamp_in_ISO8601 = "";
	my $timestamp_in_ISO8601 = "";
	foreach my $timestamp (@timestamps) {
		#if this is not the first timestamp, add a comma before adding the new timestamp to $all_timestamps_in_ISO8601
		if ($all_timestamps_in_ISO8601 ne "") {
			$all_timestamps_in_ISO8601 .= ",";
		}
		$timestamp_in_ISO8601 = Utils::convertDateTimeToISO8601($timestamp);
		$all_timestamps_in_ISO8601 .= $timestamp_in_ISO8601;
		
		if ($first_timestamp_in_ISO8601 eq "") {
			$first_timestamp_in_ISO8601 = $timestamp_in_ISO8601;
		}
	}
	my $last_timestamp_in_ISO8601 = $timestamp_in_ISO8601;
	
				
	if ($all_timestamps_in_ISO8601 eq "" || $first_timestamp_in_ISO8601 eq "" || $last_timestamp_in_ISO8601 eq "") {
		print "No timestamps in ISO8601 format!\n";
		print "Check timestamps parameter: $timestamps\n";
		exit 1;
	}

	# WMC
	print "Creating WMC file\n";
	Utils::createFileFromTemplate($wmc_template, $output_wmc, 
		('##GEOSERVER_URL##', $geoserver_url,
		 '##SOS_URL##', $sos_url,
		 '##WMS_ORDER_ID##', $unique_code,
		 '##WMS_DEFAULT_TIME##', $first_timestamp_in_ISO8601,
		 '##WMS_ALL_TIMES##', $all_timestamps_in_ISO8601,
		 '##SOS_ORDER_ID##', $unique_code,
		 '##SOS_START_TIME##', $first_timestamp_in_ISO8601,
		 '##SOS_END_TIME##', $last_timestamp_in_ISO8601)
		);
}



if ($output_type_shape || $output_type_csv) {

	my $starspan_original_output="${work_dir}/output.csv";
	performStarspan("$starspan_original_output", $input_vector_file, $input_raster_file, "");

	# copy original shape file
	#my $copy_shape_files_command = "$shell_scripts_dir/copy_shape_files.sh \"/var/www/html/genesis/data/Bayern/deud52________a8\" \"$work_dir/output_shape\"";
	
	# remove RID from CSV file (keep FID field)
	# remove first occurrence of 'RID' (in header), remove $input_raster_filename in every line
	my $input_raster_filename = basename ($input_raster_file);
	my $sed_command="sed -e \"s|,RID,|,|\" -e \"s|,$input_raster_filename,|,|g\"";
	   $sed_command="$sed_command $starspan_original_output > $work_dir/star_stats_shape_wo_RID.csv";
	print "SED command\n";
	Utils::execute($sed_command, $debug);
	
	#convert original DBF to CSV file
	#we need this CSV file to combine it with the starspan output file
	#if we don't combine these files features that are not included
	#in the starspan output file (because there is no overlay with raster)
	#will not be included in the output CSV or shapefile
	my $csv_original_dbf_output_dir = "${work_dir}/original_dbf";
	my $ogr2ogr_command = "ogr2ogr -f \"CSV\" \"${csv_original_dbf_output_dir}\" \"$input_vector_file\"";
	print "OGR command\n";
	Utils::execute($ogr2ogr_command, $debug);
	my @csv_original_dbf_files = ();
	push (@csv_original_dbf_files, glob("${csv_original_dbf_output_dir}/*.csv"));
	my $number_of_csv_files_in_dir=@csv_original_dbf_files;
	if ($number_of_csv_files_in_dir != 1) {
		print "The $csv_original_dbf_output_dir should contain exactly one .csv file!\n";
		print "Found .csv files in directory: $number_of_csv_files_in_dir\n";
		exit 1;
	}
	my $csv_original_dbf = $csv_original_dbf_files[0];

	
	my $combined_CSVs = "${work_dir}/combined.csv";
	print "Combine CSVs\n";
	combineCSVs("${work_dir}/star_stats_shape_wo_RID.csv", $csv_original_dbf, $combined_CSVs);
	
	
	if ($output_type_csv) {
		my $copy_csv_command = "cp \"${combined_CSVs}\" \"$output_csv\"";
		print "Copy CSV command\n";
		Utils::execute($copy_csv_command, $debug);
	}
	
	if ($output_type_shape) {
		# overwrite dbf file of original shape file by creating a new dbf file from CSV output
		my $ogr2ogr_command = "ogr2ogr -f \"ESRI Shapefile\" \"${work_dir}/star_stats_shape.shp\" \"${combined_CSVs}\"";
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

#combine the starspan output CSV file and the original (complete) CSV file
#derived from the original DBF
#the FID in the starspan output matches the line numer in the original DBF
sub combineCSVs {
	my ($starspan_output, $original_csv, $output_file) = @_;

	open (IN_STARSPAN, $starspan_output) or die "cannot open $starspan_output for reading: $!";
	open (IN_ORIGINAL, $original_csv) or die "cannot open $original_csv for reading: $!";
	open (OUT, ">$output_file") or die "cannot open $output_file for writing: $!";
	
	
	my @lines_starspan = <IN_STARSPAN>;
	my $numer_of_lines_in_starspan = @lines_starspan;
	my @lines_original = <IN_ORIGINAL>;
	my $numer_of_lines_in_original = @lines_original;
	
	my $fid = 0;
	my $line_without_fid = "";
	#start at line 1 (skip header)
	my $linenumber_original=0;
	
	
	#loop starspan output, starting from 1
	for (my $linenumber_starspan = 0; $linenumber_starspan < $numer_of_lines_in_starspan; $linenumber_starspan++) {
		
		my $line_starspan = $lines_starspan[$linenumber_starspan];
			
		#fetch FID	
		if ($line_starspan =~ m/^([^,]*),(.*)$/) {
			$fid = $1;
			$line_without_fid = $2;
		}
	
		#if $linenumber_starspan = 0, no need to loop through original CSV
		#just print first line (header) in starspan output
		if ($linenumber_starspan != 0) {
			#loop through original CSV until FID matches the one from starspan output
			#FID starts at feature 0
			#line 0 in CSV contains header, that's why we until
			#linenumber = FID + 1
			while ($linenumber_original < $fid + 1) {
				#write line + 6 comma's (= empty values) to output file
				my $line_with_empty_values = $lines_original[$linenumber_original];
				chomp($line_with_empty_values);
				$line_with_empty_values .= ",,,,,,";
				print OUT "$line_with_empty_values\n";
				$linenumber_original++;
			}
		}
		#at this moment the FID in starspan and original output file is the same
		#write line from starspan output
		print OUT "$line_without_fid\n";
		#also augment $line_original so this record is skipped from the original file
		#(otherwise, it would appear twice in the output file)
		$linenumber_original++;
	}
	
	#write the rest of the lines (if any)
	while ($linenumber_original < $numer_of_lines_in_original) {
			#write line + 6 comma's (= empty values) to output file
			my $line_with_empty_values = $lines_original[$linenumber_original];
			chomp($line_with_empty_values);
			$line_with_empty_values .= ",,,,,,";
			print OUT "$line_with_empty_values\n";
			$linenumber_original++;
	}

	close(IN_STARSPAN) or die "cannot close $starspan_output: $!";
	close(IN_ORIGINAL) or die "cannot close $original_csv: $!";
	close(OUT) or die "cannot close $output_file: $!";
}
