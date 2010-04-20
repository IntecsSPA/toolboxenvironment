package it.intecs.pisa.toolbox.plugins.geoServerTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class ModifyCoverage extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element addCleanupMarker) throws Exception {
        boolean isDeployed=false;
        
        System.out.println("Modify Coverage command");
        
        return Boolean.valueOf(isDeployed);
    }
    
 
}