/**
 * Thucydides JUnit test runner and associated classes.
 * This is the heart of the Thucydides JUnit integration. 
 * The {@link net.thucydides.junit.runners.ThucydidesRunner}
 * runner reads the annotations in the test class, and manages the
 * WebDriver lifecycle, creating a WebDriver instance at the start
 * of the tests, and closing it at the end. It also coordinates
 * the creation of the test run model and the reporting.
 */
package net.thucydides.junit.runners;