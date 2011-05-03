

package it.intecs.pisa.archivingserver.prefs;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Andrea Marongiu
 */
public class WatchPrefs {

    private static final String WATCH_LIST_JSON_FILE_PATH="WEB-INF/classes/watchList.json";
    
    public static final String WATCH_JSON_ID_PROPERTY="id";
    public static final String WATCH_JSON_ID_STRING_PROPERTY="idString";
    public static final String WATCH_JSON_FOLDER_PROPERTY="watchFolder";
    public static final String JSON_WATCH_LIST_PROPERTY="watchList";
    public static final String JSON_WATCH_TYPE_PROPERTY="type";

    /**
     * Load watch list configuration
     * @param webappDir File
     * @return JSON String
     */
    public static String load(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,WATCH_LIST_JSON_FILE_PATH);
        return IOUtil.inputToString(new FileInputStream(watchListFile));
    }
    
    /**
     * Load watch list configuration
     * @param webappDir File
     * @return JSON Objcet
     */
    public static JsonObject loadJson(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,WATCH_LIST_JSON_FILE_PATH);
        return JsonUtil.getInputAsJson(new FileInputStream(watchListFile));
    }
    
    /**
     * 
     * @return Save watch configutation
     * 
     */
    public static void save(File webappDir, JsonObject obj) throws IOException {
        JsonObject watch;
        File propFile;
        int idHashCode;
        JsonPrimitive idProperties;

        propFile = new File(webappDir,WATCH_LIST_JSON_FILE_PATH);
        
        JsonArray watchList=obj.getAsJsonArray(JSON_WATCH_LIST_PROPERTY);
        
        for(int i=0; i<watchList.size(); i++){
            watch=(JsonObject) watchList.get(i);
            idProperties=watch.getAsJsonPrimitive(WATCH_JSON_ID_STRING_PROPERTY);
            if(idProperties !=null){
                idHashCode=idProperties.getAsString().hashCode();
                watch.addProperty(WATCH_JSON_ID_PROPERTY, idHashCode);
                watch.remove(WATCH_JSON_ID_STRING_PROPERTY);
            }
        }
        
        IOUtil.copy(JsonUtil.getJsonAsStream(obj), new FileOutputStream(propFile));
    }
    

   
    
    /**
     * @param webappDir File
     * @param watch Type String
     * @return Load Watch configuration
     */
    public static JsonObject getWatchInformation(File webappDir,String watchType) throws Exception{
        JsonObject watchCollection=loadJson(webappDir);
        JsonObject watchObject;
        String typeName;
        JsonArray watchList=watchCollection.getAsJsonArray(JSON_WATCH_LIST_PROPERTY);
        
        for(int i=0; i<watchList.size(); i++){
            watchObject=(JsonObject) watchList.get(i);
            typeName=watchObject.getAsJsonPrimitive(JSON_WATCH_TYPE_PROPERTY).getAsString();
            if(typeName.equals(watchType))
                return watchObject;
        }
        return null;
    }

}
