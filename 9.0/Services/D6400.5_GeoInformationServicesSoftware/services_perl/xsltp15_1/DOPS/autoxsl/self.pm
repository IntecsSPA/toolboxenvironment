package XPath::Expression;
##### self axis ####
sub _get_self_element {
	my ($expr,$current,$rettype)=@_;
	if($current->{type}>=0){
	my $ns=[$current];return proceed($expr,$ns);}
	else{return [];};
}

sub _get_self_name {
	my ($expr,$current,$rettype)=@_;
	my $nid=$current->code_of_name($expr->{name})||return [];
	if($current->{nameid}==$nid){
	my $ns=[$current];return proceed($expr,$ns);}
	else{return [];};
}

sub _get_self_text {
	my ($expr,$current,$rettype)=@_;
	if($current->{type}==-1){
	my $ns=[$current];return proceed($expr,$ns);}
	else{return [];};
}
1;