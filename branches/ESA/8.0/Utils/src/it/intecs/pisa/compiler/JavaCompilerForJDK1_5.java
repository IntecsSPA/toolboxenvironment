/*
 * JavaCompilerForJDK1_5.java
 *
 * Created on 1 ottobre 2007, 12.13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.compiler;

import com.sun.tools.javac.Main;
import it.intecs.pisa.compiler.JavaCompiler;
import it.intecs.pisa.compiler.exceptions.JavaCompilerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Massimiliano
 */
public class JavaCompilerForJDK1_5 implements JavaCompiler{
    
    /**
     * Creates a new instance of JavaCompilerForJDK1_5
     */
    public JavaCompilerForJDK1_5() {
      
    }

        public Class compile(String className,String classBody,String classpath)  throws JavaCompilerException {
           try {
                   String filename=null;
                   String tempDir=System.getProperty("java.io.tmpdir");
                   FileWriter writer=null;
                   File classFile=null;
                   
                    //extracting className and package subdirectory
                   String cName=className.substring(className.lastIndexOf(".")+1);
                  
                   //Writing class body to disk
                   filename=tempDir+cName+".java";
                   writer=new FileWriter(filename);
                   writer.write(classBody);
                   writer.flush();
                  
                   classFile=new File(filename);
                   
                    return compile(className,classFile,classpath);
                } catch (Exception ex) {                
                    throw new JavaCompilerException(ex.getCause());
                }
                 
      }
        
         public Class compile(String className,File classFile,String classpath)  throws JavaCompilerException {
           File tmpDir=null;
           
             try {
                   SimpleDateFormat formatter=null;
                  
                   String tempDir=null;
                
                    formatter=new SimpleDateFormat("yyyyMMddHHmmss");
                    
                   com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();  
                   tmpDir=new File(System.getProperty("java.io.tmpdir"),formatter.format(new Date()));
                   tmpDir.mkdir();
                   tempDir=tmpDir.getAbsolutePath();
                 
                   String[] options = new String[] { "-classpath", classpath, "-d",tempDir,classFile.getAbsolutePath()};
                   Main.compile(options);

                   URL[] urls={tmpDir.toURL()};
                   URLClassLoader loader=new URLClassLoader(urls,this.getClass().getClassLoader());

                    return loader.loadClass(className);
                } catch (Exception ex) {                
                    ex.printStackTrace();
                    throw new JavaCompilerException(ex.getCause());
                }
                finally
                {
                    File inputFile;
                    
                    if(tmpDir!=null)
                    {
                        inputFile=new File(tmpDir,className+".class");
                        inputFile.delete();
                        tmpDir.delete();
                    }
                    
                }
                 
      }
    
}
