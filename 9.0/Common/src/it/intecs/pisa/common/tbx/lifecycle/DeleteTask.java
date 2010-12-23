/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.tbx.lifecycle;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author massi
 */
public class DeleteTask {
     public static void execute(File fromFile)
     {
        try {
            fromFile.delete();
        } catch (Exception ex) {
            Logger.getLogger(DeleteTask.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
