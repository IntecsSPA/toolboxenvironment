# XSLT
# CGI XSLT Processor
#
# Copyright (c) 2003-2005 Gennadii Varzugin
# All rights reserved.
# Documentation and support
# http://www.dopscripts.com
# This program is free software. You can redistribute it and/or
# modify it under the same terms as Perl itself.

package XSLT;
require DOPS::XPath;
require UNIVERSAL;
use strict;

$XSLT::VERSION=1.51;
my %template_modes=();
my %template_names=();
my %OPENED_STYLESHEETS=();
my %EXTENSIONS=();
my $NAMESPACE_ALIAS=undef;
%XSLT::PARAMS=();
$XSLT::DOC_ROOT=undef;
my %GLOBAL_VARS=();
my $MAX_CONTENT_SIZE=1024;
my $XML_BASE='';
my $error_message=undef;
my $HTTP_HEADERS_TEMPLATE=undef;
$XSLT::HTTP_HEADERS=undef;
$XSLT::CHARS_MODEL='unicode';
my $output_indent='';
my $output_method='';
my $output_encoding='';
my $omit_xmldecl='no';
$XSLT::_http_output=1;

my @_free_params=();

sub xsl_FINISH {
XPath::Vars::Init();
%template_modes=();
%template_names=();
%OPENED_STYLESHEETS=();
%XSLT::PARAMS=();
$XSLT::DOC_ROOT=undef;
my %GLOBAL_VARS=();
$MAX_CONTENT_SIZE=1024;
$XML_BASE='';
$HTTP_HEADERS_TEMPLATE=undef;
$XSLT::HTTP_HEADERS=undef;
$output_indent='';
$output_method='';
$output_encoding='';
$omit_xmldecl='no';
$XSLT::_http_output=1;
$error_message=undef;
@_free_params=();
my @prefix=keys %EXTENSIONS;
foreach my $func (@prefix) {
	$func.='_FINISH';
	my $function=UNIVERSAL::can('XSLT',$func);
	if($function){&$function();};
};
%EXTENSIONS=();
$NAMESPACE_ALIAS=undef;
$XSLT::CHARS_MODEL='unicode';
}

sub max_content_size{return $MAX_CONTENT_SIZE;};

sub set_template_key  
{
my ($str, $htempl,$templates_keys,$cond)=@_;
my @a=split('/',$str);
if($str eq '/'){push @a,'/';};
if($a[0] eq ''){$a[0]='/';};
my $next='';
my $hname=$templates_keys;
my $hstate={};
my $last=pop @a;
loop: while(1)
{   $next=pop @a;
	if(!defined($hname->{$last})){$hname->{$last}={};};
    $hstate=$hname->{$last};
if(defined($next))
  {
	if($next ne '')
	{
		if(!defined($hstate->{parent})){$hstate->{parent}={};};
		$hname=$hstate->{parent};
		$last=$next;
	}else
	{
		if(!defined($hstate->{ancestor})){$hstate->{ancestor}={};};
		$hname=$hstate->{ancestor};
		$last=pop @a;
	};
  }
  else {last loop;};
};
  if(!defined($hstate->{template}))
	 {$hstate->{template}=[$htempl];
      $hstate->{condition}=[$cond];}
  else
	 {my $conds=$hstate->{condition};
      my $bool=1;
locfor:for(my $i=0;$i<=$#{$conds};$i++){my $ocond=$conds->[$i];
		                               if(not($ocond) and not($cond)){$bool=0;last locfor;};};
	  if($bool)
		 {if($cond){
		   unshift @{$hstate->{template}}, $htempl;
	       unshift @{$hstate->{condition}}, $cond;}
		   else{
		   push @{$hstate->{template}}, $htempl;
	       push @{$hstate->{condition}}, $cond;};
		 };
	  };
}

sub select_condition {
my ($current,$ts)=@_;
my $conds=$ts->{condition};
my $tms=$ts->{template};
my $size=$#{$conds};
	for (my $i=0;$i<=$size;$i++) {
		my $cond=$conds->[$i];
		if(not($cond)){return $tms->[$i];}
		elsif($cond->{opname} eq 'n'){
			if($XPath::Vars::position==$cond->{value}){return $tms->[$i];};
		}
		else{
			if($cond->boolean($current)){return $tms->[$i];};
		};
	};
return undef;
}

