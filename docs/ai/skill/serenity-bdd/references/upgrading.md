# Upgrading to Serenity 5.x, and recognising old code

Verified against Serenity **5.4.1**. The class counts below come from the released jars (`../../upgrade-checks/moves.py`). The upgrade recipe was applied to five real projects from Serenity 2.1, 2.4, 3.0, 3.3 and 4.2 (results in section 4). `../../upgrade-checks/verify.py` re-checks the counts, the moved-class table, the JUnit 4 claims and the artifact names.

## 1. What changed, by release

| Coming from | Java needed | What you must change |
|---|---|---|
| 4.x (tested: 4.2.34) | Java **17** (4.x classes are Java 11, 5.x classes are Java 17) | Versions only. All 1,587 public classes in the core, model, JUnit, Screenplay (including WebDriver and REST), REST-assured, Cucumber and Ensure jars have the same name and package in 5.4.1. A 4.2.8 project compiled with only a version bump. Align the JUnit and Cucumber versions with Serenity's (section 3). |
| 3.x (tested: 3.9.8) | Java 17 (3.x classes are Java 8) | Version bump, **511 of 1,574 classes moved** (section 2), the artifact changes in section 3, and the fixes for removed API found by the compiler. |
| 2.x | Java 17 | The same as 3.x, plus renamed Cucumber artifacts. |

Serenity 5.4.1 depends on **JUnit 6.1.3** and **Cucumber 8.0.4** (`../references/project-setup.md`).

## 2. Classes that moved (3.x to 5.x)

Of the 1,574 public classes in the 3.9.8 jars: 1,051 are unchanged, 511 moved to exactly one new place, 7 have a same-named class in more than one place, 5 are gone. Many `net.thucydides` packages are still correct (for example `net.thucydides.core.steps.ScenarioSteps`, `net.thucydides.core.pages.Pages`, and the 47 classes in `net.thucydides.core.steps` that did not move): only the classes in the table moved. All of `net.serenitybdd.screenplay.*`, `net.serenitybdd.core.pages`, `net.serenitybdd.junit.runners` and `net.thucydides.core.webdriver` (except two classes) kept their packages.

