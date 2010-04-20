package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;

public class UpdateFtpPermissionTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element expression) throws Exception {
         String username;
        String permission;
        IVariableStore confStore;
       FTPServerManager  ftpServerManager;
       boolean retValue=false;
       
        confStore=this.engine.getConfigurationVariablesStore();
        ftpServerManager=(FTPServerManager) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_FTP_SERVER_MANAGER);
        
        username = this.engine.evaluateString(expression.getAttribute("user"),IEngine.EngineStringType.ATTRIBUTE);
        permission = this.engine.evaluateString(expression.getAttribute("permission"),IEngine.EngineStringType.ATTRIBUTE);

        retValue=ftpServerManager.setWritePermission(username, Boolean.parseBoolean(permission));

        return new Boolean(retValue);
    }
}