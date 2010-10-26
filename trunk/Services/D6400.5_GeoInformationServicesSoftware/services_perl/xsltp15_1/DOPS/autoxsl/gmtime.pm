package XPath::Expression;
sub _gmtime {
my ($expr,$current,$rettype)=@_;
my $t;
my $at=$expr->{arg};
if($at){$t=$at->[0]->string($current);}
else{$t=time();};
if($rettype>0){return gmtime($t);}
else{
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday)=gmtime($t);
	my $rt=$current->newroot();
	$rt->newchild('year')->newtext($year+1900);
	$rt->newchild('month')->newtext($mon+1);
	$rt->newchild('day')->newtext($mday);
	$rt->newchild('hours')->newtext($hour);
	$rt->newchild('minutes')->newtext($min);
	$rt->newchild('seconds')->newtext($sec);
	$rt->newchild('weekday')->newtext($wday+1);
	$rt->newchild('yearday')->newtext($yday+1);
	return $expr->proceed([$rt]);
};
}
1;