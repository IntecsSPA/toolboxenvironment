/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.CswSoapClient;
import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import java.net.URL;

/**
 *
 * @author massi
 */
public class CatalogueConnectionTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        String urlStr;
        URL serviceURL;

        urlStr=this.engine.evaluateString(connEl.getAttribute("url"), IEngine.EngineStringType.ATTRIBUTE);
        serviceURL= new URL(urlStr);

        CswClient client = new CswSoapClient(serviceURL);
        return client;
    }
}
