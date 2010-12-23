package XPath::Expression;
########## ancestor_or_self axis ######
sub _get_a_or_self_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	my $n=$current->parent();
	my $ns;
	if($current->{nameid}==$nid){$ns=[$current];}else{$ns=[];};
	while($n){if($n->{nameid}==$nid){push @{$ns},$n};$n=$n->parent();};
	return proceed($expr,$ns);
}
sub _get_a_or_self_node {
	my ($expr,$current,$rettype)=@_;
	my $n=$current->parent();
	my $ns=[$current];
	while($n){push @{$ns},$n;$n=$n->parent();};
	return proceed($expr,$ns);
}
sub _get_a_or_self_element {
	my ($expr,$current,$rettype)=@_;
	my $n=$current->parent();
	my $ns;
	if($current->type()>=0){$ns=[$current];}else{$ns=[];};
	while($n){push @{$ns},$n;$n=$n->parent();};
	return proceed($expr,$ns);
}
sub _get_a_or_self_text {
	my ($expr,$current,$rettype)=@_;
	if($current->{type}==-1){
	my $ns=[$current];return proceed($expr,$ns);}
	else{return [];};
}
1;