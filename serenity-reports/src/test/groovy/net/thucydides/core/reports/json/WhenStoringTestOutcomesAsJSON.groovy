package net.thucydides.core.reports.json

import net.serenitybdd.model.rest.RestMethod
import net.serenitybdd.model.rest.RestQuery
import net.serenitybdd.annotations.Feature
import net.serenitybdd.annotations.Issue
import net.serenitybdd.annotations.Issues
import net.serenitybdd.annotations.WithTag
import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.domain.*
import net.thucydides.model.reports.AcceptanceTestLoader
import net.thucydides.model.reports.AcceptanceTestReporter
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.model.reports.json.JSONTestOutcomeReporter
import net.thucydides.model.reports.json.gson.GsonJSONConverter
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource
import net.thucydides.model.steps.TestSourceType
import net.thucydides.model.environment.MockEnvironmentVariables
import org.junit.ComparisonFailure
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files
import java.time.ZoneId
import java.time.ZonedDateTime

class WhenStoringTestOutcomesAsJSON extends Specification {

    private static final ZonedDateTime FIRST_OF_JANUARY = ZonedDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    private static final ZonedDateTime SECOND_OF_JANUARY = ZonedDateTime.of(2013, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault())

    AcceptanceTestReporter reporter
    AcceptanceTestLoader loader


    TestOutcomes allTestOutcomes = Mock()

    File outputDirectory

    def setup() {
        outputDirectory = Files.createTempDirectory("json-outcomes").toFile()
        outputDirectory.deleteOnExit()
        reporter = new JSONTestOutcomeReporter()
        loader = new JSONTestOutcomeReporter()
        reporter.setOutputDirectory(outputDirectory)
        loader.setOutputDirectory(outputDirectory)
    }

    class AUserStory {
    }

    @net.serenitybdd.annotations.Story(AUserStory.class)
    class SomeTestScenario {
        void a_simple_test_case() {
        }

        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    @Issue("PROJ-123")
    @WithTag(name = "important feature", type = "feature")
    class SomeTestScenarioWithTags {
        void a_simple_test_case() {
        }

        @WithTag(name = "simple story", type = "story")
        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    @Feature
    class AFeature {
        class AUserStoryInAFeature {
        }
    }

    @net.serenitybdd.annotations.Story(AFeature.AUserStoryInAFeature.class)
    class SomeTestScenarioInAFeature {
        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    class ATestScenarioWithoutAStory {
        void should_do_this() {
        }

        void should_do_that() {
        }

        void and_should_do_that() {
        }
    }

    @net.serenitybdd.annotations.Story(AUserStory.class)
    @Issues(["#123", "#456"])
    class ATestScenarioWithIssues {
        void a_simple_test_case() {
        }

        @Issue("#789")
        void should_do_this() {
        }

        void should_do_that() {
        }
    }


    @net.serenitybdd.annotations.Story(AUserStory.class)
    @Issues(["PROJ-123", "PROJ-456"])
    class ATestScenarioWithLongIssues {
        void a_simple_test_case() {
        }

        @Issue("PROJ-456")
        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    @net.serenitybdd.annotations.Story(AUserStory.class)
    class SomeNestedTestScenario {
        void a_nested_test_case() {
        };

        void should_do_this() {
        };

        void should_do_that() {
        };
    }

    def "should generate an JSON report for an acceptance test run"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        def reloadedTestOutcome = loader.loadReportFrom(jsonReport)
        then:
        reloadedTestOutcome.isPresent() && reloadedTestOutcome.get() == testOutcome
    }




    def "should generate a minimized JSON report by default"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        !jsonReport.text.contains("  ")
    }

