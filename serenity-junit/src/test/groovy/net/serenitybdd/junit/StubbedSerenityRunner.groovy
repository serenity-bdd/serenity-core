package net.serenitybdd.junit

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.webdriver.WebDriverFactory

/**
 * Created by john on 31/03/2016.
 */
class StubbedSerenityRunner {
    static StubbedSerenityRunnerBuilder toRun(Class testClass) {
        new StubbedSerenityRunnerBuilder(testClass: testClass)
    }
}

class StubbedSerenityRunnerBuilder {
    Class testClass;
    WebDriverFactory factory;

    StubbedSerenityRunnerBuilder withWebDriverFactory(WebDriverFactory factory) {
        new StubbedSerenityRunnerBuilder(testClass: testClass, factory: factory)
    }

    SerenityRunner usingTemporaryDirectory(File temporaryDirectory) {
        new SerenityRunner(testClass, factory) {
            @Override
            File getOutputDirectory() {
                return temporaryDirectory
            }
        }
    }
}