package it.intecs.pisa.toolbox.plugins.json;

import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.json.JsonUtil;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class StringToJSONTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element stringEl) throws Exception {
        String jsonStr;
 
        jsonStr=stringEl.getTextContent();

        return JsonUtil.getStringAsJSON(jsonStr);
    }
}
