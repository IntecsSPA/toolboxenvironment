package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.engine.EngineVariablesConstants;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.*;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;

public class ServiceKeyStorePwdTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element zipElement) throws Exception {
       String serviceName;
        TBXService service;
        ServiceManager servMan;
        
        IVariableStore varStore = this.engine.getConfigurationVariablesStore();
        serviceName=(String) varStore.getVariable(EngineVariablesConstants.SERVICE_NAME);

        servMan=ServiceManager.getInstance();
        service=servMan.getService(serviceName);

        return ToolboxSecurityConfigurator.getJKSpassword(service);
    }
    
 
}
