# XPath::Vars
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.
########

package XPath::Vars;
use strict;
$XPath::Vars::VERSION=1.5;
%XPath::Vars::key_names=();
%XPath::Vars::stylesheet_keys=();
%XPath::Vars::Strings=();
%XPath::Vars::Objects=();
%XPath::Vars::Glob=();
@XPath::Vars::Local=(); 
@XPath::Vars::LocalName=();
$XPath::Vars::Error='';
$XPath::Vars::position=0;
$XPath::Vars::focus=[];
$XPath::Vars::cgi_object=undef;
$XPath::Vars::context=undef;

my @_focus_stack=();
my @_count_stack=();
my $extern_flag=0;
#my $XPath::Vars::_is_number='^-?(?:\d+(?:\.\d*)?|\.\d+)$';
$XPath::Vars::_is_number='^-?(?=\d|\.\d)\d*(\.\d*)?([Ee]([+-]?\d+))?$';

sub Init {
%XPath::Vars::key_names=();
%XPath::Vars::stylesheet_keys=();
%XPath::Vars::Strings=();
%XPath::Vars::Objects=();
%XPath::Vars::Glob=();
@XPath::Vars::Local=(); 
@XPath::Vars::LocalName=();
$XPath::Vars::Error='';
$XPath::Vars::position=0;
$XPath::Vars::focus=[];
$XPath::Vars::cgi_object=undef;
$XPath::Vars::context=undef;
@_focus_stack=();
@_count_stack=();
$extern_flag=0;
}

sub getCurrent {
	my $f=$_focus_stack[0] || die "cannot get focus for the current function\n";
	my $pos=$_count_stack[0] || die "cannot get position of the current node\n";
	return $f->[$pos-1];
}

sub PushFocus {
	unshift @_focus_stack,$XPath::Vars::focus;
	unshift @_count_stack,$XPath::Vars::position;
}

sub PopFocus {
	$XPath::Vars::focus=shift @_focus_stack;
	$XPath::Vars::position=shift @_count_stack;
}

sub Free {
	my $num=shift;
	while($num>0){shift @XPath::Vars::Local;shift @XPath::Vars::LocalName;$num--;};
}

my %_axis_code=('descendant-or-self'=>1,'self'=>2,'parent'=>3,
'descendant'=>4,'preceding-sibling'=>5,'following-sibling'=>6,
'ancestor'=>7,'ancestor-or-self'=>8,'attribute'=>9,'child'=>0);

my %_op_code=('|'=>8,'div'=>7,'*'=>7,'mod'=>7,
	'+'=>6,'-'=>6,'!='=>4,'='=>4,'>'=>5,'<'=>5,'>='=>5,
	'<='=>5,'and'=>3,'or'=>2);


sub _xp_get_name {
	my ($str,$tok)=@_;
    if($str=~/(.*)[\-]((?=\d|\.\d)\d*(\.\d*)?([Ee]([+-]?\d+))?)?$/) {
		if($2 ne ''){unshift @{$tok},$2;};
		unshift @{$tok},'-';
		return $1;
    };
	return $str;
}

sub _xp_set_path {
	my ($h,$tok)=@_;
	my  $t=$tok->[0];
	  if($t) {
		  if($t eq '/'){shift @{$tok};$h->{'next'}=_xp_get_path($tok);}
		  elsif($t eq '['){shift @{$tok};$h->{cond}=_xp_get_pred($tok);
		  $t=$tok->[0];
		  if($t and $t eq '/'){shift @{$tok};$h->{'next'}=_xp_get_path($tok);};
		  };
	  };
	  return $h;
}

