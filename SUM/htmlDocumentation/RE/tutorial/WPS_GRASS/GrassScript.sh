 
  
#!/bin/bash

#############################################################################
#
# $Id: execute_TestOperation.sh,v    $
# UPDATED:	
#
# MODULE:   	TEST MODULE 
#
# AUTHOR(S):	Insert autors
#               
# PURPOSE:  	Convert a raster to another file format and stores the resulting raster file.
#
# COPYRIGHT: (C) 2009 Intecs Informatica e tecnologia del Software SpA 
#
#               
#############################################################################

# ********* External Variables ************************************************************************************************
# -----WPS Service Varibles
# *OUTPUT_REPOSITORY : Output Repository Directory 
# *TEMP_REPOSITORY : Temporaney Data created Repository Directory		
# *WPS_PROCESSING_NAME: WPS Processing Name
# *INSTANCE_VALUE: Instance Operation Value (for multiple Instances)
# *GRASS_LOG_FILE: File path for GRASS text LOG		
# *STATUS_FILE: File path for XML Status file		
		
#------Processing Variables	

# *InputRaster: Local Path of Complex Value "InputRaster" defined by reference in the Execute Request. 
#      (Input Description: Input raster file)
   
# *InputRaster_MimeType: Mime Type of "InputRaster" Input Complex Data. 
#      (Mime Type Supported:  image/tiff  application/x-netcdf3  application/x-netcdf4  application/x-hdf  )
    
# *InputRasterMultiple_MULTIPLE_INPUT_FOLDER: (Folder that contains the multiple Complex Value "InputRasterMultiple" Input defined by reference in the Execute Request.)
#      (Input Description: Input raster file)
   
# *InputRasterMultiple_MULTIPLE_INPUT_MimeType: Mime Types List separted from ',' for the multiple "InputRasterMultiple" Complex Data. 
#      (Each value correspond at the Mime Type of the file, called like the list position of the Mime Type value, in the "InputRasterMultiple_MULTIPLE_INPUT_FOLDER" folder)
#      (Supported:  application/x-netcdf4  )
    
# *InputRasterNotMandatory_MULTIPLE_INPUT_FOLDER: (Folder that contains the multiple Complex Value "InputRasterNotMandatory" Input defined by reference in the Execute Request.)
#      (Input Description: Input raster file)
   
# *InputRasterNotMandatory_MULTIPLE_INPUT_MimeType: Mime Types List separted from ',' for the multiple "InputRasterNotMandatory" Complex Data. 
#      (Each value correspond at the Mime Type of the file, called like the list position of the Mime Type value, in the "InputRasterNotMandatory_MULTIPLE_INPUT_FOLDER" folder)
#      (Supported:  application/x-netcdf4  )
    
# *BoundingBox_LOWER_CORNER: Lower Corner Coordinate of "BoundingBox" Input Bounding Box Data.
# *BoundingBox_UPPER_CORNER: Upper Corner Coordinate of "BoundingBox" Input Bounding Box Data.
#      (Input Description: The bounding box to clip the raster to)
# *BoundingBox_EPSG: EPSG of "BoundingBox" Input Bounding Box Data.
#      (EPSG Supported:  )
        
# *BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_LOWER_CORNER: Lower Corner Coordinate List separted from '*' for the "BoundingBoxMultipleNotMandatory" multiple Input Bounding Box Data.
# *BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_UPPER_CORNER: Upper Corner Coordinate List separted from '*' for the "BoundingBoxMultipleNotMandatory" multiple Input Bounding Box Data.
#      (Input Description: The bounding box to clip the raster to)
# *BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_EPSG: EPSG value list separted from ',' of  the "BoundingBoxMultipleNotMandatory" multiple Input Bounding Box Data.
#      (EPSG Supported:  )
        
