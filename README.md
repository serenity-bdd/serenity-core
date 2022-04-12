<a id="summary">

# ![Serenity BDD](docs/serenity-bdd-logo.png "Logo Title Text 1")

[![Gitter](https://badges.gitter.im/serenity-bdd/serenity-core.svg)](https://gitter.im/serenity-bdd/serenity-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
![Continuous Integration](https://github.com/serenity-bdd/serenity-core/workflows/Continuous%20Integration/badge.svg)
[![javadoc](https://javadoc.io/badge2/net.serenity-bdd/serenity-core/javadoc.svg)](https://javadoc.io/doc/net.serenity-bdd/serenity-core)


### That feeling you get when you know you can trust your tests

Serenity BDD is a library designed to make writing automated acceptance tests easier, and more fun.

Learn more about Serenity, and discover tutorials, docs and more, on the [official Serenity BDD website](http://www.serenity-bdd.info)

## What does it do?

Serenity helps structure your automated acceptance tests in order to make them easier to understand and maintain,
and provides great reporting capabilties on top of tools like Cucumber and JUnit. It also provides tight integration
with WebDriver and RestAssured, to make both web testing and API testing easier and more efficient.

Serenity works in two ways:
  - It instruments your test code and reports on the steps that your tests execute to achieve their goals, and stores the test
  results in a standardized format;
  - It aggregates these test results into clear and meaningful reports, that reflect not only the outcomes of your tests,
  but also the status of your project. For example, you can get Serenity to report on what requirements, features or stories
  you have implemented, and how well (or not) they were tested.

### Serenity makes your test cleaner

Serenity provides libraries and patterns that make it easier to write cleaner, more reusable code. It provides tight integration with Selenium WebDriver, and modern testing patterns such as Lean Page Objects, Action Classes, and the [Screenplay Pattern](https://serenity-bdd.github.io/theserenitybook/latest/serenity-screenplay.html). You can learn more about these patterns [in this article](https://johnfergusonsmart.com/page-objects-that-suck-less-tips-for-writing-more-maintainable-page-objects/).

### Serenity makes your reports richer

Serenity reports aim to be more than simple test reports - they are designed to provide _living documentation_ of your product.
The reports give an overview of the test results:

![Serenity Test Summary](docs/serenity-dashboard.png "Logo Title Text 1")

But they also let you document your requirements hierarchy, and the state of the acceptance criteria associated with your requirements:

![Serenity Requirements Summary](docs/serenity-requirements.png "Logo Title Text 1")

When you use BDD tools like Cucumber or JBehave, Serenity will include the feature details in a format that both team members and business folk can read:

![Serenity Feature Details](docs/serenity-feature-overview.png "Logo Title Text 1")

And if you drill into the details, Serenity will give you a detailed account of how the test played out, including interactions and screenshots:

![Serenity Test Details](docs/serenity-details.png "Logo Title Text 1")

## Found a bug?
You can raise a defect here. Note that this is an open source project, so **the fastest way to fix an issue is to fix it yourself and to propose a pull request**. As with any open source project, support is never free - it is just payed for from a volunteer's spare time or through a sponsoring organisation.
    
Some companies and individuals who find that Serenity BDD helps in their daily work choose to give back by sponsoring the project in different capacities. Bug fixes or change requests coming from sponsors are generally prioritised over other requests. 

**You can help out by sponsoring Serenity BDD directly here:
[:heart: Sponsor](https://github.com/sponsors/serenity-bdd)**

Take a look at [this article](https://opensource.guide/how-to-contribute/#communicating-effectively) for more information.

## Need Commercial Support?
    
If you are using Serenity for your company projects, and need faster or more in-depth support, including training and coaching, why not ask your company to get some [commercial support](https://by2kx88fp4k.typeform.com/to/X8sN33jP)? We provide a range of support options including prioritied tickets, custom Serenity work, and remote mentoring/pair programming sessions, depending on your needs, so let's chat!
  
## Where can I learn more?

Check out Serenity BDD in more detail here:
* [User Guide](https://serenity-bdd.github.io/theserenitybook/latest/index.html)
* [![Javadocs](https://www.javadoc.io/badge/net.serenity-bdd/serenity-core.svg)](https://www.javadoc.io/doc/net.serenity-bdd/serenity-core)
* [The Serenity BDD Web Site](http://serenity-bdd.info)
* **[The Serenity Dojo Training Library](https://expansion.serenity-dojo.com/)** which includes a comprehensive range of free tutorials and more in depth courses on Serenity BDD.
* [**Introductory Course**: Effective UI Test Automation With Serenity BDD and Selenium](https://expansion.serenity-dojo.com/courses/testing-web-applications-with-serenity-bdd)
* [**Master The Serenity Screenplay Pattern**: Next-generation Automated Acceptance Tests With Serenity Screenplay](https://expansion.serenity-dojo.com/courses/writing-more-sustainable-tests-with-the-screenplay-pattern)
    
## Release Notes
Release notes are available for major releases:
* [3.2.4](docs/release-notes/3.2.4.md)
* [3.2.0](docs/release-notes/3.2.0.md)
* [2.3.2](docs/release-notes/2.3.2.md)
* [2.2.0](docs/release-notes/2.2.0.md)
* [2.1.5](docs/release-notes/2.1.5.md)
* [2.1.4](docs/release-notes/2.1.4.md)
* [2.1.2](docs/release-notes/2.1.2.md)

## What is the latest stable version I should use?

The tables below list the latest stable and tested versions of Serenity BDD and other related libraries.

### Serenity with JUnit

| serenity-core | serenity-maven-plugin | Selenium version |  RestAssured version | Appium client version |
|---------------|-----------------------|------------------|----------------------|-----------------------|
| 3.2.4         | 3.2.4                 | 4.1.3            | 5.0.1                | 8.0.0                 |
| 3.2.2         | 3.2.2                 | 4.1.2            | 4.5.1                | 8.0.0                 |
| 3.1.15        | 3.1.15                | 4.1.1            | 4.4.0                | 8.0.0-BETA            |
| 3.1.1         | 3.1.1                 | 4.0.0            | 4.3.3                | 8.0.0-BETA            |
| 3.0.5         | 3.0.5                 | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.5.8         | 2.5.8                 | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.5.7         | 2.5.7                 | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.4.51        | 2.4.51                | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.4.34        | 2.4.34                | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.4.24        | 2.4.24                | 3.141.59         | 4.3.3                | 7.5.1                 |
| 2.3.12        | 2.3.12                | 3.141.59         | 4.3.2                | 7.3.0                 |
| 2.3.5         | 2.3.5                 | 3.141.59         | 4.3.1                | 7.3.0                 |
| 2.3.4         | 2.3.4                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.2.12        | 2.2.12                | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.2.5         | 2.2.5                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.2.2         | 2.2.2                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.2.0         | 2.2.0                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.1.5         | 2.1.5                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.1.0         | 2.1.0                 | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.0.90        | 2.0.90                | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.0.84        | 2.0.84                | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.0.71        | 2.0.71                | 3.141.59         | 3.3.0                | 7.2.0                 |
| 2.0.69        | 2.0.69                | 3.141.59         | 3.3.0                | 7.0.0                 |
| 2.0.56        | 2.0.56                | 3.141.59         | 3.3.0                | 7.0.0                 |
| 2.0.52        | 2.0.52                | 3.141.59         | 3.3.0                | 7.0.0                 |
| 2.0.48        | 2.0.48                | 3.141.59         | 3.3.0                | 7.0.0                 |

Sample project: [Serenity JUnit Starter Project](https://github.com/serenity-bdd/serenity-junit-starter).

#### Cucumber 7 (Serenity 3.1.15 onwards)
| serenity-core | serenity-maven-plugin | serenity-cucumber | Cucumber |
|---------------|----------------------|--------------------|----------|
| 3.2.4         | 3.2.4                | 3.2.4              | 7.2.3    |
| 3.1.20        | 3.1.20               | 3.1.20             | 7.1.1    |
| 3.1.15        | 3.1.15               | 3.1.15             | 7.0.0    |
   
#### Cucumber 6
  _Note:_ Cucumber 6 is no longer supported in newer versions of Serenity.
  
| serenity-core | serenity-maven-plugin | serenity-cucumber6 | Cucumber            |
|---------------|-----------------------|--------------------|---------------------|
| 2.5.8         | 2.5.8                 |  2.5.8             | 6.10.4              |
| 2.5.7         | 2.5.7                 |  2.5.7             | 6.10.4              |
| 2.4.51        | 2.4.51                |  2.4.51            | 6.10.4              |
| 2.4.34        | 2.4.34                |  2.4.34            | 6.10.4              |
| 2.4.24        | 2.4.24                |  2.4.24            | 6.10.2              |
| 2.3.12        | 2.3.12                |  2.3.12            | 6.9.1               |
| 2.3.4         | 2.3.4                 |  2.3.4             | 6.6.0               |
| 2.3.4         | 2.3.4                 |  2.3.4             | 6.6.0               |
| 2.2.12        | 2.2.5                 |  2.2.5             | 5.6.0               |
| 2.2.2         | 2.2.2                 |  2.2.2             | 5.6.0               |
| 2.2.0         | 2.2.0                 |  2.2.0             | 5.5.0               |

Sample project: [Serenity Cucumber Starter Project](https://github.com/serenity-bdd/serenity-cucumber-starter).

#### Cucumber 4

_Note:_ Cucumber 4 is no longer supported in newer versions of Serenity.

| serenity-core | serenity-maven-plugin | serenity-cucumber4 | Cucumber            |
|---------------|-----------------------|--------------------|---------------------|
| 2.1.5         | 2.1.5                 |  2.1.2             | 4.8.0               |
| 2.1.0         | 2.1.0                 |  2.1.0             | 4.8.0               |
| 2.0.90        | 2.0.90                | 1.0.29             | 4.8.0               |
| 2.0.84        | 2.0.84                | 1.0.21             | 4.2.0               |
| 2.0.69        | 2.0.69                | 1.0.18             | 4.2.0               |
| 2.0.56        | 2.0.56                | 1.0.15             | 4.2.0               |
| 2.0.54        | 2.0.54                | 1.0.14             | 4.2.0               |
| 2.0.52        | 2.0.52                | 1.0.14             | 4.2.0               |
| 2.0.48        | 2.0.48                | 1.0.12             | 4.2.0               |

Sample project: [Serenity Cucumber 4 Starter Project](https://github.com/serenity-bdd/serenity-cucumber4-starter).


#### Cucumber 2

_Note:_ Cucumber 2 is no longer supported in newer versions of Serenity.

| serenity-core | serenity-maven-plugin | serenity-cucumber | Cucumber            |
|---------------|-----------------------|-------------------|---------------------|
| 2.1.5         | 2.1.5                 | 1.9.50            | 2.4.0               |
| 2.0.90        | 2.0.90                | 1.9.49            | 2.4.0               |
| 2.0.84        | 2.0.84                | 1.9.48            | 2.4.0               |
| 2.0.69        | 2.0.69                | 1.9.45            | 2.4.0               |
| 2.0.56        | 2.0.56                | 1.9.40            | 2.4.0               |
| 2.0.54        | 2.0.54                | 1.9.39            | 2.4.0               |
| 2.0.52        | 2.0.52                | 1.9.39            | 2.4.0               |
| 2.0.48        | 2.0.48                | 1.9.37            | 2.4.0               |

Sample project: [Serenity Cucumber Starter Project](https://github.com/serenity-bdd/serenity-cucumber-starter).

### Serenity with JBehave

No new development is being done with the Serenity/JBehave integration. Latest versions _should_ continue to work but new Cucumber-specific features may not.

| serenity-core | serenity-maven-plugin | serenity-jbehave  | JBehave             |
|---------------|-----------------------|-------------------|---------------------|
| 2.0.52        | 2.0.52                | 1.0.46            | 4.5                 |
| 2.0.42        | 2.0.48                | 1.0.45            | 4.5                 |

Sample project: [Serenity JBehave Starter Project](https://github.com/serenity-bdd/serenity-jbehave-starter).

## Version number format

Serenity uses a three-digit version number notation, with the following meaning:
```
 <major>.<minor>.<build>
```
The first and second digits are for more significant updates, including new features or important bug fixes. The third is
updated automatically for every new release, and is generated by the build process.

## Commit message conventions
Commit messages are used to generate the release notes for each release. To do this, we loosely follow the AngularJS commit conventions: for commit messages to appear in the release notes, the title line needs to respect the following format:
```
  <type>: <message>
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

**For more information, read the Contributing guide for this repo.**

Starting from version 1.1.26, any commits without one of these prefixes will not appear in the release notes.

## Licensing

This distribution, as a whole, is licensed under the terms of the Apache License, Version 2.0

## History - Serenity and Thucydides

Serenity was originally called [Thucydides](https://github.com/thucydides-webtests), and the package structure still reflects this history.
Thucydides is discussed at length in the [BDD in Action](http://www.amazon.com/BDD-Action-Behavior-driven-development-lifecycle/dp/161729165X)
under the name *Thucydides* - everything discussed in "BDD in Action" is directly applicable for Serenity except for the artifact names.

*Thucydides* was renamed *Serenity* in November 2014, and moving forward, all new work will be done on Serenity. The artifact names reflect this change, e.g.
  - *net.thucydides:thucydides-code* becomes *net.serenity:core*
  - *net.thucydides:thucydides-junit* becomes *net.serenity:serenity-junit*
  - *net.thucydides:thucydides-jbehave* becomes *net.serenity:serenity-jbehave*
  - *net.thucydides:thucydides-cucumber* becomes *net.serenity:serenity-cucumber*

and so on.

## Found a bug? Please read this before you raise an issue.

If you have found a defect, we are keen to hear about it! But there are a few things you can do to help us provide a fix sooner:

### Give as much context as possible.

Simply saying "The reports don't get generated" will not help us very much. Give as much context as possible, including:
  - Serenity version (serenity-core and the other serenity libraries, such as serenity-cucummber and serenity-jbehave)
  - If you are using Firefox, firefox and geckodriver version
  - If you are using Chrome, chrome and chromedriver version
  - What Operating System are you using

Also, make sure you try with the latest version of Serenity - your bug may already be fixed, and in any case error messages from the latest version will be more relevant when we try to track down the source of the problem.

### Use living documentation

It is easier for us to fix something we can see breaking. If someone has to volunteer an hour of there time to reproduce a defect, Start of with one of the Serenity started projects (like [this one](https://github.com/serenity-bdd/serenity-cucumber-starter) and add a scenario or test case that both illustrates and describes your issue. If possible, write the test to describe the behaviour you expect, so that it fails when the defect is present, and that it will pass when the defect is fixed.

## Found a bug?
You can raise a defect here. Note that this is an open source project, so **the fastest way to fix an issue is to fix it yourself and to propose a pull request**. As with any open source project, support is never free - it is just payed for from a volunteer's spare time or through a sponsoring organisation.
    
Some companies and individuals who find that Serenity BDD helps in their daily work choose to give back by sponsoring the project in different capacities. Bug fixes or change requests coming from sponsors are generally prioritised over other requests. 

**NOTE:** From time to time, old issues that have not been active for over 6 months will be automatically closed. If one of these issues affects your project and is still reproducible with the latest version of Serenity, feel free to reopen.
    
You can help out by sponsoring Serenity BDD directly here:
[:heart: Sponsor](https://github.com/sponsors/serenity-bdd)

Take a look at [this article](https://opensource.guide/how-to-contribute/#communicating-effectively) for more information.
   

## Need Commercial Support?
    
If you are using Serenity for your company projects, and need faster or more in-depth support, including training and coaching, why not ask your company to get some [commercial support](https://johnfergusonsmart.com/serenity-bdd/)? We provide a range of support options including prioritied tickets, custom Serenity work, and remote mentoring/pair programming sessions, depending on your needs.
