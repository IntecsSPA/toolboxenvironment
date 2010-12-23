#!/usr/bin/perl 

#perl rast_time_averaging.pl --unique_code=tmp_abc --input_folder="/tmp/april_may" --elevation=2500 --start_period=2007-04-01 --end_period=2007-05-30 --time_averaging_window=5 --start_target_time=6 --end_target_time=12 --output_file="/tmp/output" --output_band_timestamp_mapping="/tmp/output_mapping"
#perl rast_time_averaging.pl --unique_code=tmp_abc --input_folder="/tmp/march_april_may" --elevation=2500 --start_period=2007-03-01 --end_period=2007-05-12 --time_averaging_window=20 --start_target_time=6 --end_target_time=12 --output_file="/tmp/output" --output_band_timestamp_mapping="/tmp/output_mapping"
#perl rast_time_averaging.pl --unique_code=tmp_abc --input_folder="D:\testdata\poly" --elevation=2500 --start_period=2007-03-01 --end_period=2007-05-12 --time_averaging_window=20 --start_target_time=5 --end_target_time=10 --output_file="D:\temp\output" --output_band_timestamp_mapping="D:\temp\output_mapping"


use Getopt::Long;
use Switch;
use Date::Calc qw(Date_to_Time Time_to_Date Add_Delta_Days);
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use GDALUtils;


#general parameters
my %params = Utils::getParams();
my $project_dir=$params{'project_dir'};
my $shell_scripts_dir=$params{'shell_scripts_dir'};
my $perl_scripts_dir=$params{'perl_scripts_dir'};
my $debug=$params{'debug'};



# Expected parameters for this script:
# 1. a unqiue id for this process
# 2. location of input files
# 3. elevation
# 4. start period
# 5. end period
# 6. time averaging window
# 7. start target time
# 8. end target time
# 9. location of output file
# 10. location of output file containing band-timestamp mapping
# 11. optional: the raster output format
# 12. optional: the raster output type


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_folder=s" => \my $input_folder,
	"input_metadata_folder:s" => \my $input_metadata_folder,
	"elevation=i" => \my $elevation,
	"start_period=s" => \my $start_period,
	"end_period=s" => \my $end_period,
	"time_averaging_window=i" => \my $time_averaging_window,
	"start_target_time=i" => \my $start_target_time,
	"end_target_time=i" => \my $end_target_time,
	"output_file=s" => \my $output_file,
	"output_band_timestamp_mapping=s" => \my $output_band_timestamp_mapping,
	"output_mimetype:s" => \my $output_mimetype,
	"output_data_type:s" => \my $output_data_type
);


##########################
# Check input parameters #
##########################

