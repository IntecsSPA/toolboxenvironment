package XPath::Expression;

sub document {
	my ($expr,$current,$rettype)=@_;
	my $ns=[];
	my $arg=$expr->{arg};
	my $arg1=$arg->[0]->evaluate($current);
	if(not($arg->[1])){
		if(ref($arg1)){
			foreach my $n (@{$arg1}) {
				push @{$ns}, XMLDocument::new($n,$n->string());
			};
		}
		else{
			my $n=$expr->{_context};
			push @{$ns}, XMLDocument::new($n,$arg1);
		};
	}
	else{
		my $arg2=$arg->[1]->evaluate($current);
		if(not(ref($arg1)) and not(ref($arg2))){
			my $file=XMLDocument::file_name($arg2,$arg1);
			my $class=ref($current);
			my $doc=${class}->XMLDocument::new($file);
			push @{$ns},$doc;
		}
		elsif(not(ref($arg2))){
			my $class=ref($current);
			foreach my $fn (@{$arg1}) {
			my $file=XMLDocument::file_name($arg2,$fn->string());
			my $doc=${class}->XMLDocument::new($file);
			push @{$ns},$doc;
			};
		}
		else{
			my $class=ref($current);
			foreach my $fn (@{$arg1}) {
				foreach my $bn (@{$args}) {
			my $file=XMLDocument::file_name($bn->string(),$fn->string());
			my $doc=${class}->XMLDocument::new($file);
			push @{$ns},$doc;
				};
			};
		};
	};
	return proceed($expr,$ns);
}
1;