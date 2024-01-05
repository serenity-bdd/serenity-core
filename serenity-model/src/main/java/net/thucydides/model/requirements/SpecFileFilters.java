package net.thucydides.model.requirements;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SpecFileFilters {
    private static final String JAVASCRIPT_SPEC_FILE_EXTENSION_PATTERN =
            ".*" +
            "\\.(spec|test|integration|it|e2e|spec\\.e2e|spec-e2e)" +   // Consider only test files...
            "\\.(jsx?|mjsx?|cjsx?|tsx?|mtsx?|ctsx?)$";                  // implemented in either JavaScript or TypeScript

    private static final String JAVASCRIPT_SPEC_FILE_NAME_PATTERN =
            "^(?!.*/(node_modules|jspm_packages|web_modules)/)" +       // Ignore external dependencies
            JAVASCRIPT_SPEC_FILE_EXTENSION_PATTERN;

    private final static Pattern JAVASCRIPT_SPEC_FILE_PATTERN = Pattern.compile(JAVASCRIPT_SPEC_FILE_NAME_PATTERN);

    private static final Set<String> NARRATIVE_FILE_NAMES = new HashSet<>(Set.of(
            "narrative.txt",
            "narrative.md",
            "readme.md",
            "placeholder.txt"
    ));

    public static FilenameFilter javascriptSpecFiles() {
        return (dir, name) -> name.matches(JAVASCRIPT_SPEC_FILE_EXTENSION_PATTERN);
    }

    public static FilenameFilter jbehaveStoryFiles() {
        return (dir, name) -> name.endsWith(".story");
    }

    public static FilenameFilter cucumberFeatureFiles() {
        return (dir, name) -> name.endsWith(".feature");
    }

    /**
     * Used with {@link net.thucydides.model.files.TheDirectoryStructure}
     */
    public static FileFilter thatAreJavascriptSpecFiles() {
        return file -> JAVASCRIPT_SPEC_FILE_PATTERN.matcher(file.getName().toLowerCase()).matches();
    }

    /**
     * Used with {@link net.thucydides.model.files.TheDirectoryStructure}
     */
    public static FileFilter thatAreNarratives() {
        return file -> NARRATIVE_FILE_NAMES.contains(file.getName().toLowerCase());
    }
}
