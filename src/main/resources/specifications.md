# Thucydides High Level Specifications

## Introduction

This document contains the main features we want to implement in the Thucydides framework. 
Thucydides is a tool that lets you use WebDriver-based unit or BDD tests to generate documentation 
about your acceptance tests, including a narrativeText description of test, along with the corresponding
screen shots, and also a high-level summary of test stories grouped by use cases and features.
  
## Features
The main high-level features are listed here.

### Manage a webdriver instance

#### User story
As a developer writing WebDriver tests, I can use the @ManagedDriver annotation declare a WebDriver object that
will be initialized at the start of a test case and closed at the end.

#### Acceptance criteria
  - You can annotate a WebDriver variable in a JUnit test using the @ManagedDriver annotation
  - An annotated WebDriver variable is initialized at the start of a test case, and closed at the end


### Execute the unit tests in a test class in the order of appearance

### Record screenshots at each step of an acceptance test  