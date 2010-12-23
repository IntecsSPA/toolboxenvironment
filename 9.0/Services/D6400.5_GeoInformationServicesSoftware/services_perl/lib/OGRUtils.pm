package OGRUtils;
 
use strict;
use warnings;
use Switch;

# get the OGR format corresponding to the provided mimetype
sub getOGRFormatByMimeType {
	
	my $mimetype =  $_[0];
	
	my $format="";
	
	switch ( $mimetype ) {
		case "application/x-esri-shapefile" 		{ $format="ESRI Shapefile"; }
		case "application/x-mapinfo-mif-mid"		{ $format="MapInfo File"; }
		case "image/vdn.dgn"						{ $format="DGN"; }
		case "text/csv"								{ $format="CSV"; }
		case "application/xml"						{ $format="GML"; }
		case "application/vnd.google-earth.kml+xml"	{ $format="KML"; }
		case "application/gpx+xml"					{ $format="GPX"; }
	}
	return $format;
}

# get the OGR format (GRASS style) corresponding to the provided mimetype
sub getOGRFormatByMimeTypeInGrassStyle {
	
	my $mimetype =  $_[0];
	
	my $format="";
	
	switch ( $mimetype ) {
		case "application/x-esri-shapefile" 		{ $format="ESRI_Shapefile"; }
		case "application/x-mapinfo-mif-mid"		{ $format="MapInfo_File"; }
		case "image/vdn.dgn"						{ $format="DGN"; }
		case "text/csv"								{ $format="CSV"; }
		case "application/xml"						{ $format="GML"; }
		case "application/vnd.google-earth.kml+xml"	{ $format="KML"; }
#		case "application/gpx+xml"					{ $format="GPX"; }
	}
	return $format;
}

# get OGR format of file
sub getOGRFormatOfFile {
	
	my $input_file = $_[0];
	my $ogr_format = "";

	my @input_lines=`ogrinfo $input_file`;

	foreach my $input_line (@input_lines) {

		# match 'using driver' followed by a slant single quote,
		# followed by some characters, followed by a single quote, followed by a space followed by 'succesful'
		# match set parentheses around (.)* to match all characters
		# the parentheses around . only match the last character
		if ($input_line=~/using driver `((.)*)' successful/) {
			$ogr_format=$1;
			return $ogr_format;
		}
	}
	return $ogr_format;
}



# A Perl module must end with a true value or else it is considered not to
# have loaded.  By convention this value is usually 1 though it can be
# any true value.  A module can end with false to indicate failure but
# this is rarely used and it would instead die() (exit with an error).
1;

