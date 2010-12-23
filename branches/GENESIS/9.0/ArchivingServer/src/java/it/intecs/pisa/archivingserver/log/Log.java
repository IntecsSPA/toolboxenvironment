/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.log;

import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class Log {
    public static void log(String text)
    {
        Logger logger = Logger.getLogger("it.intecs.pisa.archivingserver");
        logger.info(text);
    }

    public static void logException(Exception e)
    {
        Logger logger = Logger.getLogger("it.intecs.pisa.archivingserver");
        logger.error("An exception has been thrown. Details: "+e.getMessage());
    }

    public static void logHTTPStatus(String url, int code)
    {
        Logger logger = Logger.getLogger("it.intecs.pisa.archivingserver");
        logger.debug("The HTTP exchange with URL "+url+" return code "+code);
    }
}
