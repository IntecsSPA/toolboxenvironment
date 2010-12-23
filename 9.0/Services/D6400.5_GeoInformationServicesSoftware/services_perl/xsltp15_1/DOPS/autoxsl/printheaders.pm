package CGIXSLT;

sub printHeaders {
	if($XSLT::_http_output){
		if($XSLT::HTTP_HEADERS){
			my %printed=();
			foreach my $f (@{$XSLT::HTTP_HEADERS->children()}) {
				if($f->name() eq 'http:field'){
				my $fname=$f->getAttribute('name');
				if($fname){
				$fname=ucfirst(lc($fname));
				my $val=$f->string();
				$val=~s/^\s+//g;$val=~s/\s+$//g;$val=~s/\s+/ /g;
				  if($val){
					  if(not($printed{$fname})){print STDOUT join(':',$fname,$val),"\n";};
                      $printed{$fname}=1; 
				  };
                };
				};
				if($printed{'Content-type'}){print STDOUT "\n";}
				else{print STDOUT "Content-type:text/html\n\n";};
			}
			$XSLT::HTTP_HEADERS=undef;
		}else{print STDOUT "Content-type:text/html\n\n";};
		$XSLT::_http_output=0;
	};
}
1;