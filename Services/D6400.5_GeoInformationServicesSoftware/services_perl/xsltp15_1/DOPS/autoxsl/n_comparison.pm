package XPath::Expression;
sub _n_comparison {
	my ($expr,$current,$rettype)=@_;
	my $aleft=$expr->{arg}->[0];
	my $aright=$expr->{arg}->[1];
	my $op=$expr->{opname};
	my $left=$aleft->evaluate($current);
	my $right=$aright->evaluate($current);
	if(ref($left) && ref($right)){
		for my $rarg (@{$right})
			{my $rstr=$rarg->string();
		       for my $larg (@{$left}){
				   my $lstr=$larg->string();
					  if($op eq '>')
						{if($lstr>$rstr){return 1;};}
					  elsif($op eq '>=')
                        {if($lstr>=$rstr){return 1;};}
					  elsif($op eq '<')
                        {if($lstr<$rstr){return 1;};}
					  else
                        {if($lstr<=$rstr){return 1;};};
			   };
		    };
	}
	elsif(ref($left)){
		for my $arg (@{$left}){
		my $lstr=$arg->string();
					  if($op eq '>')
						{if($lstr>$right){return 1;};}
					  elsif($op eq '>=')
                        {if($lstr>=$right){return 1;};}
					  elsif($op eq '<')
                        {if($lstr<$right){return 1;};}
					  else
                        {if($lstr<=$right){return 1;};};
		};
 	}
	elsif(ref($right)){
		for my $arg (@{$right}){
		my $rstr=$arg->string();
					  if($op eq '>')
						{if($left>$rstr){return 1;};}
					  elsif($op eq '>=')
                        {if($left>=$rstr){return 1;};}
					  elsif($op eq '<')
                        {if($left<$rstr){return 1;};}
					  else
                        {if($left<=$rstr){return 1;};};
		};
 	}
	else{
	 if($op eq '>'){if($left>$right){return 1;};}
	 elsif($op eq '>='){if($left>=$right){return 1;};}
	 elsif($op eq '<'){if($left<$right){return 1;};}
	 else{if($left<=$right){return 1;};};
	};
	return 0;
}
1;