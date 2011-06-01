package GDALUtils;

use Date::Calc qw(Add_Delta_DHMS Date_to_Time);
use strict;
use warnings;
use Switch;

# store information about the bands in the raster in a hash
# the returned information is a 2 dimensional hashmap with this structure:
#	elevation1
#		timestamp1
#			bandnumber
#		timestamp2
#			bandnumber
#		...
#	elevation2
#		timestamp1
#			bandnumber
#		...
#	...
sub storeBandInfoDLRFileInHash {
	
	my @input_datasets = @_;
	
	my $band;
	my $base_timestamp;
	my $time_offset;
	my $elevation;
	
	my %elevation_timestamp_band=();

	foreach my $input_dataset (@input_datasets) {

		my @input_lines=`gdalinfo \"$input_dataset\"`;
	
		foreach my $input_line (@input_lines) {

			# match Band followed by a space followed by one or more digits followed by a whitespace character
			# match set parentheses around (\d)* to match all digits
			# the parentheses around \d only match the last digit
			if ($input_line=~/Band\s((\d)+)\s/) {
				# we'll add the previous band (if there was any) to the hash
				if (defined($band) && defined($base_timestamp) && defined($time_offset) && defined($elevation)) {
					$elevation_timestamp_band{$elevation}{calculateTimestamp($base_timestamp,"s",$time_offset)}=$band;
				}
				$band=$1;
				#print "BAND $band\n";
			}

			# match NETCDF_DIMENSION_time= followed by one or more digits followed by a whitespace character
			if ($input_line=~/NETCDF_DIMENSION_time=((\d)+)\s/) {
				$time_offset=$1;
				#print "\tTIME $1\n";
					
			}

			# match NETCDF_time_units=seconds since followed by one or more characters
			if ($input_line=~/NETCDF_time_units=seconds since ((.)*)$/) {
				$base_timestamp=$1;
				#print "\tBASE TIMESTAMP $1\n";
			}
			
			# match NETCDF_DIMENSION_lev= followed by one or more digits followed by a whitespace character or a point
			if ($input_line=~/NETCDF_DIMENSION_lev=((\d)+)[\s,\.]/) {
				$elevation=$1;
				#print "\tELEVATION $1\n";
			}
		}

		# we'll add the last band (if there is any) to the hash
		if (defined($band) && defined($base_timestamp) && defined($time_offset) && defined($elevation)) {
			$elevation_timestamp_band{$elevation}{calculateTimestamp($base_timestamp,"s",$time_offset)}=$band;
		}
	}
	return %elevation_timestamp_band;
}

# store information about the bands in the raster in a hash
# the returned information is a hashmap with this structure if key=timestamp:
#		timestamp
#			'band'
#				bandnumber
#			'variable_name'
#				variable_name
#		timestamp
#			'band'
#				bandnumber
#			'variable_name'
#				variable_name
#		...
# ... a hashmap with this structure if key=band:
#		bandnumber
#			'timestamp'
#				timestamp
#			'variable_name'
#				variable_name
#		bandnumber
#			'timestamp'
#				timestamp
#			'variable_name'
#				variable_name
#		...
sub storeBandInfoACRIFileInHash {

	my ($key_field, @input_datasets) = @_;

	my %bandinfo=();
	
	if ($key_field eq "timestamp" || $key_field eq "band") {
		my $band = "";
		my $base_timestamp = "";
		my $time_offset = "";
		my $variable_name= "";
		
	
		foreach my $input_dataset (@input_datasets) {
	
			my @input_lines=`gdalinfo \"$input_dataset\"`;
		
			foreach my $input_line (@input_lines) {
	
				# match Band followed by a space followed by one or more digits followed by a whitespace character
				# match set parentheses around (\d)* to match all digits
				# the parentheses around \d only match the last digit
				if ($input_line=~/Band\s((\d)+)\s/) {
					# we'll add the previous band (if there was any) to the hash
					(my $key, my %values) = getKeyAndValuesForACRIBandInfo($band, $base_timestamp, $time_offset, $variable_name, $key_field);
					if ($key ne "") {
						my @key_list = keys(%values);
						for my $k (@key_list) {
							$bandinfo{$key}{$k}=$values{$k};
						}
					}
					
					# intialize values
					$band=$1;
					my $base_timestamp = "";
					my $time_offset = "";
					my $variable_name= "";
					#print "BAND $band\n";
				}
	
				# match NETCDF_DIMENSION_day= followed by one or more digits followed by a whitespace character
				if ($input_line=~/NETCDF_DIMENSION_day=((\d)+)\s/) {
					$time_offset=$1;
					#print "\tTIME $1\n";
						
				}
	
				# match NETCDF_day_units=seconds since followed by one or more characters
				if ($input_line=~/NETCDF_day_units=minutes since ((.)*)$/) {
					$base_timestamp=$1;
					#print "\tBASE TIMESTAMP $1\n";
				}
				
				# match NETCDF_VARNAME= followed by one or more characters
				if ($input_line=~/NETCDF_VARNAME=((.)*)$/) {
					$variable_name=$1;
					#print "\tVARIABLE NAME $1\n";
				}
			}
	
			# we'll add the last band (if there is any) to the hash
			(my $key, my %values) = getKeyAndValuesForACRIBandInfo($band, $base_timestamp, $time_offset, $variable_name, $key_field);
			if ($key ne "") {
				my @key_list = keys(%values);
				for my $k (@key_list) {
					$bandinfo{$key}{$k}=$values{$k};
				}
			}
		}
	} else {
		print "No valid key field has been passed to storeBandInfoACRIFileInHash method\n";
	}
	
	return %bandinfo;
}

