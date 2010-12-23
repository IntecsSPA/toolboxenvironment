

package it.intecs.pisa.gis.ogc.format;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andrea Marongiu
 */
public class WMCVersion1 implements WMC{

    final static short WMS_SERVICE=0;

    private String NAMESPACE_PREFIX="xmlns:";
    private String WMC_NAMESPACE="http://www.opengis.net/context";
    private String WMC_PREFIX="wmc";
    private String SLD_NAMESPACE="http://www.opengis.net/sld";
    private String SLD_PREFIX="sld";
    private String XLINK_NAMESPACE="http://www.w3.org/1999/xlink";
    private String XLINK_PREFIX="xlink";
    private String XSI_NAMESPACE="http://www.w3.org/2001/XMLSchema-instance";
    private String XSI_PREFIX="xsi";

    private String EPSG_PREFIX="EPSG:";

    private String WMC_ROOT="ViewContext";
    private String WMC_SCHEAMALOCATION_ATTRIBUTE="xsi:schemaLocation";


    private String GENERAL_ELEMENT="General";
    
    private String WINDOW_ELEMENT="Window";
    private String WINDOW_HEIGHT_ATTRIBUTE="height";
    private String WINDOW_WIDTH_ATTRIBUTE="width";

    private String BOUNDING_BOX_ELEMENT="BoundingBox";
    private String BOUNDING_BOX_SRS_ATTRIBUTE="SRS";
    private String BOUNDING_BOX_EAST_ATTRIBUTE="maxx";
    private String BOUNDING_BOX_NORTH_ATTRIBUTE="maxy";
    private String BOUNDING_BOX_WEST_ATTRIBUTE="minx";
    private String BOUNDING_BOX_SOUTH_ATTRIBUTE="miny";

    private String NAME_ELEMENT="Name";
    private String TITLE_ELEMENT="Title";
    private String ABSTRACT_ELEMENT="Abstract";
    private String SRS_ELEMENT="SRS";

    private String LAYER_LIST_ELEMENT="LayerList";
    private String LAYER_ELEMENT="Layer";
    private String LAYER_HIDDEN_ATTRIBUTE="hidden";
    private String LAYER_QUERYABLE_ATTRIBUTE="queryable";

    private String SERVER_ELEMENT="Server";
    private String SERVER_SERVICE_ATTRIBUTE="service";
    private String SERVER_TITLE_ATTRIBUTE="title";
    private String SERVER_VERSION_ATTRIBUTE="version";

    private String ONLINE_RESOURCE_ELEMENT="OnlineResource";
    private String ONLINE_RESOURCE_HREF_ATTRIBUTE="href";
    private String ONLINE_RESOURCE_TYPE_ATTRIBUTE="type";

    private String FORMAT_LIST_ELEMENT="FormatList";
    private String FORMAT_ELEMENT="Format";
    private String FORMAT_CURRENT_ATTRIBUTE="current";
    private String STYLE_LIST_ELEMENT="StyleList";
    private String STYLE_ELEMENT="Style";
    private String STYLE_CURRENT_ATTRIBUTE="current";
    private String EXTENSION_ELEMENT="Extension";

    
    private String schemaLocation="";
    private String version="";
    // ol: "http://openlayers.org/context",

    private DOMUtil domUtil=new DOMUtil();

    private Document wmcDocument=null;

    public void newWMC(){
        wmcDocument= domUtil.newDocument();

        Element root=wmcDocument.createElement(WMC_PREFIX+":"+WMC_ROOT);
        root.setAttribute(NAMESPACE_PREFIX+WMC_PREFIX, WMC_NAMESPACE);
        root.setAttribute(NAMESPACE_PREFIX+SLD_PREFIX, SLD_NAMESPACE);
        root.setAttribute(NAMESPACE_PREFIX+XLINK_PREFIX, XLINK_NAMESPACE);
        root.setAttribute(NAMESPACE_PREFIX+XSI_PREFIX, XSI_NAMESPACE);
        root.setAttribute(NAMESPACE_PREFIX+XSI_PREFIX, XSI_NAMESPACE);

        root.setAttribute(WMC_SCHEAMALOCATION_ATTRIBUTE, schemaLocation);

        wmcDocument.appendChild(root);

    }

    public void addWindowElement(int height, int width){
        NodeList nl=wmcDocument.getElementsByTagName(WMC_PREFIX+":"+GENERAL_ELEMENT);
        Element generalElement=null;
        if(nl.getLength() > 0){
           generalElement=(Element)nl.item(0);
        }else
           generalElement=createGeneralElement();

        Element windowElement=wmcDocument.createElement(WMC_PREFIX+":"+WINDOW_ELEMENT);
        windowElement.setAttribute(WINDOW_WIDTH_ATTRIBUTE,
                                               new Integer(width).toString());
        windowElement.setAttribute(WINDOW_HEIGHT_ATTRIBUTE,
                                               new Integer(height).toString());
       generalElement.appendChild(windowElement);

       if(nl.getLength() == 0)
          wmcDocument.appendChild(generalElement);
    }


