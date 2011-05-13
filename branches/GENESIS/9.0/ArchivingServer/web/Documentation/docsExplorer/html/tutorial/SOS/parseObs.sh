#!/bin/bash

# The following inputs are passed to the script
#   $1 -> input file to  be processed
#   $2 output directory (SOS watch - this has been set 
#      in the interface)

# The insertObsTemplate is the request template. the ##XXXXX## # values are replaced later with the data extracted from the 
# csv file

insertObsTemplate="<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<InsertObservation xmlns=\"http://www.opengis.net/sos/1.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" 
 xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sos=\"http://www.opengis.net/sos/1.0\"
 xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:gml=\"http://www.opengis.net/gml\" 
 xmlns:swe=\"http://www.opengis.net/swe/1.0.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" 
 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" 
 service=\"SOS\" version=\"1.0.0\">
	<AssignedSensorId>urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-1</AssignedSensorId>
	<om:Measurement>
		<om:samplingTime>
			<gml:TimeInstant>
				<gml:timePosition>##TIME##</gml:timePosition>
			</gml:TimeInstant>
		</om:samplingTime>
		<om:procedure xlink:href=\"urn:ogc:object:feature:Sensor:IFGI:ifgi-sensor-1\"/>
		<om:observedProperty xlink:href=\"##OBSERVED_PROPERTY_NAME##\"/>
		<om:featureOfInterest>
         <sa:SamplingPoint gml:id=\"##FEATURE_ID##\">
	    <gml:name>##FEATURE_ID##</gml:name>
            <sa:sampledFeature xlink:href=\"\"/>
                <sa:position>
                  <gml:Point>
                    <gml:pos srsName=\"urn:ogc:def:crs:EPSG:4326\">##LAT## ##LON##</gml:pos>
		  </gml:Point>
                </sa:position>
              </sa:SamplingPoint>
		</om:featureOfInterest>
		<om:result uom=\"##UOM##\">##VALUE##</om:result>
	</om:Measurement>
</InsertObservation>"


# We generate a new SOS file for each variable. In the axample
# we have only one variable(varNumber=1) thus only the first
# property will be used. If you have more than one variable
# add the appripriate property value and the uom in the
# corresponding arrays below. Then increment the varNumber 
# accordingly

propertyNameArray=( urn:ogc:def:phenomenon:OGC:1.0.30:waterlevel urn:ogc:def:phenomenon:OGC:1.0.30:myvariable )
uomArray=( cm cm )

# We loop on the lines
while read line
do
  arrList=$(echo $line | tr "," "\n")
varIndex=0
  for x in $arrList
    do
      arr[$varIndex]=$x
      varIndex=`expr $varIndex + 1`  
    done

varIndex=0
varNumber=1
insertObsRequest=""

echo 

# We loop on the variables (in this example we have only 
# one variable)

while [ "$varIndex" -lt "$varNumber" ]
 do    
  featureId=$(date +%s%N) 
  dateTime=$(date -d '1970-01-01 UTC '${arr[1]}' seconds' +"%Y-%m-%dT%T%z")

# We replace the placeholders with the data extracted from 
# the file
  insertObsRequest=${insertObsTemplate//##OBSERVED_PROPERTY_NAME##/${propertyNameArray[$varIndex]}}
  insertObsRequest=${insertObsRequest//##TIME##/$dateTime}
  insertObsRequest=${insertObsRequest//##LAT##/${arr[2]}}
  insertObsRequest=${insertObsRequest//##LON##/${arr[3]}}
  insertObsRequest=${insertObsRequest//##FEATURE_ID##/$featureId}
  insertObsRequest=${insertObsRequest//##UOM##/${uomArray[$varIndex]}}
  insertObsRequest=${insertObsRequest//##VALUE##/${arr[$varIndex]}}

# We store the file in the SOS directory ($2) using the 
# featureId as name

  varIndex=`expr $varIndex + 1` 
  echo $insertObsRequest > $2/$featureId".xml"

done  


done < $1


