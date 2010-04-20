package it.intecs.pisa.toolbox;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.toolbox.util.TimeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TimerManager extends Thread {

    private static final String TIMER_STATUS = "timerStatus.xml";
    private static final String TIMER_STATUS_SCHEMA = "timerStatus.xsd";
    private File timerFile;
    private Document timerStatus;
    TBXService outer;

    public TimerManager(TBXService outer) throws Exception {
        super(outer.getServiceName() + "-TimerManager");
        File serviceRoot;
        DOMUtil util;

        util=new DOMUtil();
        
        this.outer = outer;
        serviceRoot=outer.getServiceRoot();
        timerFile = new File(serviceRoot,TIMER_STATUS);
        if (!timerFile.exists()) {
            IOUtil.copy(new File(new File(new File(outer.getRoot(), TBXService.WEB_INF), TBXService.XML), TIMER_STATUS), serviceRoot);
        }
        timerStatus = util.fileToDocument(timerFile);
    }

    public void run() {
        boolean executed;
        Iterator timers;
        Iterator variables;
        Iterator timerChildren;
        Element timer;
        Element variable;
        Document document = null;
        String now;
        ToolboxEngine toolboxEngine;
        Object object = null;
        File serviceResourceDir;
        IVariableStore configurationVariableStore;
        for (;;) {


            executed = false;
            now = TimeUtil.getDateTime();
            synchronized (this) {

                /* Loop over children of the root */
                timers = DOMUtil.getChildren(timerStatus.getDocumentElement()).iterator();
                while (timers.hasNext()) {
                    timer = (Element) timers.next();

                    /* If the expiration time is not past, nothing has to be done */
                    if (timer.getAttribute(TBXService.EXPIRATION_DATETIME).compareTo(now) > 0) {
                        continue;
                    }
                    if (timer.getLocalName().equals(TBXService.TIMER)) {
                        outer.getLogger().info("Executing timer action ...");
                        timerChildren = DOMUtil.getChildren(timer).iterator();
                        variables = DOMUtil.getChildren((Element) timerChildren.next()).iterator();
                        toolboxEngine = new ToolboxEngine(outer.getLogger(), this, outer.getFtpServerManager(), false,new File(new File(outer.getToolbox().getRootDir(), TBXService.WEB_INF), TBXService.TMP));
                        
                        serviceResourceDir=new File(this.outer.getServiceRoot(),"Resources");
                        
                        configurationVariableStore=toolboxEngine.getConfigurationVariablesStore();
                        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR, serviceResourceDir.getAbsolutePath());
          
                        while (variables.hasNext()) {
                            variable = (Element) variables.next();
                            if (variable.getLocalName().equals(TBXService.XML)) {
                                try {
                                    object = document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                                } catch (Exception e) {
                                    outer.getLogger().error("Error creating empty document: " + TBXService.CDATA_S + e.getMessage() + TBXService.CDATA_E);
                                }
                                document.appendChild(document.importNode(DOMUtil.getFirstChild(variable), true));
                            } else {
                                object = variable.getAttribute(TBXService.VALUE);
                            }
                            toolboxEngine.put(variable.getAttribute(TBXService.NAME), object);
                        }
                        try {
                            toolboxEngine.executeScript(DOMUtil.getFirstChild((Element) timerChildren.next()));
                            outer.getLogger().info("Timer script successful");
                        } catch (Exception e) {
                            outer.getLogger().error("Error executing script: " + TBXService.CDATA_S + e.getMessage() + TBXService.CDATA_E);
                        }
                    } else {
                        outer.getLogger().info("Deleting FTP account " + timer.getAttribute(TBXService.FTP_USER));
                        try {
                            /* Remove FTP account */
                            outer.getFtpServerManager().removeUser(timer.getAttribute(TBXService.FTP_USER));
                        } catch (Exception e) {
                            outer.getLogger().error("Error removing FTP account: " + TBXService.CDATA_S + e.getMessage() + TBXService.CDATA_E);
                        }
                    }

                    /* Remove the processed entry from timer status document */
                    timerStatus.getDocumentElement().removeChild(timer);
                    executed = true;
                }
                // End of loop on children of the root
                /* If something has been processed, the timer status is dumped on disk */
                if (executed) {
                    dumpTimers();
                }
            }
            try {
                sleep(TBXService.SECONDS * TBXService.MILLISECONDS);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    outer.getLogger().info(getName() + " stopping...");
                    break;
                }
            }
        }
    }

    synchronized public void addTimer(Hashtable status, Element timerElement) {
        outer.getLogger().info("Adding new timer entry");
        String URI = timerStatus.getDocumentElement().getNamespaceURI();
        String prefix = timerStatus.getDocumentElement().getPrefix();
        Element timer = (Element) timerStatus.getDocumentElement().appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.TIMER));

        /* Sets the script execution date-time */
        timer.setAttribute(TBXService.EXPIRATION_DATETIME, getExpiration(timerElement.getAttribute(TBXService.DELAY)));
        Element statusElement = (Element) timer.appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.STATUS));
        Object key;
        Object object;
        Element variable;
        Enumeration keys = status.keys();
        while (keys.hasMoreElements()) {
            object = status.get(key = keys.nextElement());
            if (object instanceof Document) {
                variable = (Element) statusElement.appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.XML));
                variable.setAttribute(TBXService.NAME, (String) key);
                variable.appendChild(timerStatus.importNode(((Document) object).getDocumentElement(), true));
            } else if (object instanceof String) {
                variable = (Element) statusElement.appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.VARIABLE));
                variable.setAttribute(TBXService.NAME, (String) key);
                variable.setAttribute(TBXService.VALUE, (String) object);
            }
        }
        // End of loop on the status table entries
        /* append the XML script to be executed to the <timer> element */
        timer.appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.SCRIPT)).appendChild(timerStatus.importNode(DOMUtil.getFirstChild(timerElement), true));

        /* Dump timer status on disk */
        dumpTimers();
    }

    synchronized public void addTimer(String ftpUser, String duration) {
        outer.getLogger().info("Adding new FTP account expiration timer");
        String URI = timerStatus.getDocumentElement().getNamespaceURI();
        String prefix = timerStatus.getDocumentElement().getPrefix();
        Element ftpAccount = (Element) timerStatus.getDocumentElement().appendChild(timerStatus.createElementNS(URI, prefix + ':' + TBXService.FTP_ACCOUNT));
        ftpAccount.setAttribute(TBXService.EXPIRATION_DATETIME, getExpiration(duration));
        ftpAccount.setAttribute(TBXService.FTP_USER, ftpUser);

        /* Dump timer status on disk */
        dumpTimers();
    }

    private String getExpiration(String delay) {
        GregorianCalendar time = new GregorianCalendar();
        /* Add the correct number of milliseconds */
        time.setTimeInMillis(time.getTimeInMillis() + Util.getMilliseconds(delay));
        /* Build the date-time String from the time object */
        return TimeUtil.getDateTime(time);
    }

    private void dumpTimers() {
        try {
            DOMUtil.dumpXML(timerStatus, timerFile);
        } catch (Exception e) {
            outer.getLogger().error("Error dumping timer status: " + TBXService.CDATA_S + e.getMessage() + TBXService.CDATA_E);
        }
    }

    synchronized public void clear() {
        outer.getLogger().info("Removing all timer entries");
        Element timerStatusRoot = timerStatus.getDocumentElement();
        while (timerStatusRoot.hasChildNodes()) {
            timerStatusRoot.removeChild(timerStatusRoot.getFirstChild());
        }
        dumpTimers();
    }

    public InputStream getTimerStatus() throws Exception {
        return new FileInputStream(timerFile);
    }
}
