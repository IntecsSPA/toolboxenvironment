package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.w3c.dom.Element;

public class ZipTag extends NativeTagExecutor {
    protected String tagName="zip";
    @Override
    public Object executeTag(org.w3c.dom.Element zipElement) throws Exception {
          Element elemToBeZipped, file;
        String fileStr, elemToBeZippedStr;
        String tagName;
        String[] dirListing;

        elemToBeZipped = DOMUtil.getFirstChild(zipElement);
        tagName = elemToBeZipped.getTagName();

        elemToBeZippedStr = (String) this.executeChildTag(DOMUtil.getFirstChild(elemToBeZipped));

        fileStr = (String) this.engine.evaluateString(zipElement.getAttribute( "file"),IEngine.EngineStringType.ATTRIBUTE);
        String filter = (String)this.engine.evaluateString(zipElement.getAttribute("filter"),IEngine.EngineStringType.ATTRIBUTE);

        if (tagName.equals("dir")) {
            //parsing elemToBeZipped
            if (filter != null) {
                dirListing = parseDir(elemToBeZippedStr, filter);
            } else {
                dirListing = parseDir(elemToBeZippedStr);
            }
        } else {
            File tmpFile;

            tmpFile = new File(elemToBeZippedStr);

            dirListing = new String[]{tmpFile.getName()};
            elemToBeZippedStr = tmpFile.getParentFile().getAbsolutePath();
        }

        zipFiles(elemToBeZippedStr, dirListing, fileStr);

        addResourceLinkToDebugTree(new File(fileStr));
        return fileStr;
    }
    
  private String[] parseDir(String dir) {
        String[] dirListing;
        Vector fullDirListing;
        File directory,
                subItem;

        fullDirListing = new Vector();

        directory =
                new File(dir);
        dirListing = directory.list();
        for (int i = 0; i < dirListing.length; i++) {
            subItem = new File(dir, dirListing[i]);

            if (subItem.isDirectory() == true) {
                String[] subDirItems;

                subDirItems = parseDir(dir + File.separator + dirListing[i]);

                for (int j = 0; j < subDirItems.length; j++) {
                    fullDirListing.add(dirListing[i] + File.separator + subDirItems[j]);
                }
            } else {
                fullDirListing.add(dirListing[i]);
            }

        }

        String[] completelist;

        completelist = new String[fullDirListing.size()];

        for (int i = 0; i < completelist.length; i++) {
            completelist[i] = (String) fullDirListing.get(i);
        }
        return completelist;

    }

    private String[] parseDir(String dir, String filter) {
        String[] dirListing;
        Vector fullDirListing;
        File directory,
                subItem;

        fullDirListing = new Vector();

        directory =
                new File(dir);
        dirListing = directory.list();
        for (int i = 0; i < dirListing.length; i++) {
            subItem = new File(dir, dirListing[i]);
            if (subItem.isDirectory() == true) {
                String[] subDirItems;
                subDirItems = parseDir(dir + File.separator + dirListing[i], filter);
                for (int j = 0; j < subDirItems.length; j++) {
                    String fulPath = dirListing[i] + File.separator + subDirItems[j];
                    if (fulPath.indexOf(filter) != -1) {
                        fullDirListing.add(fulPath);
                    }
                }
            } else {
                if (subItem.getAbsolutePath().indexOf(filter) != -1) {
                    fullDirListing.add(dirListing[i]);
                }
            }
        }
        String[] completelist;

        completelist = new String[fullDirListing.size()];

        for (int i = 0; i < completelist.length; i++) {
            completelist[i] = (String) fullDirListing.get(i);
        }
        return completelist;

    }

    private void zipFiles(String workingDir, String[] entries, String outputFileName) throws Exception {

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFileName));

            // Compress the files
            for (int i = 0; i < entries.length; i++) {
                String fullpath;

                File f = new File(workingDir, entries[i]);
                fullpath = f.getAbsolutePath();

                FileInputStream in = new FileInputStream(fullpath);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(entries[i]));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }

    }
}
