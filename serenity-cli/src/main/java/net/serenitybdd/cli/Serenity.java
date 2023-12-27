package net.serenitybdd.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Serenity {

    private static final String DEFAULT_SOURCE_DIRECTORY = "target/site/serenity";
    private static final String DEFAULT_DESTINATION_DIRECTORY = "target/site/serenity";

    @Option(name = "--help", usage = "print this message")
    private boolean help;

    @Option(name = "--features",
            usage = "Source directory containing the feature files",
            metaVar = "<directory>")
    Path testScenariosDirectory = Paths.get("src/test/resources/features");

    @Option(name = "--source",
            usage = "Source directory containing the Serenity JSON output files",
            metaVar = "<directory>")
    Path jsonOutcomesDirectory = Paths.get("target/site/serenity");

    @Option(name = "--destination",
            usage = "Directory directory to contain the generated Serenity report",
            metaVar = "<directory>")
    Path destination = Paths.get("target/site/serenity");

    @Option(name = "--project",
            usage = "Project name to appear in the Serenity reports (defaults to the directory name",
            metaVar = "<string>")
    String project;

    @Option(name = "--issueTrackerUrl",
            usage = "Base URL for issue trackers other than JIRA",
            metaVar = "<string>")
    String issueTrackerUrl;

    @Option(name = "--jiraUrl",
            usage = "Base URL for JIRA",
            metaVar = "<string>")
    String jiraUrl;

    @Option(name = "--jiraProject",
            usage = "Default project for JIRA",
            metaVar = "<string>")
    String jiraProject;

    @Option(name = "--jiraUsername",
            metaVar = "<string>")
    String jiraUsername;

    @Option(name = "--jiraPassword",
            metaVar = "<string>")
    String jiraPassword;

    @Option(name = "--jiraWorkflow",
            metaVar = "<string>")
    String jiraWorkflow;

    @Option(name = "--jiraWorkflowActive",
            metaVar = "<string>")
    String jiraWorkflowActive;

    @Option(name = "--tags",
            metaVar = "<string>")
    String tags;


    private final PrintWriter printWriter;


    public static void main(String[] args) {
        new Serenity(new PrintWriter(System.out)).executeWith(args);
    }

    public Serenity(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public Serenity() {
        this(new PrintWriter(System.out));
    }

    public void executeWith(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            printUsage(parser);
            return;
        }

        if (help) {
            printUsage(parser);
            return;
        }

        String projectName = (project != null) ? project : workingDirectoryName();

        SerenityCLIReportCoordinator reporter = new SerenityCLIReportCoordinator(
                testScenariosDirectory.toAbsolutePath(),
                jsonOutcomesDirectory.toAbsolutePath(),
                destination,
                projectName,
                issueTrackerUrl,
                // todo: introduce a type for JiraDetails
                jiraUrl, jiraProject, jiraUsername, jiraPassword, jiraWorkflowActive, jiraWorkflow,
                tags
        );

        reporter.execute();
    }

    private String workingDirectoryName() {
        return Paths.get(System.getProperty("user.dir")).getFileName().toString();
    }

    private void printUsage(CmdLineParser parser) {
        printWriter.println("Serenity BDD Command Line Tool");
        printWriter.println(" Example: java net.serenitybdd.cli.Serenity " + parser.printExample(OptionHandlerFilter.REQUIRED));
        printWriter.println("Options:");
        parser.printUsage(printWriter, null);
    }
}
