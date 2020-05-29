package net.thucydides.core.util;

import com.google.common.base.Splitter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

class Acronym {
    private final String acronymText;
    private final int start;
    private final int end;

    Acronym(String acronym, int start, int end) {
        this.acronymText = acronym;
        this.start = start;
        this.end = end;
    }

    public static Set<Acronym> acronymsIn(String text) {
        Set<Acronym> acronyms = new HashSet();

        List<String> words = Splitter.on(Pattern.compile("\\W")).omitEmptyStrings().splitToList(text);
        for (String word : words) {
            if (isAnAcronym(word)) {
                acronyms.addAll(appearencesOf(word, text));
            }
        }
        return acronyms;
    }

    public String restoreIn(String text) {
        String prefix = (start > 0) ? text.substring(0, start) : "";
        String suffix = text.substring(end, text.length());
        return prefix + acronymText + suffix;
    }

    private static Set<Acronym> appearencesOf(String word, String text) {
        Set<Acronym> acronyms = new HashSet();

        int startAt = 0;
        while(startAt < text.length()) {
            int wordFoundAt = text.indexOf(word, startAt);
            if (wordFoundAt == -1) { break; }

            acronyms.add(new Acronym(word, wordFoundAt, wordFoundAt + word.length()));
            startAt = startAt + word.length();
        }
        return acronyms;
    }

    public static boolean isAnAcronym(String word) {
        return (word.length() > 1) && Character.isUpperCase(firstLetterIn(word)) && Character.isUpperCase(lastLetterIn(word));
    }

    private static char firstLetterIn(String word) {
        String wordWithoutDigits = word.replaceAll("\\d", "");
        return wordWithoutDigits.length() == 0 ? word.charAt(0) : wordWithoutDigits.charAt(0);
    }

    private static char lastLetterIn(String word) {
        String wordWithoutDigits = word.replaceAll("\\d", "");
        return wordWithoutDigits.length() == 0 ? word.charAt(word.length() - 1) : wordWithoutDigits.charAt(wordWithoutDigits.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acronym acronym = (Acronym) o;
        return start == acronym.start &&
                end == acronym.end &&
                Objects.equals(acronymText, acronym.acronymText);
    }

    @Override
    public int hashCode() {
        int result = acronymText != null ? acronymText.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Acronym{");
        sb.append("acronymText='").append(acronymText).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
}