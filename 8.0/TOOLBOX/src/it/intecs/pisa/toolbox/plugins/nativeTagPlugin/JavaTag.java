package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.compiler.CompilerFactory;
import it.intecs.pisa.compiler.JavaCompiler;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class JavaTag extends NativeTagExecutor {
    protected static final String TAG_CODE = "code";
    protected static final String TAG_IMPORTS = "imports";

    @Override
    public Object executeTag(org.w3c.dom.Element javaTagEl) throws Exception {
        IVariableStore confStore=null;
        FileOutputStream output=null;
        PrintWriter writer=null;
        Element importEl=null;
        Element bodyEl=null;
        File classFile=null;
        File tempDir=null;
        IJavaCompiledClass compiledClass=null;
        SimpleDateFormat formatter=null;
        
       confStore=this.engine.getVariablesStore();
       formatter=new SimpleDateFormat("yyyyMMddHHmmss");
       
       tempDir=new File(System.getProperty("java.io.tmpdir"),formatter.format(new Date()));
       tempDir.mkdir();
       classFile=new File(tempDir,"JavaTagOutput.java");
       
       output=new FileOutputStream(classFile);
       writer=new PrintWriter(output);
        
        importEl=DOMUtil.getChildByTagName(javaTagEl,TAG_IMPORTS);
        bodyEl=DOMUtil.getChildByTagName(javaTagEl,TAG_CODE);
         
        writeImports(writer,importEl);
        writeClassBeginning(writer);
        writeEngineVariablesGet(writer,engine);
        writeBody(writer,bodyEl);
        writeEngineVariablesSet(writer,engine);
        writeClassEnd(writer);
      
        writer.flush();
        writer.close();
        
        compiledClass=compileClass(classFile);
        compiledClass.execute(confStore);
      
        try
        {
            classFile.delete();
            tempDir.delete();
        }
        catch(Exception delEx)
        {
            //leaving the file and directory into the temp dir
        }
        return null;
    }

    private IJavaCompiledClass compileClass(File classFile) throws Exception {
       JavaCompiler compiler=null;
       Class compiledClass;
        String classpath;
        
       compiler=CompilerFactory.getCompiler();
        
        classpath=getClasspath();
        
        compiledClass=compiler.compile("JavaTagOutput", classFile,classpath);
        return (IJavaCompiledClass)  compiledClass.newInstance();
    }

    private String getClasspath() {
       String classpath;
       Toolbox tbx;
       String rootDir;
       File libDir;
       File nativeTagsLibs;
       File classesJar;
       File toBeRemoved;
       
       tbx=Toolbox.getInstance();
       rootDir=tbx.getServletContext().getRealPath("/");
       libDir=new File(rootDir,"WEB-INF/lib/toolbox.jar");
       nativeTagsLibs=new File(rootDir,"WEB-INF/plugins/ToolboxNativeTagPlugin/libs/");
      /* classesJar=new File(rootDir,"WEB-INF/classes");
       toBeRemoved=new File(rootDir,"../../../PluginsCore/dist/PluginsCore.jar");*/
       
       classpath =System.getProperty("java.class.path");
       classpath+=File.pathSeparator+libDir.getAbsolutePath();
       classpath+=File.pathSeparator+nativeTagsLibs.getAbsolutePath();
  /*     classpath+=File.pathSeparator+classesJar.getAbsolutePath();
       if(toBeRemoved.exists())
           classpath+=File.pathSeparator+toBeRemoved.getAbsolutePath();*/
       
       return classpath;
    }

    private void writeBody(PrintWriter writer, Element bodyEl) {
       NodeList bodyList;
       Node node;
       CDATASection cdata=null;
       
       bodyList=bodyEl.getChildNodes();
       
       for(int i=0;i<bodyList.getLength();i++)
       {
           node=bodyList.item(i);
           if(node instanceof CDATASection)
           {
               cdata=(CDATASection) node;
               
               break;
           }
           
       }
       
       if(cdata!=null)
       {
           writer.println(cdata.getWholeText());
       }
    }

    private void writeClassBeginning(PrintWriter writer) {
        String beginning="public class JavaTagOutput implements it.intecs.pisa.toolbox.plugins.nativeTagPlugin.IJavaCompiledClass {"+
                                         "  public void execute(it.intecs.pisa.soap.toolbox.IVariableStore varStore) { try{";
        
         writer.println(beginning);
    }
    
     private void writeClassEnd(PrintWriter writer) {
        String end="}catch(Exception e){e.printStackTrace();}}}";
        
         writer.println(end);
    }

    private void writeEngineVariablesGet(PrintWriter writer, IEngine engine) {
       IVariableStore varStore;
       Hashtable vars;
       Enumeration keys;
       String key;
       Object value;
       String type="";
       
       varStore=engine.getVariablesStore();
       vars=varStore.getVariables();
       keys=vars.keys();
       
       while(keys.hasMoreElements())
       {
           key=(String) keys.nextElement();
           value=vars.get(key);
           
           if(value instanceof java.lang.Byte ||
              value instanceof java.lang.Short ||
              value instanceof java.lang.Integer ||
              value instanceof java.lang.Long ||
              value instanceof java.lang.Float ||
              value instanceof java.lang.Double ||
              value instanceof java.lang.Character ||
              value instanceof java.lang.Boolean ||
              value instanceof java.lang.String )
               type=value.getClass().getName();
            else if( value instanceof org.w3c.dom.Document)     
               type="org.w3c.dom.Document";
           else type="";
           
           if(type.equals("")==false)
           {
               writer.println(type+" "+key+"=("+type+") varStore.getVariable(\""+key+"\");");
           }
              
       }

    }

    private void writeEngineVariablesSet(PrintWriter writer, IEngine engine) {
        IVariableStore varStore;
       Hashtable vars;
       Enumeration keys;
       String key;
       Object value;
       String type="";
       
       varStore=engine.getVariablesStore();
       vars=varStore.getVariables();
       keys=vars.keys();
       
       while(keys.hasMoreElements())
       {
           key=(String) keys.nextElement();
           value=vars.get(key);
           
           if(value instanceof java.lang.Byte ||
              value instanceof java.lang.Short ||
              value instanceof java.lang.Integer ||
              value instanceof java.lang.Long ||
              value instanceof java.lang.Float ||
              value instanceof java.lang.Double ||
              value instanceof java.lang.Character ||
              value instanceof java.lang.Boolean ||
              value instanceof java.lang.String ||
              value instanceof org.w3c.dom.Document )
               type=value.getClass().getName();
           else type="";
           
           if(type.equals("")==false)
           {
               writer.println(  "varStore.setVariable(\""+key+"\","+key+");");
           }
              
       }
    }

    private void writeImports(PrintWriter writer,Element importEl) {
       NodeList importList;
       Node node;
       Text importLine;
       
       importList=importEl.getChildNodes();
       
       for(int i=0;i<importList.getLength();i++)
       {
           node=importList.item(i);
           if(node instanceof Text)
           {
               importLine=(Text) node;
               
               writer.println(importLine.getNodeValue());
           }
           
       }
    }
    
 
}
