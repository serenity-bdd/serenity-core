package net.thucydides.core.files;

import com.beust.jcommander.internal.Lists;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class TheDirectoryStructure {
    private final File rootDirectory;

    public TheDirectoryStructure(File rootDirectory) {

        this.rootDirectory = rootDirectory;
    }

    public static TheDirectoryStructure startingAt(File rootDirectory) {
        return new TheDirectoryStructure(rootDirectory);
    }

    public boolean containsFiles(FileFilter... fileFilters) {
        for(File file : nestedFilesIn(rootDirectory)) {
            for(FileFilter fileFilter : fileFilters) {
                if (fileFilter.accept(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<File> nestedFilesIn(File directory) {
        if (!directory.isDirectory()) {
            return Lists.newArrayList();
        }

        List<File> nestedFiles = Lists.newArrayList();
        for(File file : directory.listFiles()) {
            if (file.isDirectory()) {
                nestedFiles.addAll(nestedFilesIn(file));
            } else {
                nestedFiles.add(file);
            }
        }
        return nestedFiles;
    }
}