sub _xp_set_axis {
my ($aname,$test,$tok)=@_;
my $axis=$_axis_code{$aname};
if(not($test)){$test=shift @{$tok};};
if(defined($axis)) {
  if($test) {
  my $h={};
  $h->{axis}=$axis;
  $h->{opname}='#';
  if($tok->[0] and $tok->[0] eq '('){
	  shift @{$tok};
	  if(($tok->[0] and $tok->[0] eq ')')and
		  ($test eq 'node' or $test eq 'text' or $test eq 'comment' or $test eq 'processing-instruction')) {
		shift @{$tok};$test.='()';$h->{name}=$test;return _xp_set_path($h,$tok);
	  }else{$XPath::Vars::Error.="invalid node test \"$test\" in expression: \n";return undef;};
  };
  #$test=_xp_get_name($test,$tok);
  if($test eq '*' or $test=~/^[A-Za-z_]/) {
  $h->{name}=$test;return _xp_set_path($h,$tok);
  }else{$XPath::Vars::Error.="invalid node test \"$test\" in expression: \n";return undef;};
  }
  else{$XPath::Vars::Error.="no node test after axis \"$aname\" in expression \n";return undef;};
}
else{$XPath::Vars::Error.="unknown or not suppored axis \"$aname\" in expression: \n";return undef;};
}

sub _xp_get_path {
	my $tok=shift;
	my $t=shift @{$tok};
	if($t=~/^([\w\-]+)\:\:/) {
       my $aname=$1;
	   my $test=substr($t,length($aname)+2);
	   return _xp_set_axis($aname,$test,$tok);
	}
	elsif($tok->[0] and $tok->[0]=~/^\:\:/) {
		my $test=shift @{$tok};
		$test=substr($test,2);
	    return _xp_set_axis($t,$test,$tok);
	}
	elsif($t=~/^[A-Za-z_\*]/) {
		return _xp_set_axis('child',$t,$tok);
	}
	elsif($t eq '.') {
	   my $h={};
	   $h->{axis}=2;
	   $h->{opname}='#';
	   $h->{name}='node()';
	   return _xp_set_path($h,$tok);
	}
	elsif($t eq '..') {
	   my $h={};
	   $h->{axis}=3;
	   $h->{opname}='#';
	   $h->{name}='node()';
	   return _xp_set_path($h,$tok);
	}
	elsif($t eq '@') {
	  my $h={};
	  $h->{opname}='#';
	  $h->{axis}=9;
	  my $name=shift @{$tok};
	  $name=_xp_get_name($name,$tok);
	  if($name and $name eq '*') {$h->{name}='*';}
	  elsif($name and $name!~/\:\:/ and $name=~/^[A-Za-z_]/) {
	  $h->{name}=$name;
	  }else{$XPath::Vars::Error.="invalid attribute axis argument \"$name\" in epression: \n";return undef;};
	  return _xp_set_path($h,$tok);
	}
	elsif($t eq '/') {
		if($tok->[0] and $tok->[0] ne '/') {
			my $h={};
			$h->{opname}='#';
			$h->{axis}=1;
			$h->{name}='node()';
			$h->{'next'}=_xp_get_path($tok);
			return $h;
		}
		else{$XPath::Vars::Error.="unexpected end of the node-set expression: \n";return undef;};
	}
	elsif($t=~/^\.\.?\-/) {
		$t=_xp_get_name($t,$tok);
		if($t eq '.' or $t eq '..'){unshift @{$tok},$t;return _xp_get_path($tok);}
		else{$XPath::Vars::Error.="invalid token \"$t\" in the node-set of expression: \n";return undef;};
	}
	else{$XPath::Vars::Error.="Invalid token \"$t\" in the node-set of expression: \n";return undef;};
}

sub _xp_get_pred {
	my $tok=shift;
	my @pred=();
	my $p=_xp_get_expr($tok);
	while(not($XPath::Vars::Error) and $tok->[0] and $tok->[0] eq ']') {
		if($p){if($p->{opname} eq 'n' or $p->{opname} eq 's'){
		$p->{opname}='n';$p->{value}=int($p->{value});};
		push @pred,$p;};
		shift @{$tok};
		if($tok->[0] and $tok->[0] eq '['){shift @{$tok};$p=_xp_get_expr($tok);};
	};
	if($pred[0]){return \@pred;}
	else{return undef;};
}

