

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
public class MetadataPrefs {
    
   
    private static final String METADATA_PROCESSING_LIST_JSON_FILE_PATH="WEB-INF/classes/metadataExtractionProcessings.json";
    private static final String METADATA_PROCESSING_JSON_ID_PROPERTY="id";
    private static final String METADATA_PROCESSING_JSON_ID_STRING_PROPERTY="idString";
    private static final String JSON_METADATA_PROCESSING_LIST_PROPERTY="metadataProcessingList";
    
    
    /**
     * Load Metadata Processing configuration
     * @param webappDir File
     * @return JSON String
     */
    public static String load(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,METADATA_PROCESSING_LIST_JSON_FILE_PATH);
        return IOUtil.inputToString(new FileInputStream(watchListFile));
    }
    
    /**
     * 
     * @return Save Metadata Processing configutation
     * 
     */
    public static void save(File webappDir, JsonObject obj) throws IOException {
        JsonObject watch;
        File propFile;
        int idHashCode;
        JsonPrimitive idProperties;

        propFile = new File(webappDir,METADATA_PROCESSING_LIST_JSON_FILE_PATH);
        
        JsonArray watchList=obj.getAsJsonArray(JSON_METADATA_PROCESSING_LIST_PROPERTY);
        
        for(int i=0; i<watchList.size(); i++){
            watch=(JsonObject) watchList.get(i);
            idProperties=watch.getAsJsonPrimitive(METADATA_PROCESSING_JSON_ID_STRING_PROPERTY);
            if(idProperties !=null){
                idHashCode=idProperties.getAsString().hashCode();
                watch.addProperty(METADATA_PROCESSING_JSON_ID_PROPERTY, idHashCode);
            }
        }
        
        IOUtil.copy(JsonUtil.getJsonAsStream(obj), new FileOutputStream(propFile));
    }

}
