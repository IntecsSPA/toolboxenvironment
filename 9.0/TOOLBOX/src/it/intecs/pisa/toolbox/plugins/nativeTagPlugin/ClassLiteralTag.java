package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;

public class ClassLiteralTag extends NativeTagExecutor{
                  
	@Override
	public Object executeTag(org.w3c.dom.Element classLiteral) throws Exception{
                String className;
                
                className=this.engine.evaluateString(classLiteral.getAttribute(CLASS),IEngine.EngineStringType.ATTRIBUTE);
                    return Class.forName(className);
    }

   

}