    def "should generate pretty JSON report if configured"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY

        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))

        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("json.pretty.printing","true")
        when:
        reporter.jsonConverter = new GsonJSONConverter(environmentVariables)
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        jsonReport.text.contains("  ")
    }

    def "should include the project and batch start time in the JSON report if specified."() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))

        testOutcome = testOutcome.forProject("Some Project").inTestRunTimestamped(SECOND_OF_JANUARY)

        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.startTime == testOutcome.startTime
        reloadedOutcome.description == testOutcome.description
        reloadedOutcome.testSteps.size() == 1
    }

    def "should record screenshot details"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.description = "Some description"
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY).addScreenshot(new ScreenshotAndHtmlSource(new File("screenshot1.png"))))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        !reloadedOutcome.testSteps.get(0).getScreenshots().isEmpty()
    }

    def "should generate an JSON report for an acceptance test run without a test class"() {
        given:
        def testOutcome = TestOutcome.forTestInStory("should_do_this",
                net.thucydides.model.domain.Story.withId("id", "name"))
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.userStory.id == "id"
        reloadedOutcome.userStory.name == "name"

    }

    def "should include issues in the JSON report"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.issues as Set == ["#123", "#456", "#789"] as Set
    }

    def "should restore formatted issues from JSON report"() {
        given:
        def issueTracking = Mock(IssueTracking)
        and:
        def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithLongIssues.class)
        issueTracking.issueTrackerUrl >> "http://my.issue.tracker/MY-PROJECT/browse/{0}"
        testOutcome = testOutcome.usingIssueTracking(issueTracking)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome = reloadedOutcome.usingIssueTracking(issueTracking)
        then:
        reloadedOutcome.formattedIssues == "(<a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/PROJ-123\">PROJ-123</a>, <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/PROJ-456\">PROJ-456</a>)"
    }



    def "should generate an JSON report for a manual acceptance test run"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).setToManual()
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.isManual()
    }


    def "should store annotated tags and issues in the JSON reports"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.issues == ["PROJ-123"]
        and:
        reloadedOutcome.tags.containsAll([TestTag.withName("simple story").andType("story"),
                                          TestTag.withName("important feature").andType("feature")])
    }

    def "should store the featureTag in the JSON reports"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.featureTag.isPresent()
        and:
        reloadedOutcome.featureTag.get() == TestTag.withName("net/thucydides/core/reports/json/WhenStoringTestOutcomesAsJSON/SomeTestScenarioWithTags").andType("feature")
    }


    def "should include the session id if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setSessionId("1234")
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.sessionId == "1234"
    }

    def "should include annotated results if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setAnnotatedResult(TestResult.IGNORED)
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.getResult() == TestResult.IGNORED
    }



    def "should include a data table if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.useExamplesFrom(DataTable.withHeaders(["a","b","c"])
                                             .andTitle("a title")
                                             .andDescription("some description").build())
        testOutcome.addRow(["a":"1", "b":"2", "c":"3"])
        testOutcome.addRow(["a":"2", "b":"3", "c":"4"])
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.dataTable.headers == ["a","b","c"]
        reloadedOutcome.dataTable.rows[0].stringValues == ["1","2","3"]
        reloadedOutcome.dataTable.rows[1].stringValues == ["2","3","4"]
    }

    def "should include a data table with multiple data sets if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.useExamplesFrom(DataTable.withHeaders(["a", "b", "c"])
                .andTitle("a title")
                .andDescription("a description").build())
        testOutcome.addRow(["a":"1", "b":"2", "c":"3"])
        testOutcome.addRow(["a":"2", "b":"3", "c":"4"])
        testOutcome.dataTable.startNewDataSet("another title","another description")
        testOutcome.addRow(["a":"3", "b":"2", "c":"3"])
        testOutcome.addRow(["a":"4", "b":"3", "c":"4"])
        testOutcome.addRow(["a":"5", "b":"3", "c":"4"])

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        def reloadedDataSets = reloadedOutcome.dataTable.dataSets

        reloadedDataSets.size() == 2
        reloadedDataSets[0].rows.size() == 2
        reloadedDataSets[0].name == "a title"
        reloadedDataSets[0].description == "a description"

        reloadedDataSets[1].rows.size() == 3
        reloadedDataSets[1].name == "another title"
        reloadedDataSets[1].description == "another description"
    }

    def "should contain the feature if provided"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setSessionId("1234")
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.feature.name == "A feature"
    }


    def "should record features and stories as tags"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.tags.contains(TestTag.withName("net/thucydides/core/reports/json/WhenStoringTestOutcomesAsJSON/AFeature/AUserStoryInAFeature").andType("feature"))
        reloadedOutcome.tags.contains(TestTag.withName("A feature").andType("feature"))
    }

    def "should generate a JSON report in the target directory"() {
        when:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        def jsonReport = reporter.generateReportFor(testOutcome)

        then:
        jsonReport.path.startsWith(outputDirectory.path)
    }

    def "should have a qualified filename if qualifier present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        def step = TestStepFactory.successfulTestStepCalled("step 1")
        testOutcome.recordStep(step)
        when:
        reporter.setQualifier("qualifier")
        def reportWithQualifier = reporter.generateReportFor(testOutcome)
        and:
        reporter.setQualifier(null)
        def reportWithoutQualifier = reporter.generateReportFor(testOutcome)
        then:
        reportWithQualifier != reportWithoutQualifier
    }

    def "should include error message for failing test"() {

        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        def step = TestStepFactory.failingTestStepCalled("step 1")
        and:
        step.failedWith(new IllegalArgumentException("Oh nose!"))
        testOutcome.recordStep(step)
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        def generatedReportText = jsonReport.text
        then:
        generatedReportText.contains "Oh nose!"
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.isError()
        reloadedOutcome.testSteps[0].errorMessage.contains "Oh nose!"
    }

    def "should generate a qualified JSON report for an acceptance test run if the qualifier is specified"() {

        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        reporter.setQualifier("qualifier")
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.title == "A simple test case [qualifier]"
    }

    def "should generate a qualified JSON report with formatted parameters if the qualifier is specified"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        reporter.setQualifier("a_b")

        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.title == "A simple test case [a_b]"
    }


    def "should record test groups as nested structures"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)

        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 3").startingAt(FIRST_OF_JANUARY))
        testOutcome.endGroup()
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps.size() == 1
        reloadedOutcome.testSteps[0].children.size() == 3

    }

    def "should record minimal nested test groups as nested structures"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1.1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Group 1.1.1").startingAt(FIRST_OF_JANUARY))
        testOutcome.startGroup()
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        testOutcome.endGroup()
        testOutcome.endGroup()
        testOutcome.endGroup()
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps.size() == 1
        reloadedOutcome.testSteps[0].children.size() == 1
        reloadedOutcome.testSteps[0].children[0].children.size() == 1
    }


    def "should include the name of any screenshots where present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        def screenshot = new File(outputDirectory, "step_1.png")
        def source = new File(outputDirectory, "step_1.html")
        and:
        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY)
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source))
        testOutcome.recordStep(step1)
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps[0].screenshotCount == 1
        reloadedOutcome.testSteps[0].screenshots[0].screenshot.name.endsWith("step_1.png")
        reloadedOutcome.testSteps[0].screenshots[0].htmlSource.isPresent()
    }



    def "should include the name of any screenshots without html where present"() {
        given:
        def testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        def screenshot = new File(outputDirectory, "step_1.png")
        and:
        TestStep step1 = TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY)
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot))
        testOutcome.recordStep(step1)
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.testSteps[0].screenshotCount == 1
        reloadedOutcome.testSteps[0].screenshots[0].screenshot.name.endsWith("step_1.png")
        !reloadedOutcome.testSteps[0].screenshots[0].htmlSource.isPresent()
    }

    @Unroll
    def "should record test results for #result"() {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStep.forStepCalled("some step").withResult(result))
        when:
            def jsonReport = reporter.generateReportFor(testOutcome)
        then:
            TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
            reloadedOutcome.getResult() == result
        where:
        result << [ TestResult.SUCCESS, TestResult.FAILURE, TestResult.ERROR, TestResult.PENDING, TestResult.IGNORED ]
    }

    def "should record test results for test with failing steps"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS))
        testOutcome.lastStepFailedWith(new AssertionError("a failure"))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.getResult() == TestResult.FAILURE
        reloadedOutcome.errorMessage.contains "a failure"
    }

    def "should record test results for test with an error"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.recordStep(TestStep.forStepCalled("some step").withResult(TestResult.SUCCESS))
        testOutcome.lastStepFailedWith(new RuntimeException("an error"))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.getResult() == TestResult.ERROR
        reloadedOutcome.errorMessage.contains "an error"
    }

    def "should record test results for test with an error outside a step"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.setAnnotatedResult(TestResult.ERROR)
        testOutcome.determineTestFailureCause(new RuntimeException("an error"))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.getResult() == TestResult.ERROR
        reloadedOutcome.errorMessage == "an error"
    }


    def "should record test results for test with an failure outside a step"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.setAnnotatedResult(TestResult.FAILURE)
        testOutcome.determineTestFailureCause(new AssertionError("a failure"))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.getResult() == TestResult.FAILURE
        reloadedOutcome.errorMessage == "a failure"
        reloadedOutcome.testFailureCause.errorType == "java.lang.AssertionError"
    }


    def "should record test results for test with an assertion failure outside a step"() {
        given:
        def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.setAnnotatedResult(TestResult.FAILURE)
        testOutcome.determineTestFailureCause(new ComparisonFailure("a failure","1","2"))
        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        then:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        reloadedOutcome.getResult() == TestResult.FAILURE
        reloadedOutcome.errorMessage == "a failure expected:<[1]> but was:<[2]>"
        reloadedOutcome.testFailureCause.errorType == "org.junit.ComparisonFailure"
    }

    @Unroll
    def "should record test results for #result with no steps"() {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        testOutcome.setStartTime(FIRST_OF_JANUARY)
        testOutcome.setAnnotatedResult(result)
        when:
            def jsonReport = reporter.generateReportFor(testOutcome)
        then:
            TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
            reloadedOutcome.getResult() == result
        where:
            result << [TestResult.SUCCESS, TestResult.FAILURE, TestResult.ERROR, TestResult.PENDING, TestResult.IGNORED ]
    }


    def "should be able to write a test outcome as a JSON string"() {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        when:
            def jsonString = testOutcome.toJson()
        then:
            def savedOutcome = new File(outputDirectory,"saved.json")
            savedOutcome << jsonString
            TestOutcome reloadedOutcome = loader.loadReportFrom(savedOutcome).get()
            reloadedOutcome.name == testOutcome.name
    }

    def "should be able to store REST query data if present"() {
        given:
            def testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class)
        def testStep = TestStep.forStepCalled("rest step").withResult(TestResult.SUCCESS)
            testStep.recordRestQuery(RestQuery.withMethod(RestMethod.GET).andPath("/foo/{id}").withParameters(["1"]))
            testOutcome.recordStep(testStep)
        when:
            def jsonString = testOutcome.toJson()
        then:
            def savedOutcome = new File(outputDirectory,"saved.json")
            savedOutcome << jsonString
            TestOutcome reloadedOutcome = loader.loadReportFrom(savedOutcome).get()
            reloadedOutcome.hasRestQueries()
    }


    def "should throw a violation exception if the json file is badly formed"() {
        given:
            def savedOutcome = new File(outputDirectory,"saved.json")
            savedOutcome << """{"firstname":"joe","lastname":"smith"}"""
        when:
            def loadedOutcome = loader.loadReportFrom(savedOutcome)
        then:
            !loadedOutcome.isPresent()
    }


    def "should read a test outcome with tags"() {
        given:
        def serializedTestOutcome =
                """{"name":"Another Addition","testSteps":[{"number":1,"description":"Given a calculator I just turned on","duration":4,"startTime":1413519392021,"result":"SUCCESS"},{"number":2,"description":"When I add 4 and 7","duration":3,"startTime":1413519392025,"result":"SUCCESS"},{"number":3,"description":"Then the result is 11","duration":2,"startTime":1413519392028,"result":"SUCCESS"}],"userStory":{"id":"basic-arithmetic","storyName":"Basic Arithmetic","path":"src/test/resources/samples/calculator/basic_arithmetic.feature","narrative":"Calculing additions","type":"feature"},"title":"Another Addition","tags":[{"name":"ISSUE-123","type":"issue"},{"name":"foo","type":"tag"},{"name":"Calculator/Basic arithmetic","type":"story"},{"name":"Basic Arithmetic","type":"feature"}],"startTime":1413519392028,"duration":10,"manual":false,"result":"SUCCESS"}"""

        def jsonReport = new File(outputDirectory,"result.json")
        jsonReport.withWriter{ it << serializedTestOutcome }

        when:
        TestOutcome loadedOutcome = loader.loadReportFrom(jsonReport).get()

        then:
        loadedOutcome.tags
    }

    def "should include test source in the JSON report"() {
        given:
        def testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class)
        testOutcome.startTime = FIRST_OF_JANUARY
        testOutcome.setTestSource(TestSourceType.TEST_SOURCE_JUNIT.getValue())

        when:
        def jsonReport = reporter.generateReportFor(testOutcome)
        and:
        TestOutcome reloadedOutcome = loader.loadReportFrom(jsonReport).get()
        then:
        reloadedOutcome.getTestSource() == TestSourceType.TEST_SOURCE_JUNIT.getValue()
    }

}
