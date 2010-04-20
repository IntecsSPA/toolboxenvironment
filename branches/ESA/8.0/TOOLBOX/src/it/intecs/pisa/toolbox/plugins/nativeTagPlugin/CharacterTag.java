package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.soap.toolbox.IEngine;

public class CharacterTag extends NativeTagExecutor{
        private static final String VALUE = "value";
    
	@Override
	public Object executeTag(org.w3c.dom.Element character) throws Exception{
                    return new Character((this.engine.evaluateString(character.getAttribute(VALUE),IEngine.EngineStringType.ATTRIBUTE)).charAt(0));
    }

   

}
