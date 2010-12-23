# XMLPrint
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.

use strict;
package XMLPrint;
$XMLPrint::VERSION=1.5;
sub disable_escaping {
	my $self=shift;
	if($self->type()==-1){$self->{noe}=1;};
}

sub print_xml {
	my ($self,$fh)=@_;
	if($self->type()>=0 and $self->name() eq '/'){
		foreach my $ch (@{$self->children()}) {
			$ch->_print_xml($fh);
		}
	}else{$self->_print_xml($fh);};
}

sub _print_xml {
	my ($self,$fh)=@_;
	if($self->type()>=0){
		my $name=$self->name();
        print $fh '<',$name;
		my $ats=$self->attributes();
		for my $a (@{$ats}) {
			$a->_print_xml($fh);
		};
		my $chs=$self->children();
		if($chs->[0]) {
        print $fh '>';
        for my $ch (@{$chs}) {
			$ch->_print_xml($fh);
        };
		print $fh '</',$name,'>';
		}
		else{print $fh '/>';};
	}
	elsif($self->type()==-1){
		my $str=$self->{value};
		if(not($self->{noe})) {
		      $str=~s/\&/\&amp;/gs;
			  $str=~s/</\&lt;/gs;
              $str=~s/>/\&gt;/gs;
              $str=~s/\"/\&quot;/gs;
		};
		print $fh $str;
	}
	elsif($self->type()==-2) {
		print $fh ' ',$self->name(),'="';
		my $str=$self->{value};
			 $str=~s/\&/\&amp;/gs;
			 $str=~s/</\&lt;/gs;
             $str=~s/>/\&gt;/gs;
			 $str=~s/\"/\&quot;/gs;
        print $fh $str,'"';
	};
}

my %_html_empty=('area'=>1,'base'=>1,'basefont'=>1, 'br'=>1, 'col'=>1, 
'frame'=>1, 'hr'=>1, 'img'=>1, 'input'=>1, 'isindex'=>1, 'link'=>1, 'meta'=>1,'param'=>1,
'colgroup'=>1, 'dd'=>1, 'dt'=>1, 'li'=>1, 'option'=>1, 'p'=>1, 'td'=>1, 'tfoot'=>1, 'th'=>1,'thead'=>1,'tr'=>1);
my %_html_codes_el=('script'=>1,'SCRIPT'=>1,'Script'=>1,
'style'=>1,'STYLE'=>1,'Style'=>1);

sub print_html {
	my ($self,$fh)=@_;
	if($self->type()>=0 and $self->name() eq '/'){
		foreach my $ch (@{$self->children()}) {
			$ch->_print_html($fh);
		}
	}else{$self->_print_html($fh);};
}

sub _print_html {
	my ($self,$fh)=@_;
	if($self->type()>=0){
		my $name=$self->name();
        print $fh '<',$name;
		my $ats=$self->attributes();
		for my $a (@{$ats}) {
			$a->_print_html($fh);
		};
		my $chs=$self->children();
		if($chs->[0]) {
        print $fh '>';
		if(not($_html_codes_el{$name})){
         for my $ch (@{$chs}) {
			$ch->_print_html($fh);
         };
		}else{
         for my $ch (@{$chs}) {
			$ch->_print_text($fh);
         };
		};
		print $fh '</',$name,'>';
		}
		else{
		my $nname=lc($name);
		print $fh '>';
		if(not($_html_empty{$nname})){print $fh '</',$name,'>';};
		};
	}
	elsif($self->type()==-1){
		my $str=$self->{value};
		if(not($self->{noe})) {
		      $str=~s/\&/\&amp;/gs;
			  $str=~s/</\&lt;/gs;
              $str=~s/>/\&gt;/gs;
              $str=~s/\"/\&quot;/gs;
			 # $str=~s/\xA0/\&nbsp;/gs;
		};
		print $fh $str;
	}
	elsif($self->type()==-2) {
		my $aname=$self->name();
		print $fh ' ',$aname,'="';
		my $str=$self->{value};
        #if(($aname ne 'href') and ($aname ne 'HREF'))
			#{
			 if($str!~/\&\{/){$str=~s/\&/\&amp;/gs;};
			 #};
             $str=~s/>/\&gt;/gs;
			 $str=~s/\"/\&quot;/gs;
        print $fh $str,'"';
	};
}

sub print_text {
	my ($self,$fh)=@_;
	if($self->type()>=0 and $self->name() eq '/'){
		foreach my $ch (@{$self->children()}) {
			$ch->_print_text($fh);
		}
	}else{$self->_print_text($fh);};
}

sub _print_text {
	my ($self,$fh)=@_;
	if($self->type()>=0){
		my $name=$self->name();
        print $fh '<',$name;
		my $ats=$self->attributes();
		for my $a (@{$ats}) {
			$a->_print_text($fh);
		};
		my $chs=$self->children();
		if($chs->[0]) {
        print $fh '>';
        for my $ch (@{$chs}) {
			$ch->_print_text($fh);
        };
		print $fh '</',$name,'>';
		}
		else{print $fh '/>';};
	}
	elsif($self->type()==-1){
		my $str=$self->{value};
		print $fh $str;
	}
	elsif($self->type()==-2) {
		print $fh ' ',$self->name(),'="';
		my $str=$self->{value};
        print $fh $str,'"';
	};
}

1;