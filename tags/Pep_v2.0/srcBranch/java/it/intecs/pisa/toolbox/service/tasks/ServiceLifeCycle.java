/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.service.tasks;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.lifecycle.*;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;

/**
 *
 * @author massi
 */
public class ServiceLifeCycle {
     public static void executeLifeCycleStep(String type, TBXService service, String operationName) {
        InterfacePluginManager interfman;
        Interface interfFromRepo;
        Interface serviceInterf;
        Operation operFromRepo;
        LifeCycle lifeCycle;
        File repoRootDir;

        try {

            serviceInterf = service.getImplementedInterface();
            interfman = InterfacePluginManager.getInstance();
            interfFromRepo = interfman.getInterfaceDescription(serviceInterf.getName(), serviceInterf.getVersion(), serviceInterf.getType(), serviceInterf.getMode());
            operFromRepo = interfFromRepo.getOperationByName(operationName);
            lifeCycle = operFromRepo.getServiceLifeCycle();
            if (lifeCycle != null) {
                repoRootDir = interfman.getInterfaceDescriptionPluginDirectory(serviceInterf.getName(), serviceInterf.getVersion(), serviceInterf.getType(), serviceInterf.getMode());
                lifeCycle.setRepoRootDir(repoRootDir);
                lifeCycle.executeScript(type, service.getServiceRoot());
            }
            System.out.println("lifecycle completed");
        } catch (Exception ex) {
            
        }
    }

    public static void executeLifeCycleStep(String type, TBXService service) {
        InterfacePluginManager interfman;
        Interface interfFromRepo;
        Interface serviceInterf;
        Operation operFromRepo;
        LifeCycle lifeCycle;
        File repoRootDir;

        try {

            serviceInterf = service.getImplementedInterface();
            interfman = InterfacePluginManager.getInstance();
            interfFromRepo = interfman.getInterfaceDescription(serviceInterf.getName(), serviceInterf.getVersion(), serviceInterf.getType(), serviceInterf.getMode());
            lifeCycle = interfFromRepo.getServiceLifeCycle();
            if (lifeCycle != null) {
                repoRootDir = interfman.getInterfaceDescriptionPluginDirectory(serviceInterf.getName(), serviceInterf.getVersion(), serviceInterf.getType(), serviceInterf.getMode());
                lifeCycle.setRepoRootDir(repoRootDir);
                lifeCycle.executeScript(type, service.getServiceRoot());
            }
            System.out.println("lifecycle completed");
        } catch (Exception ex) {

        }
    }
}
