package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;

public class StringTag extends NativeTagExecutor{

	@Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
            String returnValue = DOMUtil.getStringFromElement(tagEl);
            returnValue=engine.evaluateString(returnValue,IEngine.EngineStringType.TEXT);

            return returnValue;
    }

   

}
