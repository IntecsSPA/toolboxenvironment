package it.intecs.pisa.toolbox.plugins.json;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;
import it.intecs.pisa.util.rest.RestPost;
import java.net.URL;
import java.util.LinkedList;
import org.w3c.dom.Element;

/**
 * <RestTag username="" password="" contentType="">
 *      <url>
 *      <json>
 * </RestTag>
 * @author Massimiliano Fanciulli
 */
public class RESTPostTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element restTag) throws Exception {
        Object responseJSON;
        Object requestJSON;
        LinkedList children;
        String urlStr;

        children=DOMUtil.getChildren(restTag);
        Element urlEl=(Element)children.get(0);
        Element jsonEl=(Element)children.get(1);

        urlStr=(String) this.executeChildTag(urlEl);
        requestJSON=this.executeChildTag(jsonEl);

        System.out.println(JsonUtil.getJsonAsString((JsonObject) requestJSON));
        responseJSON=RestPost.postAsJSON(new URL(urlStr), null, null, null, (JsonObject) requestJSON);

        System.out.println(JsonUtil.getJsonAsString((JsonObject) responseJSON));
        return responseJSON;
    }
}
