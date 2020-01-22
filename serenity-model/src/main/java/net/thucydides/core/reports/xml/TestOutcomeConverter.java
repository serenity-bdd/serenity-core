package net.thucydides.core.reports.xml;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import net.thucydides.core.model.*;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * XStream converter used to generate the XML acceptance test report.
 *
 * @author johnsmart
 */
public class TestOutcomeConverter implements Converter {

    private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String ID_FIELD = "id";
    private static final String PATH_FIELD = "path";
    private static final String STEPS_FIELD = "steps";
    private static final String SUCCESSFUL_FIELD = "successful";
    private static final String FAILURES_FIELD = "failures";
    private static final String ERRORS_FIELD = "errors";
    private static final String SKIPPED_FIELD = "skipped";
    private static final String IGNORED_FIELD = "ignored";
    private static final String PENDING_FIELD = "pending";
    private static final String RESULT_FIELD = "result";
    private static final String ANNOTATED_RESULT_FIELD = "annotated-result";
    private static final String TEST_GROUP = "test-group";
    private static final String TEST_STEP = "test-step";
    private static final String USER_STORY = "user-story";
    private static final String FEATURE = "feature";
    private static final String ISSUES = "issues";
    private static final String ISSUE = "issue";
    private static final String VERSIONS = "versions";
    private static final String VERSION = "version";
    private static final String TAGS = "tags";
    private static final String TAG = "tag";
    private static final String QUALIFIER_FIELD = "qualifier";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";
    private static final String EXCEPTION = "exception";
    private static final String ERROR = "error";
    private static final String SCREENSHOT_LIST_FIELD = "screenshots";
    private static final String SCREENSHOT_FIELD = "screenshot";
    private static final String SCREENSHOT_IMAGE = "image";
    private static final String SCREENSHOT_SOURCE = "source";
    private static final String DESCRIPTION = "description";
    private static final String DURATION = "duration";
    private static final String TIMESTAMP = "timestamp";
    private static final String SESSION_ID = "session-id";
    private static final String EXAMPLES = "examples";
    private static final String HEADERS = "headers";
    private static final String HEADER = "header";
    private static final String ROWS = "rows";
    private static final String DATASET = "dataset";
    private static final String ROW = "row";
    private static final String VALUE = "value";
    private static final String MANUAL = "manual";
    private static final String TEST_SOURCE = "test-source";
    private static final String NEW_LINE_CHAR = "\n";
    private static final String ESCAPE_CHAR_FOR_NEW_LINE = "&#10;";
    private static final String DEFAULT_ERROR_MESSAGE = "Unspecified failure";


    TestOutcomeConverter() {
    }

