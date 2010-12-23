package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;

public class RmDirTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element rmdir) throws Exception {
        IOUtil.rmdir(new File((String) this.executeChildTag(DOMUtil.getFirstChild(rmdir))));
        return null;
    }
}
