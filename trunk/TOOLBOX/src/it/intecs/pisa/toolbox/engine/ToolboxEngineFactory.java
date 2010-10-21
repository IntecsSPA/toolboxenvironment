/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.engine;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.file.LibsFileFilter;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *  This class creates an instance of the Toolbox Engine taking care of
 *  creating it with a proper ClassLoader
 * @author Massimiliano Fanciulli
 */
public class ToolboxEngineFactory {
    public static ToolboxEngine create(String serviceName,boolean debug) throws Exception
    {
        ServiceManager servMan;
        URL[] urls=null;

        servMan=ServiceManager.getInstance();
        TBXService service=servMan.getService(serviceName);

        File rootDir=service.getServiceRoot();
        File libsDir=new File(rootDir,"Resources/External Jars");

        File[] libs=libsDir.listFiles(new LibsFileFilter());

        if(libs!=null)
        {
            urls=new URL[libs.length];
            for(int i=0;i<libs.length;i++)
                urls[i]=libs[i].toURI().toURL();

            URLClassLoader loader;
            loader=new URLClassLoader(urls,Toolbox.class.getClassLoader());
            Class engineClass=loader.loadClass("it.intecs.pisa.toolbox.engine.ToolboxEngine");

            Constructor con=engineClass.getConstructor(org.apache.log4j.Logger.class,Boolean.class);

            return (ToolboxEngine) con.newInstance(service.getLogger(),debug);
        }
        else return new ToolboxEngine(service.getLogger(),Boolean.valueOf(debug));

        
    }

    public static ToolboxEngine create(String serviceName, boolean debugMode, File tmpDir) throws Exception {
        ServiceManager servMan;

        servMan=ServiceManager.getInstance();
        TBXService service=servMan.getService(serviceName);

        File rootDir=service.getServiceRoot();
        File libsDir=new File(rootDir,"Resources/External Jars");

        File[] libs=libsDir.listFiles(new LibsFileFilter());

        if(libs!=null)
        {
            URL[] urls;
            urls=new URL[libs.length];
            for(int i=0;i<libs.length;i++)
                urls[i]=libs[i].toURI().toURL();

            URLClassLoader loader;
            loader=new URLClassLoader(urls,Toolbox.class.getClassLoader());
            Class engineClass=loader.loadClass("it.intecs.pisa.toolbox.engine.ToolboxEngine");

            Constructor con=engineClass.getConstructor(org.apache.log4j.Logger.class,Boolean.class,File.class);

            return (ToolboxEngine) con.newInstance(service.getLogger(),debugMode,tmpDir);
        }
        else return new ToolboxEngine(service.getLogger(),Boolean.valueOf(debugMode),tmpDir);
    }
}
