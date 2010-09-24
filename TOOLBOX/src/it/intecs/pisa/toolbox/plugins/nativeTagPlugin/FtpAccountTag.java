package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;
import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.timers.TimerManager;
import it.intecs.pisa.toolbox.util.ScriptUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class FtpAccountTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpAccount) throws Exception {
        FTPServerManager ftpServerManager;
        TimerManager timerManager;
        IVariableStore configVarStore;
        IVariableStore varStore;
        Logger logger;
        Element rootDirTag;
        Element usernameTag;
        Element passwordTag;
        Document doc;

        configVarStore = this.engine.getConfigurationVariablesStore();
        
        logger = (Logger) configVarStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_LOGGER);

        varStore = this.engine.getVariablesStore();

        doc = this.offlineDbgTag.getOwnerDocument();

        usernameTag = doc.createElement("user");
        passwordTag = doc.createElement("password");
        rootDirTag = doc.createElement("rootDir");
        this.offlineDbgTag.appendChild(usernameTag);
        this.offlineDbgTag.appendChild(passwordTag);
        this.offlineDbgTag.appendChild(rootDirTag);

        Iterator children = DOMUtil.getChildren(ftpAccount).iterator();

        String user = (String) this.executeChildTag(DOMUtil.getFirstChild((Element) children.next()), usernameTag);

        boolean writePermission = ((ftpAccount.getAttribute(WRITE_PERMISSION) != null &&
                ftpAccount.getAttribute(WRITE_PERMISSION).
                equals(TRUE)) ? true : false);

        ftpServerManager = FTPServerManager.getInstance();
        ftpServerManager.addUser(
                user,
                (String) this.executeChildTag(DOMUtil.getFirstChild((Element) children.next()), passwordTag),
                (String) executeChildTag(DOMUtil.getFirstChild((Element) children.next()),rootDirTag),
                writePermission);

        /* extracting the duration of the account using the GetAttribute that can parse the $ symbol*/
        String duration = this.engine.evaluateString(ftpAccount.getAttribute(DURATION), IEngine.EngineStringType.ATTRIBUTE);

        long service_instance_id=Long.valueOf((String)engine.getConfigurationVariablesStore().getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID));
        long interval=TimeInterval.getIntervalAsLong(duration);
        long due_date=DateUtil.getFutureDate(interval).getTime();

        timerManager=TimerManager.getInstance();
        timerManager.addFTPTimer(service_instance_id, due_date,user);

        if (children.hasNext()) {
            /*adding a new timer for executing the onExpire tag*/
            Element onExpireTag = null;
            onExpireTag = (Element) children.next();
            if (onExpireTag != null) {
                long script_id=ScriptUtil.createScriptFromLinkedList(DOMUtil.getChildren(onExpireTag), engine.getVariablesStore());

                timerManager.addTimerInstance(service_instance_id, script_id, due_date, "FTPExpire_"+due_date);

                logger.info("Added a timer for handling the onExpire tag.");
            }
        }
        return null;
    }
}