<!-- moved-table:start -->
| Old package (3.x) | New package | Classes |
|---|---|---|
| `net.serenitybdd.core` | `net.serenitybdd.model` | `CurrentOS`, `IgnoredStepException`, `PendingStepException`, `SerenitySystemProperties`, `SkipStepException` |
| `net.serenitybdd.core.buildinfo` | `net.serenitybdd.model.buildinfo` | `BuildInfoProvider`, `BuildProperties`, `DriverCapabilityRecord`, `PropertyBasedDriverCapabilityRecord` |
| `net.serenitybdd.core.collect` | `net.serenitybdd.model.collect` | `NewList`, `NewMap`, `NewSet` |
| `net.serenitybdd.core.di` | `net.serenitybdd.model.di` | `DependencyInjector`, `ModelInfrastructure` |
| `net.serenitybdd.core.environment` | `net.serenitybdd.model.environment` | `ConfiguredEnvironment`, `EnvironmentSpecificConfiguration`, `UndefinedEnvironmentVariableException`, `UnknownEnvironmentException` |
| `net.serenitybdd.core.exceptions` | `net.serenitybdd.model.exceptions` | `CausesAssertionFailure`, `CausesCompromisedTestFailure`, `NoException`, `SerenityManagedException`, `TestCompromisedException`, `TheErrorType`, `UnrecognisedException` |
| `net.serenitybdd.core.history` | `net.serenitybdd.model.history` | `ClearDirectoryContents`, `FileSystemTestOutcomeSummaryRecorder`, `HistoricalFlagProvider`, `LeaveDirectoryContents`, `NewFailure`, `PrepareHistoryDirectory`, `PreviousTestOutcome`, `TestOutcomeSummaryRecorder` |
| `net.serenitybdd.core.i8n` | `net.serenitybdd.model.i8n` | `LocalisedLabels` |
| `net.serenitybdd.core.model` | `net.serenitybdd.model.model` | `FailureDetails`, `TestOutcomeName` |
| `net.serenitybdd.core.rest` | `net.serenitybdd.model.rest` | `RestMethod`, `RestQuery` |
| `net.serenitybdd.core.strings` | `net.serenitybdd.model.strings` | `FirstLine`, `Joiner` |
| `net.serenitybdd.core.tags` | `net.serenitybdd.model.tags` | `EnvironmentDefinedTags` |
| `net.serenitybdd.core.time` | `net.serenitybdd.model.time` | `InternalSystemClock`, `Stopwatch`, `SystemClock` |
| `net.serenitybdd.core.webdriver.driverproviders` | `net.serenitybdd.core.webdriver.servicepools` | `DriverServices` |
| `net.thucydides.core` | `net.thucydides.model` | `ThucydidesSystemProperty` |
| `net.thucydides.core.adapters` | `net.thucydides.model.adapters` | `JUnitStrategy`, `MultiStrategyAdapter`, `TestFramework`, `TestStrategyAdapter` |
| `net.thucydides.core.adapters.legacy` | `net.thucydides.model.adapters.legacy` | `DefaultJUnitAdapter`, `DefaultJUnitStrategy` |
| `net.thucydides.core.annotations` | `net.serenitybdd.annotations` | `AnnotatedDescription`, `At`, `BlurScreenshots`, `ClearCookiesPolicy`, `DefaultUrl`, `Description`, `DriverOptions`, `Feature`, `Fields`, `Issue`, `Issues`, `Managed`, `ManagedPages`, `Manual`, `NamedUrl`, `NamedUrls`, `Narrative`, `Pending`, `Reported`, `Screenshots`, `SectionNumber`, `Shared`, `SingleBrowser`, `Step`, `StepGroup`, `Steps`, `TestAnnotations`, `TestsRequirement`, `TestsRequirements`, `Title`, `UsePersistantStepLibraries`, `UserStoryCode`, `Version`, `WhenPageOpens`, `WithDriver`, `WithTag`, `WithTagValuesOf`, `WithTags` |
| `net.thucydides.core.batches` | `net.thucydides.model.batches` | `BatchManager`, `BatchStrategy`, `SystemVariableBasedBatchManager`, `TestCountBasedBatchManager`, `UnsupportedBatchStrategyException` |
| `net.thucydides.core.configuration` | `net.thucydides.model.configuration` | `BuildDirectoryProvider`, `FilePathParser`, `MavenBuildDirectory`, `MavenOrGradleBuildPath`, `SessionLocalTempDirectory`, `SystemPropertiesConfiguration`, `TimeoutConfiguration`, `TimeoutValue` |
| `net.thucydides.core.digest` | `net.thucydides.model.digest` | `Digest` |
| `net.thucydides.core.environment` | `net.thucydides.model.environment` | `MockEnvironmentVariables`, `SystemEnvironmentVariables`, `TestLocalEnvironmentVariables` |
| `net.thucydides.core.files` | `net.thucydides.model.files` | `TheDirectoryStructure` |
| `net.thucydides.core.images` | `net.thucydides.model.images` | `Refit`, `ResizableImage`, `ResizedImage` |
| `net.thucydides.core.issues` | `net.thucydides.model.issues` | `IssueKeyFormat`, `IssueTracking`, `SystemPropertiesIssueTracking` |
| `net.thucydides.core.logging` | `net.thucydides.model.logging` | `ConsoleColors`, `ConsoleEvent`, `ConsoleHeading`, `ConsoleHeadingStyle`, `ConsoleLoggingListener`, `LoggingLevel` |
| `net.thucydides.core.matchers` | `net.thucydides.model.matchers` | `BeanCollectionMatcher`, `BeanCountMatcher`, `BeanFieldMatcher`, `BeanMatcher`, `BeanMatcherAsserts`, `BeanMatchers`, `BeanPropertyMatcher`, `BeanUniquenessMatcher`, `BigDecimalValueMatcher`, `FileMatchers`, `InstantiatedBeanMatcher`, `MaxFieldValueMatcher`, `MinFieldValueMatcher`, `PublicThucydidesMatchers`, `ScreenshotHasFilenamesMatcher`, `SimpleValueMatcher`, `TestOutcomeResultsMatcher`, `ThucydidesMatchers` |
| `net.thucydides.core.matchers.dates` | `net.thucydides.model.matchers.dates` | `BeanFields`, `DateCollectionContainsSameDatesMatcher`, `DateComparator`, `DateIsAfterMatcher`, `DateIsBeforeMatcher`, `DateIsBetweenMatcher`, `DateIsSameAsMatcher`, `DateMatcherFormatter`, `DateMatchers`, `DateTimeCollectionContainsSameDatesMatcher`, `DateTimeIsAfterMatcher`, `DateTimeIsBeforeMatcher`, `DateTimeIsBetweenMatcher`, `DateTimeIsCloseToAsMatcher`, `DateTimeIsSameAsMatcher`, `DateTimeIsSameTimeAsMatcher`, `TimeIsCloseToAsMatcher` |
| `net.thucydides.core.model` | `net.thucydides.model.domain` | `AnnotatedStoryTitle`, `BadgeBackground`, `ContextIcon`, `DataSet`, `DataSetDescriptor`, `DataTable`, `DataTableRow`, `Downloadables`, `ErrorMessageFormatter`, `ExternalLink`, `FeatureTagAsDefined`, `HeuristicTestResult`, `LastElement`, `ManualTestEvidence`, `NumericalFormatter`, `OutcomeCounter`, `PackageBasedLeafRequirements`, `PathElement`, `PathElementResolver`, `ReportData`, `ReportNamer`, `ReportType`, `RequirementCache`, `Rule`, `RuleBackground`, `ScenarioOutcomeCounter`, `Stories`, `TakeScreenshots`, `TestCaseOrder`, `TestClassHierarchy`, `TestDuration`, `TestFailureException`, `TestOutcome`, `TestOutcomeAnnotationReader`, `TestOutcomeSummary`, `TestResult`, `TestResultComparison`, `TestResultList`, `TestStep`, `TestTag`, `TestTagCache`, `TestTags`, `TestType`, `TitleBuilder` |
| `net.thucydides.core.model.failures` | `net.thucydides.model.domain.failures` | `FailureAnalysis`, `FailureAnalysisConfiguration`, `RootCause` |
| `net.thucydides.core.model.features` | `net.thucydides.model.domain.features` | `ApplicationFeature` |
| `net.thucydides.core.model.featuretags` | `net.thucydides.model.domain.featuretags` | `FeatureFileStrategy`, `FeatureTagStrategy`, `FeatureTitle`, `NoFeatureStrategy`, `StoryFileStrategy` |
| `net.thucydides.core.model.flags` | `net.thucydides.model.domain.flags` | `Flag`, `FlagCounts`, `FlagProvider` |
| `net.thucydides.core.model.formatters` | `net.thucydides.model.domain.formatters` | `IssueExtractor`, `ReportFormatter`, `TestCoverageFormatter` |
| `net.thucydides.core.model.results` | `net.thucydides.model.domain.results` | `MergeStepResultStrategy`, `NextStepCompromisedStrategy`, `NextStepFailedStrategy`, `NextStepStatusTakesPriorityStrategy`, `NextStepWasSkippedStrategy`, `StepResultMergeStragegy` |
| `net.thucydides.core.model.screenshots` | `net.thucydides.model.domain.screenshots` | `Screenshot`, `ScreenshotPreferencesByClass` |
| `net.thucydides.core.model.stacktrace` | `net.thucydides.model.domain.stacktrace` | `FailureCause`, `RootCauseAnalyzer`, `StackTraceSanitizer` |
| `net.thucydides.core.output` | `net.thucydides.model.output` | `ResultsOutput`, `SpreadsheetResultsOutput` |
| `net.thucydides.core.reflection` | `net.thucydides.model.reflection` | `ClassFinder`, `FieldFinder`, `FieldSetter`, `FieldValue`, `MethodFinder`, `StackTraceAnalyser` |
| `net.thucydides.core.releases` | `net.thucydides.model.releases` | `ReleaseManager` |
| `net.thucydides.core.reports` | `net.thucydides.model.reports` | `AcceptanceTestFullReporter`, `AcceptanceTestLoader`, `AcceptanceTestReporter`, `AnsiEscapes`, `AsciiColors`, `FlagsAugmenter`, `FormatConfiguration`, `JiraUpdaterService`, `NumberOfThreads`, `OutcomeAugmenter`, `OutcomeFormat`, `OutcomeSummary`, `ReportGenerationFailedError`, `ReportLoadingFailedError`, `ReportOptions`, `ReportService`, `ReporterRuntime`, `ResultChecker`, `SystemReporterRuntime`, `TestOutcomeCounter`, `TestOutcomeLoader`, `TestOutcomeStream`, `TestOutcomes`, `TestOutcomesCompromised`, `TestOutcomesError`, `TestOutcomesFailures`, `ThucydidesReporter`, `UserStoryTestReporter` |
| `net.thucydides.core.reports.adaptors` | `net.thucydides.model.reports.adaptors` | `AdaptorService`, `TestOutcomeAdaptor`, `UnknownAdaptor` |
| `net.thucydides.core.reports.adaptors.common` | `net.thucydides.model.reports.adaptors.common` | `FilebasedOutcomeAdaptor` |
| `net.thucydides.core.reports.adaptors.lettuce` | `net.thucydides.model.reports.adaptors.lettuce` | `LettuceXUnitAdaptor` |
| `net.thucydides.core.reports.adaptors.specflow` | `net.thucydides.model.reports.adaptors.specflow` | `ScenarioSplitter`, `ScenarioStep`, `ScenarioStepReader`, `SpecflowAdaptor`, `SpecflowScenario`, `SpecflowScenarioTitleLine`, `SpecflowTableRow` |
| `net.thucydides.core.reports.adaptors.xunit` | `net.thucydides.model.reports.adaptors.xunit` | `BasicXUnitLoader`, `CouldNotReadXUnitFileException`, `DefaultXUnitAdaptor`, `XUnitLoader` |
| `net.thucydides.core.reports.adaptors.xunit.io` | `net.thucydides.model.reports.adaptors.xunit.io` | `XUnitFiles` |
| `net.thucydides.core.reports.adaptors.xunit.model` | `net.thucydides.model.reports.adaptors.xunit.model` | `Skip`, `TestCase`, `TestError`, `TestException`, `TestSuite` |
| `net.thucydides.core.reports.csv` | `net.thucydides.model.reports.csv` | `CSVReporter` |
| `net.thucydides.core.reports.history` | `net.thucydides.model.reports.history` | `DateProvider`, `ProgressSnapshot`, `SystemDateProvider`, `TestResultSnapshot` |
| `net.thucydides.core.reports.html` | `net.thucydides.model.reports.html` | `ContextIconFormatter`, `CucumberCompatibleFilter`, `CucumberTagConverter`, `DescriptionSplitter`, `ExampleTable`, `MarkdownRendering`, `ReportNameProvider`, `RequirementsFilter`, `ResultCounts`, `ResultIconFormatter`, `ScenarioResultCounts`, `TagExclusions`, `TagFilter`, `TagMatch`, `TestOutcomeFilter` |
| `net.thucydides.core.reports.integration` | `net.thucydides.model.reports.integration` | `TestStepFactory` |
| `net.thucydides.core.reports.json` | `net.thucydides.model.reports.json` | `AScenarioHasNoNameException`, `JSONConverter`, `JSONTestOutcomeReporter` |
| `net.thucydides.core.reports.json.gson` | `net.thucydides.model.reports.json.gson` | `ClassTypeAdapter`, `CollectionAdapter`, `FileDeserializer`, `FileSerializer`, `GsonJSONConverter`, `GsonPreviousOutcomeConverter`, `InterfaceAdapter`, `OptionalTypeAdapter`, `StackTraceElementDeserializer`, `StackTraceElementSerializer`, `ZonedDateTimeAdapter` |
| `net.thucydides.core.reports.junit` | `net.thucydides.model.reports.junit` | `JUnitXMLConverter`, `JUnitXMLOutcomeReporter` |
| `net.thucydides.core.reports.matchers` | `net.thucydides.model.reports.matchers` | `TestOutcomeMatchers` |
| `net.thucydides.core.reports.remoteTesting` | `net.thucydides.model.reports.remoteTesting` | `BrowserStackLinkGenerator`, `LinkGenerator`, `RemoteTestingLinkManager` |
| `net.thucydides.core.reports.templates` | `net.thucydides.model.reports.templates` | `FreeMarkerTemplateManager`, `FreemarkerReportTemplate`, `ReportTemplate`, `TemplateManager`, `TemplateMergeException` |
| `net.thucydides.core.reports.util` | `net.thucydides.model.reports.util` | `CopyDirectory` |
| `net.thucydides.core.reports.xml` | `net.thucydides.model.reports.xml` | `NotAThucydidesReportException`, `TestOutcomeConverter` |
| `net.thucydides.core.requirements` | `net.thucydides.model.requirements` | `AbstractRequirementsTagProvider`, `AllRequirements`, `BaseRequirementsService`, `ChildrenFirstOrderedMap`, `ClasspathRequirementsProviderService`, `CoreTagProvider`, `CouldNotLoadRequirementsException`, `DefaultCapabilityTypes`, `DefaultRequirements`, `DisabledRequirementsStore`, `ExcludedUnrelatedRequirementTypes`, `FeatureFilePath`, `FileSystemRequirements`, `FileSystemRequirementsService`, `FileSystemRequirementsStore`, `FileSystemRequirementsTagProvider`, `JSONRequirementsTree`, `MultiSourceRequirementsService`, `OverridableTagProvider`, `PackageAnnotationBasedTagProvider`, `PackageRequirementsTagProvider`, `ParentRequirementProvider`, `ParentRequirementsProvided`, `PathStartsWith`, `ReleaseProvider`, `RequirementAncestry`, `RequirementPersister`, `RequirementTypesProvider`, `Requirements`, `RequirementsList`, `RequirementsMerger`, `RequirementsPath`, `RequirementsProvided`, `RequirementsProviderService`, `RequirementsService`, `RequirementsStore`, `RequirementsTagProvider`, `RequirementsTree`, `RootDirectory`, `SearchForFilesOfType`, `SearchForFilesWithName`, `SerenityTestCaseFinder`, `TestOutcomeRequirementsTagProvider`, `WindowsFriendly` |
| `net.thucydides.core.requirements.annotations` | `net.thucydides.model.requirements.annotations` | `ClassInfoAnnotations`, `NarrativeFinder` |
| `net.thucydides.core.requirements.classpath` | `net.thucydides.model.requirements.classpath` | `AnnotatedTags`, `ChildElementAdder`, `ClassNarrative`, `LeafRequirementAdder`, `NarrativeTitle`, `NonLeafRequirementsAdder`, `PackageInfoClass`, `PackageInfoNarrative` |
| `net.thucydides.core.requirements.model` | `net.thucydides.model.requirements.model` | `CustomFieldValue`, `Example`, `ExampleBuilder`, `FeatureBackgroundNarrative`, `FeatureType`, `LoadedNarrative`, `NarrativeReader`, `OverviewReader`, `Requirement`, `RequirementBuilderNameStep`, `RequirementBuilderTypeStep`, `RequirementDefinition`, `RequirementTree`, `RequirementTypeAt`, `RequirementsConfiguration` |
| `net.thucydides.core.requirements.model.cucumber` | `net.thucydides.model.requirements.model.cucumber` | `AnnotatedFeature`, `BlankRowResultIcon`, `CucumberParser`, `DescriptionWithScenarioReferences`, `ExampleRowResultIcon`, `ExampleTableInMarkdown`, `FeatureFileAnaysisErrors`, `FeatureFileChecker`, `FeatureFileFinder`, `IdentifiedExampleTable`, `IdentifiedScenario`, `InvalidFeatureFileException`, `NamedExampleTable`, `NamedScenario`, `NarrativeFromCucumberComments`, `ReferencedExampleTable`, `ReferencedScenario`, `RenderedExampleTable`, `RowResultIcon`, `ScenarioDisplayOption`, `ScenarioReport`, `SummaryRowResultIcon`, `URIResource`, `UnknownExampleTable`, `UnknownScenario` |
| `net.thucydides.core.requirements.reports` | `net.thucydides.model.requirements.reports` | `AcceptanceCriteriaRequirmentCounter`, `ChildRequirementCounter`, `CompoundDuration`, `ExampleOutcome`, `ExampleOutcomes`, `FileSystemRequirmentsOutcomeFactory`, `MultipleSourceRequirmentsOutcomeFactory`, `PercentageRequirementCounter`, `RenderMarkdown`, `ReportBadges`, `RequirementOutcome`, `RequirementsOutcomeFactory`, `RequirementsOutcomes`, `RequirementsOutcomesOfTypeCache`, `RequirementsPercentageFormatter`, `RequirementsProportionCounter`, `RequirmentCalculator`, `ScenarioOutcome`, `ScenarioOutcomes`, `ScenarioSummaryOutcome`, `SingleScenarioOutcome`, `SubrequirementsCount`, `SubrequirementsPercentageCount`, `SubrequirementsProportionCount` |
| `net.thucydides.core.requirements.reports.cucumber` | `net.thucydides.model.requirements.reports.cucumber` | `FeatureCache`, `FeatureFileScenarioOutcomes`, `OptionalAnnotatedFeature`, `RenderCucumber` |
| `net.thucydides.core.requirements.tree` | `net.thucydides.model.requirements.tree` | `Node` |
| `net.thucydides.core.resources` | `net.thucydides.model.resources` | `FileResources`, `ResourceCopyingError`, `ResourceList` |
| `net.thucydides.core.screenshots` | `net.serenitybdd.annotations` | `BlurLevel` |
| `net.thucydides.core.screenshots` | `net.thucydides.model.screenshots` | `ScreenshotAndHtmlSource`, `ScreenshotException` |
| `net.thucydides.core.statistics` | `net.thucydides.model.statistics` | `AtomicTestCount`, `TestCount` |
| `net.thucydides.core.statistics.service` | `net.thucydides.model.statistics.service` | `AnnotationBasedTagProvider`, `ClasspathTagProviderService`, `ContextTagProvider`, `FeatureStoryTagProvider`, `InjectedTagProvider`, `JUnitTagProviderStrategy`, `TagProvider`, `TagProviderFilter`, `TagProviderService`, `TagProviderStrategy` |
| `net.thucydides.core.steps` | `net.thucydides.model.steps` | `AnnotatedStepDescription`, `ExecutedStepDescription`, `FailingManualTestException`, `MetaField`, `ReplaceField`, `ScreenplayInspector`, `StepArgumentWriter`, `StepFailure`, `StepFailureException`, `StepListener`, `TestFailureCause`, `TestSourceType`, `TestStatus` |
| `net.thucydides.core.steps.di` | `net.thucydides.model.steps.di` | `ClasspathDependencyInjectorService`, `DependencyInjectorService` |
| `net.thucydides.core.steps.events` | `net.serenitybdd.cucumber.events` | `SetTestManualEvent` |
| `net.thucydides.core.tags` | `net.thucydides.model.tags` | `BreadcrumbTagFilter`, `OutcomeTagFilter`, `TagConverters`, `TagScanner`, `Taggable` |
| `net.thucydides.core.util` | `net.thucydides.model.util` | `Acronym`, `ConfigCache`, `EnvironmentVariables`, `FileSystemUtils`, `Inflection`, `Inflector`, `LocalPreferences`, `MultipleInflection`, `NameConverter`, `PropertiesLocalPreferences`, `PropertiesUtil`, `TagInflector`, `TestResources`, `VersionProvider` |
| `net.thucydides.core.webdriver` | `net.thucydides.model.webdriver` | `Configuration`, `WebdriverAssertionError` |
<!-- moved-table:end -->

