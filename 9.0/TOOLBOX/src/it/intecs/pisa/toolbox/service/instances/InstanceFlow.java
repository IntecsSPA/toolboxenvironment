/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package it.intecs.pisa.toolbox.service.instances;

import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.util.DOMUtil;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceFlow {

    public static InputStream getSynchronousInstanceFlowAsXML(String serviceName, String instanceId) throws Exception {
        Document doc;
        DOMUtil util;
        Element rootEl;
        Element instanceEl;
        Element el = null;
        byte instanceStatus;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement("log");
        rootEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.setAttribute("serviceName", serviceName);
        doc.appendChild(rootEl);

        instanceEl = doc.createElement("instance");
        instanceEl.setAttribute("key", instanceId);
        instanceEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(instanceEl);

        el = doc.createElement("invalidInputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INVALID_INPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("inputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);
                    
        el = doc.createElement("completed");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_FIRST_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("invalidOutputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INVALID_OUTPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("outputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_OUTPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("error");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("email");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_ERROR_EMAIL));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        return DOMUtil.getDocumentAsInputStream(doc);
    }

     public static InputStream getAsynchronousInstanceFlowAsXML(String serviceName, String instanceId) throws Exception {
        Document doc;
        DOMUtil util;
        Element rootEl;
        Element instanceEl;
        Element el = null;
        byte instanceStatus;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement("flow");
        rootEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.setAttribute("serviceName", serviceName);
        doc.appendChild(rootEl);

        instanceEl = doc.createElement("instance");
        instanceEl.setAttribute("key", instanceId);
        instanceEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(instanceEl);

        instanceStatus = InstanceStatuses.getInstanceStatus(Long.parseLong(instanceId));
        rootEl.setAttribute("status", Byte.toString(instanceStatus));

        try
        {
            int remainingAttempts;
            remainingAttempts=InstanceInfo.getRemainingAttempts(Long.parseLong(instanceId));
            rootEl.setAttribute("remainingAttempts", Integer.toString(remainingAttempts));
        }
        catch(Exception e)
        {

        }
        
        el = doc.createElement("inputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("invalidInputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INVALID_INPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("responseScriptExecution");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_RESPONSE_BUILDER_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("acknowledgeMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_RESPONSE_BUILDER_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("firstScriptExecution");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_FIRST_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("secondScriptExecution");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_SECOND_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("thirdScriptExecution");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_THIRD_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("invalidOutputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_INVALID_OUTPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("outputMessage");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_OUTPUT_MESSAGE));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("error");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        el = doc.createElement("email");
        el.setAttribute("id", getResourceId(instanceId, InstanceResources.TYPE_ERROR_EMAIL));
        el.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/log");
        rootEl.appendChild(el);

        return DOMUtil.getDocumentAsInputStream(doc);
    }

     protected static String getResourceId(String instanceId,String type)
     {
         try
         {
             return InstanceResources.getXMLResourceId(instanceId, type);
         }
         catch(Exception e)
         {
             return "";
         }
     }

}
