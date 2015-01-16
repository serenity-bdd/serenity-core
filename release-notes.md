# Releases
- [v1.0.23](#v1.0.23)
- [v1.0.22](#v1.0.22)
- [v1.0.21](#v1.0.21)
- [v1.0.20](#v1.0.20)
- [v1.0.19](#v1.0.19)
- [v1.0.18](#v1.0.18)
- [v1.0.17](#v1.0.17)
- [v1.0.16](#v1.0.16)
- [v1.0.15](#v1.0.15)
- [v1.0.14](#v1.0.14)
- [v1.0.13](#v1.0.13)
- [v1.0.12-rc.1](#v1.0.12-rc.1)
- [1.0.10](#1.0.10)
- [v1.0.9](#v1.0.9)
- [v1.0.8](#v1.0.8)
- [v1.0.7](#v1.0.7)
- [v1.0.6](#v1.0.6)
- [v1.0.5](#v1.0.5)
- [v1.0.4](#v1.0.4)
- [v1.0.2](#v1.0.2)
# Upcoming<a name='Upcoming'></a>
## Style
  -  fix typo in logging
## Refactoring
  -  PageObject still returns thucydides WebElementFacadeImpl so that can be cast to serenitybdd namespace
This will need to be cleaned up when the thucydides namespace is retired.

  -  Create serenitybdd version of WebElementFacade classes/interfaces
Deprecate Thucydides versions, but still handle them correctly

  -  Move tests from thucydides to serenitybdd package
## Chores
  -  Automated the generation of the release notes from the git commits
## Miscellaneous
  - Make sure the release notes are produced dynamically
  - Added extra support for handling Cucumber example tables
  - Merged latest changes
  - Simplified dependencies a little
  - WIP
  - Updated release notes
  - Merge pull request #14 from mikezx6r/master
Serenity BDD version of WebElementFacade classes/interfaces
  - Improved release notes to avoid empty tags
  - Improved release notes to avoid empty tags
  - Updated release notes
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Updated the README file to reflect the new commit conventions
  - Updated release notes
# v1.0.23<a name='v1.0.23'></a>
  - You can now use serenity.* instead of thucydides.* system properties. The thucydides.* system properties are still supported, but a warning is printed to the logs.
  - Merge pull request #13 from mikezx6r/master
rename serenity_bdd to serenitybdd
  - rename serenity_bdd to serenitybdd
# v1.0.22<a name='v1.0.22'></a>
  - Merge pull request #12 from mikezx6r/master
More package renaming
  - Move junit finder classes to serenity_bdd package
  - Rename package in demo to serenity_bdd
  - SerenityRunner and SerenityParameterizedRunner now contain functionality, and Thucydides equivalents merely extend
Also move a number of helper classes into serenity_bdd package

  - Move JUnit runners to serenity_bdd package
# v1.0.21<a name='v1.0.21'></a>
# v1.0.20<a name='v1.0.20'></a>
# v1.0.19<a name='v1.0.19'></a>
  - Improvements to the reports
  - Improvements to the reports
# v1.0.18<a name='v1.0.18'></a>
  - Added better support for comments in feature files, and better formatting in the 'Related Tabs' table
  - Hardening unit tests
  - Merge pull request #11 from bmwsedee/master
Updated reporting, attempt 2
  - Updated reporting, attempt 2
# v1.0.17<a name='v1.0.17'></a>
  - Added tool tips on the 'Related Tags' tables
  - Undid javascript library updates and added the number of tests for tags on the reports
  - Revert "Updated libraries"
This reverts commit 44ec91e92d90ebc3742a6221f82d1a404b1baa57.

  - Revert "Update reports to use new libraries"
This reverts commit f4a75422ecfc46a66fb5ebb617ce808c299a6d4b.

  - Revert "Refactoring to facilitate easier migrating to new versions of the libraries"
This reverts commit 6f12e5389a8499e2f9f9b69478b329f90367d4fb.

  - Revert "Updated excanvas"
This reverts commit 5d55b1eae5d424b7185ed1aab68ab6f36c53cbf6.

  - Revert "Updated JavaScript InfoVis Toolkit"
This reverts commit a3c95dc54f1165c5ea00fcb2719f14a63acba604.

  - Revert "Removed old versions of libraries"
This reverts commit 7b26344dea3c0ee710ee90fe7040141a6941f97f.

  - Merge pull request #10 from bmwsedee/master
Updated the libraries used in the reporting
  - Removed old versions of libraries
  - Updated JavaScript InfoVis Toolkit
  - Updated excanvas
  - Refactoring to facilitate easier migrating to new versions of the libraries
  - Update reports to use new libraries
  - Updated libraries
# v1.0.16<a name='v1.0.16'></a>
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Improved requirement reporting for JUnit (experimental)
  - Merge pull request #9 from marcin-caban/patch-1
This small change makes Serenity compatible with Firefox version 32 or g...
  - This small change makes Serenity compatible with Firefox version 32 or greater
Guava 18.0 is already specified in Gradle.
# v1.0.15<a name='v1.0.15'></a>
  - Improved reporting of JUnit tests as requirements
# v1.0.14<a name='v1.0.14'></a>
  - Switched back to JUnit 4.11 due to API incompatibilities with build tools
# v1.0.13<a name='v1.0.13'></a>
  - Merge branch 'feature-junit-4.12' into make_pages_annotation_optional
  - The @Pages annotated field in JUnit tests is now optional
  - Upgraded to JUnit 4.12
  - Solidified a test
# v1.0.12-rc.1<a name='v1.0.12-rc.1'></a>
# 1.0.10<a name='1.0.10'></a>
  - Added better support for radio buttons in the PageObject class
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Merge pull request #8 from mikezx6r/grgit
Use gradle-git for version and tagging
  - Use gradle-git for version and tagging
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

  - Merge pull request #7 from mikezx6r/master
Gradle build cleanup
  - Fix scm url's
All were pointing at project.name, when in fact they all exist in the same
serenity-core repository

  - Correct issue with publishing
Main project doesn't have anything to deploy, and doesn't have config. This
causes a warning when building the project.

Provide the config that is common across all projects in this config file,
but no config for the main project is required.

  - Remove unused files
It would appear that the main project was moved into core sub-directory, and
these files didn't get cleaned up.

  - Merge pull request #6 from mikezx6r/master
[namespace] Move Find annotations to serenity_bdd namespace
  - [namespace] Move Find annotations to serenity_bdd namespace
Create deprecated versions in thucydides namespace but with 2 on name to ensure
caught all changes, and returning objects of correct classes.

Also kept deprecated versions of tests to ensure old versions still work correctly

  - Made the Ant plugin a bit more robust.
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Moved the ant plugin over to the new Serenity namespace
  - Merge pull request #5 from mikezx6r/master
More migration to the Serenity namespace
  - [migrate] Move exceptions
  - [migrate] Move SessionMap
  - [rename] Create Serenity namespaced class and move some associated test classes
  - [rename] Create Serenity namespaced class, deprecate Thucydides version and delegate functions
  - [rename] Move SerenityListeners and create deprecated ThucydidesListeners
  - Display error messages for ignored steps, so that failing assumption messages are correctly displayed
  - Updated banners
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Merge pull request #4 from mikezx6r/master
Rename main class to reflect new project name, and deprecate old
  - Move Serenity to new package
  - Rename main class to reflect new project name, and deprecate old
Eventually, all Thucydides references will be removed.

  - Updated the Ascii Art banner.
# v1.0.9<a name='v1.0.9'></a>
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Integrated better support for JBehave
  - Integrated better support for JBehave
  - Changed the 'checkOutcomes' task to force it to run the tests first.
# v1.0.8<a name='v1.0.8'></a>
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Enable selection of Mac Os version on SauceLabs
  - Added support for the AssumeThat method for JUnit tests - AssumeThat will result in a test being displayed as 'ignored' in the reports.
  - Merge pull request #3 from negruta/master
Enable selection of Mac Os version on SauceLabs
  - Enable selection of Mac Os version on SauceLabs
  - Removed some code that used the JDK 8 libraries
  - Updated to Selenium 2.44.0
  - Updated the changelog to reflect the serenity-core repo. For Bintray this is a bit of a hack, since the BinTray serenity-core package gets artifacts from two repos, serenity-core and serenity-maven-plugin, which are separate only because of the fact that core needs to be built and deployed before the maven plugin generation task in the serenity-maven-plugin build can be done. So the changelogs will be up-to-date on Github for both repos, but the one on bintray will only reflect core.
  - Adding an automatically generated change log to the build
# v1.0.7<a name='v1.0.7'></a>
  - Refactored the gradle plugin
  - Merge branch 'master' of github.com:serenity-bdd/serenity-core
  - Merge pull request #2 from ptillemans/pti_hamcrest_1_1
Pti hamcrest 1 1
  - Fixed a bug where error messages were incorrectly displayed in the step details
  - jbehave was pulling in hamcrest 1.1. Excluded hamcrest from the jbehave dependency.
  - If javadoc is not told to expect UTF-8 in the strings it uses can generate ASCII errors on at least the Mac.
# v1.0.6<a name='v1.0.6'></a>
  - Fixed some formatting and navigation issues in the reports
# v1.0.5<a name='v1.0.5'></a>
  - Added the Serenity helper class, as a replacement for the legacy 'Thucydides' class
  - Fixed a bug in the reporting where duplicate story tags were displayed in the screenshot screens.
  - Logs a message indicating the path of the generated reports after report aggregation.
  - Added the Serenity utility class, which exposes and delegates to methods of the legacy Thucydides class.
  - Check if a resized file for a given screenshot already exists, and if so don't perform the resizing
  - Moved most uses of FileUtils to the Java 7 Files class, in order to remove sporadic issues when resizing screenshots
# v1.0.4<a name='v1.0.4'></a>
  - Fixed a failing test
  - Fine-tuning the reports
  - Refactored some tests
  - Cater for rare cases where the driver returns null when an element is not found
  - Repositioned the report timestamp
  - Repositioned the report timestamp
  - Added bootstrap tabs
  - Added tests to the gradle plugin
  - Added SerenityRunner and SerenityParameterizedRunner classes as alternative names for ThucydidesRunner and ThucydidesParameterizedRunner, more in line with the new naming schema.
  - Moved the serenity-maven-plugin to a separate project
  - Getting the maven plugin build working
  - Fine-tuning the release tagging
# v1.0.2<a name='v1.0.2'></a>
  - Initial release version
  - Added a selector to find buttons by their label, e.g. find(By.buttonText('Add to cart'));
  - Honor both 'thucydides.properties' and 'serenity.properties' files for local project configuration
  - Let the bintray keys be defined by the build server
  - Minor fix to work around an incompatiblity with IBM JDB 1.7
  - Changed group to serenity-bdd to make syncing with Maven Central easier
  - Changed group to serenity-bdd to make syncing with Maven Central easier
  - Fixed an issue in the BinTray deployment
  - Fine-tuning the release pipeline
  - Added more info to the README file
  - Initial move over to Serenity from Thucydides

