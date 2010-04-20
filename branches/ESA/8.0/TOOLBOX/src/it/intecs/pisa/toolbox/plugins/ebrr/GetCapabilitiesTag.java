/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.engine.EngineVariablesConstants;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class GetCapabilitiesTag extends TagExecutor {
    protected DOMUtil util;
    protected TBXService service;

    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
         Document capabilitiesDoc = null;
        Element capRootEl;
        IVariableStore confVar;
        ServiceManager servMan;
        String serviceName;

        util = new DOMUtil();
        servMan = ServiceManager.getInstance();

        confVar = this.engine.getVariablesStore();
        serviceName = (String) confVar.getVariable(EngineVariablesConstants.SERVICE_NAME);

        service = servMan.getService(serviceName);

        capabilitiesDoc = util.newDocument();
        capRootEl = addRootNode(capabilitiesDoc);
        addServiceIdentification(capRootEl);
        addServiceProvider(capRootEl);
        addOperationsMetadata(capRootEl);
        addFilterCapabilities(capRootEl);
       // addWSDLLink(capRootEl);

       return capabilitiesDoc;
    }

    private Element addRootNode(Document capabilitiesDoc) {
        Element node;

        node = capabilitiesDoc.createElement("wrs:Capabilities");
        node.setAttribute("xmlns:wrs", "http://www.opengis.net/cat/wrs/1.0");
        node.setAttribute("xmlns:ows", "http://www.opengis.net/ows");
        node.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        node.setAttribute("xmlns:rim", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
        node.setAttribute("version", "1.0.0");

        capabilitiesDoc.appendChild(node);

        return node;
    }

    private void addServiceIdentification(Element rootEl) {
        Document capabilitiesDoc;
        Element serviceIdentificationEl;
        Element titleEl;
        Element abstractEl;
        Element keywordsEl;
        Element keywordEl;
        Element serviceTypeEl;
        Element serviceTypeVersionEl;
        Element feesEl;
        Element accessConstraintEl;

        capabilitiesDoc = rootEl.getOwnerDocument();
        serviceIdentificationEl = capabilitiesDoc.createElement("ows:ServiceIdentification");
        rootEl.appendChild(serviceIdentificationEl);

        titleEl = capabilitiesDoc.createElement("ows:Title");
        titleEl.setTextContent("EarthObservation ebRIM Catalogue");
        serviceIdentificationEl.appendChild(titleEl);

        abstractEl = capabilitiesDoc.createElement("ows:Abstract");
        abstractEl.setTextContent("A web-based catalogue service that implements the CSW-ebRIM profile of the OGC Catalogue 2.0 specification, and the EO Extension Package");
        serviceIdentificationEl.appendChild(abstractEl);

        keywordsEl = capabilitiesDoc.createElement("ows:Keywords");
        serviceIdentificationEl.appendChild(keywordsEl);

        keywordEl = capabilitiesDoc.createElement("ows:Keyword");
        keywordEl.setTextContent("registry");
        keywordsEl.appendChild(keywordEl);

        keywordEl = capabilitiesDoc.createElement("ows:Keyword");
        keywordEl.setTextContent("catalogue");
        keywordsEl.appendChild(keywordEl);

        keywordEl = capabilitiesDoc.createElement("ows:Keyword");
        keywordEl.setTextContent("ebRIM");
        keywordsEl.appendChild(keywordEl);

        keywordEl = capabilitiesDoc.createElement("ows:Keyword");
        keywordEl.setTextContent("earth observation");
        keywordsEl.appendChild(keywordEl);

        serviceTypeEl = capabilitiesDoc.createElement("ows:ServiceType");
        serviceTypeEl.setTextContent("urn:ogc:service:catalogue:csw-ebrim");
        serviceIdentificationEl.appendChild(serviceTypeEl);

        serviceTypeVersionEl = capabilitiesDoc.createElement("ows:ServiceTypeVersion");
        serviceTypeVersionEl.setTextContent("1.0.0");
        serviceIdentificationEl.appendChild(serviceTypeVersionEl);

        feesEl = capabilitiesDoc.createElement("ows:Fees");
        feesEl.setTextContent("NONE");
        serviceIdentificationEl.appendChild(feesEl);

        accessConstraintEl = capabilitiesDoc.createElement("ows:AccessConstraints");
        accessConstraintEl.setTextContent("NONE");
        serviceIdentificationEl.appendChild(accessConstraintEl);
    }

    private void addServiceProvider(Element capRootEl) throws Exception {
        Document serviceProviderDoc;
        Document mainDoc;
        Element importedDoc;
        File docFile;

        mainDoc = capRootEl.getOwnerDocument();

        docFile = new File(service.getServiceRoot(), "AdditionalResources/EOCat/ServiceProviderInfo.xml");
        serviceProviderDoc = util.fileToDocument(docFile);

        importedDoc = (Element) mainDoc.importNode(serviceProviderDoc.getDocumentElement(), true);
        capRootEl.appendChild(importedDoc);
    }

    private void addOperationsMetadata(Element capRootEl) throws Exception {
        Document doc;
        Element opMetadataEl;
        Operation[] operations;
        Element extendedCapabilities;
        Element rimSlot;
        Element parameterEl;
        Element valueEl,valueListEl;
        String[] collections;

        doc = capRootEl.getOwnerDocument();
        opMetadataEl = doc.createElement("ows:OperationsMetadata");

        capRootEl.appendChild(opMetadataEl);


        operations = service.getImplementedInterface().getOperations();
        for (Operation op : operations) {
            addOperation(opMetadataEl, op.getName());
        }

        addOperation(opMetadataEl,"GetRepositoryItem");
        addPostBinding(opMetadataEl);

        collections=getCollections();

        if(collections!=null && collections.length>0)
        {
            extendedCapabilities = doc.createElement("ows:ExtendedCapabilities");
            extendedCapabilities.setAttribute("xmlns:wsdi", "http://www.w3.org/ns/wsdl-instance");

            String wsdlString = Toolbox.getInstance().getPublicAddress() + "/WSDL/" + service.getServiceName() + "/" + service.getServiceName() + ".wsdl";
            extendedCapabilities.setAttribute("wsdi:wsdlLocation", wsdlString);
            opMetadataEl.appendChild(extendedCapabilities);

            rimSlot = doc.createElement("rim:Slot");
            rimSlot.setAttribute("name", "urn:ogc:def:ebRIM-Slot:OGC-06-131:parentIdentifier");
            rimSlot.setAttribute("slotType", "urn:oasis:names:tc:ebxml-regrep:DataType:String");
            extendedCapabilities.appendChild(rimSlot);

            valueListEl = doc.createElement("rim:ValueList");
            rimSlot.appendChild(valueListEl);

            for(String col:collections)
            {
                 valueEl = doc.createElement("rim:Value");
                valueEl.setTextContent(col);
                valueListEl.appendChild(valueEl);
            }
        }

       /* parameterEl=doc.createElement("ows:Parameter");
        parameterEl.setAttribute("name", "service");
        capRootEl.appendChild(parameterEl);

        valueEl = doc.createElement("ows:Value");
        valueEl.setTextContent("urn:x-ogc:specification:csw-ebrim:Service:OGC-CSW:ebRIM");
        parameterEl.appendChild(valueEl);

        parameterEl=doc.createElement("ows:Parameter");
        parameterEl.setAttribute("name", "version");
        capRootEl.appendChild(parameterEl);

        valueEl = doc.createElement("ows:Value");
        valueEl.setTextContent("2.0.2");
        parameterEl.appendChild(valueEl);*/
    }

    private void addOperation(Element rootEl, String opName) throws Exception {
        Element opEl;
        Element dcpEl;
        Element httpEl;
        Element postEl;
        Document doc;

        doc = rootEl.getOwnerDocument();

        opEl = doc.createElement("ows:Operation");
        opEl.setAttribute("name", opName);
        rootEl.appendChild(opEl);

        addParameterForOperation(opEl, opName);
    }

    private void addFilterCapabilities(Element capRootEl) throws Exception {
        Document serviceProviderDoc;
        Document mainDoc;
        Element importedDoc;
        File docFile;

        mainDoc = capRootEl.getOwnerDocument();

        docFile = new File(service.getServiceRoot(), "AdditionalResources/EOCat/FilterCapabilities.xml");
        serviceProviderDoc = util.fileToDocument(docFile);

        importedDoc = (Element) mainDoc.importNode(serviceProviderDoc.getDocumentElement(), true);
        capRootEl.appendChild(importedDoc);
    }

    private void addParameterForOperation(Element opEl, String opName) throws Exception {
        if (opName.equals("GetCapabilities")) {
            addParametersForGetCapabilities(opEl);
        } else if (opName.equals("GetRecords")) {
            addParametersForGetRecords(opEl);
        } else if (opName.equals("GetRecordById")) {
            addParametersForGetRecordById(opEl);
        } else if (opName.equals("DescribeRecord")) {
            addParametersForDescribeRecord(opEl);
        } else if (opName.equals("Harvest")) {
            addParametersForHarvest(opEl);
        }else if (opName.equals("GetRepositoryItem")) {
           addParametersForGetRepositoryItem(opEl);
        }
    }

    private void addParametersForGetCapabilities(Element opEl) throws Exception{
        //addValueList(opEl, "sections", new String[]{"ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Filter_Capabilities", "ServiceProperties"});
        Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Post");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);

        addValueList(opEl, "AcceptVersions", new String[]{"1.0.0"});
        addValueList(opEl, "service", new String[]{"CSW", "CSW-ebRIM"});
        addValueList(opEl, "version", new String[]{"2.0.2"});
        addValueList(opEl, "AcceptFormats", new String[]{"application/xml"});
    }

    protected void addValueList(Element opEl, String name, String[] valueList) {
        Element parameter;
        Element valueEl;
        Document doc;

        doc = opEl.getOwnerDocument();
        parameter = doc.createElement("ows:Parameter");
        parameter.setAttribute("name", name);
        opEl.appendChild(parameter);

        for (String value : valueList) {
            valueEl = doc.createElement("ows:Value");
            valueEl.setTextContent(value);
            parameter.appendChild(valueEl);
        }
    }

    private void addParametersForGetRecords(Element opEl) throws Exception{
         Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Post");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);

        addValueList(opEl, "resultType", new String[]{"hits", "results","validate"});
        addValueList(opEl, "CONSTRAINTLANGUAGE", new String[]{"FILTER"});
        addValueList(opEl, "DistributedSearch", new String[]{"FALSE"});
        addValueList(opEl, "outputFormat", new String[]{"application/xml"});
        addValueList(opEl, "outputSchema", new String[]{"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"});
        addValueList(opEl, "TypeNames", new String[]{"rim:ExtrinsicObject", "rim:Association", "rim:Classification", "rim:ClassificationNode", "rim:ClassificationScheme", "rim:AdhocQuery", "rim:ExternalIdentifier", "rim:RegistryPackage"});
        //addValueList(opEl, "ElementSetName", new String[]{"brief", "summary", "full"});

    }

    private void addParametersForGetRecordById(Element opEl) throws Exception{
         Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Post");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);

        addValueList(opEl, "outputFormat", new String[]{"application/xml", "text/xml"});
        addValueList(opEl, "outputSchema", new String[]{"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"});
        addValueList(opEl, "ElementSetName", new String[]{"brief", "summary", "full"});
        addValueList(opEl, "TypeNames", new String[]{"rim:ExtrinsicObject", "rim:Association", "rim:Classification", "rim:ClassificationNode", "rim:ClassificationScheme", "rim:AdhocQuery", "rim:ExternalIdentifier", "rim:RegistryPackage"});


    }

    private void addParametersForDescribeRecord(Element opEl) throws Exception{
         Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Post");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);

        addValueList(opEl, "TypeNames", new String[]{"rim:RegistryObject"});
        addValueList(opEl, "outputFormat", new String[]{"application/xml", "text/xml"});
        addValueList(opEl, "schemaLanguage", new String[]{"XMLSCHEMA"});

    }

    private void addParametersForGetRepositoryItem(Element opEl) throws Exception{
         Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Get");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);
    }

    private void addParametersForHarvest(Element opEl) throws Exception {
         Element postEl,dcpEl,httpEl;
        Document doc;

        doc=opEl.getOwnerDocument();

        dcpEl = doc.createElement("ows:DCP");
        opEl.appendChild(dcpEl);

        httpEl = doc.createElement("ows:HTTP");
        dcpEl.appendChild(httpEl);
        postEl = doc.createElement("ows:Post");

        postEl.setAttribute("xlink:href", Toolbox.getInstance().getPublicAddress() + "/services/" + service.getServiceName());
        httpEl.appendChild(postEl);

        addValueList(opEl, "resourceType", new String[]{"eop:EarthObservation","sar:EarthObservation","atm:EarthObservation","opt:EarthObservation","rim:RegistryObjectList"});
        addValueList(opEl, "service", new String[]{"urn:x-ogc:specification:csw-ebrim:Service:OGC-CSW:ebRIM"});
        addValueList(opEl, "version", new String[]{"2.0.2"});
    }

    protected String[] getCollections() throws Exception
    {
        String[] collections;
        String jdbcConnectString;
        String dbUserName,dbPassword;
        Statement stm=null;
        Connection conn=null;
        ResultSet rs=null;
        Vector<String> collectionsVector;

        try
        {
            IVariableStore varStore = engine.getVariablesStore();

            Class.forName("org.postgresql.Driver");
            jdbcConnectString="jdbc:postgresql://"+(String) varStore.getVariable("ebRRDbUrl")+"/"+(String) varStore.getVariable("ebRRDbName");
            dbUserName=(String) varStore.getVariable("ebRRDbUser");
            dbPassword=(String) varStore.getVariable("ebRRDbPwd");

            conn= DriverManager.getConnection(jdbcConnectString, dbUserName, dbPassword);
            stm=conn.createStatement();

            rs=stm.executeQuery("select distinct stringvalue from t_slot where name_='urn:ogc:def:ebRIM-Slot:OGC-06-131:parentIdentifier' and stringvalue<>''");

            collectionsVector=new Vector<String>();
            while(rs.next())
            {
                collectionsVector.add(rs.getString("stringvalue"));
            }

            collections=new String[collectionsVector.size()];
            for(int i=0;i<collectionsVector.size();i++)
            {
                collections[i]=collectionsVector.get(i);
            }

            return collections;
        }
        catch(Exception e)
        {
            return new String[0];
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();

            if(conn!=null)
                conn.close();
        }
    }

    
    private void addPostBinding(Element opMetadataEl) {
        Element parameter;
        Element valueEl;
        Document doc;

        doc = opMetadataEl.getOwnerDocument();
        parameter = doc.createElement("ows:Constraint");
        parameter.setAttribute("name", "PostEncoding");
        opMetadataEl.appendChild(parameter);

        valueEl = doc.createElement("ows:Value");
        valueEl.setTextContent("SOAP");
        parameter.appendChild(valueEl);

    }

}