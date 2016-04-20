package net.serenitybdd.rest;

import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**
 * User: YamStranger
 * Date: 4/15/16
 * Time: 9:14 AM
 */
public class JsonConverter {
    public static String formatted(final String body) {
        if (StringUtils.isEmpty(body)) {
            return "";
        } else {
            return new JsonParser().parse(body).toString();
        }
    }
}
