package net.thucydides.core.reports.json;

import com.google.common.base.Optional;
import com.google.gson.*;
import net.thucydides.core.annotations.TestAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {


    private static final Logger LOGGER = LoggerFactory.getLogger(ClassTypeAdapter.class);

    private static final String ISSUES = "issues";
    private static final String CLASSNAME = "classname";

    @Override
    public JsonElement serialize(Class<?> src,
                                 java.lang.reflect.Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(CLASSNAME, new JsonPrimitive(src.getName()));
        JsonArray issuesJsonArray = new JsonArray();
        TestAnnotations testAnnotationsForClass = TestAnnotations.forClass(src);
        String[] annotatedIssuesForTestCase = testAnnotationsForClass.getAnnotatedIssuesForTestCase(src);
        addIssuesToCollectingJsonArray(issuesJsonArray, annotatedIssuesForTestCase);
        String annotatedIssueForTestCase = testAnnotationsForClass.getAnnotatedIssueForTestCase(src);
        if (annotatedIssueForTestCase != null) {
            issuesJsonArray.add(new JsonPrimitive(annotatedIssueForTestCase));
        }
        for (Method currentMethod : src.getMethods()) {
            String[] annotatedIssuesForMethod = testAnnotationsForClass
                    .getAnnotatedIssuesForMethod(currentMethod.getName());
            addIssuesToCollectingJsonArray(issuesJsonArray,
                    annotatedIssuesForMethod);
            Optional<String> annotatedIssueForMethod = testAnnotationsForClass
                    .getAnnotatedIssueForMethod(currentMethod.getName());
            if (annotatedIssueForMethod.isPresent()) {
                issuesJsonArray.add(new JsonPrimitive(
                        annotatedIssueForMethod.get()));
            }
        }
        if (issuesJsonArray.size() > 0) {
            jsonObject.add(ISSUES, issuesJsonArray);
        }
        return jsonObject;
    }

    private void addIssuesToCollectingJsonArray(JsonArray issuesJsonArray,
                                                String[] issues) {
        if (issues != null) {
            for (String issue : issues) {
                issuesJsonArray.add(new JsonPrimitive(issue));
            }
        }
    }

    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        try {
            return Class.forName(jsonObject.get(CLASSNAME).getAsString());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}