# XPath
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.

package XPath::Expression;
require DOPS::XPathvars;
use strict;
use constant xsl_string=>1;
use constant xsl_boolean=>2;
use constant xsl_nodeset=>0;
use constant xsl_group=>-1;
$XPath::Expression::VERSION=1.51;

sub evaluate {
	my $eh=$_[0]->{handler};
	return &$eh($_[0],$_[1],0);
}

sub evaluate_group {
	my($self,$current)=@_;
	my $eh=$self->{handler};
	my $ret=&$eh($self,$current,xsl_group);
	if(!ref($ret)){die "Node\-set or group of node\-sets were expected, instead, scaler returned\n";};
	if(not($ret->[0]) or ($ret->[0] and ref($ret->[0]) eq 'ARRAY')){return $ret;};
	return [$ret];
}

sub string {
	my $eh=$_[0]->{handler};
	my $ret=&$eh($_[0],$_[1],1);
	if(ref($ret)){
	  if($ret->[0]){return $ret->[0]->string();}
	  else{return '';};
	}
	elsif(defined($ret)){return $ret;}
	else{return '';};
}

sub boolean {
	my($self,$current)=@_;
	my $eh=$self->{handler};
	my $ret=&$eh($self,$current,2);
	if(ref($ret)){
	  if($ret->[0]){return 1;}
	  else{return 0;};
	}else{
      if($ret){return 1;}
	  else{return 0;};
	};
}

sub evaluate_for_each {
	my ($expr,$ns)=@_;
	my $sub=$expr->{handler};
#	my @merge=map{@{&$sub($expr,$_,0)}} @{$ns};
	my @merge=();
	foreach my $n (@{$ns}) {
		my $nsi=&$sub($expr,$n,0);push @merge,@{$nsi};
	}
	return \@merge;
}

sub proceed {
	my ($expr,$ns)=@_;
	if($expr->{cond}){
		my $cond=$expr->{cond};
		foreach my $c (@{$cond}) {
			if($c->{opname} eq 'n'){
				my $idx=$c->{value} - 1;
				if($idx>=0){
					if($ns->[$idx]){$ns=[$ns->[$idx]];}
					else{return [];};
				}
				else{return [];};
			}
			else{
				my @fns=();
				XPath::Vars::PushFocus();
				$XPath::Vars::focus=$ns;
				$XPath::Vars::position=0;
				foreach my $n (@{$ns}) {
				$XPath::Vars::position++;
				if($c->boolean($n)){push @fns,$n;};
				}
				XPath::Vars::PopFocus();
				$ns=\@fns;
			};
		};
	};
	if($expr->{next} and $ns->[0]){$ns=evaluate_for_each($expr->{next},$ns);};
    return $ns;
}

sub parse {
	my ($class,$str)=@_;
	my $expr=XPath::Vars::_parse($str);
	if($expr){
	_prepare_expr($expr,$class);
	if(not($XPath::Vars::Error)){return $expr;}
	else{return undef;};
	}else{
		if(not($XPath::Vars::Error)){$XPath::Vars::Error.="expression is not passed\n";};
	    return undef;
	};
}

