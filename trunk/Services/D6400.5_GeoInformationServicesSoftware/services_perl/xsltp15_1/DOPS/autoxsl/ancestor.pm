package XPath::Expression;
########## ancestor axis ######
sub _get_a_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my $n=$current->parent();
	my $ns=[];
	while($n){if($n->{nameid}==$nid){push @{$ns},$n};$n=$n->parent();};
	return proceed($expr,$ns);
}

sub _get_a_node {
	my ($expr,$current,$rettype)=@_;
	my $n=$current->parent();
	my $ns=[];
	while($n){push @{$ns},$n;$n=$n->parent();};
	return proceed($expr,$ns);
}
1;