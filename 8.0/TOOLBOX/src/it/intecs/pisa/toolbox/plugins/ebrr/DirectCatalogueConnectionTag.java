/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.CswBackendClient;
import be.kzen.ergorr.persist.service.DbConnectionParams;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import java.net.URL;

/**
 *
 * @author massi
 */
public class DirectCatalogueConnectionTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        String urlStr;
        String dbName;
        String dbUser;
        String dbPwd;
        URL serviceURL;

        DbConnectionParams cp = new DbConnectionParams();
        
        urlStr=evaluateAttribute(connEl,"dbUrl");
        cp.setDbUrl(urlStr);

        dbName=evaluateAttribute(connEl,"dbName");
        cp.setDbName(dbName);
        
        dbUser=evaluateAttribute(connEl,"dbUser");
        cp.setDbUser(dbUser);
        
        dbPwd=evaluateAttribute(connEl,"dbPassword");
        cp.setDbPassword(dbPwd);

        CswBackendClient client = new CswBackendClient(cp);

        return client;
    }
}