    /**
     * Determines which classes this converter applies to.
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return TestOutcome.class.isAssignableFrom(clazz);
    }

    /**
     * Generate an XML report given an TestOutcome object.
     */
    public void marshal(final Object value, final HierarchicalStreamWriter writer,
                        final MarshallingContext context) {
        TestOutcome testOutcome = (TestOutcome) value;
        Preconditions.checkNotNull(testOutcome, "The test run was null - WTF?");


        writer.addAttribute(TITLE_FIELD, escape(titleFrom(testOutcome)));
        writer.addAttribute(NAME_FIELD, nameFrom(testOutcome));
        if (testOutcome.getDescription() != null) {
            writer.addAttribute(DESCRIPTION_FIELD, escape(descriptionFrom(testOutcome)));
        }
        if (testOutcome.getQualifier() != null && testOutcome.getQualifier().isPresent()) {
            writer.addAttribute(QUALIFIER_FIELD, escape(testOutcome.getQualifier().get()));
        }
        writer.addAttribute(STEPS_FIELD, Long.toString(testOutcome.countTestSteps()));
        writer.addAttribute(SUCCESSFUL_FIELD, Long.toString(testOutcome.getSuccessCount()));
        writer.addAttribute(FAILURES_FIELD, Long.toString(testOutcome.getFailureCount()));
        if (testOutcome.getErrorCount() > 0) {
            writer.addAttribute(ERRORS_FIELD, Long.toString(testOutcome.getErrorCount()));
        }
        writer.addAttribute(SKIPPED_FIELD, Long.toString(testOutcome.getSkippedCount()));
        writer.addAttribute(IGNORED_FIELD, Long.toString(testOutcome.getIgnoredCount()));
        writer.addAttribute(PENDING_FIELD, Long.toString(testOutcome.getPendingCount()));
        if (testOutcome.getAnnotatedResult() != null) {
            writer.addAttribute(ANNOTATED_RESULT_FIELD, testOutcome.getAnnotatedResult().name());
        }
        writer.addAttribute(RESULT_FIELD, testOutcome.getResult().name());

        writer.addAttribute(DURATION, Long.toString(testOutcome.getDuration()));
        writer.addAttribute(TIMESTAMP, formattedTimestamp(testOutcome.getStartTime()));
        if (testOutcome.isManual()) {
            writer.addAttribute(MANUAL, "true");
        }
        if (isNotEmpty(testOutcome.getSessionId())) {
            writer.addAttribute(SESSION_ID, testOutcome.getSessionId());
        }
        if (isNotEmpty(testOutcome.getTestSource())) {
            writer.addAttribute(TEST_SOURCE, testOutcome.getTestSource());
        }
        addUserStoryTo(writer, testOutcome.getUserStory());
        addIssuesTo(writer, testOutcome.getIssues());
        addVersionsTo(writer, testOutcome.getVersions());
        addTagsTo(writer, testOutcome.getTags());
        addExamplesTo(writer, testOutcome.getDataTable());
        List<TestStep> steps = testOutcome.getTestSteps();
        for (TestStep step : steps) {
            writeStepTo(writer, step);
        }
    }

    private String formattedTimestamp(ZonedDateTime startTime) {
        return startTime.toString();
    }

    private String escape(String attribute) {

        if (StringUtils.isNotEmpty(attribute)) {
            attribute = StringUtils.replace(attribute, NEW_LINE_CHAR, ESCAPE_CHAR_FOR_NEW_LINE);
        }
        return attribute;
    }


    private String unescape(String attribute) {
        if (StringUtils.isNotEmpty(attribute)) {
            attribute = StringUtils.replace(attribute, ESCAPE_CHAR_FOR_NEW_LINE, NEW_LINE_CHAR);
        }
        return attribute;
    }

    private String titleFrom(final TestOutcome testOutcome) {
        return testOutcome.getTitle();
    }

    private String descriptionFrom(final TestOutcome testOutcome) {
        return testOutcome.getDescription();
    }

    private String nameFrom(final TestOutcome testOutcome) {
        if (testOutcome.getName() != null) {
            return testOutcome.getName();
        } else {
            return testOutcome.getTitle();
        }
    }

