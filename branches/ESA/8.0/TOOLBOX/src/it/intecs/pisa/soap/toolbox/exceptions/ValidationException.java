/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.soap.toolbox.exceptions;

/**
 *
 * @author massi
 */
public class ValidationException extends Exception {
    private String errorMessage;
    private String scriptToExecute;

    public ValidationException()
    {
        
    }

    public ValidationException(String errorMessage,String scriptToExecute)
    {
        this.errorMessage=errorMessage;
        this.scriptToExecute=scriptToExecute;
    }

     public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getScriptToExecute()
    {
        return scriptToExecute;
    }
}
