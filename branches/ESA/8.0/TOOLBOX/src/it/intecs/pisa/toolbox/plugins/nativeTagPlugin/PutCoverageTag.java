package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.geosolutions.utils.io.geoserver.GeoServerRESTService;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;

public class PutCoverageTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element putCoverage) throws Exception {
        String geoServerUrl;
        String filePath;
        String username;
        String password;
       //String layerName;
        LinkedList children;
        Iterator iterator;
        
        children=DOMUtil.getChildren(putCoverage);
        iterator=children.iterator();
        
        //layerName=(String)this.engine.evaluateString(putCoverage.getAttribute("layerName"), IEngine.EngineStringType.ATTRIBUTE);
        geoServerUrl=(String)this.executeChildTag((Element) iterator.next());
        filePath=(String)this.executeChildTag((Element) iterator.next());
        username=(String)this.executeChildTag((Element) iterator.next());
        password=(String)this.executeChildTag((Element) iterator.next());
        
     //   boolean sent = GeoServerRESTService.putContent(new URL(geoServerUrl),new File(filePath).toURL().toExternalForm(),username,password);
       
      /*  geoserverREST_URL = new URL(geoserverBaseURL + "/rest/folders/" + coverageStoreId
                    + "/layers/" + storeFilePrefix + "/file.tiff?" + getQueryString(queryParams));*/
        
         boolean   sent = GeoServerRESTService.putBinaryFileTo(new URL(geoServerUrl),new File(filePath),username,password); 
        return new Boolean(sent);
    }
    
 
}
