
# Git Changelog Gradle plugin

Changelog of Git Changelog Gradle plugin.

## v1.9.40
### No issue

**Report non-JSON or XML results from REST queries more accurately**


[4053c807bd66956](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4053c807bd66956) John Ferguson Smart *2018-09-09 09:05:37*


## v1.9.39
### GitHub #1341 

**Fixed #1341.**

 * Java 8 Predicates have no toString() method, and so appear as an object reference in the reports when they are used in Screenplay. This change addes a `returnsAValueThat` method which converts a Predicate to a printable Predicate. E.g. 
 * ``` 
 * then(dana).should(seeThat(&quot;Total cost&quot;, theTotalCost(), 
 * GivenWhenThen.returnsAValueThat(&quot;is equal to 15&quot;, isEqual(15)))); 
 * ``` 

[82462fc91039c31](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/82462fc91039c31) John Ferguson Smart *2018-08-10 13:45:53*


### GitHub #1344 

**feat #1344 - Support for running Cucumber Serenity in parallel batches**

 * - this are the core changes required by https://github.com/serenity-bdd/serenity-cucumber/pull/164 

[b8d3a823c24c6b1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b8d3a823c24c6b1) nickbarrett *2018-08-17 16:56:31*


### GitHub #1346 

**fix #1346 - validate stopwatch started where appropriate**


[85403ac3364310c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/85403ac3364310c) nickbarrett *2018-08-21 10:00:56*


### No issue

**fix: Fixing comments on PR**


[96987eb56261252](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/96987eb56261252) Vinicius Pazzini *2018-09-06 15:21:08*

**test: adding tests for experimental options on chrome capabilities**


[5a972777d477741](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5a972777d477741) Vinicius Pazzini *2018-09-05 18:52:18*

**feat: adding chrome_experimental_options as a property to add values directly into chrome Experimental Options**


[c657ce3755f18db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c657ce3755f18db) Vinicius Pazzini *2018-09-05 18:49:39*

**Support Kobiton**


[8ff614f1504c7e8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8ff614f1504c7e8) Khanh Do *2018-08-31 09:29:42*

**Updated to Selenium 3.14**


[6a8d653cbc919e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6a8d653cbc919e3) John Ferguson Smart *2018-08-20 07:44:59*

**Updated to Selenium 3.14.0**


[87687beaa960e43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/87687beaa960e43) John Ferguson Smart *2018-08-16 19:53:36*


## v1.9.38
### No issue

**Fixed issue with inlining CSS**


[7730ab977d7cf05](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7730ab977d7cf05) John Ferguson Smart *2018-09-05 10:31:59*

**Fixed a typo**


[ae49ac3a96760f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ae49ac3a96760f8) John Ferguson Smart *2018-09-05 10:19:50*

**Added a list of failing tests at the end of the email report**


[a2d6db0b28cc516](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2d6db0b28cc516) John Ferguson Smart *2018-09-05 10:08:28*

**Added Screenplay interactions for JavaScript clicks**


[e0c8b119ceb135a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0c8b119ceb135a) John Ferguson Smart *2018-09-04 14:22:47*

**WIP**


[f3456d82c836331](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3456d82c836331) John Ferguson Smart *2018-08-18 09:01:11*

**WIP**


[e71bda3457fb188](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e71bda3457fb188) John Ferguson Smart *2018-08-16 19:53:00*

**Refactoring common report config**


[19b53c92227c326](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/19b53c92227c326) John Ferguson Smart *2018-08-10 09:11:56*

**Refactoring common report config**


[a3313da379ffd58](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a3313da379ffd58) John Ferguson Smart *2018-08-10 09:11:42*


## v1.9.36
### GitHub #1325 

**Fixed #1325.**


[d57fec8b10e1ac8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d57fec8b10e1ac8) John Ferguson Smart *2018-08-09 16:48:11*


### GitHub #1338 

**Fixed #1338. Maybe.**


[7b4171c2749fba2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7b4171c2749fba2) John Ferguson Smart *2018-08-09 16:46:44*


### GitHub #1340 

**Fixed #1340.**


[eca48d3bb89fd1e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eca48d3bb89fd1e) John Ferguson Smart *2018-08-09 16:48:52*


### No issue

**Added support for extended reports in Gradle**


[82c92a9c4b09b6d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/82c92a9c4b09b6d) John Ferguson Smart *2018-08-09 16:46:18*

**catch all exceptions while getting screenshots or page sources**

 * https://github.com/serenity-bdd/serenity-core/issues/1333 

[b1d40b6cd67cd3e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b1d40b6cd67cd3e) Christoph Kutzinski *2018-08-08 12:02:19*


## v1.9.35
### No issue

**Updated appium to 6.1.0**


[c9b3f77a7227595](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9b3f77a7227595) John Ferguson Smart *2018-08-07 15:05:48*

**Updated htmlunit dependency**


[8a51096f6d18ce7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8a51096f6d18ce7) John Ferguson Smart *2018-08-07 07:57:16*


## v1.9.34
### No issue

**Improved reporting of tables and place-holders in scenario outlines.**


[784f6285ae9b76c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/784f6285ae9b76c) John Ferguson Smart *2018-08-06 22:15:49*


## v1.9.32
### No issue

**Updated gradle plugin**


[31c5c3bcbd511ad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/31c5c3bcbd511ad) John Ferguson Smart *2018-08-05 19:05:49*

**Added artifactory details**


[2c7997861d3eb7b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2c7997861d3eb7b) John Ferguson Smart *2018-08-05 18:56:45*

**Fixed a unit test**


[d7f28ee7272c622](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d7f28ee7272c622) John Ferguson Smart *2018-08-05 18:22:43*

**Removed work-in-progress module from build**


[c20a0c9f593120b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c20a0c9f593120b) John Ferguson Smart *2018-08-05 17:41:27*

**Finalised email reporter**


[fe3c79d0cffb9c2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe3c79d0cffb9c2) John Ferguson Smart *2018-08-05 17:10:42*

**Finalised email template**


[b877990e2d12312](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b877990e2d12312) John Ferguson Smart *2018-08-04 09:23:23*

**chore: Updated unit tests**


[fc6a20dad3a2260](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc6a20dad3a2260) John Ferguson Smart *2018-08-02 07:06:44*

**chore:Updated unit tests**


[2ae1c1955c81841](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ae1c1955c81841) John Ferguson Smart *2018-08-02 06:24:47*

**feature: Added a SendKeys interaction as an alternative to Enter. The Enter interaction waits for the element to be enabled and clears the field before entering the text, whereas SendKeys will just call the Selenium sendKeys method directly.**


[90cc8235a3dcc46](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/90cc8235a3dcc46) John Ferguson Smart *2018-08-02 05:59:39*

**feature: You can now use readme.md files as narrative files for requirements reporting.**


[8ba37605be3b2e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8ba37605be3b2e6) John Ferguson Smart *2018-08-02 05:41:51*

**Email reports (WIP)**


[7f218bd5c5bd3cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f218bd5c5bd3cd) John Ferguson Smart *2018-08-02 05:41:08*

**Updated Angular tests**


[6841bd88798cbf5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6841bd88798cbf5) John Ferguson Smart *2018-07-28 08:07:36*

**Updated Angular tests**


[5b9aa5dbe4e7fe4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b9aa5dbe4e7fe4) John Ferguson Smart *2018-07-28 08:05:33*

**Fixed some failing tests**


[2db73f75261b27e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2db73f75261b27e) John Ferguson Smart *2018-07-28 03:08:51*

**test refactoring**


[4d92a598d41c5a3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4d92a598d41c5a3) John Ferguson Smart *2018-07-24 11:52:21*

**fix: fixed access issue with the Screenplay Upload class**


[7c88b5ca31ef074](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7c88b5ca31ef074) John Ferguson Smart *2018-07-24 03:37:00*

**Basic requirements hierarchy**


[884b7a1afb2c411](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/884b7a1afb2c411) John Ferguson Smart *2018-07-24 03:35:32*

**Render the overview description as the introduction**


[106abb1e6144411](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/106abb1e6144411) John Ferguson Smart *2018-07-22 16:22:03*

**Minor refactoring**


[4456c6f5127e873](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4456c6f5127e873) John Ferguson Smart *2018-07-22 04:46:24*

**fix: Overview files correctly rendered on the requirements home page**


[67b5744d40cc49f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/67b5744d40cc49f) John Ferguson Smart *2018-07-21 15:54:50*

**Basic rendering**


[01ed5b8ad4db836](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01ed5b8ad4db836) John Ferguson Smart *2018-07-19 18:42:30*

**WIP**


[4fd0535557752d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4fd0535557752d9) John Ferguson Smart *2018-07-19 09:05:34*

**WIP**


[fd2b115a584efc0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fd2b115a584efc0) John Ferguson Smart *2018-07-19 09:04:54*


## v1.9.31
### GitHub #1309 

**Fixed #1309**


[26cf362d901f5ca](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26cf362d901f5ca) John Ferguson Smart *2018-07-18 12:41:35*


### No issue

**Fixed minor typo**


[17ccd8713bb322b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/17ccd8713bb322b) John Ferguson Smart *2018-07-19 11:25:45*

**Updated changelog**


[4ddbb58c8e0a856](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4ddbb58c8e0a856) John Ferguson Smart *2018-07-19 09:15:20*

**Minor refactoring**


[482985c5a660776](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/482985c5a660776) John Ferguson Smart *2018-07-16 17:27:17*

**Added an interaction class to upload files.**

 * Usage: 
 * actor.attemptsTo(Upload.theFile(pathToFile).to(SOME_TARGET); 

[0ee932d0a77c1bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ee932d0a77c1bb) John Ferguson Smart *2018-07-13 00:48:48*

**fix: report Favicon**


[f25324de75cdf30](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f25324de75cdf30) Manisha Awasthi *2018-07-10 20:32:43*


## v1.9.30
### No issue

**Added support for markdown formatting in actor descriptions**


[034a42906cb46bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/034a42906cb46bb) John Ferguson Smart *2018-07-09 17:33:19*

**Added support for Facts, to setup and teardown test data.**


[add76d98de591fe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/add76d98de591fe) John Ferguson Smart *2018-07-09 01:19:23*

**Harmonised the Screenplay Cast API**


[bef67bd73adcde7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bef67bd73adcde7) John Ferguson Smart *2018-07-06 13:29:48*


## v1.9.29
### No issue

**Added a Subject to the Remembered Question class so that the key will appear in the reports.**


[6a470595bada55d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6a470595bada55d) John Ferguson Smart *2018-07-05 16:14:21*

**Ensure that tasks are not executed for @Manual or @Pending tests.**


[c099b3d76c78d5f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c099b3d76c78d5f) John Ferguson Smart *2018-07-05 16:13:42*

**Added `it` as a recognised screenplay pronoun.**


[bc1f74ee6ee39e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc1f74ee6ee39e9) John Ferguson Smart *2018-07-05 16:13:02*

**Ensure that the tests do not fail when copying the same report asset from different threads**


[3e2a85f0d8bb0c4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e2a85f0d8bb0c4) John Ferguson Smart *2018-07-05 14:17:00*

**feat: A Question to easily make assertions about remembered values**

 * Sample usage: 
 * ``` 
 * Actor tracy = Actor.named(&quot;Tracy&quot;); 
 * tracy.remember(&quot;age&quot;,30); 
 * tracy.should(seeThat(Remembered.valueOf(&quot;age&quot;), equalTo(30))); 
 * ``` 

[36f4a20ca924d28](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36f4a20ca924d28) John Ferguson Smart *2018-07-05 08:49:47*

**feat: Added the ability to create a cast of actors with no special abilities.**


[3554fa44bc464be](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3554fa44bc464be) John Ferguson Smart *2018-07-04 23:57:13*

**feat: You can now create arbitrary Casts of actors more easily with predefined abilities for each actor.**

 * Imagine you have the following Ability class: 
 * ``` 
 * public class Fetch implements Ability { 
 * private final String item; 
 * private Fetch(String item) { 
 * this.item = item; 
 * } 
 * public static Fetch some(String item) { return new Fetch(item)} 
 * }; 
 * ``` 
 * You can define a cast of actors who have this ability like this: 
 * ``` 
 * Cast globeTheatreCast = Cast.whereEveryoneCan(Fetch.some(&quot;Coffee&quot;))); 
 * ``` 
 * Or you can use a lambda expression for more flexibility: 
 * ``` 
 * Cast globeTheatreCast = Cast.whereEveryoneCan(actor -&gt; actor.whoCan(Fetch.some(&quot;Coffee&quot;)))); 
 * ``` 

[28613000b7a9263](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28613000b7a9263) John Ferguson Smart *2018-07-04 23:50:47*

**Add double-checked locking for requirements**

 * Also make sure that the returned list is not modified by anyone 
 * https://github.com/serenity-bdd/serenity-core/issues/1291 

[125dcbce74a7252](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/125dcbce74a7252) Christoph Kutzinski *2018-07-04 07:45:13*


## v1.9.28
### No issue

**Updated REST tests**


[689978d6df03218](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/689978d6df03218) John Ferguson Smart *2018-07-03 14:56:57*

**chore: fixed some unit tests**


[012f9fa5201742a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/012f9fa5201742a) John Ferguson Smart *2018-07-03 14:37:27*

**Updated to Cucumber 3.0.2**


[badc7517a9afdee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/badc7517a9afdee) John Ferguson Smart *2018-07-03 14:06:22*

**fix: Ensure custom capabilities are always converted to boolean or numbers as appropriate**


[21408f5c3f62f79](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/21408f5c3f62f79) John Ferguson Smart *2018-07-02 19:18:44*


## v1.9.27
### No issue

**Improved handling of boolean and integer values in the serenity.conf file for Capabilities**


[dd14c54207555f0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dd14c54207555f0) John Ferguson Smart *2018-07-02 17:42:23*

**feat: added support for Predicates in the seeThat screenplay expressions,**

 * e.g. 
 * ``` 
 * sam.should( 
 * seeThat(&quot;names should match&quot;, TheUser.name(), name -&gt; name.equalsIgnoreCase(&quot;George&quot;)) 
 * ); 
 * ``` 

[1ebba53e33a6450](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1ebba53e33a6450) John Ferguson Smart *2018-07-01 23:23:35*


## v1.9.26
### No issue

**chore: Update to Selenium 3.13.0**


[26935c65aaacb9c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26935c65aaacb9c) John Ferguson Smart *2018-07-01 14:33:35*

**feat: Added the Switch interaction class to handle switching between frames in Screenplay.**

 * Sample usage: 
 * ``` 
 * actor.attemptsTo( 
 * Switch.toFrame(1)), 
 * Click.on(SOME_BUTTON), 
 * Switch.toParentFrame() 
 * ); 

[911d817d144e9b8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/911d817d144e9b8) John Ferguson Smart *2018-07-01 13:44:42*

**fix: added SERENITY_MAXIMUM_STEP_NESTING_DEPTH property to allow configuration of maximum step nesting**


[578f060615e6ce6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/578f060615e6ce6) Vytautas Martinaitis *2018-06-29 10:23:53*


## v1.9.25
### Jira buddy-1 

**upgrade to byte-buddy-1.8.12**


[ef485368c5e7358](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef485368c5e7358) cliviu *2018-06-23 21:30:46*


### No issue

**chore: Updated changelog**


[409eb096a3b6680](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/409eb096a3b6680) John Ferguson Smart *2018-06-23 10:07:36*


## v1.9.24
### No issue

**chore: fixing unit tests**


[37dc6287ded94dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/37dc6287ded94dc) John Ferguson Smart *2018-06-23 09:38:35*

**fix: Performance improvements for Screenplay matchers. Previously, matchers such as isNotVisible() and isNotPresent() would timeout before passing the test. Now they will succeed immediately if the element is missing.**


[b4afb54ef081b48](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b4afb54ef081b48) John Ferguson Smart *2018-06-23 09:30:18*

**chore: Code cleanup**


[316e980fa719878](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/316e980fa719878) John Ferguson Smart *2018-06-22 16:15:30*

**fix: updated groovy dependencies to avoid a class dependency issue that happens sometimes with serenity-rest-assured**


[2d808b7732c1db2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d808b7732c1db2) John Ferguson Smart *2018-06-20 20:53:45*

**chore: tidying up and documentation**


[b0aa33ed9f31ea3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b0aa33ed9f31ea3) John Ferguson Smart *2018-06-20 20:52:45*


## v1.9.23
### No issue

**Updated CircleCI files**


[cb36459bd7e3917](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cb36459bd7e3917) John Ferguson Smart *2018-06-18 21:41:43*

**Updated CircleCI files**


[06ef2be87813c21](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/06ef2be87813c21) John Ferguson Smart *2018-06-18 21:37:54*


## v1.9.22
### No issue

**chore: refactoring**


[d5f537eb219293a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5f537eb219293a) John Ferguson Smart *2018-06-18 15:25:25*

**fix: corrected wording for the seeIf task.**


[3294d57bc204efd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3294d57bc204efd) John Ferguson Smart *2018-06-18 15:24:57*

**fix: Questions with the @Subject annotation where not always reported correctly in the reports**


[717c4e3a0a2d6b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/717c4e3a0a2d6b9) John Ferguson Smart *2018-06-18 15:24:21*

**Updated changelog**


[6a27cd97b1c9834](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6a27cd97b1c9834) John Ferguson Smart *2018-06-14 21:30:30*


## v1.9.20
### No issue

**Added the experimental question/task 'seeIf'**


[daa522123a98ba8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/daa522123a98ba8) John Ferguson Smart *2018-06-05 22:19:59*

**Named the response status question**


[3f7e87c13c2c1e1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f7e87c13c2c1e1) John Ferguson Smart *2018-06-05 22:11:23*

**Added the e net.serenitybdd.screenplay.rest.questions package which contains a number of new ways to create custom REST questions**


[7157de8979735ce](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7157de8979735ce) John Ferguson Smart *2018-06-05 22:09:48*

**Added the e net.serenitybdd.screenplay.rest.questions package which contains a number of new ways to create custom REST questions**


[6231d1354d4ce73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6231d1354d4ce73) John Ferguson Smart *2018-06-05 22:08:53*

**Fixed a performance issue reading requirements hierarchies.**


[2ba090325cbe190](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ba090325cbe190) John Ferguson Smart *2018-06-04 21:26:23*

**Added additional REST interactions**


[50a3b1824001f19](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/50a3b1824001f19) John Ferguson Smart *2018-05-22 05:35:39*


## v1.9.19
### No issue

**Added a convenience method to refer to optional properties.**

 * A property may be defined in the project&#39;s serenity.properties or serenity.conf file, or be provided as a 
 * a system property. This is designed particularly for user-provided properties, to make it easier to store 
 * test confiuration properties in a single file. 
 * Sample usage: 
 * ``` 
 * EnvironmentVariables environmentVariables; 
 * String environment = environmentVariables.optionalProperty(&quot;env&quot;).orElse(&quot;DEV&quot;) 
 * ``` 

[26fb250336298b5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26fb250336298b5) John Ferguson Smart *2018-05-19 06:37:12*


## v1.9.18
### No issue

**Added a descriptive message to REST consequences**


[ca0f5e88b399f8e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca0f5e88b399f8e) John Ferguson Smart *2018-05-18 12:31:11*

**Removed some redundant dependencies**


[f6409c34a06b72b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f6409c34a06b72b) John Ferguson Smart *2018-05-16 08:48:15*


## v1.9.17
### No issue

**Initial version of screenplay rest support**


[7576418a8233fa3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7576418a8233fa3) John Ferguson Smart *2018-05-16 02:36:38*

**possible fix for https://github.com/serenity-bdd/serenity-core/issues/1236**


[bf35a1f7abad01d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf35a1f7abad01d) cliviu *2018-05-15 23:02:11*

**Catch WebDriverExceptions while getting the pagesource**

 * https://github.com/serenity-bdd/serenity-core/issues/1247 

[4675acf159f3187](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4675acf159f3187) Christoph Kutzinski *2018-05-14 13:41:32*

**possible fix for https://github.com/serenity-bdd/serenity-core/issues/1243**


[a562154a4fca666](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a562154a4fca666) cliviu *2018-05-09 21:11:01*


## v1.9.16
### No issue

**Fixed visibility issue in the adding report data API**


[557bee706b22a3d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/557bee706b22a3d) John Ferguson Smart *2018-05-09 07:36:13*


## v1.9.15
### No issue

**It is now possible to embed custom data in your Serenity reports. This capability has existed for REST testing for some time - the body of the REST queries and responses are included in the Serenity reports - but now you can do the same thing for your own data.**

 * This feature is quite simple to use. At any point during test execution, you can include data as a string as shown here: 
 * ``` 
 * Serenity.recordReportData().withTitle(&quot;Some data&quot;).andContents(&quot;Some special data&quot;) 
 * ``` 
 * If you have a file to include in the output, you can do that too: 
 * ``` 
 * Path testDataSource = Paths.get(&quot;/my/log/file&quot;) 
 * Serenity.recordReportData().withTitle(&quot;Output log&quot;).fromFile(testDataSource); 
 * ``` 

[1fbf6023bdc5078](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1fbf6023bdc5078) John Ferguson Smart *2018-05-08 22:27:04*

**It is now possible to embed custom data in your Serenity reports. This capability has existed for REST testing for some time - the body of the REST queries and responses are included in the Serenity reports - but now you can do the same thing for your own data.**

 * This feature is quite simple to use. At any point during test execution, you can include data as a string as shown here: 
 * ``` 
 * Serenity.recordReportData().withTitle(&quot;Some data&quot;).andContents(&quot;Some special data&quot;) 
 * ``` 
 * If you have a file to include in the output, you can do that too: 
 * ``` 
 * Path testDataSource = Paths.get(&quot;/my/log/file&quot;) 
 * Serenity.recordReportData().withTitle(&quot;Output log&quot;).fromFile(testDataSource); 
 * ``` 

[58e4eccbecc7196](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/58e4eccbecc7196) John Ferguson Smart *2018-05-08 22:26:27*

**integrate new class ReportFormatter in .ftl files**


[b1e04189f428c2a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b1e04189f428c2a) cliviu *2018-05-05 22:37:17*


## v1.9.14
### No issue

**possible fix for https://github.com/serenity-bdd/serenity-core/issues/1236**


[d5b5b9277dd472a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5b5b9277dd472a) cliviu *2018-05-05 06:38:31*


## v1.9.13
### No issue

**Removed redundant test**


[aea5d663d4054ad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aea5d663d4054ad) John Ferguson Smart *2018-05-02 19:59:36*

**Fixed a bug where the Screenplay select interaction failed when the Select element was not visible.**


[940b69b6a37760e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/940b69b6a37760e) John Ferguson Smart *2018-05-02 06:44:10*

**splitmodel: fix problem with test resources**


[4b9423f29e989e8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b9423f29e989e8) cliviu *2018-04-28 21:47:56*

**Fixed unit testing error**


[9467e4854937b6a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9467e4854937b6a) John Ferguson Smart *2018-04-28 14:50:27*

**Added screenplay interactions to allow reading multiple entries from targets**


[a82ae359e95432e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a82ae359e95432e) John Ferguson Smart *2018-04-27 10:45:23*

**More robust support for empty-string driver properties in Maven.**


[f13c72fe016739b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f13c72fe016739b) John Ferguson Smart *2018-04-26 00:39:04*

**split out serenity-model - extract serenity-reports**


[8c0887670ab48d8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8c0887670ab48d8) cliviu *2018-04-23 20:12:15*

**split out serenity-model - extract serenity-reports**


[432714117cd065d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/432714117cd065d) cliviu *2018-04-22 21:24:45*

**split out serenity-model - move part of junit tests to the model project -part 3**


[b451f93d34f0084](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b451f93d34f0084) cliviu *2018-04-18 22:07:43*

**split out serenity-model - move part of junit tests to the model project -part 2**


[24c83d2ba1fe574](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/24c83d2ba1fe574) cliviu *2018-04-15 22:23:25*

**split out serenity-model - move part of junit tests to the model project**


[94d75894cc39539](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/94d75894cc39539) cliviu *2018-04-14 21:54:43*

**split out serenity-model**


[0ef40a9d14fec35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ef40a9d14fec35) cliviu *2018-04-11 22:15:54*


## v1.9.12
### No issue

**Allows OS-specific local driver paths to be configured.**

 * For example, in the serenities.properties file, put: 
 * ``` 
 * drivers.windows.webdriver.chrome.driver = src/test/resources/webdriver/windows/chromedriver.exe 
 * drivers.mac.webdriver.chrome.driver = src/test/resources/webdriver/mac/chromedriver 
 * drivers.linux.webdriver.chrome.driver = src/test/resources/webdriver/linux/chromedriver 
 * ``` 
 * Or in the serenity.conf file, put: 
 * ``` 
 * drivers { 
 * windows { 
 * &quot;webdriver.chrome.driver&quot; = &quot;src/test/resources/webdriver/windows/chromedriver.exe&quot; 
 * &quot;webdriver.gecko.driver&quot; = &quot;src/test/resources/webdriver/windows/geckodriver.exe&quot; 
 * } 
 * mac { 
 * &quot;webdriver.chrome.driver&quot; = &quot;src/test/resources/webdriver/mac/chromedriver&quot; 
 * &quot;webdriver.gecko.driver&quot; = &quot;src/test/resources/webdriver/mac/geckodriver&quot; 
 * } 
 * linux { 
 * &quot;webdriver.chrome.driver&quot; = &quot;src/test/resources/webdriver/linux/chromedriver&quot; 
 * &quot;webdriver.gecko.driver&quot; = &quot;src/test/resources/webdriver/linux/geckodriver&quot; 
 * } 
 * } 
 * ``` 

[e376ad909998d7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e376ad909998d7c) John Ferguson Smart *2018-04-25 22:21:46*


## v1.9.11
### No issue

**Updated NGWebDriver**


[7814b6c61f95339](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7814b6c61f95339) John Ferguson Smart *2018-04-23 15:38:18*


## v1.9.10
### GitHub #1217 

**#1217 bug: iframe switching**


[f2e6752a6fabb73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f2e6752a6fabb73) nickbarrett *2018-04-22 10:50:36*


### No issue

**Updaded dependencies:**

 * - appium 6.0.0-BETA5 
 * - browsermob 2.1.5 

[5d5afd3b6502067](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d5afd3b6502067) John Ferguson Smart *2018-04-23 12:31:49*


## v1.9.9
### GitHub #1148 

**Partial fix for #1148. Set `serenity.disble.rest.calls.after.failures` to false to ensure that REST calls are always made.**


[e1885cf90b24fa3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e1885cf90b24fa3) John Ferguson Smart *2018-04-22 00:49:38*


### No issue

**Test updates**


[eeb933f7fa53763](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eeb933f7fa53763) John Ferguson Smart *2018-04-22 01:33:32*

**Test updates**


[d8c73624dd33907](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d8c73624dd33907) John Ferguson Smart *2018-04-22 01:13:52*

**Removed some deprecated Selenium method calls.**


[8fef23afac081c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fef23afac081c5) John Ferguson Smart *2018-04-22 00:16:08*

**Better support for requirements reporting in multi-module proejcts. Requirements are normally in a directory called**

 * src/test/resources/features or src/test/resources/stories. For multi-module projects, it 
 * can be a directory with this name in one of the modules. There should only be one requirements directory in a 
 * multi-module project. The easiest approach is to have a dedicated module for the acceptance tests. 
 * You can hard-code this directory using serenity.requirements.dir. Milage may vary for multi-module projects. 
 * If you need to override the root directory (e.g. to use src/test/resources/myFeatures), a better way is to 
 * set the serenity.features.directory (for Cucumber) or serenity.stories.directory (for JBehave) property to 
 * the simple name of the directory (e.g. serenity.features.directory=myFeatures). 

[84eac250f345461](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/84eac250f345461) John Ferguson Smart *2018-04-21 18:28:58*

**Refactored tests to use the updated Duration class**


[664c1a0c98e9004](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/664c1a0c98e9004) John Ferguson Smart *2018-04-19 19:48:53*

**Replaced deprecated Selenium Duration class with java.time.Duration**


[af6705aa0d93c5a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af6705aa0d93c5a) John Ferguson Smart *2018-04-19 18:57:26*


## v1.9.8
### GitHub #1208 

**Added support for serenity.skipped.on (#1208)**


[a9844ed45c413f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a9844ed45c413f3) John Ferguson Smart *2018-04-19 00:25:22*


### No issue

**Finished implementation of the serenity.skipped.on feature**


[01fe51212758efa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01fe51212758efa) John Ferguson Smart *2018-04-19 00:55:43*

**Added support for proxy definition in Chrome and InternetExplorer**


[4c1e4fadecee7ec](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c1e4fadecee7ec) John Ferguson Smart *2018-04-18 23:52:00*

**Updated Selenium to 3.11.0**


[3e578cf1b66d034](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e578cf1b66d034) John Ferguson Smart *2018-04-18 21:49:25*

**Added the ability to define both general and browser-specific capabilities for drivers in a generic way. Browser capabilities can be either general (applied to all drivers) or driver-specific. General capabilities have the prefix "driver_capabilities.common.", e.g.**

 * ``` 
 * driver_capabilities.common.takesScreenshot = false 
 * ``` 
 * Driver-specific capabilities are prefixed by the name of the driver in lower case, e.g. 
 * ``` 
 * driver_capabilities.iexplorer.ie.ensureCleanSession = true 
 * ``` 

[aa1e46ab9e4dd67](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aa1e46ab9e4dd67) John Ferguson Smart *2018-04-18 21:49:00*

**https://github.com/serenity-bdd/serenity-core/issues/1177 -allow numerical id values in SmartAnnotation**


[479689c5d1d9739](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/479689c5d1d9739) cliviu *2018-04-14 20:41:09*

**Added the accept.insecure.certificates system property for Firefox, Chrome, IE and Edge**


[ec81ae66c3577d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ec81ae66c3577d6) John Ferguson Smart *2018-04-13 23:29:03*


## v1.9.7
### GitHub #1199 

**Fixed #1199**


[65234193f5c9472](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/65234193f5c9472) John Ferguson Smart *2018-04-10 22:53:45*


### No issue

**Add browserstack link capability**


[bc164bfecac9c00](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc164bfecac9c00) Strugariu, Florin *2018-04-05 11:53:46*


## v1.9.6
### GitHub #1195 

**Fixing  JUnitXMLConverter uses wrong timestamp format #1195 - Changing year format to yyyy from YYYY**


[89f501ebfb1467e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89f501ebfb1467e) mn40262 *2018-04-10 06:11:02*

**Fixing  JUnitXMLConverter uses wrong timestamp format #1195**


[3f571b5e21c3d35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f571b5e21c3d35) mn40262 *2018-04-10 05:28:39*


### No issue

**Updated asciidoc**


[e76b0a93af82d39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e76b0a93af82d39) John Ferguson Smart *2018-04-04 11:58:55*

**Revert "Fixed JUNIT XML generation issue."**

 * This reverts commit cde0789d75f03c1544b630023d382c60bacc7d26. 

[5c84597c7f31eaa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5c84597c7f31eaa) John Ferguson Smart *2018-04-04 11:58:35*

**Fixed JUNIT XML generation issue.**

 * Wrong timestamp format was generated: for example: &quot;2018-01-09T16:38:20.646+02:00[Europe/Helsinki]&quot; 
 * This caused the report generation to break. 

[cde0789d75f03c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cde0789d75f03c1) IJsselstein, Lennard (ITCDEC) - KLM *2018-04-04 09:21:48*

**Now the `headless.mode` system property works for both Firefox and Chrome drivers.**


[b72bd166fe6a64c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b72bd166fe6a64c) John Ferguson Smart *2018-04-01 09:32:44*


## v1.9.5
### No issue

**FEAT: support for iframe switching within Screenplay**


[f863563fa22464c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f863563fa22464c) nbarrett *2018-03-26 08:46:47*

**fix for https://github.com/serenity-bdd/serenity-core/issues/1149**


[06bf38b5f9c9d3a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/06bf38b5f9c9d3a) cliviu *2018-03-22 22:49:18*

**FEAT: Custom colors for tags.**

 * The color of different types of tags in the Serenity reports can now be configured using Serenity properties. 
 * You can set the background and foreground colors using the `tag.color.for.*` tags. For example, to make 
 * error tags red, you could add: 
 * ``` 
 * tag.color.for.error=red 
 * ``` 
 * You can also add a second color for the text color. So to make the text appear white, you could use the following property: 
 * ``` 
 * tag.color.for.error=red, white 
 * ``` 

[0e7cf03dee3ba7f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0e7cf03dee3ba7f) John Ferguson Smart *2018-03-02 12:21:20*

**Refactoring tests**


[4ef49c46c04d00d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4ef49c46c04d00d) John Ferguson Smart *2018-02-17 14:26:07*

**Fixed syntax error in one of the PRs**


[574983ecb2f65da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/574983ecb2f65da) John Ferguson Smart *2018-02-17 10:12:51*

**Update WebElementFacadeImpl.java**

 * Update method isMobileDriver() to check if current driver is assignable from AppiumDriver. 

[4986f3207e8a740](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4986f3207e8a740) ricardo larrahondo *2018-02-16 18:13:35*

**Fixed JUNIT XML generation issue.**

 * Wrong timestamp format was generated: for example: &quot;2018-01-09T16:38:20.646+02:00[Europe/Helsinki]&quot; 
 * This caused the report generation to break. 
 * Issue was introduced in commit e655282e446bdb3bfbd78aca346577c15f2b8983 
 * See https://github.com/serenity-bdd/serenity-core/commit/e655282e446bdb3bfbd78aca346577c15f2b8983 
 * Issue is also mentioned in issue https://github.com/serenity-bdd/serenity-core/issues/843, but this was (incorrectly?) closed. 

[71aea536d396b6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/71aea536d396b6b) IJsselstein, Lennard (ITCDEC) - KLM *2018-02-16 12:31:54*


## v1.9.4
### GitHub #1165 

**Fix for #1165**


[eb1fa9fca0e1bbc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eb1fa9fca0e1bbc) John Ferguson Smart *2018-03-18 12:17:08*


### GitHub #1171 

**Revert "FIX: #1171: fully integrate new iframe support and fix unit tests"**

 * This reverts commit 9f32678fde0e91bb75824f83cec09173ddf7c0d1. 

[3ebf18e171fad8a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3ebf18e171fad8a) John Ferguson Smart *2018-03-18 15:13:26*

**FIX: #1171: fully integrate new iframe support and fix unit tests**


[9f32678fde0e91b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f32678fde0e91b) nbarrett *2018-03-16 17:45:19*


### GitHub #1172 

**Revert "FIX: #1172: switch iframe using expected condition"**

 * This reverts commit 36362041a45ceee687062792aec99791a1101b50. 

[5a0eb77128a3079](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5a0eb77128a3079) John Ferguson Smart *2018-03-18 15:11:50*

**FIX: #1172: switch iframe using expected condition**


[36362041a45ceee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36362041a45ceee) nbarrett *2018-03-17 23:34:35*


### GitHub #532 

**Revert "Issue #532: feat: added support for iframe switching within the TargetResolver"**

 * This reverts commit a429d6cad04fb1c3374264bcc277a22679f7b7c9. 

[5df29786f5efff6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5df29786f5efff6) John Ferguson Smart *2018-03-18 15:14:00*

**Issue #532: feat: added support for iframe switching within the TargetResolver**


[a429d6cad04fb1c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a429d6cad04fb1c) Nick Barrett *2018-03-15 22:09:17*


### No issue

**Revert "Merge branch 'master' of github.com:serenity-bdd/serenity-core"**

 * This reverts commit 2633ec5bcafa0d617b1c394259ec0e76c25ec0d2, reversing 
 * changes made to 4f2e60a28bece6074195b93c715638f8c7f8b4c2. 

[685bd5ef85f8f43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/685bd5ef85f8f43) John Ferguson Smart *2018-03-18 15:11:03*

**Revert "Minor changes to help debugging the iFrame issue"**

 * This reverts commit 99f77c657668ce06226932b8a12c28d220bc203f. 

[99da4f10931909c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/99da4f10931909c) John Ferguson Smart *2018-03-18 15:09:04*

**Minor changes to help debugging the iFrame issue**


[99f77c657668ce0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/99f77c657668ce0) John Ferguson Smart *2018-03-18 14:59:34*

**Possible fix for failing integration tests on Jenkins**


[a6bdcf43b71585a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a6bdcf43b71585a) John Ferguson Smart *2018-03-17 23:06:43*

**first commit adding an example of Screenplay Pattern using Appium in Android**


[89183101d2a058f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89183101d2a058f) Raúl Fandiño *2018-03-09 02:51:08*


## v1.9.3
### No issue

**Improved screenshot processing**


[61f1754239469d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/61f1754239469d9) John Ferguson Smart *2018-03-06 21:58:32*


## v1.9.2
### No issue

**Added better distinction between the name and pronouns in Screenplay**


[a2a82375ce86fab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2a82375ce86fab) John Ferguson Smart *2018-03-05 17:41:07*


## v1.9.1
### GitHub #777 

**FEAT: Fixed #777 - The pronouns 'he','she' and 'they' are now recognised and respected in Screenplay tests, so that they appear in the reports as they appear in the Cucumber scenarios.**


[5388f4819bae7e0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5388f4819bae7e0) John Ferguson Smart *2018-03-03 15:17:31*


### No issue

**Fixed a dependency conflict**


[30488fbba80e439](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30488fbba80e439) John Ferguson Smart *2018-03-04 19:03:22*

**Updated dependencies**


[7f690638a0ab13e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f690638a0ab13e) John Ferguson Smart *2018-03-04 18:47:29*

**Updated to selenium 3.10.0**


[f28c70385f8d56f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f28c70385f8d56f) John Ferguson Smart *2018-03-04 12:01:12*

**Updated bytebuddy**


[8a6ba4235175cc8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8a6ba4235175cc8) John Ferguson Smart *2018-03-04 11:54:28*

**FEAT: You can use the serenity.maintain.session property to force Serenity to maintain it's session data between tests. By default, session data is cleared for each test.**


[db854a61508ec6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/db854a61508ec6b) John Ferguson Smart *2018-03-03 14:44:24*


## v1.9.0
### GitHub #1130 

**FIX: Fixed #1130**


[4070de59679dccd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4070de59679dccd) John Ferguson Smart *2018-03-03 14:30:16*


### GitHub #1135 

**FIX: Fixed #1135**


[9f2e8818e9a431a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f2e8818e9a431a) John Ferguson Smart *2018-03-03 13:41:08*


### GitHub #1142 

**FIX: Fixed #1142 (made the arrows on the screenshots more visible on light screens.**


[64eb6a6c5171147](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/64eb6a6c5171147) John Ferguson Smart *2018-03-03 13:13:58*


### No issue

**FIX: Fixed a defect where the Serenity session was not being cleared between tests (see https://github.com/serenity-bdd/serenity-cucumber/issues/125).**


[96e2dd9f49d3c79](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/96e2dd9f49d3c79) John Ferguson Smart *2018-03-03 11:59:37*


## v1.9.0-rc.4
### No issue

**Test refactoring**


[4b448b1ded2ad70](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b448b1ded2ad70) John Ferguson Smart *2018-03-02 22:58:36*

**Screenshots are no longer compressed and scaled by default. This means that to view the full size original screenshot, you can right-click on a screenshot and choose "Open image in another tab". If you need this option, you can activate it by setting the serenity.compress.screenshots property to true.**


[3266cf70ce2bd2a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3266cf70ce2bd2a) John Ferguson Smart *2018-03-02 21:46:41*

**Improved error reporting on the screenshot pages**


[143a3c204d37cf3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/143a3c204d37cf3) John Ferguson Smart *2018-03-02 13:52:24*


## v1.9.0-rc.3
### No issue

**Add edge icon for context 'edge'**


[564622f7ad0fe71](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/564622f7ad0fe71) Strugariu, Florin *2018-02-13 19:55:59*


## v1.9.0-rc.2
### GitHub #1128 

**Fixed #1128**


[1d2dff04bc6f93b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1d2dff04bc6f93b) John Ferguson Smart *2018-02-10 14:58:03*


## v1.9.0-rc.1
### GitHub #1121 

**Possible work-around for #1121**


[2ff5a934a1ebf14](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ff5a934a1ebf14) John Ferguson Smart *2018-02-09 01:11:32*

**Possible work-around for #1121**


[c08ff11552393a2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c08ff11552393a2) John Ferguson Smart *2018-02-09 00:32:50*


### No issue

**Updated to Selenium 3.9.1**


[7a09f9f4d04e8f5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a09f9f4d04e8f5) John Ferguson Smart *2018-02-09 00:06:05*

**Use local version of the Guava Joiner class**


[ad2bae1a6b8038d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad2bae1a6b8038d) John Ferguson Smart *2018-02-07 14:17:54*

**Experimenting with shadow**


[4c9ec13975b8d84](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c9ec13975b8d84) John Ferguson Smart *2018-02-07 13:09:49*

**Replaced Guava Optional with Java 8 Optional**


[a05f7e6f4387320](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a05f7e6f4387320) John Ferguson Smart *2018-02-07 12:55:24*

**Experimenting with shadows**


[0318e402d37bf02](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0318e402d37bf02) John Ferguson Smart *2018-02-07 11:15:31*

**Extending the shadow publication config**


[f9ba6265db37126](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f9ba6265db37126) John Ferguson Smart *2018-02-07 02:52:19*

**Extending the shadow publication config**


[6bf1754c7a9bd87](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6bf1754c7a9bd87) John Ferguson Smart *2018-02-07 02:37:30*


## v1.8.22-rc.3
### GitHub #1108 

**Catch exceptions while taking a screenshot (#1108)**


[ae5efdfda4403d7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ae5efdfda4403d7) Christoph Kutzinski *2018-02-06 13:38:00*


### No issue

**Added the shadow jar to the bintray publications**


[37fb38f9ab6d989](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/37fb38f9ab6d989) John Ferguson Smart *2018-02-07 02:27:32*

**Experimental configuration where guava and asm are shaded to avoid dependency conflicts.**


[58ded174f3f91ea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/58ded174f3f91ea) John Ferguson Smart *2018-02-07 02:15:10*

**Refactored Guava commons libraries away**


[997e0b3a8de544d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/997e0b3a8de544d) John Ferguson Smart *2018-02-06 23:04:13*


## v1.8.22-rc.2
### No issue

**do not use AppiumFieldDecorator anymore but the SmartFieldDecorator**


[9704ba3e7f0798d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9704ba3e7f0798d) cliviu *2018-01-23 22:46:14*


## v1.8.21
### GitHub #1075 

**Updated appium java client to 6.0.0-BETA2 (#1075)**


[f3f89e810f0a4e0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3f89e810f0a4e0) John Ferguson Smart *2018-01-11 06:37:01*


## v1.8.20
### No issue

**Updated unit tests**


[2841338f282f677](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2841338f282f677) John Ferguson Smart *2018-01-04 10:31:26*

**Overview summaries should not be shown for features or stories (they have their own narrative text)**


[c8e76323dda92ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c8e76323dda92ab) John Ferguson Smart *2018-01-04 10:16:02*


## v1.8.19
### No issue

**Fixed a defect where the reports crashed if too deep a requirement hierarchy was specified in the serenity.properties file**


[c51b204e90f211b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c51b204e90f211b) John Ferguson Smart *2018-01-02 01:23:35*


## v1.8.18
### No issue

**Fixed an issue with requirements overviews**


[efd6496262fde6d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/efd6496262fde6d) John Ferguson Smart *2018-01-02 00:40:07*


## v1.8.17
### No issue

**Fixed a formatting issue**


[2d8aed01ee2fc53](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d8aed01ee2fc53) John Ferguson Smart *2018-01-01 16:00:18*

**Fixed a formatting error in overview texts**


[f0a8d90b388ad53](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f0a8d90b388ad53) John Ferguson Smart *2018-01-01 15:37:37*


## v1.8.16
### No issue

**Better support for living documentation features**


[aad23ab971caefa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aad23ab971caefa) John Ferguson Smart *2018-01-01 11:46:35*


## v1.8.15
### No issue

**Additional unit tests**


[4280866a80b8204](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4280866a80b8204) John Ferguson Smart *2017-12-31 15:33:58*

**Requirements overview: You can now add a file called overview.md at the root of your features or stories directory, or root requirements package, to appear on the requirements overview page.**


[5590f0e24638b61](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5590f0e24638b61) John Ferguson Smart *2017-12-31 15:16:09*

**Fixed an issue where copying project-specific assets could cause the report generation to fail.**


[6622624f127bef5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6622624f127bef5) John Ferguson Smart *2017-12-31 12:18:50*


## v1.8.14
### No issue

**Fixed a defect  that caused reports to crash when copying project-specific assets**


[a212a5781f4cbfa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a212a5781f4cbfa) John Ferguson Smart *2017-12-31 11:32:39*

**Updated changelog**


[d79428da9b121c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d79428da9b121c6) John Ferguson Smart *2017-12-31 10:35:23*


## v1.8.13
### No issue

**Added experimental support for dynamic tables in JUnit.**

 * ``` 
 * @Test 
 * public void should_create_a_data_table_dynamically() { 
 * ExampleTables.useExampleTable().withHeaders(&quot;Fruit&quot;,&quot;Fruit Salad&quot;) 
 * .andTitle(&quot;Fruit Salad!&quot;) 
 * .start(); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;apple&quot;, true, 0); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;pear&quot;, true,0); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;tomato&quot;, false,0); 
 * } 
 * . 
 * . 
 * . 
 * @Step(exampleRow = true) 
 * public void can_go_in_a_fruit_salad(String ingredient, boolean yesNo) { 
 * check_recipe(); 
 * check_ingredients(); 
 * } 
 * ``` 

[852f25c89d317c8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/852f25c89d317c8) John Ferguson Smart *2017-12-31 09:52:52*

**Added support for .md extension for narrative files**


[bbbc7bc87cbcfb0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bbbc7bc87cbcfb0) John Ferguson Smart *2017-12-27 17:20:32*

**Added experimental support for dynamic tables in JUnit.**

 * ``` 
 * @Test 
 * public void should_create_a_data_table_dynamically() { 
 * ExampleTables.useExampleTable().withHeaders(&quot;Fruit&quot;,&quot;Fruit Salad&quot;) 
 * .andTitle(&quot;Fruit Salad!&quot;) 
 * .start(); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;apple&quot;, true, 0); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;pear&quot;, true,0); 
 * dataSteps.can_go_in_a_fruit_salad(&quot;tomato&quot;, false,0); 
 * } 
 * . 
 * . 
 * . 
 * @Step(exampleRow = true) 
 * public void can_go_in_a_fruit_salad(String ingredient, boolean yesNo) { 
 * check_recipe(); 
 * check_ingredients(); 
 * } 
 * ``` 

[6f0a331b27f929c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f0a331b27f929c) John Ferguson Smart *2017-12-27 12:29:20*

**Better support for @Managed drivers on remote platforms.**

 * When you specify a driver using the @Managed annotation, and then provide remote connection details, the driver will be passed to the remote environment. 

[374a46f69222cb5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/374a46f69222cb5) John Ferguson Smart *2017-12-27 08:47:50*

**Updated core.css**

 * Increased the margin-left px to 30px from 15px. 

[258d9be68b20736](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/258d9be68b20736) Vaahin *2017-12-19 14:56:22*


## v1.8.12
### No issue

**The driver specified in the @Managed annotations is now honored when you run the tests using Selenium grid**


[e787b7ef1886e83](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e787b7ef1886e83) John Ferguson Smart *2017-12-19 12:29:41*

**Fixed an issue where the @Managed driver assigned to an actor was overrided by the default driver.**


[6da7305b9c9c139](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6da7305b9c9c139) John Ferguson Smart *2017-12-19 08:28:50*

**Fixed a bug which prevented an actor being reassigned a driver**


[98fb29c1e7a0d9e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/98fb29c1e7a0d9e) John Ferguson Smart *2017-12-18 23:02:27*

**Adding 'Hover over' Interaction in Screenplay Webdriver**


[4eb1d74c2de7977](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4eb1d74c2de7977) Dimitrios Liapis *2017-12-18 22:01:31*


## v1.8.11
### No issue

**Added the ability to check against web element state in the Check interation class, e.g.**

 * ``` 
 * Check.whether(the(TripPlanner.SEARCHING_MESSAGE), isCurrentlyVisible()) 
 * .andIfSo(WaitUntil.the(TripPlanner.SEARCHING_MESSAGE, isNotCurrentlyVisible())) 
 * ``` 

[48251f05ed1bc69](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/48251f05ed1bc69) John Ferguson Smart *2017-12-18 11:37:06*

**Updated changelog**


[37125365a27a564](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/37125365a27a564) John Ferguson Smart *2017-12-17 21:41:50*


## v1.8.10
### No issue

**Added the @Shared annotation, which is a more intuitively named synonym of @Steps(shared=true).**

 * Fixed a defect where step libraries where not shared correctly in Screenplay tests. 

[e7aadfd845a9491](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7aadfd845a9491) John Ferguson Smart *2017-12-17 19:20:39*


## v1.8.9
### No issue

**Added the WaitUntil.angularRequestsHaveFinished() to integrate with ngWebDriver**


[e687adc8bd7dac3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e687adc8bd7dac3) John Ferguson Smart *2017-12-15 11:43:35*


## v1.8.8
### No issue

**Added integration with ngWebDriver, to allow better support for AngularJS**


[49acdb25c18caa0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/49acdb25c18caa0) John Ferguson Smart *2017-12-15 09:39:44*

**updated appium java client version**


[a633fbd5038bfcd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a633fbd5038bfcd) Vikram Ingleshwar *2017-12-14 19:41:52*

**Added more details for errors with custom class loading**


[5ddde6cef94cb5d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5ddde6cef94cb5d) John Ferguson Smart *2017-12-14 07:59:05*


## v1.8.7
### No issue

**Fixed another unit test**


[d97f14a4fbac33e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d97f14a4fbac33e) John Ferguson Smart *2017-12-13 20:22:59*

**Fixed a failing unit test**


[0dfc6226dbe66a7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0dfc6226dbe66a7) John Ferguson Smart *2017-12-13 20:16:44*

**Serenity reporting now supports copying project assets (images etc.) that can be referenced in the markdown parts of the test reports.**

 * By default, files in src/test/resources/assets will be copied to target/site/serenity/assets. The directory where the assets are stored can be configered via the report.assets.directory property. 

[8efa8836863ca74](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8efa8836863ca74) John Ferguson Smart *2017-12-13 20:04:16*

**Cucumber tags for example tables now appear with the relevant table in the reports.**


[10e7f2010b657a2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/10e7f2010b657a2) John Ferguson Smart *2017-12-13 08:31:45*

**Upgrade to Cucumber 2.3.0.**


[75cb4469a0b336f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/75cb4469a0b336f) John Ferguson Smart *2017-12-13 08:30:54*

**Updated changelog**


[b07ce9029c44a4b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b07ce9029c44a4b) John Ferguson Smart *2017-12-10 19:39:47*

**Ensure that step libraries are cleared at the end of a test run.**


[0f19aaffa5ca84a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0f19aaffa5ca84a) John Ferguson Smart *2017-12-08 07:12:20*

**Ensure that Serenity test cases are detected for both Serenity and legacy Thucydides tests.**


[4d13a8e32c1c88b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4d13a8e32c1c88b) John Ferguson Smart *2017-12-08 07:10:29*

**Update to Selenium 3.8.1**


[5722fde4c8c7ef7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5722fde4c8c7ef7) John Ferguson Smart *2017-12-08 07:09:10*


## v1.8.5
### No issue

**fix: use correct internet explorer icon for context 'ie'**


[a982b46e4c92723](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a982b46e4c92723) Oliver Becker *2017-12-09 11:48:58*


## v1.8.4
### No issue

**fixed reporting bug (Examples title was not displayed)**


[e6968b8d798d233](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6968b8d798d233) cliviu *2017-12-05 23:13:07*

**refactor: replace System.out.println with logger API call**


[cc293fa1cd96ec2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cc293fa1cd96ec2) Pavlo Vlasenko *2017-12-05 12:04:05*

**fix: ascii heading for compromised test result**


[a421c5108eb6c44](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a421c5108eb6c44) Pavlo Vlasenko *2017-12-04 11:25:02*

**add custom findBy**


[805ab5cd541444a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/805ab5cd541444a) Sergio Sacristan *2017-12-03 21:06:30*


## v1.8.3
### GitHub #1026 

**Fixed #1026 - @WhenPageOpens-annotated methods were still executed even if a test was suspended.**


[2fef8e86c3ce40c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2fef8e86c3ce40c) John Ferguson Smart *2017-12-03 09:16:11*


## v1.8.2
### GitHub #1026 

**Fixed #1026 - @WhenPageOpens-annotated methods were still executed even if a test was suspended.**


[cd4a2ded98d50dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd4a2ded98d50dc) John Ferguson Smart *2017-12-02 09:48:42*


### No issue

**Declare Selenium dependencies to avoid them being overridden by other transitive dependencies.**


[d12e2832fa32081](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d12e2832fa32081) John Ferguson Smart *2017-12-02 09:48:07*

**Upgraded to RestAssured 3.0.5**


[b3838106e4e133f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b3838106e4e133f) John Ferguson Smart *2017-12-02 09:47:12*

**Updated changelog**


[99d41bfbe25c705](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/99d41bfbe25c705) John Ferguson Smart *2017-11-28 18:15:13*

**fixed order of row elements : https://github.com/serenity-bdd/serenity-cucumber/issues/105**


[6e85dcb12b7ced4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6e85dcb12b7ced4) cliviu *2017-11-26 16:17:38*

**Fixed an issue converting enums in Screenplay on some JVMs**


[0abe1cf2a796f73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0abe1cf2a796f73) John Ferguson Smart *2017-11-23 09:29:44*

**test refactoring to ensure all of the serenity-core tests run on Windows build machines**


[d60f1da87842a85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d60f1da87842a85) John Ferguson Smart *2017-11-23 07:52:40*

**Minor fixes for the build on Windows**


[1a01fd52a1086e2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a01fd52a1086e2) John Ferguson Smart *2017-11-22 22:11:24*


## v1.8.1
### No issue

**Implicit wait intervals are no 50ms rather than 500ms.**


[2baf86fa0ee9104](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2baf86fa0ee9104) John Ferguson Smart *2017-11-20 15:50:48*

**Fixed an issue which resulted in incorrect requirements reporting for JUnit tests**


[2b8aba1e9919380](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2b8aba1e9919380) John Ferguson Smart *2017-11-20 15:50:19*

**fixed script as per latest changes in framework related to appium driver handling in saucelabs**


[42477b263c6d173](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42477b263c6d173) Vikram Ingleshwar *2017-11-18 14:35:37*


## v1.8.0
### No issue

**fix bug when initialising page elements using AppiumFieldDecorator**


[5bb7e39906e9580](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5bb7e39906e9580) cliviu *2017-11-16 23:36:01*

**Fixed a bug in the consequence reporting**


[22d6212c34cf4fb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/22d6212c34cf4fb) John Ferguson Smart *2017-11-16 21:45:48*

**Fixed a broken unit test**


[42d4ef4f36f2f29](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42d4ef4f36f2f29) John Ferguson Smart *2017-11-16 21:34:36*

**Waits should not be reported as consequences in the reports.**


[8c0f25237f38509](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8c0f25237f38509) John Ferguson Smart *2017-11-16 21:13:04*

**Fixed a bug with the Select element integration, where the select element was not used correctly as it is not visible for Selenium**


[fe536f89364e801](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe536f89364e801) John Ferguson Smart *2017-11-16 21:12:16*

**Use FirefoxOptions instead of Capabilities, and added the headless.mode serenity property to allow headless browsing in Firefox.**


[15ded2bff6b3d15](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/15ded2bff6b3d15) John Ferguson Smart *2017-11-16 21:11:22*

**Helper methods for anonymous tasks and interactions.**


[5c3d911b9ceca8e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5c3d911b9ceca8e) John Ferguson Smart *2017-11-16 21:10:04*


## v1.7.5
### GitHub #1027 

**#1027 - remove spurious characters from last commit causing compilation error**


[e7b47c2ba3c26ff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7b47c2ba3c26ff) nickbarrett *2017-11-14 17:40:13*


### No issue

**Added the openUrl() method to open an absolute URL**


[1a9348a5e504a1a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a9348a5e504a1a) John Ferguson Smart *2017-11-12 16:33:51*

**add AcceptanceTestFullReporter listener to generate reports as in JUnitXMLOutcomeReporter**


[18b1d84edce9b05](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/18b1d84edce9b05) Sergio Sacristan *2017-11-12 01:19:30*


## v1.7.4
### No issue

**Added checks to ensure that webdriver calls are not attempted if a webelement is inadvertantly used after the test is suspended**


[c21fad2e2f69628](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c21fad2e2f69628) John Ferguson Smart *2017-11-12 11:54:26*


## v1.7.3
### GitHub #1006 

**Fixed #1006**


[9d6d7dbe56ad360](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d6d7dbe56ad360) John Ferguson Smart *2017-11-11 09:34:23*


### No issue

**Removed redundant test**


[0da1064b4bdfca8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0da1064b4bdfca8) John Ferguson Smart *2017-11-11 10:08:32*

**Refactoring to remove guava collection code.**


[3c45718460f6bda](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3c45718460f6bda) John Ferguson Smart *2017-11-11 09:34:43*


## v1.7.2
### GitHub #1012 

**Test to reproduce #1012**


[091c76ce7ce7031](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/091c76ce7ce7031) John Ferguson Smart *2017-11-08 17:23:02*

**Test to reproduce #1012**


[e6bd81c28b48aa1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6bd81c28b48aa1) John Ferguson Smart *2017-11-08 17:20:20*


### No issue

**Update Locatable to interactions package**

 * Update according to https://github.com/SeleniumHQ/selenium/commit/b37c27b4c62c4ba336a3a96b125f86ad0f15f04b 

[5f26e5b5913de51](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f26e5b5913de51) Oleg Yatsun *2017-11-08 16:31:32*

**Updated changelog to 1.7.1**


[15ad1c23afaedba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/15ad1c23afaedba) John Ferguson Smart *2017-11-08 15:24:30*


## v1.7.1
### No issue

**Cater for empty firefox preferences**


[109c56703749dc3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/109c56703749dc3) John Ferguson Smart *2017-11-08 14:39:16*

**Upgraded to Selenium 3.7.1, and some minor refactoring.**


[fbe109a8947ad81](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fbe109a8947ad81) John Ferguson Smart *2017-11-08 13:57:54*

**chore: replaced deprecated setUseCleanSession() with useCleanSession() in the Safari options.**


[ef7c0888b1d2dcb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef7c0888b1d2dcb) John Ferguson Smart *2017-11-08 12:02:47*


## v1.7.0
### No issue

**Updated redundant unit test**


[7a8020159979ecd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a8020159979ecd) John Ferguson Smart *2017-11-05 22:06:13*

**Added support for steps and page objects in nested classes.**

 * Step libraries or page objects declared as nested classes, and which have a default constructor, also need the enclosing class to have a default constructor as well. 

[ace08918de5b710](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ace08918de5b710) John Ferguson Smart *2017-11-05 21:45:50*

**Updated changelog title**


[1a4e720f73002f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a4e720f73002f8) John Ferguson Smart *2017-11-05 14:28:31*

**Fixed the title in the changelog**


[d654b7d7be8af63](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d654b7d7be8af63) John Ferguson Smart *2017-11-05 09:57:28*

**Updated changelog for version 1.7.0-rc.1**


[2eeb1f18d001113](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2eeb1f18d001113) John Ferguson Smart *2017-11-05 09:45:56*


## v1.7.0-rc.1
### No issue

**Reporting improvement: don't display the scenario outline if empty**


[1168366de1a43bd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1168366de1a43bd) John Ferguson Smart *2017-11-04 13:04:45*

**Upgraded to Selenium 3.7.0**


[8d3c04066801841](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d3c04066801841) John Ferguson Smart *2017-11-03 23:44:18*

**Updated the event bus logic to cater for the Cucumber 2.x lifecycle**


[487a8764926f60e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/487a8764926f60e) John Ferguson Smart *2017-11-03 23:43:14*

**Added a property to clear cookies and/or clean session with Safari (see https://github.com/serenity-bdd/serenity-cucumber/issues/90)**

 * The `safari.use.clean.session` property can now be used to instruct the SafariDriver to delete all existing session data when starting a new session, by setting this value to &quot;true&quot;. This includes browser history, cache, cookies, HTML5 local storage, and HTML5 databases. Not that since Safari uses a single profile for the current user, enabling this capability will permanently erase any existing session data. 

[593ad9d4eb95e2e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/593ad9d4eb95e2e) John Ferguson Smart *2017-11-02 09:49:34*

**Updated the changelog format**


[dbe048362240de9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dbe048362240de9) John Ferguson Smart *2017-11-01 18:31:48*

**Updated the changelog**


[ec39bacf66ef97e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ec39bacf66ef97e) John Ferguson Smart *2017-11-01 18:20:30*

**chore: Added an experimental CHANGELOG generator**


[39c9885e133b0a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39c9885e133b0a8) John Ferguson Smart *2017-11-01 18:15:53*


## v1.6.9
### No issue

**Fixed minor reporting glitch**


[d9cf8570550ccae](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9cf8570550ccae) John Ferguson Smart *2017-11-01 16:19:51*


## v1.6.8
### No issue

**Better rendering of non-ASCII chars in REST tests**


[49e2888a6f38278](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/49e2888a6f38278) John Ferguson Smart *2017-11-01 15:00:48*

**Switching back to Selenium 3.5.3 because of a bug in Selenium 3.6.0 in PhantomJS support**

 * (see https://github.com/SeleniumHQ/selenium/issues/4781) 

[31a226b10fcb59e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/31a226b10fcb59e) John Ferguson Smart *2017-11-01 11:27:20*

**Updated to Selenium 3.6.0 and Cucumber 2.1.0**


[b7f0798c96e8ca9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b7f0798c96e8ca9) John Ferguson Smart *2017-11-01 10:56:25*

**Updated to Selenium 3.6.0 and Cucumber 2.1.0**


[f594ba1732f9247](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f594ba1732f9247) John Ferguson Smart *2017-11-01 10:55:00*


## v1.6.7
### GitHub #981 

**Fixed #981**


[ba400cb16701147](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba400cb16701147) John Ferguson Smart *2017-10-24 08:16:36*


### GitHub #984 

**Attempted patch for #984**


[3744419d7c1cb45](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3744419d7c1cb45) John Ferguson Smart *2017-11-01 10:05:02*

**Possible fix for #984 - allow REST calls outside of the Serenity step lifecycle**


[05ee3d700106ced](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/05ee3d700106ced) John Ferguson Smart *2017-10-31 07:28:04*


### GitHub #985 

**Fix for #985**


[d3859a9b401b4d5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d3859a9b401b4d5) John Ferguson Smart *2017-10-24 11:39:08*


### GitHub #990 

**Fixed #990**


[3f77a847b7cb24f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f77a847b7cb24f) John Ferguson Smart *2017-10-31 22:30:40*


### GitHub #991 

**Fixed #991**


[3da32e7fd2a295c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3da32e7fd2a295c) John Ferguson Smart *2017-10-31 22:55:00*


### No issue

**Refactored the AjaxElementLocator for better readability**


[7b3fcb4f4e1fce8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7b3fcb4f4e1fce8) John Ferguson Smart *2017-11-01 09:49:46*

**Minor refactoring.**


[00785dcc852872d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/00785dcc852872d) John Ferguson Smart *2017-10-31 22:33:01*

**Fixed an error when trailing spaces in a numerial property value (e.g. serenity.brower.width) could cause the browser instantiation to fail.**


[c129fa0a7092999](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c129fa0a7092999) John Ferguson Smart *2017-10-31 22:32:32*

**Improved support for Saucelabs Appium integration**


[6f815a2153217e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f815a2153217e6) John Ferguson Smart *2017-10-31 07:16:25*

**fix for https://github.com/serenity-bdd/serenity-core/issues/970**


[88a9dd08132aa3b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/88a9dd08132aa3b) cliviu *2017-10-27 22:50:07*

**Fixed a broken unit test**


[022283cbbe37d58](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/022283cbbe37d58) John Ferguson Smart *2017-10-20 15:34:35*

**Added the serenity.browser.resizing system property - set to false if you don't want Serenity to resize the browser for you**


[658d34e28622f40](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/658d34e28622f40) John Ferguson Smart *2017-10-19 12:34:22*

**serenity+appium integration iOS platform first test case**


[8756802ca1cae7b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8756802ca1cae7b) Vikram Ingleshwar *2017-10-05 14:40:01*


## v1.6.7-rc.1
### No issue

**Bug fix: dates where not being correctly written to the CSV reports**


[668c20a8aa8d8ea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/668c20a8aa8d8ea) John Ferguson Smart *2017-10-18 16:27:01*

**Updated to appium 5.0.4**


[ba200f7048c0a75](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba200f7048c0a75) John Ferguson Smart *2017-10-17 22:16:59*

**Added better formatting for the tag lists on the report home page.**


[6eec9f0a02bff95](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6eec9f0a02bff95) John Ferguson Smart *2017-10-17 21:22:52*


## v1.6.6-rc.2
### No issue

**Fixed an issue where poorly formatted story files resulted in requirements with blank names**


[b3c6066069f4226](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b3c6066069f4226) John Ferguson Smart *2017-10-17 18:25:32*

**Fixed an issue where poorly formatted story files resulted in requirements with blank names**


[eed7e6cd595eaa7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eed7e6cd595eaa7) John Ferguson Smart *2017-10-17 18:14:07*

**Fixed a bug that could break the reports if there were too many nested directories and story files with identical names**


[57e0c568c4cfdac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/57e0c568c4cfdac) John Ferguson Smart *2017-10-17 17:15:42*


## v1.6.6-rc.1
### GitHub #971 

**Fixed #971 and #972**


[588b30d794219a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/588b30d794219a9) John Ferguson Smart *2017-10-17 10:06:42*


### GitHub #972 

**Fixed #971 and #972**


[588b30d794219a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/588b30d794219a9) John Ferguson Smart *2017-10-17 10:06:42*


### No issue

**Moving gradle plugin tests to integration and smoke tests**


[ce25598439f8c37](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ce25598439f8c37) John Ferguson Smart *2017-10-17 16:02:17*

**Trying to fix a groovy compatibility issue on the build server (still)**


[6670583b36cd200](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6670583b36cd200) John Ferguson Smart *2017-10-17 15:55:57*

**Trying to fix a groovy compatibility issue on the build server (still)**


[96e6fba3b579d7e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/96e6fba3b579d7e) John Ferguson Smart *2017-10-17 15:51:39*

**Trying to fix a groovy compatibility issue on the build server (still)**


[ebb1947651720fc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ebb1947651720fc) John Ferguson Smart *2017-10-17 15:44:35*

**Trying to fix a groovy compatibility issue on the build server (still)**


[ddb73b7e9743ffb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ddb73b7e9743ffb) John Ferguson Smart *2017-10-17 15:31:19*

**Trying to fix a groovy compatibility issue on the build server**


[0ad946fdd33a8f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ad946fdd33a8f8) John Ferguson Smart *2017-10-17 15:23:56*

**Fixed an error in the requirements breadcrumbs**


[e16aebfe86751c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e16aebfe86751c6) John Ferguson Smart *2017-10-17 15:11:46*

**Attempting to fix a gradle build issue**


[3d1220c97d50b90](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3d1220c97d50b90) John Ferguson Smart *2017-10-17 14:12:49*

**Fixed an error in the requirements breadcrumbs**


[36c617886cc48ce](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36c617886cc48ce) John Ferguson Smart *2017-10-17 13:48:52*

**Trying to find the right match of Groovy/Gradle libraries for all envs**


[8900284f3391bd8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8900284f3391bd8) John Ferguson Smart *2017-10-15 13:08:20*

**Added a default toString to the ScenarioActor class to print the name of the actor (fixed a unit test)**


[29c46e307d367f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/29c46e307d367f6) John Ferguson Smart *2017-10-15 11:31:00*

**Added a default toString to the ScenarioActor class to print the name of the actor**


[471ab926ed8dac3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/471ab926ed8dac3) John Ferguson Smart *2017-10-15 11:28:44*


## v1.6.5
### No issue

**Trouble-shooting failing tests on Jenkins (v4)**


[5e1a88a781c3baf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5e1a88a781c3baf) John Ferguson Smart *2017-10-15 10:24:29*

**Trouble-shooting failing tests on Jenkins (v3)**


[5208e973a7fc2f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5208e973a7fc2f8) John Ferguson Smart *2017-10-15 10:15:30*

**Trouble-shooting failing tests on Jenkins**


[8b64515efdb4c0a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b64515efdb4c0a) John Ferguson Smart *2017-10-15 09:35:57*

**Trouble-shooting failing tests on Jenkins**


[bc2adb29986e28f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc2adb29986e28f) John Ferguson Smart *2017-10-15 09:20:18*

**Trouble-shooting failing tests on Jenkins**


[62477f5180fa783](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/62477f5180fa783) John Ferguson Smart *2017-10-15 09:03:54*

**Avoid returning nulls in WebDriver calls after a test has failed**


[b6388b508bdd10c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6388b508bdd10c) John Ferguson Smart *2017-10-15 08:11:40*

**Added the ScenarioActor class, a convenience class to make it easier to use Scenario step libraries as personas in tests.**


[7431935ca7a144a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7431935ca7a144a) John Ferguson Smart *2017-10-15 07:43:34*

**A unit test to clarify the heuristics used when detecting XPath and CSS selectors.**


[7ae3a232bc3882b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ae3a232bc3882b) John Ferguson Smart *2017-10-13 15:34:23*

**Fixed a potential bug where instrumented step methods return a null value incorrectly after a previous passing step.**


[2615d7be6be036c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2615d7be6be036c) John Ferguson Smart *2017-10-13 15:33:02*


## v1.6.4-rc.2
### No issue

**cucumber 2.0 integration - remove not used classes**


[f105f9cb718e497](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f105f9cb718e497) cliviu *2017-10-06 20:18:01*

**cucumber2**


[ddd957a71448d5e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ddd957a71448d5e) liviu.carausu *2017-09-07 13:17:03*

**cucumber2**


[1901cdb893def46](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1901cdb893def46) liviu.carausu *2017-09-07 12:50:59*

**cucumber2**


[ed05fd4ccf7fe74](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ed05fd4ccf7fe74) liviu.carausu *2017-09-07 12:16:37*

**cucumber 2.0 integration**


[42eccd6e4b8e8cf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42eccd6e4b8e8cf) cliviu *2017-09-06 22:44:27*

**cucumber2**


[463b359a39a1318](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/463b359a39a1318) liviu.carausu *2017-09-06 16:06:13*


## v1.6.4-rc.1
### GitHub #955 

**Better support for out-of-lifecycle calls to Serenity methods. Possible fix for #955.**


[2966d13afe7cf9e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2966d13afe7cf9e) John Ferguson Smart *2017-10-06 10:19:41*


### No issue

**Run Chrome tests on Linux in headless mode**


[ddd4c40fc227911](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ddd4c40fc227911) John Ferguson Smart *2017-10-06 15:57:30*

**Fixed link format in readme.**


[e05fd1802c248fe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e05fd1802c248fe) John Ferguson Smart *2017-10-06 15:16:24*

**Added a section on raising issues in the readme file**


[928bba7ba909fd5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/928bba7ba909fd5) John Ferguson Smart *2017-10-06 15:04:31*

**General refactoring and removing a redundant test.**


[fa6509a9ba5db1e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fa6509a9ba5db1e) John Ferguson Smart *2017-10-06 10:20:04*

**Fixed a broken test**


[0f8fe3db8a739df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0f8fe3db8a739df) John Ferguson Smart *2017-10-05 21:23:04*

**Fixed a broken test**


[118c7eb70b8974e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/118c7eb70b8974e) John Ferguson Smart *2017-10-05 21:19:32*

**Fixed a broken test**


[b8ba4b2edda80e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b8ba4b2edda80e3) John Ferguson Smart *2017-10-05 21:03:18*

**Refactored the FileToUpload logic**


[25d000fe6183961](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/25d000fe6183961) John Ferguson Smart *2017-10-05 20:57:33*

**Upload files feature a bit more robust**


[ef4f0221a93a2db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef4f0221a93a2db) John Ferguson Smart *2017-10-05 13:47:08*

**Improved logging to avoid unnecessary log messages during tests**


[ff0244fba6d0aed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ff0244fba6d0aed) John Ferguson Smart *2017-10-05 10:00:45*

**Going back to Selenium 3.5.3 as version 3.6.0 seems buggy.**


[2eb06efab84440d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2eb06efab84440d) John Ferguson Smart *2017-10-05 09:48:48*

**Going back to Selenium 3.5.3 as version 3.6.0 seems buggy**


[f36b4cea0c7092f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f36b4cea0c7092f) John Ferguson Smart *2017-10-05 09:45:49*

**Removed some use of Guava Lists and minor tidying up**


[347cb00706c70ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/347cb00706c70ef) John Ferguson Smart *2017-10-05 06:20:45*

**Fixed a dependency conflict with selenium 3.6.0**


[d6e8a1562ffa950](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d6e8a1562ffa950) John Ferguson Smart *2017-10-04 12:47:50*

**Updated selenium to 3.6.0**


[ab2cbd1412d08a6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ab2cbd1412d08a6) John Ferguson Smart *2017-10-04 12:30:15*

**Added integration tests for CircleCI**


[6035944faf66467](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6035944faf66467) John Ferguson Smart *2017-10-04 08:59:13*

**feat: Expand references to environment variables and system properties in serenity.conf & serenity.properties.**

 * Environment variables can be references using ${AN_ENVIRONMENT_VARIABLE} and System Properties using ${system.property.example}. 

[ea327a8214ab996](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ea327a8214ab996) David Paterson *2017-10-01 15:37:51*


## v1.6.3
### No issue

**Use the actor field instead of the name field for more consistency with Serenity/JS**


[4e3d4a026b3539d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e3d4a026b3539d) John Ferguson Smart *2017-10-02 11:48:05*


## v1.6.2
### No issue

**Better support for using @Step libraries as personas.**

 * If you add a String field called &#39;name&#39; to a step library, it will be assigned the name of the step library variable, or the &#39;name&#39; attribute from the @Steps annotation. 

[4b9c0a92318e73e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b9c0a92318e73e) John Ferguson Smart *2017-10-02 10:51:09*


## v1.6.1
### GitHub #945 

**Fixed #945**


[812d3dc408ff706](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/812d3dc408ff706) John Ferguson Smart *2017-09-30 13:49:45*


### GitHub #947 

**Revert "Merge pull request #947 from bjss/feature/expand_property_variable_references"**

 * This reverts commit 8eafb9a34d6110787bb244bde087cd099892f877, reversing 
 * changes made to a6b917c51be0ee8815bdd8eb28043a47b323a214. 

[2be6ef25f841486](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2be6ef25f841486) John Ferguson Smart *2017-09-30 20:03:13*


### No issue

**feat: Expand references to environment variables and system properties in serenity.conf & serenity.properties.**

 * Environment variables can be references using ${AN_ENVIRONMENT_VARIABLE} and System Properties using ${system.property.example}. 

[1903adc4393d2a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1903adc4393d2a8) David Paterson *2017-09-30 19:08:46*

**Add a link to the Serenity BDD site to the console banner**


[a6b917c51be0ee8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a6b917c51be0ee8) John Ferguson Smart *2017-09-30 18:50:11*

**Minor reporting improvements.**


[6cd22e66749fc47](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6cd22e66749fc47) John Ferguson Smart *2017-09-30 13:58:33*

**Refactoring.**


[7207c9be2c36eae](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7207c9be2c36eae) John Ferguson Smart *2017-09-27 15:40:49*

**Deleted Travis config file.**


[adf9745b0f5f272](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/adf9745b0f5f272) John Ferguson Smart *2017-09-27 15:40:13*

**Trying TravisCI**


[25089875031b168](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/25089875031b168) John Ferguson Smart *2017-09-26 17:21:13*

**Deactivating the Gradle daemon for CircleCI**


[f3d6f0714b57225](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3d6f0714b57225) John Ferguson Smart *2017-09-26 16:27:43*

**Disable gradle daemon for circleci**


[b646eda79d673a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b646eda79d673a0) John Ferguson Smart *2017-09-26 14:23:45*

**Trying out CircleCI 2**


[8518d41ad257230](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8518d41ad257230) John Ferguson Smart *2017-09-26 13:56:25*

**Added an experimental CircleCI config file**


[797ac01c8af4af8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/797ac01c8af4af8) John Ferguson Smart *2017-09-26 13:52:36*

**fix: remove extra point in ChromePreferences getPreparedPropertyKey**


[139c93e182ced99](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/139c93e182ced99) eshvan *2017-08-16 18:39:02*


## v1.6.0
### GitHub #933 

**Possible fix for #933**


[f35a0fcfa16da81](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f35a0fcfa16da81) John Ferguson Smart *2017-09-23 06:18:43*


### No issue

**Removed unnecessary code.**


[b40cf1bb8bb4274](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b40cf1bb8bb4274) John Ferguson Smart *2017-09-25 20:21:57*

**Refactored the way step libraries are instantiated.**


[bb744b5b503a40e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bb744b5b503a40e) John Ferguson Smart *2017-09-25 15:34:41*

**Update to Guava 23.0 to match the Selenium dependency.**


[a41a75fb272321e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a41a75fb272321e) John Ferguson Smart *2017-09-23 06:17:35*

**Removed deprecated references to jcommander**


[9e5ee8231b06260](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e5ee8231b06260) John Ferguson Smart *2017-09-15 16:45:54*


## v1.5.12
### No issue

**updated versions of selenium & appium**


[ab4886322f632b1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ab4886322f632b1) Vikram Ingleshwar *2017-09-23 05:22:50*


## v1.5.11
### GitHub #928 

**Fixed #928**


[5d419c8ed01e8da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d419c8ed01e8da) John Ferguson Smart *2017-09-14 12:46:32*


## v1.5.10
### No issue

**Update to Appium 5.0.2**


[2ae6b9cb0c5c550](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ae6b9cb0c5c550) John Ferguson Smart *2017-09-06 05:54:28*

**Fix hostIsAvailableAt if behind proxy**


[48b87a520f406e0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/48b87a520f406e0) derkoe *2017-09-04 14:00:40*

**refactor: replace lambdaj with plain Java 8**


[48b9d98a17edfeb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/48b9d98a17edfeb) Valery Yatsynovich *2017-08-31 18:57:22*


## v1.5.9
### GitHub #911 

**Fixed #911, and some minor refactoring**


[ddf7e2378b1a508](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ddf7e2378b1a508) John Ferguson Smart *2017-08-30 17:12:39*


### No issue

**Fixed templating issue in the reports**


[353f8f95b50b5e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/353f8f95b50b5e9) John Ferguson Smart *2017-08-31 06:25:56*

**updated appium java client version**

 * updated commonsCodecVersion version to make it compatible with latest appium java client 

[196583bdffa680f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/196583bdffa680f) Vikram Ingleshwar *2017-08-30 16:38:21*

**Removed the CircleCI badge as the build is too big**


[481675803b31d5a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/481675803b31d5a) John Ferguson Smart *2017-08-30 13:46:54*

**Added CircleCI badge**


[69c6f571c390cbf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/69c6f571c390cbf) John Ferguson Smart *2017-08-30 12:35:56*

**Removed test reporting from the Circle CI build because it can't handle the number of tests**


[add3cb16332f847](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/add3cb16332f847) John Ferguson Smart *2017-08-30 11:56:07*

**additional test for https://github.com/serenity-bdd/serenity-core/issues/894**


[74060d0982aa120](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/74060d0982aa120) liviu.carausu *2017-08-30 11:18:59*

**additional test for https://github.com/serenity-bdd/serenity-core/issues/894**


[e2612022a6635b2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e2612022a6635b2) cliviu *2017-08-29 22:29:45*

**Removed the redundant 'list' markdown configuration**


[16c9f9c8a304648](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/16c9f9c8a304648) John Ferguson Smart *2017-08-29 21:10:17*

**Updated dependencies**


[bef307ad0c61cab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bef307ad0c61cab) John Ferguson Smart *2017-08-29 20:40:30*


## v1.5.8
### GitHub #908 

**Fixed #908**


[84ab5eae4422908](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/84ab5eae4422908) John Ferguson Smart *2017-08-29 12:52:58*


## v1.5.7
### GitHub #894 

**Fixed #894**


[e5f7d9924e27703](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e5f7d9924e27703) John Ferguson Smart *2017-08-28 11:36:51*


### No issue

**Updated to Selenium 3.5.2**


[16ddb8feddd296a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/16ddb8feddd296a) John Ferguson Smart *2017-08-28 13:37:19*


## v1.5.6
### GitHub #898 

**Update RedimensionStrategy.java**

 * Fixed #898 

[66467a15249bf95](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/66467a15249bf95) ricardo larrahondo *2017-08-25 15:24:14*


### GitHub #904 

**Fixed #904**


[72c3bff61acb839](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/72c3bff61acb839) John Ferguson Smart *2017-08-25 15:20:06*


### No issue

**Fixed a bug where expected exceptions where not correctly handled for AssertJ**


[d8177b1f3eb51cc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d8177b1f3eb51cc) John Ferguson Smart *2017-08-23 11:06:29*

**Include a message to document expected exceptions for JUnit tests**


[e36dadeebc30692](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e36dadeebc30692) John Ferguson Smart *2017-08-22 17:19:38*


## v1.5.5
### GitHub #895 

**Fixed #895**


[f387a6ec7a599ca](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f387a6ec7a599ca) John Ferguson Smart *2017-08-21 15:56:22*


## v1.5.4
### No issue

**Updated some tests**


[e3d43bf10b48347](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e3d43bf10b48347) John Ferguson Smart *2017-08-21 12:08:22*

**Updated some tests**


[fade3a61f20acd9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fade3a61f20acd9) John Ferguson Smart *2017-08-21 11:31:21*

**Updated some tests**


[eba65c8a0877f36](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eba65c8a0877f36) John Ferguson Smart *2017-08-21 11:12:06*

**Ensure that @Steps-annotated fields of the same type, declared in different classes, refer to the same instance.**


[30e69907f3b5f6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30e69907f3b5f6e) John Ferguson Smart *2017-08-21 10:15:26*

**Updated to Selenium 3.5.1**


[75aee889187046a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/75aee889187046a) John Ferguson Smart *2017-08-21 10:13:28*

**Updated the favicon.**


[34c117cc5890dad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/34c117cc5890dad) John Ferguson Smart *2017-08-20 20:19:22*

**Updated core to 3.5.1**


[79712549bf96c4f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/79712549bf96c4f) John Ferguson Smart *2017-08-18 22:46:20*


## v1.5.4-rc.2
### GitHub #872 

**Possible fix for #872**


[4e4cd0afcdc88db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e4cd0afcdc88db) John Ferguson Smart *2017-08-03 16:56:23*


### No issue

**Fixed an issue with the web element proxy support for Appium.**


[96ba9a31a1d8503](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/96ba9a31a1d8503) John Ferguson Smart *2017-08-10 19:45:06*

**Updated unit tests**


[d6dd08f7c95a9cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d6dd08f7c95a9cd) John Ferguson Smart *2017-08-10 07:51:07*

**Updated unit tests**


[6816e0dbba2873b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6816e0dbba2873b) John Ferguson Smart *2017-08-10 07:24:02*

**Updated unit tests**


[4e87d3bee882e4d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e87d3bee882e4d) John Ferguson Smart *2017-08-10 07:22:58*

**https://github.com/serenity-bdd/serenity-core/issues/856 -avoid NPE**


[0d7183ceedbd1d2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0d7183ceedbd1d2) cliviu *2017-08-01 21:48:35*


## v1.5.4-rc.1
### GitHub #846 

**Fix #846 Test Name is not sent to Browserstack**


[b744215286049da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b744215286049da) Florin Strugariu *2017-07-20 08:33:50*


### GitHub #848 

**Fixed #848 and refactored the Gradle plugin**


[719475bd0fda7d5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/719475bd0fda7d5) John Ferguson Smart *2017-07-20 09:56:42*


### No issue

**Test refactoring**


[57f952b92412915](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/57f952b92412915) John Ferguson Smart *2017-08-02 08:29:02*

**Minor refactoring to allow better support for error handling**


[c0ef418adf58f64](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c0ef418adf58f64) John Ferguson Smart *2017-08-01 14:17:05*

**AppiumWebDriver:use AppiumFieldDecorator**


[a45d498c50581f1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a45d498c50581f1) cliviu *2017-07-26 21:31:24*

**Improved error reporting for captured exceptions.**


[90216f2986151c9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/90216f2986151c9) John Ferguson Smart *2017-07-26 07:35:03*

**A little issue on select in rport (only write "click on"**


[a3ad029de327c43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a3ad029de327c43) Labouh *2017-07-26 06:57:41*

**Delete overview.adoc**


[6b8cdabb9f46e78](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6b8cdabb9f46e78) John Ferguson Smart *2017-07-22 07:46:17*

**Create README.adoc**


[5f08dfb56496fa4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f08dfb56496fa4) John Ferguson Smart *2017-07-22 07:45:56*

**Update overview.adoc**


[37301b1b768652c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/37301b1b768652c) John Ferguson Smart *2017-07-22 07:44:42*

**Create CODE_OF_CONDUCT.md**


[9759a6ae16b7f80](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9759a6ae16b7f80) John Ferguson Smart *2017-07-18 22:33:09*


## v1.5.3
### No issue

**XML reports are no longer generated. JSON reports are always generated. HTML reports can be disabled.**


[6ec77175f8efe68](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ec77175f8efe68) John Ferguson Smart *2017-07-18 20:45:02*

**If a tag provider produces an error, it will no longer stop the report from being generated.**


[44a9eca240cf12e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/44a9eca240cf12e) John Ferguson Smart *2017-07-18 20:43:46*


## v1.5.2-rc.1
### GitHub #482 

**Fixed #482**


[38af47ee3bc966a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/38af47ee3bc966a) John Ferguson Smart *2017-07-18 12:30:45*


## v1.5.1-rc.7
### No issue

**Fixed a concurrency issue in the report generation**


[348ca83b7ea5c68](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/348ca83b7ea5c68) John Ferguson Smart *2017-07-18 08:05:39*


## v1.5.1-rc.6
### GitHub #842 

**Fixed #842**


[de325faa187b89d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/de325faa187b89d) John Ferguson Smart *2017-07-18 06:20:26*


## v1.5.1-rc.5
### GitHub #841 

**Fixed #841 - potentially unreliable method used to check for the presence of a remote selenium server.**


[b70fb6ef8b0ab41](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b70fb6ef8b0ab41) John Ferguson Smart *2017-07-17 20:43:26*


### No issue

**Minor refactoring**


[cb8e8cccb3ad904](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cb8e8cccb3ad904) John Ferguson Smart *2017-07-17 20:44:35*

**If an exception class is not known, use heuristics to determine whether it is an error or a failure**


[64deebc8158d255](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/64deebc8158d255) John Ferguson Smart *2017-07-17 16:06:44*


## v1.5.1-rc.4
### No issue

**Minor refactoring**


[c65af60b1876a97](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c65af60b1876a97) John Ferguson Smart *2017-07-17 11:39:51*


## v1.5.0-rc.4
### No issue

**Added an extra method for the maven plugin**


[0cc54441f6efb29](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0cc54441f6efb29) John Ferguson Smart *2017-07-16 22:20:55*


## v1.5.0-rc.3
### No issue

**Removed dependency conflicts**


[e0d5624014fe116](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0d5624014fe116) John Ferguson Smart *2017-07-16 20:07:19*


## v1.5.0-rc.2
### GitHub #672 

**Fixed #672 - Updated appium client to 5.0.0-BETA9**


[93a719f8764f309](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93a719f8764f309) John Ferguson Smart *2017-07-16 15:18:15*


### GitHub #678 

**Fixed #678 - there is now the option to sort test results by order of execution.**


[70b1d915ddaecb9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/70b1d915ddaecb9) John Ferguson Smart *2017-07-16 16:15:17*


### GitHub #832 

**Fixed #832 - properties are now logged to the console only as DEBUG messages, not as INFO messages.**


[1522b0bcb401aa3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1522b0bcb401aa3) John Ferguson Smart *2017-07-16 11:29:34*


### No issue

**Refactoring**


[04acfa6a835c3f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/04acfa6a835c3f8) John Ferguson Smart *2017-07-16 11:01:10*

**Refactored the remote driver code**


[68a917b5fe85eee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/68a917b5fe85eee) John Ferguson Smart *2017-07-16 09:38:13*

**Refactored the remote driver code**


[734ef99dc38f169](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/734ef99dc38f169) John Ferguson Smart *2017-07-16 09:14:30*

**Added the ability to specify Chrome options in the @Managed annotation**

 * e.g. 
 * ```@Managed(driver = &quot;chrome&quot;, options = &quot;--headless&quot;)``` 

[158fb16c9029d2b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/158fb16c9029d2b) John Ferguson Smart *2017-07-14 22:29:06*

**test refactoring**


[df3c2b05b020f1b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/df3c2b05b020f1b) John Ferguson Smart *2017-07-12 15:17:04*

**Reverted to using lambdaj in the experimental SpecFlow adaptor for now**


[60fa541c994efb2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60fa541c994efb2) John Smart *2017-06-26 10:23:54*

**Removed core dependency on lambdaj**


[21d44fa9cef559c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/21d44fa9cef559c) John Smart *2017-06-26 09:58:36*

**Updated to Java 8 and replaced most references to LambdaJ with Java 8 streams.**


[e655282e446bdb3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e655282e446bdb3) John Smart *2017-06-26 09:33:04*

**Minor updates**


[3a647ecf58ad9a2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3a647ecf58ad9a2) John Smart *2017-06-22 14:11:55*


## v1.5.0-rc.1
### GitHub #818 

**Fixed #818**


[e446e6d7f54d40c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e446e6d7f54d40c) John Smart *2017-06-07 12:50:30*


### No issue

**Report service refactoring**


[4c8f9e9a25273c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c8f9e9a25273c5) John Smart *2017-06-17 10:41:02*

**Updated the wrapper to Gradle 4.0**


[b9ac1d1573128ed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b9ac1d1573128ed) John Smart *2017-06-17 08:59:18*

**Fixed an issue with thread executors that caused jobs to hang under certain circumstances.**


[199a8ae26c614ca](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/199a8ae26c614ca) John Smart *2017-06-17 08:49:31*

**Error tags are now of type "error"**


[c5d45927da2d691](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c5d45927da2d691) John Smart *2017-06-14 18:38:29*


## v1.4.1-rc.6
### No issue

**Simplified and improved performance for the way page object fields are displayed in error messages.**


[ad8ef700a152ce3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad8ef700a152ce3) John Smart *2017-06-04 11:10:11*

**Minor refactoring**


[0a63951b0e3c688](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a63951b0e3c688) John Smart *2017-06-04 10:48:42*

**Reorganising the Screenplay web tests as integration tests.**


[08bcff767169b04](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08bcff767169b04) John Smart *2017-06-04 09:48:20*


## v1.4.1-rc.5
### GitHub #806 

**Fixed #806 - browser capability properties in quotes are now always passed as Strings, and not converted to numbers, e.g.**

 * browserstack.build = &quot;1&quot; 

[534092756c22127](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/534092756c22127) John Smart *2017-06-04 09:01:32*


## v1.4.1-rc.4
### GitHub #799 

**Fixed #799**


[c604d4cca57e5e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c604d4cca57e5e3) John Smart *2017-06-03 15:02:28*

**Fix for #799 - extra browser opening when the timeout attribute is used with the @FindBy annotation.**


[dd96cf4f885f413](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dd96cf4f885f413) John Smart *2017-06-03 12:23:51*


### GitHub #804 

**Fix for #804**


[51c643197cb60a5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/51c643197cb60a5) John Smart *2017-05-30 08:31:45*


### GitHub #806 

**Work-around for #806**


[29a45261ec93cfa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/29a45261ec93cfa) John Smart *2017-05-30 13:02:25*


### GitHub #807 

**Fix for #807 - markdown rendering is now disabled for steps by default.**


[e1e3b0a4fdd6cee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e1e3b0a4fdd6cee) John Smart *2017-05-30 17:41:15*


### GitHub #814 

**Fix for #814 - better support for rendering unusual new line characters in HTML.**


[d572d4104ca191f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d572d4104ca191f) John Smart *2017-06-03 07:51:55*


### No issue

**Fixed a broken test**


[beb15c0e844a87a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/beb15c0e844a87a) John Smart *2017-06-03 17:11:36*

**Fixed a broken test**


[00115f992da6b55](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/00115f992da6b55) John Smart *2017-06-03 15:02:47*

**Fixed a broken test**


[0bef88a013fe74f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0bef88a013fe74f) John Smart *2017-06-03 12:53:21*

**Refactoring to make the inflection logic more memory-efficient.**


[a94d972c6677a78](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a94d972c6677a78) John Smart *2017-06-03 10:43:53*

**Added the io.blocking.coefficient property - an (advanced) way of fine-tuning the number of reporting threads used during report generation.**


[791a313dd05a3ea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/791a313dd05a3ea) John Smart *2017-05-31 17:54:30*

**Fixed a bug in tag reporting.**


[31284c65950e751](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/31284c65950e751) John Smart *2017-05-30 11:13:28*

**Improved logging of loaded requirements tree.**


[c1936c273691bda](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c1936c273691bda) John Smart *2017-05-30 11:12:58*

**Minor refactoring.**


[63a67d7f50902c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/63a67d7f50902c6) John Smart *2017-05-30 08:32:03*


## v1.4.1-rc.3
### GitHub #751 

**fix: geckodriver is used from environment variable #751**


[244f682fc24e804](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/244f682fc24e804) George Ciltaru *2017-04-06 11:45:36*


### No issue

**Only shutdown fixture services if closing the last tab in a browser**


[7ed14296f4c1e50](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ed14296f4c1e50) Harry King *2017-05-25 16:50:51*

**Reove extra'.' for capability getPreparedPropertyKey**


[077dbc9174f64c7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/077dbc9174f64c7) Florin Strugariu *2017-05-25 11:45:36*

**Step multiple implementation annotation**


[0300805df73d77a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0300805df73d77a) Florin Strugariu *2017-05-25 10:47:57*

**fix for https://github.com/serenity-bdd/serenity-core/issues/747**


[d0ec3c15e8c1aa9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d0ec3c15e8c1aa9) liviu.carausu *2017-04-05 14:01:43*

**upgrade to serenity-rest 2.9.0 - byte-buddy performance improvements**


[b4d1c63411ab505](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b4d1c63411ab505) cliviu *2017-04-03 22:01:55*


## v1.4.1-rc.2
### No issue

**Removed the @Nullable annotation to reduce potential dependency conflicts**


[d061c566f6174f7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d061c566f6174f7) John Smart *2017-05-22 18:05:33*

**Better support for ChromeDriver options.**


[952fba347330484](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/952fba347330484) John Smart *2017-05-22 17:59:24*

**Use guava 20.0 to preserve Java 7 compatibility.**


[9b8a1cff790aa58](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b8a1cff790aa58) John Smart *2017-05-22 14:39:26*

**Fixed a bug where parameterized JUnit tests didn't honor tag filtering**


[4a9a30970e26060](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4a9a30970e26060) John Smart *2017-05-22 13:22:39*


## v1.4.1-rc.1
### GitHub #780 

**Fixed #780**


[df91eca9736ae26](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/df91eca9736ae26) John Smart *2017-05-18 11:10:41*


### GitHub #789 

**Fixed #789**


[a3e75736fe761e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a3e75736fe761e9) John Smart *2017-05-12 14:31:21*


### GitHub #793 

**Possible fix for #793**

 * Report encoding can be configured using the report.charset property. Defaults to UTF-8. May fixe issue #793. 

[78c2daa917ee3c3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/78c2daa917ee3c3) John Smart *2017-05-18 10:54:12*


### Jira UTF-8 

**Possible fix for #793**

 * Report encoding can be configured using the report.charset property. Defaults to UTF-8. May fixe issue #793. 

[78c2daa917ee3c3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/78c2daa917ee3c3) John Smart *2017-05-18 10:54:12*


### No issue

**Refactoring**


[af3ffb8926c83b3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af3ffb8926c83b3) John Smart *2017-05-12 14:31:37*

**Created Serenity equivalents for all non Deprecated Thucydides methods**


[50b5ec226904818](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/50b5ec226904818) Harry King *2017-05-11 14:53:16*


## v1.4.0
### No issue

**API updates for more flexible data tables**


[cbdb3de0a648eac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbdb3de0a648eac) John Smart *2017-05-05 18:45:07*


## v1.3.1-rc.1
### No issue

**Fixed a defect that caused reports to fail for some cucumber tables.**


[6e7e22dee562975](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6e7e22dee562975) John Smart *2017-05-05 17:14:43*

**More robust error handling for driver failures.**


[ccb08d25e500050](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ccb08d25e500050) John Smart *2017-05-05 17:14:19*

**Minor refactoring.**


[fc82ab23fde2584](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc82ab23fde2584) John Smart *2017-05-03 18:01:33*


## v1.3.0-rc.2
### GitHub #767 

**Fix for a memory leak (#767)**

 * Contribution by Richard Luko 

[c793bd23f9c362b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c793bd23f9c362b) John Smart *2017-05-02 07:45:52*


### GitHub #768 

**Manual merge of PR #768**

 * Set the “marionnette” capability to false by default in Firefox if Marionette is not being used. 

[c58a3f01610a2df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c58a3f01610a2df) John Smart *2017-04-27 05:50:23*


### GitHub #771 

**Fixed #771**


[7527749da2e7ba3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7527749da2e7ba3) John Smart *2017-04-27 05:41:22*


### GitHub #772 

**Fixed #772 - better integration with Strping test runners.**


[1b39ac4f256d5c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b39ac4f256d5c1) John Smart *2017-04-27 14:09:14*


### No issue

**Better reporting of nested tags.**


[bf7a5126cf135f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf7a5126cf135f4) John Smart *2017-05-02 23:48:43*

**Updated unit tests.**


[0c640613760ad6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0c640613760ad6e) John Smart *2017-05-02 16:56:04*

**Better support for running tagged tests from different test frameworks in the same test run.**


[0595f0e1149dffa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0595f0e1149dffa) John Smart *2017-05-02 16:12:11*

**Pass capabilities to PhantomJS by setting capability properties with the "phantomjs." prefix.**


[a7f00da649e50f1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a7f00da649e50f1) John Smart *2017-05-02 09:58:54*

**You can override the BrowserStack test name by setting the browserstack.name system property.**


[f7bf036b0ee3594](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7bf036b0ee3594) John Smart *2017-05-02 09:41:33*

**Set and clear the context programmatically**

 * You can now use Serenity.setContext() and Serenity.clearContext() to set the test context programmatically. 

[d7367f8f733ae6f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d7367f8f733ae6f) John Smart *2017-05-02 09:26:56*

**Added an icon for Phantomjs**


[547f2d702810ef5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/547f2d702810ef5) John Smart *2017-05-02 09:26:05*

**Add the ‘serenity.add.context.tag’ property**

 * This gives the option to display a tag for the current test context (default behaviour) or not (if you set this property to false). 

[ba3a911f45a78fb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba3a911f45a78fb) John Smart *2017-05-02 09:25:56*

**Test refactoring**


[f31e44c5d16b91a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f31e44c5d16b91a) John Smart *2017-05-01 21:34:08*

**FEAT: Added the ability to report on multiple runs of the same test**

 * You can now use the `context` system property to define a context for your tests. This can be combined with the `injected.tags` system property for more clarity. For example, the two following commands would run the same tests twice, with a different context each time: 
 * $ mvn verify -Dcontext=chrome -Dinjected.tags=&quot;browser:chrome&quot; 
 * $ mvn verify -Dcontext=firefox -Dinjected.tags=&quot;browser: firefox&quot; 
 * Browsers (chrome, ie, firefox…) and OS (linux, windows, android, iphone) are indicated with an icon. All other contexts are indicated in text form. 

[051eb360a3ad24e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/051eb360a3ad24e) John Smart *2017-05-01 21:21:39*

**Fixing another broken test**


[aff8ab1804a28d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aff8ab1804a28d9) John Smart *2017-04-27 19:48:37*

**Fixed a bug in the tests**


[e6e4e275d569a31](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6e4e275d569a31) John Smart *2017-04-27 19:25:17*

**Fixed a bug in the tests**


[7f5b1e040b7e266](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f5b1e040b7e266) John Smart *2017-04-27 19:03:12*

**Upgrated gradle**


[7097ab4d7d2f589](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7097ab4d7d2f589) John Smart *2017-04-27 05:50:32*


## v1.2.5-rc.23
### GitHub #767 

**Fixed a memory leak (#767)**


[aae1a9d1152ea88](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aae1a9d1152ea88) John Smart *2017-04-19 16:07:25*


### GitHub #769 

**Attempted fix for #769**


[0bcc00684369dfd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0bcc00684369dfd) John Smart *2017-04-20 17:23:16*


### No issue

**Minor refactoring**


[46892688ecdfbc8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/46892688ecdfbc8) John Smart *2017-04-20 16:11:49*


## v1.2.5-rc.6
### No issue

**Updated gradle plugin to handle test history reporting**


[b4d40fd6f6ac368](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b4d40fd6f6ac368) John Smart *2017-04-19 15:22:21*

**Updated Gradle wrapper version**


[b5df67efbd79bab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5df67efbd79bab) John Smart *2017-04-19 13:35:57*

**Bug fixes in the history reporting**


[42479f961bb5a64](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42479f961bb5a64) John Smart *2017-04-19 12:42:45*

**rest-assured byte-buddy performance improvements**


[cbbe6c9d2494c0b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbbe6c9d2494c0b) cliviu *2017-04-17 06:12:35*


## v1.2.5-rc.5
### No issue

**Fixed a performance issue with tag management**


[b53adb3f578c743](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b53adb3f578c743) John Smart *2017-04-15 14:09:42*


## v1.2.5-rc.4
### GitHub #741 

**Fix for #741**

 * Possible fix for concurrent access bug raised in #741 

[326b694ae2124ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/326b694ae2124ab) John Smart *2017-04-13 17:24:28*


## v1.2.5-rc.3
### GitHub #756 

**Fixed #756**

 * Report both Ignored and Skipped tests correctly in Cucumber. 

[9ac0863487576da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9ac0863487576da) John Smart *2017-04-11 20:14:39*


### No issue

**com.jayway.restassured 2.9.0 > io.restassured 3.0.2**


[871ef17e39ef1b5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/871ef17e39ef1b5) Vladimir Kopcil *2017-04-09 22:28:14*


## v1.2.5-rc.2
### No issue

**Test refactoring**


[3c1a4de75ff5cb9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3c1a4de75ff5cb9) John Smart *2017-04-08 20:28:10*

**Fixed some unit tests**


[3f4e8a7a175cb0b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f4e8a7a175cb0b) John Smart *2017-04-08 20:25:06*

**Add a full test title to scenario links**


[5bff74ac39585b2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5bff74ac39585b2) John Smart *2017-04-08 19:49:22*

**Added support for flagging new tests**

 * Set show.history.flag=true to allow new failures to be flagged in the reports. 

[006e7bfccfc20ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/006e7bfccfc20ef) John Smart *2017-04-08 19:48:37*

**Ensure that empty boolean properties correctly use default values.**


[fbe656f4156cb03](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fbe656f4156cb03) John Smart *2017-04-07 21:01:34*

**Simple support for checking for new test failures (WIP)**


[a7e7f5cdb154a6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a7e7f5cdb154a6b) John Smart *2017-04-07 17:14:24*

**Fixed a potential issue with Selenium 3.x**


[036a6dd573a5157](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/036a6dd573a5157) John Smart *2017-04-07 17:13:46*

**Fixed a reporting error in the percentage failure or error count.**


[729138fa94ea803](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/729138fa94ea803) John Smart *2017-04-07 17:12:26*

**Fixed a potential NullPointerException when using the uploader from Screenplay**


[c12ff6800e6c918](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c12ff6800e6c918) John Smart *2017-04-07 17:11:55*

**Refactored a test**


[fe71517e9c08105](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe71517e9c08105) John Smart *2017-04-07 17:11:10*

**Update to Guava 21.0**


[c0dc02f53223594](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c0dc02f53223594) John Smart *2017-04-07 17:10:29*

**upgrade to serenity-rest 2.9.0 - byte-buddy performance improvements - reuse constructors**


[ead357aec9f0929](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ead357aec9f0929) cliviu *2017-04-06 21:26:14*

**Work in progress**


[08c7a526b281a7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08c7a526b281a7c) John Smart *2017-04-02 19:17:13*

**WIP**


[9512a485c7bcf0c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9512a485c7bcf0c) John Smart *2017-04-02 19:13:15*


## v1.2.5-rc.1
### No issue

**Added the injected.tags property**

 * Use the injected.tags property to inject build-wide tags that will be applied to every test execution. 

[09ecbfd938f4aed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/09ecbfd938f4aed) John Smart *2017-03-29 18:01:22*

**Fixed a minor defect.**


[ee1ee2ecc4d3338](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ee1ee2ecc4d3338) John Smart *2017-03-29 13:33:32*

**Better support for trailing spaces in the driver names.**


[fca949af66d9a92](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fca949af66d9a92) John Smart *2017-03-29 13:33:16*


## v1.2.4-rc.1
### No issue

**Better support for markdown rendering in the step outlines**


[39102b60a3636d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39102b60a3636d6) John Smart *2017-03-26 07:00:53*

**upgrade to serenity-rest 2.9.0**


[778335812600c75](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/778335812600c75) cliviu *2017-03-24 22:01:16*


## v1.2.3-rc.10
### No issue

**Restored some code that was finally needed.**


[316dab301cd2ee5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/316dab301cd2ee5) John Smart *2017-03-24 12:36:16*

**Code simplification**


[01db45aa8f550f2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01db45aa8f550f2) John Smart *2017-03-24 12:12:40*

**Refactoring**


[dd6a654cac9726b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dd6a654cac9726b) John Smart *2017-03-24 11:52:51*

**Minor bug fix.**


[ace6d8da60c30a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ace6d8da60c30a9) John Smart *2017-03-24 11:28:05*

**Improved support for data-driven tests.**


[40597b137752e36](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/40597b137752e36) John Smart *2017-03-24 10:32:38*

**Refactoring the CSS stylesheet**


[5913764ae0d782d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5913764ae0d782d) John Smart *2017-03-24 10:32:14*

**Improved readability in report stacktraces.**


[f5707cff4acf500](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f5707cff4acf500) John Smart *2017-03-24 01:38:13*

**Fixed a reporting issue in tables**

 * The overall result was the last failure, not the most severe failure in the table. 

[9ceb2b20a57e1da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9ceb2b20a57e1da) John Smart *2017-03-24 01:18:12*


## v1.2.3-rc.9
### No issue

**Fixed a bug where tags where not correctly reported**

 * JUnit tags where not correctly reported when Cucumber scenarios were run before them in the same build. 

[9317d0b41fdcddc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9317d0b41fdcddc) John Smart *2017-03-22 13:27:05*

**test: Add question to query the selected country of the profile**


[f4cc23afe16205c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4cc23afe16205c) kilimandzsaro *2017-03-22 11:55:43*

**test: fix the danaCanSelectHerCountry testcase**


[6725775ae89ac82](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6725775ae89ac82) kilimandzsaro *2017-03-22 11:46:15*

**test: Add test for SelectFromOptions.byValue(code)**


[5e3884797411ce5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5e3884797411ce5) kilimandzsaro *2017-03-22 08:38:00*

**refactor: SelectFromOptions for more readability**


[b8b385a070b73f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b8b385a070b73f4) kilimandzsaro *2017-03-22 08:30:12*

**fix: SelectByValueFromTarget to select from value instead of the visible text**


[c252f0a1d05c50c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c252f0a1d05c50c) kilimandzsaro *2017-03-22 08:26:45*

**use byte-buddy to improve serenity rest-assured integration**


[a38cc8aa2d7fbba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a38cc8aa2d7fbba) liviu.carausu *2017-03-21 14:20:42*

**Fixed a test.**


[7cd93ec3dbf04ba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7cd93ec3dbf04ba) John Smart *2017-03-20 16:08:28*

**Added a missed class.**


[5017083af6f9564](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5017083af6f9564) John Smart *2017-03-20 15:02:42*

**Improved reporting on failing Screenplay questions.**


[23e31ee1616d8e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23e31ee1616d8e6) John Smart *2017-03-20 11:12:57*


## v1.2.3-rc.8
### No issue

**Fixed a unit test**


[d4d003ad9b5e6a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d4d003ad9b5e6a9) John Smart *2017-03-19 17:33:17*

**Fixed a bug in the Cucumber table results**

 * Table results were not correctly displayed when a nested error occured within the Screenplay steps. 

[64af9652cc14991](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/64af9652cc14991) John Smart *2017-03-19 17:26:26*

**More flexible Screenplay question support.**

 * This commit contains several useful new features for ScreenPlay questions and consequences: 
 * 1) You can now group Screenplay questions under a single step, by adding a label for the step like this 
 * ``` 
 * then(dana).should(&quot;{0} should see the correct prices”, 
 * seeThat(theItemPrice(), equalTo(100)), 
 * seeThat(theVAT(), equalTo(20)), 
 * seeThat(theTotalPrice(), equalTo(120)) 
 * ); 
 * ``` 
 * 2) You can also create groups of consequences and call them without a matcher in the `shouldSee()` method. For example, you could write the following: 
 * ``` 
 * then(dana).should(seeThat(thePriceIsCorrectlyDisplayed())); 
 * ``` 
 * The `thePriceIsCorrectlyDisplayed()` method returns an object of type Question&lt;Void&gt;, that groups several questions together: 
 * ``` 
 * public class DisplayedPrices implements Question&lt;Void&gt; { 
 * @Override 
 * public Void answeredBy(Actor actor) { 
 * actor.should( 
 * seeThat(&quot;the total price&quot;, ThePrice.total(), equalTo(100)), 
 * seeThat(&quot;the VAT&quot;, ThePrice.vat(), equalTo(20)), 
 * seeThat(&quot;the price with VAT&quot;, ThePrice.totalWithVAT(), equalTo(120)) 
 * ); 
 * return null; 
 * } 
 * ``` 

[32a4440a91135f2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/32a4440a91135f2) John Smart *2017-03-19 17:24:52*

**Fixed an error in the report templates**


[7cd65cff6141acf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7cd65cff6141acf) John Smart *2017-03-16 12:18:54*

**Fixed an error with the stack trace popups**

 * Stack trace popup didnt’ appear if there were more than one error on the page. 

[e431febae343d1a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e431febae343d1a) John Smart *2017-03-16 11:40:16*

**Refactoring**


[08fd41a853e3765](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08fd41a853e3765) John Smart *2017-03-16 07:53:33*

**Fixed an incorrect reporting of pending tests**

 * And some other minor bug fixes. 

[0b48fe90e1db866](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0b48fe90e1db866) John Smart *2017-03-15 22:35:36*

**Updated changelogs**


[f5b757240a3bf59](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f5b757240a3bf59) John Smart *2017-03-14 10:13:31*

**Updated changelogs**


[05574c4c3100ca9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/05574c4c3100ca9) John Smart *2017-03-14 10:11:33*


## v1.2.3-rc.7
### GitHub #346 

**Fixed #346**


[67c7733bdf97ce2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/67c7733bdf97ce2) John Smart *2017-03-10 15:10:35*


## v1.2.3-rc.6
### GitHub #578 

**Fixed #578**


[f927a5d4de331c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f927a5d4de331c1) John Smart *2017-03-10 04:05:12*

**Fixed example table formatting (see #578)**


[83695a7cf5634c4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/83695a7cf5634c4) John Smart *2017-03-08 14:46:04*


### GitHub #58 

**Fixed #58: any properties called saucelabs.* will be passed to the Saucelabs capability.**


[c8fe8da3c5dba26](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c8fe8da3c5dba26) John Smart *2017-03-07 10:44:28*


### GitHub #618 

**FIxed #618**


[c360a38a58cc376](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c360a38a58cc376) John Smart *2017-03-09 18:56:19*


### GitHub #675 

**Fixed #675**


[23157e7a0892fc8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23157e7a0892fc8) John Smart *2017-03-10 03:27:03*


### GitHub #684 

**Fixed #684**


[6101c306a7d2e9f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6101c306a7d2e9f) John Smart *2017-03-09 14:34:39*


### GitHub #710 

**Fixed #710**


[0ce66445231107d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ce66445231107d) John Smart *2017-03-09 19:46:15*


### GitHub #712 

**Possible fix for #712**


[09ec3d7bbfbf409](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/09ec3d7bbfbf409) John Smart *2017-03-08 18:34:06*


### No issue

**Display field place-holders in example titles.**


[c109c69faedc86f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c109c69faedc86f) John Smart *2017-03-09 19:34:47*

**Improved formatting for example tables.**


[20ff8f8b7bdd5dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/20ff8f8b7bdd5dc) John Smart *2017-03-09 14:36:04*

**Fixed an issue in closing browsers**

 * In some cases, Cucumber would close the browser after each feature even when `serenity.restart.browser.for.each` was set to NEVER. 

[c3060f8f67db804](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c3060f8f67db804) John Smart *2017-02-28 22:37:49*

**Run unit tests in parallel**


[77770a06ce5ce73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/77770a06ce5ce73) John Smart *2017-02-23 20:34:17*

**Removed redundant tests**


[6ef9f779975d0a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ef9f779975d0a9) John Smart *2017-02-23 20:12:44*

**Test updates**


[9c88692e2d7d36c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9c88692e2d7d36c) John Smart *2017-02-23 20:01:40*

**Updated a test**


[a6f792d0aec4608](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a6f792d0aec4608) John Smart *2017-02-23 19:56:24*

**Switched digests to SHA256**


[0f81a03ae3f9079](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0f81a03ae3f9079) John Smart *2017-02-23 19:52:00*

**Refactored the wiremock tests to be more robust**


[9a85cc233b041af](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9a85cc233b041af) John Smart *2017-02-23 19:51:43*


## v1.2.3-rc.5
### GitHub #671 

**Fixed #671**


[4e9ec85f9362358](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e9ec85f9362358) John Smart *2017-01-30 17:34:00*


### GitHub #683 

**Fixed #683**


[3492b7f8abf3155](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3492b7f8abf3155) John Smart *2017-02-19 12:44:12*


### GitHub #695 

**tests for ability to use SerenityRest from a plain JUnit runner, issue #695**


[3ee43534c6e2e4b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3ee43534c6e2e4b) Marcin Caban *2017-02-23 01:48:42*

**feat: Ability to use SerenityRest from a plain JUnit runner, issue #695**


[bd37c94d57142c8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bd37c94d57142c8) Marcin Caban *2017-02-20 00:12:26*


### No issue

**a better name for a test class**


[1b77fd21c7fc7a1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b77fd21c7fc7a1) Marcin Caban *2017-02-23 02:11:36*

**Tweaked circleci config**


[0861f5216211051](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0861f5216211051) John Smart *2017-02-21 21:55:30*

**Tidying up redundant files**


[b6e40e1ddfec91e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6e40e1ddfec91e) John Smart *2017-02-21 20:53:44*

**Updated changelog**


[53b39b5c7c22599](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/53b39b5c7c22599) John Smart *2017-02-21 20:45:22*

**Fixed circleci config**


[1a9df386135fbd2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a9df386135fbd2) John Smart *2017-02-21 18:29:44*

**Experimental CircleCI config**


[9342c317a392ab7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9342c317a392ab7) John Smart *2017-02-21 18:17:46*

**Experimental parallel jobs in CircleCI**


[9d051b2ab2c07d8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d051b2ab2c07d8) John Smart *2017-02-21 18:13:25*

**Fixed the CircleCI build config.**


[af14abd504ccc2b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af14abd504ccc2b) John Smart *2017-02-21 06:51:44*

**The serenity.use.unique.browser is deprecated**

 * Use serenity.restart.browser.for.each=scenario|story|feature|never instead 

[12ef12bb27895ae](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/12ef12bb27895ae) John Smart *2017-02-21 06:42:31*

**Better error reporting for invalid scenarios**

 * If a scenario has no name, a `AScenarioHasNoNameException` will be thrown. 

[ea140ff866aceda](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ea140ff866aceda) John Smart *2017-02-21 06:41:38*

**Fixed CircleCI config**


[4ed2edfe7559aaa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4ed2edfe7559aaa) John Smart *2017-02-19 23:26:22*

**Chore: Added a longer timeout for CI builds on circleci.**


[b51acb212284234](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b51acb212284234) John Smart *2017-02-19 22:20:17*

**change property name for rerunning cucumber tests to test.retry.count.cucumber**


[0fb960b6c6a8d24](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0fb960b6c6a8d24) cliviu *2017-02-19 22:04:33*

**Added a configuration file for CircleCI**


[cc930689b279b96](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cc930689b279b96) John Smart *2017-02-19 14:19:09*

**Fixed dependency conflict**


[20823294facb274](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/20823294facb274) John Smart *2017-02-19 13:45:53*

**record/replay failed tests using external file -refactoring - use separate file for each test class**


[68955f1ebedd777](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/68955f1ebedd777) cliviu *2017-02-15 22:33:31*

**record/replay failed tests using external file -refactoring**


[01e6add45a6b06d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01e6add45a6b06d) cliviu *2017-02-14 02:02:04*

**record/replay failed tests using external file**


[cca50292f4dfde3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cca50292f4dfde3) cliviu *2017-02-12 22:09:38*

**Add BrowserStack test name to compatibilites**


[8fc149c3eed69b5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fc149c3eed69b5) Strugariu *2017-02-08 20:36:12*

**FIxed a bug where failing tests where not reported**


[58c9ce49d2a7f0d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/58c9ce49d2a7f0d) John Smart *2017-02-04 15:24:29*

**Switch back to appium 4.0.0**


[507b3ffdc2f0241](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/507b3ffdc2f0241) John Smart *2017-02-04 05:31:35*

**https://github.com/serenity-bdd/serenity-core/issues/670**


[f95f2083bd8bb93](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f95f2083bd8bb93) liviu.carausu *2017-02-03 14:59:54*

**https://github.com/serenity-bdd/serenity-core/issues/670**


[8148fdd40e149d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8148fdd40e149d6) liviu.carausu *2017-02-03 12:54:39*

**feat: Added support for tags in the result checker plugin.**


[fc603dd2660e2c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc603dd2660e2c6) John Smart *2017-01-30 17:36:04*

**feat: Updated appium to 4.1.2**


[9ce1236bafd17bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9ce1236bafd17bb) John Smart *2017-01-30 17:35:41*

**Updated Guava dependency to 20.0**


[dab0a7b593bb565](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dab0a7b593bb565) John Smart *2017-01-27 02:18:30*

**Added support for tags in the result checker**

 * You can now provide a tags property to the Maven checker plugin, to limit the check to test outcomes with that tag, e.g. 
 * ``` 
 * mvn serenity:check -Dtags=scrum:panther 
 * ``` 

[60a55d2897fbe7a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60a55d2897fbe7a) John Smart *2017-01-20 11:51:36*


## v1.2.3-rc.4
### No issue

**Report optimisation**


[90b8e634d476fb5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/90b8e634d476fb5) John Smart *2017-01-18 15:15:43*


## v1.2.3-rc.2
### No issue

**Minor reporting performance improvements**


[39ed3686ba96e3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39ed3686ba96e3c) John Smart *2017-01-18 13:25:54*

**Minor performance improvements for tag reporting**


[b30eefd97f26957](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b30eefd97f26957) John Smart *2017-01-18 12:05:56*


## v1.2.3-rc.1
### GitHub #655 

**Fixed #655**

 * Use of &quot;@UseTestDataFrom&quot; results in &quot;IllegalArgumentException. 

[57b0c20ea14125f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/57b0c20ea14125f) John Smart *2017-01-16 22:50:17*


### No issue

**Updated smoketest dependencies**


[6da2c8717e5d303](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6da2c8717e5d303) John Smart *2017-01-18 00:04:48*

**Temporarily suspend a test pending further investigation.**


[f94c06d04cf11b1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f94c06d04cf11b1) John Smart *2017-01-17 23:48:12*

**Temporarily suspend a test pending further investigation.**


[d9d62cee1eb51f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9d62cee1eb51f9) John Smart *2017-01-17 23:43:14*

**Reporting performance enhancements**

 * Added the `report.timeout` property to allow the individual timeouts on report generations to be configured (to avoid hanging builds due to deadlocks in file resources, for example). 
 * Added the `verbose.reporting` property to display details about what reports are being generated. 

[f3ba174f7f3561a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3ba174f7f3561a) John Smart *2017-01-17 23:37:36*

**Don’t include the line number in the failure summary.**

 * This gives more flexibility for matching similar failures. 

[b98a9ccd0a48052](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b98a9ccd0a48052) John Smart *2017-01-12 17:34:40*

**Fixed an issue related to spaces in column names in parameterized tests**

 * In some cases, column names containing acronyms and leading spaces in parameterized tests caused the freemarker reports to crash. 

[fc31fa5337ed73d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc31fa5337ed73d) John Smart *2017-01-10 11:50:14*

**Minor refactoring**


[4d35a72d307e76f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4d35a72d307e76f) John Smart *2017-01-08 15:41:45*


## v1.2.2-rc.12
### No issue

**Removed redundant JSON field.**


[0c0b86d1ab2268e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0c0b86d1ab2268e) John Smart *2017-01-07 21:24:30*

**Added a project key to the test outcome format**

 * The projectKey field is set by the serenity.project.key system property and can be used to group test outcomes for the same project. 

[b657f83743c59e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b657f83743c59e9) John Smart *2017-01-07 21:12:56*

**Removed unnecessary field in the test outcome JSON**


[08d50c22ffa2ba7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08d50c22ffa2ba7) John Smart *2017-01-07 19:24:02*


## v1.2.2-rc.11
### No issue

**Added an error summary to the JSON output**


[dbdc482b3290b17](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dbdc482b3290b17) John Smart *2017-01-07 17:51:36*

**Improved support for excluding unrelated requirements**

 * You no longer need to set `serenity.exclude.unrelated.requirements` to true, just set the `serenity.exclude.unrelated.requirements.of.type` property to the requirement types that should be filtered. 

[4e177f82a32a543](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e177f82a32a543) John Smart *2017-01-07 15:07:38*


## v1.2.2-rc.10
### No issue

**Improved support for multi-module projects**

 * Serenity will now read properties from the serenity.properties file in the parent project of a multi-module project, if no properties are found in the child project. 

[d59b9f7eb73f96b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d59b9f7eb73f96b) John Smart *2017-01-07 11:00:05*

**feat: Improved handling of maven multi-module projects**


[bbf4f920b929344](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bbf4f920b929344) John Smart *2017-01-06 23:39:37*


## v1.2.2-rc.9
### No issue

**Fixed inconsistencies in test result reporting.**


[bbaae78e3951271](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bbaae78e3951271) John Smart *2017-01-05 20:01:16*


## v1.2.2-rc.8
### GitHub #635 

**Fixed #635**

 * Fixed #635 and other issues related to JUnit spawning new threads during test execution. 

[39f5bb74d3f1db8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39f5bb74d3f1db8) John Smart *2017-01-05 09:06:50*


### GitHub #644 

**Fixed #644**

 * Removed misleading and sometimes inaccurate test count console message. 

[aba5cba03bc6d25](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aba5cba03bc6d25) John Smart *2017-01-05 10:03:30*


### GitHub #646 

**Fixed #646 - more consistant reporting of ignored tests**

 * Also fixed an inconsistancy in reporting data-driven tests for dry-run tests. 

[40585f38819f06c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/40585f38819f06c) John Smart *2017-01-05 12:46:19*


### No issue

**Tidying up**


[2ab45e30cf0e283](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ab45e30cf0e283) John Smart *2017-01-05 08:33:54*

**Updated changelog**


[ef8aa0d686a7e3f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef8aa0d686a7e3f) John Smart *2017-01-04 21:42:49*


## v1.2.2-rc.7
### GitHub #640 

**Fixed #640 - Acronyms are poorly rendered in the reports**


[c58dc47e98d35f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c58dc47e98d35f9) John Smart *2017-01-04 14:59:59*


### GitHub #641 

**Fixed #641**


[8b0da614f123fad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b0da614f123fad) John Smart *2017-01-04 15:25:27*


### GitHub #642 

**Fixed #642**


[e9b78b2cb040b28](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e9b78b2cb040b28) John Smart *2017-01-04 16:04:48*


### No issue

**feat : Better support for acronyms in test titles.**


[f191573816c5b06](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f191573816c5b06) John Smart *2017-01-04 21:03:47*

**feat: Multiple performables in conditional performables**

 * You can now pass any number of performables to the andIfSo() and otherwise() methods of the conditional performables. 

[9bc8846e3164ed3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9bc8846e3164ed3) John Smart *2017-01-04 17:04:46*

**Updated changelog**


[5e4b6cec118ec08](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5e4b6cec118ec08) John Smart *2017-01-04 07:30:14*


## v1.2.2-rc.6
### GitHub #608 

**#608 @SpringBootTest support**

 * Support dependency injection for step libraries annotated with 
 * @SpringBootTest (introduced in spring-boot-test 1.4) 
 * To test this feature additional dependency on 
 * spring-boot-test and upgrade to Spring 4.3 is needed. 

[c7186e90a8ecf63](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c7186e90a8ecf63) Csaba Koncz *2016-11-24 08:57:08*


### GitHub #612 

**#612: Apply user properties to recommended default Internet Explorer capabilities, not the other way around**


[d5ebabc6b58238e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5ebabc6b58238e) Nick Barrett *2016-11-24 10:46:19*


### GitHub #618 

**Fixed #618**


[e43e0db312c0997](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e43e0db312c0997) John Smart *2017-01-03 19:09:59*


### GitHub #633 

**Fixed #633**


[13b08715a369996](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/13b08715a369996) John Smart *2017-01-03 17:36:43*


### No issue

**feat: reports include a summary.txt file**

 * Report aggregation now produces a summary.txt file that contains a brief overview of the test results. 

[f4b510ceee4461d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4b510ceee4461d) John Smart *2017-01-03 22:59:19*

**Updated a test**


[938ad9faa226289](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/938ad9faa226289) John Smart *2017-01-03 22:04:25*

**Updated spring and spring boot dependencies**


[fc4f96678de8197](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc4f96678de8197) John Smart *2017-01-03 19:35:56*

**Updated mockito and jbehave dependencies**


[0045c6c8ca58702](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0045c6c8ca58702) John Smart *2017-01-03 10:35:10*

**Updated commons-io**


[1ae03b7105eb3d3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1ae03b7105eb3d3) John Smart *2017-01-03 10:26:32*

**Added a cleaner way to check that a web test is running.**


[bfa8546d5bdb9b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bfa8546d5bdb9b9) John Smart *2017-01-03 10:26:00*

**Added a WIP test**


[152e7ab3c1f630f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/152e7ab3c1f630f) John Smart *2017-01-03 08:45:06*

**Refactoring**


[8584f0569e79c19](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8584f0569e79c19) John Smart *2017-01-02 18:12:34*

**Fixed regression in the @Managed annotation**

 * Fixed an issue where the uniqueSession property of the @Managed annotation in JUnit was not being honored. 

[b79cd35b6f9482a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b79cd35b6f9482a) John Smart *2017-01-02 18:12:22*

**Tidying up**


[4cd9db6414bad3d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4cd9db6414bad3d) John Smart *2017-01-02 09:28:11*

**Added the `serenity.browser.maximized’ property.**


[30264cefbfa56c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30264cefbfa56c1) John Smart *2017-01-02 09:28:03*

**Better configuring of compressed or readable report names.**


[e43f35351025a6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e43f35351025a6b) John Smart *2016-11-23 15:49:06*

**Enable better configuration of markdown**

 * Use the enable.markdown property to define where you want Markdown code to be rendered (you can put multiple values separated by commas): 
 * - story: in story titles 
 * - narrative: in story narrative texts 
 * - step: in step descriptions 

[6c44b5a77da9372](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6c44b5a77da9372) John Smart *2016-11-23 15:48:20*


## v1.2.2-rc.5
### No issue

**Allow the aggregate feature to regenerate test outcome reports**


[b09f0f46006d56a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b09f0f46006d56a) John Smart *2016-12-26 11:01:42*

**Fixed a number of issues with requirements reporting.**


[8bf7fbe3649e3ff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8bf7fbe3649e3ff) John Smart *2016-12-25 13:37:19*

**Improved markdown rendering**


[89f9c424ca54cf2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89f9c424ca54cf2) John Smart *2016-12-22 17:18:12*

**Improved reporting.**


[fdc98fb8ae78307](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fdc98fb8ae78307) John Smart *2016-12-22 15:37:34*


## v1.2.2-rc.4
### No issue

**Fixed an issue in rendering scenario outlines containing regex chars**


[5f063953da9a42d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f063953da9a42d) John Smart *2016-12-20 17:08:03*


## v1.2.2-rc.3
### No issue

**Add the tags parameter to filter aggregate reports**


[362193fefae7ddf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/362193fefae7ddf) John Smart *2016-12-20 11:53:49*

**Removed redundant error message**

 * Don’t throw an error message if a screenshot already exists. 

[2d7e01795d995bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d7e01795d995bb) John Smart *2016-12-20 07:31:20*


## v1.2.2-rc.1
### GitHub #631 

**Fix for #631**


[077bd6e86521045](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/077bd6e86521045) John Smart *2016-12-17 12:01:38*


### No issue

**Fixed a potential null-pointer exception**


[6b5c709de0cffed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6b5c709de0cffed) John Smart *2016-12-17 13:05:56*

**Unit test fixes**


[3a81fc4945724c7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3a81fc4945724c7) John Smart *2016-12-17 12:13:42*

**Bug fix**


[ea4e259042b84f5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ea4e259042b84f5) John Smart *2016-12-17 12:09:45*


## v1.2.1-rc.9
### No issue

**Fixed an error in Cucumber rendering**

 * Fixed an error where the name of an example table didn’t appear on the reports. 

[04f3ff18674893f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/04f3ff18674893f) John Smart *2016-12-16 15:48:23*

**Make it possible to disable cosole banners**

 * Set serenity.console.headings to quiet to not display any banners to console output. 

[77b6ac94095658f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/77b6ac94095658f) John Smart *2016-12-16 14:39:38*

**Made the parameter reporting in data driven tests more robust.**


[c23d159a769570d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c23d159a769570d) John Smart *2016-12-16 14:30:28*

**Improved parameter substitution for parameterised test reports**


[83c6bca2fbcd115](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/83c6bca2fbcd115) John Smart *2016-12-16 13:31:01*

**Better test outline reporting for parameterised tests**


[44eea7d60d1cf05](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/44eea7d60d1cf05) John Smart *2016-12-16 12:25:15*

**Fixed an error in the requirement breadcrumbs**


[e6c49b63c671f84](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6c49b63c671f84) John Smart *2016-12-16 09:46:30*

**Respect line breaks in @Narrative descriptions.**


[53f51d76336418b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/53f51d76336418b) John Smart *2016-12-16 08:01:29*


## v1.2.1-rc.8
### No issue

**Minor bug fix**


[c35497bd2ff95f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c35497bd2ff95f6) John Smart *2016-12-14 13:14:56*

**Fixed an issue with broken links in the breadcrumbs**


[803bfe796b2a01b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/803bfe796b2a01b) John Smart *2016-12-14 10:19:25*

**Improved requirement merging between package and file-based requirement structures.**


[8b18f4c7dae3631](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b18f4c7dae3631) John Smart *2016-12-14 09:55:13*

**Added a property for compressing report file names.**


[e2d75db73248f6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e2d75db73248f6e) John Smart *2016-12-14 09:54:51*


## v1.2.1-rc.7
### GitHub #624 

**Fixed #624**


[95980f3a49ee8d7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/95980f3a49ee8d7) John Smart *2016-12-13 14:57:17*


### GitHub #626 

**Fixed #626**


[23fdacef00bb907](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23fdacef00bb907) John Smart *2016-12-13 16:36:45*


### No issue

**Tidying up.**


[a5a29f787e9a31f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a5a29f787e9a31f) John Smart *2016-12-13 17:47:02*

**Focus requirements reports on requirements related to the executed tests**

 * You can now use the `serenity.exclude.unrelated.requirements.of.type` to only include requirements related to the features or capabilities that are in progress. This makes the report generation faster and the reports more relevant. For example, to exclude capabilities with no executed tests (but to include neighboring features with no executed tests) you could set this property as follows: 
 * ``` 
 * serenity.exclude.unrelated.requirements.of.type=capability 
 * ``` 
 * The default value is “capabilty, feature”, so both capabilities and features without any tests (including pending tests) will not appear in the reports. 
 * To include all requirements at all levels, you can configure this property as follows: 
 * ``` 
 * serenity.exclude.unrelated.requirements.of.type=none 
 * ``` 

[aacf30984bd6d7a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aacf30984bd6d7a) John Smart *2016-12-13 13:49:07*

**Focused reporting**

 * Reports can now be limited to the requirements related to the tests exectued. 

[394a87298c61132](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/394a87298c61132) John Smart *2016-12-11 17:38:08*

**Fixed an issue with incorrect requirement listings.**


[c3fd8deded86709](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c3fd8deded86709) John Smart *2016-12-10 12:33:34*

**Improved reporting performance.**


[8f5d7013c882728](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8f5d7013c882728) John Smart *2016-12-10 10:51:41*

**Run requirement type reports in parallel.**


[ee7b28c2ab79b23](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ee7b28c2ab79b23) John Smart *2016-12-09 17:41:16*

**Refactoring report execution mechanism**


[4903da8a71c54fe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4903da8a71c54fe) John Smart *2016-12-09 16:02:09*

**Default requirements reports are now always generated.**

 * If requirements are not configured for a test, default requirements reports will be generated based on the test tags. 

[b2220197cc951f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b2220197cc951f8) John Smart *2016-12-09 07:25:46*

**Fixing Javadoc warnings**


[f2068e0a1a1b867](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f2068e0a1a1b867) John Smart *2016-12-09 07:24:37*

**More efficient tag report generation**


[1504f766136ed23](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1504f766136ed23) John Smart *2016-12-08 07:55:45*

**Minor refactoring**


[4f1d3ed72d80248](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f1d3ed72d80248) John Smart *2016-12-08 07:55:26*

**Ensure that page source code is only generated if available.**


[e575c6a4da1f6c8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e575c6a4da1f6c8) John Smart *2016-12-07 22:31:34*

**Allow easy configuration of environment variables for testing**

 * Using the ConfiguredEnvironment class as a central point of contact for environment variables. 

[9e393fab6abd995](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e393fab6abd995) John Smart *2016-12-07 22:10:10*

**Added some diagnostics**


[fdecc2ae6d076e2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fdecc2ae6d076e2) John Smart *2016-12-07 21:54:18*

**Improvements to reporting performance**

 * Now using a buffered writer and streams to merge the Freemarker templated. 

[d6c5c7d036724f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d6c5c7d036724f6) John Smart *2016-12-07 21:54:09*

**Ignore files not intended for github**


[59407d05c7a2839](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/59407d05c7a2839) John Smart *2016-12-07 21:34:47*

**Fixed an issue with some tests failing when generating uncompressed report names**


[e30ae07444911b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e30ae07444911b6) John Smart *2016-12-07 21:31:19*

**Restored legacy support for old saucelabs configs**


[dd8aaa3be031dcf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dd8aaa3be031dcf) John Smart *2016-12-02 08:48:26*

**Fixed an issue where passing a null parameter to instrumented classes.**


[a698abe0c2f7f33](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a698abe0c2f7f33) John Smart *2016-12-01 17:23:13*

**Better error reporting for dodgy page objects.**


[7f1e8983ac9b4c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f1e8983ac9b4c5) John Smart *2016-12-01 15:38:07*

**General refactoring**


[35dadf7b8cc69b8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/35dadf7b8cc69b8) John Smart *2016-12-01 14:58:37*

**Updated sample reports with more realistic data**


[4adf6f76463ef07](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4adf6f76463ef07) John Smart *2016-11-28 08:27:37*


## v1.2.1-rc.6
### No issue

**Record the history of retried tests.**


[6863da9746c398e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6863da9746c398e) John Smart *2016-11-22 08:03:48*

**Test refactoring**


[c9881885ce7b0d8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9881885ce7b0d8) John Smart *2016-11-21 23:29:38*

**Removed unnecessary logging**


[a8c239fd11a8523](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8c239fd11a8523) John Smart *2016-11-21 22:03:43*

**Don’t use markdown formatting for example tables.**


[5f53b4d58e0a5cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f53b4d58e0a5cd) John Smart *2016-11-21 22:03:35*

**Improved handling of retried test**

 * You can specify the maximum number of retries using the `test.retry.count` property. If a test fails, Serenity will retry up to `test.retry.count` times. If the test eventually passes, it will be tagged with a ‘unstable test’ tag. 

[275252104ad4ad2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/275252104ad4ad2) John Smart *2016-11-21 22:03:18*


## v1.2.1-rc.5
### GitHub #573 

**Manually merged in pull request #573**


[8584fc4ed6f7483](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8584fc4ed6f7483) John Smart *2016-11-16 12:32:39*


### No issue

**Add better cross-platform support for tags**

 * You can now use the alternative “color=red” notation instead of “color:red” for tags, which makes it possible to provide command-line tag options for Cucumber. 
 * The “~” symbol can now be used as a “not” operator for tags, e.g. “~color:red”. 

[e0350359af5f4f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0350359af5f4f6) John Smart *2016-11-19 15:58:42*

**rerun failed tests :"mvn verify -Dtest.retry.count=..."**


[f4e14702be77537](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4e14702be77537) cliviu *2016-11-18 22:57:00*

**Test refactoring**


[785c96718871f7e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/785c96718871f7e) John Smart *2016-11-18 17:31:48*

**Test refactoring**


[f0706e4d8467c72](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f0706e4d8467c72) John Smart *2016-11-18 17:00:31*

**Fixed issue where manual tests were not recoginsed in JUnit parameterised tests.**


[08186bab0a6000f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08186bab0a6000f) John Smart *2016-11-18 14:17:14*

**Test refactoring**


[92c7cdfa8f928de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/92c7cdfa8f928de) John Smart *2016-11-17 13:15:54*

**Improved reporting for parameterized tests.**


[9f313c4c6b08d16](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f313c4c6b08d16) John Smart *2016-11-17 08:56:45*

**Test refactoring**


[9b3050cfac50ced](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b3050cfac50ced) John Smart *2016-11-16 22:54:50*

**Improved reporting for parameterized tests.**


[65669b28e26fef8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/65669b28e26fef8) John Smart *2016-11-16 22:52:36*

**Test refactoring**


[9fe105059796eb4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9fe105059796eb4) John Smart *2016-11-16 16:13:25*

**Test refactoring**


[848db5015b12aa5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/848db5015b12aa5) John Smart *2016-11-16 15:03:15*


## v1.2.1-rc.4
### No issue

**Test refactoring**


[18683a26508c4e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/18683a26508c4e3) John Smart *2016-11-16 09:00:33*

**Test refactoring**


[5cacf5e6f0c5f51](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5cacf5e6f0c5f51) John Smart *2016-11-16 08:53:27*

**Updated unit tests**


[7198c380c59be2b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7198c380c59be2b) John Smart *2016-11-15 23:07:27*

**Updated unit tests**


[bd34d157df9ab97](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bd34d157df9ab97) John Smart *2016-11-15 22:34:21*

**Updated unit tests**


[c136ac924a1e129](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c136ac924a1e129) John Smart *2016-11-15 22:19:46*

**Test refactoring**


[d174c51a013e3df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d174c51a013e3df) John Smart *2016-11-15 21:37:43*

**Allow markdown format in parent story titles**


[9bd29e9ed3a5308](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9bd29e9ed3a5308) John Smart *2016-11-15 21:37:31*

**Don’t produce XML reports by default**

 * Now only HTML and JSON reports are generated by default. 
 * If you need XML reports, set `output.formats` to “json,xml,html” 

[cba1b1ff82d9a7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cba1b1ff82d9a7c) John Smart *2016-11-15 21:37:12*

**Reinstated SNAPSHOT support for the release process**


[497b9f03fc7e19b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/497b9f03fc7e19b) John Smart *2016-11-15 10:38:58*


## v1.2.1-rc.3
### No issue

**Support for more flexible requirements structures**

 * Requirements packages can now have a mixture of levels, e.g. 
 * ``` 
 * +features 
 * + grain                           -&gt; capability 
 * + wheat                      -&gt; feature 
 * + organic               -&gt; feature 
 * + GrowOrganic  -&gt; story 
 * + fruit                             -&gt; capability 
 * + apples                     -&gt; feature 
 * GrowApples           -&gt; story 
 * + veges                          -&gt; capability 
 * GrowPotatoes       -&gt; story 
 * + GeneralFarming          -&gt; story 
 * ``` 

[5fa627a7e58313a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5fa627a7e58313a) John Smart *2016-11-15 08:00:40*

**Provide better support for uneven requirements package structures.**


[c782c60cbf9756a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c782c60cbf9756a) John Smart *2016-11-14 22:50:31*


## v1.2.1-rc.2
### No issue

**Removed jquery field highlighting**


[4f1a9a5b48ac0d5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f1a9a5b48ac0d5) John Smart *2016-11-14 10:31:53*

**Fixed minor issue with tests**


[e550ae80fb20146](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e550ae80fb20146) John Smart *2016-11-14 08:09:23*

**Allow better control of browser sessions when using multiple actors**

 * You can now use the `serenity.different.browsers.for.each.actor` (set to true by default) to make multiple actors use the same browser, even in the same test. This can be useful if actors are used to illustrate the intent of a test, but no tests use more than one actor simultaneously. 

[03c7e2e4254e7dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/03c7e2e4254e7dc) John Smart *2016-11-14 07:58:05*


## v1.2.1-rc.1
### No issue

**Fixed an issue with webdriver persistance**

 * WebDriver proxies can now be kept and reused after being closed. 

[55ef8e18e307bd7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/55ef8e18e307bd7) John Smart *2016-11-11 19:07:07*

**Test refactoring**


[d95763073fd5c7b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d95763073fd5c7b) John Smart *2016-11-11 10:38:13*

**Fixed the release plugin**


[caadcf86fded879](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/caadcf86fded879) John Smart *2016-11-11 10:01:52*

**WIP**


[fe0549b659aa47c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe0549b659aa47c) John Smart *2016-11-11 09:59:45*

**Fixed a layout issue in the scenario descriptions**


[acc3563c03a7221](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/acc3563c03a7221) John Smart *2016-11-11 09:54:08*

**Updated release config**


[5da95275d03d4ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5da95275d03d4ab) John Smart *2016-11-11 08:21:08*


## v1.2.0
### GitHub #586 

**Applied fix for #586 to other parts of the report.**


[d738746e948cc7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d738746e948cc7c) John Smart *2016-11-04 19:45:40*


### No issue

**Ensure that all the browsers are eventually closed.**


[e6dbaf0ebd0bdfb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6dbaf0ebd0bdfb) John Smart *2016-11-11 07:46:26*

**Fixed an issue with Cucumber requirements reporting.**


[829f1ce4aaf4d48](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/829f1ce4aaf4d48) John Smart *2016-11-11 07:44:10*

**Added an experimental WaitUntil interaction class.**


[4cecdf327857ac0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4cecdf327857ac0) John Smart *2016-11-11 07:43:06*

**Ensure that all browsers are closed after the tests**


[03233a7c507e63e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/03233a7c507e63e) John Smart *2016-11-11 07:42:35*


## v1.1.44
### No issue

**Updated gradle-git plugin**


[0032fce8a42e590](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0032fce8a42e590) John Smart *2016-11-04 14:11:50*


## v1.2.0-rc.3
### No issue

**Removed redundant test**


[b5d1febe8c47894](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5d1febe8c47894) John Smart *2016-11-04 09:11:30*

**Use the parent package or folder to avoid issues with multiple features or stories with the same name.**


[ab45a1fb7af0eb7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ab45a1fb7af0eb7) John Smart *2016-11-04 08:47:00*

**Updated Cucumber dependencies to 1.2.5**


[11b268cbb79fe39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/11b268cbb79fe39) John Smart *2016-11-04 08:43:28*


## v1.2.0-rc.2
### GitHub #545 

**Added support for markdown in reports (#545)**

 * You can now include **markdown** in test and step titles and descriptions, including feature and story files. The implementation uses https://code.google.com/archive/p/markdown4j/, and should support the extensions described on this site. 

[4759838206264ba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4759838206264ba) John Smart *2016-11-03 12:01:11*


### GitHub #570 

**Fixed #570**


[aa12f0a0508d4f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aa12f0a0508d4f4) John Smart *2016-10-31 07:15:51*

**Fixed #570**


[0629976bbd9afa2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0629976bbd9afa2) John Smart *2016-10-31 06:59:23*


### GitHub #579 

**Fixed #579: WebElementFacade.waitUntilNotVisible() throws NoSuchElementException**


[792bc3df97b71a4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/792bc3df97b71a4) John Smart *2016-11-03 21:54:40*


### GitHub #582 

**Fixed #582: correct handling of DryRun mode for Screenplay tests**


[3336427bcea5250](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3336427bcea5250) John Smart *2016-11-02 11:44:35*


### GitHub #586 

**Fixed #586 (Issue links appear twice in report)**


[4ff84774bd937dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4ff84774bd937dc) John Smart *2016-11-03 21:23:11*


### GitHub #589 

**#589: Reinstate retry of IE Browser instance creation on NoSuchSessionException**


[762ce04e5508318](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/762ce04e5508318) John Smart *2016-11-01 13:35:22*


### GitHub #590 

**fix: (experimental) fix for #590**

 * Non-unique class names in test structure causes incorrect report aggregation. 

[29a9ef7507f6e7f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/29a9ef7507f6e7f) John Smart *2016-11-02 23:05:40*


### No issue

**Test refactoring**


[9b5a603552e5604](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b5a603552e5604) John Smart *2016-11-03 21:23:35*

**Adding markdown support**


[23b5abd1af9dfdd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23b5abd1af9dfdd) John Smart *2016-11-03 15:57:52*

**fix: avoid creation of multiple driver services.**


[ead9940e3bb361b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ead9940e3bb361b) John Smart *2016-11-02 08:59:14*

**Improved Saucelabs support**

 * All capabilites defined in saucelabs.* system properties are now passed to Saucelabs. 

[7609b4476fc978e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7609b4476fc978e) John Smart *2016-11-01 13:37:26*

**Minor refactoring**


[0eaeb3a85569c27](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0eaeb3a85569c27) John Smart *2016-11-01 13:36:41*


## v1.2.0-rc.1
### GitHub #551 

**Issue #551: added support for deselecting options**


[a91ec2258fd1e64](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a91ec2258fd1e64) Nick Barrett *2016-10-05 13:29:12*


### GitHub #571 

**Fixed #571: ResponseBody automatically printed**


[ef8369f138b3397](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef8369f138b3397) John Smart *2016-10-17 07:30:43*


### No issue

**Made the smoke tests more robust**


[c9d67270af57638](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9d67270af57638) John Smart *2016-10-30 22:18:21*

**Ignore IDEA project files for the smoketest**


[b0e057b21e17907](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b0e057b21e17907) John Smart *2016-10-30 18:00:04*

**Minor refactoring**


[2f50f8617a3a565](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2f50f8617a3a565) John Smart *2016-10-30 09:17:41*

**Improved support for BrowserStack capability configuration.**


[1b961043770083f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b961043770083f) John Smart *2016-10-29 22:16:35*

**docs: Untangled an unusual sentence structure in the README file.**

 * Converted a Yoda-ish sentence about the Contributing document into 
 * something clearer. 

[e419576baf6117e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e419576baf6117e) BurkHufnagel *2016-10-29 19:21:24*

**Fixed some issues when no gecko dirver was present.**


[0a8bf27bc0e5ca1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a8bf27bc0e5ca1) John Smart *2016-10-28 22:37:29*

**Fixed an error occuring when no gecko driver is present on the file system.**


[9b0561df04f0d3d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b0561df04f0d3d) John Smart *2016-10-28 21:28:51*

**Integrated better support for Edge**


[0cc86e67f88e8c0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0cc86e67f88e8c0) John Smart *2016-10-28 18:58:32*

**Removed unnecessary code**


[5da0b7f7a4dcb6a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5da0b7f7a4dcb6a) John Smart *2016-10-28 07:09:27*

**Backed out WebDriverManager integration**

 * We have slated an internal implementation at some point in the future. 

[17e33f0a78f7b05](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/17e33f0a78f7b05) John Smart *2016-10-28 06:58:34*

**Added sample phantomjs binary**


[9a654a517dd9561](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9a654a517dd9561) John Smart *2016-10-27 17:12:01*

**Serenity now downloads the webdriver binaries automatically if not present**

 * Added integration with WebDriverManager (https://github.com/bonigarcia/webdrivermanager), which allows the latest WebDriver binaries to be downloaded automatically if they are not present on the machine. You can disable this behaviour using the `automatic.driver.download` property. 

[aec742df768dbf1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aec742df768dbf1) John Smart *2016-10-27 16:57:48*

**Minor refactoring**


[1de7a5ce5e2bb9a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1de7a5ce5e2bb9a) John Smart *2016-10-27 15:29:55*

**Added better support for web driver services.**


[79fc45b9da5c558](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/79fc45b9da5c558) John Smart *2016-10-27 15:29:30*

**Fixed a sporatic issue with shutting down ScreenPlay tests.**


[19f5eac2721b87e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/19f5eac2721b87e) John Smart *2016-10-27 15:28:15*

**Added some negative web state matchers**


[49ef71ece5b9e73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/49ef71ece5b9e73) John Smart *2016-10-27 15:27:40*

**Improved driver management to use driver services where possible.**


[e3bfa62afd5c4bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e3bfa62afd5c4bb) John Smart *2016-10-27 15:26:54*

**Experimental refactoring of the ChromeDriver**


[e9c302a54a77dd3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e9c302a54a77dd3) John Smart *2016-10-21 15:39:17*

**Updated Firebug plugin**


[7c10444c962b992](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7c10444c962b992) John Smart *2016-10-21 15:38:49*

**Refactoring JQuery injection logic.**


[e7f5275c6c82563](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7f5275c6c82563) John Smart *2016-10-21 13:57:20*

**chore: test refactoring**


[e69b719624938c4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e69b719624938c4) John Smart *2016-10-21 13:46:13*

**Improved error reporting for timeout error messages.**


[1d0b9071f447559](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1d0b9071f447559) John Smart *2016-10-21 10:30:41*

**Improvement in reporting stability in Windows**

 * When generating the reports on Windows, report files sometimes get blocked by other processes, which prevents them from being copied. Now, if this happens, Serenity will retry 3 times before failing. 

[2dcf495520589e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2dcf495520589e3) John Smart *2016-10-18 12:16:43*

**feat: Use WebDriver state matchers directly with WebElementFacade fields**

 * For example: 
 * ``` 
 * assertThat(profilePage.nameField, isVisible()); 
 * ``` 

[9d3e79dbc15df1a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d3e79dbc15df1a) John Smart *2016-10-06 13:50:11*


## v1.1.42-rc.1
### GitHub #536 

**Issue #536: Support for webdriver.chrome.binary system property**


[3f45bf2c52158d7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f45bf2c52158d7) 43999583 *2016-09-20 11:31:19*


### GitHub #542 

**Issue #542: ConsequenceMatchers: Better mismatch and Unknown field messages**


[d24adc67d461f43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d24adc67d461f43) 43999583 *2016-09-22 10:03:46*


### GitHub #547 

**Fixed #547: incorrect aggregation of data-driven tests containing failures**


[b5bc0aa79eb9518](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5bc0aa79eb9518) John Smart *2016-10-01 09:19:06*


### GitHub #550 

**Fixed: Possible fix for #550**


[a7b8eb08c8108df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a7b8eb08c8108df) John Smart *2016-09-26 08:43:26*


### GitHub #553 

**Fixed #553: Messages were being truncated too much.**


[ab4798095d8e506](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ab4798095d8e506) John Smart *2016-09-30 13:51:33*


### No issue

**added FeatureStoryTagProvider in JUnitTagProviderStrategy - fix JUnit test**


[ef16c27dd9f42c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef16c27dd9f42c6) cliviu *2016-09-30 21:09:23*

**Added support for clearing cookies between scenarios**


[85b9421e534b303](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/85b9421e534b303) John Smart *2016-09-30 18:08:49*

**Attempting to make the browser capability reporting more accurate**


[541dafdeb9af3e3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/541dafdeb9af3e3) John Smart *2016-09-30 13:55:21*

**Added an extra test about actors remembering values.**


[dc96fa1a00e7a8b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dc96fa1a00e7a8b) John Smart *2016-09-30 13:54:53*

**Made the code more resistant to legacy tag provider strategies.**

 * The code should not crash if an old tag provider is used (e.g. an older version of Cucumber), but should revert to the old tag priorities. 

[a5d5a6e1afeb717](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a5d5a6e1afeb717) John Smart *2016-09-30 13:54:33*

**added FeatureStoryTagProvider in JUnitTagProviderStrategy and remove additional tags that were added in TestOutcome**


[888da6af13fb147](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/888da6af13fb147) cliviu *2016-09-29 22:25:18*

**Fixed broken tests**


[93147a1fc4b0732](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93147a1fc4b0732) John Smart *2016-09-29 16:57:30*

**Test refactoring**


[c4a5879f301449c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c4a5879f301449c) John Smart *2016-09-29 16:36:03*

**Refactored Chrome and Edge to use a driver service**


[ad2456425e15beb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad2456425e15beb) John Smart *2016-09-29 16:23:47*

**Use a ChromeDriverService to improve the performance of Chrome tests**


[3392903d55b7109](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3392903d55b7109) John Smart *2016-09-29 14:19:18*

**Use a ChromeDriverService to improve the performance of Chrome tests**


[fe8d3741bc832f5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe8d3741bc832f5) John Smart *2016-09-29 14:12:00*

**Refactored the web tests**


[e591fe3bdd4848b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e591fe3bdd4848b) John Smart *2016-09-29 10:43:42*

**Refined the screenshot tests**


[24dbcf364f4ce60](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/24dbcf364f4ce60) John Smart *2016-09-29 09:46:55*

**Better support for overriding test results.**


[c59ee9058195328](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c59ee9058195328) John Smart *2016-09-29 09:46:27*

**Use the ChromeService for the tests**


[bcb65a50ea3cdd1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bcb65a50ea3cdd1) John Smart *2016-09-28 14:56:06*

**https://github.com/serenity-bdd/serenity-core/issues/550 include resources for report**


[97625f359d317dd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/97625f359d317dd) liviu.carausu *2016-09-28 09:35:47*

**Test refactoring**


[da4b87022e74477](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/da4b87022e74477) John Smart *2016-09-27 15:54:46*

**Optimised the timeout test**


[2342b7b4aa01d54](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2342b7b4aa01d54) John Smart *2016-09-27 15:46:53*

**Optimised the timeout test**


[676f41d38b13f7d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/676f41d38b13f7d) John Smart *2016-09-27 14:38:16*

**Requirements were incorrectly read from the package structure in some cases**


[f75fcdd22c26bc1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f75fcdd22c26bc1) John Smart *2016-09-27 10:38:41*

**Requirements were incorrectly read from the package structure in some cases**


[ac10988d53f05b4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ac10988d53f05b4) John Smart *2016-09-27 10:26:33*

**Requirements were incorrectly read from the package structure in some cases**


[eb6d32aa412cfc0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eb6d32aa412cfc0) John Smart *2016-09-27 10:17:22*

**Requirements were incorrectly read from the package structure in some cases**


[fbdacbf6357c720](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fbdacbf6357c720) John Smart *2016-09-27 10:16:09*

**Requirements were incorrectly read from the package structure in some cases**


[b1c6685c2381a71](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b1c6685c2381a71) John Smart *2016-09-27 10:09:38*

**Test Refactoring**


[e0c844346611631](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0c844346611631) John Smart *2016-09-26 20:28:51*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[5cbe17aeaae8654](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5cbe17aeaae8654) John Smart *2016-09-26 20:19:22*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[9f8cd3b6538f46b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f8cd3b6538f46b) John Smart *2016-09-26 20:13:56*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[697080a623bfe4c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/697080a623bfe4c) John Smart *2016-09-26 20:09:33*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[6ea614b2db91426](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ea614b2db91426) John Smart *2016-09-26 20:01:44*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[86e9a01532e936c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/86e9a01532e936c) John Smart *2016-09-26 19:54:28*

**Added logging for troubleshooting a unit test issue on Ubuntu**


[2ac412bdd923aeb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ac412bdd923aeb) John Smart *2016-09-26 19:06:24*

**Refactored a test to be more reliable on different JVMs**


[9e6ab0ad8bf8597](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e6ab0ad8bf8597) John Smart *2016-09-26 18:42:15*

**Refactored a test to be more reliable on different JVMs**


[1f66badbbb1d12e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1f66badbbb1d12e) John Smart *2016-09-26 18:34:15*

**Refactored a test to be more reliable on different JVMs**


[e0435fe9581413c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0435fe9581413c) John Smart *2016-09-26 18:27:36*

**Refactored screenshot-related tests to simplify test maintenance**


[cfe7608bf0a8600](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cfe7608bf0a8600) John Smart *2016-09-26 17:33:15*

**added hasHighPriority() in TagProviderStrategy**


[5a93bfaf44f3097](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5a93bfaf44f3097) cliviu *2016-09-22 22:31:22*

**partial work for fixing windows build failure.**


[fa663cd1d708f01](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fa663cd1d708f01) hantsy *2016-09-15 14:00:43*


## v1.1.41-rc.1
### No issue

**feat: Improved support for Internet Explorer**


[a1b7f83b7b015d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a1b7f83b7b015d9) John Smart *2016-09-20 16:06:59*

**fix: Fixed an issue creating the IE driver.**


[0b82d0e723c2bcb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0b82d0e723c2bcb) John Smart *2016-09-20 16:01:53*

**Removed unnecessary logging**


[d1b5469d6300561](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d1b5469d6300561) John Smart *2016-09-20 16:01:33*

**Support for webdriver.ie.driver system property**


[bb4f60b14f1d2dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bb4f60b14f1d2dc) 43999583 *2016-09-20 15:06:21*


## v1.1.40-rc.1
### No issue

**Added support for comparators in aggregate questions**


[fca1739982754f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fca1739982754f6) John Smart *2016-09-17 10:00:12*

**refactoring: Fine-tuned the AggregateQuestions API**


[1ee69c63242733a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1ee69c63242733a) John Smart *2016-09-17 09:15:43*

**feat: Added Aggregate features**

 * Special questions that take that work with collection-based questions to answer aggregate questions such as total, max,  min and sum. 
 * ``` 
 * AggregateQuestions.theTotalNumberOf(whatColours) 
 * ``` 

[95cbabb52d19fe1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/95cbabb52d19fe1) John Smart *2016-09-16 20:33:06*


## v1.1.39-rc.2
### No issue

**Fix: issue with too many browsers opening.**

 * In some older-style JUnit tests, two browsers were opening where only one should have opened. 

[1997e9271138866](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1997e9271138866) John Smart *2016-09-14 15:51:27*


## v1.1.39-rc.1
### No issue

**Refactored the requirements tests to make them more robust**


[f7f782baaa74abb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7f782baaa74abb) John Smart *2016-09-14 13:04:02*

**Renamed SelectOptions interaction for more consistancy**


[0d11a8e94eb4f1c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0d11a8e94eb4f1c) John Smart *2016-09-14 11:25:32*

**Fix: Fixed an issue with the attribute and css targets**


[d9399232d60b05a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9399232d60b05a) John Smart *2016-09-14 10:11:00*

**Minor refactoring**


[4bb199054d46c5d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4bb199054d46c5d) John Smart *2016-09-14 05:36:10*

**Fix: Fixed an issue where you could not check the selected status of an invisible element**

 * WebDriver lets you call isSelected() on elements whether they are visible or not. Serenity ensured that the element was visible first. However there are cases where the element is not visible to webdriver, but is still on the screen (for example  with some Javascript frameworks). 

[cb7c1995168e0d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cb7c1995168e0d4) John Smart *2016-09-14 05:35:26*

**Added an Interation class to open a URL directly.**


[3f44818f2163c36](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f44818f2163c36) John Smart *2016-09-13 11:39:33*


## v1.1.38-rc.1
### GitHub #520 

**Fixed #520 - include Apache license in the distributed JAR files**


[f4592d880919108](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4592d880919108) John Smart *2016-09-11 07:22:33*


### No issue

**documentation: Added some Javadocs to explain the isDisplayed vs isVisible distinction.**


[d74a38478b8a728](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d74a38478b8a728) John Smart *2016-09-11 07:58:58*

**fix: Fixed a broken test**


[ffab8b508f283c9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ffab8b508f283c9) John Smart *2016-09-10 22:19:40*

**Feature: Improved screenshot configuration**

 * You can now configure how screenshots are taken for screenplay questions, e.g. 
 * ``` 
 * serenity.take.screenshots.for.tasks=before_and_after_each_step 
 * serenity.take.screenshots.for.interactions=disabled 
 * serenity.take.screenshots.for.questions=for_failures 
 * ``` 

[36f4b3ae899517f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36f4b3ae899517f) John Smart *2016-09-10 21:14:41*

**Feature: more flexible screenshot configuration**

 * You can now configure screenshots by the class or interface that contains the step. For ScreenPlay, this means you can now write something like this: 
 * ``` 
 * serenity.take.screenshots.for.tasks=before_and_after_each_step 
 * serenity.take.screenshots.for.interactions=disabled 
 * ``` 

[969c69643be6d17](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/969c69643be6d17) John Smart *2016-09-10 19:26:11*

**Tweaked the build scripts to make parallel tests configurable**


[fbb083a696289c0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fbb083a696289c0) John Smart *2016-09-08 11:27:06*

**Tweaked the build scripts to make parallel tests configurable**


[9c6d45c4e3c03cb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9c6d45c4e3c03cb) John Smart *2016-09-08 10:35:21*

**Refactored the BrowserMob service**


[98c2d68aab3d88c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/98c2d68aab3d88c) John Smart *2016-09-08 09:02:10*

**Fixed a missing import**


[d52f332cd3e1f77](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d52f332cd3e1f77) Hazmeister *2016-08-25 16:35:39*

**Made it possible to ignore invalid certificates when using BrowserMob Proxy**


[a3a6e1fb73ad2d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a3a6e1fb73ad2d9) Harry King *2016-08-25 15:49:04*


## v1.1.37-rc.9
### GitHub #515 

**Fixed #515**


[9f3bb545d4c1d84](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f3bb545d4c1d84) John Smart *2016-08-31 08:44:26*


### No issue

**Refined some tests to make them more reliable on slow machines**


[15858a745342214](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/15858a745342214) John Smart *2016-08-30 14:58:22*

**Refined some tests to make them more reliable on slow machines**


[92341776eaaaaa8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/92341776eaaaaa8) John Smart *2016-08-30 14:26:04*

**Added an Interaction class to Screenplay for more readability**

 * Added the Interaction class to supersceed the Action class. The name ‘Interaction’ conveys the intent of this class more accurately. 

[fda5b83b50dd4e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fda5b83b50dd4e4) John Smart *2016-08-30 13:37:48*


## v1.1.37-rc.8
### No issue

**Refactored out an unreliable test**


[17ab32f20ad37de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/17ab32f20ad37de) John Smart *2016-08-24 04:56:22*

**Fixed a regression in the screenshot processing logic**


[e0b01f3e92a2e87](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0b01f3e92a2e87) John Smart *2016-08-23 16:22:18*

**Refactored the screenshot logic to be more modular**

 * Now a new darkroom is used for each instance of BaseStepListener. 

[992a4a7ee16ad3d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/992a4a7ee16ad3d) John Smart *2016-08-23 11:14:07*

**Added the usingAbilityTo() method**

 * The `usingAbilityTo()` method is a synonyme for the `abilityTo()` method, that allows for more readable constructs. 

[5bcff13bfc5ca2a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5bcff13bfc5ca2a) John Smart *2016-08-22 21:27:48*

**Simplified logging**

 * Moved several messages from INFO to DEBUG to reduce the volume of console output. 

[53b61a0d87d9e55](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/53b61a0d87d9e55) John Smart *2016-08-22 20:34:52*

**Simplified the Screenplay Ability interface**

 * The `Ability` interface is now a simple marker class with no methods to implement. If you need an Ability that knows about it’s actor, you can implement (in addition to the `Abilitiy` interface the `RefersToActor` interface, which defines the `asActor()` method previously found in the `Ability` class. 

[0fa5892adb8045a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0fa5892adb8045a) John Smart *2016-08-22 20:34:12*


## v1.1.37-rc.7
### GitHub #177 

**Partial fix for #177 (Test not included in report if constructor of Steps class throw exception)  - tests with a broken step class will still not appear in the reports but will no longer fail silently.**


[494a704e9b974f1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/494a704e9b974f1) John Smart *2016-05-15 21:21:25*


### GitHub #227 

**fix: Fixed #227 - issue with resetImplicitTimeout**


[32b0850200ef686](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/32b0850200ef686) John Smart *2016-05-15 09:46:26*


### GitHub #246 

**fix: Wrong wrapping params in step name (#246) and Assertion messages are displayed incompletely in report in case of failure (#380)**


[837dc49b213387f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/837dc49b213387f) John Smart *2016-05-15 20:24:54*


### GitHub #285 

**Fix: Fixed #285 - incorrect reporting of errors when using  @Test(expected=...)**

 * This was actually fixed in a previous commit, but these are some extra tests to make sure. 

[b8bdb90873ee87f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b8bdb90873ee87f) John Smart *2016-05-15 10:59:02*


### GitHub #380 

**fix: Wrong wrapping params in step name (#246) and Assertion messages are displayed incompletely in report in case of failure (#380)**


[837dc49b213387f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/837dc49b213387f) John Smart *2016-05-15 20:24:54*


### GitHub #460 

**Fixed #460**


[f856c759c1643cc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f856c759c1643cc) John Smart *2016-07-18 10:40:56*


### GitHub #462 

**Fixed #462 - json reports should default to UTF-8.**


[c674388a9a482d0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c674388a9a482d0) John Smart *2016-06-27 12:29:06*


### Jira UTF-8 

**Use UTF-8 for all file reading and writing**

 * Use UTF-8 for all file reading and writing to avoid charset issues on different environments. 

[ac036579c46f549](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ac036579c46f549) John Smart *2016-07-30 08:33:16*

**Fixed #462 - json reports should default to UTF-8.**


[c674388a9a482d0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c674388a9a482d0) John Smart *2016-06-27 12:29:06*

**use thucydides.report.encoding property to store/load outcomes  default is UTF-8 ; triggered by https://github.com/serenity-bdd/serenity-cucumber/issues/41**


[a15d988887122ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a15d988887122ef) liviu.carausu *2016-03-03 12:52:22*

**use UTF-8 to load json outcomes from file ; triggered by https://github.com/serenity-bdd/serenity-cucumber/issues/41**


[1f4ebb44f057fe4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1f4ebb44f057fe4) liviu.carausu *2016-03-02 12:11:27*


### No issue

**Test outcomes are now loaded in order of execution**


[c699febf49184df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c699febf49184df) John Smart *2016-07-31 07:39:09*

**Made the Screenplay Stage easier to use**

 * Added core support for the Screenplay Stage, making it easier for tests to manage actors in a consistent manner. 

[43d5f1e2c28999f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/43d5f1e2c28999f) John Smart *2016-07-31 07:38:57*

**Added links to the requirements types**

 * Added links to the requirements types and acceptance criteria on the requirements page. 

[5625f7e0c2e4645](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5625f7e0c2e4645) John Smart *2016-07-30 11:39:03*

**Use a separate WebDriver manager instance for each test**

 * The WebDriverManager instance for each test is now ThreadLocal and managed by the ThucydidesWebDriverSupport class, rather than being a Guice singleton. 

[a7b7c118a75e227](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a7b7c118a75e227) John Smart *2016-07-30 11:11:39*

**Added a “Requirements” breadcrumb for consistancy**


[0082ebd8e2e955f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0082ebd8e2e955f) John Smart *2016-07-30 11:09:45*

**Minor fixes and tidying up.**


[337c4f1d9648bf8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/337c4f1d9648bf8) John Smart *2016-07-30 08:34:52*

**Tests without any steps called are now considered to be successful**

 * Previously, tests with no steps in an example table were considered pending. Now they are considered successful. 

[db7fed3a871078e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/db7fed3a871078e) John Smart *2016-07-30 08:31:31*

**Requirement hierarchies can now have tests at multiple levels in JUnit**


[afb3fbb4bba4b79](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/afb3fbb4bba4b79) John Smart *2016-07-29 17:24:59*

**Fixed issue with reporting untested requirements.**


[ddd940ed0dfa027](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ddd940ed0dfa027) John Smart *2016-07-26 13:38:51*

**Improved requirements test result reporting**


[4fa9cb96ee753ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4fa9cb96ee753ef) John Smart *2016-07-26 03:41:09*

**rerun tests for WhenLocatingWebElementsUsingEnhancedFindBys**


[6cfb8cbc238b390](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6cfb8cbc238b390) cliviu *2016-07-24 20:46:49*

**try to fix WhenManagingWebdriverTimeouts**


[d34e6a94d687635](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d34e6a94d687635) cliviu *2016-07-24 06:17:34*

**JiraUpdaterService - add constants in ThucydidesSystemProperty**


[d00518e2ef3301c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d00518e2ef3301c) cliviu *2016-07-23 22:37:29*

**JiraUpdaterService**


[5c239ab7043935a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5c239ab7043935a) cliviu *2016-07-23 19:52:04*

**Improved requirements reporting**

 * Feature coverage is now reported in terms of the percentage of working requirements. 

[86f7b801178e658](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/86f7b801178e658) John Smart *2016-07-23 17:12:32*

**Improved requirements reporting.**

 * Show the correct total requirements in the requirements table when requirements without associated tests are present. 

[8e0607690135852](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8e0607690135852) John Smart *2016-07-21 18:22:06*

**Improved requirements reporting.**

 * Requirements with no corresponding stories are shown with an empty checkbox. 

[5b6e09f9f69e527](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b6e09f9f69e527) John Smart *2016-07-21 18:11:51*

**Included expected title in stack trace if waitForTitleToAppear (and similar methods) timeout**


[7b518c335f7f62a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7b518c335f7f62a) Harry King *2016-07-21 15:19:30*

**Fix: Narrative texts were not appearing in the reports**

 * Narrative texts defined using the @Narrative annotation in classes and package-info.java files are now reported correctly. A package-info.java file can be placed in an otherwise empty package to define a requirement with no tests. 

[3b07863dd1a9063](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3b07863dd1a9063) John Smart *2016-07-20 22:16:29*

**Refactored some tests**


[0769272807fe137](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0769272807fe137) John Smart *2016-07-18 11:56:13*

**Fixed an issue rendering screenshots in JUnit**


[5486ebd47a143e7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5486ebd47a143e7) John Smart *2016-07-18 11:04:09*

**More consistant requirements reporting**

 * The totals reported for the acceptance criteria on the Requirements page are now consistant with those reported for the test results. 

[3f04d4243626c40](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3f04d4243626c40) John Smart *2016-07-18 09:57:28*

**Improved requirements reporting**

 * Fixed several issues and improved performance for the package-based requirements reporting in JUnit. 

[deafb5052528a28](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/deafb5052528a28) John Smart *2016-07-17 23:05:20*

**refactor: made the TestOutcome streams more robust.**


[91ceccc601bb4b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91ceccc601bb4b9) John Smart *2016-07-05 14:43:12*

**refactor: Removed redundant experimental code**


[63d9de98fa6a5ff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/63d9de98fa6a5ff) John Smart *2016-07-05 14:41:57*

**fix: Fixed an issue with screenshots within nested groups**

 * Allow screenshots to be correctly reported even if they are stored in a nested group. 

[1e46b709e0a2d6a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1e46b709e0a2d6a) John Smart *2016-07-05 14:40:23*

**Updated htmlunit**


[6bafc172d4f4a24](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6bafc172d4f4a24) John Smart *2016-07-01 18:37:17*

**Updated Selenium to 2.53.1 to support Firefox 47**


[8b8165dd17c792a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b8165dd17c792a) John Smart *2016-07-01 17:50:23*

**fix: rest-assured fixed params wrapping**


[974869733f48600](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/974869733f48600) YamStranger *2016-06-30 11:01:33*

**updating changelog**


[1ba78d372b2b6aa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1ba78d372b2b6aa) YamStranger *2016-06-29 15:38:20*

**Little proxy is no longer required**


[21de91398d78bef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/21de91398d78bef) Harry King *2016-06-27 16:20:25*

**Update bmp to 2.1.1**


[d8779fb3fb884cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d8779fb3fb884cd) Harry King *2016-06-27 15:54:50*

**refactoring: test refactoring**


[cbba968356d0f85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbba968356d0f85) John Smart *2016-06-27 14:41:36*

**refactoring: test refactoring**


[a546e17b6411903](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a546e17b6411903) John Smart *2016-06-27 12:39:14*

**refactoring: requirements analysis code**


[c0def5f861dfae2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c0def5f861dfae2) John Smart *2016-06-27 12:12:55*

**refactor: refactored the webdriver instance handling for easier maintenance.**


[535d687317c4b50](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/535d687317c4b50) John Smart *2016-06-25 09:31:26*

**refactor: refactored tests to use MD5 filenames**


[6947f1f4dc38787](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6947f1f4dc38787) John Smart *2016-06-25 05:38:20*

**refactor: allow HTML reports to be generated uniquely from the JSON**


[8fa16c02f615ea6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fa16c02f615ea6) John Smart *2016-06-25 05:37:50*

**refactor: minor optimisation of the screenshot processing.**

 * Avoid trying to read a screenshot file if it is not in the working directory. 

[67b5fdfc5fe0ec5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/67b5fdfc5fe0ec5) John Smart *2016-06-25 05:34:41*

**Removed serenity-cli from the core build config.**


[ca2930bc5e8e92b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca2930bc5e8e92b) John Smart *2016-06-25 05:32:59*

**Switched file names to MD5 hashes.**


[312e3faf1eae055](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/312e3faf1eae055) John Smart *2016-06-25 05:32:39*

**refactor: Remove unnecessary legacy code.**


[08ff8a67edfd7eb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08ff8a67edfd7eb) John Smart *2016-06-24 19:01:22*

**refactor: minor refactoring**


[46ea87b28c5a9eb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/46ea87b28c5a9eb) John Smart *2016-06-24 18:57:48*

**Get tags optionally displayed in the menu from config**

 * Use the serenity.report.tag.menus property to define the tags you want to appear in the menu, and set serenity.report.show.tag.menus to true to get them to appear. 

[c31b020742dd13c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c31b020742dd13c) John Smart *2016-06-24 18:56:36*

**Reduced noisy logging**


[2cf9f3ba77661f8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2cf9f3ba77661f8) John Smart *2016-06-24 18:55:02*

**Moved the Serenity CIT to a separate repo**


[fb6ad82a0f802f0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fb6ad82a0f802f0) John Smart *2016-06-24 18:49:37*

**identical with upstream**


[a2614374a2ed1ea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2614374a2ed1ea) liviu.carausu *2016-06-09 20:49:20*

**delete not used files**


[09175ff1b23b8cb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/09175ff1b23b8cb) liviu.carausu *2016-06-09 20:43:03*

**feat: Improved handling of soft asserts**

 * You can now use soft asserts to combine a task to be executed, and a check to perform, optionally annotated with a business rule. This makes it much easier to check several business or validation rules on the same screen, in the same test. 
 * Sample code: 
 * &#39;&#39;&#39; 
 * then(alice).should( 
 * seeThat(TheValidationMessages.displayed(), reportsErrors(&quot;BSB must be 6 digits&quot;)). 
 * whenAttemptingTo(EnterABankAccount.bsbValueOf(&quot;1234&quot;)). 
 * because(&quot;BSB cannot be a number with less than 6 digits&quot;), 
 * seeThat(TheValidationMessages.displayed(), reportsErrors(&quot;BSB must be 6 digits&quot;)). 
 * whenAttemptingTo(EnterABankAccount.bsbValueOf(&quot;qwerty&quot;)). 
 * because(&quot;BSB cannot have alphabetical characters&quot;), 
 * seeThat(TheValidationMessages.displayed(), reportsErrors(&quot;BSB must be 6 digits&quot;)). 
 * whenAttemptingTo(EnterABankAccount.bsbValueOf(&quot;~!@#$%^&amp;*(&quot;)). 
 * because(&quot;BSB cannot have symbols&quot;), 
 * ); 
 * &#39;&#39;&#39; 

[62dc5f693a35d35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/62dc5f693a35d35) John Smart *2016-06-08 16:46:14*

**updating changelog**


[cd7ee0f319203de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd7ee0f319203de) YamStranger *2016-06-07 21:36:27*

**Test refactoring**


[52126150566c665](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/52126150566c665) John Smart *2016-05-15 21:38:58*

**Added a test to ensure that PageObjects can be used outside the @Step library.**


[e7a20c049727c47](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7a20c049727c47) John Smart *2016-05-15 13:26:54*

**Unit tests related to @Manual tests and recording the active driver used for a test.**


[f679ad049e35386](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f679ad049e35386) John Smart *2016-05-15 08:57:42*

**Added a short-hand way of expressing questions about Targets.**

 * For example, you can now say this: 
 * actor.should(seeThat(TheTarget.valueOf(NAME_FIELD), equalTo(&quot;Jim&quot;))) 

[02e1f2466334e04](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/02e1f2466334e04) John Smart *2016-05-15 08:44:22*

**fix: Fixed issues that resulted in declared drivers not being reported correctly.**


[70c7680aba10b47](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/70c7680aba10b47) John Smart *2016-05-15 08:13:10*

**A sanity check to ensure that you don't ask an actor to use a browser if you have not already given the actor the BrowseTheWeb ability.**


[0bf8b493fca3477](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0bf8b493fca3477) John Smart *2016-05-15 07:51:40*

**fix: Fixed an issue where tests that failed when using the @Test expected attribute where not being reported correctly.**


[b491091b6ad93b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b491091b6ad93b6) John Smart *2016-05-14 22:34:15*

**https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider - change packages**


[c4b32d4feb8b97e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c4b32d4feb8b97e) liviu.carausu *2016-05-12 21:50:42*

**https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider**


[5459b700350c958](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5459b700350c958) liviu.carausu *2016-05-12 21:43:07*

**https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider**


[12bcbc6d9f4ee8d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/12bcbc6d9f4ee8d) liviu.carausu *2016-05-12 11:36:19*

**https://github.com/serenity-bdd/serenity-core/issues/54 - introduce CleanupMethodAnnotationProvider**


[7bf2cc3bd632336](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7bf2cc3bd632336) liviu.carausu *2016-05-12 11:27:58*

**fix:Fixed a reporting issue with the PageTitleQuestion class**


[cc81c5f3ee8e6de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cc81c5f3ee8e6de) John Smart *2016-05-11 14:26:15*

**test: fixed test to work with new isAlive implementation**


[b50a1f85e7fcdb1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b50a1f85e7fcdb1) YamStranger *2016-05-11 11:34:40*

**fix: updated isAlive implementation to work with appium**


[b902c2e78fdd985](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b902c2e78fdd985) YamStranger *2016-05-11 11:25:11*

**fix: fixed alive function to work with appium**


[fab8b79b8a850f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fab8b79b8a850f3) YamStranger *2016-05-11 10:45:06*

**https://github.com/serenity-bdd/serenity-core/issues/54 - do not skip step if is called from @After or @AfterClass annotated method**


[cbe6f66b5e5d49a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbe6f66b5e5d49a) liviu.carausu *2016-05-10 11:48:12*

**https://github.com/serenity-bdd/serenity-core/issues/54 - do not skip step if is called from @After or @AfterClass annotated method**


[3d806af554e2644](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3d806af554e2644) liviu.carausu *2016-05-10 11:10:25*

**Fixed an issue where tasks with primitive parameters in the constructor could not be instantiated.**


[c4aa5fc27f56532](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c4aa5fc27f56532) John Smart *2016-05-10 07:12:53*

**switch nack to Chrome driver**


[c7bef26bb18bac9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c7bef26bb18bac9) liviu.carausu *2016-05-06 22:53:44*

**https://github.com/serenity-bdd/serenity-core/issues/406 . UseTagProviderStrategy**


[343a1ba4dfe1d0a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/343a1ba4dfe1d0a) liviu.carausu *2016-05-06 22:49:47*

**save testSource in TestOutcome and store it in JSON and XML format**


[ebff2013a00cb63](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ebff2013a00cb63) liviu.carausu *2016-05-05 22:12:31*

**feat: updated rest report template**


[bea7f473a9797c4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bea7f473a9797c4) YamStranger *2016-05-05 18:36:49*

**Revert "Avoid duplicated test outcome in reports"**


[39c6df7f695c815](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39c6df7f695c815) Liviu Carausu *2016-05-05 07:48:10*

**Revert "set property "use.test.case.for.story.tag = false" by default"**


[5aaa3e935b7db4c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5aaa3e935b7db4c) Liviu Carausu *2016-05-05 07:46:21*

**set property use.test.case.for.story.tag = false by default (https://github.com/serenity-bdd/serenity-jbehave/issues/17)**


[7be65dd8073a657](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7be65dd8073a657) liviu.carausu *2016-05-04 22:49:11*

**set property use.test.case.for.story.tag = false by default (https://github.com/serenity-bdd/serenity-jbehave/issues/17)**


[91f659860f052d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91f659860f052d1) liviu.carausu *2016-05-04 21:21:42*

**deactivate by default FeatureStoryTagProvider**


[ebf4ffb3dcf02cf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ebf4ffb3dcf02cf) liviu.carausu *2016-05-02 21:40:09*

**Updated the SLF4J API**


[e40bb99d2440250](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e40bb99d2440250) John Smart *2016-05-02 13:10:19*

**avoid duplicated test outcome in reports : see  https://github.com/serenity-bdd/serenity-cucumber/issues/19**


[83793d47044da0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/83793d47044da0e) liviu.carausu *2016-04-25 22:51:37*

**avoid duplicated test outcome in reports : see  https://github.com/serenity-bdd/serenity-cucumber/issues/19**


[d89bf6bec68cd97](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d89bf6bec68cd97) liviu.carausu *2016-04-25 22:24:16*

**fix: updated implementation to support multiple constructors in steps libraries**


[3bbb0c0580638a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3bbb0c0580638a9) YamStranger *2016-04-25 19:29:35*

**test: added test to check how serenity works with multiple constructors in step libraries**


[3df916a63853763](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3df916a63853763) YamStranger *2016-04-25 19:25:59*

**switch back to previous version**


[03aac5170754ef4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/03aac5170754ef4) liviu.carausu *2016-04-10 21:13:41*

**https://github.com/serenity-bdd/serenity-core/issues/374 : in SerenityRunner use always one instance of the StepFactory**


[ec7dd10ec59bb40](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ec7dd10ec59bb40) liviu.carausu *2016-04-07 21:50:42*

**Updated gradle wrapper version**


[76ef11b52b48895](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/76ef11b52b48895) John Smart *2016-03-07 01:43:32*

**Tidied up some dependencies**


[646a0c9276dbd20](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/646a0c9276dbd20) John Smart *2016-03-07 01:28:30*

**Trimmed down requirements logs**


[488d557e33e3aa9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/488d557e33e3aa9) John Smart *2016-03-07 01:28:03*

**updating changelog**


[1209ed289ac75da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1209ed289ac75da) YamStranger *2016-03-04 15:26:47*

**test: updated tests for rest-assurance to use wiremock server instead of petstore**


[4d2029c6b2b6313](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4d2029c6b2b6313) YamStranger *2016-03-04 10:37:21*

**updating changelog**


[0ac588d9b29bfed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ac588d9b29bfed) YamStranger *2016-03-03 06:07:58*

**Updated the smoketests**


[b585afa23592ce3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b585afa23592ce3) John Smart *2016-03-02 21:36:10*


## v1.1.37-rc.6
### GitHub #450 

**Fixed #450 - disabled WebDriver sometimes causes classcast exceptions.**


[eb604025c85456b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eb604025c85456b) John Smart *2016-06-14 07:47:03*


### No issue

**refactoring: hardening build**


[7d4b04ba8f0fb46](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7d4b04ba8f0fb46) John Smart *2016-06-22 20:43:44*

**refactoring: Hardening the build process**


[0660cba01de1bfa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0660cba01de1bfa) John Smart *2016-06-22 20:35:09*

**refactor: fixing Gradle build**


[4daf2f80b8b6b7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4daf2f80b8b6b7c) John Smart *2016-06-22 19:06:12*

**refactoring: Fine-tuning bintray deployment**


[ad61bf328487f7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad61bf328487f7c) John Smart *2016-06-22 18:58:39*

**refactor: tweaked the parallel tests in the Gradle build.**


[11532ae11710c56](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/11532ae11710c56) John Smart *2016-06-22 18:26:30*

**refactor: ensure the use of threadlocal StepEventBus instances.**


[12b9bf2d21a0353](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/12b9bf2d21a0353) John Smart *2016-06-22 18:00:19*

**Added a CLI module to run the Serenity reports aggregation from an executable JAR**


[6fbf736acf1a399](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6fbf736acf1a399) John Smart *2016-06-22 17:19:38*

**performance: Improved speed and memory management for report aggregation.**


[b5afdcce9514a9e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5afdcce9514a9e) John Smart *2016-06-22 11:57:22*

**updating changelog**


[5aff5c3baf04199](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5aff5c3baf04199) YamStranger *2016-06-21 18:19:37*

**feat: add ability to see response body for responses with  HTML content type in report**

 * Rest-Assured Integration: now it will be available to verify Response Body in report for REST Queries for responses with HTML content type 

[b5cd76d68401dad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5cd76d68401dad) viktor-klymenko *2016-06-17 13:24:45*

**Added a test illustrating the whenAttemptingTo() construct.**


[86c6aefcf78804a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/86c6aefcf78804a) John Smart *2016-06-17 10:11:00*


## v1.1.37-rc.5
### No issue

**feat: add ability to assert that matched element is not present in collection**

 * now it is possible to assert that element is not present in collection using BeanMatcherAsserts.shouldNotMatch(List&lt;T&gt; items, BeanMatcher... matchers) method 

[1010a8dac5ac18d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1010a8dac5ac18d) viktor-klymenko *2016-06-15 10:47:12*


## v1.1.37-rc.4
### GitHub #440 

**Fixed #440 - issue with JSON serialisation.**

 * No reports were being generated due to an issue with the GSON deserialisation. 

[6b0c90586fafb66](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6b0c90586fafb66) John Smart *2016-06-10 16:14:51*


### GitHub #445 

**feature: truncate long titles in HTML tip texts to save space (#445)**


[0a8d1cc51959bb4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a8d1cc51959bb4) John Smart *2016-06-10 16:42:50*


### No issue

**refactor: Improved screenshot test performance.**


[3e547d4c53868b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e547d4c53868b0) John Smart *2016-06-10 16:53:07*


## v1.1.37-rc.3
### GitHub #428 

**Fixed #428**


[b979db723954863](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b979db723954863) John Smart *2016-06-09 18:14:39*


### GitHub #442 

**Refined the collection loading strategy (#442)**

 * You can now use the &quot;serenity.webdriver.collection_loading_strategy&quot; property to define how Serenity loads collections of web elements. There are three options: 
 * - Optimistic 
 * - Pessimistic (default) 
 * - Paranoid 
 * Optimistic will only wait until the field is defined. This is the native Selenium behaviour. 
 * Pessimistic will wait until at least the first element is displayed. This is currently the default. 
 * Paranoid will wait until all of the elements are displayed. This can be slow for long lists. 

[84204cf501e9492](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/84204cf501e9492) John Smart *2016-06-10 07:34:04*


### GitHub #443 

**Fixed #443 (localisation issue when generating the reports from Gradle in some non-English locales)**


[58eb4c4fb2304ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/58eb4c4fb2304ef) John Smart *2016-06-09 17:00:16*


### No issue

**refactor:Test refactoring**


[87545d1a243898e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/87545d1a243898e) John Smart *2016-06-10 08:41:25*

**refactoring:improved reporting when printing web elements to the console**


[efe8f04c254f61b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/efe8f04c254f61b) John Smart *2016-06-10 07:58:55*

**Added the senity.webdriver.collection.loading.strategy.**

 * hould we assume that collections of webdriver elements are already on the page, or if we should wait for them to be available. 
 * This property takes two values: Optimistic or Pessimistic. Optimistic means that the elements are assumed to be on the screen, and will be 
 * loaded as-is immediately. This is the normal WebDriver behavior. 
 * For applications with lots of ASynchronous activity, it is often better to wait until the elements are visible before using them. The Pessimistic 
 * mode waits for at least one element to be visible before proceeding. 
 * For legacy reasons, the default strategy is Pessimistic. 

[89feb38a53b9a24](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89feb38a53b9a24) John Smart *2016-06-09 22:48:21*

**refactoring: test hardening**


[4257dd8568e4d9c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4257dd8568e4d9c) John Smart *2016-06-09 18:37:25*


## v1.1.37-rc.2
### GitHub #419 

**Refactored fix for #419**


[9d20f48c24e90b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d20f48c24e90b0) John Smart *2016-06-09 07:12:12*

**Refactored fix for #419**


[54d4f697bfff034](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/54d4f697bfff034) John Smart *2016-06-09 06:39:18*

**Fixed #419**


[6e943ce46f02b57](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6e943ce46f02b57) John Smart *2016-06-08 21:57:38*


### GitHub #424 

**Fixed #424**


[6d8d8c212d61433](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d8d8c212d61433) John Smart *2016-06-08 21:44:50*


### No issue

**refactor: hardening time-based tests and minor refactoring.**


[a36b539ea57112c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a36b539ea57112c) John Smart *2016-06-09 09:05:32*

**refactor: test refactoring**


[a5755b767d175e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a5755b767d175e4) John Smart *2016-06-09 07:33:15*

**refact: Making the tests more cross-platform**


[c23e24f4a05ee0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c23e24f4a05ee0e) John Smart *2016-06-08 22:43:26*

**Added some memory for the tests**


[f7ea309a33df0b7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7ea309a33df0b7) John Smart *2016-06-08 22:19:37*

**refact: Making the tests more cross-platform**


[78b58de2ed94948](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/78b58de2ed94948) John Smart *2016-06-08 22:07:23*

**feat:Added extra diagnostics for the Check gradle and maven tasks**


[8e5571cd96af1bc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8e5571cd96af1bc) John Smart *2016-06-08 17:50:13*

**WIP**


[4ea4e50eeca1a5e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4ea4e50eeca1a5e) John Smart *2016-06-06 14:51:42*

**Added a UIAction to perform a sendKeys() without clearing the field first.**


[a97a7d6de41abef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a97a7d6de41abef) John Smart *2016-06-06 14:51:31*


## v1.1.36-rc.1
### No issue

**fix: Fixed an issue with the screenplay webdriver integration**

 * Browser windows were not closing correctly when there were more than one driver used in a single test. 

[0ea09c7f24809c7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0ea09c7f24809c7) John Smart *2016-06-02 19:43:55*

**refactor: Renamed the 'browse-the-web' module**

 * Renamed the &#39;browse-the-web&#39; module to &#39;serenity-screenplay-webdriver&#39; for more consistancy. 

[022217733bafafb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/022217733bafafb) John Smart *2016-06-02 19:43:12*

**Revert "fix: Fixed an issue where the screenplay module was not closing the last browser if several browsers were being used in a test"**

 * This reverts commit e9f81313bd86b1222667e137fe00a133226cad98. 

[28652f505e0cc72](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28652f505e0cc72) John Smart *2016-06-02 13:21:09*

**fix: Fixed an issue where the screenplay module was not closing the last browser if several browsers were being used in a test**


[e9f81313bd86b12](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e9f81313bd86b12) John Smart *2016-06-02 11:42:00*

**Test hardening**


[6d735591d4eb95a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d735591d4eb95a) John Smart *2016-06-02 10:38:16*

**Test simplification**


[92f15b9196ac3ad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/92f15b9196ac3ad) John Smart *2016-06-02 09:56:07*

**Test refactoring - removed unnecessary code**


[75abe17d88d18a2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/75abe17d88d18a2) John Smart *2016-06-02 09:06:47*

**Improved consistancy in driver use.**


[91d77a09d12b7c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91d77a09d12b7c5) John Smart *2016-06-02 08:59:12*

**refactoring: Removed redundant mock**


[bf4a6ecb42b5766](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf4a6ecb42b5766) John Smart *2016-06-02 08:58:27*

**Test refactoring**


[9cc788958b17f59](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9cc788958b17f59) John Smart *2016-06-02 08:32:48*

**refactoring: made the screenshot processing more robust**


[feeaab0d355b6d8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/feeaab0d355b6d8) John Smart *2016-06-02 08:22:10*

**Fixed a refactoring error.**


[2d77834537e12a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d77834537e12a8) John Smart *2016-06-02 07:45:19*

**chore: General refactoring and fixing minor performance issues.**


[de858923fdfa585](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/de858923fdfa585) John Smart *2016-06-02 07:07:26*

**updating changelog**


[e799eeb87a9403b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e799eeb87a9403b) YamStranger *2016-06-02 06:20:47*

**Test hardening**


[88e11b0ac64e206](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/88e11b0ac64e206) John Smart *2016-06-02 06:04:53*

**Temporarily suspended parallel unit tests**


[3c22e3da4a0fec2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3c22e3da4a0fec2) John Smart *2016-06-02 05:31:01*

**Fixed the browser tests**


[20b382233b94a6d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/20b382233b94a6d) John Smart *2016-06-01 22:59:23*

**Refactoring**


[654de43084162a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/654de43084162a0) John Smart *2016-06-01 22:33:01*

**Refactoring**


[97a59eace3bf89a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/97a59eace3bf89a) John Smart *2016-06-01 22:18:52*

**Refactoring**


[76b30eb2f9ea4fe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/76b30eb2f9ea4fe) John Smart *2016-06-01 21:34:32*

**Refactoring**


[847e6bae3800b10](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/847e6bae3800b10) John Smart *2016-06-01 21:27:06*

**Refactoring**


[99767081bc0f1c8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/99767081bc0f1c8) John Smart *2016-06-01 20:49:58*

**Refactoring**


[68dcf4098949f1d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/68dcf4098949f1d) John Smart *2016-06-01 20:39:07*

**Refactoring and simplification of the driver management.**


[25c7a626768b8c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/25c7a626768b8c5) John Smart *2016-06-01 19:50:26*

**Improved screenshot capture logic**

 * The screenshots are taken using the current active driver. Sometimes this gets out of sync, and the wrong driver is used for screenshots (resulting in no screenshots being taken). Now, when a driver is called it becomes the current active driver. 

[4a7a8edc47139e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4a7a8edc47139e4) John Smart *2016-06-01 16:15:23*


## v1.1.35-rc.1
### GitHub #426 

**Work-around for #426**

 * This allows the reports to be generated even with unknown result types, allowing for easier trouble-shooting. 

[d0cf954f7076df1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d0cf954f7076df1) John Smart *2016-05-27 07:00:28*


### No issue

**Continued refactoring.**


[405d22de1bfcc46](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/405d22de1bfcc46) John Smart *2016-05-27 14:12:17*

**General refactoring**


[18d46305fb6d3bd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/18d46305fb6d3bd) John Smart *2016-05-27 13:57:11*

**Fixed an issue where Cucumber screenshots where not being recorded correctly**


[fe342f63df08a67](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe342f63df08a67) John Smart *2016-05-27 12:10:20*

**chore: simple refactoring**


[8a7e77e4b06b4d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8a7e77e4b06b4d4) John Smart *2016-05-27 09:53:38*


## v1.1.34-rc.1
### GitHub #235 

**Skip resizing on appium, see #235**


[238c8819789b567](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/238c8819789b567) Martin Reinhardt *2016-05-19 14:23:46*


### No issue

**Make sure soft asserts don't make webdriver calls unnecessarily.**


[d2bb5318a5caa0b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d2bb5318a5caa0b) John Smart *2016-05-20 08:31:42*

**Ensure that a browser doesn't open for suspended tests**

 * Suspended tests include pending, skipped and manual tests. 

[fe81e6857c61e54](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe81e6857c61e54) John Smart *2016-05-19 20:03:38*

**Refactored an Appium test to work on different environments.**


[885fb0ac2660857](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/885fb0ac2660857) John Smart *2016-05-19 20:02:30*

**Adding basic appium android tests**


[9deb75b0d7a5973](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9deb75b0d7a5973) Martin Reinhardt *2016-05-19 14:40:27*


## v1.1.33-rc.1
### No issue

**Fixed an issue where error messages containing < or > where not correctly reported**


[b735e4c555c3aac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b735e4c555c3aac) John Smart *2016-05-18 15:48:23*


## v1.1.32-rc.3
### GitHub #247 

**fix: multiple csv file used as test data issue (#247)**


[b6b55edd2b94037](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6b55edd2b94037) John Smart *2016-05-17 08:55:35*


### GitHub #378 

**Fix: #378 - Serenity launches 2 instances of Chrome if the driver parameter is not set in the @Managed annotation**


[69b5babbddefd21](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/69b5babbddefd21) John Smart *2016-05-17 11:39:55*


### GitHub #39 

**fix: Incorrect alignment of index.html file when story title is too long (#39)**


[2262c007b81f3d0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2262c007b81f3d0) John Smart *2016-05-17 22:22:24*


### No issue

**Refactored an integration test**


[7021b294e9956ee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7021b294e9956ee) John Smart *2016-05-18 14:38:33*

**Removed redundent test**


[f7200aefa8a4354](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7200aefa8a4354) John Smart *2016-05-18 14:06:53*

**Updated unit tests**


[c44a79a0acdf9b3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c44a79a0acdf9b3) John Smart *2016-05-18 13:48:45*

**Fixed an issue with broken links in the requirements reports.**


[60c2fc8116ccfec](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60c2fc8116ccfec) John Smart *2016-05-18 09:40:30*

**Removed redundant test**


[4cbed6fc46e87f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4cbed6fc46e87f9) John Smart *2016-05-17 11:59:12*


## v1.1.32-rc.2
### Jira version-3 

**Updated appium java client to version 3.4.1**

 * https://discuss.appium.io/t/java-client-version-3-4-0-released/8961 
 * https://discuss.appium.io/t/java-client-version-3-4-1-released/9416 

[4bc580628611e9f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4bc580628611e9f) Hazmeister *2016-05-16 10:39:03*


### No issue

**Test refactoring**


[796e43b9c92f887](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/796e43b9c92f887) John Smart *2016-05-16 21:35:19*

**Test refactoring**


[51abeab24d9b101](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/51abeab24d9b101) John Smart *2016-05-16 21:20:11*

**Test refactoring**


[ba1fa1caddbac7e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba1fa1caddbac7e) John Smart *2016-05-16 21:13:32*

**Removed redundant tesxt**


[6d579dad70d7416](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d579dad70d7416) John Smart *2016-05-16 19:06:27*

**Removed diagnostic log messages**


[0382a40bea4aaff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0382a40bea4aaff) John Smart *2016-05-16 15:49:19*

**Removed diagnostic log messages**


[63b9bb77c7a0ef9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/63b9bb77c7a0ef9) John Smart *2016-05-16 15:47:48*

**Added logging to troubleshoot odd issue on SnapCI**


[719c0759a630b18](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/719c0759a630b18) John Smart *2016-05-16 15:40:17*

**Added logging to troubleshoot odd issue on SnapCI**


[6f13b8ce61cf5ac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f13b8ce61cf5ac) John Smart *2016-05-16 15:36:06*

**Disable parallel unit and integration tests (experimental)**


[891ee2368152e2e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/891ee2368152e2e) John Smart *2016-05-16 15:21:28*

**Fixed issue where a RemoteDriver was being used instead of an AppiumDriver**


[dfb0cd3f08ac781](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dfb0cd3f08ac781) John Smart *2016-05-16 15:21:14*

**Removed redundant test**


[6a0805c88cd1bfb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6a0805c88cd1bfb) John Smart *2016-05-16 13:56:34*

**Removed the junit retry logic (use the native JUnit feature instead)**


[cb7891d21923d4c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cb7891d21923d4c) John Smart *2016-05-16 10:39:53*


## v1.1.32-rc.1
### No issue

**Refactoring the Screenplay code - WIP**


[af83a0d0b143789](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af83a0d0b143789) John Smart *2016-04-20 14:22:55*

**updating changelog**


[6b978228442a505](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6b978228442a505) YamStranger *2016-04-20 06:54:57*


## v1.1.31-rc.1
### Jira UTF-8 

**feat: serenity-rest implemented recording of all basic fields, like status-code, body, path, method. Implemented recoding of exceptions.**

 * Now all operations will be recorded, after request executed, in report all fields will appear in same format as send. 
 * After this update content/body should be matched with additional transformation, because if string contains json/xml it will be reformatted by RestAssured, 
 * for example for Json it can be: 
 * ``` 
 * ... 
 * import static net.serenitybdd.rest.staging.JsonConverter.*; 
 * ... 
 * assert formatted(query.responseBody) == formatted(body) 
 * ``` 
 * Introduced DecomposedContentType class for representation simple content types: 
 * if in rest assured defined only: 
 * ``` 
 * ANY(&quot;*/*&quot;), 
 * TEXT(&quot;text/plain&quot;), 
 * JSON(&quot;application/json&quot;, &quot;application/javascript&quot;, &quot;text/javascript&quot;), 
 * XML(&quot;application/xml&quot;, &quot;text/xml&quot;, &quot;application/xhtml+xml&quot;), 
 * HTML(&quot;text/html&quot;), 
 * URLENC(&quot;application/x-www-form-urlencoded&quot;), 
 * BINARY(&quot;application/octet-stream&quot;); 
 * ``` 
 * not it will be possible define something like that `given().contentType(&quot;$APPLICATION_JSON&quot;)` for: 
 * ``` 
 * ANY(&quot;*/*&quot;), 
 * TEXT(&quot;text/plain&quot;), 
 * APPLICATION_JSON(&quot;application/json&quot;), 
 * APPLICATION_JAVASCRIPT(&quot;application/javascript&quot;), 
 * APPLICATION_XML(&quot;application/xml&quot;), 
 * TEST_XML(&quot;text/xml&quot;), 
 * APPLICATION_XHTML_XML(&quot;application/xhtml+xml&quot;), 
 * TEST_JAVASCRIPT(&quot;text/javascript&quot;), 
 * HTML(&quot;text/html&quot;), 
 * URLENC(&quot;application/x-www-form-urlencoded&quot;), 
 * BINARY(&quot;application/octet-stream&quot;); 
 * ``` 
 * as well as find some RestAssured content type by DecomposedContentType or String: 
 * ``` 
 * DecomposedContentType.byString(&quot;application/javascript; Charset: UTF-8&quot;).contentType() 
 * ``` 

[8040ade93c477c2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8040ade93c477c2) YamStranger *2016-04-16 12:15:51*


### No issue

**fixed issue with lambdaJ and CGLib when using Hamcrest assertions on collections**


[1e5870431bad8fd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1e5870431bad8fd) John Smart *2016-04-20 04:59:23*

**feat: serenity-rest-assured updated rest core and tests, renamed some classes**


[a608efe35f711f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a608efe35f711f3) YamStranger *2016-04-20 00:32:51*

**feat: serenity-core updated restQuery and reports to include Cookies and headers, changed wrapping of request and response**


[ae9e4a87b1b76ad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ae9e4a87b1b76ad) YamStranger *2016-04-19 23:31:01*

**feat: serenity-rest supported sequences of operations in different steps:**

 * Now it is possible run sequence of rest operations, with restspecification and response shared in one thread: 
 * ``` 
 * class RestSteps { 
 * @Step 
 * def successfulGet(final String url) { 
 * given().get(&quot;$url/{id}&quot;, 1000).then().body(&quot;id&quot;, Matchers.equalTo(1000)); 
 * } 
 * @Step 
 * def getById(final String url) { 
 * rest().get(&quot;$url/{id}&quot;, 1000); 
 * } 
 * @Step 
 * def thenCheckOutcome() { 
 * then().body(&quot;Id&quot;, Matchers.anything()) 
 * } 
 * } 
 * ``` 

[2d4719b5c0ab7cf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d4719b5c0ab7cf) YamStranger *2016-04-16 19:43:55*

**feat: implemented support of DryRun for Serenity Rest**

 * Now it dryRun enabled all rests tests will be successful, all checks for rest will be successful, 
 * no requests to external systems will be send. Some default values will be returned for request 
 * body, header, cookies and son on, and all values for request will be recorded and included in report. 
 * It is possible enable dryRun only for one test/class, but it can produce some performance issues because of 
 * analyzing of stacktrace for all invocations during check if DryRun enabled. Should be used in part of some rule 
 * to enable it before test and disable after test. On other cases serenity.dry.run should be used: 
 * ``` 
 * RestExecutionHelper.enableDryRunForClass(WhenEnabledDryRunWithSerenityRest.class) 
 * ``` 

[de2dc5ee86b2f85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/de2dc5ee86b2f85) YamStranger *2016-04-16 18:08:14*

**test: serenity-rest created test to check how dryRun works for serenity rest**


[b99deb4009cb2a1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b99deb4009cb2a1) YamStranger *2016-04-16 17:56:34*

**added DecomposedContentType**


[36dae0ca34309fb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36dae0ca34309fb) YamStranger *2016-04-16 13:09:51*

**test: serenity-rest implemented tests to check how recording of rest requests and reponses works**


[d869ad2252483e7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d869ad2252483e7) YamStranger *2016-04-16 10:51:11*

**fixed configuration tests, added base step listener**


[a4b5c70e06b7d98](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a4b5c70e06b7d98) YamStranger *2016-04-16 10:00:17*

**added test listener to tests, fixed patch operation**


[e16bec4f669be5b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e16bec4f669be5b) YamStranger *2016-04-16 09:52:20*

**test: serenity-rest implemented test to check if failed query recorded and it is possible to use assertions**


[f5eb6455a017508](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f5eb6455a017508) YamStranger *2016-04-15 06:50:50*

**fixed style for RestReportingHelper**


[606d906e39cea0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/606d906e39cea0e) YamStranger *2016-04-15 06:29:46*

**feat: serenity-rest implemented recording of boyd, content-type, path, method, prepared recoding structure**

 * Now it will be is easy to include in restQuery recording info about cookies, headers, and so on using filtering RestAssured mechanism 

[2787384de5c0e4d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2787384de5c0e4d) YamStranger *2016-04-15 06:26:36*

**test: serenity-rest implemented test to check if body, contenttype, responce is recorded**


[ca6690e39b56727](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca6690e39b56727) YamStranger *2016-04-15 06:22:20*

**style updated**


[f7c61efd7bf46f5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7c61efd7bf46f5) YamStranger *2016-04-05 18:53:48*

**feat: serenity rest core decomposed to make possible use shaned method invocations for configuring default parameters.**

 * Now it is possible to configure default parameters in two ways (default and changed): 
 * ``` 
 * new RestDefaultsChained() 
 * .setDefaultBasePath(&quot;some/path&quot;) 
 * .setDefaultProxy(object) 
 * .setDefaultPort(10) 
 * // or 
 * SerinityRest.setDefaultBasePath(&quot;some/path&quot;); 
 * SerinityRest.setDefaultProxy(object); 
 * SerinityRest.setDefaultPort(10); 
 * // or is static imports are used 
 * setDefaultBasePath(&quot;some/path&quot;); 
 * setDefaultProxy(object); 
 * setDefaultPort(10); 
 * ``` 

[ebe88bdb34972ca](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ebe88bdb34972ca) YamStranger *2016-04-05 18:51:26*

**feat: serenity rest core decomposed to make possible to use different classes for different purposes.**

 * Now it is possible to execute almost all serenity rest operations using SerenityRest (90+ methods) or use smaller classes: 
 * RestDefaults (50+ methods)can be used for configuratins only default parameters 
 * RestRequests (20+ methods)can be used for making requests 
 * RestUtility  (20+ methods)can be used for initialising some default or reusable objects 

[8d3e5c93e264970](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d3e5c93e264970) YamStranger *2016-04-05 18:51:25*

**feat: serenity rest core updated to return updated configurations, filters, etc.**


[117dbf23d51a4e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/117dbf23d51a4e6) YamStranger *2016-04-05 18:51:25*

**feat: serenity-rest implemented wrapping for all requests in SerenityRest class, all covered by tests**


[5bdeda5fa991e70](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5bdeda5fa991e70) YamStranger *2016-04-05 17:25:35*

**adding reflection helper**


[f3ab690a4ab25af](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3ab690a4ab25af) YamStranger *2016-04-05 16:25:32*

**feat: implemented wrapping of request after configuring cookies**

 * Now serenity rest will work correctly after operations like below: 
 * ``` 
 * given().cookies(mapWithCookies).get(url) 
 * given().cookies(&quot;value&quot;).get(url) 
 * given().cookies(&quot;value&quot;, param).get(url) 
 * ``` 

[5b6c531b8551b46](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b6c531b8551b46) YamStranger *2016-04-05 16:24:54*

**test: added test to ceheck wrapping after cookie operations**


[7c0f58aedac4ea2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7c0f58aedac4ea2) YamStranger *2016-04-05 16:22:05*

**feat: implemented wrapping of request specification after multypart request configurations**


[52e54a9c668627a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/52e54a9c668627a) YamStranger *2016-04-05 14:17:28*

**test: implemented test for wrapping request with configured multipart request**


[7d5f5c881d59a8d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7d5f5c881d59a8d) YamStranger *2016-04-05 14:16:36*

**feat: implemented wrapping during executing on request instance operations like when, with, given, and**


[432e5f6a141cfe0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/432e5f6a141cfe0) YamStranger *2016-04-05 06:19:29*

**test: serenity-rest added tests for using chains of given, and, when, with based on request instance**


[7f439d278111f39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f439d278111f39) YamStranger *2016-04-05 06:18:03*

**feat: in serenity-rest implemented setting base path, base uri, session id, port, urlEncodingEnabled**

 * To set base paramters next code can be used: 
 * ``` 
 * given().basePath(path).get(url) 
 * given().baseUri(base).port(port).get(path) 
 * given().baseUri(base).port(port).get(path) 
 * given().baseUri(base).basePath(&quot;/test&quot;).get(&quot;log/levels&quot;) 
 * ``` 

[5cc45f33718c705](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5cc45f33718c705) YamStranger *2016-04-05 06:01:14*

**test: serenity-rest tested to configure base path and pase uri and other base configurations**


[08c48231157fcdc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08c48231157fcdc) YamStranger *2016-04-05 05:57:56*

**feat: impelemented processing and wrapping of result for operations with pathParameter, queryParams, param, params, parameters, formParam**


[81c6d3092a1522b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/81c6d3092a1522b) YamStranger *2016-04-01 06:55:04*

**feat: implemented wrapping after operations with body, content, headers, contentTypes**

 * Now serenity-rest will work correctly with opertions like: 
 * ``` 
 * given().contentType(ContentType.XML).get(url) 
 * given().contentType(ContentType.XML).get(url) 
 * given().headers(map).get(url) 
 * given().header(CONTENT_TYPE.asString(), ContentType.JSON).get(url) 
 * given().contentType(&quot;application/json&quot;).content(body).post(url) 
 * ``` 

[231052beeacac17](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/231052beeacac17) YamStranger *2016-04-01 06:17:16*

**test: added tests to check wrapping response and request and correctness of setting body, content, headers**


[63d9538b0322057](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/63d9538b0322057) YamStranger *2016-04-01 06:02:33*

**feat: implemented wrapping of request/response after HTTPS and Auth configurations**

 * Now, rest assured will use wrapped request and response after configurations like below: 
 * ``` 
 * given().relaxedHTTPSValidation().get(url) 
 * when().authentication().basic(&quot;login&quot;,&quot;password&quot;).get(url) 
 * given().authentication().none() 
 * given().auth().oauth2(token) 
 * given().authentication().basic(&quot;user&quot;, &quot;password&quot;) 
 * ``` 

[4cb944933b81583](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4cb944933b81583) YamStranger *2016-03-31 06:43:34*

**test: serenity-rest, added tests for checking https requests and auth params configurations**


[443ca49c83bc412](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/443ca49c83bc412) YamStranger *2016-03-31 06:39:51*

**feat: serenity-rest implemented wrapping of request after redirects configuration**


[146b1d8129b31e8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/146b1d8129b31e8) YamStranger *2016-03-31 05:11:09*

**test: serenity-rest, added tests for checking wrapping after redirects configuration**


[794f5af4fa1a9e8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/794f5af4fa1a9e8) YamStranger *2016-03-31 05:09:47*

**feat: implemented processing log operation with rest assurance, and created tests for it**

 * Now it is possible log some information during request/response and wrapped request/response will be returned after that: 
 * ``` 
 * request.log().all().response() 
 * request.log().body().request().get(url) 
 * request.log().body().get(url) 
 * ``` 

[af19533f10fc384](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af19533f10fc384) YamStranger *2016-03-30 20:03:53*

**feat: implemented HEAD, PATCH, DELETE, OPTIONS, POST operations and wrapping of results**


[7a014ec919eb224](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a014ec919eb224) YamStranger *2016-03-30 18:51:47*

**test: created tests to check if HEAD operatin works correctly and return empty body**


[30105b4face22f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30105b4face22f9) YamStranger *2016-03-30 18:50:24*

**test: added tests to check wrapping repsponse after PUT, POST, PATCH, DELETE, OPTIONS, HEAD requests**


[ca059a5b9a51c87](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca059a5b9a51c87) YamStranger *2016-03-30 18:39:23*

**feat: wrapped put request, updated wrapping of get request - to use only one function-endpoint**


[5b6b67fa068e7a7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b6b67fa068e7a7) YamStranger *2016-03-30 07:02:07*

**test: added tests for PUT operations**


[f1e2cde371629a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f1e2cde371629a9) YamStranger *2016-03-30 06:17:48*

**style: changed request organisation, added tests for get operation**


[6ed97f3f78d8aa7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ed97f3f78d8aa7) YamStranger *2016-03-30 06:02:25*

**simple refactoring of request and response**


[508e664096bc50d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/508e664096bc50d) YamStranger *2016-03-30 05:08:16*

**test: updated tests to check how SerenityRest works with expectation and validations like then, expect, statusCode etc**


[cd93fbb84276728](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd93fbb84276728) YamStranger *2016-03-22 21:35:45*

**feat: wrapped response from GET to make possible including in report validations**

 * Now during execution then, expect, and validations like then.statusCode(200) used wrapped response. 
 * For example supported lines: 
 * ``` 
 * given() 
 * .param(&quot;x&quot;, &quot;y&quot;) 
 * .expect() 
 * .statusCode(200) 
 * .body(Matchers.equalTo(body)) 
 * .when() 
 * .get(url) 
 * given().get(url).then().statusCode(200) 
 * ``` 

[5b4590c78e6e245](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b4590c78e6e245) YamStranger *2016-03-22 21:34:25*

**feat: updated serentyRest and tests. Checking Response and request wrapping during creation**


[7996d30f9977393](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7996d30f9977393) YamStranger *2016-03-22 17:26:55*

**feat: implemented wrapping during initialisation of request and response using methods given,when,expect**


[90ffb4e6ca2e892](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/90ffb4e6ca2e892) YamStranger *2016-03-21 07:11:23*

**test: added tests to check wrapping during initialisation of requests and responses**


[8fb8303860637e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fb8303860637e6) YamStranger *2016-03-21 07:09:16*

**style: updated test, removed blank lines and unused imports**


[43151004ed37461](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/43151004ed37461) YamStranger *2016-03-16 07:20:03*

**updated endlines to fit project code style**


[d0f5100cdb35b8f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d0f5100cdb35b8f) YamStranger *2016-03-14 13:41:47*

**feat: implemented SerenityRest methods for setting default values**

 * Now it is possible to set simple values, that will be used in requests/responses: 
 * ``` 
 * setDefaultBasePath(&quot;/test/resources&quot;); // will be used as base path 
 * setDefaultPort(9542); 
 * setUrlEncodingEnabled(true); 
 * setDefaultRootPath(&quot;core&quot;); 
 * setDefaultSessionId(&quot;session id&quot;); 
 * setDefaultAuthentication(RestAssured.basic(login, password)); 
 * setDefaultConfig(configuration); 
 * setDefaultProxy(new ProxySpecification(&quot;exampleHost&quot;, 8095, &quot;schema&quot;)); 
 * setDefaultParser(Parser.JSON); 
 * ``` 
 * Also default request/responses can be provided to be used as base for each request/responses: 
 * ``` 
 * ResponseSpecification specification = new ResponseSpecBuilder().rootPath(&quot;parent&quot;).build(); 
 * setDefaultResponseSpecification(specification); 
 * ResponseSpecification created = given().get(url); //will use &quot;parent&quot; value as default rootPath 
 * ``` 

[a291ba1e4402dbe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a291ba1e4402dbe) YamStranger *2016-03-14 13:33:01*

**feat: added rule for configuration initialization of SerenityRest using configurtion Actions, added tests**

 * Now it will be possible to use same default configuration of SerenityRest during execution different test methods: 
 * ``` 
 * @Rule 
 * def RestConfigurationRule rule = new RestConfigurationRule( 
 * new RestConfigurationAction() { 
 * @Override 
 * void apply() { 
 * setDefaultBasePath(&quot;home/units&quot;) 
 * } 
 * }, new RestConfigurationAction() { 
 * @Override 
 * void apply() { 
 * setDefaultPort(0) 
 * } 
 * },) 
 * ``` 
 * Before test actions executed one by one, after tests SerenityRest.reset() is executed 

[24c5d3858022ddc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/24c5d3858022ddc) YamStranger *2016-03-14 13:10:28*


## v1.1.30-rc.1
### No issue

**updating changelog**


[42ceca721e3a3e5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42ceca721e3a3e5) YamStranger *2016-04-05 13:54:46*


## v1.1.29-rc.3
### No issue

**Added basic Microsoft Edge driver support**


[07762e7942397f7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/07762e7942397f7) John Smart *2016-03-29 17:38:27*

**updating changelog**


[61a1476cf533a3a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/61a1476cf533a3a) YamStranger *2016-03-28 04:40:02*


## v1.1.29-rc.2
### No issue

**Updated dependencies for the latest WebDriver version**


[4f3ce0989535bee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f3ce0989535bee) John Smart *2016-03-25 14:57:49*

**Updated to Selenium 2.53.0**


[56b027874e67ebb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/56b027874e67ebb) John Smart *2016-03-25 13:36:54*

**Made some of the tests more robust**


[550923311f59fd2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/550923311f59fd2) John Smart *2016-03-25 12:29:08*

**Added a sample test for boolean questions**


[d525c1a50a20065](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d525c1a50a20065) John Smart *2016-03-25 12:10:19*

**updating changelog**


[bad49c5a270e8d7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bad49c5a270e8d7) YamStranger *2016-03-25 12:03:35*

**Removed some Java 8-specific code**


[e1d68e698ff7b38](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e1d68e698ff7b38) John Smart *2016-03-25 11:45:01*

**Added the possiblity to provide boolean questions without a hamcrest matcher**


[dc088066a321938](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dc088066a321938) John Smart *2016-03-25 11:01:39*

**bug: taking screenhot with browser without TakesScreenshot ability**


[bd732c51c637fa1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bd732c51c637fa1) YamStranger *2016-03-23 07:35:39*

**added missed methods to check if webdriver alive**


[1c0eae5529d307a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1c0eae5529d307a) YamStranger *2016-03-23 07:27:13*

**test: added tests to check how taking screenshorts works with died browsers**


[89e9512d61ebfa1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89e9512d61ebfa1) YamStranger *2016-03-23 07:24:20*

**feat: added checking if browser alive before taking screenshot or saving page-source**


[240b93be5bd79a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/240b93be5bd79a8) YamStranger *2016-03-23 07:21:43*

**Remove spurrious warning messages during stack trace analysis.**


[a1de78afa65cb88](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a1de78afa65cb88) John Smart *2016-03-17 11:53:50*

**fix: updated closing web driver when appium is used, it seems that window handles still does not implemented for Android devices, only for iOS**


[a505bd9bcc18bde](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a505bd9bcc18bde) YamStranger *2016-03-16 07:07:26*

**updating changelog**


[da6182271ea1329](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/da6182271ea1329) YamStranger *2016-03-14 05:34:12*

**fix: When using SerenityParameterizedRunner tests there was a need to add @Managed WebDriver in order to see the examples table. This is due to TestClassRunnerForInstanciatedTestCase not overriding initListeners**


[a4d6b324a8c8097](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a4d6b324a8c8097) Jordan Bragg *2016-03-11 18:28:29*

**Remove spurrious warning messages during stack trace analysis.**


[3b3caad641fe25f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3b3caad641fe25f) John Smart *2016-03-11 10:30:04*

**added cheking array length**


[879a794fae661c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/879a794fae661c5) YamStranger *2016-03-10 09:09:10*

**updated test, added missed method**


[59c3a42156cf186](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/59c3a42156cf186) YamStranger *2016-03-10 08:40:41*

**fix: updated scenario data driven steps processing for report - now report will contains steps as from first scenario**


[64174370c98b6db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/64174370c98b6db) YamStranger *2016-03-10 07:57:56*

**test: refactored test for Qualifier**


[6aefe6917efc7ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6aefe6917efc7ab) YamStranger *2016-03-10 06:30:29*

**test: updated name of test method**


[e6e665143f90393](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e6e665143f90393) YamStranger *2016-03-10 06:26:44*

**added qualifiedTestImplementation**


[30d44ccbe4d3a2f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30d44ccbe4d3a2f) YamStranger *2016-03-09 15:20:55*

**fix: updated processing of @Qualifier tag in junit tests with data tables. Now it is possible add short description to steps based on parameters value**


[71102acf032fd2c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/71102acf032fd2c) YamStranger *2016-03-09 14:54:51*

**Ensure that elements are visible before manipulating them with the Screenplay actions.**


[0f64dc1a2affa03](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0f64dc1a2affa03) John Smart *2016-03-09 09:08:22*

**chore: created gradle build config for smoketests to execute them against latest serenty core**

 * If build of smoketests will be run with `./gradlew clean test aggregate` - before build all version tags will be loaded and latest will be used as serenity-core version. 

[bf6d91e2f403ea4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf6d91e2f403ea4) YamStranger *2016-03-09 06:33:53*

**Moving definition of reportDirectory in order to allow easy configuration through the serenity block. Currently this directory gets set when applying the plugin, which makes it only possible to change through setting an environment variable at the same level as applying the plugin. For multi-module projects with compile dependencies, this does not work**


[9429532576a50ad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9429532576a50ad) Jordan Bragg *2016-01-26 17:57:19*


## v1.1.29-rc.1
### No issue

**updating changelog**


[4f31025adb90677](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f31025adb90677) YamStranger *2016-03-02 17:32:13*

**fix: updated moving files. Not tmp files of reports will be moved only after stream will be closed**


[0691998f552206e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0691998f552206e) YamStranger *2016-03-02 17:05:51*

**chore: updated appium java client to 3.3.0**


[3cacf176fa0d2dd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3cacf176fa0d2dd) YamStranger *2016-03-02 15:16:00*

**appium.app is no longer required if appium.browserName is supplied**


[7cfbcfdf9002cea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7cfbcfdf9002cea) Harry King *2016-03-02 14:08:18*

**feat: updated processing of names**

 * If mothod contains CSV JSON XML this abbreviations will not be changed 
 * `TestCaseForCSVFormat` will be transformed to `Test case for CSV format` 

[bf3fa9a3d87560e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf3fa9a3d87560e) YamStranger *2016-03-02 12:00:28*


## v1.1.28-rc.1
### GitHub #321 

**test: added test for fix of Exception/assertion message in serenity report for #321**


[5fdbaa99ea3d8b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5fdbaa99ea3d8b6) Robert Zimmerann *2016-03-01 09:15:29*

**fix: included in serenity report Exception/assertion message for #321**


[f6fd88055f428df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f6fd88055f428df) Robert Zimmerann *2016-03-01 09:14:05*


### No issue

**test: redusing resource usage during testing of darkroom**


[1a6f15809e451f0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a6f15809e451f0) YamStranger *2016-03-01 09:56:09*

**updated error handling in test**


[28908c9c14e1841](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28908c9c14e1841) YamStranger *2016-03-01 09:30:18*

**test: Darkroom can be used in parallel**


[1c84dd4f4a7b27a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1c84dd4f4a7b27a) YamStranger *2016-03-01 09:09:22*

**fix: updated test to fail if darkroom fail in parallel screenshot taking**


[5f7e414fd31985e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f7e414fd31985e) YamStranger *2016-03-01 07:48:33*

**updating changelog**


[b6b3c3449cf0e6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6b3c3449cf0e6b) YamStranger *2016-03-01 07:07:30*


## v1.1.27-rc.1
### No issue

**fix: updated returned file to use generated file**


[80d82d0461688f5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80d82d0461688f5) YamStranger *2016-02-29 10:57:37*

**feat: updated report generation to use atomic operations**


[1314d42bbd0fd09](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1314d42bbd0fd09) YamStranger *2016-02-29 10:26:21*

**fix: updated report generation to use temp files with random names**


[fa256947a31f6ee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fa256947a31f6ee) YamStranger *2016-02-29 08:36:29*

**fix: updated report generation to use temp files, it fixes bugs with running tests with multiple workers (and different Java Runtime as well)**


[5afea7264123c2b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5afea7264123c2b) YamStranger *2016-02-29 08:33:00*

**feat: updated method to pring exception ifit will appear**


[d9e9c80a2db830c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9e9c80a2db830c) YamStranger *2016-02-26 09:40:45*

**updated test to check ReportLoadingFailedError**


[b6e99e00c58bd44](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6e99e00c58bd44) YamStranger *2016-02-26 08:46:21*

**feat: test updated for reporter and loader - same testoutcoume should be writed only once in report dirrectory**


[f91e7b346093be7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f91e7b346093be7) YamStranger *2016-02-26 08:30:18*

**fix: updated report loading and generating code and added test to be sure that all can be run concurrently**


[e0200f274f3c578](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0200f274f3c578) YamStranger *2016-02-25 13:31:29*

**Added the Open.browserOn(somePage) method**

 * Feature suggested by @jan-molak. 

[db2848abf2bec5b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/db2848abf2bec5b) John Smart *2016-02-23 23:26:46*

**feat: updated jbehave to 4.0.5**


[ab632f4de53980f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ab632f4de53980f) YamStranger *2016-02-23 16:04:23*

**perf: updated checking of empty string to use StringUtils**


[3fe8573b0f5a7f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3fe8573b0f5a7f9) YamStranger *2016-02-18 19:44:36*

**docs: updating contributing docs**


[82f8953051af929](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/82f8953051af929) YamStranger *2016-02-18 18:42:21*

**Updated Selenium to 2.52.0**


[60d1a4fbad5c31a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60d1a4fbad5c31a) John Smart *2016-02-18 14:38:32*

**Improved the renderening of EventualConsequence**

 * EventualConsequences were not rendering correctly in the reports. 
 * Thanks to @antonymarcano for steering this solution with the idea of an EventualConsequence where eventually() wraps the consequence, accepting a number of failures before returning. 

[28efecc2ab74246](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28efecc2ab74246) John Smart *2016-02-18 14:38:21*

**updating changelog**


[39fd6f97f8ea5b1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39fd6f97f8ea5b1) YamStranger *2016-02-18 14:23:51*

**Adding a line break for formatting.**


[cf88c486f30db8a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cf88c486f30db8a) Marek *2016-02-18 02:52:41*

**Added a default value incase fileName is null.**


[f9ba6e3ed041c5f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f9ba6e3ed041c5f) Marek *2016-02-18 02:52:23*

**doc: filtered revert and merge commits and pullrequests**


[d01538c9936826a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d01538c9936826a) YamStranger *2016-02-17 17:50:54*

**doc: updated title in release notes**


[ffdda0622ae46a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ffdda0622ae46a0) YamStranger *2016-02-17 17:22:00*

**doc: updated name of release notes**


[455902c550aa4ee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/455902c550aa4ee) YamStranger *2016-02-17 17:20:41*

**doc: updated changelog**


[c6fd574d7929aa7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c6fd574d7929aa7) YamStranger *2016-02-17 17:15:08*


## v1.1.26-rc.4
### No issue

**Refactored some unit tests to check the fix for the Saucelinks link problem properly**


[bc1a2848e81e56c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc1a2848e81e56c) John Smart *2016-02-15 11:18:55*

**Made the Cucumber support more robust**

 * The parser will no longer crash if there are empty or badly-formed feature files. 

[c80004c26a41faa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c80004c26a41faa) John Smart *2016-02-15 10:49:30*

**Minor bug fix with the Saucelabs video icon**

 * Minor bug fix where the Saucelabs video icon was incorrectly displayed. 

[7200f4b42c1dc8d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7200f4b42c1dc8d) John Smart *2016-02-15 10:48:47*


## v1.1.26-rc.3
### No issue

**Allow more elegant waits in the Screenplay module**

 * You can now write code like this: 
 * jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(10)))) 
 * This will not fail if the matcher cannot be evaluated the first time, but will retry up to a maximum of &#39;serenity.timouts&#39; seconds (5 by default). 

[433b732734f4eaa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/433b732734f4eaa) John Smart *2016-02-14 00:08:45*

**Corrected an error in the module names**


[e0c22637eccbd6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0c22637eccbd6e) John Smart *2016-02-13 12:25:07*


## v1.1.26-rc.2
### No issue

**Use the correct name for the screenplay library for this version**


[0e08c8c86f76f9c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0e08c8c86f76f9c) John Smart *2016-02-13 11:26:11*

**style: updating test style**


[9d5fb9e53e2a7c7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d5fb9e53e2a7c7) YamStranger *2016-02-10 15:23:47*

**fix: updating version of serenityc-core and maven-plugin**


[91c32382040967d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91c32382040967d) YamStranger *2016-02-10 14:34:25*

**fix: updating logging for serenity gradle plugin, using simple out stream**


[6f4f5d461d969c0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f4f5d461d969c0) YamStranger *2016-02-10 13:31:56*

**fix: updating logging for serenity gradle plugin**


[0340f82dcb76e3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0340f82dcb76e3c) YamStranger *2016-02-10 13:26:54*

**fix: updating gradle plugin to work with new configuration**


[412657bb56c8740](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/412657bb56c8740) YamStranger *2016-02-10 13:24:01*

**fix: updating requirements directory to be able work with multimodule projects**


[ffdc3efce06074d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ffdc3efce06074d) YamStranger *2016-02-10 09:57:47*

**fix: updated gradle plugin to work with multimodule projects**


[cbc92cba6fc8792](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbc92cba6fc8792) YamStranger *2016-02-09 10:03:53*

**fix: updating getSessionId method to get session id without init new webdriver**


[4f1581ce6369aaa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f1581ce6369aaa) YamStranger *2016-02-08 12:58:35*

**style: updated name of test method**


[d9c1e6a65a28bb8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9c1e6a65a28bb8) YamStranger *2016-02-08 12:49:38*

**fix: remote driver session id can be under proxied driver**


[bf8fca33efc9aa0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf8fca33efc9aa0) YamStranger *2016-02-08 12:23:42*

**Renamed serenity-journey to serenity-screenplay**

 * Also allow conditional tasks of the following form: 
 * dana.attemptsTo( 
 * Check.whether(cost&gt;100) 
 * .andIfSo(purchaseAPear) 
 * .otherwise(purchaseAnApple) 
 * ); 
 * Or with a Question&lt;Boolean&gt;: 
 * dana.attemptsTo( 
 * Check.whether(itIsTooExpensive) 
 * .andIfSo(purchaseAPear) 
 * .otherwise(purchaseAnApple) 
 * ); 

[909f21a66d8f732](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/909f21a66d8f732) John Smart *2016-02-08 11:02:27*

**chore: added gitattributes**


[720c516a2a126fb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/720c516a2a126fb) YamStranger *2016-02-06 15:43:45*

**Updated smoke tests**


[862b790b16d85ac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/862b790b16d85ac) John Smart *2016-02-06 09:25:48*

**Minor refactoring**


[612401d19781815](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/612401d19781815) John Smart *2016-02-06 09:11:30*

**Actors can now perform tasks conditionally**

 * Use the Unless class static methods and a bolean expression, e.g. 
 * ``` 
 * Unless.the(items.isEmpty(), AddTodoItems.called(items)) 
 * ``` 
 * or use a question of type Question&lt;Boolean&gt;: 
 * ``` 
 * Unless.the(itemsListisEmpty(), AddTodoItems.called(items)) 
 * ``` 

[c9572c849d9e710](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9572c849d9e710) John Smart *2016-02-06 09:10:09*

**Actors can now perform tasks conditionally**

 * Use the Unless class static methods and a bolean expression, e.g. 
 * ``` 
 * Unless.the(items.isEmpty(), AddTodoItems.called(items)) 
 * ``` 
 * or use a question of type Question&lt;Boolean&gt;: 
 * ``` 
 * Unless.the(itemsListisEmpty(), AddTodoItems.called(items)) 
 * ``` 

[3e0d99168dbec1a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e0d99168dbec1a) John Smart *2016-02-06 09:09:32*


## v1.1.26-rc.1
### No issue

**Revert "Git Attributes Experiment, please don't merge"**


[91aee6a29aae70e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91aee6a29aae70e) Yaroslav *2016-02-06 15:40:06*

**Revert "Updating gitattributes not to update chromedriver and woff files"**


[0dcba83994ed144](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0dcba83994ed144) Yaroslav *2016-02-06 15:39:17*

**fix: updated gitattributes**


[e71056d57767f35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e71056d57767f35) YamStranger *2016-02-06 12:42:31*

**chore: updated gitattributes**


[b98f19f058c8035](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b98f19f058c8035) YamStranger *2016-02-06 12:00:33*

**Revert "Git Attributes Experiment, please don't merge"**


[5236c58bf846648](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5236c58bf846648) Yaroslav *2016-02-06 11:58:51*

**fix: fixed nullpointer if json config does not exists**


[c6122c8a4976149](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c6122c8a4976149) YamStranger *2016-02-06 11:35:27*

**fix: restored gitattributes**


[ea5f1adc05581fc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ea5f1adc05581fc) YamStranger *2016-02-05 06:13:59*


## v1.1.25-rc.7
### No issue

**Made reading UI values more fluent.**

 * The narrative was interrupted by the .value() so hidden away for now behind a more fluent method 

[5cb49b2e6ce64c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5cb49b2e6ce64c5) John Smart *2016-02-05 17:44:16*

**Support for multiple matchers in Consequences**

 * You can now make multiple assertions against a single question 

[c28a95ec9310748](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c28a95ec9310748) John Smart *2016-02-05 17:41:33*


## v1.1.25-rc.6
### No issue

**Restored a renamed method to maintain backward compatibility.**


[4b1537ede322502](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b1537ede322502) John Smart *2016-02-04 23:23:12*

**fix: updated commons-collection for jira/jbehave/cucumber modules**


[9e66e4bd6f59659](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e66e4bd6f59659) YamStranger *2016-02-04 21:36:54*


## v1.1.25-rc.5
### No issue

**Refactoring and performance improvements**


[c4a8fc16ec943ca](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c4a8fc16ec943ca) John Smart *2016-02-04 15:06:06*

**Tests can now manage whether cookies should be cleared between each test**


[2e2450a32770d5e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2e2450a32770d5e) John Smart *2016-02-04 15:05:45*


## v1.1.25-rc.4
### GitHub #255 

**Fixed #255**


[0a9e50ae5782ba9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a9e50ae5782ba9) John Smart *2016-02-03 17:49:29*


### GitHub #281 

**Fixed issue #281**

 * During verbose logging, Serenity tried to read the tag from web elements. This could cause failures if the element was stale or unavailable when the logging happen. This has now been changed to log the locator and not the element tag type. 

[4531d43df845228](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4531d43df845228) John Smart *2016-02-03 11:24:06*


### No issue

**style: update test style**


[95449fed05268e1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/95449fed05268e1) YamStranger *2016-02-04 01:05:24*

**style: test updated**


[4e495f56444915a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e495f56444915a) YamStranger *2016-02-04 00:56:52*

**fix: update charset usage during reading/writing**


[85e23de89036021](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/85e23de89036021) YamStranger *2016-02-04 00:56:06*

**fix: updated report template generation**


[194f3d966d78014](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/194f3d966d78014) YamStranger *2016-02-04 00:56:06*

**fix: selenium version upgrade to 2.50.1**


[3e864ae7bfeef59](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e864ae7bfeef59) YamStranger *2016-02-04 00:55:45*

**fix: updating report engine to wait results of report generation, stream and readers closing**


[4858e0b156973d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4858e0b156973d1) YamStranger *2016-02-04 00:06:35*

**Hardened some of the integration tests**


[ba8fcee2574ce65](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba8fcee2574ce65) John Smart *2016-02-03 14:49:01*

**Attempt to make some of the tests more robust.**


[e772cd1b4d1773d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e772cd1b4d1773d) John Smart *2016-02-03 14:12:13*

**Removed the .gitattribues file from git as it causes problems with the build pipeline on Snap-CI**


[1b3aa6568c4a004](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b3aa6568c4a004) John Smart *2016-02-03 13:46:48*

**Added an Action class to scroll to a particular eleemtn on the screen.**


[9f1f6b5c05f6c1b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f1f6b5c05f6c1b) John Smart *2016-02-03 09:58:31*

**fix: updated processing of "browserstack.os.version" and "browserstack.browser.version" system properties according to latest changes on BrowserStack side**


[123e26d0bda8d05](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/123e26d0bda8d05) ovenal *2016-02-02 15:11:35*

**style: updated test**


[e96d512758d1846](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e96d512758d1846) YamStranger *2016-02-01 10:13:30*

**chore: updating gitignore**


[9eb93903d47414a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9eb93903d47414a) YamStranger *2016-02-01 09:29:01*

**chore: updated wrapper, and build publishing libs**


[737b1aaf46a94ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/737b1aaf46a94ab) YamStranger *2016-02-01 09:18:28*

**chore: updated wrapper, and build publishing libs**


[55b06c1ce3ecc50](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/55b06c1ce3ecc50) YamStranger *2016-02-01 09:10:59*

**Updated to Seleniy, 2.49.1**


[ed33d6a045ffeed](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ed33d6a045ffeed) John Smart *2016-01-26 11:43:37*


## v1.1.25-rc.3
### No issue

**added a test to check the test report output; updated previously failed tests for customized step title**


[adc66f3bd6142af](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/adc66f3bd6142af) ovenal *2016-01-22 12:48:29*

**updated existing tests after changes in ExecutedStepDescription class**


[af3562dcf668650](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af3562dcf668650) ovenal *2016-01-22 11:21:30*

**added the tests to cover storing arguments list in ExecutedStepDescription class**


[e25d54860c48130](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e25d54860c48130) ovenal *2016-01-22 10:32:24*

**fix: customized step title if some parameter contains comma character**


[9d046220442e6d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d046220442e6d6) ovenal *2016-01-22 10:18:32*

**delted maven repo from build.gradle**


[864c00c9d01ce7b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/864c00c9d01ce7b) slawert *2016-01-22 09:49:27*

**Browsermob update: using browsermob-core-littleproxy instead of old browsermob-proxy**


[42be3ba710578c2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42be3ba710578c2) slawert *2016-01-22 09:45:42*

**chore: turn off parallel execution of submodules**


[c9c9e1b8a682ba3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9c9e1b8a682ba3) YamStranger *2016-01-20 16:20:34*

**chore: updated org.gradle.workers.max value to reduce memory usage during build**


[12b5943d2f74407](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/12b5943d2f74407) YamStranger *2016-01-20 14:57:36*

**chore: updated org.gradle.workers.max value to reduce memory usage during build**


[9d6e27dcb4f231d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d6e27dcb4f231d) YamStranger *2016-01-20 14:50:32*

**chore: updated org.gradle.workers.max value to reduce memory usage during build**


[418d37ca898fe72](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/418d37ca898fe72) YamStranger *2016-01-20 14:41:50*

**chore: updated build to enable paralell build**


[0a8a1856c6c06b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a8a1856c6c06b9) YamStranger *2016-01-20 11:12:45*

**fix: updated plugin to get serenity.properties from current module build dir**


[f973f15b6c89d2e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f973f15b6c89d2e) YamStranger *2016-01-20 09:58:46*

**fix: serenity.properties can be located not in workin dir, but in gradle/maven module folder**


[89eebf56d85309e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89eebf56d85309e) YamStranger *2016-01-20 09:58:46*

**fix: updated resolution of output dir based on gradle/maven module**


[530450bb5c94b71](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/530450bb5c94b71) YamStranger *2016-01-20 09:58:46*

**fix: updated build task dependecies**


[246246020826f03](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/246246020826f03) YamStranger *2016-01-20 09:58:46*

**chore: build test parallel execution enabled (PerCore)**


[c47b69d154d32aa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c47b69d154d32aa) YamStranger *2016-01-20 09:58:45*

**fix: report generation for multimodule builds**


[36225a5908e8b2c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36225a5908e8b2c) YamStranger *2016-01-19 19:14:38*

**Fine-tuned the soft-assert tests and minor reporting bug  fix.**


[60b1b99079044a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60b1b99079044a0) John Smart *2016-01-14 23:07:32*


## v1.1.25-rc.2
### No issue

**Added support for By locators in Target objects and Action classes.**


[511f6079b8eb17d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/511f6079b8eb17d) John Smart *2016-01-14 18:42:32*

**Updated smoketests**


[e2ea2ea4999fe72](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e2ea2ea4999fe72) John Smart *2016-01-13 23:31:29*

**Added support for By locators in Target objects and Action classes.**


[2b2d49d1ac787ac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2b2d49d1ac787ac) John Smart *2016-01-13 22:44:16*


## v1.1.25-rc.1
### GitHub #243 

**Revert "#243 Upgrading typesafe.config from 1.2 to 1.3"**


[05ac6bd4c911b89](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/05ac6bd4c911b89) Yaroslav *2016-01-13 12:34:35*


### No issue

**Updated smoke test dependencies**


[1f0dba14b3c4c55](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1f0dba14b3c4c55) John Smart *2016-01-13 14:31:25*

**Removed a redundant test**


[7caec064fd2d81d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7caec064fd2d81d) John Smart *2016-01-13 14:03:33*

**Multiple assertions in the same should() method are now treated as "soft" asserts.**


[c7bcd9643a1d3b7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c7bcd9643a1d3b7) John Smart *2016-01-13 12:55:52*

**style: changed style of one test**


[5f78a7b2ca9d63e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f78a7b2ca9d63e) YamStranger *2016-01-12 21:22:53*

**fix: update serenity-gradle-plugin to use same Configuration as Tests**


[5db32dfbc9fba66](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5db32dfbc9fba66) YamStranger *2016-01-12 19:00:18*

**fix: aggregation report generation in gradle plugin**


[a610239496c0955](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a610239496c0955) YamStranger *2016-01-12 17:23:31*

**chore: upgrade groovy from 2.3.* to 2.4.4**


[b944867051253bd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b944867051253bd) YamStranger *2016-01-12 16:25:33*

**fix: move reports about configuration to specific folder**


[a60d52606dfc710](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a60d52606dfc710) YamStranger *2016-01-12 16:01:44*

**chore: gradle take version from local variable**


[5d17bd44a9869b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d17bd44a9869b0) YamStranger *2016-01-12 15:50:19*

**chore: gradle to 2.10 and groovy to 2.4.4 upgraded**


[d5b5401f3adfc8f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5b5401f3adfc8f) YamStranger *2016-01-12 15:46:20*

**fix: report with properties should be in json**


[777c06110da4f2a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/777c06110da4f2a) YamStranger *2016-01-12 13:42:26*

**fix: report with properties should be in report folder**


[9e412bfeb9d5839](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e412bfeb9d5839) YamStranger *2016-01-12 13:29:11*

**chore: added report for configuration, with actual properties**


[8efe039bbb25754](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8efe039bbb25754) YamStranger *2016-01-12 13:12:57*


## v1.1.24
### No issue

**Improved exception reporting**


[32aba8a8a1676a7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/32aba8a8a1676a7) John Smart *2016-01-10 21:48:50*

**Improved exception reporting**


[b5a6c975fb31e82](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5a6c975fb31e82) John Smart *2016-01-10 19:56:47*

**Added matchers to allow questions about web element states (designed mainly to be used for low-level preconditions or assertions), e.g.**

 * then(dana).should(seeThat(the(ProfilePage.NAME_FIELD), isVisible())); 

[f36115c4978f460](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f36115c4978f460) John Smart *2016-01-08 10:41:28*

**Improved reporting of customised error messages in consequences**


[198a036ac2e3ce4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/198a036ac2e3ce4) John Smart *2016-01-08 10:40:01*

**docs: adding instructions of contributors**


[1dbda1aec900a98](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1dbda1aec900a98) YamStranger *2016-01-08 09:56:48*

**chore: upgrade typesafe.config to 1.3 from 1.2**


[e240e7f100216a1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e240e7f100216a1) YamStranger *2016-01-07 12:01:32*


## v1.1.22-rc.15
### No issue

**Fixed a bug in the reporting of Journey Pattern web actions.**


[8d6dafa79e3917e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d6dafa79e3917e) John Smart *2016-01-05 14:12:38*

**When using a unique browser for multiple tests, clear the cookies and HTML local storage between tests.**


[594460938c80423](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/594460938c80423) John Smart *2015-12-29 09:35:55*

**Improved the reporting of Journey pattern by removing redundant "is" clauses generated by the Hamcrest matchers.**


[c200b3262d83a1d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c200b3262d83a1d) John Smart *2015-12-29 09:34:58*


## v1.1.22-rc.14
### No issue

**Added the Evaluate action and the JavaScript question to perform JavaScript queries.**


[b6586774ee26b6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b6586774ee26b6e) John Smart *2015-12-28 11:31:41*

**Updated the smoke tests.**


[ef0e61af836455a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef0e61af836455a) John Smart *2015-12-28 09:55:17*

**The Target class now accepts a prefix notation to specify the locator, e.g Target.the("name field").locatedBy("css:#name") or Target.the("name field").locatedBy("id:name")**

 * This supports all of the WebDriver locator strategies: 
 * - id 
 * - css 
 * - xpath 
 * - name 
 * - tagName 
 * - className 
 * - linkText 
 * - partialLinkText 

[a700aa2d0654491](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a700aa2d0654491) John Smart *2015-12-28 09:54:46*

**Refactored a journey pattern test to illustrate the displays matcher**


[e8d86a9b1562f22](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e8d86a9b1562f22) John Smart *2015-12-28 09:08:43*

**Refactored the Journey Pattern code**


[182dfa03c9c217c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/182dfa03c9c217c) John Smart *2015-12-28 09:05:14*

**Refactored the Enter action to allow entering text and keys in the same action**


[e9610ed6fc8e761](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e9610ed6fc8e761) John Smart *2015-12-26 20:42:45*


## v1.1.22-rc.13
### No issue

**Trying to fixe a performance issue related to resource copying**


[7e41d67d6acaf5d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7e41d67d6acaf5d) John Smart *2015-12-25 21:42:17*

**Added a method to the WebDriverManager instance to retreive a named webdriver instance.**


[00615130588c8b2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/00615130588c8b2) John Smart *2015-12-25 19:57:00*

**Fixed a bug that reported a misleading "class cast exception" when the moveTo() method was called after a test failure.**


[eec89ad0c6d2488](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eec89ad0c6d2488) John Smart *2015-12-25 19:56:22*

**Added the ability to use the Serenity WebDriver API directly in Action classes, by extending the WebAction class.**


[8dad9ccfa05c76e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8dad9ccfa05c76e) John Smart *2015-12-25 19:53:45*

**Fixed a bug where enums did not appear correctly in the test reports when they were referenced by Journey Pattern Questions.**


[5fdf07c2b07a731](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5fdf07c2b07a731) John Smart *2015-12-23 12:42:17*

**It is now possible to add page objects as member variables in Performable or Question classes - they will be correctly configured with the WebDriver instance associated with the actor.**


[edcdc4ad932fc62](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/edcdc4ad932fc62) John Smart *2015-12-23 12:04:17*

**Refactored the bundled Journey Pattern action classes.**


[c8261771133d28e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c8261771133d28e) John Smart *2015-12-23 12:02:57*


## v1.1.22-rc.12
### Jira api-2 

**217_issue: removed dependency org.mortbay.jetty:servlet-api-2.5:6.1.9, it is duplicated with javax.servlet:javax.servlet-api:3.1.0**


[9c8f3b20036cdb6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9c8f3b20036cdb6) YamStranger *2015-12-07 08:31:54*


### No issue

**Updated smoketests to refactored journey pattern**


[32282fee3f80aaf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/32282fee3f80aaf) John Smart *2015-12-21 15:20:43*

**Readability improvements and moved the UI Action classes into their own package.**


[c6803e834640e9d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c6803e834640e9d) John Smart *2015-12-20 18:59:28*

**223_issue: reloading result dir**


[4c9a1b1ec71d248](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c9a1b1ec71d248) YamStranger *2015-12-09 19:13:41*

**223_issue: reverting updating of some imports**


[8d42d0d2f40dea5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d42d0d2f40dea5) YamStranger *2015-12-07 11:25:33*

**Revert "Pull request for updating SerenityRest to log all types of input"**


[994da12a7d2a331](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/994da12a7d2a331) Yaroslav *2015-12-07 10:33:35*

**223_issue: added reloading output dir for tests**


[54721c4a11479df](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/54721c4a11479df) YamStranger *2015-12-07 09:51:18*

**217_issue: style fix**


[c46729fb285850d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c46729fb285850d) YamStranger *2015-12-07 09:38:20*

**217_issue: removed old and never updated files**


[0e4b788b99f6a3a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0e4b788b99f6a3a) YamStranger *2015-12-07 07:47:52*


## v1.1.22-rc.11
### No issue

**216_issue: update versions**


[86ff95a0f343746](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/86ff95a0f343746) YamStranger *2015-12-06 22:54:40*

**218_issue: added test for checking if web scenarious executed successfully with HTMLUnit (fails now, so added @Ignore)**


[c44f078c170cb79](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c44f078c170cb79) YamStranger *2015-12-06 22:47:55*

**216_issue: update versions**


[5f8752f8da66e0b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f8752f8da66e0b) YamStranger *2015-12-05 12:07:31*

**185_issue: log and auth wrappers implemented, tests profivided. redirects still not supported**


[f998b4a55e25fe8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f998b4a55e25fe8) YamStranger *2015-11-30 11:34:14*

**197_issue: updated SerenityRest to log all types of input for content/body rest call**


[7ab6c2502ff35f1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ab6c2502ff35f1) YamStranger *2015-11-26 19:08:58*

**fix: Fix for setting serenity.proxy.type and http_port. Needs to be an number instead of string.**


[2cec6f26c6f2f87](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2cec6f26c6f2f87) Kishen Simbhoedatpanday *2015-11-26 18:54:06*

**fix: cglib dependency conflict from guice**


[7c4e0df97b5bb04](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7c4e0df97b5bb04) Ben Scott *2015-11-26 06:54:01*


## v1.1.22-rc.10
### No issue

**Updated tests**


[e21e9a5c18b2e3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e21e9a5c18b2e3c) John Smart *2015-11-24 23:42:28*

**Retry to unzip a resource file if it is locked. This is a work-around for Windows-related file locking issues.**


[9f9075212fc2f3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9f9075212fc2f3c) John Smart *2015-11-24 23:25:25*

**Fixed an error with the screenshots that always displayed the screen source link, even for successful tests.**


[81c87bb9040cacb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/81c87bb9040cacb) John Smart *2015-11-24 23:24:29*

**Restored step logging to INFO.**


[fc8442b03447e52](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc8442b03447e52) John Smart *2015-11-24 20:55:40*

**Added more robustness to the report generation by allowing ZIP files to be opened again if they couldn't the first time**


[c9889ca6204e9a5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9889ca6204e9a5) John Smart *2015-11-24 13:12:12*

**fix for nested cleaning resources**


[73015c29b297366](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/73015c29b297366) YamStranger *2015-11-22 22:45:30*

**Updated tests for screenshots**


[8d03dda6b41d7f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d03dda6b41d7f3) YamStranger *2015-11-22 22:09:39*

**updated Resizer. Fixed opening output and input stream in same time**


[4267bd936dc4dc3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4267bd936dc4dc3) YamStranger *2015-11-22 22:06:05*

**Updated input streams closing**


[a7bdaeb08399244](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a7bdaeb08399244) YamStranger *2015-11-22 18:06:44*

**see https://github.com/serenity-bdd/serenity-core/issues/179**


[79d266003d25a27](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/79d266003d25a27) liviu.carausu *2015-11-21 23:09:08*

**184_issue: test added**


[7ebca229c0a391d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ebca229c0a391d) YamStranger *2015-11-20 01:49:15*

**184_issue: logging for PATCH operation added**


[b5e520997b94138](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5e520997b94138) YamStranger *2015-11-19 23:31:03*


## v1.1.22-rc.9
### No issue

**Record screen source code for failing tests.**


[930f34cb355c189](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/930f34cb355c189) John Smart *2015-11-23 00:33:33*


## v1.1.22-rc.8
### No issue

**Set the serenity.console.colors property to true to get ANSI colors in the console output (don't use on Jenkins).**


[552172c80ce7608](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/552172c80ce7608) John Smart *2015-11-19 20:53:09*


## v1.1.22-rc.7
### No issue

**Fixed a bug that prevented @Pending annotations from working with non-instrumented Performable objects**


[d4791cdd90311c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d4791cdd90311c5) John Smart *2015-11-19 10:53:24*


## v1.1.22-rc.6
### No issue

**Made the console logging colors a bit lighter for better readability**


[283093853e20adf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/283093853e20adf) John Smart *2015-11-19 04:40:19*

**Updated unit tests**


[e7d7c85df2489c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7d7c85df2489c1) John Smart *2015-11-19 04:08:12*

**Added color to logs**


[5099debaaa78053](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5099debaaa78053) John Smart *2015-11-19 04:01:56*

**Added color to logs**


[93b1411da124872](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93b1411da124872) John Smart *2015-11-19 04:00:01*

**Improved console log messages to cater for errors and failed assumptions**


[8846f6e32637978](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8846f6e32637978) John Smart *2015-11-19 03:01:04*

**Updated unit tests**


[e329275237f7aa4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e329275237f7aa4) John Smart *2015-11-19 00:46:13*

**Improved reporting**


[8a21a9f2ca827b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8a21a9f2ca827b9) John Smart *2015-11-18 21:49:48*

**188_issue: new level of take screenshots configuration added**


[4f189ca2db7b989](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f189ca2db7b989) YamStranger *2015-11-18 20:27:52*

**Improved reporting**


[42691e1efb02790](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42691e1efb02790) John Smart *2015-11-18 10:50:19*

**Minor Base Step Listener Constructor update**


[597960b30fe36a4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/597960b30fe36a4) YamStranger *2015-11-18 08:10:24*

**179_issue: added tests and fix for issue**


[1af09f3f475ab89](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1af09f3f475ab89) YamStranger *2015-11-17 11:25:39*

**Photographer cleanup fix. If driver not initialized - nothing to clean**


[5b77f9ac5c51742](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b77f9ac5c51742) YamStranger *2015-11-17 09:01:45*

**Remove an unnecessary moveTo() operation.**

 * This seems to cause class cast exceptions from time to time. 

[ece74d2b38035f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ece74d2b38035f3) John Ferguson Smart *2015-11-14 19:00:02*

**130_issue: added system configuration for output dirrectory**


[2d39575adf3fa35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d39575adf3fa35) YamStranger *2015-11-13 19:47:07*

**130_issue: added system environment properties to configuration**


[ef3ca8d84926738](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef3ca8d84926738) YamStranger *2015-11-13 19:23:29*

**Reduce noise in the logs by removing an superfluous error message.**


[bc6e1f9ea2d8fd3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc6e1f9ea2d8fd3) John Ferguson Smart *2015-11-13 15:48:42*

**fix build fail by updating test-outcomes.ftl**


[0a1679f36c02583](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a1679f36c02583) YamStranger *2015-11-13 15:31:25*

**130_issue: reading serenity.properties fix**


[26219f70f9f5905](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26219f70f9f5905) YamStranger *2015-11-13 14:53:29*


## v1.1.22-rc.4
### No issue

**Minor improvement to assertion reporting, to avoid lines being hidden for some assertions**


[4c20cd5f39da75b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c20cd5f39da75b) John Smart *2015-11-12 08:47:35*


## v1.1.22-rc.3
### No issue

**Better handling of reporting arbitrary AssertionError exceptions.**


[8edf62c3dcbf93a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8edf62c3dcbf93a) John Smart *2015-11-12 07:39:52*


## v1.1.22-rc.2
### GitHub #175 

**Fixed #175**


[6ccd53e78491ebb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ccd53e78491ebb) John Smart *2015-11-11 11:54:19*


### Jira THUCYDIDES-253 

**Having ProvidedDriver implement JavascriptExecutor should not be the correct way to fix THUCYDIDES-253. The method that checks if the driver is javascript enabled looks at the driver class returned from WebDriverFacade and in the case, it will see that ProvidedDriver implements JavascriptExecutor but when it tries to execute javascript on the proxied driver that does not necessarily have to implement JavascriptExecutor, then it will throw a method not found exception. This proposed fix checks if the driverclass in the WebDriverFacade is a provided driver, if it is, then the correct driver class it should look at is contained in the proxied driver.**


[ad3af93eda7fa4a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad3af93eda7fa4a) willhuang85 *2015-10-22 08:09:11*


### No issue

**Better formatting for the result line at the bottom of the test outcome table**


[9bcc6643ff58626](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9bcc6643ff58626) John Smart *2015-11-12 06:03:02*

**Test hardening**


[a241bd85dac63aa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a241bd85dac63aa) John Smart *2015-11-12 05:50:20*

**Fine-tuned reporting**


[83496ffda0d8e41](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/83496ffda0d8e41) John Smart *2015-11-11 23:17:00*

**132_issue: desabling retries in smoke-tests**


[eb37b9b1589c016](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eb37b9b1589c016) YamStranger *2015-11-11 12:42:54*

**128_issue: fixing style**


[f3e533f87c0a853](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f3e533f87c0a853) YamStranger *2015-11-11 01:25:39*

**128_issue: updated checking Content Type according RFC1341, and added test for https rest tests based on github**


[3eba4e88493fffc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3eba4e88493fffc) YamStranger *2015-11-11 01:23:09*

**132_issue: clean task fix**


[8a671ce7f4640e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8a671ce7f4640e4) YamStranger *2015-11-10 00:45:56*

**132_issue: little refactoring, moving string to constants**


[632b09d0fb3388f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/632b09d0fb3388f) YamStranger *2015-11-10 00:45:56*

**132_issue: fixing incorrect test. Notifier should record failure**


[02b260e4116e90d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/02b260e4116e90d) YamStranger *2015-11-10 00:45:56*

**132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, fixing tests from different modules**


[dc809646f130397](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dc809646f130397) YamStranger *2015-11-10 00:45:55*

**132_issue: fixing name of menthod in reports**


[acf58d8c842dc3f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/acf58d8c842dc3f) YamStranger *2015-11-10 00:45:55*

**132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing imports**


[a798480e4e89951](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a798480e4e89951) YamStranger *2015-11-10 00:45:55*

**132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true**


[41361d014bac870](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/41361d014bac870) YamStranger *2015-11-10 00:45:55*

**132_issue: test and solution provided**


[27508e1975be3e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/27508e1975be3e4) YamStranger *2015-11-10 00:45:55*

**132_issue: updated SerenityRunner to use max.retries only if junit.retry.tests=true, removing description creating method**


[1dae0a507294270](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1dae0a507294270) YamStranger *2015-11-10 00:45:55*

**Fixing getdrivername method to take this.driverClass instead of the getter since the getter may not return a SupportedDriver anymore**


[ca0ffa080140a93](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca0ffa080140a93) willhuang85 *2015-11-03 00:36:10*

**Test that checks to see if the proxy driver class is returned when the the driver class is the provided driver**


[652c048ac985d1f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/652c048ac985d1f) willhuang85 *2015-10-22 08:41:10*

**Refactoring**


[169b5e8fd721a0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/169b5e8fd721a0e) John Smart *2015-10-21 20:02:13*

**Refactored redundant tests**


[e957030c91106c4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e957030c91106c4) John Smart *2015-10-20 09:05:23*

**Refactoring**


[a83934edd78de54](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a83934edd78de54) John Smart *2015-10-20 08:25:53*

**refactor: Corrects throwning of IOException, instead of Exception**


[0bcdbcfb461f8ce](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0bcdbcfb461f8ce) Tjeerd Verhagen *2015-10-20 08:13:38*

**Refactoring**


[ea10f238c429373](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ea10f238c429373) John Smart *2015-10-19 17:29:15*

**fix loop when parameter is null in ddt tests**


[e0b51a437d355f9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0b51a437d355f9) a.dybov *2015-10-19 11:32:50*


## v1.1.22-rc.1
### No issue

**Removed redundant test**


[1b62b2c1e4df337](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b62b2c1e4df337) John Smart *2015-11-09 20:41:35*

**Updated a unit test**


[3e05bb0b83686c9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3e05bb0b83686c9) John Smart *2015-11-09 20:27:38*

**Improved reporting**

 * Add the &#39;serenity.linked.tags&#39; property, which allows you to defined tag types which will result in human-readable tags that can be used as bookmarks or external links. 

[3794e2b28ed858c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3794e2b28ed858c) John Smart *2015-11-09 20:19:57*

**Updated versions in the smoketests**


[73946ec52a54f9c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/73946ec52a54f9c) John Smart *2015-11-09 18:05:44*

**Made the WebdriverManager publicly available for advanced use cases.**


[7a1c66f46acc6d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a1c66f46acc6d4) John Smart *2015-11-09 17:22:58*


## v1.1.21-rc.1
### No issue

**Removed an incorrect reference to a Java 8 class**


[4af8b65a5867895](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4af8b65a5867895) John Smart *2015-11-06 10:46:47*


## v1.1.20-rc.1
### No issue

**Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests**


[00d0237227e7f39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/00d0237227e7f39) John Smart *2015-11-05 15:05:08*

**Added the 'serenity.tag.failures' property, which causes Serenity to add a tag containing the error type for failing tests**


[0e777550299bd00](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0e777550299bd00) John Smart *2015-11-05 12:40:13*


## v1.1.19
### No issue

**Fixed a potential infinite loop in the report generation if image processing failed for some reason**


[ba52bc42560b4a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba52bc42560b4a0) John Smart *2015-11-04 15:27:45*


## v1.1.18-rc.2
### No issue

**Finished merge**


[89a443f17da25d0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89a443f17da25d0) John Smart *2015-11-04 10:16:05*

**Finished merge**


[785c7bedb82ce42](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/785c7bedb82ce42) John Smart *2015-11-04 09:33:28*

**Improved reporting**


[062d4daac873cb0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/062d4daac873cb0) John Smart *2015-11-04 07:46:02*

**updating tests for using ThucydesWebDriverSupport**


[f6464d1224fd8d5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f6464d1224fd8d5) YamStranger *2015-11-02 19:26:47*

**updating test to use ThucydidesWebDriverSupport**


[dcab9c63c6952de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dcab9c63c6952de) YamStranger *2015-11-02 17:59:45*

**130_issue: removing unused dependencies**


[b4bbc317f98a23a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b4bbc317f98a23a) YamStranger *2015-11-02 15:39:51*

**130_issue: fixing copying jars bug**

 * Conflicts: 
 * serenity-gradle-plugin/build.gradle 

[e28f899a152e798](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e28f899a152e798) YamStranger *2015-11-02 13:26:43*

**130_issue: removed emtpy lines**


[d2c156debf5b8d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d2c156debf5b8d1) YamStranger *2015-11-02 13:22:17*

**130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin**


[920e4cf20364baa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/920e4cf20364baa) YamStranger *2015-11-02 13:22:03*

**130_issue: spelling error fix**


[59a3cb2a96f7a39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/59a3cb2a96f7a39) root *2015-11-02 13:21:47*

**130_issue: build.config updated for simple project for serenity-gradle_plugin**


[60b4922421276c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60b4922421276c5) YamStranger *2015-11-02 13:21:26*

**130_issue: updated simple-gradle-project for serenity-gradle-plugin**


[af55a048e07a081](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/af55a048e07a081) root *2015-11-02 13:21:05*

**130_issue: updated simple-gradle-project for serenity-gradle-plugin**


[0d48d61cf44cc34](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0d48d61cf44cc34) root *2015-11-02 13:20:42*

**130_issue: added test and default project for gradle plugin**


[356fbb9f80c6789](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/356fbb9f80c6789) YamStranger *2015-11-02 13:20:14*


## v1.1.18-rc.1
### GitHub #160 

**Refactoring of the report generation code to rectify #160**


[84d095558dcd615](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/84d095558dcd615) John Smart *2015-10-31 11:37:54*


### No issue

**Revert "Merge branch 'master' of github.com:serenity-bdd/serenity-core"**

 * This reverts commit 4397786f9fd7f37cb6c2e4f00741a2343e9e4d57, reversing 
 * changes made to 84d095558dcd61554c2a0a988977bb1e9eecb71d. 

[6e91a31d3426c4d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6e91a31d3426c4d) John Smart *2015-10-31 12:10:50*

**Refactoring WebDriver integration to use the ThucydidesWebDriverSupport class**


[8fedb5437877646](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fedb5437877646) John Smart *2015-10-31 11:36:10*

**130_issue: removed emtpy lines**


[a1979bb7f938344](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a1979bb7f938344) YamStranger *2015-10-31 04:34:10*

**130_issue: fixed.  Added sample projects for testing serenity-gradle-plugin**


[e8607afe6fb9998](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e8607afe6fb9998) YamStranger *2015-10-31 04:30:29*

**130_issue: spelling error fix**


[322e572db71d9d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/322e572db71d9d6) root *2015-10-31 04:30:22*

**130_issue: build.config updated for simple project for serenity-gradle_plugin**


[28be7ba00561d2f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28be7ba00561d2f) YamStranger *2015-10-31 04:30:15*

**130_issue: updated simple-gradle-project for serenity-gradle-plugin**


[d5883dbac1b97c5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5883dbac1b97c5) root *2015-10-31 04:30:04*

**130_issue: updated simple-gradle-project for serenity-gradle-plugin**


[ee6807e845a9293](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ee6807e845a9293) root *2015-10-31 04:29:50*

**130_issue: added test and default project for gradle plugin**


[0c105044830f797](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0c105044830f797) YamStranger *2015-10-31 04:29:18*

**Fixed an issue with the reporting of pending and skipped tests**


[c66f0fe7113da4a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c66f0fe7113da4a) John Smart *2015-10-30 21:17:00*

**Fixed typo in the smoketests**


[d9eca6e184af361](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9eca6e184af361) John Smart *2015-10-29 21:57:34*

**Updating dependencies for the smoketest project**


[c497bb6b231cdb7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c497bb6b231cdb7) John Smart *2015-10-29 21:21:03*

**Fixed typo in the smoketests**


[a4dc59d0aa64b2d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a4dc59d0aa64b2d) John Smart *2015-10-29 14:23:47*

**Made the instantiation of test steps more robust, mainly for use in the Journey pattern**


[ad774592904ec3e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad774592904ec3e) John Smart *2015-10-29 09:54:25*


## v1.1.17-rc.5
### No issue

**Added the serenity.error.on, serenity.fail.on and serenity.pending.on properties to the ThucydidesSystemProperty class.**


[d2f951a2b282659](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d2f951a2b282659) John Smart *2015-10-28 08:22:23*

**You can use the serenity.pending.on property to define exceptions that will cause a test to be marked as Pending.**


[3c397299c342b15](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3c397299c342b15) John Smart *2015-10-28 08:05:02*

**Added a general solution for defining or overriding how exceptions should be reported.**


[032dbb615134963](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/032dbb615134963) John Smart *2015-10-28 07:52:48*


## v1.1.17-rc.4
### No issue

**Fixed an error in the freemarker templates.**


[5d1b871b917e0c0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d1b871b917e0c0) John Smart *2015-10-28 01:41:47*

**Added support for customising exception handling.**

 * You can now specify your own exceptions that will cause a failure by using the /serenity.fail.on/ property. You can also specify those that will cause an error using /serenity.error.on/. 

[271ffe108f5880f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/271ffe108f5880f) John Smart *2015-10-27 21:48:16*

**Improved report icons**


[b2c29a9ed0f6e51](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b2c29a9ed0f6e51) John Smart *2015-10-27 21:08:41*

**Fixed some issues related to displaying manual tests**


[c58450593b567a1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c58450593b567a1) John Smart *2015-10-27 10:54:35*

**Fixed some broken tests**


[bafaead4743dd9d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bafaead4743dd9d) John Smart *2015-10-27 09:38:16*

**Fixed some broken tests**


[2e959d4fef6356f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2e959d4fef6356f) John Smart *2015-10-27 09:20:39*

**Refactoring**


[5fdc2be5bf86270](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5fdc2be5bf86270) John Smart *2015-10-27 08:33:50*

**Trim Appium system properties**


[157c616b2f9b519](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/157c616b2f9b519) John Smart *2015-10-27 08:33:40*

**Improved reporting**

 * Use FontAwesome for more readable test result icons. 

[01a6e9d3a51cff8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01a6e9d3a51cff8) John Smart *2015-10-27 08:33:26*

**Better error/failure distinction**

 * Exceptions such as ElementShouldBeInvisibleException are now reported as failures, not errors. 

[7de1dd5ba3fe508](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7de1dd5ba3fe508) John Smart *2015-10-27 08:31:15*

**Added a more meaningful error message if a resource file cannot be copied.**


[2e0752d93e05f48](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2e0752d93e05f48) John Smart *2015-10-23 07:40:18*


## v1.1.17-rc.3
### No issue

**Refactoring**


[b16d29a29509c8d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b16d29a29509c8d) John Smart *2015-10-22 10:50:46*

**Refactoring the html resource copying code.**


[93f8e34a2747219](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93f8e34a2747219) John Smart *2015-10-22 10:07:33*

**Refining support for multi-thread report generation to avoid contention on resource files**


[4d6e9bc104b4a27](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4d6e9bc104b4a27) John Smart *2015-10-22 09:07:10*

**Fix: Do not execute remaining steps after assumption failed**


[60ef04c4b8b46f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60ef04c4b8b46f4) Frédérik Rouleau *2015-10-22 08:25:37*

**fix: correct unit test**


[93337a754212f36](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93337a754212f36) Frédérik Rouleau *2015-10-20 21:07:15*

**fix: stop further steps execution if test is suspended**


[8cff02bf05a93bf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8cff02bf05a93bf) Frédérik Rouleau *2015-10-20 13:50:25*


## v1.1.17-rc.2
### No issue

**Refactoring of a solution to avoid contention on resource JAR files.**


[13edac4e25606fa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/13edac4e25606fa) John Smart *2015-10-22 07:26:09*

**Fixed a test**


[fe753a880bb2dff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe753a880bb2dff) John Smart *2015-10-22 07:06:59*

**Ensure that HTML report resource files are only copied if they are not already present.**


[496320a0c08f381](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/496320a0c08f381) John Smart *2015-10-22 05:23:56*

**Ignore warnings if we try to save a screenshot that already exists.**


[f195e492df7618f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f195e492df7618f) John Smart *2015-10-22 05:23:10*

**Fixed a broken test**


[80dc1909929b502](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80dc1909929b502) John Smart *2015-10-22 05:22:40*


## v1.1.17-rc.1
### No issue

**The withTestDataFrom() method now accepts a list of strings as well as a CSV file.**


[64af9acd1d8d9da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/64af9acd1d8d9da) John Smart *2015-10-18 14:57:36*

**Added smoketest tags to illustrate using tags.**


[d49c8b2f488adfe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d49c8b2f488adfe) John Smart *2015-10-18 11:42:47*

**Added a new sample data-driven test to the smoke tests**


[3dddb91bd54347d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3dddb91bd54347d) John Smart *2015-10-18 10:34:11*

**Removed old screenshot processing logic**


[4f95fd346b7419e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4f95fd346b7419e) John Smart *2015-10-18 10:03:09*

**chore: General test refactoring.**


[577dacf7f777583](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/577dacf7f777583) John Smart *2015-10-18 04:39:21*

**Minor refactoring**

 * Added multi-thread testing for the screenshot pipeline, and removed misleading warnings that could happen when two threads try to save the same screenshot. 

[0b94e8daba308bc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0b94e8daba308bc) John Smart *2015-10-17 21:25:45*

**fix: Removed a potential issue where the screenshot target directory was not created correctly**


[e07f25002f7ec85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e07f25002f7ec85) John Smart *2015-10-17 07:32:36*

**chore:test hardening**


[ca53d5e75470fef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ca53d5e75470fef) John Smart *2015-10-16 20:14:35*

**Made the screenshot processing a bit more robust**


[e1525d38bca5fc9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e1525d38bca5fc9) John Smart *2015-10-16 05:14:26*

**Fine-tuned the smoke test app**


[01e59d1a7199b27](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/01e59d1a7199b27) John Smart *2015-10-16 04:23:25*

**Fine-tuned the smoke test app**


[70db76ded080214](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/70db76ded080214) John Smart *2015-10-16 01:38:23*

**Added additional tests to the smoke test suite**


[929e14da0922d4b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/929e14da0922d4b) John Smart *2015-10-15 23:10:12*

**chore:Refactoring and tidying up the code**


[2e315ec39b25021](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2e315ec39b25021) John Smart *2015-10-15 20:48:17*

**[JDK7 compatibility] Corrections to maintain JDK7 compatibility**

 * Replace usage of java Optional with Guava optional 

[2341b7409624050](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2341b7409624050) Mike Wilkes *2015-10-15 14:55:09*

**Added smoke tests**


[37aa19d2ddef10c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/37aa19d2ddef10c) John Smart *2015-10-15 07:11:27*

**fix: Handle empty screenshots without crashing.**


[1b84d2e7d7b33aa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b84d2e7d7b33aa) John Smart *2015-10-15 00:59:18*

**Added the Todo app smoke tests**


[825328a5cd76f7d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/825328a5cd76f7d) John Smart *2015-10-15 00:04:12*

**Refactored the screenshot processing logic**


[f20daf748843c6b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f20daf748843c6b) John Smart *2015-10-13 09:36:45*

**Just trigger rebuild**


[395632770ed8fc1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/395632770ed8fc1) Tjeerd Verhagen *2015-10-12 15:22:10*

**fix: Checks if driver is not null (before calling close() )**


[461c7843410b9f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/461c7843410b9f4) Tjeerd Verhagen *2015-10-07 19:59:32*

**fix: Adds JAVA_HOME to the environment (map) in case the key / value is**

 * not available from the System.getenv() 

[540ce87d44b93cb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/540ce87d44b93cb) Tjeerd Verhagen *2015-10-07 19:58:19*

**fix: Checks if the driver != null, before calling close() and quit(),**


[9e7e55695924f47](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e7e55695924f47) Tjeerd Verhagen *2015-10-07 16:05:30*

**fix: Checks if the driver != null, before calling close() and quit(),**


[7578ed2ff16cf34](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7578ed2ff16cf34) Tjeerd Verhagen *2015-10-07 15:56:39*

**refactor: Removes maven-easyb-plugin, is not used, or correct me if I'm**

 * wrong. 

[666e9dcfd8d8df3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/666e9dcfd8d8df3) Tjeerd Verhagen *2015-10-07 15:40:49*

**refactor: Removes warning that log4j was not initialized**

 * Updates thucydides-core with exclusion of log4j 
 * Adds dependency log4j-over-slf4j 

[1381ebdaa955146](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1381ebdaa955146) Tjeerd Verhagen *2015-10-07 15:33:47*

**fix: Updates default URL to 'http://www.google.com/ncr' this prevents**

 * redirects from &#39;google.com&#39; to country specific google search pages. 

[6200d4effd5470a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6200d4effd5470a) Tjeerd Verhagen *2015-10-07 09:52:02*

**fix: Corrects auto redirect to secure connection (https instead of http)**


[94cf1d57fad63a1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/94cf1d57fad63a1) Tjeerd Verhagen *2015-10-07 09:47:41*

**docs: Adds description how to correct add chrome-web-driver**


[6148fe2833f5e7f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6148fe2833f5e7f) Tjeerd Verhagen *2015-10-07 09:37:33*

**docs: Adds description about the Serenity Demo**


[365388539e0dfc3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/365388539e0dfc3) Tjeerd Verhagen *2015-10-07 09:32:29*

**chore: Adds profiles 'firefox' and 'chrome', for easier running the**

 * tests with different browsers. 

[5556ddae9469a43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5556ddae9469a43) Tjeerd Verhagen *2015-10-07 09:00:51*

**fix: Corrects issue with auto redirect to secure connection (https**

 * instead of http) 

[a860b0bb5f3b58f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a860b0bb5f3b58f) Tjeerd Verhagen *2015-10-07 08:09:31*

**fix: Corrects issue auto forwarding from google.com to google.xxx the**

 * country specific search page. 

[7d21048e9a2b3b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7d21048e9a2b3b0) Tjeerd Verhagen *2015-10-07 08:07:57*

**fix: Updates dependencies to latest stable release 0.8**

 * thucydides-junit 0.8.31 (was 0.8.1-SNAPSHTOT) 
 * thucydides-core  0.8.31 (was 0.8.1-SNAPSHTOT) 
 * Adds dependency 
 * slf4j-simple  1.6.4 

[476a18322150cbb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/476a18322150cbb) Tjeerd Verhagen *2015-10-07 07:40:32*

**fix: Brings package name in class inline with the package directory**

 * structure 

[7974322366574a3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7974322366574a3) Tjeerd Verhagen *2015-10-06 17:38:18*

**Renames package 'net.serenity_bdd.*' into 'net.serenitybdd.*', to bring**

 * them inline with the rest 

[7b58b52e3c13baa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7b58b52e3c13baa) Tjeerd Verhagen *2015-10-06 16:30:48*

**Removes unused imports**


[33fac2e859d6bc7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/33fac2e859d6bc7) Tjeerd Verhagen *2015-10-06 14:03:14*

**Removes unused variable registeredWebdriverManagers**


[618a81345bdd93c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/618a81345bdd93c) Tjeerd Verhagen *2015-10-06 13:34:50*

**Removes unused import**


[b57fce26d9baea8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b57fce26d9baea8) Tjeerd Verhagen *2015-10-06 13:28:15*

**Removes generics warning**


[4cf4c1123a50402](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4cf4c1123a50402) Tjeerd Verhagen *2015-10-06 13:27:41*

**Removes no longer needed @SuppressWarnings**


[8cfa26db66a93f2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8cfa26db66a93f2) Tjeerd Verhagen *2015-10-06 13:25:55*

**Removes generics warnings**


[d370f8441d4d146](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d370f8441d4d146) Tjeerd Verhagen *2015-10-06 13:14:01*

**Removes warning 'Use static field LoggingLevel.VERBOSE'**


[4318dc2a8180f75](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4318dc2a8180f75) Tjeerd Verhagen *2015-10-06 13:11:38*

**Removes @SuppressWarnings, no longer needed**


[99c05b534ed9d49](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/99c05b534ed9d49) Tjeerd Verhagen *2015-10-06 13:08:47*

**Removes unsued variable**


[910356643cb2257](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/910356643cb2257) Tjeerd Verhagen *2015-10-06 13:05:59*

**Fixes generics issue (no longer warning)**


[52a42c40edd6b9b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/52a42c40edd6b9b) Tjeerd Verhagen *2015-10-06 13:05:06*

**Removes unused imports**


[bf415be9811359f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf415be9811359f) Tjeerd Verhagen *2015-10-06 13:04:15*

**Corrects javadoc description**


[f38011c61aa04c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f38011c61aa04c6) Tjeerd Verhagen *2015-10-06 13:02:38*

**Simplified a unit test**


[dc6c68e81a9031e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dc6c68e81a9031e) John Smart *2015-09-27 10:57:17*

**Simplified a unit test**


[6c0391deaec315d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6c0391deaec315d) John Smart *2015-09-27 10:50:25*

**Fixed an error in the reporting in the Hit interaction**


[20f7f3075e538f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/20f7f3075e538f3) John Smart *2015-09-27 10:05:02*

**Fixed project build on Windows**


[917ff5d67fbce78](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/917ff5d67fbce78) Alexey Bychkov *2015-09-27 06:20:40*

**Fixing Java warnings - Redundant cast**

 * GoogleSearchSteps.java:27: warning: [cast] redundant cast to GoogleHomePage 
 * GoogleHomePage page = (GoogleHomePage) getPages().currentPageAt(GoogleHomePage.class); 
 * GoogleSearchSteps.java:33: warning: [cast] redundant cast to GoogleResultsPage 
 * GoogleResultsPage page = (GoogleResultsPage) getPages().currentPageAt(GoogleResultsPage.class); 

[cbfb1788cd8e35a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cbfb1788cd8e35a) Hallvard Nygård *2015-08-31 14:54:21*

**Refactoring tests**


[fc33695ff16e8fb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fc33695ff16e8fb) John Smart *2015-07-25 01:43:32*

**Upgraded to cucumber-jvm 1.2.4**


[8b837b624eae592](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b837b624eae592) John Smart *2015-07-24 13:55:24*

**Upgraded to cucumber-jvm 1.2.4**


[31b954c14c634fa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/31b954c14c634fa) John Smart *2015-07-24 13:36:38*

**Requirements reporting now support mixing JBehave and JUnit tests.**


[1d3b1039c1e1c02](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1d3b1039c1e1c02) John Smart *2015-07-24 09:04:15*

**Fixed an issue with JBehave where the browser was not closing after tests.**


[869b7276a4defa3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/869b7276a4defa3) John Smart *2015-07-24 07:10:02*

**More consistent breadcrumbs**


[5d78861f5501df9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d78861f5501df9) John Smart *2015-07-24 07:09:47*

**Minor bug fixes**


[8797dae10c1fae3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8797dae10c1fae3) John Smart *2015-07-24 00:25:12*

**Added the 'deep.step.execution.after.failures' option**

 * This option (set to false by default), lets you control the execution depth of the @Step methods after a step has failed. If set to true, it will run nested @Step methods as well as top-level ones. 

[9c94af955b05e0f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9c94af955b05e0f) John Smart *2015-07-24 00:24:53*

**Include the name of the exception in error messages**


[7e82d4f3b315f58](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7e82d4f3b315f58) John Smart *2015-07-24 00:23:19*

**Refactored JSON file loading for better error reporting**


[6d3847c93b9c330](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d3847c93b9c330) John Smart *2015-07-24 00:22:58*

**Added more robust support for running REST tests in DryRun mode.**


[8e6e206ac33e52d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8e6e206ac33e52d) John Smart *2015-07-23 12:23:28*

**Improved reporting for tag-related reports**


[e32a2a0099755f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e32a2a0099755f3) John Smart *2015-07-23 05:41:52*

**Requirements breadcrumbs for JBehave**


[f5fbb68ad3d7b54](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f5fbb68ad3d7b54) John Smart *2015-07-23 04:41:54*

**Fixed a timeout issue.**

 * Fixed a timeout issue related to using withTimeoutOf(...).waitForElementToDisappear() 

[1764651aff9b8a7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1764651aff9b8a7) John Smart *2015-07-23 03:38:43*

**Added a utility method to wait for an AngularJS page to load properly.**


[bcc83dd04ebc0a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bcc83dd04ebc0a8) John Smart *2015-07-23 03:09:39*

**Removed thread leak issue**


[4b5298def11a998](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b5298def11a998) John Smart *2015-07-23 02:36:31*

**Added basic support for the 'dry-run' option.**

 * Rest calls will now be skipped if you activtate &#39;dry-run&#39; mode (e.g. by setting the &#39;serenity.dry.run&#39; system property to true). 

[7a57c62bcb1dff0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a57c62bcb1dff0) John Smart *2015-07-23 01:38:29*

**refactoring requirements processing - wip**


[b3c78559ba2cc36](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b3c78559ba2cc36) John Smart *2015-07-22 20:17:05*

**Improved error reporting.**


[e64d7a75f03e723](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e64d7a75f03e723) John Smart *2015-07-20 07:26:39*

**Added support for XML REST messages**


[c14bdf6c5034ed7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c14bdf6c5034ed7) John Smart *2015-07-20 06:26:28*

**Display the overall time taken for the tests**


[8ed69d8a271bc4a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8ed69d8a271bc4a) John Smart *2015-07-17 05:37:20*

**Moved the screenshot caption to the top of the screenshots to make it easier to see**


[06ccc69767b8287](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/06ccc69767b8287) John Smart *2015-07-17 02:06:56*

**Updated appium version**


[a66cef5da958b5f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a66cef5da958b5f) John Smart *2015-07-14 22:35:19*

**feat: Dropdown.selectByValue()**


[400fca7439f69a4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/400fca7439f69a4) Kassem Sandarusi *2015-07-08 21:38:06*


## v1.1.16
### No issue

**Refactoring**


[632a91a024abd3b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/632a91a024abd3b) John Smart *2015-10-13 04:18:28*

**Updated dependencies**


[975f25e90c30c25](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/975f25e90c30c25) John Smart *2015-10-13 01:25:42*

**Removed unnused imports**


[3fc89becc4c606b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3fc89becc4c606b) John Smart *2015-10-12 23:25:20*


## v1.1.15
### No issue

**build:refactoring test phase**


[020fd6fa6e101f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/020fd6fa6e101f6) John Smart *2015-10-11 22:49:56*

**Deprecated old screenshot processor**


[0068f01d15df7dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0068f01d15df7dc) John Smart *2015-10-11 22:42:03*

**Deprecated old screenshot processor**


[92fc1c67d24fb6e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/92fc1c67d24fb6e) John Smart *2015-10-11 22:16:12*

**Deprecated old screenshot processor**


[f4670d119a6c8ec](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4670d119a6c8ec) John Smart *2015-10-11 21:16:16*

**Deprecated old screenshot processor**


[84e1e5f25cd5bac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/84e1e5f25cd5bac) John Smart *2015-10-11 09:18:41*

**refactor:fine-tuning build job**


[ad43f431f450dac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad43f431f450dac) John Smart *2015-10-11 00:19:39*

**refactor:Better error handling for screenshots**


[60fa70b080f1200](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/60fa70b080f1200) John Smart *2015-10-09 06:55:43*

**refactor:Better error handling for screenshots**


[29b129c8ef92e3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/29b129c8ef92e3c) John Smart *2015-10-09 06:20:15*

**refactor:Better error handling for screenshots**


[9bd7368cbb05b3b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9bd7368cbb05b3b) John Smart *2015-10-09 06:02:57*

**refactor:Better error handling for screenshots**


[4bfc541c331a6e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4bfc541c331a6e9) John Smart *2015-10-09 05:46:03*

**refactor:Better error handling for screenshots**


[f5c5fc76afcc3b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f5c5fc76afcc3b0) John Smart *2015-10-09 00:43:37*

**refactor:Removed old screenshot logic**


[0bfbd9578a80bf1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0bfbd9578a80bf1) John Smart *2015-10-08 09:50:21*

**fix: Fixed a memory leak.**


[8d77bc49dfa524b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d77bc49dfa524b) John Smart *2015-10-08 08:19:39*

**refactor:Removed old screenshot logic**


[5e392c973fb53eb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5e392c973fb53eb) John Smart *2015-10-08 04:26:15*

**Inital version of a new implementation of the screenshot logic.**


[f84df26505988ea](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f84df26505988ea) John Smart *2015-10-08 02:39:53*

**Added support for blurring.**


[91ffac148057562](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/91ffac148057562) John Smart *2015-10-08 02:39:53*

**Inital version of a new implementation of the screenshot logic.**


[252524c9056b047](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/252524c9056b047) John Smart *2015-10-08 02:39:53*

**Added new implementation of the screenshot logic**


[1599557cf37ab9d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1599557cf37ab9d) John Smart *2015-10-08 02:39:53*

**chore:Added the chromedriver binary for the Snap-CI builds**


[80913b94b709b8b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80913b94b709b8b) John Smart *2015-10-07 20:57:58*

**refactor: Added better error handling for the actors.**


[cf774f8d24741dc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cf774f8d24741dc) John Smart *2015-10-07 19:41:50*


## v1.1.14
### No issue

**Test refactoring**


[c7658dfd48b8fdd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c7658dfd48b8fdd) John Smart *2015-09-26 21:43:27*

**Removed the redundant 'Stabie' column in the reports**


[5d16f7a17ee6056](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d16f7a17ee6056) John Smart *2015-09-26 21:29:12*

**Better error reporting for actors in the Journey module.**


[0f4803e013ed331](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0f4803e013ed331) John Smart *2015-09-26 21:28:49*

**Improved logging messages**


[05a1789883ec4e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/05a1789883ec4e4) John Smart *2015-09-26 21:27:46*

**Better support for BrowserStack capability options**


[9af3f06f1329590](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9af3f06f1329590) John Smart *2015-09-26 21:26:58*

**Added 'feature.file.encoding' system property to specify an encoding of Cucumber files**


[7e181098f94ff32](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7e181098f94ff32) Alexey Bychkov *2015-09-25 13:09:39*


## v1.1.13
### No issue

**Fixed an issue with taking screenshots when using multiple browsers**


[d131e1535be9ee2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d131e1535be9ee2) John Smart *2015-09-20 04:40:03*

**Fixed an issue with the moveTo() PageObject method**


[48305b4dfdf4d31](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/48305b4dfdf4d31) John Smart *2015-09-20 03:13:00*

**Fixed an issue that caused tests with multiple actors to report steps out of order.**


[1c3eb5e7fa91c9a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1c3eb5e7fa91c9a) John Smart *2015-09-20 03:12:35*


## v1.1.12
### No issue

**Additional requirements testing**


[89461ce6f7eeb53](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89461ce6f7eeb53) John Smart *2015-09-16 07:44:15*

**Updated selenium core**


[b843013bf18cb0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b843013bf18cb0e) John Smart *2015-09-16 07:08:13*

**Updated selenium to 2.47.1**


[ef0a39f8ad5fd3e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef0a39f8ad5fd3e) John Smart *2015-09-16 06:42:53*


## v1.1.11
### No issue

**Better support for multiple browser management.**


[65eada22ba2665f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/65eada22ba2665f) John Smart *2015-09-16 04:41:30*

**Refactored screenshot-related logging to DEBUG**


[5f69b1bc6d82798](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f69b1bc6d82798) John Smart *2015-09-15 02:10:11*

**Improved the step message when an actor enteres a value into a field.**


[714f2a9025f8efe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/714f2a9025f8efe) John Smart *2015-09-15 00:40:12*


## v1.1.10
### No issue

**Improved log reporting for the Journey pattern.**


[3721d0ab6bd6369](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3721d0ab6bd6369) John Smart *2015-09-07 04:11:46*

**Improved reporting in the console logging.**


[969c74b4f4838cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/969c74b4f4838cd) John Smart *2015-09-07 03:08:28*

**fix: prevent null pointers when generating reports**


[66cffe7b361595b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/66cffe7b361595b) afoltin *2015-09-04 08:18:24*


## v1.1.9
### No issue

**Updated dependencies**


[9051d518de25ee8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9051d518de25ee8) John Smart *2015-09-01 08:00:14*


## v1.1.8
### GitHub #115 

**Fixed #115**

 * Only record REST responses for non-binary response types. 

[32f488557eea780](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/32f488557eea780) John Smart *2015-09-01 01:24:19*


### GitHub #122 

**Attempt to fix #122**


[de2d4287aab991f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/de2d4287aab991f) John Smart *2015-09-01 00:21:45*


### Jira cglib-3 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### Jira core-1 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### Jira junit-1 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### Jira junit-4 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### Jira messaging-2 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### Jira plugins-2 

**Debug log: Adding exception to output**

 * Sample output: 
 * 11:29:07.868 [Test worker] DEBUG n.s.j.r.RetryFilteringRunNotifier - Test failed: com.skagenfondene.test.stories.InactiveUser: java.lang.NullPointerException--&gt;null 
 * net.sf.cglib.core.CodeGenerationException: java.lang.NullPointerException--&gt;null 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:235) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.ReflectUtils.newInstance(ReflectUtils.java:220) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createUsingReflection(Enhancer.java:639) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.firstInstance(Enhancer.java:538) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:225) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377) ~[cglib-3.1.jar:na] 
 * at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:304) ~[cglib-3.1.jar:na] 
 * at net.thucydides.core.steps.StepFactory.webEnabledStepLibrary(StepFactory.java:167) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.createProxyStepLibrary(StepFactory.java:157) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:111) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.instantiateNewStepLibraryFor(StepFactory.java:103) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getNewStepLibraryFor(StepFactory.java:75) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepFactory.getStepLibraryFor(StepFactory.java:70) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instantiateAnyUnitiaializedSteps(StepAnnotations.java:52) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.instanciateScenarioStepFields(StepAnnotations.java:41) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.thucydides.core.steps.StepAnnotations.injectScenarioStepsInto(StepAnnotations.java:23) ~[serenity-core-1.1.5.jar:1.1.5] 
 * at net.serenitybdd.junit.runners.SerenityRunner.injectScenarioStepsInto(SerenityRunner.java:590) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.methodInvoker(SerenityRunner.java:552) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:251) ~[junit-4.11.jar:na] 
 * at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:440) [serenity-junit-1.1.5.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.runChild(SerenityRunner.java:60) [serenity-junit-1.1.5.jar:na] 
 * at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229) ~[junit-4.11.jar:na] 
 * at org.junit.runners.ParentRunner.run(ParentRunner.java:309) ~[junit-4.11.jar:na] 
 * at net.serenitybdd.junit.runners.SerenityRunner.run(SerenityRunner.java:249) [serenity-junit-1.1.5.jar:na] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48) [gradle-plugins-2.2.1.jar:2.2.1] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_05] 
 * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_05] 
 * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_05] 
 * at java.lang.reflect.Method.invoke(Method.java:483) ~[na:1.8.0_05] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93) [gradle-messaging-2.2.1.jar:2.2.1] 
 * at com.sun.proxy.$Proxy2.processTestClass(Unknown Source) [na:na] 

[a8ae2e5f4ac22d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a8ae2e5f4ac22d1) Hallvard Nygård *2015-08-31 09:53:52*


### No issue

**Changed dependencies back to mockito 1.9.5 to avoid dependency conflict issues**


[9bd70c2429c11b7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9bd70c2429c11b7) John Smart *2015-09-01 06:22:55*

**Test refactoring**


[45f1eae8491c8d9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/45f1eae8491c8d9) John Smart *2015-09-01 01:24:27*

**see https://github.com/serenity-bdd/serenity-core/issues/37**


[1bdbc53a34dba3b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1bdbc53a34dba3b) liviu.carausu *2015-08-31 21:11:54*

**Added support for multiple browsers in the same test using the Journey pattern**

 * For example: 
 * @Managed(driver=&quot;chrome&quot;) 
 * WebDriver firstBrowser; 
 * @Managed(driver=&quot;firefox&quot;) 
 * WebDriver anotherBrowser; 
 * @Test 
 * public void danaCanUpdateHerProfile() { 
 * Actor dana = new Actor(&quot;Dana&quot;); 
 * dana.can(BrowseTheWeb.with(firstBrowser)); 
 * givenThat(dana).has(openedTheApplication); 
 * when(dana).attemptsTo(viewHerProfile); 
 * and(dana).attemptsTo(UpdateHerProfile.withName(&quot;Dana&quot;).andCountryOfResidence(&quot;France&quot;)); 
 * then(dana).should(seeThat(TheProfile.name(), equalTo(&quot;Dana&quot;))); 
 * and(dana).should(seeThat(TheProfile.country(), equalTo(&quot;France&quot;))); 

[c6ed01d846f4646](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c6ed01d846f4646) John Smart *2015-08-31 10:42:25*

**Improving logging in ReportService**


[9eeabc7e93e6b9b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9eeabc7e93e6b9b) Hallvard Nygård *2015-08-31 09:26:29*

**feat: the phantomjs ssl-property can now be set using the PHANTOMJS_SSL_PROPERTY environment variable, like the PHANTOMJS_BINARY_PATH. Possible options are 'sslv2', 'sslv3', 'tlsv1' and 'any'.**


[2f1bf50b655c68a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2f1bf50b655c68a) david *2015-08-31 09:01:34*


## v1.1.7
### No issue

**Better integration of Actors and Question objects**

 * Actors can now ask questions directly, e.g. 
 * --- 
 * Integer totalCost = dana.asksFor(theTotalCost()); 
 * --- 
 * They can also remember the answers to questions for future use: 
 * --- 
 * dana.remember(&quot;Total Cost&quot;, theTotalCost()); 
 * assertThat(dana.recall(&quot;Total Cost&quot;)).isEqualTo(14); 
 * --- 

[75415857c76fa70](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/75415857c76fa70) John Smart *2015-08-31 02:24:28*


## v1.1.6
### No issue

**Refactoring and better console reporting.**


[f77a999a3f1fe3c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f77a999a3f1fe3c) John Smart *2015-08-25 03:33:52*

**Ensure that Consequence steps are not evaluated after a previous step has failed.**

 * This avoids misleading error messages. 

[a2089724582c419](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2089724582c419) John Smart *2015-08-24 21:24:55*

**Refactored the journey demo test**

 * To better illustrate tasks/interaction layers. 

[4404ea3c9f3c91b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4404ea3c9f3c91b) John Smart *2015-08-22 18:06:31*

**Added support for dropdowns in the interaction-level bundled Performables.**


[f7dc3d73a581aa9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f7dc3d73a581aa9) John Smart *2015-08-22 17:56:25*

**Removed unnecessary warning messages**


[d3d2e38936d3979](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d3d2e38936d3979) John Smart *2015-08-20 10:26:14*

**Revolving dependency conflicts with hamcrest 1.1**


[743ec7b5c289e57](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/743ec7b5c289e57) John Smart *2015-08-20 10:25:55*

**fix: Improved error reporting for provided drivers**

 * DriverSources may implement some non-trivial logic, so it is very handy 
 * for debugging to include in stack trace exception occurred while tried to 
 * initialize new webdriver. Especially in multi-node test environment 
 * configurations. 

[3006a4ea233ddc9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3006a4ea233ddc9) Nikolay Artamonov *2015-08-20 09:52:56*

**Removed redundant code that was causing errors in the reports.**

 * If there were more than one given clause in a journey-style test, the initial givens where incorrectly nested. 

[63458448580c2b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/63458448580c2b0) John Smart *2015-08-19 14:43:41*


## v1.1.5
### GitHub #109 

**Fix #109**


[b06d96153f9ed78](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b06d96153f9ed78) Sergey Morgunov *2015-08-12 13:00:23*


### No issue

**Avoid unnecessary error messages with Java 8 lambdas.**


[b7a8e7190bf7486](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b7a8e7190bf7486) John Smart *2015-08-17 18:57:53*

**Fixed class conflict issue.**


[d7af4ed258615c6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d7af4ed258615c6) John Smart *2015-08-17 18:57:30*

**Improved reporting of questions**


[3afdafb4c61939c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3afdafb4c61939c) John Smart *2015-08-17 18:56:58*

**Renamed 'serenity-ability-to-browse-the-web' to 'browse-the-web'**


[08c6aab6d2797bc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08c6aab6d2797bc) John Smart *2015-08-17 18:56:30*

**Added the "ability-to-browse-the-web" module to the journey module.**


[ed62f9307eb62e7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ed62f9307eb62e7) John Smart *2015-08-16 18:53:31*

**Fix inject Pages in super class**


[8bec974f768f3c8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8bec974f768f3c8) Sergey Morgunov *2015-08-12 15:56:18*

**Check for existence of the angular object.**


[cfb3bd63f86fd3f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cfb3bd63f86fd3f) Andreas Urban *2015-08-12 08:17:35*

**Refactoring build**


[d64e692974cc952](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d64e692974cc952) John Smart *2015-08-11 10:14:58*


## v1.1.4
### GitHub #103 

**Improve readability of "View stack trace" dialog (#103)**


[cd553eb90d77547](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd553eb90d77547) John Smart *2015-08-05 20:19:59*


### GitHub #106 

**Fix issue #106**


[7f8f10c771f3dba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7f8f10c771f3dba) Sergey Morgunov *2015-08-07 18:38:32*


### No issue

**Minor refactoring**


[d29c52eeab26550](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d29c52eeab26550) John Smart *2015-08-10 19:53:16*

**Added the Question as a concept.**


[65da5169c348795](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/65da5169c348795) John Smart *2015-08-10 12:13:22*

**Test refactoring**


[7848a32e8265882](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7848a32e8265882) John Smart *2015-08-10 06:52:17*

**Test refactoring**


[7ae80bfa70188b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ae80bfa70188b9) John Smart *2015-08-09 21:48:28*

**You can now include assertions in the tests**

 * You can now include assertions in the tests and reports using the Consequence class. 

[9722c9025f24bef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9722c9025f24bef) John Smart *2015-08-09 21:04:55*

**Works for nested failing tasks**


[eeb1f7353ea5466](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eeb1f7353ea5466) John Smart *2015-08-09 16:01:27*

**Report task steps correctly when an error occurs in a task step**


[5ee8c7e4cd29642](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5ee8c7e4cd29642) John Smart *2015-08-09 15:24:34*

**Got successful and failing journey scenarios working, as long as the failing assertion is in the performAs method. Currently, if one of the chained methods fails, the following steps are not executed and the results are unpredictable**


[9e49f8c1d500cc2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e49f8c1d500cc2) John Smart *2015-08-08 10:20:30*

**You can now use #this or #self in a @Step description.**


[95aa4af410f5c73](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/95aa4af410f5c73) John Smart *2015-08-07 14:34:23*

**Better treatment of invalid or undefined fields in step definitions.**


[968c4030467adf7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/968c4030467adf7) John Smart *2015-08-06 14:16:34*

**Simplified variable injection into step descriptions.**

 * You can now inject any member variable in the step class by name into step descriptions 
 * You can now use member variables of a step library in the @Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example: 
 * ---- 
 * private final String siteName = &quot;Etsy&quot;; 
 * @Step(&quot;Search for a shop called {0} on the #siteName website&quot;) 
 * public void searches_for_shop_called(String shopName) { 
 * homePage.searchForShopCalled(shopName); 
 * } 
 * ---- 

[7755f2efdada037](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7755f2efdada037) John Smart *2015-08-06 08:44:01*

**Refactoring**


[f353c363ae54e52](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f353c363ae54e52) John Smart *2015-08-05 21:41:17*

**Inject step variables by name into step descriptions**

 * You can now use member variables of a step library in the @Step annotation to augment the step description. Just refer to the variable using the name of the variable prefixed by a &quot;#&quot;, as shown in this example: 
 * ---- 
 * @Reported 
 * private final String siteName = &quot;Etsy&quot;; 
 * @Step(&quot;Search for a shop called {0} on the #siteName website&quot;) 
 * public void searches_for_shop_called(String shopName) { 
 * homePage.searchForShopCalled(shopName); 
 * } 
 * ---- 

[9b7c6025d8fef90](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b7c6025d8fef90) John Smart *2015-08-05 20:19:20*

**Added support for TypeSafeConfig**

 * You can now provide a TypeSafeConfig configuration file (https://github.com/typesafehub/config) instead of a serenity.properties file. The file can be called &#39;serenity.conf&#39; or any of the other Type Safe Config configuration files names. 
 * The configuration file can contain both Serenity variables and other arbitrary variables, which will be available in the EnvironmentVariables field. For example, a simple configuration file might look like this. 
 * ---- 
 * serenity { 
 * logging = DEBUG 
 * } 
 * environment { 
 * uat = uat-server 
 * } 
 * ---- 

[c0b0fffc15caa85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c0b0fffc15caa85) John Smart *2015-08-05 14:21:02*

**Refactored a unit test for more clarity**


[e50e5578683e887](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e50e5578683e887) John Smart *2015-08-05 09:07:22*


## v1.1.3
### No issue

**Fixed an issue causing the drivers to be closed incorrectly during parallel tests**


[71f56847e32e7a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/71f56847e32e7a9) John Smart *2015-08-02 12:04:24*


## v1.1.2
### No issue

**Added the 'uniqueInstance' attribute to the @Steps annotation to support multiple instances of the same step library in the same class**


[6aae0d4539b3142](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6aae0d4539b3142) John Smart *2015-07-30 09:49:50*

**Fixed colors in some of the reports causing text to be light grey on white**


[c4033eaa90b53ff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c4033eaa90b53ff) John Smart *2015-07-30 09:00:16*


## v1.1.1
### No issue

**Updated unit tests for manual tests**


[4c85801565208d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c85801565208d6) John Smart *2015-07-26 01:49:10*

**Added support for manual annotated tests**


[b8190993a962b85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b8190993a962b85) John Smart *2015-07-25 23:10:19*

**Fixed an issue that caused broken links in JUnit and Cucumber requirements reports**


[312a8ec943d0094](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/312a8ec943d0094) John Smart *2015-07-25 14:03:47*

**Adding support for manual tests**


[30c89b8286c0378](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/30c89b8286c0378) John Smart *2015-07-25 04:58:49*


## v1.1.0
### No issue

**Simplified some redundant tests**


[26c716c69e9a2ee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26c716c69e9a2ee) John Smart *2015-07-25 04:49:14*

**JSON requirements files are now stored in a dedicated 'requirements' directory.**


[d4222f315f744b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d4222f315f744b6) John Smart *2015-07-25 03:41:27*

**Minor refactoring**


[3fd16d7b62a7770](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3fd16d7b62a7770) John Smart *2015-07-25 02:07:59*

**Update BrowserStackRemoteDriverCapabilities.java**


[c2e028ca2847e22](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c2e028ca2847e22) Imran Khan *2015-07-21 14:22:02*


## v1.0.64
### No issue

**fix: Improved error messages for remote drivers**

 * Better error message reporting if a remote driver is incorrectly configured, and some minor refactoring. 

[21b96f814fd53d6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/21b96f814fd53d6) John Smart *2015-07-14 12:03:40*

**chore:reinstated Bintray plugin**


[404d3bf2f6bdec1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/404d3bf2f6bdec1) John Smart *2015-07-13 13:38:04*


## v1.0.62
### No issue

**chore:Updating release plugins**


[85c600129e4c94b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/85c600129e4c94b) John Smart *2015-07-13 12:28:50*

**chore: Removed unnecessary wrapper directories**


[5a907598c5f0f06](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5a907598c5f0f06) John Smart *2015-07-13 12:24:09*


## v1.0.61
### No issue

**chore:Updating the version of the gradle-git plugin**


[0fbada91e9c3ccf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0fbada91e9c3ccf) John Smart *2015-07-13 06:12:22*

**chore:Removed dependency on bintray plugin.**


[197d00981b482a5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/197d00981b482a5) John Smart *2015-07-13 03:08:57*


## v1.0.59
### GitHub #87 

**Fixed #87**


[6f1628bc721463c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f1628bc721463c) John Ferguson Smart *2015-06-30 06:31:08*


### No issue

**Fixes unit tests - nullpointer exception fix when system property webdriver.remote.driver is not set**


[395a9a64e193991](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/395a9a64e193991) Kishen Simbhoedatpanday *2015-06-24 15:57:21*

**Fix for setting up the remote webdriver capability: webdriver.remote.browser.version**


[cca6cd03b475dbd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cca6cd03b475dbd) Kishen Simbhoedatpanday *2015-06-24 14:35:50*


## v1.0.58
### GitHub #79 

**Fixed #79**


[5b16d86c86702cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5b16d86c86702cd) John Ferguson Smart *2015-06-20 19:55:59*


### GitHub #81 

**Fixeds #81**


[8e8b01a5fd49895](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8e8b01a5fd49895) John Ferguson Smart *2015-06-21 10:02:27*


### No issue

**Refactored tests**


[b06c39978f06dad](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b06c39978f06dad) John Ferguson Smart *2015-06-21 13:14:52*


## v1.0.57
### No issue

**Performance improvements**


[d3777295f2efd74](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d3777295f2efd74) John Ferguson Smart *2015-06-18 21:40:45*

**Fixed timeout issues with waitForAbsence* methods**


[54529c12066cd60](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/54529c12066cd60) John Ferguson Smart *2015-06-18 11:45:51*


## v1.0.56
### No issue

**Fixed an issue with reporting RestAssured assertion failures.**


[4350090ac6ec609](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4350090ac6ec609) John Ferguson Smart *2015-06-15 15:27:58*


## v1.0.54
### No issue

**Fixed some timout issues**


[fb614fb2bc5bce0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fb614fb2bc5bce0) John Ferguson Smart *2015-06-15 06:49:37*

**Handle null-value parameters more elegantly.**


[75047a1b8d6fcda](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/75047a1b8d6fcda) John Ferguson Smart *2015-06-15 06:36:50*

**feat: Added support for Spring meta-annotations for @ContextConfiguration and @ContextHierarchy**


[7bc74def0bf22fe](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7bc74def0bf22fe) Martin Lau *2015-06-15 03:41:37*

**feat: Add support for Spring @ContextHierarchy annotations**


[05c4283ddeedcc9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/05c4283ddeedcc9) Martin Lau *2015-06-15 03:03:47*


## v1.0.53
### GitHub #66 

**Handle recursive parameters correctly (#66)**


[259741557e10c1f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/259741557e10c1f) John Ferguson Smart *2015-06-11 18:40:32*


### GitHub #71 

**Better reporting of exceptions (fixes #71)**


[2a4a06f28a3a92b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2a4a06f28a3a92b) John Ferguson Smart *2015-06-13 09:11:29*


### No issue

**Temporarily disable some tests with environment-specific issues**


[4aee7bb76735218](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4aee7bb76735218) John Ferguson Smart *2015-06-13 10:44:56*

**More refactoring tests**


[778ea0699595172](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/778ea0699595172) John Ferguson Smart *2015-06-13 10:34:18*

**More refactoring tests**


[11b84b726006eff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/11b84b726006eff) John Ferguson Smart *2015-06-13 10:31:27*

**More refactoring tests**


[1a4268c8749d0f6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1a4268c8749d0f6) John Ferguson Smart *2015-06-13 10:24:07*

**Refactoring tests**


[45d11b56f149692](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/45d11b56f149692) John Ferguson Smart *2015-06-13 10:10:05*

**Refactoring tests**


[1345adf38741118](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1345adf38741118) John Ferguson Smart *2015-06-13 10:06:14*

**Refactoring tests**


[1e636a6e0192ade](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1e636a6e0192ade) John Ferguson Smart *2015-06-13 09:58:50*

**Refactoring tests**


[eed9a7c1b837c4d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eed9a7c1b837c4d) John Ferguson Smart *2015-06-13 09:48:31*

**Added better support for reporting exceptions**

 * We now support reporting exceptions with a zero-parameter constructor as well as a single-parameter constructor. 

[6421a4ccd0b4964](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6421a4ccd0b4964) John Ferguson Smart *2015-06-11 16:48:35*


## v1.0.52
### GitHub #69 

**Modifying screenshot code to work better with Windows (see #69)**


[0a7fdeacf6a3f4a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a7fdeacf6a3f4a) John Ferguson Smart *2015-06-10 15:05:04*

**Modifying screenshot code to work better with Windows (see #69)**


[b184b84b1d9701e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b184b84b1d9701e) John Ferguson Smart *2015-06-10 14:40:24*


## v1.0.51
### GitHub #69 

**Attempt to fix #69**

 * Issue #69 looks like an OS/filesystem-specific issue related to Java 7 atomic copies. This version uses REPLACE_EXISTING instead of ATOMIC_MOVE. 

[d0d500c06511a93](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d0d500c06511a93) John Ferguson Smart *2015-06-09 13:47:20*


### No issue

**Updated to Selenium 2.46.0**


[a100646a8697ec4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a100646a8697ec4) John Ferguson Smart *2015-06-09 18:17:59*

**Resolved dependency conflict**


[dac9fead420ecb8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dac9fead420ecb8) John Ferguson Smart *2015-06-09 14:19:52*

**Updated to Selenium 2.46.0**


[5dfcc1a4ec89a09](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5dfcc1a4ec89a09) John Ferguson Smart *2015-06-09 13:45:33*

**Deprecate SpringIntegration.**

 * Add SpringIntegrationMethodRule and SpringIntegrationClassRule, as well as the utility runner SpringIntegrationSerenityRunner, which conveniently automatically adds the aforementioned rules. 
 * (Note that some of the main code and test code for the above new classes were originally written in Java 8 and used method references, lambdas and java.util.function.*. To get Java 7 support, these has been replaced by interfaces and anonymous inner classes, but if the project ever moves to Java 8, it is recommended that these are replaced once again). 

[6e7255dc2c22bfc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6e7255dc2c22bfc) Scott Dennison *2015-06-04 11:24:27*

**Moved the tests in serenity-junit that depended on serenity-spring, into serenity-spring, so serenity-junit no longer depends on serenity-spring.**

 * This is in preparation for an upcoming commit - Not doing this would cause a circular dependency between serenity-junit and serenity-spring. 

[e38147cc49d8c86](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e38147cc49d8c86) Scott Dennison *2015-06-04 11:09:50*


## v1.0.50
### No issue

**Fixed incorrect timeout issue**


[6edddb2d16a2796](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6edddb2d16a2796) John Ferguson Smart *2015-06-04 00:40:42*

**Refactored unit tests for more clarity**


[a5c86db27b7657e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a5c86db27b7657e) John Ferguson Smart *2015-06-03 02:05:44*


## v1.0.49
### No issue

**Fixed a minor formatting issue for JBehave embedded tables.**


[08aba9be2f264cc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08aba9be2f264cc) John Ferguson Smart *2015-06-02 00:56:35*


## v1.0.48
### GitHub #61 

**Fixed #61: issue with path checks on remote appium server**


[28b9b7bfd02d007](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/28b9b7bfd02d007) John Ferguson Smart *2015-05-31 01:58:09*


### GitHub #64 

**Fixed #64: issue with resetting implicit timeouts**


[a47b0aea9f58fe1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a47b0aea9f58fe1) John Ferguson Smart *2015-05-31 01:55:11*


### GitHub #65 

**Fixed #65 - temporary screenshots not deleted**


[d4404c10e741ea0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d4404c10e741ea0) John Ferguson Smart *2015-05-31 21:35:28*


### Jira THUCYDIDES-253 

**Fix for THUCYDIDES-253**


[a2d03c8dbf1fcb0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2d03c8dbf1fcb0) John Ferguson Smart *2015-05-27 10:57:06*


### No issue

**Simplified some tests**


[2e74fdf36e9c37c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2e74fdf36e9c37c) John Ferguson Smart *2015-06-01 07:02:08*

**Refactoring some old multi-select code**


[ebb84712cf92e62](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ebb84712cf92e62) John Ferguson Smart *2015-06-01 06:35:52*

**Refactoring multiple select code**


[bf01941c99a7857](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bf01941c99a7857) John Ferguson Smart *2015-06-01 06:27:01*

**Harmonized test data**


[8835d57485f46a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8835d57485f46a8) John Ferguson Smart *2015-06-01 06:10:43*

**JUnit tests using the 'expected' attribute are not supported.**


[95f84a18ec27e43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/95f84a18ec27e43) John Ferguson Smart *2015-06-01 05:53:12*

**Fixed a bug related to deriving requirements structures from Cucumber feature files.**


[0dd7d28dbc0a255](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0dd7d28dbc0a255) John Ferguson Smart *2015-06-01 05:52:06*

**Made the reporting a bit more robust**

 * Correctly cater for exceptions without an error message. 

[35250bdb902423f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/35250bdb902423f) John Ferguson Smart *2015-05-31 21:40:07*

**Display full screenshots in the slideshow view.**


[ad83c2afa1652ac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad83c2afa1652ac) John Ferguson Smart *2015-05-31 01:56:37*

**Added basic support for RestAssured.**

 * You can now add the serenity-rest-assured module to have tight integration with Rest Assured for testing REST web services. Serenity provides a wrapper around the RestAssured methods that reports on the REST queries sent and the responses recieved. Use the SerenityRest.rest() method as a starting point, e.g. 
 * ```` 
 * rest().given().contentType(&quot;application/json&quot;).content(jsonPet).post(&quot;http://petstore.swagger.io/v2/pet&quot;); 
 * rest().get(&quot;http://petstore.swagger.io/v2/pet/{id}&quot;, pet.getId()).then().statusCode(200) 
 * .and().body(&quot;name&quot;, equalTo(pet.getName())); 
 * ```` 

[23af5a66522cc35](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23af5a66522cc35) John Ferguson Smart *2015-05-27 10:54:27*

**Renamed 'core' to 'serenity-core'**


[f0952b4b4245ef2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f0952b4b4245ef2) John Ferguson Smart *2015-05-18 20:54:01*

**Inject EnvironmentVariables fields in PageObjects**


[6ab197f15acdd0f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ab197f15acdd0f) John Ferguson Smart *2015-05-11 09:34:43*

**Minor formatting fixes in the reports.**


[9ba8c65f64e1346](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9ba8c65f64e1346) John Ferguson Smart *2015-05-11 09:34:19*


## v1.0.47
### GitHub #49 

**Fixed #49 - sysinfo for build report doesn't support values with spaces**


[f1ebd7ab77f7b60](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f1ebd7ab77f7b60) John Ferguson Smart *2015-04-23 13:12:41*


### No issue

**Support for detection of feature file directories.**


[d296863429cfdab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d296863429cfdab) John Ferguson Smart *2015-05-01 23:26:39*

**Added containsElements() methods to the PageObject class.**


[897226ef47f7828](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/897226ef47f7828) John Ferguson Smart *2015-05-01 22:51:07*

**Improved automatic detection of file-system requirements hierarchies.**

 * Looks for JBehave or Cucumber feature files and derives the 
 * corresponding requirements structure based on the depth of the 
 * requirements directories. 

[ba4153da4c89e33](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ba4153da4c89e33) John Ferguson Smart *2015-05-01 22:23:58*

**Ensure that the correct stack trace is displayed in the reports**


[ac5ff92cbc9e82e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ac5ff92cbc9e82e) John Ferguson Smart *2015-05-01 22:21:35*

**Removed system properties from the JUnit results to save space.**


[81fd9206bafb849](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/81fd9206bafb849) John Ferguson Smart *2015-04-30 23:54:29*

**Added a hasClass() method to the WebElementFacade**

 * This method is a convenient way to check whether a web element has a 
 * particular CSS class. 

[39d059a4b103ea1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/39d059a4b103ea1) John Ferguson Smart *2015-04-30 23:54:13*

**Fixed a bug that caused the screenshots to not always be taken correctly.**


[b4fbf0017c6b530](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b4fbf0017c6b530) John Ferguson Smart *2015-04-30 13:40:07*

**Fixed a bug in the JUnit parameterized reports**

 * Ensure that errors are correctly reported in JUnit parameterized reports. 

[ef5aebc607c0df5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ef5aebc607c0df5) John Ferguson Smart *2015-04-29 10:34:46*

**Refactoring and improving the JUnit test reports**


[faabd32b9da94de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/faabd32b9da94de) John Ferguson Smart *2015-04-29 10:33:49*

**Display the stack trace for failing tests in the reports**


[17466a54578f563](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/17466a54578f563) John Ferguson Smart *2015-04-29 10:27:22*

**Fixed a bug preventing requirements to be loaded from the filesystem with JBehave.**


[2c7b976d4323e39](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2c7b976d4323e39) John Ferguson Smart *2015-04-29 03:28:47*

**Serenity now generates JUnit-compatible XML reports.**

 * These reports have the SERENITY-JUNIT prefix. 

[bdc3c68d6bf902c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bdc3c68d6bf902c) John Ferguson Smart *2015-04-24 13:45:10*

**Refactoring some tests**


[036b26b9ae631f4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/036b26b9ae631f4) John Ferguson Smart *2015-04-23 23:00:52*

**Improved the consistency of requirements reporting for JUnit tests.**


[b810e490feb06ac](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b810e490feb06ac) John Ferguson Smart *2015-04-23 14:37:06*

**Fixed an issue where the tests hang if you call Javascript after a failing step.**


[da5c2b71bef30c1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/da5c2b71bef30c1) John Ferguson Smart *2015-04-23 14:36:04*

**Fixed a bug where tests hung if an invalid selector was used in a waitFor expression.**


[a549ef00739f185](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a549ef00739f185) John Ferguson Smart *2015-04-23 13:14:49*

**Improved error reporting and performance.**


[27abd5ccdb9869e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/27abd5ccdb9869e) John Ferguson Smart *2015-04-15 05:12:05*

**Fixed an issue that prevented screenshots from being taken correctly in Cucumber scenarios**


[d5dfc1ea82b7954](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5dfc1ea82b7954) John Ferguson Smart *2015-04-15 05:11:44*

**Refactored some unit tests**


[6bb343b89f02f85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6bb343b89f02f85) John Ferguson Smart *2015-04-15 05:09:49*

**Fixed bug that prevents the arrow keys working in the screenshot slideshow on webkit browsers**


[ace9f68089764dd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ace9f68089764dd) John Ferguson Smart *2015-04-15 05:07:51*

**Only process a new screenshot if an existing one doesn't already exist.**


[3414969576f7c76](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3414969576f7c76) John Ferguson Smart *2015-04-14 09:57:27*

**Allow EnvironmentVariables and SystemConfiguration fields to be**

 * injected into JUnit tests. 

[7d969e88d54fc0c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7d969e88d54fc0c) John Ferguson Smart *2015-04-14 02:19:47*

**Reformatting and tidying up imports**


[413d8398ede5b58](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/413d8398ede5b58) John Ferguson Smart *2015-04-14 02:18:53*

**Fixed formatting error in the screenshots**


[66d334368720385](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/66d334368720385) John Ferguson Smart *2015-04-14 02:17:24*


## v1.0.46
### No issue

**Test refactoring**


[657232a147988a8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/657232a147988a8) John Ferguson Smart *2015-04-13 10:35:16*

**Unit test refactoring**


[137c534cdfb84b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/137c534cdfb84b0) John Ferguson Smart *2015-04-13 05:11:38*

**General refactoring**


[c8ab82bc59925ba](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c8ab82bc59925ba) John Ferguson Smart *2015-04-13 03:18:13*

**Support Cucumber feature files written in other languages.**


[5705bc2b8287608](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5705bc2b8287608) John Ferguson Smart *2015-04-13 03:18:01*

**Better error reporting for errors around the @DefaultUrl definitions for Page Objects.**


[8fb2e5e5e0b8204](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fb2e5e5e0b8204) John Ferguson Smart *2015-04-13 03:16:30*

**Added better error reporting if a groovy expression in the build info properties was incorrect**

 * Better error handling for Groovy expressions used in the “sysinfo.*” 
 * system properties that appear in the build info page. 

[5205c75874d5344](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5205c75874d5344) John Ferguson Smart *2015-04-13 03:15:48*


## v1.0.45
### GitHub #41 

**Don't display the browser icon for non-web tests.**

 * Distinguish among Serenity Web Tests (Selenium) and Serenity Non-Web Test (#41) 

[42884b1685a5b2d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/42884b1685a5b2d) John Ferguson Smart *2015-03-31 00:55:07*


### No issue

**Refactoring and improving the unit tests.**


[7848aaa63808598](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7848aaa63808598) John Ferguson Smart *2015-03-31 00:56:33*

**Support for PhantomJS 1.2.1**


[7586862312bb43a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7586862312bb43a) John Ferguson Smart *2015-03-31 00:55:28*

**Fixed issue with uploading files from the Windows file system.**


[d054c41b4ff58b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d054c41b4ff58b9) John Ferguson Smart *2015-03-31 00:51:01*

**feat: added the possiblity to wait for a CSS or XPath expression from a chained expression.**


[c16591002d6d237](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c16591002d6d237) John Ferguson Smart *2015-03-31 00:48:40*

**feat: Custom build properties are reported in a more human-readable way in the build info screen.**


[daacd77e56937db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/daacd77e56937db) John Ferguson Smart *2015-03-31 00:47:50*

**Added the 'feature.file.language' to support I18N feature files**

 * Narrative text can now be read from non-English feature files, by setting the &#39;feature.file.language&#39; system property. 

[4e17cef7fc39428](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4e17cef7fc39428) John Ferguson Smart *2015-03-30 11:04:25*

**Fix problem with uploading file on Windows. Changed creation of file path (if file in classpath)**


[e7ae87e1fd29953](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7ae87e1fd29953) Alexey Krutilko *2015-03-30 08:48:05*

**ensure unused threads are terminated and removed from executor pool**


[2ec6dd245337d4a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ec6dd245337d4a) Sarinder Virk *2015-03-30 06:05:43*


## v1.0.44
### No issue

**feat: Added a total time in the test results report.**


[e5d04ef4380fc77](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e5d04ef4380fc77) John Ferguson Smart *2015-03-26 04:43:11*

**feat: Added a total time in the test results report.**


[5e446cb122eb203](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5e446cb122eb203) John Ferguson Smart *2015-03-26 04:28:22*

**feat: You can now automatically inject EnvironmentVariables and Configuration variables into your step libraries, simply by declaring a variable of the corresponding type.**


[3838821d026b929](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3838821d026b929) John Ferguson Smart *2015-03-26 04:07:51*


## v1.0.43
### No issue

**Changed the default page size for the test results to 50.**


[85f58025c933967](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/85f58025c933967) John Ferguson Smart *2015-03-20 20:21:01*


## v1.0.42
### No issue

**Allows explicit waits on web elements in a page**

 * For example: 
 * withTimeoutOf(5, TimeUnit.SECONDS).waitFor(facebookIcon).click() 

[33ff1a16031cb98](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/33ff1a16031cb98) John Ferguson Smart *2015-03-19 11:38:11*


## v1.0.41
### No issue

**Implemented the timeoutInSeconds attribute on the FindBy annotation.**


[b497a1db7698833](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b497a1db7698833) John Ferguson Smart *2015-03-18 03:35:05*

**Implemented the timeoutInSeconds attribute on the FindBy annotation.**


[d8ccfdabf6a5952](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d8ccfdabf6a5952) John Ferguson Smart *2015-03-18 03:01:49*


## v1.0.40
### No issue

**Refactored wait logic to use distinct values for implicit waits and wait-for waits.**


[e7235f713c2d379](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e7235f713c2d379) John Ferguson Smart *2015-03-17 11:54:56*

**Added containsElements() and shouldContainElements() methods to WebElementFacade**


[0fa63e2aba5951d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0fa63e2aba5951d) John Ferguson Smart *2015-03-13 12:20:32*

**Added a convenience method to allow more fluent waitFor() constructs**


[9d9c5a4b6a19c4f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d9c5a4b6a19c4f) John Ferguson Smart *2015-03-13 12:04:39*


## v1.0.39
### No issue

**tests: hardeding the timeout tests**


[ac3de4e6f2f409b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ac3de4e6f2f409b) John Ferguson Smart *2015-03-12 22:42:08*

**feature: Added support for a waitUntilClickable() method on web elements**


[b29b7cc6ce97ee9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b29b7cc6ce97ee9) John Ferguson Smart *2015-03-12 21:58:52*

**tests: test hardening**


[82d1ab1c848b7d3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/82d1ab1c848b7d3) John Ferguson Smart *2015-03-12 21:25:37*

**fix: Fixed a bug in reading the requirements from the file system.**


[2eca74a07bd6db6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2eca74a07bd6db6) John Ferguson Smart *2015-03-12 21:00:21*

**fix: Fixed a bug in reading the requirements from the file system.**


[9a6c99dcc3e1949](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9a6c99dcc3e1949) John Ferguson Smart *2015-03-12 20:51:16*

**fix: Fixed a bug in reading the requirements from the file system.**


[38280276b961e60](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/38280276b961e60) John Ferguson Smart *2015-03-12 12:03:40*

**Removed redundant test**


[73ce792b29c26b4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/73ce792b29c26b4) John Ferguson Smart *2015-03-12 06:00:33*

**test: Temporarily disabling a test that doesn't work on the build server pending further investigation**


[dbddf6df434355f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/dbddf6df434355f) John Ferguson Smart *2015-03-12 05:58:22*

**test: Use phantomjs to check implicit timeouts more realisticly**


[5a46d718876a96a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5a46d718876a96a) John Ferguson Smart *2015-03-12 05:53:12*

**Rewrote much of the timeout APIs**


[204900f5f48211a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/204900f5f48211a) John Ferguson Smart *2015-03-12 05:52:52*

**Added tests to doument implicit wait behavior**


[9e653329dd691a5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e653329dd691a5) John Ferguson Smart *2015-03-08 22:11:14*


## v1.0.38
### No issue

**test: Temporarily disabling a test that doesn't work on the build server pending further investigation**


[0c23f3a8c26b06e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0c23f3a8c26b06e) John Ferguson Smart *2015-03-08 23:00:26*

**test: Use phantomjs to check implicit timeouts more realisticly**


[536bfdf46cac5b0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/536bfdf46cac5b0) John Ferguson Smart *2015-03-08 22:53:28*

**test: Added sample JSON test data**


[4bfdb9133300e0e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4bfdb9133300e0e) John Ferguson Smart *2015-03-06 01:34:34*

**test: Added sample JSON test data**


[219441fb70fb4b9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/219441fb70fb4b9) John Ferguson Smart *2015-03-06 01:34:34*

**Added test data for a sample pending report**


[cd09406dcb34a96](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd09406dcb34a96) John Ferguson Smart *2015-03-06 01:25:56*

**Added test data for a sample pending report**


[c191b5a2b3d0a8c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c191b5a2b3d0a8c) John Ferguson Smart *2015-03-06 01:25:56*

**Added test data for a sample pending report**


[66801ffbbc769e4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/66801ffbbc769e4) John Ferguson Smart *2015-03-06 01:25:56*

**Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file.**


[ac60be617fe6dd1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ac60be617fe6dd1) John Ferguson Smart *2015-03-06 01:03:47*

**Fixed an issue with Cucumber requirements reporting when the name of the feature differs from the name of the feature file.**


[a6d6cc62c576351](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a6d6cc62c576351) John Ferguson Smart *2015-03-06 01:03:47*

**Update WhenLoadingTestDataFromACSVFile.java**

 * Added all possible parameters for CSVReader to be able to parse special chars like \n \t ... 

[d2a20188ab24fd4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d2a20188ab24fd4) Hovhannes *2015-03-05 10:52:26*

**Update WhenLoadingTestDataFromACSVFile.java**

 * Added all possible parameters for CSVReader to be able to parse special chars like \n \t ... 

[8043809da640f99](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8043809da640f99) Hovhannes *2015-03-05 10:52:26*

**Update CSVTestDataSource.java**

 * Added all possible parameters for CSVReader to be able to parse special chars like \n \t ... 

[df893d20c253dbc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/df893d20c253dbc) Hovhannes *2015-03-05 10:50:46*

**Update CSVTestDataSource.java**

 * Added all possible parameters for CSVReader to be able to parse special chars like \n \t ... 

[aa1c3ed0fa00fc7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/aa1c3ed0fa00fc7) Hovhannes *2015-03-05 10:50:46*

**Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts.**


[cd9d78657ed78ab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cd9d78657ed78ab) John Ferguson Smart *2015-03-04 12:12:09*

**Fixed an issue in the reports where pending test results were not being accurately reported in the pie charts.**


[326a643ba2bcb0d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/326a643ba2bcb0d) John Ferguson Smart *2015-03-04 12:12:09*


## v1.0.37
### No issue

**test:Made a unit test more readable**


[b3d38fb9a30fd74](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b3d38fb9a30fd74) John Ferguson Smart *2015-03-03 01:05:26*

**Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy**


[403003dfbac409d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/403003dfbac409d) John Ferguson Smart *2015-03-03 00:58:29*

**Refactored the dependencies to use both the group and the module names in exclusions, to make the Maven Enforcer plugin happy**


[9d25a1aa46fcfe3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9d25a1aa46fcfe3) John Ferguson Smart *2015-03-03 00:06:06*

**Fixed an issue that had broken the async timeout behavior in the setScriptTimeout() method**


[fe952b944bf628d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe952b944bf628d) John Ferguson Smart *2015-03-02 22:21:05*


## v1.0.36
### No issue

**Standardized the Groovy version used throughout the build to 2.3.6**


[d21e03e66f56d86](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d21e03e66f56d86) John Ferguson Smart *2015-03-02 21:13:00*

**Updated to Selenium 2.45.0**


[e8c1a874c9030db](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e8c1a874c9030db) John Ferguson Smart *2015-02-28 01:52:47*

**test: Refactored a few tests to reduce sporadic errors**


[71fcf22e7fe7693](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/71fcf22e7fe7693) John Ferguson Smart *2015-02-23 07:28:20*

**test: ensured that HTMLUnit tests closed the drivers to avoid memory leaks during the build.**


[3026d248d044014](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3026d248d044014) John Ferguson Smart *2015-02-23 01:33:06*


## v1.0.35
### No issue

**fix: Fixed an issue in which tests were slowed down after a failing step because Serenity continued to try to take screenshots**


[d7f4cd3ab1d16d1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d7f4cd3ab1d16d1) John Ferguson Smart *2015-02-20 12:41:23*


## v1.0.34
### No issue

**test:Updated some unit tests**


[e20146db9d8e882](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e20146db9d8e882) John Ferguson Smart *2015-02-20 04:51:34*

**test:Updated some unit tests**


[2d48ba34363f7e1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2d48ba34363f7e1) John Ferguson Smart *2015-02-20 04:26:36*

**feat: Distinguish between element-level timing and "wait-for"-style timing.**


[b55c8cd17404b9a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b55c8cd17404b9a) John Ferguson Smart *2015-02-20 03:06:34*

**fix: Fixed a bug where the reports fail to generate if there are skipped test results in the outcomes.**


[2cb5e77f4aa71a6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2cb5e77f4aa71a6) John Ferguson Smart *2015-02-20 03:06:00*

**feat: Improved the readability of parameters in the screenshot pages.**


[924764f8f9f38eb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/924764f8f9f38eb) John Ferguson Smart *2015-02-20 03:05:16*

**feat: You can now distinguish between AJAX element waits (defaults to 500 ms) and explicit fluent waits (which default to 5 seconds)**


[a1dba09cd2737da](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a1dba09cd2737da) John Ferguson Smart *2015-02-19 12:13:39*


## v1.0.33
### No issue

**fix: Tidied up dependencies used by the other serenity modules**


[4931d367a7d3e80](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4931d367a7d3e80) John Ferguson Smart *2015-02-19 06:15:51*

**fix: Tidied up dependencies used by the other serenity modules**


[23d27526cd201f3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/23d27526cd201f3) John Ferguson Smart *2015-02-19 06:05:39*

**fix: Tidied up dependencies used by the other serenity modules**


[931e476b086f4a0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/931e476b086f4a0) John Ferguson Smart *2015-02-19 04:43:10*


## v1.0.32
### GitHub #23 

**Fixed issue #23**


[26f09b00e71da04](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26f09b00e71da04) John Ferguson Smart *2015-02-16 11:30:09*


### No issue

**fix: fixed an issue loading the JSON test reports during aggregate report generation.**


[93b836f8f2a811e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/93b836f8f2a811e) John Ferguson Smart *2015-02-19 01:11:44*

**fix: Removed dependency conflicts in the Gradle build.**


[5894af6eb234394](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5894af6eb234394) John Ferguson Smart *2015-02-19 01:11:04*

**feat: nested page objects i.e. widget objects**

 * WidgetObject: reusable page fragment with a nested search context implied by the Composition pattern.  This feature was requested here: 
 * https://groups.google.com/forum/#!topic/thucydides-users/-SiQwD86W8I 
 * https://groups.google.com/forum/#!topic/thucydides-users/01oNgOD9TnY 
 * See attached unit tests for usage examples. 

[388304241495e0c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/388304241495e0c) CoeJoder *2015-02-17 08:16:40*

**removed duplicate test model**


[072b8de691e74fc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/072b8de691e74fc) CoeJoder *2015-02-15 23:01:51*

**feat: Lists of WebElementFacade and subtypes as PageObject members.**


[7ba089050433c51](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7ba089050433c51) CoeJoder *2015-02-15 22:52:13*

**Fixed a bug where if a null value was stored in the Serenity session after a failing step, a null pointer exception was thrown.**


[7094f8dc6dd6bae](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7094f8dc6dd6bae) John Ferguson Smart *2015-02-14 00:07:25*


## v1.0.31
### No issue

**Added support for displaying Saucelabs configuration in the build info screen.**


[e78dd2cfdd98e23](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e78dd2cfdd98e23) John Ferguson Smart *2015-02-13 04:01:39*

**Made table formatting more robust by providing support for unicode brackets and new line chars.**


[56f672a7f8d5941](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/56f672a7f8d5941) John Ferguson Smart *2015-02-13 04:01:13*

**Removed redundant Jackson adaptor classes**


[4b6696672cfa002](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4b6696672cfa002) John Ferguson Smart *2015-02-13 04:00:20*

**Use Durations rather than longs and ints to handle timeout values, in order to avoid coding errors, make the code clearer, and as a basis for more flexible timeout configuration.**


[11b988b4948ee76](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/11b988b4948ee76) John Ferguson Smart *2015-02-12 22:25:29*


## v1.0.30
### No issue

**Use Java NIO to copy report resources.**


[5f4b87c9b97869e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5f4b87c9b97869e) John Ferguson Smart *2015-02-12 10:37:05*

**Refactored the PageObject class for better backward compatibility.**


[826c30fb0ea0c99](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/826c30fb0ea0c99) John Ferguson Smart *2015-02-12 10:36:44*


## v1.0.29
### No issue

**Made a test cross-platform**


[26cce2dce32adc6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/26cce2dce32adc6) John Ferguson Smart *2015-02-12 04:47:19*

**Added a page to the reports containing system and configuration properties and browser capabilities for a given test run.**

 * The browser used for each test is also recorded and displayed as an icon on the test report pages. 
 * You can also add your own custom fields into the build information page. You do this by adding properties with the &quot;sysinfo&quot; prefix to your serenity.properties file. These variables take Groovy expressions, which will be evaluated when the report is run, e.g: 
 * sysinfo.theAnswer = 6*7 
 * sysinfo.homeDir = System.getenv(&quot;HOME&quot;) 

[fe1ab3e2ce34859](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe1ab3e2ce34859) John Ferguson Smart *2015-02-12 04:39:05*

**Made the JSON tests a bit more robust**


[828c57af675ff9d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/828c57af675ff9d) John Ferguson Smart *2015-02-11 20:01:40*

**Chrome no longer opens a new window when you specify the browser size.**

 * Also, the browser is now automatically positioned in the top left hand corner of the screen. 
 * Signed-off-by: John Ferguson Smart &lt;john.smart@wakaleo.com&gt; 

[8fee3ad84895083](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8fee3ad84895083) John Ferguson Smart *2015-02-11 19:51:18*

**Migrated the PageObject class to the serenitybdd namespace.**

 * Signed-off-by: John Ferguson Smart &lt;john.smart@wakaleo.com&gt; 

[9784811403dd789](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9784811403dd789) John Ferguson Smart *2015-02-11 11:36:51*

**Removed Jackson dependencies**


[8b360c130452d8c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8b360c130452d8c) John Ferguson Smart *2015-02-11 05:14:07*

**Removing Jackson**


[7020e788a36a4c2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7020e788a36a4c2) John Ferguson Smart *2015-02-10 10:44:32*

**Fixed a bug that prevented the proper use of commands like 'webdriver.manage().window().setSize(new Dimension(1600, 1200));'**


[f4ccaf8310b5ddf](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4ccaf8310b5ddf) John Ferguson Smart *2015-02-10 07:11:19*


## v1.0.28
### No issue

**Working again after serenity package rename**


[80e0099cf475874](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80e0099cf475874) Ben Scott *2015-01-20 19:45:24*

**Updated release notes**


[25e0cd1393bcc92](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/25e0cd1393bcc92) Snap CI Build Bot *2015-12-29 08:49:53*

**refactor: PageObject still returns thucydides WebElementFacadeImpl so that can be cast to serenitybdd namespace**

 * This will need to be cleaned up when the thucydides namespace is retired. 

[3a71aaea630baf7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3a71aaea630baf7) Mike Wilkes *2014-12-28 16:05:37*

**style: fix typo in logging**


[f9d713e343d9380](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f9d713e343d9380) Mike Wilkes *2014-12-28 15:40:46*

**refactor: Create serenitybdd version of WebElementFacade classes/interfaces**

 * Deprecate Thucydides versions, but still handle them correctly 

[2648daa127fe42d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2648daa127fe42d) Mike Wilkes *2014-12-28 03:12:04*

**refactor: Move tests from thucydides to serenitybdd package**


[2bde33abd91afd7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2bde33abd91afd7) Mike Wilkes *2014-12-28 03:02:58*

**Improved release notes to avoid empty tags**


[7edba47608a800c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7edba47608a800c) John Ferguson Smart *2014-12-23 09:48:52*

**Improved release notes to avoid empty tags**


[9e47250a7a37d86](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e47250a7a37d86) John Ferguson Smart *2014-12-23 09:48:05*

**Updated release notes**


[948caa8ad7924d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/948caa8ad7924d4) Snap CI Build Bot *2014-12-23 07:38:26*

**Updated the README file to reflect the new commit conventions**


[71d6c5a562d886d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/71d6c5a562d886d) John Ferguson Smart *2014-12-23 07:03:38*

**Updated release notes**


[8208430d61bec12](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8208430d61bec12) Snap CI Build Bot *2014-12-23 05:54:04*

**chore: Automated the generation of the release notes from the git commits**


[80ee2cfb7f92285](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80ee2cfb7f92285) John Ferguson Smart *2014-12-23 05:36:02*

**Fixed issues with identifying appium driver**


[911799b02a2d987](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/911799b02a2d987) Ben Scott *2014-12-11 22:32:38*

**SmartAnnotation needs platform for Appium annotations to work**


[e5a13c7723cb73c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e5a13c7723cb73c) Ben Scott *2014-11-11 21:40:43*

**Support for appium annotations, added accessibility and ui automation for IOS and android**


[69252742737e848](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/69252742737e848) Ben Scott *2014-11-11 11:55:00*

**Missed change to PathProcessor**


[52f0eeadcfc82d2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/52f0eeadcfc82d2) Ben Scott *2014-11-10 11:58:24*

**Porting changes from thucydides appium-driver-support**


[e84ac40f8da7831](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e84ac40f8da7831) Ben Scott *2014-11-10 11:56:57*


## v1.0.27
### No issue

**Now you can use the -Dserenity.dry.run=true option to skip step executions - useful when testing JBehave or Cucumber step definitions**


[b52b55a39a9d501](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b52b55a39a9d501) John Ferguson Smart *2015-02-02 04:59:51*


## v1.0.26
### GitHub #16 

**Upgrade javassist version to match transitive dep. #16**

 * core&#39;s version was 3.9.0.GA; reflections version is 3.12.1.GA 

[c9f95050aadcd98](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c9f95050aadcd98) Jeff Jensen *2015-01-21 19:10:53*


### No issue

**Performance improvements: forces WebDriver to force an immediate response for findElements() calls if no matching elements are found, and some other minor improvements.**

 * Also improved step logging to include the test class and method as well as the step name. 
 * Signed-off-by: John Ferguson Smart &lt;john.smart@wakaleo.com&gt; 

[068315f4f0e63d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/068315f4f0e63d4) John Ferguson Smart *2015-01-30 08:24:07*

**Made the log messages for each step include the calling class and method.**

 * Signed-off-by: John Ferguson Smart &lt;john.smart@wakaleo.com&gt; 

[f9d996950d02e31](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f9d996950d02e31) John Ferguson Smart *2015-01-30 04:19:48*

**Fix to remove 'Session ID is null. Using WebDriver after calling quit()?' messages appearing when the tests are run in threads**


[afaf0b947f97a8c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/afaf0b947f97a8c) John Ferguson Smart *2015-01-24 09:43:58*

**Refactored optional Spring dependencies into the serenity-spring module - include this module if you want Serenity to honor Spring annotations and dependency injection**


[a2d3a0f17b4ad20](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a2d3a0f17b4ad20) John Ferguson Smart *2015-01-22 07:32:34*


## v1.0.25
### GitHub #16 

**Upgrade groovy-all version for transitive convergence #16.**


[3144ad12699cd6a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3144ad12699cd6a) Jeff Jensen *2015-01-19 22:36:02*

**Upgrade SLF4J version for transitive convergence #16.**


[392bc01b88be7b4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/392bc01b88be7b4) Jeff Jensen *2015-01-19 22:36:00*

**core build: exclude transitive deps with convergence problems. #16**

 * Declare additional transitive deps. 

[099d1189d1c5d0c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/099d1189d1c5d0c) Jeff Jensen *2015-01-19 22:35:58*

**Top build: declare transitives as deps. #16**


[197fab566647c12](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/197fab566647c12) Jeff Jensen *2015-01-19 22:35:56*

**Top build: fail fast on dependency convergence problems. #16**

 * Added &quot;force version&quot; on transitive versions with convergence 
 * problems. 
 * Issue: While this works to keep gradle build clean, it doesn&#39;t 
 * affect the generated POM/install for clients. 

[22d5395e9df2cbc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/22d5395e9df2cbc) Jeff Jensen *2015-01-19 22:35:54*

**Build: Add plugins that help with dep versions. #16**

 * - project-report: 
 * - gradlew htmlDependencyReport creates HTML dep report that shows 
 * which deps the build managed to different version. 
 * - com.github.ben-manes.versions: 
 * - gradlew dependencyUpdates shows deps with new versions 

[7a267aa8399a3dd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a267aa8399a3dd) Jeff Jensen *2015-01-19 22:03:42*

**Upgrade commons-lang3 to htmlunit's dep version. #16**

 * HtmlUnit uses 3.3.2, Serenity was using 3.1. 

[70325bb74775cb3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/70325bb74775cb3) Jeff Jensen *2015-01-19 15:57:19*

**Upgrade htmlunit to Selenium's dep version. #16**

 * Selenium uses 2.15, Serenity was using 2.9. 

[ceb0c1d103411a9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ceb0c1d103411a9) Jeff Jensen *2015-01-19 04:59:01*


### No issue

**Provide better support for step-level error reporting in Cucumber.**


[89f6ca56633ed1c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/89f6ca56633ed1c) John Ferguson Smart *2015-01-20 12:03:46*

**Tidied up some dependencies.**


[52e64aef5ebbe28](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/52e64aef5ebbe28) John Ferguson Smart *2015-01-20 12:02:55*

**Extracted dependency injection into an external module, to make it easier to add additional dependency injection modules later**


[003791a889e2988](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/003791a889e2988) John Ferguson Smart *2015-01-19 23:29:28*


## v1.0.24
### No issue

**Release notes are now triggered manually before the release**


[d9a768af4b3eb2a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d9a768af4b3eb2a) John Ferguson Smart *2015-01-18 05:59:47*

**Updated release notest**


[c36529114af5daa](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c36529114af5daa) John Ferguson Smart *2015-01-18 05:52:29*

**Migrated the default output directory to target/site/serenity**


[7c429c02e9f8522](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7c429c02e9f8522) John Ferguson Smart *2015-01-18 04:58:41*

**Migrated the default output directory to target/site/serenity**


[82b98664486be5d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/82b98664486be5d) John Ferguson Smart *2015-01-18 04:57:48*

**Added support for better Cucumber reporting**


[97156bd6c56b571](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/97156bd6c56b571) John Ferguson Smart *2015-01-18 04:32:18*

**Updated release notes**


[5ea5e898b1bcab7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5ea5e898b1bcab7) Snap CI Build Bot *2015-01-16 05:38:39*

**Restored release notes**


[88dbe9c8342f0d8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/88dbe9c8342f0d8) John Ferguson Smart *2015-01-16 05:27:00*

**Make sure the release notes are produced dynamically**


[9716bc56de482ff](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9716bc56de482ff) John Ferguson Smart *2015-01-16 05:05:34*

**Added extra support for handling Cucumber example tables**


[9e9711d48eca8bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9e9711d48eca8bb) John Ferguson Smart *2015-01-16 04:48:35*

**Simplified dependencies a little**


[e3ce499a6d4f91e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e3ce499a6d4f91e) John Ferguson Smart *2015-01-14 22:37:28*

**WIP**


[7cb2a81cae949bd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7cb2a81cae949bd) John Ferguson Smart *2015-01-07 18:09:02*


## v1.0.23
### No issue

**You can now use serenity.* instead of thucydides.* system properties. The thucydides.* system properties are still supported, but a warning is printed to the logs.**


[8d8b0bf5fb05fce](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8d8b0bf5fb05fce) John Ferguson Smart *2014-12-22 04:14:24*

**rename serenity_bdd to serenitybdd**


[cfaae5a78a36fbb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/cfaae5a78a36fbb) Mike Wilkes *2014-12-21 21:49:10*


## v1.0.22
### No issue

**Move junit finder classes to serenity_bdd package**


[3443435570d0e97](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3443435570d0e97) Mike Wilkes *2014-12-21 02:57:37*

**Rename package in demo to serenity_bdd**


[7bde2389379fa22](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7bde2389379fa22) Mike Wilkes *2014-12-21 02:55:12*

**SerenityRunner and SerenityParameterizedRunner now contain functionality, and Thucydides equivalents merely extend**

 * Also move a number of helper classes into serenity_bdd package 

[2aa92f97522d705](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2aa92f97522d705) Mike Wilkes *2014-12-21 02:54:19*

**Move JUnit runners to serenity_bdd package**


[b94933d99cc7e9f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b94933d99cc7e9f) Mike Wilkes *2014-12-21 02:22:51*


## v1.0.21
### No issue

**Improvements to the reports**


[5fc6c9a9e3e0b7c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5fc6c9a9e3e0b7c) John Ferguson Smart *2014-12-16 00:38:25*

**Improvements to the reports**


[c31cb4f4b17a086](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c31cb4f4b17a086) John Ferguson Smart *2014-12-15 23:47:21*


## v1.0.18
### No issue

**Added better support for comments in feature files, and better formatting in the 'Related Tabs' table**


[6ee7578c7e241b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6ee7578c7e241b6) John Ferguson Smart *2014-12-15 04:26:22*

**Hardening unit tests**


[9b7e9c43d7f6bab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/9b7e9c43d7f6bab) John Ferguson Smart *2014-12-14 22:22:51*

**Updated reporting, attempt 2**


[199e60a595c0830](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/199e60a595c0830) bmwsedee *2014-12-13 17:41:41*


## v1.0.17
### No issue

**Added tool tips on the 'Related Tags' tables**


[602eaf8dfe8633e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/602eaf8dfe8633e) John Ferguson Smart *2014-12-13 00:38:07*

**Undid javascript library updates and added the number of tests for tags on the reports**


[a05b31ffb0928e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a05b31ffb0928e9) John Ferguson Smart *2014-12-12 12:19:58*

**Revert "Updated libraries"**

 * This reverts commit 44ec91e92d90ebc3742a6221f82d1a404b1baa57. 

[0a272c47a9a49f2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0a272c47a9a49f2) John Ferguson Smart *2014-12-12 08:43:42*

**Revert "Update reports to use new libraries"**

 * This reverts commit f4a75422ecfc46a66fb5ebb617ce808c299a6d4b. 

[f8f476230acb6e8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f8f476230acb6e8) John Ferguson Smart *2014-12-12 08:43:37*

**Revert "Refactoring to facilitate easier migrating to new versions of the libraries"**

 * This reverts commit 6f12e5389a8499e2f9f9b69478b329f90367d4fb. 

[d017a61caa8d820](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d017a61caa8d820) John Ferguson Smart *2014-12-12 08:43:33*

**Revert "Updated excanvas"**

 * This reverts commit 5d55b1eae5d424b7185ed1aab68ab6f36c53cbf6. 

[a25fed4b5fe3830](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a25fed4b5fe3830) John Ferguson Smart *2014-12-12 08:43:27*

**Revert "Updated JavaScript InfoVis Toolkit"**

 * This reverts commit a3c95dc54f1165c5ea00fcb2719f14a63acba604. 

[b49d68030bb88d0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b49d68030bb88d0) John Ferguson Smart *2014-12-12 08:43:23*

**Revert "Removed old versions of libraries"**

 * This reverts commit 7b26344dea3c0ee710ee90fe7040141a6941f97f. 

[1b62cb0a07240b4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1b62cb0a07240b4) John Ferguson Smart *2014-12-12 08:43:16*

**Removed old versions of libraries**


[7b26344dea3c0ee](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7b26344dea3c0ee) bmwsedee *2014-12-09 23:27:56*

**Updated JavaScript InfoVis Toolkit**


[a3c95dc54f1165c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/a3c95dc54f1165c) bmwsedee *2014-12-09 23:26:46*

**Updated excanvas**


[5d55b1eae5d424b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d55b1eae5d424b) bmwsedee *2014-12-09 23:15:28*

**Refactoring to facilitate easier migrating to new versions of the libraries**


[6f12e5389a8499e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6f12e5389a8499e) bmwsedee *2014-12-09 23:12:38*

**Update reports to use new libraries**


[f4a75422ecfc46a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f4a75422ecfc46a) bmwsedee *2014-12-09 22:41:31*

**Updated libraries**


[44ec91e92d90ebc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/44ec91e92d90ebc) bmwsedee *2014-12-09 22:36:04*


## v1.0.16
### No issue

**Improved requirement reporting for JUnit (experimental)**


[18d5f80d55e8b83](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/18d5f80d55e8b83) John Ferguson Smart *2014-12-09 01:03:42*

**This small change makes Serenity compatible with Firefox version 32 or greater**

 * Guava 18.0 is already specified in Gradle. 

[6376d9951792d7b](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6376d9951792d7b) marcin-caban *2014-12-09 00:46:21*


## v1.0.15
### No issue

**Improved reporting of JUnit tests as requirements**


[892b4fe6a8d0fab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/892b4fe6a8d0fab) John Ferguson Smart *2014-12-08 08:27:31*


## v1.0.14
### No issue

**Switched back to JUnit 4.11 due to API incompatibilities with build tools**


[d5f35b9cf08b4e6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5f35b9cf08b4e6) John Ferguson Smart *2014-12-05 04:32:32*


## v1.0.13
### No issue

**The @Pages annotated field in JUnit tests is now optional**


[7cbe55192607ef2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7cbe55192607ef2) John Ferguson Smart *2014-12-05 02:22:54*

**Upgraded to JUnit 4.12**


[3d985d15871a528](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3d985d15871a528) John Ferguson Smart *2014-12-05 01:25:58*


## v1.0.12
### No issue

**Solidified a test**


[1290a90ccf2c6c3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1290a90ccf2c6c3) John Ferguson Smart *2014-12-04 07:42:31*


## v1.0.12-rc.1
### No issue

**Added better support for radio buttons in the PageObject class**


[878c2a1edb79d85](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/878c2a1edb79d85) John Ferguson Smart *2014-12-04 06:49:36*

**Use gradle-git for version and tagging**

 * === If local repository is dirty 
 * -Always builds a SNAPSHOT version. 
 * -Will complain that &#39;Stage {} is not one of [SNAPSHOT] allowed for strategy snapshot.&#39; 
 * === If local repository is not dirty 
 * Set release type using property release.stage. Valid values are: 
 * -milestone 
 * -rc 
 * -final 
 * milestone creates a tag with the next version from tag + -milestone.# 
 * rc similar, but uses rc. Cannot create a milestone after an rc 
 * final creates a version with no endings 
 * If want to use ssh authorization, must ensure ssh-agent contains correct key for repository being worked on. 
 * If you experience issues, try ssh-add -D to clear identities and add the one identity for the repo in question. 
 * The release tags the current commit, and pushes to the remote repository. It does not check if there&#39;s a new commit, so only use it if you really need to. 
 * gradle bintrayUpload release -Prelease.stage={milestone|rc|final} 

[0902fc79603d4f0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0902fc79603d4f0) Mike Wilkes *2014-12-02 03:56:15*

**Fix scm url's**

 * All were pointing at project.name, when in fact they all exist in the same 
 * serenity-core repository 

[e0a96d7cd7499a4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e0a96d7cd7499a4) Mike Wilkes *2014-12-02 02:49:59*

**Correct issue with publishing**

 * Main project doesn&#39;t have anything to deploy, and doesn&#39;t have config. This 
 * causes a warning when building the project. 
 * Provide the config that is common across all projects in this config file, 
 * but no config for the main project is required. 

[6d6327665844b25](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d6327665844b25) Mike Wilkes *2014-12-02 02:47:46*

**Remove unused files**

 * It would appear that the main project was moved into core sub-directory, and 
 * these files didn&#39;t get cleaned up. 

[3ee866cd987cfb1](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3ee866cd987cfb1) Mike Wilkes *2014-12-02 02:46:15*

**[namespace] Move Find annotations to serenity_bdd namespace**

 * Create deprecated versions in thucydides namespace but with 2 on name to ensure 
 * caught all changes, and returning objects of correct classes. 
 * Also kept deprecated versions of tests to ensure old versions still work correctly 

[ed62753b69b522f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ed62753b69b522f) Mike Wilkes *2014-12-01 04:00:53*

**Made the Ant plugin a bit more robust.**


[47542e1b4cfc29c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/47542e1b4cfc29c) John Ferguson Smart *2014-11-28 19:53:59*

**Moved the ant plugin over to the new Serenity namespace**


[c0a1aa089cd72af](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c0a1aa089cd72af) John Ferguson Smart *2014-11-27 05:35:39*

**[migrate] Move exceptions**


[2ed2864f88aaf29](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2ed2864f88aaf29) Mike Wilkes *2014-11-27 03:03:14*

**[migrate] Move SessionMap**


[ad3a486ced855de](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad3a486ced855de) Mike Wilkes *2014-11-27 03:00:27*

**[rename] Create Serenity namespaced class and move some associated test classes**


[d84aeede8457858](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d84aeede8457858) Mike Wilkes *2014-11-27 02:58:41*

**[rename] Create Serenity namespaced class, deprecate Thucydides version and delegate functions**


[3705ee4ffed330e](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3705ee4ffed330e) Mike Wilkes *2014-11-27 02:41:51*

**[rename] Move SerenityListeners and create deprecated ThucydidesListeners**


[8bdaf7db8f1f501](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8bdaf7db8f1f501) Mike Wilkes *2014-11-27 02:39:46*

**Display error messages for ignored steps, so that failing assumption messages are correctly displayed**


[61cc4d855d5a0ef](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/61cc4d855d5a0ef) John Ferguson Smart *2014-11-26 19:45:47*

**Updated banners**


[04cace4c7b9b053](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/04cace4c7b9b053) John Ferguson Smart *2014-11-26 10:23:30*

**Move Serenity to new package**


[be15eb47c729538](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/be15eb47c729538) Mike Wilkes *2014-11-25 19:44:51*

**Rename main class to reflect new project name, and deprecate old**

 * Eventually, all Thucydides references will be removed. 

[581dd4753b647b3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/581dd4753b647b3) Mike Wilkes *2014-11-25 18:56:30*

**Updated the Ascii Art banner.**


[40a532d21efa776](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/40a532d21efa776) John Ferguson Smart *2014-11-25 16:17:05*


## v1.0.9
### No issue

**Integrated better support for JBehave**


[09927b0fda489ce](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/09927b0fda489ce) John Ferguson Smart *2014-11-23 11:15:04*

**Integrated better support for JBehave**


[b3340e5d3756a26](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b3340e5d3756a26) John Ferguson Smart *2014-11-23 10:45:00*

**Changed the 'checkOutcomes' task to force it to run the tests first.**


[5ea5b718068a34f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5ea5b718068a34f) John Ferguson Smart *2014-11-23 10:44:33*


## v1.0.8
### No issue

**Enable selection of Mac Os version on SauceLabs**


[e1956cfd278a505](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/e1956cfd278a505) Dumitru Negruta *2014-11-22 08:09:57*

**Added support for the AssumeThat method for JUnit tests - AssumeThat will result in a test being displayed as 'ignored' in the reports.**


[8344474fc2d7c23](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8344474fc2d7c23) John Ferguson Smart *2014-11-22 07:44:48*

**Enable selection of Mac Os version on SauceLabs**


[40db746819856e7](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/40db746819856e7) Dumitru Negruta *2014-11-21 08:53:14*

**Removed some code that used the JDK 8 libraries**


[eb4608f6d8c1818](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/eb4608f6d8c1818) John Ferguson Smart *2014-11-20 12:40:09*

**Updated to Selenium 2.44.0**


[c12c6ddc076bcb8](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c12c6ddc076bcb8) John Ferguson Smart *2014-11-19 08:17:06*

**Updated the changelog to reflect the serenity-core repo. For Bintray this is a bit of a hack, since the BinTray serenity-core package gets artifacts from two repos, serenity-core and serenity-maven-plugin, which are separate only because of the fact that core needs to be built and deployed before the maven plugin generation task in the serenity-maven-plugin build can be done. So the changelogs will be up-to-date on Github for both repos, but the one on bintray will only reflect core.**


[308ec8f50c5dbcc](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/308ec8f50c5dbcc) John Ferguson Smart *2014-11-17 09:21:16*

**Adding an automatically generated change log to the build**


[50c45e31c5432cd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/50c45e31c5432cd) John Ferguson Smart *2014-11-17 09:12:31*


## v1.0.7
### Jira UTF-8 

**If javadoc is not told to expect UTF-8 in the strings it uses can generate ASCII errors on at least the Mac.**


[4494dee65ac0b1f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4494dee65ac0b1f) Peter Tillemans *2014-11-14 09:15:03*


### No issue

**Refactored the gradle plugin**


[00de150f4da3aab](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/00de150f4da3aab) John Ferguson Smart *2014-11-17 08:02:39*

**Fixed a bug where error messages were incorrectly displayed in the step details**


[66556bb4e71cf65](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/66556bb4e71cf65) John Ferguson Smart *2014-11-14 09:44:02*

**jbehave was pulling in hamcrest 1.1. Excluded hamcrest from the jbehave dependency.**


[6d0f8ee7d7ee3c2](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6d0f8ee7d7ee3c2) Peter Tillemans *2014-11-14 09:16:52*


## v1.0.6
### No issue

**Fixed some formatting and navigation issues in the reports**


[2f58c3b419c5330](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/2f58c3b419c5330) John Ferguson Smart *2014-11-14 06:51:31*


## v1.0.5
### No issue

**Added the Serenity helper class, as a replacement for the legacy 'Thucydides' class**


[6780200d8b74535](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/6780200d8b74535) John Ferguson Smart *2014-11-13 13:57:26*

**Fixed a bug in the reporting where duplicate story tags were displayed in the screenshot screens.**


[08b5502af44c08f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/08b5502af44c08f) John Ferguson Smart *2014-11-13 07:32:08*

**Logs a message indicating the path of the generated reports after report aggregation.**


[805dbf1a9bf72b6](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/805dbf1a9bf72b6) John Ferguson Smart *2014-11-13 07:31:38*

**Added the Serenity utility class, which exposes and delegates to methods of the legacy Thucydides class.**


[fe1c3c5eb2cee95](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/fe1c3c5eb2cee95) John Ferguson Smart *2014-11-13 07:27:09*

**Check if a resized file for a given screenshot already exists, and if so don't perform the resizing**


[4138f8900eb6259](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4138f8900eb6259) John Ferguson Smart *2014-11-12 19:24:52*

**Moved most uses of FileUtils to the Java 7 Files class, in order to remove sporadic issues when resizing screenshots**


[0e9d614b462448a](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/0e9d614b462448a) John Ferguson Smart *2014-11-12 10:09:31*


## v1.0.4
### No issue

**Fixed a failing test**


[7a65f64d3bd4d6f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/7a65f64d3bd4d6f) John Ferguson Smart *2014-11-11 17:01:01*

**Fine-tuning the reports**


[b42d58b33af6ea3](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b42d58b33af6ea3) John Ferguson Smart *2014-11-11 15:25:39*

**Refactored some tests**


[f07879ca4b94183](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f07879ca4b94183) John Ferguson Smart *2014-11-10 05:27:22*

**Cater for rare cases where the driver returns null when an element is not found**


[d5511b6706701d4](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/d5511b6706701d4) John Ferguson Smart *2014-11-10 01:18:08*

**Repositioned the report timestamp**


[36d471f7c2acdbd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/36d471f7c2acdbd) John Ferguson Smart *2014-11-09 20:04:06*

**Repositioned the report timestamp**


[80e1ef06258e1e5](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/80e1ef06258e1e5) John Ferguson Smart *2014-11-09 19:47:52*

**Added bootstrap tabs**


[c8fd3b94c1bd867](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/c8fd3b94c1bd867) John Ferguson Smart *2014-11-09 19:33:29*

**Added tests to the gradle plugin**


[4a132ad31b57d7f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4a132ad31b57d7f) John Ferguson Smart *2014-11-09 14:56:55*

**Added SerenityRunner and SerenityParameterizedRunner classes as alternative names for ThucydidesRunner and ThucydidesParameterizedRunner, more in line with the new naming schema.**


[98073bdbe5ff127](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/98073bdbe5ff127) John Ferguson Smart *2014-11-08 12:30:55*

**Moved the serenity-maven-plugin to a separate project**


[4c953d868707e2c](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4c953d868707e2c) John Ferguson Smart *2014-11-08 12:30:00*

**Getting the maven plugin build working**


[ad4800ebcf39afd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/ad4800ebcf39afd) John Ferguson Smart *2014-11-07 03:29:39*

**Fine-tuning the release tagging**


[74df0296738f380](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/74df0296738f380) John Ferguson Smart *2014-11-07 01:53:31*


## v1.0.2
### No issue

**Initial release version**


[527387e98a503f0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/527387e98a503f0) John Ferguson Smart *2014-11-06 21:48:58*

**Added a selector to find buttons by their label, e.g. find(By.buttonText('Add to cart'));**


[4a119f5eb78613d](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/4a119f5eb78613d) John Ferguson Smart *2014-11-06 05:17:02*

**Honor both 'thucydides.properties' and 'serenity.properties' files for local project configuration**


[8ba6aeb194a96bb](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/8ba6aeb194a96bb) John Ferguson Smart *2014-11-06 03:22:25*

**Let the bintray keys be defined by the build server**


[b5732dc3a744246](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/b5732dc3a744246) John Ferguson Smart *2014-11-06 00:04:12*

**Minor fix to work around an incompatiblity with IBM JDB 1.7**


[f2322d488bb19e9](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/f2322d488bb19e9) John Ferguson Smart *2014-11-05 21:00:53*

**Changed group to serenity-bdd to make syncing with Maven Central easier**


[5caf4a28cbcb818](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5caf4a28cbcb818) John Ferguson Smart *2014-11-05 20:40:13*

**Changed group to serenity-bdd to make syncing with Maven Central easier**


[5d3f58a217827dd](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/5d3f58a217827dd) John Ferguson Smart *2014-11-05 19:42:26*

**Fixed an issue in the BinTray deployment**


[1d7740dc9d007c0](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/1d7740dc9d007c0) John Ferguson Smart *2014-11-05 08:01:05*

**Fine-tuning the release pipeline**


[3620bc2af882c43](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/3620bc2af882c43) John Ferguson Smart *2014-11-05 07:41:13*

**Added more info to the README file**


[bc0e078f187ae7f](https://github.com/tomasbjerre/git-changelog-gradle-plugin/commit/bc0e078f187ae7f) John Ferguson Smart *2014-11-05 06:08:30*


