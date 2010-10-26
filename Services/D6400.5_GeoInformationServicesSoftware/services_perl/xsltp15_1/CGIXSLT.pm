# CGIXSLT
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.

package CGIXSLT;

require DOPS::XSLT;
require DOPS::XMLTree;
require DOPS::XMLDocument;
use strict;
$CGIXSLT::VERSION=1.5;

BEGIN {
if(exists $ENV{MOD_PERL}){
require Apache;
require 'DOPS/autoxsl/printheaders_mod.pm';
}
else{
require 'DOPS/autoxsl/printheaders.pm';
};
}

sub printResult {
	my ($fh,$new_xml)=@_;
	printHeaders();
	eval {XSLT::printResultTree($fh,$new_xml);};
	if($@){error($@);};
}

sub error {
my $err=shift;
if(open(LOG,'>> xsltp.log')){
	binmode(LOG);
	print LOG "ERROR:\n", $err;
	my $t=localtime(time);
	print LOG "DATE: ", $t,"\n";
	print LOG "REMOTE_ADDR: ", $ENV{'REMOTE_ADDR'},"\n";
	print LOG "PARAMETERS PASSED:\n";
	for my $key (keys %XSLT::PARAMS)
		{print LOG $key,'="',$XSLT::PARAMS{$key},'"',"\n";};
	print LOG '****************************',"\n";
	close(LOG);
}else{$err.="Can not open log file\n";print STDERR $err;}
	$XSLT::HTTP_HEADERS=undef;
	printHeaders();
	my $errt=undef;
    eval{$errt=XSLT::xsl_error_message($err);};
	if($@){$err.="Error in top xsl:message element:$@\n";}
	if($errt){my $fh=\*STDOUT;$errt->print_html($fh);}
	else{print $err;};
	XSLT::xsl_FINISH();
	exit;
}

sub read_https
{
 my $params_string;
 my $cgi_metod=$ENV{'REQUEST_METHOD'};
 my $content_length=$ENV{'CONTENT_LENGTH'};
 my $content_type=$ENV{'CONTENT_TYPE'};
if(defined($cgi_metod))
{
if ($cgi_metod eq 'POST')
    { if($content_type eq 'application/x-www-form-urlencoded')
		{if(read(STDIN, $params_string, $content_length)!=$content_length)
		 {error("Cannot read parameters passed in STDIN\n");};
		}
	 else
		{eval{require CGI;
	     $XPath::Vars::cgi_object=new CGI();
		 %XSLT::PARAMS=$XPath::Vars::cgi_object->Vars();};
		 if($@){error("CGI error $@\n");};
		 goto start_prog;
	    };
    }
elsif ($cgi_metod eq 'GET' || $cgi_metod eq 'HEAD')
    {
      $params_string=$ENV{'QUERY_STRING'};
    }
else{error("Unknown method $cgi_metod\n");};
 my @params_array=split(/[&;]/,$params_string);
 for my $param (@params_array)
	{my ($key,$value)=split(/=/,$param,2);
     $value=~tr/+/ /;
     $value=~s/%([\dA-Fa-f][\dA-Fa-f])/pack ("C", hex ($1))/eg;
     $key=~tr/+/ /;
     $key=~s/%([\dA-Fa-f][\dA-Fa-f])/pack ("C", hex ($1))/eg;
	 if(!defined($XSLT::PARAMS{$key}))
	 {$XSLT::PARAMS{$key}=$value;}else{$XSLT::PARAMS{$key}=join("\0",$XSLT::PARAMS{$key},$value)};
	};
};

start_prog:
 my $cookie_string='';
 if($ENV{'HTTP_COOKIE'})
	{$cookie_string=$ENV{'HTTP_COOKIE'};}
 elsif($ENV{'COOKIE'})
	{$cookie_string=$ENV{'COOKIE'};};
 if($cookie_string)
	{my @params_cookie=split(/;/,$cookie_string);
     for my $kv (@params_cookie)
	 {$kv=~s/^\s+//sg;
	  $kv=~s/\s+$//sg;
	  if($kv)
		 {my ($key,$value)=split(/=/,$kv,2);
	      if($key){
	      if(!defined($XSLT::PARAMS{$key})){$XSLT::PARAMS{$key}=$value;};};
		 };
     }; 
	};
return \%XSLT::PARAMS;
}

sub  transform
{
my $xml=undef;
my $xml_file=$XSLT::PARAMS{'source'};
if($xml_file){
	if($xml_file=~/\0/sg){die "File ($xml_file) contains string end (0) character\n";};
	if($xml_file!~/\.xml$/){$xml_file=join('',$xml_file,'.xml');};
	$xml=XMLTree->XMLDocument::new($xml_file);
};
my $xsl=undef;
my $st_file='';
if($XSLT::PARAMS{'style'}){
$st_file=$XSLT::PARAMS{'style'};
if($st_file=~/\0/sg){die "File ($st_file) contains string end (0) character\n";};
if($st_file!~/\.xsl$/){$st_file=join('',$st_file,'.xsl');};
$xsl=XMLTree->XMLDocument::new($st_file);
}
else{
	if($xml and $xml->{_stylesheet}){
	$xsl=XMLDocument::new($xml,$xml->{_stylesheet});
	}
    else{
		if($xml_file=~/\.xml$/){
			$st_file=$xml_file;$st_file=~s/\.xml$/\.xsl/;
			$xsl=XMLTree->XMLDocument::new($st_file);
			};
		};
	};
if($xsl)
{
my $ch_model=$xsl->{_chars_model};
if($ch_model){$XSLT::CHARS_MODEL=$ch_model;};
if($XSLT::CHARS_MODEL ne 'bytes'){
	my @pnames=keys %XSLT::PARAMS;
	if($] > 5.007){require Encode;};
	foreach my $pname (@pnames) {
	if($] > 5.007){Encode::_utf8_on($XSLT::PARAMS{$pname});}
	else{$XSLT::PARAMS{$pname}=pack('U0C*', unpack ('C*', $XSLT::PARAMS{$pname}));};
	};
};
my $vars=XSLT::compile($xsl);
my $MAX_CONTENT_SIZE=XSLT::max_content_size();
if(not($xml)){$xml=$xsl->newroot();};
XSLT::set_glob_variables($vars,$xml);
if($ENV{'CONTENT_LENGTH'} and $ENV{'CONTENT_LENGTH'}>$MAX_CONTENT_SIZE){die "exceed data length ($MAX_CONTENT_SIZE) limitation\n";};
$XSLT::DOC_ROOT=$xml;
return XSLT::transform($xml);
}else{die "XSLT stylesheet is not passed\n";};
}

sub params {
	my $params=shift;
	while(my ($name,$val)=each %{$params}) {
	$XSLT::PARAMS{$name}=$val;
	};
	return \%XSLT::PARAMS;
}

sub omit_https {
	$XSLT::_http_output=0;
}
1;
