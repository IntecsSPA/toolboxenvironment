#!/usr/bin/perl

# Documentation and support
# http://www.dopscripts.com

require CGIXSLT;
use strict;

#$params is a ref. to hash containing the parameters
#passed to the stylesheet through HTTP. It is empty if nothing is passed
# or the program is called as a shell program. 

my $params=CGIXSLT::read_https();
# add or set params
#$params=CGIXSLT::params({style=>'xslt/my_style','source'=>'xml/mysource','param'=>'myparamvalue'});
#if parameter exists it will be overwritten
#do not print http headers
#CGIXSLT::omit_https();

my $new_xml;
eval{$new_xml=CGIXSLT::transform();};
if($@){CGIXSLT::error($@);};

#Now, we are printing and saving what we get
my $fh=\*STDOUT;
CGIXSLT::printResult($fh,$new_xml);
XSLT::xsl_FINISH();#for mod_perl
