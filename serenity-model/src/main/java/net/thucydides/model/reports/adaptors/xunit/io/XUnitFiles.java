package net.thucydides.model.reports.adaptors.xunit.io;

import java.io.File;
import java.io.FilenameFilter;

public class XUnitFiles {
    public static File[] in(File directory) {
        return directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
    }
}