The annotations are the ones most code touches: `Step`, `Steps`, `Managed`, `DefaultUrl`, `Title`, `Narrative`, `Pending`, `WithTag`, `Issue` and the rest of `net.thucydides.core.annotations` are now in **`net.serenitybdd.annotations`**. `EnvironmentVariables` is in `net.thucydides.model.util`; `EnvironmentSpecificConfiguration` is in `net.serenitybdd.model.environment`.

Ambiguous: `Story` (`net.serenitybdd.annotations.Story` for the annotation, `net.thucydides.model.domain.Story` for the model class), `CastMember` (`net.serenitybdd.screenplay.annotations.CastMember` for Screenplay), `Release`, `PathElements` and `TagParser`: decide by what the code uses it for. Gone: the WebDriverManager integration classes (`WebDriverManagerSetup`, `WebDriverManagerEnhancer`, `DefaultWebDriverManagerEnhancer`; drivers are found by Selenium itself now), `MergedRequirementList` and `RecordStepResultEvent`.

**Rewriting imports automatically:** `../../upgrade-checks/fix_imports.py PROJECT_DIR` rewrites every class in the table (imports, fully qualified names, wildcard imports) and lists the ambiguous and removed classes it found, for a person to decide.

## 3. The upgrade recipe

Each step was needed by at least one of the five tested projects.

