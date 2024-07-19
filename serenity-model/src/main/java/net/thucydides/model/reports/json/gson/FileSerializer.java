package net.thucydides.model.reports.json.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by john on 11/02/15.
 */
public class FileSerializer implements JsonSerializer<File> {
    public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
