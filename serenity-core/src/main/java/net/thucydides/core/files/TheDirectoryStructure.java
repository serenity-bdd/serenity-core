package net.thucydides.core.files;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
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

    public int maxDepth() {

        if (!rootDirectory.isDirectory()) { return 0; }

        Collection<File> directoryContents = FileUtils.listFilesAndDirs(rootDirectory,
                                   new NotFileFilter(TrueFileFilter.INSTANCE),
                                   normalDirectoriesOnly());

        int maxDepth = 0;
        for(File file : directoryContents) {
            String relativePath = file.getPath().replace(rootDirectory.getPath(),"");
            int depth = Splitter.on(File.separator).trimResults().splitToList(relativePath).size() - 1;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        return maxDepth;
    }

    private IOFileFilter normalDirectoriesOnly() {
        return new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory() && !name.startsWith(".");
            }
        };
    }
}
