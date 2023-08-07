package net.thucydides.core.webdriver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


public class CapabilityValue {

    public static Object asObject(String value) {
        if (isInteger(value))  {
            return Integer.parseInt(value);
        }
        if (isLong(value))  {
            return Long.parseLong(value);
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        if (isAList(handleQuotes(value))) {
            return asList(handleQuotes(value));
        }
        if (isAMap(handleQuotes(value))) {
            return asMap(handleQuotes(value));
        }
        return handleQuotes(value);
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException notAnInt) {
            return false;
        }
    }

    private static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException notALong) {
            return false;
        }
    }

    public static String handleQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static boolean isAList(String value) {
        return value.trim().startsWith("[") && value.trim().endsWith("]");
    }

    private static boolean isAMap(String value) {
        return value.startsWith("{") && value.endsWith("}");
    }

    private static final String COMMAS_OUTSIDE_QUOTES = ",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

    private static List<Object> asList(String value) {
        String listContents = stripCommentsAndEmptyEntriesFrom(StringUtils.removeEnd(StringUtils.removeStart(value.trim(), "["), "]"));
        return stream(listContents.split(COMMAS_OUTSIDE_QUOTES))
                                .map(String::trim)
                                .filter(item -> !(item.isEmpty()))
                                .map(CapabilityValue::asObject)
                                .collect(Collectors.toList());
    }

    private static String stripCommentsAndEmptyEntriesFrom(String value) {
        return stream(value.split("\\r?\\n"))
                .map(item -> removeTrailingCommaFrom(item))
                .filter(line -> !(line.trim().startsWith("#") || line.trim().startsWith("//")))
                .filter(item -> !item.trim().isEmpty())
                .collect(Collectors.joining(","));

    }

    private static String removeTrailingCommaFrom(String item) {
        String trimmed = item.trim();
        return trimmed.endsWith(",") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private static Map<String,Object> asMap(String value) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        try {
            return mapper.readValue(value, typeRef);
        } catch (JsonProcessingException e) {
            throw new CapabilitiesCouldNotBeParsedException("Failed to parse capability value " + value, e);
        }
    }

}
