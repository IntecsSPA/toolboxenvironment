package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

public class FileInfoTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element finfo) throws Exception {
        String information;
        String filePath;

        information = (String) this.engine.evaluateString(finfo.getAttribute( "information"),IEngine.EngineStringType.ATTRIBUTE);
        filePath = (String) this.executeChildTag(DOMUtil.getFirstChild(finfo));

        File file = new File(filePath);
        if (file.exists()) {
            if (information.equals("MODIFICATION")) {
                return new Date(file.lastModified());
            } else {
                return new Long(file.length());
            }
        } else {
            throw new FileNotFoundException("File "+filePath+" doesn't exists");
        }
    }
}
