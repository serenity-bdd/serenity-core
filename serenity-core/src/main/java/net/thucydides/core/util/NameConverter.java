package net.thucydides.core.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Utility class to convert test case and method names into human-readable form.
 *
 * @author johnsmart
 */
public final class NameConverter {

    private static final String INDEXED_METHOD_NAME = ".*\\[\\d+]";
    private static final String[] abbreviations = {"CSV", "XML", "JSON"};

    private NameConverter() {
    }

    /**
     * Converts a class or method name into a human-readable sentence.
     *
     * @param name a class or method name
     * @return the human-readable form
     */
    public static String humanize(final String name) {

        if ((name == null) || (name.trim().length() == 0)) {
            return "";
        }
        if (name.contains(" ") && !thereAreParametersIn(name)) {
            return name;
        } else if (thereAreParametersIn(name)) {
            return humanizeNameWithParameters(name);
        } else {

            String noUnderscores = name.replaceAll("_", " ");
            String splitCamelCase = splitCamelCase(noUnderscores);

            Set<Acronym> acronyms = Acronym.acronymsIn(splitCamelCase);
            String capitalized = StringUtils.capitalize(splitCamelCase);
            for(Acronym acronym : acronyms) {
                capitalized = acronym.restoreIn(capitalized);
            }
            return restoreAbbreviations(capitalized);
        }
    }

    private static String restoreAbbreviations(final String sentence){
        String processing = sentence;
        for(String abbreviation: abbreviations){
            processing = processing.replaceAll(StringUtils.capitalize(abbreviation), abbreviation);
        }
        return processing;
    }

    private static String humanizeNameWithParameters(final String name) {
        int parametersStartAt = name.indexOf(": ");
        String bareName = name.substring(0, parametersStartAt);
        String humanizedBareName = humanize(bareName);
        String parameters = name.substring(parametersStartAt);
        return humanizedBareName + parameters;
    }

    private static boolean thereAreParametersIn(final String name) {
        return name.contains(": ");
    }

    /**
     * Inserts spaces between words in a CamelCase name.
     *
     * @param name a name in camel-case
     * @return the name with spaces instead of underscores
     */
    public static String splitCamelCase(final String name) {
        List<String> splitWords = new ArrayList<>();

        List<String> phrases = Splitter.on(" ").omitEmptyStrings().splitToList(name);

        for(String phrase : phrases) {
            splitWords.addAll(splitWordsIn(phrase));
        }

        String splitPhrase = Joiner.on(" ").join(splitWords);
        return splitPhrase.trim();
    }

    private static List<String> splitWordsIn(String phrase) {

        List<String> splitWords = new ArrayList<>();

        String currentWord = "";
        for (int index = 0; index < phrase.length(); index++) {
            if (onWordBoundary(phrase, index)) {
                splitWords.add(lowercaseOrAcronym(currentWord));
                currentWord = String.valueOf(phrase.charAt(index));
            } else {
                currentWord = currentWord + (phrase.charAt(index));
            }
        }
        splitWords.add(lowercaseOrAcronym(currentWord));

        return splitWords;
    }

    private static String lowercaseOrAcronym(String word) {
        if (Acronym.isAnAcronym(word)) {
            return word;
        } else {
            return StringUtils.lowerCase(word);
        }
    }

    private static boolean onWordBoundary(String name, int index) {
        return (uppercaseLetterAt(name, index)
                && (lowercaseLetterAt(name, index - 1) || lowercaseLetterAt(name, index + 1)));
    }

    private static boolean uppercaseLetterAt(String name, int index) {
        return CharUtils.isAsciiAlphaUpper(name.charAt(index));
    }

    private static boolean lowercaseLetterAt(String name, int index) {
        return (index >= 0)
                && (index < name.length())
                && CharUtils.isAsciiAlphaLower(name.charAt(index));
    }

    public static String withNoArguments(final String methodName) {
        return stripArgumentsFrom(stripIndexesFrom(methodName));
    }