1. **Java 17** or later for the build and the tests.
2. **Set the Serenity version** everywhere it appears (`serenity.version`, the Maven plugin version, `serenity.cucumber.version`).
3. **Artifact names.** `serenity-cucumber4`, `serenity-cucumber5` and `serenity-cucumber6` stopped at 2.1.2, 2.2.6 and 2.6.0: use **`serenity-cucumber`**. The JUnit 4 runner (`net.serenitybdd.junit.runners.SerenityRunner`) lives in **`serenity-junit`**, which a 3.x project got through `serenity-core`: add it, with JUnit 4.13.2, if you keep JUnit 4 tests. `serenity-jbehave` has no 5.x release (its last version is 1.46.0): JBehave stories need migrating to Cucumber first. Check any other `serenity-*` artifact on Maven Central before relying on it.
4. **Remove versions that Serenity manages.** Pins that were right for the old Serenity break the new one at run time, not at compile time:
   - Cucumber: a pinned `cucumber-java`, `cucumber-junit` or an `<exclusion>` of `cucumber-core` gives `NoClassDefFoundError: io/cucumber/core/eventbus/EventBus` (a project with Cucumber 4.8.1 and the exclusion). Use 8.0.4, or none, and delete the exclusion.
   - REST-assured and Groovy: pinned `io.rest-assured:*` and `org.codehaus.groovy:groovy*` (Groovy 3) give `Conflicting module versions. Module [groovy-xml is loaded in version 3.0.6 and you are trying to load version 4.0.29`. Delete them; `serenity-rest-assured` brings the right ones.
   - JUnit: see the troubleshooting reference ("conflicting versions"); import the JUnit BOM.
