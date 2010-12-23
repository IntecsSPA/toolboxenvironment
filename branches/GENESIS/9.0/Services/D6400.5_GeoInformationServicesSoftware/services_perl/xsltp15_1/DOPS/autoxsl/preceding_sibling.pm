package XPath::Expression;

##### preceding-sibling axis ####
sub _get_ps_node {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	$idx=$idx-2;
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i>=0 ;$i--) {
		my $ch=$chs->[$i];if($ch->{type}>=-1){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_ps_element {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	$idx=$idx-2;
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i>=0 ;$i--) {
		my $ch=$chs->[$i];if($ch->{type}>=0){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_ps_text {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	$idx=$idx-2;
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i>=0 ;$i--) {
		my $ch=$chs->[$i];if($ch->{type}==-1){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_ps_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my $idx=$current->position()||return [];
	my $ns=[];
	my $chs=$current->parent()->children();
	$idx=$idx-2;
	for (my $i=$idx;$i>=0 ;$i--) {
		my $ch=$chs->[$i];if($ch->{nameid}==$nid){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

1;