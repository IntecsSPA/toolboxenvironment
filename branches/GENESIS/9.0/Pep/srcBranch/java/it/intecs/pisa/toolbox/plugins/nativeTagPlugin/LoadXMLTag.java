package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;

public class LoadXMLTag extends NativeTagExecutor{
        
	@Override
	public Object executeTag(org.w3c.dom.Element loadXML) throws Exception{
              String schemaLocation = this.engine.evaluateString(loadXML.getAttribute(VALIDATING_SCHEMA), IEngine.EngineStringType.ATTRIBUTE);
        if (schemaLocation.length() == 0) {
            return new DOMUtil().fileToDocument(String.valueOf(executeChildTag(
                    DOMUtil.getFirstChild(loadXML))));
        }
        return DOMUtil.loadXML(new File(String.valueOf(executeChildTag(DOMUtil.
                getFirstChild(loadXML)))), new File(schemaLocation));

    }

   

}
