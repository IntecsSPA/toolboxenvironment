package XPath::Expression;
##### descendant axis ####
sub _get_d_node {
	my ($expr,$current,$rettype)=@_;
	my $ns=[];
	my @tns=@{$current->children()};
	while(my $ch=shift @tns){
		push @{$ns},$ch;
		my $chs=$ch->children();
		if($chs->[0]){unshift @tns,@{$chs};};
	};
	return proceed($expr,$ns);
}

sub _get_d_element {
	my ($expr,$current,$rettype)=@_;
    my $ns=[];
	my @tns=grep{$_->{type}>=0} @{$current->children()};
	while(my $ch=shift @tns){
		push @{$ns},$ch;
        my $chs=$ch->children();
		my $i=$#{$chs};
		while($i>=0){if($chs->[$i]->{type}>=0){unshift @tns,$chs->[$i];}$i--;};
	};
	return proceed($expr,$ns);
}

sub _get_d_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
    my $ns=[];
	my @tns=@{$current->children()};
	while(my $ch=shift @tns){
		if($ch->{nameid}==$nid){push @{$ns},$ch;};
        my $chs=$ch->children();
		my $i=$#{$chs};
		while($i>=0){if($chs->[$i]->{type}>=0){unshift @tns,$chs->[$i];}$i--;};
	};
	return proceed($expr,$ns);
}

sub _get_d_text {
	my ($expr,$current,$rettype)=@_;
	my $ns=[];
	my @tns=@{$current->children()};
	while(my $ch=shift @tns){
		if($ch->{type}==-1){push @{$ns},$ch;};
		my $chs=$ch->children();
		if($chs->[0]){unshift @tns,@{$chs};};
	};
	return proceed($expr,$ns);
}
1;