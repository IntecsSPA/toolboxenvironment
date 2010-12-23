package XPath::Expression;

sub _key {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $kname=$arg->[0]->string($current);
	my $kval=$arg->[1]->string($current);
	my $key=$XPath::Vars::stylesheet_keys{$kname};
	if($key){
		my $ns=$key->{$kval};
		if($ns){return proceed($expr,$ns);}else{return [];}; 
	}
	else{
		my $kelement=$XPath::Vars::key_names{$kname};
		if($kelement){
			$XPath::Vars::key_names{$kname}=undef;
			my $match=$kelement->getAttribute('match');
			my $use=$kelement->getAttribute('use');
			my $cuse=XPath::Expression->parse($use)||die "key function with name=\"$kname\" has invalid use attribute\n$XPath::Vars::Error";
            my $hmatch=XPath::Vars::_parse($match)|| die "key function with name=\"$kname\" has invalid match attribute\n$XPath::Vars::Error";
			my $op=$hmatch->{opname};
			if($op ne '/' and $op ne 'f' and $op ne '$'){
               my $newm={};$newm->{opname}='/';
			   my $newn={};$newn->{opname}='#';$newm->{next}=$newn;
			   if($op eq '#'){
				   if($hmatch->{axis}==0){
			           $newn->{axis}=4;
					   $newn->{name}=$hmatch->{name};
					   if($hmatch->{next}){$newn->{next}=$hmatch->{next};};
					   if($hmatch->{cond}){$newn->{cond}=$hmatch->{cond};};
				   }else{
					$newn->{axis}=1;$newn->{name}='*';$newn->{next}=$hmatch;
				   };
			   }elsif($op eq '|'){
				$newn->{axis}=1;$newn->{name}='*';$newn->{next}=$hmatch;
			   }else{die "key function with name=\"$kname\" has invalid match attribute type\n";};
			$hmatch=$newm;
			};
			my $cmatch=_prepare_expr($hmatch,'XPath::Expression')||die "key function with name=\"$kname\" has invalid match attribute\n$XPath::Vars::Error";
			my $ns=$cmatch->evaluate($current);
			if(ref($ns)){
				$key={};$XPath::Vars::stylesheet_keys{$kname}=$key;
				my $tmpf=$XPath::Vars::focus;
				my $tmpp=$XPath::Vars::position;
				$XPath::Vars::focus=$ns;
				$XPath::Vars::position=0;
				foreach my $n (@{$ns}) {
				$XPath::Vars::position++;
				my $uns=$cuse->evaluate($n);
				  if(ref($uns)){
					  foreach my $un (@{$uns}) {
						  my $str=$un->string();
						  if($str ne ''){
							  if(not($key->{$str})){$key->{$str}=[$n];}
							  else{push @{$key->{$str}},$n;};
						  };
					  }
				  }else{
						  if($uns ne ''){
							  if(not($key->{$uns})){$key->{$uns}=[$n];}
							  else{push @{$key->{$uns}},$n;};
						  };
				  };
				}
				$XPath::Vars::focus=$tmpf;
				$XPath::Vars::position=$tmpp;
		    $ns=$key->{$kval};
		    if($ns){return proceed($expr,$ns);}else{return [];}; 
			}else{die "The match attribute of the key function must be a node set\n";};
		}else{die "no key function with name=\"$kname\"\n";};
	}
}

1;