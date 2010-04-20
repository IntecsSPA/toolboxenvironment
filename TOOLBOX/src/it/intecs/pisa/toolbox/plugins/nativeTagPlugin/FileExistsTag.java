package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;

public class FileExistsTag extends NativeTagExecutor {
    protected String tagName="fileExists";
    
    @Override
    public Object executeTag(org.w3c.dom.Element fileExists) throws Exception {
        File fileToBeChecked;
        
        fileToBeChecked=new File((String) this.executeChildTag(DOMUtil.getFirstChild(fileExists)));
        Object result = new Boolean(fileToBeChecked.exists());
      
        addResourceLinkToDebugTree(fileToBeChecked);
        
        return result;
    }
}
