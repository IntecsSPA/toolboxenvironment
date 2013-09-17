package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;

public class LoadFileTag extends NativeTagExecutor{
        
	@Override
	public Object executeTag(org.w3c.dom.Element loadFile) throws Exception{
                   if (loadFile.getAttribute(FILE_TYPE).equals(BINARY)) {
            return IOUtil.loadBytes(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(
                    loadFile))));
        }
        return IOUtil.loadString(String.valueOf(executeChildTag(DOMUtil.getFirstChild(
                loadFile))));
    }

   

}
