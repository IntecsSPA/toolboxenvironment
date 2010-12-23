package XPath::Expression;
###### parent axis ######
sub _get_parent {
	my ($expr,$current,$rettype)=@_;
	my $n=$current->parent();
	if($n){my $ns=[$n];return proceed($expr,$ns);}
	else{return [];};
}

sub _get_parent_name {
	my ($expr,$current,$rettype)=@_;
	my $n=$current->parent();
	if($n){
    my $nid=$n->code_of_name($expr->{name})||return [];	
	if($n->{nameid}==$nid){
	my $ns=[$n];return proceed($expr,$ns);}
	else{return [];};
	}
	else{return [];};
}
1;