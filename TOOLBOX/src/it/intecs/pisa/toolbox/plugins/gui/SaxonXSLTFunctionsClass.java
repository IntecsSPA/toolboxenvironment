/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.pluginscore.InterfacePluginManager;

/**
 *
 * @author massi
 */
public class SaxonXSLTFunctionsClass {
    public static String getInterfaces(String interfaceType)
    {
        try {
            InterfacePluginManager interfman;
            interfman = InterfacePluginManager.getInstance();
            Interface[] interfaces = interfman.getInterfaces(interfaceType);

            String value="[";
            for(Interface interf:interfaces)
            {
                String interfaceName,interfaceVersion;

                interfaceName=interf.getName();
                interfaceVersion=interf.getVersion();
                value+="['"+interfaceName+" version "+interfaceVersion+
                        "','"+interfaceType+"','"+interfaceName+"','"+interfaceVersion+
                        "','rest/manager/getInterfaceModes/"+interfaceType+"/"+interfaceName+"/"+
                        interfaceVersion+".json']";
            }

            return value+"]";

        } catch (Exception ex) {
           return "";
        }


    }
}
