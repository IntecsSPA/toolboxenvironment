/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.tbx.lifecycle;

import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author massi
 */
public class CopyDirectoryTask {
     public static void execute(File fromFile,File toFile)
     {
        try {
            toFile.getParentFile().mkdirs();
            IOUtil.copyDirectory(fromFile, toFile);
        } catch (Exception ex) {
            Logger.getLogger(CopyDirectoryTask.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