# store information about the bands in the raster in a hash
# the returned information is a dimensional hashmap with this structure:
#		timestamp1
#			bandnumber
#		timestamp2
#			bandnumber
#		...
sub storeBandInfoSGHFileInHash {

	my (@input_datasets) = @_;

	my %bandinfo=();
	
	my $band = "";
	my $base_timestamp = "2009-06-05 00:00:00";
#	my $base_timestamp = "2009-06-05 01:00:00";
	my $time_offset = "";
	

	foreach my $input_dataset (@input_datasets) {

		my @input_lines=`gdalinfo \"$input_dataset\"`;
	
		foreach my $input_line (@input_lines) {

			# match Band followed by a space followed by one or more digits followed by a whitespace character
			# match set parentheses around (\d)* to match all digits
			# the parentheses around \d only match the last digit
			if ($input_line=~/Band\s((\d)+)\s/) {
				# we'll add the previous band (if there was any) to the hash
				#if (defined($band) && defined($base_timestamp) && defined($time_offset)) {
				if (defined($band) && defined($base_timestamp)) {
					$time_offset=(($band - 1) * 86400);
					$bandinfo{calculateTimestamp($base_timestamp,"s",$time_offset)}=$band;
				}
				# intialize values
				$band=$1;
				my $time_offset = "";
				#print "BAND $band\n";
			}

			# match NETCDF_DIMENSION_number_time_step= followed by one or more digits followed by a whitespace character
			# timestep = 86400, time_offset of first timestep should be 0, so - 1 
			#if ($input_line=~/NETCDF_DIMENSION_number_time_step=((\d)+)\s/) {
			#	$time_offset=(($1 - 1) * 86400);
			#	#print "\tTIME STEP NUMBER $1\n";
			#}
		}

		# we'll add the last band (if there is any) to the hash
		if (defined($band) && defined($base_timestamp) && defined($time_offset)) {
			$time_offset=(($band - 1) * 86400);
			$bandinfo{calculateTimestamp($base_timestamp,"s",$time_offset)}=$band;
		}
	}
	
	return %bandinfo;
}


sub getKeyAndValuesForACRIBandInfo {

	(my $band, my $base_timestamp, my $time_offset, my $variable_name, my $key_field) = @_;
	
	my $timestamp = "";
	if ($base_timestamp ne "" && $time_offset ne "") {
		$timestamp = calculateTimestamp($base_timestamp,"m",$time_offset);
	}

	my $key = "";
	my %values;
	
	if ($key_field eq "timestamp") {
		$key = $timestamp;
		$values{'band'} = $band;
	}
	if ($key_field eq "band") {
		$key = $band;
		$values{'timestamp'} = $timestamp;
	}
	$values{'variable_name'} = $variable_name;
	
	return ($key, %values)
}





# get the BBOX of covering the input datasets
# the returned information is a list containing minx, miny, maxx and maxy
sub getBBox {
	
	my @input_datasets = @_;
	
	my $bbox_minx;
	my $bbox_miny;
	my $bbox_maxx;
	my $bbox_maxy;
		

	foreach my $input_dataset (@input_datasets) {

		my @input_lines=`gdalinfo \"$input_dataset\"`;
	
		foreach my $input_line (@input_lines) {

			# match Lower Left or Upper Right followed by zero or more spaces, a round opening bracket, zero or more spaces,
			# a coordinate (= one or more digits, a point, one or more digits),
			# zero or more spaces, a comma, zero or more spaces,
			# a coordinate (= one or more digits, a point, one or more digits),
			# zero or more spaces and a round closing bracket
			# eg. "Upper Left  (   5.0000000,  51.2500000)"
			# or  "Upper Left  (   5.0000000  ,  51.2500000  )" should match
			#
			# match parentheses around the ordinates to fetch them
			my $ordinate = '([-\d\.])*';

			if ($input_line=~/Lower Left(\s)*\((\s)*($ordinate)(\s)*,(\s)*($ordinate)(\s)*\)/) {

				#print "Matched input line: $input_line";
				#3rd and 7th parameter are minx and miny
				my $bb_minx = $3;
				if (!(defined $bbox_minx) || $bb_minx < $bbox_minx) {
					$bbox_minx = $bb_minx;
				}
				my $bb_miny = $7;
				if (!(defined $bbox_miny) || $bb_miny < $bbox_miny) {
					$bbox_miny = $bb_miny;
				}
			}
			if ($input_line=~/Upper Right(\s)*\((\s)*($ordinate)(\s)*,(\s)*($ordinate)(\s)*\)/) {
				#print "Matched input line: $input_line";
				#3rd and 7th parameter are maxx and maxy
				my $bb_maxx = $3;
				if (!(defined $bbox_maxx) || $bb_maxx < $bbox_maxx) {
					$bbox_maxx = $bb_maxx;
				}
				my $bb_maxy = $7;
				if (!(defined $bbox_maxy) || $bb_maxy < $bbox_maxy) {
					$bbox_maxy = $bb_maxy;
				}
			}
		}
	}
	return ($bbox_minx, $bbox_miny, $bbox_maxx, $bbox_maxy);
}


