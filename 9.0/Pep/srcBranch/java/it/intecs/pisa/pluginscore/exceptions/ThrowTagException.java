/*
 * ReturnTagException.java
 *
 * Created on 31 luglio 2007, 16.57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore.exceptions;

import org.w3c.dom.Element;


/**
 *
 * @author Massimiliano
 */
public class ThrowTagException extends Exception  {
  
    private Element errorEl;
    
    /** Creates a new instance of ReturnTagException */
    public ThrowTagException(String errorMessage) {
        super(errorMessage);
    }

    public ThrowTagException(String errorMessage, Element documentElement) {
        super(errorMessage);

        errorEl=documentElement;
    }
    
    
   

    public Element getErrorDetails()
    {
        return errorEl;
    }
}
