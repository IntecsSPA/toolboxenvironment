

package it.intecs.pisa.gisclient.util.log.monitor;

import java.io.File;
import java.util.*;
/**
 *
 * @author maro
 */
public class FolderFilesTimerDelete extends TimerTask{

private String folderPath;
private int fileDuration;

 FolderFilesTimerDelete (String folderPath, int fileHoursDuration){
    this.folderPath=folderPath;
    this.fileDuration=fileHoursDuration;
 }


    public void run() {
        Date currentDate = new Date();
        Long currentTime=currentDate.getTime();

        File di = new File(this.folderPath);
        File fl[] = di.listFiles();
        int i;
        Long lastModified;

        for (i=0; i < fl.length; i++){
             lastModified = fl[i].lastModified();
             if( currentTime -lastModified >= this.fileDuration){
                if(fl[i].isDirectory()){
                }else
                  fl[i].delete();

             }
        }
        System.out.println("Running the task");
    }

  private void recursiveDeleteFolderContent(File folder, Long maxDuration){
        File contentList[] = folder.listFiles();
        Date currentDate = new Date();
        Long currentTime=currentDate.getTime();
        Long lastModified;
        int i;
        for (i=0; i < contentList.length; i++){
            lastModified = contentList[i].lastModified();
            if( currentTime -lastModified >= maxDuration){
                //if(contentList[i].isDirectory())
            }
        }


  }
}
