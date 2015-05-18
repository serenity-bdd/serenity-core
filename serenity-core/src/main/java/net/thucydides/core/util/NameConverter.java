package net.thucydides.core.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Utility class to convert test case and method names into human-readable form.
 * 
 * @author johnsmart
 *
 */
public final class NameConverter {

    private static final String INDEXED_METHOD_NAME = ".*\\[\\d+]";

    private NameConverter() {}

    /**
     * Converts a class or method name into a human-readable sentence.
     * @param name a class or method name
     * @return the human-readable form
     */
    public static String humanize(final String name) {
        if ((name == null) || (name.trim().length() == 0)) {
            return "";
        }

        if (name.contains(" ") && !thereAreParametersIn(name)) {
            return name;
        } else if (thereAreParametersIn(name)){
            return humanizeNameWithParameters(name);
        } else {
            String noUnderscores = name.replaceAll("_", " ");
            String splitCamelCase = splitCamelCase(noUnderscores);
            return StringUtils.capitalize(splitCamelCase);
        }
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
     * @param name a name in camel-case
     * @return the name with spaces instead of underscores
     */
    public static String splitCamelCase(final String name) {
        StringBuffer splitWords = new StringBuffer();

        // AbcDef
        boolean inWord = false;
        String currentWord = "";
        for(int index = 0; index < name.length(); index++) {
            if (onWordBoundary(name, index)) {
                splitWords.append(lowercaseOrAcronym(currentWord)).append(" ");
                currentWord = String.valueOf(name.charAt(index));
            } else {
                currentWord = currentWord + (name.charAt(index));
            }
        }
        splitWords.append(lowercaseOrAcronym(currentWord));

        return splitWords.toString().trim();
    }

    private static String lowercaseOrAcronym(String word) {
        if (StringUtils.isAllUpperCase(word) && word.length() > 1) {
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

    public static String stripArgumentsFrom(final String methodName)  {
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


    public static String stripIndexesFrom(final String methodName)  {
        if (methodName == null) {
            return null;
        }
        return (methodName.matches(INDEXED_METHOD_NAME)) ?  methodName.substring(0, methodName.lastIndexOf('[')) :  methodName;
    }

    /**
     * Transform a camel-case word to underscored-version.
     * @param name name to be converted
     * @return a name with underscore separators
     */
    public static String underscore(final String name) {
        if (name != null) {
            return name.replaceAll(" ", "_")
                    .replaceAll("<","_")
                    .replaceAll(">","_")
                    .replaceAll("'","_")
                    .replaceAll(",","_")
                    .replaceAll(":","_")
                    .replaceAll("/","_")
                    .replaceAll("\"","_")
                    .replaceAll("=","_")
                        .toLowerCase(Locale.getDefault()).trim();
        } else {
            return "";
        }
    }

}
