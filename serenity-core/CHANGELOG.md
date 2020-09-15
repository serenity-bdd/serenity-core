
# Serenity Core change log
## v1.7.1
### No issue
 * [109c56703749dc3](https://github.com/serenity-bdd/serenity-core/commit/109c56703749dc3) Cater for empty firefox preferences
 * [fbe109a8947ad81](https://github.com/serenity-bdd/serenity-core/commit/fbe109a8947ad81) Upgraded to Selenium 3.7.1, and some minor refactoring.
 * [ef7c0888b1d2dcb](https://github.com/serenity-bdd/serenity-core/commit/ef7c0888b1d2dcb) chore: replaced deprecated setUseCleanSession() with useCleanSession() in the Safari options.
## v1.7.0
### No issue
 * [7a8020159979ecd](https://github.com/serenity-bdd/serenity-core/commit/7a8020159979ecd) Updated redundant unit test
 * [ace08918de5b710](https://github.com/serenity-bdd/serenity-core/commit/ace08918de5b710) Added support for steps and page objects in nested classes.
Step libraries or page objects declared as nested classes, and which have a default constructor, also need the enclosing class to have a default constructor as well.
 * [1a4e720f73002f8](https://github.com/serenity-bdd/serenity-core/commit/1a4e720f73002f8) Updated changelog title
 * [d654b7d7be8af63](https://github.com/serenity-bdd/serenity-core/commit/d654b7d7be8af63) Fixed the title in the changelog
 * [2eeb1f18d001113](https://github.com/serenity-bdd/serenity-core/commit/2eeb1f18d001113) Updated changelog for version 1.7.0-rc.1
## v1.7.0-rc.1
### No issue
 * [1168366de1a43bd](https://github.com/serenity-bdd/serenity-core/commit/1168366de1a43bd) Reporting improvement: don't display the scenario outline if empty
 * [8d3c04066801841](https://github.com/serenity-bdd/serenity-core/commit/8d3c04066801841) Upgraded to Selenium 3.7.0
 * [487a8764926f60e](https://github.com/serenity-bdd/serenity-core/commit/487a8764926f60e) Updated the event bus logic to cater for the Cucumber 2.x lifecycle
 * [593ad9d4eb95e2e](https://github.com/serenity-bdd/serenity-core/commit/593ad9d4eb95e2e) Added a property to clear cookies and/or clean session with Safari (see https://github.com/serenity-bdd/serenity-cucumber/issues/90)

The `safari.use.clean.session` property can now be used to instruct the SafariDriver to delete all existing session data when starting a new session, by setting this value to "true". This includes browser history, cache, cookies, HTML5 local storage, and HTML5 databases. Not that since Safari uses a single profile for the current user, enabling this capability will permanently erase any existing session data.
 * [dbe048362240de9](https://github.com/serenity-bdd/serenity-core/commit/dbe048362240de9) Updated the changelog format
 * [ec39bacf66ef97e](https://github.com/serenity-bdd/serenity-core/commit/ec39bacf66ef97e) Updated the changelog
 * [39c9885e133b0a8](https://github.com/serenity-bdd/serenity-core/commit/39c9885e133b0a8) chore: Added an experimental CHANGELOG generator
## v1.6.9
### No issue
 * [d9cf8570550ccae](https://github.com/serenity-bdd/serenity-core/commit/d9cf8570550ccae) Fixed minor reporting glitch
## v1.6.8
### No issue
 * [49e2888a6f38278](https://github.com/serenity-bdd/serenity-core/commit/49e2888a6f38278) Better rendering of non-ASCII chars in REST tests
 * [31a226b10fcb59e](https://github.com/serenity-bdd/serenity-core/commit/31a226b10fcb59e) Switching back to Selenium 3.5.3 because of a bug in Selenium 3.6.0 in PhantomJS support

(see https://github.com/SeleniumHQ/selenium/issues/4781)
 * [b7f0798c96e8ca9](https://github.com/serenity-bdd/serenity-core/commit/b7f0798c96e8ca9) Updated to Selenium 3.6.0 and Cucumber 2.1.0
 * [f594ba1732f9247](https://github.com/serenity-bdd/serenity-core/commit/f594ba1732f9247) Updated to Selenium 3.6.0 and Cucumber 2.1.0
## v1.6.7
### [#981](https://github.com/serenity-bdd/serenity-core/issues/981) Capitalisation in test case title in Reports
 * [ba400cb16701147](https://github.com/serenity-bdd/serenity-core/commit/ba400cb16701147) Fixed #981
### [#984](https://github.com/serenity-bdd/serenity-core/issues/984) EmptyStackException is thrown after sending Response using SerenityRest.rest() in BeforeClass step
 * [3744419d7c1cb45](https://github.com/serenity-bdd/serenity-core/commit/3744419d7c1cb45) Attempted patch for #984
 * [05ee3d700106ced](https://github.com/serenity-bdd/serenity-core/commit/05ee3d700106ced) Possible fix for #984 - allow REST calls outside of the Serenity step lifecycle
### [#985](https://github.com/serenity-bdd/serenity-core/issues/985) Caused by: groovy.lang.MissingMethodException: No signature of method: sun.nio.f s.WindowsPath.exists() is applicable for argument types: () values: []
 * [d3859a9b401b4d5](https://github.com/serenity-bdd/serenity-core/commit/d3859a9b401b4d5) Fix for #985
### [#990](https://github.com/serenity-bdd/serenity-core/issues/990) Valuable information in Exception messages lost due to html formatting
 * [3f77a847b7cb24f](https://github.com/serenity-bdd/serenity-core/commit/3f77a847b7cb24f) Fixed #990
### [#991](https://github.com/serenity-bdd/serenity-core/issues/991) Char sequence should not be converted to HTML special char in GET query string in report
 * [3da32e7fd2a295c](https://github.com/serenity-bdd/serenity-core/commit/3da32e7fd2a295c) Fixed #991
### No issue
 * [7b3fcb4f4e1fce8](https://github.com/serenity-bdd/serenity-core/commit/7b3fcb4f4e1fce8) Refactored the AjaxElementLocator for better readability
 * [00785dcc852872d](https://github.com/serenity-bdd/serenity-core/commit/00785dcc852872d) Minor refactoring.
 * [c129fa0a7092999](https://github.com/serenity-bdd/serenity-core/commit/c129fa0a7092999) Fixed an error when trailing spaces in a numerial property value (e.g. serenity.brower.width) could cause the browser instantiation to fail.
 * [6f815a2153217e6](https://github.com/serenity-bdd/serenity-core/commit/6f815a2153217e6) Improved support for Saucelabs Appium integration
 * [88a9dd08132aa3b](https://github.com/serenity-bdd/serenity-core/commit/88a9dd08132aa3b) fix for https://github.com/serenity-bdd/serenity-core/issues/970
 * [022283cbbe37d58](https://github.com/serenity-bdd/serenity-core/commit/022283cbbe37d58) Fixed a broken unit test
 * [658d34e28622f40](https://github.com/serenity-bdd/serenity-core/commit/658d34e28622f40) Added the serenity.browser.resizing system property - set to false if you don't want Serenity to resize the browser for you
 * [8756802ca1cae7b](https://github.com/serenity-bdd/serenity-core/commit/8756802ca1cae7b) serenity+appium integration iOS platform first test case
## v1.6.7-rc.1
### No issue
 * [668c20a8aa8d8ea](https://github.com/serenity-bdd/serenity-core/commit/668c20a8aa8d8ea) Bug fix: dates where not being correctly written to the CSV reports
 * [ba200f7048c0a75](https://github.com/serenity-bdd/serenity-core/commit/ba200f7048c0a75) Updated to appium 5.0.4
 * [6eec9f0a02bff95](https://github.com/serenity-bdd/serenity-core/commit/6eec9f0a02bff95) Added better formatting for the tag lists on the report home page.
## v1.6.6-rc.2
### No issue
 * [b3c6066069f4226](https://github.com/serenity-bdd/serenity-core/commit/b3c6066069f4226) Fixed an issue where poorly formatted story files resulted in requirements with blank names
 * [eed7e6cd595eaa7](https://github.com/serenity-bdd/serenity-core/commit/eed7e6cd595eaa7) Fixed an issue where poorly formatted story files resulted in requirements with blank names
 * [57e0c568c4cfdac](https://github.com/serenity-bdd/serenity-core/commit/57e0c568c4cfdac) Fixed a bug that could break the reports if there were too many nested directories and story files with identical names
## v1.6.6-rc.1
### [#971](https://github.com/serenity-bdd/serenity-core/issues/971) Reports not getting created
 * [588b30d794219a9](https://github.com/serenity-bdd/serenity-core/commit/588b30d794219a9) Fixed #971 and #972
### [#972](https://github.com/serenity-bdd/serenity-core/issues/972) serenity report : stackoverflow error
 * [588b30d794219a9](https://github.com/serenity-bdd/serenity-core/commit/588b30d794219a9) Fixed #971 and #972
### No issue
 * [ce25598439f8c37](https://github.com/serenity-bdd/serenity-core/commit/ce25598439f8c37) Moving gradle plugin tests to integration and smoke tests
 * [6670583b36cd200](https://github.com/serenity-bdd/serenity-core/commit/6670583b36cd200) Trying to fix a groovy compatibility issue on the build server (still)
 * [96e6fba3b579d7e](https://github.com/serenity-bdd/serenity-core/commit/96e6fba3b579d7e) Trying to fix a groovy compatibility issue on the build server (still)
 * [ebb1947651720fc](https://github.com/serenity-bdd/serenity-core/commit/ebb1947651720fc) Trying to fix a groovy compatibility issue on the build server (still)
 * [ddb73b7e9743ffb](https://github.com/serenity-bdd/serenity-core/commit/ddb73b7e9743ffb) Trying to fix a groovy compatibility issue on the build server (still)
 * [0ad946fdd33a8f8](https://github.com/serenity-bdd/serenity-core/commit/0ad946fdd33a8f8) Trying to fix a groovy compatibility issue on the build server
 * [e16aebfe86751c6](https://github.com/serenity-bdd/serenity-core/commit/e16aebfe86751c6) Fixed an error in the requirements breadcrumbs
 * [3d1220c97d50b90](https://github.com/serenity-bdd/serenity-core/commit/3d1220c97d50b90) Attempting to fix a gradle build issue
 * [36c617886cc48ce](https://github.com/serenity-bdd/serenity-core/commit/36c617886cc48ce) Fixed an error in the requirements breadcrumbs
 * [8900284f3391bd8](https://github.com/serenity-bdd/serenity-core/commit/8900284f3391bd8) Trying to find the right match of Groovy/Gradle libraries for all envs
 * [29c46e307d367f6](https://github.com/serenity-bdd/serenity-core/commit/29c46e307d367f6) Added a default toString to the ScenarioActor class to print the name of the actor (fixed a unit test)
 * [471ab926ed8dac3](https://github.com/serenity-bdd/serenity-core/commit/471ab926ed8dac3) Added a default toString to the ScenarioActor class to print the name of the actor
## v1.6.5
### No issue
 * [5e1a88a781c3baf](https://github.com/serenity-bdd/serenity-core/commit/5e1a88a781c3baf) Trouble-shooting failing tests on Jenkins (v4)
 * [5208e973a7fc2f8](https://github.com/serenity-bdd/serenity-core/commit/5208e973a7fc2f8) Trouble-shooting failing tests on Jenkins (v3)
 * [8b64515efdb4c0a](https://github.com/serenity-bdd/serenity-core/commit/8b64515efdb4c0a) Trouble-shooting failing tests on Jenkins
 * [bc2adb29986e28f](https://github.com/serenity-bdd/serenity-core/commit/bc2adb29986e28f) Trouble-shooting failing tests on Jenkins
 * [62477f5180fa783](https://github.com/serenity-bdd/serenity-core/commit/62477f5180fa783) Trouble-shooting failing tests on Jenkins
 * [b6388b508bdd10c](https://github.com/serenity-bdd/serenity-core/commit/b6388b508bdd10c) Avoid returning nulls in WebDriver calls after a test has failed
 * [7431935ca7a144a](https://github.com/serenity-bdd/serenity-core/commit/7431935ca7a144a) Added the ScenarioActor class, a convenience class to make it easier to use Scenario step libraries as personas in tests.
 * [7ae3a232bc3882b](https://github.com/serenity-bdd/serenity-core/commit/7ae3a232bc3882b) A unit test to clarify the heuristics used when detecting XPath and CSS selectors.
 * [2615d7be6be036c](https://github.com/serenity-bdd/serenity-core/commit/2615d7be6be036c) Fixed a potential bug where instrumented step methods return a null value incorrectly after a previous passing step.
## v1.6.4-rc.2
### No issue
 * [f105f9cb718e497](https://github.com/serenity-bdd/serenity-core/commit/f105f9cb718e497) cucumber 2.0 integration - remove not used classes
 * [ddd957a71448d5e](https://github.com/serenity-bdd/serenity-core/commit/ddd957a71448d5e) cucumber2
 * [1901cdb893def46](https://github.com/serenity-bdd/serenity-core/commit/1901cdb893def46) cucumber2
 * [ed05fd4ccf7fe74](https://github.com/serenity-bdd/serenity-core/commit/ed05fd4ccf7fe74) cucumber2
 * [42eccd6e4b8e8cf](https://github.com/serenity-bdd/serenity-core/commit/42eccd6e4b8e8cf) cucumber 2.0 integration
 * [463b359a39a1318](https://github.com/serenity-bdd/serenity-core/commit/463b359a39a1318) cucumber2
## v1.6.4-rc.1
### [#955](https://github.com/serenity-bdd/serenity-core/issues/955) Getting an error in AfterStories when we call a method from Serenity Step files in Step file
 * [2966d13afe7cf9e](https://github.com/serenity-bdd/serenity-core/commit/2966d13afe7cf9e) Better support for out-of-lifecycle calls to Serenity methods. Possible fix for #955.
### No issue
 * [ddd4c40fc227911](https://github.com/serenity-bdd/serenity-core/commit/ddd4c40fc227911) Run Chrome tests on Linux in headless mode
 * [e05fd1802c248fe](https://github.com/serenity-bdd/serenity-core/commit/e05fd1802c248fe) Fixed link format in readme.
 * [928bba7ba909fd5](https://github.com/serenity-bdd/serenity-core/commit/928bba7ba909fd5) Added a section on raising issues in the readme file
 * [fa6509a9ba5db1e](https://github.com/serenity-bdd/serenity-core/commit/fa6509a9ba5db1e) General refactoring and removing a redundant test.
 * [0f8fe3db8a739df](https://github.com/serenity-bdd/serenity-core/commit/0f8fe3db8a739df) Fixed a broken test
 * [118c7eb70b8974e](https://github.com/serenity-bdd/serenity-core/commit/118c7eb70b8974e) Fixed a broken test
 * [b8ba4b2edda80e3](https://github.com/serenity-bdd/serenity-core/commit/b8ba4b2edda80e3) Fixed a broken test
 * [25d000fe6183961](https://github.com/serenity-bdd/serenity-core/commit/25d000fe6183961) Refactored the FileToUpload logic
 * [ef4f0221a93a2db](https://github.com/serenity-bdd/serenity-core/commit/ef4f0221a93a2db) Upload files feature a bit more robust
 * [ff0244fba6d0aed](https://github.com/serenity-bdd/serenity-core/commit/ff0244fba6d0aed) Improved logging to avoid unnecessary log messages during tests
 * [2eb06efab84440d](https://github.com/serenity-bdd/serenity-core/commit/2eb06efab84440d) Going back to Selenium 3.5.3 as version 3.6.0 seems buggy.
 * [f36b4cea0c7092f](https://github.com/serenity-bdd/serenity-core/commit/f36b4cea0c7092f) Going back to Selenium 3.5.3 as version 3.6.0 seems buggy
 * [347cb00706c70ef](https://github.com/serenity-bdd/serenity-core/commit/347cb00706c70ef) Removed some use of Guava Lists and minor tidying up
 * [d6e8a1562ffa950](https://github.com/serenity-bdd/serenity-core/commit/d6e8a1562ffa950) Fixed a dependency conflict with selenium 3.6.0
 * [ab2cbd1412d08a6](https://github.com/serenity-bdd/serenity-core/commit/ab2cbd1412d08a6) Updated selenium to 3.6.0
 * [6035944faf66467](https://github.com/serenity-bdd/serenity-core/commit/6035944faf66467) Added integration tests for CircleCI
 * [ea327a8214ab996](https://github.com/serenity-bdd/serenity-core/commit/ea327a8214ab996) feat: Expand references to environment variables and system properties in serenity.conf & serenity.properties.
Environment variables can be references using ${AN_ENVIRONMENT_VARIABLE} and System Properties using ${system.property.example}.
## v1.6.3
### No issue
 * [4e3d4a026b3539d](https://github.com/serenity-bdd/serenity-core/commit/4e3d4a026b3539d) Use the actor field instead of the name field for more consistency with Serenity/JS
## v1.6.2
### No issue
 * [4b9c0a92318e73e](https://github.com/serenity-bdd/serenity-core/commit/4b9c0a92318e73e) Better support for using @Step libraries as personas.
If you add a String field called 'name' to a step library, it will be assigned the name of the step library variable, or the 'name' attribute from the @Steps annotation.
## v1.6.1
### [#945](https://github.com/serenity-bdd/serenity-core/issues/945) Appium Behaviour Different from WebDriver SendKeys
 * [812d3dc408ff706](https://github.com/serenity-bdd/serenity-core/commit/812d3dc408ff706) Fixed #945
### [#947](https://github.com/serenity-bdd/serenity-core/pull/947) Feature/expand property variable references
 * [2be6ef25f841486](https://github.com/serenity-bdd/serenity-core/commit/2be6ef25f841486) Revert "Merge pull request #947 from bjss/feature/expand_property_variable_references"

This reverts commit 8eafb9a34d6110787bb244bde087cd099892f877, reversing
changes made to a6b917c51be0ee8815bdd8eb28043a47b323a214.
### No issue
 * [1903adc4393d2a8](https://github.com/serenity-bdd/serenity-core/commit/1903adc4393d2a8) feat: Expand references to environment variables and system properties in serenity.conf & serenity.properties.
Environment variables can be references using ${AN_ENVIRONMENT_VARIABLE} and System Properties using ${system.property.example}.
 * [a6b917c51be0ee8](https://github.com/serenity-bdd/serenity-core/commit/a6b917c51be0ee8) Add a link to the Serenity BDD site to the console banner
 * [6cd22e66749fc47](https://github.com/serenity-bdd/serenity-core/commit/6cd22e66749fc47) Minor reporting improvements.
 * [7207c9be2c36eae](https://github.com/serenity-bdd/serenity-core/commit/7207c9be2c36eae) Refactoring.
 * [adf9745b0f5f272](https://github.com/serenity-bdd/serenity-core/commit/adf9745b0f5f272) Deleted Travis config file.
 * [25089875031b168](https://github.com/serenity-bdd/serenity-core/commit/25089875031b168) Trying TravisCI
 * [f3d6f0714b57225](https://github.com/serenity-bdd/serenity-core/commit/f3d6f0714b57225) Deactivating the Gradle daemon for CircleCI
 * [b646eda79d673a0](https://github.com/serenity-bdd/serenity-core/commit/b646eda79d673a0) Disable gradle daemon for circleci
 * [8518d41ad257230](https://github.com/serenity-bdd/serenity-core/commit/8518d41ad257230) Trying out CircleCI 2
 * [797ac01c8af4af8](https://github.com/serenity-bdd/serenity-core/commit/797ac01c8af4af8) Added an experimental CircleCI config file
 * [139c93e182ced99](https://github.com/serenity-bdd/serenity-core/commit/139c93e182ced99) fix: remove extra point in ChromePreferences getPreparedPropertyKey
## v1.6.0
### [#933](https://github.com/serenity-bdd/serenity-core/issues/933) Update from Serenity 1.5.3 to &gt; 1.5.5 broke - webdriver.provided.type
 * [f35a0fcfa16da81](https://github.com/serenity-bdd/serenity-core/commit/f35a0fcfa16da81) Possible fix for #933
### No issue
 * [b40cf1bb8bb4274](https://github.com/serenity-bdd/serenity-core/commit/b40cf1bb8bb4274) Removed unnecessary code.
 * [bb744b5b503a40e](https://github.com/serenity-bdd/serenity-core/commit/bb744b5b503a40e) Refactored the way step libraries are instantiated.
 * [a41a75fb272321e](https://github.com/serenity-bdd/serenity-core/commit/a41a75fb272321e) Update to Guava 23.0 to match the Selenium dependency.
 * [9e5ee8231b06260](https://github.com/serenity-bdd/serenity-core/commit/9e5ee8231b06260) Removed deprecated references to jcommander
## v1.5.12
### No issue
 * [ab4886322f632b1](https://github.com/serenity-bdd/serenity-core/commit/ab4886322f632b1) updated versions of selenium & appium
## v1.5.11
### [#928](https://github.com/serenity-bdd/serenity-core/issues/928) Wait is not working properly whwn using `withTimeoutOf`
 * [5d419c8ed01e8da](https://github.com/serenity-bdd/serenity-core/commit/5d419c8ed01e8da) Fixed #928
## v1.5.10
### No issue
 * [2ae6b9cb0c5c550](https://github.com/serenity-bdd/serenity-core/commit/2ae6b9cb0c5c550) Update to Appium 5.0.2
 * [48b87a520f406e0](https://github.com/serenity-bdd/serenity-core/commit/48b87a520f406e0) Fix hostIsAvailableAt if behind proxy
 * [48b9d98a17edfeb](https://github.com/serenity-bdd/serenity-core/commit/48b9d98a17edfeb) refactor: replace lambdaj with plain Java 8
## v1.5.9
### [#911](https://github.com/serenity-bdd/serenity-core/issues/911) How to set useragent for Firefox on serenity.properties file?
 * [ddf7e2378b1a508](https://github.com/serenity-bdd/serenity-core/commit/ddf7e2378b1a508) Fixed #911, and some minor refactoring
### No issue
 * [353f8f95b50b5e9](https://github.com/serenity-bdd/serenity-core/commit/353f8f95b50b5e9) Fixed templating issue in the reports
 * [196583bdffa680f](https://github.com/serenity-bdd/serenity-core/commit/196583bdffa680f) updated appium java client version

updated commonsCodecVersion version to make it compatible with latest appium java client
 * [481675803b31d5a](https://github.com/serenity-bdd/serenity-core/commit/481675803b31d5a) Removed the CircleCI badge as the build is too big
 * [69c6f571c390cbf](https://github.com/serenity-bdd/serenity-core/commit/69c6f571c390cbf) Added CircleCI badge
 * [add3cb16332f847](https://github.com/serenity-bdd/serenity-core/commit/add3cb16332f847) Removed test reporting from the Circle CI build because it can't handle the number of tests
 * [74060d0982aa120](https://github.com/serenity-bdd/serenity-core/commit/74060d0982aa120) additional test for https://github.com/serenity-bdd/serenity-core/issues/894
 * [e2612022a6635b2](https://github.com/serenity-bdd/serenity-core/commit/e2612022a6635b2) additional test for https://github.com/serenity-bdd/serenity-core/issues/894
 * [16c9f9c8a304648](https://github.com/serenity-bdd/serenity-core/commit/16c9f9c8a304648) Removed the redundant 'list' markdown configuration
 * [bef307ad0c61cab](https://github.com/serenity-bdd/serenity-core/commit/bef307ad0c61cab) Updated dependencies
## v1.5.8
### [#908](https://github.com/serenity-bdd/serenity-core/issues/908) java.util.concurrent.ExecutionException: java.lang.RuntimeException: Failed to merge template: Parsing error in template net.thucydides.core.reports.html.Merger$MergeBuilder.to(Merger.java:39) java.util.concurrent.ExecutionException: java.lang.RuntimeException: Failed to merge template: Parsing error in template
 * [84ab5eae4422908](https://github.com/serenity-bdd/serenity-core/commit/84ab5eae4422908) Fixed #908
## v1.5.7
### [#894](https://github.com/serenity-bdd/serenity-core/issues/894) Rendering Hungarian characters in HTML report
 * [e5f7d9924e27703](https://github.com/serenity-bdd/serenity-core/commit/e5f7d9924e27703) Fixed #894
### No issue
 * [16ddb8feddd296a](https://github.com/serenity-bdd/serenity-core/commit/16ddb8feddd296a) Updated to Selenium 3.5.2
## v1.5.6
### [#898](https://github.com/serenity-bdd/serenity-core/issues/898) Error with serenity.browser.maximized property
 * [66467a15249bf95](https://github.com/serenity-bdd/serenity-core/commit/66467a15249bf95) Update RedimensionStrategy.java

Fixed #898
### [#904](https://github.com/serenity-bdd/serenity-core/issues/904) serenity 1.5.5 reporting bug
 * [72c3bff61acb839](https://github.com/serenity-bdd/serenity-core/commit/72c3bff61acb839) Fixed #904
### No issue
 * [d8177b1f3eb51cc](https://github.com/serenity-bdd/serenity-core/commit/d8177b1f3eb51cc) Fixed a bug where expected exceptions where not correctly handled for AssertJ
 * [e36dadeebc30692](https://github.com/serenity-bdd/serenity-core/commit/e36dadeebc30692) Include a message to document expected exceptions for JUnit tests
## v1.5.5
### [#895](https://github.com/serenity-bdd/serenity-core/issues/895) &#39;Cannot cast object&#39; error occurs for clean and aggregate gradle tasks
 * [f387a6ec7a599ca](https://github.com/serenity-bdd/serenity-core/commit/f387a6ec7a599ca) Fixed #895
## v1.5.4
### No issue
 * [e3d43bf10b48347](https://github.com/serenity-bdd/serenity-core/commit/e3d43bf10b48347) Updated some tests
 * [fade3a61f20acd9](https://github.com/serenity-bdd/serenity-core/commit/fade3a61f20acd9) Updated some tests
 * [eba65c8a0877f36](https://github.com/serenity-bdd/serenity-core/commit/eba65c8a0877f36) Updated some tests
 * [30e69907f3b5f6e](https://github.com/serenity-bdd/serenity-core/commit/30e69907f3b5f6e) Ensure that @Steps-annotated fields of the same type, declared in different classes, refer to the same instance.
 * [75aee889187046a](https://github.com/serenity-bdd/serenity-core/commit/75aee889187046a) Updated to Selenium 3.5.1
 * [34c117cc5890dad](https://github.com/serenity-bdd/serenity-core/commit/34c117cc5890dad) Updated the favicon.
 * [79712549bf96c4f](https://github.com/serenity-bdd/serenity-core/commit/79712549bf96c4f) Updated core to 3.5.1
## v1.5.4-rc.2
### [#872](https://github.com/serenity-bdd/serenity-core/issues/872) Test Name is not sent to Browserstack
 * [4e4cd0afcdc88db](https://github.com/serenity-bdd/serenity-core/commit/4e4cd0afcdc88db) Possible fix for #872
### No issue
 * [96ba9a31a1d8503](https://github.com/serenity-bdd/serenity-core/commit/96ba9a31a1d8503) Fixed an issue with the web element proxy support for Appium.
 * [d6dd08f7c95a9cd](https://github.com/serenity-bdd/serenity-core/commit/d6dd08f7c95a9cd) Updated unit tests
 * [6816e0dbba2873b](https://github.com/serenity-bdd/serenity-core/commit/6816e0dbba2873b) Updated unit tests
 * [4e87d3bee882e4d](https://github.com/serenity-bdd/serenity-core/commit/4e87d3bee882e4d) Updated unit tests
 * [0d7183ceedbd1d2](https://github.com/serenity-bdd/serenity-core/commit/0d7183ceedbd1d2) https://github.com/serenity-bdd/serenity-core/issues/856 -avoid NPE
## v1.5.4-rc.1
### [#846](https://github.com/serenity-bdd/serenity-core/issues/846) Test Name is not sent to Browserstack
 * [b744215286049da](https://github.com/serenity-bdd/serenity-core/commit/b744215286049da) Fix #846 Test Name is not sent to Browserstack
### [#848](https://github.com/serenity-bdd/serenity-core/issues/848) gradle option --quiet not respected
 * [719475bd0fda7d5](https://github.com/serenity-bdd/serenity-core/commit/719475bd0fda7d5) Fixed #848 and refactored the Gradle plugin
### No issue
 * [57f952b92412915](https://github.com/serenity-bdd/serenity-core/commit/57f952b92412915) Test refactoring
 * [c0ef418adf58f64](https://github.com/serenity-bdd/serenity-core/commit/c0ef418adf58f64) Minor refactoring to allow better support for error handling
 * [a45d498c50581f1](https://github.com/serenity-bdd/serenity-core/commit/a45d498c50581f1) AppiumWebDriver:use AppiumFieldDecorator
 * [90216f2986151c9](https://github.com/serenity-bdd/serenity-core/commit/90216f2986151c9) Improved error reporting for captured exceptions.
 * [a3ad029de327c43](https://github.com/serenity-bdd/serenity-core/commit/a3ad029de327c43) A little issue on select in rport (only write "click on"
 * [6b8cdabb9f46e78](https://github.com/serenity-bdd/serenity-core/commit/6b8cdabb9f46e78) Delete overview.adoc
 * [5f08dfb56496fa4](https://github.com/serenity-bdd/serenity-core/commit/5f08dfb56496fa4) Create README.adoc
 * [37301b1b768652c](https://github.com/serenity-bdd/serenity-core/commit/37301b1b768652c) Update overview.adoc
 * [9759a6ae16b7f80](https://github.com/serenity-bdd/serenity-core/commit/9759a6ae16b7f80) Create CODE_OF_CONDUCT.md
## v1.5.3
### No issue
 * [6ec77175f8efe68](https://github.com/serenity-bdd/serenity-core/commit/6ec77175f8efe68) XML reports are no longer generated. JSON reports are always generated. HTML reports can be disabled.
 * [44a9eca240cf12e](https://github.com/serenity-bdd/serenity-core/commit/44a9eca240cf12e) If a tag provider produces an error, it will no longer stop the report from being generated.
## v1.5.2-rc.1
### [#482](https://github.com/serenity-bdd/serenity-core/issues/482) Reporting: exception with message has missed root cause
 * [38af47ee3bc966a](https://github.com/serenity-bdd/serenity-core/commit/38af47ee3bc966a) Fixed #482
## v1.5.1-rc.7
### No issue
 * [348ca83b7ea5c68](https://github.com/serenity-bdd/serenity-core/commit/348ca83b7ea5c68) Fixed a concurrency issue in the report generation
## v1.5.1-rc.6
### [#842](https://github.com/serenity-bdd/serenity-core/issues/842) Dserenity.driver.capabilities does not work for saucelabs devices
 * [de325faa187b89d](https://github.com/serenity-bdd/serenity-core/commit/de325faa187b89d) Fixed #842
## v1.5.1-rc.5
### [#841](https://github.com/serenity-bdd/serenity-core/issues/841) Wrong check on remote host is available
 * [b70fb6ef8b0ab41](https://github.com/serenity-bdd/serenity-core/commit/b70fb6ef8b0ab41) Fixed #841 - potentially unreliable method used to check for the presence of a remote selenium server.
### No issue
 * [cb8e8cccb3ad904](https://github.com/serenity-bdd/serenity-core/commit/cb8e8cccb3ad904) Minor refactoring
 * [64deebc8158d255](https://github.com/serenity-bdd/serenity-core/commit/64deebc8158d255) If an exception class is not known, use heuristics to determine whether it is an error or a failure
## v1.5.1-rc.4
### No issue
 * [c65af60b1876a97](https://github.com/serenity-bdd/serenity-core/commit/c65af60b1876a97) Minor refactoring
## v1.5.0-rc.4
### No issue
 * [0cc54441f6efb29](https://github.com/serenity-bdd/serenity-core/commit/0cc54441f6efb29) Added an extra method for the maven plugin
## v1.5.0-rc.3
### No issue
 * [e0d5624014fe116](https://github.com/serenity-bdd/serenity-core/commit/e0d5624014fe116) Removed dependency conflicts
## v1.5.0-rc.2
### [#672](https://github.com/serenity-bdd/serenity-core/issues/672) Please update io.appium:java-client
 * [93a719f8764f309](https://github.com/serenity-bdd/serenity-core/commit/93a719f8764f309) Fixed #672 - Updated appium client to 5.0.0-BETA9
### [#678](https://github.com/serenity-bdd/serenity-core/issues/678) Report scenario order = scenario execution
 * [70b1d915ddaecb9](https://github.com/serenity-bdd/serenity-core/commit/70b1d915ddaecb9) Fixed #678 - there is now the option to sort test results by order of execution.
### [#832](https://github.com/serenity-bdd/serenity-core/issues/832) &#39;net.thucydides.core.util.PropertiesFileLocalPreferences&#39; are displaying multiple time while building through maven
 * [1522b0bcb401aa3](https://github.com/serenity-bdd/serenity-core/commit/1522b0bcb401aa3) Fixed #832 - properties are now logged to the console only as DEBUG messages, not as INFO messages.
### No issue
 * [04acfa6a835c3f8](https://github.com/serenity-bdd/serenity-core/commit/04acfa6a835c3f8) Refactoring
 * [68a917b5fe85eee](https://github.com/serenity-bdd/serenity-core/commit/68a917b5fe85eee) Refactored the remote driver code
 * [734ef99dc38f169](https://github.com/serenity-bdd/serenity-core/commit/734ef99dc38f169) Refactored the remote driver code
 * [158fb16c9029d2b](https://github.com/serenity-bdd/serenity-core/commit/158fb16c9029d2b) Added the ability to specify Chrome options in the @Managed annotation

e.g.
```@Managed(driver = "chrome", options = "--headless")```
 * [df3c2b05b020f1b](https://github.com/serenity-bdd/serenity-core/commit/df3c2b05b020f1b) test refactoring
 * [60fa541c994efb2](https://github.com/serenity-bdd/serenity-core/commit/60fa541c994efb2) Reverted to using lambdaj in the experimental SpecFlow adaptor for now
 * [21d44fa9cef559c](https://github.com/serenity-bdd/serenity-core/commit/21d44fa9cef559c) Removed core dependency on lambdaj
 * [e655282e446bdb3](https://github.com/serenity-bdd/serenity-core/commit/e655282e446bdb3) Updated to Java 8 and replaced most references to LambdaJ with Java 8 streams.
 * [3a647ecf58ad9a2](https://github.com/serenity-bdd/serenity-core/commit/3a647ecf58ad9a2) Minor updates
## v1.5.0-rc.1
### [#818](https://github.com/serenity-bdd/serenity-core/issues/818) NullPointer when generating report
 * [e446e6d7f54d40c](https://github.com/serenity-bdd/serenity-core/commit/e446e6d7f54d40c) Fixed #818
### No issue
 * [4c8f9e9a25273c5](https://github.com/serenity-bdd/serenity-core/commit/4c8f9e9a25273c5) Report service refactoring
 * [b9ac1d1573128ed](https://github.com/serenity-bdd/serenity-core/commit/b9ac1d1573128ed) Updated the wrapper to Gradle 4.0
 * [199a8ae26c614ca](https://github.com/serenity-bdd/serenity-core/commit/199a8ae26c614ca) Fixed an issue with thread executors that caused jobs to hang under certain circumstances.
 * [c5d45927da2d691](https://github.com/serenity-bdd/serenity-core/commit/c5d45927da2d691) Error tags are now of type "error"
## v1.4.1-rc.6
### No issue
 * [ad8ef700a152ce3](https://github.com/serenity-bdd/serenity-core/commit/ad8ef700a152ce3) Simplified and improved performance for the way page object fields are displayed in error messages.
 * [0a63951b0e3c688](https://github.com/serenity-bdd/serenity-core/commit/0a63951b0e3c688) Minor refactoring
 * [08bcff767169b04](https://github.com/serenity-bdd/serenity-core/commit/08bcff767169b04) Reorganising the Screenplay web tests as integration tests.
## v1.4.1-rc.5
### [#806](https://github.com/serenity-bdd/serenity-core/issues/806)  Could not instantiate new WebDriver instance when sending the browserstack.build as a Integer (en.bs_caps.build_not_string)
 * [534092756c22127](https://github.com/serenity-bdd/serenity-core/commit/534092756c22127) Fixed #806 - browser capability properties in quotes are now always passed as Strings, and not converted to numbers, e.g.

    browserstack.build = "1"
## v1.4.1-rc.4
### [#799](https://github.com/serenity-bdd/serenity-core/issues/799) Using &quot;timeoutInSeconds&quot; option in @FindBy opens additional browser
 * [c604d4cca57e5e3](https://github.com/serenity-bdd/serenity-core/commit/c604d4cca57e5e3) Fixed #799
 * [dd96cf4f885f413](https://github.com/serenity-bdd/serenity-core/commit/dd96cf4f885f413) Fix for #799 - extra browser opening when the timeout attribute is used with the @FindBy annotation.
### [#804](https://github.com/serenity-bdd/serenity-core/issues/804) Gradle. StackTraceAnalyser - Couldn&#39;t find class during Stack analysis
 * [51c643197cb60a5](https://github.com/serenity-bdd/serenity-core/commit/51c643197cb60a5) Fix for #804
### [#806](https://github.com/serenity-bdd/serenity-core/issues/806)  Could not instantiate new WebDriver instance when sending the browserstack.build as a Integer (en.bs_caps.build_not_string)
 * [29a45261ec93cfa](https://github.com/serenity-bdd/serenity-core/commit/29a45261ec93cfa) Work-around for #806
### [#807](https://github.com/serenity-bdd/serenity-core/issues/807) Underscores in steps arguments are erased in the report
 * [e1e3b0a4fdd6cee](https://github.com/serenity-bdd/serenity-core/commit/e1e3b0a4fdd6cee) Fix for #807 - markdown rendering is now disabled for steps by default.
### [#814](https://github.com/serenity-bdd/serenity-core/issues/814) Serenity with Jbehave - report shows binary characters
 * [d572d4104ca191f](https://github.com/serenity-bdd/serenity-core/commit/d572d4104ca191f) Fix for #814 - better support for rendering unusual new line characters in HTML.
### No issue
 * [beb15c0e844a87a](https://github.com/serenity-bdd/serenity-core/commit/beb15c0e844a87a) Fixed a broken test
 * [00115f992da6b55](https://github.com/serenity-bdd/serenity-core/commit/00115f992da6b55) Fixed a broken test
 * [0bef88a013fe74f](https://github.com/serenity-bdd/serenity-core/commit/0bef88a013fe74f) Fixed a broken test
 * [a94d972c6677a78](https://github.com/serenity-bdd/serenity-core/commit/a94d972c6677a78) Refactoring to make the inflection logic more memory-efficient.
 * [791a313dd05a3ea](https://github.com/serenity-bdd/serenity-core/commit/791a313dd05a3ea) Added the io.blocking.coefficient property - an (advanced) way of fine-tuning the number of reporting threads used during report generation.
 * [31284c65950e751](https://github.com/serenity-bdd/serenity-core/commit/31284c65950e751) Fixed a bug in tag reporting.
 * [c1936c273691bda](https://github.com/serenity-bdd/serenity-core/commit/c1936c273691bda) Improved logging of loaded requirements tree.
 * [63a67d7f50902c6](https://github.com/serenity-bdd/serenity-core/commit/63a67d7f50902c6) Minor refactoring.
## v1.4.1-rc.3
### [#751](https://github.com/serenity-bdd/serenity-core/issues/751) webdriver.gecko.driver is ignored when instantiating new Firefox driver
 * [244f682fc24e804](https://github.com/serenity-bdd/serenity-core/commit/244f682fc24e804) fix: geckodriver is used from environment variable #751
### No issue
 * [7ed14296f4c1e50](https://github.com/serenity-bdd/serenity-core/commit/7ed14296f4c1e50) Only shutdown fixture services if closing the last tab in a browser
 * [077dbc9174f64c7](https://github.com/serenity-bdd/serenity-core/commit/077dbc9174f64c7) Reove extra'.' for capability getPreparedPropertyKey
 * [0300805df73d77a](https://github.com/serenity-bdd/serenity-core/commit/0300805df73d77a) Step multiple implementation annotation
 * [d0ec3c15e8c1aa9](https://github.com/serenity-bdd/serenity-core/commit/d0ec3c15e8c1aa9) fix for https://github.com/serenity-bdd/serenity-core/issues/747
 * [b4d1c63411ab505](https://github.com/serenity-bdd/serenity-core/commit/b4d1c63411ab505) upgrade to serenity-rest 2.9.0 - byte-buddy performance improvements
## v1.4.1-rc.2
### No issue
 * [d061c566f6174f7](https://github.com/serenity-bdd/serenity-core/commit/d061c566f6174f7) Removed the @Nullable annotation to reduce potential dependency conflicts
 * [952fba347330484](https://github.com/serenity-bdd/serenity-core/commit/952fba347330484) Better support for ChromeDriver options.
 * [9b8a1cff790aa58](https://github.com/serenity-bdd/serenity-core/commit/9b8a1cff790aa58) Use guava 20.0 to preserve Java 7 compatibility.
 * [4a9a30970e26060](https://github.com/serenity-bdd/serenity-core/commit/4a9a30970e26060) Fixed a bug where parameterized JUnit tests didn't honor tag filtering
## v1.4.1-rc.1
### [#780](https://github.com/serenity-bdd/serenity-core/issues/780) WebElementStateMatchers have grammatical problems in expectation failure messages
 * [df91eca9736ae26](https://github.com/serenity-bdd/serenity-core/commit/df91eca9736ae26) Fixed #780
### [#789](https://github.com/serenity-bdd/serenity-core/issues/789) Dry run doesn&#39;t render consequence steps
 * [a3e75736fe761e9](https://github.com/serenity-bdd/serenity-core/commit/a3e75736fe761e9) Fixed #789
### [#793](https://github.com/serenity-bdd/serenity-core/issues/793) Encoding problem in Scenario Outline
 * [78c2daa917ee3c3](https://github.com/serenity-bdd/serenity-core/commit/78c2daa917ee3c3) Possible fix for #793

Report encoding can be configured using the report.charset property. Defaults to UTF-8. May fixe issue #793.
### Jira
 * [78c2daa917ee3c3](https://github.com/serenity-bdd/serenity-core/commit/78c2daa917ee3c3) Possible fix for #793

Report encoding can be configured using the report.charset property. Defaults to UTF-8. May fixe issue #793.
### No issue
 * [af3ffb8926c83b3](https://github.com/serenity-bdd/serenity-core/commit/af3ffb8926c83b3) Refactoring
 * [50b5ec226904818](https://github.com/serenity-bdd/serenity-core/commit/50b5ec226904818) Created Serenity equivalents for all non Deprecated Thucydides methods
## v1.4.0
### No issue
 * [cbdb3de0a648eac](https://github.com/serenity-bdd/serenity-core/commit/cbdb3de0a648eac) API updates for more flexible data tables
## v1.3.1-rc.1
### No issue
 * [6e7e22dee562975](https://github.com/serenity-bdd/serenity-core/commit/6e7e22dee562975) Fixed a defect that caused reports to fail for some cucumber tables.
 * [ccb08d25e500050](https://github.com/serenity-bdd/serenity-core/commit/ccb08d25e500050) More robust error handling for driver failures.
 * [fc82ab23fde2584](https://github.com/serenity-bdd/serenity-core/commit/fc82ab23fde2584) Minor refactoring.
## v1.3.0-rc.2
### [#767](https://github.com/serenity-bdd/serenity-core/issues/767) WebDriverFacade fails to remove closed timeouts, thus introducing a memory leak
 * [c793bd23f9c362b](https://github.com/serenity-bdd/serenity-core/commit/c793bd23f9c362b) Fix for a memory leak (#767)

Contribution by Richard Luko
### [#768](https://github.com/serenity-bdd/serenity-core/pull/768) Disable marionette by default, 
 * [c58a3f01610a2df](https://github.com/serenity-bdd/serenity-core/commit/c58a3f01610a2df) Manual merge of PR #768

Set the “marionnette” capability to false by default in Firefox if Marionette is not being used.
### [#771](https://github.com/serenity-bdd/serenity-core/issues/771) Serenity is not able to generate reports
 * [7527749da2e7ba3](https://github.com/serenity-bdd/serenity-core/commit/7527749da2e7ba3) Fixed #771
### [#772](https://github.com/serenity-bdd/serenity-core/issues/772) BaseStepListener trying to get TestOutcome from latest absent TestOutcome
 * [1b39ac4f256d5c1](https://github.com/serenity-bdd/serenity-core/commit/1b39ac4f256d5c1) Fixed #772 - better integration with Strping test runners.
### No issue
 * [bf7a5126cf135f4](https://github.com/serenity-bdd/serenity-core/commit/bf7a5126cf135f4) Better reporting of nested tags.
 * [0c640613760ad6e](https://github.com/serenity-bdd/serenity-core/commit/0c640613760ad6e) Updated unit tests.
 * [0595f0e1149dffa](https://github.com/serenity-bdd/serenity-core/commit/0595f0e1149dffa) Better support for running tagged tests from different test frameworks in the same test run.
 * [a7f00da649e50f1](https://github.com/serenity-bdd/serenity-core/commit/a7f00da649e50f1) Pass capabilities to PhantomJS by setting capability properties with the "phantomjs." prefix.
 * [f7bf036b0ee3594](https://github.com/serenity-bdd/serenity-core/commit/f7bf036b0ee3594) You can override the BrowserStack test name by setting the browserstack.name system property.
 * [d7367f8f733ae6f](https://github.com/serenity-bdd/serenity-core/commit/d7367f8f733ae6f) Set and clear the context programmatically

You can now use Serenity.setContext() and Serenity.clearContext() to set the test context programmatically.
 * [547f2d702810ef5](https://github.com/serenity-bdd/serenity-core/commit/547f2d702810ef5) Added an icon for Phantomjs
 * [ba3a911f45a78fb](https://github.com/serenity-bdd/serenity-core/commit/ba3a911f45a78fb) Add the ‘serenity.add.context.tag’ property

This gives the option to display a tag for the current test context (default behaviour) or not (if you set this property to false).
 * [f31e44c5d16b91a](https://github.com/serenity-bdd/serenity-core/commit/f31e44c5d16b91a) Test refactoring
 * [051eb360a3ad24e](https://github.com/serenity-bdd/serenity-core/commit/051eb360a3ad24e) FEAT: Added the ability to report on multiple runs of the same test

You can now use the `context` system property to define a context for your tests. This can be combined with the `injected.tags` system property for more clarity. For example, the two following commands would run the same tests twice, with a different context each time:

    $ mvn verify -Dcontext=chrome -Dinjected.tags="browser:chrome"
    $ mvn verify -Dcontext=firefox -Dinjected.tags="browser: firefox"

Browsers (chrome, ie, firefox…) and OS (linux, windows, android, iphone) are indicated with an icon. All other contexts are indicated in text form.
 * [aff8ab1804a28d9](https://github.com/serenity-bdd/serenity-core/commit/aff8ab1804a28d9) Fixing another broken test
 * [e6e4e275d569a31](https://github.com/serenity-bdd/serenity-core/commit/e6e4e275d569a31) Fixed a bug in the tests
 * [7f5b1e040b7e266](https://github.com/serenity-bdd/serenity-core/commit/7f5b1e040b7e266) Fixed a bug in the tests
 * [7097ab4d7d2f589](https://github.com/serenity-bdd/serenity-core/commit/7097ab4d7d2f589) Upgrated gradle
## v1.2.5-rc.23
### [#767](https://github.com/serenity-bdd/serenity-core/issues/767) WebDriverFacade fails to remove closed timeouts, thus introducing a memory leak
 * [aae1a9d1152ea88](https://github.com/serenity-bdd/serenity-core/commit/aae1a9d1152ea88) Fixed a memory leak (#767)
### [#769](https://github.com/serenity-bdd/serenity-core/issues/769) Aggregate task runs with error
 * [0bcc00684369dfd](https://github.com/serenity-bdd/serenity-core/commit/0bcc00684369dfd) Attempted fix for #769
### No issue
 * [46892688ecdfbc8](https://github.com/serenity-bdd/serenity-core/commit/46892688ecdfbc8) Minor refactoring
## v1.2.5-rc.6
### No issue
 * [b4d40fd6f6ac368](https://github.com/serenity-bdd/serenity-core/commit/b4d40fd6f6ac368) Updated gradle plugin to handle test history reporting
 * [b5df67efbd79bab](https://github.com/serenity-bdd/serenity-core/commit/b5df67efbd79bab) Updated Gradle wrapper version
 * [42479f961bb5a64](https://github.com/serenity-bdd/serenity-core/commit/42479f961bb5a64) Bug fixes in the history reporting
 * [cbbe6c9d2494c0b](https://github.com/serenity-bdd/serenity-core/commit/cbbe6c9d2494c0b) rest-assured byte-buddy performance improvements
## v1.2.5-rc.5
### No issue
 * [b53adb3f578c743](https://github.com/serenity-bdd/serenity-core/commit/b53adb3f578c743) Fixed a performance issue with tag management
## v1.2.5-rc.4
### [#741](https://github.com/serenity-bdd/serenity-core/issues/741) ReportGenerationFailedError: Failed to generate configuration report
 * [326b694ae2124ab](https://github.com/serenity-bdd/serenity-core/commit/326b694ae2124ab) Fix for #741

Possible fix for concurrent access bug raised in #741
## v1.2.5-rc.3
### [#756](https://github.com/serenity-bdd/serenity-core/issues/756) Test result summary doesn&#39;t show ignored test
 * [9ac0863487576da](https://github.com/serenity-bdd/serenity-core/commit/9ac0863487576da) Fixed #756

Report both Ignored and Skipped tests correctly in Cucumber.
### No issue
 * [871ef17e39ef1b5](https://github.com/serenity-bdd/serenity-core/commit/871ef17e39ef1b5) com.jayway.restassured 2.9.0 > io.restassured 3.0.2
## v1.2.5-rc.2
### No issue
 * [3c1a4de75ff5cb9](https://github.com/serenity-bdd/serenity-core/commit/3c1a4de75ff5cb9) Test refactoring
 * [3f4e8a7a175cb0b](https://github.com/serenity-bdd/serenity-core/commit/3f4e8a7a175cb0b) Fixed some unit tests
 * [5bff74ac39585b2](https://github.com/serenity-bdd/serenity-core/commit/5bff74ac39585b2) Add a full test title to scenario links
 * [006e7bfccfc20ef](https://github.com/serenity-bdd/serenity-core/commit/006e7bfccfc20ef) Added support for flagging new tests

Set show.history.flag=true to allow new failures to be flagged in the reports.
 * [fbe656f4156cb03](https://github.com/serenity-bdd/serenity-core/commit/fbe656f4156cb03) Ensure that empty boolean properties correctly use default values.
 * [a7e7f5cdb154a6b](https://github.com/serenity-bdd/serenity-core/commit/a7e7f5cdb154a6b) Simple support for checking for new test failures (WIP)
 * [036a6dd573a5157](https://github.com/serenity-bdd/serenity-core/commit/036a6dd573a5157) Fixed a potential issue with Selenium 3.x
 * [729138fa94ea803](https://github.com/serenity-bdd/serenity-core/commit/729138fa94ea803) Fixed a reporting error in the percentage failure or error count.
 * [c12ff6800e6c918](https://github.com/serenity-bdd/serenity-core/commit/c12ff6800e6c918) Fixed a potential NullPointerException when using the uploader from Screenplay
 * [fe71517e9c08105](https://github.com/serenity-bdd/serenity-core/commit/fe71517e9c08105) Refactored a test
 * [c0dc02f53223594](https://github.com/serenity-bdd/serenity-core/commit/c0dc02f53223594) Update to Guava 21.0
 * [ead357aec9f0929](https://github.com/serenity-bdd/serenity-core/commit/ead357aec9f0929) upgrade to serenity-rest 2.9.0 - byte-buddy performance improvements - reuse constructors
 * [08c7a526b281a7c](https://github.com/serenity-bdd/serenity-core/commit/08c7a526b281a7c) Work in progress
 * [9512a485c7bcf0c](https://github.com/serenity-bdd/serenity-core/commit/9512a485c7bcf0c) WIP
## v1.2.5-rc.1
### No issue
 * [09ecbfd938f4aed](https://github.com/serenity-bdd/serenity-core/commit/09ecbfd938f4aed) Added the injected.tags property

Use the injected.tags property to inject build-wide tags that will be applied to every test execution.
 * [ee1ee2ecc4d3338](https://github.com/serenity-bdd/serenity-core/commit/ee1ee2ecc4d3338) Fixed a minor defect.
 * [fca949af66d9a92](https://github.com/serenity-bdd/serenity-core/commit/fca949af66d9a92) Better support for trailing spaces in the driver names.
## v1.2.4-rc.1
### No issue
 * [39102b60a3636d6](https://github.com/serenity-bdd/serenity-core/commit/39102b60a3636d6) Better support for markdown rendering in the step outlines
 * [778335812600c75](https://github.com/serenity-bdd/serenity-core/commit/778335812600c75) upgrade to serenity-rest 2.9.0
## v1.2.3-rc.10
### No issue
 * [316dab301cd2ee5](https://github.com/serenity-bdd/serenity-core/commit/316dab301cd2ee5) Restored some code that was finally needed.
 * [01db45aa8f550f2](https://github.com/serenity-bdd/serenity-core/commit/01db45aa8f550f2) Code simplification
 * [dd6a654cac9726b](https://github.com/serenity-bdd/serenity-core/commit/dd6a654cac9726b) Refactoring
 * [ace6d8da60c30a9](https://github.com/serenity-bdd/serenity-core/commit/ace6d8da60c30a9) Minor bug fix.
 * [40597b137752e36](https://github.com/serenity-bdd/serenity-core/commit/40597b137752e36) Improved support for data-driven tests.
 * [5913764ae0d782d](https://github.com/serenity-bdd/serenity-core/commit/5913764ae0d782d) Refactoring the CSS stylesheet
 * [f5707cff4acf500](https://github.com/serenity-bdd/serenity-core/commit/f5707cff4acf500) Improved readability in report stacktraces.
 * [9ceb2b20a57e1da](https://github.com/serenity-bdd/serenity-core/commit/9ceb2b20a57e1da) Fixed a reporting issue in tables

The overall result was the last failure, not the most severe failure in the table.
## v1.2.3-rc.9
### No issue
 * [9317d0b41fdcddc](https://github.com/serenity-bdd/serenity-core/commit/9317d0b41fdcddc) Fixed a bug where tags where not correctly reported

JUnit tags where not correctly reported when Cucumber scenarios were run before them in the same build.
 * [f4cc23afe16205c](https://github.com/serenity-bdd/serenity-core/commit/f4cc23afe16205c) test: Add question to query the selected country of the profile
 * [6725775ae89ac82](https://github.com/serenity-bdd/serenity-core/commit/6725775ae89ac82) test: fix the danaCanSelectHerCountry testcase
 * [5e3884797411ce5](https://github.com/serenity-bdd/serenity-core/commit/5e3884797411ce5) test: Add test for SelectFromOptions.byValue(code)
 * [b8b385a070b73f4](https://github.com/serenity-bdd/serenity-core/commit/b8b385a070b73f4) refactor: SelectFromOptions for more readability
 * [c252f0a1d05c50c](https://github.com/serenity-bdd/serenity-core/commit/c252f0a1d05c50c) fix: SelectByValueFromTarget to select from value instead of the visible text
 * [a38cc8aa2d7fbba](https://github.com/serenity-bdd/serenity-core/commit/a38cc8aa2d7fbba) use byte-buddy to improve serenity rest-assured integration
 * [7cd93ec3dbf04ba](https://github.com/serenity-bdd/serenity-core/commit/7cd93ec3dbf04ba) Fixed a test.
 * [5017083af6f9564](https://github.com/serenity-bdd/serenity-core/commit/5017083af6f9564) Added a missed class.
 * [23e31ee1616d8e6](https://github.com/serenity-bdd/serenity-core/commit/23e31ee1616d8e6) Improved reporting on failing Screenplay questions.
## v1.2.3-rc.8
### No issue
 * [d4d003ad9b5e6a9](https://github.com/serenity-bdd/serenity-core/commit/d4d003ad9b5e6a9) Fixed a unit test
 * [64af9652cc14991](https://github.com/serenity-bdd/serenity-core/commit/64af9652cc14991) Fixed a bug in the Cucumber table results

Table results were not correctly displayed when a nested error occured within the Screenplay steps.
 * [32a4440a91135f2](https://github.com/serenity-bdd/serenity-core/commit/32a4440a91135f2) More flexible Screenplay question support.

This commit contains several useful new features for ScreenPlay questions and consequences:

1) You can now group Screenplay questions under a single step, by adding a label for the step like this

```
        then(dana).should("{0} should see the correct prices”,
                seeThat(theItemPrice(), equalTo(100)),
                seeThat(theVAT(), equalTo(20)),
                seeThat(theTotalPrice(), equalTo(120))
        );
```

2) You can also create groups of consequences and call them without a matcher in the `shouldSee()` method. For example, you could write the following:

```
then(dana).should(seeThat(thePriceIsCorrectlyDisplayed()));
```

The `thePriceIsCorrectlyDisplayed()` method returns an object of type Question<Void>, that groups several questions together:

```
public class DisplayedPrices implements Question<Void> {

    @Override
    public Void answeredBy(Actor actor) {
        actor.should(
                seeThat("the total price", ThePrice.total(), equalTo(100)),
                seeThat("the VAT", ThePrice.vat(), equalTo(20)),
                seeThat("the price with VAT", ThePrice.totalWithVAT(), equalTo(120))
        );
        return null;
    }
```
 * [7cd65cff6141acf](https://github.com/serenity-bdd/serenity-core/commit/7cd65cff6141acf) Fixed an error in the report templates
 * [e431febae343d1a](https://github.com/serenity-bdd/serenity-core/commit/e431febae343d1a) Fixed an error with the stack trace popups

Stack trace popup didnt’ appear if there were more than one error on the page.
 * [08fd41a853e3765](https://github.com/serenity-bdd/serenity-core/commit/08fd41a853e3765) Refactoring
 * [0b48fe90e1db866](https://github.com/serenity-bdd/serenity-core/commit/0b48fe90e1db866) Fixed an incorrect reporting of pending tests

And some other minor bug fixes.
 * [f5b757240a3bf59](https://github.com/serenity-bdd/serenity-core/commit/f5b757240a3bf59) Updated changelogs
 * [05574c4c3100ca9](https://github.com/serenity-bdd/serenity-core/commit/05574c4c3100ca9) Updated changelogs
## v1.2.3-rc.7
### [#346](https://github.com/serenity-bdd/serenity-core/issues/346) jira.url property breaks links to scenario details in reports.
 * [67c7733bdf97ce2](https://github.com/serenity-bdd/serenity-core/commit/67c7733bdf97ce2) Fixed #346
## v1.2.3-rc.6
### [#578](https://github.com/serenity-bdd/serenity-core/issues/578) How to make tests that are uploading files to work remotely also?
 * [f927a5d4de331c1](https://github.com/serenity-bdd/serenity-core/commit/f927a5d4de331c1) Fixed #578
 * [83695a7cf5634c4](https://github.com/serenity-bdd/serenity-core/commit/83695a7cf5634c4) Fixed example table formatting (see #578)
### [#58](https://github.com/serenity-bdd/serenity-core/issues/58) Question: How to use serenity to execute WebDriver remotely (eg. via saucelabs)
 * [c8fe8da3c5dba26](https://github.com/serenity-bdd/serenity-core/commit/c8fe8da3c5dba26) Fixed #58: any properties called saucelabs.* will be passed to the Saucelabs capability.
### [#618](https://github.com/serenity-bdd/serenity-core/issues/618) Browser is not started for test which is executed after pending
 * [c360a38a58cc376](https://github.com/serenity-bdd/serenity-core/commit/c360a38a58cc376) FIxed #618
### [#675](https://github.com/serenity-bdd/serenity-core/issues/675) Generating report is crashing for large test suite
 * [23157e7a0892fc8](https://github.com/serenity-bdd/serenity-core/commit/23157e7a0892fc8) Fixed #675
### [#684](https://github.com/serenity-bdd/serenity-core/issues/684) Serenity Report displays incorrect Scenario Outline
 * [6101c306a7d2e9f](https://github.com/serenity-bdd/serenity-core/commit/6101c306a7d2e9f) Fixed #684
### [#710](https://github.com/serenity-bdd/serenity-core/issues/710) waitFor(webElementFacade) not possible when chained withTimeoutOf()
 * [0ce66445231107d](https://github.com/serenity-bdd/serenity-core/commit/0ce66445231107d) Fixed #710
### [#712](https://github.com/serenity-bdd/serenity-core/issues/712) non-deterministic crash on steps class with multiple constructors
 * [09ec3d7bbfbf409](https://github.com/serenity-bdd/serenity-core/commit/09ec3d7bbfbf409) Possible fix for #712
### No issue
 * [c109c69faedc86f](https://github.com/serenity-bdd/serenity-core/commit/c109c69faedc86f) Display field place-holders in example titles.
 * [20ff8f8b7bdd5dc](https://github.com/serenity-bdd/serenity-core/commit/20ff8f8b7bdd5dc) Improved formatting for example tables.
 * [c3060f8f67db804](https://github.com/serenity-bdd/serenity-core/commit/c3060f8f67db804) Fixed an issue in closing browsers

In some cases, Cucumber would close the browser after each feature even when `serenity.restart.browser.for.each` was set to NEVER.
 * [77770a06ce5ce73](https://github.com/serenity-bdd/serenity-core/commit/77770a06ce5ce73) Run unit tests in parallel
 * [6ef9f779975d0a9](https://github.com/serenity-bdd/serenity-core/commit/6ef9f779975d0a9) Removed redundant tests
 * [9c88692e2d7d36c](https://github.com/serenity-bdd/serenity-core/commit/9c88692e2d7d36c) Test updates
 * [a6f792d0aec4608](https://github.com/serenity-bdd/serenity-core/commit/a6f792d0aec4608) Updated a test
 * [0f81a03ae3f9079](https://github.com/serenity-bdd/serenity-core/commit/0f81a03ae3f9079) Switched digests to SHA256
 * [9a85cc233b041af](https://github.com/serenity-bdd/serenity-core/commit/9a85cc233b041af) Refactored the wiremock tests to be more robust
## v1.2.3-rc.5
### [#671](https://github.com/serenity-bdd/serenity-core/issues/671) Acronym and underscore processing don&#39;t work well together
 * [4e9ec85f9362358](https://github.com/serenity-bdd/serenity-core/commit/4e9ec85f9362358) Fixed #671
### [#683](https://github.com/serenity-bdd/serenity-core/issues/683) Parameter serenity.restart.browser.frequency doesn&#39;t work
 * [3492b7f8abf3155](https://github.com/serenity-bdd/serenity-core/commit/3492b7f8abf3155) Fixed #683
### [#695](https://github.com/serenity-bdd/serenity-core/issues/695) Ability to use SerenityRest from a plain JUnit runner
 * [3ee43534c6e2e4b](https://github.com/serenity-bdd/serenity-core/commit/3ee43534c6e2e4b) tests for ability to use SerenityRest from a plain JUnit runner, issue #695
 * [bd37c94d57142c8](https://github.com/serenity-bdd/serenity-core/commit/bd37c94d57142c8) feat: Ability to use SerenityRest from a plain JUnit runner, issue #695
### No issue
 * [1b77fd21c7fc7a1](https://github.com/serenity-bdd/serenity-core/commit/1b77fd21c7fc7a1) a better name for a test class
 * [0861f5216211051](https://github.com/serenity-bdd/serenity-core/commit/0861f5216211051) Tweaked circleci config
 * [b6e40e1ddfec91e](https://github.com/serenity-bdd/serenity-core/commit/b6e40e1ddfec91e) Tidying up redundant files
 * [53b39b5c7c22599](https://github.com/serenity-bdd/serenity-core/commit/53b39b5c7c22599) Updated changelog
 * [1a9df386135fbd2](https://github.com/serenity-bdd/serenity-core/commit/1a9df386135fbd2) Fixed circleci config
 * [9342c317a392ab7](https://github.com/serenity-bdd/serenity-core/commit/9342c317a392ab7) Experimental CircleCI config
 * [9d051b2ab2c07d8](https://github.com/serenity-bdd/serenity-core/commit/9d051b2ab2c07d8) Experimental parallel jobs in CircleCI
 * [af14abd504ccc2b](https://github.com/serenity-bdd/serenity-core/commit/af14abd504ccc2b) Fixed the CircleCI build config.
 * [12ef12bb27895ae](https://github.com/serenity-bdd/serenity-core/commit/12ef12bb27895ae) The serenity.use.unique.browser is deprecated

Use serenity.restart.browser.for.each=scenario|story|feature|never instead
 * [ea140ff866aceda](https://github.com/serenity-bdd/serenity-core/commit/ea140ff866aceda) Better error reporting for invalid scenarios

If a scenario has no name, a `AScenarioHasNoNameException` will be thrown.
 * [4ed2edfe7559aaa](https://github.com/serenity-bdd/serenity-core/commit/4ed2edfe7559aaa) Fixed CircleCI config
 * [b51acb212284234](https://github.com/serenity-bdd/serenity-core/commit/b51acb212284234) Chore: Added a longer timeout for CI builds on circleci.
 * [0fb960b6c6a8d24](https://github.com/serenity-bdd/serenity-core/commit/0fb960b6c6a8d24) change property name for rerunning cucumber tests to test.retry.count.cucumber
 * [cc930689b279b96](https://github.com/serenity-bdd/serenity-core/commit/cc930689b279b96) Added a configuration file for CircleCI
 * [20823294facb274](https://github.com/serenity-bdd/serenity-core/commit/20823294facb274) Fixed dependency conflict
 * [68955f1ebedd777](https://github.com/serenity-bdd/serenity-core/commit/68955f1ebedd777) record/replay failed tests using external file -refactoring - use separate file for each test class
 * [01e6add45a6b06d](https://github.com/serenity-bdd/serenity-core/commit/01e6add45a6b06d) record/replay failed tests using external file -refactoring
 * [cca50292f4dfde3](https://github.com/serenity-bdd/serenity-core/commit/cca50292f4dfde3) record/replay failed tests using external file
 * [8fc149c3eed69b5](https://github.com/serenity-bdd/serenity-core/commit/8fc149c3eed69b5) Add BrowserStack test name to compatibilites
 * [58c9ce49d2a7f0d](https://github.com/serenity-bdd/serenity-core/commit/58c9ce49d2a7f0d) FIxed a bug where failing tests where not reported
 * [507b3ffdc2f0241](https://github.com/serenity-bdd/serenity-core/commit/507b3ffdc2f0241) Switch back to appium 4.0.0
 * [f95f2083bd8bb93](https://github.com/serenity-bdd/serenity-core/commit/f95f2083bd8bb93) https://github.com/serenity-bdd/serenity-core/issues/670
 * [8148fdd40e149d6](https://github.com/serenity-bdd/serenity-core/commit/8148fdd40e149d6) https://github.com/serenity-bdd/serenity-core/issues/670
 * [fc603dd2660e2c6](https://github.com/serenity-bdd/serenity-core/commit/fc603dd2660e2c6) feat: Added support for tags in the result checker plugin.
 * [9ce1236bafd17bb](https://github.com/serenity-bdd/serenity-core/commit/9ce1236bafd17bb) feat: Updated appium to 4.1.2
 * [dab0a7b593bb565](https://github.com/serenity-bdd/serenity-core/commit/dab0a7b593bb565) Updated Guava dependency to 20.0
 * [60a55d2897fbe7a](https://github.com/serenity-bdd/serenity-core/commit/60a55d2897fbe7a) Added support for tags in the result checker

You can now provide a tags property to the Maven checker plugin, to limit the check to test outcomes with that tag, e.g.

```
mvn serenity:check -Dtags=scrum:panther
```
## v1.2.3-rc.4
### No issue
 * [90b8e634d476fb5](https://github.com/serenity-bdd/serenity-core/commit/90b8e634d476fb5) Report optimisation
## v1.2.3-rc.2
### No issue
 * [39ed3686ba96e3c](https://github.com/serenity-bdd/serenity-core/commit/39ed3686ba96e3c) Minor reporting performance improvements
 * [b30eefd97f26957](https://github.com/serenity-bdd/serenity-core/commit/b30eefd97f26957) Minor performance improvements for tag reporting
## v1.2.3-rc.1
### [#655](https://github.com/serenity-bdd/serenity-core/issues/655) serenity-core 1.2.2: Use of &quot;@UseTestDataFrom&quot; results in &quot;IllegalArgumentException&quot;
 * [57b0c20ea14125f](https://github.com/serenity-bdd/serenity-core/commit/57b0c20ea14125f) Fixed #655

Use of "@UseTestDataFrom" results in "IllegalArgumentException.
### No issue
 * [6da2c8717e5d303](https://github.com/serenity-bdd/serenity-core/commit/6da2c8717e5d303) Updated smoketest dependencies
 * [f94c06d04cf11b1](https://github.com/serenity-bdd/serenity-core/commit/f94c06d04cf11b1) Temporarily suspend a test pending further investigation.
 * [d9d62cee1eb51f9](https://github.com/serenity-bdd/serenity-core/commit/d9d62cee1eb51f9) Temporarily suspend a test pending further investigation.
 * [f3ba174f7f3561a](https://github.com/serenity-bdd/serenity-core/commit/f3ba174f7f3561a) Reporting performance enhancements

Added the `report.timeout` property to allow the individual timeouts on report generations to be configured (to avoid hanging builds due to deadlocks in file resources, for example).
Added the `verbose.reporting` property to display details about what reports are being generated.
 * [b98a9ccd0a48052](https://github.com/serenity-bdd/serenity-core/commit/b98a9ccd0a48052) Don’t include the line number in the failure summary.

This gives more flexibility for matching similar failures.
 * [fc31fa5337ed73d](https://github.com/serenity-bdd/serenity-core/commit/fc31fa5337ed73d) Fixed an issue related to spaces in column names in parameterized tests

In some cases, column names containing acronyms and leading spaces in parameterized tests caused the freemarker reports to crash.
 * [4d35a72d307e76f](https://github.com/serenity-bdd/serenity-core/commit/4d35a72d307e76f) Minor refactoring
## v1.2.2-rc.12
### No issue
 * [0c0b86d1ab2268e](https://github.com/serenity-bdd/serenity-core/commit/0c0b86d1ab2268e) Removed redundant JSON field.
 * [b657f83743c59e9](https://github.com/serenity-bdd/serenity-core/commit/b657f83743c59e9) Added a project key to the test outcome format

The projectKey field is set by the serenity.project.key system property and can be used to group test outcomes for the same project.
 * [08d50c22ffa2ba7](https://github.com/serenity-bdd/serenity-core/commit/08d50c22ffa2ba7) Removed unnecessary field in the test outcome JSON
## v1.2.2-rc.11
### No issue
 * [dbdc482b3290b17](https://github.com/serenity-bdd/serenity-core/commit/dbdc482b3290b17) Added an error summary to the JSON output
 * [4e177f82a32a543](https://github.com/serenity-bdd/serenity-core/commit/4e177f82a32a543) Improved support for excluding unrelated requirements

You no longer need to set `serenity.exclude.unrelated.requirements` to true, just set the `serenity.exclude.unrelated.requirements.of.type` property to the requirement types that should be filtered.
## v1.2.2-rc.10
### No issue
 * [d59b9f7eb73f96b](https://github.com/serenity-bdd/serenity-core/commit/d59b9f7eb73f96b) Improved support for multi-module projects

Serenity will now read properties from the serenity.properties file in the parent project of a multi-module project, if no properties are found in the child project.
 * [bbf4f920b929344](https://github.com/serenity-bdd/serenity-core/commit/bbf4f920b929344) feat: Improved handling of maven multi-module projects
## v1.2.2-rc.9
### No issue
 * [bbaae78e3951271](https://github.com/serenity-bdd/serenity-core/commit/bbaae78e3951271) Fixed inconsistencies in test result reporting.
## v1.2.2-rc.8
### [#635](https://github.com/serenity-bdd/serenity-core/issues/635) Junit test case not failing when exception occurred in step class when timeout is specified in the main class
 * [39f5bb74d3f1db8](https://github.com/serenity-bdd/serenity-core/commit/39f5bb74d3f1db8) Fixed #635

Fixed #635 and other issues related to JUnit spawning new threads during test execution.
### [#644](https://github.com/serenity-bdd/serenity-core/issues/644) Unclear message in report when executing parameterized test
 * [aba5cba03bc6d25](https://github.com/serenity-bdd/serenity-core/commit/aba5cba03bc6d25) Fixed #644

Removed misleading and sometimes inaccurate test count console message.
### [#646](https://github.com/serenity-bdd/serenity-core/issues/646) Ignored parameterized tests are marked as Passed
 * [40585f38819f06c](https://github.com/serenity-bdd/serenity-core/commit/40585f38819f06c) Fixed #646 - more consistant reporting of ignored tests

Also fixed an inconsistancy in reporting data-driven tests for dry-run tests.
### No issue
 * [2ab45e30cf0e283](https://github.com/serenity-bdd/serenity-core/commit/2ab45e30cf0e283) Tidying up
 * [ef8aa0d686a7e3f](https://github.com/serenity-bdd/serenity-core/commit/ef8aa0d686a7e3f) Updated changelog
## v1.2.2-rc.7
### [#640](https://github.com/serenity-bdd/serenity-core/issues/640) Acronyms are poorly rendered in the reports
 * [c58dc47e98d35f9](https://github.com/serenity-bdd/serenity-core/commit/c58dc47e98d35f9) Fixed #640 - Acronyms are poorly rendered in the reports
### [#641](https://github.com/serenity-bdd/serenity-core/issues/641) InvalidManagedWebDriverFieldException when &quot;@Managed was found in the test case&quot;
 * [8b0da614f123fad](https://github.com/serenity-bdd/serenity-core/commit/8b0da614f123fad) Fixed #641
### [#642](https://github.com/serenity-bdd/serenity-core/issues/642) Error in FreeMarker template when passing Map in Parameterized test as a parameter
 * [e9b78b2cb040b28](https://github.com/serenity-bdd/serenity-core/commit/e9b78b2cb040b28) Fixed #642
### No issue
 * [f191573816c5b06](https://github.com/serenity-bdd/serenity-core/commit/f191573816c5b06) feat : Better support for acronyms in test titles.
 * [9bc8846e3164ed3](https://github.com/serenity-bdd/serenity-core/commit/9bc8846e3164ed3) feat: Multiple performables in conditional performables

You can now pass any number of performables to the andIfSo() and otherwise() methods of the conditional performables.
 * [5e4b6cec118ec08](https://github.com/serenity-bdd/serenity-core/commit/5e4b6cec118ec08) Updated changelog
## v1.2.2-rc.6
### [#608](https://github.com/serenity-bdd/serenity-core/issues/608) Support for @SpringBootTest
 * [c7186e90a8ecf63](https://github.com/serenity-bdd/serenity-core/commit/c7186e90a8ecf63) #608 @SpringBootTest support

Support dependency injection for step libraries annotated with
@SpringBootTest (introduced in spring-boot-test 1.4)

To test this feature additional dependency on
spring-boot-test and upgrade to Spring 4.3 is needed.
### [#612](https://github.com/serenity-bdd/serenity-core/pull/612) #612: Apply user properties to recommended Internet Explorer defaults
 * [d5ebabc6b58238e](https://github.com/serenity-bdd/serenity-core/commit/d5ebabc6b58238e) #612: Apply user properties to recommended default Internet Explorer capabilities, not the other way around
### [#618](https://github.com/serenity-bdd/serenity-core/issues/618) Browser is not started for test which is executed after pending
 * [e43e0db312c0997](https://github.com/serenity-bdd/serenity-core/commit/e43e0db312c0997) Fixed #618
### [#633](https://github.com/serenity-bdd/serenity-core/issues/633) Serenity Rest-Assured wrapper doesn&#39;t handle byte arrays like Rest-Assured
 * [13b08715a369996](https://github.com/serenity-bdd/serenity-core/commit/13b08715a369996) Fixed #633
### No issue
 * [f4b510ceee4461d](https://github.com/serenity-bdd/serenity-core/commit/f4b510ceee4461d) feat: reports include a summary.txt file

Report aggregation now produces a summary.txt file that contains a brief overview of the test results.
 * [938ad9faa226289](https://github.com/serenity-bdd/serenity-core/commit/938ad9faa226289) Updated a test
 * [fc4f96678de8197](https://github.com/serenity-bdd/serenity-core/commit/fc4f96678de8197) Updated spring and spring boot dependencies
 * [0045c6c8ca58702](https://github.com/serenity-bdd/serenity-core/commit/0045c6c8ca58702) Updated mockito and jbehave dependencies
 * [1ae03b7105eb3d3](https://github.com/serenity-bdd/serenity-core/commit/1ae03b7105eb3d3) Updated commons-io
 * [bfa8546d5bdb9b9](https://github.com/serenity-bdd/serenity-core/commit/bfa8546d5bdb9b9) Added a cleaner way to check that a web test is running.
 * [152e7ab3c1f630f](https://github.com/serenity-bdd/serenity-core/commit/152e7ab3c1f630f) Added a WIP test
 * [8584f0569e79c19](https://github.com/serenity-bdd/serenity-core/commit/8584f0569e79c19) Refactoring
 * [b79cd35b6f9482a](https://github.com/serenity-bdd/serenity-core/commit/b79cd35b6f9482a) Fixed regression in the @Managed annotation

Fixed an issue where the uniqueSession property of the @Managed annotation in JUnit was not being honored.
 * [4cd9db6414bad3d](https://github.com/serenity-bdd/serenity-core/commit/4cd9db6414bad3d) Tidying up
 * [30264cefbfa56c1](https://github.com/serenity-bdd/serenity-core/commit/30264cefbfa56c1) Added the `serenity.browser.maximized’ property.
 * [e43f35351025a6b](https://github.com/serenity-bdd/serenity-core/commit/e43f35351025a6b) Better configuring of compressed or readable report names.
 * [6c44b5a77da9372](https://github.com/serenity-bdd/serenity-core/commit/6c44b5a77da9372) Enable better configuration of markdown

Use the enable.markdown property to define where you want Markdown code to be rendered (you can put multiple values separated by commas):
  - story: in story titles
  - narrative: in story narrative texts
  - step: in step descriptions
## v1.2.2-rc.5
### No issue
 * [b09f0f46006d56a](https://github.com/serenity-bdd/serenity-core/commit/b09f0f46006d56a) Allow the aggregate feature to regenerate test outcome reports
 * [8bf7fbe3649e3ff](https://github.com/serenity-bdd/serenity-core/commit/8bf7fbe3649e3ff) Fixed a number of issues with requirements reporting.
 * [89f9c424ca54cf2](https://github.com/serenity-bdd/serenity-core/commit/89f9c424ca54cf2) Improved markdown rendering
 * [fdc98fb8ae78307](https://github.com/serenity-bdd/serenity-core/commit/fdc98fb8ae78307) Improved reporting.
## v1.2.2-rc.4
### No issue
 * [5f063953da9a42d](https://github.com/serenity-bdd/serenity-core/commit/5f063953da9a42d) Fixed an issue in rendering scenario outlines containing regex chars
## v1.2.2-rc.3
### No issue
 * [362193fefae7ddf](https://github.com/serenity-bdd/serenity-core/commit/362193fefae7ddf) Add the tags parameter to filter aggregate reports
 * [2d7e01795d995bb](https://github.com/serenity-bdd/serenity-core/commit/2d7e01795d995bb) Removed redundant error message

Don’t throw an error message if a screenshot already exists.
## v1.2.2-rc.1
### [#631](https://github.com/serenity-bdd/serenity-core/issues/631) Report generated with wrong stats when using JBehave GivenStories
 * [077bd6e86521045](https://github.com/serenity-bdd/serenity-core/commit/077bd6e86521045) Fix for #631
### No issue
 * [6b5c709de0cffed](https://github.com/serenity-bdd/serenity-core/commit/6b5c709de0cffed) Fixed a potential null-pointer exception
 * [3a81fc4945724c7](https://github.com/serenity-bdd/serenity-core/commit/3a81fc4945724c7) Unit test fixes
 * [ea4e259042b84f5](https://github.com/serenity-bdd/serenity-core/commit/ea4e259042b84f5) Bug fix
## v1.2.1-rc.9
### No issue
 * [04f3ff18674893f](https://github.com/serenity-bdd/serenity-core/commit/04f3ff18674893f) Fixed an error in Cucumber rendering

Fixed an error where the name of an example table didn’t appear on the reports.
 * [77b6ac94095658f](https://github.com/serenity-bdd/serenity-core/commit/77b6ac94095658f) Make it possible to disable cosole banners

Set serenity.console.headings to quiet to not display any banners to console output.
 * [c23d159a769570d](https://github.com/serenity-bdd/serenity-core/commit/c23d159a769570d) Made the parameter reporting in data driven tests more robust.
 * [83c6bca2fbcd115](https://github.com/serenity-bdd/serenity-core/commit/83c6bca2fbcd115) Improved parameter substitution for parameterised test reports
 * [44eea7d60d1cf05](https://github.com/serenity-bdd/serenity-core/commit/44eea7d60d1cf05) Better test outline reporting for parameterised tests
 * [e6c49b63c671f84](https://github.com/serenity-bdd/serenity-core/commit/e6c49b63c671f84) Fixed an error in the requirement breadcrumbs
 * [53f51d76336418b](https://github.com/serenity-bdd/serenity-core/commit/53f51d76336418b) Respect line breaks in @Narrative descriptions.
## v1.2.1-rc.8
### No issue
 * [c35497bd2ff95f6](https://github.com/serenity-bdd/serenity-core/commit/c35497bd2ff95f6) Minor bug fix
 * [803bfe796b2a01b](https://github.com/serenity-bdd/serenity-core/commit/803bfe796b2a01b) Fixed an issue with broken links in the breadcrumbs
 * [8b18f4c7dae3631](https://github.com/serenity-bdd/serenity-core/commit/8b18f4c7dae3631) Improved requirement merging between package and file-based requirement structures.
 * [e2d75db73248f6e](https://github.com/serenity-bdd/serenity-core/commit/e2d75db73248f6e) Added a property for compressing report file names.
## v1.2.1-rc.7
### [#624](https://github.com/serenity-bdd/serenity-core/issues/624) File upload with remote web driver broken when using WebElementFacade
 * [95980f3a49ee8d7](https://github.com/serenity-bdd/serenity-core/commit/95980f3a49ee8d7) Fixed #624
### [#626](https://github.com/serenity-bdd/serenity-core/issues/626) NullPointerException at PageObject.openPageAtUrl
 * [23fdacef00bb907](https://github.com/serenity-bdd/serenity-core/commit/23fdacef00bb907) Fixed #626
### No issue
 * [a5a29f787e9a31f](https://github.com/serenity-bdd/serenity-core/commit/a5a29f787e9a31f) Tidying up.
 * [aacf30984bd6d7a](https://github.com/serenity-bdd/serenity-core/commit/aacf30984bd6d7a) Focus requirements reports on requirements related to the executed tests

You can now use the `serenity.exclude.unrelated.requirements.of.type` to only include requirements related to the features or capabilities that are in progress. This makes the report generation faster and the reports more relevant. For example, to exclude capabilities with no executed tests (but to include neighboring features with no executed tests) you could set this property as follows:

```
serenity.exclude.unrelated.requirements.of.type=capability
```

The default value is “capabilty, feature”, so both capabilities and features without any tests (including pending tests) will not appear in the reports.

To include all requirements at all levels, you can configure this property as follows:
```
serenity.exclude.unrelated.requirements.of.type=none
```
 * [394a87298c61132](https://github.com/serenity-bdd/serenity-core/commit/394a87298c61132) Focused reporting

Reports can now be limited to the requirements related to the tests exectued.
 * [c3fd8deded86709](https://github.com/serenity-bdd/serenity-core/commit/c3fd8deded86709) Fixed an issue with incorrect requirement listings.
 * [8f5d7013c882728](https://github.com/serenity-bdd/serenity-core/commit/8f5d7013c882728) Improved reporting performance.
 * [ee7b28c2ab79b23](https://github.com/serenity-bdd/serenity-core/commit/ee7b28c2ab79b23) Run requirement type reports in parallel.
 * [4903da8a71c54fe](https://github.com/serenity-bdd/serenity-core/commit/4903da8a71c54fe) Refactoring report execution mechanism
 * [b2220197cc951f8](https://github.com/serenity-bdd/serenity-core/commit/b2220197cc951f8) Default requirements reports are now always generated.

If requirements are not configured for a test, default requirements reports will be generated based on the test tags.
 * [f2068e0a1a1b867](https://github.com/serenity-bdd/serenity-core/commit/f2068e0a1a1b867) Fixing Javadoc warnings
 * [1504f766136ed23](https://github.com/serenity-bdd/serenity-core/commit/1504f766136ed23) More efficient tag report generation
 * [4f1d3ed72d80248](https://github.com/serenity-bdd/serenity-core/commit/4f1d3ed72d80248) Minor refactoring
 * [e575c6a4da1f6c8](https://github.com/serenity-bdd/serenity-core/commit/e575c6a4da1f6c8) Ensure that page source code is only generated if available.
 * [9e393fab6abd995](https://github.com/serenity-bdd/serenity-core/commit/9e393fab6abd995) Allow easy configuration of environment variables for testing

Using the ConfiguredEnvironment class as a central point of contact for environment variables.
 * [fdecc2ae6d076e2](https://github.com/serenity-bdd/serenity-core/commit/fdecc2ae6d076e2) Added some diagnostics
 * [d6c5c7d036724f6](https://github.com/serenity-bdd/serenity-core/commit/d6c5c7d036724f6) Improvements to reporting performance

Now using a buffered writer and streams to merge the Freemarker templated.
 * [59407d05c7a2839](https://github.com/serenity-bdd/serenity-core/commit/59407d05c7a2839) Ignore files not intended for github
 * [e30ae07444911b6](https://github.com/serenity-bdd/serenity-core/commit/e30ae07444911b6) Fixed an issue with some tests failing when generating uncompressed report names
 * [dd8aaa3be031dcf](https://github.com/serenity-bdd/serenity-core/commit/dd8aaa3be031dcf) Restored legacy support for old saucelabs configs
 * [a698abe0c2f7f33](https://github.com/serenity-bdd/serenity-core/commit/a698abe0c2f7f33) Fixed an issue where passing a null parameter to instrumented classes.
 * [7f1e8983ac9b4c5](https://github.com/serenity-bdd/serenity-core/commit/7f1e8983ac9b4c5) Better error reporting for dodgy page objects.
 * [35dadf7b8cc69b8](https://github.com/serenity-bdd/serenity-core/commit/35dadf7b8cc69b8) General refactoring
 * [4adf6f76463ef07](https://github.com/serenity-bdd/serenity-core/commit/4adf6f76463ef07) Updated sample reports with more realistic data
## v1.2.1-rc.6
### No issue
 * [6863da9746c398e](https://github.com/serenity-bdd/serenity-core/commit/6863da9746c398e) Record the history of retried tests.
 * [c9881885ce7b0d8](https://github.com/serenity-bdd/serenity-core/commit/c9881885ce7b0d8) Test refactoring
 * [a8c239fd11a8523](https://github.com/serenity-bdd/serenity-core/commit/a8c239fd11a8523) Removed unnecessary logging
 * [5f53b4d58e0a5cd](https://github.com/serenity-bdd/serenity-core/commit/5f53b4d58e0a5cd) Don’t use markdown formatting for example tables.
 * [275252104ad4ad2](https://github.com/serenity-bdd/serenity-core/commit/275252104ad4ad2) Improved handling of retried test

You can specify the maximum number of retries using the `test.retry.count` property. If a test fails, Serenity will retry up to `test.retry.count` times. If the test eventually passes, it will be tagged with a ‘unstable test’ tag.
## v1.2.1-rc.5
### [#573](https://github.com/serenity-bdd/serenity-core/pull/573) #Issue 573: ignore more exceptions
 * [8584fc4ed6f7483](https://github.com/serenity-bdd/serenity-core/commit/8584fc4ed6f7483) Manually merged in pull request #573
### No issue
 * [e0350359af5f4f6](https://github.com/serenity-bdd/serenity-core/commit/e0350359af5f4f6) Add better cross-platform support for tags

You can now use the alternative “color=red” notation instead of “color:red” for tags, which makes it possible to provide command-line tag options for Cucumber.

The “~” symbol can now be used as a “not” operator for tags, e.g. “~color:red”.
 * [f4e14702be77537](https://github.com/serenity-bdd/serenity-core/commit/f4e14702be77537) rerun failed tests :"mvn verify -Dtest.retry.count=..."
 * [785c96718871f7e](https://github.com/serenity-bdd/serenity-core/commit/785c96718871f7e) Test refactoring
 * [f0706e4d8467c72](https://github.com/serenity-bdd/serenity-core/commit/f0706e4d8467c72) Test refactoring
 * [08186bab0a6000f](https://github.com/serenity-bdd/serenity-core/commit/08186bab0a6000f) Fixed issue where manual tests were not recoginsed in JUnit parameterised tests.
 * [92c7cdfa8f928de](https://github.com/serenity-bdd/serenity-core/commit/92c7cdfa8f928de) Test refactoring
 * [9f313c4c6b08d16](https://github.com/serenity-bdd/serenity-core/commit/9f313c4c6b08d16) Improved reporting for parameterized tests.
 * [9b3050cfac50ced](https://github.com/serenity-bdd/serenity-core/commit/9b3050cfac50ced) Test refactoring
 * [65669b28e26fef8](https://github.com/serenity-bdd/serenity-core/commit/65669b28e26fef8) Improved reporting for parameterized tests.
 * [9fe105059796eb4](https://github.com/serenity-bdd/serenity-core/commit/9fe105059796eb4) Test refactoring
 * [848db5015b12aa5](https://github.com/serenity-bdd/serenity-core/commit/848db5015b12aa5) Test refactoring
## v1.2.1-rc.4
### No issue
 * [18683a26508c4e3](https://github.com/serenity-bdd/serenity-core/commit/18683a26508c4e3) Test refactoring
 * [5cacf5e6f0c5f51](https://github.com/serenity-bdd/serenity-core/commit/5cacf5e6f0c5f51) Test refactoring
 * [7198c380c59be2b](https://github.com/serenity-bdd/serenity-core/commit/7198c380c59be2b) Updated unit tests
 * [bd34d157df9ab97](https://github.com/serenity-bdd/serenity-core/commit/bd34d157df9ab97) Updated unit tests
 * [c136ac924a1e129](https://github.com/serenity-bdd/serenity-core/commit/c136ac924a1e129) Updated unit tests
 * [d174c51a013e3df](https://github.com/serenity-bdd/serenity-core/commit/d174c51a013e3df) Test refactoring
 * [9bd29e9ed3a5308](https://github.com/serenity-bdd/serenity-core/commit/9bd29e9ed3a5308) Allow markdown format in parent story titles
 * [cba1b1ff82d9a7c](https://github.com/serenity-bdd/serenity-core/commit/cba1b1ff82d9a7c) Don’t produce XML reports by default

Now only HTML and JSON reports are generated by default.
If you need XML reports, set `output.formats` to “json,xml,html”
 * [497b9f03fc7e19b](https://github.com/serenity-bdd/serenity-core/commit/497b9f03fc7e19b) Reinstated SNAPSHOT support for the release process
## v1.2.1-rc.3
### No issue
 * [5fa627a7e58313a](https://github.com/serenity-bdd/serenity-core/commit/5fa627a7e58313a) Support for more flexible requirements structures

Requirements packages can now have a mixture of levels, e.g.
```
+features
    + grain                           -> capability
       + wheat                      -> feature
            + organic               -> feature
                + GrowOrganic  -> story
    + fruit                             -> capability
        + apples                     -> feature
            GrowApples           -> story
    + veges                          -> capability
             GrowPotatoes       -> story
    + GeneralFarming          -> story
```
 * [c782c60cbf9756a](https://github.com/serenity-bdd/serenity-core/commit/c782c60cbf9756a) Provide better support for uneven requirements package structures.
## v1.2.1-rc.2
### No issue
 * [4f1a9a5b48ac0d5](https://github.com/serenity-bdd/serenity-core/commit/4f1a9a5b48ac0d5) Removed jquery field highlighting
 * [e550ae80fb20146](https://github.com/serenity-bdd/serenity-core/commit/e550ae80fb20146) Fixed minor issue with tests
 * [03c7e2e4254e7dc](https://github.com/serenity-bdd/serenity-core/commit/03c7e2e4254e7dc) Allow better control of browser sessions when using multiple actors

You can now use the `serenity.different.browsers.for.each.actor` (set to true by default) to make multiple actors use the same browser, even in the same test. This can be useful if actors are used to illustrate the intent of a test, but no tests use more than one actor simultaneously.
## v1.2.1-rc.1
### No issue
 * [55ef8e18e307bd7](https://github.com/serenity-bdd/serenity-core/commit/55ef8e18e307bd7) Fixed an issue with webdriver persistance

WebDriver proxies can now be kept and reused after being closed.
 * [d95763073fd5c7b](https://github.com/serenity-bdd/serenity-core/commit/d95763073fd5c7b) Test refactoring
 * [caadcf86fded879](https://github.com/serenity-bdd/serenity-core/commit/caadcf86fded879) Fixed the release plugin
 * [fe0549b659aa47c](https://github.com/serenity-bdd/serenity-core/commit/fe0549b659aa47c) WIP
 * [acc3563c03a7221](https://github.com/serenity-bdd/serenity-core/commit/acc3563c03a7221) Fixed a layout issue in the scenario descriptions
 * [5da95275d03d4ab](https://github.com/serenity-bdd/serenity-core/commit/5da95275d03d4ab) Updated release config
## v1.2.0
### [#586](https://github.com/serenity-bdd/serenity-core/issues/586) Issue links appear twice in report
 * [d738746e948cc7c](https://github.com/serenity-bdd/serenity-core/commit/d738746e948cc7c) Applied fix for #586 to other parts of the report.
### No issue
 * [e6dbaf0ebd0bdfb](https://github.com/serenity-bdd/serenity-core/commit/e6dbaf0ebd0bdfb) Ensure that all the browsers are eventually closed.
 * [829f1ce4aaf4d48](https://github.com/serenity-bdd/serenity-core/commit/829f1ce4aaf4d48) Fixed an issue with Cucumber requirements reporting.
 * [4cecdf327857ac0](https://github.com/serenity-bdd/serenity-core/commit/4cecdf327857ac0) Added an experimental WaitUntil interaction class.
 * [03233a7c507e63e](https://github.com/serenity-bdd/serenity-core/commit/03233a7c507e63e) Ensure that all browsers are closed after the tests
## v1.1.44
### No issue
 * [0032fce8a42e590](https://github.com/serenity-bdd/serenity-core/commit/0032fce8a42e590) Updated gradle-git plugin
## v1.2.0-rc.3
### No issue
 * [b5d1febe8c47894](https://github.com/serenity-bdd/serenity-core/commit/b5d1febe8c47894) Removed redundant test
 * [ab45a1fb7af0eb7](https://github.com/serenity-bdd/serenity-core/commit/ab45a1fb7af0eb7) Use the parent package or folder to avoid issues with multiple features or stories with the same name.
 * [11b268cbb79fe39](https://github.com/serenity-bdd/serenity-core/commit/11b268cbb79fe39) Updated Cucumber dependencies to 1.2.5
## v1.2.0-rc.2
### [#545](https://github.com/serenity-bdd/serenity-core/issues/545) Html in step descriptions is broken
 * [4759838206264ba](https://github.com/serenity-bdd/serenity-core/commit/4759838206264ba) Added support for markdown in reports (#545)

You can now include **markdown** in test and step titles and descriptions, including feature and story files. The implementation uses https://code.google.com/archive/p/markdown4j/, and should support the extensions described on this site.
### [#570](https://github.com/serenity-bdd/serenity-core/issues/570) Level of precision rather high on average steps per test
 * [aa12f0a0508d4f4](https://github.com/serenity-bdd/serenity-core/commit/aa12f0a0508d4f4) Fixed #570
 * [0629976bbd9afa2](https://github.com/serenity-bdd/serenity-core/commit/0629976bbd9afa2) Fixed #570
### [#579](https://github.com/serenity-bdd/serenity-core/issues/579) WebElementFacade.waitUntilNotVisible() throws NoSuchElementException
 * [792bc3df97b71a4](https://github.com/serenity-bdd/serenity-core/commit/792bc3df97b71a4) Fixed #579: WebElementFacade.waitUntilNotVisible() throws NoSuchElementException
### [#582](https://github.com/serenity-bdd/serenity-core/issues/582) Screenplay features fail with ClassCastException when in dry run mode
 * [3336427bcea5250](https://github.com/serenity-bdd/serenity-core/commit/3336427bcea5250) Fixed #582: correct handling of DryRun mode for Screenplay tests
### [#586](https://github.com/serenity-bdd/serenity-core/issues/586) Issue links appear twice in report
 * [4ff84774bd937dc](https://github.com/serenity-bdd/serenity-core/commit/4ff84774bd937dc) Fixed #586 (Issue links appear twice in report)
### [#589](https://github.com/serenity-bdd/serenity-core/pull/589) Issue #589 - Reinstate retry of IE Browser instance creation on NoSuchSessionException
 * [762ce04e5508318](https://github.com/serenity-bdd/serenity-core/commit/762ce04e5508318) #589: Reinstate retry of IE Browser instance creation on NoSuchSessionException
### [#590](https://github.com/serenity-bdd/serenity-core/issues/590) Non-unique class names in test structure causes incorrect report aggregation
 * [29a9ef7507f6e7f](https://github.com/serenity-bdd/serenity-core/commit/29a9ef7507f6e7f) fix: (experimental) fix for #590

Non-unique class names in test structure causes incorrect report aggregation.
### No issue
 * [9b5a603552e5604](https://github.com/serenity-bdd/serenity-core/commit/9b5a603552e5604) Test refactoring
 * [23b5abd1af9dfdd](https://github.com/serenity-bdd/serenity-core/commit/23b5abd1af9dfdd) Adding markdown support
 * [ead9940e3bb361b](https://github.com/serenity-bdd/serenity-core/commit/ead9940e3bb361b) fix: avoid creation of multiple driver services.
 * [7609b4476fc978e](https://github.com/serenity-bdd/serenity-core/commit/7609b4476fc978e) Improved Saucelabs support

All capabilites defined in saucelabs.* system properties are now passed to Saucelabs.
 * [0eaeb3a85569c27](https://github.com/serenity-bdd/serenity-core/commit/0eaeb3a85569c27) Minor refactoring
## v1.2.0-rc.1
### [#551](https://github.com/serenity-bdd/serenity-core/pull/551) Issue #551: added support for deselecting options
 * [a91ec2258fd1e64](https://github.com/serenity-bdd/serenity-core/commit/a91ec2258fd1e64) Issue #551: added support for deselecting options
### [#571](https://github.com/serenity-bdd/serenity-core/issues/571) ResponseBody automatically printed.
 * [ef8369f138b3397](https://github.com/serenity-bdd/serenity-core/commit/ef8369f138b3397) Fixed #571: ResponseBody automatically printed
### No issue
 * [c9d67270af57638](https://github.com/serenity-bdd/serenity-core/commit/c9d67270af57638) Made the smoke tests more robust
 * [b0e057b21e17907](https://github.com/serenity-bdd/serenity-core/commit/b0e057b21e17907) Ignore IDEA project files for the smoketest
 * [2f50f8617a3a565](https://github.com/serenity-bdd/serenity-core/commit/2f50f8617a3a565) Minor refactoring
 * [1b961043770083f](https://github.com/serenity-bdd/serenity-core/commit/1b961043770083f) Improved support for BrowserStack capability configuration.
 * [e419576baf6117e](https://github.com/serenity-bdd/serenity-core/commit/e419576baf6117e) docs: Untangled an unusual sentence structure in the README file.

Converted a Yoda-ish sentence about the Contributing document into
something clearer.
 * [0a8bf27bc0e5ca1](https://github.com/serenity-bdd/serenity-core/commit/0a8bf27bc0e5ca1) Fixed some issues when no gecko dirver was present.
 * [9b0561df04f0d3d](https://github.com/serenity-bdd/serenity-core/commit/9b0561df04f0d3d) Fixed an error occuring when no gecko driver is present on the file system.
 * [0cc86e67f88e8c0](https://github.com/serenity-bdd/serenity-core/commit/0cc86e67f88e8c0) Integrated better support for Edge
 * [5da0b7f7a4dcb6a](https://github.com/serenity-bdd/serenity-core/commit/5da0b7f7a4dcb6a) Removed unnecessary code
 * [17e33f0a78f7b05](https://github.com/serenity-bdd/serenity-core/commit/17e33f0a78f7b05) Backed out WebDriverManager integration

We have slated an internal implementation at some point in the future.
 * [9a654a517dd9561](https://github.com/serenity-bdd/serenity-core/commit/9a654a517dd9561) Added sample phantomjs binary
 * [aec742df768dbf1](https://github.com/serenity-bdd/serenity-core/commit/aec742df768dbf1) Serenity now downloads the webdriver binaries automatically if not present

Added integration with WebDriverManager (https://github.com/bonigarcia/webdrivermanager), which allows the latest WebDriver binaries to be downloaded automatically if they are not present on the machine. You can disable this behaviour using the `webdriver.autodownload` property.
 * [1de7a5ce5e2bb9a](https://github.com/serenity-bdd/serenity-core/commit/1de7a5ce5e2bb9a) Minor refactoring
 * [79fc45b9da5c558](https://github.com/serenity-bdd/serenity-core/commit/79fc45b9da5c558) Added better support for web driver services.
 * [19f5eac2721b87e](https://github.com/serenity-bdd/serenity-core/commit/19f5eac2721b87e) Fixed a sporatic issue with shutting down ScreenPlay tests.
 * [49ef71ece5b9e73](https://github.com/serenity-bdd/serenity-core/commit/49ef71ece5b9e73) Added some negative web state matchers
 * [e3bfa62afd5c4bb](https://github.com/serenity-bdd/serenity-core/commit/e3bfa62afd5c4bb) Improved driver management to use driver services where possible.
 * [e9c302a54a77dd3](https://github.com/serenity-bdd/serenity-core/commit/e9c302a54a77dd3) Experimental refactoring of the ChromeDriver
 * [7c10444c962b992](https://github.com/serenity-bdd/serenity-core/commit/7c10444c962b992) Updated Firebug plugin
 * [e7f5275c6c82563](https://github.com/serenity-bdd/serenity-core/commit/e7f5275c6c82563) Refactoring JQuery injection logic.
 * [e69b719624938c4](https://github.com/serenity-bdd/serenity-core/commit/e69b719624938c4) chore: test refactoring
 * [1d0b9071f447559](https://github.com/serenity-bdd/serenity-core/commit/1d0b9071f447559) Improved error reporting for timeout error messages.
 * [2dcf495520589e3](https://github.com/serenity-bdd/serenity-core/commit/2dcf495520589e3) Improvement in reporting stability in Windows

When generating the reports on Windows, report files sometimes get blocked by other processes, which prevents them from being copied. Now, if this happens, Serenity will retry 3 times before failing.
 * [9d3e79dbc15df1a](https://github.com/serenity-bdd/serenity-core/commit/9d3e79dbc15df1a) feat: Use WebDriver state matchers directly with WebElementFacade fields

For example:

```
assertThat(profilePage.nameField, isVisible());
```
## v1.1.42-rc.1
### [#536](https://github.com/serenity-bdd/serenity-core/issues/536) webdriver.chrome.binary property ignored
 * [3f45bf2c52158d7](https://github.com/serenity-bdd/serenity-core/commit/3f45bf2c52158d7) Issue #536: Support for webdriver.chrome.binary system property
### [#542](https://github.com/serenity-bdd/serenity-core/pull/542) Issue #542: ConsequenceMatchers: Better mismatch and Unknown field messages
 * [d24adc67d461f43](https://github.com/serenity-bdd/serenity-core/commit/d24adc67d461f43) Issue #542: ConsequenceMatchers: Better mismatch and Unknown field messages
### [#547](https://github.com/serenity-bdd/serenity-core/issues/547) Fail will be 100% when last scenario row in the scenario outline example fails. 
 * [b5bc0aa79eb9518](https://github.com/serenity-bdd/serenity-core/commit/b5bc0aa79eb9518) Fixed #547: incorrect aggregation of data-driven tests containing failures
### [#550](https://github.com/serenity-bdd/serenity-core/issues/550) Serenity doesn&#39;t generate folders for report
 * [a7b8eb08c8108df](https://github.com/serenity-bdd/serenity-core/commit/a7b8eb08c8108df) Fixed: Possible fix for #550
### [#553](https://github.com/serenity-bdd/serenity-core/issues/553) Valuable error message being is truncated in Serenity report
 * [ab4798095d8e506](https://github.com/serenity-bdd/serenity-core/commit/ab4798095d8e506) Fixed #553: Messages were being truncated too much.
### No issue
 * [ef16c27dd9f42c6](https://github.com/serenity-bdd/serenity-core/commit/ef16c27dd9f42c6) added FeatureStoryTagProvider in JUnitTagProviderStrategy - fix JUnit test
 * [85b9421e534b303](https://github.com/serenity-bdd/serenity-core/commit/85b9421e534b303) Added support for clearing cookies between scenarios
 * [541dafdeb9af3e3](https://github.com/serenity-bdd/serenity-core/commit/541dafdeb9af3e3) Attempting to make the browser capability reporting more accurate
 * [dc96fa1a00e7a8b](https://github.com/serenity-bdd/serenity-core/commit/dc96fa1a00e7a8b) Added an extra test about actors remembering values.
 * [a5d5a6e1afeb717](https://github.com/serenity-bdd/serenity-core/commit/a5d5a6e1afeb717) Made the code more resistant to legacy tag provider strategies.

The code should not crash if an old tag provider is used (e.g. an older version of Cucumber), but should revert to the old tag priorities.
 * [888da6af13fb147](https://github.com/serenity-bdd/serenity-core/commit/888da6af13fb147) added FeatureStoryTagProvider in JUnitTagProviderStrategy and remove additional tags that were added in TestOutcome
 * [93147a1fc4b0732](https://github.com/serenity-bdd/serenity-core/commit/93147a1fc4b0732) Fixed broken tests
 * [c4a5879f301449c](https://github.com/serenity-bdd/serenity-core/commit/c4a5879f301449c) Test refactoring
 * [ad2456425e15beb](https://github.com/serenity-bdd/serenity-core/commit/ad2456425e15beb) Refactored Chrome and Edge to use a driver service
 * [3392903d55b7109](https://github.com/serenity-bdd/serenity-core/commit/3392903d55b7109) Use a ChromeDriverService to improve the performance of Chrome tests
 * [fe8d3741bc832f5](https://github.com/serenity-bdd/serenity-core/commit/fe8d3741bc832f5) Use a ChromeDriverService to improve the performance of Chrome tests
 * [e591fe3bdd4848b](https://github.com/serenity-bdd/serenity-core/commit/e591fe3bdd4848b) Refactored the web tests
 * [24dbcf364f4ce60](https://github.com/serenity-bdd/serenity-core/commit/24dbcf364f4ce60) Refined the screenshot tests
 * [c59ee9058195328](https://github.com/serenity-bdd/serenity-core/commit/c59ee9058195328) Better support for overriding test results.
 * [bcb65a50ea3cdd1](https://github.com/serenity-bdd/serenity-core/commit/bcb65a50ea3cdd1) Use the ChromeService for the tests
 * [97625f359d317dd](https://github.com/serenity-bdd/serenity-core/commit/97625f359d317dd) https://github.com/serenity-bdd/serenity-core/issues/550 include resources for report
 * [da4b87022e74477](https://github.com/serenity-bdd/serenity-core/commit/da4b87022e74477) Test refactoring
 * [2342b7b4aa01d54](https://github.com/serenity-bdd/serenity-core/commit/2342b7b4aa01d54) Optimised the timeout test
 * [676f41d38b13f7d](https://github.com/serenity-bdd/serenity-core/commit/676f41d38b13f7d) Optimised the timeout test
 * [f75fcdd22c26bc1](https://github.com/serenity-bdd/serenity-core/commit/f75fcdd22c26bc1) Requirements were incorrectly read from the package structure in some cases
 * [ac10988d53f05b4](https://github.com/serenity-bdd/serenity-core/commit/ac10988d53f05b4) Requirements were incorrectly read from the package structure in some cases
 * [eb6d32aa412cfc0](https://github.com/serenity-bdd/serenity-core/commit/eb6d32aa412cfc0) Requirements were incorrectly read from the package structure in some cases
 * [fbdacbf6357c720](https://github.com/serenity-bdd/serenity-core/commit/fbdacbf6357c720) Requirements were incorrectly read from the package structure in some cases
 * [b1c6685c2381a71](https://github.com/serenity-bdd/serenity-core/commit/b1c6685c2381a71) Requirements were incorrectly read from the package structure in some cases
 * [e0c844346611631](https://github.com/serenity-bdd/serenity-core/commit/e0c844346611631) Test Refactoring
 * [5cbe17aeaae8654](https://github.com/serenity-bdd/serenity-core/commit/5cbe17aeaae8654) Added logging for troubleshooting a unit test issue on Ubuntu
 * [9f8cd3b6538f46b](https://github.com/serenity-bdd/serenity-core/commit/9f8cd3b6538f46b) Added logging for troubleshooting a unit test issue on Ubuntu
 * [697080a623bfe4c](https://github.com/serenity-bdd/serenity-core/commit/697080a623bfe4c) Added logging for troubleshooting a unit test issue on Ubuntu
 * [6ea614b2db91426](https://github.com/serenity-bdd/serenity-core/commit/6ea614b2db91426) Added logging for troubleshooting a unit test issue on Ubuntu
 * [86e9a01532e936c](https://github.com/serenity-bdd/serenity-core/commit/86e9a01532e936c) Added logging for troubleshooting a unit test issue on Ubuntu
 * [2ac412bdd923aeb](https://github.com/serenity-bdd/serenity-core/commit/2ac412bdd923aeb) Added logging for troubleshooting a unit test issue on Ubuntu
 * [9e6ab0ad8bf8597](https://github.com/serenity-bdd/serenity-core/commit/9e6ab0ad8bf8597) Refactored a test to be more reliable on different JVMs
 * [1f66badbbb1d12e](https://github.com/serenity-bdd/serenity-core/commit/1f66badbbb1d12e) Refactored a test to be more reliable on different JVMs
 * [e0435fe9581413c](https://github.com/serenity-bdd/serenity-core/commit/e0435fe9581413c) Refactored a test to be more reliable on different JVMs
 * [cfe7608bf0a8600](https://github.com/serenity-bdd/serenity-core/commit/cfe7608bf0a8600) Refactored screenshot-related tests to simplify test maintenance
 * [5a93bfaf44f3097](https://github.com/serenity-bdd/serenity-core/commit/5a93bfaf44f3097) added hasHighPriority() in TagProviderStrategy
 * [fa663cd1d708f01](https://github.com/serenity-bdd/serenity-core/commit/fa663cd1d708f01) partial work for fixing windows build failure.
## v1.1.41-rc.1
### No issue
 * [a1b7f83b7b015d9](https://github.com/serenity-bdd/serenity-core/commit/a1b7f83b7b015d9) feat: Improved support for Internet Explorer
 * [0b82d0e723c2bcb](https://github.com/serenity-bdd/serenity-core/commit/0b82d0e723c2bcb) fix: Fixed an issue creating the IE driver.
 * [d1b5469d6300561](https://github.com/serenity-bdd/serenity-core/commit/d1b5469d6300561) Removed unnecessary logging
 * [bb4f60b14f1d2dc](https://github.com/serenity-bdd/serenity-core/commit/bb4f60b14f1d2dc) Support for webdriver.ie.driver system property
## v1.1.40-rc.1
### No issue
 * [fca1739982754f6](https://github.com/serenity-bdd/serenity-core/commit/fca1739982754f6) Added support for comparators in aggregate questions
 * [1ee69c63242733a](https://github.com/serenity-bdd/serenity-core/commit/1ee69c63242733a) refactoring: Fine-tuned the AggregateQuestions API
 * [95cbabb52d19fe1](https://github.com/serenity-bdd/serenity-core/commit/95cbabb52d19fe1) feat: Added Aggregate features

Special questions that take that work with collection-based questions to answer aggregate questions such as total, max,  min and sum.

```
AggregateQuestions.theTotalNumberOf(whatColours)
```
## v1.1.39-rc.2
### No issue
 * [1997e9271138866](https://github.com/serenity-bdd/serenity-core/commit/1997e9271138866) Fix: issue with too many browsers opening.

In some older-style JUnit tests, two browsers were opening where only one should have opened.
## v1.1.39-rc.1
### No issue
 * [f7f782baaa74abb](https://github.com/serenity-bdd/serenity-core/commit/f7f782baaa74abb) Refactored the requirements tests to make them more robust
 * [0d11a8e94eb4f1c](https://github.com/serenity-bdd/serenity-core/commit/0d11a8e94eb4f1c) Renamed SelectOptions interaction for more consistancy
 * [d9399232d60b05a](https://github.com/serenity-bdd/serenity-core/commit/d9399232d60b05a) Fix: Fixed an issue with the attribute and css targets
 * [4bb199054d46c5d](https://github.com/serenity-bdd/serenity-core/commit/4bb199054d46c5d) Minor refactoring
 * [cb7c1995168e0d4](https://github.com/serenity-bdd/serenity-core/commit/cb7c1995168e0d4) Fix: Fixed an issue where you could not check the selected status of an invisible element

WebDriver lets you call isSelected() on elements whether they are visible or not. Serenity ensured that the element was visible first. However there are cases where the element is not visible to webdriver, but is still on the screen (for example  with some Javascript frameworks).
 * [3f44818f2163c36](https://github.com/serenity-bdd/serenity-core/commit/3f44818f2163c36) Added an Interation class to open a URL directly.
## v1.1.38-rc.1
### [#520](https://github.com/serenity-bdd/serenity-core/issues/520) Request to include license.txt in the Jar files distributed through maven central
 * [f4592d880919108](https://github.com/serenity-bdd/serenity-core/commit/f4592d880919108) Fixed #520 - include Apache license in the distributed JAR files
### No issue
 * [d74a38478b8a728](https://github.com/serenity-bdd/serenity-core/commit/d74a38478b8a728) documentation: Added some Javadocs to explain the isDisplayed vs isVisible distinction.
 * [ffab8b508f283c9](https://github.com/serenity-bdd/serenity-core/commit/ffab8b508f283c9) fix: Fixed a broken test
 * [36f4b3ae899517f](https://github.com/serenity-bdd/serenity-core/commit/36f4b3ae899517f) Feature: Improved screenshot configuration

You can now configure how screenshots are taken for screenplay questions, e.g.
```
serenity.take.screenshots.for.tasks=before_and_after_each_step
serenity.take.screenshots.for.interactions=disabled
serenity.take.screenshots.for.questions=for_failures
```
 * [969c69643be6d17](https://github.com/serenity-bdd/serenity-core/commit/969c69643be6d17) Feature: more flexible screenshot configuration

You can now configure screenshots by the class or interface that contains the step. For ScreenPlay, this means you can now write something like this:
```
serenity.take.screenshots.for.tasks=before_and_after_each_step
serenity.take.screenshots.for.interactions=disabled
```
 * [fbb083a696289c0](https://github.com/serenity-bdd/serenity-core/commit/fbb083a696289c0) Tweaked the build scripts to make parallel tests configurable
 * [9c6d45c4e3c03cb](https://github.com/serenity-bdd/serenity-core/commit/9c6d45c4e3c03cb) Tweaked the build scripts to make parallel tests configurable
 * [98c2d68aab3d88c](https://github.com/serenity-bdd/serenity-core/commit/98c2d68aab3d88c) Refactored the BrowserMob service
 * [d52f332cd3e1f77](https://github.com/serenity-bdd/serenity-core/commit/d52f332cd3e1f77) Fixed a missing import
 * [a3a6e1fb73ad2d9](https://github.com/serenity-bdd/serenity-core/commit/a3a6e1fb73ad2d9) Made it possible to ignore invalid certificates when using BrowserMob Proxy
## v1.1.37-rc.9
### [#515](https://github.com/serenity-bdd/serenity-core/issues/515) isCurrentlyVisible and isCurrentlyEnabled methods wait for default timeout 
 * [9f3bb545d4c1d84](https://github.com/serenity-bdd/serenity-core/commit/9f3bb545d4c1d84) Fixed #515
### No issue
 * [15858a745342214](https://github.com/serenity-bdd/serenity-core/commit/15858a745342214) Refined some tests to make them more reliable on slow machines
 * [92341776eaaaaa8](https://github.com/serenity-bdd/serenity-core/commit/92341776eaaaaa8) Refined some tests to make them more reliable on slow machines
 * [fda5b83b50dd4e4](https://github.com/serenity-bdd/serenity-core/commit/fda5b83b50dd4e4) Added an Interaction class to Screenplay for more readability

Added the Interaction class to supersceed the Action class. The name ‘Interaction’ conveys the intent of this class more accurately.
## v1.1.37-rc.8
### No issue
 * [17ab32f20ad37de](https://github.com/serenity-bdd/serenity-core/commit/17ab32f20ad37de) Refactored out an unreliable test
 * [e0b01f3e92a2e87](https://github.com/serenity-bdd/serenity-core/commit/e0b01f3e92a2e87) Fixed a regression in the screenshot processing logic
 * [992a4a7ee16ad3d](https://github.com/serenity-bdd/serenity-core/commit/992a4a7ee16ad3d) Refactored the screenshot logic to be more modular
Now a new darkroom is used for each instance of BaseStepListener.
 * [5bcff13bfc5ca2a](https://github.com/serenity-bdd/serenity-core/commit/5bcff13bfc5ca2a) Added the usingAbilityTo() method

The `usingAbilityTo()` method is a synonyme for the `abilityTo()` method, that allows for more readable constructs.
 * [53b61a0d87d9e55](https://github.com/serenity-bdd/serenity-core/commit/53b61a0d87d9e55) Simplified logging

Moved several messages from INFO to DEBUG to reduce the volume of console output.
 * [0fa5892adb8045a](https://github.com/serenity-bdd/serenity-core/commit/0fa5892adb8045a) Simplified the Screenplay Ability interface

The `Ability` interface is now a simple marker class with no methods to implement. If you need an Ability that knows about it’s actor, you can implement (in addition to the `Abilitiy` interface the `RefersToActor` interface, which defines the `asActor()` method previously found in the `Ability` class.
## v1.1.37-rc.7
### [#177](https://github.com/serenity-bdd/serenity-core/issues/177) Test not included in report if constructor of Steps class throw exception
 * [494a704e9b974f1](https://github.com/serenity-bdd/serenity-core/commit/494a704e9b974f1) Partial fix for #177 (Test not included in report if constructor of Steps class throw exception)  - tests with a broken step class will still not appear in the reports but will no longer fail silently.
### [#227](https://github.com/serenity-bdd/serenity-core/issues/227) resetImplicitTimeout broken again
 * [32b0850200ef686](https://github.com/serenity-bdd/serenity-core/commit/32b0850200ef686) fix: Fixed #227 - issue with resetImplicitTimeout
### [#246](https://github.com/serenity-bdd/serenity-core/issues/246) Wrong wrapping params in step name
 * [837dc49b213387f](https://github.com/serenity-bdd/serenity-core/commit/837dc49b213387f) fix: Wrong wrapping params in step name (#246) and Assertion messages are displayed incompletely in report in case of failure (#380)
### [#285](https://github.com/serenity-bdd/serenity-core/issues/285) Serenity reporting showing incorrect results
 * [b8bdb90873ee87f](https://github.com/serenity-bdd/serenity-core/commit/b8bdb90873ee87f) Fix: Fixed #285 - incorrect reporting of errors when using  @Test(expected=...)
This was actually fixed in a previous commit, but these are some extra tests to make sure.
### [#380](https://github.com/serenity-bdd/serenity-core/issues/380) Assertion messages are displayed incompletely in report in case of failure.
 * [837dc49b213387f](https://github.com/serenity-bdd/serenity-core/commit/837dc49b213387f) fix: Wrong wrapping params in step name (#246) and Assertion messages are displayed incompletely in report in case of failure (#380)
### [#460](https://github.com/serenity-bdd/serenity-core/issues/460) Assert messages are corrupted in HTML report
 * [f856c759c1643cc](https://github.com/serenity-bdd/serenity-core/commit/f856c759c1643cc) Fixed #460
### [#462](https://github.com/serenity-bdd/serenity-core/issues/462) Reporting: the json.charset property is not set to UTF-8 by default
 * [c674388a9a482d0](https://github.com/serenity-bdd/serenity-core/commit/c674388a9a482d0) Fixed #462 - json reports should default to UTF-8.
### Jira
 * [ac036579c46f549](https://github.com/serenity-bdd/serenity-core/commit/ac036579c46f549) Use UTF-8 for all file reading and writing

Use UTF-8 for all file reading and writing to avoid charset issues on different environments.
 * [c674388a9a482d0](https://github.com/serenity-bdd/serenity-core/commit/c674388a9a482d0) Fixed #462 - json reports should default to UTF-8.
 * [a15d988887122ef](https://github.com/serenity-bdd/serenity-core/commit/a15d988887122ef) use thucydides.report.encoding property to store/load outcomes  default is UTF-8 ; triggered by https://github.com/serenity-bdd/serenity-cucumber/issues/41
 * [1f4ebb44f057fe4](https://github.com/serenity-bdd/serenity-core/commit/1f4ebb44f057fe4) use UTF-8 to load json outcomes from file ; triggered by https://github.com/serenity-bdd/serenity-cucumber/issues/41
### No issue
 * [c699febf49184df](https://github.com/serenity-bdd/serenity-core/commit/c699febf49184df) Test outcomes are now loaded in order of execution
 * [43d5f1e2c28999f](https://github.com/serenity-bdd/serenity-core/commit/43d5f1e2c28999f) Made the Screenplay Stage easier to use

Added core support for the Screenplay Stage, making it easier for tests to manage actors in a consistent manner.
 * [5625f7e0c2e4645](https://github.com/serenity-bdd/serenity-core/commit/5625f7e0c2e4645) Added links to the requirements types

Added links to the requirements types and acceptance criteria on the requirements page.
 * [a7b7c118a75e227](https://github.com/serenity-bdd/serenity-core/commit/a7b7c118a75e227) Use a separate WebDriver manager instance for each test

The WebDriverManager instance for each test is now ThreadLocal and managed by the ThucydidesWebDriverSupport class, rather than being a Guice singleton.
 * [0082ebd8e2e955f](https://github.com/serenity-bdd/serenity-core/commit/0082ebd8e2e955f) Added a “Requirements” breadcrumb for consistancy
 * [337c4f1d9648bf8](https://github.com/serenity-bdd/serenity-core/commit/337c4f1d9648bf8) Minor fixes and tidying up.
 * [db7fed3a871078e](https://github.com/serenity-bdd/serenity-core/commit/db7fed3a871078e) Tests without any steps called are now considered to be successful

Previously, tests with no steps in an example table were considered pending. Now they are considered successful.
 * [afb3fbb4bba4b79](https://github.com/serenity-bdd/serenity-core/commit/afb3fbb4bba4b79) Requirement hierarchies can now have tests at multiple levels in JUnit
 * [ddd940ed0dfa027](https://github.com/serenity-bdd/serenity-core/commit/ddd940ed0dfa027) Fixed issue with reporting untested requirements.
 * [4fa9cb96ee753ef](https://github.com/serenity-bdd/serenity-core/commit/4fa9cb96ee753ef) Improved requirements test result reporting
 * [6cfb8cbc238b390](https://github.com/serenity-bdd/serenity-core/commit/6cfb8cbc238b390) rerun tests for WhenLocatingWebElementsUsingEnhancedFindBys
 * [d34e6a94d687635](https://github.com/serenity-bdd/serenity-core/commit/d34e6a94d687635) try to fix WhenManagingWebdriverTimeouts
 * [d00518e2ef3301c](https://github.com/serenity-bdd/serenity-core/commit/d00518e2ef3301c) JiraUpdaterService - add constants in ThucydidesSystemProperty
 * [5c239ab7043935a](https://github.com/serenity-bdd/serenity-core/commit/5c239ab7043935a) JiraUpdaterService
 * [86f7b801178e658](https://github.com/serenity-bdd/serenity-core/commit/86f7b801178e658) Improved requirements reporting

Feature coverage is now reported in terms of the percentage of working requirements.
 * [8e0607690135852](https://github.com/serenity-bdd/serenity-core/commit/8e0607690135852) Improved requirements reporting.

Show the correct total requirements in the requirements table when requirements without associated tests are present.
 * [5b6e09f9f69e527](https://github.com/serenity-bdd/serenity-core/commit/5b6e09f9f69e527) Improved requirements reporting.

Requirements with no corresponding stories are shown with an empty checkbox.
 * [7b518c335f7f62a](https://github.com/serenity-bdd/serenity-core/commit/7b518c335f7f62a) Included expected title in stack trace if waitForTitleToAppear (and similar methods) timeout
 * [3b07863dd1a9063](https://github.com/serenity-bdd/serenity-core/commit/3b07863dd1a9063) Fix: Narrative texts were not appearing in the reports

Narrative texts defined using the @Narrative annotation in classes and package-info.java files are now reported correctly. A package-info.java file can be placed in an otherwise empty package to define a requirement with no tests.
 * [0769272807fe137](https://github.com/serenity-bdd/serenity-core/commit/0769272807fe137) Refactored some tests
 * [5486ebd47a143e7](https://github.com/serenity-bdd/serenity-core/commit/5486ebd47a143e7) Fixed an issue rendering screenshots in JUnit
 * [3f04d4243626c40](https://github.com/serenity-bdd/serenity-core/commit/3f04d4243626c40) More consistant requirements reporting

The totals reported for the acceptance criteria on the Requirements page are now consistant with those reported for the test results.
 * [deafb5052528a28](https://github.com/serenity-bdd/serenity-core/commit/deafb5052528a28) Improved requirements reporting

Fixed several issues and improved performance for the package-based requirements reporting in JUnit.
 * [91ceccc601bb4b9](https://github.com/serenity-bdd/serenity-core/commit/91ceccc601bb4b9) refactor: made the TestOutcome streams more robust.
 * [63d9de98fa6a5ff](https://github.com/serenity-bdd/serenity-core/commit/63d9de98fa6a5ff) refactor: Removed redundant experimental code
 * [1e46b709e0a2d6a](https://github.com/serenity-bdd/serenity-core/commit/1e46b709e0a2d6a) fix: Fixed an issue with screenshots within nested groups

Allow screenshots to be correctly reported even if they are stored in a nested group.
 * [6bafc172d4f4a24](https://github.com/serenity-bdd/serenity-core/commit/6bafc172d4f4a24) Updated htmlunit
 * [8b8165dd17c792a](https://github.com/serenity-bdd/serenity-core/commit/8b8165dd17c792a) Updated Selenium to 2.53.1 to support Firefox 47
 * [974869733f48600](https://github.com/serenity-bdd/serenity-core/commit/974869733f48600) fix: rest-assured fixed params wrapping
 * [1ba78d372b2b6aa](https://github.com/serenity-bdd/serenity-core/commit/1ba78d372b2b6aa) updating changelog
 * [21de91398d78bef](https://github.com/serenity-bdd/serenity-core/commit/21de91398d78bef) Little proxy is no longer required
 * [d8779fb3fb884cd](https://github.com/serenity-bdd/serenity-core/commit/d8779fb3fb884cd) Update bmp to 2.1.1
 * [cbba968356d0f85](https://github.com/serenity-bdd/serenity-core/commit/cbba968356d0f85) refactoring: test refactoring
 * [a546e17b6411903](https://github.com/serenity-bdd/serenity-core/commit/a546e17b6411903) refactoring: test refactoring
 * [c0def5f861dfae2](https://github.com/serenity-bdd/serenity-core/commit/c0def5f861dfae2) refactoring: requirements analysis code
 * [535d687317c4b50](https://github.com/serenity-bdd/serenity-core/commit/535d687317c4b50) refactor: refactored the webdriver instance handling for easier maintenance.
 * [6947f1f4dc38787](https://github.com/serenity-bdd/serenity-core/commit/6947f1f4dc38787) refactor: refactored tests to use MD5 filenames
 * [8fa16c02f615ea6](https://github.com/serenity-bdd/serenity-core/commit/8fa16c02f615ea6) refactor: allow HTML reports to be generated uniquely from the JSON
 * [67b5fdfc5fe0ec5](https://github.com/serenity-bdd/serenity-core/commit/67b5fdfc5fe0ec5) refactor: minor optimisation of the screenshot processing.

Avoid trying to read a screenshot file if it is not in the working directory.
 * [ca2930bc5e8e92b](https://github.com/serenity-bdd/serenity-core/commit/ca2930bc5e8e92b) Removed serenity-cli from the core build config.
 * [312e3faf1eae055](https://github.com/serenity-bdd/serenity-core/commit/312e3faf1eae055) Switched file names to MD5 hashes.
 * [08ff8a67edfd7eb](https://github.com/serenity-bdd/serenity-core/commit/08ff8a67edfd7eb) refactor: Remove unnecessary legacy code.
 * [46ea87b28c5a9eb](https://github.com/serenity-bdd/serenity-core/commit/46ea87b28c5a9eb) refactor: minor refactoring
 * [c31b020742dd13c](https://github.com/serenity-bdd/serenity-core/commit/c31b020742dd13c) Get tags optionally displayed in the menu from config

Use the serenity.report.tag.menus property to define the tags you want to appear in the menu, and set serenity.report.show.tag.menus to true to get them to appear.
 * [2cf9f3ba77661f8](https://github.com/serenity-bdd/serenity-core/commit/2cf9f3ba77661f8) Reduced noisy logging
 * [fb6ad82a0f802f0](https://github.com/serenity-bdd/serenity-core/commit/fb6ad82a0f802f0) Moved the Serenity CIT to a separate repo
 * [a2614374a2ed1ea](https://github.com/serenity-bdd/serenity-core/commit/a2614374a2ed1ea) identical with upstream
 * [09175ff1b23b8cb](https://github.com/serenity-bdd/serenity-core/commit/09175ff1b23b8cb) delete not used files
 * [62dc5f693a35d35](https://github.com/serenity-bdd/serenity-core/commit/62dc5f693a35d35) feat: Improved handling of soft asserts

You can now use soft asserts to combine a task to be executed, and a check to perform, optionally annotated with a business rule. This makes it much easier to check several business or validation rules on the same screen, in the same test.

Sample code:
'''
        then(alice).should(

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("1234")).
                        because("BSB cannot be a number with less than 6 digits"),

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("qwerty")).
                        because("BSB cannot have alphabetical characters"),

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("~!@#$%^&*(")).
                        because("BSB cannot have symbols"),
        );
'''
 * [cd7ee0f319203de](https://github.com/serenity-bdd/serenity-core/commit/cd7ee0f319203de) updating changelog
 * [52126150566c665](https://github.com/serenity-bdd/serenity-core/commit/52126150566c665) Test refactoring
 * [e7a20c049727c47](https://github.com/serenity-bdd/serenity-core/commit/e7a20c049727c47) Added a test to ensure that PageObjects can be used outside the @Step library.
 * [f679ad049e35386](https://github.com/serenity-bdd/serenity-core/commit/f679ad049e35386) Unit tests related to @Manual tests and recording the active driver used for a test.
 * [02e1f2466334e04](https://github.com/serenity-bdd/serenity-core/commit/02e1f2466334e04) Added a short-hand way of expressing questions about Targets.
For example, you can now say this:

actor.should(seeThat(TheTarget.valueOf(NAME_FIELD), equalTo("Jim")))
 * [70c7680aba10b47](https://github.com/serenity-bdd/serenity-core/commit/70c7680aba10b47) fix: Fixed issues that resulted in declared drivers not being reported correctly.
 * [0bf8b493fca3477](https://github.com/serenity-bdd/serenity-core/commit/0bf8b493fca3477) A sanity check to ensure that you don't ask an actor to use a browser if you have not already given the actor the BrowseTheWeb ability.
 * [b491091b6ad93b6](https://github.com/serenity-bdd/serenity-core/commit/b491091b6ad93b6) fix: Fixed an issue where tests that failed when using the @Test expected attribute where not being reported correctly.
 * [c4b32d4feb8b97e](https://github.com/serenity-bdd/serenity-core/commit/c4b32d4feb8b97e) https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider - change packages
 * [5459b700350c958](https://github.com/serenity-bdd/serenity-core/commit/5459b700350c958) https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider
 * [12bcbc6d9f4ee8d](https://github.com/serenity-bdd/serenity-core/commit/12bcbc6d9f4ee8d) https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider
 * [7bf2cc3bd632336](https://github.com/serenity-bdd/serenity-core/commit/7bf2cc3bd632336) https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider
 * [cc81c5f3ee8e6de](https://github.com/serenity-bdd/serenity-core/commit/cc81c5f3ee8e6de) fix:Fixed a reporting issue with the PageTitleQuestion class
 * [b50a1f85e7fcdb1](https://github.com/serenity-bdd/serenity-core/commit/b50a1f85e7fcdb1) test: fixed test to work with new isAlive implementation
 * [b902c2e78fdd985](https://github.com/serenity-bdd/serenity-core/commit/b902c2e78fdd985) fix: updated isAlive implementation to work with appium
 * [fab8b79b8a850f3](https://github.com/serenity-bdd/serenity-core/commit/fab8b79b8a850f3) fix: fixed alive function to work with appium
 * [cbe6f66b5e5d49a](https://github.com/serenity-bdd/serenity-core/commit/cbe6f66b5e5d49a) https://github.com/serenity-bdd/serenity-core/issues/54 - do not skip step if is called from @After or @AfterClass annotated method
 * [3d806af554e2644](https://github.com/serenity-bdd/serenity-core/commit/3d806af554e2644) https://github.com/serenity-bdd/serenity-core/issues/54 - do not skip step if is called from @After or @AfterClass annotated method
 * [c4aa5fc27f56532](https://github.com/serenity-bdd/serenity-core/commit/c4aa5fc27f56532) Fixed an issue where tasks with primitive parameters in the constructor could not be instantiated.
 * [c7bef26bb18bac9](https://github.com/serenity-bdd/serenity-core/commit/c7bef26bb18bac9) switch nack to Chrome driver
 * [343a1ba4dfe1d0a](https://github.com/serenity-bdd/serenity-core/commit/343a1ba4dfe1d0a) https://github.com/serenity-bdd/serenity-core/issues/406 . UseTagProviderStrategy
 * [ebff2013a00cb63](https://github.com/serenity-bdd/serenity-core/commit/ebff2013a00cb63) save testSource in TestOutcome and store it in JSON and XML format
 * [bea7f473a9797c4](https://github.com/serenity-bdd/serenity-core/commit/bea7f473a9797c4) feat: updated rest report template
 * [39c6df7f695c815](https://github.com/serenity-bdd/serenity-core/commit/39c6df7f695c815) Revert "Avoid duplicated test outcome in reports"
 * [5aaa3e935b7db4c](https://github.com/serenity-bdd/serenity-core/commit/5aaa3e935b7db4c) Revert "set property "use.test.case.for.story.tag = false" by default"
 * [7be65dd8073a657](https://github.com/serenity-bdd/serenity-core/commit/7be65dd8073a657) set property use.test.case.for.story.tag = false by default (https://github.com/serenity-bdd/serenity-jbehave/issues/17)
 * [91f659860f052d1](https://github.com/serenity-bdd/serenity-core/commit/91f659860f052d1) set property use.test.case.for.story.tag = false by default (https://github.com/serenity-bdd/serenity-jbehave/issues/17)
 * [ebf4ffb3dcf02cf](https://github.com/serenity-bdd/serenity-core/commit/ebf4ffb3dcf02cf) deactivate by default FeatureStoryTagProvider
 * [e40bb99d2440250](https://github.com/serenity-bdd/serenity-core/commit/e40bb99d2440250) Updated the SLF4J API
 * [83793d47044da0e](https://github.com/serenity-bdd/serenity-core/commit/83793d47044da0e) avoid duplicated test outcome in reports : see  https://github.com/serenity-bdd/serenity-cucumber/issues/19
 * [d89bf6bec68cd97](https://github.com/serenity-bdd/serenity-core/commit/d89bf6bec68cd97) avoid duplicated test outcome in reports : see  https://github.com/serenity-bdd/serenity-cucumber/issues/19
 * [3bbb0c0580638a9](https://github.com/serenity-bdd/serenity-core/commit/3bbb0c0580638a9) fix: updated implementation to support multiple constructors in steps libraries
 * [3df916a63853763](https://github.com/serenity-bdd/serenity-core/commit/3df916a63853763) test: added test to check how serenity works with multiple constructors in step libraries
 * [03aac5170754ef4](https://github.com/serenity-bdd/serenity-core/commit/03aac5170754ef4) switch back to previous version
 * [ec7dd10ec59bb40](https://github.com/serenity-bdd/serenity-core/commit/ec7dd10ec59bb40) https://github.com/serenity-bdd/serenity-core/issues/374 : in SerenityRunner use always one instance of the StepFactory
 * [76ef11b52b48895](https://github.com/serenity-bdd/serenity-core/commit/76ef11b52b48895) Updated gradle wrapper version
 * [646a0c9276dbd20](https://github.com/serenity-bdd/serenity-core/commit/646a0c9276dbd20) Tidied up some dependencies
 * [488d557e33e3aa9](https://github.com/serenity-bdd/serenity-core/commit/488d557e33e3aa9) Trimmed down requirements logs
 * [1209ed289ac75da](https://github.com/serenity-bdd/serenity-core/commit/1209ed289ac75da) updating changelog
 * [4d2029c6b2b6313](https://github.com/serenity-bdd/serenity-core/commit/4d2029c6b2b6313) test: updated tests for rest-assurance to use wiremock server instead of petstore
 * [0ac588d9b29bfed](https://github.com/serenity-bdd/serenity-core/commit/0ac588d9b29bfed) updating changelog
 * [b585afa23592ce3](https://github.com/serenity-bdd/serenity-core/commit/b585afa23592ce3) Updated the smoketests
## v1.1.37-rc.6
### [#450](https://github.com/serenity-bdd/serenity-core/issues/450) WebDriverFacade.isEnabled() not always consulted
 * [eb604025c85456b](https://github.com/serenity-bdd/serenity-core/commit/eb604025c85456b) Fixed #450 - disabled WebDriver sometimes causes classcast exceptions.
### No issue
 * [7d4b04ba8f0fb46](https://github.com/serenity-bdd/serenity-core/commit/7d4b04ba8f0fb46) refactoring: hardening build
 * [0660cba01de1bfa](https://github.com/serenity-bdd/serenity-core/commit/0660cba01de1bfa) refactoring: Hardening the build process
 * [4daf2f80b8b6b7c](https://github.com/serenity-bdd/serenity-core/commit/4daf2f80b8b6b7c) refactor: fixing Gradle build
 * [ad61bf328487f7c](https://github.com/serenity-bdd/serenity-core/commit/ad61bf328487f7c) refactoring: Fine-tuning bintray deployment
 * [11532ae11710c56](https://github.com/serenity-bdd/serenity-core/commit/11532ae11710c56) refactor: tweaked the parallel tests in the Gradle build.
 * [12b9bf2d21a0353](https://github.com/serenity-bdd/serenity-core/commit/12b9bf2d21a0353) refactor: ensure the use of threadlocal StepEventBus instances.
 * [6fbf736acf1a399](https://github.com/serenity-bdd/serenity-core/commit/6fbf736acf1a399) Added a CLI module to run the Serenity reports aggregation from an executable JAR
 * [b5afdcce9514a9e](https://github.com/serenity-bdd/serenity-core/commit/b5afdcce9514a9e) performance: Improved speed and memory management for report aggregation.
 * [5aff5c3baf04199](https://github.com/serenity-bdd/serenity-core/commit/5aff5c3baf04199) updating changelog
 * [b5cd76d68401dad](https://github.com/serenity-bdd/serenity-core/commit/b5cd76d68401dad) feat: add ability to see response body for responses with  HTML content type in report
Rest-Assured Integration: now it will be available to verify Response Body in report for REST Queries for responses with HTML content type
 * [86c6aefcf78804a](https://github.com/serenity-bdd/serenity-core/commit/86c6aefcf78804a) Added a test illustrating the whenAttemptingTo() construct.
## v1.1.37-rc.5
### No issue
 * [1010a8dac5ac18d](https://github.com/serenity-bdd/serenity-core/commit/1010a8dac5ac18d) feat: add ability to assert that matched element is not present in collection
now it is possible to assert that element is not present in collection using BeanMatcherAsserts.shouldNotMatch(List<T> items, BeanMatcher... matchers) method
## v1.1.37-rc.4
### [#440](https://github.com/serenity-bdd/serenity-core/issues/440) Empty reports when using @RunWith(SerenityParameterizedRunner.class)
 * [6b0c90586fafb66](https://github.com/serenity-bdd/serenity-core/commit/6b0c90586fafb66) Fixed #440 - issue with JSON serialisation.

No reports were being generated due to an issue with the GSON deserialisation.
### [#445](https://github.com/serenity-bdd/serenity-core/issues/445) suggestion: cut message for failed testcases during final crossreference generation
 * [0a8d1cc51959bb4](https://github.com/serenity-bdd/serenity-core/commit/0a8d1cc51959bb4) feature: truncate long titles in HTML tip texts to save space (#445)
### No issue
 * [3e547d4c53868b0](https://github.com/serenity-bdd/serenity-core/commit/3e547d4c53868b0) refactor: Improved screenshot test performance.
## v1.1.37-rc.3
### [#428](https://github.com/serenity-bdd/serenity-core/issues/428) Serenity:check hangs at attempt to fail the build
 * [b979db723954863](https://github.com/serenity-bdd/serenity-core/commit/b979db723954863) Fixed #428
### [#442](https://github.com/serenity-bdd/serenity-core/issues/442) With Serenity 1.1.36 more tests are flaky
 * [84204cf501e9492](https://github.com/serenity-bdd/serenity-core/commit/84204cf501e9492) Refined the collection loading strategy (#442)

You can now use the "serenity.webdriver.collection_loading_strategy" property to define how Serenity loads collections of web elements. There are three options:
    - Optimistic
    - Pessimistic (default)
    - Paranoid

Optimistic will only wait until the field is defined. This is the native Selenium behaviour.

Pessimistic will wait until at least the first element is displayed. This is currently the default.

Paranoid will wait until all of the elements are displayed. This can be slow for long lists.
### [#443](https://github.com/serenity-bdd/serenity-core/issues/443) Pie-Chart broken with gradle
 * [58eb4c4fb2304ef](https://github.com/serenity-bdd/serenity-core/commit/58eb4c4fb2304ef) Fixed #443 (localisation issue when generating the reports from Gradle in some non-English locales)
### No issue
 * [87545d1a243898e](https://github.com/serenity-bdd/serenity-core/commit/87545d1a243898e) refactor:Test refactoring
 * [efe8f04c254f61b](https://github.com/serenity-bdd/serenity-core/commit/efe8f04c254f61b) refactoring:improved reporting when printing web elements to the console
 * [89feb38a53b9a24](https://github.com/serenity-bdd/serenity-core/commit/89feb38a53b9a24) Added the senity.webdriver.collection.loading.strategy.

hould we assume that collections of webdriver elements are already on the page, or if we should wait for them to be available.
     * This property takes two values: Optimistic or Pessimistic. Optimistic means that the elements are assumed to be on the screen, and will be
     * loaded as-is immediately. This is the normal WebDriver behavior.
     * For applications with lots of ASynchronous activity, it is often better to wait until the elements are visible before using them. The Pessimistic
     * mode waits for at least one element to be visible before proceeding.
     * For legacy reasons, the default strategy is Pessimistic.
 * [4257dd8568e4d9c](https://github.com/serenity-bdd/serenity-core/commit/4257dd8568e4d9c) refactoring: test hardening
## v1.1.37-rc.2
### [#419](https://github.com/serenity-bdd/serenity-core/issues/419) @AndroidFindBy annotation doesn&#39;t work on 1.1.34
 * [9d20f48c24e90b0](https://github.com/serenity-bdd/serenity-core/commit/9d20f48c24e90b0) Refactored fix for #419
 * [54d4f697bfff034](https://github.com/serenity-bdd/serenity-core/commit/54d4f697bfff034) Refactored fix for #419
 * [6e943ce46f02b57](https://github.com/serenity-bdd/serenity-core/commit/6e943ce46f02b57) Fixed #419
### [#424](https://github.com/serenity-bdd/serenity-core/issues/424) Unable to get size from searched elements from FindAll and FindBys annotation when list is long.
 * [6d8d8c212d61433](https://github.com/serenity-bdd/serenity-core/commit/6d8d8c212d61433) Fixed #424
### No issue
 * [a36b539ea57112c](https://github.com/serenity-bdd/serenity-core/commit/a36b539ea57112c) refactor: hardening time-based tests and minor refactoring.
 * [a5755b767d175e4](https://github.com/serenity-bdd/serenity-core/commit/a5755b767d175e4) refactor: test refactoring
 * [c23e24f4a05ee0e](https://github.com/serenity-bdd/serenity-core/commit/c23e24f4a05ee0e) refact: Making the tests more cross-platform
 * [f7ea309a33df0b7](https://github.com/serenity-bdd/serenity-core/commit/f7ea309a33df0b7) Added some memory for the tests
 * [78b58de2ed94948](https://github.com/serenity-bdd/serenity-core/commit/78b58de2ed94948) refact: Making the tests more cross-platform
 * [8e5571cd96af1bc](https://github.com/serenity-bdd/serenity-core/commit/8e5571cd96af1bc) feat:Added extra diagnostics for the Check gradle and maven tasks
 * [4ea4e50eeca1a5e](https://github.com/serenity-bdd/serenity-core/commit/4ea4e50eeca1a5e) WIP
 * [a97a7d6de41abef](https://github.com/serenity-bdd/serenity-core/commit/a97a7d6de41abef) Added a UIAction to perform a sendKeys() without clearing the field first.
## v1.1.36-rc.1
### No issue
 * [0ea09c7f24809c7](https://github.com/serenity-bdd/serenity-core/commit/0ea09c7f24809c7) fix: Fixed an issue with the screenplay webdriver integration

Browser windows were not closing correctly when there were more than one driver used in a single test.
 * [022217733bafafb](https://github.com/serenity-bdd/serenity-core/commit/022217733bafafb) refactor: Renamed the 'browse-the-web' module

Renamed the 'browse-the-web' module to 'serenity-screenplay-webdriver' for more consistancy.
 * [28652f505e0cc72](https://github.com/serenity-bdd/serenity-core/commit/28652f505e0cc72) Revert "fix: Fixed an issue where the screenplay module was not closing the last browser if several browsers were being used in a test"

This reverts commit e9f81313bd86b1222667e137fe00a133226cad98.
 * [e9f81313bd86b12](https://github.com/serenity-bdd/serenity-core/commit/e9f81313bd86b12) fix: Fixed an issue where the screenplay module was not closing the last browser if several browsers were being used in a test
 * [6d735591d4eb95a](https://github.com/serenity-bdd/serenity-core/commit/6d735591d4eb95a) Test hardening
 * [92f15b9196ac3ad](https://github.com/serenity-bdd/serenity-core/commit/92f15b9196ac3ad) Test simplification
 * [75abe17d88d18a2](https://github.com/serenity-bdd/serenity-core/commit/75abe17d88d18a2) Test refactoring - removed unnecessary code
 * [91d77a09d12b7c5](https://github.com/serenity-bdd/serenity-core/commit/91d77a09d12b7c5) Improved consistancy in driver use.
 * [bf4a6ecb42b5766](https://github.com/serenity-bdd/serenity-core/commit/bf4a6ecb42b5766) refactoring: Removed redundant mock
 * [9cc788958b17f59](https://github.com/serenity-bdd/serenity-core/commit/9cc788958b17f59) Test refactoring
 * [feeaab0d355b6d8](https://github.com/serenity-bdd/serenity-core/commit/feeaab0d355b6d8) refactoring: made the screenshot processing more robust
 * [2d77834537e12a8](https://github.com/serenity-bdd/serenity-core/commit/2d77834537e12a8) Fixed a refactoring error.
 * [de858923fdfa585](https://github.com/serenity-bdd/serenity-core/commit/de858923fdfa585) chore: General refactoring and fixing minor performance issues.
 * [e799eeb87a9403b](https://github.com/serenity-bdd/serenity-core/commit/e799eeb87a9403b) updating changelog
 * [88e11b0ac64e206](https://github.com/serenity-bdd/serenity-core/commit/88e11b0ac64e206) Test hardening
 * [3c22e3da4a0fec2](https://github.com/serenity-bdd/serenity-core/commit/3c22e3da4a0fec2) Temporarily suspended parallel unit tests
 * [20b382233b94a6d](https://github.com/serenity-bdd/serenity-core/commit/20b382233b94a6d) Fixed the browser tests
 * [654de43084162a0](https://github.com/serenity-bdd/serenity-core/commit/654de43084162a0) Refactoring
 * [97a59eace3bf89a](https://github.com/serenity-bdd/serenity-core/commit/97a59eace3bf89a) Refactoring
 * [76b30eb2f9ea4fe](https://github.com/serenity-bdd/serenity-core/commit/76b30eb2f9ea4fe) Refactoring
 * [847e6bae3800b10](https://github.com/serenity-bdd/serenity-core/commit/847e6bae3800b10) Refactoring
 * [99767081bc0f1c8](https://github.com/serenity-bdd/serenity-core/commit/99767081bc0f1c8) Refactoring
 * [68dcf4098949f1d](https://github.com/serenity-bdd/serenity-core/commit/68dcf4098949f1d) Refactoring
 * [25c7a626768b8c5](https://github.com/serenity-bdd/serenity-core/commit/25c7a626768b8c5) Refactoring and simplification of the driver management.
 * [4a7a8edc47139e4](https://github.com/serenity-bdd/serenity-core/commit/4a7a8edc47139e4) Improved screenshot capture logic

The screenshots are taken using the current active driver. Sometimes this gets out of sync, and the wrong driver is used for screenshots (resulting in no screenshots being taken). Now, when a driver is called it becomes the current active driver.
## v1.1.35-rc.1
### [#426](https://github.com/serenity-bdd/serenity-core/issues/426) Syntax error in report template: Java method,FTL stack trace (&quot;~&quot; means nesting-related):
 * [d0cf954f7076df1](https://github.com/serenity-bdd/serenity-core/commit/d0cf954f7076df1) Work-around for #426

This allows the reports to be generated even with unknown result types, allowing for easier trouble-shooting.
### No issue
 * [405d22de1bfcc46](https://github.com/serenity-bdd/serenity-core/commit/405d22de1bfcc46) Continued refactoring.
 * [18d46305fb6d3bd](https://github.com/serenity-bdd/serenity-core/commit/18d46305fb6d3bd) General refactoring
 * [fe342f63df08a67](https://github.com/serenity-bdd/serenity-core/commit/fe342f63df08a67) Fixed an issue where Cucumber screenshots where not being recorded correctly
 * [8a7e77e4b06b4d4](https://github.com/serenity-bdd/serenity-core/commit/8a7e77e4b06b4d4) chore: simple refactoring
## v1.1.34-rc.1
### [#235](https://github.com/serenity-bdd/serenity-core/issues/235) Serenity with Appium Could not instantiate class io.appium.java_client.AppiumDriver
 * [238c8819789b567](https://github.com/serenity-bdd/serenity-core/commit/238c8819789b567) Skip resizing on appium, see #235
### No issue
 * [d2bb5318a5caa0b](https://github.com/serenity-bdd/serenity-core/commit/d2bb5318a5caa0b) Make sure soft asserts don't make webdriver calls unnecessarily.
 * [fe81e6857c61e54](https://github.com/serenity-bdd/serenity-core/commit/fe81e6857c61e54) Ensure that a browser doesn't open for suspended tests

Suspended tests include pending, skipped and manual tests.
 * [885fb0ac2660857](https://github.com/serenity-bdd/serenity-core/commit/885fb0ac2660857) Refactored an Appium test to work on different environments.
 * [9deb75b0d7a5973](https://github.com/serenity-bdd/serenity-core/commit/9deb75b0d7a5973) Adding basic appium android tests
## v1.1.33-rc.1
### No issue
 * [b735e4c555c3aac](https://github.com/serenity-bdd/serenity-core/commit/b735e4c555c3aac) Fixed an issue where error messages containing < or > where not correctly reported
## v1.1.32-rc.3
### [#247](https://github.com/serenity-bdd/serenity-core/issues/247) multiple csv file used as test data  issue
 * [b6b55edd2b94037](https://github.com/serenity-bdd/serenity-core/commit/b6b55edd2b94037) fix: multiple csv file used as test data issue (#247)
### [#378](https://github.com/serenity-bdd/serenity-core/issues/378) Serenity launches 2 instances of Chrome if the driver parameter is not set in the @Managed annotation
 * [69b5babbddefd21](https://github.com/serenity-bdd/serenity-core/commit/69b5babbddefd21) Fix: #378 - Serenity launches 2 instances of Chrome if the driver parameter is not set in the @Managed annotation
### [#39](https://github.com/serenity-bdd/serenity-core/issues/39) Failed to get requirements from a jar file
 * [2262c007b81f3d0](https://github.com/serenity-bdd/serenity-core/commit/2262c007b81f3d0) fix: Incorrect alignment of index.html file when story title is too long (#39)
### No issue
 * [7021b294e9956ee](https://github.com/serenity-bdd/serenity-core/commit/7021b294e9956ee) Refactored an integration test
 * [f7200aefa8a4354](https://github.com/serenity-bdd/serenity-core/commit/f7200aefa8a4354) Removed redundent test
 * [c44a79a0acdf9b3](https://github.com/serenity-bdd/serenity-core/commit/c44a79a0acdf9b3) Updated unit tests
 * [60c2fc8116ccfec](https://github.com/serenity-bdd/serenity-core/commit/60c2fc8116ccfec) Fixed an issue with broken links in the requirements reports.
 * [4cbed6fc46e87f9](https://github.com/serenity-bdd/serenity-core/commit/4cbed6fc46e87f9) Removed redundant test
## v1.1.32-rc.2
### Jira
 * [4bc580628611e9f](https://github.com/serenity-bdd/serenity-core/commit/4bc580628611e9f) Updated appium java client to version 3.4.1

https://discuss.appium.io/t/java-client-version-3-4-0-released/8961
https://discuss.appium.io/t/java-client-version-3-4-1-released/9416
### No issue
 * [796e43b9c92f887](https://github.com/serenity-bdd/serenity-core/commit/796e43b9c92f887) Test refactoring
 * [51abeab24d9b101](https://github.com/serenity-bdd/serenity-core/commit/51abeab24d9b101) Test refactoring
 * [ba1fa1caddbac7e](https://github.com/serenity-bdd/serenity-core/commit/ba1fa1caddbac7e) Test refactoring
 * [6d579dad70d7416](https://github.com/serenity-bdd/serenity-core/commit/6d579dad70d7416) Removed redundant tesxt
 * [0382a40bea4aaff](https://github.com/serenity-bdd/serenity-core/commit/0382a40bea4aaff) Removed diagnostic log messages
 * [63b9bb77c7a0ef9](https://github.com/serenity-bdd/serenity-core/commit/63b9bb77c7a0ef9) Removed diagnostic log messages
 * [719c0759a630b18](https://github.com/serenity-bdd/serenity-core/commit/719c0759a630b18) Added logging to troubleshoot odd issue on SnapCI
 * [6f13b8ce61cf5ac](https://github.com/serenity-bdd/serenity-core/commit/6f13b8ce61cf5ac) Added logging to troubleshoot odd issue on SnapCI
 * [891ee2368152e2e](https://github.com/serenity-bdd/serenity-core/commit/891ee2368152e2e) Disable parallel unit and integration tests (experimental)
 * [dfb0cd3f08ac781](https://github.com/serenity-bdd/serenity-core/commit/dfb0cd3f08ac781) Fixed issue where a RemoteDriver was being used instead of an AppiumDriver
 * [6a0805c88cd1bfb](https://github.com/serenity-bdd/serenity-core/commit/6a0805c88cd1bfb) Removed redundant test
 * [cb7891d21923d4c](https://github.com/serenity-bdd/serenity-core/commit/cb7891d21923d4c) Removed the junit retry logic (use the native JUnit feature instead)
## v1.1.32-rc.1
### No issue
 * [af83a0d0b143789](https://github.com/serenity-bdd/serenity-core/commit/af83a0d0b143789) Refactoring the Screenplay code - WIP
 * [6b978228442a505](https://github.com/serenity-bdd/serenity-core/commit/6b978228442a505) updating changelog
## v1.1.31-rc.1
### Jira
 * [8040ade93c477c2](https://github.com/serenity-bdd/serenity-core/commit/8040ade93c477c2) feat: serenity-rest implemented recording of all basic fields, like status-code, body, path, method. Implemented recoding of exceptions.
Now all operations will be recorded, after request executed, in report all fields will appear in same format as send.
After this update content/body should be matched with additional transformation, because if string contains json/xml it will be reformatted by RestAssured,
for example for Json it can be:
```
...
import static net.serenitybdd.rest.staging.JsonConverter.*;
...
assert formatted(query.responseBody) == formatted(body)
```
Introduced DecomposedContentType class for representation simple content types:
if in rest assured defined only:
```
ANY("*/*"),
TEXT("text/plain"),
JSON("application/json", "application/javascript", "text/javascript"),
XML("application/xml", "text/xml", "application/xhtml+xml"),
HTML("text/html"),
URLENC("application/x-www-form-urlencoded"),
BINARY("application/octet-stream");
 ```
 not it will be possible define something like that `given().contentType("$APPLICATION_JSON")` for:
 ```
ANY("*/*"),
TEXT("text/plain"),
APPLICATION_JSON("application/json"),
APPLICATION_JAVASCRIPT("application/javascript"),
APPLICATION_XML("application/xml"),
TEST_XML("text/xml"),
APPLICATION_XHTML_XML("application/xhtml+xml"),
TEST_JAVASCRIPT("text/javascript"),
HTML("text/html"),
URLENC("application/x-www-form-urlencoded"),
BINARY("application/octet-stream");
 ```

as well as find some RestAssured content type by DecomposedContentType or String:
```
DecomposedContentType.byString("application/javascript; Charset: UTF-8").contentType()

```
### No issue
 * [1e5870431bad8fd](https://github.com/serenity-bdd/serenity-core/commit/1e5870431bad8fd) fixed issue with lambdaJ and CGLib when using Hamcrest assertions on collections
 * [a608efe35f711f3](https://github.com/serenity-bdd/serenity-core/commit/a608efe35f711f3) feat: serenity-rest-assured updated rest core and tests, renamed some classes
 * [ae9e4a87b1b76ad](https://github.com/serenity-bdd/serenity-core/commit/ae9e4a87b1b76ad) feat: serenity-core updated restQuery and reports to include Cookies and headers, changed wrapping of request and response
 * [2d4719b5c0ab7cf](https://github.com/serenity-bdd/serenity-core/commit/2d4719b5c0ab7cf) feat: serenity-rest supported sequences of operations in different steps:
 Now it is possible run sequence of rest operations, with restspecification and response shared in one thread:
```
class RestSteps {
        @Step
        def successfulGet(final String url) {
            given().get("$url/{id}", 1000).then().body("id", Matchers.equalTo(1000));
        }

        @Step
        def getById(final String url) {
            rest().get("$url/{id}", 1000);
        }

        @Step
        def thenCheckOutcome() {
            then().body("Id", Matchers.anything())
        }
    }
```
 * [de2dc5ee86b2f85](https://github.com/serenity-bdd/serenity-core/commit/de2dc5ee86b2f85) feat: implemented support of DryRun for Serenity Rest
 Now it dryRun enabled all rests tests will be successful, all checks for rest will be successful,
 no requests to external systems will be send. Some default values will be returned for request
 body, header, cookies and son on, and all values for request will be recorded and included in report.

 It is possible enable dryRun only for one test/class, but it can produce some performance issues because of
 analyzing of stacktrace for all invocations during check if DryRun enabled. Should be used in part of some rule
 to enable it before test and disable after test. On other cases serenity.dry.run should be used:
 ```
 RestExecutionHelper.enableDryRunForClass(WhenEnabledDryRunWithSerenityRest.class)
 ```
 * [b99deb4009cb2a1](https://github.com/serenity-bdd/serenity-core/commit/b99deb4009cb2a1) test: serenity-rest created test to check how dryRun works for serenity rest
 * [36dae0ca34309fb](https://github.com/serenity-bdd/serenity-core/commit/36dae0ca34309fb) added DecomposedContentType
 * [d869ad2252483e7](https://github.com/serenity-bdd/serenity-core/commit/d869ad2252483e7) test: serenity-rest implemented tests to check how recording of rest requests and reponses works
 * [a4b5c70e06b7d98](https://github.com/serenity-bdd/serenity-core/commit/a4b5c70e06b7d98) fixed configuration tests, added base step listener
 * [e16bec4f669be5b](https://github.com/serenity-bdd/serenity-core/commit/e16bec4f669be5b) added test listener to tests, fixed patch operation
 * [f5eb6455a017508](https://github.com/serenity-bdd/serenity-core/commit/f5eb6455a017508) test: serenity-rest implemented test to check if failed query recorded and it is possible to use assertions
 * [606d906e39cea0e](https://github.com/serenity-bdd/serenity-core/commit/606d906e39cea0e) fixed style for RestReportingHelper
 * [2787384de5c0e4d](https://github.com/serenity-bdd/serenity-core/commit/2787384de5c0e4d) feat: serenity-rest implemented recording of boyd, content-type, path, method, prepared recoding structure
Now it will be is easy to include in restQuery recording info about cookies, headers, and so on using filtering RestAssured mechanism
 * [ca6690e39b56727](https://github.com/serenity-bdd/serenity-core/commit/ca6690e39b56727) test: serenity-rest implemented test to check if body, contenttype, responce is recorded
 * [f7c61efd7bf46f5](https://github.com/serenity-bdd/serenity-core/commit/f7c61efd7bf46f5) style updated
 * [ebe88bdb34972ca](https://github.com/serenity-bdd/serenity-core/commit/ebe88bdb34972ca) feat: serenity rest core decomposed to make possible use shaned method invocations for configuring default parameters.
Now it is possible to configure default parameters in two ways (default and changed):
```
new RestDefaultsChained()
.setDefaultBasePath("some/path")
.setDefaultProxy(object)
.setDefaultPort(10)
// or
SerinityRest.setDefaultBasePath("some/path");
SerinityRest.setDefaultProxy(object);
SerinityRest.setDefaultPort(10);
// or is static imports are used
setDefaultBasePath("some/path");
setDefaultProxy(object);
setDefaultPort(10);

```
 * [8d3e5c93e264970](https://github.com/serenity-bdd/serenity-core/commit/8d3e5c93e264970) feat: serenity rest core decomposed to make possible to use different classes for different purposes.
Now it is possible to execute almost all serenity rest operations using SerenityRest (90+ methods) or use smaller classes:
RestDefaults (50+ methods)can be used for configuratins only default parameters
RestRequests (20+ methods)can be used for making requests
RestUtility  (20+ methods)can be used for initialising some default or reusable objects
 * [117dbf23d51a4e6](https://github.com/serenity-bdd/serenity-core/commit/117dbf23d51a4e6) feat: serenity rest core updated to return updated configurations, filters, etc.
 * [5bdeda5fa991e70](https://github.com/serenity-bdd/serenity-core/commit/5bdeda5fa991e70) feat: serenity-rest implemented wrapping for all requests in SerenityRest class, all covered by tests
 * [f3ab690a4ab25af](https://github.com/serenity-bdd/serenity-core/commit/f3ab690a4ab25af) adding reflection helper
 * [5b6c531b8551b46](https://github.com/serenity-bdd/serenity-core/commit/5b6c531b8551b46) feat: implemented wrapping of request after configuring cookies
Now serenity rest will work correctly after operations like below:
```
given().cookies(mapWithCookies).get(url)
given().cookies("value").get(url)
given().cookies("value", param).get(url)
```
 * [7c0f58aedac4ea2](https://github.com/serenity-bdd/serenity-core/commit/7c0f58aedac4ea2) test: added test to ceheck wrapping after cookie operations
 * [52e54a9c668627a](https://github.com/serenity-bdd/serenity-core/commit/52e54a9c668627a) feat: implemented wrapping of request specification after multypart request configurations
 * [7d5f5c881d59a8d](https://github.com/serenity-bdd/serenity-core/commit/7d5f5c881d59a8d) test: implemented test for wrapping request with configured multipart request
 * [432e5f6a141cfe0](https://github.com/serenity-bdd/serenity-core/commit/432e5f6a141cfe0) feat: implemented wrapping during executing on request instance operations like when, with, given, and
 * [7f439d278111f39](https://github.com/serenity-bdd/serenity-core/commit/7f439d278111f39) test: serenity-rest added tests for using chains of given, and, when, with based on request instance
 * [5cc45f33718c705](https://github.com/serenity-bdd/serenity-core/commit/5cc45f33718c705) feat: in serenity-rest implemented setting base path, base uri, session id, port, urlEncodingEnabled
To set base paramters next code can be used:
```
given().basePath(path).get(url)
given().baseUri(base).port(port).get(path)
given().baseUri(base).port(port).get(path)
given().baseUri(base).basePath("/test").get("log/levels")
```
 * [08c48231157fcdc](https://github.com/serenity-bdd/serenity-core/commit/08c48231157fcdc) test: serenity-rest tested to configure base path and pase uri and other base configurations
 * [81c6d3092a1522b](https://github.com/serenity-bdd/serenity-core/commit/81c6d3092a1522b) feat: impelemented processing and wrapping of result for operations with pathParameter, queryParams, param, params, parameters, formParam
 * [231052beeacac17](https://github.com/serenity-bdd/serenity-core/commit/231052beeacac17) feat: implemented wrapping after operations with body, content, headers, contentTypes
Now serenity-rest will work correctly with opertions like:
```
given().contentType(ContentType.XML).get(url)
given().contentType(ContentType.XML).get(url)
given().headers(map).get(url)
given().header(CONTENT_TYPE.asString(), ContentType.JSON).get(url)
given().contentType("application/json").content(body).post(url)
```
 * [63d9538b0322057](https://github.com/serenity-bdd/serenity-core/commit/63d9538b0322057) test: added tests to check wrapping response and request and correctness of setting body, content, headers
 * [4cb944933b81583](https://github.com/serenity-bdd/serenity-core/commit/4cb944933b81583) feat: implemented wrapping of request/response after HTTPS and Auth configurations
Now, rest assured will use wrapped request and response after configurations like below:
```
given().relaxedHTTPSValidation().get(url)
when().authentication().basic("login","password").get(url)
given().authentication().none()
given().auth().oauth2(token)
given().authentication().basic("user", "password")
```
 * [443ca49c83bc412](https://github.com/serenity-bdd/serenity-core/commit/443ca49c83bc412) test: serenity-rest, added tests for checking https requests and auth params configurations
 * [146b1d8129b31e8](https://github.com/serenity-bdd/serenity-core/commit/146b1d8129b31e8) feat: serenity-rest implemented wrapping of request after redirects configuration
 * [794f5af4fa1a9e8](https://github.com/serenity-bdd/serenity-core/commit/794f5af4fa1a9e8) test: serenity-rest, added tests for checking wrapping after redirects configuration
 * [af19533f10fc384](https://github.com/serenity-bdd/serenity-core/commit/af19533f10fc384) feat: implemented processing log operation with rest assurance, and created tests for it
Now it is possible log some information during request/response and wrapped request/response will be returned after that:
```
request.log().all().response()
request.log().body().request().get(url)
request.log().body().get(url)
```
 * [7a014ec919eb224](https://github.com/serenity-bdd/serenity-core/commit/7a014ec919eb224) feat: implemented HEAD, PATCH, DELETE, OPTIONS, POST operations and wrapping of results
 * [30105b4face22f9](https://github.com/serenity-bdd/serenity-core/commit/30105b4face22f9) test: created tests to check if HEAD operatin works correctly and return empty body
 * [ca059a5b9a51c87](https://github.com/serenity-bdd/serenity-core/commit/ca059a5b9a51c87) test: added tests to check wrapping repsponse after PUT, POST, PATCH, DELETE, OPTIONS, HEAD requests
 * [5b6b67fa068e7a7](https://github.com/serenity-bdd/serenity-core/commit/5b6b67fa068e7a7) feat: wrapped put request, updated wrapping of get request - to use only one function-endpoint
 * [f1e2cde371629a9](https://github.com/serenity-bdd/serenity-core/commit/f1e2cde371629a9) test: added tests for PUT operations
 * [6ed97f3f78d8aa7](https://github.com/serenity-bdd/serenity-core/commit/6ed97f3f78d8aa7) style: changed request organisation, added tests for get operation
 * [508e664096bc50d](https://github.com/serenity-bdd/serenity-core/commit/508e664096bc50d) simple refactoring of request and response
 * [cd93fbb84276728](https://github.com/serenity-bdd/serenity-core/commit/cd93fbb84276728) test: updated tests to check how SerenityRest works with expectation and validations like then, expect, statusCode etc
 * [5b4590c78e6e245](https://github.com/serenity-bdd/serenity-core/commit/5b4590c78e6e245) feat: wrapped response from GET to make possible including in report validations
Now during execution then, expect, and validations like then.statusCode(200) used wrapped response.
For example supported lines:
```
given()
.param("x", "y")
.expect()
.statusCode(200)
.body(Matchers.equalTo(body))
.when()
.get(url)

given().get(url).then().statusCode(200)
```
 * [7996d30f9977393](https://github.com/serenity-bdd/serenity-core/commit/7996d30f9977393) feat: updated serentyRest and tests. Checking Response and request wrapping during creation
 * [90ffb4e6ca2e892](https://github.com/serenity-bdd/serenity-core/commit/90ffb4e6ca2e892) feat: implemented wrapping during initialisation of request and response using methods given,when,expect
 * [8fb8303860637e6](https://github.com/serenity-bdd/serenity-core/commit/8fb8303860637e6) test: added tests to check wrapping during initialisation of requests and responses
 * [43151004ed37461](https://github.com/serenity-bdd/serenity-core/commit/43151004ed37461) style: updated test, removed blank lines and unused imports
 * [d0f5100cdb35b8f](https://github.com/serenity-bdd/serenity-core/commit/d0f5100cdb35b8f) updated endlines to fit project code style
 * [a291ba1e4402dbe](https://github.com/serenity-bdd/serenity-core/commit/a291ba1e4402dbe) feat: implemented SerenityRest methods for setting default values
 Now it is possible to set simple values, that will be used in requests/responses:
 ```
 setDefaultBasePath("/test/resources"); // will be used as base path
 setDefaultPort(9542);
 setUrlEncodingEnabled(true);
 setDefaultRootPath("core");
 setDefaultSessionId("session id");
 setDefaultAuthentication(RestAssured.basic(login, password));
 setDefaultConfig(configuration);
 setDefaultProxy(new ProxySpecification("exampleHost", 8095, "schema"));
 setDefaultParser(Parser.JSON);

 ```
 Also default request/responses can be provided to be used as base for each request/responses:
 ```
 ResponseSpecification specification = new ResponseSpecBuilder().rootPath("parent").build();
 setDefaultResponseSpecification(specification);
 ResponseSpecification created = given().get(url); //will use "parent" value as default rootPath

 ```
 * [24c5d3858022ddc](https://github.com/serenity-bdd/serenity-core/commit/24c5d3858022ddc) feat: added rule for configuration initialization of SerenityRest using configurtion Actions, added tests
Now it will be possible to use same default configuration of SerenityRest during execution different test methods:
```
    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(
        new RestConfigurationAction() {
            @Override
            void apply() {
                setDefaultBasePath("home/units")
            }
        }, new RestConfigurationAction() {
        @Override
        void apply() {
            setDefaultPort(0)
        }
    },)
```
Before test actions executed one by one, after tests SerenityRest.reset() is executed
## v1.1.30-rc.1
### No issue
 * [42ceca721e3a3e5](https://github.com/serenity-bdd/serenity-core/commit/42ceca721e3a3e5) updating changelog
## v1.1.29-rc.3
### No issue
 * [07762e7942397f7](https://github.com/serenity-bdd/serenity-core/commit/07762e7942397f7) Added basic Microsoft Edge driver support
 * [61a1476cf533a3a](https://github.com/serenity-bdd/serenity-core/commit/61a1476cf533a3a) updating changelog
## v1.1.29-rc.2
### No issue
 * [4f3ce0989535bee](https://github.com/serenity-bdd/serenity-core/commit/4f3ce0989535bee) Updated dependencies for the latest WebDriver version
 * [56b027874e67ebb](https://github.com/serenity-bdd/serenity-core/commit/56b027874e67ebb) Updated to Selenium 2.53.0
 * [550923311f59fd2](https://github.com/serenity-bdd/serenity-core/commit/550923311f59fd2) Made some of the tests more robust
 * [d525c1a50a20065](https://github.com/serenity-bdd/serenity-core/commit/d525c1a50a20065) Added a sample test for boolean questions
 * [bad49c5a270e8d7](https://github.com/serenity-bdd/serenity-core/commit/bad49c5a270e8d7) updating changelog
 * [e1d68e698ff7b38](https://github.com/serenity-bdd/serenity-core/commit/e1d68e698ff7b38) Removed some Java 8-specific code
 * [dc088066a321938](https://github.com/serenity-bdd/serenity-core/commit/dc088066a321938) Added the possiblity to provide boolean questions without a hamcrest matcher
 * [bd732c51c637fa1](https://github.com/serenity-bdd/serenity-core/commit/bd732c51c637fa1) bug: taking screenhot with browser without TakesScreenshot ability
 * [1c0eae5529d307a](https://github.com/serenity-bdd/serenity-core/commit/1c0eae5529d307a) added missed methods to check if webdriver alive
 * [89e9512d61ebfa1](https://github.com/serenity-bdd/serenity-core/commit/89e9512d61ebfa1) test: added tests to check how taking screenshorts works with died browsers
 * [240b93be5bd79a8](https://github.com/serenity-bdd/serenity-core/commit/240b93be5bd79a8) feat: added checking if browser alive before taking screenshot or saving page-source
 * [a1de78afa65cb88](https://github.com/serenity-bdd/serenity-core/commit/a1de78afa65cb88) Remove spurrious warning messages during stack trace analysis.
 * [a505bd9bcc18bde](https://github.com/serenity-bdd/serenity-core/commit/a505bd9bcc18bde) fix: updated closing web driver when appium is used, it seems that window handles still does not implemented for Android devices, only for iOS
 * [da6182271ea1329](https://github.com/serenity-bdd/serenity-core/commit/da6182271ea1329) updating changelog
 * [a4d6b324a8c8097](https://github.com/serenity-bdd/serenity-core/commit/a4d6b324a8c8097) fix: When using SerenityParameterizedRunner tests there was a need to add @Managed WebDriver in order to see the examples table. This is due to TestClassRunnerForInstanciatedTestCase not overriding initListeners
 * [3b3caad641fe25f](https://github.com/serenity-bdd/serenity-core/commit/3b3caad641fe25f) Remove spurrious warning messages during stack trace analysis.
 * [879a794fae661c5](https://github.com/serenity-bdd/serenity-core/commit/879a794fae661c5) added cheking array length
 * [59c3a42156cf186](https://github.com/serenity-bdd/serenity-core/commit/59c3a42156cf186) updated test, added missed method
 * [64174370c98b6db](https://github.com/serenity-bdd/serenity-core/commit/64174370c98b6db) fix: updated scenario data driven steps processing for report - now report will contains steps as from first scenario
 * [6aefe6917efc7ab](https://github.com/serenity-bdd/serenity-core/commit/6aefe6917efc7ab) test: refactored test for Qualifier
 * [e6e665143f90393](https://github.com/serenity-bdd/serenity-core/commit/e6e665143f90393) test: updated name of test method
 * [30d44ccbe4d3a2f](https://github.com/serenity-bdd/serenity-core/commit/30d44ccbe4d3a2f) added qualifiedTestImplementation
 * [71102acf032fd2c](https://github.com/serenity-bdd/serenity-core/commit/71102acf032fd2c) fix: updated processing of @Qualifier tag in junit tests with data tables. Now it is possible add short description to steps based on parameters value
 * [0f64dc1a2affa03](https://github.com/serenity-bdd/serenity-core/commit/0f64dc1a2affa03) Ensure that elements are visible before manipulating them with the Screenplay actions.
 * [bf6d91e2f403ea4](https://github.com/serenity-bdd/serenity-core/commit/bf6d91e2f403ea4) chore: created gradle build config for smoketests to execute them against latest serenty core
If build of smoketests will be run with `./gradlew clean test aggregate` - before build all version tags will be loaded and latest will be used as serenity-core version.
 * [9429532576a50ad](https://github.com/serenity-bdd/serenity-core/commit/9429532576a50ad) Moving definition of reportDirectory in order to allow easy configuration through the serenity block. Currently this directory gets set when applying the plugin, which makes it only possible to change through setting an environment variable at the same level as applying the plugin. For multi-module projects with compile dependencies, this does not work
## v1.1.29-rc.1
### No issue
 * [4f31025adb90677](https://github.com/serenity-bdd/serenity-core/commit/4f31025adb90677) updating changelog
 * [0691998f552206e](https://github.com/serenity-bdd/serenity-core/commit/0691998f552206e) fix: updated moving files. Not tmp files of reports will be moved only after stream will be closed
 * [3cacf176fa0d2dd](https://github.com/serenity-bdd/serenity-core/commit/3cacf176fa0d2dd) chore: updated appium java client to 3.3.0
 * [7cfbcfdf9002cea](https://github.com/serenity-bdd/serenity-core/commit/7cfbcfdf9002cea) appium.app is no longer required if appium.browserName is supplied
 * [bf3fa9a3d87560e](https://github.com/serenity-bdd/serenity-core/commit/bf3fa9a3d87560e) feat: updated processing of names
If mothod contains CSV JSON XML this abbreviations will not be changed
`TestCaseForCSVFormat` will be transformed to `Test case for CSV format`
## v1.1.28-rc.1
### [#321](https://github.com/serenity-bdd/serenity-core/issues/321) Exception/assertion messages missing
 * [5fdbaa99ea3d8b6](https://github.com/serenity-bdd/serenity-core/commit/5fdbaa99ea3d8b6) test: added test for fix of Exception/assertion message in serenity report for #321
 * [f6fd88055f428df](https://github.com/serenity-bdd/serenity-core/commit/f6fd88055f428df) fix: included in serenity report Exception/assertion message for #321
### No issue
 * [1a6f15809e451f0](https://github.com/serenity-bdd/serenity-core/commit/1a6f15809e451f0) test: redusing resource usage during testing of darkroom
 * [28908c9c14e1841](https://github.com/serenity-bdd/serenity-core/commit/28908c9c14e1841) updated error handling in test
 * [1c84dd4f4a7b27a](https://github.com/serenity-bdd/serenity-core/commit/1c84dd4f4a7b27a) test: Darkroom can be used in parallel
 * [5f7e414fd31985e](https://github.com/serenity-bdd/serenity-core/commit/5f7e414fd31985e) fix: updated test to fail if darkroom fail in parallel screenshot taking
 * [b6b3c3449cf0e6b](https://github.com/serenity-bdd/serenity-core/commit/b6b3c3449cf0e6b) updating changelog
## v1.1.27-rc.1
### No issue
 * [80d82d0461688f5](https://github.com/serenity-bdd/serenity-core/commit/80d82d0461688f5) fix: updated returned file to use generated file
 * [1314d42bbd0fd09](https://github.com/serenity-bdd/serenity-core/commit/1314d42bbd0fd09) feat: updated report generation to use atomic operations
 * [fa256947a31f6ee](https://github.com/serenity-bdd/serenity-core/commit/fa256947a31f6ee) fix: updated report generation to use temp files with random names
 * [5afea7264123c2b](https://github.com/serenity-bdd/serenity-core/commit/5afea7264123c2b) fix: updated report generation to use temp files, it fixes bugs with running tests with multiple workers (and different Java Runtime as well)
 * [d9e9c80a2db830c](https://github.com/serenity-bdd/serenity-core/commit/d9e9c80a2db830c) feat: updated method to pring exception ifit will appear
 * [b6e99e00c58bd44](https://github.com/serenity-bdd/serenity-core/commit/b6e99e00c58bd44) updated test to check ReportLoadingFailedError
 * [f91e7b346093be7](https://github.com/serenity-bdd/serenity-core/commit/f91e7b346093be7) feat: test updated for reporter and loader - same testoutcoume should be writed only once in report dirrectory
 * [e0200f274f3c578](https://github.com/serenity-bdd/serenity-core/commit/e0200f274f3c578) fix: updated report loading and generating code and added test to be sure that all can be run concurrently
 * [db2848abf2bec5b](https://github.com/serenity-bdd/serenity-core/commit/db2848abf2bec5b) Added the Open.browserOn(somePage) method

Feature suggested by @jan-molak.
 * [ab632f4de53980f](https://github.com/serenity-bdd/serenity-core/commit/ab632f4de53980f) feat: updated jbehave to 4.0.5
 * [3fe8573b0f5a7f9](https://github.com/serenity-bdd/serenity-core/commit/3fe8573b0f5a7f9) perf: updated checking of empty string to use StringUtils
 * [82f8953051af929](https://github.com/serenity-bdd/serenity-core/commit/82f8953051af929) docs: updating contributing docs
 * [60d1a4fbad5c31a](https://github.com/serenity-bdd/serenity-core/commit/60d1a4fbad5c31a) Updated Selenium to 2.52.0
 * [28efecc2ab74246](https://github.com/serenity-bdd/serenity-core/commit/28efecc2ab74246) Improved the renderening of EventualConsequence

EventualConsequences were not rendering correctly in the reports.

Thanks to @antonymarcano for steering this solution with the idea of an EventualConsequence where eventually() wraps the consequence, accepting a number of failures before returning.
 * [39fd6f97f8ea5b1](https://github.com/serenity-bdd/serenity-core/commit/39fd6f97f8ea5b1) updating changelog
 * [cf88c486f30db8a](https://github.com/serenity-bdd/serenity-core/commit/cf88c486f30db8a) Adding a line break for formatting.
 * [f9ba6e3ed041c5f](https://github.com/serenity-bdd/serenity-core/commit/f9ba6e3ed041c5f) Added a default value incase fileName is null.
 * [d01538c9936826a](https://github.com/serenity-bdd/serenity-core/commit/d01538c9936826a) doc: filtered revert and merge commits and pullrequests
 * [ffdda0622ae46a0](https://github.com/serenity-bdd/serenity-core/commit/ffdda0622ae46a0) doc: updated title in release notes
 * [455902c550aa4ee](https://github.com/serenity-bdd/serenity-core/commit/455902c550aa4ee) doc: updated name of release notes
 * [c6fd574d7929aa7](https://github.com/serenity-bdd/serenity-core/commit/c6fd574d7929aa7) doc: updated changelog
## v1.1.26-rc.4
### No issue
 * [bc1a2848e81e56c](https://github.com/serenity-bdd/serenity-core/commit/bc1a2848e81e56c) Refactored some unit tests to check the fix for the Saucelinks link problem properly
 * [c80004c26a41faa](https://github.com/serenity-bdd/serenity-core/commit/c80004c26a41faa) Made the Cucumber support more robust

The parser will no longer crash if there are empty or badly-formed feature files.
 * [7200f4b42c1dc8d](https://github.com/serenity-bdd/serenity-core/commit/7200f4b42c1dc8d) Minor bug fix with the Saucelabs video icon

Minor bug fix where the Saucelabs video icon was incorrectly displayed.
## v1.1.26-rc.3
### No issue
 * [433b732734f4eaa](https://github.com/serenity-bdd/serenity-core/commit/433b732734f4eaa) Allow more elegant waits in the Screenplay module

You can now write code like this:

jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(10))))

This will not fail if the matcher cannot be evaluated the first time, but will retry up to a maximum of 'serenity.timouts' seconds (5 by default).
 * [e0c22637eccbd6e](https://github.com/serenity-bdd/serenity-core/commit/e0c22637eccbd6e) Corrected an error in the module names
## v1.1.26-rc.2
### No issue
 * [0e08c8c86f76f9c](https://github.com/serenity-bdd/serenity-core/commit/0e08c8c86f76f9c) Use the correct name for the screenplay library for this version
 * [9d5fb9e53e2a7c7](https://github.com/serenity-bdd/serenity-core/commit/9d5fb9e53e2a7c7) style: updating test style
 * [91c32382040967d](https://github.com/serenity-bdd/serenity-core/commit/91c32382040967d) fix: updating version of serenityc-core and maven-plugin
 * [6f4f5d461d969c0](https://github.com/serenity-bdd/serenity-core/commit/6f4f5d461d969c0) fix: updating logging for serenity gradle plugin, using simple out stream
 * [0340f82dcb76e3c](https://github.com/serenity-bdd/serenity-core/commit/0340f82dcb76e3c) fix: updating logging for serenity gradle plugin
 * [412657bb56c8740](https://github.com/serenity-bdd/serenity-core/commit/412657bb56c8740) fix: updating gradle plugin to work with new configuration
 * [ffdc3efce06074d](https://github.com/serenity-bdd/serenity-core/commit/ffdc3efce06074d) fix: updating requirements directory to be able work with multimodule projects
 * [cbc92cba6fc8792](https://github.com/serenity-bdd/serenity-core/commit/cbc92cba6fc8792) fix: updated gradle plugin to work with multimodule projects
 * [4f1581ce6369aaa](https://github.com/serenity-bdd/serenity-core/commit/4f1581ce6369aaa) fix: updating getSessionId method to get session id without init new webdriver
 * [d9c1e6a65a28bb8](https://github.com/serenity-bdd/serenity-core/commit/d9c1e6a65a28bb8) style: updated name of test method
 * [bf8fca33efc9aa0](https://github.com/serenity-bdd/serenity-core/commit/bf8fca33efc9aa0) fix: remote driver session id can be under proxied driver
 * [909f21a66d8f732](https://github.com/serenity-bdd/serenity-core/commit/909f21a66d8f732) Renamed serenity-journey to serenity-screenplay

Also allow conditional tasks of the following form:

        dana.attemptsTo(
                Check.whether(cost>100)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

Or with a Question<Boolean>:

        dana.attemptsTo(
                Check.whether(itIsTooExpensive)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );
 * [720c516a2a126fb](https://github.com/serenity-bdd/serenity-core/commit/720c516a2a126fb) chore: added gitattributes
 * [862b790b16d85ac](https://github.com/serenity-bdd/serenity-core/commit/862b790b16d85ac) Updated smoke tests
 * [612401d19781815](https://github.com/serenity-bdd/serenity-core/commit/612401d19781815) Minor refactoring
 * [c9572c849d9e710](https://github.com/serenity-bdd/serenity-core/commit/c9572c849d9e710) Actors can now perform tasks conditionally

Use the Unless class static methods and a bolean expression, e.g.

```
Unless.the(items.isEmpty(), AddTodoItems.called(items))
```

or use a question of type Question<Boolean>:

```
Unless.the(itemsListisEmpty(), AddTodoItems.called(items))
```
 * [3e0d99168dbec1a](https://github.com/serenity-bdd/serenity-core/commit/3e0d99168dbec1a) Actors can now perform tasks conditionally

Use the Unless class static methods and a bolean expression, e.g.

```
Unless.the(items.isEmpty(), AddTodoItems.called(items))
```

or use a question of type Question<Boolean>:

```
Unless.the(itemsListisEmpty(), AddTodoItems.called(items))
```
## v1.1.26-rc.1
### No issue
 * [91aee6a29aae70e](https://github.com/serenity-bdd/serenity-core/commit/91aee6a29aae70e) Revert "Git Attributes Experiment, please don't merge"
 * [0dcba83994ed144](https://github.com/serenity-bdd/serenity-core/commit/0dcba83994ed144) Revert "Updating gitattributes not to update chromedriver and woff files"
 * [e71056d57767f35](https://github.com/serenity-bdd/serenity-core/commit/e71056d57767f35) fix: updated gitattributes
 * [b98f19f058c8035](https://github.com/serenity-bdd/serenity-core/commit/b98f19f058c8035) chore: updated gitattributes
 * [5236c58bf846648](https://github.com/serenity-bdd/serenity-core/commit/5236c58bf846648) Revert "Git Attributes Experiment, please don't merge"
 * [c6122c8a4976149](https://github.com/serenity-bdd/serenity-core/commit/c6122c8a4976149) fix: fixed nullpointer if json config does not exists
 * [ea5f1adc05581fc](https://github.com/serenity-bdd/serenity-core/commit/ea5f1adc05581fc) fix: restored gitattributes
## v1.1.25-rc.7
### No issue
 * [5cb49b2e6ce64c5](https://github.com/serenity-bdd/serenity-core/commit/5cb49b2e6ce64c5) Made reading UI values more fluent.

The narrative was interrupted by the .value() so hidden away for now behind a more fluent method
 * [c28a95ec9310748](https://github.com/serenity-bdd/serenity-core/commit/c28a95ec9310748) Support for multiple matchers in Consequences

You can now make multiple assertions against a single question
## v1.1.25-rc.6
### No issue
 * [4b1537ede322502](https://github.com/serenity-bdd/serenity-core/commit/4b1537ede322502) Restored a renamed method to maintain backward compatibility.
 * [9e66e4bd6f59659](https://github.com/serenity-bdd/serenity-core/commit/9e66e4bd6f59659) fix: updated commons-collection for jira/jbehave/cucumber modules
## v1.1.25-rc.5
### No issue
 * [c4a8fc16ec943ca](https://github.com/serenity-bdd/serenity-core/commit/c4a8fc16ec943ca) Refactoring and performance improvements
 * [2e2450a32770d5e](https://github.com/serenity-bdd/serenity-core/commit/2e2450a32770d5e) Tests can now manage whether cookies should be cleared between each test
## v1.1.25-rc.4
### [#255](https://github.com/serenity-bdd/serenity-core/issues/255) Slowness using 1.1.17-rc.3
 * [0a9e50ae5782ba9](https://github.com/serenity-bdd/serenity-core/commit/0a9e50ae5782ba9) Fixed #255
### [#281](https://github.com/serenity-bdd/serenity-core/issues/281) Click on a link is not waiting for default time
 * [4531d43df845228](https://github.com/serenity-bdd/serenity-core/commit/4531d43df845228) Fixed issue #281

During verbose logging, Serenity tried to read the tag from web elements. This could cause failures if the element was stale or unavailable when the logging happen. This has now been changed to log the locator and not the element tag type.
### No issue
 * [95449fed05268e1](https://github.com/serenity-bdd/serenity-core/commit/95449fed05268e1) style: update test style
 * [4e495f56444915a](https://github.com/serenity-bdd/serenity-core/commit/4e495f56444915a) style: test updated
 * [85e23de89036021](https://github.com/serenity-bdd/serenity-core/commit/85e23de89036021) fix: update charset usage during reading/writing
 * [194f3d966d78014](https://github.com/serenity-bdd/serenity-core/commit/194f3d966d78014) fix: updated report template generation
 * [3e864ae7bfeef59](https://github.com/serenity-bdd/serenity-core/commit/3e864ae7bfeef59) fix: selenium version upgrade to 2.50.1
 * [4858e0b156973d1](https://github.com/serenity-bdd/serenity-core/commit/4858e0b156973d1) fix: updating report engine to wait results of report generation, stream and readers closing
 * [ba8fcee2574ce65](https://github.com/serenity-bdd/serenity-core/commit/ba8fcee2574ce65) Hardened some of the integration tests
 * [e772cd1b4d1773d](https://github.com/serenity-bdd/serenity-core/commit/e772cd1b4d1773d) Attempt to make some of the tests more robust.
 * [1b3aa6568c4a004](https://github.com/serenity-bdd/serenity-core/commit/1b3aa6568c4a004) Removed the .gitattribues file from git as it causes problems with the build pipeline on Snap-CI
 * [9f1f6b5c05f6c1b](https://github.com/serenity-bdd/serenity-core/commit/9f1f6b5c05f6c1b) Added an Action class to scroll to a particular eleemtn on the screen.
 * [123e26d0bda8d05](https://github.com/serenity-bdd/serenity-core/commit/123e26d0bda8d05) fix: updated processing of "browserstack.os.version" and "browserstack.browser.version" system properties according to latest changes on BrowserStack side
 * [e96d512758d1846](https://github.com/serenity-bdd/serenity-core/commit/e96d512758d1846) style: updated test
 * [9eb93903d47414a](https://github.com/serenity-bdd/serenity-core/commit/9eb93903d47414a) chore: updating gitignore
 * [737b1aaf46a94ab](https://github.com/serenity-bdd/serenity-core/commit/737b1aaf46a94ab) chore: updated wrapper, and build publishing libs
 * [55b06c1ce3ecc50](https://github.com/serenity-bdd/serenity-core/commit/55b06c1ce3ecc50) chore: updated wrapper, and build publishing libs
 * [ed33d6a045ffeed](https://github.com/serenity-bdd/serenity-core/commit/ed33d6a045ffeed) Updated to Seleniy, 2.49.1
## v1.1.25-rc.3
### No issue
 * [adc66f3bd6142af](https://github.com/serenity-bdd/serenity-core/commit/adc66f3bd6142af) added a test to check the test report output; updated previously failed tests for customized step title
 * [af3562dcf668650](https://github.com/serenity-bdd/serenity-core/commit/af3562dcf668650) updated existing tests after changes in ExecutedStepDescription class
 * [e25d54860c48130](https://github.com/serenity-bdd/serenity-core/commit/e25d54860c48130) added the tests to cover storing arguments list in ExecutedStepDescription class
 * [9d046220442e6d6](https://github.com/serenity-bdd/serenity-core/commit/9d046220442e6d6) fix: customized step title if some parameter contains comma character
 * [864c00c9d01ce7b](https://github.com/serenity-bdd/serenity-core/commit/864c00c9d01ce7b) delted maven repo from build.gradle
 * [42be3ba710578c2](https://github.com/serenity-bdd/serenity-core/commit/42be3ba710578c2) Browsermob update: using browsermob-core-littleproxy instead of old browsermob-proxy
 * [c9c9e1b8a682ba3](https://github.com/serenity-bdd/serenity-core/commit/c9c9e1b8a682ba3) chore: turn off parallel execution of submodules
 * [12b5943d2f74407](https://github.com/serenity-bdd/serenity-core/commit/12b5943d2f74407) chore: updated org.gradle.workers.max value to reduce memory usage during build
 * [9d6e27dcb4f231d](https://github.com/serenity-bdd/serenity-core/commit/9d6e27dcb4f231d) chore: updated org.gradle.workers.max value to reduce memory usage during build
 * [418d37ca898fe72](https://github.com/serenity-bdd/serenity-core/commit/418d37ca898fe72) chore: updated org.gradle.workers.max value to reduce memory usage during build
 * [0a8a1856c6c06b9](https://github.com/serenity-bdd/serenity-core/commit/0a8a1856c6c06b9) chore: updated build to enable paralell build
 * [f973f15b6c89d2e](https://github.com/serenity-bdd/serenity-core/commit/f973f15b6c89d2e) fix: updated plugin to get serenity.properties from current module build dir
 * [89eebf56d85309e](https://github.com/serenity-bdd/serenity-core/commit/89eebf56d85309e) fix: serenity.properties can be located not in workin dir, but in gradle/maven module folder
 * [530450bb5c94b71](https://github.com/serenity-bdd/serenity-core/commit/530450bb5c94b71) fix: updated resolution of output dir based on gradle/maven module
 * [246246020826f03](https://github.com/serenity-bdd/serenity-core/commit/246246020826f03) fix: updated build task dependecies
 * [c47b69d154d32aa](https://github.com/serenity-bdd/serenity-core/commit/c47b69d154d32aa) chore: build test parallel execution enabled (PerCore)
 * [36225a5908e8b2c](https://github.com/serenity-bdd/serenity-core/commit/36225a5908e8b2c) fix: report generation for multimodule builds
 * [60b1b99079044a0](https://github.com/serenity-bdd/serenity-core/commit/60b1b99079044a0) Fine-tuned the soft-assert tests and minor reporting bug  fix.
## v1.1.25-rc.2
### No issue
 * [511f6079b8eb17d](https://github.com/serenity-bdd/serenity-core/commit/511f6079b8eb17d) Added support for By locators in Target objects and Action classes.
 * [e2ea2ea4999fe72](https://github.com/serenity-bdd/serenity-core/commit/e2ea2ea4999fe72) Updated smoketests
 * [2b2d49d1ac787ac](https://github.com/serenity-bdd/serenity-core/commit/2b2d49d1ac787ac) Added support for By locators in Target objects and Action classes.
## v1.1.25-rc.1
### [#243](https://github.com/serenity-bdd/serenity-core/issues/243) upgrade typesafe config to newer version
 * [05ac6bd4c911b89](https://github.com/serenity-bdd/serenity-core/commit/05ac6bd4c911b89) Revert "#243 Upgrading typesafe.config from 1.2 to 1.3"
### No issue
 * [1f0dba14b3c4c55](https://github.com/serenity-bdd/serenity-core/commit/1f0dba14b3c4c55) Updated smoke test dependencies
 * [7caec064fd2d81d](https://github.com/serenity-bdd/serenity-core/commit/7caec064fd2d81d) Removed a redundant test
 * [c7bcd9643a1d3b7](https://github.com/serenity-bdd/serenity-core/commit/c7bcd9643a1d3b7) Multiple assertions in the same should() method are now treated as "soft" asserts.
 * [5f78a7b2ca9d63e](https://github.com/serenity-bdd/serenity-core/commit/5f78a7b2ca9d63e) style: changed style of one test
 * [5db32dfbc9fba66](https://github.com/serenity-bdd/serenity-core/commit/5db32dfbc9fba66) fix: update serenity-gradle-plugin to use same Configuration as Tests
 * [a610239496c0955](https://github.com/serenity-bdd/serenity-core/commit/a610239496c0955) fix: aggregation report generation in gradle plugin
 * [b944867051253bd](https://github.com/serenity-bdd/serenity-core/commit/b944867051253bd) chore: upgrade groovy from 2.3.* to 2.4.4
 * [a60d52606dfc710](https://github.com/serenity-bdd/serenity-core/commit/a60d52606dfc710) fix: move reports about configuration to specific folder
 * [5d17bd44a9869b0](https://github.com/serenity-bdd/serenity-core/commit/5d17bd44a9869b0) chore: gradle take version from local variable
 * [d5b5401f3adfc8f](https://github.com/serenity-bdd/serenity-core/commit/d5b5401f3adfc8f) chore: gradle to 2.10 and groovy to 2.4.4 upgraded
 * [777c06110da4f2a](https://github.com/serenity-bdd/serenity-core/commit/777c06110da4f2a) fix: report with properties should be in json
 * [9e412bfeb9d5839](https://github.com/serenity-bdd/serenity-core/commit/9e412bfeb9d5839) fix: report with properties should be in report folder
 * [8efe039bbb25754](https://github.com/serenity-bdd/serenity-core/commit/8efe039bbb25754) chore: added report for configuration, with actual properties
## v1.1.24
### No issue
 * [32aba8a8a1676a7](https://github.com/serenity-bdd/serenity-core/commit/32aba8a8a1676a7) Improved exception reporting
 * [b5a6c975fb31e82](https://github.com/serenity-bdd/serenity-core/commit/b5a6c975fb31e82) Improved exception reporting
 * [f36115c4978f460](https://github.com/serenity-bdd/serenity-core/commit/f36115c4978f460) Added matchers to allow questions about web element states (designed mainly to be used for low-level preconditions or assertions), e.g.

then(dana).should(seeThat(the(ProfilePage.NAME_FIELD), isVisible()));
 * [198a036ac2e3ce4](https://github.com/serenity-bdd/serenity-core/commit/198a036ac2e3ce4) Improved reporting of customised error messages in consequences
 * [1dbda1aec900a98](https://github.com/serenity-bdd/serenity-core/commit/1dbda1aec900a98) docs: adding instructions of contributors
 * [e240e7f100216a1](https://github.com/serenity-bdd/serenity-core/commit/e240e7f100216a1) chore: upgrade typesafe.config to 1.3 from 1.2
## v1.1.22-rc.15
### No issue
 * [8d6dafa79e3917e](https://github.com/serenity-bdd/serenity-core/commit/8d6dafa79e3917e) Fixed a bug in the reporting of Journey Pattern web actions.
 * [594460938c80423](https://github.com/serenity-bdd/serenity-core/commit/594460938c80423) When using a unique browser for multiple tests, clear the cookies and HTML local storage between tests.
 * [c200b3262d83a1d](https://github.com/serenity-bdd/serenity-core/commit/c200b3262d83a1d) Improved the reporting of Journey pattern by removing redundant "is" clauses generated by the Hamcrest matchers.
## v1.1.22-rc.14
### No issue
 * [b6586774ee26b6e](https://github.com/serenity-bdd/serenity-core/commit/b6586774ee26b6e) Added the Evaluate action and the JavaScript question to perform JavaScript queries.
 * [ef0e61af836455a](https://github.com/serenity-bdd/serenity-core/commit/ef0e61af836455a) Updated the smoke tests.
 * [a700aa2d0654491](https://github.com/serenity-bdd/serenity-core/commit/a700aa2d0654491) The Target class now accepts a prefix notation to specify the locator, e.g Target.the("name field").locatedBy("css:#name") or Target.the("name field").locatedBy("id:name")
This supports all of the WebDriver locator strategies:
   - id
   - css
   - xpath
   - name
   - tagName
   - className
   - linkText
   - partialLinkText
 * [e8d86a9b1562f22](https://github.com/serenity-bdd/serenity-core/commit/e8d86a9b1562f22) Refactored a journey pattern test to illustrate the displays matcher
 * [182dfa03c9c217c](https://github.com/serenity-bdd/serenity-core/commit/182dfa03c9c217c) Refactored the Journey Pattern code
 * [e9610ed6fc8e761](https://github.com/serenity-bdd/serenity-core/commit/e9610ed6fc8e761) Refactored the Enter action to allow entering text and keys in the same action
## v1.1.22-rc.13
### No issue
 * [7e41d67d6acaf5d](https://github.com/serenity-bdd/serenity-core/commit/7e41d67d6acaf5d) Trying to fixe a performance issue related to resource copying
 * [00615130588c8b2](https://github.com/serenity-bdd/serenity-core/commit/00615130588c8b2) Added a method to the WebDriverManager instance to retreive a named webdriver instance.
 * [eec89ad0c6d2488](https://github.com/serenity-bdd/serenity-core/commit/eec89ad0c6d2488) Fixed a bug that reported a misleading "class cast exception" when the moveTo() method was called after a test failure.
 * [8dad9ccfa05c76e](https://github.com/serenity-bdd/serenity-core/commit/8dad9ccfa05c76e) Added the ability to use the Serenity WebDriver API directly in Action classes, by extending the WebAction class.
 * [5fdf07c2b07a731](https://github.com/serenity-bdd/serenity-core/commit/5fdf07c2b07a731) Fixed a bug where enums did not appear correctly in the test reports when they were referenced by Journey Pattern Questions.
 * [edcdc4ad932fc62](https://github.com/serenity-bdd/serenity-core/commit/edcdc4ad932fc62) It is now possible to add page objects as member variables in Performable or Question classes - they will be correctly configured with the WebDriver instance associated with the actor.
 * [c8261771133d28e](https://github.com/serenity-bdd/serenity-core/commit/c8261771133d28e) Refactored the bundled Journey Pattern action classes.
## v1.1.22-rc.12
### Jira
 * [9c8f3b20036cdb6](https://github.com/serenity-bdd/serenity-core/commit/9c8f3b20036cdb6) 217_issue: removed dependency org.mortbay.jetty:servlet-api-2.5:6.1.9, it is duplicated with javax.servlet:javax.servlet-api:3.1.0
### No issue
 * [32282fee3f80aaf](https://github.com/serenity-bdd/serenity-core/commit/32282fee3f80aaf) Updated smoketests to refactored journey pattern
 * [c6803e834640e9d](https://github.com/serenity-bdd/serenity-core/commit/c6803e834640e9d) Readability improvements and moved the UI Action classes into their own package.
 * [4c9a1b1ec71d248](https://github.com/serenity-bdd/serenity-core/commit/4c9a1b1ec71d248) 223_issue: reloading result dir
 * [8d42d0d2f40dea5](https://github.com/serenity-bdd/serenity-core/commit/8d42d0d2f40dea5) 223_issue: reverting updating of some imports
 * [994da12a7d2a331](https://github.com/serenity-bdd/serenity-core/commit/994da12a7d2a331) Revert "Pull request for updating SerenityRest to log all types of input"
 * [54721c4a11479df](https://github.com/serenity-bdd/serenity-core/commit/54721c4a11479df) 223_issue: added reloading output dir for tests
 * [c46729fb285850d](https://github.com/serenity-bdd/serenity-core/commit/c46729fb285850d) 217_issue: style fix
 * [0e4b788b99f6a3a](https://github.com/serenity-bdd/serenity-core/commit/0e4b788b99f6a3a) 217_issue: removed old and never updated files
## v1.1.22-rc.11
### No issue
 * [86ff95a0f343746](https://github.com/serenity-bdd/serenity-core/commit/86ff95a0f343746) 216_issue: update versions
 * [c44f078c170cb79](https://github.com/serenity-bdd/serenity-core/commit/c44f078c170cb79) 218_issue: added test for checking if web scenarious executed successfully with HTMLUnit (fails now, so added @Ignore)
 * [5f8752f8da66e0b](https://github.com/serenity-bdd/serenity-core/commit/5f8752f8da66e0b) 216_issue: update versions
 * [f998b4a55e25fe8](https://github.com/serenity-bdd/serenity-core/commit/f998b4a55e25fe8) 185_issue: log and auth wrappers implemented, tests profivided. redirects still not supported
 * [7ab6c2502ff35f1](https://github.com/serenity-bdd/serenity-core/commit/7ab6c2502ff35f1) 197_issue: updated SerenityRest to log all types of input for content/body rest call
 * [2cec6f26c6f2f87](https://github.com/serenity-bdd/serenity-core/commit/2cec6f26c6f2f87) fix: Fix for setting serenity.proxy.type and http_port. Needs to be an number instead of string.
 * [7c4e0df97b5bb04](https://github.com/serenity-bdd/serenity-core/commit/7c4e0df97b5bb04) fix: cglib dependency conflict from guice
## v1.1.22-rc.10
### No issue
 * [e21e9a5c18b2e3c](https://github.com/serenity-bdd/serenity-core/commit/e21e9a5c18b2e3c) Updated tests
 * [9f9075212fc2f3c](https://github.com/serenity-bdd/serenity-core/commit/9f9075212fc2f3c) Retry to unzip a resource file if it is locked. This is a work-around for Windows-related file locking issues.
 * [81c87bb9040cacb](https://github.com/serenity-bdd/serenity-core/commit/81c87bb9040cacb) Fixed an error with the screenshots that always displayed the screen source link, even for successful tests.
 * [fc8442b03447e52](https://github.com/serenity-bdd/serenity-core/commit/fc8442b03447e52) Restored step logging to INFO.
 * [c9889ca6204e9a5](https://github.com/serenity-bdd/serenity-core/commit/c9889ca6204e9a5) Added more robustness to the report generation by allowing ZIP files to be opened again if they couldn't the first time
 * [73015c29b297366](https://github.com/serenity-bdd/serenity-core/commit/73015c29b297366) fix for nested cleaning resources
 * [8d03dda6b41d7f3](https://github.com/serenity-bdd/serenity-core/commit/8d03dda6b41d7f3) Updated tests for screenshots
 * [4267bd936dc4dc3](https://github.com/serenity-bdd/serenity-core/commit/4267bd936dc4dc3) updated Resizer. Fixed opening output and input stream in same time
 * [a7bdaeb08399244](https://github.com/serenity-bdd/serenity-core/commit/a7bdaeb08399244) Updated input streams closing
 * [79d266003d25a27](https://github.com/serenity-bdd/serenity-core/commit/79d266003d25a27) see https://github.com/serenity-bdd/serenity-core/issues/179
 * [7ebca229c0a391d](https://github.com/serenity-bdd/serenity-core/commit/7ebca229c0a391d) 184_issue: test added
 * [b5e520997b94138](https://github.com/serenity-bdd/serenity-core/commit/b5e520997b94138) 184_issue: logging for PATCH operation added
## v1.1.22-rc.9
### No issue
 * [930f34cb355c189](https://github.com/serenity-bdd/serenity-core/commit/930f34cb355c189) Record screen source code for failing tests.
## v1.1.22-rc.8
### No issue
 * [552172c80ce7608](https://github.com/serenity-bdd/serenity-core/commit/552172c80ce7608) Set the serenity.console.colors property to true to get ANSI colors in the console output (don't use on Jenkins).
## v1.1.22-rc.7
### No issue
 * [d4791cdd90311c5](https://github.com/serenity-bdd/serenity-core/commit/d4791cdd90311c5) Fixed a bug that prevented @Pending annotations from working with non-instrumented Performable objects
## v1.1.22-rc.6
### No issue
 * [283093853e20adf](https://github.com/serenity-bdd/serenity-core/commit/283093853e20adf) Made the console logging colors a bit lighter for better readability
 * [e7d7c85df2489c1](https://github.com/serenity-bdd/serenity-core/commit/e7d7c85df2489c1) Updated unit tests
 * [5099debaaa78053](https://github.com/serenity-bdd/serenity-core/commit/5099debaaa78053) Added color to logs
 * [93b1411da124872](https://github.com/serenity-bdd/serenity-core/commit/93b1411da124872) Added color to logs
 * [8846f6e32637978](https://github.com/serenity-bdd/serenity-core/commit/8846f6e32637978) Improved console log messages to cater for errors and failed assumptions
 * [e329275237f7aa4](https://github.com/serenity-bdd/serenity-core/commit/e329275237f7aa4) Updated unit tests
 * [8a21a9f2ca827b9](https://github.com/serenity-bdd/serenity-core/commit/8a21a9f2ca827b9) Improved reporting
 * [4f189ca2db7b989](https://github.com/serenity-bdd/serenity-core/commit/4f189ca2db7b989) 188_issue: new level of take screenshots configuration added
 * [42691e1efb02790](https://github.com/serenity-bdd/serenity-core/commit/42691e1efb02790) Improved reporting
 * [597960b30fe36a4](https://github.com/serenity-bdd/serenity-core/commit/597960b30fe36a4) Minor Base Step Listener Constructor update
 * [1af09f3f475ab89](https://github.com/serenity-bdd/serenity-core/commit/1af09f3f475ab89) 179_issue: added tests and fix for issue
 * [5b77f9ac5c51742](https://github.com/serenity-bdd/serenity-core/commit/5b77f9ac5c51742) Photographer cleanup fix. If driver not initialized - nothing to clean
 * [ece74d2b38035f3](https://github.com/serenity-bdd/serenity-core/commit/ece74d2b38035f3) Remove an unnecessary moveTo() operation.

This seems to cause class cast exceptions from time to time.
 * [2d39575adf3fa35](https://github.com/serenity-bdd/serenity-core/commit/2d39575adf3fa35) 130_issue: added system configuration for output dirrectory
 * [ef3ca8d84926738](https://github.com/serenity-bdd/serenity-core/commit/ef3ca8d84926738) 130_issue: added system environment properties to configuration
 * [bc6e1f9ea2d8fd3](https://github.com/serenity-bdd/serenity-core/commit/bc6e1f9ea2d8fd3) Reduce noise in the logs by removing an superfluous error message.
 * [0a1679f36c02583](https://github.com/serenity-bdd/serenity-core/commit/0a1679f36c02583) fix build fail by updating test-outcomes.ftl
 * [26219f70f9f5905](https://github.com/serenity-bdd/serenity-core/commit/26219f70f9f5905) 130_issue: reading serenity.properties fix
## v1.1.22-rc.4
### No issue
 * [4c20cd5f39da75b](https://github.com/serenity-bdd/serenity-core/commit/4c20cd5f39da75b) Minor improvement to assertion reporting, to avoid lines being hidden for some assertions
## v1.1.22-rc.3
### No issue
 * [8edf62c3dcbf93a](https://github.com/serenity-bdd/serenity-core/commit/8edf62c3dcbf93a) Better handling of reporting arbitrary AssertionError exceptions.
## v1.1.22-rc.2
### [#175](https://github.com/serenity-bdd/serenity-core/issues/175) error in test-outcomes.ftl
 * [6ccd53e78491ebb](https://github.com/serenity-bdd/serenity-core/commit/6ccd53e78491ebb) Fixed #175
### Jira
 * [ad3af93eda7fa4a](https://github.com/serenity-bdd/serenity-core/commit/ad3af93eda7fa4a) Having ProvidedDriver implement JavascriptExecutor should not be the correct way to fix THUCYDIDES-253. The method that checks if the driver is javascript enabled looks at the driver class returned from WebDriverFacade and in the case, it will see that ProvidedDriver implements JavascriptExecutor but when it tries to execute javascript on the proxied driver that does not necessarily have to implement JavascriptExecutor, then it will throw a method not found exception. This proposed fix checks if the driverclass in the WebDriverFacade is a provided driver, if it is, then the correct driver class it should look at is contained in the proxied driver.
### No issue
 * [9bcc6643ff58626](https://github.com/serenity-bdd/serenity-core/commit/9bcc6643ff58626) Better formatting for the result line at the bottom of the test outcome table
 * [a241bd85dac63aa](https://github.com/serenity-bdd/serenity-core/commit/a241bd85dac63aa) Test hardening
 * [83496ffda0d8e41](https://github.com/serenity-bdd/serenity-core/commit/83496ffda0d8e41) Fine-tuned reporting
 * [eb37b9b1589c016](https://github.com/serenity-bdd/serenity-core/commit/eb37b9b1589c016) 132_issue: desabling retries in smoke-tests
 * [f3e533f87c0a853](https://github.com/serenity-bdd/serenity-core/commit/f3e533f87c0a853) 128_issue: fixing style
 * [3eba4e88493fffc](https://github.com/serenity-bdd/serenity-core/commit/3eba4e88493fffc) 128_issue: updated checking Content Type according RFC1341, and added test for https rest tests based on github
 * [8a671ce7f4640e4](https://github.com/serenity-bdd/serenity-core/commit/8a671ce7f4640e4) 132_issue: clean task fix
 * [632b09d0fb3388f](https://github.com/serenity-bdd/serenity-core/commit/632b09d0fb3388f) 132_issue: little refactoring, moving string to constants
 * [02b260e4116e90d](https://github.com/serenity-bdd/serenity-core/commit/02b260e4116e90d) 132_issue: fixing incorrect test. Notifier should record failure
 * [dc809646f130397](https://github.com/serenity-bdd/serenity-core/commit/dc809646f130397) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, fixing tests from different modules
 * [acf58d8c842dc3f](https://github.com/serenity-bdd/serenity-core/commit/acf58d8c842dc3f) 132_issue: fixing name of menthod in reports
 * [a798480e4e89951](https://github.com/serenity-bdd/serenity-core/commit/a798480e4e89951) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing imports
 * [41361d014bac870](https://github.com/serenity-bdd/serenity-core/commit/41361d014bac870) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true
 * [27508e1975be3e4](https://github.com/serenity-bdd/serenity-core/commit/27508e1975be3e4) 132_issue: test and solution provided
 * [1dae0a507294270](https://github.com/serenity-bdd/serenity-core/commit/1dae0a507294270) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing description creating method
 * [ca0ffa080140a93](https://github.com/serenity-bdd/serenity-core/commit/ca0ffa080140a93) Fixing getdrivername method to take this.driverClass instead of the getter since the getter may not return a SupportedDriver anymore
 * [652c048ac985d1f](https://github.com/serenity-bdd/serenity-core/commit/652c048ac985d1f) Test that checks to see if the proxy driver class is returned when the the driver class is the provided driver
 * [169b5e8fd721a0e](https://github.com/serenity-bdd/serenity-core/commit/169b5e8fd721a0e) Refactoring
 * [e957030c91106c4](https://github.com/serenity-bdd/serenity-core/commit/e957030c91106c4) Refactored redundant tests
 * [a83934edd78de54](https://github.com/serenity-bdd/serenity-core/commit/a83934edd78de54) Refactoring
 * [0bcdbcfb461f8ce](https://github.com/serenity-bdd/serenity-core/commit/0bcdbcfb461f8ce) refactor: Corrects throwning of IOException, instead of Exception
 * [ea10f238c429373](https://github.com/serenity-bdd/serenity-core/commit/ea10f238c429373) Refactoring
 * [e0b51a437d355f9](https://github.com/serenity-bdd/serenity-core/commit/e0b51a437d355f9) fix loop when parameter is null in ddt tests
## v1.1.22-rc.1
### No issue
 * [1b62b2c1e4df337](https://github.com/serenity-bdd/serenity-core/commit/1b62b2c1e4df337) Removed redundant test
 * [3e05bb0b83686c9](https://github.com/serenity-bdd/serenity-core/commit/3e05bb0b83686c9) Updated a unit test
 * [3794e2b28ed858c](https://github.com/serenity-bdd/serenity-core/commit/3794e2b28ed858c) Improved reporting

Add the 'serenity.linked.tags' property, which allows you to defined tag types which will result in human-readable tags that can be used as bookmarks or external links.
 * [73946ec52a54f9c](https://github.com/serenity-bdd/serenity-core/commit/73946ec52a54f9c) Updated versions in the smoketests
 * [7a1c66f46acc6d4](https://github.com/serenity-bdd/serenity-core/commit/7a1c66f46acc6d4) Made the WebdriverManager publicly available for advanced use cases.
## v1.1.21-rc.1
### No issue
 * [4af8b65a5867895](https://github.com/serenity-bdd/serenity-core/commit/4af8b65a5867895) Removed an incorrect reference to a Java 8 class
## v1.1.20-rc.1
### No issue
 * [00d0237227e7f39](https://github.com/serenity-bdd/serenity-core/commit/00d0237227e7f39) Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests
 * [0e777550299bd00](https://github.com/serenity-bdd/serenity-core/commit/0e777550299bd00) Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests
## v1.1.19
### No issue
 * [ba52bc42560b4a0](https://github.com/serenity-bdd/serenity-core/commit/ba52bc42560b4a0) Fixed a potential infinite loop in the report generation if image processing failed for some reason
## v1.1.18-rc.2
### No issue
 * [89a443f17da25d0](https://github.com/serenity-bdd/serenity-core/commit/89a443f17da25d0) Finished merge
 * [785c7bedb82ce42](https://github.com/serenity-bdd/serenity-core/commit/785c7bedb82ce42) Finished merge
 * [062d4daac873cb0](https://github.com/serenity-bdd/serenity-core/commit/062d4daac873cb0) Improved reporting
 * [f6464d1224fd8d5](https://github.com/serenity-bdd/serenity-core/commit/f6464d1224fd8d5) updating tests for using ThucydesWebDriverSupport
 * [dcab9c63c6952de](https://github.com/serenity-bdd/serenity-core/commit/dcab9c63c6952de) updating test to use ThucydidesWebDriverSupport
 * [b4bbc317f98a23a](https://github.com/serenity-bdd/serenity-core/commit/b4bbc317f98a23a) 130_issue: removing unused dependencies
 * [e28f899a152e798](https://github.com/serenity-bdd/serenity-core/commit/e28f899a152e798) 130_issue: fixing copying jars bug

Conflicts:
	serenity-gradle-plugin/build.gradle
 * [d2c156debf5b8d1](https://github.com/serenity-bdd/serenity-core/commit/d2c156debf5b8d1) 130_issue: removed emtpy lines
 * [920e4cf20364baa](https://github.com/serenity-bdd/serenity-core/commit/920e4cf20364baa) 130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin
 * [59a3cb2a96f7a39](https://github.com/serenity-bdd/serenity-core/commit/59a3cb2a96f7a39) 130_issue: spelling error fix
 * [60b4922421276c5](https://github.com/serenity-bdd/serenity-core/commit/60b4922421276c5) 130_issue: build.config updated for simple project for serenity-gradle_plugin
 * [af55a048e07a081](https://github.com/serenity-bdd/serenity-core/commit/af55a048e07a081) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
 * [0d48d61cf44cc34](https://github.com/serenity-bdd/serenity-core/commit/0d48d61cf44cc34) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
 * [356fbb9f80c6789](https://github.com/serenity-bdd/serenity-core/commit/356fbb9f80c6789) 130_issue: added test and default project for gradle plugin
## v1.1.18-rc.1
### [#160](https://github.com/serenity-bdd/serenity-core/issues/160) Error &quot;Too many open files&quot; with property serenity.take.screenshots=AFTER_EACH_STEP
 * [84d095558dcd615](https://github.com/serenity-bdd/serenity-core/commit/84d095558dcd615) Refactoring of the report generation code to rectify #160
### No issue
 * [6e91a31d3426c4d](https://github.com/serenity-bdd/serenity-core/commit/6e91a31d3426c4d) Revert "Merge branch 'master' of github.com:serenity-bdd/serenity-core"

This reverts commit 4397786f9fd7f37cb6c2e4f00741a2343e9e4d57, reversing
changes made to 84d095558dcd61554c2a0a988977bb1e9eecb71d.
 * [8fedb5437877646](https://github.com/serenity-bdd/serenity-core/commit/8fedb5437877646) Refactoring WebDriver integration to use the ThucydidesWebDriverSupport class
 * [a1979bb7f938344](https://github.com/serenity-bdd/serenity-core/commit/a1979bb7f938344) 130_issue: removed emtpy lines
 * [e8607afe6fb9998](https://github.com/serenity-bdd/serenity-core/commit/e8607afe6fb9998) 130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin
 * [322e572db71d9d6](https://github.com/serenity-bdd/serenity-core/commit/322e572db71d9d6) 130_issue: spelling error fix
 * [28be7ba00561d2f](https://github.com/serenity-bdd/serenity-core/commit/28be7ba00561d2f) 130_issue: build.config updated for simple project for serenity-gradle_plugin
 * [d5883dbac1b97c5](https://github.com/serenity-bdd/serenity-core/commit/d5883dbac1b97c5) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
 * [ee6807e845a9293](https://github.com/serenity-bdd/serenity-core/commit/ee6807e845a9293) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
 * [0c105044830f797](https://github.com/serenity-bdd/serenity-core/commit/0c105044830f797) 130_issue: added test and default project for gradle plugin
 * [c66f0fe7113da4a](https://github.com/serenity-bdd/serenity-core/commit/c66f0fe7113da4a) Fixed an issue with the reporting of pending and skipped tests
 * [d9eca6e184af361](https://github.com/serenity-bdd/serenity-core/commit/d9eca6e184af361) Fixed typo in the smoketests
 * [c497bb6b231cdb7](https://github.com/serenity-bdd/serenity-core/commit/c497bb6b231cdb7) Updating dependencies for the smoketest project
 * [a4dc59d0aa64b2d](https://github.com/serenity-bdd/serenity-core/commit/a4dc59d0aa64b2d) Fixed typo in the smoketests
 * [ad774592904ec3e](https://github.com/serenity-bdd/serenity-core/commit/ad774592904ec3e) Made the instantiation of test steps more robust, mainly for use in the Journey pattern
## v1.1.17-rc.5
### No issue
 * [d2f951a2b282659](https://github.com/serenity-bdd/serenity-core/commit/d2f951a2b282659) Added the serenity.error.on, serenity.fail.on and serenity.pending.on properties to the ThucydidesSystemProperty class.
 * [3c397299c342b15](https://github.com/serenity-bdd/serenity-core/commit/3c397299c342b15) You can use the serenity.pending.on property to define exceptions that will cause a test to be marked as Pending.
 * [032dbb615134963](https://github.com/serenity-bdd/serenity-core/commit/032dbb615134963) Added a general solution for defining or overriding how exceptions should be reported.
## v1.1.17-rc.4
### No issue
 * [5d1b871b917e0c0](https://github.com/serenity-bdd/serenity-core/commit/5d1b871b917e0c0) Fixed an error in the freemarker templates.
 * [271ffe108f5880f](https://github.com/serenity-bdd/serenity-core/commit/271ffe108f5880f) Added support for customising exception handling.

You can now specify your own exceptions that will cause a failure by using the /serenity.fail.on/ property. You can also specify those that will cause an error using /serenity.error.on/.
 * [b2c29a9ed0f6e51](https://github.com/serenity-bdd/serenity-core/commit/b2c29a9ed0f6e51) Improved report icons
 * [c58450593b567a1](https://github.com/serenity-bdd/serenity-core/commit/c58450593b567a1) Fixed some issues related to displaying manual tests
 * [bafaead4743dd9d](https://github.com/serenity-bdd/serenity-core/commit/bafaead4743dd9d) Fixed some broken tests
 * [2e959d4fef6356f](https://github.com/serenity-bdd/serenity-core/commit/2e959d4fef6356f) Fixed some broken tests
 * [5fdc2be5bf86270](https://github.com/serenity-bdd/serenity-core/commit/5fdc2be5bf86270) Refactoring
 * [157c616b2f9b519](https://github.com/serenity-bdd/serenity-core/commit/157c616b2f9b519) Trim Appium system properties
 * [01a6e9d3a51cff8](https://github.com/serenity-bdd/serenity-core/commit/01a6e9d3a51cff8) Improved reporting

Use FontAwesome for more readable test result icons.
 * [7de1dd5ba3fe508](https://github.com/serenity-bdd/serenity-core/commit/7de1dd5ba3fe508) Better error/failure distinction

Exceptions such as ElementShouldBeInvisibleException are now reported as failures, not errors.
 * [2e0752d93e05f48](https://github.com/serenity-bdd/serenity-core/commit/2e0752d93e05f48) Added a more meaningful error message if a resource file cannot be copied.
## v1.1.17-rc.3
### No issue
 * [b16d29a29509c8d](https://github.com/serenity-bdd/serenity-core/commit/b16d29a29509c8d) Refactoring
 * [93f8e34a2747219](https://github.com/serenity-bdd/serenity-core/commit/93f8e34a2747219) Refactoring the html resource copying code.
 * [4d6e9bc104b4a27](https://github.com/serenity-bdd/serenity-core/commit/4d6e9bc104b4a27) Refining support for multi-thread report generation to avoid contention on resource files
 * [60ef04c4b8b46f4](https://github.com/serenity-bdd/serenity-core/commit/60ef04c4b8b46f4) Fix: Do not execute remaining steps after assumption failed
 * [93337a754212f36](https://github.com/serenity-bdd/serenity-core/commit/93337a754212f36) fix: correct unit test
 * [8cff02bf05a93bf](https://github.com/serenity-bdd/serenity-core/commit/8cff02bf05a93bf) fix: stop further steps execution if test is suspended
## v1.1.17-rc.2
### No issue
 * [13edac4e25606fa](https://github.com/serenity-bdd/serenity-core/commit/13edac4e25606fa) Refactoring of a solution to avoid contention on resource JAR files.
 * [fe753a880bb2dff](https://github.com/serenity-bdd/serenity-core/commit/fe753a880bb2dff) Fixed a test
 * [496320a0c08f381](https://github.com/serenity-bdd/serenity-core/commit/496320a0c08f381) Ensure that HTML report resource files are only copied if they are not already present.
 * [f195e492df7618f](https://github.com/serenity-bdd/serenity-core/commit/f195e492df7618f) Ignore warnings if we try to save a screenshot that already exists.
 * [80dc1909929b502](https://github.com/serenity-bdd/serenity-core/commit/80dc1909929b502) Fixed a broken test
## v1.1.17-rc.1
### No issue
 * [64af9acd1d8d9da](https://github.com/serenity-bdd/serenity-core/commit/64af9acd1d8d9da) The withTestDataFrom() method now accepts a list of strings as well as a CSV file.
 * [d49c8b2f488adfe](https://github.com/serenity-bdd/serenity-core/commit/d49c8b2f488adfe) Added smoketest tags to illustrate using tags.
 * [3dddb91bd54347d](https://github.com/serenity-bdd/serenity-core/commit/3dddb91bd54347d) Added a new sample data-driven test to the smoke tests
 * [4f95fd346b7419e](https://github.com/serenity-bdd/serenity-core/commit/4f95fd346b7419e) Removed old screenshot processing logic
 * [577dacf7f777583](https://github.com/serenity-bdd/serenity-core/commit/577dacf7f777583) chore: General test refactoring.
 * [0b94e8daba308bc](https://github.com/serenity-bdd/serenity-core/commit/0b94e8daba308bc) Minor refactoring

Added multi-thread testing for the screenshot pipeline, and removed misleading warnings that could happen when two threads try to save the same screenshot.
 * [e07f25002f7ec85](https://github.com/serenity-bdd/serenity-core/commit/e07f25002f7ec85) fix: Removed a potential issue where the screenshot target directory was not created correctly
 * [ca53d5e75470fef](https://github.com/serenity-bdd/serenity-core/commit/ca53d5e75470fef) chore:test hardening
 * [e1525d38bca5fc9](https://github.com/serenity-bdd/serenity-core/commit/e1525d38bca5fc9) Made the screenshot processing a bit more robust
 * [01e59d1a7199b27](https://github.com/serenity-bdd/serenity-core/commit/01e59d1a7199b27) Fine-tuned the smoke test app
 * [70db76ded080214](https://github.com/serenity-bdd/serenity-core/commit/70db76ded080214) Fine-tuned the smoke test app
 * [929e14da0922d4b](https://github.com/serenity-bdd/serenity-core/commit/929e14da0922d4b) Added additional tests to the smoke test suite
 * [2e315ec39b25021](https://github.com/serenity-bdd/serenity-core/commit/2e315ec39b25021) chore:Refactoring and tidying up the code
 * [2341b7409624050](https://github.com/serenity-bdd/serenity-core/commit/2341b7409624050) [JDK7 compatibility] Corrections to maintain JDK7 compatibility

Replace usage of java Optional with Guava optional
 * [37aa19d2ddef10c](https://github.com/serenity-bdd/serenity-core/commit/37aa19d2ddef10c) Added smoke tests
 * [1b84d2e7d7b33aa](https://github.com/serenity-bdd/serenity-core/commit/1b84d2e7d7b33aa) fix: Handle empty screenshots without crashing.
 * [825328a5cd76f7d](https://github.com/serenity-bdd/serenity-core/commit/825328a5cd76f7d) Added the Todo app smoke tests
 * [f20daf748843c6b](https://github.com/serenity-bdd/serenity-core/commit/f20daf748843c6b) Refactored the screenshot processing logic
 * [395632770ed8fc1](https://github.com/serenity-bdd/serenity-core/commit/395632770ed8fc1) Just trigger rebuild
 * [461c7843410b9f4](https://github.com/serenity-bdd/serenity-core/commit/461c7843410b9f4) fix: Checks if driver is not null (before calling close() )
 * [540ce87d44b93cb](https://github.com/serenity-bdd/serenity-core/commit/540ce87d44b93cb) fix: Adds JAVA_HOME to the environment (map) in case the key / value is
not available from the System.getenv()
 * [9e7e55695924f47](https://github.com/serenity-bdd/serenity-core/commit/9e7e55695924f47) fix: Checks if the driver != null, before calling close() and quit(),
 * [7578ed2ff16cf34](https://github.com/serenity-bdd/serenity-core/commit/7578ed2ff16cf34) fix: Checks if the driver != null, before calling close() and quit(),
 * [666e9dcfd8d8df3](https://github.com/serenity-bdd/serenity-core/commit/666e9dcfd8d8df3) refactor: Removes maven-easyb-plugin, is not used, or correct me if I'm
wrong.
 * [1381ebdaa955146](https://github.com/serenity-bdd/serenity-core/commit/1381ebdaa955146) refactor: Removes warning that log4j was not initialized
Updates thucydides-core with exclusion of log4j
Adds dependency log4j-over-slf4j
 * [6200d4effd5470a](https://github.com/serenity-bdd/serenity-core/commit/6200d4effd5470a) fix: Updates default URL to 'http://www.google.com/ncr' this prevents
redirects from 'google.com' to country specific google search pages.
 * [94cf1d57fad63a1](https://github.com/serenity-bdd/serenity-core/commit/94cf1d57fad63a1) fix: Corrects auto redirect to secure connection (https instead of http)
 * [6148fe2833f5e7f](https://github.com/serenity-bdd/serenity-core/commit/6148fe2833f5e7f) docs: Adds description how to correct add chrome-web-driver
 * [365388539e0dfc3](https://github.com/serenity-bdd/serenity-core/commit/365388539e0dfc3) docs: Adds description about the Serenity Demo
 * [5556ddae9469a43](https://github.com/serenity-bdd/serenity-core/commit/5556ddae9469a43) chore: Adds profiles 'firefox' and 'chrome', for easier running the
tests with different browsers.
 * [a860b0bb5f3b58f](https://github.com/serenity-bdd/serenity-core/commit/a860b0bb5f3b58f) fix: Corrects issue with auto redirect to secure connection (https
instead of http)
 * [7d21048e9a2b3b0](https://github.com/serenity-bdd/serenity-core/commit/7d21048e9a2b3b0) fix: Corrects issue auto forwarding from google.com to google.xxx the
country specific search page.
 * [476a18322150cbb](https://github.com/serenity-bdd/serenity-core/commit/476a18322150cbb) fix: Updates dependencies to latest stable release 0.8
  thucydides-junit 0.8.31 (was 0.8.1-SNAPSHTOT)
  thucydides-core  0.8.31 (was 0.8.1-SNAPSHTOT)
Adds dependency
  slf4j-simple  1.6.4
 * [7974322366574a3](https://github.com/serenity-bdd/serenity-core/commit/7974322366574a3) fix: Brings package name in class inline with the package directory
structure
 * [7b58b52e3c13baa](https://github.com/serenity-bdd/serenity-core/commit/7b58b52e3c13baa) Renames package 'net.serenity_bdd.*' into 'net.serenitybdd.*', to bring
them inline with the rest
 * [33fac2e859d6bc7](https://github.com/serenity-bdd/serenity-core/commit/33fac2e859d6bc7) Removes unused imports
 * [618a81345bdd93c](https://github.com/serenity-bdd/serenity-core/commit/618a81345bdd93c) Removes unused variable registeredWebdriverManagers
 * [b57fce26d9baea8](https://github.com/serenity-bdd/serenity-core/commit/b57fce26d9baea8) Removes unused import
 * [4cf4c1123a50402](https://github.com/serenity-bdd/serenity-core/commit/4cf4c1123a50402) Removes generics warning
 * [8cfa26db66a93f2](https://github.com/serenity-bdd/serenity-core/commit/8cfa26db66a93f2) Removes no longer needed @SuppressWarnings
 * [d370f8441d4d146](https://github.com/serenity-bdd/serenity-core/commit/d370f8441d4d146) Removes generics warnings
 * [4318dc2a8180f75](https://github.com/serenity-bdd/serenity-core/commit/4318dc2a8180f75) Removes warning 'Use static field LoggingLevel.VERBOSE'
 * [99c05b534ed9d49](https://github.com/serenity-bdd/serenity-core/commit/99c05b534ed9d49) Removes @SuppressWarnings, no longer needed
 * [910356643cb2257](https://github.com/serenity-bdd/serenity-core/commit/910356643cb2257) Removes unsued variable
 * [52a42c40edd6b9b](https://github.com/serenity-bdd/serenity-core/commit/52a42c40edd6b9b) Fixes generics issue (no longer warning)
 * [bf415be9811359f](https://github.com/serenity-bdd/serenity-core/commit/bf415be9811359f) Removes unused imports
 * [f38011c61aa04c6](https://github.com/serenity-bdd/serenity-core/commit/f38011c61aa04c6) Corrects javadoc description
 * [dc6c68e81a9031e](https://github.com/serenity-bdd/serenity-core/commit/dc6c68e81a9031e) Simplified a unit test
 * [6c0391deaec315d](https://github.com/serenity-bdd/serenity-core/commit/6c0391deaec315d) Simplified a unit test
 * [20f7f3075e538f3](https://github.com/serenity-bdd/serenity-core/commit/20f7f3075e538f3) Fixed an error in the reporting in the Hit interaction
 * [917ff5d67fbce78](https://github.com/serenity-bdd/serenity-core/commit/917ff5d67fbce78) Fixed project build on Windows
 * [cbfb1788cd8e35a](https://github.com/serenity-bdd/serenity-core/commit/cbfb1788cd8e35a) Fixing Java warnings - Redundant cast

GoogleSearchSteps.java:27: warning: [cast] redundant cast to GoogleHomePage
        GoogleHomePage page = (GoogleHomePage) getPages().currentPageAt(GoogleHomePage.class);
GoogleSearchSteps.java:33: warning: [cast] redundant cast to GoogleResultsPage
        GoogleResultsPage page = (GoogleResultsPage) getPages().currentPageAt(GoogleResultsPage.class);
 * [fc33695ff16e8fb](https://github.com/serenity-bdd/serenity-core/commit/fc33695ff16e8fb) Refactoring tests
 * [8b837b624eae592](https://github.com/serenity-bdd/serenity-core/commit/8b837b624eae592) Upgraded to cucumber-jvm 1.2.4
 * [31b954c14c634fa](https://github.com/serenity-bdd/serenity-core/commit/31b954c14c634fa) Upgraded to cucumber-jvm 1.2.4
 * [1d3b1039c1e1c02](https://github.com/serenity-bdd/serenity-core/commit/1d3b1039c1e1c02) Requirements reporting now support mixing JBehave and JUnit tests.
 * [869b7276a4defa3](https://github.com/serenity-bdd/serenity-core/commit/869b7276a4defa3) Fixed an issue with JBehave where the browser was not closing after tests.
 * [5d78861f5501df9](https://github.com/serenity-bdd/serenity-core/commit/5d78861f5501df9) More consistent breadcrumbs
 * [8797dae10c1fae3](https://github.com/serenity-bdd/serenity-core/commit/8797dae10c1fae3) Minor bug fixes
 * [9c94af955b05e0f](https://github.com/serenity-bdd/serenity-core/commit/9c94af955b05e0f) Added the 'deep.step.execution.after.failures' option

This option (set to false by default), lets you control the execution depth of the @Step methods after a step has failed. If set to true, it will run nested @Step methods as well as top-level ones.
 * [7e82d4f3b315f58](https://github.com/serenity-bdd/serenity-core/commit/7e82d4f3b315f58) Include the name of the exception in error messages
 * [6d3847c93b9c330](https://github.com/serenity-bdd/serenity-core/commit/6d3847c93b9c330) Refactored JSON file loading for better error reporting
 * [8e6e206ac33e52d](https://github.com/serenity-bdd/serenity-core/commit/8e6e206ac33e52d) Added more robust support for running REST tests in DryRun mode.
 * [e32a2a0099755f3](https://github.com/serenity-bdd/serenity-core/commit/e32a2a0099755f3) Improved reporting for tag-related reports
 * [f5fbb68ad3d7b54](https://github.com/serenity-bdd/serenity-core/commit/f5fbb68ad3d7b54) Requirements breadcrumbs for JBehave
 * [1764651aff9b8a7](https://github.com/serenity-bdd/serenity-core/commit/1764651aff9b8a7) Fixed a timeout issue.

Fixed a timeout issue related to using withTimeoutOf(...).waitForElementToDisappear()
 * [bcc83dd04ebc0a8](https://github.com/serenity-bdd/serenity-core/commit/bcc83dd04ebc0a8) Added a utility method to wait for an AngularJS page to load properly.
 * [4b5298def11a998](https://github.com/serenity-bdd/serenity-core/commit/4b5298def11a998) Removed thread leak issue
 * [7a57c62bcb1dff0](https://github.com/serenity-bdd/serenity-core/commit/7a57c62bcb1dff0) Added basic support for the 'dry-run' option.

Rest calls will now be skipped if you activtate 'dry-run' mode (e.g. by setting the 'serenity.dry.run' system property to true).
 * [b3c78559ba2cc36](https://github.com/serenity-bdd/serenity-core/commit/b3c78559ba2cc36) refactoring requirements processing - wip
 * [e64d7a75f03e723](https://github.com/serenity-bdd/serenity-core/commit/e64d7a75f03e723) Improved error reporting.
 * [c14bdf6c5034ed7](https://github.com/serenity-bdd/serenity-core/commit/c14bdf6c5034ed7) Added support for XML REST messages
 * [8ed69d8a271bc4a](https://github.com/serenity-bdd/serenity-core/commit/8ed69d8a271bc4a) Display the overall time taken for the tests
 * [06ccc69767b8287](https://github.com/serenity-bdd/serenity-core/commit/06ccc69767b8287) Moved the screenshot caption to the top of the screenshots to make it easier to see
 * [a66cef5da958b5f](https://github.com/serenity-bdd/serenity-core/commit/a66cef5da958b5f) Updated appium version
 * [400fca7439f69a4](https://github.com/serenity-bdd/serenity-core/commit/400fca7439f69a4) feat: Dropdown.selectByValue()
## v1.1.16
### No issue
 * [632a91a024abd3b](https://github.com/serenity-bdd/serenity-core/commit/632a91a024abd3b) Refactoring
 * [975f25e90c30c25](https://github.com/serenity-bdd/serenity-core/commit/975f25e90c30c25) Updated dependencies
 * [3fc89becc4c606b](https://github.com/serenity-bdd/serenity-core/commit/3fc89becc4c606b) Removed unnused imports
## v1.1.15
### No issue
 * [020fd6fa6e101f6](https://github.com/serenity-bdd/serenity-core/commit/020fd6fa6e101f6) build:refactoring test phase
 * [0068f01d15df7dc](https://github.com/serenity-bdd/serenity-core/commit/0068f01d15df7dc) Deprecated old screenshot processor
 * [92fc1c67d24fb6e](https://github.com/serenity-bdd/serenity-core/commit/92fc1c67d24fb6e) Deprecated old screenshot processor
 * [f4670d119a6c8ec](https://github.com/serenity-bdd/serenity-core/commit/f4670d119a6c8ec) Deprecated old screenshot processor
 * [84e1e5f25cd5bac](https://github.com/serenity-bdd/serenity-core/commit/84e1e5f25cd5bac) Deprecated old screenshot processor
 * [ad43f431f450dac](https://github.com/serenity-bdd/serenity-core/commit/ad43f431f450dac) refactor:fine-tuning build job
 * [60fa70b080f1200](https://github.com/serenity-bdd/serenity-core/commit/60fa70b080f1200) refactor:Better error handling for screenshots
 * [29b129c8ef92e3c](https://github.com/serenity-bdd/serenity-core/commit/29b129c8ef92e3c) refactor:Better error handling for screenshots
 * [9bd7368cbb05b3b](https://github.com/serenity-bdd/serenity-core/commit/9bd7368cbb05b3b) refactor:Better error handling for screenshots
 * [4bfc541c331a6e9](https://github.com/serenity-bdd/serenity-core/commit/4bfc541c331a6e9) refactor:Better error handling for screenshots
 * [f5c5fc76afcc3b0](https://github.com/serenity-bdd/serenity-core/commit/f5c5fc76afcc3b0) refactor:Better error handling for screenshots
 * [0bfbd9578a80bf1](https://github.com/serenity-bdd/serenity-core/commit/0bfbd9578a80bf1) refactor:Removed old screenshot logic
 * [8d77bc49dfa524b](https://github.com/serenity-bdd/serenity-core/commit/8d77bc49dfa524b) fix: Fixed a memory leak.
 * [5e392c973fb53eb](https://github.com/serenity-bdd/serenity-core/commit/5e392c973fb53eb) refactor:Removed old screenshot logic
 * [f84df26505988ea](https://github.com/serenity-bdd/serenity-core/commit/f84df26505988ea) Inital version of a new implementation of the screenshot logic.
 * [91ffac148057562](https://github.com/serenity-bdd/serenity-core/commit/91ffac148057562) Added support for blurring.
 * [252524c9056b047](https://github.com/serenity-bdd/serenity-core/commit/252524c9056b047) Inital version of a new implementation of the screenshot logic.
 * [1599557cf37ab9d](https://github.com/serenity-bdd/serenity-core/commit/1599557cf37ab9d) Added new implementation of the screenshot logic
 * [80913b94b709b8b](https://github.com/serenity-bdd/serenity-core/commit/80913b94b709b8b) chore:Added the chromedriver binary for the Snap-CI builds
 * [cf774f8d24741dc](https://github.com/serenity-bdd/serenity-core/commit/cf774f8d24741dc) refactor: Added better error handling for the actors.
## v1.1.14
### No issue
 * [c7658dfd48b8fdd](https://github.com/serenity-bdd/serenity-core/commit/c7658dfd48b8fdd) Test refactoring
 * [5d16f7a17ee6056](https://github.com/serenity-bdd/serenity-core/commit/5d16f7a17ee6056) Removed the redundant 'Stabie' column in the reports
 * [0f4803e013ed331](https://github.com/serenity-bdd/serenity-core/commit/0f4803e013ed331) Better error reporting for actors in the Journey module.
 * [05a1789883ec4e4](https://github.com/serenity-bdd/serenity-core/commit/05a1789883ec4e4) Improved logging messages
 * [9af3f06f1329590](https://github.com/serenity-bdd/serenity-core/commit/9af3f06f1329590) Better support for BrowserStack capability options
 * [7e181098f94ff32](https://github.com/serenity-bdd/serenity-core/commit/7e181098f94ff32) Added 'feature.file.encoding' system property to specify an encoding of Cucumber files
## v1.1.13
### No issue
 * [d131e1535be9ee2](https://github.com/serenity-bdd/serenity-core/commit/d131e1535be9ee2) Fixed an issue with taking screenshots when using multiple browsers
 * [48305b4dfdf4d31](https://github.com/serenity-bdd/serenity-core/commit/48305b4dfdf4d31) Fixed an issue with the moveTo() PageObject method
 * [1c3eb5e7fa91c9a](https://github.com/serenity-bdd/serenity-core/commit/1c3eb5e7fa91c9a) Fixed an issue that caused tests with multiple actors to report steps out of order.
## v1.1.12
### No issue
 * [89461ce6f7eeb53](https://github.com/serenity-bdd/serenity-core/commit/89461ce6f7eeb53) Additional requirements testing
 * [b843013bf18cb0e](https://github.com/serenity-bdd/serenity-core/commit/b843013bf18cb0e) Updated selenium core
 * [ef0a39f8ad5fd3e](https://github.com/serenity-bdd/serenity-core/commit/ef0a39f8ad5fd3e) Updated selenium to 2.47.1
## v1.1.11
### No issue
 * [65eada22ba2665f](https://github.com/serenity-bdd/serenity-core/commit/65eada22ba2665f) Better support for multiple browser management.
 * [5f69b1bc6d82798](https://github.com/serenity-bdd/serenity-core/commit/5f69b1bc6d82798) Refactored screenshot-related logging to DEBUG
 * [714f2a9025f8efe](https://github.com/serenity-bdd/serenity-core/commit/714f2a9025f8efe) Improved the step message when an actor enteres a value into a field.
## v1.1.10
### No issue
 * [3721d0ab6bd6369](https://github.com/serenity-bdd/serenity-core/commit/3721d0ab6bd6369) Improved log reporting for the Journey pattern.
 * [969c74b4f4838cd](https://github.com/serenity-bdd/serenity-core/commit/969c74b4f4838cd) Improved reporting in the console logging.
 * [66cffe7b361595b](https://github.com/serenity-bdd/serenity-core/commit/66cffe7b361595b) fix: prevent null pointers when generating reports
## v1.1.9
### No issue
 * [9051d518de25ee8](https://github.com/serenity-bdd/serenity-core/commit/9051d518de25ee8) Updated dependencies
## v1.1.8
### [#115](https://github.com/serenity-bdd/serenity-core/issues/115) Report generation hangs
 * [32f488557eea780](https://github.com/serenity-bdd/serenity-core/commit/32f488557eea780) Fixed #115

Only record REST responses for non-binary response types.
### [#122](https://github.com/serenity-bdd/serenity-core/issues/122) Report generation hangs
 * [de2d4287aab991f](https://github.com/serenity-bdd/serenity-core/commit/de2d4287aab991f) Attempt to fix #122
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### Jira
 * [a8ae2e5f4ac22d1](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d1) Debug log: Adding exception to output

Sample output:

    11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException-->null
    net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException-->null
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na]
    	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na]
    	at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5]
    	at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na]
    	at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1]
    	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05]
    	at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1]
    	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1]
    	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na]
### No issue
 * [9bd70c2429c11b7](https://github.com/serenity-bdd/serenity-core/commit/9bd70c2429c11b7) Changed dependencies back to mockito 1.9.5 to avoid dependency conflict issues
 * [45f1eae8491c8d9](https://github.com/serenity-bdd/serenity-core/commit/45f1eae8491c8d9) Test refactoring
 * [1bdbc53a34dba3b](https://github.com/serenity-bdd/serenity-core/commit/1bdbc53a34dba3b) see https://github.com/serenity-bdd/serenity-core/issues/37
 * [c6ed01d846f4646](https://github.com/serenity-bdd/serenity-core/commit/c6ed01d846f4646) Added support for multiple browsers in the same test using the Journey pattern

For example:

    @Managed(driver="chrome")
    WebDriver firstBrowser;

    @Managed(driver="firefox")
    WebDriver anotherBrowser;

    @Test
    public void danaCanUpdateHerProfile() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(TheProfile.name(), equalTo("Dana")));
        and(dana).should(seeThat(TheProfile.country(), equalTo("France")));
 * [9eeabc7e93e6b9b](https://github.com/serenity-bdd/serenity-core/commit/9eeabc7e93e6b9b) Improving logging in ReportService
 * [2f1bf50b655c68a](https://github.com/serenity-bdd/serenity-core/commit/2f1bf50b655c68a) feat: the phantomjs ssl-property can now be set using the PHANTOMJS_SSL_PROPERTY environment variable, like the PHANTOMJS_BINARY_PATH. Possible options are 'sslv2', 'sslv3', 'tlsv1' and 'any'.
## v1.1.7
### No issue
 * [75415857c76fa70](https://github.com/serenity-bdd/serenity-core/commit/75415857c76fa70) Better integration of Actors and Question objects

Actors can now ask questions directly, e.g.

---
Integer totalCost = dana.asksFor(theTotalCost());
---

They can also remember the answers to questions for future use:

---
dana.remember("Total Cost", theTotalCost());
assertThat(dana.recall("Total Cost")).isEqualTo(14);
---
## v1.1.6
### No issue
 * [f77a999a3f1fe3c](https://github.com/serenity-bdd/serenity-core/commit/f77a999a3f1fe3c) Refactoring and better console reporting.
 * [a2089724582c419](https://github.com/serenity-bdd/serenity-core/commit/a2089724582c419) Ensure that Consequence steps are not evaluated after a previous step has failed.

This avoids misleading error messages.
 * [4404ea3c9f3c91b](https://github.com/serenity-bdd/serenity-core/commit/4404ea3c9f3c91b) Refactored the journey demo test

To better illustrate tasks/interaction layers.
 * [f7dc3d73a581aa9](https://github.com/serenity-bdd/serenity-core/commit/f7dc3d73a581aa9) Added support for dropdowns in the interaction-level bundled Performables.
 * [d3d2e38936d3979](https://github.com/serenity-bdd/serenity-core/commit/d3d2e38936d3979) Removed unnecessary warning messages
 * [743ec7b5c289e57](https://github.com/serenity-bdd/serenity-core/commit/743ec7b5c289e57) Revolving dependency conflicts with hamcrest 1.1
 * [3006a4ea233ddc9](https://github.com/serenity-bdd/serenity-core/commit/3006a4ea233ddc9) fix: Improved error reporting for provided drivers

DriverSources may implement some non-trivial logic, so it is very handy
for debugging to include in stack trace exception occurred while tried to
initialize new webdriver. Especially in multi-node test environment
configurations.
 * [63458448580c2b0](https://github.com/serenity-bdd/serenity-core/commit/63458448580c2b0) Removed redundant code that was causing errors in the reports.

If there were more than one given clause in a journey-style test, the initial givens where incorrectly nested.
## v1.1.5
### [#109](https://github.com/serenity-bdd/serenity-core/issues/109) Serenity incompatible with JDK7
 * [b06d96153f9ed78](https://github.com/serenity-bdd/serenity-core/commit/b06d96153f9ed78) Fix #109
### No issue
 * [b7a8e7190bf7486](https://github.com/serenity-bdd/serenity-core/commit/b7a8e7190bf7486) Avoid unnecessary error messages with Java 8 lambdas.
 * [d7af4ed258615c6](https://github.com/serenity-bdd/serenity-core/commit/d7af4ed258615c6) Fixed class conflict issue.
 * [3afdafb4c61939c](https://github.com/serenity-bdd/serenity-core/commit/3afdafb4c61939c) Improved reporting of questions
 * [08c6aab6d2797bc](https://github.com/serenity-bdd/serenity-core/commit/08c6aab6d2797bc) Renamed 'serenity-ability-to-browse-the-web' to 'browse-the-web'
 * [ed62f9307eb62e7](https://github.com/serenity-bdd/serenity-core/commit/ed62f9307eb62e7) Added the "ability-to-browse-the-web" module to the journey module.
 * [8bec974f768f3c8](https://github.com/serenity-bdd/serenity-core/commit/8bec974f768f3c8) Fix inject Pages in super class
 * [cfb3bd63f86fd3f](https://github.com/serenity-bdd/serenity-core/commit/cfb3bd63f86fd3f) Check for existence of the angular object.
 * [d64e692974cc952](https://github.com/serenity-bdd/serenity-core/commit/d64e692974cc952) Refactoring build
## v1.1.4
### [#103](https://github.com/serenity-bdd/serenity-core/issues/103) Improve readability of &quot;View stack trace&quot; dialog
 * [cd553eb90d77547](https://github.com/serenity-bdd/serenity-core/commit/cd553eb90d77547) Improve readability of "View stack trace" dialog (#103)
### [#106](https://github.com/serenity-bdd/serenity-core/issues/106) Double registration BaseStepListener
 * [7f8f10c771f3dba](https://github.com/serenity-bdd/serenity-core/commit/7f8f10c771f3dba) Fix issue #106
### No issue
 * [d29c52eeab26550](https://github.com/serenity-bdd/serenity-core/commit/d29c52eeab26550) Minor refactoring
 * [65da5169c348795](https://github.com/serenity-bdd/serenity-core/commit/65da5169c348795) Added the Question as a concept.
 * [7848a32e8265882](https://github.com/serenity-bdd/serenity-core/commit/7848a32e8265882) Test refactoring
 * [7ae80bfa70188b9](https://github.com/serenity-bdd/serenity-core/commit/7ae80bfa70188b9) Test refactoring
 * [9722c9025f24bef](https://github.com/serenity-bdd/serenity-core/commit/9722c9025f24bef) You can now include assertions in the tests

You can now include assertions in the tests and reports using the Consequence class.
 * [eeb1f7353ea5466](https://github.com/serenity-bdd/serenity-core/commit/eeb1f7353ea5466) Works for nested failing tasks
 * [5ee8c7e4cd29642](https://github.com/serenity-bdd/serenity-core/commit/5ee8c7e4cd29642) Report task steps correctly when an error occurs in a task step
 * [9e49f8c1d500cc2](https://github.com/serenity-bdd/serenity-core/commit/9e49f8c1d500cc2) Got successful and failing journey scenarios working, as long as the failing assertion is in the performAs method. Currently, if one of the chained methods fails, the following steps are not executed and the results are unpredictable
 * [95aa4af410f5c73](https://github.com/serenity-bdd/serenity-core/commit/95aa4af410f5c73) You can now use #this or #self in a @Step description.
 * [968c4030467adf7](https://github.com/serenity-bdd/serenity-core/commit/968c4030467adf7) Better treatment of invalid or undefined fields in step definitions.
 * [7755f2efdada037](https://github.com/serenity-bdd/serenity-core/commit/7755f2efdada037) Simplified variable injection into step descriptions.

You can now inject any member variable in the step class by name into step descriptions

You can now use member variables of a step library in the @Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a "#", as shown in this example:
----
    private final String siteName = "Etsy";

    @Step("Search for a shop called {0} on the #siteName website")
    public void searches_for_shop_called(String shopName) {
        homePage.searchForShopCalled(shopName);
    }
----
 * [f353c363ae54e52](https://github.com/serenity-bdd/serenity-core/commit/f353c363ae54e52) Refactoring
 * [9b7c6025d8fef90](https://github.com/serenity-bdd/serenity-core/commit/9b7c6025d8fef90) Inject step variables by name into step descriptions

You can now use member variables of a step library in the @Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a "#", as shown in this example:
----
    @Reported
    private final String siteName = "Etsy";

    @Step("Search for a shop called {0} on the #siteName website")
    public void searches_for_shop_called(String shopName) {
        homePage.searchForShopCalled(shopName);
    }
----
 * [c0b0fffc15caa85](https://github.com/serenity-bdd/serenity-core/commit/c0b0fffc15caa85) Added support for TypeSafeConfig

You can now provide a TypeSafeConfig configuration file (https://github.com/typesafehub/config) instead of a serenity.properties file. The file can be called 'serenity.conf' or any of the other Type Safe Config configuration files names.

The configuration file can contain both Serenity variables and other arbitrary variables, which will be available in the EnvironmentVariables field. For example, a simple configuration file might look like this.
----
serenity {
  logging = DEBUG
}
environment {
   uat = uat-server
}
----
 * [e50e5578683e887](https://github.com/serenity-bdd/serenity-core/commit/e50e5578683e887) Refactored a unit test for more clarity
## v1.1.3
### No issue
 * [71f56847e32e7a9](https://github.com/serenity-bdd/serenity-core/commit/71f56847e32e7a9) Fixed an issue causing the drivers to be closed incorrectly during parallel tests
## v1.1.2
### No issue
 * [6aae0d4539b3142](https://github.com/serenity-bdd/serenity-core/commit/6aae0d4539b3142) Added the 'uniqueInstance' attribute to the @Steps annotation to support multiple instances of the same step library in the same class
 * [c4033eaa90b53ff](https://github.com/serenity-bdd/serenity-core/commit/c4033eaa90b53ff) Fixed colors in some of the reports causing text to be light grey on white
## v1.1.1
### No issue
 * [4c85801565208d6](https://github.com/serenity-bdd/serenity-core/commit/4c85801565208d6) Updated unit tests for manual tests
 * [b8190993a962b85](https://github.com/serenity-bdd/serenity-core/commit/b8190993a962b85) Added support for manual annotated tests
 * [312a8ec943d0094](https://github.com/serenity-bdd/serenity-core/commit/312a8ec943d0094) Fixed an issue that caused broken links in JUnit and Cucumber requirements reports
 * [30c89b8286c0378](https://github.com/serenity-bdd/serenity-core/commit/30c89b8286c0378) Adding support for manual tests
## v1.1.0
### No issue
 * [26c716c69e9a2ee](https://github.com/serenity-bdd/serenity-core/commit/26c716c69e9a2ee) Simplified some redundant tests
 * [d4222f315f744b6](https://github.com/serenity-bdd/serenity-core/commit/d4222f315f744b6) JSON requirements files are now stored in a dedicated 'requirements' directory.
 * [3fd16d7b62a7770](https://github.com/serenity-bdd/serenity-core/commit/3fd16d7b62a7770) Minor refactoring
 * [c2e028ca2847e22](https://github.com/serenity-bdd/serenity-core/commit/c2e028ca2847e22) Update BrowserStackRemoteDriverCapabilities.java
## v1.0.64
### No issue
 * [21b96f814fd53d6](https://github.com/serenity-bdd/serenity-core/commit/21b96f814fd53d6) fix: Improved error messages for remote drivers

Better error message reporting if a remote driver is incorrectly configured, and some minor refactoring.
 * [404d3bf2f6bdec1](https://github.com/serenity-bdd/serenity-core/commit/404d3bf2f6bdec1) chore:reinstated Bintray plugin
## v1.0.62
### No issue
 * [85c600129e4c94b](https://github.com/serenity-bdd/serenity-core/commit/85c600129e4c94b) chore:Updating release plugins
 * [5a907598c5f0f06](https://github.com/serenity-bdd/serenity-core/commit/5a907598c5f0f06) chore: Removed unnecessary wrapper directories
## v1.0.61
### No issue
 * [0fbada91e9c3ccf](https://github.com/serenity-bdd/serenity-core/commit/0fbada91e9c3ccf) chore:Updating the version of the gradle-git plugin
 * [197d00981b482a5](https://github.com/serenity-bdd/serenity-core/commit/197d00981b482a5) chore:Removed dependency on bintray plugin.
## v1.0.59
### [#87](https://github.com/serenity-bdd/serenity-core/issues/87) Accessing FindBy field after step failure still waits for timeout
 * [6f1628bc721463c](https://github.com/serenity-bdd/serenity-core/commit/6f1628bc721463c) Fixed #87
### No issue
 * [395a9a64e193991](https://github.com/serenity-bdd/serenity-core/commit/395a9a64e193991) Fixes unit tests - nullpointer exception fix when system property webdriver.remote.driver is not set
 * [cca6cd03b475dbd](https://github.com/serenity-bdd/serenity-core/commit/cca6cd03b475dbd) Fix for setting up the remote webdriver capability: webdriver.remote.browser.version
## v1.0.58
### [#79](https://github.com/serenity-bdd/serenity-core/issues/79) Serenity BDD report shows errors with JUnit @Test(expected=...)
 * [5b16d86c86702cd](https://github.com/serenity-bdd/serenity-core/commit/5b16d86c86702cd) Fixed #79
### [#81](https://github.com/serenity-bdd/serenity-core/issues/81) Serenity fails to substitute placeholders, e.g. {0} when content contains an inner class
 * [8e8b01a5fd49895](https://github.com/serenity-bdd/serenity-core/commit/8e8b01a5fd49895) Fixeds #81
### No issue
 * [b06c39978f06dad](https://github.com/serenity-bdd/serenity-core/commit/b06c39978f06dad) Refactored tests
## v1.0.57
### No issue
 * [d3777295f2efd74](https://github.com/serenity-bdd/serenity-core/commit/d3777295f2efd74) Performance improvements
 * [54529c12066cd60](https://github.com/serenity-bdd/serenity-core/commit/54529c12066cd60) Fixed timeout issues with waitForAbsence* methods
## v1.0.56
### No issue
 * [4350090ac6ec609](https://github.com/serenity-bdd/serenity-core/commit/4350090ac6ec609) Fixed an issue with reporting RestAssured assertion failures.
## v1.0.54
### No issue
 * [fb614fb2bc5bce0](https://github.com/serenity-bdd/serenity-core/commit/fb614fb2bc5bce0) Fixed some timout issues
 * [75047a1b8d6fcda](https://github.com/serenity-bdd/serenity-core/commit/75047a1b8d6fcda) Handle null-value parameters more elegantly.
 * [7bc74def0bf22fe](https://github.com/serenity-bdd/serenity-core/commit/7bc74def0bf22fe) feat: Added support for Spring meta-annotations for @ContextConfiguration and @ContextHierarchy
 * [05c4283ddeedcc9](https://github.com/serenity-bdd/serenity-core/commit/05c4283ddeedcc9) feat: Add support for Spring @ContextHierarchy annotations
## v1.0.53
### [#66](https://github.com/serenity-bdd/serenity-core/issues/66) Serenity BDD can&#39;t deal with recursive POJOs
 * [259741557e10c1f](https://github.com/serenity-bdd/serenity-core/commit/259741557e10c1f) Handle recursive parameters correctly (#66)
### [#71](https://github.com/serenity-bdd/serenity-core/issues/71) Serenity throws ClassCastException on a generic method
 * [2a4a06f28a3a92b](https://github.com/serenity-bdd/serenity-core/commit/2a4a06f28a3a92b) Better reporting of exceptions (fixes #71)
### No issue
 * [4aee7bb76735218](https://github.com/serenity-bdd/serenity-core/commit/4aee7bb76735218) Temporarily disable some tests with environment-specific issues
 * [778ea0699595172](https://github.com/serenity-bdd/serenity-core/commit/778ea0699595172) More refactoring tests
 * [11b84b726006eff](https://github.com/serenity-bdd/serenity-core/commit/11b84b726006eff) More refactoring tests
 * [1a4268c8749d0f6](https://github.com/serenity-bdd/serenity-core/commit/1a4268c8749d0f6) More refactoring tests
 * [45d11b56f149692](https://github.com/serenity-bdd/serenity-core/commit/45d11b56f149692) Refactoring tests
 * [1345adf38741118](https://github.com/serenity-bdd/serenity-core/commit/1345adf38741118) Refactoring tests
 * [1e636a6e0192ade](https://github.com/serenity-bdd/serenity-core/commit/1e636a6e0192ade) Refactoring tests
 * [eed9a7c1b837c4d](https://github.com/serenity-bdd/serenity-core/commit/eed9a7c1b837c4d) Refactoring tests
 * [6421a4ccd0b4964](https://github.com/serenity-bdd/serenity-core/commit/6421a4ccd0b4964) Added better support for reporting exceptions

We now support reporting exceptions with a zero-parameter constructor as well as a single-parameter constructor.
## v1.0.52
### [#69](https://github.com/serenity-bdd/serenity-core/issues/69) Failed to copy the screenshot to the destination directory
 * [0a7fdeacf6a3f4a](https://github.com/serenity-bdd/serenity-core/commit/0a7fdeacf6a3f4a) Modifying screenshot code to work better with Windows (see #69)
 * [b184b84b1d9701e](https://github.com/serenity-bdd/serenity-core/commit/b184b84b1d9701e) Modifying screenshot code to work better with Windows (see #69)
## v1.0.51
### [#69](https://github.com/serenity-bdd/serenity-core/issues/69) Failed to copy the screenshot to the destination directory
 * [d0d500c06511a93](https://github.com/serenity-bdd/serenity-core/commit/d0d500c06511a93) Attempt to fix #69

Issue #69 looks like an OS/filesystem-specific issue related to Java 7 atomic copies. This version uses REPLACE_EXISTING instead of ATOMIC_MOVE.
### No issue
 * [a100646a8697ec4](https://github.com/serenity-bdd/serenity-core/commit/a100646a8697ec4) Updated to Selenium 2.46.0
 * [dac9fead420ecb8](https://github.com/serenity-bdd/serenity-core/commit/dac9fead420ecb8) Resolved dependency conflict
 * [5dfcc1a4ec89a09](https://github.com/serenity-bdd/serenity-core/commit/5dfcc1a4ec89a09) Updated to Selenium 2.46.0
 * [6e7255dc2c22bfc](https://github.com/serenity-bdd/serenity-core/commit/6e7255dc2c22bfc) Deprecate SpringIntegration.
Add SpringIntegrationMethodRule and SpringIntegrationClassRule, as well as the utility runner SpringIntegrationSerenityRunner, which conveniently automatically adds the aforementioned rules.
(Note that some of the main code and test code for the above new classes were originally written in Java 8 and used method references, lambdas and java.util.function.*. To get Java 7 support, these has been replaced by interfaces and anonymous inner classes, but if the project ever moves to Java 8, it is recommended that these are replaced once again).
 * [e38147cc49d8c86](https://github.com/serenity-bdd/serenity-core/commit/e38147cc49d8c86) Moved the tests in serenity-junit that depended on serenity-spring, into serenity-spring, so serenity-junit no longer depends on serenity-spring.
This is in preparation for an upcoming commit - Not doing this would cause a circular dependency between serenity-junit and serenity-spring.
## v1.0.50
### No issue
 * [6edddb2d16a2796](https://github.com/serenity-bdd/serenity-core/commit/6edddb2d16a2796) Fixed incorrect timeout issue
 * [a5c86db27b7657e](https://github.com/serenity-bdd/serenity-core/commit/a5c86db27b7657e) Refactored unit tests for more clarity
## v1.0.49
### No issue
 * [08aba9be2f264cc](https://github.com/serenity-bdd/serenity-core/commit/08aba9be2f264cc) Fixed a minor formatting issue for JBehave embedded tables.
## v1.0.48
### [#61](https://github.com/serenity-bdd/serenity-core/issues/61) Serenity can&#39;t work with remote appium server cause of appium.app property check
 * [28b9b7bfd02d007](https://github.com/serenity-bdd/serenity-core/commit/28b9b7bfd02d007) Fixed #61: issue with path checks on remote appium server
### [#64](https://github.com/serenity-bdd/serenity-core/issues/64) reset implicitly timeout issue. **CRITICAL**
 * [a47b0aea9f58fe1](https://github.com/serenity-bdd/serenity-core/commit/a47b0aea9f58fe1) Fixed #64: issue with resetting implicit timeouts
### [#65](https://github.com/serenity-bdd/serenity-core/issues/65) Screenshots are being stored in /tmp folder on Jenkins slaves taking up lots of space
 * [d4404c10e741ea0](https://github.com/serenity-bdd/serenity-core/commit/d4404c10e741ea0) Fixed #65 - temporary screenshots not deleted
### Jira
 * [a2d03c8dbf1fcb0](https://github.com/serenity-bdd/serenity-core/commit/a2d03c8dbf1fcb0) Fix for THUCYDIDES-253
### No issue
 * [2e74fdf36e9c37c](https://github.com/serenity-bdd/serenity-core/commit/2e74fdf36e9c37c) Simplified some tests
 * [ebb84712cf92e62](https://github.com/serenity-bdd/serenity-core/commit/ebb84712cf92e62) Refactoring some old multi-select code
 * [bf01941c99a7857](https://github.com/serenity-bdd/serenity-core/commit/bf01941c99a7857) Refactoring multiple select code
 * [8835d57485f46a8](https://github.com/serenity-bdd/serenity-core/commit/8835d57485f46a8) Harmonized test data
 * [95f84a18ec27e43](https://github.com/serenity-bdd/serenity-core/commit/95f84a18ec27e43) JUnit tests using the 'expected' attribute are not supported.
 * [0dd7d28dbc0a255](https://github.com/serenity-bdd/serenity-core/commit/0dd7d28dbc0a255) Fixed a bug related to deriving requirements structures from Cucumber feature files.
 * [35250bdb902423f](https://github.com/serenity-bdd/serenity-core/commit/35250bdb902423f) Made the reporting a bit more robust

Correctly cater for exceptions without an error message.
 * [ad83c2afa1652ac](https://github.com/serenity-bdd/serenity-core/commit/ad83c2afa1652ac) Display full screenshots in the slideshow view.
 * [23af5a66522cc35](https://github.com/serenity-bdd/serenity-core/commit/23af5a66522cc35) Added basic support for RestAssured.

You can now add the serenity-rest-assured module to have tight integration with Rest Assured for testing REST web services. Serenity provides a wrapper around the RestAssured methods that reports on the REST queries sent and the responses recieved. Use the SerenityRest.rest() method as a starting point, e.g.

````
rest().given().contentType("application/json").content(jsonPet).post("http://petstore.swagger.io/v2/pet");
rest().get("http://petstore.swagger.io/v2/pet/{id}", pet.getId()).then().statusCode(200)
                                                                                              .and().body("name", equalTo(pet.getName()));
````
 * [f0952b4b4245ef2](https://github.com/serenity-bdd/serenity-core/commit/f0952b4b4245ef2) Renamed 'core' to 'serenity-core'
 * [6ab197f15acdd0f](https://github.com/serenity-bdd/serenity-core/commit/6ab197f15acdd0f) Inject EnvironmentVariables fields in PageObjects
 * [9ba8c65f64e1346](https://github.com/serenity-bdd/serenity-core/commit/9ba8c65f64e1346) Minor formatting fixes in the reports.
## v1.0.47
### [#49](https://github.com/serenity-bdd/serenity-core/issues/49) sysinfo for build report doesn&#39;t support values with spaces
 * [f1ebd7ab77f7b60](https://github.com/serenity-bdd/serenity-core/commit/f1ebd7ab77f7b60) Fixed #49 - sysinfo for build report doesn't support values with spaces
### No issue
 * [d296863429cfdab](https://github.com/serenity-bdd/serenity-core/commit/d296863429cfdab) Support for detection of feature file directories.
 * [897226ef47f7828](https://github.com/serenity-bdd/serenity-core/commit/897226ef47f7828) Added containsElements() methods to the PageObject class.
 * [ba4153da4c89e33](https://github.com/serenity-bdd/serenity-core/commit/ba4153da4c89e33) Improved automatic detection of file-system requirements hierarchies.

Looks for JBehave or Cucumber feature files and derives the
corresponding requirements structure based on the depth of the
requirements directories.
 * [ac5ff92cbc9e82e](https://github.com/serenity-bdd/serenity-core/commit/ac5ff92cbc9e82e) Ensure that the correct stack trace is displayed in the reports
 * [81fd9206bafb849](https://github.com/serenity-bdd/serenity-core/commit/81fd9206bafb849) Removed system properties from the JUnit results to save space.
 * [39d059a4b103ea1](https://github.com/serenity-bdd/serenity-core/commit/39d059a4b103ea1) Added a hasClass() method to the WebElementFacade

This method is a convenient way to check whether a web element has a
particular CSS class.
 * [b4fbf0017c6b530](https://github.com/serenity-bdd/serenity-core/commit/b4fbf0017c6b530) Fixed a bug that caused the screenshots to not always be taken correctly.
 * [ef5aebc607c0df5](https://github.com/serenity-bdd/serenity-core/commit/ef5aebc607c0df5) Fixed a bug in the JUnit parameterized reports

Ensure that errors are correctly reported in JUnit parameterized reports.
 * [faabd32b9da94de](https://github.com/serenity-bdd/serenity-core/commit/faabd32b9da94de) Refactoring and improving the JUnit test reports
 * [17466a54578f563](https://github.com/serenity-bdd/serenity-core/commit/17466a54578f563) Display the stack trace for failing tests in the reports
 * [2c7b976d4323e39](https://github.com/serenity-bdd/serenity-core/commit/2c7b976d4323e39) Fixed a bug preventing requirements to be loaded from the filesystem with JBehave.
 * [bdc3c68d6bf902c](https://github.com/serenity-bdd/serenity-core/commit/bdc3c68d6bf902c) Serenity now generates JUnit-compatible XML reports.

These reports have the SERENITY-JUNIT prefix.
 * [036b26b9ae631f4](https://github.com/serenity-bdd/serenity-core/commit/036b26b9ae631f4) Refactoring some tests
 * [b810e490feb06ac](https://github.com/serenity-bdd/serenity-core/commit/b810e490feb06ac) Improved the consistency of requirements reporting for JUnit tests.
 * [da5c2b71bef30c1](https://github.com/serenity-bdd/serenity-core/commit/da5c2b71bef30c1) Fixed an issue where the tests hang if you call Javascript after a failing step.
 * [a549ef00739f185](https://github.com/serenity-bdd/serenity-core/commit/a549ef00739f185) Fixed a bug where tests hung if an invalid selector was used in a waitFor expression.
 * [27abd5ccdb9869e](https://github.com/serenity-bdd/serenity-core/commit/27abd5ccdb9869e) Improved error reporting and performance.
 * [d5dfc1ea82b7954](https://github.com/serenity-bdd/serenity-core/commit/d5dfc1ea82b7954) Fixed an issue that prevented screenshots from being taken correctly in Cucumber scenarios
 * [6bb343b89f02f85](https://github.com/serenity-bdd/serenity-core/commit/6bb343b89f02f85) Refactored some unit tests
 * [ace9f68089764dd](https://github.com/serenity-bdd/serenity-core/commit/ace9f68089764dd) Fixed bug that prevents the arrow keys working in the screenshot slideshow on webkit browsers
 * [3414969576f7c76](https://github.com/serenity-bdd/serenity-core/commit/3414969576f7c76) Only process a new screenshot if an existing one doesn't already exist.
 * [7d969e88d54fc0c](https://github.com/serenity-bdd/serenity-core/commit/7d969e88d54fc0c) Allow EnvironmentVariables and SystemConfiguration fields to be
injected into JUnit tests.
 * [413d8398ede5b58](https://github.com/serenity-bdd/serenity-core/commit/413d8398ede5b58) Reformatting and tidying up imports
 * [66d334368720385](https://github.com/serenity-bdd/serenity-core/commit/66d334368720385) Fixed formatting error in the screenshots
## v1.0.46
### No issue
 * [657232a147988a8](https://github.com/serenity-bdd/serenity-core/commit/657232a147988a8) Test refactoring
 * [137c534cdfb84b0](https://github.com/serenity-bdd/serenity-core/commit/137c534cdfb84b0) Unit test refactoring
 * [c8ab82bc59925ba](https://github.com/serenity-bdd/serenity-core/commit/c8ab82bc59925ba) General refactoring
 * [5705bc2b8287608](https://github.com/serenity-bdd/serenity-core/commit/5705bc2b8287608) Support Cucumber feature files written in other languages.
 * [8fb2e5e5e0b8204](https://github.com/serenity-bdd/serenity-core/commit/8fb2e5e5e0b8204) Better error reporting for errors around the @DefaultUrl definitions for Page Objects.
 * [5205c75874d5344](https://github.com/serenity-bdd/serenity-core/commit/5205c75874d5344) Added better error reporting if a groovy expression in the build info properties was incorrect

Better error handling for Groovy expressions used in the “sysinfo.*”
system properties that appear in the build info page.
## v1.0.45
### [#41](https://github.com/serenity-bdd/serenity-core/issues/41) Distinction among Serenity Web Test (Selenium) and Serenity Non-Web Test
 * [42884b1685a5b2d](https://github.com/serenity-bdd/serenity-core/commit/42884b1685a5b2d) Don't display the browser icon for non-web tests.

Distinguish among Serenity Web Tests (Selenium) and Serenity Non-Web Test (#41)
### No issue
 * [7848aaa63808598](https://github.com/serenity-bdd/serenity-core/commit/7848aaa63808598) Refactoring and improving the unit tests.
 * [7586862312bb43a](https://github.com/serenity-bdd/serenity-core/commit/7586862312bb43a) Support for PhantomJS 1.2.1
 * [d054c41b4ff58b9](https://github.com/serenity-bdd/serenity-core/commit/d054c41b4ff58b9) Fixed issue with uploading files from the Windows file system.
 * [c16591002d6d237](https://github.com/serenity-bdd/serenity-core/commit/c16591002d6d237) feat: added the possiblity to wait for a CSS or XPath expression from a chained expression.
 * [daacd77e56937db](https://github.com/serenity-bdd/serenity-core/commit/daacd77e56937db) feat: Custom build properties are reported in a more human-readable way in the build info screen.
 * [4e17cef7fc39428](https://github.com/serenity-bdd/serenity-core/commit/4e17cef7fc39428) Added the 'feature.file.language' to support I18N feature files

Narrative text can now be read from non-English feature files, by setting the 'feature.file.language' system property.
 * [e7ae87e1fd29953](https://github.com/serenity-bdd/serenity-core/commit/e7ae87e1fd29953) Fix problem with uploading file on Windows. Changed creation of file path (if file in classpath)
 * [2ec6dd245337d4a](https://github.com/serenity-bdd/serenity-core/commit/2ec6dd245337d4a) ensure unused threads are terminated and removed from executor pool
## v1.0.44
### No issue
 * [e5d04ef4380fc77](https://github.com/serenity-bdd/serenity-core/commit/e5d04ef4380fc77) feat: Added a total time in the test results report.
 * [5e446cb122eb203](https://github.com/serenity-bdd/serenity-core/commit/5e446cb122eb203) feat: Added a total time in the test results report.
 * [3838821d026b929](https://github.com/serenity-bdd/serenity-core/commit/3838821d026b929) feat: You can now automatically inject EnvironmentVariables and Configuration variables into your step libraries, simply by declaring a variable of the corresponding type.
## v1.0.43
### No issue
 * [85f58025c933967](https://github.com/serenity-bdd/serenity-core/commit/85f58025c933967) Changed the default page size for the test results to 50.
## v1.0.42
### No issue
 * [33ff1a16031cb98](https://github.com/serenity-bdd/serenity-core/commit/33ff1a16031cb98) Allows explicit waits on web elements in a page

For example:
 withTimeoutOf(5, TimeUnit.SECONDS).waitFor(facebookIcon).click()
## v1.0.41
### No issue
 * [b497a1db7698833](https://github.com/serenity-bdd/serenity-core/commit/b497a1db7698833) Implemented the timeoutInSeconds attribute on the FindBy annotation.
 * [d8ccfdabf6a5952](https://github.com/serenity-bdd/serenity-core/commit/d8ccfdabf6a5952) Implemented the timeoutInSeconds attribute on the FindBy annotation.
## v1.0.40
### No issue
 * [e7235f713c2d379](https://github.com/serenity-bdd/serenity-core/commit/e7235f713c2d379) Refactored wait logic to use distinct values for implicit waits and wait-for waits.
 * [0fa63e2aba5951d](https://github.com/serenity-bdd/serenity-core/commit/0fa63e2aba5951d) Added containsElements() and shouldContainElements() methods to WebElementFacade
 * [9d9c5a4b6a19c4f](https://github.com/serenity-bdd/serenity-core/commit/9d9c5a4b6a19c4f) Added a convenience method to allow more fluent waitFor() constructs
## v1.0.39
### No issue
 * [ac3de4e6f2f409b](https://github.com/serenity-bdd/serenity-core/commit/ac3de4e6f2f409b) tests: hardeding the timeout tests
 * [b29b7cc6ce97ee9](https://github.com/serenity-bdd/serenity-core/commit/b29b7cc6ce97ee9) feature: Added support for a waitUntilClickable() method on web elements
 * [82d1ab1c848b7d3](https://github.com/serenity-bdd/serenity-core/commit/82d1ab1c848b7d3) tests: test hardening
 * [2eca74a07bd6db6](https://github.com/serenity-bdd/serenity-core/commit/2eca74a07bd6db6) fix: Fixed a bug in reading the requirements from the file system.
 * [9a6c99dcc3e1949](https://github.com/serenity-bdd/serenity-core/commit/9a6c99dcc3e1949) fix: Fixed a bug in reading the requirements from the file system.
 * [38280276b961e60](https://github.com/serenity-bdd/serenity-core/commit/38280276b961e60) fix: Fixed a bug in reading the requirements from the file system.
 * [73ce792b29c26b4](https://github.com/serenity-bdd/serenity-core/commit/73ce792b29c26b4) Removed redundant test
 * [dbddf6df434355f](https://github.com/serenity-bdd/serenity-core/commit/dbddf6df434355f) test: Temporarily disabling a test that doesn't work on the build server pending further investigation
 * [5a46d718876a96a](https://github.com/serenity-bdd/serenity-core/commit/5a46d718876a96a) test: Use phantomjs to check implicit timeouts more realisticly
 * [204900f5f48211a](https://github.com/serenity-bdd/serenity-core/commit/204900f5f48211a) Rewrote much of the timeout APIs
 * [9e653329dd691a5](https://github.com/serenity-bdd/serenity-core/commit/9e653329dd691a5) Added tests to doument implicit wait behavior
## v1.0.38
### No issue
 * [0c23f3a8c26b06e](https://github.com/serenity-bdd/serenity-core/commit/0c23f3a8c26b06e) test: Temporarily disabling a test that doesn't work on the build server pending further investigation
 * [536bfdf46cac5b0](https://github.com/serenity-bdd/serenity-core/commit/536bfdf46cac5b0) test: Use phantomjs to check implicit timeouts more realisticly
 * [4bfdb9133300e0e](https://github.com/serenity-bdd/serenity-core/commit/4bfdb9133300e0e) test: Added sample JSON test data
 * [219441fb70fb4b9](https://github.com/serenity-bdd/serenity-core/commit/219441fb70fb4b9) test: Added sample JSON test data
 * [cd09406dcb34a96](https://github.com/serenity-bdd/serenity-core/commit/cd09406dcb34a96) Added test data for a sample pending report
 * [c191b5a2b3d0a8c](https://github.com/serenity-bdd/serenity-core/commit/c191b5a2b3d0a8c) Added test data for a sample pending report
 * [66801ffbbc769e4](https://github.com/serenity-bdd/serenity-core/commit/66801ffbbc769e4) Added test data for a sample pending report
 * [ac60be617fe6dd1](https://github.com/serenity-bdd/serenity-core/commit/ac60be617fe6dd1) Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file.
 * [a6d6cc62c576351](https://github.com/serenity-bdd/serenity-core/commit/a6d6cc62c576351) Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file.
 * [d2a20188ab24fd4](https://github.com/serenity-bdd/serenity-core/commit/d2a20188ab24fd4) Update WhenLoadingTestDataFromACSVFile.java

Added all possible parameters for CSVReader to be able to parse special chars like \n \t ...
 * [8043809da640f99](https://github.com/serenity-bdd/serenity-core/commit/8043809da640f99) Update WhenLoadingTestDataFromACSVFile.java

Added all possible parameters for CSVReader to be able to parse special chars like \n \t ...
 * [df893d20c253dbc](https://github.com/serenity-bdd/serenity-core/commit/df893d20c253dbc) Update CSVTestDataSource.java

Added all possible parameters for CSVReader to be able to parse special chars like \n \t ...
 * [aa1c3ed0fa00fc7](https://github.com/serenity-bdd/serenity-core/commit/aa1c3ed0fa00fc7) Update CSVTestDataSource.java

Added all possible parameters for CSVReader to be able to parse special chars like \n \t ...
 * [cd9d78657ed78ab](https://github.com/serenity-bdd/serenity-core/commit/cd9d78657ed78ab) Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts.
 * [326a643ba2bcb0d](https://github.com/serenity-bdd/serenity-core/commit/326a643ba2bcb0d) Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts.
## v1.0.37
### No issue
 * [b3d38fb9a30fd74](https://github.com/serenity-bdd/serenity-core/commit/b3d38fb9a30fd74) test:Made a unit test more readable
 * [403003dfbac409d](https://github.com/serenity-bdd/serenity-core/commit/403003dfbac409d) Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy
 * [9d25a1aa46fcfe3](https://github.com/serenity-bdd/serenity-core/commit/9d25a1aa46fcfe3) Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy
 * [fe952b944bf628d](https://github.com/serenity-bdd/serenity-core/commit/fe952b944bf628d) Fixed an issue that had broken the async timeout behavior in the setScriptTimeout() method
## v1.0.36
### No issue
 * [d21e03e66f56d86](https://github.com/serenity-bdd/serenity-core/commit/d21e03e66f56d86) Standardized the Groovy version used throughout the build to 2.3.6
 * [e8c1a874c9030db](https://github.com/serenity-bdd/serenity-core/commit/e8c1a874c9030db) Updated to Selenium 2.45.0
 * [71fcf22e7fe7693](https://github.com/serenity-bdd/serenity-core/commit/71fcf22e7fe7693) test: Refactored a few tests to reduce sporadic errors
 * [3026d248d044014](https://github.com/serenity-bdd/serenity-core/commit/3026d248d044014) test: ensured that HTMLUnit tests closed the drivers to avoid memory leaks during the build.
## v1.0.35
### No issue
 * [d7f4cd3ab1d16d1](https://github.com/serenity-bdd/serenity-core/commit/d7f4cd3ab1d16d1) fix: Fixed an issue in which tests were slowed down after a failing step because Serenity continued to try to take screenshots
## v1.0.34
### No issue
 * [e20146db9d8e882](https://github.com/serenity-bdd/serenity-core/commit/e20146db9d8e882) test:Updated some unit tests
 * [2d48ba34363f7e1](https://github.com/serenity-bdd/serenity-core/commit/2d48ba34363f7e1) test:Updated some unit tests
 * [b55c8cd17404b9a](https://github.com/serenity-bdd/serenity-core/commit/b55c8cd17404b9a) feat: Distinguish between element-level timing and "wait-for"-style timing.
 * [2cb5e77f4aa71a6](https://github.com/serenity-bdd/serenity-core/commit/2cb5e77f4aa71a6) fix: Fixed a bug where the reports fail to generate if there are skipped test results in the outcomes.
 * [924764f8f9f38eb](https://github.com/serenity-bdd/serenity-core/commit/924764f8f9f38eb) feat: Improved the readability of parameters in the screenshot pages.
 * [a1dba09cd2737da](https://github.com/serenity-bdd/serenity-core/commit/a1dba09cd2737da) feat: You can now distinguish between AJAX element waits (defaults to 500 ms) and explicit fluent waits (which default to 5 seconds)
## v1.0.33
### No issue
 * [4931d367a7d3e80](https://github.com/serenity-bdd/serenity-core/commit/4931d367a7d3e80) fix: Tidied up dependencies used by the other serenity modules
 * [23d27526cd201f3](https://github.com/serenity-bdd/serenity-core/commit/23d27526cd201f3) fix: Tidied up dependencies used by the other serenity modules
 * [931e476b086f4a0](https://github.com/serenity-bdd/serenity-core/commit/931e476b086f4a0) fix: Tidied up dependencies used by the other serenity modules
## v1.0.32
### [#23](https://github.com/serenity-bdd/serenity-core/issues/23) Reporting name specified by serenity.project.name renders with low visibility contrast
 * [26f09b00e71da04](https://github.com/serenity-bdd/serenity-core/commit/26f09b00e71da04) Fixed issue #23
### No issue
 * [93b836f8f2a811e](https://github.com/serenity-bdd/serenity-core/commit/93b836f8f2a811e) fix: fixed an issue loading the JSON test reports during aggregate report generation.
 * [5894af6eb234394](https://github.com/serenity-bdd/serenity-core/commit/5894af6eb234394) fix: Removed dependency conflicts in the Gradle build.
 * [388304241495e0c](https://github.com/serenity-bdd/serenity-core/commit/388304241495e0c) feat: nested page objects i.e. widget objects

WidgetObject: reusable page fragment with a nested search context implied by the Composition pattern.  This feature was requested here:
  https://groups.google.com/forum/#!topic/thucydides-users/-SiQwD86W8I
  https://groups.google.com/forum/#!topic/thucydides-users/01oNgOD9TnY

See attached unit tests for usage examples.
 * [072b8de691e74fc](https://github.com/serenity-bdd/serenity-core/commit/072b8de691e74fc) removed duplicate test model
 * [7ba089050433c51](https://github.com/serenity-bdd/serenity-core/commit/7ba089050433c51) feat: Lists of WebElementFacade and subtypes as PageObject members.
 * [7094f8dc6dd6bae](https://github.com/serenity-bdd/serenity-core/commit/7094f8dc6dd6bae) Fixed a bug where if a null value was stored in the Serenity session after a failing step, a null pointer exception was thrown.
## v1.0.31
### No issue
 * [e78dd2cfdd98e23](https://github.com/serenity-bdd/serenity-core/commit/e78dd2cfdd98e23) Added support for displaying Saucelabs configuration in the build info screen.
 * [56f672a7f8d5941](https://github.com/serenity-bdd/serenity-core/commit/56f672a7f8d5941) Made table formatting more robust by providing support for unicode brackets and new line chars.
 * [4b6696672cfa002](https://github.com/serenity-bdd/serenity-core/commit/4b6696672cfa002) Removed redundant Jackson adaptor classes
 * [11b988b4948ee76](https://github.com/serenity-bdd/serenity-core/commit/11b988b4948ee76) Use Durations rather than longs and ints to handle timeout values, in order to avoid coding errors, make the code clearer, and as a basis for more flexible timeout configuration.
## v1.0.30
### No issue
 * [5f4b87c9b97869e](https://github.com/serenity-bdd/serenity-core/commit/5f4b87c9b97869e) Use Java NIO to copy report resources.
 * [826c30fb0ea0c99](https://github.com/serenity-bdd/serenity-core/commit/826c30fb0ea0c99) Refactored the PageObject class for better backward compatibility.
## v1.0.29
### No issue
 * [26cce2dce32adc6](https://github.com/serenity-bdd/serenity-core/commit/26cce2dce32adc6) Made a test cross-platform
 * [fe1ab3e2ce34859](https://github.com/serenity-bdd/serenity-core/commit/fe1ab3e2ce34859) Added a page to the reports containing system and configuration properties and browser capabilities for a given test run.

The browser used for each test is also recorded and displayed as an icon on the test report pages.
You can also add your own custom fields into the build information page. You do this by adding properties with the "sysinfo" prefix to your serenity.properties file. These variables take Groovy expressions, which will be evaluated when the report is run, e.g:
    sysinfo.theAnswer = 6*7
    sysinfo.homeDir = System.getenv("HOME")
 * [828c57af675ff9d](https://github.com/serenity-bdd/serenity-core/commit/828c57af675ff9d) Made the JSON tests a bit more robust
 * [8fee3ad84895083](https://github.com/serenity-bdd/serenity-core/commit/8fee3ad84895083) Chrome no longer opens a new window when you specify the browser size.

Also, the browser is now automatically positioned in the top left hand corner of the screen.

Signed-off-by: John Ferguson Smart <john.smart@wakaleo.com>
 * [9784811403dd789](https://github.com/serenity-bdd/serenity-core/commit/9784811403dd789) Migrated the PageObject class to the serenitybdd namespace.

Signed-off-by: John Ferguson Smart <john.smart@wakaleo.com>
 * [8b360c130452d8c](https://github.com/serenity-bdd/serenity-core/commit/8b360c130452d8c) Removed Jackson dependencies
 * [7020e788a36a4c2](https://github.com/serenity-bdd/serenity-core/commit/7020e788a36a4c2) Removing Jackson
 * [f4ccaf8310b5ddf](https://github.com/serenity-bdd/serenity-core/commit/f4ccaf8310b5ddf) Fixed a bug that prevented the proper use of commands like 'webdriver.manage().window().setSize(new Dimension(1600, 1200));'
## v1.0.28
### No issue
 * [80e0099cf475874](https://github.com/serenity-bdd/serenity-core/commit/80e0099cf475874) Working again after serenity package rename
 * [25e0cd1393bcc92](https://github.com/serenity-bdd/serenity-core/commit/25e0cd1393bcc92) Updated release notes
 * [3a71aaea630baf7](https://github.com/serenity-bdd/serenity-core/commit/3a71aaea630baf7) refactor: PageObject still returns thucydides WebElementFacadeImpl so that can be cast to serenitybdd namespace

This will need to be cleaned up when the thucydides namespace is retired.
 * [f9d713e343d9380](https://github.com/serenity-bdd/serenity-core/commit/f9d713e343d9380) style: fix typo in logging
 * [2648daa127fe42d](https://github.com/serenity-bdd/serenity-core/commit/2648daa127fe42d) refactor: Create serenitybdd version of WebElementFacade classes/interfaces

Deprecate Thucydides versions, but still handle them correctly
 * [2bde33abd91afd7](https://github.com/serenity-bdd/serenity-core/commit/2bde33abd91afd7) refactor: Move tests from thucydides to serenitybdd package
 * [7edba47608a800c](https://github.com/serenity-bdd/serenity-core/commit/7edba47608a800c) Improved release notes to avoid empty tags
 * [9e47250a7a37d86](https://github.com/serenity-bdd/serenity-core/commit/9e47250a7a37d86) Improved release notes to avoid empty tags
 * [948caa8ad7924d4](https://github.com/serenity-bdd/serenity-core/commit/948caa8ad7924d4) Updated release notes
 * [71d6c5a562d886d](https://github.com/serenity-bdd/serenity-core/commit/71d6c5a562d886d) Updated the README file to reflect the new commit conventions
 * [8208430d61bec12](https://github.com/serenity-bdd/serenity-core/commit/8208430d61bec12) Updated release notes
 * [80ee2cfb7f92285](https://github.com/serenity-bdd/serenity-core/commit/80ee2cfb7f92285) chore: Automated the generation of the release notes from the git commits
 * [911799b02a2d987](https://github.com/serenity-bdd/serenity-core/commit/911799b02a2d987) Fixed issues with identifying appium driver
 * [e5a13c7723cb73c](https://github.com/serenity-bdd/serenity-core/commit/e5a13c7723cb73c) SmartAnnotation needs platform for Appium annotations to work
 * [69252742737e848](https://github.com/serenity-bdd/serenity-core/commit/69252742737e848) Support for appium annotations, added accessibility and ui automation for IOS and android
 * [52f0eeadcfc82d2](https://github.com/serenity-bdd/serenity-core/commit/52f0eeadcfc82d2) Missed change to PathProcessor
 * [e84ac40f8da7831](https://github.com/serenity-bdd/serenity-core/commit/e84ac40f8da7831) Porting changes from thucydides appium-driver-support
## v1.0.27
### No issue
 * [b52b55a39a9d501](https://github.com/serenity-bdd/serenity-core/commit/b52b55a39a9d501) Now you can use the -Dserenity.dry.run=true option to skip step executions - useful when testing JBehave or Cucumber step definitions
## v1.0.26
### [#16](https://github.com/serenity-bdd/serenity-core/issues/16) Cleanup dep tree, especially convergence errors
 * [c9f95050aadcd98](https://github.com/serenity-bdd/serenity-core/commit/c9f95050aadcd98) Upgrade javassist version to match transitive dep. #16

core's version was 3.9.0.GA; reflections version is 3.12.1.GA
### No issue
 * [068315f4f0e63d4](https://github.com/serenity-bdd/serenity-core/commit/068315f4f0e63d4) Performance improvements: forces WebDriver to force an immediate response for findElements() calls if no matching elements are found, and some other minor improvements.

Also improved step logging to include the test class and method as well as the step name.

Signed-off-by: John Ferguson Smart <john.smart@wakaleo.com>
 * [f9d996950d02e31](https://github.com/serenity-bdd/serenity-core/commit/f9d996950d02e31) Made the log messages for each step include the calling class and method.

Signed-off-by: John Ferguson Smart <john.smart@wakaleo.com>
 * [afaf0b947f97a8c](https://github.com/serenity-bdd/serenity-core/commit/afaf0b947f97a8c) Fix to remove 'Session ID is null. Using WebDriver after calling quit()?' messages appearing when the tests are run in threads
 * [a2d3a0f17b4ad20](https://github.com/serenity-bdd/serenity-core/commit/a2d3a0f17b4ad20) Refactored optional Spring dependencies into the serenity-spring module - include this module if you want Serenity to honor Spring annotations and dependency injection
## v1.0.25
### [#16](https://github.com/serenity-bdd/serenity-core/issues/16) Cleanup dep tree, especially convergence errors
 * [3144ad12699cd6a](https://github.com/serenity-bdd/serenity-core/commit/3144ad12699cd6a) Upgrade groovy-all version for transitive convergence #16.
 * [392bc01b88be7b4](https://github.com/serenity-bdd/serenity-core/commit/392bc01b88be7b4) Upgrade SLF4J version for transitive convergence #16.
 * [099d1189d1c5d0c](https://github.com/serenity-bdd/serenity-core/commit/099d1189d1c5d0c) core build: exclude transitive deps with convergence problems. #16

Declare additional transitive deps.
 * [197fab566647c12](https://github.com/serenity-bdd/serenity-core/commit/197fab566647c12) Top build: declare transitives as deps. #16
 * [22d5395e9df2cbc](https://github.com/serenity-bdd/serenity-core/commit/22d5395e9df2cbc) Top build: fail fast on dependency convergence problems. #16

Added "force version" on transitive versions with convergence
problems.

Issue: While this works to keep gradle build clean, it doesn't
affect the generated POM/install for clients.
 * [7a267aa8399a3dd](https://github.com/serenity-bdd/serenity-core/commit/7a267aa8399a3dd) Build: Add plugins that help with dep versions. #16

- project-report:
  - gradlew htmlDependencyReport creates HTML dep report that shows
    which deps the build managed to different version.

- com.github.ben-manes.versions:
  - gradlew dependencyUpdates shows deps with new versions
 * [70325bb74775cb3](https://github.com/serenity-bdd/serenity-core/commit/70325bb74775cb3) Upgrade commons-lang3 to htmlunit's dep version. #16

HtmlUnit uses 3.3.2, Serenity was using 3.1.
 * [ceb0c1d103411a9](https://github.com/serenity-bdd/serenity-core/commit/ceb0c1d103411a9) Upgrade htmlunit to Selenium's dep version. #16

Selenium uses 2.15, Serenity was using 2.9.
### No issue
 * [89f6ca56633ed1c](https://github.com/serenity-bdd/serenity-core/commit/89f6ca56633ed1c) Provide better support for step-level error reporting in Cucumber.
 * [52e64aef5ebbe28](https://github.com/serenity-bdd/serenity-core/commit/52e64aef5ebbe28) Tidied up some dependencies.
 * [003791a889e2988](https://github.com/serenity-bdd/serenity-core/commit/003791a889e2988) Extracted dependency injection into an external module, to make it easier to add additional dependency injection modules later
## v1.0.24
### No issue
 * [d9a768af4b3eb2a](https://github.com/serenity-bdd/serenity-core/commit/d9a768af4b3eb2a) Release notes are now triggered manually before the release
 * [c36529114af5daa](https://github.com/serenity-bdd/serenity-core/commit/c36529114af5daa) Updated release notest
 * [7c429c02e9f8522](https://github.com/serenity-bdd/serenity-core/commit/7c429c02e9f8522) Migrated the default output directory to target/site/serenity
 * [82b98664486be5d](https://github.com/serenity-bdd/serenity-core/commit/82b98664486be5d) Migrated the default output directory to target/site/serenity
 * [97156bd6c56b571](https://github.com/serenity-bdd/serenity-core/commit/97156bd6c56b571) Added support for better Cucumber reporting
 * [5ea5e898b1bcab7](https://github.com/serenity-bdd/serenity-core/commit/5ea5e898b1bcab7) Updated release notes
 * [88dbe9c8342f0d8](https://github.com/serenity-bdd/serenity-core/commit/88dbe9c8342f0d8) Restored release notes
 * [9716bc56de482ff](https://github.com/serenity-bdd/serenity-core/commit/9716bc56de482ff) Make sure the release notes are produced dynamically
 * [9e9711d48eca8bb](https://github.com/serenity-bdd/serenity-core/commit/9e9711d48eca8bb) Added extra support for handling Cucumber example tables
 * [e3ce499a6d4f91e](https://github.com/serenity-bdd/serenity-core/commit/e3ce499a6d4f91e) Simplified dependencies a little
 * [7cb2a81cae949bd](https://github.com/serenity-bdd/serenity-core/commit/7cb2a81cae949bd) WIP
## v1.0.23
### No issue
 * [8d8b0bf5fb05fce](https://github.com/serenity-bdd/serenity-core/commit/8d8b0bf5fb05fce) You can now use serenity.* instead of thucydides.* system properties. The thucydides.* system properties are still supported, but a warning is printed to the logs.
 * [cfaae5a78a36fbb](https://github.com/serenity-bdd/serenity-core/commit/cfaae5a78a36fbb) rename serenity_bdd to serenitybdd
## v1.0.22
### No issue
 * [3443435570d0e97](https://github.com/serenity-bdd/serenity-core/commit/3443435570d0e97) Move junit finder classes to serenity_bdd package
 * [7bde2389379fa22](https://github.com/serenity-bdd/serenity-core/commit/7bde2389379fa22) Rename package in demo to serenity_bdd
 * [2aa92f97522d705](https://github.com/serenity-bdd/serenity-core/commit/2aa92f97522d705) SerenityRunner and SerenityParameterizedRunner now contain functionality, and Thucydides equivalents merely extend

Also move a number of helper classes into serenity_bdd package
 * [b94933d99cc7e9f](https://github.com/serenity-bdd/serenity-core/commit/b94933d99cc7e9f) Move JUnit runners to serenity_bdd package
## v1.0.21
### No issue
 * [5fc6c9a9e3e0b7c](https://github.com/serenity-bdd/serenity-core/commit/5fc6c9a9e3e0b7c) Improvements to the reports
 * [c31cb4f4b17a086](https://github.com/serenity-bdd/serenity-core/commit/c31cb4f4b17a086) Improvements to the reports
## v1.0.18
### No issue
 * [6ee7578c7e241b6](https://github.com/serenity-bdd/serenity-core/commit/6ee7578c7e241b6) Added better support for comments in feature files, and better formatting in the 'Related Tabs' table
 * [9b7e9c43d7f6bab](https://github.com/serenity-bdd/serenity-core/commit/9b7e9c43d7f6bab) Hardening unit tests
 * [199e60a595c0830](https://github.com/serenity-bdd/serenity-core/commit/199e60a595c0830) Updated reporting, attempt 2
## v1.0.17
### No issue
 * [602eaf8dfe8633e](https://github.com/serenity-bdd/serenity-core/commit/602eaf8dfe8633e) Added tool tips on the 'Related Tags' tables
 * [a05b31ffb0928e9](https://github.com/serenity-bdd/serenity-core/commit/a05b31ffb0928e9) Undid javascript library updates and added the number of tests for tags on the reports
 * [0a272c47a9a49f2](https://github.com/serenity-bdd/serenity-core/commit/0a272c47a9a49f2) Revert "Updated libraries"

This reverts commit 44ec91e92d90ebc3742a6221f82d1a404b1baa57.
 * [f8f476230acb6e8](https://github.com/serenity-bdd/serenity-core/commit/f8f476230acb6e8) Revert "Update reports to use new libraries"

This reverts commit f4a75422ecfc46a66fb5ebb617ce808c299a6d4b.
 * [d017a61caa8d820](https://github.com/serenity-bdd/serenity-core/commit/d017a61caa8d820) Revert "Refactoring to facilitate easier migrating to new versions of the libraries"

This reverts commit 6f12e5389a8499e2f9f9b69478b329f90367d4fb.
 * [a25fed4b5fe3830](https://github.com/serenity-bdd/serenity-core/commit/a25fed4b5fe3830) Revert "Updated excanvas"

This reverts commit 5d55b1eae5d424b7185ed1aab68ab6f36c53cbf6.
 * [b49d68030bb88d0](https://github.com/serenity-bdd/serenity-core/commit/b49d68030bb88d0) Revert "Updated JavaScript InfoVis Toolkit"

This reverts commit a3c95dc54f1165c5ea00fcb2719f14a63acba604.
 * [1b62cb0a07240b4](https://github.com/serenity-bdd/serenity-core/commit/1b62cb0a07240b4) Revert "Removed old versions of libraries"

This reverts commit 7b26344dea3c0ee710ee90fe7040141a6941f97f.
 * [7b26344dea3c0ee](https://github.com/serenity-bdd/serenity-core/commit/7b26344dea3c0ee) Removed old versions of libraries
 * [a3c95dc54f1165c](https://github.com/serenity-bdd/serenity-core/commit/a3c95dc54f1165c) Updated JavaScript InfoVis Toolkit
 * [5d55b1eae5d424b](https://github.com/serenity-bdd/serenity-core/commit/5d55b1eae5d424b) Updated excanvas
 * [6f12e5389a8499e](https://github.com/serenity-bdd/serenity-core/commit/6f12e5389a8499e) Refactoring to facilitate easier migrating to new versions of the libraries
 * [f4a75422ecfc46a](https://github.com/serenity-bdd/serenity-core/commit/f4a75422ecfc46a) Update reports to use new libraries
 * [44ec91e92d90ebc](https://github.com/serenity-bdd/serenity-core/commit/44ec91e92d90ebc) Updated libraries
## v1.0.16
### No issue
 * [18d5f80d55e8b83](https://github.com/serenity-bdd/serenity-core/commit/18d5f80d55e8b83) Improved requirement reporting for JUnit (experimental)
 * [6376d9951792d7b](https://github.com/serenity-bdd/serenity-core/commit/6376d9951792d7b) This small change makes Serenity compatible with Firefox version 32 or greater

Guava 18.0 is already specified in Gradle.
## v1.0.15
### No issue
 * [892b4fe6a8d0fab](https://github.com/serenity-bdd/serenity-core/commit/892b4fe6a8d0fab) Improved reporting of JUnit tests as requirements
## v1.0.14
### No issue
 * [d5f35b9cf08b4e6](https://github.com/serenity-bdd/serenity-core/commit/d5f35b9cf08b4e6) Switched back to JUnit 4.11 due to API incompatibilities with build tools
## v1.0.13
### No issue
 * [7cbe55192607ef2](https://github.com/serenity-bdd/serenity-core/commit/7cbe55192607ef2) The @Pages annotated field in JUnit tests is now optional
 * [3d985d15871a528](https://github.com/serenity-bdd/serenity-core/commit/3d985d15871a528) Upgraded to JUnit 4.12
## v1.0.12
### No issue
 * [1290a90ccf2c6c3](https://github.com/serenity-bdd/serenity-core/commit/1290a90ccf2c6c3) Solidified a test
## v1.0.12-rc.1
### No issue
 * [878c2a1edb79d85](https://github.com/serenity-bdd/serenity-core/commit/878c2a1edb79d85) Added better support for radio buttons in the PageObject class
 * [0902fc79603d4f0](https://github.com/serenity-bdd/serenity-core/commit/0902fc79603d4f0) Use gradle-git for version and tagging

    === If local repository is dirty
        -Always builds a SNAPSHOT version.
        -Will complain that 'Stage {} is not one of [SNAPSHOT] allowed for strategy snapshot.'

    === If local repository is not dirty
        Set release type using property release.stage. Valid values are:
            -milestone
            -rc
            -final
        milestone creates a tag with the next version from tag + -milestone.#
        rc similar, but uses rc. Cannot create a milestone after an rc
        final creates a version with no endings

    If want to use ssh authorization, must ensure ssh-agent contains correct key for repository being worked on.
    If you experience issues, try ssh-add -D to clear identities and add the one identity for the repo in question.

    The release tags the current commit, and pushes to the remote repository. It does not check if there's a new commit, so only use it if you really need to.

    gradle bintrayUpload release -Prelease.stage={milestone|rc|final}
 * [e0a96d7cd7499a4](https://github.com/serenity-bdd/serenity-core/commit/e0a96d7cd7499a4) Fix scm url's

All were pointing at project.name, when in fact they all exist in the same
serenity-core repository
 * [6d6327665844b25](https://github.com/serenity-bdd/serenity-core/commit/6d6327665844b25) Correct issue with publishing

Main project doesn't have anything to deploy, and doesn't have config. This
causes a warning when building the project.

Provide the config that is common across all projects in this config file,
but no config for the main project is required.
 * [3ee866cd987cfb1](https://github.com/serenity-bdd/serenity-core/commit/3ee866cd987cfb1) Remove unused files

It would appear that the main project was moved into core sub-directory, and
these files didn't get cleaned up.
 * [ed62753b69b522f](https://github.com/serenity-bdd/serenity-core/commit/ed62753b69b522f) [namespace] Move Find annotations to serenity_bdd namespace

Create deprecated versions in thucydides namespace but with 2 on name to ensure
caught all changes, and returning objects of correct classes.

Also kept deprecated versions of tests to ensure old versions still work correctly
 * [47542e1b4cfc29c](https://github.com/serenity-bdd/serenity-core/commit/47542e1b4cfc29c) Made the Ant plugin a bit more robust.
 * [c0a1aa089cd72af](https://github.com/serenity-bdd/serenity-core/commit/c0a1aa089cd72af) Moved the ant plugin over to the new Serenity namespace
 * [2ed2864f88aaf29](https://github.com/serenity-bdd/serenity-core/commit/2ed2864f88aaf29) [migrate] Move exceptions
 * [ad3a486ced855de](https://github.com/serenity-bdd/serenity-core/commit/ad3a486ced855de) [migrate] Move SessionMap
 * [d84aeede8457858](https://github.com/serenity-bdd/serenity-core/commit/d84aeede8457858) [rename] Create Serenity namespaced class and move some associated test classes
 * [3705ee4ffed330e](https://github.com/serenity-bdd/serenity-core/commit/3705ee4ffed330e) [rename] Create Serenity namespaced class, deprecate Thucydides version and delegate functions
 * [8bdaf7db8f1f501](https://github.com/serenity-bdd/serenity-core/commit/8bdaf7db8f1f501) [rename] Move SerenityListeners and create deprecated ThucydidesListeners
 * [61cc4d855d5a0ef](https://github.com/serenity-bdd/serenity-core/commit/61cc4d855d5a0ef) Display error messages for ignored steps, so that failing assumption messages are correctly displayed
 * [04cace4c7b9b053](https://github.com/serenity-bdd/serenity-core/commit/04cace4c7b9b053) Updated banners
 * [be15eb47c729538](https://github.com/serenity-bdd/serenity-core/commit/be15eb47c729538) Move Serenity to new package
 * [581dd4753b647b3](https://github.com/serenity-bdd/serenity-core/commit/581dd4753b647b3) Rename main class to reflect new project name, and deprecate old

Eventually, all Thucydides references will be removed.
 * [40a532d21efa776](https://github.com/serenity-bdd/serenity-core/commit/40a532d21efa776) Updated the Ascii Art banner.
## v1.0.9
### No issue
 * [09927b0fda489ce](https://github.com/serenity-bdd/serenity-core/commit/09927b0fda489ce) Integrated better support for JBehave
 * [b3340e5d3756a26](https://github.com/serenity-bdd/serenity-core/commit/b3340e5d3756a26) Integrated better support for JBehave
 * [5ea5b718068a34f](https://github.com/serenity-bdd/serenity-core/commit/5ea5b718068a34f) Changed the 'checkOutcomes' task to force it to run the tests first.
## v1.0.8
### No issue
 * [e1956cfd278a505](https://github.com/serenity-bdd/serenity-core/commit/e1956cfd278a505) Enable selection of Mac Os version on SauceLabs
 * [8344474fc2d7c23](https://github.com/serenity-bdd/serenity-core/commit/8344474fc2d7c23) Added support for the AssumeThat method for JUnit tests - AssumeThat will result in a test being displayed as 'ignored' in the reports.
 * [40db746819856e7](https://github.com/serenity-bdd/serenity-core/commit/40db746819856e7) Enable selection of Mac Os version on SauceLabs
 * [eb4608f6d8c1818](https://github.com/serenity-bdd/serenity-core/commit/eb4608f6d8c1818) Removed some code that used the JDK 8 libraries
 * [c12c6ddc076bcb8](https://github.com/serenity-bdd/serenity-core/commit/c12c6ddc076bcb8) Updated to Selenium 2.44.0
 * [308ec8f50c5dbcc](https://github.com/serenity-bdd/serenity-core/commit/308ec8f50c5dbcc) Updated the changelog to reflect the serenity-core repo. For Bintray this is a bit of a hack, since the BinTray serenity-core package gets artifacts from two repos, serenity-core and serenity-maven-plugin, which are separate only because of the fact that core needs to be built and deployed before the maven plugin generation task in the serenity-maven-plugin build can be done. So the changelogs will be up-to-date on Github for both repos, but the one on bintray will only reflect core.
 * [50c45e31c5432cd](https://github.com/serenity-bdd/serenity-core/commit/50c45e31c5432cd) Adding an automatically generated change log to the build
## v1.0.7
### Jira
 * [4494dee65ac0b1f](https://github.com/serenity-bdd/serenity-core/commit/4494dee65ac0b1f) If javadoc is not told to expect UTF-8 in the strings it uses can generate ASCII errors on at least the Mac.
### No issue
 * [00de150f4da3aab](https://github.com/serenity-bdd/serenity-core/commit/00de150f4da3aab) Refactored the gradle plugin
 * [66556bb4e71cf65](https://github.com/serenity-bdd/serenity-core/commit/66556bb4e71cf65) Fixed a bug where error messages were incorrectly displayed in the step details
 * [6d0f8ee7d7ee3c2](https://github.com/serenity-bdd/serenity-core/commit/6d0f8ee7d7ee3c2) jbehave was pulling in hamcrest 1.1. Excluded hamcrest from the jbehave dependency.
## v1.0.6
### No issue
 * [2f58c3b419c5330](https://github.com/serenity-bdd/serenity-core/commit/2f58c3b419c5330) Fixed some formatting and navigation issues in the reports
## v1.0.5
### No issue
 * [6780200d8b74535](https://github.com/serenity-bdd/serenity-core/commit/6780200d8b74535) Added the Serenity helper class, as a replacement for the legacy 'Thucydides' class
 * [08b5502af44c08f](https://github.com/serenity-bdd/serenity-core/commit/08b5502af44c08f) Fixed a bug in the reporting where duplicate story tags were displayed in the screenshot screens.
 * [805dbf1a9bf72b6](https://github.com/serenity-bdd/serenity-core/commit/805dbf1a9bf72b6) Logs a message indicating the path of the generated reports after report aggregation.
 * [fe1c3c5eb2cee95](https://github.com/serenity-bdd/serenity-core/commit/fe1c3c5eb2cee95) Added the Serenity utility class, which exposes and delegates to methods of the legacy Thucydides class.
 * [4138f8900eb6259](https://github.com/serenity-bdd/serenity-core/commit/4138f8900eb6259) Check if a resized file for a given screenshot already exists, and if so don't perform the resizing
 * [0e9d614b462448a](https://github.com/serenity-bdd/serenity-core/commit/0e9d614b462448a) Moved most uses of FileUtils to the Java 7 Files class, in order to remove sporadic issues when resizing screenshots
## v1.0.4
### No issue
 * [7a65f64d3bd4d6f](https://github.com/serenity-bdd/serenity-core/commit/7a65f64d3bd4d6f) Fixed a failing test
 * [b42d58b33af6ea3](https://github.com/serenity-bdd/serenity-core/commit/b42d58b33af6ea3) Fine-tuning the reports
 * [f07879ca4b94183](https://github.com/serenity-bdd/serenity-core/commit/f07879ca4b94183) Refactored some tests
 * [d5511b6706701d4](https://github.com/serenity-bdd/serenity-core/commit/d5511b6706701d4) Cater for rare cases where the driver returns null when an element is not found
 * [36d471f7c2acdbd](https://github.com/serenity-bdd/serenity-core/commit/36d471f7c2acdbd) Repositioned the report timestamp
 * [80e1ef06258e1e5](https://github.com/serenity-bdd/serenity-core/commit/80e1ef06258e1e5) Repositioned the report timestamp
 * [c8fd3b94c1bd867](https://github.com/serenity-bdd/serenity-core/commit/c8fd3b94c1bd867) Added bootstrap tabs
 * [4a132ad31b57d7f](https://github.com/serenity-bdd/serenity-core/commit/4a132ad31b57d7f) Added tests to the gradle plugin
 * [98073bdbe5ff127](https://github.com/serenity-bdd/serenity-core/commit/98073bdbe5ff127) Added SerenityRunner and SerenityParameterizedRunner classes as alternative names for ThucydidesRunner and ThucydidesParameterizedRunner, more in line with the new naming schema.
 * [4c953d868707e2c](https://github.com/serenity-bdd/serenity-core/commit/4c953d868707e2c) Moved the serenity-maven-plugin to a separate project
 * [ad4800ebcf39afd](https://github.com/serenity-bdd/serenity-core/commit/ad4800ebcf39afd) Getting the maven plugin build working
 * [74df0296738f380](https://github.com/serenity-bdd/serenity-core/commit/74df0296738f380) Fine-tuning the release tagging
## v1.0.2
### No issue
 * [527387e98a503f0](https://github.com/serenity-bdd/serenity-core/commit/527387e98a503f0) Initial release version
 * [4a119f5eb78613d](https://github.com/serenity-bdd/serenity-core/commit/4a119f5eb78613d) Added a selector to find buttons by their label, e.g. find(By.buttonText('Add to cart'));
 * [8ba6aeb194a96bb](https://github.com/serenity-bdd/serenity-core/commit/8ba6aeb194a96bb) Honor both 'thucydides.properties' and 'serenity.properties' files for local project configuration
 * [b5732dc3a744246](https://github.com/serenity-bdd/serenity-core/commit/b5732dc3a744246) Let the bintray keys be defined by the build server
 * [f2322d488bb19e9](https://github.com/serenity-bdd/serenity-core/commit/f2322d488bb19e9) Minor fix to work around an incompatiblity with IBM JDB 1.7
 * [5caf4a28cbcb818](https://github.com/serenity-bdd/serenity-core/commit/5caf4a28cbcb818) Changed group to serenity-bdd to make syncing with Maven Central easier
 * [5d3f58a217827dd](https://github.com/serenity-bdd/serenity-core/commit/5d3f58a217827dd) Changed group to serenity-bdd to make syncing with Maven Central easier
 * [1d7740dc9d007c0](https://github.com/serenity-bdd/serenity-core/commit/1d7740dc9d007c0) Fixed an issue in the BinTray deployment
 * [3620bc2af882c43](https://github.com/serenity-bdd/serenity-core/commit/3620bc2af882c43) Fine-tuning the release pipeline
 * [bc0e078f187ae7f](https://github.com/serenity-bdd/serenity-core/commit/bc0e078f187ae7f) Added more info to the README file
 
