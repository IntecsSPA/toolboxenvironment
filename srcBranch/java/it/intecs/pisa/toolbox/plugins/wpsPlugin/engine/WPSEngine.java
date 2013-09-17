
package it.intecs.pisa.toolbox.plugins.wpsPlugin.engine;


import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Andrea Marongiu
 */
public interface WPSEngine {

  static final String EXECUTE_OPERATION_PREFIX ="ExecuteProcess_";
  static final String RESPONSE_MESSAGE_NAME="ExecuteResponse";
  static final String EXECUTE_ASYNC_OPERATION_SUFIX="_ASYNC";
  static final String EXECUTE_ASYNC_PUSH_MESSAGE_NAME="ExecuteResponse";
  static final String EXECUTE_ASYNC_PUSH_RESPONSE_NAME="ExecuteResponse";


  static final String EXECUTE_POST_TARGET_NAMESPACE="http://www.opengis.net/wps/1.0.0";
  static final String EXECUTE_TOOLBOX_SCRIPT_FOLDER_PATH="AdditionalResources/WPS/ExecuteRequestToolboxScript/";
  static final String COMPLEX_DATA_PATH="AdditionalResources/WPS/XSL/ComplexData/";

  static final String PATH_OPERATION="Operations";
  
  static final String FIRST_SCRIPT_FILE_NAME="firstScript.tscript";
  static final String SECOND_SCRIPT_FILE_NAME="secondScript.tscript";
  static final String THIRD_SCRIPT_FILE_NAME="thirdScript.tscript";
  static final String RESPONSE_BUILDER_SCRIPT_FILE_NAME="respBuilder.tscript";
  static final String RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME="errorOnRespBuilder.tscript";
  static final String GLOBAL_ERROR_SCRIPT_FILE_NAME="globalError.tscript";

  public String getEngineName();

  public String generateEngineTemplate(File newServicePath, Document describeDocument, String processingName) throws Exception;

  public TBXSynchronousOperation createWPSSyncOperation(File newServicePath, String processingName) throws Exception;

  public TBXAsynchronousOperation createWPSAsyncOperation(File newServicePath, String processingName) throws Exception;

  public TBXScript[] getExecuteScriptDescriptorSync(File servicePath, String operationName) throws IOException, SAXException;

  public TBXScript[] getExecuteScriptDescriptorAsync(File servicePath, String operationName) throws IOException, SAXException;

  public void setScriptEngine(Object script) throws Exception;

  public void deleteScriptEngine(File newServicePath, String processingName)throws Exception;

  public void updateScriptEngine(File newServicePath, String processingName, Object script)throws Exception;

  public String getScriptPathforProcessingName(String processingName);

  public String getOriginalScriptPathforProcessingName(String processingName);

  public void generateEngineOutputManager(File servicePath,Document describeDocument, String processingName)throws Exception;
}
