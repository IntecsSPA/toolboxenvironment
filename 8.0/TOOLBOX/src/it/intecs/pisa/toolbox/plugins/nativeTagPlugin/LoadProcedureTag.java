package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;

public class LoadProcedureTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element loadProcedure) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Element result = documentBuilderFactory.newDocumentBuilder().parse(new File((String) this.executeChildTag(DOMUtil.getFirstChild(loadProcedure)))).
                getDocumentElement();

        put(this.engine.evaluateString(loadProcedure.getAttribute(NAME), IEngine.EngineStringType.ATTRIBUTE), result);
        return result;
    }
}
