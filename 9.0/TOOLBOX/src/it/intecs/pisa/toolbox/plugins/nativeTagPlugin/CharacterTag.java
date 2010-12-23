package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;

public class CharacterTag extends NativeTagExecutor{
        private static final String VALUE = "value";
    
	@Override
	public Object executeTag(org.w3c.dom.Element character) throws Exception{
                    return new Character((this.engine.evaluateString(character.getAttribute(VALUE),IEngine.EngineStringType.ATTRIBUTE)).charAt(0));
    }

   

}
