package XPath::Expression;

sub _substring_before {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
    my $str1=$arg->[0]->string($current);
	my $str2=$arg->[1]->string($current);
#	$str2=~s/(\W)/\\$1/gs;
	if($str1=~/^(.*?)\Q$str2\E/s){return $1;}else{return $str1;};
}

sub _substring_after {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
    my $str1=$arg->[0]->string($current);
	my $str2=$arg->[1]->string($current);
#    $str2=~s/(\W)/\\$1/gs;
    if($str1=~/^(.*?)\Q$str2\E(.*)/s){return $2;}else{return '';};
}

sub _starts_with {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $str1=$arg->[0]->string($current);
	my $str2=$arg->[1]->string($current);
    #$str2=~s/(\W)/\\$1/gs;
    if($str1=~/^\Q$str2\E/s){return 1;}else{return 0;};
}

sub _contains {
	my ($expr,$current,$rettype)=@_;
	my $arg=$expr->{arg};
	my $str1=$arg->[0]->string($current);
	my $str2=$arg->[1]->string($current);
    #$str2=~s/(\W)/\\$1/gs;
    if($str1=~/\Q$str2\E/s){return 1;}else{return 0;};
}
1;