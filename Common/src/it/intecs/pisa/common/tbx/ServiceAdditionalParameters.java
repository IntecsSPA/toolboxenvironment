/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Massimiliano
 */
public class ServiceAdditionalParameters {

    protected Properties props;

    public ServiceAdditionalParameters(File dir, String parSetId) {
        File parametersFile;

        try {
            parametersFile = new File(dir, parSetId + ".properties");

            props = new Properties();
            props.load(new FileInputStream(parametersFile));
        } catch (Exception e) {
        }

    }
    
     public ServiceAdditionalParameters(File properties) {
        try {
            props = new Properties();
            props.load(new FileInputStream(properties));
        } catch (Exception e) {
        }

    }
    
    public String getParameter(String key)
    {
        return props.getProperty(key);
    }
}
