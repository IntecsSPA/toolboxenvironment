package it.intecs.pisa.toolbox.plugins.json;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonXpath;
import java.util.LinkedList;
import org.w3c.dom.Element;

/**
 * 
 * @author Massimiliano Fanciulli
 */
public class xPathTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element restTag) throws Exception {
        LinkedList children;

        children=DOMUtil.getChildren(restTag);
        Element jsonEl=(Element)children.get(0);
        Element xpathEl=(Element)children.get(1);

        JsonObject json=(JsonObject) this.executeChildTag(jsonEl);
        String xpath=(String) this.executeChildTag(xpathEl);

        String retVal=JsonXpath.getString(json, xpath);
        return retVal;
    }
}
