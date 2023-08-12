package net.thucydides.model.reports.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyDirectory extends SimpleFileVisitor<Path> {

    private Path source;
    private Path target;

    public CopyDirectory(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
            throws IOException {
        if (!Files.exists(target.resolve(source.relativize(file)))) {
            Files.copy(file, target.resolve(source.relativize(file)));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory,
                                             BasicFileAttributes attributes) throws IOException {
        Path targetDirectory = target.resolve(source.relativize(directory));
        try {
            Files.copy(directory, targetDirectory);
        } catch (FileAlreadyExistsException e) {
            if (!Files.isDirectory(targetDirectory)) {
                throw e;
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
