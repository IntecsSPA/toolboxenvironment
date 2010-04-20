/*
 * JavaCompiler.java
 *
 * Created on 1 ottobre 2007, 11.26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.util.compiler;

import  it.intecs.pisa.util.compiler.exceptions.JavaCompilerException;
import java.io.File;

/**
 * This interface is used to mask the implementation of the code compiler. 
 * @author Massimiliano
 */
public interface JavaCompiler {
    public Class compile(String className,String classBody,String classPath) throws JavaCompilerException;
    public Class compile(String className,File classFile,String classPath) throws JavaCompilerException;
}
