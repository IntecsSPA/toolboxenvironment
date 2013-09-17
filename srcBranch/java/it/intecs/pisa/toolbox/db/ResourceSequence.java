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

package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ResourceSequence {
    protected static long id=0;
    protected static boolean inited=false;

    public static long getNext() throws Exception
    {
        /*try
        {
            return getNextUntrapped();
        }
        catch(Exception e)
        {
            //don't know why but it seems that sequence is not aligned when one element is deleted
            return getNextUntrapped();
        }*/

        if(inited==false)
            init();

        return id++;
    }

    protected static long getNextUntrapped() throws Exception
    {
        String sql = "SELECT NEXT VALUE FOR SEQ_ITEM_ID AS ID FROM T_SERVICE_INSTANCES";
        ToolboxInternalDatabase db = ToolboxInternalDatabase.getInstance();
        Statement stm = db.getStatement();
        ResultSet rs = stm.executeQuery(sql);
        rs.next();

        return rs.getLong("ID");
    }

    private static void init() throws Exception {
        String sql = "SELECT LIMIT 0 1 ID FROM T_INSTANCES_RESOURCES ORDER BY ID DESC";
        ToolboxInternalDatabase db = ToolboxInternalDatabase.getInstance();
        Statement stm = db.getStatement();
        ResultSet rs = stm.executeQuery(sql);

        if(rs.next()==true)
           id=rs.getLong("ID");
        else id=0;

        inited=true;
    }
}
