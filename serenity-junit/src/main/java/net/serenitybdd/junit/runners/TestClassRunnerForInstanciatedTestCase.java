package net.serenitybdd.junit.runners;

import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import static org.apache.commons.lang3.StringUtils.isEmpty;

class TestClassRunnerForInstanciatedTestCase extends QualifiedTestsRunner {
    private final int parameterSetNumber;
    private final Object instanciatedTest;
    private final DataTable parametersTable;

    TestClassRunnerForInstanciatedTestCase(final Object instanciatedTest,
                                           Configuration configuration,
                                           WebDriverFactory webDriverFactory,
                                           final BatchManager batchManager,
                                           final DataTable parametersTable,
                                           final int parameterSetNumber) throws InitializationError {
        super(instanciatedTest.getClass(), configuration, webDriverFactory, batchManager);
        this.instanciatedTest = instanciatedTest;
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;
    }

    @Override
    protected JUnitStepListener initListenersUsing(final Pages pageFactory) {
        setStepListener(JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
                .and().withPageFactory(pageFactory)
                .and().withParameterSetNumber(parameterSetNumber)
                .and().withParametersTable(parametersTable)
                .and().withTestClass(getTestClass().getJavaClass())
                .and().build());
        return getStepListener();
    }

    @Override
    protected JUnitStepListener initListeners() {
        setStepListener(JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
              .and().withParameterSetNumber(parameterSetNumber)
              .and().withParametersTable(parametersTable)
              .and().withTestClass(getTestClass().getJavaClass())
              .and().build());
        return getStepListener();
    }

    @Override
    protected Object initializeTest() throws Exception {
        return instanciatedTest;
    }


    @Override
    protected String getName() {
        String qualifier = QualifierFinder.forTestCase(instanciatedTest).getQualifier();
        return (isEmpty(qualifier)) ? super.getName() : qualifier;
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        return String.format("%s[%s]", method.getName(), parameterSetNumber);
    }

    @Override
    protected Statement classBlock(final RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

    @Override
    protected void generateReports() {
        //do not generate reports at example level
    }
}
