package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.util.DOMUtil;
import java.io.File;

public class MkDirTag extends NativeTagExecutor{
          
	@Override
	public Object executeTag(org.w3c.dom.Element mkdir) throws Exception{
                     Object result = new Boolean(new File((String) this.executeChildTag(DOMUtil.getFirstChild(
                mkdir))).mkdirs());
       
        return result;
    }

   

}
