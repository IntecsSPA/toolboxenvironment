package it.intecs.pisa.util.file;

import java.io.File;
import java.io.FileFilter;

public class LibsFileFilter implements FileFilter {

    public LibsFileFilter() {
    }

    public boolean accept(File file) {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            return true;
        } else {
            return false;
        }
    }
}
