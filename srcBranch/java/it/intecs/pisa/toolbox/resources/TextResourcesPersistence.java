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

import it.intecs.pisa.toolbox.db.ResourceSequence;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import org.w3c.dom.Document;

/**
 *
 * @author massi
 */
public class TextResourcesPersistence {
    protected File storeDirectory;
    protected static final TextResourcesPersistence instance=new TextResourcesPersistence();

    protected TextResourcesPersistence()
    {

    }

    public static TextResourcesPersistence getInstance()
    {
        return instance;
    }

    public void setDirectory(File dir)
    {
        storeDirectory=dir;
    }

    public synchronized String storeText(String text) throws Exception
    {
        File resFile;
        long id;
        try
        {
            id=ResourceSequence.getNext();
            resFile=getSubDirForId(id);

            PrintWriter tWriter;
            tWriter=new PrintWriter(new FileOutputStream(resFile));
            tWriter.print(text);
            tWriter.close();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot store Text document",e);
        }

        return Long.toString(id);
    }

    public long getNewResourceFile() throws Exception
    {
        long id;

        try
        {
            id=ResourceSequence.getNext();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot obtain a new resource file",e);
        }

        return id;
    }

    public void deleteText(String id) throws Exception {
        File resFile;
        Document doc=null;
        DOMUtil util;

        try
        {
            util=new DOMUtil();

            resFile=getSubDirForId(Long.parseLong(id));
            resFile.delete();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot delete Text resource",e);
        }
    }

    protected File getSubDirForId(long id)
    {
        int subDir;
        File resFile;
        String fullId;

        subDir=(int)(id/10000);
        fullId=subDir+"/"+Long.toString(id)+".txt";

        resFile=new File(storeDirectory,fullId);
        resFile.getParentFile().mkdirs();

        return resFile;
    }

    public File getTextFile(long forkedId) {
        return getSubDirForId(forkedId);
    }

    public void deleteOlderThan(Date treshold)
    {
        IOUtil.deleteOlderThan(storeDirectory, treshold);
    }
}
