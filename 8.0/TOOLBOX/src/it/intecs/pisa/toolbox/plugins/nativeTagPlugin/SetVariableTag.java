package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetVariableTag extends NativeTagExecutor{
                   
        public SetVariableTag()
        {
            tagName="SetVariable";
        }
        
	@Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
            Object value =null;
            Element child=null;
            String variableName=null;
            String nameAttrValue=null;
            IVariableStore varStore=null;
            
            child=DOMUtil.getFirstChild(tagEl);
            value = this.executeChildTag(child);
            
            nameAttrValue=tagEl.getAttribute(NAME);
             variableName =this.engine.evaluateString(nameAttrValue, IEngine.EngineStringType.ATTRIBUTE);      
             
             varStore=this.engine.getVariablesStore();
             
             varStore.setVariable(variableName, value);

             if(value instanceof Document)
             {
                 dumpResourceAndAddToDebugTree((Document)value);
             }
            return value;
    }

   

}
