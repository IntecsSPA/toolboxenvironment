package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.XMLSerializer2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DumpXMLTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dumpXML) throws Exception {
        Iterator children = DOMUtil.getChildren(dumpXML).iterator();
        File outputFile = null;

        outputFile = new File(this.executeChildTag((Element) children.next()).toString());
        OutputStream out = new FileOutputStream(outputFile);

        Document root = (Document) executeChildTag((Element) children.next());
        if (this.engine.evaluateString(dumpXML.getAttribute("indent"),IEngine.EngineStringType.ATTRIBUTE).equals("true")) {
            DOMUtil.indent(root);
        }
        new XMLSerializer2(out).serialize((Node) root.getDocumentElement());
        out.close();
        return null;
    }
}