sub select_template {
 my ($ha,$mode)=@_;
 my $templates_keys=$template_modes{$mode}||return undef;
 my $str=$ha->name();
 if(!$str){$str='text()';};
 if($ha->{type}==-2){$str='@'.$str;};

 my @stack=();
 my @idx=();
 my @depths=();

 my $p=$ha->parent();
 my $depth=1;
	 
 my $hstep=$templates_keys->{$str};
 if($hstep)
	 {
	 if($ha->{type}>=0)
		 {
	     if($templates_keys->{'*'})
	       {unshift @stack, $templates_keys->{'*'};
	        unshift @idx,$p;
	        unshift @depths,1;};
		 }
     else
		 {if($ha->{type}==-2){
	       if($templates_keys->{'@*'})
	       {unshift @stack, $templates_keys->{'@*'};
	        unshift @idx,$p;
	        unshift @depths,1;};};
		 };
     }
 else{if($ha->{type}>=0){$hstep=$templates_keys->{'*'};}
      else{if($ha->{type}==-2){$hstep=$templates_keys->{'@*'};};};
     };

 my $ret=undef;
 my $order=0;
    while($hstep)
	{   my $bool=0;
		if($hstep->{template}){if($order<$depth){
			                    if(not($hstep->{condition}->[0]))
			                       {$order=$depth;
		                            $ret=$hstep->{template}->[0];
									}
                                else{my $t=select_condition($ha,$hstep);
								     if($t){$order=$depth;$ret=$t;};  
									};
							   };};
		
		if($p)
		{  $str=$p->name();  
		   my $hn=$hstep->{ancestor};
           my $hnp=$hstep->{parent};
		   my $p0=$p;
		   if($hn){if($hn->{'*'}){$bool=1;$p=$p->parent();$depth++;$hstep=$hn->{'*'};
		                          };};
		   if($hnp){if($hnp->{'*'}){
			                      if($bool){
			                      unshift @stack,$hstep;
								  unshift @idx,$p;
								  unshift @depths, $depth;
								  }else{$p=$p->parent();$depth++;};
								  $bool=1;
								  $hstep=$hnp->{'*'};
		                          };};
		   if($hn)
				{
		         my $str0=$str;
			     loop: while($str0)
					{
					 if($hn->{$str0}){
						          if($bool){
                                  unshift @stack,$hstep;
								  unshift @idx,$p;
								  unshift @depths, $depth;
					              }else{$depth++;$p=$p->parent();};
								  $bool=$p;$p0=$p0->parent();
								  $p=$p0;
								  $hstep=$hn->{$str0};
								  last loop;
								  };
					   $p0=$p0->parent();
					   if($p0){$str0=$p0->name();}else{$str0='';};
					};
                };
		   if($hnp){if($hnp->{$str}){
			                      if($bool){
			                      unshift @stack,$hstep;
								  unshift @depths, $depth;
								  unshift @idx,$p;
								    if(ref($bool)){$p=$bool;};
								  }else{$depth++;$p=$p->parent();};
								  $bool=1;
                                  $hstep=$hnp->{$str}; 
		                          };};
		};
		if(!$bool){
	   $hstep=shift @stack;
	   $p=shift @idx;
	   $depth=shift @depths;
		};
	};
if($ret and not($ret->{_compiled})){compile_template($ret);};
return $ret;
}