# *InputFormat: Literal Datata "InputFormat" Input value.
#      (Input Description: The format of the raster input file)
#      (Allowed Values:  netCDF4  )
# *OutputDataType: Literal Datata "OutputDataType" Input value.
#      (Input Description: The data type of the raster output file)
#      (Allowed Values:  Byte  Int16  UInt16  UInt32  Int32  Float32  Float64  CInt16  CInt32  CFloat32  CFloat64  )
# *LiteralMultipleNotMandatory_MULTIPLE_LITERAL_LIST: Values List separted from ',' that contains the multiple Literal Value "LiteralMultipleNotMandatory" Input.
#      (Input Description: The data type of the raster output file)
#      (Allowed Values:  Byte  Int16  UInt16  UInt32  Int32  Float32  Float64  CInt16  CInt32  CFloat32  CFloat64  )
# *OutputRaster: File Name of the "OutputRaster" Complex Data Output.
#      (Output Description: Output raster file)
    
# *OutputRaster_MimeType: OutputRaster Output Mime Type.
#      (Mime Type Supported:  image/tiff  application/x-netcdf  application/x-hdf  image/png  image/jpeg  image/gif  )
    
# *OutputMessage: This variabile will contain the value for "OutputMessage" Literal Data Output.
#      (Output Description: OutputMessage)
#      (Data Type: string)
      
# *BBOX_N: This variabile will contain the NORTH coordinate for the  "BBOX" Bounding Box Data Output.
#  BBOX_S: This variabile will contain the SOUTH coordinate for the  "BBOX" Bounding Box Data Output.
#  BBOX_E: This variabile will contain the EAST coordinate for the  "BBOX" Bounding Box Data Output.
#  BBOX_W: This variabile will contain the WEST coordinate for the  "BBOX" Bounding Box Data Output.
#      (Output Description: BBOX)
# *BBOX_EPSG: EPSG of "BBOX" Bounding Box Data Output.
#      (Supported:  EPSG:4326  )



      

#					 
#******************************************************************************************************************************


# ------------------------------  GRASS SCRIPT -------------------------------------------------------------------------------------------------------------------------------------


echo "Instance "$INSTANCE_VALUE" "$WPS_PROCESSING_NAME" --------- START" 
echo "." 

echo "Import the input in the GRASS Location" 
r.in.gdal -o input=$InputRaster output=InputRaster_$INSTANCE_VALUE 

sleep_seconds=10
total_sleep_seconds=10
LIMIT=101
sleep $sleep_seconds



while [ "$total_sleep_seconds" -lt "$LIMIT" ]
do
  echo "TOTAL: $total_sleep_seconds"	
  doc="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Percentage value=\""$total_sleep_seconds"\"/>"
  rm $STATUS_FILE
  echo $doc >> $STATUS_FILE	
  total_sleep_seconds=`expr $total_sleep_seconds + 10`  
  sleep $sleep_seconds
done 



echo "Set Region" 
g.region rast=InputRaster_$INSTANCE_VALUE 

echo "OutputRaster_MimeType: "$OutputRaster_MimeType

case "$OutputRaster_MimeType" in
 "image/tiff" ) export OutputRaster_Format=GTiff;
  ;;						 
 "application/x-netcdf" ) export OutputRaster_Format=netCDF;
 ;; 
 "application/x-hdf" ) export OutputRaster_Format=HDF4;
 ;; 
 "image/png" ) export OutputRaster_Format=PNG;
 ;; 
 "image/jpeg" ) export OutputRaster_Format=JPEG;
 ;; 
 "image/gif" ) export OutputRaster_Format=GIF;
 ;; 
esac

 #export OutputRaster_Format=GTiff

echo "Export Converted ("$OutputRaster_Format") Raster file: "$OUTPUT_REPOSITORY 
r.out.gdal input=InputRaster_$INSTANCE_VALUE@PERMANENT format=$OutputRaster_Format type=$OutputDataType output=$OUTPUT_REPOSITORY$OutputRaster 
r.out.gdal input=InputRaster_$INSTANCE_VALUE@PERMANENT format=$OutputRaster_Format type=$OutputDataType output=$OUTPUT_REPOSITORY$WCSLayer

