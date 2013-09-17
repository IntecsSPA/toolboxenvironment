/*
 * JavaCompilerException.java
 *
 * Created on 1 ottobre 2007, 15.38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.util.compiler.exceptions;

import java.lang.Exception;

/**
 *
 * @author Massimiliano
 */
public class JavaCompilerException extends Exception{
    
    /** Creates a new instance of JavaCompilerException */
    public JavaCompilerException(Throwable t) {
       super(t);
    }
    
}
