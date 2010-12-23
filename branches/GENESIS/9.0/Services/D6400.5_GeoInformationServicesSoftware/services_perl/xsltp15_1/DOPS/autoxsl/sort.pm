package XSLT;

sub xsl_sort {
	my $e=shift;
	my $p=$e->parent();
	my $pname=$p->name();
	if($pname eq 'xsl:apply-templates' or $pname eq 'xsl:for-each'){
		my $xpstr='xsl_sort(';
		my $param=$e->getAttribute('select');
		if(!$param){$param='.';};$xpstr.=$param;$xpstr.=',"';
		$param=$e->getAttribute('order');
		if(!$param){$param='ascending';};$xpstr.=$param;$xpstr.='","';
		$param=$e->getAttribute('data-type');
		if(!$param){$param='text';};$xpstr.=$param;$xpstr.='")';
		my $sort=XPath::Expression->parse($xpstr)|| die "xsl:sort has invalid select attribute\n$XPath::Vars::Error";
		unshift @{$sort->{arg}},$p->{_select};
		$p->{_select}=$sort;
	}else{die "xsl:sort must be a child of apply\-templates or for\-each instructions\n";};
	$e->{_code}=\&_do_nothing;
}

package XPath::Expression;

sub xsl_sort {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $needsort=$arg->[0]->evaluate_group($current);
	my $select=$arg->[1];
	my $order=$arg->[2]->string($current);
	my $d_type=$arg->[3]->string($current);
	my $r_node_set=[];
	foreach my $ns (@{$needsort}) {
	my %skeys=();
	    for my $n (@{$ns})
		{my $val=$select->string($n);
	     if($skeys{$val}){push @{$skeys{$val}},$n;}
		 else{$skeys{$val}=[$n];};
		};
    my @sorted;
	if($d_type eq 'number')
		{if($order eq 'descending')
			{@sorted=sort{$XPath::Expression::b <=> $XPath::Expression::a} keys %skeys;}
         else
			{@sorted=sort{$XPath::Expression::a <=> $XPath::Expression::b} keys %skeys;};
		}
    else
		{if($order eq 'descending')
			{@sorted=sort{$XPath::Expression::b cmp $XPath::Expression::a} keys %skeys;}
         else
			{@sorted=sort keys %skeys;};
	    };

	for my $sval (@sorted){
		my $l_ns=$skeys{$sval};
	    if($rettype==xsl_group){push @{$r_node_set},$l_ns;}
		else{push @{$r_node_set},@{$l_ns};};
	    };

	};
	return $r_node_set;
}

1;