BBOX_N=90
BBOX_S=-90
BBOX_E=180
BBOX_W=-180
BBOX_EPSG=4326
OutputMessage="Test Dummy WPS output"

echo "------------------------------------------------------------------------------"
echo "				    WPS Service Varibles"
echo "------------------------------------------------------------------------------"
echo "OUTPUT_REPOSITORY (Output Repository Directory) - VALUE:"$OUTPUT_REPOSITORY  
echo "TEMP_REPOSITORY (Temporaney Data created Repository Directory) - VALUE:"$TEMP_REPOSITORY		
echo "WPS_PROCESSING_NAME (WPS Processing Name) - VALUE:"$WPS_PROCESSING_NAME
echo "INSTANCE_VALUE (Instance Operation Value) - VALUE:"$INSTANCE_VALUE
echo "GRASS_LOG_FILE (File path for GRASS text LOG) - VALUE:"$GRASS_LOG_FILE		
echo "STATUS_FILE (File path for XML Status file) - VALUE:"$STATUS_FILE
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "				    Inputs"
echo "------------------------------------------------------------------------------"
echo "InputRaster: Local Path of Complex Value \"InputRaster\" defined by reference in the Execute Request." 
echo "      (Input Description: Input raster file) - VALUE: "$InputRaster
   
echo "InputRaster_MimeType: Mime Type of \"InputRaster\" Input Complex Data. "
echo "      (Mime Type Supported:  image/tiff  application/x-netcdf3  application/x-netcdf4  application/x-hdf  ) - VALUE: "$InputRaster_MimeType:
    
echo "InputRasterMultiple_MULTIPLE_INPUT_FOLDER: (Folder that contains the multiple Complex Value \"InputRasterMultiple\" Input defined by reference in the Execute Request.)"
echo "      (Input Description: Input raster file) - VALUE: "$InputRasterMultiple_MULTIPLE_INPUT_FOLDER
   
echo "InputRasterMultiple_MULTIPLE_INPUT_MimeType: Mime Types List separted from ',' for the multiple \"InputRasterMultiple\" Complex Data. "
echo "      (Each value correspond at the Mime Type of the file, called like the list position of the Mime Type value, in the \"InputRasterMultiple_MULTIPLE_INPUT_FOLDER\" folder)"
echo "      (Supported:  application/x-netcdf4  ) - VALUE: "$InputRasterMultiple_MULTIPLE_INPUT_MimeType
    
echo "InputRasterNotMandatory_MULTIPLE_INPUT_FOLDER: (Folder that contains the multiple Complex Value \"InputRasterNotMandatory\" Input defined by reference in the Execute Request.)"
echo "      (Input Description: Input raster file)- VALUE: "$InputRasterNotMandatory_MULTIPLE_INPUT_FOLDER
   
echo "InputRasterNotMandatory_MULTIPLE_INPUT_MimeType: Mime Types List separted from ',' for the multiple \"InputRasterNotMandatory\" Complex Data." 
echo "      (Each value correspond at the Mime Type of the file, called like the list position of the Mime Type value, in the \"InputRasterNotMandatory_MULTIPLE_INPUT_FOLDER\" folder)"
echo "      (Supported:  application/x-netcdf4  ) - VALUE: "$InputRasterNotMandatory_MULTIPLE_INPUT_MimeType
    
echo "BoundingBox_LOWER_CORNER: Lower Corner Coordinate of \"BoundingBox\" Input Bounding Box Data. - VALUE: "$BoundingBox_LOWER_CORNER
echo "BoundingBox_UPPER_CORNER: Upper Corner Coordinate of \"BoundingBox\" Input Bounding Box Data. - VALUE: "$BoundingBox_UPPER_CORNER
echo "      (Input Description: The bounding box to clip the raster to)"
echo "BoundingBox_EPSG: EPSG of \"BoundingBox\" Input Bounding Box Data."
echo "      (EPSG Supported:  ) - VALUE: "$BoundingBox_EPSG
        
