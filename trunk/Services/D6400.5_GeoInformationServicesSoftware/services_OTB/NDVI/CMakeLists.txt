PROJECT(ndvi)

cmake_minimum_required(VERSION 2.8)

FIND_PACKAGE(OTB)
IF(OTB_FOUND)
  INCLUDE(${OTB_USE_FILE})
ELSE(OTB_FOUND)
  MESSAGE(FATAL_ERROR
      "Cannot build OTB project without OTB. Please set OTB_DIR.")
ENDIF(OTB_FOUND)


ADD_EXECUTABLE(ndvi ndvi.cxx )

TARGET_LINK_LIBRARIES(ndvi OTBCommon OTBIO ITKCommon ITKIO)
