#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use Utils;
use GDALUtils;


#general parameters
my %params = Utils::getParams();
my $debug=$params{'debug'};

	
# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#

GetOptions(
	"unique_code=s" => \my $unique_code,
	"csv_input_file=s" => \my $csv_input_file,
	"input_file:s" => \my $input_file,
	"db_driver=s" => \my $db_driver,
	"db_setting:s" => \my $db_setting,
	"db_user:s" => \my $db_user,
	"db_psw:s" => \my $db_psw,
	"mode=s" => \my $mode,
	"output_file:s" => \my $output_file,
	"column_name:s" => \my $column_name
);


##########################
# Check input parameters #
##########################

if (!(defined ($unique_code) && defined ($csv_input_file) && defined ($db_driver) && defined ($mode))) {
	print "One or more parameters are missing or invalid\n";
	exit 1;
}

if (defined $mode && $mode ne "WMC") {
	if (!(defined $input_file) && $input_file eq "") {
		print "If mode is not WMC, input_file is a mandatory parameter\n";
		exit 1;
	}
	if (!(defined $output_file) && $output_file eq "") {
		print "If mode is not WMC, output_file is a mandatory parameter\n";
		exit 1;
	}
}

#################
# Set variables #
#################
if (defined $db_psw && $db_psw ne "") {
	$db_psw="password=$db_psw";
} else {
	$db_psw="";
}

if (defined $db_setting && $db_setting ne "") {
	$db_setting="database=$db_setting";
} else {
	$db_setting="";
}

if (!defined $column_name || $column_name eq "") {
	$column_name = "Result";
}

##############
# Processing #
##############

print "Instance $unique_code  --------- START\n";
print ".\n";

print "Connect to database $db_setting using driver $db_driver\n";
Utils::execute("db.connect driver=$db_driver $db_setting", $debug);

if (defined ($db_user) && $db_user ne "") {
	Utils::execute("db.login user=$db_user $db_psw", $debug);
}

# remove header line from csv file
open (IN, $csv_input_file) or die "cannot open $csv_input_file for reading: $!";
my @csv_input_file_lines = <IN>;
close(IN) or die "cannot close $csv_input_file: $!";


if ($mode ne "WMC") {
	#read input file into temp_table
	my $db_in_ogr_command = "db.in.ogr dsn=${input_file} output=temp_table";
	Utils::execute($db_in_ogr_command, $debug);
	
	#Utils::execute("db.describe -c temp_table", $debug);
	
	Utils::execute("echo \"alter table temp_table add column \\\"${column_name}\\\" double precision\" | db.execute", $debug);
}


# loop lines in input file and store result in CSV file
foreach my $csv_input_file_line (@csv_input_file_lines) {
	# match the second property in the CSV file
	if ($csv_input_file_line =~ m/^([^,]*),([^,]*),([^,]*),([^,]*),([^,]*)/) {
		# structure of input file is like this: <FID>,<gid>,<RID>,<numPixels>,<[s]_Band1>,<[s]_Band2>,<[s]_Band3>,<[s]_Band4>,<[s]_Band5>,...
		#     where [s] is the code of the statistics
		# $2 contains everything between the first and second comma => gid
		# $5 contains everything between the first and second comma
		#   or till the end of the line, if there is no second comma => avg_Band1
		
		if ($mode eq "WMC") {
			Utils::execute("echo \"update gtad_with_gid set \\\"${column_name}\\\" = $5 where gid = $2\" | db.execute", $debug);
		} else {
			Utils::execute("echo \"update temp_table set \\\"${column_name}\\\" = $5 where gid = $2\" | db.execute", $debug);
		}
	} else {
		print "CSV input file line was not in the expected format: $csv_input_file_line\n";
	}
}

if ($mode ne "WMC") {
		
	#my $db_out_ogr_command = "db.out.ogr temp_table dsn=\"${output_file}\" format=DBF";
	#Utils::execute($db_out_ogr_command, $debug);
	
	my $pgsql2shp_command = "sudo -u root /usr/local/pgsql/bin/pgsql2shp -f ${output_file} -u postgres -P grespost grass public.temp_table";
	Utils::execute($pgsql2shp_command, $debug);
	
	# if $output_file doesn't have a dbf-extension, the pgsql2shp_command-command will add it
	# so if  $output_file does not exist and ${output_file}.dbf does, rename it
	if ( (!(-e "${output_file}")) && (-e "${output_file}.dbf") ) {
		Utils::execute("mv ${output_file}.dbf $output_file", $debug);
	}
	
	Utils::execute("echo \"drop table temp_table\" | db.execute", $debug);
}


print ".\n";
print "Instance $unique_code  --------- END\n";