    public void addContextBoundingBox(int srs,
            float west,
            float south,
            float east,
            float north){
        NodeList nl=wmcDocument.getElementsByTagName(WMC_PREFIX+":"+GENERAL_ELEMENT);
        Element generalElement=null;
        if(nl.getLength() > 0){
           generalElement=(Element)nl.item(0);
        }else
           generalElement=createGeneralElement();

        Element boundingBoxElement=wmcDocument.createElement(WMC_PREFIX+":"+BOUNDING_BOX_ELEMENT);
        boundingBoxElement.setAttribute(BOUNDING_BOX_SRS_ATTRIBUTE,EPSG_PREFIX+srs);
        boundingBoxElement.setAttribute(BOUNDING_BOX_EAST_ATTRIBUTE,
                                                new Float(east).toString());
        boundingBoxElement.setAttribute(BOUNDING_BOX_SOUTH_ATTRIBUTE,
                                                new Float(south).toString());
        boundingBoxElement.setAttribute(BOUNDING_BOX_WEST_ATTRIBUTE,
                                                new Float(west).toString());
        boundingBoxElement.setAttribute(BOUNDING_BOX_NORTH_ATTRIBUTE,
                                                new Float(north).toString());
                                              
       generalElement.appendChild(boundingBoxElement);

       if(nl.getLength() == 0)
          wmcDocument.appendChild(generalElement);
    }


    public void addContextTitle(String title){
        NodeList nl=wmcDocument.getElementsByTagName(WMC_PREFIX+":"+GENERAL_ELEMENT);
        Element generalElement=null;
        if(nl.getLength() > 0){
           generalElement=(Element)nl.item(0);
        }else
           generalElement=createGeneralElement();

       Element titleElement=createTitleElement(title);

       generalElement.appendChild(titleElement);

       if(nl.getLength() == 0)
          wmcDocument.appendChild(generalElement);
    }


    public void addContextAbstract(String abstractText){
        NodeList nl=wmcDocument.getElementsByTagName(WMC_PREFIX+":"+GENERAL_ELEMENT);
        Element generalElement=null;
        if(nl.getLength() > 0){
           generalElement=(Element)nl.item(0);
        }else
           generalElement=createGeneralElement();

       Element abstractElement=createAbstractElement(abstractText);
      

       generalElement.appendChild(abstractElement);

       if(nl.getLength() == 0)
          wmcDocument.appendChild(generalElement);
    }


    public void addLayer(String layerName,
            String layerTitle,
            int srs,
            boolean hidden,
            boolean queryable,
            Element server,
            Element formatList,
            Element styleList,
            Element extension){
        NodeList nl=wmcDocument.getElementsByTagName(WMC_PREFIX+":"+LAYER_LIST_ELEMENT);
        Element layerListElement=null;
        if(nl.getLength() > 0){
           layerListElement=(Element)nl.item(0);
        }else
           layerListElement=createLayerListElement();

        Element layerElement=wmcDocument.createElement(
                                                WMC_PREFIX+":"+LAYER_ELEMENT);

        layerElement.setAttribute(LAYER_HIDDEN_ATTRIBUTE, new Boolean(hidden).toString());
        layerElement.setAttribute(LAYER_QUERYABLE_ATTRIBUTE, new Boolean(queryable).toString());
        layerElement.appendChild(server);
        layerElement.appendChild(createNameElement(layerName));
        layerElement.appendChild(createTitleElement(layerTitle));
        layerElement.appendChild(createSRSElement(srs));
        if(formatList!=null)
           layerElement.appendChild(formatList);
        if(styleList!=null)
           layerElement.appendChild(styleList);
        if(extension!=null)
           layerElement.appendChild(extension);
        
        layerListElement.appendChild(layerElement);
       if(nl.getLength() == 0)
          wmcDocument.appendChild(layerListElement);
    }


    public Element newOWSServer(String serverURL,
            short service,
            String version) throws Exception{
        Element owsServer=wmcDocument.createElement(WMC_PREFIX+":"+SERVER_ELEMENT);
        String serviceAtt=null,titleAtt=null,versionAtt=null;

        switch (service){
            case WMS_SERVICE:
                 serviceAtt=WMS_SERVICE_NAME;
                 titleAtt=WMS_SERVICE_TITLE;
                 versionAtt=(version == null ? WMS_SERVICE_DEFAULT_VERSION: version);
                break;
           case WFS_SERVICE:
                 serviceAtt=WFS_SERVICE_NAME;
                 titleAtt=WFS_SERVICE_TITLE;
                 versionAtt=(version == null ? WFS_SERVICE_DEFAULT_VERSION: version);
                break;
           case WCS_SERVICE:
                 serviceAtt=WCS_SERVICE_NAME;
                 titleAtt=WCS_SERVICE_TITLE;
                 versionAtt=(version == null ? WCS_SERVICE_DEFAULT_VERSION: version);
                break;
           default: 
               throw new Exception("Crete Service Type Not Supported",
                       new Throwable());
        }
          owsServer.setAttribute(SERVER_SERVICE_ATTRIBUTE, serviceAtt);
          owsServer.setAttribute(SERVER_TITLE_ATTRIBUTE, titleAtt);
          owsServer.setAttribute(SERVER_VERSION_ATTRIBUTE, versionAtt);

          Element onlineResource=createOnlineResourceElement(serverURL,"simple");
          owsServer.appendChild(onlineResource);
        return owsServer;
    }

    
    public Element newFormatList(String [] formatList,
            int indexDefaultFormat){
       Element formatListElement=wmcDocument.createElement(WMC_PREFIX+":"+FORMAT_LIST_ELEMENT);
       Element tmpElement=null;
       for(int i=0; i<formatList.length; i++){
          tmpElement=createFormatElement(formatList[i],i==indexDefaultFormat);
          formatListElement.appendChild(tmpElement);
       }
       return formatListElement;
    }

