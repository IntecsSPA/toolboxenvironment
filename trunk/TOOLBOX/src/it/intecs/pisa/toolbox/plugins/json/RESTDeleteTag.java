package it.intecs.pisa.toolbox.plugins.json;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;
import it.intecs.pisa.util.rest.RestDelete;
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
public class RESTDeleteTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element restTag) throws Exception {
        int status;
        LinkedList children;
        String urlStr;

        children=DOMUtil.getChildren(restTag);
        Element urlEl=(Element)children.get(0);
        urlStr=(String) this.executeChildTag(urlEl);
       
        status=RestDelete.del(new URL(urlStr), null, null, null);

        System.out.println(status);
        return new Integer(status);
    }
}
