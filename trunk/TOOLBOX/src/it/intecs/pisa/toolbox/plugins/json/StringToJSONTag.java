package it.intecs.pisa.toolbox.plugins.json;

import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class StringToJSONTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element stringEl) throws Exception {
        String jsonStr;
        Element childEl;

        childEl=DOMUtil.getFirstChild(stringEl);
        jsonStr=(String) this.executeChildTag(childEl);

        return JsonUtil.getStringAsJSON(jsonStr);
    }
}
