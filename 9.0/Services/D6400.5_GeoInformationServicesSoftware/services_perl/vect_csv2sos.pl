#!/usr/bin/perl

#perl vect_csv2sos.pl --input_file="/var/www/html/genesis/data/GIM_test_data/CSV/output_wo_header_w_timestamp.csv" --order_id="test1" --parameter="no2" 

use Getopt::Long;
use Switch;
use LWP::UserAgent;
use HTTP::Request::Common;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use GDALUtils;


#general parameters
my %params = Utils::getParams();
my $register_sensor_template=$params{'sos_register_sensor_template'};
my $insert_observation_template=$params{'sos_insert_observation_template'};
my $debug=$params{'debug'};

	
# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"input_file=s" => \my $input_file,
	"sos_url=s" => \my $sos_url,
	"order_id=s" => \my $order_id,
	"parameter=s" => \my $parameter
);

##########################
# Check input parameters #
##########################

if (!(defined ($input_file) && defined ($sos_url) && defined ($order_id) && defined ($parameter) )) {
	print "One or more parameters are missing or invalid\n";
	exit 1;
}


if ($parameter ne "no2") {
	print "Output parameter should be no2\n";
	exit 1;
}

#################
# Set variables #
#################

my $user_agent = LWP::UserAgent->new;

my $observed_property_min = "urn:ogc:def:phenomenon:OGC:1.0.30:min_no2";
my $observed_property_max = "urn:ogc:def:phenomenon:OGC:1.0.30:max_no2";
my $observed_property_avg = "urn:ogc:def:phenomenon:OGC:1.0.30:avg_no2";
my $observed_property_median = "urn:ogc:def:phenomenon:OGC:1.0.30:median_no2";
		


##############
# Processing #
##############

print "Starting CSV2SOS\n";
print ".\n";



## 1. Register sensor
# read template file
open(IN, $register_sensor_template) or die "cannot open $register_sensor_template for reading: $!";
my @template_lines = <IN>;
close(IN);

# fill placeholder
my $register_sensor = "";
foreach my $template_line (@template_lines) {
	$template_line =~ s/##ORDER_ID##/$order_id/g;
	$register_sensor .= $template_line;
}

my $register_sensor_successful = sendSOSRequest("$sos_url", $register_sensor, "<sos:AssignedSensorId>urn:ogc:object:feature:Sensor:IFGI:$order_id</sos:AssignedSensorId>");
if (! ($register_sensor_successful)) {
	exit 1;
}




## 2. Insert observations
# read template file
open(IN, $input_file) or die "cannot open $input_file for reading: $!";
my @input_file_lines = <IN>;
close(IN);

# fill placeholder
foreach my $input_file_line (@input_file_lines) {

	#if ($debug) {
		#print "\nInput line: $input_file_line";
	#}
	
	## Check if the lines in the input CSV file have the correct format
	if( $input_file_line =~ m|^([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*),([^,]*)$| ) {
		my $fid = $1;
		my $id = $2;
		my $rid = $3;
		my $num_pixels = $4;
		my $min = $5;
		my $max = $6;
		my $avg = $7;
		my $median = $8;
		my $stdev = $9;
		
		#if ($debug) {
			#print "Match!\n";
			#print "fid: $fid \t id: $id \t rid: $rid \t num pixels: $num_pixels \t min: $min \t max: $max \t avg: $avg \t median: $median \t stdev: $stdev\n";
		#}
		
		# Insert 4 observations: min, max, avg and median
		# read template file
		open(IN, $insert_observation_template) or die "cannot open $insert_observation_template for reading: $!";
		my @template_lines = <IN>;
		close(IN);
		
		# fill placeholder
		my $insert_observation_min = "";
		my $insert_observation_max = "";
		my $insert_observation_avg = "";
		my $insert_observation_median = "";
		my $template_line_min = "";
		my $template_line_max = "";
		my $template_line_avg = "";
		my $template_line_median = "";

		foreach my $template_line (@template_lines) {

			#convert time
			my $timeOfObservation = Utils::convertDateTimeToISO8601($rid, 1);
			# if time cannot be converted goto the next iteration of the looop
			if ($timeOfObservation eq "") {
				print "Date and time of observation $rid could not be converted to ISO 8601";
				print "Continuing with next line in CSV...";
				next;
			}
				
			$template_line =~ s/##ORDER_ID##/$order_id/g;
			$template_line =~ s/##FEATURE_ID##/$id/g;
			$template_line =~ s/##TIME##/$timeOfObservation/g;

			# replace placeholders with specific values for min, max, avg and median
			$template_line_min = $template_line;
			$template_line_min =~ s/##OBSERVED_PROPERTY_NAME##/$observed_property_min/g;
			$template_line_min =~ s/##VALUE##/$min/g;
			$insert_observation_min .= $template_line_min;
			
			$template_line_max = $template_line;
			$template_line_max =~ s/##OBSERVED_PROPERTY_NAME##/$observed_property_max/g;
			$template_line_max =~ s/##VALUE##/$max/g;
			$insert_observation_max .= $template_line_max;
			
			$template_line_avg = $template_line;
			$template_line_avg =~ s/##OBSERVED_PROPERTY_NAME##/$observed_property_avg/g;
			$template_line_avg =~ s/##VALUE##/$avg/g;
			$insert_observation_avg .= $template_line_avg;
			
			$template_line_median = $template_line;
			$template_line_median =~ s/##OBSERVED_PROPERTY_NAME##/$observed_property_median/g;
			$template_line_median =~ s/##VALUE##/$median/g;
			$insert_observation_median .= $template_line_median;
			
		}

		my $expected_sos_response = "<sos:AssignedObservationId>.*</sos:AssignedObservationId>";
		#sendSOSRequest("$sos_url", $insert_observation_min, $expected_sos_response);
		#sendSOSRequest("$sos_url", $insert_observation_max, $expected_sos_response);
		sendSOSRequest("$sos_url", $insert_observation_avg, $expected_sos_response);
		#sendSOSRequest("$sos_url", $insert_observation_median, $expected_sos_response);

	} else {
		print "ERROR: no match for this input line:\n";
		print "$input_file_line\n";
	}
}

print ".\n";
print "Ending CSV2SOS\n";

exit;

sub sendSOSRequest {

	my ($sos_url, $request, $expected_response) = @_;

	my $response = $user_agent->request(POST $sos_url, Content => $request);
	#my $response = $user_agent->request(POST $sos_url, [request => $request]);
	#my $response = $user_agent->request(POST $sos_url, Content_Type => 'form-data', Content => $request);
	
	# check if response status is OK and the content type is XML
	die "$sos_url error: ", $response->status_line unless $response->is_success;
	die "Wrong content type at $sos_url -- ", $response->content_type unless $response->content_type eq 'text/xml';
	
	# check if response content is like expected
	if(!($response->content =~ m|$expected_response|)) {
		print "The response from the SOS was not what we expected\n";
		print "Request:\n";
		print $request;
		print "\n\n";
		print "Response from SOS at $sos_url:\n";
		print $response->content;
		print "\n\n";
		return 0;
#	} else {
#		if ($debug) {
#			print "Observation inserted\n";
#		}
	}
	return 1;
}



