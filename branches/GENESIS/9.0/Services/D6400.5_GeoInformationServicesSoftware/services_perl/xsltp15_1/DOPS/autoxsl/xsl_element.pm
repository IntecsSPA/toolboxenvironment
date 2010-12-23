package XSLT;
use strict;
sub xsl_element {
	my $e=shift;
	my $name=$e->getAttribute('name') || die "xsl:element has no name attribute\n";
    my $nexpr=compile_attr_expr($name);
	if(ref($nexpr)){$e->{_eval}=$nexpr;}
	else{if($name=~/^[\w\-\:\.]+$/){$e->{_aname}=$name;}else{die "xsl:element has illegal name attribute\n";};};
	compile_template($e);
	$e->{_code}=\&_xsl_element;
}

sub _xsl_element {
	my ($a,$current,$rtree)=@_;
	my $name=$a->{_aname};
	if($a->{_eval}){
	$name=eval_attr_expr($a->{_eval},$current);
	if($name!~/^[\w\-\:\.]+$/){die "xsl:element has illegal name attribute\n";};
	};
	my $el=$rtree->newchild($name);
	eval_template($a,$current,$el);
}
1;