5. **Imports.** Run `fix_imports.py` (section 2).
6. **Configuration clean-up** (`../references/configuration.md`):
   - Delete a `drivers { mac { webdriver.chrome.driver = "src/test/resources/webdriver/..." } }` block of bundled driver binaries. A 2020 chromedriver cannot drive a current Chrome, and the symptom is `Could not instantiate class org.openqa.selenium.chrome.ChromeDriver ... chromedriver must be executable`. With the block removed, Chrome started.
   - `screenshooter=shutterbug` needs the separate `serenity-shutterbug` artifact; otherwise the test fails with `Unable to instantiate screen shooter net.serenitybdd.screenshots.shutterbug.ShutterbugScreenShooter`. Delete the line (default screenshots) or add the artifact.
7. **Compile from clean:** `mvn clean test-compile`. Without `clean`, Maven keeps classes compiled against the old libraries: a `cucumber.api.CucumberOptions` import compiled for as long as the stale class file existed, and the runner then silently ran **0 scenarios**.
8. **Fix removed or renamed API** (the compiler lists it). Found in the tested projects: `InputField.called("x")` and `PageElement.called("x")` (gone by 3.9.8) became `InputField.withNameOrId("x")` and `PageElement.withNameOrId("x")`. `cucumber.api.CucumberOptions` became `io.cucumber.junit.CucumberOptions`, and `cucumber.api.java.en.*` became `io.cucumber.java.en.*` (the `cucumber.api` package does not exist in Cucumber 8).
9. **Run the tests.** Failures that remain may belong to the application under test: three of the five old projects' remaining failures were a changed page (a search box and a to-do input that no longer exist).

