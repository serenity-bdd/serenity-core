package net.thucydides.core.reports

import net.thucydides.core.model.*
import net.thucydides.core.model.features.ApplicationFeature
import net.thucydides.core.reports.json.JSONTestOutcomeReporter
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter
import org.joda.time.DateTime
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadLocalRandom

class WhenWritingAndReadingTestOutcomes extends Specification {

    @Rule
    final TemporaryFolder temporary = new TemporaryFolder()

    def loader = new TestOutcomeLoader()

    def "should be possible write and load reports using different formats"(
        def OutcomeFormat format, def AcceptanceTestReporter reporter) {
        given:
            def Set<TestOutcome> outcomes = generate(100)
            def directory = temporary.getRoot()
            Collection<AcceptanceTestReporter> reporters = new LinkedList<>()
            reporters.add(reporter)
            def TestOutcomeLoader loader = new TestOutcomeLoader().forFormat(format);
        when:
            def ReportService service = new ReportService(directory, reporters)
            service.generateReportsFor(new ArrayList<TestOutcome>(outcomes))
            def Set<TestOutcome> result = loader.loadFrom(directory)
            def notFound = {
                outcomes.removeAll(result)
                outcomes.size()
            }
            def int generated = outcomes.size()
            def int read = result.size()
        then:
            generated == read
            0 == notFound.call()
        where:
            format             | reporter
            OutcomeFormat.XML  | new XMLTestOutcomeReporter()
            OutcomeFormat.JSON | new JSONTestOutcomeReporter()

    }

    @Ignore("Deprecated")
    def "should be possible generate and load reports concurrently"(
        def AcceptanceTestReporter reporter, def AcceptanceTestLoader loader) {
        given:
            def directory = temporary.getRoot()
            def processors = Runtime.runtime.availableProcessors()
            def Set<TestOutcome> outcomes = generate(processors * 5)
            def TestOutcomes testOutcomes = TestOutcomes.of(new ArrayList<TestOutcome>(outcomes))
            reporter.setOutputDirectory(directory)
            def executor = Executors.newFixedThreadPool(processors);
            def List<Future<File>> result = new ArrayList<>()
            def List<Future> loading = new ArrayList<>()
            def List<Future<TestOutcome>> checked = new ArrayList<>()
            def List<File> created = new ArrayList<>()
            def List<TestOutcome> loaded = new ArrayList<>()
        when:
            outcomes.each { outcome ->
                result.add(executor.submit(new Callable<File>() {
                    @Override
                    File call() throws Exception {
                        reporter.generateReportFor(outcome, testOutcomes)
                    }
                }))
            }
            result.each { future ->
                created.add(future.get())
            }

            created.each { file ->
                loading.add(executor.submit(new Runnable() {
                    @Override
                    void run() {
                        Optional<TestOutcome> outcome = loader.loadReportFrom(file)
                        if (outcome.isPresent()) {
                            loaded.add(outcome.get())
                        }
                    }
                }))
            }
            loading.each {
                it.get()
            }

            def notFountOutcomes = {
                loaded.findAll { outcome ->
                    return !(outcomes.contains(outcome))
                }.size()
            }
        then:
            reporter.getFormat() == loader.getFormat() && notFountOutcomes.call() == 0
        where:
            reporter                      | loader
            new XMLTestOutcomeReporter()  | new XMLTestOutcomeReporter()
            new JSONTestOutcomeReporter() | new JSONTestOutcomeReporter()
    }


    def "should be possible generate and load reports in series"(
        def AcceptanceTestReporter reporter, def AcceptanceTestLoader loader) {
        given:
            def directory = temporary.getRoot()
            def Set<TestOutcome> outcomes = generate(10)
            reporter.setOutputDirectory(directory)
            def List<Future<File>> result = new ArrayList<>()
        when:
            100.times {
                outcomes.each { outcome ->
                    reporter.generateReportFor(outcome)
                }
            }
            def notFountOutcomes = {
                result.findAll { future ->
                    def File generated = future.get()
                    Optional<TestOutcome> outcome = loader.loadReportFrom(generated)
                    return !(outcome.isPresent() && outcomes.contains(outcome.get()))
                }.size()
            }
        then:
            reporter.getFormat() == loader.getFormat() && notFountOutcomes.call() == 0
        where:
            reporter                      | loader
            new XMLTestOutcomeReporter()  | new XMLTestOutcomeReporter()
            new JSONTestOutcomeReporter() | new JSONTestOutcomeReporter()
    }

    def private static List<TestOutcome> generate(def int amount) {
        List<TestOutcome> outcomes = new ArrayList<>(amount)
        amount.times {
            def method = word()
            def ThreadLocalRandom random = ThreadLocalRandom.current()
            def TestOutcome outcome = new TestOutcome(method)
            outcome.setStartTime(DateTime.now())
            outcome.setDuration(random.nextLong(10, 100))
            if (random.nextBoolean()) {
                outcome.asManualTest()
            }
            outcome.setTitle(word())

            (amount + 1).times {
                def TestStep step = new TestStep(DateTime.now(), word())
                step.setDuration(random.nextLong(10, 100))
                step.renumberFrom(it)
                outcome.recordStep(step)
            }

            outcome.setUserStory(new Story(
                word(),
                word(),
                word(),
                word(),
                new ApplicationFeature(word(), word())))
            (amount + 1).times {
                def TestTag tag = TestTag.withValue("${word()}:${word()}");
                outcome.addTag(tag)
            }
            if (random.nextBoolean()) {
                outcome.setAnnotatedResult(TestResult.FAILURE);
                outcome.setAllStepsTo(TestResult.FAILURE)
            } else {
                outcome.setAnnotatedResult(TestResult.SUCCESS);
                outcome.setAllStepsTo(TestResult.SUCCESS)
            }
            outcomes.add(outcome)
        }
        outcomes
    }

    def private static String word() {
        return UUID.randomUUID().toString()
    }
}