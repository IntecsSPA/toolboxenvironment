# XMLDocument
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.
########

package XMLDocument;
use strict;
require XML::Parser;
$XMLDocument::VERSION=1.5;
my $_root=undef;
my $_source_style='';
my $CHARS_MODEL='';

sub new {
	my ($treeclass,$file)=@_;
	if(ref($treeclass)){
		my $class=ref($treeclass);
		$_root=${class}->newroot();
		my $o_root=$treeclass->root();
		$_root->{_file}=file_name($o_root->{_file},$file);
	}else{
		$_root=${treeclass}->newroot();
		$_root->{_file}=file_name('',$file);
	};
    my $r=parsefile();
	if($_source_style){$r->{_stylesheet}=$_source_style;$_source_style='';};
	if($CHARS_MODEL){$r->{_chars_model}=$CHARS_MODEL;$CHARS_MODEL='';};
	$_root=undef;
	return $r;
}

sub is_file_name_notsafe
{my $name=shift;
  if ($name =~ /^\s*[|>+]/
      or $name =~ /\|\s*$/) {return 1;};
return 0;
}

sub file_name
{my ($base,$file)=@_;
#the following code from XML::Parser package of 
#Larry Wall and Clark Cooper is what we need here
my $newfile=$file;
  if ($base
      and not ($file =~ m!^(?:[\\/]|\w+:)!))
    {
      $newfile = $base;
      $newfile =~ s![^\\/:]*$!$file!;
    };
  if (is_file_name_notsafe($newfile)) {
  die("File ($newfile) contains Perl IO control characters\n");
  }

return $newfile;
}


sub _escape_
{my $str=shift;
if($str=~/\&/){
 $str=~s/\&lt\;/\</gs;
 $str=~s/\&gt\;/\>/gs;
 $str=~s/\&nbsp\;/\xA0/gs;
 $str=~s/\&quot\;/\"/gs;
 $str=~s/\&apos\;/\'/gs;
 $str=~s/\&amp\;/\&/gs;
};
 return $str;
}

sub _elstart
   { 
	my ($p, $el, %atts)=@_;
	if(defined($p->{CharsData})){
		if($p->{CharsData}!~/^\s*$/s){$_root->newtext($p->{CharsData});};
        $p->{CharsData}=undef;
	};
	my $n=$_root->newchild($el);
	while(my ($aname,$value)=each %atts) {
		$n->newattribute($aname,$value);
	};
	$_root=$n;
   }

sub _elstart_b
   {my $p=$_[0]; 
	if(defined($p->{CharsData})){
		if($p->{CharsData}!~/^\s*$/s){$_root->newtext($p->{CharsData});};
        $p->{CharsData}=undef;
	};
	my $elstr=$p->original_string();
	$elstr=~/^\<(.+?)[\s\/\>]/;
	my $el=$1;
	my $n=$_root->newchild($el);
	$elstr=substr($elstr,length($el)+2);
	$elstr=~s/\/?\>$//gs;
	$elstr=~s/^\s+//g;
	$elstr=~s/\s+$//g;
	if($elstr){
	$elstr=~s/\s*(.+?)\s*\=\s*(\"|\')(.*?)\2/$n->newattribute($1,_escape_($3));/egs;
	};
	$_root=$n;
   }

sub _elend
   {
	my ($p, $el)=@_;
	if(defined($p->{CharsData})){
		if($p->{CharsData}!~/^\s*$/s){$_root->newtext($p->{CharsData});}
		else{if(not($_root->children()->[0])){$_root->newtext($p->{CharsData});};};
        $p->{CharsData}=undef;
	};
	$_root=$_root->parent();
   }

sub _chdata
   {
	my ($p, $str)=@_;
	if(defined($p->{CharsData})){$p->{CharsData}.=$str;}
	else{$p->{CharsData}=$str;};
   }

sub _chdata_b
   {
	my $p=$_[0];
	my $str=$p->original_string();
	if(defined($p->{CharsData})){$p->{CharsData}.=_escape_($str);}
	else{$p->{CharsData}=_escape_($str);};
   }

sub _st_pi
	{my ($p,$target,$data)=@_;
     if($target eq 'xml-stylesheet')
		{if($data=~/href\s*=\s*(\"|\')(.+?)(\"|\')/s){$_source_style=$2;};
		 $p->setHandlers('Proc'=>undef);};
	}

sub _decl
{my ($p, $ver, $enc, $stalone)=@_;
 if($enc and ($enc eq 'bytes')){$p->setHandlers('Char'=>\&_chdata_b,'Start'=>\&_elstart_b);
 if(not($CHARS_MODEL)){$CHARS_MODEL='bytes';};
 }else{if(not($CHARS_MODEL)){$CHARS_MODEL='unicode';};};
}

sub parsefile
{
my $file=$_root->{_file} || die "xml source file name is not passed\n";
my $p0=new XML::Parser(ErrorContext=>1);
$p0->setHandlers('XMLDecl'=>\&_decl, 'Start'=>\&_elstart, 'End'=>\&_elend, 'Char'=>\&_chdata);
$p0->setHandlers('Proc'=>\&_st_pi);
eval{$p0->parsefile($file);};
if($@){die "XML error in file \"$file\"\: $@ \n";};
return $_root;
}

sub parsestring
{my $str=shift;
my $p0=new XML::Parser(ErrorContext=>1);
$p0->setHandlers('XMLDecl'=>\&_decl, 'Start'=>\&_elstart, 'End'=>\&_elend, 'Char'=>\&_chdata);
#$p0->setHandlers('Proc'=>\&_st_pi);
eval{$p0->parsestring($str);};
if($@){die "XML error in string: $@ \n";};
return $_root;
}

1;