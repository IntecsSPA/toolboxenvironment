# XMLTree
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.

use strict;
package XMLTree;
use DOPS::XMLPrint;
use vars qw(@ISA);
@ISA=qw(XMLPrint);
$XMLTree::VERSION=1.5;

my %element_names_id=('/'=>1);
my @element_names=(undef,'/');
my $names_count=1;

sub print_axml {
	my ($self,$fh,$alias)=@_;
	my @enames=@element_names;
	for(my $i=2;$i<=$#element_names;$i++){
myloop:	while (my ($pre,$rpre)=each %{$alias}) {
			if($element_names[$i]=~/^$pre\:/){
			$element_names[$i]=~s/^$pre\:/$rpre\:/;
			last myloop;
			};
		}
	};
	$self->print_xml($fh);
	@element_names=@enames;
}

sub code_of_name {
	my $name=$_[1];return $element_names_id{$name};
}

sub name {
	my $id=$_[0]->{nameid};
	return $element_names[$id];
}

sub _get_name_id {
if($element_names_id{$_[0]}){
return $element_names_id{$_[0]};
}
else{
push @element_names, $_[0];
$names_count++;$element_names_id{$_[0]}=$names_count;
return $names_count;
};
}

############## constructors #######
sub newroot
{
my $class = shift;
my $self={};
$self->{parent}=undef;
$self->{type}=0;
$self->{nameid}=1;
$self->{attributes}=[];
$self->{children}=[];
if(ref($class)){
	my $root=$class->root();
	$self->{_file}=$root->{_file};
	$self->{_chars_model}=$root->{_chars_model};
	$class=ref($class);
};
bless ($self, $class);
}

sub newchild
{
my $parent=shift;
my $class=ref($parent)|| die "invalid call of child constructor";
my $self={};
$self->{parent}=$parent;
$self->{type}=0;
$self->{nameid}=&_get_name_id;
$self->{attributes}=[];
$self->{children}=[];
bless ($self, $class);
if($parent->{type}>=0){push @{$parent->{children}},$self;}else{die "this node type can not have children";};
return $self;
}

sub newattribute
{my $parent=shift;
my $class=ref($parent)|| die "invalid call of attribute node constructor";
my $self={};
$self->{parent}=$parent;
$self->{type}=-2;
$self->{nameid}=&_get_name_id;
$self->{value}=$_[1];
bless ($self, $class);
if($parent->{type}>=0){push @{$parent->{attributes}},$self;}else{die "this node type can not have children";};
return $self;
}

sub newtext
{
my $parent=shift;
my $class=ref($parent)|| die "invalid call of text node constructor";
my $self={};
$self->{parent}=$parent;
$self->{type}=-1;
$self->{nameid}=0;
$self->{value}=$_[0];
bless ($self, $class);
if($parent->{type}>=0){push @{$parent->{children}},$self;}else{die "this node type can not have children";};
return $self;
}

sub copy {
my ($parent,$n)=@_;
my $class=ref($parent)|| die "invalid call of copy constructor";
if($parent->{type}<0){die "invalid call of copy constructor";};
my $self={};
my $type=$n->{type};
$self->{parent}=$parent;$self->{type}=$type;$self->{nameid}=$n->{nameid};
bless($self,$class);
if($type>=0){$self->{attributes}=[];$self->{children}=[];
push @{$parent->{children}},$self;
}
elsif($type==-1){$self->{value}=$n->{value};
push @{$parent->{children}},$self;
}
elsif($type==-2){$self->{value}=$n->{value};
push @{$parent->{attributes}},$self;
}else{die "invalid XML tree\n";};
return $self;
}

sub copy_of {
my ($parent,$n)=@_;
if($n->{parent}){
my $copy=$parent->copy($n);
if($copy->{type}>=0){
	my $atrs=$n->{attributes};
	foreach my $an (@{$atrs}) {$copy->copy($an);};
	my $chs=$n->{children};
	if($chs->[0]){
		$copy->{_cutoff}=1;
		$copy->{children}=$chs;
	};
};
}else{
	my $chs=$n->{children};
	foreach my $ch (@{$chs}) {$parent->copy_of($ch);};
};
}

sub cloneAttribute {
	my ($parent,$a,$data)=@_;
	my $class=ref($parent)|| die "invalid call of cloneAttribute constructor";
	if($parent->{type}>=0){
	 if($a->{type}==-2){
	 my $self={};
     $self->{parent}=$parent;$self->{type}=-2;$self->{nameid}=$a->{nameid};
	 $self->{value}=$data;bless($self,$class);
	 push @{$parent->{attributes}},$self;return $self;
	 };
	};
	die "invalid call of cloneAttribute constructor";
}

############## end constructors #######

sub name_id {$_[0]->{nameid};}

sub type {$_[0]->{type};}

sub parent {$_[0]->{parent};}

sub test_name {return $_[0]->{nameid}==$_[1]?1:0;}

sub root {
	my $node=$_[0];
	while($node->parent()) {
		$node=$node->parent();
	};
	return $node;
}

sub children
{if($_[0]->{type}>=0){
	if(not($_[0]->{_cutoff})){
	return $_[0]->{children};
    }
    else{
    my $c=$_[0];
	my $chs=$c->{children};
	$c->{children}=[];
	foreach my $ch (@{$chs}) {$c->copy_of($ch);}
	$c->{_cutoff}=undef;
	return $c->{children};
    };
 }
 else{return [];};
}

sub string {
	if($_[0]->{type}<0){return $_[0]->{value};}
	else{my $self=$_[0];my @vals=map{$_->string()} @{$self->children()};return join('',@vals);};
}

sub attributes
{if($_[0]->{type}>=0){return $_[0]->{attributes};}
 else{return [];};
}

sub position
{my $idx=$_[0]->{index};
 if($idx){return $idx;}
 else {
	 if($_[0]->{type}==-2){return undef;}
	 else{
		 my $p=$_[0]->{parent};
		 if($p){
		 my $chs=$p->children();
	     for (my $i=0;$i<=$#{$chs};$i++) {
		  my $ch0=$chs->[$i];$ch0->{index}=$i+1;
		 };
		 return $_[0]->{index};
		 }else{return undef;};
	 };
 };
}

sub addText
{my ($self,$str)=@_;
if($self->{type}==-1){$self->{value}.=$str;}
elsif($self->{type}>=0){
	 my $chs=$self->children();
	 my $i=$#{$chs};
	 if($i>=0){
	 my $ch=$chs->[$i];
	 if($ch->{type}==-1){$ch->{value}.=$str;}
	 else{$self->newtext($str);}
	 }
	 else{$self->newtext($str);};
 };
}

sub getAttribute {
	my ($self,$name)=@_;
	my $nid=$element_names_id{$name}|| return undef;
	my $attrs=$self->{attributes}|| return undef;
	foreach my $a (@{$attrs}) {
		if($a->{nameid}==$nid){return $a->{value};};
	};
	return undef;
}

1;