package it.intecs.pisa.gis.geoserver.util;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */



public class GISGeoserverDOM {

    private static String LAYER_ELEMENT="layer";
    private static String DEFAULT_STYLE_ELEMENT="defaultStyle";
    private static String NAME_ELEMENT="name";
    private static String WORKSPACE_ELEMENT="workspace";

    public static Document createSetDefaultStyleDocument(String styleName){
     DOMUtil domUtil=new DOMUtil();
     Document setDefStyleDoc=domUtil.newDocument();
     Element layer=setDefStyleDoc.createElement(LAYER_ELEMENT);
     Element defaultStyle=setDefStyleDoc.createElement(DEFAULT_STYLE_ELEMENT);
     Element name=setDefStyleDoc.createElement(NAME_ELEMENT);
     name.setTextContent(styleName);
     defaultStyle.appendChild(name);
     layer.appendChild(defaultStyle);
     setDefStyleDoc.appendChild(layer);
     return setDefStyleDoc;
    }


    public static Document createCreateWorkspaceDocument(String workspaceName){
     DOMUtil domUtil=new DOMUtil();
     Document createWorkspaceDoc=domUtil.newDocument();
     Element workspace=createWorkspaceDoc.createElement(WORKSPACE_ELEMENT);
     Element name=createWorkspaceDoc.createElement(NAME_ELEMENT);
     name.setTextContent(workspaceName);
     workspace.appendChild(name);
     createWorkspaceDoc.appendChild(workspace);
     return createWorkspaceDoc;
    }


}
