package XSLT;
use strict;
#greeting string the variable is not seen by other extensions
my $greeting='Hello World!';

#top element greeting:string
sub greeting_string {
	my $e=shift;
	$greeting=$e->string();
	#prevent execution if the element is not top 
	return \&_do_nothing;
}

#compiling element greeting:hello
sub greeting_hello {
	my $e=shift;
	return \&_greeting_hello;
}

#executing element greeting:hello
sub _greeting_hello {
	my ($i,$current,$rtree)=@_;
	$rtree->newtext($greeting);
}

sub greeting_FINISH {
	print '<br>Finishing code OK';
}

package XPath::Expression;
# Not documented XPath function greeting:hello() 
# the variable greeting is not seen here 
sub greeting_hello {
	my ($expr,$current,$rettype)=@_;
	return 'Hello World!!';
}

1;