#!/usr/bin/perl 

use Getopt::Long;
use Date::Calc qw(Add_Delta_DHMS Date_to_Time);
use strict;
use warnings;


# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"input_file=s" => \my $input_file,
	"tmp_file=s" => \my $tmp_file,
	"output_file=s" => \my $output_file,
	"feature_member_template_file=s" => \my $feature_member_template_file,
	"gml_header_template_file=s" => \my $gml_header_template_file,
	"base_year=i" => \my $base_year,
	"base_month=i" => \my $base_month,
	"base_day=i" => \my $base_day,
	"reduce_timesteps_factor=i" => \my $reduce_timesteps_factor
);

# bs:
# {print " defined ($input_file) && defined ($tmp_file) && defined ($output_file) && defined ($feature_member_template_file)	&& defined ($gml_header_template_file)
# 	&& defined ($base_year) & defined ($base_month) && defined ($base_day) && defined ($reduce_timesteps_factor)"}

##########################
# Check input parameters #
##########################

# every mandatory parameter available?
if (!(defined ($input_file) && defined ($tmp_file) && defined ($output_file) && defined ($feature_member_template_file)	&& defined ($gml_header_template_file)
	&& defined ($base_year) & defined ($base_month) && defined ($base_day) && defined ($reduce_timesteps_factor))) {
	print "One or more parameters are missing or invalid (string were integer is expected)\n";
	exit 1;
}


#################
# Set variables #
#################

my @base_datetime = ($base_year,$base_month,$base_day,0,0,0);
(my $byear, my $bmonth, my $bday, my $bhour, my $bmin, my $bsec) = getDateInFormatYYYYMMDDhhmmss(@base_datetime);
my $base_datetime_in_ISO8601 = "${byear}-${bmonth}-${bday}T${bhour}:${bmin}:${bsec}Z";
		
my $min_latitude = "";
my $max_latitude = "";
my $min_longitude = "";
my $max_longitude = "";

my $featuremember_counter = 0;
my $timestep_counter = 0;


##############
# Processing #
##############

# read input file
open(IN, $input_file) or die "cannot open $input_file for reading: $!";
my @input_lines = <IN>;
close(IN);


# create output
open (TMP_OUT, ">$tmp_file") or die "cannot open $tmp_file for writing: $!";
foreach my $input_line (@input_lines) {

	my $number = '([\d\.])+';

	# match if the input line consists of 3 numbers seperated by whitespace(s)
	# the line can start and end with whitespace(s) (not mandatory)
	if ($input_line=~/^(\s)*($number)(\s)+($number)(\s)+($number)(\s)*$/) {

		#print "Matched input line: $input_line";
		my $time = $2;
		my $latitude = $5;
		my $longitude = $8;
		#print "time: $time\n";
		#print "latitude: $latitude\n";
		#print "longitude: $longitude\n";
		#print "\n";
		
		
		#Syntax: Add_Delta_DHMS( $year, $month, $day, $hour, $minute, $second, $days_offset, $hour_offset, $minute_offset, $second_offset);
		(my @timestamp) = Add_Delta_DHMS(@base_datetime, 0,0,0, $time);
		(my $year, my $month, my $day, my $hour, my $min, my $sec) = getDateInFormatYYYYMMDDhhmmss(@timestamp);
		my $timestamp_in_ISO8601 = "${year}-${month}-${day}T${hour}:${min}:${sec}Z";
		
		if ($timestamp_in_ISO8601 eq $base_datetime_in_ISO8601) {
			$featuremember_counter++;
			$timestep_counter = 0;
		}
		
		#only include this timestep if $timestep_counter can be divided by $reduce_timesteps_factor
		$timestep_counter++;
		if ($timestep_counter % $reduce_timesteps_factor == 0) {
			my $output = createStringFromTemplate($feature_member_template_file, 
				('##CODE##', "Code${featuremember_counter}",
				 '##TIME##', $timestamp_in_ISO8601,
				 '##LONGITUDE##', $longitude,
				 '##LATITUDE##', $latitude)
				);
			# write output file
			print TMP_OUT "$output"; 
			
			if ($min_latitude eq "" || $latitude < $min_latitude) {
				$min_latitude = $latitude;
			}
			if ($min_longitude eq "" || $longitude < $min_longitude) {
				$min_longitude = $longitude;
			}
			if ($max_latitude eq "" || $latitude > $max_latitude) {
				$max_latitude = $latitude;
			}
			if ($max_longitude eq "" || $longitude > $max_longitude) {
				$max_longitude = $longitude;
			}
		}
	}
}
close(TMP_OUT) or die "cannot close $tmp_file: $!";





# creat output file
open (OUT, ">$output_file") or die "cannot open $output_file for writing: $!";

# write header to output file
my $header = createStringFromTemplate($gml_header_template_file, 
	('##MIN_X##', $min_longitude,
	 '##MIN_Y##', $min_latitude,
	 '##MAX_X##', $max_longitude,
	 '##MAX_Y##', $max_latitude)
	);
print OUT "$header";	
		
# write temporary output file to output file
open(TMP_OUT, $tmp_file) or die "cannot open $tmp_file for reading: $!";
my @lines = <TMP_OUT>;
close(TMP_OUT) or die "cannot close $tmp_file: $!";
foreach my $line (@lines) {
	print OUT "$line";		
}

#write footer to output file
print OUT "</ann:AnnotationCollection>";


close(OUT) or die "cannot close $output_file: $!";
	
exit 0;




# create string from template
sub createStringFromTemplate {

	my ($template_file, %markers_and_values) = @_;

	# read template file
	open(IN, $template_file) or die "cannot open $template_file for reading: $!";
	my @template_lines = <IN>;
	close(IN);
	
	# read all lines
	my $output = "";
	foreach my $template_line (@template_lines) {
		# replace markers
	    while ( my ($marker, $value) = each(%markers_and_values) ) {
			$template_line =~ s/$marker/$value/g;
	    }
		$output .= $template_line;
	}
	
	return $output;
}

# convert eg. 2006-01-16 09:00:00 to 2006-01-16T09:00:00
# discarding time zone information
sub convertDateTimeToISO8601 {

	my ($datetime) = @_;
	my $datetime_in_ISO8601 = "";
	
	if ($datetime =~ m/^((\d){4}-(\d){2}-(\d){2})\s((\d){2}:(\d){2}:(\d){2})/) {
		$datetime_in_ISO8601 = "$1T$5Z";
	}
	return $datetime_in_ISO8601;
}

# get date in YYYYMMDDhhmms format
sub getDateInFormatYYYYMMDDhhmmss {
	my ($year, $month, $day, $hour, $min, $sec) = @_;
	
	while(length($year) < 4) {
		$year = "0${year}";
	}
	while(length($month) < 2) {
		$month = "0${month}";
	}
	while(length($day) < 2) {
		$day = "0${day}";
	}
	while(length($hour) < 2) {
		$hour = "0${hour}";
	}
	while(length($min) < 2) {
		$min = "0${min}";
	}
	while(length($sec) < 2) {
		$sec = "0${sec}";
	}
	return ($year, $month, $day, $hour, $min, $sec);
}