# 1. every mandatory parameter available?
if (!(defined ($unique_code) && defined ($input_folder) && defined ($elevation) && defined ($start_period) && defined ($end_period)
	&& defined ($time_averaging_window) && defined ($start_target_time) && defined ($end_target_time) && defined ($output_file)
	&& defined ($output_band_timestamp_mapping))) {
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


# 2b. check if input_metadata_folder is a directory
my @input_metadata_files=();
if (defined $input_metadata_folder && $input_metadata_folder ne "") {
	# glob input file names
	if (!(-d $input_metadata_folder)) {
		print "The input_metadata_folder parameter should be a directory\n";
		exit 1;
	}
	@input_metadata_files = glob("${input_metadata_folder}/*");
}

# 2c. check if the number of input_files is the same as the number of input_metadata files
my $number_of_input_files = @input_files;
my $number_of_input_metadata_files = @input_metadata_files;
if ($number_of_input_metadata_files != 0 && $number_of_input_files != $number_of_input_metadata_files) {
	print "If metadata files are provided, the number of metadata files must be equal to the number of data files";
	exit 1;
}


#3. start and end period in correct format?
#format should be YYYY-MM-DD hh:mm:ss
if (!($start_period=~/^(\d{4})-(\d{2})-(\d{2})$/) || !($end_period=~/^(\d{4})-(\d{2})-(\d{2})$/)) {
	print "Start and end period should be in format YYYY-MM-DD\n";
	exit 1;
}

#4. start_target_time and end_target_time should be less than 24
if ( $start_target_time >= 24 || $end_target_time >= 24 ) {
	print "Start and end target time should be less than 24\n";
	exit 1;
}

#5. end_target_time should be greater than start_target_time
if (  $start_target_time >= $end_target_time ) {
	print "Start target time should be less than end target time\n";
	exit 1;
}

#6. test if start period comes before end period
my @start_period_ymd = convertToDate($start_period);
my @end_period_ymd = convertToDate($end_period);
my $start_period_sec_since_1970 = Date_to_Time(@start_period_ymd,0,0,0);
my $end_period_sec_since_1970 = Date_to_Time(@end_period_ymd,0,0,0);
if ($start_period_sec_since_1970 > $end_period_sec_since_1970) {
	print "Start period should come before end period\n";
	exit 1;
}


#################
# Set variables #
#################

my $output_format="";

if (defined($output_mimetype) && $output_mimetype ne "") {
	print "set output format\n";
	print "Output MimeType: $output_mimetype\n";
	switch ( $output_mimetype ) {
		case "image/tiff" 			{ $output_format="GTiff"; }
		case "application/x-netcdf"	{ $output_format="netCDF"; }
		case "application/x-hdf"	{ $output_format="HDF4"; }
		case "image/png"			{ $output_format="PNG"; }
		case "image/jpeg"			{ $output_format="JPEG"; }
		case "image/gif"			{ $output_format="GIF"; }
	}
	print "Output format: $output_format\n";
}

if (!(defined($output_data_type))) {
	$output_data_type="";
}


#derived parameters
my $work_dir= "$project_dir/work/$unique_code";

# create working directory
mkdir($work_dir, 0755) or die "cannot mkdir $work_dir: $!";



##############
# Processing #
##############

Utils::printStart($unique_code, $debug);

# copy metadata files
foreach my $index (0..$number_of_input_metadata_files-1) {
	my $copy_command = "cp $input_metadata_files[$index] ${input_files[$index]}.aux.xml";
	Utils::execute($copy_command, $debug);
}



#create hash with timestamp of the day as key and a list with 
# - the name of the file
# - and a hash containing timestamps as keys and bandnumbers as values
# as value
#
#	/tmp/april_may/poly_aq_2007-05-26.nc
#		timestamp at 00:00:00
#		timestamp1
#			bandnumber1
#			bandnumber2
#			...
#		timestamp2
#			bandnumber1
#			bandnumber2
#			...
#		...
#	/tmp/april_may/poly_aq_2007-05-27.nc
#		timestamp at 00:00:00
#		timestamp1
#			bandnumber1
#			...
#		...
#	...

my %timestamp_input_file_and_data = ();
foreach my $input_file (@input_files) {
	
	# get band information for this input file
	# the returned information is a 2 dimensional hashmap with this structure:
	#	elevation1
	#		timestamp1
	#			bandnumber
	#		timestamp2
	#			bandnumber
	#		...
	#	elevation2
	#		timestamp1
	#			bandnumber
	#		...
	#	...
	my %elevation_timestamp_band = GDALUtils::storeBandInfoDLRFileInHash("$input_file");
	#we only need the bands with the correct elevation
	my $timestamp_bandnumber_ref = $elevation_timestamp_band{$elevation};
	
	if (defined $timestamp_bandnumber_ref) {
		#dereference hash using % sigil (actually, 2 dimensional hashmaps don't exist in Perl,
		#	 so the value of the elevation key is not a hashmap, but the link to a hashmap)
		my %tmp_timestamp_bandnumber = %$timestamp_bandnumber_ref;
		
		#order numeric using the "spaceship operator" <=>
		my @ordered_timestamps = sort { $a <=> $b } (keys %tmp_timestamp_bandnumber);
		
		my $number_of_timestamps = @ordered_timestamps;
		if ($number_of_timestamps > 0) {
			my $first_timestamp = $ordered_timestamps[0];
			my $input_file_date_sec_since_1970 = getTimestampOfDay($first_timestamp);
			my @input_file_and_data = ($input_file,%tmp_timestamp_bandnumber);
			$timestamp_input_file_and_data{$input_file_date_sec_since_1970} = [$input_file,$timestamp_bandnumber_ref];
		}
	}
}


#my $count = 1;
#while ( my ($key, $value) = each(%timestamp_input_file) ) {
#	print "$count. $key => $value\n";
#	$count += 1;
#}



my @rasters_period = ();
my @timestamps_period = ();

my @start_window_ymd = @start_period_ymd;
my $start_window_sec_since_1970 = Date_to_Time(@start_window_ymd,0,0,0);


# loop per time averaging window
while ($start_window_sec_since_1970 <= $end_period_sec_since_1970) {

	my @rasters_window = ();
	my $total_timestamp_window=0;

	my @end_window_ymd = Add_Delta_Days(@start_window_ymd,($time_averaging_window-1));
	my $end_window_sec_since_1970 = Date_to_Time(@end_window_ymd,0,0,0);
	
	# end window cannot be after the end period
	if ($end_window_sec_since_1970 > $end_period_sec_since_1970) {
		@end_window_ymd = @end_period_ymd;
		$end_window_sec_since_1970 = $end_period_sec_since_1970;
	}

	print "\n";
	print "start window: $start_window_ymd[0] $start_window_ymd[1] $start_window_ymd[2] \n";
	print "end window: $end_window_ymd[0] $end_window_ymd[1] $end_window_ymd[2]\n";
	
	# look in the available files for any files containing information for this time period
	# every file contains information for 1 day
	#order numeric using the "spaceship operator" <=>
	my @ordered_timestamp_inp_file = sort { $a <=> $b } (keys %timestamp_input_file_and_data);
	foreach my $timestamp_inp_file (@ordered_timestamp_inp_file) {
		
		#check if this input file contains information for this time period
		if ($timestamp_inp_file >= $start_window_sec_since_1970 && $timestamp_inp_file <= $end_window_sec_since_1970) {
			
			my @bandnumbers_1_day=();
			my $total_timestamp_1_day=0;

			#only use bands with the correct elevation
			my $input_file_and_data_ref = $timestamp_input_file_and_data{$timestamp_inp_file};
			if (defined $input_file_and_data_ref) {
				#dereference list using @ sigil
				#	 the value is not a list, but the link to a list
				my @input_file_and_data = @$input_file_and_data_ref;
				my $input_file = $input_file_and_data[0];
				my $timestamp_bandnumber_ref = $input_file_and_data[1];
				
				if (defined $timestamp_bandnumber_ref) {
					#dereference hash using % sigil (actually, 2 dimensional hashmaps don't exist in Perl,
					#	 so the value of the elevation key is not a hashmap, but the link to a hashmap)
					my %timestamp_bandnumber = %$timestamp_bandnumber_ref;
					my @timestamp = keys (%timestamp_bandnumber);
					#order numeric using the "spaceship operator" <=>
					my @ordered_timestamps = sort { $a <=> $b } @timestamp;
					foreach my $timestamp (@ordered_timestamps) {
						#convert timestamp to datetime
						my @datetime = Time_to_Date($timestamp);
						#only use bands containing information between start and end target hour (= 3th element in @datetime)
						if ($datetime[3] >= $start_target_time && $datetime[3] <= $end_target_time) {
							push(@bandnumbers_1_day, $timestamp_bandnumber{$timestamp});
							$total_timestamp_1_day=$total_timestamp_1_day+$timestamp;
						}
						#my $formatted_datetime = sprintf("%d-%02d-%02d %02d:%02d:%02d", @datetime);
						#print "Datetime: $formatted_datetime\n";
					}
				}
				# average over all bands in @bands_1_day
				my $nr_of_rasters_1_day = @bandnumbers_1_day;
				if ($nr_of_rasters_1_day > 0) {
	
					my $avg_timestamp_1_day = $total_timestamp_1_day/$nr_of_rasters_1_day;
					my $avg_command_file_1_day="${work_dir}/tmp_avg_1_day_${avg_timestamp_1_day}_input_average";
					my $avg_command_1_day=getAvgMathML(@bandnumbers_1_day);
					Utils::execute("echo \"$avg_command_1_day\" > \"$avg_command_file_1_day\"", $debug);

					# perform time averaging for the bands in 1 file
					my $rast_calc_unique_code="${unique_code}_avg_1_day_${avg_timestamp_1_day}";
					my $rast_calc_input_folder="${work_dir}/tmp_avg_1_day_${avg_timestamp_1_day}_input_folder";
					mkdir($rast_calc_input_folder, 0755) or die "cannot mkdir $rast_calc_input_folder: $!";
					Utils::execute("mv $input_file $rast_calc_input_folder/.", $debug);
					my $rast_calc_output_file="${work_dir}/tmp_avg_1_day_${avg_timestamp_1_day}_output";
					
					
					# avg_command cannot be surrounded by quotes (gives problems with rast_calc.sh), therefore $avg_command_1_day cannot contain spaces!
					#my $rast_calc_command="$shell_scripts_dir/rast_calc.sh ${rast_calc_unique_code} \"$input_file\"  \"$avg_command_1_day\" \"$rast_calc_output_file\" $output_format $output_data_type";
					my $rast_calc_command="perl $perl_scripts_dir/rast_calc.pl --unique_code=\"${rast_calc_unique_code}\" --input_folder=\"$rast_calc_input_folder\"  --calculation=\"$avg_command_file_1_day\" --output_file=\"$rast_calc_output_file\" $output_format $output_data_type";
					print "AVERAGING PER DAY\n";
					Utils::execute($rast_calc_command, $debug);
					# rm -rf ${project_dir}/work/${rast_calc_unique_code}
				
					#add output files to an array and calculate total timestamp of this window
					push(@rasters_window,$rast_calc_output_file);
					$total_timestamp_window=$total_timestamp_window+$avg_timestamp_1_day;
	
				}
			}
		}
	}
	
	# group all days of this time averaging window and then average over these rasters
	my $nr_of_rasters_window = @rasters_window;
	my $rasters_window_sep_comma = join(',',@rasters_window);

	if ($nr_of_rasters_window > 0) {
		
		#my $avg_timestamp_window = $total_timestamp_window/$nr_of_rasters_window;
		# just store the average of start and end window and not the average of all used timestamps
		my $avg_timestamp_window = ($start_window_sec_since_1970 + $end_window_sec_since_1970)/2;
		my $avg_hours = ( ($end_target_time + $start_target_time) /2) * 3600;
		$avg_timestamp_window = $avg_timestamp_window + $avg_hours;

		# group all days of this time averaging window
		my $rast_group_unique_code="${unique_code}_group_window_${avg_timestamp_window}";
		my $rast_group_output_file="${work_dir}/tmp_group_window_${avg_timestamp_window}";

		#my $rast_group_command="$shell_scripts_dir/rast_group.sh \"${rast_group_unique_code}\" \"@rasters_window\" \"$rast_group_output_file\"";
		my $rast_group_command="perl $perl_scripts_dir/rast_group.pl --unique_code=\"${rast_group_unique_code}\" --input_file=\"$rasters_window_sep_comma\"  --output_file=\"$rast_group_output_file\" $output_format $output_data_type";
		print "GROUPING PER WINDOW\n";
		Utils::execute($rast_group_command, $debug);
		# rm -rf ${project_dir}/work/${unique_code}_rast_group

		#make average of all days of this time averaging window
		my $rast_calc_unique_code="${unique_code}_avg_window_${avg_timestamp_window}";
		my $rast_calc_input_folder="${work_dir}/tmp_avg_window_${avg_timestamp_window}_input_folder";
		mkdir($rast_calc_input_folder, 0755) or die "cannot mkdir $rast_calc_input_folder: $!";
		Utils::execute("mv $rast_group_output_file $rast_calc_input_folder/.", $debug);
		my $rast_calc_output_file="${work_dir}/tmp_avg_window_${avg_timestamp_window}_output";

		# we need to average over all bands in the grouped raster
		my @bandnumbers=(1..$nr_of_rasters_window); 
		my $avg_command_file_window="${work_dir}/tmp_avg_window_${avg_timestamp_window}_input_average";
		my $avg_command_window=getAvgMathML(@bandnumbers);
		Utils::execute("echo \"$avg_command_window\" > \"$avg_command_file_window\"", $debug);

		# avg_command cannot be surronded by quotes (gives problems with rast_calc.sh), therefore $avg_command_window cannot contain spaces!
		#my $rast_calc_command="$shell_scripts_dir/rast_calc.sh ${rast_calc_unique_code} \"$rast_group_output_file\"  \"$avg_command_window\" \"$rast_calc_output_file\" $output_format $output_data_type";
		my $rast_calc_command="perl $perl_scripts_dir/rast_calc.pl --unique_code=\"${rast_calc_unique_code}\" --input_folder=\"$rast_calc_input_folder\"  --calculation=\"$avg_command_file_window\" --output_file=\"$rast_calc_output_file\" $output_format $output_data_type";
		print "AVERAGING PER WINDOW\n";
		Utils::execute($rast_calc_command, $debug);
		# rm -rf ${project_dir}/work/${rast_calc_unique_code}

		#add output files and timestamps to array
		push(@rasters_period,$rast_calc_output_file);
		push(@timestamps_period,$avg_timestamp_window);
	}
	
	# set new start_window_ymd and update start_window_sec_since_1970
	@start_window_ymd = Add_Delta_Days(@start_window_ymd,$time_averaging_window);
	$start_window_sec_since_1970 = Date_to_Time(@start_window_ymd,0,0,0);
}

my $nr_of_rasters_period = @rasters_period;
my $rasters_period_sep_comma = join(',',@rasters_period);
if ($nr_of_rasters_period > 0) {
	my $rast_group_unique_code="${unique_code}_group_period";
	my $rast_group_output_file="${work_dir}/tmp_group_period";
	
	#my $rast_group_command="$shell_scripts_dir/rast_group.sh \"${rast_group_unique_code}\" \"@rasters_period\" \"$rast_group_output_file\"";
	my $rast_group_command="perl $perl_scripts_dir/rast_group.pl --unique_code=\"${rast_group_unique_code}\" --input_file=\"$rasters_period_sep_comma\"  --output_file=\"$rast_group_output_file\" $output_format $output_data_type";
	print "GROUPING PERIOD\n";
	Utils::execute($rast_group_command, $debug);
	# rm -rf ${project_dir}/work/${unique_code}_rast_group


	# output file of rast_group.sh is also the output file of rast_time_averaging.sh
	Utils::execute("cp $rast_group_output_file $output_file", $debug);
	
	# write the timestamps to file
	my $timestamps = "";
	foreach my $timestamp_period (@timestamps_period) {
		#convert timestamp to datetime
		my @datetime = Time_to_Date($timestamp_period);
		my $formatted_datetime = sprintf("%d-%02d-%02d %02d:%02d:%02d", @datetime);
		if ($timestamps eq "") {
			$timestamps = "$formatted_datetime";
		} else {
			$timestamps = "$timestamps,$formatted_datetime";
		}
	}
	open(OUT, ">$output_band_timestamp_mapping") or die "cannot create $output_band_timestamp_mapping: $!";
	print OUT "$timestamps";
	close(OUT) or die "cannot close $output_band_timestamp_mapping: $!";
}

Utils::printEnd($unique_code, $debug);

exit;

# converts a datetime in format YYYY-MM-DD hh:mm:ss into year, month, day, hour, min and sec array
sub convertToDate {
	my ($date) = @_;
	#date  should be in the format YYYY-MM-DD
	if ($date=~/(\d{4})-(\d{2})-(\d{2})/) {
		my $year = $1;
		my $month = $2;
		my $day = $3;
		return ($year,$month,$day);
	}
}

sub getAvgCommand {
	my (@bands) = @_;
	my $nr_of_bands = @bands;
	if ($nr_of_bands > 1) {
		my $avg_command="";
		foreach my $band (@bands) {
			if ($avg_command eq "") {
				$avg_command="raster1.${band}"
			} else {
				$avg_command="$avg_command+raster1.${band}"
			}
		}
		return "($avg_command)/$nr_of_bands";
	} else {
		return "raster1/1"
	}
}

sub getAvgMathML {
	#escpape " twice, because this output will be echoed into a new file
	my $mathML = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\n";
	$mathML .=   "<apply xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"http://www.w3.org/1998/Math/MathML http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd\\\" xmlns=\\\"http://www.w3.org/1998/Math/MathML\\\">\n";
	$mathML .=   "	<divide/>\n";
	$mathML .=   "	<apply>\n";
	$mathML .=   "		<plus/>\n";
	
	my (@bands) = @_;
	my $nr_of_bands = @bands;
	if ($nr_of_bands > 1) {
		foreach my $band (@bands) {
			$mathML .= "		<ci>raster1.${band}</ci>\n";
		}
	} else {
			$mathML .= "		<ci>raster1</ci>\n";
	}
	$mathML .=   "	</apply>\n";
	
	$mathML .=   "	<cn>$nr_of_bands</cn>\n";
	$mathML .=   "</apply>\n";
}




# returns the timestamp of the day (so hours=00, minutes=00 and seconds=00)
# for a given timestamp
# eg. if the timestamp respresents the datetime 2/4/2005 09:12:11
# the resulting timestamp represents the datetime 2/4/2005 00:00:00
sub getTimestampOfDay {
	my ($timestamp) = @_;
	my @datetime = Time_to_Date($timestamp);
	#since we want to return the timestamp of the day
	#set datetime[3] (hours), #datetime[4] (minutes) and datetime[5] (seconds)
	#explicitely to 00
	($datetime[3], $datetime[4], $datetime[5]) = (0, 0, 0);
	#my $formatted_datetime = sprintf("%d-%02d-%02d %02d:%02d:%02d", @datetime);
	#print "Datetime: $formatted_datetime\n";
	return Date_to_Time(@datetime);
}