sub _xp_get_func_arg {
	my $tok=shift;
	my @args=();
	my $p=_xp_get_expr($tok);
	while(not($XPath::Vars::Error) and $tok->[0] and $tok->[0] eq ',') {
		if($p){push @args,$p;};
		shift @{$tok};$p=_xp_get_expr($tok);
	};
	if($p){push @args,$p;};
	if($tok->[0] and $tok->[0] eq ')'){shift @{$tok};}
	else{$XPath::Vars::Error.="no closing ) in expression \n";}; 
	if($args[0]){return \@args;}else{return undef;};
}

sub _xp_get_expr {
	my $tok=shift;
	if(defined($tok->[0])) {
		if($tok->[0] eq ')' or $tok->[0] eq ',' or $tok->[0] eq ']'){return undef;};
		my $cb=1;
		my @arg=();
		my @bin=();
		while(defined($tok->[0]) and ($tok->[0] ne ')') and ($tok->[0] ne ',') and ($tok->[0] ne ']')) {
		if($cb==1) {
			my $ar=_xp_get_arg($tok);
			if($ar){$cb=2;push @arg,$ar;}
			else{return undef;};
		}
		else {
			my $b=$tok->[0];
			if($_op_code{$b}) {
				$cb=1;shift @{$tok};push @bin,$b;
			}
			elsif($b=~/^\-/){
				$cb=1;shift @{$tok};push @bin,'-';
				unshift @{$tok},substr($b,1);
			}
			else {$XPath::Vars::Error.="binary operation expected in place of \"$b\" token: \n"; return undef;};
		};
		};
     if($cb==2) {
     my $larg=shift @arg;
	 my $rarg=undef;
	 my $c_bin=undef;
	 my @wait_r=();
	 while(my $b=shift @bin) {
		 $rarg=shift @arg;
		 my $h=$c_bin;
		 if(not($h)){$h={};$h->{opname}=$b;$h->{arg}=[$larg];};
	 if($b ne '|') {
		 my $b0=$bin[0];
		 if($b0 and $b0 eq '|'){
			 my $h0={};$h0->{opname}='|';$h0->{arg}=[$rarg];
		     while ($bin[0] and $bin[0] eq '|') {
			 shift @bin; push @{$h0->{arg}},shift @arg;
		     };
			 $rarg=$h0;$b0=$bin[0];
		 };

		 if(not($b0)){push @{$h->{arg}},$rarg;$larg=$h;}
		 elsif($_op_code{$b}>=$_op_code{$b0}) {
			 push @{$h->{arg}},$rarg;
			 while($wait_r[0] and ($_op_code{$wait_r[0]->{opname}}>=$_op_code{$b0})){
				 push @{$wait_r[0]->{arg}},$h;
				 $h=shift @wait_r;
			 };
			 $larg=$h;$c_bin=undef;
		 }
		 else {
			 my $h0={};$h0->{opname}=$b0;$h0->{arg}=[$rarg];
			 $larg=$h0;$c_bin=$h0;
			 unshift @wait_r,$h;
		 };
	 }
	 else {
		 push @{$h->{arg}},$rarg;
		 while ($bin[0] and $bin[0] eq '|') {
			 shift @bin; push @{$h->{arg}},shift @arg;
		 };
		 $larg=$h;$c_bin=undef;
	 };
	 };
	 while(my $wr=shift @wait_r){push @{$wr->{arg}},$larg;$larg=$wr;};
	 return $larg;
	 }else{$XPath::Vars::Error.="no right argument for operation \"$bin[-1]\" in expression \n";return undef;};
	}else{$XPath::Vars::Error.="unexpected end of expression: \n";return undef;};
}

