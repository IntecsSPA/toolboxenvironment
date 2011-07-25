/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.configuration;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ToolboxNetwork {
    public static String getEndpointURL()
    {
        ToolboxConfiguration instance = ToolboxConfiguration.getInstance();

        return "http://"+instance.getConfigurationValue(ToolboxConfiguration.ENDPOINT_ADDRESS)+
                ":"+instance.getConfigurationValue(ToolboxConfiguration.ENDPOINT_PORT)+
                "/"+instance.getApplicationName();
    }

    public static String getTOOLBOXEndpointURL()
    {
        ToolboxConfiguration instance = ToolboxConfiguration.getInstance();

        return "http://"+instance.getConfigurationValue(ToolboxConfiguration.ENDPOINT_ADDRESS)+
                ":"+instance.getConfigurationValue(ToolboxConfiguration.ENDPOINT_PORT)+
                "/"+instance.getApplicationName();
    }
    
   
}
