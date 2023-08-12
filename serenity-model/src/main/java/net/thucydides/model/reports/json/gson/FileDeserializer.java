package net.thucydides.model.reports.json.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by john on 11/02/15.
 */
public class FileDeserializer implements JsonDeserializer<File> {
    public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new File(json.getAsJsonPrimitive().getAsString());
    }
}
