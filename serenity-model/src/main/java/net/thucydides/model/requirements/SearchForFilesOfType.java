package net.thucydides.model.requirements;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitResult.CONTINUE;

public class SearchForFilesOfType extends SimpleFileVisitor<Path> {


    private final Pattern pattern;
    Path root;
    List<Path> matchingFiles;
    int maxDepth;

    public SearchForFilesOfType(Path root, String suffix) {
        this(root, Pattern.compile(".*" + Pattern.quote(suffix) + "$"));
    }

    public SearchForFilesOfType(Path root, Pattern pattern) {
        this.pattern = pattern;
        this.root = root;
        this.matchingFiles = new ArrayList<>();
        this.maxDepth = 0;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        Matcher matcher = pattern.matcher(file.getFileName().toString());

        if (matcher.matches()) {
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
