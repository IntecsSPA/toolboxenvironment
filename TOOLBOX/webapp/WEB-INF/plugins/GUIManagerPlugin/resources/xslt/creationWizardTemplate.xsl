<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fun="java:it.intecs.pisa.toolbox.plugins.gui.SaxonXSLTFunctionsClass" xmlns:gml="http://www.opengis.net/gml" xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
    <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="no"/>

  <xsl:param name="interfaceType">Ordering</xsl:param>
    <xsl:template match="/">
        <inputInterface xmlns="http://gisClient.pisa.intecs.it/gisClient">
            <section name="{$interfaceType} Identification">
                <group label="{$interfaceType} Service Identification Info: ">
                    <input type="text" name="{$interfaceType}ServiceName" label="Service Name" id="{$interfaceType}ServiceName" optional="false" value="" size="52" remoteControlURL="rest/gui/creationWizard/validate/serviceName.json"/>
                    <input type="textarea" name="{$interfaceType}ServiceAbstract" label="Service Abstract" id="{$interfaceType}ServiceAbstract" optional="true" value="" cols="50" rows="5"/>
                    <input type="text" name="{$interfaceType}ServiceDescription" label="Service Description" id="{$interfaceType}ServiceDescription" optional="true" value="" size="52"/>
                </group>
            </section>
            <section name="{$interfaceType} Information">

                <group label="Interface Type">
                    <input type="text" name="interfaceType" id="interfaceType" hidden="true" hideLabel="true" value="" optional="false"/>
                    <input type="text" name="interfaceName" id="interfaceName" hidden="true" hideLabel="true" value="" optional="false"/>
                    <input type="text" name="interfaceVersion" id="interfaceVersion" hidden="true" hideLabel="true" value="" optional="false"/>
                    <input type="text" name="interfaceMode" id="interfaceMode" hidden="true" hideLabel="true" value="" optional="false"/>

                    <input type="combo" optional="false" name="{$interfaceType}ServiceInterface" id="{$interfaceType}ServiceInterface" store="VALUES" size="40"
               storeFields="['interfaceTitle', 'interfaceType', 'interfaceName', 'interfaceVersion','interfaceModeUrlRequest']"
               storeData="{fun:getInterfaces($interfaceType)}" value=""
                onChange="serviceInterfaceTypeChange"
               />
                </group>
                <group label="Interface Mode">

                    <input type="combo" optional="false" name="{$interfaceType}InterfaceMode" id="{$interfaceType}InterfaceMode" store="VALUES" size="40"
               storeFields="['interfaceMode']" value=""
               storeData="[]"
                onChange="serviceInterfaceModeChange"/>
                </group>

            </section>
            <section name="{$interfaceType} Settings">
                <group label="General Setting">
                    <input type="checkbox" name="{$interfaceType}ServiceQueueIncoming" id="{$interfaceType}ServiceQueueIncoming" label="Queue incoming service requests"/>
                </group>
                <group label="Suspend Mode">

                    <input type="radiogroup" name="{$interfaceType}SuspendMode" id="{$interfaceType}SuspendMode" labelList="SOFT,HARD" valueList="SOFT,HARD"
                valueCheked="SOFT"/>
                </group>
            </section>
            <section name="{$interfaceType} Security">
                <group label="Secure Communication">

                    <input type="text" name="{$interfaceType}SSLCertificateLocation" label="SSL certificate location" id="{$interfaceType}SSLCertificateLocation" optional="true" value="" size="52"/>
                </group>

            </section>

            <requestInformations>

                <buttons>

                    <button name="create{$interfaceType}ServiceButton" id="create{$interfaceType}ServiceButton" label="Create {$interfaceType} Service" iconImage="" onclick="createServiceRequest"/>
                </buttons>
            </requestInformations>
        </inputInterface>

    </xsl:template>
</xsl:stylesheet>