# get the minimum and maximum values of a dataset
# the returned information is a list containing the minimum and maximum value
sub getMinMaxOfDatasets {
	
	my $input_dataset = $_[0];
	
	my $min;
	my $max;

	my @input_lines=`gdalinfo -stats \"$input_dataset\"`;
	
	foreach my $input_line (@input_lines) {
		# match STATISTICS_MINIMUM= or STATISTICS_MAXIMUM= followed by a number
		# match parentheses around the values to fetch the values
		if ($input_line=~/STATISTICS_MINIMUM=(([\d\.])*)/) {
			$min = $1;
		}
		if ($input_line=~/STATISTICS_MAXIMUM=(([\d\.])*)/) {
			$max = $1;
		}
	}
	return ($min, $max);
}



# count the number of Bands in this raster
sub countBands {
	
	my @gdalinfo_commands = @_;
	
	my $last_band;

	foreach my $gdalinfo_command (@gdalinfo_commands) {

		my @input_lines=`$gdalinfo_command`;
	
		foreach my $input_line (@input_lines) {

			# match Band followed by a space followed by one or more digits followed by a whitespace character
			# match set parentheses around (\d)* to match all digits
			# the parentheses around \d only match the last digit
			if ($input_line=~/Band\s((\d)+)\s/) {
				$last_band=$1;
				#print "BAND $band\n";
			}
		}
	}
	return $last_band;
}


# get the GDAL format corresponding to the provided mimetype
sub getGDALFormatByMimeType {
	
	my $mimetype =  $_[0];
	
	my $format="";
	
	switch ( $mimetype ) {
		case "image/tiff" 			{ $format="GTiff"; }
		case "application/x-netcdf"	{ $format="netCDF"; }
		case "application/x-hdf"	{ $format="HDF4Image"; }
		case "image/png"			{ $format="PNG"; }
		case "image/jpeg"			{ $format="JPEG"; }
		case "image/gif"			{ $format="GIF"; }
	}
	return $format;
}


# get GDAL format of file
sub getGDALFormatOfFile {
	
	my $input_file = $_[0];
	my $gdal_format = "";

	my @input_lines=`gdalinfo $input_file`;

	foreach my $input_line (@input_lines) {

		# match 'Driver:' followed by a whitespace, followed by some characters, followed by a a slash and some other characters
		# match set parentheses around (.)* to match all characters
		# the parentheses around . only match the last character
		if ($input_line=~/Driver:\s((.)*)\/(.)*/) {
			$gdal_format=$1;
			return $gdal_format;
		}
	}
	return $gdal_format;
}


##################
# HELP FUNCTIONS #
##################

#calculate timestamp based on a base timestamp and an offset
#return the timestamp as the seconds since 1970
sub calculateTimestamp {
	my ($string_base_timestamp,$time_offset_field,$time_offset) = @_;
	my @base_timestamp=convertToDatetime($string_base_timestamp);
	
	#Syntax: Add_Delta_DHMS( $year, $month, $day, $hour, $minute, $second, $days_offset, $hour_offset, $minute_offset, $second_offset);
	my @timestamp;
	if ($time_offset_field eq "m") {
		(@timestamp) = Add_Delta_DHMS(@base_timestamp, 0,0,$time_offset,0);
	} else {
		#default time_offset_field = "s"
		#(my $year, my $month, my $day, my $hour, my $min, my $sec) = Add_Delta_DHMS(@base_timestamp, 0,0,0,$time_offset);
		(@timestamp) = Add_Delta_DHMS(@base_timestamp, 0,0,0,$time_offset);
	}
	#return sprintf("%d-%02d-%02d %02d:%02d:%02d", $year,$month,$day,$hour,$min,$sec);
	#return seconds since 1970
	return Date_to_Time(@timestamp);
}

# converts a timestamp in format YYYY-MM-DD hh:mm:ss into year, month, day, hour, min and sec array
sub convertToDatetime {
	my ($timestamp) = @_;
	#timestamp should be in the format YYYY-MM-DD hh:mm:ss
	if ($timestamp=~/(\d{4})-(\d{2})-(\d{2})\s(\d{2}):(\d{2}):(\d{2})/) {
		my $year = $1;
		my $month = $2;
		my $day = $3;
		my $hour = $4;
		my $min = $5;
		my $sec = $6;
		return ($year,$month,$day,$hour,$min,$sec);
	}
}


# A Perl module must end with a true value or else it is considered not to
# have loaded.  By convention this value is usually 1 though it can be
# any true value.  A module can end with false to indicate failure but
# this is rarely used and it would instead die() (exit with an error).
1;

