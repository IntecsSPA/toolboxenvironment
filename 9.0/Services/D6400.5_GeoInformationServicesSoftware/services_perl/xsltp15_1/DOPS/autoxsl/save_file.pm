package XPath::Expression;

sub save_file {
my ($expr,$current,$rettype)=@_;
 my $arg=$expr->{arg};
 my $hfile=$arg->[0];
 my $newfile=$arg->[1]->string($current);
 if((!ref($hfile)) or ($hfile->{opname} ne '$')){die "The save\-file function has invalid first argument\n";};
 $newfile=~s/\s+//sg;
 if($newfile)
	{if(XMLDocument::is_file_name_notsafe($newfile)){die "File name ($newfile) for upload contains Perl IO chars\n";};
	 if($newfile=~/\0/sg){die "File ($newfile) for upload contains string end (0) character\n";};
     if($newfile=~/\.xsl$/){die "File ($newfile) for upload can not have xsl extension\n";};
	 if($XPath::Vars::cgi_object){
	 my $fname=$hfile->{name};
	 my $fh=undef;
	 eval{$fh=$XPath::Vars::cgi_object->upload($fname);};
	 if($@){die "Error in CGI $@\n";};
	 if($fh){
	 my $com='> '.$newfile;
	 open(FILEUPLOAD,$com) or die "Couldn't open file ($newfile) for uploading\n";
	 binmode(FILEUPLOAD);
	 my $buff='';
	 while(read($fh,$buff,4096)){print FILEUPLOAD $buff;};
	 close(FILEUPLOAD);
	 }else{die "CGI object didn't return a file handler for uploading\n";};
	 }else{die "Couldn't upload file, maybe wrong enctype of the form; use enctype=\"multipart/form-data\"\n";};
	}
else{die "The save\-file function has empty string as second argument\n";};
return $newfile;
}

1;