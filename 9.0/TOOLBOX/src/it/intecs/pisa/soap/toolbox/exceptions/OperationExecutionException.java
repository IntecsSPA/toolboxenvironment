/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.soap.toolbox.exceptions;

import java.io.File;

/**
 *
 * @author Massimiliano
 */
public class OperationExecutionException extends Exception{
    
    private String errorMessage;
    private String scriptToExecute;
    private File logDir;
    
    public OperationExecutionException(String errorMessage,String scriptToExecute)
    {
        this.errorMessage=errorMessage;
        this.scriptToExecute=scriptToExecute;
    }

    public OperationExecutionException(String errorMsg, String scriptToExecute, File requestLogDir) {
        this(errorMsg,scriptToExecute);
        this.logDir=requestLogDir;
    }
    
   
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    public String getScriptToExecute()
    {
        return scriptToExecute;
    }

    public void setLogDir(File requestLogDir) {
      logDir=requestLogDir;
    }

    public File getLogDir() {
        return logDir;
    }
}
