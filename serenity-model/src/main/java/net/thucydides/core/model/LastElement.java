package net.thucydides.core.model;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.thucydides.core.model.LastElement.LastElementStrategy.forFeatureOrStoryFiles;
import static net.thucydides.core.model.LastElement.LastElementStrategy.forTestCases;

public class LastElement {

    public static String of(String path) {
        return LAST_ELEMENT_FINDER.get(forPathType(path)).lastElementIn(path);
    }

    private static LastElementStrategy forPathType(String path) {
        if (path != null && path.toLowerCase().endsWith(".feature")) {
            return forFeatureOrStoryFiles;
        }
        if (path != null && path.toLowerCase().endsWith(".story")) {
            return forFeatureOrStoryFiles;
        }
        return forTestCases;
    }

    interface LastElementFinder {
        String lastElementIn(String path);
    }

    enum LastElementStrategy {
        forTestCases, forFeatureOrStoryFiles
    }

    static Map<LastElementStrategy, LastElementFinder> LAST_ELEMENT_FINDER = new HashMap();
    static {
        LAST_ELEMENT_FINDER.put(forTestCases, new LastElementOfATestCase());
        LAST_ELEMENT_FINDER.put(forFeatureOrStoryFiles, new LastElementOfAFeatureOrStoryFile());
    }

    static class LastElementOfAFeatureOrStoryFile implements LastElementFinder {

        @Override
        public String lastElementIn(String path) {
            List<String> pathElements = new ArrayList<>(elementsOfFeatureOrStory(withoutFeatureFileSuffixes(path)));
            if (pathElements.isEmpty()) { return ""; }

            pathElements.remove(pathElements.size() - 1);
            return (pathElements.isEmpty()) ? "" : pathElements.get(pathElements.size() - 1);
        }
    }

    static class LastElementOfATestCase implements LastElementFinder {

        @Override
        public String lastElementIn(String path) {
            List<String> pathElements = elementsOfTestCase(path);
            return (pathElements.isEmpty()) ? "" :  pathElements.get(pathElements.size() - 1);
        }
    }

    private static final Pattern BACKSLASH = Pattern.compile("\\.");
    private static List<String> elementsOfTestCase(String path) {
        if (path == null) {
            return new ArrayList<>();
        }
        return  Splitter.on(BACKSLASH).splitToList(path);
    }

    private static List<String> elementsOfFeatureOrStory(String path) {
        if (path == null) {
            return new ArrayList<>();
        }
        return  Splitter.on(Pattern.compile("/")).splitToList(path);
    }


    private static String withoutFeatureFileSuffixes(String path) {
        if (path.endsWith(".feature")) {
            return path.substring(0, path.length() - 8);
        }
        if (path.endsWith(".story")) {
            return path.substring(0, path.length() - 6);
        }
        return path;
    }
}