sub install_templates
{
my $ts=shift;
for my $template (@{$ts}){
my $match='';my $name='';my $mode='';
foreach my $ta (@{$template->attributes()}) {
	my $aname=$ta->name();
	if($aname eq 'match'){$match=$ta->string();}
    elsif($aname eq 'name'){$name=$ta->string();}
    elsif($aname eq 'mode'){$mode=$ta->string();};
}
if(!$mode){$mode='#default';};
my $t_keys=$template_modes{$mode};
if(!$t_keys){$t_keys={};$template_modes{$mode}=$t_keys;};
	if($match)
	{my @patterns=split('\|',$match);
	 for my $s (@patterns)
	 {if($s){
			 my $patern=$s;
			 my $cond=undef;
			 if($patern=~/([^\[]+)\[(.+)\]\s*$/s){
				 $patern=$1;$cond=XPath::Expression->parse($2);
				 if(!$cond){die $XPath::Vars::Error;};
				 };
			   $patern=~s/\s+//g;
			      if($patern=~/(.*)node\(\)$/)
					  {my $newp=$1.'text()';
			           $newp=~s/node\(\)/\*/g;
					   $newp=~s/child\:\://g;
					   set_template_key($newp,$template,$t_keys,$cond);};
               $patern=~s/node\(\)/\*/g;
			   $patern=~s/attribute\:\:/@/g;
			   $patern=~s/child\:\://g;
			   set_template_key($patern,$template,$t_keys,$cond);
		    };
	 };
	};
	if($name)
	{if(!defined($template_names{$name})){$template_names{$name}=$template;};
	};
};
}

sub xsl_include_tree {
	my $include=shift;
	my $file=$include->getAttribute('href');
	if($file){
	my $stroot=XMLDocument::new($include,$file);
	$file=$stroot->{_file};
	if(not($OPENED_STYLESHEETS{$file})){
    $OPENED_STYLESHEETS{$file}=1;
	return $stroot;
	};
	};
	return $include->newroot();
}

sub install_stylesheet
{
	my ($style,$ts,$vars)=@_;
if($style){
	my $ch=$style->children();
	my $i=$#{$ch};
	while($i>=0)
	{my $name=$ch->[$i]->name();
	    if($name eq 'xsl:template'){push @{$ts},$ch->[$i];}
		elsif($name eq 'xsl:key'){
			my $key=$ch->[$i];my $key_n=$key->getAttribute('name');
		    if($key_n){
				if(!defined($XPath::Vars::key_names{$key_n})){
					if(not($key->getAttribute('match'))){die "xsl:key name=\"$key_n\" has no match attribute\n";};
					if(not($key->getAttribute('use'))){die "xsl:key name=\"$key_n\" has no use attribute\n";};
					$XPath::Vars::key_names{$key_n}=$key;
				}
				else{die "redefined key function name \"$key_n\"\n";};
			}else{die "xsl:key element has no name attribute\n";};
		}
		elsif(($name eq 'xsl:include')||($name eq 'xsl:import')){
			install_stylesheet(getStyle(xsl_include_tree($ch->[$i])),$ts,$vars);
		}
		elsif($name eq 'xsl:variable'){push @{$vars},$ch->[$i];}
		elsif($name eq 'xsl:param'){set_glob_params($ch->[$i]);}
		elsif($name eq 'xsltp:max-content-length'){
        my $add=$ch->[$i]->getAttribute('select');
		if($add=~/^-?\d+$/){$MAX_CONTENT_SIZE+=$add;};
		}
		elsif($name eq 'xsl:output'){
        my $out=$ch->[$i];my $outparam=$out->getAttribute('indent');
		 if($outparam and $outparam eq 'yes'){$output_indent='yes';};
		 $outparam=$out->getAttribute('method');
		 if($outparam){if(!$output_method){$output_method=$outparam;};};
		 $outparam=$out->getAttribute('encoding');
		 if($outparam){if(!$output_encoding){$output_encoding=$outparam;};};
		 $outparam=$out->getAttribute('omit-xml-declaration');
		 if($outparam){$omit_xmldecl=$outparam;};
		 $outparam=$out->getAttribute('omit-http-headers');
		 if($outparam and $outparam eq 'yes'){$XSLT::_http_output=0;};
		}
		elsif($name eq 'xsltp:xml-base'){
		if(not($XML_BASE)){
		my $newbase=$ch->[$i]->getAttribute('select');
		$XML_BASE=compile_attr_expr($newbase);
		}else{die "more than one xsltp:xml\-base elements\n";};
		}
		elsif($name eq 'http:headers'){$HTTP_HEADERS_TEMPLATE=$ch->[$i] if !$HTTP_HEADERS_TEMPLATE;}
        elsif($name eq 'xsl:message'){$error_message=$ch->[$i] if !$error_message;}
		elsif($name eq 'xsl:namespace-alias'){
        my $alias=$ch->[$i];my $stpre=$alias->getAttribute('stylesheet-prefix');
		my $rpre=$alias->getAttribute('result-prefix');
		if(!$NAMESPACE_ALIAS){$NAMESPACE_ALIAS={};};
			if($stpre and $rpre and !$NAMESPACE_ALIAS->{$stpre}){
			 if($rpre=~/^[\w\-]+$/){$NAMESPACE_ALIAS->{$stpre}=$rpre;}
			 else{die "invalid namespace result prefix \"$rpre\"\n";};
			};
		}
		else{
				if($name=~/^([\w\-]+)\:.+/){
					if($EXTENSIONS{$1}){
                    my $function=$name;
					$function=~s/[\:\-]/\_/;
					no strict 'refs';
					&$function($ch->[$i]);
					};
				};
			};
		$i--;
	};
};
}

sub set_glob_params {
my $parame=shift;
my $pname=$parame->getAttribute('name')||die "global xsl:param has no name attribute\n";
if((!defined($XPath::Vars::Objects{$pname}))&&(!defined($XPath::Vars::Strings{$pname}))&&(!defined($XPath::Vars::Glob{$pname}))){
my $type=$parame->getAttribute('as');
   if($type){
	   my $passval=$XSLT::PARAMS{$pname};
	   if(defined($passval)){

		my $dsize=$parame->getAttribute('max-size');
		if(defined($dsize)){
			if($dsize=~/^\d+$/){
				if(length($passval)>$dsize){die "exceed length limitation for value of param \"$pname\"\n";};
				}
			else{die "invalid value of max\-size attribute\n";};
		};

			 if($type eq 'expression')
			 {if($passval!~/\$/s){$XPath::Vars::Objects{$pname}=$passval;}
			  else{die "illegal value of parameter \"$pname\" of expression type\n";};
			 }
			 elsif($type eq 'number')
			 {if($passval=~/$XPath::Vars::_is_number/){$XPath::Vars::Objects{$pname}=$passval;}
			      else{die "illegal value of parameter \"$pname\" of number type\n";};
			 }
            elsif($type eq 'int' or $type eq 'integer')
			 {if($passval=~/^-?[0-9]+/){$XPath::Vars::Objects{$pname}=$passval;}
			      else{die "illegal value of of parameter int \"$pname\" of int type\n";};
			 }
            elsif($type eq 'unsigned-int' or $type eq 'unsignedInt')
			 {if($passval=~/^[0-9]+/){$XPath::Vars::Objects{$pname}=$passval;}
			      else{die "illegal value of parameter \"$pname\" of unsignedInt type\n";};
			 }
            elsif($type eq 'positiveInt' or $type eq 'positiveInteger')
			 {if($passval=~/^[0-9]+/ and $passval>0){$XPath::Vars::Objects{$pname}=$passval;}
			      else{die "illegal value of parameter \"$pname\" of positiveInteger type\n";};
			 }
            elsif($type eq 'string'){
				my $echars=$parame->getAttribute('escape-chars');
				if($echars){$passval=~s/([$echars])/\\$1/gs;};
				$XPath::Vars::Strings{$pname}=$passval;
				}
			elsif($type eq 'set'){
		     my @values=split("\0",$passval);
			 my $pset=$parame->newroot();
			 for my $value (@values) {
				 my $nv=$pset->newchild('value');$nv->newtext($value);
			 };
			 $XPath::Vars::Glob{$pname}=[$pset];
			 }
			elsif($type eq 'file'){$XPath::Vars::Glob{$pname}=$passval;}
			else{die "unknown type \"$type\" of param \"$pname\"\n";};

	   }
	   else{
		   #not passed
		   if($type ne 'set' and $type ne 'file'){
			   my $default=$parame->getAttribute('select');
			   if(defined($default)){
				   if($type eq 'string' and $default!~/^(\'|\")/){
				   $XPath::Vars::Strings{$pname}=$default;
				   }else{$XPath::Vars::Objects{$pname}=$default;};
			   }else{
				   $default=$parame->string();
				   if($default){$XPath::Vars::Strings{$pname}=$default;}
				   else{die "Parameter \"$pname\" is not passed\n";};
			   };
		   }
		   else{
			   if($type eq 'file'){$XPath::Vars::Glob{$pname}='';}
			   else{
			 my $pset=$parame->newroot();
			 my $flag=0;
			 for my $value (@{$parame->children()}) {
				 if($value->name() eq 'value'){
					 my $nv=$pset->newchild('value');
					 $nv->newtext($value->string());
					 $flag=1;
				 };
			 };
			 if($flag){$XPath::Vars::Glob{$pname}=[$pset];}
			 else{die "Parameter \"$pname\" is not passed\n";};
			   };
		   };
	   };
   }
   else{
	   #no type
	   my $pval=$parame->getAttribute('select');
	   if($pval){$XPath::Vars::Objects{$pname}=$pval;}
	   else{$XPath::Vars::Strings{$pname}=$parame->string();};
   };
}
else{die "redefined global param or variable name \"$pname\"\n";};
}


sub getStyle {
	my $st_root=shift;
		my $chs=$st_root->children();
	for my $st (@{$chs}){
		my $stname=$st->name();
	    if(($stname eq 'xsl:stylesheet')||($stname eq 'xsl:transform'))	{
			foreach my $a (@{$st->attributes()}) {
				my $aname=$a->name();
				if($aname=~/^xmlns\:([\w\-]+)/){
					$aname=$1;
					my $aval=$a->string();
					if($aval=~/^\#.+/){
						if(not($EXTENSIONS{$aname})){
						$aval=substr($aval,1);
						require $aval || die "Couldn't load extension package $aval\n";
						$EXTENSIONS{$aname}=$aval;
						};
					};
				};
			};
			return $st;
			};
	};
    return undef;
}

sub compile {
	my $st_root=shift;
	my $st=getStyle($st_root);
    my $ts=[];
    my $vars=[];
if($st){
 install_stylesheet($st,$ts,$vars);
 install_templates($ts);
 foreach my $v (@{$vars}) {
    my $select=$v->getAttribute('select');
    my $name=$v->getAttribute('name') || die "Global xsl:variable has no name attribute\n";
	$v->{_pname}=$name;
    if($select){
    $v->{_select}=XPath::Expression->parse($select)|| die "global xsl:variable name=\"$name\" has invalid select attribute\n$XPath::Vars::Error";
    }
    else{compile_template($v);};
 };
};
return $vars;
}

sub set_glob_variables {
	my ($vars,$current)=@_;
	foreach my $p (@{$vars}) {
		my $pname=$p->{_pname};
        if((!defined($XPath::Vars::Objects{$pname}))&&(!defined($XPath::Vars::Strings{$pname}))&&(!defined($GLOBAL_VARS{$pname}))&&(!defined($XPath::Vars::Glob{$pname}))){
		$GLOBAL_VARS{$pname}=$p;
		}else{die "redefined global param or variable name \"$pname\"\n";};
	};
}

sub _find_glob_variable {
	my $name=shift;
	my $p=$GLOBAL_VARS{$name};
	if($p and $XSLT::DOC_ROOT){
	delete $GLOBAL_VARS{$name};
		my $xp=$p->{_select};
		my $val;
		if($xp){$val=$xp->evaluate($XSLT::DOC_ROOT);}
		else{$val=newTree($p,$XSLT::DOC_ROOT);};
		$XPath::Vars::Glob{$name}=$val;
		return 1;
	}else{return 0;};
}

sub transform {
	my $current=shift;
	my $rtree=$current->newroot();
	if($XML_BASE){$XML_BASE=eval_attr_expr($XML_BASE,$current);$rtree->{_file}=$XML_BASE;};
	if($XSLT::_http_output and $HTTP_HEADERS_TEMPLATE){
		compile_template($HTTP_HEADERS_TEMPLATE);
		my $https=$HTTP_HEADERS_TEMPLATE->newroot();
		eval_template($HTTP_HEADERS_TEMPLATE,$current,$https);
		$XSLT::HTTP_HEADERS=$https;
		$HTTP_HEADERS_TEMPLATE=undef;
	};
	
	my $t=select_template($current,'#default');
	if($t){eval_template($t,$current,$rtree);}
	else{_default_template($current,$rtree,'#default');};
	return $rtree;
}

sub eval_template {
	my ($instr,$current,$rtree)=@_;
	my $codes=$instr->children();
	foreach my $c (@{$codes}) {
		my $func=$c->{_code} || die 'instruction "',$c->name(),"\" is not compiled\n";
		&$func($c,$current,$rtree);
	};
	if($instr->{_nvars}){XPath::Vars::Free($instr->{_nvars});};
}

sub _for_each {
	my ($instr,$current,$rtree)=@_;
	my $xps=$instr->{_select};
    XPath::Vars::PushFocus();
	my $ns=$xps->evaluate($current);
	if(!ref($ns)){die "xsl:for\-each select attribute must be a node\-set\n";};
	$XPath::Vars::focus=$ns;
	$XPath::Vars::position=0;
	for my $n (@{$ns})
	{$XPath::Vars::position++;eval_template($instr,$n,$rtree);};
	XPath::Vars::PopFocus();
}

sub xsl_for_each {
	my $foreach=shift;
	my $select=$foreach->getAttribute('select')|| die "xsl:for\-each has no select attribute\n";
	$foreach->{_select}=XPath::Expression->parse($select)|| die "xsl:for\-each has invalid select attribute\n$XPath::Vars::Error";
	$foreach->{_code}=\&_for_each;
	compile_template($foreach);
}

sub _default_template {
	my ($c,$rtree,$mode)=@_;
	if($c->type()<0){$rtree->newtext($c->string());}
	else{
	my $ns=$c->children();
    XPath::Vars::PushFocus();
	$XPath::Vars::focus=$ns;
	$XPath::Vars::position=0;
	for my $n (@{$ns}){
		$XPath::Vars::position++;
		my $t=select_template($n,$mode);
		if($t){eval_template($t,$n,$rtree);}
		else{_default_template($n,$rtree,$mode);};
		};
    XPath::Vars::PopFocus();
	};
}

sub _apply_templates {
	my ($instr,$current,$rtree)=@_;
	my $xps=$instr->{_select};
	my $mode=$instr->{_mode};
    XPath::Vars::PushFocus();
	my $ns=$xps->evaluate($current);
	if(!ref($ns)){die "xsl:apply\-templates select attribute must be a node\-set\n";};
	my $pnum=_init_params($instr,$current);
	$XPath::Vars::focus=$ns;
	$XPath::Vars::position=0;
	for my $n (@{$ns}){
		$XPath::Vars::position++;
		my $t=select_template($n,$mode);
		if($t){
			eval_template($t,$n,$rtree);
			if($_free_params[0]>$pnum){
			XPath::Vars::Free($_free_params[0] - $pnum);
			$_free_params[0]=$pnum;
			};
		}
		else{_default_template($n,$rtree,$mode);};
		};
    $pnum=shift @_free_params;
	if($pnum){XPath::Vars::Free($pnum);};
    XPath::Vars::PopFocus();
}

my $_default_select=XPath::Expression->parse('node()');
sub xsl_apply_templates {
	my $at=shift;
	my $mode=$at->getAttribute('mode');
	my $select=$at->getAttribute('select');
	if($mode){$at->{_mode}=$mode}else{$at->{_mode}='#default'};
	if($select){
	$at->{_select}=XPath::Expression->parse($select)|| die "xsl:apply\-templates has invalid select attribute\n$XPath::Vars::Error";
	}
	else{$at->{_select}=$_default_select;};
	compile_template($at);
	$at->{_code}=\&_apply_templates;
}

sub newTree {
	my ($var,$current)=@_;
	my $nr=$var->newroot();
	eval_template($var,$current,$nr);
	my $chs=$nr->children();
	if($chs->[0]){return [$nr];}
	else{return [];};
}

sub _init_params {
	my ($instr,$current)=@_;
	my $pnum=0;
	if($instr->{_params}){
	foreach my $p (@{$instr->{_params}}) {
		my $xp=$p->{_select};
		my $val;
		if($xp){$val=$xp->evaluate($current);}
		else{$val=newTree($p,$current);};
		unshift @XPath::Vars::Local,$val;
		unshift @XPath::Vars::LocalName,$p->{_pname};
		$pnum++;
	}
	};
	unshift @_free_params,$pnum;
	return $pnum;
}

sub _do_nothing{};

sub xsl_with_param {
	my $wp=shift;
	my $ct=$wp->parent();
	my $params=$ct->{_params};
	if(!$params){$params=[];$ct->{_params}=$params;};
	my $select=$wp->getAttribute('select');
	my $name=$wp->getAttribute('name') || die "xsl:with\-param has no name attribute\n";
	$wp->{_pname}=$name;
if($select){
$wp->{_select}=XPath::Expression->parse($select)|| die "xsl:with\-param name=",$name," has invalid select attribute\n$XPath::Vars::Error";
push @{$params},$wp;
}
else{push @{$params}, compile_template($wp);};
$wp->{_code}=\&_do_nothing;
}

sub _i_param {
	my ($p,$current,$rtree)=@_;
	my $pn=$_free_params[0];
	my $pname=$p->{_pname};
	for (my $k=0;$k<$pn ;$k++) {
		if($XPath::Vars::LocalName[$k] eq $pname){goto subend;};
	};
		my $xp=$p->{_select};
		my $val;
		if($xp){$val=$xp->evaluate($current);}
		else{$val=newTree($p,$current);};
		unshift @XPath::Vars::Local,$val;
		unshift @XPath::Vars::LocalName,$pname;
		$_free_params[0]=$pn+1;
subend:
}

sub xsl_param {
   my $p=shift;
   my $select=$p->getAttribute('select');
   my $name=$p->getAttribute('name') || die "xsl:param has no name attribute\n";
   $p->{_pname}=$name;
   my $pp=$p->parent();
   if($pp->name() ne 'xsl:template'){xsl_variable($p);goto subend;};
   if(defined($pp->{_nvars})){die "xsl:param name=\"$name\" must be decleared before xsl:variable element\n";};
if($select){
$p->{_select}=XPath::Expression->parse($select)|| die "xsl:param name=\"$name\" has invalid select attribute\n$XPath::Vars::Error";
}
else{compile_template($p);};
  $p->{_code}=\&_i_param;
  subend:
}

sub _init_variable {
	my ($p,$current,$rtree)=@_;
		my $xp=$p->{_select};
		my $val;
		if($xp){$val=$xp->evaluate($current);}
		else{$val=newTree($p,$current);};
		unshift @XPath::Vars::Local,$val;
		unshift @XPath::Vars::LocalName,$p->{_pname};
}

sub xsl_variable {
	my $v=shift;
    my $vp=$v->parent();
	if($vp->{_nvars}){$vp->{_nvars}=$vp->{_nvars}+1;}
	else{$vp->{_nvars}=1;};
    my $select=$v->getAttribute('select');
    my $name=$v->getAttribute('name') || die "xsl:variable has no name attribute\n";
	$v->{_pname}=$name;
if($select){
$v->{_select}=XPath::Expression->parse($select)|| die "xsl:variable name=",$name," has invalid select attribute\n$XPath::Vars::Error";
}
else{compile_template($v);};
	$v->{_code}=\&_init_variable;
}

sub xsl_call_template {
	my $ct=shift;
	my $name=$ct->getAttribute('name') || die "xsl:call\-template has no name attribute\n";
    $ct->{_tname}=$name;
	compile_template($ct);
	$ct->{_code}=\&_xsl_call_template;
}

sub _xsl_call_template {
	my ($instr,$current,$rtree)=@_;
	my $tname=$instr->{_tname};
	my $t=$template_names{$tname} || die "no template with name \"$tname\"\n";
	if(not($t->{_compiled})){compile_template($t);};
	_init_params($instr,$current);
	eval_template($t,$current,$rtree);
	my $pnum=shift @_free_params;
	if($pnum){XPath::Vars::Free($pnum);};
}

sub xsl_attribute {
	my $e=shift;
	my $name=$e->getAttribute('name') || die "xsl:attribute has no name attribute\n";
    my $nexpr=compile_attr_expr($name);
	if(ref($nexpr)){$e->{_eval}=$nexpr;}
	else{if($name=~/^[\w\-\:\.]+$/){$e->{_aname}=$name;}else{die "xsl:attribute has illegal name attribute\n";};};
	compile_template($e);
	$e->{_code}=\&_xsl_attribute;
}

sub _xsl_attribute {
	my ($a,$current,$rtree)=@_;
	my $name=$a->{_aname};
	if($a->{_eval}){
	$name=eval_attr_expr($a->{_eval},$current);
	if($name!~/^[\w\-\:\.]+$/){die "xsl:attribute has illegal name attribute\n";};
	};
	my $str='';
    my $ns=newTree($a,$current);
	if($ns->[0]){$str=$ns->[0]->string();};
	$rtree->newattribute($name,$str);
}

sub _attribute {
	my ($a,$current,$rtree)=@_;
	if($a->{_eval}){
	$rtree->cloneAttribute($a,eval_attr_expr($a->{_eval},$current));
	}else{$rtree->copy($a);};
}

sub eval_attr_expr {
	my ($aexpr,$current)=@_;
	if(!ref($aexpr)){return $aexpr;};
	my $data='';
		foreach my $xp (@{$aexpr}) {
			if(ref($xp)){$data.=$xp->string($current);}
			else{$data.=$xp;};
		};
    return $data;
}
sub compile_attr_expr {
	my $str=shift;
	my $elist=[];
	  while($str=~/^(.*?)\{(.*?)\}(.*)/s)
	  {if($1 ne ''){push @{$elist}, $1;};
	   my $sexpr=$2;
	   $str=$3;
       my  $xp=XPath::Expression->parse($sexpr)|| die "invalid expression in attribute value\n$XPath::Vars::Error";
	   push @{$elist},$xp;
	  };
	if($elist->[0]){if($str ne ''){push @{$elist}, $str;};return $elist;};
    return $str;
}

sub compile_attributes {
	my $e=shift;
	foreach my $a (@{$e->attributes()}){
	  my $elist=compile_attr_expr($a->string());
	  if(ref($elist)){$a->{_eval}=$elist;};
	};
}

sub _element {
	my ($instr,$current,$rtree)=@_;
	my $rtnew=$rtree->copy($instr);
	foreach my $a (@{$instr->attributes()}) {_attribute($a,$current,$rtnew);};
	eval_template($instr,$current,$rtnew);
}

sub _xsl_value_of {
	my ($instr,$current,$rtree)=@_;
	my $xp=$instr->{_select};
	my $t=$rtree->newtext($xp->string($current));
	if($instr->{_noe}){$t->disable_escaping();};
}

sub xsl_value_of {
	my $e=shift;
	my $select=$e->getAttribute('select');
	$e->{_select}=XPath::Expression->parse($select)|| die "xsl:value\-of has invalid select attribute\n$XPath::Vars::Error";
	$e->{_code}=\&_xsl_value_of;
	my $noe=$e->getAttribute('disable-output-escaping');
	if ($noe and $noe eq 'yes') {$e->{_noe}=1;};
}

sub _xsl_text {
	my ($instr,$current,$rtree)=@_;
	my $t=$rtree->newtext($instr->string());
	if($instr->{_noe}){$t->disable_escaping()};
}

sub xsl_text {
	my $e=shift;
	$e->{_code}=\&_xsl_text;
	my $noe=$e->getAttribute('disable-output-escaping');
	if($noe and ($noe eq 'yes')){$e->{_noe}=1;};
}

sub _when {
	my ($instr,$current,$rtree)=@_;
	my $xp=$instr->{_test};
	if(not($xp)){
		my $test=$instr->getAttribute('test');
	    $xp=XPath::Expression->parse($test)|| die "xsl:when has invalid test attribute\n$XPath::Vars::Error";
        $instr->{_test}=$xp;
	};
	if($xp->boolean($current)){
		if(not($instr->{_compiled})){compile_template($instr);}
		eval_template($instr,$current,$rtree);
		return 1;
	}
	return 0;
}

sub _if {
	my ($instr,$current,$rtree)=@_;
	my $xp=$instr->{_test};
	if(not($xp)){
		my $test=$instr->getAttribute('test');
	    $xp=XPath::Expression->parse($test)|| die "xsl:if has invalid test attribute\n$XPath::Vars::Error";
        $instr->{_test}=$xp;
	};
	if($xp->boolean($current)){
		if(not($instr->{_compiled})){compile_template($instr);}
		eval_template($instr,$current,$rtree);
	}
}

sub xsl_if {
	my $e=shift;
	$e->{_code}=\&_if;
}

sub xsl_copy {
	my $e=shift;
	$e->{_code}=\&_xsl_copy;
	compile_template($e);
}

sub _xsl_copy {
	my ($instr,$current,$rtree)=@_;
	if($current->parent()){
	my $nr=$rtree->copy($current);
	if($nr->type()>=0){eval_template($instr,$current,$nr);};
	}else{eval_template($instr,$current,$rtree);};
}

sub _otherwise {
	my ($instr,$current,$rtree)=@_;
		if(not($instr->{_compiled})){compile_template($instr);}
		eval_template($instr,$current,$rtree);
		return 1;
}

sub _choose {
	my ($instr,$current,$rtree)=@_;
	foreach my $ch (@{$instr->children()}) {
		if($ch->{_code}){
			my $wi=$ch->{_code};
			if(&$wi($ch,$current,$rtree)){goto subend;};
		};
	};
	subend:
}

sub xsl_choose {
	my $e=shift;
	foreach my $ch (@{$e->children()}) {
		my $name=$ch->name();
		if($name and $name eq 'xsl:when'){$ch->{_code}=\&_when;}
		elsif($name and $name eq 'xsl:otherwise'){$ch->{_code}=\&_otherwise;};
	}
	$e->{_code}=\&_choose;
}

sub _text {
	my ($instr,$current,$rtree)=@_;
	$rtree->copy($instr);
}

sub xsl_error_message {
	my $err=shift;
if($error_message){
	compile_template($error_message);
	my $ermsg=$error_message->newroot();
	my $c=$ermsg->newroot();
	$c->newchild('error')->newtext($err);
	eval_template($error_message,$c,$ermsg);
	return $ermsg;
}else{return undef;};
}

sub _xsl_message {
	my ($instr,$current,$rtree)=@_;
	my $msg=$rtree->newroot();
	eval_template($instr,$current,$msg);
	my $terminate=$instr->getAttribute('terminate');
	my $hmsg=$instr->getAttribute('msg-handler');
		if($hmsg){
			my $msgattrs={};
			foreach my $a (@{$instr->attributes()}) {
				my $aname=$a->name();
				my $aval=$a->string();
				my $aexpr=compile_attr_expr($aval);
				$msgattrs->{$aname}=eval_attr_expr($aexpr,$current);
			}
			eval "require $hmsg;\&$hmsg\:\:message_handler(\$msg,\$msgattrs);";
			if($@){die "xsl:message return error: $@\n";};
		}
		else{
        my $fh=\*STDOUT;
		CGIXSLT::printHeaders();
        printResultTree($fh,$msg);
		};
if($terminate and $terminate eq 'yes'){xsl_FINISH();exit;};
}

sub _xsl_copy_of {
	my ($instr,$current,$rtree)=@_;
	my $xp=$instr->{_select};
	my $ns=$xp->evaluate($current);
	if(ref($ns)){
		foreach my $n (@{$ns}) {$rtree->copy_of($n);};
	}else{$rtree->newtext($ns);};
}

sub xsl_copy_of {
	my $e=shift;
	my $select=$e->getAttribute('select');
	$e->{_select}=XPath::Expression->parse($select)|| die "xsl:copy\-of has invalid select attribute\n$XPath::Vars::Error";
	$e->{_code}=\&_xsl_copy_of;
}

sub compile_template {
	my $t=shift;
	my $chs=$t->children();
	foreach my $e (@{$chs}) {
		$XPath::Vars::context=$e;
		my $ename=$e->name();
		if($ename){
			if($ename!~/^xsl\:/){
				if($ename=~/^([\w\-]+)\:.+/){
					if($EXTENSIONS{$1}){
                    my $function=$ename;
					$function=~s/[\:\-]/\_/;
					no strict 'refs';
					$e->{_code}=&$function($e);
					goto compile;
					};
				};
				$e->{_code}=\&_element;compile_attributes($e);
				compile: compile_template($e);
			}
			else{
				if($ename eq 'xsl:for-each'){xsl_for_each($e);}
				elsif($ename eq 'xsl:apply-templates'){xsl_apply_templates($e);}
				elsif($ename eq 'xsl:value-of'){xsl_value_of($e);}
				elsif($ename eq 'xsl:choose'){xsl_choose($e);}
				elsif($ename eq 'xsl:if'){xsl_if($e);}
				elsif($ename eq 'xsl:variable'){xsl_variable($e);}
				elsif($ename eq 'xsl:call-template'){xsl_call_template($e);}
				elsif($ename eq 'xsl:copy'){xsl_copy($e);}
				elsif($ename eq 'xsl:attribute'){xsl_attribute($e);}
				elsif($ename eq 'xsl:param'){xsl_param($e);}
				elsif($ename eq 'xsl:with-param'){xsl_with_param($e);}
				elsif($ename eq 'xsl:text'){xsl_text($e);}
				elsif($ename eq 'xsl:copy-of'){xsl_copy_of($e);}
				elsif($ename eq 'xsl:message'){$e->{_code}=\&_xsl_message;compile_template($e);}
				elsif($ename eq 'xsl:include'){$e->{_code}=\&_do_nothing;compile(xsl_include_tree($e));}
				elsif($ename eq 'xsl:sort'){require 'DOPS/autoxsl/sort.pm';xsl_sort($e);}
				elsif($ename eq 'xsl:element'){require 'DOPS/autoxsl/xsl_element.pm';xsl_element($e);}
				elsif($ename eq 'xsl:comment'){require 'DOPS/autoxsl/xsl_comment.pm';xsl_comment($e);}
				elsif($ename eq 'xsl:processing-instruction'){require 'DOPS/autoxsl/xsl_processing_instruction.pm';xsl_processing_instruction($e);}
				else{die "instruction \"$ename\" is not supported\n";};
			};
		}
		else{$e->{_code}=\&_text;};
	}
	$t->{_compiled}=1;
	return $t;
}

sub __binmode {
my ($model,$fh)=@_;
if($model eq 'bytes'){binmode($fh);}
else{if($] > 5.007){binmode($fh,":utf8");};};
}

sub printResultTree {
	my ($fh,$result)=@_;
	my $base=$XML_BASE?$XML_BASE:$result->{_file};
	my $model=$result->{_chars_model};
	__binmode($model,$fh);

	 if(($output_method eq 'xml') and ($omit_xmldecl eq 'no')){
		 print $fh '<?xml version="1.0"';
	     if($output_encoding){print $fh ' encoding=',"\"$output_encoding\"";};
	     print $fh '?>';
	  };

	foreach my $ch (@{$result->children()}) {
		my $chname=$ch->name();
		if($ch->type()==-1){$ch->print_text($fh);}
		elsif($chname eq 'xml-document'){
			my $system=$ch->getAttribute('system')||die "system attribute of xml\-document is not set\n";
			my $file=XMLDocument::file_name($base,$system);
			open my $ffh, "> $file" or die "could not open file \"$file\" for writting\n";
			__binmode($model,$ffh);
			printDocument($ffh,$ch);
			close($ffh);
		}
		else{
			if($output_method eq 'xml'){if(!$NAMESPACE_ALIAS){$ch->print_xml($fh);}else{$ch->print_axml($fh,$NAMESPACE_ALIAS);};}
			elsif($output_method eq 'html'){$ch->print_html($fh);}
			elsif($output_method eq 'text'){$ch->print_text($fh);}
			else{
				if(lc($chname) eq 'html'){$ch->print_html($fh);}
				else{if(!$NAMESPACE_ALIAS){$ch->print_xml($fh);}else{$ch->print_axml($fh,$NAMESPACE_ALIAS);};};
			};
		};
	};

}

sub printDocument {
	my ($fh,$doc)=@_;
	my $method=$doc->getAttribute('method');
	my $omit_decl=$doc->getAttribute('omit-xml-declaration');
	my $encod=$doc->getAttribute('encoding');
    if(!$method){$method=$output_method;};
    if(!$encod){$encod=$output_encoding;};
    if(!$omit_decl){$omit_decl=$omit_xmldecl;};
    my $chs=$doc->children();
	 if($method eq 'xml'){
		 if($omit_decl eq 'no'){
		 print $fh '<?xml version="1.0"';
	     if($encod){print $fh ' encoding=',"\"$encod\"";};
	     print $fh '?>';
		 };
		 foreach my $ch (@{$chs}) {
			 if($ch->type()==-1){$ch->print_text($fh);}
			 else{if(!$NAMESPACE_ALIAS){$ch->print_xml($fh);}else{$ch->print_axml($fh,$NAMESPACE_ALIAS);};};
		 };
	  }
      elsif($method eq 'html'){
		 foreach my $ch (@{$chs}) {
			 if($ch->type()==-1){$ch->print_text($fh);}
			 else{$ch->print_html($fh);};
		 };
	  }
      elsif($method eq 'text'){
		 foreach my $ch (@{$chs}) {
			 $ch->print_text($fh);
		 };
	  }
	  else{
		 foreach my $ch (@{$chs}) {
			 my $chname=$ch->name();
			 if($ch->type()==-1){$ch->print_text($fh);}
			 elsif(lc($chname) eq 'html'){$ch->print_html();}
			 else{if(!$NAMESPACE_ALIAS){$ch->print_xml($fh);}else{$ch->print_axml($fh,$NAMESPACE_ALIAS);};};
		 };
	  }

}
1;
