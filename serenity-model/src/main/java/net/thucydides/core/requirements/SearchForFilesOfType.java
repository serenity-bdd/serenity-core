package net.thucydides.core.requirements;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

import static java.nio.file.FileVisitResult.*;

public class SearchForFilesOfType extends SimpleFileVisitor<Path> {


    private final String suffix;
    Path root;
    List<Path> matchingFiles;
    int maxDepth;

    public SearchForFilesOfType(Path root, String suffix) {
        this.suffix = suffix;
        this.root = root;
        this.matchingFiles = new ArrayList<>();
        this.maxDepth = 0;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (file.getFileName().toString().endsWith(suffix)) {
            matchingFiles.add(file);
            int depth = file.getNameCount() - root.getNameCount() - 1;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return CONTINUE;
    }

    public List<Path> getMatchingFiles() {
        return matchingFiles;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean hasMatchingFiles() {
        return !matchingFiles.isEmpty();
    }
}