package net.thucydides.core.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static net.thucydides.core.reports.html.HtmlAggregateStoryReporter.COPY_OPTIONS;

class FileCopier implements Callable<Path> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileCopier.class);

    private final Path sourcePath;
        private final Path targetDirectory;

        FileCopier(Path sourcePath, Path targetDirectory) {
            this.sourcePath = sourcePath;
            this.targetDirectory = targetDirectory;
        }

        @Override
        public Path call() throws Exception {
            Path destinationFile = targetDirectory.resolve(sourcePath.getFileName());
            try {
                return Files.copy(sourcePath, destinationFile, COPY_OPTIONS);
            } catch (IOException e) {
                LOGGER.error("Error during copying files to the target directory", e);
                return null;
            }
        }
    }