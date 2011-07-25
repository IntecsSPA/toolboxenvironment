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
public class TagException extends Exception  {

    public TagException(Class c) {
        super("Error while executing tag "+c.getCanonicalName());
    }

}
