package XPath::Expression;

##### following-sibling axis ####
sub _get_fs_node {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i<=$#{$chs} ;$i++) {
		my $ch=$chs->[$i];if($ch->{type}>=-1){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_fs_element {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i<=$#{$chs} ;$i++) {
		my $ch=$chs->[$i];if($ch->{type}>=0){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_fs_text {
	my ($expr,$current,$rettype)=@_;
	my $idx=$current->position()||return [];
	my $ns=[];
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i<=$#{$chs} ;$i++) {
		my $ch=$chs->[$i];if($ch->{type}==-1){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}

sub _get_fs_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my $idx=$current->position()||return [];
	my $ns=[];
	my $chs=$current->parent()->children();
	for (my $i=$idx;$i<=$#{$chs} ;$i++) {
		my $ch=$chs->[$i];if($ch->{nameid}==$nid){push @{$ns},$ch;};
	}
	return proceed($expr,$ns);
}
1;
