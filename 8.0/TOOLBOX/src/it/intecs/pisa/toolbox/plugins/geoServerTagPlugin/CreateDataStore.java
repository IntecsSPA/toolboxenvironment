package it.intecs.pisa.toolbox.plugins.geoServerTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class CreateDataStore extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element addCleanupMarker) throws Exception {
        System.out.println("Executing CreateDataStore tag from GeoServer Tag  Plugin");
       System.out.println("Configured parameter value: "+ plugIn.getConfigurationParameter("testParameter"));
        return null;
    }
    
 
}
