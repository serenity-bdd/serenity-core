## Serenity BDD core change Log

### v1.1.29-rc.3 (2016/03/29 17:38 +00:00)
 
**Pull requests:**
 
 
### v1.1.29-rc.2 (2016/03/25 14:57 +00:00)
 
**Pull requests:**
 
- [#367](https://github.com/serenity-bdd/serenity-core/pull/367) Updated making screenshots for browsers without such abbility ([@YamStranger](https://github.com/YamStranger))
- [#366](https://github.com/serenity-bdd/serenity-core/pull/366) Updated making screenshort with died browser - nothing should be taken ([@YamStranger](https://github.com/YamStranger))
- [#360](https://github.com/serenity-bdd/serenity-core/pull/360) Fixed closing web driver when appium is used, it seems that window handles still does not implemented for Android devices, only for iOS ([@YamStranger](https://github.com/YamStranger))
- [#354](https://github.com/serenity-bdd/serenity-core/pull/354) fix: Issues with SerenityParameterized test requiring WebDriver ([@jordanbragg](https://github.com/jordanbragg))
- [#349](https://github.com/serenity-bdd/serenity-core/pull/349) Updated scenario data driven steps processing for report - now report will contains steps as for first scenario ([@YamStranger](https://github.com/YamStranger))
- [#348](https://github.com/serenity-bdd/serenity-core/pull/348) Updated name of test method for Qualifier tests ([@YamStranger](https://github.com/YamStranger))
- [#347](https://github.com/serenity-bdd/serenity-core/pull/347) Updated processing of @Qualifier tag in junit tests with data tables. Now it is possible add short description to steps based on parameters value ([@YamStranger](https://github.com/YamStranger))
- [#344](https://github.com/serenity-bdd/serenity-core/pull/344) Created gradle build config for smoketests to execute them against latest serenty core ([@YamStranger](https://github.com/YamStranger))
- [#339](https://github.com/serenity-bdd/serenity-core/pull/339) Updated tests for rest-assurance to use wiremock server instead of petstore. Added TestCase rule - now it is possible initialize test using spock ([@YamStranger](https://github.com/YamStranger))
- [#330](https://github.com/serenity-bdd/serenity-core/pull/330) Updated store/load of outcomes - serenity.report.encoding property will be used with UTF-8 as default ([@cliviu](https://github.com/cliviu))
 
**Commits:**
 
- [240b93b](https://github.com/serenity-bdd/serenity-core/commit/240b93be5bd79a80bf13778033753a0fd62b3f7f) feat: added checking if browser alive before taking screenshot or saving page-source ([@YamStranger](https://github.com/YamStranger))
- [4d2029c](https://github.com/serenity-bdd/serenity-core/commit/4d2029c6b2b6313c1825c80f39acf2b425ec959b) test: updated tests for rest-assurance to use wiremock server instead of petstore ([@YamStranger](https://github.com/YamStranger))
- [6417437](https://github.com/serenity-bdd/serenity-core/commit/64174370c98b6db742fe95e47d8ce25d9de59c96) fix: updated scenario data driven steps processing for report - now report will contains steps as from first scenario ([@YamStranger](https://github.com/YamStranger))
- [6aefe69](https://github.com/serenity-bdd/serenity-core/commit/6aefe6917efc7abf5b1902b77f69a8b70723b914) test: refactored test for Qualifier ([@YamStranger](https://github.com/YamStranger))
- [71102ac](https://github.com/serenity-bdd/serenity-core/commit/71102acf032fd2c5aa6b2919b7f378fbf285da94) fix: updated processing of @Qualifier tag in junit tests with data tables. Now it is possible add short description to steps based on parameters value ([@YamStranger](https://github.com/YamStranger))
- [89e9512](https://github.com/serenity-bdd/serenity-core/commit/89e9512d61ebfa1227c9106b774e82176780bb04) test: added tests to check how taking screenshorts works with died browsers ([@YamStranger](https://github.com/YamStranger))
- [a4d6b32](https://github.com/serenity-bdd/serenity-core/commit/a4d6b324a8c80976961b4312ab75f11980519d62) fix: When using SerenityParameterizedRunner tests there was a need to add [@Managed WebDriver in order to see the examples table. This is due to TestClassRunnerForInstanciatedTestCase not overriding initListener](https://github.com/Managed WebDriver in order to see the examples table. This is due to TestClassRunnerForInstanciatedTestCase not overriding initListener)s
- [a505bd9](https://github.com/serenity-bdd/serenity-core/commit/a505bd9bcc18bde3625c0d0b4b4a86d43416f438) fix: updated closing web driver when appium is used, it seems that window handles still does not implemented for Android devices, only for iOS ([@YamStranger](https://github.com/YamStranger))
- [bf6d91e](https://github.com/serenity-bdd/serenity-core/commit/bf6d91e2f403ea4c5049ea32c72cade8a2f1f2d9) chore: created gradle build config for smoketests to execute them against latest serenty core ([@YamStranger](https://github.com/YamStranger))
- [e6e6651](https://github.com/serenity-bdd/serenity-core/commit/e6e665143f90393636e44ff85d1f8784f8b3910d) test: updated name of test method ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.29-rc.1 (2016/03/02 17:33 +00:00)
 
**Pull requests:**
 
- [#335](https://github.com/serenity-bdd/serenity-core/pull/335) Updated moving files. Now tmp files of reports will be moved only after stream will be closed ([@YamStranger](https://github.com/YamStranger))
- [#333](https://github.com/serenity-bdd/serenity-core/pull/333) Updated appium java client to 3.3.0 ([@YamStranger](https://github.com/YamStranger))
- [#332](https://github.com/serenity-bdd/serenity-core/pull/332) Updated processing of names to keep abbreviations of JSON CSV XML ([@YamStranger](https://github.com/YamStranger))
- [#331](https://github.com/serenity-bdd/serenity-core/pull/331) Updated appium configuration to be compatible with Appium 1.5 -  appium.app is no longer required if appium.browserName is supplied ([@hazmeister](https://github.com/hazmeister))
 
**Commits:**
 
- [0691998](https://github.com/serenity-bdd/serenity-core/commit/0691998f552206e3b505883b8bc81a77e5968d9e) fix: updated moving files. Not tmp files of reports will be moved only after stream will be closed ([@YamStranger](https://github.com/YamStranger))
- [3cacf17](https://github.com/serenity-bdd/serenity-core/commit/3cacf176fa0d2dd7f50c5684f9aa1cc690465bd9) chore: updated appium java client to 3.3.0 ([@YamStranger](https://github.com/YamStranger))
- [bf3fa9a](https://github.com/serenity-bdd/serenity-core/commit/bf3fa9a3d87560e8eb70bbcecb6a9e4c9c695446) feat: updated processing of names ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.28 (2016/03/01 10:28 +00:00)
 
**Pull requests:**
 
- [#295](https://github.com/serenity-bdd/serenity-core/pull/295) Updated test to check how darkroom works in parallel screenshot taking ([@YamStranger](https://github.com/YamStranger))
- [#328](https://github.com/serenity-bdd/serenity-core/pull/328) Included exception/assertion message into serenity report ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [1a6f158](https://github.com/serenity-bdd/serenity-core/commit/1a6f15809e451f0c2b56e58e812ce663e3d78ae0) test: redusing resource usage during testing of darkroom ([@YamStranger](https://github.com/YamStranger))
- [1c84dd4](https://github.com/serenity-bdd/serenity-core/commit/1c84dd4f4a7b27abce81fdf265bbc4b7ba586f79) test: Darkroom can be used in parallel ([@YamStranger](https://github.com/YamStranger))
- [5f7e414](https://github.com/serenity-bdd/serenity-core/commit/5f7e414fd31985eb9706c20b28632c6d054be927) fix: updated test to fail if darkroom fail in parallel screenshot taking ([@YamStranger](https://github.com/YamStranger))
- [5fdbaa9](https://github.com/serenity-bdd/serenity-core/commit/5fdbaa99ea3d8b6da780c2ba2cd6b6de59fd1ed8) test: added test for fix of Exception/assertion message in serenity report for #321 ([@robertzett](https://github.com/robertzett))
- [f6fd880](https://github.com/serenity-bdd/serenity-core/commit/f6fd88055f428df81098ba77c1b5cf0a0a2f51c3) fix: included in serenity report Exception/assertion message for #321 ([@robertzett](https://github.com/robertzett))
 
### v1.1.27 (2016/02/29 11:09 +00:00)
 
**Pull requests:**
 
- [#326](https://github.com/serenity-bdd/serenity-core/pull/326) Updated report generation to use temp files. ([@YamStranger](https://github.com/YamStranger))
- [#324](https://github.com/serenity-bdd/serenity-core/pull/324) Updated method to print exception if it will appear ([@YamStranger](https://github.com/YamStranger))
- [#323](https://github.com/serenity-bdd/serenity-core/pull/323) Updated loader and reporter to load and generate testoutcomes concurrently with tests, updated gson core to 2.4 ([@YamStranger](https://github.com/YamStranger))
- [#319](https://github.com/serenity-bdd/serenity-core/pull/319) Updated jbehave to 4.0.5 ([@YamStranger](https://github.com/YamStranger))
- [#317](https://github.com/serenity-bdd/serenity-core/pull/317) Updated checking of empty string to use StringUtils, for #310 ([@YamStranger](https://github.com/YamStranger))
- [#316](https://github.com/serenity-bdd/serenity-core/pull/316) Updated contiributing guide - explaned multiline commits and pull requests name convention ([@YamStranger](https://github.com/YamStranger))
- [#311](https://github.com/serenity-bdd/serenity-core/pull/311) Change log generated for all releases ([@YamStranger](https://github.com/YamStranger))
- [#313](https://github.com/serenity-bdd/serenity-core/pull/313) Fixed Test Failure stacktrace, now it can contain elements with no fileName (default.ftl), for #312 ([@marek5050](https://github.com/marek5050))
 
**Commits:**
 
- [1314d42](https://github.com/serenity-bdd/serenity-core/commit/1314d42bbd0fd09ae43a5a3e8a93845c89d2d062) feat: updated report generation to use atomic operations ([@YamStranger](https://github.com/YamStranger))
- [3fe8573](https://github.com/serenity-bdd/serenity-core/commit/3fe8573b0f5a7f9bd27e3509255be58ccca42591) perf: updated checking of empty string to use StringUtils ([@YamStranger](https://github.com/YamStranger))
- [5afea72](https://github.com/serenity-bdd/serenity-core/commit/5afea7264123c2b3d4a19283e5fa04bba1e34a4d) fix: updated report generation to use temp files, it fixes bugs with running tests with multiple workers (and different Java Runtime as well) ([@YamStranger](https://github.com/YamStranger))
- [80d82d0](https://github.com/serenity-bdd/serenity-core/commit/80d82d0461688f56326f9fea95f11ce12b5daa9d) fix: updated returned file to use generated file ([@YamStranger](https://github.com/YamStranger))
- [82f8953](https://github.com/serenity-bdd/serenity-core/commit/82f8953051af9297ab66a5e0ac6aca81031f4864) docs: updating contributing docs ([@YamStranger](https://github.com/YamStranger))
- [ab632f4](https://github.com/serenity-bdd/serenity-core/commit/ab632f4de53980ff5d4310a01c5dd832f7ff5538) feat: updated jbehave to 4.0.5 ([@YamStranger](https://github.com/YamStranger))
- [d9e9c80](https://github.com/serenity-bdd/serenity-core/commit/d9e9c80a2db830ceeb24b84d44b6bbfed49606db) feat: updated method to pring exception ifit will appear ([@YamStranger](https://github.com/YamStranger))
- [e0200f2](https://github.com/serenity-bdd/serenity-core/commit/e0200f274f3c578faf4104a4e0b68b99a226d74a) fix: updated report loading and generating code and added test to be sure that all can be run concurrently ([@YamStranger](https://github.com/YamStranger))
- [f91e7b3](https://github.com/serenity-bdd/serenity-core/commit/f91e7b346093be7c27667a74698a26e8d749ca71) feat: test updated for reporter and loader - same testoutcoume should be writed only once in report dirrectory ([@YamStranger](https://github.com/YamStranger))
- [fa25694](https://github.com/serenity-bdd/serenity-core/commit/fa256947a31f6eea786d0f2c6a585e4cdaf836bd) fix: updated report generation to use temp files with random names ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.26 (2016/02/15 11:18 +00:00)
 
 
### v1.1.26-rc.3 (2016/02/14 00:08 +00:00)
 
 
**Commits:**
 
- [433b732](https://github.com/serenity-bdd/serenity-core/commit/433b732734f4eaa0157f53951d16f1a5957853ff) Allow more elegant waits in the Screenplay module ([@wakaleo](https://github.com/wakaleo))
 
 > You can now write code like this:
 > jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(10))))
 > This will not fail if the matcher cannot be evaluated the first time, but will retry up to a maximum of &#39;serenity.timouts&#39; seconds (5 by default).
- [e0c2263](https://github.com/serenity-bdd/serenity-core/commit/e0c22637eccbd6eaf717b6f43e91edc22605e965) Corrected an error in the module names ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.26-rc.2 (2016/02/13 11:26 +00:00)
 
**Pull requests:**
 
- [#308](https://github.com/serenity-bdd/serenity-core/pull/308) Updated groovy test style ([@YamStranger](https://github.com/YamStranger))
- [#307](https://github.com/serenity-bdd/serenity-core/pull/307) Updated smoke-test to use latest serenity-core and serenity maven ([@YamStranger](https://github.com/YamStranger))
- [#306](https://github.com/serenity-bdd/serenity-core/pull/306) Updated requirements loading to fix multimodule projects builds, for #305 ([@YamStranger](https://github.com/YamStranger))
- [#303](https://github.com/serenity-bdd/serenity-core/pull/303) Gradle plugin updated to enable separate call of goals ([@YamStranger](https://github.com/YamStranger))
- [#301](https://github.com/serenity-bdd/serenity-core/pull/301) Updated ThucydidesWebDriverSupport.getSessionId() to work properly with remote drivers, for #299 ([@YamStranger](https://github.com/YamStranger))
- [#298](https://github.com/serenity-bdd/serenity-core/pull/298) Added gitattributes ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [0340f82](https://github.com/serenity-bdd/serenity-core/commit/0340f82dcb76e3c72221cafdc334f94320409b23) fix: updating logging for serenity gradle plugin ([@YamStranger](https://github.com/YamStranger))
- [0e08c8c](https://github.com/serenity-bdd/serenity-core/commit/0e08c8c86f76f9c596dca271cb82f73f555421da) Use the correct name for the screenplay library for this version ([@wakaleo](https://github.com/wakaleo))
- [412657b](https://github.com/serenity-bdd/serenity-core/commit/412657bb56c87401a6e71e329e19374bcc93799d) fix: updating gradle plugin to work with new configuration ([@YamStranger](https://github.com/YamStranger))
- [4f1581c](https://github.com/serenity-bdd/serenity-core/commit/4f1581ce6369aaa7995552563df04c11d391465a) fix: updating getSessionId method to get session id without init new webdriver ([@YamStranger](https://github.com/YamStranger))
- [6f4f5d4](https://github.com/serenity-bdd/serenity-core/commit/6f4f5d461d969c0b675b81795cfc87e3c93d6a5c) fix: updating logging for serenity gradle plugin, using simple out stream ([@YamStranger](https://github.com/YamStranger))
- [720c516](https://github.com/serenity-bdd/serenity-core/commit/720c516a2a126fb44d5aeb9662b1b9df6d459e57) chore: added gitattributes ([@YamStranger](https://github.com/YamStranger))
- [909f21a](https://github.com/serenity-bdd/serenity-core/commit/909f21a66d8f7321495ec717b666b6a77f8b0d56) Renamed serenity-journey to serenity-screenplay ([@wakaleo](https://github.com/wakaleo))
 
 > Also allow conditional tasks of the following form:
 > dana.attemptsTo(
 > Check.whether(cost&gt;100)
 > .andIfSo(purchaseAPear)
 > .otherwise(purchaseAnApple)
 > );
 > Or with a Question&lt;Boolean&gt;:
 > dana.attemptsTo(
 > Check.whether(itIsTooExpensive)
 > .andIfSo(purchaseAPear)
 > .otherwise(purchaseAnApple)
 > );
- [91c3238](https://github.com/serenity-bdd/serenity-core/commit/91c32382040967d4ac57068f8ff2cb4656aad480) fix: updating version of serenityc-core and maven-plugin ([@YamStranger](https://github.com/YamStranger))
- [9d5fb9e](https://github.com/serenity-bdd/serenity-core/commit/9d5fb9e53e2a7c7592735d10046fb137fa073dae) style: updating test style ([@YamStranger](https://github.com/YamStranger))
- [bf8fca3](https://github.com/serenity-bdd/serenity-core/commit/bf8fca33efc9aa079eb81e9d0e3763c2eb308472) fix: remote driver session id can be under proxied driver ([@YamStranger](https://github.com/YamStranger))
- [cbc92cb](https://github.com/serenity-bdd/serenity-core/commit/cbc92cba6fc8792d46ae6d9df466797dd286ca57) fix: updated gradle plugin to work with multimodule projects ([@YamStranger](https://github.com/YamStranger))
- [d9c1e6a](https://github.com/serenity-bdd/serenity-core/commit/d9c1e6a65a28bb86ad80e76f593da6a6c6d364b4) style: updated name of test method ([@YamStranger](https://github.com/YamStranger))
- [ffdc3ef](https://github.com/serenity-bdd/serenity-core/commit/ffdc3efce06074d0b7b1f36525b96d027c994fb4) fix: updating requirements directory to be able work with multimodule projects ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.26-rc.1 (2016/02/06 15:40 +00:00)
 
**Pull requests:**
 
- [#293](https://github.com/serenity-bdd/serenity-core/pull/293) Updating gitattributes not to update chromedriver and woff files ([@YamStranger](https://github.com/YamStranger))
- [#289](https://github.com/serenity-bdd/serenity-core/pull/289) Updated RequirementPersister ([@YamStranger](https://github.com/YamStranger))
- [#291](https://github.com/serenity-bdd/serenity-core/pull/291) Git Attributes Experiment, please don't merge ([@YamStranger](https://github.com/YamStranger))
- [#288](https://github.com/serenity-bdd/serenity-core/pull/288) Git Attributes Experiment, please don't merge ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [3e0d991](https://github.com/serenity-bdd/serenity-core/commit/3e0d99168dbec1a580677df52287c6393139fc1b) Actors can now perform tasks conditionally ([@wakaleo](https://github.com/wakaleo))
 
 > Use the Unless class static methods and a bolean expression, e.g.
 > ```
 > Unless.the(items.isEmpty(), AddTodoItems.called(items))
 > ```
 > or use a question of type Question&lt;Boolean&gt;:
 > ```
 > Unless.the(itemsListisEmpty(), AddTodoItems.called(items))
 > ```
- [862b790](https://github.com/serenity-bdd/serenity-core/commit/862b790b16d85acd31d4e64213700e15c6cf6af0) Updated smoke tests ([@wakaleo](https://github.com/wakaleo))
- [b98f19f](https://github.com/serenity-bdd/serenity-core/commit/b98f19f058c80353804a61459854115c51d7d296) chore: updated gitattributes ([@YamStranger](https://github.com/YamStranger))
- [c6122c8](https://github.com/serenity-bdd/serenity-core/commit/c6122c8a49761495287233a068fba18cddb7a635) fix: fixed nullpointer if json config does not exists ([@YamStranger](https://github.com/YamStranger))
- [c9572c8](https://github.com/serenity-bdd/serenity-core/commit/c9572c849d9e710202dd01e4d75026952b869f1a) Actors can now perform tasks conditionally ([@wakaleo](https://github.com/wakaleo))
- [e71056d](https://github.com/serenity-bdd/serenity-core/commit/e71056d57767f35c6840445618a8fa2466f9eeae) fix: updated gitattributes ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.25 (2016/02/05 17:44 +00:00)
 
 
**Commits:**
 
- [5cb49b2](https://github.com/serenity-bdd/serenity-core/commit/5cb49b2e6ce64c52dbde7c00332ab1b6d403197d) Made reading UI values more fluent. ([@wakaleo](https://github.com/wakaleo))
 
 > The narrative was interrupted by the .value() so hidden away for now behind a more fluent method
- [c28a95e](https://github.com/serenity-bdd/serenity-core/commit/c28a95ec9310748f5cf38ed5b1575aabb10d0d65) Support for multiple matchers in Consequences ([@wakaleo](https://github.com/wakaleo))
- [ea5f1ad](https://github.com/serenity-bdd/serenity-core/commit/ea5f1adc05581fcf2f8ab3eaa010d0c70b3e9020) fix: restored gitattributes ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.25-rc.6 (2016/02/04 23:36 +00:00)
 
**Pull requests:**
 
- [#287](https://github.com/serenity-bdd/serenity-core/pull/287) Updating dependency for using in serenity modules ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [4b1537e](https://github.com/serenity-bdd/serenity-core/commit/4b1537ede3225027492b7ce35f013414d61310f1) Restored a renamed method to maintain backward compatibility. ([@wakaleo](https://github.com/wakaleo))
- [9e66e4b](https://github.com/serenity-bdd/serenity-core/commit/9e66e4bd6f5965972f4cbaf4f614b2de5b55f767) fix: updated commons-collection for jira/jbehave/cucumber modules ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.25-rc.5 (2016/02/04 15:06 +00:00)
 
 
**Commits:**
 
- [194f3d9](https://github.com/serenity-bdd/serenity-core/commit/194f3d966d780148be98061a22ee04ccc2dc213e) fix: updated report template generation ([@YamStranger](https://github.com/YamStranger))
- [2e2450a](https://github.com/serenity-bdd/serenity-core/commit/2e2450a32770d5e0f2c906f3886372a705df01a0) Tests can now manage whether cookies should be cleared between each test ([@wakaleo](https://github.com/wakaleo))
- [3e864ae](https://github.com/serenity-bdd/serenity-core/commit/3e864ae7bfeef596bf22ee3a802ddef0bf277771) fix: selenium version upgrade to 2.50.1 ([@YamStranger](https://github.com/YamStranger))
- [4858e0b](https://github.com/serenity-bdd/serenity-core/commit/4858e0b156973d105a73e6eda9e95c5cad114651) fix: updating report engine to wait results of report generation, stream and readers closing ([@YamStranger](https://github.com/YamStranger))
- [4e495f5](https://github.com/serenity-bdd/serenity-core/commit/4e495f56444915a7e6749a9ba4061ba2c0e50131) style: test updated ([@YamStranger](https://github.com/YamStranger))
- [85e23de](https://github.com/serenity-bdd/serenity-core/commit/85e23de89036021e0a0d4aad3e376b4d96944fcc) fix: update charset usage during reading/writing ([@YamStranger](https://github.com/YamStranger))
- [95449fe](https://github.com/serenity-bdd/serenity-core/commit/95449fed05268e1e3dff2c2475bed4814f341e4d) style: update test style ([@YamStranger](https://github.com/YamStranger))
- [c4a8fc1](https://github.com/serenity-bdd/serenity-core/commit/c4a8fc16ec943cad9d2fc35aa9271dadf63a6fc7) Refactoring and performance improvements ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.25-rc.4 (2016/02/03 17:49 +00:00)
 
**Pull requests:**
 
- [#284](https://github.com/serenity-bdd/serenity-core/pull/284) Upgrading selenium version to 2.50.1 ([@YamStranger](https://github.com/YamStranger))
- [#279](https://github.com/serenity-bdd/serenity-core/pull/279) Upgrading Charset ([@YamStranger](https://github.com/YamStranger))
- [#283](https://github.com/serenity-bdd/serenity-core/pull/283) Updating of report generation engine to wait for started threads, and close all opened streams ([@YamStranger](https://github.com/YamStranger))
- [#278](https://github.com/serenity-bdd/serenity-core/pull/278) Updated processing of system properties for OS and browser versions for BrowserStack ([@ovenal](https://github.com/ovenal))
- [#277](https://github.com/serenity-bdd/serenity-core/pull/277) Build script updating ([@YamStranger](https://github.com/YamStranger))
- [#275](https://github.com/serenity-bdd/serenity-core/pull/275) Moving definition of reportDirectory to within the tasks ([@jordanbragg](https://github.com/jordanbragg))
 
**Commits:**
 
- [0a9e50a](https://github.com/serenity-bdd/serenity-core/commit/0a9e50ae5782ba9ac9f733f84b0b0426e9a9ab26) Fixed #255 ([@wakaleo](https://github.com/wakaleo))
- [123e26d](https://github.com/serenity-bdd/serenity-core/commit/123e26d0bda8d05b6483c34f1011fbb3c57bfc7d) fix: updated processing of "browserstack.os.version" and "browserstack.browser.version" system properties according to latest changes on BrowserStack side ([@ovenal](https://github.com/ovenal))
- [1b3aa65](https://github.com/serenity-bdd/serenity-core/commit/1b3aa6568c4a0048abd09e97f4d902abb7358efb) Removed the .gitattribues file from git as it causes problems with the build pipeline on Snap-CI ([@wakaleo](https://github.com/wakaleo))
- [4531d43](https://github.com/serenity-bdd/serenity-core/commit/4531d43df845228fbde8693b87aadf62e51f3c52) Fixed issue #281 ([@wakaleo](https://github.com/wakaleo))
 
 > During verbose logging, Serenity tried to read the tag from web elements. This could cause failures if the element was stale or unavailable when the logging happen. This has now been changed to log the locator and not the element tag type.
- [55b06c1](https://github.com/serenity-bdd/serenity-core/commit/55b06c1ce3ecc501cd26ac4524e3fccb44ac1fd4) chore: updated wrapper, and build publishing libs ([@YamStranger](https://github.com/YamStranger))
- [737b1aa](https://github.com/serenity-bdd/serenity-core/commit/737b1aaf46a94ab4a75792ff9170b983dfe3eb80) chore: updated wrapper, and build publishing libs ([@YamStranger](https://github.com/YamStranger))
- [9429532](https://github.com/serenity-bdd/serenity-core/commit/9429532576a50ad12ff09e97ff064a377aeda0b3) Moving definition of reportDirectory in order to allow easy configuration through the serenity block. Currently this directory gets set when applying the plugin, which makes it only possible to change through setting an environment variable at the same level as applying the plugin. For multi-module projects with compile dependencies, this does not work
- [9eb9390](https://github.com/serenity-bdd/serenity-core/commit/9eb93903d47414aa03231ffc74a0e0dd6de5ffca) chore: updating gitignore ([@YamStranger](https://github.com/YamStranger))
- [9f1f6b5](https://github.com/serenity-bdd/serenity-core/commit/9f1f6b5c05f6c1b568a574a26f487f974badaaa6) Added an Action class to scroll to a particular eleemtn on the screen. ([@wakaleo](https://github.com/wakaleo))
- [ba8fcee](https://github.com/serenity-bdd/serenity-core/commit/ba8fcee2574ce65357829edece05228235ecab56) Hardened some of the integration tests ([@wakaleo](https://github.com/wakaleo))
- [e772cd1](https://github.com/serenity-bdd/serenity-core/commit/e772cd1b4d1773d01d9c05ec680a5f38fc417689) Attempt to make some of the tests more robust. ([@wakaleo](https://github.com/wakaleo))
- [e96d512](https://github.com/serenity-bdd/serenity-core/commit/e96d512758d18462ab14c2ad00ddee515a317ad7) style: updated test ([@YamStranger](https://github.com/YamStranger))
- [ed33d6a](https://github.com/serenity-bdd/serenity-core/commit/ed33d6a045ffeed0cf72eb41b8d9980e5e8f4dd4) Updated to Seleniy, 2.49.1 ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.25-rc.3 (2016/01/22 13:08 +00:00)
 
**Pull requests:**
 
- [#271](https://github.com/serenity-bdd/serenity-core/pull/271) #268 Fix issue with custom step name when a method parameter contains with comma ([@ovenal](https://github.com/ovenal))
- [#270](https://github.com/serenity-bdd/serenity-core/pull/270) BrowserMob Proxy, updated to use the new browsermob-core-littleproxy embedded mode ([@slawert](https://github.com/slawert))
- [#266](https://github.com/serenity-bdd/serenity-core/pull/266) Updated org.gradle.workers.max value to reduce memory usage ([@YamStranger](https://github.com/YamStranger))
- [#263](https://github.com/serenity-bdd/serenity-core/pull/263) Updating config to build subprojects in parallel ([@YamStranger](https://github.com/YamStranger))
- [#258](https://github.com/serenity-bdd/serenity-core/pull/258) Updating core to support report generation for multimodule projects with gradle/mvn ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [0a8a185](https://github.com/serenity-bdd/serenity-core/commit/0a8a1856c6c06b9e06037f7f30851dd8a5f91d61) chore: updated build to enable paralell build ([@YamStranger](https://github.com/YamStranger))
- [12b5943](https://github.com/serenity-bdd/serenity-core/commit/12b5943d2f7440734ffecb79c906f60ff62ca916) chore: updated org.gradle.workers.max value to reduce memory usage during build ([@YamStranger](https://github.com/YamStranger))
- [2462460](https://github.com/serenity-bdd/serenity-core/commit/246246020826f0356b5135abf28074a45e2f961a) fix: updated build task dependecies ([@YamStranger](https://github.com/YamStranger))
- [36225a5](https://github.com/serenity-bdd/serenity-core/commit/36225a5908e8b2c9995f4f3b9f296794a2d977c6) fix: report generation for multimodule builds ([@YamStranger](https://github.com/YamStranger))
- [418d37c](https://github.com/serenity-bdd/serenity-core/commit/418d37ca898fe72e0227a8cc642ed3dc9a09078f) chore: updated org.gradle.workers.max value to reduce memory usage during build ([@YamStranger](https://github.com/YamStranger))
- [42be3ba](https://github.com/serenity-bdd/serenity-core/commit/42be3ba710578c277f5166b341896f91c4a583d0) Browsermob update: using browsermob-core-littleproxy instead of old browsermob-proxy ([@slawert](https://github.com/slawert))
- [530450b](https://github.com/serenity-bdd/serenity-core/commit/530450bb5c94b712427e9fb14381832bd766eada) fix: updated resolution of output dir based on gradle/maven module ([@YamStranger](https://github.com/YamStranger))
- [60b1b99](https://github.com/serenity-bdd/serenity-core/commit/60b1b99079044a0e6203877725b437dcce4af4db) Fine-tuned the soft-assert tests and minor reporting bug  fix. ([@wakaleo](https://github.com/wakaleo))
- [864c00c](https://github.com/serenity-bdd/serenity-core/commit/864c00c9d01ce7bd5fb2d246e0c862e5abc7bcca) delted maven repo from build.gradle ([@slawert](https://github.com/slawert))
- [89eebf5](https://github.com/serenity-bdd/serenity-core/commit/89eebf56d85309eb1fb7ee7838034ba92d01209e) fix: serenity.properties can be located not in workin dir, but in gradle/maven module folder ([@YamStranger](https://github.com/YamStranger))
- [9d04622](https://github.com/serenity-bdd/serenity-core/commit/9d046220442e6d6ccc93732ab8b4def577b958ff) fix: customized step title if some parameter contains comma character ([@ovenal](https://github.com/ovenal))
- [9d6e27d](https://github.com/serenity-bdd/serenity-core/commit/9d6e27dcb4f231d5d7451fa3b7b910c9b70037c8) chore: updated org.gradle.workers.max value to reduce memory usage during build ([@YamStranger](https://github.com/YamStranger))
- [adc66f3](https://github.com/serenity-bdd/serenity-core/commit/adc66f3bd6142af886cd81a4bacb669ba12d69e5) added a test to check the test report output; updated previously failed tests for customized step title ([@ovenal](https://github.com/ovenal))
- [af3562d](https://github.com/serenity-bdd/serenity-core/commit/af3562dcf6686501347d840ddb3b982ed7a73f07) updated existing tests after changes in ExecutedStepDescription class ([@ovenal](https://github.com/ovenal))
- [c47b69d](https://github.com/serenity-bdd/serenity-core/commit/c47b69d154d32aa98cce294100a73764d405b9c9) chore: build test parallel execution enabled (PerCore) ([@YamStranger](https://github.com/YamStranger))
- [c9c9e1b](https://github.com/serenity-bdd/serenity-core/commit/c9c9e1b8a682ba3f1298542bd63e2e6ab85c0549) chore: turn off parallel execution of submodules ([@YamStranger](https://github.com/YamStranger))
- [e25d548](https://github.com/serenity-bdd/serenity-core/commit/e25d54860c4813063b17f804a8acccab3a047549) added the tests to cover storing arguments list in ExecutedStepDescription class ([@ovenal](https://github.com/ovenal))
- [f973f15](https://github.com/serenity-bdd/serenity-core/commit/f973f15b6c89d2eca9e0ed38e5ecf0b0ad83e684) fix: updated plugin to get serenity.properties from current module build dir ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.25-rc.2 (2016/01/14 18:42 +00:00)
 
 
**Commits:**
 
- [2b2d49d](https://github.com/serenity-bdd/serenity-core/commit/2b2d49d1ac787ac203ddc6e2b1132a6a12511112) Added support for By locators in Target objects and Action classes. ([@wakaleo](https://github.com/wakaleo))
- [511f607](https://github.com/serenity-bdd/serenity-core/commit/511f6079b8eb17d85dc89710ccfca22c9aa9110b) Added support for By locators in Target objects and Action classes. ([@wakaleo](https://github.com/wakaleo))
- [e2ea2ea](https://github.com/serenity-bdd/serenity-core/commit/e2ea2ea4999fe72ba1a8c2b0f319569ed0fbccdb) Updated smoketests ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.25-rc.1 (2016/01/13 14:31 +00:00)
 
**Pull requests:**
 
- [#253](https://github.com/serenity-bdd/serenity-core/pull/253) #183 Updating of gradle plugin to use same Configuration as SerenityRunner ([@YamStranger](https://github.com/YamStranger))
- [#252](https://github.com/serenity-bdd/serenity-core/pull/252) #250 Upgrading gradle and groovy ([@YamStranger](https://github.com/YamStranger))
- [#251](https://github.com/serenity-bdd/serenity-core/pull/251) Report for configuration ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [1f0dba1](https://github.com/serenity-bdd/serenity-core/commit/1f0dba14b3c4c559704508ba27076549f2903884) Updated smoke test dependencies ([@wakaleo](https://github.com/wakaleo))
- [5d17bd4](https://github.com/serenity-bdd/serenity-core/commit/5d17bd44a9869b057fb8859a649fcc5b399e7a83) chore: gradle take version from local variable ([@YamStranger](https://github.com/YamStranger))
- [5db32df](https://github.com/serenity-bdd/serenity-core/commit/5db32dfbc9fba66aa476e2e2febcbef853d50049) fix: update serenity-gradle-plugin to use same Configuration as Tests ([@YamStranger](https://github.com/YamStranger))
- [5f78a7b](https://github.com/serenity-bdd/serenity-core/commit/5f78a7b2ca9d63ed61a34551313a5f4bce6d9b98) style: changed style of one test ([@YamStranger](https://github.com/YamStranger))
- [777c061](https://github.com/serenity-bdd/serenity-core/commit/777c06110da4f2a3c41cb657ad23c50d959501f9) fix: report with properties should be in json ([@YamStranger](https://github.com/YamStranger))
- [7caec06](https://github.com/serenity-bdd/serenity-core/commit/7caec064fd2d81debbe42f45ae29aad2c8d0f741) Removed a redundant test ([@wakaleo](https://github.com/wakaleo))
- [8efe039](https://github.com/serenity-bdd/serenity-core/commit/8efe039bbb25754831b2691c0d8a21822720d181) chore: added report for configuration, with actual properties ([@YamStranger](https://github.com/YamStranger))
- [9e412bf](https://github.com/serenity-bdd/serenity-core/commit/9e412bfeb9d583924f87e1f39b245649049636f5) fix: report with properties should be in report folder ([@YamStranger](https://github.com/YamStranger))
- [a60d526](https://github.com/serenity-bdd/serenity-core/commit/a60d52606dfc71023c20a98ed1c96dbf4c64115e) fix: move reports about configuration to specific folder ([@YamStranger](https://github.com/YamStranger))
- [a610239](https://github.com/serenity-bdd/serenity-core/commit/a610239496c0955e66d5908b93a77534134e395a) fix: aggregation report generation in gradle plugin ([@YamStranger](https://github.com/YamStranger))
- [b944867](https://github.com/serenity-bdd/serenity-core/commit/b944867051253bd3c5562965c6059f615967a0fa) chore: upgrade groovy from 2.3.* to 2.4.4 ([@YamStranger](https://github.com/YamStranger))
- [c7bcd96](https://github.com/serenity-bdd/serenity-core/commit/c7bcd9643a1d3b7246bd96ffbdba9afc64416c13) Multiple assertions in the same should() method are now treated as "soft" asserts. ([@wakaleo](https://github.com/wakaleo))
- [d5b5401](https://github.com/serenity-bdd/serenity-core/commit/d5b5401f3adfc8f54838e310391191f858072f1b) chore: gradle to 2.10 and groovy to 2.4.4 upgraded ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.24 (2016/01/10 21:48 +00:00)
 
**Pull requests:**
 
- [#249](https://github.com/serenity-bdd/serenity-core/pull/249) Adding instructions for contributors ([@YamStranger](https://github.com/YamStranger))
- [#248](https://github.com/serenity-bdd/serenity-core/pull/248) #243 Upgrading typesafe.config from 1.2 to 1.3 ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [198a036](https://github.com/serenity-bdd/serenity-core/commit/198a036ac2e3ce4c87b651aa62d5ce27748bcfd6) Improved reporting of customised error messages in consequences ([@wakaleo](https://github.com/wakaleo))
- [1dbda1a](https://github.com/serenity-bdd/serenity-core/commit/1dbda1aec900a98ca8469c317a830ff7f1212731) docs: adding instructions of contributors ([@YamStranger](https://github.com/YamStranger))
- [32aba8a](https://github.com/serenity-bdd/serenity-core/commit/32aba8a8a1676a701b2d48a4f007c1ea322a4d59) Improved exception reporting ([@wakaleo](https://github.com/wakaleo))
- [b5a6c97](https://github.com/serenity-bdd/serenity-core/commit/b5a6c975fb31e82708eac40fbe0ae0888d2574f0) Improved exception reporting ([@wakaleo](https://github.com/wakaleo))
- [e240e7f](https://github.com/serenity-bdd/serenity-core/commit/e240e7f100216a17d47b5e97c5fc4e34b2536093) chore: upgrade typesafe.config to 1.3 from 1.2 ([@YamStranger](https://github.com/YamStranger))
- [f36115c](https://github.com/serenity-bdd/serenity-core/commit/f36115c4978f46008d5c2d897851930e71cc5357) Added matchers to allow questions about web element states (designed mainly to be used for low-level preconditions or assertions), e.g. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.15 (2016/01/05 14:12 +00:00)
 
 
**Commits:**
 
- [5944609](https://github.com/serenity-bdd/serenity-core/commit/594460938c8042341684b06295052ff20ca9f25a) When using a unique browser for multiple tests, clear the cookies and HTML local storage between tests. ([@wakaleo](https://github.com/wakaleo))
- [8d6dafa](https://github.com/serenity-bdd/serenity-core/commit/8d6dafa79e3917e106ea31fb562ecb13f1b61543) Fixed a bug in the reporting of Journey Pattern web actions. ([@wakaleo](https://github.com/wakaleo))
- [c200b32](https://github.com/serenity-bdd/serenity-core/commit/c200b3262d83a1d68cf885d477476c11df34d033) Improved the reporting of Journey pattern by removing redundant "is" clauses generated by the Hamcrest matchers. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.14 (2015/12/28 11:31 +00:00)
 
 
**Commits:**
 
- [182dfa0](https://github.com/serenity-bdd/serenity-core/commit/182dfa03c9c217c1da1af36cc9e87bc626dc095e) Refactored the Journey Pattern code ([@wakaleo](https://github.com/wakaleo))
- [a700aa2](https://github.com/serenity-bdd/serenity-core/commit/a700aa2d065449108086cbf4267e1df264c2e5fb) The Target class now accepts a prefix notation to specify the locator, e.g Target.the("name field").locatedBy("css:#name") or Target.the("name field").locatedBy("id:name") ([@wakaleo](https://github.com/wakaleo))
- [b658677](https://github.com/serenity-bdd/serenity-core/commit/b6586774ee26b6eca398bd34c0d3f9425babcd5d) Added the Evaluate action and the JavaScript question to perform JavaScript queries. ([@wakaleo](https://github.com/wakaleo))
- [e8d86a9](https://github.com/serenity-bdd/serenity-core/commit/e8d86a9b1562f229095e3865f66d81fd65be55bb) Refactored a journey pattern test to illustrate the displays matcher ([@wakaleo](https://github.com/wakaleo))
- [e9610ed](https://github.com/serenity-bdd/serenity-core/commit/e9610ed6fc8e76112a06e71858935f04a2970d42) Refactored the Enter action to allow entering text and keys in the same action ([@wakaleo](https://github.com/wakaleo))
- [ef0e61a](https://github.com/serenity-bdd/serenity-core/commit/ef0e61af836455ab573b97408436f21698f52e13) Updated the smoke tests. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.13 (2015/12/25 21:42 +00:00)
 
 
**Commits:**
 
- [0061513](https://github.com/serenity-bdd/serenity-core/commit/00615130588c8b2e0a01dcc8c480de95b4244a38) Added a method to the WebDriverManager instance to retreive a named webdriver instance. ([@wakaleo](https://github.com/wakaleo))
- [5fdf07c](https://github.com/serenity-bdd/serenity-core/commit/5fdf07c2b07a73123537243ba0d3ac7615900487) Fixed a bug where enums did not appear correctly in the test reports when they were referenced by Journey Pattern Questions. ([@wakaleo](https://github.com/wakaleo))
- [7e41d67](https://github.com/serenity-bdd/serenity-core/commit/7e41d67d6acaf5dea8b2855d7d1c4e7022615cff) Trying to fixe a performance issue related to resource copying ([@wakaleo](https://github.com/wakaleo))
- [8dad9cc](https://github.com/serenity-bdd/serenity-core/commit/8dad9ccfa05c76e16030e86a2fe19cc762965e19) Added the ability to use the Serenity WebDriver API directly in Action classes, by extending the WebAction class. ([@wakaleo](https://github.com/wakaleo))
- [c826177](https://github.com/serenity-bdd/serenity-core/commit/c8261771133d28ee703cbb9cf489c0bac1fd37c3) Refactored the bundled Journey Pattern action classes. ([@wakaleo](https://github.com/wakaleo))
- [edcdc4a](https://github.com/serenity-bdd/serenity-core/commit/edcdc4ad932fc6291b68ba61f5505e1e1879c29e) It is now possible to add page objects as member variables in Performable or Question classes - they will be correctly configured with the WebDriver instance associated with the actor. ([@wakaleo](https://github.com/wakaleo))
- [eec89ad](https://github.com/serenity-bdd/serenity-core/commit/eec89ad0c6d24884cccdc632c2bb3c102742f3f7) Fixed a bug that reported a misleading "class cast exception" when the moveTo() method was called after a test failure. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.12 (2015/12/21 15:20 +00:00)
 
**Pull requests:**
 
- [#224](https://github.com/serenity-bdd/serenity-core/pull/224) Pull request for adding reloading output dir before checks in tests ([@YamStranger](https://github.com/YamStranger))
- [#222](https://github.com/serenity-bdd/serenity-core/pull/222) Pull request for removing dependecy ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [0e4b788](https://github.com/serenity-bdd/serenity-core/commit/0e4b788b99f6a3ab92df73430bc1b1b47cc53065) 217_issue: removed old and never updated files ([@YamStranger](https://github.com/YamStranger))
- [32282fe](https://github.com/serenity-bdd/serenity-core/commit/32282fee3f80aaff98b8f972be4e031407ab6557) Updated smoketests to refactored journey pattern ([@wakaleo](https://github.com/wakaleo))
- [4c9a1b1](https://github.com/serenity-bdd/serenity-core/commit/4c9a1b1ec71d2489a72fd654891697def1b4bdfc) 223_issue: reloading result dir ([@YamStranger](https://github.com/YamStranger))
- [54721c4](https://github.com/serenity-bdd/serenity-core/commit/54721c4a11479df11c1177ed6138261950cd314a) 223_issue: added reloading output dir for tests ([@YamStranger](https://github.com/YamStranger))
- [8d42d0d](https://github.com/serenity-bdd/serenity-core/commit/8d42d0d2f40dea536930f1032f7f3f79e9ea0110) 223_issue: reverting updating of some imports ([@YamStranger](https://github.com/YamStranger))
- [9c8f3b2](https://github.com/serenity-bdd/serenity-core/commit/9c8f3b20036cdb6fd98fba128843fe4553bb374e) 217_issue: removed dependency org.mortbay.jetty:servlet-api-2.5:6.1.9, it is duplicated with javax.servlet:javax.servlet-api:3.1.0 ([@YamStranger](https://github.com/YamStranger))
- [c46729f](https://github.com/serenity-bdd/serenity-core/commit/c46729fb285850d38a179bf3be9e17eac0aeff92) 217_issue: style fix ([@YamStranger](https://github.com/YamStranger))
- [c6803e8](https://github.com/serenity-bdd/serenity-core/commit/c6803e834640e9dfe84d80a7bbdeabec8655a883) Readability improvements and moved the UI Action classes into their own package. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.11 (2015/12/07 07:09 +00:00)
 
**Pull requests:**
 
- [#220](https://github.com/serenity-bdd/serenity-core/pull/220) 218 issue ([@YamStranger](https://github.com/YamStranger))
- [#219](https://github.com/serenity-bdd/serenity-core/pull/219) 216_issue: update versions ([@YamStranger](https://github.com/YamStranger))
- [#213](https://github.com/serenity-bdd/serenity-core/pull/213) Pull request  for adding log and auth wrappers for serenity-rest-assured ([@YamStranger](https://github.com/YamStranger))
- [#210](https://github.com/serenity-bdd/serenity-core/pull/210) Pull request for updating SerenityRest to log all types of input ([@YamStranger](https://github.com/YamStranger))
- [#208](https://github.com/serenity-bdd/serenity-core/pull/208) fix: Fix for setting serenity.proxy.type and http_port. Needs to be a… ([@eskape](https://github.com/eskape))
- [#206](https://github.com/serenity-bdd/serenity-core/pull/206) fix: cglib dependency conflict from guice ([@schmurgon](https://github.com/schmurgon))
 
**Commits:**
 
- [2cec6f2](https://github.com/serenity-bdd/serenity-core/commit/2cec6f26c6f2f87d55a210c7449e175443007e4c) fix: Fix for setting serenity.proxy.type and http_port. Needs to be an number instead of string. ([@eskape](https://github.com/eskape))
- [5f8752f](https://github.com/serenity-bdd/serenity-core/commit/5f8752f8da66e0b102ecaea5cb5680924f8e1469) 216_issue: update versions ([@YamStranger](https://github.com/YamStranger))
- [7ab6c25](https://github.com/serenity-bdd/serenity-core/commit/7ab6c2502ff35f175f74db13750e8383b5ecf5eb) 197_issue: updated SerenityRest to log all types of input for content/body rest call ([@YamStranger](https://github.com/YamStranger))
- [7c4e0df](https://github.com/serenity-bdd/serenity-core/commit/7c4e0df97b5bb04652c1d7898b7859b3388ebfd6) fix: cglib dependency conflict from guice ([@schmurgon](https://github.com/schmurgon))
- [86ff95a](https://github.com/serenity-bdd/serenity-core/commit/86ff95a0f343746c459c26881ebdac0af4d12cc7) 216_issue: update versions ([@YamStranger](https://github.com/YamStranger))
- [c44f078](https://github.com/serenity-bdd/serenity-core/commit/c44f078c170cb7963d9703c9dd7fa72d6f9d955b) 218_issue: added test for checking if web scenarious executed successfully with HTMLUnit (fails now, so added @Ignore) ([@YamStranger](https://github.com/YamStranger))
- [f998b4a](https://github.com/serenity-bdd/serenity-core/commit/f998b4a55e25fe86468100a2a326d156344894d0) 185_issue: log and auth wrappers implemented, tests profivided. redirects still not supported ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.22-rc.10 (2015/11/24 23:42 +00:00)
 
**Pull requests:**
 
- [#202](https://github.com/serenity-bdd/serenity-core/pull/202) Pull requst for updating tests for screenshots ([@YamStranger](https://github.com/YamStranger))
- [#200](https://github.com/serenity-bdd/serenity-core/pull/200) see https://github.com/serenity-bdd/serenity-core/issues/179 ([@cliviu](https://github.com/cliviu))
- [#196](https://github.com/serenity-bdd/serenity-core/pull/196) Pull requst for 184_issue: logging for PATCH operation added ([@YamStranger](https://github.com/YamStranger))
- [#201](https://github.com/serenity-bdd/serenity-core/pull/201) Pull requst for adding input streams closing. Please don't merge if not success ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [81c87bb](https://github.com/serenity-bdd/serenity-core/commit/81c87bb9040cacb48f31e2c58516b4e356ee9842) Fixed an error with the screenshots that always displayed the screen source link, even for successful tests. ([@wakaleo](https://github.com/wakaleo))
- [9f90752](https://github.com/serenity-bdd/serenity-core/commit/9f9075212fc2f3c94c017822a98f7af731191028) Retry to unzip a resource file if it is locked. This is a work-around for Windows-related file locking issues. ([@wakaleo](https://github.com/wakaleo))
- [c9889ca](https://github.com/serenity-bdd/serenity-core/commit/c9889ca6204e9a5620190e10bed0e89ac1be83cb) Added more robustness to the report generation by allowing ZIP files to be opened again if they couldn't the first time ([@wakaleo](https://github.com/wakaleo))
- [e21e9a5](https://github.com/serenity-bdd/serenity-core/commit/e21e9a5c18b2e3cf8a1cd480ed2196b5ea5e4549) Updated tests ([@wakaleo](https://github.com/wakaleo))
- [fc8442b](https://github.com/serenity-bdd/serenity-core/commit/fc8442b03447e523904304a2ddca97f851cae38a) Restored step logging to INFO. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.9 (2015/11/23 00:33 +00:00)
 
 
**Commits:**
 
- [4267bd9](https://github.com/serenity-bdd/serenity-core/commit/4267bd936dc4dc3d34c4cb39113b23bc748547d7) updated Resizer. Fixed opening output and input stream in same time ([@YamStranger](https://github.com/YamStranger))
- [73015c2](https://github.com/serenity-bdd/serenity-core/commit/73015c29b2973663050af2d6b37fe140e96d279d) fix for nested cleaning resources ([@YamStranger](https://github.com/YamStranger))
- [79d2660](https://github.com/serenity-bdd/serenity-core/commit/79d266003d25a27fcf77c5428de94654f2adba2c) see https://github.com/serenity-bdd/serenity-core/issues/179 ([@cliviu](https://github.com/cliviu))
- [7ebca22](https://github.com/serenity-bdd/serenity-core/commit/7ebca229c0a391d1d629de27a16a0f3b13aca078) 184_issue: test added ([@YamStranger](https://github.com/YamStranger))
- [8d03dda](https://github.com/serenity-bdd/serenity-core/commit/8d03dda6b41d7f31673b1199c5284c9b408f7cd6) Updated tests for screenshots ([@YamStranger](https://github.com/YamStranger))
- [930f34c](https://github.com/serenity-bdd/serenity-core/commit/930f34cb355c18927591b1db971909626c6e699e) Record screen source code for failing tests. ([@wakaleo](https://github.com/wakaleo))
- [a7bdaeb](https://github.com/serenity-bdd/serenity-core/commit/a7bdaeb08399244f04288825cf0d9f926391c523) Updated input streams closing ([@YamStranger](https://github.com/YamStranger))
- [b5e5209](https://github.com/serenity-bdd/serenity-core/commit/b5e520997b94138d9e3cf8e2df9dc884e414e97b) 184_issue: logging for PATCH operation added ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.22-rc.8 (2015/11/19 20:53 +00:00)
 
 
**Commits:**
 
- [552172c](https://github.com/serenity-bdd/serenity-core/commit/552172c80ce760894a6f24ea5ba0e1c4b940efcc) Set the serenity.console.colors property to true to get ANSI colors in the console output (don't use on Jenkins). ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.7 (2015/11/19 10:55 +00:00)
 
 
**Commits:**
 
- [d4791cd](https://github.com/serenity-bdd/serenity-core/commit/d4791cdd90311c52d802fb4863308882e10bd7db) Fixed a bug that prevented @Pending annotations from working with non-instrumented Performable objects ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.6 (2015/11/19 04:43 +00:00)
 
**Pull requests:**
 
- [#191](https://github.com/serenity-bdd/serenity-core/pull/191) Pull request for 188_issue: new level of take screenshots configuration added ([@YamStranger](https://github.com/YamStranger))
- [#190](https://github.com/serenity-bdd/serenity-core/pull/190) Pull requst for minor Base Step Listener Constructor update ([@YamStranger](https://github.com/YamStranger))
- [#186](https://github.com/serenity-bdd/serenity-core/pull/186) Pull request for adding photographer test fix ([@YamStranger](https://github.com/YamStranger))
- [#187](https://github.com/serenity-bdd/serenity-core/pull/187) Pull request for 179_issue - added tests and fix for issue ([@YamStranger](https://github.com/YamStranger))
- [#181](https://github.com/serenity-bdd/serenity-core/pull/181) Pull requst for fix build fail  ([@YamStranger](https://github.com/YamStranger))
- [#182](https://github.com/serenity-bdd/serenity-core/pull/182) 130_issue: reading serenity.properties fix.  ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [0a1679f](https://github.com/serenity-bdd/serenity-core/commit/0a1679f36c025830d06318ed34f0263f6014dc1c) fix build fail by updating test-outcomes.ftl ([@YamStranger](https://github.com/YamStranger))
- [1af09f3](https://github.com/serenity-bdd/serenity-core/commit/1af09f3f475ab895b4a793f44b568c7e7e29ff57) 179_issue: added tests and fix for issue ([@YamStranger](https://github.com/YamStranger))
- [26219f7](https://github.com/serenity-bdd/serenity-core/commit/26219f70f9f59058babc24a6aad7c2a0e1646678) 130_issue: reading serenity.properties fix ([@YamStranger](https://github.com/YamStranger))
- [2830938](https://github.com/serenity-bdd/serenity-core/commit/283093853e20adf3e7f4911b1b6cec403abcc656) Made the console logging colors a bit lighter for better readability ([@wakaleo](https://github.com/wakaleo))
- [2d39575](https://github.com/serenity-bdd/serenity-core/commit/2d39575adf3fa359ef4bb40ed26fcb302e3d17f7) 130_issue: added system configuration for output dirrectory ([@YamStranger](https://github.com/YamStranger))
- [42691e1](https://github.com/serenity-bdd/serenity-core/commit/42691e1efb02790f2f3c18c776ded742e52e556e) Improved reporting ([@wakaleo](https://github.com/wakaleo))
- [4f189ca](https://github.com/serenity-bdd/serenity-core/commit/4f189ca2db7b9895e0e5ea60f44742b5840feb05) 188_issue: new level of take screenshots configuration added ([@YamStranger](https://github.com/YamStranger))
- [5099deb](https://github.com/serenity-bdd/serenity-core/commit/5099debaaa780538ffc911318e3c687f740709b6) Added color to logs ([@wakaleo](https://github.com/wakaleo))
- [597960b](https://github.com/serenity-bdd/serenity-core/commit/597960b30fe36a4f45af19822470bfa15bfcef3c) Minor Base Step Listener Constructor update ([@YamStranger](https://github.com/YamStranger))
- [5b77f9a](https://github.com/serenity-bdd/serenity-core/commit/5b77f9ac5c5174287a81d03204f99e768d1d39aa) Photographer cleanup fix. If driver not initialized - nothing to clean ([@YamStranger](https://github.com/YamStranger))
- [8846f6e](https://github.com/serenity-bdd/serenity-core/commit/8846f6e32637978946f8caab9b4b2c5a0b4347bb) Improved console log messages to cater for errors and failed assumptions ([@wakaleo](https://github.com/wakaleo))
- [8a21a9f](https://github.com/serenity-bdd/serenity-core/commit/8a21a9f2ca827b9ade4661f724f3f5c7a5e7160b) Improved reporting ([@wakaleo](https://github.com/wakaleo))
- [93b1411](https://github.com/serenity-bdd/serenity-core/commit/93b1411da124872dbf9213414c61d2d8dfee88f7) Added color to logs ([@wakaleo](https://github.com/wakaleo))
- [bc6e1f9](https://github.com/serenity-bdd/serenity-core/commit/bc6e1f9ea2d8fd35d590735341e4fddddb3cac2c) Reduce noise in the logs by removing an superfluous error message. ([@wakaleo](https://github.com/wakaleo))
- [e329275](https://github.com/serenity-bdd/serenity-core/commit/e329275237f7aa423bd97014b8c36e965b2125a0) Updated unit tests ([@wakaleo](https://github.com/wakaleo))
- [e7d7c85](https://github.com/serenity-bdd/serenity-core/commit/e7d7c85df2489c11793ac58cdb3252828dfe6e68) Updated unit tests ([@wakaleo](https://github.com/wakaleo))
- [ece74d2](https://github.com/serenity-bdd/serenity-core/commit/ece74d2b38035f37d81fcb2533ab5af496b27e3b) Remove an unnecessary moveTo() operation. ([@wakaleo](https://github.com/wakaleo))
- [ef3ca8d](https://github.com/serenity-bdd/serenity-core/commit/ef3ca8d849267389d05d7c009b36d0350b1fbdaf) 130_issue: added system environment properties to configuration ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.22-rc.4 (2015/11/12 08:47 +00:00)
 
 
**Commits:**
 
- [4c20cd5](https://github.com/serenity-bdd/serenity-core/commit/4c20cd5f39da75badad77faff249f8c3b1327383) Minor improvement to assertion reporting, to avoid lines being hidden for some assertions ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.3 (2015/11/12 07:39 +00:00)
 
 
**Commits:**
 
- [8edf62c](https://github.com/serenity-bdd/serenity-core/commit/8edf62c3dcbf93ab5718665af63463ca59d50e2a) Better handling of reporting arbitrary AssertionError exceptions. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.22-rc.2 (2015/11/12 06:03 +00:00)
 
**Pull requests:**
 
- [#176](https://github.com/serenity-bdd/serenity-core/pull/176) Pull request for desabling retries in smoke-tests ([@YamStranger](https://github.com/YamStranger))
- [#174](https://github.com/serenity-bdd/serenity-core/pull/174) Pull requst for fixing #128 issue ([@YamStranger](https://github.com/YamStranger))
- [#168](https://github.com/serenity-bdd/serenity-core/pull/168) Fixing getdrivername method to take this.driverClass instead of the g… ([@willhuang85](https://github.com/willhuang85))
- [#171](https://github.com/serenity-bdd/serenity-core/pull/171) Pull request for #132 issue ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [02b260e](https://github.com/serenity-bdd/serenity-core/commit/02b260e4116e90dc120d85e1d1f0f43050353f43) 132_issue: fixing incorrect test. Notifier should record failure ([@YamStranger](https://github.com/YamStranger))
- [1dae0a5](https://github.com/serenity-bdd/serenity-core/commit/1dae0a50729427007e8dfbced941d8b37622249f) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing description creating method ([@YamStranger](https://github.com/YamStranger))
- [27508e1](https://github.com/serenity-bdd/serenity-core/commit/27508e1975be3e4ce76a209c6b85c7f35deb2f3b) 132_issue: test and solution provided ([@YamStranger](https://github.com/YamStranger))
- [3eba4e8](https://github.com/serenity-bdd/serenity-core/commit/3eba4e88493fffc1f27416d1f49fc1c47bc7b8bb) 128_issue: updated checking Content Type according RFC1341, and added test for https rest tests based on github ([@YamStranger](https://github.com/YamStranger))
- [41361d0](https://github.com/serenity-bdd/serenity-core/commit/41361d014bac870cfb8b33b87a104247bc775105) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true ([@YamStranger](https://github.com/YamStranger))
- [632b09d](https://github.com/serenity-bdd/serenity-core/commit/632b09d0fb3388fe9ad2a95a0739da8f00e3891d) 132_issue: little refactoring, moving string to constants ([@YamStranger](https://github.com/YamStranger))
- [6ccd53e](https://github.com/serenity-bdd/serenity-core/commit/6ccd53e78491ebb40a94ebb66a69a8c34af9277a) Fixed #175 ([@wakaleo](https://github.com/wakaleo))
- [83496ff](https://github.com/serenity-bdd/serenity-core/commit/83496ffda0d8e41ee28e092ebe236e4b4d915713) Fine-tuned reporting ([@wakaleo](https://github.com/wakaleo))
- [8a671ce](https://github.com/serenity-bdd/serenity-core/commit/8a671ce7f4640e42d259ac0dfc2a6f883f3959fa) 132_issue: clean task fix ([@YamStranger](https://github.com/YamStranger))
- [9bcc664](https://github.com/serenity-bdd/serenity-core/commit/9bcc6643ff58626460bb31192571b80c7e719578) Better formatting for the result line at the bottom of the test outcome table ([@wakaleo](https://github.com/wakaleo))
- [a241bd8](https://github.com/serenity-bdd/serenity-core/commit/a241bd85dac63aa2756aa2748bd5360a97e4ea89) Test hardening ([@wakaleo](https://github.com/wakaleo))
- [a798480](https://github.com/serenity-bdd/serenity-core/commit/a798480e4e89951f86647c58f5808d9175008b2c) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing imports ([@YamStranger](https://github.com/YamStranger))
- [acf58d8](https://github.com/serenity-bdd/serenity-core/commit/acf58d8c842dc3f5372d0e1c83c58e2c1c05fc1d) 132_issue: fixing name of menthod in reports ([@YamStranger](https://github.com/YamStranger))
- [dc80964](https://github.com/serenity-bdd/serenity-core/commit/dc809646f1303970aae617154e0a8b265dcf81d5) 132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, fixing tests from different modules ([@YamStranger](https://github.com/YamStranger))
- [eb37b9b](https://github.com/serenity-bdd/serenity-core/commit/eb37b9b1589c016678c851cb18357504785fd819) 132_issue: desabling retries in smoke-tests ([@YamStranger](https://github.com/YamStranger))
- [f3e533f](https://github.com/serenity-bdd/serenity-core/commit/f3e533f87c0a853d7e086bf2172b70687efb1f9f) 128_issue: fixing style ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.22-rc.1 (2015/11/09 20:41 +00:00)
 
 
**Commits:**
 
- [1b62b2c](https://github.com/serenity-bdd/serenity-core/commit/1b62b2c1e4df337a3cf5e3169fedef7f84047d67) Removed redundant test ([@wakaleo](https://github.com/wakaleo))
- [3794e2b](https://github.com/serenity-bdd/serenity-core/commit/3794e2b28ed858cca60c4b1e662c0df08469250c) Improved reporting ([@wakaleo](https://github.com/wakaleo))
 
 > Add the &#39;serenity.linked.tags&#39; property, which allows you to defined tag types which will result in human-readable tags that can be used as bookmarks or external links.
- [3e05bb0](https://github.com/serenity-bdd/serenity-core/commit/3e05bb0b83686c9059dda4f1d3bb7b803f49558d) Updated a unit test ([@wakaleo](https://github.com/wakaleo))
- [73946ec](https://github.com/serenity-bdd/serenity-core/commit/73946ec52a54f9cdcfd9e7c50c9351ebc70047c0) Updated versions in the smoketests ([@wakaleo](https://github.com/wakaleo))
- [7a1c66f](https://github.com/serenity-bdd/serenity-core/commit/7a1c66f46acc6d45aa389915472693b72ae23d82) Made the WebdriverManager publicly available for advanced use cases. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.21 (2015/11/06 10:46 +00:00)
 
 
**Commits:**
 
- [4af8b65](https://github.com/serenity-bdd/serenity-core/commit/4af8b65a5867895e928dad79e3c964bfa89e16f3) Removed an incorrect reference to a Java 8 class ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.20 (2015/11/05 15:05 +00:00)
 
 
**Commits:**
 
- [00d0237](https://github.com/serenity-bdd/serenity-core/commit/00d0237227e7f39d2b01906d6114e1d30264492e) Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests ([@wakaleo](https://github.com/wakaleo))
- [0e77755](https://github.com/serenity-bdd/serenity-core/commit/0e777550299bd00773b005eb74013d3c1c19c017) Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.19 (2015/11/04 15:27 +00:00)
 
 
**Commits:**
 
- [ba52bc4](https://github.com/serenity-bdd/serenity-core/commit/ba52bc42560b4a09b789a98a13eea23c77c01829) Fixed a potential infinite loop in the report generation if image processing failed for some reason ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.18-rc.2 (2015/11/04 10:16 +00:00)
 
**Pull requests:**
 
- [#167](https://github.com/serenity-bdd/serenity-core/pull/167) #130 issue fix ([@YamStranger](https://github.com/YamStranger))
- [#166](https://github.com/serenity-bdd/serenity-core/pull/166) Pull request for updating test in serenity_core (ThucydidesWebDriverSupport init) ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [062d4da](https://github.com/serenity-bdd/serenity-core/commit/062d4daac873cb0ae440993da6e20fc2c566c778) Improved reporting ([@wakaleo](https://github.com/wakaleo))
- [0d48d61](https://github.com/serenity-bdd/serenity-core/commit/0d48d61cf44cc34f728eec29d6a4200c897d351d) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
- [356fbb9](https://github.com/serenity-bdd/serenity-core/commit/356fbb9f80c6789750c93bea253bc87ef7295799) 130_issue: added test and default project for gradle plugin ([@YamStranger](https://github.com/YamStranger))
- [59a3cb2](https://github.com/serenity-bdd/serenity-core/commit/59a3cb2a96f7a399aff7e13e79bacc6faa3817d2) 130_issue: spelling error fix
- [60b4922](https://github.com/serenity-bdd/serenity-core/commit/60b4922421276c54cf863bb3c3cd52900b517f46) 130_issue: build.config updated for simple project for serenity-gradle_plugin ([@YamStranger](https://github.com/YamStranger))
- [785c7be](https://github.com/serenity-bdd/serenity-core/commit/785c7bedb82ce420b25e9fdaf44f082d448c2e09) Finished merge ([@wakaleo](https://github.com/wakaleo))
- [89a443f](https://github.com/serenity-bdd/serenity-core/commit/89a443f17da25d0f0c562692fe7d520e11c58023) Finished merge ([@wakaleo](https://github.com/wakaleo))
- [920e4cf](https://github.com/serenity-bdd/serenity-core/commit/920e4cf20364baa0bf592e35ad6c7ec1868f93e3) 130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin ([@YamStranger](https://github.com/YamStranger))
- [af55a04](https://github.com/serenity-bdd/serenity-core/commit/af55a048e07a081a69a182bb992940072dd4954a) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
- [b4bbc31](https://github.com/serenity-bdd/serenity-core/commit/b4bbc317f98a23a6cc45eee7e6ca082acdf91945) 130_issue: removing unused dependencies ([@YamStranger](https://github.com/YamStranger))
- [ca0ffa0](https://github.com/serenity-bdd/serenity-core/commit/ca0ffa080140a93fa555dcf9bd1991bb38c41bf6) Fixing getdrivername method to take this.driverClass instead of the getter since the getter may not return a SupportedDriver anymore ([@willhuang85](https://github.com/willhuang85))
- [d2c156d](https://github.com/serenity-bdd/serenity-core/commit/d2c156debf5b8d151056e740951b1c56e7e4f0c4) 130_issue: removed emtpy lines ([@YamStranger](https://github.com/YamStranger))
- [dcab9c6](https://github.com/serenity-bdd/serenity-core/commit/dcab9c63c6952def4092362074c2e422732e5bed) updating test to use ThucydidesWebDriverSupport ([@YamStranger](https://github.com/YamStranger))
- [e28f899](https://github.com/serenity-bdd/serenity-core/commit/e28f899a152e79846ea4e940d9b129738eeefea9) 130_issue: fixing copying jars bug ([@YamStranger](https://github.com/YamStranger))
- [f6464d1](https://github.com/serenity-bdd/serenity-core/commit/f6464d1224fd8d57d9246ea12a39219537e46667) updating tests for using ThucydesWebDriverSupport ([@YamStranger](https://github.com/YamStranger))
 
### v1.1.18-rc.1 (2015/10/31 12:10 +00:00)
 
**Pull requests:**
 
- [#161](https://github.com/serenity-bdd/serenity-core/pull/161) gradle plugin update for #130 issue ([@YamStranger](https://github.com/YamStranger))
 
**Commits:**
 
- [0c10504](https://github.com/serenity-bdd/serenity-core/commit/0c105044830f7973b7a80a57c970fb9e005e7c2e) 130_issue: added test and default project for gradle plugin ([@YamStranger](https://github.com/YamStranger))
- [28be7ba](https://github.com/serenity-bdd/serenity-core/commit/28be7ba00561d2f72f386ee6e6672c715189cc03) 130_issue: build.config updated for simple project for serenity-gradle_plugin ([@YamStranger](https://github.com/YamStranger))
- [322e572](https://github.com/serenity-bdd/serenity-core/commit/322e572db71d9d6b5d4b17a025242b2ffb331613) 130_issue: spelling error fix
 
 > This reverts commit 4397786f9fd7f37cb6c2e4f00741a2343e9e4d57, reversing
 > changes made to 84d095558dcd61554c2a0a988977bb1e9eecb71d.
- [84d0955](https://github.com/serenity-bdd/serenity-core/commit/84d095558dcd61554c2a0a988977bb1e9eecb71d) Refactoring of the report generation code to rectify #160 ([@wakaleo](https://github.com/wakaleo))
- [8fedb54](https://github.com/serenity-bdd/serenity-core/commit/8fedb5437877646684a733cd134427652f9b19ad) Refactoring WebDriver integration to use the ThucydidesWebDriverSupport class ([@wakaleo](https://github.com/wakaleo))
- [a1979bb](https://github.com/serenity-bdd/serenity-core/commit/a1979bb7f938344e301e6aafa8577d5432fac1e1) 130_issue: removed emtpy lines ([@YamStranger](https://github.com/YamStranger))
- [a4dc59d](https://github.com/serenity-bdd/serenity-core/commit/a4dc59d0aa64b2d2329262e738448f3385a68f4a) Fixed typo in the smoketests ([@wakaleo](https://github.com/wakaleo))
- [ad77459](https://github.com/serenity-bdd/serenity-core/commit/ad774592904ec3e45d148f449f4eb5aeb356eead) Made the instantiation of test steps more robust, mainly for use in the Journey pattern ([@wakaleo](https://github.com/wakaleo))
- [c497bb6](https://github.com/serenity-bdd/serenity-core/commit/c497bb6b231cdb7beec07165b1c5011aefc9718a) Updating dependencies for the smoketest project ([@wakaleo](https://github.com/wakaleo))
- [c66f0fe](https://github.com/serenity-bdd/serenity-core/commit/c66f0fe7113da4aa78838d14acf80990b33354e2) Fixed an issue with the reporting of pending and skipped tests ([@wakaleo](https://github.com/wakaleo))
- [d5883db](https://github.com/serenity-bdd/serenity-core/commit/d5883dbac1b97c51f0398d5a6a72b371aee82b35) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
- [d9eca6e](https://github.com/serenity-bdd/serenity-core/commit/d9eca6e184af361ce71a5b2a072a88727be3f42a) Fixed typo in the smoketests ([@wakaleo](https://github.com/wakaleo))
- [e8607af](https://github.com/serenity-bdd/serenity-core/commit/e8607afe6fb999876cb6629570cbb6d27d767b84) 130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin ([@YamStranger](https://github.com/YamStranger))
- [ee6807e](https://github.com/serenity-bdd/serenity-core/commit/ee6807e845a92939e89d1f38f95af798599b5378) 130_issue: updated simple-gradle-project for serenity-gradle-plugin
 
### v1.1.17 (2015/10/28 08:22 +00:00)
 
 
**Commits:**
 
- [032dbb6](https://github.com/serenity-bdd/serenity-core/commit/032dbb615134963a8986683aaab15764e55c5cf7) Added a general solution for defining or overriding how exceptions should be reported. ([@wakaleo](https://github.com/wakaleo))
- [3c39729](https://github.com/serenity-bdd/serenity-core/commit/3c397299c342b15b85a0f345735cc6b755e75886) You can use the serenity.pending.on property to define exceptions that will cause a test to be marked as Pending. ([@wakaleo](https://github.com/wakaleo))
- [d2f951a](https://github.com/serenity-bdd/serenity-core/commit/d2f951a2b2826591e697fffe84eadf4c9465c7fe) Added the serenity.error.on, serenity.fail.on and serenity.pending.on properties to the ThucydidesSystemProperty class. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.17-rc.4 (2015/10/28 01:41 +00:00)
 
 
**Commits:**
 
- [01a6e9d](https://github.com/serenity-bdd/serenity-core/commit/01a6e9d3a51cff8ebc4304aef1f06b8a31fce9bd) Improved reporting ([@wakaleo](https://github.com/wakaleo))
 
 > Use FontAwesome for more readable test result icons.
- [157c616](https://github.com/serenity-bdd/serenity-core/commit/157c616b2f9b51932e3e3ae0d58b8feace1dff0d) Trim Appium system properties ([@wakaleo](https://github.com/wakaleo))
- [271ffe1](https://github.com/serenity-bdd/serenity-core/commit/271ffe108f5880f0df172211c393ae0ed5031b1d) Added support for customising exception handling. ([@wakaleo](https://github.com/wakaleo))
 
 > You can now specify your own exceptions that will cause a failure by using the /serenity.fail.on/ property. You can also specify those that will cause an error using /serenity.error.on/.
- [2e0752d](https://github.com/serenity-bdd/serenity-core/commit/2e0752d93e05f480cf1a12412ddca7b46605ffe9) Added a more meaningful error message if a resource file cannot be copied. ([@wakaleo](https://github.com/wakaleo))
- [2e959d4](https://github.com/serenity-bdd/serenity-core/commit/2e959d4fef6356f8de808f4bb55d38b353e06420) Fixed some broken tests ([@wakaleo](https://github.com/wakaleo))
- [5d1b871](https://github.com/serenity-bdd/serenity-core/commit/5d1b871b917e0c02d957d6c7885c5af57dbe3f67) Fixed an error in the freemarker templates. ([@wakaleo](https://github.com/wakaleo))
- [5fdc2be](https://github.com/serenity-bdd/serenity-core/commit/5fdc2be5bf86270f6c0ff1f9a3920b997a99b8bc) Refactoring ([@wakaleo](https://github.com/wakaleo))
- [7de1dd5](https://github.com/serenity-bdd/serenity-core/commit/7de1dd5ba3fe50829c9a086fa6fbd35adf57712e) Better error/failure distinction ([@wakaleo](https://github.com/wakaleo))
 
 > Exceptions such as ElementShouldBeInvisibleException are now reported as failures, not errors.
- [b2c29a9](https://github.com/serenity-bdd/serenity-core/commit/b2c29a9ed0f6e51807ff95a55d4f285f59b399c9) Improved report icons ([@wakaleo](https://github.com/wakaleo))
- [bafaead](https://github.com/serenity-bdd/serenity-core/commit/bafaead4743dd9d80733ab5f2ea9ab92356fc864) Fixed some broken tests ([@wakaleo](https://github.com/wakaleo))
- [c584505](https://github.com/serenity-bdd/serenity-core/commit/c58450593b567a1eb5af9b82c689c947ba4776cd) Fixed some issues related to displaying manual tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.17-rc.3 (2015/10/22 10:50 +00:00)
 
**Pull requests:**
 
- [#154](https://github.com/serenity-bdd/serenity-core/pull/154) Better way to fix THUCYDIDES-253 ([@willhuang85](https://github.com/willhuang85))
- [#152](https://github.com/serenity-bdd/serenity-core/pull/152) fix: stop further steps execution if assumption failed ([@frouleau](https://github.com/frouleau))
 
**Commits:**
 
- [4d6e9bc](https://github.com/serenity-bdd/serenity-core/commit/4d6e9bc104b4a2773d7d29cea766f82653b1f2db) Refining support for multi-thread report generation to avoid contention on resource files ([@wakaleo](https://github.com/wakaleo))
- [652c048](https://github.com/serenity-bdd/serenity-core/commit/652c048ac985d1f3864eb90bdf4172066afbfbc6) Test that checks to see if the proxy driver class is returned when the the driver class is the provided driver ([@willhuang85](https://github.com/willhuang85))
- [93f8e34](https://github.com/serenity-bdd/serenity-core/commit/93f8e34a2747219c6756abf3d475a9f4ef5566ab) Refactoring the html resource copying code. ([@wakaleo](https://github.com/wakaleo))
- [ad3af93](https://github.com/serenity-bdd/serenity-core/commit/ad3af93eda7fa4a9bfbc47d1a94b151ef37bbc25) Having ProvidedDriver implement JavascriptExecutor should not be the correct way to fix THUCYDIDES-253. The method that checks if the driver is javascript enabled looks at the driver class returned from WebDriverFacade and in the case, it will see that ProvidedDriver implements JavascriptExecutor but when it tries to execute javascript on the proxied driver that does not necessarily have to implement JavascriptExecutor, then it will throw a method not found exception. This proposed fix checks if the driverclass in the WebDriverFacade is a provided driver, if it is, then the correct driver class it should look at is contained in the proxied driver. ([@willhuang85](https://github.com/willhuang85))
- [b16d29a](https://github.com/serenity-bdd/serenity-core/commit/b16d29a29509c8d15e198712a5a723d16576718c) Refactoring ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.17-rc.2 (2015/10/22 07:26 +00:00)
 
**Pull requests:**
 
- [#149](https://github.com/serenity-bdd/serenity-core/pull/149) refactor: Corrects throwning of IOException, instead of Exception ([@verhagen](https://github.com/verhagen))
- [#148](https://github.com/serenity-bdd/serenity-core/pull/148) fix loop when parameter is null in ddt tests ([@AlexeyDybov](https://github.com/AlexeyDybov))
 
**Commits:**
 
- [0bcdbcf](https://github.com/serenity-bdd/serenity-core/commit/0bcdbcfb461f8cec034c28a86c5bbb3ba7f8b671) refactor: Corrects throwning of IOException, instead of Exception
- [13edac4](https://github.com/serenity-bdd/serenity-core/commit/13edac4e25606faced4e5e3fa1a4f2dccd246cb2) Refactoring of a solution to avoid contention on resource JAR files. ([@wakaleo](https://github.com/wakaleo))
- [169b5e8](https://github.com/serenity-bdd/serenity-core/commit/169b5e8fd721a0ef6d8f5891d27e59bf152024d4) Refactoring ([@wakaleo](https://github.com/wakaleo))
- [496320a](https://github.com/serenity-bdd/serenity-core/commit/496320a0c08f381c8d483b3e8f2bd6ee90e3fa79) Ensure that HTML report resource files are only copied if they are not already present. ([@wakaleo](https://github.com/wakaleo))
- [80dc190](https://github.com/serenity-bdd/serenity-core/commit/80dc1909929b502f2cd442bad0140861b0f83c9a) Fixed a broken test ([@wakaleo](https://github.com/wakaleo))
- [8cff02b](https://github.com/serenity-bdd/serenity-core/commit/8cff02bf05a93bff27873064f02ebce2cae9d9e7) fix: stop further steps execution if test is suspended
- [93337a7](https://github.com/serenity-bdd/serenity-core/commit/93337a754212f36ce2877697bfb4337b87487fde) fix: correct unit test ([@frouleau](https://github.com/frouleau))
- [a83934e](https://github.com/serenity-bdd/serenity-core/commit/a83934edd78de54f492bff1291b3342e3ca6f064) Refactoring ([@wakaleo](https://github.com/wakaleo))
- [e0b51a4](https://github.com/serenity-bdd/serenity-core/commit/e0b51a437d355f9d30cbc1ecf0c2bda64615bcab) fix loop when parameter is null in ddt tests
- [e957030](https://github.com/serenity-bdd/serenity-core/commit/e957030c91106c4852d4692095c7a0fd50104507) Refactored redundant tests ([@wakaleo](https://github.com/wakaleo))
- [ea10f23](https://github.com/serenity-bdd/serenity-core/commit/ea10f238c42937384411c50d282ad7afe86de64a) Refactoring ([@wakaleo](https://github.com/wakaleo))
- [f195e49](https://github.com/serenity-bdd/serenity-core/commit/f195e492df7618f7440f24f69d86d28cc48d9d00) Ignore warnings if we try to save a screenshot that already exists. ([@wakaleo](https://github.com/wakaleo))
- [fe753a8](https://github.com/serenity-bdd/serenity-core/commit/fe753a880bb2dff6932aa9991b3972456a4043a9) Fixed a test ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.17-rc.1 (2015/10/18 14:57 +00:00)
 
**Pull requests:**
 
- [#143](https://github.com/serenity-bdd/serenity-core/pull/143) Fixes the project Serenity Demo - based on branch master ([@verhagen](https://github.com/verhagen))
- [#146](https://github.com/serenity-bdd/serenity-core/pull/146) Maintain JDK7 compatibility ([@mikezx6r](https://github.com/mikezx6r))
 
**Commits:**
 
- [01e59d1](https://github.com/serenity-bdd/serenity-core/commit/01e59d1a7199b2701b2cebde296e3fcd3f1032df) Fine-tuned the smoke test app ([@wakaleo](https://github.com/wakaleo))
- [0b94e8d](https://github.com/serenity-bdd/serenity-core/commit/0b94e8daba308bc17106d164eb35a62a2070c497) Minor refactoring ([@wakaleo](https://github.com/wakaleo))
 
 > Added multi-thread testing for the screenshot pipeline, and removed misleading warnings that could happen when two threads try to save the same screenshot.
- [1b84d2e](https://github.com/serenity-bdd/serenity-core/commit/1b84d2e7d7b33aa25051cd53f08c4373b51fc486) fix: Handle empty screenshots without crashing. ([@wakaleo](https://github.com/wakaleo))
- [2341b74](https://github.com/serenity-bdd/serenity-core/commit/2341b7409624050c46d90fbe2b7c9eb397e5fb66) [JDK7 compatibility] Corrections to maintain JDK7 compatibility ([@mikezx6r](https://github.com/mikezx6r))
 
 > Replace usage of java Optional with Guava optional
- [37aa19d](https://github.com/serenity-bdd/serenity-core/commit/37aa19d2ddef10c1952c2a59c12fa3bea7738c50) Added smoke tests ([@wakaleo](https://github.com/wakaleo))
- [3dddb91](https://github.com/serenity-bdd/serenity-core/commit/3dddb91bd54347d20f786db09fc1fbc243c984f6) Added a new sample data-driven test to the smoke tests ([@wakaleo](https://github.com/wakaleo))
- [4f95fd3](https://github.com/serenity-bdd/serenity-core/commit/4f95fd346b7419e7766f8971ae90bac42914281e) Removed old screenshot processing logic ([@wakaleo](https://github.com/wakaleo))
- [577dacf](https://github.com/serenity-bdd/serenity-core/commit/577dacf7f77758337c3ab10fdefad628dede64c3) chore: General test refactoring. ([@wakaleo](https://github.com/wakaleo))
- [64af9ac](https://github.com/serenity-bdd/serenity-core/commit/64af9acd1d8d9da8bd05d68facb6396cde1fcf90) The withTestDataFrom() method now accepts a list of strings as well as a CSV file. ([@wakaleo](https://github.com/wakaleo))
- [70db76d](https://github.com/serenity-bdd/serenity-core/commit/70db76ded0802144ab477005d4e188579ae32f89) Fine-tuned the smoke test app ([@wakaleo](https://github.com/wakaleo))
- [825328a](https://github.com/serenity-bdd/serenity-core/commit/825328a5cd76f7dad2750fc7058cb58cb6066f45) Added the Todo app smoke tests ([@wakaleo](https://github.com/wakaleo))
- [929e14d](https://github.com/serenity-bdd/serenity-core/commit/929e14da0922d4b57f8fd697220a23eb942e8a93) Added additional tests to the smoke test suite ([@wakaleo](https://github.com/wakaleo))
- [ca53d5e](https://github.com/serenity-bdd/serenity-core/commit/ca53d5e75470fefe449679308e0aaf31f7a869c0) chore:test hardening ([@wakaleo](https://github.com/wakaleo))
- [d49c8b2](https://github.com/serenity-bdd/serenity-core/commit/d49c8b2f488adfea284d4b5376e95c38394f1150) Added smoketest tags to illustrate using tags. ([@wakaleo](https://github.com/wakaleo))
- [e07f250](https://github.com/serenity-bdd/serenity-core/commit/e07f25002f7ec858756913cc8218c49c8ea9352d) fix: Removed a potential issue where the screenshot target directory was not created correctly ([@wakaleo](https://github.com/wakaleo))
- [e1525d3](https://github.com/serenity-bdd/serenity-core/commit/e1525d38bca5fc954f7791fa4242a0feb41d0a57) Made the screenshot processing a bit more robust ([@wakaleo](https://github.com/wakaleo))
- [f20daf7](https://github.com/serenity-bdd/serenity-core/commit/f20daf748843c6b474311018c83dd84272474a88) Refactored the screenshot processing logic ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.16 (2015/10/13 04:18 +00:00)
 
 
**Commits:**
 
- [3956327](https://github.com/serenity-bdd/serenity-core/commit/395632770ed8fc1d8a79adf434ea6317d1558cfc) Just trigger rebuild ([@verhagen](https://github.com/verhagen))
- [3fc89be](https://github.com/serenity-bdd/serenity-core/commit/3fc89becc4c606b381dee80facc15471d605e7b4) Removed unnused imports ([@wakaleo](https://github.com/wakaleo))
- [632a91a](https://github.com/serenity-bdd/serenity-core/commit/632a91a024abd3be602f1ad0ab4c7e1a0bb7823b) Refactoring ([@wakaleo](https://github.com/wakaleo))
- [975f25e](https://github.com/serenity-bdd/serenity-core/commit/975f25e90c30c2588b7dfbfa03e9bbb8d02d3fdb) Updated dependencies ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.15 (2015/10/11 22:49 +00:00)
 
**Pull requests:**
 
- [#120](https://github.com/serenity-bdd/serenity-core/pull/120) Fixing Java warnings - Redundant cast ([@HNygard](https://github.com/HNygard))
- [#91](https://github.com/serenity-bdd/serenity-core/pull/91) feat: Dropdown selectByValue() ([@kassemsandarusi](https://github.com/kassemsandarusi))
- [#134](https://github.com/serenity-bdd/serenity-core/pull/134) Fixed project build on Windows ([@joxerTMD](https://github.com/joxerTMD))
 
**Commits:**
 
- [0068f01](https://github.com/serenity-bdd/serenity-core/commit/0068f01d15df7dcdc120d5db1017d139d221757e) Deprecated old screenshot processor ([@wakaleo](https://github.com/wakaleo))
- [020fd6f](https://github.com/serenity-bdd/serenity-core/commit/020fd6fa6e101f659b812f434fe3114259b5f71f) build:refactoring test phase ([@wakaleo](https://github.com/wakaleo))
- [0bfbd95](https://github.com/serenity-bdd/serenity-core/commit/0bfbd9578a80bf1ad0899f3dfc91fb77725d8568) refactor:Removed old screenshot logic ([@wakaleo](https://github.com/wakaleo))
- [1381ebd](https://github.com/serenity-bdd/serenity-core/commit/1381ebdaa95514675adcdc6e175824b05d42e285) refactor: Removes warning that log4j was not initialized ([@verhagen](https://github.com/verhagen))
 
 > Updates thucydides-core with exclusion of log4j
 > Adds dependency log4j-over-slf4j
- [1599557](https://github.com/serenity-bdd/serenity-core/commit/1599557cf37ab9dc40166538db1fc3f9e53681e6) Added new implementation of the screenshot logic ([@wakaleo](https://github.com/wakaleo))
- [20f7f30](https://github.com/serenity-bdd/serenity-core/commit/20f7f3075e538f307792c81c780c5c7469ccf20a) Fixed an error in the reporting in the Hit interaction ([@wakaleo](https://github.com/wakaleo))
- [252524c](https://github.com/serenity-bdd/serenity-core/commit/252524c9056b0471f121c352967155fb488f404e) Inital version of a new implementation of the screenshot logic. ([@wakaleo](https://github.com/wakaleo))
- [29b129c](https://github.com/serenity-bdd/serenity-core/commit/29b129c8ef92e3c3bfa7ffc7697b8b42b67d810e) refactor:Better error handling for screenshots ([@wakaleo](https://github.com/wakaleo))
- [33fac2e](https://github.com/serenity-bdd/serenity-core/commit/33fac2e859d6bc7cb43b57bfb74aaac55200e091) Removes unused imports ([@verhagen](https://github.com/verhagen))
- [3653885](https://github.com/serenity-bdd/serenity-core/commit/365388539e0dfc3cb9f9a98a4616a4ab98997156) docs: Adds description about the Serenity Demo ([@verhagen](https://github.com/verhagen))
- [4318dc2](https://github.com/serenity-bdd/serenity-core/commit/4318dc2a8180f751c430754904abbf8e38fcf50c) Removes warning 'Use static field LoggingLevel.VERBOSE' ([@verhagen](https://github.com/verhagen))
- [461c784](https://github.com/serenity-bdd/serenity-core/commit/461c7843410b9f41eae142acece60a20db7e3574) fix: Checks if driver is not null (before calling close() ) ([@verhagen](https://github.com/verhagen))
- [476a183](https://github.com/serenity-bdd/serenity-core/commit/476a18322150cbba6439a96ee9492243434ad476) fix: Updates dependencies to latest stable release 0.8 ([@verhagen](https://github.com/verhagen))
 
 > thucydides-junit 0.8.31 (was 0.8.1-SNAPSHTOT)
 > thucydides-core  0.8.31 (was 0.8.1-SNAPSHTOT)
 > Adds dependency
 > slf4j-simple  1.6.4
- [4bfc541](https://github.com/serenity-bdd/serenity-core/commit/4bfc541c331a6e9e93d4bd873ca151911c733ea2) refactor:Better error handling for screenshots ([@wakaleo](https://github.com/wakaleo))
- [4cf4c11](https://github.com/serenity-bdd/serenity-core/commit/4cf4c1123a50402423ef1a9d98dc4dbf7fb28c5f) Removes generics warning ([@verhagen](https://github.com/verhagen))
- [52a42c4](https://github.com/serenity-bdd/serenity-core/commit/52a42c40edd6b9b6681ed4db16d5a98a4d7ab9c1) Fixes generics issue (no longer warning) ([@verhagen](https://github.com/verhagen))
- [540ce87](https://github.com/serenity-bdd/serenity-core/commit/540ce87d44b93cb6f13a0c070658b4d84aa78dd9) fix: Adds JAVA_HOME to the environment (map) in case the key / value is ([@verhagen](https://github.com/verhagen))
 
 > not available from the System.getenv()
- [5556dda](https://github.com/serenity-bdd/serenity-core/commit/5556ddae9469a43e829046a999ed505ac9a0a2a1) chore: Adds profiles 'firefox' and 'chrome', for easier running the ([@verhagen](https://github.com/verhagen))
 
 > tests with different browsers.
- [5e392c9](https://github.com/serenity-bdd/serenity-core/commit/5e392c973fb53eb27213d0668b6b0e788653291a) refactor:Removed old screenshot logic ([@wakaleo](https://github.com/wakaleo))
- [60fa70b](https://github.com/serenity-bdd/serenity-core/commit/60fa70b080f1200dc9d986b88b5ec9a536f8765f) refactor:Better error handling for screenshots ([@wakaleo](https://github.com/wakaleo))
- [6148fe2](https://github.com/serenity-bdd/serenity-core/commit/6148fe2833f5e7fb6d4de327ae27447538a07661) docs: Adds description how to correct add chrome-web-driver ([@verhagen](https://github.com/verhagen))
- [618a813](https://github.com/serenity-bdd/serenity-core/commit/618a81345bdd93c99f2d16f3fde130dd9f21815e) Removes unused variable registeredWebdriverManagers ([@verhagen](https://github.com/verhagen))
- [6200d4e](https://github.com/serenity-bdd/serenity-core/commit/6200d4effd5470ab828a3a9caa21ec16b67b2c86) fix: Updates default URL to 'http://www.google.com/ncr' this prevents ([@verhagen](https://github.com/verhagen))
 
 > redirects from &#39;google.com&#39; to country specific google search pages.
- [666e9dc](https://github.com/serenity-bdd/serenity-core/commit/666e9dcfd8d8df3e2aee31f295b682c8f284431e) refactor: Removes maven-easyb-plugin, is not used, or correct me if I'm ([@verhagen](https://github.com/verhagen))
 
 > wrong.
- [6c0391d](https://github.com/serenity-bdd/serenity-core/commit/6c0391deaec315d95f3e77787988124531cc698d) Simplified a unit test ([@wakaleo](https://github.com/wakaleo))
- [7578ed2](https://github.com/serenity-bdd/serenity-core/commit/7578ed2ff16cf34cce2bcea025965136fe2f10b9) fix: Checks if the driver != null, before calling close() and quit(), ([@verhagen](https://github.com/verhagen))
- [7974322](https://github.com/serenity-bdd/serenity-core/commit/7974322366574a349c389fbe816d0058d8ec962e) fix: Brings package name in class inline with the package directory ([@verhagen](https://github.com/verhagen))
 
 > structure
- [7b58b52](https://github.com/serenity-bdd/serenity-core/commit/7b58b52e3c13baa3b70391a6cfb6dcaee9be7620) Renames package 'net.serenity_bdd.*' into 'net.serenitybdd.*', to bring ([@verhagen](https://github.com/verhagen))
 
 > them inline with the rest
- [7d21048](https://github.com/serenity-bdd/serenity-core/commit/7d21048e9a2b3b0d000f82adba06c7c3e7e33dc7) fix: Corrects issue auto forwarding from google.com to google.xxx the ([@verhagen](https://github.com/verhagen))
 
 > country specific search page.
- [80913b9](https://github.com/serenity-bdd/serenity-core/commit/80913b94b709b8bb3fa93dbdfc4267aa884e2fcf) chore:Added the chromedriver binary for the Snap-CI builds ([@wakaleo](https://github.com/wakaleo))
- [84e1e5f](https://github.com/serenity-bdd/serenity-core/commit/84e1e5f25cd5bacfbda3fb2a7ccd3a788298a9b9) Deprecated old screenshot processor ([@wakaleo](https://github.com/wakaleo))
- [8cfa26d](https://github.com/serenity-bdd/serenity-core/commit/8cfa26db66a93f22638b3aaff4c8176431050fb5) Removes no longer needed @SuppressWarnings ([@verhagen](https://github.com/verhagen))
- [8d77bc4](https://github.com/serenity-bdd/serenity-core/commit/8d77bc49dfa524bc343a7f036ff1a7a418d5095f) fix: Fixed a memory leak. ([@wakaleo](https://github.com/wakaleo))
- [9103566](https://github.com/serenity-bdd/serenity-core/commit/910356643cb225752f30f776a480105b67a81277) Removes unsued variable ([@verhagen](https://github.com/verhagen))
- [917ff5d](https://github.com/serenity-bdd/serenity-core/commit/917ff5d67fbce785139eea27c209837b32380432) Fixed project build on Windows ([@joxerTMD](https://github.com/joxerTMD))
- [91ffac1](https://github.com/serenity-bdd/serenity-core/commit/91ffac1480575625224ca5151e4139e220014c38) Added support for blurring. ([@wakaleo](https://github.com/wakaleo))
- [92fc1c6](https://github.com/serenity-bdd/serenity-core/commit/92fc1c67d24fb6ee6b9868b31f9e1ac95125ac12) Deprecated old screenshot processor ([@wakaleo](https://github.com/wakaleo))
- [94cf1d5](https://github.com/serenity-bdd/serenity-core/commit/94cf1d57fad63a14506bb034164be6367fc020cc) fix: Corrects auto redirect to secure connection (https instead of http) ([@verhagen](https://github.com/verhagen))
- [99c05b5](https://github.com/serenity-bdd/serenity-core/commit/99c05b534ed9d49b6e3e3bb0bfd5d7f996c07c59) Removes @SuppressWarnings, no longer needed ([@verhagen](https://github.com/verhagen))
- [9bd7368](https://github.com/serenity-bdd/serenity-core/commit/9bd7368cbb05b3b1a44cafdf5d7b1a462a5c6bb5) refactor:Better error handling for screenshots ([@wakaleo](https://github.com/wakaleo))
- [9e7e556](https://github.com/serenity-bdd/serenity-core/commit/9e7e55695924f471cd972eba2dd98d3d32ce8a48) fix: Checks if the driver != null, before calling close() and quit(), ([@verhagen](https://github.com/verhagen))
- [a860b0b](https://github.com/serenity-bdd/serenity-core/commit/a860b0bb5f3b58f5d66f89e49fbc64df9dc3402e) fix: Corrects issue with auto redirect to secure connection (https ([@verhagen](https://github.com/verhagen))
- [ad43f43](https://github.com/serenity-bdd/serenity-core/commit/ad43f431f450dac6d2ab736c5aad96f1fa873ad0) refactor:fine-tuning build job ([@wakaleo](https://github.com/wakaleo))
- [b57fce2](https://github.com/serenity-bdd/serenity-core/commit/b57fce26d9baea837d0b96bd062b8b49b5b7b93d) Removes unused import ([@verhagen](https://github.com/verhagen))
- [bf415be](https://github.com/serenity-bdd/serenity-core/commit/bf415be9811359fce50c951c4700e7279f4051a3) Removes unused imports ([@verhagen](https://github.com/verhagen))
- [cf774f8](https://github.com/serenity-bdd/serenity-core/commit/cf774f8d24741dc79069003c12e3bca1ef1c53f2) refactor: Added better error handling for the actors. ([@wakaleo](https://github.com/wakaleo))
- [d370f84](https://github.com/serenity-bdd/serenity-core/commit/d370f8441d4d1468dfb2bcc6b2e79bde129e289f) Removes generics warnings ([@verhagen](https://github.com/verhagen))
- [dc6c68e](https://github.com/serenity-bdd/serenity-core/commit/dc6c68e81a9031efb416658a0d355c48819aed4c) Simplified a unit test ([@wakaleo](https://github.com/wakaleo))
- [f38011c](https://github.com/serenity-bdd/serenity-core/commit/f38011c61aa04c6e7860b7f374a9d584688f28d4) Corrects javadoc description ([@verhagen](https://github.com/verhagen))
- [f4670d1](https://github.com/serenity-bdd/serenity-core/commit/f4670d119a6c8ec15e52832becce10171ed3cf89) Deprecated old screenshot processor ([@wakaleo](https://github.com/wakaleo))
- [f5c5fc7](https://github.com/serenity-bdd/serenity-core/commit/f5c5fc76afcc3b07652db959db9f0f996ac9b174) refactor:Better error handling for screenshots ([@wakaleo](https://github.com/wakaleo))
- [f84df26](https://github.com/serenity-bdd/serenity-core/commit/f84df26505988ea450f7fb1da1c0b324adf68713) Inital version of a new implementation of the screenshot logic. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.14 (2015/09/26 21:43 +00:00)
 
**Pull requests:**
 
- [#133](https://github.com/serenity-bdd/serenity-core/pull/133) Added 'feature.file.encoding' system property to specify an encoding of Cucumber files ([@joxerTMD](https://github.com/joxerTMD))
 
**Commits:**
 
- [05a1789](https://github.com/serenity-bdd/serenity-core/commit/05a1789883ec4e42903c0a53f2b79127ef5bc308) Improved logging messages ([@wakaleo](https://github.com/wakaleo))
- [0f4803e](https://github.com/serenity-bdd/serenity-core/commit/0f4803e013ed3314be67758c344af9c643cf05b5) Better error reporting for actors in the Journey module. ([@wakaleo](https://github.com/wakaleo))
- [5d16f7a](https://github.com/serenity-bdd/serenity-core/commit/5d16f7a17ee6056e34332fea1a8cda5a192e7854) Removed the redundant 'Stabie' column in the reports ([@wakaleo](https://github.com/wakaleo))
- [7e18109](https://github.com/serenity-bdd/serenity-core/commit/7e181098f94ff326dd82e53cf8bf04865c7202de) Added 'feature.file.encoding' system property to specify an encoding of Cucumber files ([@joxerTMD](https://github.com/joxerTMD))
- [9af3f06](https://github.com/serenity-bdd/serenity-core/commit/9af3f06f1329590a14e335dd00ad59d8a23c7a5e) Better support for BrowserStack capability options ([@wakaleo](https://github.com/wakaleo))
- [c7658df](https://github.com/serenity-bdd/serenity-core/commit/c7658dfd48b8fdd3290422cc3f4d148c56a4f3f3) Test refactoring ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.13 (2015/09/20 04:40 +00:00)
 
 
**Commits:**
 
- [1c3eb5e](https://github.com/serenity-bdd/serenity-core/commit/1c3eb5e7fa91c9a4020422918e043781f9a46ccd) Fixed an issue that caused tests with multiple actors to report steps out of order. ([@wakaleo](https://github.com/wakaleo))
- [48305b4](https://github.com/serenity-bdd/serenity-core/commit/48305b4dfdf4d3137f0a09ae2d42487cf7515cf7) Fixed an issue with the moveTo() PageObject method ([@wakaleo](https://github.com/wakaleo))
- [d131e15](https://github.com/serenity-bdd/serenity-core/commit/d131e1535be9ee26d40778fdb971b4723953d882) Fixed an issue with taking screenshots when using multiple browsers ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.12 (2015/09/16 07:44 +00:00)
 
 
**Commits:**
 
- [89461ce](https://github.com/serenity-bdd/serenity-core/commit/89461ce6f7eeb53d0d537cd53607cc65362fcf3c) Additional requirements testing ([@wakaleo](https://github.com/wakaleo))
- [b843013](https://github.com/serenity-bdd/serenity-core/commit/b843013bf18cb0e7f3df7838a3ad4d3ed5acec65) Updated selenium core ([@wakaleo](https://github.com/wakaleo))
- [ef0a39f](https://github.com/serenity-bdd/serenity-core/commit/ef0a39f8ad5fd3ebf59202b16c1dc031ec3438ef) Updated selenium to 2.47.1 ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.11 (2015/09/16 04:41 +00:00)
 
 
**Commits:**
 
- [5f69b1b](https://github.com/serenity-bdd/serenity-core/commit/5f69b1bc6d827984d937d61ce9b11e09be99dcd9) Refactored screenshot-related logging to DEBUG ([@wakaleo](https://github.com/wakaleo))
- [65eada2](https://github.com/serenity-bdd/serenity-core/commit/65eada22ba2665f2915299082c584b690a57aeef) Better support for multiple browser management. ([@wakaleo](https://github.com/wakaleo))
- [714f2a9](https://github.com/serenity-bdd/serenity-core/commit/714f2a9025f8efe9e665c538743c7520df126379) Improved the step message when an actor enteres a value into a field. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.10 (2015/09/07 04:11 +00:00)
 
**Pull requests:**
 
- [#126](https://github.com/serenity-bdd/serenity-core/pull/126) fix: prevent null pointers when generating reports ([@afoltin](https://github.com/afoltin))
 
**Commits:**
 
- [3721d0a](https://github.com/serenity-bdd/serenity-core/commit/3721d0ab6bd63694e68a0f998aaaae8c899a27f6) Improved log reporting for the Journey pattern. ([@wakaleo](https://github.com/wakaleo))
- [66cffe7](https://github.com/serenity-bdd/serenity-core/commit/66cffe7b361595b27888ba9870eadb9e12b80ddf) fix: prevent null pointers when generating reports ([@afoltin](https://github.com/afoltin))
- [969c74b](https://github.com/serenity-bdd/serenity-core/commit/969c74b4f4838cd4e1570a5b7b2829b444fbccd3) Improved reporting in the console logging. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.9 (2015/09/01 08:00 +00:00)
 
 
**Commits:**
 
- [9051d51](https://github.com/serenity-bdd/serenity-core/commit/9051d518de25ee8ed2c98e05190264e50368ff5d) Updated dependencies ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.8 (2015/09/01 06:22 +00:00)
 
**Pull requests:**
 
- [#123](https://github.com/serenity-bdd/serenity-core/pull/123) feat: the phantomjs ssl-property can now be set using the PHANTOMJS_S… ([@wakaleo](https://github.com/wakaleo))
- [#121](https://github.com/serenity-bdd/serenity-core/pull/121) see https://github.com/serenity-bdd/serenity-core/issues/37 ([@cliviu](https://github.com/cliviu))
- [#119](https://github.com/serenity-bdd/serenity-core/pull/119) Improving logging in ReportService ([@HNygard](https://github.com/HNygard))
 
**Commits:**
 
- [1bdbc53](https://github.com/serenity-bdd/serenity-core/commit/1bdbc53a34dba3b4aee110a33b085e9ebb38ae20) see https://github.com/serenity-bdd/serenity-core/issues/37 ([@cliviu](https://github.com/cliviu))
- [2f1bf50](https://github.com/serenity-bdd/serenity-core/commit/2f1bf50b655c68adeb160bcd0c0f6a31d46899ba) feat: the phantomjs ssl-property can now be set using the PHANTOMJS_SSL_PROPERTY environment variable, like the PHANTOMJS_BINARY_PATH. Possible options are 'sslv2', 'sslv3', 'tlsv1' and 'any'. ([@ddt-tdd](https://github.com/ddt-tdd))
- [32f4885](https://github.com/serenity-bdd/serenity-core/commit/32f488557eea7809a3ebe2aed6ede0fe12183b1c) Fixed #115 ([@wakaleo](https://github.com/wakaleo))
 
 > Only record REST responses for non-binary response types.
- [45f1eae](https://github.com/serenity-bdd/serenity-core/commit/45f1eae8491c8d9301ff880672281756389c2be0) Test refactoring ([@wakaleo](https://github.com/wakaleo))
- [9bd70c2](https://github.com/serenity-bdd/serenity-core/commit/9bd70c2429c11b75af2a0fc19a11156b2f74df32) Changed dependencies back to mockito 1.9.5 to avoid dependency conflict issues ([@wakaleo](https://github.com/wakaleo))
- [9eeabc7](https://github.com/serenity-bdd/serenity-core/commit/9eeabc7e93e6b9bab390747664f0e7106eb09565) Improving logging in ReportService ([@HNygard](https://github.com/HNygard))
- [a8ae2e5](https://github.com/serenity-bdd/serenity-core/commit/a8ae2e5f4ac22d16d25dc90917c57858509fa469) Debug log: Adding exception to output ([@HNygard](https://github.com/HNygard))
- [c6ed01d](https://github.com/serenity-bdd/serenity-core/commit/c6ed01d846f46461d09fc9c77a1ec8c3adbb0ae9) Added support for multiple browsers in the same test using the Journey pattern ([@wakaleo](https://github.com/wakaleo))
- [cbfb178](https://github.com/serenity-bdd/serenity-core/commit/cbfb1788cd8e35af269882e170fd92204545fcc1) Fixing Java warnings - Redundant cast ([@HNygard](https://github.com/HNygard))
- [de2d428](https://github.com/serenity-bdd/serenity-core/commit/de2d4287aab991f99a6553943418d1da15f5b3af) Attempt to fix #122 ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.7 (2015/08/31 02:24 +00:00)
 
 
**Commits:**
 
- [7541585](https://github.com/serenity-bdd/serenity-core/commit/75415857c76fa7098223327716a189ead844b12e) Better integration of Actors and Question objects ([@wakaleo](https://github.com/wakaleo))
 
 > Actors can now ask questions directly, e.g.
 > ---
 > Integer totalCost = dana.asksFor(theTotalCost());
 > ---
 > They can also remember the answers to questions for future use:
 > ---
 > dana.remember(&quot;Total Cost&quot;, theTotalCost());
 > assertThat(dana.recall(&quot;Total Cost&quot;)).isEqualTo(14);
 > ---
 
### v1.1.6 (2015/08/25 03:33 +00:00)
 
**Pull requests:**
 
- [#116](https://github.com/serenity-bdd/serenity-core/pull/116) fix: Improved error reporting for provided drivers ([@nartamonov](https://github.com/nartamonov))
 
**Commits:**
 
- [3006a4e](https://github.com/serenity-bdd/serenity-core/commit/3006a4ea233ddc91e5f55da32764c8bd3b10f459) fix: Improved error reporting for provided drivers ([@nartamonov](https://github.com/nartamonov))
 
 > DriverSources may implement some non-trivial logic, so it is very handy
 > for debugging to include in stack trace exception occurred while tried to
 > initialize new webdriver. Especially in multi-node test environment
 > configurations.
- [4404ea3](https://github.com/serenity-bdd/serenity-core/commit/4404ea3c9f3c91bd2bafb1eab5cf5935c40c064b) Refactored the journey demo test ([@wakaleo](https://github.com/wakaleo))
 
 > To better illustrate tasks/interaction layers.
- [6345844](https://github.com/serenity-bdd/serenity-core/commit/63458448580c2b0cced8f1dcd248cdf86adefe77) Removed redundant code that was causing errors in the reports. ([@wakaleo](https://github.com/wakaleo))
 
 > If there were more than one given clause in a journey-style test, the initial givens where incorrectly nested.
- [743ec7b](https://github.com/serenity-bdd/serenity-core/commit/743ec7b5c289e575057fbc78b448f6d08f5c6e0a) Revolving dependency conflicts with hamcrest 1.1 ([@wakaleo](https://github.com/wakaleo))
- [a208972](https://github.com/serenity-bdd/serenity-core/commit/a2089724582c419f4583d504ba39d074ff770c49) Ensure that Consequence steps are not evaluated after a previous step has failed. ([@wakaleo](https://github.com/wakaleo))
- [d3d2e38](https://github.com/serenity-bdd/serenity-core/commit/d3d2e38936d39794166691d8a94b22e9ad0e188e) Removed unnecessary warning messages ([@wakaleo](https://github.com/wakaleo))
- [f77a999](https://github.com/serenity-bdd/serenity-core/commit/f77a999a3f1fe3c32d2cf186fb493931c59fbd81) Refactoring and better console reporting. ([@wakaleo](https://github.com/wakaleo))
- [f7dc3d7](https://github.com/serenity-bdd/serenity-core/commit/f7dc3d73a581aa9fb4d629d9755cd370ad8a9a25) Added support for dropdowns in the interaction-level bundled Performables. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.5 (2015/08/17 18:57 +00:00)
 
**Pull requests:**
 
- [#110](https://github.com/serenity-bdd/serenity-core/pull/110) Fix #109 ([@ihostage](https://github.com/ihostage))
- [#112](https://github.com/serenity-bdd/serenity-core/pull/112) Fix inject Pages in super class ([@ihostage](https://github.com/ihostage))
- [#108](https://github.com/serenity-bdd/serenity-core/pull/108) Check for existence of the angular object. ([@docurban](https://github.com/docurban))
 
**Commits:**
 
- [08c6aab](https://github.com/serenity-bdd/serenity-core/commit/08c6aab6d2797bc344a3ddd5a75d2974a32a477a) Renamed 'serenity-ability-to-browse-the-web' to 'browse-the-web' ([@wakaleo](https://github.com/wakaleo))
- [3afdafb](https://github.com/serenity-bdd/serenity-core/commit/3afdafb4c61939c56005074cdffc7bd983f19807) Improved reporting of questions ([@wakaleo](https://github.com/wakaleo))
- [8bec974](https://github.com/serenity-bdd/serenity-core/commit/8bec974f768f3c8311432a217c9470efc7f4f994) Fix inject Pages in super class ([@ihostage](https://github.com/ihostage))
- [b06d961](https://github.com/serenity-bdd/serenity-core/commit/b06d96153f9ed784c9a26a1d6a2a349cd1c276c8) Fix #109 ([@ihostage](https://github.com/ihostage))
- [b7a8e71](https://github.com/serenity-bdd/serenity-core/commit/b7a8e7190bf7486b9d5d9ceadf7883ec77bddb66) Avoid unnecessary error messages with Java 8 lambdas. ([@wakaleo](https://github.com/wakaleo))
- [cfb3bd6](https://github.com/serenity-bdd/serenity-core/commit/cfb3bd63f86fd3f4da810d6b0312d4f216731a0b) Check for existence of the angular object. ([@docurban](https://github.com/docurban))
- [d64e692](https://github.com/serenity-bdd/serenity-core/commit/d64e692974cc9528e92bb367a41b9210d93ef657) Refactoring build ([@wakaleo](https://github.com/wakaleo))
- [d7af4ed](https://github.com/serenity-bdd/serenity-core/commit/d7af4ed258615c6970bb975519f4d7558b5514ce) Fixed class conflict issue. ([@wakaleo](https://github.com/wakaleo))
- [ed62f93](https://github.com/serenity-bdd/serenity-core/commit/ed62f9307eb62e79ba8a880b4c54b64b7a14f1b2) Added the "ability-to-browse-the-web" module to the journey module. ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.4 (2015/08/10 19:53 +00:00)
 
**Pull requests:**
 
- [#107](https://github.com/serenity-bdd/serenity-core/pull/107) Fix issue #106 ([@ihostage](https://github.com/ihostage))
 
**Commits:**
 
- [5ee8c7e](https://github.com/serenity-bdd/serenity-core/commit/5ee8c7e4cd29642f055f9e3cd9455ebc26ded045) Report task steps correctly when an error occurs in a task step ([@wakaleo](https://github.com/wakaleo))
- [65da516](https://github.com/serenity-bdd/serenity-core/commit/65da5169c34879532bd241e2aed1102d2727e797) Added the Question as a concept. ([@wakaleo](https://github.com/wakaleo))
- [7755f2e](https://github.com/serenity-bdd/serenity-core/commit/7755f2efdada037f69fad6ed9d300f6f09921ab2) Simplified variable injection into step descriptions. ([@wakaleo](https://github.com/wakaleo))
 
 > You can now inject any member variable in the step class by name into step descriptions
 > You can now use member variables of a step library in the [@Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example](https://github.com/Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example):
 > ----
 > private final String siteName = &quot;Etsy&quot;;
 > [@Step](https://github.com/Step)(&quot;Search for a shop called {0} on the #siteName website&quot;)
 > public void searches_for_shop_called(String shopName) {
 > homePage.searchForShopCalled(shopName);
 > }
 > ----
- [7848a32](https://github.com/serenity-bdd/serenity-core/commit/7848a32e82658828b62fbde691c5ba4d917f25b8) Test refactoring ([@wakaleo](https://github.com/wakaleo))
- [7ae80bf](https://github.com/serenity-bdd/serenity-core/commit/7ae80bfa70188b94cae87d94fbd0820091d958dc) Test refactoring ([@wakaleo](https://github.com/wakaleo))
- [7f8f10c](https://github.com/serenity-bdd/serenity-core/commit/7f8f10c771f3dba6738c634366c457d936c7f0f8) Fix issue #106 ([@ihostage](https://github.com/ihostage))
- [95aa4af](https://github.com/serenity-bdd/serenity-core/commit/95aa4af410f5c73879892368303a81dae76c0ec2) You can now use #this or #self in a @Step description. ([@wakaleo](https://github.com/wakaleo))
- [968c403](https://github.com/serenity-bdd/serenity-core/commit/968c4030467adf754fc202a0238cf2d85d4b3487) Better treatment of invalid or undefined fields in step definitions. ([@wakaleo](https://github.com/wakaleo))
- [9722c90](https://github.com/serenity-bdd/serenity-core/commit/9722c9025f24befe90d40fc7dd4eb775e448207c) You can now include assertions in the tests ([@wakaleo](https://github.com/wakaleo))
 
 > You can now include assertions in the tests and reports using the Consequence class.
- [9b7c602](https://github.com/serenity-bdd/serenity-core/commit/9b7c6025d8fef90802d0e272033dde9825626206) Inject step variables by name into step descriptions ([@wakaleo](https://github.com/wakaleo))
 
 > You can now use member variables of a step library in the [@Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example](https://github.com/Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example):
 > ----
 > [@Reporte](https://github.com/Reporte)d
 > private final String siteName = &quot;Etsy&quot;;
 > [@Step](https://github.com/Step)(&quot;Search for a shop called {0} on the #siteName website&quot;)
 > public void searches_for_shop_called(String shopName) {
 > homePage.searchForShopCalled(shopName);
 > }
 > ----
- [9e49f8c](https://github.com/serenity-bdd/serenity-core/commit/9e49f8c1d500cc279851c29f63e3c2d7589abdaa) Got successful and failing journey scenarios working, as long as the failing assertion is in the performAs method. Currently, if one of the chained methods fails, the following steps are not executed and the results are unpredictable ([@wakaleo](https://github.com/wakaleo))
- [c0b0fff](https://github.com/serenity-bdd/serenity-core/commit/c0b0fffc15caa85d5d226782802cc5e26172855a) Added support for TypeSafeConfig ([@wakaleo](https://github.com/wakaleo))
- [cd553eb](https://github.com/serenity-bdd/serenity-core/commit/cd553eb90d77547f7970e3b29478319437523f42) Improve readability of "View stack trace" dialog (#103) ([@wakaleo](https://github.com/wakaleo))
- [d29c52e](https://github.com/serenity-bdd/serenity-core/commit/d29c52eeab2655032946c97bf240e59f78097c59) Minor refactoring ([@wakaleo](https://github.com/wakaleo))
- [e50e557](https://github.com/serenity-bdd/serenity-core/commit/e50e5578683e88741905bb8767f41ac898e9e75e) Refactored a unit test for more clarity ([@wakaleo](https://github.com/wakaleo))
- [eeb1f73](https://github.com/serenity-bdd/serenity-core/commit/eeb1f7353ea5466433099b98fa93531709822b48) Works for nested failing tasks ([@wakaleo](https://github.com/wakaleo))
- [f353c36](https://github.com/serenity-bdd/serenity-core/commit/f353c363ae54e5249b9b1c32f459a105e42a178f) Refactoring ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.3 (2015/08/02 12:04 +00:00)
 
 
**Commits:**
 
- [71f5684](https://github.com/serenity-bdd/serenity-core/commit/71f56847e32e7a939f46e03780617e13a1c7580a) Fixed an issue causing the drivers to be closed incorrectly during parallel tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.2 (2015/07/30 09:49 +00:00)
 
 
**Commits:**
 
- [6aae0d4](https://github.com/serenity-bdd/serenity-core/commit/6aae0d4539b3142630604c172cb4a5effc0641ad) Added the 'uniqueInstance' attribute to the @Steps annotation to support multiple instances of the same step library in the same class ([@wakaleo](https://github.com/wakaleo))
- [c4033ea](https://github.com/serenity-bdd/serenity-core/commit/c4033eaa90b53ff538b6931153109ac0f7f9dca7) Fixed colors in some of the reports causing text to be light grey on white ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.1 (2015/07/26 01:49 +00:00)
 
 
**Commits:**
 
- [30c89b8](https://github.com/serenity-bdd/serenity-core/commit/30c89b8286c037829a61ad8be8fc749410ff012d) Adding support for manual tests ([@wakaleo](https://github.com/wakaleo))
- [312a8ec](https://github.com/serenity-bdd/serenity-core/commit/312a8ec943d00945fee83f84081efb9de73fc909) Fixed an issue that caused broken links in JUnit and Cucumber requirements reports ([@wakaleo](https://github.com/wakaleo))
- [4c85801](https://github.com/serenity-bdd/serenity-core/commit/4c85801565208d61c4d32b9a4558844a0d9ea362) Updated unit tests for manual tests ([@wakaleo](https://github.com/wakaleo))
- [b819099](https://github.com/serenity-bdd/serenity-core/commit/b8190993a962b85dbaeadef65bb907aa7c645824) Added support for manual annotated tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.1.0 (2015/07/25 04:49 +00:00)
 
**Pull requests:**
 
- [#94](https://github.com/serenity-bdd/serenity-core/pull/94) Update BrowserStackRemoteDriverCapabilities.java ([@9ikhan](https://github.com/9ikhan))
 
**Commits:**
 
- [06ccc69](https://github.com/serenity-bdd/serenity-core/commit/06ccc69767b8287f52976f05708e932b67ecf641) Moved the screenshot caption to the top of the screenshots to make it easier to see ([@wakaleo](https://github.com/wakaleo))
- [1764651](https://github.com/serenity-bdd/serenity-core/commit/1764651aff9b8a79586d1578b65d1b08047b49d4) Fixed a timeout issue. ([@wakaleo](https://github.com/wakaleo))
 
 > Fixed a timeout issue related to using withTimeoutOf(...).waitForElementToDisappear()
- [1d3b103](https://github.com/serenity-bdd/serenity-core/commit/1d3b1039c1e1c02357760270c7eca193983fe716) Requirements reporting now support mixing JBehave and JUnit tests. ([@wakaleo](https://github.com/wakaleo))
- [26c716c](https://github.com/serenity-bdd/serenity-core/commit/26c716c69e9a2ee17b13a42c39bb95798435b34b) Simplified some redundant tests ([@wakaleo](https://github.com/wakaleo))
- [31b954c](https://github.com/serenity-bdd/serenity-core/commit/31b954c14c634fa756426731a836f98224607298) Upgraded to cucumber-jvm 1.2.4 ([@wakaleo](https://github.com/wakaleo))
- [3fd16d7](https://github.com/serenity-bdd/serenity-core/commit/3fd16d7b62a7770df29e0bf9d085355f912ab076) Minor refactoring ([@wakaleo](https://github.com/wakaleo))
- [4b5298d](https://github.com/serenity-bdd/serenity-core/commit/4b5298def11a9989dc5e7a9c5922a24aa71112bd) Removed thread leak issue ([@wakaleo](https://github.com/wakaleo))
- [5d78861](https://github.com/serenity-bdd/serenity-core/commit/5d78861f5501df9e46c9bebd9febadc0d0280475) More consistent breadcrumbs ([@wakaleo](https://github.com/wakaleo))
- [6d3847c](https://github.com/serenity-bdd/serenity-core/commit/6d3847c93b9c3303989f7397ba6756215e4ba966) Refactored JSON file loading for better error reporting ([@wakaleo](https://github.com/wakaleo))
- [7a57c62](https://github.com/serenity-bdd/serenity-core/commit/7a57c62bcb1dff0c35074821e445a6a99a6fc27c) Added basic support for the 'dry-run' option. ([@wakaleo](https://github.com/wakaleo))
 
 > Rest calls will now be skipped if you activtate &#39;dry-run&#39; mode (e.g. by setting the &#39;serenity.dry.run&#39; system property to true).
- [7e82d4f](https://github.com/serenity-bdd/serenity-core/commit/7e82d4f3b315f58c8016b7b10a9f330b87e8ab01) Include the name of the exception in error messages ([@wakaleo](https://github.com/wakaleo))
- [869b727](https://github.com/serenity-bdd/serenity-core/commit/869b7276a4defa3a0ace450e475cfc2db1c706d6) Fixed an issue with JBehave where the browser was not closing after tests. ([@wakaleo](https://github.com/wakaleo))
- [8797dae](https://github.com/serenity-bdd/serenity-core/commit/8797dae10c1fae3107466c198270114f89b403a2) Minor bug fixes ([@wakaleo](https://github.com/wakaleo))
- [8b837b6](https://github.com/serenity-bdd/serenity-core/commit/8b837b624eae59257aff27bd459d21c067689399) Upgraded to cucumber-jvm 1.2.4 ([@wakaleo](https://github.com/wakaleo))
- [8e6e206](https://github.com/serenity-bdd/serenity-core/commit/8e6e206ac33e52d2329cd5423653376bcbe3e74d) Added more robust support for running REST tests in DryRun mode. ([@wakaleo](https://github.com/wakaleo))
- [8ed69d8](https://github.com/serenity-bdd/serenity-core/commit/8ed69d8a271bc4adbd6e7b85c781b6aa258dba61) Display the overall time taken for the tests ([@wakaleo](https://github.com/wakaleo))
- [9c94af9](https://github.com/serenity-bdd/serenity-core/commit/9c94af955b05e0f240a2ca34e13c049ed3b09a4e) Added the 'deep.step.execution.after.failures' option ([@wakaleo](https://github.com/wakaleo))
 
 > This option (set to false by default), lets you control the execution depth of the @Step methods after a step has failed. If set to true, it will run nested [@Step methods as well as top-level ones](https://github.com/Step methods as well as top-level ones).
- [a66cef5](https://github.com/serenity-bdd/serenity-core/commit/a66cef5da958b5fcb9250ef350854fa6ed788c03) Updated appium version ([@wakaleo](https://github.com/wakaleo))
- [b3c7855](https://github.com/serenity-bdd/serenity-core/commit/b3c78559ba2cc364ca6194a981fe15fce76e1011) refactoring requirements processing - wip ([@wakaleo](https://github.com/wakaleo))
- [bcc83dd](https://github.com/serenity-bdd/serenity-core/commit/bcc83dd04ebc0a8b694ed01c712fa169edae7cc3) Added a utility method to wait for an AngularJS page to load properly. ([@wakaleo](https://github.com/wakaleo))
- [c14bdf6](https://github.com/serenity-bdd/serenity-core/commit/c14bdf6c5034ed7e0752bef681d66bf35e83f897) Added support for XML REST messages ([@wakaleo](https://github.com/wakaleo))
- [c2e028c](https://github.com/serenity-bdd/serenity-core/commit/c2e028ca2847e220c2019fcbac05c15c680f06cf) Update BrowserStackRemoteDriverCapabilities.java ([@9ikhan](https://github.com/9ikhan))
- [d4222f3](https://github.com/serenity-bdd/serenity-core/commit/d4222f315f744b686769611118b1964d235a2cf1) JSON requirements files are now stored in a dedicated 'requirements' directory. ([@wakaleo](https://github.com/wakaleo))
- [e32a2a0](https://github.com/serenity-bdd/serenity-core/commit/e32a2a0099755f32748a5773d5e70e7705331796) Improved reporting for tag-related reports ([@wakaleo](https://github.com/wakaleo))
- [e64d7a7](https://github.com/serenity-bdd/serenity-core/commit/e64d7a75f03e72371df8f915345eb2d7acadfcfa) Improved error reporting. ([@wakaleo](https://github.com/wakaleo))
- [f5fbb68](https://github.com/serenity-bdd/serenity-core/commit/f5fbb68ad3d7b5419f20363d84a6ce35ed9e5c64) Requirements breadcrumbs for JBehave ([@wakaleo](https://github.com/wakaleo))
- [fc33695](https://github.com/serenity-bdd/serenity-core/commit/fc33695ff16e8fba7e66c81426dfc064d633ec07) Refactoring tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.64 (2015/07/14 12:03 +00:00)
 
 
**Commits:**
 
- [21b96f8](https://github.com/serenity-bdd/serenity-core/commit/21b96f814fd53d6b4baab55dcf27224e84b5c7bf) fix: Improved error messages for remote drivers ([@wakaleo](https://github.com/wakaleo))
 
 > Better error message reporting if a remote driver is incorrectly configured, and some minor refactoring.
- [404d3bf](https://github.com/serenity-bdd/serenity-core/commit/404d3bf2f6bdec142607471246bed00432bdc84d) chore:reinstated Bintray plugin ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.62 (2015/07/13 13:33 +00:00)
 
 
**Commits:**
 
- [5a90759](https://github.com/serenity-bdd/serenity-core/commit/5a907598c5f0f066a2a1f0e0568120c8ecc3c87c) chore: Removed unnecessary wrapper directories ([@wakaleo](https://github.com/wakaleo))
- [85c6001](https://github.com/serenity-bdd/serenity-core/commit/85c600129e4c94b6a52ad17b136aa56a53780895) chore:Updating release plugins ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.61 (2015/07/13 06:12 +00:00)
 
 
**Commits:**
 
- [0fbada9](https://github.com/serenity-bdd/serenity-core/commit/0fbada91e9c3ccf9657066718c5857031d215571) chore:Updating the version of the gradle-git plugin ([@wakaleo](https://github.com/wakaleo))
- [197d009](https://github.com/serenity-bdd/serenity-core/commit/197d00981b482a5265eb01faa5901cc376de93f8) chore:Removed dependency on bintray plugin. ([@wakaleo](https://github.com/wakaleo))
- [400fca7](https://github.com/serenity-bdd/serenity-core/commit/400fca7439f69a46325dc7b81a0406e5d9c726fc) feat: Dropdown.selectByValue() ([@kassemsandarusi](https://github.com/kassemsandarusi))
 
### v1.0.59 (2015/06/30 06:31 +00:00)
 
**Pull requests:**
 
- [#82](https://github.com/serenity-bdd/serenity-core/pull/82) Fix for setting up the remote webdriver capability: webdriver.remote.browser.version ([@eskape](https://github.com/eskape))
 
**Commits:**
 
- [395a9a6](https://github.com/serenity-bdd/serenity-core/commit/395a9a64e193991b4387c90db6f81b6150b48d4f) Fixes unit tests - nullpointer exception fix when system property webdriver.remote.driver is not set ([@eskape](https://github.com/eskape))
- [6f1628b](https://github.com/serenity-bdd/serenity-core/commit/6f1628bc721463cdb3e97ee3700ef979b5dc7d8d) Fixed #87 ([@wakaleo](https://github.com/wakaleo))
- [cca6cd0](https://github.com/serenity-bdd/serenity-core/commit/cca6cd03b475dbdc256c6e43eb2034d024e2d59a) Fix for setting up the remote webdriver capability: webdriver.remote.browser.version ([@eskape](https://github.com/eskape))
 
### v1.0.58 (2015/06/21 13:14 +00:00)
 
 
**Commits:**
 
- [5b16d86](https://github.com/serenity-bdd/serenity-core/commit/5b16d86c86702cd500649b1419ab3f3cae361de4) Fixed #79 ([@wakaleo](https://github.com/wakaleo))
- [8e8b01a](https://github.com/serenity-bdd/serenity-core/commit/8e8b01a5fd49895332442a65c29ceced053dcfd3) Fixeds #81 ([@wakaleo](https://github.com/wakaleo))
- [b06c399](https://github.com/serenity-bdd/serenity-core/commit/b06c39978f06dad6ab966ddd89bbdfb0af87460b) Refactored tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.57 (2015/06/18 21:40 +00:00)
 
 
**Commits:**
 
- [54529c1](https://github.com/serenity-bdd/serenity-core/commit/54529c12066cd60c141ffb3485c6ed5ed1ec43db) Fixed timeout issues with waitForAbsence* methods ([@wakaleo](https://github.com/wakaleo))
- [d377729](https://github.com/serenity-bdd/serenity-core/commit/d3777295f2efd743939dbe85820a71271c47be57) Performance improvements ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.56 (2015/06/15 15:27 +00:00)
 
 
**Commits:**
 
- [4350090](https://github.com/serenity-bdd/serenity-core/commit/4350090ac6ec6090d2069105160add52f1d698c0) Fixed an issue with reporting RestAssured assertion failures. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.54 (2015/06/15 06:50 +00:00)
 
**Pull requests:**
 
- [#77](https://github.com/serenity-bdd/serenity-core/pull/77) serenity-spring support for @ContextHierarchy and meta-annotations ([@martinlau](https://github.com/martinlau))
 
**Commits:**
 
- [05c4283](https://github.com/serenity-bdd/serenity-core/commit/05c4283ddeedcc9cf591b6a3a92fb79a40452033) feat: Add support for Spring [@ContextHierarchy annotation](https://github.com/ContextHierarchy annotation)s
- [75047a1](https://github.com/serenity-bdd/serenity-core/commit/75047a1b8d6fcda72d12994e09b4f380bf88b6e1) Handle null-value parameters more elegantly. ([@wakaleo](https://github.com/wakaleo))
- [7bc74de](https://github.com/serenity-bdd/serenity-core/commit/7bc74def0bf22fe413dea72de7a1d83b53bf300b) feat: Added support for Spring meta-annotations for @ContextConfiguration and [@ContextHierarch](https://github.com/ContextHierarch)y
- [fb614fb](https://github.com/serenity-bdd/serenity-core/commit/fb614fb2bc5bce09ecff3866dfdea949e8d48590) Fixed some timout issues ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.53 (2015/06/13 10:44 +00:00)
 
 
**Commits:**
 
- [11b84b7](https://github.com/serenity-bdd/serenity-core/commit/11b84b726006eff27c6d0d93d245e1eb57c5f057) More refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [1345adf](https://github.com/serenity-bdd/serenity-core/commit/1345adf38741118bf29fb24d5f76f654e5e2ccab) Refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [1a4268c](https://github.com/serenity-bdd/serenity-core/commit/1a4268c8749d0f6b92d258ad5f9c2c618a39a5d2) More refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [1e636a6](https://github.com/serenity-bdd/serenity-core/commit/1e636a6e0192ade1a0a1012d7e7a9573de7affbe) Refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [2597415](https://github.com/serenity-bdd/serenity-core/commit/259741557e10c1ff42a91dcfeedd6dc0cbf1fdd6) Handle recursive parameters correctly (#66) ([@wakaleo](https://github.com/wakaleo))
- [2a4a06f](https://github.com/serenity-bdd/serenity-core/commit/2a4a06f28a3a92bafc00ef8f27e2a45c3d51e1b9) Better reporting of exceptions (fixes #71) ([@wakaleo](https://github.com/wakaleo))
- [45d11b5](https://github.com/serenity-bdd/serenity-core/commit/45d11b56f149692c3f131ef45fc509cd8835f10c) Refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [4aee7bb](https://github.com/serenity-bdd/serenity-core/commit/4aee7bb76735218343b8ffffcfb7e3ee2541852f) Temporarily disable some tests with environment-specific issues ([@wakaleo](https://github.com/wakaleo))
- [6421a4c](https://github.com/serenity-bdd/serenity-core/commit/6421a4ccd0b49648ddbadc2243c7f6ec636b636d) Added better support for reporting exceptions ([@wakaleo](https://github.com/wakaleo))
 
 > We now support reporting exceptions with a zero-parameter constructor as well as a single-parameter constructor.
- [778ea06](https://github.com/serenity-bdd/serenity-core/commit/778ea0699595172b426718a1d5364de06d4c3c79) More refactoring tests ([@wakaleo](https://github.com/wakaleo))
- [eed9a7c](https://github.com/serenity-bdd/serenity-core/commit/eed9a7c1b837c4dba242cff0cc61397c9fc31d09) Refactoring tests ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.52 (2015/06/10 15:05 +00:00)
 
 
**Commits:**
 
- [0a7fdea](https://github.com/serenity-bdd/serenity-core/commit/0a7fdeacf6a3f4a7e73852fa6e46c42a4d9bf650) Modifying screenshot code to work better with Windows (see #69) ([@wakaleo](https://github.com/wakaleo))
- [b184b84](https://github.com/serenity-bdd/serenity-core/commit/b184b84b1d9701e9991b60cdcc113aae07c32d16) Modifying screenshot code to work better with Windows (see #69) ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.51 (2015/06/09 18:17 +00:00)
 
**Pull requests:**
 
- [#70](https://github.com/serenity-bdd/serenity-core/pull/70) Re: Issue #52 - Add support for class level spring annotations ([@ScottDennison](https://github.com/ScottDennison))
 
**Commits:**
 
- [5dfcc1a](https://github.com/serenity-bdd/serenity-core/commit/5dfcc1a4ec89a09f06cee1d015181238f106c072) Updated to Selenium 2.46.0 ([@wakaleo](https://github.com/wakaleo))
- [6e7255d](https://github.com/serenity-bdd/serenity-core/commit/6e7255dc2c22bfc5fd2bad0deb667ba656d4af8b) Deprecate SpringIntegration.
 
 > Add SpringIntegrationMethodRule and SpringIntegrationClassRule, as well as the utility runner SpringIntegrationSerenityRunner, which conveniently automatically adds the aforementioned rules.
 > (Note that some of the main code and test code for the above new classes were originally written in Java 8 and used method references, lambdas and java.util.function.*. To get Java 7 support, these has been replaced by interfaces and anonymous inner classes, but if the project ever moves to Java 8, it is recommended that these are replaced once again).
- [a100646](https://github.com/serenity-bdd/serenity-core/commit/a100646a8697ec4bb7f187f2cdd0f829e252595b) Updated to Selenium 2.46.0 ([@wakaleo](https://github.com/wakaleo))
- [d0d500c](https://github.com/serenity-bdd/serenity-core/commit/d0d500c06511a93f0b399aad72c259673bd78242) Attempt to fix #69 ([@wakaleo](https://github.com/wakaleo))
- [dac9fea](https://github.com/serenity-bdd/serenity-core/commit/dac9fead420ecb80ab892d477bddb1e65be33ca2) Resolved dependency conflict ([@wakaleo](https://github.com/wakaleo))
- [e38147c](https://github.com/serenity-bdd/serenity-core/commit/e38147cc49d8c86909d262d3cfb2cd2a58d3f1ae) Moved the tests in serenity-junit that depended on serenity-spring, into serenity-spring, so serenity-junit no longer depends on serenity-spring.
 
### v1.0.50 (2015/06/04 00:40 +00:00)
 
 
**Commits:**
 
- [6edddb2](https://github.com/serenity-bdd/serenity-core/commit/6edddb2d16a2796675b390c06feadc0c7e8252fb) Fixed incorrect timeout issue ([@wakaleo](https://github.com/wakaleo))
- [a5c86db](https://github.com/serenity-bdd/serenity-core/commit/a5c86db27b7657ea94db80f3f6ed302dcccfd6e8) Refactored unit tests for more clarity ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.49 (2015/06/02 00:56 +00:00)
 
 
**Commits:**
 
- [08aba9b](https://github.com/serenity-bdd/serenity-core/commit/08aba9be2f264cc44caebf03674ade4082c02ec0) Fixed a minor formatting issue for JBehave embedded tables. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.48 (2015/06/01 07:02 +00:00)
 
 
**Commits:**
 
- [0dd7d28](https://github.com/serenity-bdd/serenity-core/commit/0dd7d28dbc0a2556bd72b59fcbcd8315b2bd91e1) Fixed a bug related to deriving requirements structures from Cucumber feature files. ([@wakaleo](https://github.com/wakaleo))
- [23af5a6](https://github.com/serenity-bdd/serenity-core/commit/23af5a66522cc3598c6b8151805d63d2eb038d93) Added basic support for RestAssured. ([@wakaleo](https://github.com/wakaleo))
 
 > You can now add the serenity-rest-assured module to have tight integration with Rest Assured for testing REST web services. Serenity provides a wrapper around the RestAssured methods that reports on the REST queries sent and the responses recieved. Use the SerenityRest.rest() method as a starting point, e.g.
 > ````
 > rest().given().contentType(&quot;application/json&quot;).content(jsonPet).post(&quot;http://petstore.swagger.io/v2/pet&quot;);
 > rest().get(&quot;http://petstore.swagger.io/v2/pet/{id}&quot;, pet.getId()).then().statusCode(200)
 > .and().body(&quot;name&quot;, equalTo(pet.getName()));
 > ````
- [28b9b7b](https://github.com/serenity-bdd/serenity-core/commit/28b9b7bfd02d007582ce950667da771e65aed892) Fixed #61: issue with path checks on remote appium server ([@wakaleo](https://github.com/wakaleo))
- [2e74fdf](https://github.com/serenity-bdd/serenity-core/commit/2e74fdf36e9c37ca806d958ae5df650dbf113bca) Simplified some tests ([@wakaleo](https://github.com/wakaleo))
- [35250bd](https://github.com/serenity-bdd/serenity-core/commit/35250bdb902423faf606de48860b83a81e6a89b2) Made the reporting a bit more robust ([@wakaleo](https://github.com/wakaleo))
 
 > Correctly cater for exceptions without an error message.
- [6ab197f](https://github.com/serenity-bdd/serenity-core/commit/6ab197f15acdd0fc389c5ea9132a8fb5b8543e68) Inject EnvironmentVariables fields in PageObjects ([@wakaleo](https://github.com/wakaleo))
- [8835d57](https://github.com/serenity-bdd/serenity-core/commit/8835d57485f46a88881134b35704244916052596) Harmonized test data ([@wakaleo](https://github.com/wakaleo))
- [95f84a1](https://github.com/serenity-bdd/serenity-core/commit/95f84a18ec27e438e09540e4056ed25ae52af0ab) JUnit tests using the 'expected' attribute are not supported. ([@wakaleo](https://github.com/wakaleo))
- [9ba8c65](https://github.com/serenity-bdd/serenity-core/commit/9ba8c65f64e134665d98f2f71b65fb7f45dcdc3b) Minor formatting fixes in the reports. ([@wakaleo](https://github.com/wakaleo))
- [a2d03c8](https://github.com/serenity-bdd/serenity-core/commit/a2d03c8dbf1fcb06753274bcdc190badfaabd12d) Fix for THUCYDIDES-253 ([@wakaleo](https://github.com/wakaleo))
- [a47b0ae](https://github.com/serenity-bdd/serenity-core/commit/a47b0aea9f58fe19f3360e2af59a83675f2c146d) Fixed #64: issue with resetting implicit timeouts ([@wakaleo](https://github.com/wakaleo))
- [ad83c2a](https://github.com/serenity-bdd/serenity-core/commit/ad83c2afa1652aca1e6ea5a5ea52a43f86fb0a7b) Display full screenshots in the slideshow view. ([@wakaleo](https://github.com/wakaleo))
- [bf01941](https://github.com/serenity-bdd/serenity-core/commit/bf01941c99a7857458850ff872f37ae8dcef0aa7) Refactoring multiple select code ([@wakaleo](https://github.com/wakaleo))
- [d4404c1](https://github.com/serenity-bdd/serenity-core/commit/d4404c10e741ea0918deb9f136219e955b6c4425) Fixed #65 - temporary screenshots not deleted ([@wakaleo](https://github.com/wakaleo))
- [ebb8471](https://github.com/serenity-bdd/serenity-core/commit/ebb84712cf92e625040cc67b49c5cc460665a3a7) Refactoring some old multi-select code ([@wakaleo](https://github.com/wakaleo))
- [f0952b4](https://github.com/serenity-bdd/serenity-core/commit/f0952b4b4245ef220d4678ea49246f09bd7cc287) Renamed 'core' to 'serenity-core' ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.47 (2015/05/01 23:26 +00:00)
 
 
**Commits:**
 
- [036b26b](https://github.com/serenity-bdd/serenity-core/commit/036b26b9ae631f4002fe74866e3e9bf4ad934941) Refactoring some tests ([@wakaleo](https://github.com/wakaleo))
- [17466a5](https://github.com/serenity-bdd/serenity-core/commit/17466a54578f56384a2dca64fcbf4e916cab7460) Display the stack trace for failing tests in the reports ([@wakaleo](https://github.com/wakaleo))
- [27abd5c](https://github.com/serenity-bdd/serenity-core/commit/27abd5ccdb9869e7dc7e0fad6af786d515ff2478) Improved error reporting and performance. ([@wakaleo](https://github.com/wakaleo))
- [2c7b976](https://github.com/serenity-bdd/serenity-core/commit/2c7b976d4323e39f1b869bd5e4bf20942993dedd) Fixed a bug preventing requirements to be loaded from the filesystem with JBehave. ([@wakaleo](https://github.com/wakaleo))
- [3414969](https://github.com/serenity-bdd/serenity-core/commit/3414969576f7c76bb74d2ae0c9fe6b3ce9b5beb9) Only process a new screenshot if an existing one doesn't already exist. ([@wakaleo](https://github.com/wakaleo))
- [39d059a](https://github.com/serenity-bdd/serenity-core/commit/39d059a4b103ea12dae35e5728079de9f7093697) Added a hasClass() method to the WebElementFacade ([@wakaleo](https://github.com/wakaleo))
 
 > This method is a convenient way to check whether a web element has a
 > particular CSS class.
- [413d839](https://github.com/serenity-bdd/serenity-core/commit/413d8398ede5b582c8601039ca428b38d5432514) Reformatting and tidying up imports ([@wakaleo](https://github.com/wakaleo))
- [66d3343](https://github.com/serenity-bdd/serenity-core/commit/66d334368720385b64bd377053c2487441038302) Fixed formatting error in the screenshots ([@wakaleo](https://github.com/wakaleo))
- [6bb343b](https://github.com/serenity-bdd/serenity-core/commit/6bb343b89f02f85ef91bbc4ca686287bde265c55) Refactored some unit tests ([@wakaleo](https://github.com/wakaleo))
- [7d969e8](https://github.com/serenity-bdd/serenity-core/commit/7d969e88d54fc0c10a5c13c886a13fb58e7e2f32) Allow EnvironmentVariables and SystemConfiguration fields to be ([@wakaleo](https://github.com/wakaleo))
 
 > injected into JUnit tests.
- [81fd920](https://github.com/serenity-bdd/serenity-core/commit/81fd9206bafb84951325d0794e4af9f3da6c1951) Removed system properties from the JUnit results to save space. ([@wakaleo](https://github.com/wakaleo))
- [897226e](https://github.com/serenity-bdd/serenity-core/commit/897226ef47f78283c07b038f0b26b00db499a76e) Added containsElements() methods to the PageObject class. ([@wakaleo](https://github.com/wakaleo))
- [a549ef0](https://github.com/serenity-bdd/serenity-core/commit/a549ef00739f18514f21c4666cc8ec386dc28c25) Fixed a bug where tests hung if an invalid selector was used in a waitFor expression. ([@wakaleo](https://github.com/wakaleo))
- [ac5ff92](https://github.com/serenity-bdd/serenity-core/commit/ac5ff92cbc9e82e0b32fccedc0e7503c3eb7e532) Ensure that the correct stack trace is displayed in the reports ([@wakaleo](https://github.com/wakaleo))
- [ace9f68](https://github.com/serenity-bdd/serenity-core/commit/ace9f68089764dd3d50ee138e5130d97083636f8) Fixed bug that prevents the arrow keys working in the screenshot slideshow on webkit browsers ([@wakaleo](https://github.com/wakaleo))
- [b4fbf00](https://github.com/serenity-bdd/serenity-core/commit/b4fbf0017c6b53050b492d96cf36789ca7e6b031) Fixed a bug that caused the screenshots to not always be taken correctly. ([@wakaleo](https://github.com/wakaleo))
- [b810e49](https://github.com/serenity-bdd/serenity-core/commit/b810e490feb06acc4d09048444683080539b7c42) Improved the consistency of requirements reporting for JUnit tests. ([@wakaleo](https://github.com/wakaleo))
- [ba4153d](https://github.com/serenity-bdd/serenity-core/commit/ba4153da4c89e33adb84d40de57906a82f01ba32) Improved automatic detection of file-system requirements hierarchies. ([@wakaleo](https://github.com/wakaleo))
- [bdc3c68](https://github.com/serenity-bdd/serenity-core/commit/bdc3c68d6bf902cb6908d159ba026ece4f7ca1e9) Serenity now generates JUnit-compatible XML reports. ([@wakaleo](https://github.com/wakaleo))
- [d296863](https://github.com/serenity-bdd/serenity-core/commit/d296863429cfdabe1206b0f45421dec403f5e07a) Support for detection of feature file directories. ([@wakaleo](https://github.com/wakaleo))
- [d5dfc1e](https://github.com/serenity-bdd/serenity-core/commit/d5dfc1ea82b7954cef2f22b897ff9fb4925d98a2) Fixed an issue that prevented screenshots from being taken correctly in Cucumber scenarios ([@wakaleo](https://github.com/wakaleo))
- [da5c2b7](https://github.com/serenity-bdd/serenity-core/commit/da5c2b71bef30c179ed0580f70b80cabeb2c0b68) Fixed an issue where the tests hang if you call Javascript after a failing step. ([@wakaleo](https://github.com/wakaleo))
- [ef5aebc](https://github.com/serenity-bdd/serenity-core/commit/ef5aebc607c0df5a8771c749fd382968cee713b3) Fixed a bug in the JUnit parameterized reports ([@wakaleo](https://github.com/wakaleo))
- [f1ebd7a](https://github.com/serenity-bdd/serenity-core/commit/f1ebd7ab77f7b607d4720f27cce0d28ccfb5f0a8) Fixed #49 - sysinfo for build report doesn't support values with spaces ([@wakaleo](https://github.com/wakaleo))
- [faabd32](https://github.com/serenity-bdd/serenity-core/commit/faabd32b9da94de9c935f1c8e51eb347fdb14139) Refactoring and improving the JUnit test reports ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.46 (2015/04/13 10:35 +00:00)
 
 
**Commits:**
 
- [137c534](https://github.com/serenity-bdd/serenity-core/commit/137c534cdfb84b06a89ec988dad1e1b2961812dc) Unit test refactoring ([@wakaleo](https://github.com/wakaleo))
- [5205c75](https://github.com/serenity-bdd/serenity-core/commit/5205c75874d5344a02ded9caf57754fd5dd731fd) Added better error reporting if a groovy expression in the build info properties was incorrect ([@wakaleo](https://github.com/wakaleo))
 
 > Better error handling for Groovy expressions used in the “sysinfo.*”
 > system properties that appear in the build info page.
- [5705bc2](https://github.com/serenity-bdd/serenity-core/commit/5705bc2b828760858d3cd1f229495ded74d5b399) Support Cucumber feature files written in other languages. ([@wakaleo](https://github.com/wakaleo))
- [657232a](https://github.com/serenity-bdd/serenity-core/commit/657232a147988a83dbc143557f52c5df7ddbe0f7) Test refactoring ([@wakaleo](https://github.com/wakaleo))
- [8fb2e5e](https://github.com/serenity-bdd/serenity-core/commit/8fb2e5e5e0b82042c88e25a01db0f46e40dc0f60) Better error reporting for errors around the @DefaultUrl definitions for Page Objects. ([@wakaleo](https://github.com/wakaleo))
- [c8ab82b](https://github.com/serenity-bdd/serenity-core/commit/c8ab82bc59925ba1ab2dc2a5b1793c60c4f93e28) General refactoring ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.45 (2015/03/31 01:50 +00:00)
 
**Pull requests:**
 
- [#43](https://github.com/serenity-bdd/serenity-core/pull/43) Fix problem with uploading file on Windows.  ([@Tyson1986](https://github.com/Tyson1986))
- [#42](https://github.com/serenity-bdd/serenity-core/pull/42) ensure unused threads are terminated and removed from executor pool ([@sarinderv](https://github.com/sarinderv))
 
**Commits:**
 
- [2ec6dd2](https://github.com/serenity-bdd/serenity-core/commit/2ec6dd245337d4ad18dd6a76a58090e362fda9b0) ensure unused threads are terminated and removed from executor pool
- [42884b1](https://github.com/serenity-bdd/serenity-core/commit/42884b1685a5b2d182aeb4cec6db040381b1d4fc) Don't display the browser icon for non-web tests. ([@wakaleo](https://github.com/wakaleo))
 
 > Distinguish among Serenity Web Tests (Selenium) and Serenity Non-Web Test (#41)
- [4e17cef](https://github.com/serenity-bdd/serenity-core/commit/4e17cef7fc39428268651721ca908571400b805d) Added the 'feature.file.language' to support I18N feature files ([@wakaleo](https://github.com/wakaleo))
 
 > Narrative text can now be read from non-English feature files, by setting the &#39;feature.file.language&#39; system property.
- [7586862](https://github.com/serenity-bdd/serenity-core/commit/7586862312bb43a2e6b61a208aba51f8cdb70905) Support for PhantomJS 1.2.1 ([@wakaleo](https://github.com/wakaleo))
- [7848aaa](https://github.com/serenity-bdd/serenity-core/commit/7848aaa638085986a3ba7ad24f938b4b69e59b38) Refactoring and improving the unit tests. ([@wakaleo](https://github.com/wakaleo))
- [c165910](https://github.com/serenity-bdd/serenity-core/commit/c16591002d6d2377ae9098716a94dfb1ae862013) feat: added the possiblity to wait for a CSS or XPath expression from a chained expression. ([@wakaleo](https://github.com/wakaleo))
- [d054c41](https://github.com/serenity-bdd/serenity-core/commit/d054c41b4ff58b9d9c6bb74826456345c0776efa) Fixed issue with uploading files from the Windows file system. ([@wakaleo](https://github.com/wakaleo))
- [daacd77](https://github.com/serenity-bdd/serenity-core/commit/daacd77e56937db2df4fba4ea91c8be3b5947b1e) feat: Custom build properties are reported in a more human-readable way in the build info screen. ([@wakaleo](https://github.com/wakaleo))
- [e7ae87e](https://github.com/serenity-bdd/serenity-core/commit/e7ae87e1fd29953f7f08a692e59f973397369155) Fix problem with uploading file on Windows. Changed creation of file path (if file in classpath) ([@Tyson1986](https://github.com/Tyson1986))
 
### v1.0.44 (2015/03/26 04:43 +00:00)
 
 
**Commits:**
 
- [3838821](https://github.com/serenity-bdd/serenity-core/commit/3838821d026b929645a14363051768dd9a244db3) feat: You can now automatically inject EnvironmentVariables and Configuration variables into your step libraries, simply by declaring a variable of the corresponding type. ([@wakaleo](https://github.com/wakaleo))
- [5e446cb](https://github.com/serenity-bdd/serenity-core/commit/5e446cb122eb20352ea228d28c709383dc86a267) feat: Added a total time in the test results report. ([@wakaleo](https://github.com/wakaleo))
- [e5d04ef](https://github.com/serenity-bdd/serenity-core/commit/e5d04ef4380fc77956b60d3bcc29a3183403cd5c) feat: Added a total time in the test results report. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.43 (2015/03/20 20:21 +00:00)
 
 
**Commits:**
 
- [85f5802](https://github.com/serenity-bdd/serenity-core/commit/85f58025c93396796005ee1cf364d6c1bbef4dd0) Changed the default page size for the test results to 50. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.42 (2015/03/19 11:38 +00:00)
 
 
**Commits:**
 
- [33ff1a1](https://github.com/serenity-bdd/serenity-core/commit/33ff1a16031cb982ece39dfec2d3c6933f5566c1) Allows explicit waits on web elements in a page ([@wakaleo](https://github.com/wakaleo))
 
 > For example:
 > withTimeoutOf(5, TimeUnit.SECONDS).waitFor(facebookIcon).click()
 
### v1.0.41 (2015/03/18 03:35 +00:00)
 
 
**Commits:**
 
- [b497a1d](https://github.com/serenity-bdd/serenity-core/commit/b497a1db76988333b8cafacc057104178bbf90b1) Implemented the timeoutInSeconds attribute on the FindBy annotation. ([@wakaleo](https://github.com/wakaleo))
- [d8ccfda](https://github.com/serenity-bdd/serenity-core/commit/d8ccfdabf6a5952f5313a140e6a2fd5889ffbf00) Implemented the timeoutInSeconds attribute on the FindBy annotation. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.40 (2015/03/17 11:59 +00:00)
 
 
**Commits:**
 
- [0fa63e2](https://github.com/serenity-bdd/serenity-core/commit/0fa63e2aba5951d2b87ff001fa04895d72f9fad1) Added containsElements() and shouldContainElements() methods to WebElementFacade ([@wakaleo](https://github.com/wakaleo))
- [9d9c5a4](https://github.com/serenity-bdd/serenity-core/commit/9d9c5a4b6a19c4f11c577553f160c41a08e15bf8) Added a convenience method to allow more fluent waitFor() constructs ([@wakaleo](https://github.com/wakaleo))
- [e7235f7](https://github.com/serenity-bdd/serenity-core/commit/e7235f713c2d37952baec9c54431fa379d4035b5) Refactored wait logic to use distinct values for implicit waits and wait-for waits. ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.39 (2015/03/12 22:42 +00:00)
 
 
**Commits:**
 
- [204900f](https://github.com/serenity-bdd/serenity-core/commit/204900f5f48211acb9e1527f40ddc9c065d17e9e) Rewrote much of the timeout APIs ([@wakaleo](https://github.com/wakaleo))
- [2eca74a](https://github.com/serenity-bdd/serenity-core/commit/2eca74a07bd6db6630d376265d132ade63f37431) fix: Fixed a bug in reading the requirements from the file system. ([@wakaleo](https://github.com/wakaleo))
- [3828027](https://github.com/serenity-bdd/serenity-core/commit/38280276b961e60528a675c6f0990434d2cd4647) fix: Fixed a bug in reading the requirements from the file system. ([@wakaleo](https://github.com/wakaleo))
- [5a46d71](https://github.com/serenity-bdd/serenity-core/commit/5a46d718876a96a86f3be5affef0432f4b15f828) test: Use phantomjs to check implicit timeouts more realisticly ([@wakaleo](https://github.com/wakaleo))
- [73ce792](https://github.com/serenity-bdd/serenity-core/commit/73ce792b29c26b4f75dcb49e74e360a5f9e368a4) Removed redundant test ([@wakaleo](https://github.com/wakaleo))
- [82d1ab1](https://github.com/serenity-bdd/serenity-core/commit/82d1ab1c848b7d38ea74584597c29913b448561a) tests: test hardening ([@wakaleo](https://github.com/wakaleo))
- [9a6c99d](https://github.com/serenity-bdd/serenity-core/commit/9a6c99dcc3e1949c69b724549685383f505bd449) fix: Fixed a bug in reading the requirements from the file system. ([@wakaleo](https://github.com/wakaleo))
- [ac3de4e](https://github.com/serenity-bdd/serenity-core/commit/ac3de4e6f2f409bce189dc81dcc03affe2ff2804) tests: hardeding the timeout tests ([@wakaleo](https://github.com/wakaleo))
- [b29b7cc](https://github.com/serenity-bdd/serenity-core/commit/b29b7cc6ce97ee96b9d3dc80661f623d805afc79) feature: Added support for a waitUntilClickable() method on web elements ([@wakaleo](https://github.com/wakaleo))
- [dbddf6d](https://github.com/serenity-bdd/serenity-core/commit/dbddf6df434355f7f21ccb8ad2178ad755e4530e) test: Temporarily disabling a test that doesn't work on the build server pending further investigation ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.38 (2015/03/08 23:00 +00:00)
 
**Pull requests:**
 
- [#32](https://github.com/serenity-bdd/serenity-core/pull/32) Update CSVTestDataSource.java ([@x-hovo-x](https://github.com/x-hovo-x))
- [#31](https://github.com/serenity-bdd/serenity-core/pull/31) Update WhenLoadingTestDataFromACSVFile.java ([@x-hovo-x](https://github.com/x-hovo-x))
 
**Commits:**
 
- [0c23f3a](https://github.com/serenity-bdd/serenity-core/commit/0c23f3a8c26b06e44854f92fe9433615e6f88c83) test: Temporarily disabling a test that doesn't work on the build server pending further investigation ([@wakaleo](https://github.com/wakaleo))
- [219441f](https://github.com/serenity-bdd/serenity-core/commit/219441fb70fb4b94c54118770940cdae043efc7a) test: Added sample JSON test data ([@wakaleo](https://github.com/wakaleo))
- [326a643](https://github.com/serenity-bdd/serenity-core/commit/326a643ba2bcb0d92355dc59a897426d8632003c) Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts. ([@wakaleo](https://github.com/wakaleo))
- [4bfdb91](https://github.com/serenity-bdd/serenity-core/commit/4bfdb9133300e0e016beaddcc46ed35e83d4a8de) test: Added sample JSON test data ([@wakaleo](https://github.com/wakaleo))
- [536bfdf](https://github.com/serenity-bdd/serenity-core/commit/536bfdf46cac5b0f3cc48854f176e74abac441d0) test: Use phantomjs to check implicit timeouts more realisticly ([@wakaleo](https://github.com/wakaleo))
- [66801ff](https://github.com/serenity-bdd/serenity-core/commit/66801ffbbc769e407f60b93fe8fdae7572ce2188) Added test data for a sample pending report ([@wakaleo](https://github.com/wakaleo))
- [8043809](https://github.com/serenity-bdd/serenity-core/commit/8043809da640f99f1eaae165f7774a2d5fe1eb19) Update WhenLoadingTestDataFromACSVFile.java ([@x-hovo-x](https://github.com/x-hovo-x))
 
 > Added all possible parameters for CSVReader to be able to parse special chars like \n \t ...
- [9e65332](https://github.com/serenity-bdd/serenity-core/commit/9e653329dd691a596a5ff450a868b4372957f7a8) Added tests to doument implicit wait behavior ([@wakaleo](https://github.com/wakaleo))
- [a6d6cc6](https://github.com/serenity-bdd/serenity-core/commit/a6d6cc62c5763512c53b4c79fbdfc3844de54ea6) Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file. ([@wakaleo](https://github.com/wakaleo))
- [aa1c3ed](https://github.com/serenity-bdd/serenity-core/commit/aa1c3ed0fa00fc75928c81c234e3102af9d22700) Update CSVTestDataSource.java ([@x-hovo-x](https://github.com/x-hovo-x))
- [ac60be6](https://github.com/serenity-bdd/serenity-core/commit/ac60be617fe6dd15076a8406347e0be2897ff62e) Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file. ([@wakaleo](https://github.com/wakaleo))
- [c191b5a](https://github.com/serenity-bdd/serenity-core/commit/c191b5a2b3d0a8cf12bfb189944c91784ec15a35) Added test data for a sample pending report ([@wakaleo](https://github.com/wakaleo))
- [cd09406](https://github.com/serenity-bdd/serenity-core/commit/cd09406dcb34a96553926e964ebe87a267da19c2) Added test data for a sample pending report ([@wakaleo](https://github.com/wakaleo))
- [cd9d786](https://github.com/serenity-bdd/serenity-core/commit/cd9d78657ed78aba1c2f66351eb925db3e0daa09) Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts. ([@wakaleo](https://github.com/wakaleo))
- [d2a2018](https://github.com/serenity-bdd/serenity-core/commit/d2a20188ab24fd4213845331ed79351149d546cd) Update WhenLoadingTestDataFromACSVFile.java ([@x-hovo-x](https://github.com/x-hovo-x))
- [df893d2](https://github.com/serenity-bdd/serenity-core/commit/df893d20c253dbc94e69edbe96df003d1138e146) Update CSVTestDataSource.java ([@x-hovo-x](https://github.com/x-hovo-x))
 
### v1.0.37 (2015/03/03 01:05 +00:00)
 
 
**Commits:**
 
- [403003d](https://github.com/serenity-bdd/serenity-core/commit/403003dfbac409d11bac19502210d587cec46f4b) Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy ([@wakaleo](https://github.com/wakaleo))
- [9d25a1a](https://github.com/serenity-bdd/serenity-core/commit/9d25a1aa46fcfe3113a1cf013f4269f376f4aa42) Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy ([@wakaleo](https://github.com/wakaleo))
- [b3d38fb](https://github.com/serenity-bdd/serenity-core/commit/b3d38fb9a30fd748cf95ef5d4c81b580be76edfa) test:Made a unit test more readable ([@wakaleo](https://github.com/wakaleo))
- [fe952b9](https://github.com/serenity-bdd/serenity-core/commit/fe952b944bf628da851698e3b02095c31e08e91c) Fixed an issue that had broken the async timeout behavior in the setScriptTimeout() method ([@wakaleo](https://github.com/wakaleo))
 
### v1.0.36 (2015/03/02 21:13 +00:00)
 
**Pull requests:**
 
- [#25](https://github.com/serenity-bdd/serenity-core/pull/25) feat: nested page objects i.e. widget objects ([@CoeJoder](https://github.com/CoeJoder))
- [#22](https://github.com/serenity-bdd/serenity-core/pull/22) Listelements ([@CoeJoder](https://github.com/CoeJoder))
- [#20](https://github.com/serenity-bdd/serenity-core/pull/20) Appium support ([@schmurgon](https://github.com/schmurgon))
- [#19](https://github.com/serenity-bdd/serenity-core/pull/19) Upgrade javassist version to match transitive dep. #16 ([@jeffjensen](https://github.com/jeffjensen))
- [#17](https://github.com/serenity-bdd/serenity-core/pull/17) Updating core module for #16. ([@jeffjensen](https://github.com/jeffjensen))
- [#14](https://github.com/serenity-bdd/serenity-core/pull/14) Serenity BDD version of WebElementFacade classes/interfaces ([@mikezx6r](https://github.com/mikezx6r))
- [#13](https://github.com/serenity-bdd/serenity-core/pull/13) rename serenity_bdd to serenitybdd ([@mikezx6r](https://github.com/mikezx6r))
- [#12](https://github.com/serenity-bdd/serenity-core/pull/12) More package renaming ([@mikezx6r](https://github.com/mikezx6r))
- [#11](https://github.com/serenity-bdd/serenity-core/pull/11) Updated reporting, attempt 2 ([@bmwsedee](https://github.com/bmwsedee))
- [#10](https://github.com/serenity-bdd/serenity-core/pull/10) Updated the libraries used in the reporting ([@bmwsedee](https://github.com/bmwsedee))
- [#9](https://github.com/serenity-bdd/serenity-core/pull/9) This small change makes Serenity compatible with Firefox version 32 or g... ([@marcin-caban](https://github.com/marcin-caban))
- [#8](https://github.com/serenity-bdd/serenity-core/pull/8) Use gradle-git for version and tagging ([@mikezx6r](https://github.com/mikezx6r))
- [#7](https://github.com/serenity-bdd/serenity-core/pull/7) Gradle build cleanup ([@mikezx6r](https://github.com/mikezx6r))
- [#6](https://github.com/serenity-bdd/serenity-core/pull/6) [namespace] Move Find annotations to serenity_bdd namespace ([@mikezx6r](https://github.com/mikezx6r))
- [#5](https://github.com/serenity-bdd/serenity-core/pull/5) More migration to the Serenity namespace ([@mikezx6r](https://github.com/mikezx6r))
- [#4](https://github.com/serenity-bdd/serenity-core/pull/4) Rename main class to reflect new project name, and deprecate old ([@mikezx6r](https://github.com/mikezx6r))
- [#3](https://github.com/serenity-bdd/serenity-core/pull/3) Enable selection of Mac Os version on SauceLabs ([@negruta](https://github.com/negruta))
- [#2](https://github.com/serenity-bdd/serenity-core/pull/2) Pti hamcrest 1 1 ([@ptillemans](https://github.com/ptillemans))
 
**Commits:**
 
- [003791a](https://github.com/serenity-bdd/serenity-core/commit/003791a889e29882189051fb8bc901df7e997e3a) Extracted dependency injection into an external module, to make it easier to add additional dependency injection modules later ([@wakaleo](https://github.com/wakaleo))
- [00de150](https://github.com/serenity-bdd/serenity-core/commit/00de150f4da3aab7b6d281e7a261d5378c656d49) Refactored the gradle plugin ([@wakaleo](https://github.com/wakaleo))
- [04cace4](https://github.com/serenity-bdd/serenity-core/commit/04cace4c7b9b0535b49c21f372368e86eb38fa09) Updated banners ([@wakaleo](https://github.com/wakaleo))
- [068315f](https://github.com/serenity-bdd/serenity-core/commit/068315f4f0e63d4a9e58bc401cee1cf55af9fb7c) Performance improvements: forces WebDriver to force an immediate response for findElements() calls if no matching elements are found, and some other minor improvements. ([@wakaleo](https://github.com/wakaleo))
 
 > Also improved step logging to include the test class and method as well as the step name.
 > Signed-off-by: John Ferguson Smart &lt;john.smart[@wakaleo.com&gt](https://github.com/wakaleo.com&gt);
- [072b8de](https://github.com/serenity-bdd/serenity-core/commit/072b8de691e74fce316d83f7297cd95d4c6d06ba) removed duplicate test model ([@CoeJoder](https://github.com/CoeJoder))
- [08b5502](https://github.com/serenity-bdd/serenity-core/commit/08b5502af44c08fe401f3ca9e140c13d85da9da7) Fixed a bug in the reporting where duplicate story tags were displayed in the screenshot screens. ([@wakaleo](https://github.com/wakaleo))
- [0902fc7](https://github.com/serenity-bdd/serenity-core/commit/0902fc79603d4f0b7efe1644e5f1adcbefed7c67) Use gradle-git for version and tagging ([@mikezx6r](https://github.com/mikezx6r))
 
 > === If local repository is dirty
 > -Always builds a SNAPSHOT version.
 > -Will complain that &#39;Stage {} is not one of [SNAPSHOT] allowed for strategy snapshot.&#39;
 > === If local repository is not dirty
 > Set release type using property release.stage. Valid values are:
 > -milestone
 > -rc
 > -final
 > milestone creates a tag with the next version from tag + -milestone.#
 > rc similar, but uses rc. Cannot create a milestone after an rc
 > final creates a version with no endings
 > If want to use ssh authorization, must ensure ssh-agent contains correct key for repository being worked on.
 > If you experience issues, try ssh-add -D to clear identities and add the one identity for the repo in question.
 > The release tags the current commit, and pushes to the remote repository. It does not check if there&#39;s a new commit, so only use it if you really need to.
 > gradle bintrayUpload release -Prelease.stage={milestone|rc|final}
- [09927b0](https://github.com/serenity-bdd/serenity-core/commit/09927b0fda489cebf49c46063367fc560333e885) Integrated better support for JBehave ([@wakaleo](https://github.com/wakaleo))
- [099d118](https://github.com/serenity-bdd/serenity-core/commit/099d1189d1c5d0c99b031de16c8b17050e94f236) core build: exclude transitive deps with convergence problems. #16 ([@jeffjensen](https://github.com/jeffjensen))
 
 > Declare additional transitive deps.
 
 > This reverts commit 44ec91e92d90ebc3742a6221f82d1a404b1baa57.
- [0aa2e31](https://github.com/serenity-bdd/serenity-core/commit/0aa2e31aa5f29f3a2452cf14f241587417b436c2) Merged in Appium support and tweaked an internal class as a work-around for potential dependency issues related to commons-lang3 ([@wakaleo](https://github.com/wakaleo))
- [0e9d614](https://github.com/serenity-bdd/serenity-core/commit/0e9d614b462448a994614470c2fb9a3eb21cca96) Moved most uses of FileUtils to the Java 7 Files class, in order to remove sporadic issues when resizing screenshots ([@wakaleo](https://github.com/wakaleo))
- [11b988b](https://github.com/serenity-bdd/serenity-core/commit/11b988b4948ee76105712fffe1d871bac3ecd97e) Use Durations rather than longs and ints to handle timeout values, in order to avoid coding errors, make the code clearer, and as a basis for more flexible timeout configuration. ([@wakaleo](https://github.com/wakaleo))
- [1290a90](https://github.com/serenity-bdd/serenity-core/commit/1290a90ccf2c6c394059eaff03f8583bf55945da) Solidified a test ([@wakaleo](https://github.com/wakaleo))
- [18d5f80](https://github.com/serenity-bdd/serenity-core/commit/18d5f80d55e8b837be57aeaca402a93a5d493092) Improved requirement reporting for JUnit (experimental) ([@wakaleo](https://github.com/wakaleo))
- [197fab5](https://github.com/serenity-bdd/serenity-core/commit/197fab566647c12c3b8a8963049f04bb94f56b58) Top build: declare transitives as deps. #16 ([@jeffjensen](https://github.com/jeffjensen))
- [199e60a](https://github.com/serenity-bdd/serenity-core/commit/199e60a595c0830172e8fb648363cb3b068ca660) Updated reporting, attempt 2 ([@bmwsedee](https://github.com/bmwsedee))
 
 > This reverts commit 7b26344dea3c0ee710ee90fe7040141a6941f97f.
- [1d7740d](https://github.com/serenity-bdd/serenity-core/commit/1d7740dc9d007c0e84470f30137bb611e9f74336) Fixed an issue in the BinTray deployment ([@wakaleo](https://github.com/wakaleo))
- [22d5395](https://github.com/serenity-bdd/serenity-core/commit/22d5395e9df2cbc7f8a6a352fc71a516c469f237) Top build: fail fast on dependency convergence problems. #16 ([@jeffjensen](https://github.com/jeffjensen))
 
 > Added &quot;force version&quot; on transitive versions with convergence
 > problems.
 > Issue: While this works to keep gradle build clean, it doesn&#39;t
 > affect the generated POM/install for clients.
- [23d2752](https://github.com/serenity-bdd/serenity-core/commit/23d27526cd201f3959b47466793e3f799bb2f963) fix: Tidied up dependencies used by the other serenity modules ([@wakaleo](https://github.com/wakaleo))
- [25e0cd1](https://github.com/serenity-bdd/serenity-core/commit/25e0cd1393bcc9221f782675d150e4d96618a839) Updated release notes
- [2648daa](https://github.com/serenity-bdd/serenity-core/commit/2648daa127fe42d1f64a9558b9f0559556afe639) refactor: Create serenitybdd version of WebElementFacade classes/interfaces ([@mikezx6r](https://github.com/mikezx6r))
 
 > Deprecate Thucydides versions, but still handle them correctly
- [26cce2d](https://github.com/serenity-bdd/serenity-core/commit/26cce2dce32adc641537ff5cfb115482d42f66f3) Made a test cross-platform ([@wakaleo](https://github.com/wakaleo))
- [26f09b0](https://github.com/serenity-bdd/serenity-core/commit/26f09b00e71da04a63faf96106f8ab31b4a060c3) Fixed issue #23 ([@wakaleo](https://github.com/wakaleo))
- [2aa92f9](https://github.com/serenity-bdd/serenity-core/commit/2aa92f97522d70524215d577ff205c002d7f9d90) SerenityRunner and SerenityParameterizedRunner now contain functionality, and Thucydides equivalents merely extend ([@mikezx6r](https://github.com/mikezx6r))
 
 > Also move a number of helper classes into serenity_bdd package
- [2bde33a](https://github.com/serenity-bdd/serenity-core/commit/2bde33abd91afd7aee14fae2a1ff5109eb570c04) refactor: Move tests from thucydides to serenitybdd package ([@mikezx6r](https://github.com/mikezx6r))
- [2cb5e77](https://github.com/serenity-bdd/serenity-core/commit/2cb5e77f4aa71a61a6fb092c7b1d9564e9344c94) fix: Fixed a bug where the reports fail to generate if there are skipped test results in the outcomes. ([@wakaleo](https://github.com/wakaleo))
- [2d48ba3](https://github.com/serenity-bdd/serenity-core/commit/2d48ba34363f7e10f3102a72123fa58b1a241420) test:Updated some unit tests ([@wakaleo](https://github.com/wakaleo))
- [2ed2864](https://github.com/serenity-bdd/serenity-core/commit/2ed2864f88aaf29666617767cbe492fc8d05fbed) [migrate] Move exceptions ([@mikezx6r](https://github.com/mikezx6r))
- [2f58c3b](https://github.com/serenity-bdd/serenity-core/commit/2f58c3b419c5330bab8eb141d5a354a57bb12a07) Fixed some formatting and navigation issues in the reports ([@wakaleo](https://github.com/wakaleo))
- [3026d24](https://github.com/serenity-bdd/serenity-core/commit/3026d248d04401404d5ba128e222306d8ac6b605) test: ensured that HTMLUnit tests closed the drivers to avoid memory leaks during the build. ([@wakaleo](https://github.com/wakaleo))
- [3049d14](https://github.com/serenity-bdd/serenity-core/commit/3049d1465732d6c384a0a9019b70c1e6366a5328) Initial move over to Serenity from Thucydides ([@wakaleo](https://github.com/wakaleo))
- [308ec8f](https://github.com/serenity-bdd/serenity-core/commit/308ec8f50c5dbccb0710620feb77886078c0cae6) Updated the changelog to reflect the serenity-core repo. For Bintray this is a bit of a hack, since the BinTray serenity-core package gets artifacts from two repos, serenity-core and serenity-maven-plugin, which are separate only because of the fact that core needs to be built and deployed before the maven plugin generation task in the serenity-maven-plugin build can be done. So the changelogs will be up-to-date on Github for both repos, but the one on bintray will only reflect core. ([@wakaleo](https://github.com/wakaleo))
- [3144ad1](https://github.com/serenity-bdd/serenity-core/commit/3144ad12699cd6a380a67826eeb5e58c26911855) Upgrade groovy-all version for transitive convergence #16. ([@jeffjensen](https://github.com/jeffjensen))
- [3443435](https://github.com/serenity-bdd/serenity-core/commit/3443435570d0e97e9af784edc5ec7dec1b72abeb) Move junit finder classes to serenity_bdd package ([@mikezx6r](https://github.com/mikezx6r))
- [3620bc2](https://github.com/serenity-bdd/serenity-core/commit/3620bc2af882c4309783aee127d0dff9a9984833) Fine-tuning the release pipeline ([@wakaleo](https://github.com/wakaleo))
- [36d471f](https://github.com/serenity-bdd/serenity-core/commit/36d471f7c2acdbd9d33dd54c99b33c1df503b964) Repositioned the report timestamp ([@wakaleo](https://github.com/wakaleo))
- [3705ee4](https://github.com/serenity-bdd/serenity-core/commit/3705ee4ffed330e6063fc70ef085a188ca760a1b) [rename] Create Serenity namespaced class, deprecate Thucydides version and delegate functions ([@mikezx6r](https://github.com/mikezx6r))
- [3883042](https://github.com/serenity-bdd/serenity-core/commit/388304241495e0cab71616a06fe753166a61b71d) feat: nested page objects i.e. widget objects ([@CoeJoder](https://github.com/CoeJoder))
 
 > WidgetObject: reusable page fragment with a nested search context implied by the Composition pattern.  This feature was requested here:
 > https://groups.google.com/forum/#!topic/thucydides-users/-SiQwD86W8I
 > https://groups.google.com/forum/#!topic/thucydides-users/01oNgOD9TnY
 > See attached unit tests for usage examples.
- [392bc01](https://github.com/serenity-bdd/serenity-core/commit/392bc01b88be7b48f46d2ddf5535c59ec205f1b3) Upgrade SLF4J version for transitive convergence #16. ([@jeffjensen](https://github.com/jeffjensen))
- [3a71aae](https://github.com/serenity-bdd/serenity-core/commit/3a71aaea630baf757e3172f48ca5bd43f1e8d7fc) refactor: PageObject still returns thucydides WebElementFacadeImpl so that can be cast to serenitybdd namespace ([@mikezx6r](https://github.com/mikezx6r))
 
 > This will need to be cleaned up when the thucydides namespace is retired.
- [3d985d1](https://github.com/serenity-bdd/serenity-core/commit/3d985d15871a5289126fd20c906ec65e93409c73) Upgraded to JUnit 4.12 ([@wakaleo](https://github.com/wakaleo))
- [3ee866c](https://github.com/serenity-bdd/serenity-core/commit/3ee866cd987cfb1c6e1f3a2688172bf5eaf25d6c) Remove unused files ([@mikezx6r](https://github.com/mikezx6r))
 
 > It would appear that the main project was moved into core sub-directory, and
 > these files didn&#39;t get cleaned up.
- [40a532d](https://github.com/serenity-bdd/serenity-core/commit/40a532d21efa7763887a7f941d7e1c5e608e15e3) Updated the Ascii Art banner. ([@wakaleo](https://github.com/wakaleo))
- [40db746](https://github.com/serenity-bdd/serenity-core/commit/40db746819856e7f6f65bc880a705fa0031f5f9c) Enable selection of Mac Os version on SauceLabs
- [4138f89](https://github.com/serenity-bdd/serenity-core/commit/4138f8900eb6259f9c64bcfb75f713a29cc6eae8) Check if a resized file for a given screenshot already exists, and if so don't perform the resizing ([@wakaleo](https://github.com/wakaleo))
- [4494dee](https://github.com/serenity-bdd/serenity-core/commit/4494dee65ac0b1fb24803ca4baf2d9241ac88e3c) If javadoc is not told to expect UTF-8 in the strings it uses can generate ASCII errors on at least the Mac. ([@ptillemans](https://github.com/ptillemans))
- [44ec91e](https://github.com/serenity-bdd/serenity-core/commit/44ec91e92d90ebc3742a6221f82d1a404b1baa57) Updated libraries ([@bmwsedee](https://github.com/bmwsedee))
- [47542e1](https://github.com/serenity-bdd/serenity-core/commit/47542e1b4cfc29c93cf6b0ee295129fd92f01837) Made the Ant plugin a bit more robust. ([@wakaleo](https://github.com/wakaleo))
- [4931d36](https://github.com/serenity-bdd/serenity-core/commit/4931d367a7d3e801f418eaad15ff401b1f9d5c12) fix: Tidied up dependencies used by the other serenity modules ([@wakaleo](https://github.com/wakaleo))
- [4a119f5](https://github.com/serenity-bdd/serenity-core/commit/4a119f5eb78613da322bc3a69d548e859b2d597e) Added a selector to find buttons by their label, e.g. find(By.buttonText('Add to cart')); ([@wakaleo](https://github.com/wakaleo))
- [4a132ad](https://github.com/serenity-bdd/serenity-core/commit/4a132ad31b57d7fd42db1bc78770a57e2414f7f4) Added tests to the gradle plugin ([@wakaleo](https://github.com/wakaleo))
- [4b66966](https://github.com/serenity-bdd/serenity-core/commit/4b6696672cfa00293bd1d2c947fc5edb04f1a5f2) Removed redundant Jackson adaptor classes ([@wakaleo](https://github.com/wakaleo))
- [4c953d8](https://github.com/serenity-bdd/serenity-core/commit/4c953d868707e2cf59ea7edde1a85008966d5e2e) Moved the serenity-maven-plugin to a separate project ([@wakaleo](https://github.com/wakaleo))
- [50c45e3](https://github.com/serenity-bdd/serenity-core/commit/50c45e31c5432cde0067be0df0b458c3a908137e) Adding an automatically generated change log to the build ([@wakaleo](https://github.com/wakaleo))
- [527387e](https://github.com/serenity-bdd/serenity-core/commit/527387e98a503f08e74ede8790156cfcf6ae8b3b) Initial release version ([@wakaleo](https://github.com/wakaleo))
- [52e64ae](https://github.com/serenity-bdd/serenity-core/commit/52e64aef5ebbe285abd1045e35619978aadfbc29) Tidied up some dependencies. ([@wakaleo](https://github.com/wakaleo))
- [52f0eea](https://github.com/serenity-bdd/serenity-core/commit/52f0eeadcfc82d26a36ffa16d5b757291dfaa717) Missed change to PathProcessor ([@schmurgon](https://github.com/schmurgon))
- [56f672a](https://github.com/serenity-bdd/serenity-core/commit/56f672a7f8d59413bb81c1b767bb5787c7784c2f) Made table formatting more robust by providing support for unicode brackets and new line chars. ([@wakaleo](https://github.com/wakaleo))
- [581dd47](https://github.com/serenity-bdd/serenity-core/commit/581dd4753b647b37fbe7e3e29403048a0c0cfac4) Rename main class to reflect new project name, and deprecate old ([@mikezx6r](https://github.com/mikezx6r))
 
 > Eventually, all Thucydides references will be removed.
- [5894af6](https://github.com/serenity-bdd/serenity-core/commit/5894af6eb23439437bb5162894b9046bf0724765) fix: Removed dependency conflicts in the Gradle build. ([@wakaleo](https://github.com/wakaleo))
- [5caf4a2](https://github.com/serenity-bdd/serenity-core/commit/5caf4a28cbcb818b7642ce5d4b5614ce188f9b22) Changed group to serenity-bdd to make syncing with Maven Central easier ([@wakaleo](https://github.com/wakaleo))
- [5d3f58a](https://github.com/serenity-bdd/serenity-core/commit/5d3f58a217827dd31050f2d1b79237cc86f245f3) Changed group to serenity-bdd to make syncing with Maven Central easier ([@wakaleo](https://github.com/wakaleo))
- [5d55b1e](https://github.com/serenity-bdd/serenity-core/commit/5d55b1eae5d424b7185ed1aab68ab6f36c53cbf6) Updated excanvas ([@bmwsedee](https://github.com/bmwsedee))
- [5ea5b71](https://github.com/serenity-bdd/serenity-core/commit/5ea5b718068a34f69bb5cc484ba13ac08e6120ca) Changed the 'checkOutcomes' task to force it to run the tests first. ([@wakaleo](https://github.com/wakaleo))
- [5ea5e89](https://github.com/serenity-bdd/serenity-core/commit/5ea5e898b1bcab75f08756a5deaf9cd42f140ae2) Updated release notes
- [5f4b87c](https://github.com/serenity-bdd/serenity-core/commit/5f4b87c9b97869edf979654437c6febb89605e08) Use Java NIO to copy report resources. ([@wakaleo](https://github.com/wakaleo))
- [5fc6c9a](https://github.com/serenity-bdd/serenity-core/commit/5fc6c9a9e3e0b7c3dd9249403f2d1c6ce8ad73a8) Improvements to the reports ([@wakaleo](https://github.com/wakaleo))
- [602eaf8](https://github.com/serenity-bdd/serenity-core/commit/602eaf8dfe8633e9e90005a68b56079638437f7c) Added tool tips on the 'Related Tags' tables ([@wakaleo](https://github.com/wakaleo))
- [61cc4d8](https://github.com/serenity-bdd/serenity-core/commit/61cc4d855d5a0ef7d50f260bb9b2bc74e568a4c1) Display error messages for ignored steps, so that failing assumption messages are correctly displayed ([@wakaleo](https://github.com/wakaleo))
- [6376d99](https://github.com/serenity-bdd/serenity-core/commit/6376d9951792d7b7aa44512bc4d941f3ca21bb7c) This small change makes Serenity compatible with Firefox version 32 or greater ([@marcin-caban](https://github.com/marcin-caban))
 
 > Guava 18.0 is already specified in Gradle.
- [66556bb](https://github.com/serenity-bdd/serenity-core/commit/66556bb4e71cf652362fbedca72788ac5b0eb1ce) Fixed a bug where error messages were incorrectly displayed in the step details ([@wakaleo](https://github.com/wakaleo))
- [6780200](https://github.com/serenity-bdd/serenity-core/commit/6780200d8b74535f6d1e8b092c8953a249d5889e) Added the Serenity helper class, as a replacement for the legacy 'Thucydides' class ([@wakaleo](https://github.com/wakaleo))
- [6925274](https://github.com/serenity-bdd/serenity-core/commit/69252742737e848e5bb10b14061faa76b9e7ae1d) Support for appium annotations, added accessibility and ui automation for IOS and android ([@schmurgon](https://github.com/schmurgon))
- [6d0f8ee](https://github.com/serenity-bdd/serenity-core/commit/6d0f8ee7d7ee3c266af742c0e930c9ed5c7107e2) jbehave was pulling in hamcrest 1.1. Excluded hamcrest from the jbehave dependency. ([@ptillemans](https://github.com/ptillemans))
- [6d63276](https://github.com/serenity-bdd/serenity-core/commit/6d6327665844b25ef78c7761862c8b51ff7c58bc) Correct issue with publishing ([@mikezx6r](https://github.com/mikezx6r))
 
 > Main project doesn&#39;t have anything to deploy, and doesn&#39;t have config. This
 > causes a warning when building the project.
 > Provide the config that is common across all projects in this config file,
 > but no config for the main project is required.
- [6ee7578](https://github.com/serenity-bdd/serenity-core/commit/6ee7578c7e241b6a7bba5719dd4fc832909fbf62) Added better support for comments in feature files, and better formatting in the 'Related Tabs' table ([@wakaleo](https://github.com/wakaleo))
- [6f12e53](https://github.com/serenity-bdd/serenity-core/commit/6f12e5389a8499e2f9f9b69478b329f90367d4fb) Refactoring to facilitate easier migrating to new versions of the libraries ([@bmwsedee](https://github.com/bmwsedee))
- [7020e78](https://github.com/serenity-bdd/serenity-core/commit/7020e788a36a4c28dcdd241276ed403990a96677) Removing Jackson ([@wakaleo](https://github.com/wakaleo))
- [70325bb](https://github.com/serenity-bdd/serenity-core/commit/70325bb74775cb3575502f905a0f62dff1e5db1d) Upgrade commons-lang3 to htmlunit's dep version. #16 ([@jeffjensen](https://github.com/jeffjensen))
 
 > HtmlUnit uses 3.3.2, Serenity was using 3.1.
- [7094f8d](https://github.com/serenity-bdd/serenity-core/commit/7094f8dc6dd6baefff894d4646d94dac6de0ec46) Fixed a bug where if a null value was stored in the Serenity session after a failing step, a null pointer exception was thrown. ([@wakaleo](https://github.com/wakaleo))
- [71d6c5a](https://github.com/serenity-bdd/serenity-core/commit/71d6c5a562d886dc92cef007ecabd03f22cd6e80) Updated the README file to reflect the new commit conventions ([@wakaleo](https://github.com/wakaleo))
- [71fcf22](https://github.com/serenity-bdd/serenity-core/commit/71fcf22e7fe769309798d67551450f4ef791246b) test: Refactored a few tests to reduce sporadic errors ([@wakaleo](https://github.com/wakaleo))
- [74df029](https://github.com/serenity-bdd/serenity-core/commit/74df0296738f380196774513f93e725690233975) Fine-tuning the release tagging ([@wakaleo](https://github.com/wakaleo))
- [7a267aa](https://github.com/serenity-bdd/serenity-core/commit/7a267aa8399a3dd9d754e7fc99280c3f5256fed8) Build: Add plugins that help with dep versions. #16 ([@jeffjensen](https://github.com/jeffjensen))
 
 > - project-report:
 > - gradlew htmlDependencyReport creates HTML dep report that shows
 > which deps the build managed to different version.
 > - com.github.ben-manes.versions:
 > - gradlew dependencyUpdates shows deps with new versions
- [7a65f64](https://github.com/serenity-bdd/serenity-core/commit/7a65f64d3bd4d6fe1ab6a798265e0a3729f9df1e) Fixed a failing test ([@wakaleo](https://github.com/wakaleo))
- [7b26344](https://github.com/serenity-bdd/serenity-core/commit/7b26344dea3c0ee710ee90fe7040141a6941f97f) Removed old versions of libraries ([@bmwsedee](https://github.com/bmwsedee))
- [7ba0890](https://github.com/serenity-bdd/serenity-core/commit/7ba089050433c51cdf6aaca479904c09b333e83c) feat: Lists of WebElementFacade and subtypes as PageObject members. ([@CoeJoder](https://github.com/CoeJoder))
- [7bde238](https://github.com/serenity-bdd/serenity-core/commit/7bde2389379fa22b4eebe9f1ff8ef19c89b094ef) Rename package in demo to serenity_bdd ([@mikezx6r](https://github.com/mikezx6r))
- [7c429c0](https://github.com/serenity-bdd/serenity-core/commit/7c429c02e9f85228b3672b7b7ee04a9791ffaeb7) Migrated the default output directory to target/site/serenity ([@wakaleo](https://github.com/wakaleo))
- [7cb2a81](https://github.com/serenity-bdd/serenity-core/commit/7cb2a81cae949bdcd6915434313cf03d57c06da6) WIP ([@wakaleo](https://github.com/wakaleo))
- [7cbe551](https://github.com/serenity-bdd/serenity-core/commit/7cbe55192607ef259bd2e573d6b34a7c85e244d1) The @Pages annotated field in JUnit tests is now optional ([@wakaleo](https://github.com/wakaleo))
- [805dbf1](https://github.com/serenity-bdd/serenity-core/commit/805dbf1a9bf72b6f67eb739a8de251e885daeba7) Logs a message indicating the path of the generated reports after report aggregation. ([@wakaleo](https://github.com/wakaleo))
- [80e1ef0](https://github.com/serenity-bdd/serenity-core/commit/80e1ef06258e1e5f17487731641eaec8434776b7) Repositioned the report timestamp ([@wakaleo](https://github.com/wakaleo))
- [80ee2cf](https://github.com/serenity-bdd/serenity-core/commit/80ee2cfb7f92285bab102ac8f889d102605fc292) chore: Automated the generation of the release notes from the git commits ([@wakaleo](https://github.com/wakaleo))
- [8208430](https://github.com/serenity-bdd/serenity-core/commit/8208430d61bec12a84e47e7ef9d3f59baf511d82) Updated release notes
- [826c30f](https://github.com/serenity-bdd/serenity-core/commit/826c30fb0ea0c9989d91c0d8233f83211f5c40d3) Refactored the PageObject class for better backward compatibility. ([@wakaleo](https://github.com/wakaleo))
- [828c57a](https://github.com/serenity-bdd/serenity-core/commit/828c57af675ff9d2cd1f69de6be3c60540abf783) Made the JSON tests a bit more robust ([@wakaleo](https://github.com/wakaleo))
- [82b9866](https://github.com/serenity-bdd/serenity-core/commit/82b98664486be5ddba53b41938aa2ec92667aa31) Migrated the default output directory to target/site/serenity ([@wakaleo](https://github.com/wakaleo))
- [8344474](https://github.com/serenity-bdd/serenity-core/commit/8344474fc2d7c2393e5d140a0992177cb4379496) Added support for the AssumeThat method for JUnit tests - AssumeThat will result in a test being displayed as 'ignored' in the reports. ([@wakaleo](https://github.com/wakaleo))
- [878c2a1](https://github.com/serenity-bdd/serenity-core/commit/878c2a1edb79d85808b6c2e4e37e7de79e48f5cc) Added better support for radio buttons in the PageObject class ([@wakaleo](https://github.com/wakaleo))
- [88dbe9c](https://github.com/serenity-bdd/serenity-core/commit/88dbe9c8342f0d84c9e5e0fc0b82530eb6e7e5ae) Restored release notes ([@wakaleo](https://github.com/wakaleo))
- [892b4fe](https://github.com/serenity-bdd/serenity-core/commit/892b4fe6a8d0fab74637cd2c58727a8cdf35faae) Improved reporting of JUnit tests as requirements ([@wakaleo](https://github.com/wakaleo))
- [89f6ca5](https://github.com/serenity-bdd/serenity-core/commit/89f6ca56633ed1cc9867cdb7267c9818f72bb395) Provide better support for step-level error reporting in Cucumber. ([@wakaleo](https://github.com/wakaleo))
- [8b360c1](https://github.com/serenity-bdd/serenity-core/commit/8b360c130452d8c8bd5651e43aedc66a39ed4c3d) Removed Jackson dependencies ([@wakaleo](https://github.com/wakaleo))
- [8ba6aeb](https://github.com/serenity-bdd/serenity-core/commit/8ba6aeb194a96bbcaad15918632d57d88aeb0b5b) Honor both 'thucydides.properties' and 'serenity.properties' files for local project configuration ([@wakaleo](https://github.com/wakaleo))
- [8bdaf7d](https://github.com/serenity-bdd/serenity-core/commit/8bdaf7db8f1f501e932c7efafc5fc32bab8ce953) [rename] Move SerenityListeners and create deprecated ThucydidesListeners ([@mikezx6r](https://github.com/mikezx6r))
- [8d8b0bf](https://github.com/serenity-bdd/serenity-core/commit/8d8b0bf5fb05fcec0ace6eb62c511645caf6945b) You can now use serenity.* instead of thucydides.* system properties. The thucydides.* system properties are still supported, but a warning is printed to the logs. ([@wakaleo](https://github.com/wakaleo))
- [8fee3ad](https://github.com/serenity-bdd/serenity-core/commit/8fee3ad848950836be8030dd82c9a5fb50d068f1) Chrome no longer opens a new window when you specify the browser size. ([@wakaleo](https://github.com/wakaleo))
 
 > Also, the browser is now automatically positioned in the top left hand corner of the screen.
 > Signed-off-by: John Ferguson Smart &lt;john.smart[@wakaleo.com&gt](https://github.com/wakaleo.com&gt);
- [911799b](https://github.com/serenity-bdd/serenity-core/commit/911799b02a2d987ac49ac41f1735925b173538cd) Fixed issues with identifying appium driver ([@schmurgon](https://github.com/schmurgon))
- [924764f](https://github.com/serenity-bdd/serenity-core/commit/924764f8f9f38eb4eb0d0edbe3e763e4979efc80) feat: Improved the readability of parameters in the screenshot pages. ([@wakaleo](https://github.com/wakaleo))
- [931e476](https://github.com/serenity-bdd/serenity-core/commit/931e476b086f4a082a906596202601c9978c85fa) fix: Tidied up dependencies used by the other serenity modules ([@wakaleo](https://github.com/wakaleo))
- [93b836f](https://github.com/serenity-bdd/serenity-core/commit/93b836f8f2a811ee75a89194af7407d64e1ac464) fix: fixed an issue loading the JSON test reports during aggregate report generation. ([@wakaleo](https://github.com/wakaleo))
- [948caa8](https://github.com/serenity-bdd/serenity-core/commit/948caa8ad7924d47466dd791113e7263ac82a927) Updated release notes
- [97156bd](https://github.com/serenity-bdd/serenity-core/commit/97156bd6c56b57149b2a9a4c1b957e90e79bb6d2) Added support for better Cucumber reporting ([@wakaleo](https://github.com/wakaleo))
- [9716bc5](https://github.com/serenity-bdd/serenity-core/commit/9716bc56de482ffed921cd259f9c8b1401f2a782) Make sure the release notes are produced dynamically ([@wakaleo](https://github.com/wakaleo))
- [9784811](https://github.com/serenity-bdd/serenity-core/commit/9784811403dd789ec445dd8649593bd1c02d6263) Migrated the PageObject class to the serenitybdd namespace. ([@wakaleo](https://github.com/wakaleo))
 
 > Signed-off-by: John Ferguson Smart &lt;john.smart[@wakaleo.com&gt](https://github.com/wakaleo.com&gt);
- [98073bd](https://github.com/serenity-bdd/serenity-core/commit/98073bdbe5ff127561ea662aa6b8ea7eba514e09) Added SerenityRunner and SerenityParameterizedRunner classes as alternative names for ThucydidesRunner and ThucydidesParameterizedRunner, more in line with the new naming schema. ([@wakaleo](https://github.com/wakaleo))
- [9b7e9c4](https://github.com/serenity-bdd/serenity-core/commit/9b7e9c43d7f6babeb5dc100a803e50686f026a9b) Hardening unit tests ([@wakaleo](https://github.com/wakaleo))
- [9e47250](https://github.com/serenity-bdd/serenity-core/commit/9e47250a7a37d8607e455ba8ba7dac50a7ae4879) Improved release notes to avoid empty tags ([@wakaleo](https://github.com/wakaleo))
- [9e9711d](https://github.com/serenity-bdd/serenity-core/commit/9e9711d48eca8bbc95c7d5b59a6ad2610e5599d6) Added extra support for handling Cucumber example tables ([@wakaleo](https://github.com/wakaleo))
- [a05b31f](https://github.com/serenity-bdd/serenity-core/commit/a05b31ffb0928e9bc3b80c809a7a029975fefe22) Undid javascript library updates and added the number of tests for tags on the reports ([@wakaleo](https://github.com/wakaleo))
- [a1dba09](https://github.com/serenity-bdd/serenity-core/commit/a1dba09cd2737da45d3a6f6de3b74cd47e42e16b) feat: You can now distinguish between AJAX element waits (defaults to 500 ms) and explicit fluent waits (which default to 5 seconds) ([@wakaleo](https://github.com/wakaleo))
- [a2d3a0f](https://github.com/serenity-bdd/serenity-core/commit/a2d3a0f17b4ad209710aee1a9f5ec7f69a34785a) Refactored optional Spring dependencies into the serenity-spring module - include this module if you want Serenity to honor Spring annotations and dependency injection ([@wakaleo](https://github.com/wakaleo))
- [a3c95dc](https://github.com/serenity-bdd/serenity-core/commit/a3c95dc54f1165c5ea00fcb2719f14a63acba604) Updated JavaScript InfoVis Toolkit ([@bmwsedee](https://github.com/bmwsedee))
- [ad3a486](https://github.com/serenity-bdd/serenity-core/commit/ad3a486ced855de8542b79048c26c3fdd56fbcbc) [migrate] Move SessionMap ([@mikezx6r](https://github.com/mikezx6r))
- [ad4800e](https://github.com/serenity-bdd/serenity-core/commit/ad4800ebcf39afdf66abb76a28d9290f29fd3ad7) Getting the maven plugin build working ([@wakaleo](https://github.com/wakaleo))
- [afaf0b9](https://github.com/serenity-bdd/serenity-core/commit/afaf0b947f97a8c13ac4a225ea3db378b5cbc08b) Fix to remove 'Session ID is null. Using WebDriver after calling quit()?' messages appearing when the tests are run in threads ([@wakaleo](https://github.com/wakaleo))
- [b3340e5](https://github.com/serenity-bdd/serenity-core/commit/b3340e5d3756a26169073690c95f2eea73737fb9) Integrated better support for JBehave ([@wakaleo](https://github.com/wakaleo))
- [b42d58b](https://github.com/serenity-bdd/serenity-core/commit/b42d58b33af6ea34b2155a2f6b30c1634b323799) Fine-tuning the reports ([@wakaleo](https://github.com/wakaleo))
- [b52b55a](https://github.com/serenity-bdd/serenity-core/commit/b52b55a39a9d5016fc87306108c5a661915da0d3) Now you can use the -Dserenity.dry.run=true option to skip step executions - useful when testing JBehave or Cucumber step definitions ([@wakaleo](https://github.com/wakaleo))
- [b55c8cd](https://github.com/serenity-bdd/serenity-core/commit/b55c8cd17404b9a555c02383ee33335b3c0f9cff) feat: Distinguish between element-level timing and "wait-for"-style timing. ([@wakaleo](https://github.com/wakaleo))
- [b5732dc](https://github.com/serenity-bdd/serenity-core/commit/b5732dc3a744246365f512d30484aae735f0f636) Let the bintray keys be defined by the build server ([@wakaleo](https://github.com/wakaleo))
- [b94933d](https://github.com/serenity-bdd/serenity-core/commit/b94933d99cc7e9f679584550b49c0947ea89b8b0) Move JUnit runners to serenity_bdd package ([@mikezx6r](https://github.com/mikezx6r))
- [bc0e078](https://github.com/serenity-bdd/serenity-core/commit/bc0e078f187ae7fdd0c7889180b09d4fe51778fb) Added more info to the README file ([@wakaleo](https://github.com/wakaleo))
- [be15eb4](https://github.com/serenity-bdd/serenity-core/commit/be15eb47c729538f92d1eccbe88f860917bdeeb9) Move Serenity to new package ([@mikezx6r](https://github.com/mikezx6r))
- [c0a1aa0](https://github.com/serenity-bdd/serenity-core/commit/c0a1aa089cd72aff4996202a1753dcdd2f24c2f6) Moved the ant plugin over to the new Serenity namespace ([@wakaleo](https://github.com/wakaleo))
- [c12c6dd](https://github.com/serenity-bdd/serenity-core/commit/c12c6ddc076bcb8c3575d4243b211027fcfe33ea) Updated to Selenium 2.44.0 ([@wakaleo](https://github.com/wakaleo))
- [c31cb4f](https://github.com/serenity-bdd/serenity-core/commit/c31cb4f4b17a0864e128762cdd4a9931b5224258) Improvements to the reports ([@wakaleo](https://github.com/wakaleo))
- [c365291](https://github.com/serenity-bdd/serenity-core/commit/c36529114af5daaf93592b609056276aac7bdc76) Updated release notest ([@wakaleo](https://github.com/wakaleo))
- [c8fd3b9](https://github.com/serenity-bdd/serenity-core/commit/c8fd3b94c1bd867c8d79fe6554b4e36a267c7648) Added bootstrap tabs ([@wakaleo](https://github.com/wakaleo))
- [c9f9505](https://github.com/serenity-bdd/serenity-core/commit/c9f95050aadcd98861dce9b16d11a3c5876126ff) Upgrade javassist version to match transitive dep. #16 ([@jeffjensen](https://github.com/jeffjensen))
- [ceb0c1d](https://github.com/serenity-bdd/serenity-core/commit/ceb0c1d103411a97645454b9e99ec78084514d48) Upgrade htmlunit to Selenium's dep version. #16 ([@jeffjensen](https://github.com/jeffjensen))
- [cfaae5a](https://github.com/serenity-bdd/serenity-core/commit/cfaae5a78a36fbbb7fecea07762c93237839eca3) rename serenity_bdd to serenitybdd ([@mikezx6r](https://github.com/mikezx6r))
- [d21e03e](https://github.com/serenity-bdd/serenity-core/commit/d21e03e66f56d862fa20ddf6d97064c331762e33) Standardized the Groovy version used throughout the build to 2.3.6 ([@wakaleo](https://github.com/wakaleo))
- [d5511b6](https://github.com/serenity-bdd/serenity-core/commit/d5511b6706701d49a361192e5a8752e273c23ebe) Cater for rare cases where the driver returns null when an element is not found ([@wakaleo](https://github.com/wakaleo))
- [d5f35b9](https://github.com/serenity-bdd/serenity-core/commit/d5f35b9cf08b4e6f9c37ee706122a7778cd6dbd0) Switched back to JUnit 4.11 due to API incompatibilities with build tools ([@wakaleo](https://github.com/wakaleo))
- [d7f4cd3](https://github.com/serenity-bdd/serenity-core/commit/d7f4cd3ab1d16d172b81afe10208394116480cbb) fix: Fixed an issue in which tests were slowed down after a failing step because Serenity continued to try to take screenshots ([@wakaleo](https://github.com/wakaleo))
- [d84aeed](https://github.com/serenity-bdd/serenity-core/commit/d84aeede8457858be99f934e05636e49e0c97fff) [rename] Create Serenity namespaced class and move some associated test classes ([@mikezx6r](https://github.com/mikezx6r))
- [d9a768a](https://github.com/serenity-bdd/serenity-core/commit/d9a768af4b3eb2acc4fefb401e01aedada972039) Release notes are now triggered manually before the release ([@wakaleo](https://github.com/wakaleo))
- [e0a96d7](https://github.com/serenity-bdd/serenity-core/commit/e0a96d7cd7499a43fceeb887a0697b7409c2a8d2) Fix scm url's ([@mikezx6r](https://github.com/mikezx6r))
- [e1956cf](https://github.com/serenity-bdd/serenity-core/commit/e1956cfd278a505bf7c06db7ed2ab17e077f6466) Enable selection of Mac Os version on SauceLabs
- [e20146d](https://github.com/serenity-bdd/serenity-core/commit/e20146db9d8e88298f5bd089392f164fbf1e9c56) test:Updated some unit tests ([@wakaleo](https://github.com/wakaleo))
- [e3ce499](https://github.com/serenity-bdd/serenity-core/commit/e3ce499a6d4f91ec14cb6d7fa7a4454bc7c6cf51) Simplified dependencies a little ([@wakaleo](https://github.com/wakaleo))
- [e5a13c7](https://github.com/serenity-bdd/serenity-core/commit/e5a13c7723cb73c0a76a1ed9527411ec53196e12) SmartAnnotation needs platform for Appium annotations to work ([@schmurgon](https://github.com/schmurgon))
- [e78dd2c](https://github.com/serenity-bdd/serenity-core/commit/e78dd2cfdd98e23c1e69c92ebafffce27dbf7f5b) Added support for displaying Saucelabs configuration in the build info screen. ([@wakaleo](https://github.com/wakaleo))
- [e84ac40](https://github.com/serenity-bdd/serenity-core/commit/e84ac40f8da7831e03a899de15418f0d0ff1be9c) Porting changes from thucydides appium-driver-support ([@schmurgon](https://github.com/schmurgon))
- [e8c1a87](https://github.com/serenity-bdd/serenity-core/commit/e8c1a874c9030db021b596943c4ab3a166f7be8c) Updated to Selenium 2.45.0 ([@wakaleo](https://github.com/wakaleo))
- [eb4608f](https://github.com/serenity-bdd/serenity-core/commit/eb4608f6d8c18187608e7112ed6b83f0546eb623) Removed some code that used the JDK 8 libraries ([@wakaleo](https://github.com/wakaleo))
- [ed62753](https://github.com/serenity-bdd/serenity-core/commit/ed62753b69b522f1321750164cbd035d1ebc4c8d) [namespace] Move Find annotations to serenity_bdd namespace ([@mikezx6r](https://github.com/mikezx6r))
- [f07879c](https://github.com/serenity-bdd/serenity-core/commit/f07879ca4b9418342d144ba78200c76f2a8d323a) Refactored some tests ([@wakaleo](https://github.com/wakaleo))
- [f2322d4](https://github.com/serenity-bdd/serenity-core/commit/f2322d488bb19e970bc0e60f8dce015f4e713e69) Minor fix to work around an incompatiblity with IBM JDB 1.7 ([@wakaleo](https://github.com/wakaleo))
- [f4a7542](https://github.com/serenity-bdd/serenity-core/commit/f4a75422ecfc46a66fb5ebb617ce808c299a6d4b) Update reports to use new libraries ([@bmwsedee](https://github.com/bmwsedee))
- [f4ccaf8](https://github.com/serenity-bdd/serenity-core/commit/f4ccaf8310b5ddf79dce60fb6bc13ede47a97d46) Fixed a bug that prevented the proper use of commands like 'webdriver.manage().window().setSize(new Dimension(1600, 1200));' ([@wakaleo](https://github.com/wakaleo))
- [f9d713e](https://github.com/serenity-bdd/serenity-core/commit/f9d713e343d9380cee58edee6154dd09acfb71d5) style: fix typo in logging ([@mikezx6r](https://github.com/mikezx6r))
- [f9d9969](https://github.com/serenity-bdd/serenity-core/commit/f9d996950d02e3147070a4d6113909338f5591ea) Made the log messages for each step include the calling class and method. ([@wakaleo](https://github.com/wakaleo))
- [fe1ab3e](https://github.com/serenity-bdd/serenity-core/commit/fe1ab3e2ce34859b9bee974c8df0228a0e0280ca) Added a page to the reports containing system and configuration properties and browser capabilities for a given test run. ([@wakaleo](https://github.com/wakaleo))
- [fe1c3c5](https://github.com/serenity-bdd/serenity-core/commit/fe1c3c5eb2cee95bcc3fc17f0008589f80b16dd0) Added the Serenity utility class, which exposes and delegates to methods of the legacy Thucydides class. ([@wakaleo](https://github.com/wakaleo))
 
