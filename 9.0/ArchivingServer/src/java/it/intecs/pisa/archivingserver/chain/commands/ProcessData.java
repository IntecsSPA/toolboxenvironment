/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
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
public class ProcessData implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        StoreItem storeItem;
        Document doc=null;
        File webappDir;

        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            doc=(Document) cc.getAttribute(CommandsConstants.ITEM_METADATA);
            storeItem=(StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);

            if(doc==null &&
               storeItem.type!=null &&
               storeItem.type.equals("")==false)
            {
               processData(itemId,storeItem,webappDir);
            }
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private void processData(String itemId,StoreItem storeItem,File webappDir) throws Exception {
        Document extractedDoc=null;

        Properties props;
        props=new Properties();
        props.load(new FileInputStream(new File(webappDir,"WEB-INF/classes/dataProcessings.properties")));
        
        //trying shell script
        String command=props.getProperty("shell."+storeItem.type);
        if(command!=null && command.equals("")==false)
        {
            File commandFile;
            commandFile=new File(webappDir,"WEB-INF/dataProcessors/"+command);
            commandFile.setExecutable(true,false);
            
            Properties prefs = Prefs.load(webappDir);

            File dataFile;
            dataFile=new File(prefs.getProperty("download.dir"),itemId);

            File outFile;
            outFile= new File(prefs.getProperty("download.dir"), itemId + "_processed");
            
            ProcessBuilder pb;
            Process p;
  
            pb = new ProcessBuilder("./"+command, dataFile.getAbsolutePath(), outFile.getAbsolutePath());
            pb.directory(commandFile.getParentFile());
            p = pb.start();
            p.waitFor();

            dataFile.delete();
            outFile.renameTo(dataFile);
            
        }
    }
}
