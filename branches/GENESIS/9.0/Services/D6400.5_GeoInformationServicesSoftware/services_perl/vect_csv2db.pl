#!/usr/bin/perl 

#perl vect_csv2db.pl --unique_code=tmp_vect_csv2db_abc --input_file="/tmp/tmp_vect_overlay/output_wo_header_w_timestamp.csv" --order_id="order ID" --elevation="2500" --parameter="NO2" --db_driver="pg" --db_setting="host=localhost,dbname=grass" --tmp_table=rast_tmp --perm_table=rast --db_user=postgres --db_psw=grespost 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use GDALUtils;

	
# read input parameters:
#
# = indicates mandatory options
# : indicatates optional options
#
GetOptions(
	"unique_code=s" => \my $unique_code,
	"input_file=s" => \my $input_file,
	"order_id=s" => \my $order_id,
	"elevation=s" => \my $elevation,
	"parameter=s" => \my $parameter,
	"db_driver=s" => \my $db_driver,
	"db_setting=s" => \my $db_setting,
	"tmp_table=s" => \my $tmp_table,
	"perm_table=s" => \my $perm_table,
	"db_user=s" => \my $db_user,
	"db_psw:s" => \my $db_psw,
);


##########################
# Check input parameters #
##########################

if (!(defined ($unique_code) && defined ($input_file) && defined ($order_id) &&
      defined ($elevation) && defined ($parameter)  && defined ($db_driver) && defined ($db_setting) &&
      defined ($tmp_table) && defined ($perm_table)  && defined ($db_user))) {
	print "One or more parameters are missing or invalid\n";
	exit 1;
}


#################
# Set variables #
#################
if (defined $db_psw) {
	$db_psw="password=$db_psw";
}


##############
# Processing #
##############

print "Instance $unique_code  --------- START\n";
print ".\n";

print "Connect to database $db_setting using driver $db_driver\n";
system("db.connect driver=$db_driver database=\"$db_setting\"");

print "Login\n";
system("db.login user=$db_user $db_psw");

print "Drop temporary table\n";
system("echo \"drop table rast_tmp\" | db.execute");

print "Create temporary table\n";
system("echo \"create table $tmp_table (fid integer, id varchar(20), rid varchar(20), num_pixels integer, min double precision, max double precision, avg double precision, median double precision, stdev double precision)\" | db.execute");

# not necessary: permanent table should already exist
#print "Create permanent table"
#system("echo \"create table $perm_table (order_id varchar(20), polygon_id varchar(20), timestamp timestamp without time zone, elevation integer, parameter varchar(20), min double precision, max double precision, avg double precision, median double precision, stdev double precision)\" | db.execute");

print "Fill temporary table\n";
system("echo \"copy $tmp_table(fid, id, rid, num_pixels, min, max, avg, median, stdev) from '$input_file' with delimiter as ',';\" | db.execute");

print "Insert contents of temporary table into permanent table\n";
system("echo \"insert into $perm_table(order_id, polygon_id, timestamp, elevation, parameter, min, max, avg, median, stdev) select '$order_id', id, CAST(rid AS timestamp), '$elevation', '$parameter', min, max, avg, median, stdev from $tmp_table\" | db.execute");

print ".\n";
print "Instance $unique_code  --------- END\n";



