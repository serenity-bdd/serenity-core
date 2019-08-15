package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CapabilityValue {

    public static Object asObject(String value) {
        if (StringUtils.isNumeric(value))  {
            return Integer.parseInt(value);
        }
        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.parseBoolean(value);
        }
        if (isAList(value)) {
            return asList(value);
        }
        return value;
    }

    private static boolean isAList(String value) {
        return value.startsWith("[") && value.endsWith("]");
    }

    private static List<Object> asList(String value) {
        String listContents = StringUtils.removeEnd(StringUtils.removeStart(value, "["), "]");
        List<String> items = Splitter.on(",").trimResults().splitToList(listContents);
        return items.stream()
                .map(CapabilityValue::asObject)
                .collect(Collectors.toList());
    }
}