echo "BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_LOWER_CORNER: Lower Corner Coordinate List separted from '*' for the \"BoundingBoxMultipleNotMandatory\" multiple Input Bounding Box Data."
echo " - VALUE: "$BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_LOWER_CORNER
echo "BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_UPPER_CORNER: Upper Corner Coordinate List separted from '*' for the \"BoundingBoxMultipleNotMandatory\" multiple Input Bounding Box Data."
echo " - VALUE:" $BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_UPPER_CORNER
echo "      (Input Description: The bounding box to clip the raster to)"
echo "BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_EPSG: EPSG value list separted from ',' of  the \"BoundingBoxMultipleNotMandatory\" multiple Input Bounding Box Data."
echo "      (EPSG Supported:  ) - VALUE: "$BoundingBoxMultipleNotMandatory_MULTIPLE_BBOX_EPSG
        
echo "InputFormat: Literal Datata \"InputFormat\" Input value."
echo "      (Input Description: The format of the raster input file) "
echo "      (Allowed Values:  netCDF4  ) - VALUE: "$InputFormat
echo "OutputDataType: Literal Datata \"OutputDataType\" Input value."
echo "      (Input Description: The data type of the raster output file)"
echo "      (Allowed Values:  Byte  Int16  UInt16  UInt32  Int32  Float32  Float64  CInt16  CInt32  CFloat32  CFloat64  )- VALUE: "$OutputDataType
echo "LiteralMultipleNotMandatory_MULTIPLE_LITERAL_LIST: Values List separted from ',' that contains the multiple Literal Value \"LiteralMultipleNotMandatory\" Input."
echo "      (Input Description: The data type of the raster output file)"
echo "      (Allowed Values:  Byte  Int16  UInt16  UInt32  Int32  Float32  Float64  CInt16  CInt32  CFloat32  CFloat64  )- VALUE: "$LiteralMultipleNotMandatory_MULTIPLE_LITERAL_LIST
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "				    Outputs"
echo "------------------------------------------------------------------------------"
echo "OutputRaster: File Name of the \"OutputRaster\" Complex Data Output."
echo "      (Output Description: Output raster file)- VALUE:"$OutputRaster
    
echo "OutputRaster_MimeType: OutputRaster Output Mime Type."
echo "      (Mime Type Supported:  image/tiff  application/x-netcdf  application/x-hdf  image/png  image/jpeg  image/gif  )- VALUE:"$OutputRaster_MimeType
    
echo "OutputMessage: This variabile will contain the value for \"OutputMessage\" Literal Data Output."
echo "      (Output Description: OutputMessage)"
echo "     (Data Type: string)- VALUE: "$OutputMessage
      
echo " BBOX_N: This variabile will contain the NORTH coordinate for the  \"BBOX\" Bounding Box Data Output- VALUE: "$BBOX_N
echo "  BBOX_S: This variabile will contain the SOUTH coordinate for the  \"BBOX\" Bounding Box Data Output- VALUE: "$OBBOX_S
echo "  BBOX_E: This variabile will contain the EAST coordinate for the  \"BBOX\" Bounding Box Data Output- VALUE: "$BBOX_E
echo "  BBOX_W: This variabile will contain the WEST coordinate for the  \"BBOX\" Bounding Box Data Output- VALUE: "$BBOX_W
echo "      (Output Description: BBOX)"
echo " BBOX_EPSG: EPSG of \"BBOX\" Bounding Box Data Output."
echo "     (Supported:  EPSG:4326  ) - VALUE: "$BBOX_EPSG
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"
echo "------------------------------------------------------------------------------"


echo "." 
echo "Instance "$INSTANCE_VALUE" "$WPS_PROCESSING_NAME" --------- END" 





# ------------------------------  END GRASS SCRIPT ------------------------------------------------------------------------------------------------------------------------------ 
