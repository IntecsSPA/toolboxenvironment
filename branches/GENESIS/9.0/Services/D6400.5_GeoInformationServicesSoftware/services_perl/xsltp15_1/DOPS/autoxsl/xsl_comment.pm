package XSLT;
use strict;
sub _xsl_comment {
	my ($instr,$current,$rtree)=@_;
	my $class=ref($current);
	my $comment=${class}->newroot();
	eval_template($instr,$current,$comment);
	$rtree->newtext(join('','<!--',$comment->string(),'-->'))->disable_escaping();
}

sub xsl_comment {
my $e=shift;
$e->{_code}=\&_xsl_comment;
compile_template($e);
}
1;