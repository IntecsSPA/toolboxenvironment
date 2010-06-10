package it.intecs.pisa.toolbox.plugins.json;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;
import it.intecs.pisa.util.rest.RestGet;
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
public class RESTGetTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element restTag) throws Exception {
        Object responseJSON;
        LinkedList children;
        String urlStr;

        children=DOMUtil.getChildren(restTag);
        Element urlEl=(Element)children.get(0);
        urlStr=(String) this.executeChildTag(urlEl);
       
        responseJSON=RestGet.getAsJSON(new URL(urlStr), null, null, null);

        System.out.println(JsonUtil.getJsonAsString((JsonObject) responseJSON));
        return responseJSON;
    }
}
