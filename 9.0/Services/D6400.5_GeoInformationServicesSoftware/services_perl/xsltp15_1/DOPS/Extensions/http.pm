package XSLT;

sub http_headers {
my $e=shift;
if(!$XSLT::HTTP_HEADERS){$XSLT::HTTP_HEADERS=$e->newroot();};
return \&_http_headers;
}

sub http_field {
my $e=shift;
compile_attributes($e);
return \&_element;
}

sub _http_headers {
my ($instr,$current,$rtree)=@_;
eval_template($instr,$current,$XSLT::HTTP_HEADERS);
}
1;