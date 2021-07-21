package net.thucydides.core.webdriver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        if (isAList(stripQuotesFrom(value))) {
            return asList(stripQuotesFrom(value));
        }
        if (isAMap(stripQuotesFrom(value))) {
            return asMap(stripQuotesFrom(value));
        }
        return stripQuotesFrom(value);
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

    private static String stripQuotesFrom(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static boolean isAList(String value) {
        return value.startsWith("[") && value.endsWith("]");
    }

    private static boolean isAMap(String value) {
        return value.startsWith("{") && value.endsWith("}");
    }

    private static List<Object> asList(String value) {
        String listContents = StringUtils.removeEnd(StringUtils.removeStart(value, "["), "]");
        List<String> items = Splitter.on(",").trimResults().splitToList(listContents);
        return items.stream()
                .map(CapabilityValue::asObject)
                .collect(Collectors.toList());
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