    private void writeStepTo(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.isAGroup()) {
            writer.startNode(TEST_GROUP);
            writer.addAttribute(NAME_FIELD, step.getDescription());
            writeResult(writer, step);
            writeScreenshotIfPresent(writer, step);

            List<TestStep> nestedSteps = step.getChildren();
            for (TestStep nestedStep : nestedSteps) {
                writeStepTo(writer, nestedStep);
            }
            writer.endNode();
        } else {
            writer.startNode(TEST_STEP);
            writeResult(writer, step);
            writer.addAttribute(DURATION, Long.toString(step.getDuration()));
            writeScreenshotIfPresent(writer, step);
            writeDescription(writer, step);
            writeErrorForFailingTest(writer, step);
            writer.endNode();
        }
    }

    private void addUserStoryTo(final HierarchicalStreamWriter writer, final Story userStory) {
        if (userStory != null) {
            writer.startNode(USER_STORY);
            writer.addAttribute(ID_FIELD, userStory.getId());
            writer.addAttribute(NAME_FIELD, userStory.getName());
            if (userStory.getPath() != null) {
                writer.addAttribute(PATH_FIELD, userStory.getPath());
            }
            if (userStory.getFeature() != null) {// userStory.getFeatureClass() != null) {
                writeFeatureNode(writer, userStory);
            }
            writer.endNode();
        }
    }

    private void writeFeatureNode(HierarchicalStreamWriter writer, Story userStory) {
        ApplicationFeature feature = userStory.getFeature();// ApplicationFeature.from(userStory.getFeatureClass());
        writer.startNode(FEATURE);
        writer.addAttribute(ID_FIELD, feature.getId());
        writer.addAttribute(NAME_FIELD, feature.getName());
        writer.endNode();
    }


    private void addIssuesTo(final HierarchicalStreamWriter writer, final List<String> issues) {
        addNodeTo(writer, issues, ISSUES, ISSUE);
    }

    private void addVersionsTo(final HierarchicalStreamWriter writer, final List<String> versions) {
        addNodeTo(writer, versions, VERSIONS, VERSION);
    }

    private void addNodeTo(final HierarchicalStreamWriter writer, final List<String> entries, String groupName, String itemName) {
        if (!entries.isEmpty()) {
            writer.startNode(groupName);
            for (String entry : entries) {
                writer.startNode(itemName);
                writer.setValue(entry);
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void addTagsTo(HierarchicalStreamWriter writer, Set<TestTag> tags) {
        if (!CollectionUtils.isEmpty(tags)) {
            writer.startNode(TAGS);
            List<TestTag> orderedTags = new ArrayList(tags);
            Collections.sort(orderedTags);
            for (TestTag tag : orderedTags) {
                writer.startNode(TAG);
                writer.addAttribute(TAG_NAME, tag.getName());
                writer.addAttribute(TAG_TYPE, tag.getType());
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void addExamplesTo(HierarchicalStreamWriter writer, DataTable dataTable) {
        if ((dataTable != null) && (!dataTable.getRows().isEmpty())) {
            writer.startNode(EXAMPLES);
            writeDatasetDescriptors(writer,dataTable);
            writeHeaders(writer, dataTable);
            writeRows(writer, dataTable);
            writer.endNode();
        }
    }

    private void writeHeaders(HierarchicalStreamWriter writer, DataTable dataTable) {
        writer.startNode(HEADERS);
        for(String header : dataTable.getHeaders()) {
            writeHeader(writer, header);
        }
        writer.endNode();
    }

    private void writeRows(HierarchicalStreamWriter writer, DataTable dataTable) {
        writer.startNode(ROWS);
        for(DataTableRow rowData : dataTable.getRows()) {
            writeRow(writer, rowData);
        }
        writer.endNode();
    }


    private void writeDatasetDescriptors(HierarchicalStreamWriter writer, DataTable dataTable) {
        writer.startNode("datasets");
        for(DataSetDescriptor descriptor : dataTable.getDataSetDescriptors()) {
            writeDescriptor(writer, descriptor);
        }
        writer.endNode();
    }

    private void writeDescriptor(HierarchicalStreamWriter writer, DataSetDescriptor descriptor) {
        writer.startNode("dataset");
        writer.addAttribute("startRow", Integer.toString(descriptor.getStartRow()));
        writer.addAttribute("rowCount", Integer.toString(descriptor.getRowCount()));
        if (descriptor.getName() != null) {
            writer.addAttribute("name", descriptor.getName());
        }
        if (descriptor.getDescription() != null) {
            writer.addAttribute("description", descriptor.getDescription());
        }
        writer.endNode();
    }


    private void writeRow(HierarchicalStreamWriter writer, DataTableRow rowData) {
        writer.startNode(ROW);
        if (rowData.getResult() != TestResult.UNDEFINED) {
            writer.addAttribute("result", rowData.getResult().toString());
        }
        for(Object cellValue : rowData.getValues()) {
            writeCellValue(writer, cellValue.toString());
        }
        writer.endNode();
    }

    private void writeHeader(final HierarchicalStreamWriter writer, final String header) {
        writer.startNode(HEADER);
        writer.setValue(header);
        writer.endNode();
    }

    private void writeCellValue(final HierarchicalStreamWriter writer, final String cellValue) {
        writer.startNode(VALUE);
        writer.setValue(cellValue);
        writer.endNode();
    }

    private void writeErrorForFailingTest(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.isFailure() || step.isError() || step.isCompromised()) {
            writeErrorMessageAndException(writer, step);
        }
    }

    private void writeErrorMessageAndException(final HierarchicalStreamWriter writer,
                                               final TestStep step) {
        String errorMessage = isEmpty(step.getErrorMessage()) ? DEFAULT_ERROR_MESSAGE : step.getErrorMessage();
        writeErrorMessageNode(writer, errorMessage);
        if (step.getException() != null) {
            writeFailureCauseNode(writer, step.getException());
        }
    }

    private void writeFailureCauseNode(final HierarchicalStreamWriter writer, final FailureCause cause) {
        writer.startNode(EXCEPTION);
        writer.setValue(cause.getErrorType() + ":" + cause.getMessage() + System.lineSeparator() + Arrays.toString(cause.getStackTrace()));
        writer.endNode();

    }

    private void writeErrorMessageNode(final HierarchicalStreamWriter writer,
                                       final String errorMessage) {
        writer.startNode(ERROR);
        writer.setValue(errorMessage);
        writer.endNode();
    }

    private void writeScreenshotIfPresent(final HierarchicalStreamWriter writer, final TestStep step) {
        if ((step.getScreenshots() != null) && (step.getScreenshots().size() > 0)) {
            writer.startNode(SCREENSHOT_LIST_FIELD);
            for(ScreenshotAndHtmlSource screenshotAndHtmlSource : step.getScreenshots()) {
                writer.startNode(SCREENSHOT_FIELD);
                writer.addAttribute(SCREENSHOT_IMAGE, screenshotAndHtmlSource.getScreenshot().getName());
                if (screenshotAndHtmlSource.getHtmlSource().isPresent()) {
                    writer.addAttribute(SCREENSHOT_SOURCE, screenshotAndHtmlSource.getHtmlSource().get().getName());
                }
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void writeResult(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.addAttribute(RESULT_FIELD, step.getResult().toString());
    }

    private void writeDescription(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.startNode(DESCRIPTION);
        writer.setValue(step.getDescription());
        writer.endNode();
    }

    /**
     * Convert XML to an TestOutcome object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
                            final UnmarshallingContext context) {

        String methodName = reader.getAttribute(NAME_FIELD);
        TestOutcome testOutcome = new TestOutcome(methodName);
        testOutcome.setTitle(unescape(reader.getAttribute(TITLE_FIELD)));

        if (reader.getAttribute(DESCRIPTION_FIELD) != null) {
            testOutcome.setDescription(unescape(reader.getAttribute(DESCRIPTION_FIELD)));
        }

        TestResult savedTestResult = TestResult.valueOf(reader.getAttribute(RESULT_FIELD));
        TestResult savedAnnotatedResult = null;
        if ((reader.getAttribute(ANNOTATED_RESULT_FIELD) != null)) {
            savedAnnotatedResult = TestResult.valueOf(reader.getAttribute(ANNOTATED_RESULT_FIELD));
        }

        if (reader.getAttribute(QUALIFIER_FIELD) != null) {
            testOutcome = testOutcome.withQualifier(unescape(reader.getAttribute(QUALIFIER_FIELD)));
        }
        Long duration = readDuration(reader);
        testOutcome.setDuration(duration);
        Optional<ZonedDateTime> startTime = readTimestamp(reader);
        if (startTime.isPresent()) {
            testOutcome.setStartTime(startTime.get());
        }
        boolean isManualTest = readManualTest(reader);
        if (isManualTest) {
            testOutcome = testOutcome.setToManual();
        }
        String sessionId = readSessionId(reader);
        testOutcome.setSessionId(sessionId);
        String source = readSource(reader);
        testOutcome.setTestSource(source);
        readChildren(reader, testOutcome);
        if (savedAnnotatedResult != null) {
            testOutcome.setAnnotatedResult(savedAnnotatedResult);
        }
        if (savedAnnotatedResult == null && testOutcome.getStepCount().equals(0)) {
            testOutcome.setAnnotatedResult(savedTestResult);
        }
        return testOutcome;
    }

    private boolean readManualTest(HierarchicalStreamReader reader) {
        boolean isManualTest = false;
        String manualTestAttribute = reader.getAttribute(MANUAL);
        if (manualTestAttribute != null) {
            isManualTest = Boolean.valueOf(manualTestAttribute);
        }
        return isManualTest;
    }

    private void readChildren(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            switch (childNode) {
                case TEST_STEP:
                    readTestStep(reader, testOutcome);
                    break;
                case TEST_GROUP:
                    readTestGroup(reader, testOutcome);
                    break;
                case ISSUES:
                    readTestRunIssues(reader, testOutcome);
                    break;
                case VERSIONS:
                    readTestRunVersions(reader, testOutcome);
                    break;
                case USER_STORY:
                    readUserStory(reader, testOutcome);
                    break;
                case TAGS:
                    readTags(reader, testOutcome);
                    break;
                case EXAMPLES:
                    readExamples(reader, testOutcome);
                    break;
            }
            reader.moveUp();
        }
    }


    private void readUserStory(final HierarchicalStreamReader reader,
                               final TestOutcome testOutcome) {

        String storyId = reader.getAttribute(ID_FIELD);
        String storyName = reader.getAttribute(NAME_FIELD);
        String storyPath = reader.getAttribute(PATH_FIELD);
        ApplicationFeature feature = null;

        if (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(FEATURE)) {
                feature = readFeature(reader);
            }
            reader.moveUp();
        }
        Story story;
        if (feature == null) {
            story = Story.withIdAndPath(storyId, storyName,storyPath);
        } else {
            story = Story.withIdAndPathAndFeature(storyId, storyName, storyPath, feature.getId(), feature.getName());
        }
        testOutcome.setUserStory(story);
    }

    private ApplicationFeature readFeature(final HierarchicalStreamReader reader) {

        String featureId = reader.getAttribute(ID_FIELD);
        String featureName = reader.getAttribute(NAME_FIELD);
        return new ApplicationFeature(featureId, featureName);
    }


    private void readTestRunIssues(final HierarchicalStreamReader reader,
                                         final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String issue = reader.getValue();
            testOutcome.isRelatedToIssue(issue);
            reader.moveUp();
        }
    }

    private void readTestRunVersions(final HierarchicalStreamReader reader,
                                     final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String version = reader.getValue();
            testOutcome.addVersion(version);
            reader.moveUp();
        }
    }

    private void readTags(final HierarchicalStreamReader reader,
                          final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(TAG)) {
                tags.add(readTag(reader));
            }
            reader.moveUp();
        }
        testOutcome.setTags(tags);
    }

    private void readExamples(final HierarchicalStreamReader reader,
                              final TestOutcome testOutcome) {
        List<String> headers = new ArrayList<>();
        List<DataTableRow> rows = new ArrayList<>();
        List<DataSetDescriptor> descriptors = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            switch (childNode) {
                case HEADERS:
                    headers = readHeaders(reader);
                    break;
                case ROWS:
                    rows = readRows(reader);
                    break;
                case "datasets":
                    descriptors = readDescriptors(reader);
                    break;
            }
            reader.moveUp();
        }

        DataTable table = DataTable.withHeaders(headers).andRowData(rows).andDescriptors(descriptors).build();
        testOutcome.useExamplesFrom(table);
    }

    private List<String> readHeaders(final HierarchicalStreamReader reader) {
        List<String> headers = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(HEADER)) {
                headers.add(reader.getValue());
            }
            reader.moveUp();
        }
        return headers;
    }

    private List<DataTableRow> readRows(final HierarchicalStreamReader reader) {
        List<DataTableRow> rows = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(ROW)) {
                rows.add(readRow(reader));
            }
            reader.moveUp();
        }
        return rows;
    }

    private List<DataSetDescriptor> readDescriptors(final HierarchicalStreamReader reader) {
        List<DataSetDescriptor> descriptors = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(DATASET)) {
                int startRow = Integer.parseInt(reader.getAttribute("startRow"));
                int rowCount = Integer.parseInt(reader.getAttribute("rowCount"));
                String name = reader.getAttribute("name");
                String description = reader.getAttribute("description");
                // TODO: Not sure where this is called, it may need to read the example tags as well.
                descriptors.add(new DataSetDescriptor(startRow,rowCount,name, description, Collections.emptyList()));
            }
            reader.moveUp();
        }
        return descriptors;
    }


    private DataTableRow readRow(final HierarchicalStreamReader reader) {
        List<String> rowValues = new ArrayList<>();
        TestResult result = null;
        String rowValue = reader.getAttribute("row");
        int row = (rowValue != null) ? Integer.parseInt(rowValue) : 0;
        String resultValue = reader.getAttribute("result");
        while (reader.hasMoreChildren()) {
            if (resultValue != null) {
                result = TestResult.valueOf(resultValue);
            } else {
                result = TestResult.SUCCESS;
            }
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(VALUE)) {
                rowValues.add(reader.getValue());
            }
            reader.moveUp();
        }
        DataTableRow newRow = new DataTableRow(rowValues, row);
        if (result != null) {
            newRow.setResult(result);
        }
        return newRow;
    }

    private TestTag readTag(HierarchicalStreamReader reader) {
        return TestTag.withName(reader.getAttribute("name")).andType(reader.getAttribute("type"));
    }

    /*
    * <test-step result="SUCCESS"> <description>The customer navigates to the
    * metro masthead site.</description>
    * <screenshot>the_customer_navigates_to_the_metro_masthead_site2
    * .png</screenshot> </test-step>
    */
    private void readTestStep(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        TestStep step = new TestStep();
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        step.setResult(result);

        Long duration = readDuration(reader);
        step.setDuration(duration);
        readTestStepChildren(reader, step);

        testOutcome.recordStep(step);
    }

    private long readDuration(HierarchicalStreamReader reader) {
        String durationValue = reader.getAttribute(DURATION);
        if (StringUtils.isNumeric(durationValue)) {
            return Long.parseLong(reader.getAttribute(DURATION));
        } else {
            return 0;
        }
    }

    private Optional<ZonedDateTime> readTimestamp(HierarchicalStreamReader reader) {
        String timestamp = reader.getAttribute(TIMESTAMP);
        return (timestamp != null) ? Optional.of(ZonedDateTime.parse(reader.getAttribute(TIMESTAMP))) : Optional.empty();
    }

    private String readSessionId(HierarchicalStreamReader reader) {
        return reader.getAttribute(SESSION_ID);
    }

    private String readSource(HierarchicalStreamReader reader) {
        return reader.getAttribute(TEST_SOURCE);
    }

    private void readTestGroup(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        String name = reader.getAttribute(NAME_FIELD);
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        testOutcome.recordStep(new TestStep(name));
        testOutcome.startGroup();
        testOutcome.currentGroup().setResult(result);
        readChildren(reader, testOutcome);
        testOutcome.endGroup();
    }

    private void readTestStepChildren(final HierarchicalStreamReader reader, final TestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(DESCRIPTION)) {
                step.setDescription(reader.getValue());
            } else if (childNode.equals(SCREENSHOT_LIST_FIELD)) {
                readScreenshots(reader, step);
            }
            reader.moveUp();
        }
    }

    private void readScreenshots(HierarchicalStreamReader reader, TestStep step) {
        if (reader.getNodeName().equals(SCREENSHOT_LIST_FIELD)) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String childNode = reader.getNodeName();
                if (childNode.equals(SCREENSHOT_FIELD)) {
                    String screenshot = reader.getAttribute(SCREENSHOT_IMAGE);
                    String source = reader.getAttribute(SCREENSHOT_SOURCE);
                    if (source != null) {
                        step.addScreenshot(new ScreenshotAndHtmlSource(new File(screenshot), new File(source)));
                    } else {
                        step.addScreenshot(new ScreenshotAndHtmlSource(new File(screenshot)));
                    }
                }
                reader.moveUp();
            }
        }
    }
}