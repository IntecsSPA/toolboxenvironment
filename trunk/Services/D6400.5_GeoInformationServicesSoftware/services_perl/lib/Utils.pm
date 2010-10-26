package Utils;

use strict;
use warnings;
use Switch;
use Cwd;

my %params=();


$params{'project_dir'}=										"/home_local/projects/c08678b";
$params{'shell_scripts_dir'}=								"$params{'project_dir'}/__scripts";
$params{'template_dir'}=									"$params{'shell_scripts_dir'}/templates";
$params{'perl_scripts_dir'}=								"$params{'project_dir'}/scripts";
$params{'grass_root_dir'}=									"$params{'project_dir'}/grassdata";
$params{'resources_dir'}=									"$params{'project_dir'}/scripts/resources";

$params{'acri-dat_vrt_template'}=							"$params{'resources_dir'}/ACRI-dat_template.vrt";
$params{'acri-dat_csvt_file'}=								"$params{'resources_dir'}/ACRI-dat.csvt";

$params{'sos_url'}=											"http://maxwell.gim.be/SOS";
$params{'sos_register_sensor_template'}=					"$params{'resources_dir'}/SOS_RegisterSensor_template.xml";
$params{'sos_insert_observation_template'}=					"$params{'resources_dir'}/SOS_InsertObservation_template.xml";

$params{'mosaic_indexer_properties'}=						"$params{'resources_dir'}/indexer.properties";
$params{'mosaic_regex_properties'}=							"$params{'resources_dir'}/regex.properties";

$params{'geoserver_url'}=									"http://maxwell.gim.be/geoserver4genesis";
$params{'geoserver_url_rast_format_conv'}=					"http://maxwell.gim.be/geoserver4genesistime";
#$params{'geoserver_url_rast_format_conv'}=					"http://193.74.120.35/geoserver4genesistime";
$params{'geoserver_url_raster_times'}=						"http://maxwell.gim.be/geoserver4genesistime";
#$params{'geoserver_url_raster_times'}=						"http://193.74.120.35/geoserver4genesistime";
$params{'geoserver_rest_user'}=								"restadmin";
$params{'geoserver_rest_password'}=							"rest123";
$params{'geoserver_rest_workspace'}=						"genesis_rest";

$params{'wmc_template_rast_vect_polygon_intersection'}=		"$params{'resources_dir'}/WMC_template_rast_vect_polygon_intersection.xml";
$params{'wmc_template_vect_rast_interp_regular_grid'}=		"$params{'resources_dir'}/WMC_template_vect_rast_interpolation_regular_grid.xml";
$params{'wmc_template_rast_vect_polylines_intersection'}=	"$params{'resources_dir'}/WMC_template_rast_vect_polylines_intersection.xml";
$params{'wmc_template_rast_format_conv'}=					"$params{'resources_dir'}/WMC_template_rast_format_conv.xml";

$params{'wmc_template_rast_format_conv2'}=					"$params{'resources_dir'}/WMC_template_rast_format_conv2.xml";
$params{'wmc_template_rast_format_conv2_layer'}=			"$params{'resources_dir'}/WMC_template_rast_format_conv2_layer.xml";

$params{'math_xsl'}=										"$params{'resources_dir'}/math.xsl";

$params{'debug'}=1;

sub getParams {
	return %params;
}


sub execute {
	(my $command, my $debug) = @_;
	if ($debug == 1) {
		print "###########################\n";
		print "EXECUTING $command\n";
		print "###########################\n";
	}
	system($command);
}

sub printStart {
	printMessage(@_, "START_PROCESS");
}

sub printEnd {
	printMessage(@_, "END_PROCESS");
}

sub printMessage {
	(my $unique_code, my $debug, my $message) = @_;
	if ($debug == 1 && defined($message)) {
		print "\n\n";
		print "###########################\n";
		switch ( $message ) {
			case "START_PROCESS" {
				print "Instance $unique_code  --------- START\n";
			}
			case "END_PROCESS" {
				print "Instance $unique_code  --------- END\n";
			}
		}
		print "###########################\n";
	}
}

# convert eg. 2006-01-16 09:00:00 to 2006-01-16T09:00:00
# discarding time zone information
sub convertDateTimeToISO8601 {

	my ($datetime, $encode_plus_sign) = @_;
	my $datetime_in_ISO8601 = "";
	
	if ($datetime =~ m/^((\d){4}-(\d){2}-(\d){2})\s((\d){2}:(\d){2}:(\d){2})/) {

		$datetime_in_ISO8601 = "$1T$5Z";

#		# add +00 timezone, for the SOS
#		# if $encode_plus_sign, encode + to %2B!
#		if ($encode_plus_sign) {
#			$datetime_in_ISO8601 = "$1T$5%2B00";
#		} else {
#			$datetime_in_ISO8601 = "$1T$5+00";
#		}
	}
	return $datetime_in_ISO8601;
}

# create file from template
sub createFileFromTemplate {

	my ($template_file, $output_file, %markers_and_values) = @_;

	my $output = createStringFromTemplate($template_file, %markers_and_values);

	# write  output file
	open (OUT, ">$output_file") or die "cannot open $output_file for writing: $!";
	print OUT "$output"; 
	close(OUT) or die "cannot close $output_file: $!";
}

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


sub zip {
	my ($zip_command, $root_to_start_zipping, $output_file, $debug) = @_;
	
	# change to the directory containing the input files
	#otherwise the complete directory-structure is stored in the ZIP archive
	my $current_dir = getcwd(); 
	chdir "$root_to_start_zipping";
	print "ZIP command\n";
	execute($zip_command, $debug);
	chdir "$current_dir";
	
	# if $output_file doesn't have a zip-extension, the zip-command will add it
	# so if  $output_file does not exist and ${output_file}.zip does, rename it
	if ( (!(-e "${output_file}")) && (-e "${output_file}.zip") ) {
		execute("mv ${output_file}.zip $output_file", $debug);
	}
}

sub unzip {
	my ($input_file, $output_dir, $debug) = @_;
	
	# create output directory
	mkdir($output_dir, 0755) or die "cannot mkdir $output_dir: $!";
	Utils::execute("unzip \"$input_file\" -d \"$output_dir\"", $debug);
	my @input_files = ();
	
	# if the zip file contains a .shp file,
	# check if it contain exactly one .shp file
	# if this is the case, return the path to the .shp file
	push (@input_files, glob("${output_dir}/*.shp"));
	my $number_of_shp_files_in_zip=@input_files;
	if ($number_of_shp_files_in_zip > 0) {
		if ($number_of_shp_files_in_zip == 1) {
			return $input_files[0];
		} else {
			print "The input zip file should contain exactly one .shp file!\n";
			print "Found .shp files in zip file: $number_of_shp_files_in_zip\n";
			exit 1;
		}
	}
	
	# if the zip file doesn't contains a .shp file,
	# return the path to the first file
	push (@input_files, glob("${output_dir}/*"));
	return $input_files[0];
}

# print the list provided as argument
sub printList {
	foreach (@_) {
	  print "$_ \n";
	}
}

# remove the first line (eg. header line) from a file
sub removeFirstLineFromFile {
	my ($input_file, $output_file) = @_;
	
	open (IN, $input_file) or die "cannot open $input_file for reading: $!";
	open (OUT, ">$output_file") or die "cannot open $output_file for writing: $!";
	my $linenumber=1;
	while (<IN>) {
		if ($linenumber!=1) {
			print OUT $_;
		}
		$linenumber = $linenumber + 1;
	}
	close(IN) or die "cannot close $input_file: $!";
	close(OUT) or die "cannot close $output_file: $!";
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