################## compile #####
sub _prepare_expr {
	my ($expr,$class)=@_;
	my $op=$expr->{opname}||die "invalid expression passed\n";
	if($op eq '/'){
		$expr->{handler}=\&_select_root;
	}
	elsif($op eq '#'){
atbegin:
		my $axis=$expr->{axis};
		my $test=$expr->{name};
		if($axis==0){
			if($test eq '*'){$expr->{handler}=\&_get_child_element;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_child_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_child_text;}
			else{$expr->{handler}=\&_get_child_name;};
		}
		elsif($axis==1){
			require 'DOPS/autoxsl/descendant_or_self.pm';
			if($test eq '*'){$expr->{handler}=\&_get_d_or_self_element;}
			elsif($test eq 'node()'){
				if(not($expr->{cond}) and $expr->{next}){
					my $next=$expr->{next};
					if($next->{axis}==0){
						if(not($next->{cond})){
						$expr->{axis}=4;
						$expr->{next}=$next->{next};
						$expr->{name}=$next->{name};
						goto atbegin;
						};
					};
				}
				$expr->{handler}=\&_get_d_or_self_node;
			}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_d_or_self_text;}
			else{$expr->{handler}=\&_get_d_or_self_name;};
		}
		elsif($axis==2){
			if($test eq 'node()'){$expr->{handler}=\&_get_self_node;}
			else{
			require 'DOPS/autoxsl/self.pm';
			if($test eq '*'){$expr->{handler}=\&_get_self_element;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_self_text;}
			else{$expr->{handler}=\&_get_self_name;};
			};
		}
		elsif($axis==3){
			require 'DOPS/autoxsl/parent.pm';
			if($test eq '*'){$expr->{handler}=\&_get_parent;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_parent;}
			elsif($test eq 'text()'){$expr->{handler}=\&_empty;}
			else{$expr->{handler}=\&_get_parent_name;};
		}
		elsif($axis==4){
			require 'DOPS/autoxsl/descendant.pm';
			if($test eq '*'){$expr->{handler}=\&_get_d_element;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_d_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_d_text;}
			else{$expr->{handler}=\&_get_d_name;};
		}
		elsif($axis==5){
			require 'DOPS/autoxsl/preceding_sibling.pm';
			if($test eq '*'){$expr->{handler}=\&_get_ps_element;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_ps_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_ps_text;}
			else{$expr->{handler}=\&_get_ps_name;};
		}
		elsif($axis==6){
			require 'DOPS/autoxsl/following_sibling.pm';
			if($test eq '*'){$expr->{handler}=\&_get_fs_element;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_fs_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_fs_text;}
			else{$expr->{handler}=\&_get_fs_name;};
		}
		elsif($axis==7){
			require 'DOPS/autoxsl/ancestor.pm';
			if($test eq '*'){$expr->{handler}=\&_get_a_node;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_a_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_empty;}
			else{$expr->{handler}=\&_get_a_name;};
		}
		elsif($axis==8){
			require 'DOPS/autoxsl/ancestor_or_self.pm';
			if($test eq '*'){$expr->{handler}=\&_get_a_or_self_element;}
			elsif($test eq 'node()'){$expr->{handler}=\&_get_a_or_self_node;}
			elsif($test eq 'text()'){$expr->{handler}=\&_get_a_or_self_text;}
			else{$expr->{handler}=\&_get_a_or_self_name;};
		}
		elsif($axis==9){
			if($test eq '*'){$expr->{handler}=\&_get_attributes;}
			else{$expr->{handler}=\&_get_n_attribute;};
		}
		else{die "no handler for axis with code $axis\n";};
	}
	elsif($op eq 'n' or $op eq 's'){$expr->{handler}=\&_scaler;}
	elsif($op eq '$'){$expr->{handler}=\&_get_var;}
	elsif($op eq '='){$expr->{handler}=\&_is_equal;}
	elsif($op eq '!='){$expr->{handler}=\&_is_not_equal;}
	elsif($op eq 'or'){$expr->{handler}=\&_or;}
	elsif($op eq 'and'){$expr->{handler}=\&_and;}
	elsif($op eq '>' or $op eq '>=' or $op eq '<' or $op eq '<='){
		require 'DOPS/autoxsl/n_comparison.pm';
		$expr->{handler}=\&_n_comparison;}
	elsif($op eq '-'){$expr->{handler}=\&_minus;}
	elsif($op eq '+'){$expr->{handler}=\&_plus;}
	elsif($op eq 'div'){$expr->{handler}=\&_div;}
	elsif($op eq 'mod'){$expr->{handler}=\&_mod;}
	elsif($op eq '*'){$expr->{handler}=\&_product;}
	elsif($op eq '|'){$expr->{handler}=\&_union;}
	elsif($op eq 'f'){
		my $fname=$expr->{name};
		my $farg=$expr->{arg};
		if($fname eq 'position'){$expr->{handler}=\&_position;}
		elsif($fname eq 'last'){$expr->{handler}=\&_last;}
		elsif($fname eq 'concat'){
			if($farg){$expr->{handler}=\&_concat;}
			else{$XPath::Vars::Error.="The concat function must have argument\n";return undef;};
		}
		elsif($fname eq 'contains' or $fname eq 'substring-before'
		or $fname eq 'substring-after' or $fname eq 'starts-with'){
			if($farg and $#{$farg}==1){
				require 'DOPS/autoxsl/stringfunc.pm';
				my $func='_';$func.=$fname;
				$func=~s/[\-]/\_/;
				$expr->{handler}=\&$func;
				}
			else{$XPath::Vars::Error.="The $fname function must have two argument\n";return undef;};
		}
		elsif($fname eq 'not'){
			if($farg and $#{$farg}==0){$expr->{handler}=\&_not;}
			else{$XPath::Vars::Error.="The not function must have one argument\n";return undef;};
		}
		elsif($fname eq 'substring'){
			if($farg and $#{$farg}>=1){$expr->{handler}=\&_substring;}
			else{$XPath::Vars::Error.="The substring function must have 2 or 3 arguments\n";return undef;};
		}
		elsif($fname eq 'key'){
			if($farg and $#{$farg}==1){require 'DOPS/autoxsl/key.pm';$expr->{handler}=\&_key;}
			else{$XPath::Vars::Error.="The key function must have two arguments\n";return undef;};
		}
		elsif($fname eq 'save-file'){
			if($farg and $#{$farg}==1){
				if(not($expr->{extern})){
				require 'DOPS/autoxsl/save_file.pm';$expr->{handler}=\&save_file;
				}else{$XPath::Vars::Error.="The save\-file function can not be passed in expression\n";return undef;};
				}
			else{$XPath::Vars::Error.="The save\-file function must have two arguments\n";return undef;};
		}
		elsif($fname eq 'file-name'){
			if($farg and $#{$farg}==0){$expr->{handler}=\&file_name;}
			else{$XPath::Vars::Error.="The file\-name function must have one argument\n";return undef;};
		}
		elsif($fname eq 'document'){
			if($farg and ($#{$farg}==0 or $#{$farg}==1)){
			 if(not($expr->{extern})){
				require 'DOPS/autoxsl/document.pm';
				$expr->{handler}=\&document;
				if($XPath::Vars::context){$expr->{_context}=$XPath::Vars::context;}
				else{$XPath::Vars::Error.="The context of the document function is not resolved\n";return undef;};
			 }else{$XPath::Vars::Error.="The document function can not be passed in expression\n";return undef;};
				}
			else{$XPath::Vars::Error.="The document function must have one or two arguments\n";return undef;};
		}
		elsif($fname eq 'translate'){
			if($farg and $#{$farg}==2){$expr->{handler}=\&_translate;}
			else{$XPath::Vars::Error.="The translate function must have three arguments\n";return undef;};
		}
		elsif($fname eq 'string-length'){$expr->{handler}=\&_string_length;}
		elsif($fname eq 'normalize-space'){$expr->{handler}=\&_normalize_space;}
		elsif($fname eq 'string'){$expr->{handler}=\&_string;}
		elsif($fname eq 'number'){$expr->{handler}=\&_number;}
		elsif($fname eq 'current'){$expr->{handler}=\&current;}
		elsif($fname eq 'count'){
			if($farg and $#{$farg}==0){$expr->{handler}=\&_count;}
			else{$XPath::Vars::Error.="The count function must have one argument\n";return undef;};
		}
		elsif($fname eq 'boolean'){
			if($farg and $#{$farg}==0){$expr->{handler}=\&_boolean;}
			else{$XPath::Vars::Error.="The boolean function must have one argument\n";return undef;};
		}
		elsif($fname eq 'name'){$expr->{handler}=\&_name;}
		elsif($fname eq 'time'){$expr->{handler}=\&_time;}
		elsif($fname eq 'gmtime'){require 'DOPS/autoxsl/gmtime.pm';$expr->{handler}=\&_gmtime;}
		elsif($fname eq 'true'){$expr->{handler}=\&_true;}
		elsif($fname eq 'false'){$expr->{handler}=\&_false;}
		else {
			if(not($expr->{extern})){
			$fname=~s/[\:\-]/\_/;
			$expr->{handler}=\&$fname;
			}else{die "Extension function \"$fname\" passed in external parameter\n";};
		};
	}
	else{die "no handler for operation \"$op\"\n";};

	if($expr->{arg}){foreach my $e (@{$expr->{arg}}) {_prepare_expr($e,$class);};};
	if($expr->{cond}){foreach my $e (@{$expr->{cond}}) {_prepare_expr($e,$class);};};
	if($expr->{next}){_prepare_expr($expr->{next},$class);};
	bless($expr,$class);
}

##################
sub _select_root {
	my ($expr,$current,$rettype)=@_;
	my $e=$expr->{next};
	my $eh=$e->{handler};
	my $r=$current->root();
	if($e){return &$eh($e,$r,0);}else{return [$r];};
}

sub _scaler {
	return $_[0]->{value};
}
##### child axis ####
sub _get_child_node {
	my ($expr,$current,$rettype)=@_;
	my $ns=$current->children();
	return proceed($expr,$ns);
}

sub _get_child_element {
	my ($expr,$current,$rettype)=@_;
	my @nsa=grep{$_->{type}>=0} @{$current->children()};
	my $ns=\@nsa;
	return proceed($expr,$ns);
}

sub _get_child_text {
	my ($expr,$current,$rettype)=@_;
	my @nsa=grep{$_->{type}==-1} @{$current->children()};
	my $ns=\@nsa;
	return proceed($expr,$ns);
}

sub _get_child_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my @nsa=grep{$_->{nameid}==$nid} @{$current->children()};
	my $ns=\@nsa;
	return proceed($expr,$ns);
}

##### attribute axis ####

sub _get_attributes {
	my ($expr,$current,$rettype)=@_;
	my $ns=$current->attributes();
	return proceed($expr,$ns);
}

sub _get_n_attribute {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my @nsa=grep{$_->{nameid}==$nid} @{$current->attributes()};
	return proceed($expr,\@nsa);
}

sub _get_self_node {
	my ($expr,$current,$rettype)=@_;
	my $ns=[$current];
	return proceed($expr,$ns);
}

sub _empty {return [];}
########## functions ##########

sub _substring {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $str=$arg->[0]->string($current);
	my $n1=$arg->[1]->string($current);
	my $n2e=$arg->[2];
	if($n2e){my $n2=$n2e->string($current);return substr($str,$n1-1,$n2);}
	else{return substr($str,$n1-1);};
}

sub _string {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	if($arg){return $arg->[0]->string($current);};
	return $current->string();
}

sub _number {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $d;
    if($arg){$d=$arg->[0]->string($current);}
	else{$d=$current->string();};
	if($d=~/$XPath::Vars::_is_number/){return $d;}else{return 'NaN';};
}

sub _time {return time();}

sub _count {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
    my $ns=$arg->[0]->evaluate($current);
	if(ref($ns)){return $#{$ns}+1;}else{return 0;};
}

sub _boolean {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
    return $arg->[0]->boolean($current);
}

sub _get_var {
	my ($expr,$current,$rettype)=@_;
    my $name=$expr->{name};
	my $i=-1;
	my $flag=0;
	my $val=undef;
	for my $vname (@XPath::Vars::LocalName){
		$i++;if($vname eq $name){$val=$XPath::Vars::Local[$i];$flag=1;goto nline;};
		};
if(exists($XPath::Vars::Glob{$name})){$flag=1;$val=$XPath::Vars::Glob{$name};}
else{if(XSLT::_find_glob_variable($name)){$flag=1;$val=$XPath::Vars::Glob{$name};};};
nline:
     if($flag){
	   if(ref($val)){return proceed($expr,$val);}
	   elsif(defined($val)){return $val;}
	   else{return '';};
     }
     else{die "unknown variable \"$name\"\n";}
}

sub _position {
	return $XPath::Vars::position;
}

sub _last {
	return $#{$XPath::Vars::focus}+1;
}

sub _string_length {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	if($arg){return length $arg->[0]->string($current);}
	else {return length $current->string();};
}

sub _normalize_space {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $str;
	if($arg){$str=$arg->[0]->string($current);}
	else {$str=$current->string();};
	$str=~s/^\s+//g;
	$str=~s/\s+$//g;
	$str=~s/\s+/ /g;
	return $str;
}

sub _name {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	if($arg){
	my $ns=$arg->[0]->evaluate($current);
	if($ns and $ns->[0]){return $ns->[0]->name();};
	}
	else {return $current->name();};
}

sub _concat {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my @strs=map {$_->string($current)} @{$arg};
	return join('',@strs);
}

sub _translate {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $str=$arg->[0]->string($current);
	my $rlist=$arg->[1]->string($current);
	my $slist=$arg->[2]->string($current);
	$rlist=~s/(\W)/\\$1/gs;
	$slist=~s/(\W)/\\$1/gs;
	eval "\$str=\~tr\/$rlist\/$slist\/d";
	if($@){warn "translate function error $@\n";};
	return $str;
}

sub file_name {
	my ($expr,$current,$rettype)=@_;
	my $ename=$expr->{arg}->[0];
	my $name=$ename->string($current);
    if($name=~/[\\\/\:]?([\w\-\.]+)$/){return $1;}else{return ''};
}

sub current {
	my ($expr,$current,$rettype)=@_;
	my $c=XPath::Vars::getCurrent();
	my $ns=[$c];
	return proceed($expr,$ns);
}

sub system_property {
	my ($expr,$current,$rettype)=@_;
	my $ename=$expr->{arg}->[0];
	my $name=$ename ? $ename->string($current):'';
	if($name and defined($ENV{$name})){return $ENV{$name};}else{return '';};
 }

sub sum {
	my ($expr,$current,$rettype)=@_;
    my $arg1=$expr->{arg}->[0];
	my $ns=$arg1 ? $arg1->evaluate($current):'NaN';
	if(ref($ns)){
		my $sum=0;for my $n (@{$ns}){$sum+=$n->string();};return $sum;
	}else{if($ns=~/$XPath::Vars::_is_number/){return $ns}else{return 'NaN';}};
}
########### operations #########
sub _union {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my @union=();
	foreach my $a (@{$arg}) {
		my $ns=$a->evaluate($current);
		if(ref($ns)){
			foreach my $n (@{$ns}) {
				if(not($n->{_included})){$n->{_included}=1;push @union,$n;};
			};
		};
	};
    foreach my $un (@union) {$un->{_included}=undef;};
	return proceed($expr,\@union);
}

sub _is_equal {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
	my $left=$aleft->evaluate($current);
	my $right=$aright->evaluate($current);
	if(ref($left) && ref($right)){
		for my $rarg (@{$right})
			{my $rstr=$rarg->string();
		       for my $larg (@{$left}) 
				   {my $lstr=$larg->string();
				    if($rstr eq $lstr){return 1;};};
		    };
        return 0;
	}
	elsif(ref($left)){
		for my $arg (@{$left}){
		my $str=$arg->string();
	    if($str eq $right){return 1;};
		};
        return 0;
	}
	elsif(ref($right)){
		for my $arg (@{$right}){
		my $str=$arg->string();
	    if($str eq $left){return 1;};
		};
        return 0;
	}
	else{
		if($left eq $right){return 1;}else{return 0;};
	};
}

sub _is_not_equal {
	my $eq=&_is_equal;
	if($eq==1){return 0;}else{return 1;};
}

sub _or {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
	my $a=$aleft->boolean($current);
	if($a){return 1;}
	else{$a=$aright->boolean($current);
	if($a){return 1;}else{return 0;}
	};
}

sub _and {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
	my $a=$aleft->boolean($current);
	if(not($a)){return 0;}
	else{$a=$aright->boolean($current);
	if($a){return 1;}else{return 0;}
	};
}

sub _minus {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
    return $aleft->string($current) - $aright->string($current);
}

sub _plus {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
    return $aleft->string($current) + $aright->string($current);
}

sub _product {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
    return  $aleft->string($current) * $aright->string($current);
}

sub _div {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
    return  $aleft->string($current) / $aright->string($current);
}

sub _mod {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
    return  $aleft->string($current) % $aright->string($current);
}

sub _not {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $a=$aleft->boolean($current);
	if($a){return 0;}else{return 1;};
}

sub _true {return 1;}
sub _false{return 0;}

1;