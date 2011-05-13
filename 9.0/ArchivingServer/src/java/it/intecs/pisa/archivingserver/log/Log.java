

package it.intecs.pisa.archivingserver.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class Log {
    
    private static Logger log;
    
    
    public static void setup() {

        if (log == null) {
            System.out.println("Setting up logger");
            try { 
                Handler consHandler = new ConsoleHandler();
                consHandler.setFormatter(new LogFormatter());
                consHandler.setLevel(Level.FINEST);
                
                log = Logger.getLogger("it.intecs.pisa.archivingserver");
                log.addHandler(consHandler);
                log.setLevel(Level.FINEST);
            } catch (Exception ex) {
                System.out.println("Error setting up Logger");
                ex.printStackTrace();
            }
        }
    }
    
    public static void log(String text){
        if(log == null)
              setup();
        log.log(Level.INFO,text);
    }
    
    
    public static void debug(String text){
        if(log == null)
              setup();
        log.log(Level.FINEST,text);
    }

    public static void logException(Exception e)
    {
        if(log == null)
              setup();
        log.log(Level.SEVERE, "An exception has been thrown. Details: ", e);
        log.log(Level.SEVERE, e.toString());
       
    }

    public static void logHTTPStatus(String url, int code)
    {
        if(log == null)
              setup();
        log.log(Level.FINEST, "The HTTP exchange with URL {0} return code {1}", new Object[]{url, code});
    }
    
    public static Level getLogLevel(){
        if(log == null)
              setup();
        return log.getLevel();
    }
    
    public static void setLevel(Level newLevel) {
           System.out.println("Setting new level logger");
            try {

                Handler consHandler = new ConsoleHandler();
                consHandler.setFormatter(new LogFormatter());
                consHandler.setLevel(newLevel);
                
                log = Logger.getLogger("be.kzen.ergorr");
                log.addHandler(consHandler);
                log.setLevel(newLevel);
            } catch (Exception ex) {
                System.out.println("Error setting new Level Logger");
                ex.printStackTrace();
            }

        
    }
}
