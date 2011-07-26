/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.DownloadsDB;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli, Andrea Marongiu
 */
public class CreateFakeDownload implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        File localFile;
        File webappDir;
        ZipFile zipFile=null;
        Document doc=null;
        boolean zipFileCheck=false;
        boolean metadataCheck=false;
        int zipFilesNumber=0;

        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            localFile=(File) cc.getAttribute(CommandsConstants.LOCAL_FILE);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            File destFile;
            destFile=new File(Prefs.getDownloadFolder(webappDir),itemId);
            
            try {
                zipFile = new ZipFile(localFile);
                zipFileCheck=true;
                
            } catch (ZipException exZip) {
            /*Data without Metadata*/
             IOUtil.moveFile(localFile,destFile);
            }
            
            if(zipFileCheck){
               File dataFile=IOUtil.getTemporaryFile();
               ZipEntry entry = null;
               Enumeration entries = zipFile.entries();
               while (entries.hasMoreElements()) {
                    zipFilesNumber++;
                    entry = (ZipEntry) entries.nextElement();
                    if(entry.getName().equals("metadata.xml")){
                       /*Data with Metadata*/ 
                       metadataCheck=true;
                       DOMUtil domUtil=new DOMUtil();
                       
                       doc=domUtil.inputStreamToDocument(zipFile.getInputStream(entry));
                       cc.setAttribute(CommandsConstants.ITEM_METADATA, doc);
                    }else
                       IOUtil.copy(zipFile.getInputStream(entry), 
                               new FileOutputStream(dataFile));
                    
              }
              zipFile.close();
              if(metadataCheck && zipFilesNumber==2){
                  localFile.delete();
                  IOUtil.moveFile(dataFile,destFile); 
              }else
                IOUtil.moveFile(localFile,destFile);
            
            }
            
            DownloadsDB.addDownload(itemId, "");
            DownloadsDB.updateStatus(itemId, "DOWNLOAD COMPLETE");
           
            cc.setAttribute(CommandsConstants.DOWNLOADED_FILE_NAME, itemId);            
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
