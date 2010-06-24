/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ExtractMetadataFromData implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        StoreItem storeItem;
        Document doc=null;
        File webappDir;

        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            doc=(Document) cc.getAttribute(CommandsConstants.ITEM_METADATA);
            storeItem=(StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);

            if(doc==null &&
               storeItem.type!=null &&
               storeItem.type.equals("")==false)
            {
               doc=extractMetadataFromItem(itemId,storeItem,webappDir);
               cc.setAttribute(CommandsConstants.ITEM_METADATA, doc);
            }
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private Document extractMetadataFromItem(String itemId,StoreItem storeItem,File webappDir) throws Exception {
        Document extractedDoc=null;

        Properties props;
        props=new Properties();
        props.load(new FileInputStream(new File(webappDir,"WEB-INF/classes/metadataExtractionProcessings.properties")));
        
        //trying shell script
        String command=props.getProperty("shell."+storeItem.type);
        if(command!=null && command.equals("")==false)
        {
            File commandFile;
            commandFile=new File(webappDir,"WEB-INF/metadataExtractors/"+command);

            Properties prefs = Prefs.load(webappDir);

            File dataFile;
            dataFile=new File(prefs.getProperty("download.dir"),itemId);

            File outFile;
            outFile= new File(prefs.getProperty("download.dir"), itemId + "_metadata.xml");

            String shellCommand;
            shellCommand=commandFile.getAbsolutePath();
            shellCommand+=" "+dataFile.getAbsolutePath()+" "+outFile.getAbsolutePath();

            /*Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(shellCommand);
            process.waitFor();*/

            ProcessBuilder pb = new ProcessBuilder("./"+command, dataFile.getAbsolutePath(), outFile.getAbsolutePath());

            /*Map<String, String> env = pb.environment();
             env.put("VAR1", "myValue");
             env.remove("OTHERVAR");
             env.put("VAR2", env.get("VAR1") + "suffix");*/
             pb.directory(commandFile.getParentFile());
             Process p = pb.start();
             p.waitFor();

             DOMUtil util;
             util=new DOMUtil();

             extractedDoc=util.fileToDocument(outFile);
        }

        return extractedDoc;
    }
}
