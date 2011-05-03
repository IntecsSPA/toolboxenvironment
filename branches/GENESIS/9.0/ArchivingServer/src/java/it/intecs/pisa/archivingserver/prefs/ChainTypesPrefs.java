

package it.intecs.pisa.archivingserver.prefs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
public class ChainTypesPrefs {
    
 private static final String CHAIN_TYPES_LIST_JSON_FILE_PATH="WEB-INF/classes/chainTypes.json";
    
 
 public static final String CHAIN_TYPE_JSON_ID_PROPERTY="id";
 public static final String CHAIN_TYPE_JSON_ID_STRING_PROPERTY="idString";
 public static final String PRE_PROCESSING_JSON_ID_STORE_PROPERTY="ppIdScriptFileStored";
 public static final String METADATA_PROCESSING_JSON_ID_STORE_PROPERTY="mpIdScriptFileStored";
 public static final String PRE_PROCESSING_JSON_SCRIPT_PATH_PROPERTY="pplocalScriptPath";
 public static final String METADATA_PROCESSING_JSON_SCRIPT_PATH_PROPERTY="mplocalScriptPath";
 public static final String CHAIN_TYPES_JSON_LIST_PROPERTY="chainTypesList";
 public static final String CHAIN_TYPE_JSON_OUTPUT_TYPE_PROPERTY="ppOuputType";
 public static final String CHAIN_TYPE_JSON_TYPE_NAME_PROPERTY="typeName";
    
   /**
     * Load Data Processing configuration
     * @param webappDir File
     * @return JSON String
     */
    public static String load(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,CHAIN_TYPES_LIST_JSON_FILE_PATH);
        return IOUtil.inputToString(new FileInputStream(watchListFile));
    }
    
    
    
    /**
     * Load Chain Type list configuration
     * @param webappDir File
     * @return JSON Objcet
     */
    public static JsonObject loadJson(File webappDir)
                                    throws Exception {
        File watchListFile=new File(webappDir,CHAIN_TYPES_LIST_JSON_FILE_PATH);
        return JsonUtil.getInputAsJson(new FileInputStream(watchListFile));
    }
    
    /**
     * 
     * @return Save Chain Type list configuration 
     * 
     */
    public static void save(File webappDir, JsonObject obj) throws IOException {
        JsonObject chainType;
        JsonElement idStoreProperty,idProperty;
        File propFile;
        int idHashCode;
        String scriptPath="";

        propFile = new File(webappDir,CHAIN_TYPES_LIST_JSON_FILE_PATH);
        
        JsonArray chainTypesList=obj.getAsJsonArray(CHAIN_TYPES_JSON_LIST_PROPERTY);
        
        for(int i=0; i<chainTypesList.size(); i++){
            chainType=(JsonObject) chainTypesList.get(i);
            idProperty=chainType.get(CHAIN_TYPE_JSON_ID_STRING_PROPERTY);
            if(idProperty !=null){
                idHashCode=idProperty.getAsString().hashCode();
                chainType.addProperty(CHAIN_TYPE_JSON_ID_PROPERTY, idHashCode);
                idStoreProperty=chainType.get(PRE_PROCESSING_JSON_ID_STORE_PROPERTY);
                if(idStoreProperty== null || idStoreProperty instanceof JsonNull)
                   scriptPath= null; 
                else
                   scriptPath=new File(Prefs.getStoreFolder(webappDir), idStoreProperty.getAsString()).getCanonicalPath();
                
                chainType.addProperty(PRE_PROCESSING_JSON_SCRIPT_PATH_PROPERTY, scriptPath);
                idStoreProperty=chainType.get(METADATA_PROCESSING_JSON_ID_STORE_PROPERTY);
                if(idStoreProperty ==null || idStoreProperty instanceof JsonNull)
                  scriptPath= null;
                else 
                  scriptPath=new File(Prefs.getStoreFolder(webappDir), idStoreProperty.getAsString()).getCanonicalPath();
                
                chainType.addProperty(METADATA_PROCESSING_JSON_SCRIPT_PATH_PROPERTY, scriptPath);
                chainType.remove(CHAIN_TYPE_JSON_ID_STRING_PROPERTY);
            }
        }
        
        IOUtil.copy(JsonUtil.getJsonAsStream(obj), new FileOutputStream(propFile));
    }
    
    
    /**
     * @param webappDir File
     * @param chain Type String
     * @return Load Chain Type configuration
     * 
     */
    public static JsonObject getChainTypeInformation(File webappDir,String chainType) throws Exception{
        JsonObject chainTypeCollection=loadJson(webappDir);
        JsonObject chainTypeObject;
        String typeName;
        JsonArray chainTypesList=chainTypeCollection.getAsJsonArray(CHAIN_TYPES_JSON_LIST_PROPERTY);
        
        for(int i=0; i<chainTypesList.size(); i++){
            chainTypeObject=(JsonObject) chainTypesList.get(i);
            typeName=chainTypeObject.getAsJsonPrimitive(CHAIN_TYPE_JSON_TYPE_NAME_PROPERTY).getAsString();
            if(typeName.equals(chainType))
                return chainTypeObject;
        }
        
        
        return null;
    }
    
    
}
