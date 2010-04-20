/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.CatalogueCorrespondence;
import it.intecs.pisa.archivingserver.db.SOAPCatalogueAccessible;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteFromEbRIMCatalogue implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        File appDir;
        File itemDir;
        try {
            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            //Add deletion of item from catalogue
            SOAPCatalogueAccessible.delete(itemId);
            CatalogueCorrespondence.delete(itemId);
        } catch (Exception e) {
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
