package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DumpFileTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dumpFile) throws Exception {
        Iterator children = DOMUtil.getChildren(dumpFile).iterator();

        if (dumpFile.getAttribute(FILE_TYPE).equals(BINARY)) {
            IOUtil.dumpBytes((String) this.executeChildTag((Element) children.next()),
                    (byte[]) executeChildTag((Element) children.next()));
            return null;
        }
        IOUtil.dumpString((String) executeChildTag((Element) children.next()),
                (String) executeChildTag((Element) children.next()));
        return null;
    }
}
