package XSLT;
use strict;
sub xsl_processing_instruction {
	my $e=shift;
	my $piname=$e->getAttribute('name')|| die "processing\-instruction must has the name attribute\n";
    $e->{_piname}=compile_attr_expr($piname);
	$e->{_code}=\&_xsl_processing_instruction;
	compile_template($e);
}

sub _xsl_processing_instruction {
	my ($instr,$current,$rtree)=@_;
	my $class=ref($current);
	my $pi=${class}->newroot();
	my $piname=eval_attr_expr($instr->{_piname},$current);
	if($piname=~/^[\w\-]+$/){
	eval_template($instr,$current,$pi);
	$rtree->newtext(join('','<?',$piname,$pi->string(),'?>'))->disable_escaping();
	};
}
1;