#!/usr/bin/perl 

use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use GDALUtils;


my @min_max_longitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"/var/www/html/genesis/data/TAS_FTP/ACRI/results.nc\":longicrs");
print "Min longitude: $min_max_longitude[0]\n";
print "Max longitude: $min_max_longitude[1]\n";

my @min_max_latitude = GDALUtils::getMinMaxOfDatasets("NETCDF:\"/var/www/html/genesis/data/TAS_FTP/ACRI/results.nc\":latitcrs");
print "Min latitude: $min_max_latitude[0]\n";
print "Max latitude: $min_max_latitude[1]\n";


