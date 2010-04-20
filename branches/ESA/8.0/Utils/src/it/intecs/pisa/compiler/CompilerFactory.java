/*
 * CompilerFactory.java
 *
 * Created on 1 ottobre 2007, 15.19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.compiler;

/**
 *
 * @author Massimiliano
 */
public class CompilerFactory {
    
    private static final String JAVA_VERSION_1_5="1.5";
    private static final String JAVA_VERSION_1_6="1.6";
    /**
     * Creates a new instance of CompilerFactory
     */
    public CompilerFactory() {
    }
    
    public static JavaCompiler getCompiler() {
        //This class instantiates the best compiler available for the current
        //machine
        String javaVersion=System.getProperty("java.version").substring(0,3);
        
     //   if(javaVersion.equals(JAVA_VERSION_1_5))
            return new JavaCompilerForJDK1_5();
        
         
        //return null;
    }
}
