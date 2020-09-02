package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CapabilityValue {

    public static Object asObject(String value) {
        if (StringUtils.isNumeric(value))  {
            return Integer.parseInt(value);
        }
        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.parseBoolean(value);
        }
        if (isAList(stripQuotesFrom(value))) {
            return asList(stripQuotesFrom(value));
        }
        if (isAMap(stripQuotesFrom(value))) {
            return asMap(stripQuotesFrom(value));
        }
        return value;
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
        Gson gson = new Gson();
        return gson.fromJson(value, Map.class);
    }

}
