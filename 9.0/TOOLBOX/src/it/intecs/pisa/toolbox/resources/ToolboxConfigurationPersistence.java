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

import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ToolboxConfigurationPersistence {

    protected static final ToolboxConfigurationPersistence instance = new ToolboxConfigurationPersistence();
    protected Hashtable<String, String> configVariables;
    protected boolean isConfigLoaded = false;

    protected ToolboxConfigurationPersistence() {
        super();
        configVariables = new Hashtable<String, String>();
    }

    public static ToolboxConfigurationPersistence getInstance() {
        return instance;
    }

    protected boolean isConfigurationLoaded() {
        return isConfigLoaded;
    }

    public void loadConfiguration() throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        ResultSet rs=null;

        try {
            if (isConfigLoaded == true) {
                isConfigLoaded = false;
                configVariables.clear();
            }

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();

            rs=stm.executeQuery("SELECT * FROM T_TOOLBOX_CONFIGURATION");
            while(rs.next())
            {
                configVariables.put(rs.getString("NAME"), rs.getString("VALUE"));
                System.out.println("Loaded condfiguration "+rs.getString("NAME")+" "+rs.getString("VALUE"));
            }

            isConfigLoaded = true;
        } finally {
            if(rs!=null)
                rs.close();

            if (stm != null) 
                stm.close();
            
            if (db != null) 
                db.close();
            
        }
    }

     public void saveConfiguration() throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String updtQuery;
        String key;
        String value;
        Enumeration<String> en;
        try {
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();

            stm.executeUpdate("DELETE FROM T_TOOLBOX_CONFIGURATION");

            en=configVariables.keys();

            while(en.hasMoreElements())
            {
                key=en.nextElement();
                value=configVariables.get(key);

                updtQuery="INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+key+"','"+value+"')";
                stm.executeUpdate(updtQuery);
            }

        } finally {
            if (stm != null)
                stm.close();

            if (db != null)
                db.close();

        }
    }

    public String getConfigurationValue(String key)
    {
        return configVariables.get(key);
    }

    public void setConfigurationValue(String key,String value)
    {
        configVariables.put(key, value);
    }
}
