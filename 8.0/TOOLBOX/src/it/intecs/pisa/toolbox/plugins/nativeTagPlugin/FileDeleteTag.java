package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;

public class FileDeleteTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element fileDelete) throws Exception {
       Object result = new Boolean(new File((String) this.executeChildTag(DOMUtil.getFirstChild(
                fileDelete))).delete());
     
        return result;
    }
}
