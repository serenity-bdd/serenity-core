/**
 * The Thucydides domain model, which represents acceptance test runs.
 * Thucydides is designed to make it easier to implement automated tests for web applications.
 * In Thucydides, we implement tests to validate <em>acceptance criteria</em>. Each <em>user story</em> 
 * (use case/story card/...) should have a set of acceptance criteria that determine when the story is implemented. 
 * These acceptance criteria are automated by Webdriver-based tests, either using JUnit or easyb. In JUnit,
 * a test case typically maps to a story card, though this is not a strict requirement. Using easyb, a test case
 * maps to a story. Each acceptance criteria is automated by a unit test (JUnit) or a scenario (easyb). 
 * A given test is made up of <em>test steps</em>, which are executed in order, and can be organized and nested 
 * in <em>test groups</em>.
 */
package net.thucydides.model.domain;