sub _xp_get_arg {
	my $tok=shift;
	my $t=$tok->[0];
	if($t=~/$XPath::Vars::_is_number/) {
		my $h={};
		$h->{opname}='n';
		$h->{value}=$t;shift @{$tok};return $h;
	}
	elsif($t=~/^[\'\"]/) {
		my $h={};
		$h->{opname}='s';
		my $val=substr($t,1,length($t)-2);
		shift @{$tok};
		if($val=~/\$/s){
	     my @vnames=keys %XPath::Vars::Objects;
         for my $vname (@vnames){$val=~s/\$$vname/$XPath::Vars::Objects{$vname}/gs;};
		};
		$h->{value}=$val;
		return $h;
	}
	elsif($t eq '$') {
	  my $h={};
	  $h->{opname}='$';
	  shift @{$tok};
	  my $name=shift @{$tok};
	  $name=_xp_get_name($name,$tok);
	  if($name!~/^\s*$/) {
		  $h->{name}=$name;
		  if(defined($XPath::Vars::Objects{$name})){
			  my $inexpr=$XPath::Vars::Objects{$name};
			  $extern_flag=1;$h=_parse($inexpr);$extern_flag=0;
			  if(!$h){$XPath::Vars::Error.="\nPassed expression \"$inexpr\" is invalid\n";return undef;};
		      if($h->{opname} eq 'n' or $h->{opname} eq 's'){return $h;};
		  }
		  elsif(defined($XPath::Vars::Strings{$name})){$h->{opname}='s';$h->{value}=$XPath::Vars::Strings{$name};return $h;};
		  }
	  else{$XPath::Vars::Error.="invalid reference to variable in expression: \n";return undef;};
	  return _xp_set_path($h,$tok);
	}
	elsif($t=~/^\w/) {
	  if($tok->[1] and $tok->[1] eq '(') {
		if($t!~/\:\:/) {
			shift @{$tok};shift @{$tok};
			if($t ne 'node' and $t ne 'text' and $t ne 'comment' and 
				$t ne 'processing-instruction'){
				my $h={};
				$h->{opname}='f';
				$h->{name}=$t;
				if($extern_flag){$h->{extern}=$extern_flag;};
				$h->{arg}=_xp_get_func_arg($tok);
				return _xp_set_path($h,$tok);
			}
			else
			{my $h={};
			 $h->{opname}='#';
			 $h->{name}=$t.'()';$h->{axis}=0;
			 my $t1=shift @{$tok};
			 if($t1 and $t1 eq ')'){return _xp_set_path($h,$tok);}
			 else{$XPath::Vars::Error.="invalid token after node test \"$t\" in expression: \n";return undef;};
			};
		};
	  };
		return _xp_get_path($tok);
	}
	elsif($t eq '/') {
		shift @{$tok};
		if($tok->[0] and $tok->[0]=~/^[A-Za-z_\.\*\/\@]/) {
		my $h=_xp_get_path($tok);
		if($h){my $hr={};$hr->{opname}='/';$hr->{'next'}=$h;return $hr;};
		return $h;
		}
		else{my $h={};$h->{opname}='#';$h->{name}='/';$h->{axis}=2;
		my $hr={};$hr->{opname}='/';$hr->{'next'}=$h;return $hr;};
	}
	elsif($t eq '(') {
		shift @{$tok};my $h=_xp_get_expr($tok);
		if($tok->[0] and $tok->[0] eq ')'){shift @{$tok};}
		else{$XPath::Vars::Error.="no closing ) in expression \n";};
		return $h;
	}
	else{return _xp_get_path($tok);};
}

sub _xp_tokens {
my $str=shift;
my @tokens=();
$str=~s/(-?(?=\d|\.\d)\d*(\.\d*)?([Ee]([+-]?\d+))?|[\w\:\.][\w\:\-\.]*|[@\$\(\)\[\],\|\/\+\*\-]|\'[^\']*\'|\"[^\"]*\"|<=|>=|\!=|[><=])/push @tokens,$1;'';/egs;
if($str!~/^\s*$/s){$str=~s/\s+/ /sg;$XPath::Vars::Error.="illegal symbol \"$str\" in expression: \n";return undef;};
return \@tokens;
}

sub _parse {
	my $expr=shift;
	my $tok=_xp_tokens($expr);
	if($XPath::Vars::Error){$XPath::Vars::Error.=$expr;return undef;};
	push @{$tok},')';
	my $hexpr=_xp_get_expr($tok);
		if(not($tok->[0] and $tok->[0] eq ')' and not($tok->[1]))) 
		{if(not($XPath::Vars::Error)){$XPath::Vars::Error.="missing ) or ( in expression \n";};$hexpr=undef;};
	if(not($XPath::Vars::Error)){return $hexpr;}
	else{$XPath::Vars::Error.=$expr;return undef;};
}

1;