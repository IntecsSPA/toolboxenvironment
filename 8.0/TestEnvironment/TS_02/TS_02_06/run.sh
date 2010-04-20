#!/bin/bash
rm ../../InputDir/TS_02_02.xml
rm ../..//OutputDir/TS_02_02.xml

java -jar ../../Code/TestTool.jar CopyFileWhenResourceAvailable ../../InputDir/TS_02_02.xml ../..//OutputDir/TS_02_02.xml ../../Responses/Order_response_TS_02_02.xml
