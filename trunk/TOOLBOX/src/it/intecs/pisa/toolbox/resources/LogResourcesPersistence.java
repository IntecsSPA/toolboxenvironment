/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package it.intecs.pisa.toolbox.resources;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.toolbox.util.ToolboxRollingFileAppender;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.FileFilter;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class LogResourcesPersistence {
    protected static final int MAX_BACKUP_INDEX = 9;
    protected static final  String DOT_LOG = ".log";
    
    protected File storeDirectory;
    protected static final LogResourcesPersistence instance=new LogResourcesPersistence();

    protected LogResourcesPersistence()
    {

    }

    public static LogResourcesPersistence getInstance()
    {
        return instance;
    }

    public void setDirectory(File dir)
    {
        storeDirectory=dir;
    }

    public Logger getRollingLogForService(String serviceName) throws Exception
    {
        Logger logger;
        File logFile;
        File logDir;
        String logFileSize;
        int inKbFileSize;
        ToolboxConfiguration tbxConfig;
        String logPattern;

        try
        {
            tbxConfig=ToolboxConfiguration.getInstance();

            logger = Logger.getLogger("it.intecs.pisa.soap.toolbox.Service." + serviceName);

            logDir=new File(storeDirectory,serviceName);
            logDir.mkdirs();

            logFile = new File(logDir, serviceName + ".log");
            logFileSize = tbxConfig.getConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE);
            logPattern=tbxConfig.getConfigurationValue(ToolboxConfiguration.LOG_PATTERN);

            inKbFileSize=Integer.parseInt(logFileSize)*1024;

            ToolboxRollingFileAppender rollingAppender = new ToolboxRollingFileAppender(new PatternLayout(logPattern), logFile.getAbsolutePath());
            rollingAppender.setMaxFileSize(Integer.toString(inKbFileSize));
            rollingAppender.setMaxBackupIndex(MAX_BACKUP_INDEX);
            logger.addAppender(rollingAppender);
            rollingAppender.activateOptions();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot create logger",e);
        }

        return logger;
    }

    public File getLogForService(String serviceName,int index) throws Exception
    {
        File logFile;
        File logDir;
        String filename;

        try
        {
           if (index == 0) {
                filename=serviceName+".log";
            } else {
                filename=serviceName+".log."+index;
            }

            logDir=new File(storeDirectory,serviceName);
            logFile = new File(logDir, filename);


            return logFile;
        }
        catch(Exception e)
        {
            throw new Exception("Cannot get log file",e);
        }

    }

    public int getLogPagingSize(String serviceName) throws Exception {
        File logDir;

        logDir=new File(storeDirectory,serviceName);
        return logDir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                return (file.getName().indexOf(DOT_LOG) != -1);
            }
        }).length;
    }

     public Logger getRollingLogForToolbox() throws Exception
    {
        Logger logger;
        File logFile;
        String logFileSize;
        ToolboxConfiguration tbxConfig;
        String logPattern;

        try
        {
            tbxConfig=ToolboxConfiguration.getInstance();

            logger = Logger.getLogger("it.intecs.pisa.soap.toolbox.Toolbox");

            logFile = new File(storeDirectory, "Toolbox.log");
            logFileSize = tbxConfig.getConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE);
            logPattern=tbxConfig.getConfigurationValue(ToolboxConfiguration.LOG_PATTERN);
            int inKbFileSize = Integer.parseInt(logFileSize) * 1024;

            ToolboxRollingFileAppender rollingAppender = new ToolboxRollingFileAppender(new PatternLayout(logPattern), logFile.getAbsolutePath());
            rollingAppender.setMaxFileSize(Integer.toString(inKbFileSize));
            rollingAppender.setMaxBackupIndex(MAX_BACKUP_INDEX);
            logger.addAppender(rollingAppender);
            rollingAppender.activateOptions();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot create logger",e);
        }

        return logger;
    }

    public void deleteLog(String service) throws Exception {
        File logDir;

        try
        {

            logDir=new File(storeDirectory,service);
            IOUtil.rmdir(logDir);

            logDir.mkdirs();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot delete log files",e);
        }
    }

    public File getLogForToolbox(int index) throws Exception {
        File logFile;
        String filename;

        try
        {
           if (index == 0) {
                filename="Toolbox.log";
            } else {
                filename="Toolbox.log."+index;
            }

            logFile=new File(storeDirectory,filename);
            return logFile;
        }
        catch(Exception e)
        {
            throw new Exception("Cannot get Toolbox log file",e);
        }
    }

     public int getToolboxLogPagingSize() throws Exception {
        return storeDirectory.listFiles(new FileFilter() {

            public boolean accept(File file) {
                String fileName;
                fileName=file.getName();

                return (fileName.indexOf(DOT_LOG) != -1);
            }
        }).length;
    }

    public void deleteToolboxLog() throws Exception {
        File logFile;
        try
        {
            logFile=new File(storeDirectory,"Toolbox.log");
            logFile.delete();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot delete log files",e);
        }
    }


}
