#!/usr/bin/perl 

use Getopt::Long;
use Switch;
use strict;
use warnings;

use FindBin ();
use lib "$FindBin::Bin/lib";
use OGRUtils;
use GDALUtils;

print "TEST GDAL\n";
print "1. BY MIME TYPE\n";
my $output_format = GDALUtils::getGDALFormatByMimeType("image/tiff");
print "format: $output_format\n";
print "2. OF FILE\n";
my $file = "/var/www/html/genesis/data/DLR/POLY-AQ-test/poly_aq_2007-08-09.nc";
$output_format = GDALUtils::getGDALFormatOfFile("$file");
print "format: $output_format\n";
print "\n";

print "TEST OGR\n";
print "1. BY MIME TYPE\n";
$output_format = OGRUtils::getOGRFormatByMimeType("text/csv");
print "format: $output_format\n";
print "2. OF FILE\n";
$file = "/var/www/html/genesis/data/Bayern/deud52________a8.shp";
$output_format = OGRUtils::getOGRFormatOfFile($file);
print "format: $output_format\n";
$file = "/var/www/html/genesis/data/GIM_test_data/belgium/provincies.gml";
$output_format = OGRUtils::getOGRFormatOfFile($file);
print "format: $output_format\n";


