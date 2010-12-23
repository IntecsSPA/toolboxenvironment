/*
 * ReturnTagException.java
 *
 * Created on 31 luglio 2007, 16.57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore.exceptions;


/**
 *
 * @author Massimiliano
 */
public class ReturnTagException extends Exception  {
    
    private Object returnedStm;
    /** Creates a new instance of ReturnTagException */
    public ReturnTagException(Object toBeReturned) {
        super("Return tag exception");
        
        this.returnedStm=toBeReturned;
    }
    
    
    public Object getReturnedObject()
    {
        return this.returnedStm;
    }
}
