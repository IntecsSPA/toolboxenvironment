

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
public class PreProcessingPrefs {
    
 private static final String PRE_PROCESSING_LIST_JSON_FILE_PATH="WEB-INF/classes/dataProcessings.json";
    
 private static final String PRE_PROCESSING_JSON_ID_PROPERTY="id";
 private static final String PRE_PROCESSING_JSON_ID_STRING_PROPERTY="idString";
 private static final String PRE_PROCESSING_JSON_ID_STORE_PROPERTY="idScriptFileStored";
 private static final String PRE_PROCESSING_JSON_SCRIPT_PATH_PROPERTY="localScriptPath";
 private static final String JSON_PRE_PROCESSING_LIST_PROPERTY="dataProcessingList";
    
   /**
     * Load Data Processing configuration
     * @param webappDir File
     * @return JSON String
     */
    public static String load(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,PRE_PROCESSING_LIST_JSON_FILE_PATH);
        return IOUtil.inputToString(new FileInputStream(watchListFile));
    }
    
    /**
     * 
     * @return Save Data Processing configutation
     * 
     */
    public static void save(File webappDir, JsonObject obj) throws IOException {
        JsonObject preProcessing;
        File propFile;
        int idHashCode;
        JsonPrimitive idProperty, idStoreProperty;
        String scriptPath="";

        propFile = new File(webappDir,PRE_PROCESSING_LIST_JSON_FILE_PATH);
        
        JsonArray preProcessingList=obj.getAsJsonArray(JSON_PRE_PROCESSING_LIST_PROPERTY);
        
        for(int i=0; i<preProcessingList.size(); i++){
            preProcessing=(JsonObject) preProcessingList.get(i);
            idProperty=preProcessing.getAsJsonPrimitive(PRE_PROCESSING_JSON_ID_STRING_PROPERTY);
            if(idProperty !=null){
                idHashCode=idProperty.getAsString().hashCode();
                preProcessing.addProperty(PRE_PROCESSING_JSON_ID_PROPERTY, idHashCode);
                idStoreProperty=preProcessing.getAsJsonPrimitive(PRE_PROCESSING_JSON_ID_STORE_PROPERTY);
                scriptPath=new File(Prefs.getWorkspaceFolder(webappDir), idStoreProperty.getAsString()).getCanonicalPath();
                preProcessing.addProperty(PRE_PROCESSING_JSON_SCRIPT_PATH_PROPERTY, scriptPath);
                preProcessing.remove(PRE_PROCESSING_JSON_ID_STRING_PROPERTY);
            }
        }
        
        IOUtil.copy(JsonUtil.getJsonAsStream(obj), new FileOutputStream(propFile));
    }
    
    

}
