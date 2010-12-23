#!/usr/bin/perl 

use Getopt::Long;
use Date::Calc qw(Time_to_Date);
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use GDALUtils;


my $file = "/var/www/html/genesis/data/DLR/POLY-AQ-test/poly_aq_2007-08-09.nc";
(my $bbox_minx, my $bbox_miny, my $bbox_maxx, my $bbox_maxy) = GDALUtils::getBBox($file);
print "minx: $bbox_minx\n";
print "miny: $bbox_miny\n";
print "maxx: $bbox_maxx\n";
print "maxy: $bbox_maxy\n";



$file = "/var/www/html/genesis/data/DLR/LST/noaa18avhrr_oplstnd_____eur__________071118000000071119125406dlr.tif";
($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy) = GDALUtils::getBBox($file);
print "minx: $bbox_minx\n";
print "miny: $bbox_miny\n";
print "maxx: $bbox_maxx\n";
print "maxy: $bbox_maxy\n";


