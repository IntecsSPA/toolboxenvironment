/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.DownloadsDB;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.ChainTypesPrefs;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.prefs.WatchPrefs;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
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
        JsonObject chainTypesListJson = null;
        String command=null,outputWatchFolderPath=null;
        JsonElement el,el1;
        chainTypesListJson = ChainTypesPrefs.getChainTypeInformation(webappDir,storeItem.type); 
        
       
        if(chainTypesListJson !=null){
           el=chainTypesListJson.get(
           ChainTypesPrefs.PRE_PROCESSING_JSON_SCRIPT_PATH_PROPERTY);
            if(!(el ==null || el instanceof JsonNull))
                command=el.getAsString();
            el1=chainTypesListJson.get(
               ChainTypesPrefs.CHAIN_TYPE_JSON_OUTPUT_WATCH_PROPERTY);
            if(!(el1 ==null || el1 instanceof JsonNull))
                outputWatchFolderPath=el1.getAsString();
            if(command!=null && !command.equals(""))
            {
                File commandFile;
                File outFile;
                commandFile=new File(command);
                commandFile.setExecutable(true,false);

                File downloadDir=Prefs.getDownloadFolder(webappDir);

                File dataFile=new File(downloadDir,itemId);
                if(outputWatchFolderPath.equals("")){
                    
                    outFile= new File(downloadDir, itemId + "_processed");
                }else{
                  outFile= new File(outputWatchFolderPath);
                }
                ProcessBuilder pb;
                Process p;

                pb = new ProcessBuilder(command, dataFile.getAbsolutePath(), outFile.getCanonicalPath());
                pb.directory(commandFile.getParentFile());
                p = pb.start();
                p.waitFor();
                if(p.exitValue()!=0){
                    Log.log("Chain failed, Instance \""+itemId+"\" PRE PROCESSING ERROR");
                    Log.log("Instance \""+itemId+"\" Pre Processing Error: "+IOUtil.inputToString(p.getErrorStream()));
                    Log.log(" Instance \""+itemId+"\" Pre Processing Output: "+IOUtil.inputToString(p.getInputStream()));
                    File watchErrorFolder= new File("tmp/error");
                    watchErrorFolder.mkdirs();
                    DownloadsDB.updateStatus(itemId, "PRE PROCESSING ERROR");
                    File movedFile=new File(Prefs.getNotProcessedFolder(webappDir), dataFile.getName());
                    IOUtil.moveFile(dataFile, movedFile);
                    Log.log(" Instance \""+itemId+"\" not processed File moved to: "+movedFile.getCanonicalPath());
                }else                   
                    dataFile.delete();
                

            }
        }     
    }
}
