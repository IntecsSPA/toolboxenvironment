package DOPS::Test::msg;
use strict;

sub message_handler {
 my ($msg,$params)=@_;
 open my $fh, '> DOPS/Test/test_message.txt' or die "could not open file test_message.txt";
 print $fh "PARAMETERS\:\r\n";
 while (my ($name,$value)=each %{$params}) {
	 print $fh $name,'=',$value,"\r\n";
 };
 print $fh "CONTENT\:\r\n";
 $msg->print_xml($fh);
 close($fh);
}

1;