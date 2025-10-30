package net.thucydides.model.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class TheDirectoryStructure {
    private final Path rootDirectory;

    private final static Logger LOGGER = LoggerFactory.getLogger(TheDirectoryStructure.class);

    public TheDirectoryStructure(File rootDirectory) {
        this.rootDirectory = Paths.get(rootDirectory.getPath().replace("\\","/"));
    }

    public static TheDirectoryStructure startingAt(File rootDirectory) {
        return new TheDirectoryStructure(rootDirectory);
    }

    public boolean containsFiles(FileFilter... fileFilters) {
        for(File file : nestedFilesIn(rootDirectory.toFile())) {
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
            return new ArrayList<>();
        }

        List<File> nestedFiles = new ArrayList<>();
        for(File file : directory.listFiles()) {
            if (file.isDirectory()) {
                nestedFiles.addAll(nestedFilesIn(file));
            } else {
                nestedFiles.add(file);
            }
        }
        return nestedFiles;
    }

    private static final Pattern FILE_PATH_SEPARATORS = Pattern.compile("\\/");
    public int maxDepth() {
        LOGGER.trace("Calculating maximum directory depth for requirements hierarchy at: {} ({})", rootDirectory, rootDirectory.toFile().getAbsolutePath());

        if (!rootDirectory.toFile().isDirectory()) { return 0; }

        Collection<File> directoryContents = FileUtils.listFilesAndDirs(rootDirectory.toFile(),
                                                                        new NotFileFilter(TrueFileFilter.INSTANCE),
                                                                        normalDirectoriesOnly());

        int maxDepth = directoryContents.stream()
                .map(fileInDirectory -> rootDirectory.relativize(fileInDirectory.toPath()))
                .mapToInt(this::numberOfElementsIn)
                .max()
                .orElse(0);

        LOGGER.trace("Max depth = {}", maxDepth);
        return maxDepth;
    }

    private int numberOfElementsIn(Path relativePath) {
        return relativePath.toString().split(Pattern.quote(File.separator)).length;
        //return Splitter.on(FILE_PATH_SEPARATORS).trimResults().omitEmptyStrings().splitToList(relativePath.toFile().getPath()).size();
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
