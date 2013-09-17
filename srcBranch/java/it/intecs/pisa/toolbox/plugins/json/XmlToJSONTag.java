package it.intecs.pisa.toolbox.plugins.json;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class XmlToJSONTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element tagEl) throws Exception {
        List<Element> childrenList=DOMUtil.getChildren(tagEl);
        JsonObject obj;



        return null;
    }
}