## 4. Evidence: five real projects

| Project (Serenity version) | Result |
|---|---|
| REST starter with Cucumber (2.4.51) | Version, `serenity-cucumber6` to `serenity-cucumber`, 2 class references rewritten, REST-assured and Groovy pins removed: **3 of 3 scenarios pass**. |
| Cucumber 4 starter (2.1.12) | Version, 4 class references rewritten, Cucumber 4.8.1 and the exclusion replaced by 8.0.4, runner import fixed, bundled drivers removed: compiles and the 4 scenarios run in Chrome; they fail on the DuckDuckGo search box, which has changed. |
| Screenplay with JUnit 4 and Cucumber, todo app (3.3.0) | Version, `serenity-junit` added, 6 class references rewritten: compiles; 4 of 10 scenarios pass, 6 fail on a to-do input that one of the sample apps no longer has. |
| Screenplay quickstart (3.0.2) | Compiles after `called` became `withNameOrId`; the run needed the `shutterbug` line removed, then fails on Wikipedia's changed search box (`element not interactable`). |
| Toolshop (4.2.8) | Compiles with only the version bump. Its tests were not run. |

## 5. JUnit 4 and the old Cucumber runner still work

You do not have to move to JUnit 5. With `serenity-junit` and JUnit 4.13.2, `@RunWith(SerenityRunner.class)` with `@Steps` ran on 5.4.1 (1 test passed), and so did `@RunWith(CucumberWithSerenity.class)` with `@CucumberOptions(features = ..., glue = ...)` from `io.cucumber.junit` (3 scenarios passed). Use the JUnit 5 extension and the JUnit Platform suite for new code (`../references/cucumber.md`); do not rewrite a working JUnit 4 suite only to follow it. Keep one style per project.

