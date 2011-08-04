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

package it.intecs.pisa.toolbox.log;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.resources.LogResourcesPersistence;
import java.io.File;
import java.io.InputStream;
import it.intecs.pisa.toolbox.util.Util;
import java.io.FileInputStream;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ToolboxLogHandler {
    public static InputStream extractLog(int index) throws Exception {
        LogResourcesPersistence resPers;
        File logFile;
        int pages=0;

        resPers=LogResourcesPersistence.getInstance();
        logFile=resPers.getLogForToolbox(index);
        pages=resPers.getToolboxLogPagingSize();
        return getHandler(pages,logFile);
    }

    public static void deleteLog() throws Exception
    {
         LogResourcesPersistence resPers;

         resPers=LogResourcesPersistence.getInstance();
         resPers.deleteToolboxLog();

    }
    
    private static InputStream getHandler(int pageNumber, File logFile)throws Exception{
        ToolboxConfiguration conf=ToolboxConfiguration.getInstance();
        String logType=conf.getConfigurationValue(ToolboxConfiguration.LOG_TYPE);
        if(logType.equals("json"))
            return Util.addHeaderAndFooter("{ \"totalFileNumber\" : \"" + pageNumber + "\", \"logs\" : [ {\"level\":\"\", \"thread\":\"\", \"date\":\"\", \"text\":\"LOG\" }", new FileInputStream(logFile), "]}");
        else
            return Util.addHeaderAndFooter("<?xml version=\"1.0\" encoding=\"UTF-8\"?><logs xmlns=\"http://pisa.intecs.it/mass/toolbox/log\" pagesNumber=\"" + pageNumber + "\">", new FileInputStream(logFile), "</logs>");
    }
}