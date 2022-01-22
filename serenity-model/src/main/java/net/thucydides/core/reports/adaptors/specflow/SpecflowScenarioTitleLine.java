package net.thucydides.core.reports.adaptors.specflow;

import com.google.common.base.Splitter;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SpecflowScenarioTitleLine {
    public static final String START_ARGUMENT = "(";
    public static final char ESCAPE_CHAR = '\\';
    public static final char STRING_SEP = '"';
    public static final char ARG_SEP = ',';
    public static final char END_ARGUMENT = ')';
    public static final String NULL = "null";
    private final String scenarioTitle;
    private final String storyTitle;
    private final String storyPath;
    private final List<String> parameters;
    private final Inflector inflector = Inflector.getInstance();

    private final EnvironmentVariables environmentVariables;

    public SpecflowScenarioTitleLine(String titleLine, EnvironmentVariables environmentVariables) {
        List<String> titleElements = elementsFrom(stripLead(titleLine));

        this.environmentVariables = environmentVariables;
        scenarioTitle = scenarioTitleIn(NewList.reverse(titleElements).get(0));
        storyTitle = storyTitleIn(titleElements);
        storyPath = pathFrom(titleElements);
        parameters = argumentsFrom(titleLine);
    }

    /**
     * From a title line that looks like:
     * ***** ESD.Epp.RegularPaymentCapture.SpecFlow.Features.RegularPaymentGroupServiceCallsFeature.PopulateBusinessTransactionAndPaymentTypeDropDownLists("Inputter","Funds Transfer between Own Accounts","N/A","Funds Transfer",null)
     * returns a list of string: ["Inputter","Funds Transfer between Own Accounts","N/A","Funds Transfer",""]
     * It assumes that the escape character is '\' and it replaces null by an empty string
     *
     * @param titleLine the title line
     * @return a list of argumenents
     */
    private List<String> argumentsFrom(String titleLine) {
        if (!titleLine.contains(START_ARGUMENT)) {
            return new ArrayList<>();
        }
        String argumentString = titleLine.substring(titleLine.indexOf(START_ARGUMENT) + 1, titleLine.lastIndexOf(END_ARGUMENT));
        List<String> result = new ArrayList<>();
        StringBuilder currentResult = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < argumentString.length(); i++) {
            Character c = argumentString.charAt(i);
            if (c == ESCAPE_CHAR) {
                i++;
                currentResult.append(argumentString.charAt(i));
            } else if (c == STRING_SEP) {//this should happen only while parsing string
                inString = !inString;
            } else if (c == ARG_SEP && !inString) {
                addResult(result, currentResult);
                currentResult = new StringBuilder();
            } else {
                currentResult.append(c);
            }
        }
        if (currentResult.length() != 0) {
            addResult(result, currentResult);
        }
        return result;
    }

    private void addResult(List<String> result, StringBuilder currentResult) {
        String s = currentResult.toString();
        if (s.equals(NULL)) {
            s = "";
        }
        result.add(s);
    }

    public SpecflowScenarioTitleLine(String titleLine) {
        this(titleLine, Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    private String pathFrom(List<String> titleElements) {
        List<String> pathElements = NewList.copyOf(titleElements);
        pathElements.remove(pathElements.size() - 1);
        pathElements = removeExcludedElementsFrom(pathElements);
        return Joiner.on(".").join(pathElements);
    }

    private List<String> removeExcludedElementsFrom(List<String> pathElements) {
        List<String> purgedPathElements = new ArrayList<>(pathElements);
        String excludedElementConfiguration
                = ThucydidesSystemProperty.THUCYDIDES_REQUIREMENT_EXCLUSIONS.from(environmentVariables, "");

        List<String> excludedElements
                = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(excludedElementConfiguration);
        purgedPathElements.removeAll(excludedElements);
        return purgedPathElements;
    }

    private String storyTitleIn(List<String> titleElements) {
        List<String> reverseTitleElements = NewList.reverse(titleElements);
        return inflector.of(reverseTitleElements.get(1)).inHumanReadableForm().toString();
    }

    private String scenarioTitleIn(String titleElement) {
        return Splitter.on("(").split(titleElement).iterator().next();
    }

    private List<String> elementsFrom(String titleElements) {
        return new ArrayList(Splitter.on(".").splitToList(titleElements));
    }

    private String stripLead(String titleLine) {
        return StringUtils.strip(titleLine, "* ");
    }

    public String getScenarioTitle() {
        return scenarioTitle;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public String getStoryPath() {
        return storyPath;
    }

    public String getTitleName() {
        return storyPath + "." + scenarioTitle;
    }

    public List getArguments() {
        return parameters;
    }


    public String getRowTitle() {
        return scenarioTitle + "[" + getArguments() + "]";
    }
}
