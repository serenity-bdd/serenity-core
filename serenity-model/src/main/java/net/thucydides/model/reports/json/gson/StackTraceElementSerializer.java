package net.thucydides.model.reports.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

public class StackTraceElementSerializer implements JsonSerializer<StackTraceElement> {
    @Override
    public JsonElement serialize(StackTraceElement src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        String declaringClass = src.getClassName();
        String methodName = src.getMethodName();
        String fileName = src.getFileName();
        int lineNumber = src.getLineNumber();

        JsonObject stackTraceElement = new JsonObject();
        stackTraceElement.addProperty("declaringClass",declaringClass);
        stackTraceElement.addProperty("methodName",methodName);
        stackTraceElement.addProperty("fileName",fileName);
        stackTraceElement.addProperty("lineNumber",lineNumber);

        return stackTraceElement;
    }
}