    public static String withNoIssueNumbers(final String methodName) {
        if (methodName == null) {
            return null;
        }
        int firstIssueNumberIndex = methodName.indexOf("_(#");
        if (firstIssueNumberIndex == -1) {
            firstIssueNumberIndex = methodName.indexOf("(#");
        }
        if (firstIssueNumberIndex == -1) {
            firstIssueNumberIndex = methodName.indexOf("#");
        }
        if (firstIssueNumberIndex > 0) {
            return methodName.substring(0, firstIssueNumberIndex);
        } else {
            return methodName;
        }
    }

    public static String stripArgumentsFrom(final String methodName) {
        if (methodName == null) {
            return null;
        }
        int firstArgument = methodName.indexOf(':');
        if (firstArgument > 0) {
            return methodName.substring(0, firstArgument);
        } else {
            return methodName;
        }
    }


    public static String stripIndexesFrom(final String methodName) {
        if (methodName == null) {
            return null;
        }
        return (methodName.matches(INDEXED_METHOD_NAME)) ? methodName.substring(0, methodName.lastIndexOf('[')) : methodName;
    }

    /**
     * Transform a camel-case word to underscored-version.
     *
     * @param name name to be converted
     * @return a name with underscore separators
     */
    public static String underscore(final String name) {
        if (name != null) {
            return name.replaceAll(" ", "_")
                    .replaceAll("<", "_lt_")
                    .replaceAll(">", "underscore_gt_")
                    .replaceAll("'", "_sq_")
                    .replaceAll("\"", "_dq_")
                    .replaceAll(",", "_c_")
                    .replaceAll(":", "_cl_")
                    .replaceAll(";", "_sc_")
                    .replaceAll("/", "_sl_")
                    .replaceAll("=", "_eq_")
                    .toLowerCase(Locale.getDefault()).trim();
        } else {
            return "";
        }
    }

    private final static Map<Character, String> EXCLUDE_FROM_FILENAMES = new HashMap<>();
    static {
        EXCLUDE_FROM_FILENAMES.put('$', "_");
        EXCLUDE_FROM_FILENAMES.put('/', "_");
        EXCLUDE_FROM_FILENAMES.put('\\', "_");
        EXCLUDE_FROM_FILENAMES.put(':', "_");
        EXCLUDE_FROM_FILENAMES.put(';', "_");
        EXCLUDE_FROM_FILENAMES.put('<', "_lt_");
        EXCLUDE_FROM_FILENAMES.put('>', "_gt_");
        EXCLUDE_FROM_FILENAMES.put('[', "_obr_");
        EXCLUDE_FROM_FILENAMES.put(']', "_cbr_");
        EXCLUDE_FROM_FILENAMES.put('{', "_obrc_");
        EXCLUDE_FROM_FILENAMES.put('}', "_cbrc_");
        EXCLUDE_FROM_FILENAMES.put('*', "_star_");
        EXCLUDE_FROM_FILENAMES.put('^', "_caret_");
        EXCLUDE_FROM_FILENAMES.put('%', "_per_");
        EXCLUDE_FROM_FILENAMES.put('"', "_quote_");
        EXCLUDE_FROM_FILENAMES.put('?', "_question_");
        EXCLUDE_FROM_FILENAMES.put('|', "_pipe_");
        EXCLUDE_FROM_FILENAMES.put('&', "_amp_");
        EXCLUDE_FROM_FILENAMES.put(',', "_comma_");
        EXCLUDE_FROM_FILENAMES.put('=', "_equals_");
        EXCLUDE_FROM_FILENAMES.put('\'', "_");
        EXCLUDE_FROM_FILENAMES.put('\"', "_");
        EXCLUDE_FROM_FILENAMES.put('@', "_at_");
        EXCLUDE_FROM_FILENAMES.put('#', "_hash_");
        EXCLUDE_FROM_FILENAMES.put('+', "_plus_");
        EXCLUDE_FROM_FILENAMES.put(' ', "_");
    }

    public static String filesystemSafe(final String name) {
        if (name == null) { return name; }

        String safeName = name.trim();
        for(Character substitutableChar : EXCLUDE_FROM_FILENAMES.keySet()) {
            safeName = StringUtils.replace(safeName, substitutableChar.toString(), EXCLUDE_FROM_FILENAMES.get(substitutableChar));
        }
        return safeName.toLowerCase();
    }

}
