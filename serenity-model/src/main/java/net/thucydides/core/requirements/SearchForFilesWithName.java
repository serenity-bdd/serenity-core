package net.thucydides.core.requirements;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitResult.CONTINUE;

public class SearchForFilesWithName extends SimpleFileVisitor<Path> {

    Path root;
    List<Path> matchingFiles;
    Pattern pattern;

    public SearchForFilesWithName(Path root, String pathPattern) {
        this.pattern = Pattern.compile(pathPattern);
        this.root = root;
        this.matchingFiles = new ArrayList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (pattern.matcher(file.toString()).matches()) {
            matchingFiles.add(file);
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

    public static SearchForFilesWithName matching(Path root, String pattern) throws IOException {
        SearchForFilesWithName matcher = new SearchForFilesWithName(root,pattern);
        Files.walkFileTree(root, matcher);
        return matcher;
    }

}