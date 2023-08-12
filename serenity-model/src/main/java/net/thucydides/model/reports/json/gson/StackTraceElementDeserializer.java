package net.thucydides.model.reports.json.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by john on 11/02/15.
 */
public class StackTraceElementDeserializer implements JsonDeserializer<StackTraceElement> {
    public StackTraceElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String declaringClass = json.getAsJsonObject().get("declaringClass").getAsString();
        String methodName = json.getAsJsonObject().get("methodName").getAsString();
        String fileName = json.getAsJsonObject().has("fileName") ? json.getAsJsonObject().get("fileName").getAsString() : null;
        int lineNumber = json.getAsJsonObject().has("lineNumber") ? json.getAsJsonObject().get("lineNumber").getAsInt() : 0;

        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}
