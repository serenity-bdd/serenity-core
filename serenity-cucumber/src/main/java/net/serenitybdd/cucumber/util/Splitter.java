package net.serenitybdd.cucumber.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Splitter {
    private String separator;
    private boolean omitEmptyStrings = false;
    private boolean trimResults = false;
    private String trimmable = null;

    public Splitter(String separator) {
        this.separator = separator;
    }

    public static Splitter on(String separator) {
        return new Splitter(separator);
    }

    public Splitter omitEmptyStrings() {
        omitEmptyStrings = true;
        return this;
    }

    public Splitter trimResults() {
        this.trimResults = true;
        return this;
    }

    public Splitter trimResults(String trimmable) {
        this.trimResults = true;
        this.trimmable = trimmable;
        return this;
    }

    public List<String> splitToList(String value) {
        String[] separatedElements = StringUtils.split(value, separator);
        List<String> result = Arrays.asList(separatedElements);

        if (omitEmptyStrings) {
            result = result.stream()
                    .filter(element -> !element.trim().equals(""))
                    .collect(Collectors.toList());
        }

        if (trimResults) {
            result = result.stream()
                    .map(v -> StringUtils.strip(v, trimmable))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static Splitter on(char separator) {
        return on(Character.toString(separator));
    }

}