## 6. Recognising old code in generated or copied examples

| Old | Current |
|---|---|
| `import net.thucydides.core.annotations.{Step,Steps,Managed,DefaultUrl,...}` | `net.serenitybdd.annotations.*` |
| `net.thucydides.core.util.EnvironmentVariables` | `net.thucydides.model.util.EnvironmentVariables` |
| `net.serenitybdd.core.environment.EnvironmentSpecificConfiguration` | `net.serenitybdd.model.environment.EnvironmentSpecificConfiguration` |
| `serenity-cucumber4/5/6`, `cucumber.api.*` | `serenity-cucumber`, `io.cucumber.*` |
| `InputField.called(..)`, `PageElement.called(..)` | `InputField.withNameOrId(..)`, `PageElement.withNameOrId(..)` |
| `drivers { os { webdriver.chrome.driver = ... } }` with bundled binaries | nothing: leave driver selection to Serenity and Selenium |
| `serenity-jbehave` stories | no 5.x release: migrate to Cucumber |
| `@RunWith(SerenityRunner.class)` | still valid with `serenity-junit` and JUnit 4; for JUnit 5 use `@ExtendWith(SerenityJUnit5Extension.class)` |
| `net.thucydides.core.steps.ScenarioSteps`, `net.thucydides.core.pages.Pages` | **not old**: still in those packages |

## 7. Not covered

Gradle plugin version history, Serenity 1.x, Appium and mobile modules, the Spring, JIRA and cloud-provider modules, and Selenium 3 to 4 API changes outside Serenity's own classes.
