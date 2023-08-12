package net.thucydides.junit.listeners;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.junit.runners.ParameterizedJUnitStepListener;
import net.thucydides.model.domain.DataTable;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.model.steps.StepListener;

import java.io.File;

public class JUnitStepListenerBuilder {
    private final File outputDirectory;
    private final Pages pageFactory;
    private final int parameterSetNumber;
    private final DataTable parametersTable;
    private final Class<?> testClass;

    public JUnitStepListenerBuilder(File outputDirectory) {
        this(outputDirectory, null, -1, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory) {
        this(outputDirectory, pageFactory, -1, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory,
                                    int parameterSetNumber) {
        this(outputDirectory, pageFactory, parameterSetNumber, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory,
                                    int parameterSetNumber,
                                    DataTable parametersTable) {
        this(outputDirectory, pageFactory, parameterSetNumber, parametersTable, null);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory,
                                    int parameterSetNumber,
                                    DataTable parametersTable,
                                    Class<?> testClass) {
        this.outputDirectory = outputDirectory;
        this.pageFactory = pageFactory;
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;
        this.testClass = testClass;
    }

    public JUnitStepListenerBuilder and() {
        return this;
    }

    public JUnitStepListenerBuilder withPageFactory(Pages pageFactory) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory);
    }

    public JUnitStepListenerBuilder withParameterSetNumber(int parameterSetNumber) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory, parameterSetNumber);
    }

    public JUnitStepListenerBuilder withParametersTable(DataTable parametersTable) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory, parameterSetNumber, parametersTable);
    }

    public JUnitStepListenerBuilder withTestClass(Class<?> testClass) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory, parameterSetNumber, parametersTable, testClass);
    }

    public JUnitStepListener build() {
        if (parameterSetNumber >= 0) {
            return newParameterizedJUnitStepListener();
        } else {
            return newStandardJunitStepListener();
        }
    }

    private BaseStepListener buildBaseStepListener() {
        if (pageFactory != null) {
            return Listeners.getBaseStepListener()
                    .and().withOutputDirectory(outputDirectory);
        } else {
            return Listeners.getBaseStepListener()
                    .withOutputDirectory(outputDirectory);
        }
    }

    private JUnitStepListener newParameterizedJUnitStepListener() {
        return new ParameterizedJUnitStepListener(parameterSetNumber,
                parametersTable,
                testClass,
                buildBaseStepListener(),
                Listeners.getLoggingListener(),
                newTestCountListener());
    }

    private StepListener newTestCountListener() {
        return new TestCountListener(SerenityInfrastructure.getEnvironmentVariables(),
                                     SerenityInfrastructure.getTestCount());
    }

    private JUnitStepListener newStandardJunitStepListener() {
        return new JUnitStepListener(testClass,
                buildBaseStepListener(),
                Listeners.getLoggingListener(),
                newTestCountListener());
//                Listeners.getStatisticsListener());
    }

}
