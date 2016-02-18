# CONTRIBUTING 

Everyone can take a part in making our product better, it can be done by submitting bug ticket or enhancement ticket, and best what can be done – submitting new pull request with changes that makes usage of product more efficient, add some feature or fix some reported bug.  Following next instructions make us much easy (and faster as well) fix submitted issue or review and include pull request. 
## Getting Started
* Submit a ticket for your issue, assuming one does not already exist.
* Clearly describe the issue including steps to reproduce when it is a bug, if it possible include code examples and screenshots.
* Make sure you fill in the earliest version that you know has the issue.
* If you feel yourself strong enough to implement fix - fork the repository on GitHub, and follow [Making Changes](#making_changes) to provide fix/update and pull it to product.

## <a name="making_changes"></a> Making Changes

It is easy to make changes, and test them as well. If you need some instructions about build this module – please take a look to [How to build](#how_to_build). 
* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Only target release branches if you are certain your fix must be on that
    branch.
  * To quickly create a topic branch based on master; `git checkout -b
    fix_my_contribution master`. Please avoid working directly on the
    `master` branch. 
* Make commits of logical units using [Commit message conventions](#commit_message_conventions).
* Check for unnecessary whitespace with `git diff --check` before committing.
* Make sure your commit messages are in the proper format.
* Make sure you have added the necessary tests for your changes, they can be written with groovy or java – as you like.
* Run _all_ the tests to assure nothing else was accidentally broken, for instructions additional instructions about build take a look to [How to build](#how_to_build).

## <a name="submitting_changes"></a> Submitting Changes
* Push your changes to a topic branch in your fork of the repository.
* Submit a pull request to the repository, please follow [Pull Request Template](#pull_request_template), it makes much easy to review updates and test them.
  * If automatic build enabled - you will find some status of testing this pullre quest. 
* After feedback has been given we expect responses within two weeks. After two  weeks we may close the pull request if it isn't showing any activity.

## <a name="commit_message_conventions"></a> Commit message conventions
Commit messages are used to generate the release notes for each release. To do this, we loosely follow the AngularJS commit conventions: for commit messages to appear in the release notes, the title line needs to respect the following format:
```
  <type>: <message>
```
examples:
```
chore: upgrade plugin
fix: report statistics
```
where <type> is one of the following:
  - feat: A new feature
  - fix: A bug fix
  - docs: Documentation only changes
  - style: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
  - refactor: A code change that neither fixes a bug or adds a feature
  - perf: A code change that improves performance
  - test: Adding missing tests
  - chore: Changes to the build process or auxiliary tools and libraries such as documentation generation
  
Also commits can consists of several lines - to include some additional information in relase notes. For example:
```  
feat: A new feature to make something better
now it will be available to call api.function() with additional parameters like api.function(Integer)
```
it will be included in release notes as:

 - feat: A new feature to make something better
     
     > now it will be available to call api.function() with additional parameters like api.function(Integer)

Please take a look at release notes to find some more examples of mutiline commits. 
  
Starting from version 1.1.26, any commits without one of these prefixes will not appear in the release notes.

## <a name="pull_request_template"></a> Pull Request Template
During creating pull request please use name conventions and short template for description. 

If Pull request related to some issue, or issues – name of it should starts from reference, like #241 for example. If no issues for this pull request just short title. Here is some examples for pull requests names:
```
Included test execution duration for #124
some:lib was updated to 2.45 for #435
Updated structure of configuration to for retries
```
**Remember**
almost all pull requests will be included in release notes, so name of pull requests is very important

PR body Template:

```
#### Summary of this PR
What does this pull request do in general terms? 
Does it fix something, add some feature or just change code style, etc. ? 
#### Intended effect
What is the expected effect of the pull request? 
Specifically, indicate how the behavior will be different than before the pull request.
#### How should this be manually tested?
How can reviewers verify the request has the intended effect?  
How it can be tested, can it be tested using serenity-demos project or some other repos? 
#### Side effects
Does the pull request contain any side effects? 
This should list non-obvious things that have changed in the request.
#### Documentation
If the pull request is user facing, how is it documented?
Are there examples of how to use the new behavior that users need to know about?
#### Relevant tickets
List of tickets, that are fixed or somehow related to this PR
#### Screenshots (if appropriate)
If this PR change something, that can change view of report or console output, etc – please include 
screenshots “what was before this PR” and how it will be changed”. 

```
examples:
* [Upgrading gradle and bintray plugin version](serenity-bdd/serenity-jbehave/pull/25) 
* [#243 Upgrading typesafe.config from 1.2 to 1.3](serenity-bdd/serenity-core/pull/248)

## <a name="how_to_build"></a> How to build
First of all make sure that your environment is ready to build this module. We actively use [Selenium](http://www.seleniumhq.org/) so Chrome, Firefox, Phantomsjs, chromedriver should be available in your PATH. Also java shuold be installed on your dev machine. 

#### Chrome 
To get instructions about installing Chrome use [offical site](https://www.google.com/chrome/). 
#### FireFox
To get instructions about installing FireFox use [offical site](https://www.mozilla.org/en-US/firefox/new/). 
It is possible to check if all fine with your installation, run in console
```
firefox
```
If executed without error - this part is ready. 

#### PhantomJs 
PhantomJs it is headless browser, it is sutable for writing tests as with real browsers and using less resources for such tests. 
To install phantomJs follow [instructions](http://phantomjs.org/download.html). 
It is possible to check if all fine with phantomJs just run in console
```
phantomjs -v
```
If executed without error - this part is ready. 

#### ChromeDriver

ChromeDriver can be downloaded from [site](https://sites.google.com/a/chromium.org/chromedriver/downloads), after it will be included in PATH you should check if all fine with versions of ChromeDriver/Chrome execute in console:
```
chromedriver
```
this command will run chromedriver as server and if output does not contains errors - chromedriver and Chrome are ready. 

For builds we use [gradle](http://gradle.org), and to achive that all developers will use same version - [wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) is configured.
So, now you are redy to build module. After you checkout it go to folder and execute:
```
./gradlew clean test integrationTests install
```
It will take some time... A lot of tests should execute before build can be marked successfull. Using gradle documenation you can configure executing only your tests.