package net.thucydides.core.reports;

import com.beust.jcommander.internal.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CopyFiles {
    private final File sourceDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyFiles.class);

    public CopyFiles(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public static CopyFiles from(File sourceDirectory) {
        return new CopyFiles(sourceDirectory);
    }

    public void to(File outputDirectory) {
        if (sourceDirectoryIsTheSameAs(outputDirectory)) {
            return;
        }

        final Path sourcePath = Paths.get(sourceDirectory.toURI());
        final Path targetDirectory = Paths.get(outputDirectory.toURI());

        List<Path> filesToCopy = filesToCopyBetween(sourcePath, targetDirectory);
        int numberOfThreads = NumberOfThreads.forIOOperations();
        final List<Callable<Path>> partitions = Lists.newArrayList();

        for (Path fileToCopy : filesToCopy) {
            partitions.add(new FileCopier(fileToCopy, targetDirectory));
        }

        final ExecutorService executorPool = Executors.newFixedThreadPool(numberOfThreads);
        try {
            final List<Future<Path>> copiedFiles = executorPool.invokeAll(partitions);
            for (Future<Path> copiedFile : copiedFiles) {
                copiedFile.get();
            }
        } catch (Exception e) {
            LOGGER.error("Error during copying files to the target directory", e);
        }
        executorPool.shutdown();

    }


    private boolean sourceDirectoryIsTheSameAs(File outputDirectory) {
        return (outputDirectory == null) || (outputDirectory.equals(sourceDirectory));
    }


    private List<Path> filesToCopyBetween(Path sourcePath, Path targetPath) {
        List<Path> filesToCopy = Lists.newArrayList();
        try (DirectoryStream<Path> directoryContents = Files.newDirectoryStream(sourcePath)) {
            for (Path sourceFile : directoryContents) {
                Path destinationFile = targetPath.resolve(sourceFile.getFileName());
                if (Files.notExists(destinationFile)) {
                    filesToCopy.add(sourceFile);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error during copying files to the target directory", e);
        }
        return filesToCopy;
    }
}
