package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;
import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.TimerManager;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
        ftpServerManager = (FTPServerManager) configVarStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_FTP_SERVER_MANAGER);
        timerManager = (TimerManager) configVarStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TIMER_MANAGER);
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

        ftpServerManager.addUser(
                user,
                (String) this.executeChildTag(DOMUtil.getFirstChild((Element) children.next()), passwordTag),
                (String) executeChildTag(DOMUtil.getFirstChild((Element) children.next()),rootDirTag),
                writePermission);

        /* extracting the duration of the account using the GetAttribute that can parse the $ symbol*/
        String duration = this.engine.evaluateString(ftpAccount.getAttribute(DURATION), IEngine.EngineStringType.ATTRIBUTE);
        timerManager.addTimer(user, duration);

        if (children.hasNext()) {
            /*adding a new timer for executing the onExpire tag*/
            Element onExpireTag = null;
            onExpireTag = (Element) children.next();
            if (onExpireTag != null) {
                /*creating the timer*/
                /*WARNING: the method addTimer thinks that the tag name is timer, now we are passing to them a onExpire Tag!
                 *Everithing works because the method doesn't che the tag name and its content is the same in both tags*/

                Element timerElement = ftpAccount.getOwnerDocument().createElement(TIMER);

                children = DOMUtil.getChildren(onExpireTag).iterator();

                while (children.hasNext()) {

                    timerElement.appendChild(((Node) children.next()).cloneNode(true));

                }
                timerElement.setAttribute(DELAY, duration);
                timerManager.addTimer(varStore.getVariables(), timerElement);

                logger.info("Added a timer for handling the onExpire tag.");
            }
        }
        return null;
    }
}