    public Element newStyleList(Object [] styleList,
            int indexDefaultFormat){
       Element styleListElement=wmcDocument.createElement(WMC_PREFIX+":"+STYLE_LIST_ELEMENT);
       Element tmpElement=null;
       for(int i=0; i<styleList.length; i++){

          tmpElement=createStyleElement(styleList[i],i==indexDefaultFormat);
          styleListElement.appendChild(tmpElement);
       }
       return styleListElement;
    }

    public Element newExtension(Node [] extension){
       Element extensionElement=wmcDocument.createElement(WMC_PREFIX+":"+EXTENSION_ELEMENT);
       Node tmpNode=null;
       for(int i=0; i<extension.length; i++){
          tmpNode=wmcDocument.adoptNode(extension[i]);
          extensionElement.appendChild(tmpNode);
       }
       return extensionElement;
    }


    public Document getWMCDocument() {
        return wmcDocument;
    }

    public String getWMCVersion() {
        return version;
    }

    public Element getLayerElement(String layerName){
       Element layer=null;
       String name=null;
       NodeList layers=wmcDocument.getElementsByTagName(LAYER_ELEMENT);
       for(int i=0; i<layers.getLength(); i++){
           name=((Element)layers.item(i)).getElementsByTagName(NAME_ELEMENT).item(0).getTextContent();
           if(name.equals(layerName)){
             layer=(Element)layers.item(i);
             break;
           }
       }   
       return layer;
    }




   private Element createGeneralElement(){
       return wmcDocument.createElement(WMC_PREFIX+":"+GENERAL_ELEMENT);
    }

   private Element createLayerListElement(){
       return wmcDocument.createElement(WMC_PREFIX+":"+LAYER_LIST_ELEMENT);
    }
    
   private Element createOnlineResourceElement(String resourceURL,
           String resourceType){
     Element onlineRes= wmcDocument.createElement(WMC_PREFIX+":"+ONLINE_RESOURCE_ELEMENT);
     onlineRes.setAttribute(XLINK_PREFIX+":"+ONLINE_RESOURCE_HREF_ATTRIBUTE, resourceURL);
     onlineRes.setAttribute(XLINK_PREFIX+":"+ONLINE_RESOURCE_TYPE_ATTRIBUTE, resourceType);

     return onlineRes;
    }

   private Element createFormatElement(String format,
           boolean current){
    Element formatElement= wmcDocument.createElement(
                                                WMC_PREFIX+":"+FORMAT_ELEMENT);
    if(current)
       formatElement.setAttribute(FORMAT_CURRENT_ATTRIBUTE, "1");
    formatElement.setTextContent(format);
    return formatElement;
   }

   private Element createNameElement (String name){
    Element nameElement=wmcDocument.createElement(WMC_PREFIX+":"+NAME_ELEMENT);
    nameElement.setTextContent(name);
    return nameElement;
   }


   private Element createTitleElement (String title){
    Element titleElement=wmcDocument.createElement(WMC_PREFIX+":"+TITLE_ELEMENT);
    titleElement.setTextContent(title);
    return titleElement;
   }

   private Element createAbstractElement (String abstractText){
    Element abstractElement=wmcDocument.createElement(WMC_PREFIX+":"+ABSTRACT_ELEMENT);
    abstractElement.setTextContent(abstractText);
    return abstractElement;
   }

   private Element createSRSElement (int  srs){
    Element srsElement=wmcDocument.createElement(WMC_PREFIX+":"+SRS_ELEMENT);
    srsElement.setTextContent(EPSG_PREFIX+srs);
    return srsElement;
   }

   private Element createStyleElement(Object style,
           boolean current){
    Element styleElement= wmcDocument.createElement(
                                                WMC_PREFIX+":"+STYLE_ELEMENT);

    if(current)
       styleElement.setAttribute(STYLE_CURRENT_ATTRIBUTE, "1");
    if(style instanceof String){
        Element nameStyle=createNameElement((String)style);
        styleElement.appendChild(nameStyle);
     }else
       if(style instanceof SLD){
          Node sld=null;
          Element sldStyle=((SLD) style).getDocument().getDocumentElement();
          sld=wmcDocument.adoptNode(sldStyle);
          styleElement.appendChild(sld);
       }
    
    return styleElement;
   }

    
}
