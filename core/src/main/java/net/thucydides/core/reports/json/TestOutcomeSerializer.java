package net.thucydides.core.reports.json;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.thucydides.core.model.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class TestOutcomeSerializer implements JsonSerializer<TestOutcome>,
		JsonDeserializer<TestOutcome> {

	private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String STEPS_FIELD = "steps";
    private static final String SUCCESSFUL_FIELD = "successful";
    private static final String FAILURES_FIELD = "failures";
    private static final String ERRORS_FIELD = "errors";
    private static final String SKIPPED_FIELD = "skipped";
    private static final String IGNORED_FIELD = "ignored";
    private static final String PENDING_FIELD = "pending";
    private static final String RESULT_FIELD = "result";
    private static final String TEST_STEPS = "test-steps";
    private static final String USER_STORY = "user-story";
    private static final String ISSUES = "issues";
    private static final String VERSIONS = "versions";
    private static final String TAGS = "tags";
    private static final String QUALIFIER_FIELD = "qualifier";
    private static final String DURATION = "duration";
    private static final String TIMESTAMP = "timestamp";
    private static final String BATCH_START_TIME = "batchStartTime";
    private static final String SESSION_ID = "session-id";
    private static final String EXAMPLES = "examples";
    private static final String MANUAL = "manual";
    public static final String NEW_LINE_CHAR = "\n";
    public static final String ESCAPE_CHAR_FOR_NEW_LINE = "&#10;";
    private static final String TEST_CASE_FIELD = "test-case";
    private static final String PROJECT_FIELD = "project";
	
	
	@Override
	public JsonElement serialize(TestOutcome testOutcome, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.addProperty(TITLE_FIELD, escape(titleFrom(testOutcome)));
        obj.addProperty(NAME_FIELD, nameFrom(testOutcome));
        if (testOutcome.getDescription() != null) {
            obj.addProperty(DESCRIPTION_FIELD, escape(descriptionFrom(testOutcome)));
        }
		obj.add(TEST_CASE_FIELD, context.serialize(testOutcome.getTestCase()));
        if (testOutcome.getProject() != null) {
            obj.add(PROJECT_FIELD, context.serialize(testOutcome.getProject()));
        }
		obj.addProperty(RESULT_FIELD, testOutcome.getResult().name());
        if (testOutcome.getQualifier() != null && testOutcome.getQualifier().isPresent()) {
            obj.addProperty(QUALIFIER_FIELD, escape(testOutcome.getQualifier().get()));
        }
        obj.addProperty(STEPS_FIELD, Integer.toString(testOutcome.countTestSteps()));
        obj.addProperty(SUCCESSFUL_FIELD, Integer.toString(testOutcome.getSuccessCount()));
        obj.addProperty(FAILURES_FIELD, Integer.toString(testOutcome.getFailureCount()));
        if (testOutcome.getErrorCount() > 0) {
        	obj.addProperty(ERRORS_FIELD, Integer.toString(testOutcome.getErrorCount()));
        }
        obj.addProperty(SKIPPED_FIELD, Integer.toString(testOutcome.getSkippedCount()));
        obj.addProperty(IGNORED_FIELD, Integer.toString(testOutcome.getIgnoredCount()));
        obj.addProperty(PENDING_FIELD, Integer.toString(testOutcome.getPendingCount()));
        obj.addProperty(DURATION, Long.toString(testOutcome.getDuration()));
        obj.addProperty(TIMESTAMP, formattedTimestamp(testOutcome.getStartTime()));
        if (testOutcome.has().testRunTimestamp()) {
            obj.addProperty(BATCH_START_TIME, formattedTimestamp(testOutcome.getTestRunTimestamp()));
        }
        if (testOutcome.isManual()) {
        	 obj.addProperty(MANUAL, "true");
        }
        if (isNotEmpty(testOutcome.getSessionId())) {
        	obj.addProperty(SESSION_ID, testOutcome.getSessionId());
        }
        obj.add(USER_STORY, context.serialize(testOutcome.getUserStory()));
        obj.add(ISSUES, context.serialize(testOutcome.getIssues()));
        obj.add(VERSIONS, context.serialize(testOutcome.getVersions()));
        obj.add(TAGS, context.serialize(testOutcome.getTags()));
        obj.add(TEST_STEPS, context.serialize(testOutcome.getTestSteps()));
        obj.add(EXAMPLES, context.serialize(testOutcome.getDataTable()));
		return obj;
	}



	@Override
	public TestOutcome deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject outcomeJsonObject = json.getAsJsonObject();
		Class<?> testCase = context.deserialize(outcomeJsonObject.getAsJsonObject(TEST_CASE_FIELD), Class.class);


		String testOutcomeName = outcomeJsonObject.get(NAME_FIELD).getAsString();
		TestOutcome testOutcome = new TestOutcome(testOutcomeName,testCase);
		testOutcome.setTitle(unescape(outcomeJsonObject.get(TITLE_FIELD).getAsString()));
        if (outcomeJsonObject.get(PROJECT_FIELD) != null) {
            String project = (outcomeJsonObject.get(PROJECT_FIELD).getAsString());
            testOutcome = testOutcome.forProject(project);
        }

        if (outcomeJsonObject.get(DESCRIPTION_FIELD) != null) {
            testOutcome.setDescription(unescape(outcomeJsonObject.get(DESCRIPTION_FIELD).getAsString()));
        }

		TestResult savedTestResult = TestResult.valueOf(outcomeJsonObject.get(RESULT_FIELD).getAsString());
		Long duration = readDuration(outcomeJsonObject);
        testOutcome.setDuration(duration);
        Optional<DateTime> timestamp = readTimestamp(outcomeJsonObject);
        if (timestamp.isPresent()) {
            testOutcome.setStartTime(timestamp.get());
        }
        Optional<DateTime> batchStartTime = readBatchStartTime(outcomeJsonObject);
        if (batchStartTime.isPresent()) {
            testOutcome.setTestRunTimestamp(batchStartTime.get());
        }
        boolean isManualTest = readManualTest(outcomeJsonObject);
        if (isManualTest) {
            testOutcome = testOutcome.asManualTest();
        }
        String sessionId = readSessionId(outcomeJsonObject);        
        testOutcome.setSessionId(sessionId);

        Story story = context.deserialize(outcomeJsonObject.getAsJsonObject(USER_STORY), Story.class);

        testOutcome.setUserStory(story);

        testOutcome = addQualifierIfPresent(outcomeJsonObject, testOutcome);
        addIssuesIfPresent(context, outcomeJsonObject, testOutcome);
        addVersionsIfPresent(context, outcomeJsonObject, testOutcome);
        addTagsIfPresent(context, outcomeJsonObject, testOutcome);
        
        JsonArray testStepsJsonArray = outcomeJsonObject.getAsJsonArray(TEST_STEPS);
        for(JsonElement currentJsonElement : testStepsJsonArray ){

        	TestStep currentStep = context.deserialize(currentJsonElement, TestStep.class);
        	testOutcome.recordStep(currentStep);
        }        

        DataTable dataTable  = context.deserialize(outcomeJsonObject.getAsJsonObject(EXAMPLES), DataTable.class);        
        testOutcome.useExamplesFrom(dataTable);
        
        if(testOutcome.getStepCount().equals(0)) {
            testOutcome.setAnnotatedResult(savedTestResult);
        }
		return testOutcome;
	}

    private TestOutcome addQualifierIfPresent(JsonObject outcomeJsonObject, TestOutcome testOutcome) {
        JsonElement qualifierField = outcomeJsonObject.get(QUALIFIER_FIELD);
        if (qualifierField != null) {
            testOutcome = testOutcome.withQualifier(unescape(qualifierField.getAsString()));
        }
        return testOutcome;
    }

    private void addTagsIfPresent(JsonDeserializationContext context, JsonObject outcomeJsonObject, TestOutcome testOutcome) {
        Set<TestTag> tags = Sets.newHashSet(convert(context.deserialize(outcomeJsonObject.getAsJsonArray(TAGS), Set.class), toTags()));
        if (tags != null) {
            testOutcome.setTags(tags);
        }
    }

    private Converter<Map, TestTag> toTags() {
        return new Converter<Map, TestTag>() {

            @Override
            public TestTag convert(Map from) {
                return TestTag.withName((String)from.get("name")).andType((String)from.get("type"));
            }
        };
    }

    private void addIssuesIfPresent(JsonDeserializationContext context, JsonObject outcomeJsonObject, TestOutcome testOutcome) {
        Set<String> issues = context.deserialize(outcomeJsonObject.getAsJsonArray(ISSUES), Set.class);
        if (issues != null) {
            ArrayList<String> issuesAsString = new ArrayList<String>();
            issuesAsString.addAll(issues);
            testOutcome.addIssues(issuesAsString);
        }
    }

    private void addVersionsIfPresent(JsonDeserializationContext context, JsonObject outcomeJsonObject, TestOutcome testOutcome) {
        Set<String> versions = context.deserialize(outcomeJsonObject.getAsJsonArray(VERSIONS), Set.class);
        if (versions != null) {
            ArrayList<String> versionsAsString = new ArrayList<String>();
            versionsAsString.addAll(versions);
            testOutcome.addVersions(versionsAsString);
        }
    }

    private String escape(String attribute) {
		return StringUtils.replace(attribute, NEW_LINE_CHAR, ESCAPE_CHAR_FOR_NEW_LINE);
	}

	private String unescape(String attribute) {
		return StringUtils.replace(attribute, ESCAPE_CHAR_FOR_NEW_LINE, NEW_LINE_CHAR);
	}

	private String titleFrom(final TestOutcome testOutcome) {
		return testOutcome.getTitle();
	}

    private String nameFrom(final TestOutcome testOutcome) {
        return testOutcome.getMethodName();
    }

    private String descriptionFrom(final TestOutcome testOutcome) {
        return testOutcome.getDescription();
    }

	private String formattedTimestamp(DateTime startTime) {
		return startTime.toString();
	}
		
	private long readDuration(JsonObject jsonObject) {
        JsonElement durationElement = jsonObject.get(DURATION);
        if (durationElement != null && StringUtils.isNumeric(durationElement.getAsString())) {
			return Long.parseLong(jsonObject.get(DURATION).getAsString());
		} else {
			return 0;
		}
	}
	
    private Optional<DateTime> readTimestamp(JsonObject jsonObject) {
        DateTime timestamp = null;
        JsonElement timestampElement = jsonObject.get(TIMESTAMP);
        if (timestampElement != null) {
            String timestampValue = jsonObject.get(TIMESTAMP).getAsString();
            timestamp = DateTime.parse(timestampValue);
        }
        return Optional.fromNullable(timestamp);
    }

    private Optional<DateTime> readBatchStartTime(JsonObject jsonObject) {
        DateTime timestamp = null;
        JsonElement timestampElement = jsonObject.get(BATCH_START_TIME);
        if (timestampElement != null) {
            String timestampValue = jsonObject.get(BATCH_START_TIME).getAsString();
            timestamp = DateTime.parse(timestampValue);
        }
        return Optional.fromNullable(timestamp);
    }
    
    private boolean readManualTest(JsonObject jsonObject) {
        boolean isManualTest = false;
        JsonElement manualTestAttribute = jsonObject.get(MANUAL);
        if (manualTestAttribute != null) {
            isManualTest = Boolean.valueOf(manualTestAttribute.getAsString());
        }
        return isManualTest;
    }

    private String readSessionId(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(SESSION_ID);
        if(jsonElement != null) {
        	return jsonElement.getAsString();
        } else {
        	return null;
        }
    }    
}
