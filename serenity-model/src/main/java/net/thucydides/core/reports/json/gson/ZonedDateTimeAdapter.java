package net.thucydides.core.reports.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeAdapter implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
        try {

            // if provided as String - '2011-12-03T10:15:30+01:00[Europe/Paris]'
            if (jsonPrimitive.isString()) {
                return ZonedDateTime.parse(jsonPrimitive.getAsString());
            }

            // if provided as Long
            if (jsonPrimitive.isNumber()) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
            }

        } catch (RuntimeException e) {
            throw new JsonParseException("Unable to parse ZonedDateTime", e);
        }
        throw new JsonParseException("Unable to parse ZonedDateTime");
    }


    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
